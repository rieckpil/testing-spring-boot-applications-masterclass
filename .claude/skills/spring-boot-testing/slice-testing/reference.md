# Slice Testing — Reference

Annotation surface and Spring Boot 4 import paths for the common test slices.

## Contents
- Slice annotations and what they auto-configure
- Spring Boot 4 import paths (modularized)
- `@DataJpaTest` knobs
- Scope-narrowing arguments
- Context-cache notes

## Slice annotations and what they auto-configure

| Annotation | Loads | Injectable helpers | Excludes |
| --- | --- | --- | --- |
| `@DataJpaTest` | JPA, `DataSource`, transaction mgr; tx + rollback per test | `TestEntityManager`, repositories | `@Service`/`@Component`, web layer |
| `@WebMvcTest` | MVC infra, controllers, security, advice | `MockMvc` | repositories, services (provide via `@MockitoBean`) |
| `@RestClientTest` | a named client + Jackson | `MockRestServiceServer`, the client bean | everything else |
| `@JsonTest` | Jackson/Gson config | `JacksonTester`, `GsonTester` | everything else |
| `@JdbcTest` | `DataSource` + `JdbcTemplate` (no JPA) | `JdbcTemplate` | JPA, services, web |
| `@DataR2dbcTest` | R2DBC + reactive repos | repositories | web, services |

All slices are meta-annotated with `@AutoConfigure...` and a transactional or
filtered component scan. They do **not** load `@Component`/`@Service` beans —
that is the point.

## Spring Boot 4 import paths (modularized)

```java
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;        // web slice
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;   // replaces @MockBean
```

These differ from Spring Boot 3, where most lived under
`org.springframework.boot.test.autoconfigure.*`. Using the old paths will not
compile against Boot 4.

## `@DataJpaTest` knobs

- `@AutoConfigureTestDatabase(replace = Replace.NONE)` — keep the configured `DataSource` (e.g. Testcontainers Postgres) instead of swapping in embedded H2.
- `@DataJpaTest(properties = { ... })` — override properties inline (e.g. `spring.jpa.hibernate.ddl-auto`, P6Spy datasource). Note: inline properties create a *distinct* cached context.
- `@DataJpaTest(showSql = false)` — silence the default SQL echo.
- Tests are `@Transactional` and roll back; call `testEntityManager.flush()`/`clear()` to force SQL and detach.

## Scope-narrowing arguments

- `@WebMvcTest(BookController.class)` — load one controller, not all. Faster and avoids a broad context that every other web test must also build.
- `@RestClientTest(OpenLibraryApiClient.class)` — load one client.
- `@DataJpaTest` does not take a "which repository" argument; it loads all repositories in scope (cheap, they are just proxies).

## Context-cache notes

Each unique slice configuration is cached separately. Two `@DataJpaTest` classes
with identical properties share one context; add `properties = {...}` to one and
it forks a second. Keep slice configurations uniform across the suite and
centralize deviations. See [test-setup-reviewer](../test-setup-reviewer/SKILL.md).
