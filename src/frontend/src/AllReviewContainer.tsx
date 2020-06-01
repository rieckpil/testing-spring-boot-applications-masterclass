import React, {useEffect, useState} from "react";
import {Container, Grid, Header, Item} from "semantic-ui-react";
import {BookReview} from "./types";
import BookReviewComponent from "./BookReviewComponent";

const AllReviewContainer = () => {

  const [reviews, setReviews] = useState<BookReview[]>([])

  useEffect(() => {
    fetch(`http://localhost:8080/api/books/reviews`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: BookReview[]) => setReviews(result))
  }, [])

  return (
    <Container>
      <Header as='h1' textAlign='center'>Browse through all book reviews</Header>
      <Grid centered>
        <Grid.Column width={10}>
          {reviews.length === 0 ? 'There are no books reviews yet. Consider adding the first' : ''}
          <Item.Group divided>
            {reviews.map((review, index) =>
              <BookReviewComponent
                key={index}
                reviewContent={review.reviewContent}
                reviewTitle={review.reviewTitle}
                rating={review.rating}
                bookIsbn={review.bookIsbn}
                bookTitle={review.bookTitle}
                bookThumbnailUrl={review.bookThumbnailUrl}
                submittedBy={review.submittedBy}
                submittedAt={review.submittedAt}
              />
            )}
          </Item.Group>
        </Grid.Column>
      </Grid>
    </Container>
  );
}

export default AllReviewContainer;
