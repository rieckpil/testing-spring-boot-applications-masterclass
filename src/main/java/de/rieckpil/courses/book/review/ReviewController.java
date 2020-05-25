package de.rieckpil.courses.book.review;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class ReviewController {

  @GetMapping("/reviews")
  public String getAllReviews() {
    return "Hello World";
  }

  @PostMapping("/{bookId}/reviews")
  public String createBookReview(@PathVariable("bookId") Long bookId) {
    return "Hello World";
  }

  @PutMapping("/{bookId}/reviews/{reviewId}")
  public String updateBookReview() {
    return "Hello World";
  }

  @DeleteMapping("/{bookId}/reviews/{reviewId}")
  public String deleteBookReview() {
    return "Hello World";
  }

  @GetMapping("/{bookId}/reviews")
  public String getReviewsForBook() {
    return "Hello World";
  }

}
