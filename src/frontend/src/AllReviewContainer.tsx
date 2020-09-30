import React, {useEffect, useState} from "react";
import {Container, Header, Item, Message} from "semantic-ui-react";
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
      <Header as='h2' textAlign='center'>Browse through all book reviews</Header>
          {reviews.length === 0 ?
            <Message
            icon='inbox'
            header='Sad news :('
            content={<span>There are no books reviews yet. Consider <Link to='/submit-review'>adding</Link> the first.</span>}
            /> : ''}
      <Item.Group id='reviews' divided>
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
          </Item.Group>
    </Container>
  );
}

export default connector(AllReviewContainer);
