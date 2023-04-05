package de.rieckpil.courses.book.management;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

  private final BookManagementService bookManagementService;

  public BookController(BookManagementService bookManagementService) {
    this.bookManagementService = bookManagementService;
  }

  @GetMapping
  public List<Book> getAvailableBooks() {
    return bookManagementService.getAllBooks();
  }
}
