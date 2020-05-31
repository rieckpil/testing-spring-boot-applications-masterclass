package de.rieckpil.courses.book.review;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/books")
public class ReviewController {

  private final ReviewService reviewService;

  public ReviewController(ReviewService reviewService) {
    this.reviewService = reviewService;
  }

  @GetMapping("/reviews")
  @CrossOrigin(origins = "*")
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
  public String deleteBookReview() {
    return "Hello World";
  }

  @GetMapping("/{isbn}/reviews")
  public String getReviewsForBook() {
    return "Hello World";
  }

}
