import React from "react";
import {Button, Icon, Item, Rating} from "semantic-ui-react";
import {BookReview} from "./types";

type Props = BookReview & {
  onDelete?: () => void,
  isModerator: boolean,
  elementId: number
}

const BookReviewComponent: React.FC<Props> = ({elementId, bookTitle, bookIsbn, reviewTitle, bookThumbnailUrl, reviewContent, rating, submittedBy, submittedAt, onDelete, isModerator}) => {
  return (
    <Item id={'review-' + elementId}>
      <Item.Image size='mini' src={bookThumbnailUrl}/>

      <Item.Content>
        <Item.Header className='review-title'>{reviewTitle}</Item.Header>
        <Item.Meta>
          <span>{bookTitle}</span>
          <span> | </span>
          <span>ISBN: {bookIsbn}</span>
        </Item.Meta>

        <Item.Description className='review-content'>{reviewContent}</Item.Description>
        <Item.Extra>
          <span>Rating: <Rating icon='star' rating={rating} maxRating={5} disabled/></span>
          <span><Icon color='green' name='check'/>Confirmed reader</span>
          <span>Submitted by {submittedBy} on {new Date(submittedAt).toLocaleString()}</span>
          {isModerator ?
            <Button
              floated='right'
              compact
              color='red'
              onClick={onDelete ? () => onDelete() : () => null}>
              Delete
            </Button> : ''
          }
        </Item.Extra>
      </Item.Content>
    </Item>
  );
}

export default BookReviewComponent;
