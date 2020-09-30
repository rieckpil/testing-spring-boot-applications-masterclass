import React from "react";
import {Button, Container, Header, Item, Segment} from "semantic-ui-react";
import {Link} from "react-router-dom";
import {BookReview} from "./types";
import BookReviewComponent from "./BookReviewComponent";

type Props = {
  recentReviews: BookReview[],
  bestRatedReviews: BookReview[]
}

const LatestReviewComponent: React.FC<Props> = ({recentReviews, bestRatedReviews}) => {
  return (
    <Container style={{marginTop: '20px'}}>
      <Header as='h2' textAlign='center'>Best rated reviews</Header>
      <Segment>
        <Button
          as={Link}
          floated='right'
          to='/submit-review'
          size='tiny'
          circular
          icon='add'/>
        <Item.Group>
          {bestRatedReviews.map((review, index) =>
            <BookReviewComponent
              key={index}
              elementId={index}
              isModerator={false}
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
      </Segment>
      <Header as='h2' textAlign='center'>Recently submitted reviews</Header>
      <Segment>
        <Button
          as={Link}
          floated='right'
          to='/submit-review'
          size='tiny'
          circular
          icon='add'/>
        <Item.Group>
          {recentReviews.map((review, index) =>
            <BookReviewComponent
              key={index}
              elementId={index}
              isModerator={false}
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
      </Segment>
    </Container>
  );
}

export default LatestReviewComponent;
