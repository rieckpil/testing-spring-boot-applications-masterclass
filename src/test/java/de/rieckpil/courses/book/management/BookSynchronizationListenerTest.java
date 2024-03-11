package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSynchronizationListenerTest {

  private static final String VALID_ISBN = "1234567891234";

  @Mock private BookRepository bookRepository;

  @Mock private OpenLibraryApiClient openLibraryApiClient;

  @InjectMocks private BookSynchronizationListener cut;

  @Captor private ArgumentCaptor<Book> bookArgumentCaptor;

  @Test
  void shouldRejectBookWhenIsbnIsMalformed() {}

  @Test
  void shouldNotOverrideWhenBookAlreadyExists() {}

  @Test
  void shouldThrowExceptionWhenProcessingFails() {}

  @Test
  void shouldStoreBookWhenNewAndCorrectIsbn() {}
}
