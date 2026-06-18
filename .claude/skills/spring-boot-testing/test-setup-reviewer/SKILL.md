---
name: test-setup-reviewer
description: >-
  Audit a Spring Boot test suite for speed, flakiness, parallelizability, and
  ApplicationContext-cache health. Use when reviewing existing tests, diagnosing
  a slow or flaky build, before enabling parallel test execution, or when asked
  why the test suite is slow. Detects context-cache fragmentation (each distinct
  @MockitoBean set, properties, @ActiveProfiles, @Import, or @DirtiesContext
  forks a new cached context) and parallel-safety hazards (shared mutable state,
  fixed ports, Thread.sleep, test-ordering dependence). Ships a scanner script.
---

# Test Setup Reviewer

Reviews a Spring Boot test suite along two axes that dominate build time and
reliability: **ApplicationContext-cache health** and **parallel safety**. Run
the scanner, then apply the checklist judgment to its findings.

## Workflow

Copy this checklist and work through it:

```
Test Setup Audit:
- [ ] Step 1: Run the scanner to get a structured inventory
- [ ] Step 2: Triage context-cache fragmentation (fewer distinct contexts = faster)
- [ ] Step 3: Triage parallel-safety & flakiness hazards
- [ ] Step 4: Recommend consolidations and fixes, highest-impact first
- [ ] Step 5: (If fixing) re-run the scanner and the build to confirm
```

### Step 1: Run the scanner

```bash
python3 .claude/skills/spring-boot-testing/test-setup-reviewer/scripts/audit_test_setup.py src/test/java
```

It prints distinct context configurations (grouped) and a list of hazards with
`file:line`. It is read-only and advisory — no files are changed.

### Step 2: Triage context-cache fragmentation

Spring caches one `ApplicationContext` per **distinct test configuration** and
reuses it across tests. Every difference forks a new context, and each new
context pays full startup (component scan, DB pool, Testcontainers, etc.). The
goal is the *fewest distinct contexts* possible.

A configuration forks when any of these differ between two tests:

- the set of `@MockitoBean` / `@MockBean` types (a different mock set = a new context)
- inline `properties = {...}`, `@TestPropertySource`, or `@DynamicPropertySource` content
- `@ActiveProfiles`
- `@Import` / `@ContextConfiguration` (classes or initializers)
- presence of `@DirtiesContext` (also *evicts* the context, forcing the next test to rebuild)
- `webEnvironment`, or any `@SpringBootTest` annotation attribute

**What good looks like**: a handful of large groups (e.g. all `IT`s share one
context via a single abstract base; all `BookController` web tests share one).
**What to fix**: many singleton groups that differ only slightly — consolidate
them onto a shared base class or a common test configuration.

### Step 3: Triage parallel-safety & flakiness hazards

Before enabling JUnit parallel execution, every hazard the scanner flags must be
understood. Common ones and the fix are in [reference.md](reference.md).

### Step 4: Recommend, highest-impact first

Order recommendations by build-time saved: collapsing N context configs to 1
saves N−1 cold starts; removing a stray `@DirtiesContext` can save a rebuild on
every following test. Quantify where possible ("12 web tests currently build 4
contexts; aligning the `@MockitoBean` sets collapses them to 1").

## What the scanner cannot see (review by hand)

- Whether two property sets are *semantically* identical (it compares text).
- Hidden shared state through the database, filesystem, or external services.
- Whether a singleton group is intentionally isolated.
Treat scanner output as leads, not verdicts.

## More detail

- Full anti-pattern catalogue with the fix for each, plus JUnit parallel-execution config: see [reference.md](reference.md).
- For *writing* tests correctly in the first place, route back to the level subskills via [the router](../SKILL.md).
