import React from "react";
import {Book, ReviewStatistic} from "./types";
import {Badge, Card, Group, Image, Stack, Text} from "@mantine/core";
import {IconChartLine} from "@tabler/icons-react";

type Props = {
  metadata: Book,
  statistics?: ReviewStatistic
}

const BookComponent: React.FC<Props> = ({metadata, statistics}) => {
  return (
    <Card withBorder shadow="sm" radius="md">
      <Card.Section p="md">
        <Group align="flex-start">
          <Stack gap={4} style={{flex: 1}}>
            <Text fw={700}>{metadata.title}</Text>
            <Text c="dimmed" size="sm">from {metadata.author}</Text>
            <Text size="sm">{metadata.description}</Text>
          </Stack>
          <Image
            src={metadata.thumbnailUrl}
            w={60}
            h={80}
            fit="contain"
            radius="sm"
          />
        </Group>
      </Card.Section>
      <Card.Section p="md" pt={0}>
        <Group gap="xs">
          <Badge variant="outline">{metadata.publisher}</Badge>
          <Badge variant="outline">{metadata.pages} pages</Badge>
          <Badge variant="outline">{metadata.genre}</Badge>
          {statistics ? (
            <>
              <Badge color="blue">
                Avg. rating: {statistics.ratings === 0 ? 'n.A.' : statistics.avg}
              </Badge>
              <Badge color="blue">
                Total ratings: {statistics.ratings === 0 ? 'n.A.' : statistics.ratings}
              </Badge>
            </>
          ) : (
            <Badge color="blue" leftSection={<IconChartLine size={12}/>}>
              Statistics only for logged in users
            </Badge>
          )}
        </Group>
      </Card.Section>
    </Card>
  )
}

export default BookComponent
