package de.rieckpil.courses.book.management;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;

@Component
public class OpenLibraryRestTemplateApiClient {

  private final RestTemplate restTemplate;

  public OpenLibraryRestTemplateApiClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder
      .rootUri("https://openlibrary.org")
      .setConnectTimeout(Duration.ofSeconds(2))
      .setReadTimeout(Duration.ofSeconds(2))
      .build();
  }

  public Book fetchMetadataForBook(String isbn) {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("X-Custom-Auth", "Duke42");
    headers.set("X-Customer-Id", "42");

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    ObjectNode result = restTemplate
      .exchange("/api/books?jscmd=data&format=json&bibkeys={isbn}",
        HttpMethod.GET, entity, ObjectNode.class, "ISBN:" + isbn).getBody();

    JsonNode content = result.get("ISBN:" + isbn);

    return convertToBook(isbn, content);
  }

  private Book convertToBook(String isbn, JsonNode content) {
    Book book = new Book();
    book.setIsbn(isbn);
    book.setThumbnailUrl(content.get("cover").get("small").asText());
    book.setTitle(content.get("title").asText());
    book.setAuthor(content.get("authors").get(0).get("name").asText());
    book.setPublisher(content.get("publishers").get(0).get("name").asText("n.A."));
    book.setPages(content.get("number_of_pages").asLong(0));
    book.setDescription(content.get("notes") == null ? "n.A" : content.get("notes").asText("n.A."));
    book.setGenre(content.get("subjects") == null ? "n.A" : content.get("subjects").get(0).get("name").asText("n.A."));
    return book;
  }
}
