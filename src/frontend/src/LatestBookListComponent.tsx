import React from "react";
import {Card, Container, Header} from "semantic-ui-react";
import {Book} from "./types";
import BookComponent from "./BookComponent";

type Props = {
  availableBooks: Book[]
}

const LatestBookListComponent: React.FC<Props> = ({availableBooks}) => {
  return (
    <Container>
      <Header as='h2' textAlign='center'>Latest books</Header>
      <Card.Group itemsPerRow='1'>
        {availableBooks.map((book, index) =>
          <BookComponent
            title={book.title}
            isbn={book.isbn}
            author={book.author}
            genre={book.genre}
            thumbnailUrl={book.thumbnailUrl}
            description={book.description}
            publisher={book.publisher}
            pages={book.pages}
          />
        )}
      </Card.Group>
    </Container>
  );
}

export default LatestBookListComponent;
