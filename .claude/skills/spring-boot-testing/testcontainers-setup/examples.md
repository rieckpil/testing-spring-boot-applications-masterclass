# Testcontainers Setup — Examples

## Contents
- `@ServiceConnection` Postgres in a shared `@TestConfiguration`
- `DynamicPropertyRegistrar` for services without `@ServiceConnection`
- Singleton container shared across all tests
- Reuse across runs (local speed)
- LocalStack (SQS) and Keycloak
- Local-dev launcher reusing the test containers

## `@ServiceConnection` Postgres in a shared `@TestConfiguration`

```java
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfig {

  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> database() {
    return new PostgreSQLContainer<>("postgres:17.2")
        .withDatabaseName("test").withUsername("duke").withPassword("s3cret");
  }
}
```

Import it from the integration base class with `@Import(TestcontainersConfig.class)`.
No `spring.datasource.*` needed — `@ServiceConnection` supplies them.

## `DynamicPropertyRegistrar` for services without `@ServiceConnection`

Composes with `@ServiceConnection` beans and lives in the same config. Use it
for LocalStack/Keycloak where Boot has no connection-details support:

```java
@Bean
DynamicPropertyRegistrar properties(LocalStackContainer localStack,
                                    GenericContainer<?> keycloak) {
  return registry -> {
    registry.add("spring.cloud.aws.endpoint", localStack::getEndpoint);
    registry.add("spring.cloud.aws.credentials.access-key", () -> "bar");
    registry.add("spring.cloud.aws.credentials.secret-key", () -> "foo");
    registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
        () -> "http://%s:%d/auth/realms/spring"
            .formatted(keycloak.getHost(), keycloak.getMappedPort(8080)));
  };
}
```

## Singleton container shared across all tests

The cheapest pattern: a `static` container started once per JVM, referenced by
the shared base class. It is never stopped — Ryuk reaps it when the JVM exits.

```java
public abstract class AbstractIntegrationTest {
  static final PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>("postgres:17.2");
  static { DATABASE.start(); }
}
```

## Reuse across runs (local speed)

Keep the container alive *between* test-suite runs for fast local iteration:

```java
static final PostgreSQLContainer<?> DATABASE =
    new PostgreSQLContainer<>("postgres:17.2").withReuse(true);
```

Enable globally once: add `testcontainers.reuse.enable=true` to
`~/.testcontainers.properties`. Reuse is ignored on CI by default — keep CI
clean and ephemeral.

## LocalStack (SQS) and Keycloak

```java
@Bean
LocalStackContainer localStack() {
  return new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.9.2"))
      .withServices(SQS.getLocalStackName());
}

@Bean
GenericContainer<?> keycloakContainer() {
  return new GenericContainer<>(DockerImageName.parse("quay.io/keycloak/keycloak:18.0.2"))
      .withCommand("start-dev", "--http-relative-path", "/auth", "--import-realm")
      .withEnv("KEYCLOAK_ADMIN", "keycloak")
      .withEnv("KEYCLOAK_ADMIN_PASSWORD", "keycloak")
      .withFileSystemBind("./tmp", "/opt/keycloak/data/import", BindMode.READ_ONLY)
      .withExposedPorts(8080)
      .waitingFor(Wait.forHttp("/auth").forStatusCode(200))
      .withStartupTimeout(Duration.ofMinutes(2));
}
```

For queues that must exist before tests run, create them in `@BeforeAll`:

```java
localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME);
```

## Local-dev launcher reusing the test containers

Run the real app locally on the same containers your tests use:

```java
public class LocalApplication {
  public static void main(String[] args) {
    SpringApplication.from(Application::main)
        .with(TestcontainersConfig.class)
        .run(args);
  }
}
```

Pin every image to an explicit version (`postgres:17.2`, not `postgres:latest`)
so tests are reproducible across machines and over time.
