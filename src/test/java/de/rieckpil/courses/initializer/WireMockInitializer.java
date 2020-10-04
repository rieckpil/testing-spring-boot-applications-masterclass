package de.rieckpil.courses.initializer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import de.rieckpil.courses.stubs.OAuth2Stubs;
import de.rieckpil.courses.stubs.OpenLibraryStubs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;

@Order(Ordered.LOWEST_PRECEDENCE - 1000)
public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final Logger LOG = LoggerFactory.getLogger(WireMockInitializer.class);

  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {

    LOG.info("About to start the WireMockServer");

    WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().dynamicPort());
    wireMockServer.start();

    LOG.info("WireMockServer successfully started");

    if (Arrays.asList(applicationContext.getEnvironment().getActiveProfiles()).contains("integration-test")) {
      RSAKeyGenerator rsaKeyGenerator = new RSAKeyGenerator();
      rsaKeyGenerator.initializeKeys();

      OAuth2Stubs oAuth2Stubs = new OAuth2Stubs(wireMockServer, rsaKeyGenerator);
      oAuth2Stubs.stubForConfiguration();
      oAuth2Stubs.stubForJWKS();

      applicationContext.getBeanFactory().registerSingleton("oAuth2Stubs", oAuth2Stubs);
      applicationContext.getBeanFactory().registerSingleton("rsaKeyGenerator", rsaKeyGenerator);

      TestPropertyValues
        .of(
          "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:" + wireMockServer.port() + "/auth/realms/spring"
        ).applyTo(applicationContext);
    }

    OpenLibraryStubs openLibraryStubs = new OpenLibraryStubs(wireMockServer);

    applicationContext.getBeanFactory().registerSingleton("wireMockServer", wireMockServer);
    applicationContext.getBeanFactory().registerSingleton("openLibraryStubs", openLibraryStubs);

    applicationContext.addApplicationListener(applicationEvent -> {
      if (applicationEvent instanceof ContextClosedEvent) {
        LOG.info("Stopping the WireMockServer");
        wireMockServer.stop();
      }
    });

    TestPropertyValues
      .of(
        "clients.open-library.base-url=http://localhost:" + wireMockServer.port() + "/openLibrary"
      ).applyTo(applicationContext);
  }
}
