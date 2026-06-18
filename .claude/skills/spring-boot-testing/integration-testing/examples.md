# Integration Testing — Examples

## Contents
- Shared base class with multiple containers
- `WebTestClient` assertions
- `TestRestTemplate` alternative
- Minting a real JWT for secured requests
- Asserting asynchronous/messaging behavior with Awaitility
- Per-test state cleanup

## Shared base class with multiple containers

All `IT`s extend this one class so they share a single context and one set of
JVM-lifetime containers:

```java
@ActiveProfiles("integration-test")
@ContextConfiguration(initializers = WireMockInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public abstract class AbstractIntegrationTest {

  static PostgreSQLContainer<?> database =
      new PostgreSQLContainer<>("postgres:17.2")
          .withDatabaseName("test").withUsername("duke").withPassword("s3cret");

  static LocalStackContainer localStack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.9.2"))
          .withServices(SQS.getLocalStackName());

  static { database.start(); localStack.start(); }

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("spring.datasource.password", database::getPassword);
    registry.add("spring.cloud.aws.endpoint", localStack::getEndpoint);
  }

  @Autowired protected WebTestClient webTestClient;
  @Autowired private ReviewRepository reviewRepository;
  @Autowired private BookRepository bookRepository;

  @BeforeEach
  @AfterEach
  void resetState() {
    reviewRepository.deleteAll();
    bookRepository.deleteAll();
  }
}
```

## `WebTestClient` assertions

```java
webTestClient.get().uri("/api/books")
    .exchange()
    .expectStatus().isOk()
    .expectHeader().contentType(APPLICATION_JSON)
    .expectBody()
    .jsonPath("$.size()").isEqualTo(2)
    .jsonPath("$[0].isbn").isEqualTo("42");
```

## `TestRestTemplate` alternative

When you prefer a blocking client (inject it with `RANDOM_PORT`):

```java
@Autowired private TestRestTemplate restTemplate;

ResponseEntity<Book[]> response =
    restTemplate.getForEntity("/api/books", Book[].class);

assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(response.getBody()).hasSize(2);
```

## Minting a real JWT for secured requests

Sign a token with the test RSA key whose issuer the resource server trusts
(Keycloak/WireMock). Helper lives in the base class:

```java
protected String getSignedJWT() throws JOSEException {
  JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
      .type(JOSEObjectType.JWT).keyID(RSAKeyGenerator.KEY_ID).build();
  JWTClaimsSet claims = new JWTClaimsSet.Builder()
      .issuer(oAuth2Stubs.getIssuerUri())
      .subject("duke").claim("email", "duke@spring.io")
      .claim("scope", "openid email profile")
      .expirationTime(Date.from(Instant.now().plusSeconds(120)))
      .build();
  SignedJWT jwt = new SignedJWT(header, claims);
  jwt.sign(new RSASSASigner(rsaKeyGenerator.getPrivateKey()));
  return jwt.serialize();
}
```

Use it: `.headers(h -> h.setBearerAuth(getSignedJWT()))`.

## Asserting asynchronous/messaging behavior with Awaitility

Never `Thread.sleep`. Poll for the expected end state:

```java
sqsTemplate.send(QUEUE_NAME, new BookSynchronization("42"));

await().atMost(Duration.ofSeconds(5))
    .untilAsserted(() -> assertThat(bookRepository.findByIsbn("42")).isPresent());
```

## Per-test state cleanup

A shared context means a shared database across tests. Reset it in
`@BeforeEach`/`@AfterEach` (as above) or rely on `@Transactional` rollback when
no separate thread/HTTP boundary is involved. Do not depend on execution order.
