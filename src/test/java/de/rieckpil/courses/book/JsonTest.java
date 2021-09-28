package de.rieckpil.courses.book;

import com.jayway.jsonpath.JsonPath;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class JsonTest {

  @Test
  void testWithJSONAssert() throws JSONException {
    String result = "{\"name\": \"duke\", \"age\":\"42\", \"hobbies\": [\"soccer\", \"java\"]}";

  }

  @Test
  void testWithJsonPath() {
    String result = "{\"age\":\"42\", \"name\": \"duke\", \"tags\":[\"java\", \"jdk\"], \"orders\": [42, 42, 16]}";
  }
}
