# Slice Testing — Examples

## Contents
- `@DataJpaTest`: query method against a real DB
- `@DataJpaTest`: seeding with `TestEntityManager`
- `@DataJpaTest`: asserting the executed SQL (P6Spy)
- `@RestClientTest` with `MockRestServiceServer`
- MockWebServer harness for a non-bean client (timeouts, retries, errors)
- `@JsonTest` round-trip

## `@DataJpaTest`: query method against a real DB

Run against Testcontainers Postgres so vendor SQL and migrations are exercised:

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

  @Autowired private TestEntityManager testEntityManager;
  @Autowired private ReviewRepository cut;

  @Test
  void shouldReturnReviewsOrderedByCreatedAtDesc() {
    testEntityManager.persistFlushFind(reviewCreatedAt("2026-01-01T10:00"));
    testEntityManager.persistFlushFind(reviewCreatedAt("2026-01-02T10:00"));

    List<Review> result = cut.findAllByOrderByCreatedAtDesc();

    assertThat(result)
        .extracting(Review::getCreatedAt)
        .isSortedAccordingTo(Comparator.reverseOrder());
  }
}
```

Provide the container via a shared base class or `@ServiceConnection` —
see [testcontainers-setup](../testcontainers-setup/SKILL.md).

## `@DataJpaTest`: seeding with `TestEntityManager`

```java
Review persisted = testEntityManager.persistFlushFind(newReview("42"));
assertThat(persisted.getId()).isNotNull();
testEntityManager.clear(); // detach so the next read hits the DB, not the 1st-level cache
```

## `@DataJpaTest`: asserting the executed SQL

When the point of the test is "no N+1" or "this index is used", route the
`DataSource` through P6Spy and assert on the logged statements:

```java
@DataJpaTest(properties = {
    "spring.datasource.driver-class-name=com.p6spy.engine.spy.P6SpyDriver",
    "spring.datasource.url=jdbc:p6spy:h2:mem:testing;DB_CLOSE_DELAY=-1"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositorySqlTest { /* ... */ }
```

## `@RestClientTest` with `MockRestServiceServer`

For a `RestClient`/`RestTemplate`-based bean:

```java
@RestClientTest(OpenLibraryApiClient.class)
class OpenLibraryApiClientRestClientTest {

  @Autowired private OpenLibraryApiClient cut;
  @Autowired private MockRestServiceServer server;

  @Test
  void shouldParseSuccessResponse() {
    server.expect(requestTo(containsString("/api/books")))
          .andRespond(withSuccess(validJson(), APPLICATION_JSON));

    Book result = cut.fetchMetadataForBook("9780596004651");

    assertThat(result.getTitle()).isEqualTo("Head first Java");
    server.verify();
  }
}
```

## MockWebServer harness for a non-bean client

Best for `WebClient`/reactive clients and for asserting timeout/retry behavior
that a static mock server can't express. No Spring context:

```java
class OpenLibraryApiClientTest {

  private MockWebServer mockWebServer;
  private OpenLibraryApiClient cut;

  @BeforeEach
  void setup() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    cut = new OpenLibraryApiClient(
        WebClient.builder().baseUrl(mockWebServer.url("/").toString()).build());
  }

  @AfterEach
  void shutdown() throws IOException { mockWebServer.shutdown(); }

  @Test
  void shouldRetryThenSucceedWhenRemoteIsFlaky() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));
    mockWebServer.enqueue(new MockResponse()
        .addHeader("Content-Type", "application/json")
        .setBody(validJson()));

    Book result = cut.fetchMetadataForBook("9780596004651");

    assertThat(result.getIsbn()).isEqualTo("9780596004651");
  }

  @Test
  void shouldPropagateExceptionWhenRemoteIsDown() {
    mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("down"));
    assertThatThrownBy(() -> cut.fetchMetadataForBook("42")).isInstanceOf(RuntimeException.class);
  }
}
```

## `@JsonTest` round-trip

```java
@JsonTest
class BookJsonTest {

  @Autowired private JacksonTester<Book> json;

  @Test
  void shouldSerializeWithoutInternalId() throws Exception {
    JsonContent<Book> result = json.write(new Book(1L, "42", "Effective Java"));
    assertThat(result).extractingJsonPathStringValue("$.isbn").isEqualTo("42");
    assertThat(result).doesNotHaveJsonPath("$.id");
  }

  @Test
  void shouldDeserialize() throws Exception {
    Book result = json.parseObject("""
        { "isbn": "42", "title": "Effective Java" }
        """);
    assertThat(result.getTitle()).isEqualTo("Effective Java");
  }
}
```
