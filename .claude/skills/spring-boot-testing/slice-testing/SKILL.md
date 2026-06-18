---
name: slice-testing
description: >-
  Write Spring Boot slice tests that load only one layer of auto-configuration.
  Use for the persistence slice (@DataJpaTest — repositories, JPA mappings,
  queries, with TestEntityManager), the HTTP-client slice (@RestClientTest or a
  MockWebServer/WireMock harness for RestClient/RestTemplate/WebClient), and the
  JSON slice (@JsonTest for serializer/deserializer round-trips). Use when
  testing one technical layer in isolation, faster than @SpringBootTest but with
  real framework wiring. For controllers see slice-testing-webmvc.
---

# Slice Testing (`@DataJpaTest`, `@RestClientTest`, `@JsonTest`)

A slice test boots a *narrow* set of auto-configuration — one layer — instead of
the whole application. Faster than `@SpringBootTest`, more realistic than a unit
test for layer-specific concerns (SQL, mapping, serialization). The web-layer
slice has its own subskill: [slice-testing-webmvc](../slice-testing-webmvc/SKILL.md).

## Choose the slice

| Layer under test | Annotation | What loads |
| --- | --- | --- |
| JPA repositories, entity mappings, queries | `@DataJpaTest` | JPA, a `DataSource`, `TestEntityManager`; transactional + rolled back per test |
| `RestClient`/`RestTemplate` client beans | `@RestClientTest(MyClient.class)` | The client + `MockRestServiceServer` |
| `WebClient` / reactive client | (no slice) MockWebServer harness | Plain unit-style test, no context |
| JSON (de)serialization | `@JsonTest` | Jackson/Gson config + `JacksonTester` |

Spring Boot 4 imports: `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`,
`org.springframework.boot.jpa.test.autoconfigure.TestEntityManager`,
`org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase`.
Full annotation/import cheat-sheet: [reference.md](reference.md).

## `@DataJpaTest` recipe

```java
@DataJpaTest
class ReviewRepositoryTest {

  @Autowired private TestEntityManager testEntityManager;
  @Autowired private ReviewRepository cut;

  @Test
  void shouldFindReviewsByBookIsbn() {
    testEntityManager.persistAndFlush(newReview("42"));

    List<Review> result = cut.findAllByBookIsbn("42");

    assertThat(result).hasSize(1);
  }
}
```

- **Database choice.** By default `@DataJpaTest` replaces your `DataSource` with an embedded H2. To test against the *real* database engine (recommended when you use vendor-specific SQL/migrations), add `@AutoConfigureTestDatabase(replace = Replace.NONE)` and point it at a Testcontainers Postgres — see [testcontainers-setup](../testcontainers-setup/SKILL.md). H2 dialect drift is a common source of "passes locally, fails in prod" bugs.
- **Each test is transactional and rolls back** automatically — no manual cleanup, and tests stay isolated.
- **`TestEntityManager`** seeds and flushes data without going through the repository under test, keeping arrange and act separate.

## HTTP client slice

- **For `RestClient`/`RestTemplate`**: use `@RestClientTest(OpenLibraryApiClient.class)` and drive responses with the injected `MockRestServiceServer`.
- **For `WebClient`/reactive or non-bean clients**: skip the slice entirely and use an OkHttp `MockWebServer` (or WireMock) — construct the client against `mockWebServer.url("/")`. This is effectively a unit test: no context, full control over timeouts, retries, and error codes.

## JSON slice

`@JsonTest` + `JacksonTester` verifies the wire format and round-trips,
decoupled from controllers.

## More detail

- `@DataJpaTest` query tests, `TestEntityManager` seeding, asserting generated SQL, `@RestClientTest` with `MockRestServiceServer`, MockWebServer harness for retries/timeouts, and `@JsonTest` round-trips: see [examples.md](examples.md).
- Annotation surface, what each slice auto-configures, and common properties: see [reference.md](reference.md).
