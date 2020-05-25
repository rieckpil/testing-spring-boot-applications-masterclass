package de.rieckpil.courses.book.management;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

@Entity
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  private String isbn;

  private String author;

  private String genre;

  private String thumbnailUrl;

  private LocalDate publishedAt;

  private String publisher;

  private Long pages;
}
