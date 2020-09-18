package de.rieckpil.courses.stubs;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import de.rieckpil.courses.initializer.RSAKeyGenerator;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class OAuth2Stubs {

  private final WireMockServer wireMockServer;
  private final RSAKeyGenerator rsaKeyGenerator;

  public OAuth2Stubs(WireMockServer wireMockServer, RSAKeyGenerator rsaKeyGenerator) {
    this.wireMockServer = wireMockServer;
    this.rsaKeyGenerator = rsaKeyGenerator;
  }

  public void stubForJWKS() {
    wireMockServer.stubFor(
      WireMock.get("/jwks")
        .willReturn(aResponse()
          .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
          .withBody(rsaKeyGenerator.getJWKSetJsonString())
        )
    );
  }

  public void stubForConfiguration() {
    wireMockServer.stubFor(
      WireMock.get("/auth/realms/spring/.well-known/openid-configuration")
        .willReturn(aResponse()
          .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
          .withBody("""
            {
             "issuer":"%s",
             "jwks_uri":"%s"
            }
            """.formatted(getIssuerUri(), getJWKSUri())))
    );
  }

  public String getIssuerUri() {
    return "http://localhost:" + wireMockServer.port() + "/auth/realms/spring";
  }

  private String getJWKSUri() {
    return "http://localhost:" + wireMockServer.port() + "/jwks";
  }
}
