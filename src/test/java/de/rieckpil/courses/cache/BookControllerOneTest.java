package de.rieckpil.courses.cache;

import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookController;
import de.rieckpil.courses.book.management.BookManagementService;
import de.rieckpil.courses.config.WebSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
// see
// https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.7-Release-Notes#migrating-from-websecurityconfigureradapter-to-securityfilterchain
@Import(WebSecurityConfig.class)
class BookControllerOneTest {

  @MockBean private BookManagementService bookManagementService;

  @Autowired private MockMvc mockMvc;

  @Test
  void shouldGetEmptyArrayWhenNoBooksExists() throws Exception {}

  @Test
  void shouldNotReturnXML() throws Exception {}

  @Test
  void shouldGetBooksWhenServiceReturnsBooks() throws Exception {}

  private Book createBook(
      Long id,
      String isbn,
      String title,
      String author,
      String description,
      String genre,
      Long pages,
      String publisher,
      String thumbnailUrl) {
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
