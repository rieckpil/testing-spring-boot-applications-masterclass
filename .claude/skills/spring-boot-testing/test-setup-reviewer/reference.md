# Test Setup Reviewer — Reference

Anti-pattern catalogue. Each entry: the smell, why it hurts, the fix.

## Contents
- Context-cache fragmentation
- Context-cache eviction
- Parallel-safety hazards
- Flakiness hazards
- Enabling JUnit 5 parallel execution
- Quick triage table

## Context-cache fragmentation

Spring keeps a static cache of `ApplicationContext`s keyed by the full test
configuration. Reuse is free; a cache miss costs a full context startup. These
fork a new context:

| Smell | Why it forks | Fix |
| --- | --- | --- |
| Different `@MockitoBean` set per test | The mock set is part of the cache key | Standardize the mocked collaborators; mock the same beans in a shared base |
| Inline `properties = {...}` / `@TestPropertySource` varying per test | Property differences fork contexts | Move shared properties into an `application-test.yml` profile; keep per-test overrides rare |
| `@ActiveProfiles` differing | Profile set is part of the key | Converge on one test profile per layer |
| Ad-hoc `@Import` of test config per class | Different bean definitions = new context | Centralize in one `@TestConfiguration` imported by a shared base |
| `@SpringBootTest` attributes differing (`webEnvironment`, `classes`) | All are part of the key | Pick one base configuration per integration layer |
| One-off `@MockitoBean` inside an `IT` | Forks *and* defeats the end-to-end purpose | Remove it; containerize the dependency or mock only true externals in the base |

**Rule of thumb**: every `IT` should extend ONE abstract base; every web test for
a given controller should share ONE mock set. Aim for a single-digit number of
distinct contexts across the whole suite.

## Context-cache eviction

| Smell | Why it hurts | Fix |
| --- | --- | --- |
| `@DirtiesContext` | Closes and evicts the context; the next test rebuilds from cold | Remove unless the test genuinely corrupts shared state; reset state in `@BeforeEach`/`@AfterEach` instead |
| Mutating singleton bean state in a test | Forces `@DirtiesContext` or causes cross-test bleed | Reset via the bean's API in teardown; avoid stateful singletons |

## Parallel-safety hazards

| Smell | Why it breaks parallel runs | Fix |
| --- | --- | --- |
| Non-final mutable `static` field | Shared across concurrently-running tests → races | Make it instance state, or `final` + immutable; containers as `static` are fine (read-only handles) |
| Fixed port (`DEFINED_PORT`, `server.port=8080`, fixed MockWebServer/WireMock port) | Port collisions across parallel JVMs/threads | Use `RANDOM_PORT` and `mockWebServer.url(...)`; let WireMock pick a dynamic port |
| `System.setProperty(...)` in a test | Global JVM state mutated mid-run | Use `@DynamicPropertySource` / `registry.add(...)` instead |
| Shared database without per-test isolation | Concurrent writes interfere | `@Transactional` rollback, or unique keys per test, or disable parallelism for that group |
| `@TestMethodOrder` / `@Order` / ordering assumptions | Parallel execution ignores order | Make each test self-contained; never depend on a sibling having run first |

## Flakiness hazards

| Smell | Why it's flaky | Fix |
| --- | --- | --- |
| `Thread.sleep(...)` | Timing guess; fails under load, wastes time otherwise | Awaitility `await().untilAsserted(...)` polling the real end state |
| Real clock / `LocalDateTime.now()` in assertions | Time-dependent results | Inject a `Clock` or mock the static |
| `Math.random()` / unseeded randomness | Non-reproducible | Seed it, or assert on properties not exact values |
| Asserting on external service availability | Network flakiness | Containerize (Testcontainers) or stub (WireMock/MockWebServer) |

## Enabling JUnit 5 parallel execution

Only after the hazards above are clear. `src/test/resources/junit-platform.properties`:

```properties
junit.jupiter.execution.parallel.enabled=true
junit.jupiter.execution.parallel.mode.default=concurrent
junit.jupiter.execution.parallel.mode.classes.default=concurrent
junit.jupiter.execution.parallel.config.strategy=dynamic
```

Mark stateful tests `@Execution(ExecutionMode.SAME_THREAD)` and use
`@ResourceLock` for tests that touch a shared resource. Start with classes
concurrent / methods same-thread, then tighten.

## Quick triage table

| Scanner finding | First question |
| --- | --- |
| Many distinct context configs | Can these share an abstract base / common `@TestConfiguration`? |
| `@DirtiesContext` present | Is it truly needed, or can state be reset in teardown? |
| Mutable static field | Is it read-only (container) or genuinely shared mutable state? |
| Fixed port | Switch to `RANDOM_PORT` / dynamic port |
| `Thread.sleep` | Replace with Awaitility |
| Mock bean in `IT` | Remove or push to the shared base; prefer real infra |
