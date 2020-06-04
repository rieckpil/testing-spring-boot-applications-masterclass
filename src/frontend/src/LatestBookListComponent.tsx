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
            statistics={reviewStatistics && reviewStatistics.find(statistic => statistic.isbn === book.isbn)}
          />
        )}
      </Card.Group>
    </Container>
  );
}

export default LatestBookListComponent;
