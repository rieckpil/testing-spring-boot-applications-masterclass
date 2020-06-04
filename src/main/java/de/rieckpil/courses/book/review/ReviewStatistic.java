package de.rieckpil.courses.book.review;

import java.math.BigDecimal;

public interface ReviewStatistic {
  Long getId();

  Long getRatings();

  Long getUsers();

  String getIsbn();

  BigDecimal getAvg();
}
