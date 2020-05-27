package de.rieckpil.courses.book.management;

import org.springframework.stereotype.Service;

import java.util.List;

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
