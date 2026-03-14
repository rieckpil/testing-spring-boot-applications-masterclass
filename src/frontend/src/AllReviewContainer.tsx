import React, {useEffect, useState} from "react";
import {Alert, Container, Stack, Title} from "@mantine/core";
import {IconInbox} from "@tabler/icons-react";
import {BookReview, RootState} from "./types";
import BookReviewComponent from "./BookReviewComponent";
import {connect, ConnectedProps} from "react-redux";
import {Link} from "react-router-dom";

const mapState = (state: RootState) => ({
  isModerator: state.authentication.details?.roles?.includes('moderator'),
  token: state.authentication.details?.token
});

const connector = connect(mapState);

type Props = ConnectedProps<typeof connector>

const AllReviewContainer: React.FC<Props> = ({token, isModerator}) => {

  const [reviews, setReviews] = useState<BookReview[]>([])

  useEffect(() => {
    fetch(`/api/books/reviews`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: BookReview[]) => {
        setReviews(result)
      })
  }, [])

  const deleteReview = (bookIsbn: string, reviewId: number) => {
    fetch(`/api/books/${bookIsbn}/reviews/${reviewId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      },
    }).then(result => {
      if (result.status === 200) {
        setReviews(prevState => prevState.filter(review => review.reviewId !== reviewId))
      }
    })
  }

  return (
    <Container>
      <Title order={2} ta="center" mb="md">Browse through all book reviews</Title>
      {reviews.length === 0 && (
        <Alert icon={<IconInbox/>} title="Sad news :(" color="blue" mb="md">
          There are no book reviews yet. Consider <Link to='/submit-review'>adding</Link> the first.
        </Alert>
      )}
      <Stack id="reviews">
        {reviews.map((review, index) =>
          <BookReviewComponent
            key={index}
            elementId={index}
            isModerator={isModerator ? isModerator : false}
            onDelete={() => deleteReview(review.bookIsbn, review.reviewId)}
            reviewId={review.reviewId}
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
      </Stack>
    </Container>
  );
}

export default connector(AllReviewContainer);
