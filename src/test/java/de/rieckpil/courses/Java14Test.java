package de.rieckpil.courses;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class Java14Test {

  @Test
  void shouldAllowJava14PreviewFeatures() {
    String json = """
      {
        "name":"Duke"
      }
      """;

    assertNotNull(json);
  }
}
