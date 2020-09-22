package de.rieckpil.courses.book.review;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  @Query(value =
    "SELECT id, ratings, isbn, avg " +
      "FROM books " +
      "JOIN " +
      "(SELECT book_id, ROUND(AVG(rating), 2) AS avg, COUNT(*) ratings FROM reviews group by book_id) AS statistics " +
      "ON statistics.book_id = id;",
    nativeQuery = true)
  List<ReviewStatistic> getReviewStatistics();

  List<Review> findTop5ByOrderByRatingDescCreatedAtDesc();

  List<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);

  void deleteByIdAndBookIsbn(Long reviewId, String isbn);

  Optional<Review> findByIdAndBookIsbn(Long reviewId, String isbn);
}
