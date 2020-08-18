package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTest {

  @MockBean
  private BookManagementService bookManagementService;

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldGetEmptyArrayWhenNoBooksExists() throws Exception {
    MvcResult mvcResult = this.mockMvc
      .perform(get("/api/books")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON))
      .andExpect(status().is(200))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.size()", is(0)))
      .andDo(print())
      .andReturn();
  }

  @Test
  void shouldNotReturnXML() throws Exception {
    this.mockMvc
      .perform(get("/api/books")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_XML))
      .andExpect(status().isNotAcceptable());
  }

  @Test
  void shouldGetBooksWhenServiceReturnsBooks() throws Exception {

    Book bookOne = createBook(1L, "42", "Java 14", "Mike", "Good book",
      "Software Engineering", 200L, "Oracle", "ftp://localhost:42");

    Book bookTwo = createBook(2L, "84", "Java 15", "Duke", "Good book",
      "Software Engineering", 200L, "Oracle", "ftp://localhost:42");

    when(bookManagementService.getAllBooks()).thenReturn(List.of(bookOne, bookTwo));

    this.mockMvc
      .perform(get("/api/books")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON))
      .andExpect(status().is(200))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.size()", is(2)))
      .andExpect(jsonPath("$[0].isbn", is("42")))
      .andExpect(jsonPath("$[0].id").doesNotExist())
      .andExpect(jsonPath("$[0].title", is("Java 14")))
      .andExpect(jsonPath("$[1].isbn", is("84")))
      .andExpect(jsonPath("$[1].id").doesNotExist())
      .andExpect(jsonPath("$[1].title", is("Java 15")));
  }

  private Book createBook(Long id, String isbn, String title, String author, String description, String genre, Long pages, String publisher, String thumbnailUrl) {
    Book result = new Book();
    result.setId(id);
    result.setIsbn(isbn);
    result.setTitle(title);
    result.setAuthor(author);
    result.setDescription(description);
    result.setGenre(genre);
    result.setPages(pages);
    result.setPublisher(publisher);
    result.setThumbnailUrl(thumbnailUrl);
    return result;
  }

}
