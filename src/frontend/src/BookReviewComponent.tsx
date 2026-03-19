import React from "react";
import {Button, Group, Image, Paper, Rating, Stack, Text} from "@mantine/core";
import {IconCheck} from "@tabler/icons-react";
import {BookReview} from "./types";

type Props = BookReview & {
  onDelete?: () => void,
  isModerator: boolean,
  elementId: number
}

const BookReviewComponent: React.FC<Props> = ({elementId, bookTitle, bookIsbn, reviewTitle, bookThumbnailUrl, reviewContent, rating, submittedBy, submittedAt, onDelete, isModerator}) => {
  return (
    <Paper id={'review-' + elementId} withBorder p="md" radius="md">
      <Group align="flex-start">
        <Image src={bookThumbnailUrl} w={60} h={80} fit="contain" radius="sm"/>
        <Stack gap={4} style={{flex: 1}}>
          <Text fw={700} className="review-title">{reviewTitle}</Text>
          <Text c="dimmed" size="sm">{bookTitle} | ISBN: {bookIsbn}</Text>
          <Text className="review-content">{reviewContent}</Text>
          <Group gap="md" mt="xs" wrap="wrap">
            <Group gap={4}>
              <Text size="sm">Rating:</Text>
              <Rating value={rating} readOnly/>
            </Group>
            <Group gap={4}>
              <IconCheck color="green" size={16}/>
              <Text size="sm">Confirmed reader</Text>
            </Group>
            <Text size="sm">Submitted by {submittedBy} on {new Date(submittedAt).toLocaleString()}</Text>
            {isModerator && (
              <Button
                ml="auto"
                size="compact-sm"
                color="red"
                onClick={onDelete ? () => onDelete() : undefined}
              >
                Delete
              </Button>
            )}
          </Group>
        </Stack>
      </Group>
    </Paper>
  );
}

export default BookReviewComponent;
