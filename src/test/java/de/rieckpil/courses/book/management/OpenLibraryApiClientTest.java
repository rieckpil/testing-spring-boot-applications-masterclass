package de.rieckpil.courses.book.management;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenLibraryApiClientTest {

  private MockWebServer mockWebServer;
  private OpenLibraryApiClient cut;

  private static final String ISBN = "9780596004651";

  private static String VALID_RESPONSE;

  @Test
  void notNull() {}

  @Test
  void shouldReturnBookWhenResultIsSuccess() throws InterruptedException {}

  @Test
  void shouldReturnBookWhenResultIsSuccessButLackingAllInformation() {}

  @Test
  void shouldPropagateExceptionWhenRemoteSystemIsDown() {}

  @Test
  void shouldRetryWhenRemoteSystemIsSlowOrFailing() {}
}
