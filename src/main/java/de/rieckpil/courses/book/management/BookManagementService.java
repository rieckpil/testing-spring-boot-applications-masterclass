package de.rieckpil.courses.book.management;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BookManagementService {

  private final BookRepository bookRepository;

  public BookManagementService(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  public List<Book> getAllBooks() {
    return bookRepository.findAll();
  }
}
