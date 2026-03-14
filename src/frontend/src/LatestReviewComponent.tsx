import React from "react";
import {ActionIcon, Container, Group, Paper, Stack, Title} from "@mantine/core";
import {useNavigate} from "react-router-dom";
import {IconPlus} from "@tabler/icons-react";
import {BookReview} from "./types";
import BookReviewComponent from "./BookReviewComponent";

type Props = {
  recentReviews: BookReview[],
  bestRatedReviews: BookReview[]
}

const LatestReviewComponent: React.FC<Props> = ({recentReviews, bestRatedReviews}) => {
  const navigate = useNavigate();

  return (
    <Container mt="md">
      <Title order={2} ta="center" mb="md">Best rated reviews</Title>
      <Paper withBorder p="md" mb="xl">
        <Group justify="flex-end" mb="sm">
          <ActionIcon variant="filled" radius="xl" onClick={() => navigate('/submit-review')}>
            <IconPlus size={16}/>
          </ActionIcon>
        </Group>
        <Stack>
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
        </Stack>
      </Paper>
      <Title order={2} ta="center" mb="md">Recently submitted reviews</Title>
      <Paper withBorder p="md">
        <Group justify="flex-end" mb="sm">
          <ActionIcon variant="filled" radius="xl" onClick={() => navigate('/submit-review')}>
            <IconPlus size={16}/>
          </ActionIcon>
        </Group>
        <Stack>
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
        </Stack>
      </Paper>
    </Container>
  );
}

export default LatestReviewComponent;
