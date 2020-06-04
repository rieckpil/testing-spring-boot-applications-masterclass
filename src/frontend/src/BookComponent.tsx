import React from "react";
import {Book} from "./types";
import {Card, Image, Label} from "semantic-ui-react";

const BookComponent: React.FC<Book> = ({isbn, author, description, genre, thumbnailUrl, title, pages, publisher}) => {
  return (
    <Card>
      <Card.Content>
        <Image
          floated='right'
          size='mini'
          src={thumbnailUrl}
        />
        <Card.Header>{title}</Card.Header>
        <Card.Meta>from {author}</Card.Meta>
        <Card.Description>
          {description}
        </Card.Description>
      </Card.Content>
      <Card.Content extra>
        <Label>
          {publisher}
        </Label>
        <Label>
          ISBN: {isbn}
        </Label>
        <Label>
          {pages} pages
        </Label>
        <Label as='a' tag>
          {genre}
        </Label>
      </Card.Content>
    </Card>
  )
}

export default BookComponent
