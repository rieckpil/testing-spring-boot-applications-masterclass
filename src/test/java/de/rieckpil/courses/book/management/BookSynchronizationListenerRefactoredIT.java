package de.rieckpil.courses.book.management;

import com.nimbusds.jose.JOSEException;
import de.rieckpil.courses.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

class BookSynchronizationListenerRefactoredIT extends AbstractIntegrationTest {

  @Autowired private WebTestClient webTestClient;

  @Autowired private BookRepository bookRepository;

  @Test
  void shouldGetSuccessWhenClientIsAuthenticated() throws JOSEException {}

  @Test
  void shouldReturnBookFromAPIWhenApplicationConsumesNewSyncRequest() {}
}
