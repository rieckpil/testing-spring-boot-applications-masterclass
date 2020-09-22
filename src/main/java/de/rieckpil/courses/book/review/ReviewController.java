package de.rieckpil.courses.book.review;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/books")
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping("/reviews")
  public ArrayNode getAllReviews(@RequestParam(name = "size", defaultValue = "20") Integer size,
                                 @RequestParam(name = "orderBy", defaultValue = "none") String orderBy) {
    return reviewService.getAllReviews(size, orderBy);
  }

  @GetMapping("/reviews/statistics")
  public ArrayNode getReviewStatistics() {
    return reviewService.getReviewStatistics();
  }

  @PostMapping("/{isbn}/reviews")
  public ResponseEntity<Void> createBookReview(@PathVariable("isbn") String isbn,
                                               @RequestBody @Valid BookReviewRequest bookReviewRequest,
                                               JwtAuthenticationToken jwt,
                                               UriComponentsBuilder uriComponentsBuilder) {

    Long reviewId = reviewService.createBookReview(isbn, bookReviewRequest,
      jwt.getTokenAttributes().get("preferred_username").toString(),
      jwt.getTokenAttributes().get("email").toString());

    UriComponents uriComponents = uriComponentsBuilder.path("/api/books/{isbn}/reviews/{reviewId}").buildAndExpand(isbn, reviewId);
    return ResponseEntity.created(uriComponents.toUri()).build();
  }

  @DeleteMapping("/{isbn}/reviews/{reviewId}")
  @PreAuthorize("hasAuthority('ROLE_moderator')")
  public void deleteBookReview(@PathVariable String isbn, @PathVariable Long reviewId) {
    reviewService.deleteReview(isbn, reviewId);
  }

  @GetMapping("/{isbn}/reviews/{reviewId}")
  public ObjectNode getReviewById(@PathVariable String isbn, @PathVariable Long reviewId) {
    return reviewService.getReviewById(isbn, reviewId);
  }
}
