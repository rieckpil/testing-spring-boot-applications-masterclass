package de.rieckpil.courses.book.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.book.management.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
@Transactional
public class ReviewService {

  private final ReviewVerifier reviewVerifier;
  private final UserService userService;
  private final BookRepository bookRepository;
  private final ReviewRepository reviewRepository;

  public ReviewService(ReviewVerifier reviewVerifier, UserService userService, BookRepository bookRepository, ReviewRepository reviewRepository) {
    this.reviewVerifier = reviewVerifier;
    this.userService = userService;
    this.bookRepository = bookRepository;
    this.reviewRepository = reviewRepository;
  }

  public Long createBookReview(String isbn, BookReviewRequest bookReviewRequest, String userName, String email) {

    Book book = bookRepository.findByIsbn(isbn);

    if (book == null) {
      throw new IllegalArgumentException("Book not found");
    }

    if (reviewVerifier.doesMeetQualityStandards(bookReviewRequest.getReviewContent())) {
      Review review = new Review();

      review.setBook(book);
      review.setContent(bookReviewRequest.getReviewContent());
      review.setTitle(bookReviewRequest.getReviewTitle());
      review.setRating(bookReviewRequest.getRating());
      review.setUser(userService.getOrCreateUser(userName, email));
      review.setCreatedAt(LocalDateTime.now());

      review = reviewRepository.save(review);

      return review.getId();
    } else {
      throw new IllegalArgumentException("Not meeting standards");
    }
  }

  public ArrayNode getAllReviews() {
    ObjectMapper objectMapper = new ObjectMapper();
    ArrayNode result = objectMapper.createArrayNode();

    reviewRepository.findAll().stream().map(review -> {
      ObjectNode objectNode = objectMapper.createObjectNode();
      objectNode.put("reviewContent", review.getContent());
      objectNode.put("reviewTitle", review.getTitle());
      objectNode.put("rating", review.getRating());
      objectNode.put("bookIsbn", review.getBook().getIsbn());
      objectNode.put("bookTitle", review.getBook().getTitle());
      objectNode.put("submittedBy", review.getUser().getName());
      objectNode.put("submittedAt",
        review.getCreatedAt().atZone(ZoneId.of("Europe/Berlin")).toInstant().toEpochMilli());
      return objectNode;
    }).forEach(review -> result.add(review));

    return result;
  }
}
