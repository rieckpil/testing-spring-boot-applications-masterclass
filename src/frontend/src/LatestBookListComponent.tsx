import React from "react";
import {Container, Stack, Title} from "@mantine/core";
import {Book, ReviewStatistic} from "./types";
import BookComponent from "./BookComponent";

type Props = {
  availableBooks: Book[],
  reviewStatistics: ReviewStatistic[]
}

const LatestBookListComponent: React.FC<Props> = ({availableBooks, reviewStatistics}) => {
  return (
    <Container>
      <Title order={2} ta="center" mb="md">Latest books</Title>
      <Stack>
        {availableBooks.map((book, index) =>
          <BookComponent
            key={index}
            metadata={book}
            statistics={reviewStatistics.length === 0 ? undefined : reviewStatistics.find(statistic => statistic.isbn === book.isbn) || {
              ratings: 0,
              avg: 0,
              isbn: book.isbn,
              bookId: 42
            }}
          />
        )}
      </Stack>
    </Container>
  );
}

export default LatestBookListComponent;
