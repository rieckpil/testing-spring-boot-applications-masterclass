#!/usr/bin/env python3
"""Audit a Spring Boot test suite for ApplicationContext-cache fragmentation
and parallel-safety / flakiness hazards.

Read-only and advisory: it never modifies files. It scans Java test sources,
derives a "context configuration signature" per test class, groups classes that
share a signature (and therefore share one cached ApplicationContext), and flags
patterns that break parallel execution or cause flakiness.

Usage:
    python3 audit_test_setup.py [TEST_ROOT]

TEST_ROOT defaults to "src/test/java". A relative path is resolved from the
current working directory; if it does not exist the script searches upward and
downward for a "src/test/java" directory so it works from anywhere in a repo.
"""

from __future__ import annotations

import re
import sys
from dataclasses import dataclass, field
from pathlib import Path

# Annotations that cause Spring to build/cache an ApplicationContext.
CONTEXT_ANNOTATIONS = (
    "SpringBootTest",
    "WebMvcTest",
    "WebFluxTest",
    "DataJpaTest",
    "DataR2dbcTest",
    "JdbcTest",
    "RestClientTest",
    "JsonTest",
    "DataMongoTest",
    "DataRedisTest",
)


@dataclass
class TestFile:
    path: Path
    text: str
    context_type: str | None = None
    signature: str | None = None
    hazards: list[tuple[str, int, str]] = field(default_factory=list)  # (category, line, snippet)


def find_test_root(arg: str | None) -> Path | None:
    """Locate src/test/java, being forgiving about the working directory."""
    if arg:
        candidate = Path(arg)
        if candidate.is_dir():
            return candidate
    default = Path("src/test/java")
    if default.is_dir():
        return default
    # Search upward for a project root containing src/test/java.
    for parent in [Path.cwd(), *Path.cwd().parents]:
        candidate = parent / "src" / "test" / "java"
        if candidate.is_dir():
            return candidate
    # Last resort: search downward.
    matches = list(Path.cwd().glob("**/src/test/java"))
    return matches[0] if matches else None


def annotation_value(text: str, name: str) -> str | None:
    """Return the raw argument string of @name(...) or '' for a bare @name."""
    match = re.search(r"@" + name + r"\b(\s*\(([^()]*(?:\([^()]*\)[^()]*)*)\))?", text)
    if not match:
        return None
    return (match.group(2) or "").strip()


def normalize(value: str) -> str:
    """Collapse whitespace so cosmetic formatting differences don't fork signatures."""
    return re.sub(r"\s+", " ", value).strip()


def derive_signature(tf: TestFile) -> None:
    text = tf.text

    context_type = next((a for a in CONTEXT_ANNOTATIONS if re.search(r"@" + a + r"\b", text)), None)
    tf.context_type = context_type
    if context_type is None:
        return  # plain unit test: no context, excluded from cache grouping

    parts: list[str] = [context_type]

    # The context-loading annotation's own attributes (webEnvironment, classes, properties, ...).
    own_args = annotation_value(text, context_type)
    if own_args:
        parts.append(f"args=({normalize(own_args)})")

    for ann in ("ActiveProfiles", "TestPropertySource", "ContextConfiguration", "Import"):
        val = annotation_value(text, ann)
        if val is not None:
            parts.append(f"{ann}=({normalize(val)})")

    # The set of overridden/mocked beans is part of the cache key.
    mocked = sorted(set(re.findall(r"@(?:MockitoBean|MockBean|MockitoSpyBean|SpyBean)\b[^;]*?\s(\w+)\s+\w+\s*;", text)))
    if mocked:
        parts.append("mocks={" + ",".join(mocked) + "}")

    if re.search(r"@DirtiesContext\b", text):
        parts.append("DIRTIES_CONTEXT")

    if re.search(r"@DynamicPropertySource\b", text):
        parts.append("HAS_DYNAMIC_PROPERTY_SOURCE")

    tf.signature = " | ".join(parts)


def line_of(text: str, index: int) -> int:
    return text.count("\n", 0, index) + 1


def scan_hazards(tf: TestFile) -> None:
    text = tf.text

    def add(category: str, pattern: str, predicate=None) -> None:
        for m in re.finditer(pattern, text):
            if predicate and not predicate(m):
                continue
            line = line_of(text, m.start())
            snippet = text[m.start():text.find("\n", m.start())].strip()[:100]
            tf.hazards.append((category, line, snippet))

    add("Context eviction (@DirtiesContext)", r"@DirtiesContext\b")
    add("Blocking sleep (use Awaitility)", r"Thread\.sleep\s*\(")
    add("Global JVM state mutation", r"System\.setProperty\s*\(")
    add("Test-ordering dependence", r"@(?:TestMethodOrder|FixMethodOrder)\b")
    add("Explicit @Order on test method", r"@Order\s*\(")

    # Fixed ports break parallel JVMs/threads.
    add("Fixed server port", r"DEFINED_PORT")
    add("Fixed server port", r"\"server\.port=\d+")
    add("Fixed exposed port", r"withFixedExposedPort\s*\(")

    # Mock bean inside a full @SpringBootTest forks the context cache and weakens the IT.
    if re.search(r"@SpringBootTest\b", text) and re.search(r"@(?:MockitoBean|MockBean)\b", text):
        m = re.search(r"@(?:MockitoBean|MockBean)\b", text)
        tf.hazards.append(
            ("Mock bean inside @SpringBootTest (forks context, weakens IT)",
             line_of(text, m.start()),
             text[m.start():text.find("\n", m.start())].strip()[:100]))

    # Non-final mutable static fields shared across parallel tests. Containers and
    # loggers held in static fields are read-only handles and are intentionally excluded.
    for m in re.finditer(r"^[ \t]*(?:private|protected|public)?[ \t]*static[ \t]+(?!final\b)(\w[\w<>,.\[\]?]*(?:[ \t]+\w[\w<>,.\[\]?]*)*?)[ \t]+(\w+)[ \t]*[=;]", text, re.MULTILINE):
        type_name = m.group(1).strip()
        if re.search(r"(Container|Logger|MockWebServer|WireMockServer)", type_name):
            continue
        tf.hazards.append(
            ("Mutable static field (shared across parallel tests)",
             line_of(text, m.start()),
             text[m.start():text.find("\n", m.start())].strip()[:100]))


def main() -> int:
    arg = sys.argv[1] if len(sys.argv) > 1 else None
    root = find_test_root(arg)
    if root is None:
        print("ERROR: could not locate a 'src/test/java' directory. Pass the test root explicitly.")
        return 2

    files: list[TestFile] = []
    for path in sorted(root.rglob("*.java")):
        try:
            text = path.read_text(encoding="utf-8", errors="replace")
        except OSError as exc:
            print(f"WARN: could not read {path}: {exc}")
            continue
        # Heuristic: a test class contains at least one @Test (or @ParameterizedTest/@RepeatedTest).
        if not re.search(r"@(?:Test|ParameterizedTest|RepeatedTest)\b", text):
            continue
        tf = TestFile(path=path, text=text)
        derive_signature(tf)
        scan_hazards(tf)
        files.append(tf)

    rel = lambda p: str(p.relative_to(Path.cwd())) if p.is_relative_to(Path.cwd()) else str(p)

    print("=" * 72)
    print(f"Spring Boot Test Setup Audit  —  root: {rel(root)}")
    print(f"Scanned {len(files)} test classes")
    print("=" * 72)

    # ----- ApplicationContext cache -----
    context_files = [f for f in files if f.context_type]
    groups: dict[str, list[TestFile]] = {}
    for f in context_files:
        groups.setdefault(f.signature, []).append(f)

    unit_only = len(files) - len(context_files)
    print()
    print("## ApplicationContext cache")
    print(f"{len(context_files)} context-loading tests -> {len(groups)} distinct cached context(s).")
    print(f"{unit_only} context-free unit test class(es) (no context cost).")
    print("Each distinct configuration pays a full context startup. Fewer is faster.\n")

    shared = {s: g for s, g in groups.items() if len(g) > 1}
    singletons = {s: g for s, g in groups.items() if len(g) == 1}

    for idx, (sig, group) in enumerate(sorted(shared.items(), key=lambda kv: -len(kv[1])), start=1):
        print(f"  [shared x{len(group)}] context #{idx}")
        print(f"     signature: {sig}")
        for f in group:
            print(f"       - {rel(f.path)}")
    if singletons:
        print(f"\n  [singletons] {len(singletons)} context(s) used by exactly one test "
              f"(consolidation candidates if they differ only slightly):")
        for sig, group in singletons.items():
            f = group[0]
            print(f"       - {rel(f.path)}")
            print(f"           signature: {sig}")

    # ----- Hazards -----
    print()
    print("## Parallel-safety & flakiness hazards")
    by_category: dict[str, list[tuple[Path, int, str]]] = {}
    for f in files:
        for category, line, snippet in f.hazards:
            by_category.setdefault(category, []).append((f.path, line, snippet))

    total_hazards = sum(len(v) for v in by_category.values())
    if total_hazards == 0:
        print("  None detected.")
    else:
        for category in sorted(by_category):
            hits = by_category[category]
            print(f"  [{category}] {len(hits)}")
            for path, line, snippet in hits:
                print(f"       {rel(path)}:{line}  {snippet}")

    # ----- Summary -----
    print()
    print("=" * 72)
    print(f"Summary: {len(groups)} distinct context configuration(s), {total_hazards} hazard(s).")
    if len(groups) > 1:
        print(f"  Consolidating to 1 context would save ~{len(groups) - 1} cold start(s).")
    print("This report is advisory; verify findings against intent before changing tests.")
    print("=" * 72)
    return 0


if __name__ == "__main__":
    sys.exit(main())
