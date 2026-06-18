---
name: integration-testing
description: >-
  Write full-stack Spring Boot integration tests with @SpringBootTest that boot
  the wired application against real infrastructure (Testcontainers databases,
  brokers, LocalStack, Keycloak) and exercise it end to end over HTTP. Use when
  verifying that layers, configuration, security, serialization, and persistence
  work together — the critical user-facing paths. Covers webEnvironment choices,
  WebTestClient/TestRestTemplate, a shared abstract base class, profiles,
  DynamicPropertySource, and authenticating real requests. These tests end in IT.
---

# Integration Testing (`@SpringBootTest`)

Boots the whole `ApplicationContext` and runs against real infrastructure. The
top of the pyramid: the slowest and fewest tests, reserved for verifying that
everything wired together actually works. File names end in `IT`.

## When this is the right level

Use it for the handful of critical end-to-end paths: a request that flows
controller → service → repository → real database, security enforced, JSON
serialized, messages published/consumed. Anything provable one layer down
belongs in a [slice test](../slice-testing/SKILL.md) or
[unit test](../unit-testing/SKILL.md) instead — those are 10–100x faster.

## Core recipe: one shared abstract base class

Put all the expensive, identical setup in a single abstract base so every `IT`
**shares one cached ApplicationContext and one set of containers**. This is the
most important performance decision in the whole suite.

```java
@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {

  static PostgreSQLContainer<?> database = new PostgreSQLContainer<>("postgres:17.2");
  static { database.start(); } // start once, shared across all subclasses (JVM-lifetime)

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("spring.datasource.password", database::getPassword);
  }

  @Autowired protected WebTestClient webTestClient;

  @BeforeEach
  void cleanState() { /* truncate / deleteAll so tests are isolated */ }
}
```

Then each test extends it and stays small:

```java
class ReviewControllerIT extends AbstractIntegrationTest {

  @Test
  void shouldCreateReviewForAuthenticatedUser() throws Exception {
    webTestClient.post().uri("/api/books/42/reviews")
        .headers(h -> h.setBearerAuth(getSignedJWT()))
        .bodyValue(payload)
        .exchange()
        .expectStatus().isCreated();
  }
}
```

Imports (Spring Boot 4): `org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient`.

## Rules

- **One base class, one context.** Every `IT` extending the same configured base reuses the cached context and the already-started containers. Diverging annotations (extra `@MockitoBean`, different `properties`, a second profile) fork a new context and a new container set — slow. Keep deviations rare and centralized.
- **`webEnvironment`**: `RANDOM_PORT` starts a real servlet container (test over HTTP with `WebTestClient`/`TestRestTemplate`). `MOCK` (default) uses `MockMvc` without a port. Use `RANDOM_PORT`, never a fixed port — fixed ports break parallel runs and CI.
- **Isolate state between tests.** A shared context means a shared database. Reset it per test (`@BeforeEach` cleanup, or `@Transactional` where a rollback suffices). Do not rely on test ordering.
- **Avoid `@MockitoBean` in `IT`s.** Mocking a bean changes the context and forks the cache; it also undermines the point of an end-to-end test. Mock only true externals you cannot containerize, and do it in the shared base.
- **Authenticate like production.** For OAuth2, mint a real signed JWT against a Keycloak/WireMock issuer (see base-class helper) rather than bypassing security.
- **Never `@DirtiesContext`** unless a test genuinely corrupts the context — it evicts the cached context and forces a full, slow rebuild for the next test.

## More detail

- Full base class with WireMock + LocalStack + Keycloak, `@ServiceConnection` vs `@DynamicPropertySource`, `WebTestClient` vs `TestRestTemplate`, JWT minting, and async/messaging assertions with Awaitility: see [examples.md](examples.md).
- Container wiring belongs to [testcontainers-setup](../testcontainers-setup/SKILL.md).
- Before adding an `IT`, sanity-check it won't fork the context: [test-setup-reviewer](../test-setup-reviewer/SKILL.md).
