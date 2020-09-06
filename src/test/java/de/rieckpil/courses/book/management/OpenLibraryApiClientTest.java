package de.rieckpil.courses.book.management;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static reactor.netty.tcp.TcpClient.create;

class OpenLibraryApiClientTest {

  private MockWebServer mockWebServer;
  private OpenLibraryApiClient cut;

  private static final String ISBN = "9780596004651";

  private static String VALID_RESPONSE;

  static {
    try {
      VALID_RESPONSE = new String(OpenLibraryApiClientTest.class
        .getClassLoader()
        .getResourceAsStream("stubs/openlibrary/success-" + ISBN + ".json")
        .readAllBytes());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BeforeEach
  public void setup() throws IOException {

    TcpClient tcpClient = create()
      .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1_000)
      .doOnConnected(connection ->
        connection.addHandlerLast(new ReadTimeoutHandler(1))
          .addHandlerLast(new WriteTimeoutHandler(1)));

    this.mockWebServer = new MockWebServer();
    this.mockWebServer.start();

    this.cut = new OpenLibraryApiClient(
      WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
        .baseUrl(mockWebServer.url("/").toString())
        .build()
    );
  }

  @AfterEach
  public void shutdown() throws IOException {
    this.mockWebServer.shutdown();
  }

  @Test
  void notNull() {
    assertNotNull(cut);
    assertNotNull(mockWebServer);
  }

  @Test
  void shouldReturnBookWhenResultIsSuccess() throws InterruptedException {

    MockResponse mockResponse = new MockResponse()
      .addHeader("Content-Type", "application/json; charset=utf-8")
      .setBody(VALID_RESPONSE);

    this.mockWebServer.enqueue(mockResponse);

    Book result = cut.fetchMetadataForBook(ISBN);

    assertEquals("9780596004651", result.getIsbn());
    assertEquals("Head first Java", result.getTitle());
    assertEquals("https://covers.openlibrary.org/b/id/388761-S.jpg", result.getThumbnailUrl());
    assertEquals("Kathy Sierra", result.getAuthor());
    assertEquals("Your brain on Java--a learner's guide--Cover.Includes index.", result.getDescription());
    assertEquals("Java (Computer program language)", result.getGenre());
    assertEquals("O'Reilly", result.getPublisher());
    assertEquals(619, result.getPages());

    assertNull(result.getId());

    RecordedRequest recordedRequest = this.mockWebServer.takeRequest();
    assertEquals("/api/books?jscmd=data&format=json&bibkeys=ISBN:" + ISBN, recordedRequest.getPath());
  }

  @Test
  void shouldReturnBookWhenResultIsSuccessButLackingAllInformation() {

    String response = """
       {
        "ISBN:9780596004651": {
          "publishers": [
            {
              "name": "O'Reilly"
            }
          ],
          "title": "Head second Java",
          "authors": [
            {
              "url": "https://openlibrary.org/authors/OL1400543A/Kathy_Sierra",
              "name": "Kathy Sierra"
            }
          ],
          "number_of_pages": 42,
          "cover": {
            "small": "https://covers.openlibrary.org/b/id/388761-S.jpg",
            "large": "https://covers.openlibrary.org/b/id/388761-L.jpg",
            "medium": "https://covers.openlibrary.org/b/id/388761-M.jpg"
          }
         }
       }
      """;

    this.mockWebServer.enqueue(new MockResponse()
      .addHeader("Content-Type", "application/json; charset=utf-8")
      .setResponseCode(200)
      .setBody(response));

    Book result = cut.fetchMetadataForBook(ISBN);

    assertEquals("9780596004651", result.getIsbn());
    assertEquals("Head second Java", result.getTitle());
    assertEquals("https://covers.openlibrary.org/b/id/388761-S.jpg", result.getThumbnailUrl());
    assertEquals("Kathy Sierra", result.getAuthor());
    assertEquals("n.A", result.getDescription());
    assertEquals("n.A", result.getGenre());
    assertEquals("O'Reilly", result.getPublisher());
    assertEquals(42, result.getPages());

    assertNull(result.getId());
  }

  @Test
  void shouldPropagateExceptionWhenRemoteSystemIsDown() {
    assertThrows(RuntimeException.class, () -> {
      this.mockWebServer.enqueue(new MockResponse()
        .setResponseCode(500)
        .setBody("Sorry, system is down :("));

      cut.fetchMetadataForBook(ISBN);
    });
  }

  @Test
  void shouldRetryWhenRemoteSystemIsSlowOrFailing() {

    this.mockWebServer.enqueue(new MockResponse()
      .setResponseCode(500)
      .setBody("Sorry, system is down :("));

    this.mockWebServer.enqueue(new MockResponse()
      .addHeader("Content-Type", "application/json; charset=utf-8")
      .setResponseCode(200)
      .setBody(VALID_RESPONSE)
      .setBodyDelay(2, TimeUnit.SECONDS));

    this.mockWebServer.enqueue(new MockResponse()
      .addHeader("Content-Type", "application/json; charset=utf-8")
      .setResponseCode(200)
      .setBody(VALID_RESPONSE));


    Book result = cut.fetchMetadataForBook(ISBN);

    assertEquals("9780596004651", result.getIsbn());
    assertNull(result.getId());
  }
}
