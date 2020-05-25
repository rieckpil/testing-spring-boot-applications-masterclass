package de.rieckpil.courses.book.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private final WebClient openLibraryWebClient;

  public BookController(WebClient openLibraryWebClient) {
    this.openLibraryWebClient = openLibraryWebClient;
  }

  @GetMapping
  public ArrayNode getAvailableBooks() {

    List<String> isbnList = List.of("978-0321751041", "978-0321160768", "978-0596004651");

    ObjectMapper objectMapper = new ObjectMapper();

    ArrayNode bookList = objectMapper.createArrayNode();

    for (String isbn : isbnList) {
      ObjectNode result = openLibraryWebClient.get().uri("/api/books",
        uriBuilder -> uriBuilder.queryParam("jscmd", "data")
          .queryParam("format", "json")
          .queryParam("bibkeys", "ISBN:" + isbn)
          .build()).retrieve().bodyToMono(ObjectNode.class).block();

      System.out.println(result);
      ObjectNode book = objectMapper.createObjectNode();
      book.put("key", isbn);
      book.put("text", result.get("ISBN:" + isbn).get("title").asText() + " - " + result.get("ISBN:" + isbn).get("authors").get(0).get("name").asText());
      book.put("value", isbn);
      book.set("image", objectMapper.createObjectNode().put("src", result.get("ISBN:" + isbn).get("cover").get("small").asText()));

      bookList.add(book);
    }
    return bookList;
  }
}
