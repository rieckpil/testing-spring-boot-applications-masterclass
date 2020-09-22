package de.rieckpil.courses.book.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.rieckpil.courses.book.management.Book;
import de.rieckpil.courses.book.management.BookRepository;
import de.rieckpil.courses.book.management.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Transactional
public class ReviewService {

  private final ReviewVerifier reviewVerifier;
  private final UserService userService;
  private final BookRepository bookRepository;
  private final ReviewRepository reviewRepository;
  private final ObjectMapper objectMapper = new ObjectMapper();

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
      throw new BadReviewQualityException("Not meeting standards");
    }
  }

  public ArrayNode getReviewStatistics() {
    ArrayNode result = objectMapper.createArrayNode();

    reviewRepository.getReviewStatistics()
      .stream()
      .map(this::mapReviewStatistic)
      .forEach(result::add);

    return result;
  }

  public ArrayNode getAllReviews(Integer size, String orderBy) {
    ArrayNode result = objectMapper.createArrayNode();

    List<Review> requestedReviews;

    if (orderBy.equals("rating")) {
      requestedReviews = reviewRepository.findTop5ByOrderByRatingDescCreatedAtDesc();
    } else {
      requestedReviews = reviewRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, size));
    }

    requestedReviews
      .stream()
      .map(this::mapReview)
      .forEach(result::add);

    return result;
  }

  private ObjectNode mapReview(Review review) {
    ObjectNode objectNode = objectMapper.createObjectNode();
    objectNode.put("reviewId", review.getId());
    objectNode.put("reviewContent", review.getContent());
    objectNode.put("reviewTitle", review.getTitle());
    objectNode.put("rating", review.getRating());
    objectNode.put("bookIsbn", review.getBook().getIsbn());
    objectNode.put("bookTitle", review.getBook().getTitle());
    objectNode.put("bookThumbnailUrl", review.getBook().getThumbnailUrl());
    objectNode.put("submittedBy", review.getUser().getName());
    objectNode.put("submittedAt",
      review.getCreatedAt().atZone(ZoneId.of("Europe/Berlin")).toInstant().toEpochMilli());
    return objectNode;
  }

  private ObjectNode mapReviewStatistic(ReviewStatistic reviewStatistic) {
    ObjectNode statistic = objectMapper.createObjectNode();
    statistic.put("bookId", reviewStatistic.getId());
    statistic.put("isbn", reviewStatistic.getIsbn());
    statistic.put("avg", reviewStatistic.getAvg());
    statistic.put("ratings", reviewStatistic.getRatings());
    return statistic;
  }

  public void deleteReview(String isbn, Long reviewId) {
    this.reviewRepository.deleteByIdAndBookIsbn(reviewId, isbn);
  }

  public ObjectNode getReviewById(String isbn, Long reviewId) {
    return this.reviewRepository.findByIdAndBookIsbn(reviewId, isbn)
      .map(this::mapReview)
      .orElseThrow(ReviewNotFoundException::new);
  }
}
