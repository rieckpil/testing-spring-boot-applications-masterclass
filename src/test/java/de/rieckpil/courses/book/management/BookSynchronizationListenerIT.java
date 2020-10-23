package de.rieckpil.courses.book.management;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.rieckpil.courses.initializer.RSAKeyGenerator;
import de.rieckpil.courses.initializer.WireMockInitializer;
import de.rieckpil.courses.stubs.OAuth2Stubs;
import de.rieckpil.courses.stubs.OpenLibraryStubs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.*;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.awaitility.Awaitility.given;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@ActiveProfiles("integration-test")
@Testcontainers(disabledWithoutDocker = true)
@ContextConfiguration(initializers = WireMockInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookSynchronizationListenerIT {

  static PostgreSQLContainer database = (PostgreSQLContainer) new PostgreSQLContainer("postgres:12.3")
    .withDatabaseName("test")
    .withUsername("duke")
    .withPassword("s3cret");

  static LocalStackContainer localStack = new LocalStackContainer("0.10.0")
    .withServices(SQS)
    .withEnv("DEFAULT_REGION", "eu-central-1");

  private static final String QUEUE_NAME = UUID.randomUUID().toString();
  private static final String ISBN = "9780596004651";
  private static String VALID_RESPONSE;

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.password", database::getPassword);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("sqs.book-synchronization-queue", () -> QUEUE_NAME);
  }

  @TestConfiguration
  static class TestConfig {
    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
      return AmazonSQSAsyncClientBuilder.standard()
        .withCredentials(localStack.getDefaultCredentialsProvider())
        .withEndpointConfiguration(localStack.getEndpointConfiguration(SQS))
        .build();
    }
  }

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME);
  }

  static {
    database.start();
    localStack.start();
    try {
      VALID_RESPONSE = new String(BookSynchronizationListenerIT.class
        .getClassLoader()
        .getResourceAsStream("stubs/openlibrary/success-" + ISBN + ".json")
        .readAllBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Autowired
  private QueueMessagingTemplate queueMessagingTemplate;

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private RSAKeyGenerator rsaKeyGenerator;

  @Autowired
  private OAuth2Stubs oAuth2Stubs;

  @Autowired
  private OpenLibraryStubs openLibraryStubs;

  @Autowired
  private BookRepository bookRepository;

  @BeforeEach
  public void cleanUp() {
    this.bookRepository.deleteAll();
  }

  @AfterEach
  public void tearDown() {
    this.bookRepository.deleteAll();
  }

  @Test
  public void shouldGetSuccessWhenClientIsAuthenticated() throws JOSEException {

    JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
      .type(JOSEObjectType.JWT)
      .keyID(RSAKeyGenerator.KEY_ID)
      .build();

    JWTClaimsSet payload = new JWTClaimsSet.Builder()
      .issuer(oAuth2Stubs.getIssuerUri())
      .audience("account")
      .subject("duke")
      .claim("preferred_username", "duke")
      .claim("email", "duke@spring.io")
      .claim("scope", "openid email profile")
      .claim("azp", "react-client")
      .claim("realm_access", Map.of("roles", List.of()))
      .expirationTime(Date.from(Instant.now().plusSeconds(120)))
      .issueTime(new Date())
      .build();

    SignedJWT signedJWT = new SignedJWT(header, payload);
    signedJWT.sign(new RSASSASigner(rsaKeyGenerator.getPrivateKey()));

    this.webTestClient
      .get()
      .uri("/api/books/reviews/statistics")
      .header(HttpHeaders.AUTHORIZATION, "Bearer " + signedJWT.serialize())
      .exchange()
      .expectStatus().is2xxSuccessful();
  }

  @Test
  public void shouldReturnBookFromAPIWhenApplicationConsumesNewSyncRequest() {

    this.webTestClient
      .get()
      .uri("/api/books")
      .exchange()
      .expectStatus().isOk()
      .expectBody().jsonPath("$.size()").isEqualTo(0);

    this.openLibraryStubs.stubForSuccessfulBookResponse(ISBN, VALID_RESPONSE);

    this.queueMessagingTemplate.send(QUEUE_NAME, new GenericMessage<>(
      """
          {
            "isbn": "%s"
          }
        """.formatted(ISBN), Map.of("contentType", "application/json")));

    given()
      .atMost(Duration.ofSeconds(5))
      .await()
      .untilAsserted(() -> {
        this.webTestClient
          .get()
          .uri("/api/books")
          .exchange()
          .expectStatus().isOk()
          .expectBody()
          .jsonPath("$.size()").isEqualTo(1)
          .jsonPath("$[0].isbn").isEqualTo(ISBN);
      });
  }
}
