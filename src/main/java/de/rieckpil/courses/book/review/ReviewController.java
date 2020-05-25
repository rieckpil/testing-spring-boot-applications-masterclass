package de.rieckpil.courses.book.review;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class ReviewController {

  @GetMapping("/reviews")
  public String getAllReviews() {
    return "Hello World";
  }

  @PostMapping("/{isbn}/reviews")
  public ResponseEntity<Void> createBookReview(@PathVariable("isbn") Long isbn, @RequestBody ObjectNode objectNode) {
    System.out.println(objectNode);
    return ResponseEntity.created(null).build();
  }

  @DeleteMapping("/{isbn}/reviews/{reviewId}")
  public String deleteBookReview() {
    return "Hello World";
  }

  @GetMapping("/{isbn}/reviews")
  public String getReviewsForBook() {
    return "Hello World";
  }

}
