package de.rieckpil.courses.book.management;

import java.time.Duration;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

@Component
public class OpenLibraryApiClient {

  private final WebClient openLibraryWebClient;

  public OpenLibraryApiClient(WebClient openLibraryWebClient) {
    this.openLibraryWebClient = openLibraryWebClient;
  }

  public Book fetchMetadataForBook(String isbn) {

    ObjectNode result =
        openLibraryWebClient
            .get()
            .uri(
                "/api/books",
                uriBuilder ->
                    uriBuilder
                        .queryParam("jscmd", "data")
                        .queryParam("format", "json")
                        .queryParam("bibkeys", isbn)
                        .build())
            .retrieve()
            .bodyToMono(ObjectNode.class)
            .retryWhen(Retry.fixedDelay(2, Duration.ofMillis(200)))
            .block();

    JsonNode content = result.get(isbn);

    return convertToBook(isbn, content);
  }

  private Book convertToBook(String isbn, JsonNode content) {
    Book book = new Book();
    book.setIsbn(isbn);
    book.setThumbnailUrl(content.get("cover").get("small").asString());
    book.setTitle(content.get("title").asString());
    book.setAuthor(content.get("authors").get(0).get("name").asString());
    book.setPublisher(content.get("publishers").get(0).get("name").asString("n.A."));
    book.setPages(content.get("number_of_pages").asLong(0));
    book.setDescription(
        content.get("notes") == null ? "n.A" : content.get("notes").asString("n.A."));
    book.setGenre(
        content.get("subjects") == null
            ? "n.A"
            : content.get("subjects").get(0).get("name").asString("n.A."));
    return book;
  }
}
