---
name: testcontainers-setup
description: >-
  Set up Testcontainers to provide real infrastructure (PostgreSQL, Kafka,
  Redis, LocalStack, Keycloak, generic Docker images) for Spring Boot tests. Use
  when a test needs a real database, broker, or external service instead of an
  in-memory fake or mock. Covers @ServiceConnection auto-wiring, the
  @SpringBootTest plus @TestConfiguration pattern, DynamicPropertyRegistrar and
  @DynamicPropertySource, singleton/shared containers for speed, reuse, the
  local-dev @TestConfiguration with `main`, and wait strategies.
---

# Testcontainers Setup

Provide real, disposable infrastructure for tests via Docker. Prefer a real
Postgres/Kafka/Redis over H2/embedded fakes whenever the test depends on
vendor behavior, SQL dialect, migrations, or wire protocol — fakes hide bugs
that only surface in production.

Used by [integration-testing](../integration-testing/SKILL.md) and by
[slice-testing](../slice-testing/SKILL.md) `@DataJpaTest` against a real DB.

## Two wiring styles — pick one

### 1. `@ServiceConnection` (preferred, least boilerplate)

For containers Spring Boot understands (Postgres, MySQL, Mongo, Redis, Kafka,
RabbitMQ, …), `@ServiceConnection` derives all connection properties
automatically — no manual `spring.datasource.*`:

```java
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> database() {
    return new PostgreSQLContainer<>("postgres:17.2");
  }
}
```

Import: `org.springframework.boot.testcontainers.service.connection.ServiceConnection`.

### 2. `@DynamicPropertySource` / `DynamicPropertyRegistrar` (manual)

For services with no `@ServiceConnection` support (LocalStack, Keycloak, a bespoke
container), map container coordinates onto properties yourself:

```java
@DynamicPropertySource
static void props(DynamicPropertyRegistry registry) {
  registry.add("spring.datasource.url", database::getJdbcUrl);
  registry.add("spring.cloud.aws.endpoint", localStack::getEndpoint);
}
```

`DynamicPropertyRegistrar` (a `@Bean`) is the same idea but composes cleanly with
`@ServiceConnection` containers and works from a `@TestConfiguration`. See
[examples.md](examples.md).

## Make containers fast: share, don't restart

Starting Docker images is the dominant cost. Strategies, fastest first:

- **Singleton container** — `static` field with a `static {}` start block, started once per JVM and shared by every test that references it. This is the default for the shared integration base class.
- **`@ServiceConnection` bean in a shared `@TestConfiguration`** imported by the common base — one container, one cached context.
- **Reuse across runs** — opt in with `.withReuse(true)` plus `testcontainers.reuse.enable=true` in `~/.testcontainers.properties`. Speeds up local iteration; leaves containers running between runs.
- **Don't** create a fresh container per test class with a non-static field unless you truly need isolation — it restarts Docker for each class and forks the context cache.

## Wait strategies

A container being "started" is not the same as "ready". Use an explicit wait so
tests don't race startup:

```java
new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak:18.0.2"))
    .withExposedPorts(8080)
    .waitingFor(Wait.forHttp("/auth").forStatusCode(200))
    .withStartupTimeout(Duration.ofMinutes(2));
```

## Local development with the same containers

Spring Boot's `@TestConfiguration` + a `main` launcher (and/or
`spring-boot-docker-compose`) lets you run the app locally on the very same
containers the tests use. See [examples.md](examples.md).

## More detail

- `@ServiceConnection`, `DynamicPropertyRegistrar`, LocalStack/Keycloak/Kafka setups, singleton vs reuse, the local-dev launcher, and pinning image versions: see [examples.md](examples.md).
- Image catalog, `@ServiceConnection` support matrix, and the Spring Boot 4 import paths: see [reference.md](reference.md).
