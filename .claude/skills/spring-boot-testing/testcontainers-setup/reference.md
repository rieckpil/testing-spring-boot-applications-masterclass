# Testcontainers Setup — Reference

## Contents
- Spring Boot 4 import paths
- `@ServiceConnection` support matrix
- Common container modules and images
- Wiring decision: `@ServiceConnection` vs manual
- Speed/lifecycle cheat-sheet

## Spring Boot 4 import paths

```java
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.DynamicPropertyRegistrar;
```

Testcontainers 2.x split container types into per-technology modules; import the
container from its module package, e.g.
`org.testcontainers.postgresql.PostgreSQLContainer`,
`org.testcontainers.localstack.LocalStackContainer`.

## `@ServiceConnection` support matrix

`@ServiceConnection` auto-derives connection properties for containers Boot has
connection-details support for. Common ones:

| Service | Supported by `@ServiceConnection` | If not, wire via |
| --- | --- | --- |
| PostgreSQL / MySQL / MariaDB | Yes (`jdbc`) | — |
| MongoDB | Yes | — |
| Redis | Yes | — |
| Kafka | Yes | — |
| RabbitMQ | Yes | — |
| Elasticsearch | Yes | — |
| LocalStack (AWS) | No | `DynamicPropertyRegistrar` / `@DynamicPropertySource` |
| Keycloak / OAuth2 issuer | No | manual issuer-uri property |
| Arbitrary `GenericContainer` | No | manual host/port mapping |

When unsupported, map `container.getHost()` + `container.getMappedPort(port)`
onto the relevant properties yourself.

## Common container modules and images

| Need | Container | Pin example |
| --- | --- | --- |
| Relational DB | `PostgreSQLContainer` | `postgres:17.2` |
| AWS services | `LocalStackContainer` | `localstack/localstack:4.9.2` |
| OAuth2/OIDC | `GenericContainer` (Keycloak) | `quay.io/keycloak/keycloak:18.0.2` |
| Messaging | `KafkaContainer` / `RabbitMQContainer` | pin explicitly |
| Anything | `GenericContainer` | always pin a tag |

Always pin a concrete tag — never `latest` — for reproducible builds.

## Wiring decision: `@ServiceConnection` vs manual

1. Container has `@ServiceConnection` support → annotate the `@Bean` with it. Done.
2. No support, but a starter reads standard properties → `DynamicPropertyRegistrar` (a `@Bean`) or `@DynamicPropertySource` (a `static` method).
3. Need the registry to see *other* started containers → `DynamicPropertyRegistrar` (takes containers as method params; composes with `@ServiceConnection`).

## Speed/lifecycle cheat-sheet

| Pattern | Starts when | Shared scope | Use for |
| --- | --- | --- | --- |
| `static` field + `static {}` start | first class load | whole JVM | the default shared base |
| `@Bean` in shared `@TestConfiguration` | context creation | all tests on that context | `@ServiceConnection` wiring |
| Non-`static` field, `@Container` | per test class | one class | rare; true isolation only |
| `.withReuse(true)` + global flag | once, persists | across runs (local) | fast local iteration |
| `@DirtiesContext` near containers | — | — | avoid; evicts the cached context |
