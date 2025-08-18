package de.rieckpil.courses.cache;

import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookController;
import de.rieckpil.courses.book.management.BookManagementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BookController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BookControllerTwoTest {

  @MockitoBean private BookManagementService bookManagementService;

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
