import React from "react";
import {Card, Container, Header} from "semantic-ui-react";
import {Book, ReviewStatistic} from "./types";
import BookComponent from "./BookComponent";

type Props = {
  availableBooks: Book[],
  reviewStatistics: ReviewStatistic[]
}

const LatestBookListComponent: React.FC<Props> = ({availableBooks, reviewStatistics}) => {
  return (
    <Container>
      <Header as='h2' textAlign='center'>Latest books</Header>
      <Card.Group itemsPerRow='1'>
        {availableBooks.map((book, index) =>
          <BookComponent
            metadata={book}
            statistics={reviewStatistics.length === 0 ? undefined : reviewStatistics.find(statistic => statistic.isbn === book.isbn) || {
              ratings: 0,
              avg: 0,
              isbn: book.isbn,
              bookId: 42
            }}
          />
        )}
      </Card.Group>
    </Container>
  );
}

export default LatestBookListComponent;
