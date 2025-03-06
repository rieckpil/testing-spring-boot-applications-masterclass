package de.rieckpil.courses;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.book.review.ReviewRepository;
import de.rieckpil.courses.initializer.RSAKeyGenerator;
import de.rieckpil.courses.initializer.WireMockInitializer;
import de.rieckpil.courses.stubs.OAuth2Stubs;
import de.rieckpil.courses.stubs.OpenLibraryStubs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

@ActiveProfiles("integration-test")
@ContextConfiguration(initializers = WireMockInitializer.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

  static PostgreSQLContainer<?> database =
      new PostgreSQLContainer<>("postgres:17.4")
          .withDatabaseName("test")
          .withUsername("duke")
          .withPassword("s3cret");

  static LocalStackContainer localStack =
      new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.1.1"))
          .withServices(SQS);

  // can be removed with version 0.12.17 as LocalStack now has multi-region support
  // https://docs.localstack.cloud/localstack/configuration/#deprecated
  // .withEnv("DEFAULT_REGION", "eu-central-1");

  static {
    database.start();
    localStack.start();
  }

  protected static final String QUEUE_NAME = UUID.randomUUID().toString();

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", database::getJdbcUrl);
    registry.add("spring.datasource.password", database::getPassword);
    registry.add("spring.datasource.username", database::getUsername);
    registry.add("sqs.book-synchronization-queue", () -> QUEUE_NAME);
    registry.add("spring.cloud.aws.credentials.secret-key", () -> "foo");
    registry.add("spring.cloud.aws.credentials.access-key", () -> "bar");
    registry.add("spring.cloud.aws.endpoint", () -> localStack.getEndpointOverride(SQS));
  }

  @Autowired private ReviewRepository reviewRepository;

  @Autowired private BookRepository bookRepository;

  @Autowired private RSAKeyGenerator rsaKeyGenerator;

  @Autowired private OAuth2Stubs oAuth2Stubs;

  @Autowired protected OpenLibraryStubs openLibraryStubs;

  @Autowired private WireMockServer wireMockServer;

  @BeforeAll
  static void beforeAll() throws IOException, InterruptedException {
    localStack.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", QUEUE_NAME);
  }

  @BeforeEach
  void init() {
    this.reviewRepository.deleteAll();
    this.bookRepository.deleteAll();
  }

  @AfterEach
  void cleanUp() {
    this.reviewRepository.deleteAll();
    this.bookRepository.deleteAll();
  }

  protected String getSignedJWT(String username, String email) throws JOSEException {
    return createJWT(username, email);
  }

  protected String getSignedJWT() throws JOSEException {
    return createJWT("duke", "duke@spring.io");
  }

  private String createJWT(String username, String email) throws JOSEException {
    JWSHeader header =
        new JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .keyID(RSAKeyGenerator.KEY_ID)
            .build();

    JWTClaimsSet payload =
        new JWTClaimsSet.Builder()
            .issuer(oAuth2Stubs.getIssuerUri())
            .audience("account")
            .subject(username)
            .claim("preferred_username", username)
            .claim("email", email)
            .claim("scope", "openid email profile")
            .claim("azp", "react-client")
            .claim("realm_access", Map.of("roles", List.of()))
            .expirationTime(Date.from(Instant.now().plusSeconds(120)))
            .issueTime(new Date())
            .build();

    SignedJWT signedJWT = new SignedJWT(header, payload);
    signedJWT.sign(new RSASSASigner(rsaKeyGenerator.getPrivateKey()));
    return signedJWT.serialize();
  }
}
