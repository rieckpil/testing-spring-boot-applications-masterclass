package de.rieckpil.courses.book.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query(value = "SELECT * FROM reviews", nativeQuery = true)
  List<Review> getHighestRatedReviews(Long amount);

  void deleteByIdAndBookIsbn(Long reviewId, String isbn);
}
