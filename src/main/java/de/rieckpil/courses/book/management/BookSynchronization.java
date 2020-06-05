package de.rieckpil.courses.book.management;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BookSynchronization {

  private String isbn;

  @JsonCreator
  public BookSynchronization(@JsonProperty("isbn") String isbn) {
    this.isbn = isbn;
  }

  public String getIsbn() {
    return isbn;
  }

  public void setIsbn(String isbn) {
    this.isbn = isbn;
  }

  @Override
  public String toString() {
    return "BookUpdate{" +
      "isbn='" + isbn + '\'' +
      '}';
  }
}
