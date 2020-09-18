package de.rieckpil.courses.initializer;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import java.security.*;
import java.security.interfaces.RSAPublicKey;

public class RSAKeyGenerator {

  public static final String KEY_ID = "reviewr";

  private PublicKey publicKey;
  private PrivateKey privateKey;

  // oauth2-jose
  public String getJWKSetJsonString() {
    RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) publicKey)
      .keyUse(KeyUse.SIGNATURE)
      .algorithm(JWSAlgorithm.RS256)
      .keyID(KEY_ID);

    return new JWKSet(builder.build()).toJSONObject().toJSONString();
  }

  public void initializeKeys() {
    try {
      KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(2048);
      KeyPair keyPair = kpg.generateKeyPair();

      this.publicKey = keyPair.getPublic();
      this.privateKey = keyPair.getPrivate();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new RuntimeException("Unable to initialize RSA key pair");
    }
  }

  public PublicKey getPublicKey() {
    return publicKey;
  }

  public PrivateKey getPrivateKey() {
    return privateKey;
  }
}
