package de.rieckpil.courses.book.review;

import com.fasterxml.jackson.databind.node.ArrayNode;
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
@CrossOrigin(origins = "*")
@RequestMapping("/api/books")
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping("/reviews")
  public ArrayNode getAllReviews() {
    return reviewService.getAllReviews();
  }

  @PostMapping("/{isbn}/reviews")
  public ResponseEntity<Void> createBookReview(@PathVariable("isbn") String isbn,
                                               @RequestBody @Valid BookReviewRequest bookReviewRequest,
                                               JwtAuthenticationToken jwt,
                                               UriComponentsBuilder uriComponentsBuilder) {

    Long reviewId = reviewService.createBookReview(isbn, bookReviewRequest,
      jwt.getTokenAttributes().get("preferred_username").toString(),
      jwt.getTokenAttributes().get("email").toString());

    UriComponents uriComponents = uriComponentsBuilder.path("/books/{isbn}/reviews/{reviewId}").buildAndExpand(isbn, reviewId);
    return ResponseEntity.created(uriComponents.toUri()).build();
  }

  @DeleteMapping("/{isbn}/reviews/{reviewId}")
  @PreAuthorize("hasAuthority('SCOPE_moderator')")
  public void deleteBookReview(@PathVariable String isbn, @PathVariable Long reviewId) {
    reviewService.deleteReview(isbn, reviewId);
  }

  @GetMapping("/{isbn}/reviews")
  public String getReviewsForBook() {
    return "Hello World";
  }

}
