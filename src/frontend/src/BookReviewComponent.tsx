import React from "react";
import {Button, Icon, Item, Rating} from "semantic-ui-react";
import {BookReview} from "./types";

type Props = BookReview & {
  onDelete: () => void,
  isModerator: boolean
}

const BookReviewComponent: React.FC<Props> = ({bookTitle, bookIsbn, reviewTitle, bookThumbnailUrl, reviewContent, rating, submittedBy, submittedAt, onDelete, isModerator}) => {
  return (
    <Item>
      <Item.Image size='mini' src={bookThumbnailUrl}/>

      <Item.Content>
        <Item.Header>{reviewTitle}</Item.Header>
        <Item.Meta>
          <span>{bookTitle}</span>
          <span> | </span>
          <span>ISBN: {bookIsbn}</span>
        </Item.Meta>

        <Item.Description>{reviewContent}</Item.Description>
        <Item.Extra>
          <span>Rating: <Rating icon='star' rating={rating} maxRating={5} disabled/></span>
          <span><Icon color='green' name='check'/>Confirmed reader</span>
          <span>Submitted by {submittedBy} on {new Date(submittedAt).toLocaleString()}</span>
          {isModerator ?
            <Button
              floated='right'
              compact
              color='red'
              onClick={() => onDelete()}>
              Delete
            </Button> : ''
          }
        </Item.Extra>
      </Item.Content>
    </Item>
  );
}

export default BookReviewComponent;
