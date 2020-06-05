import React from "react";
import {Book, ReviewStatistic} from "./types";
import {Card, Icon, Image, Label} from "semantic-ui-react";

type Props = {
  metadata: Book,
  statistics?: ReviewStatistic
}

const BookComponent: React.FC<Props> = ({metadata, statistics}) => {
  return (
    <Card>
      <Card.Content>
        <Image
          floated='right'
          size='mini'
          src={metadata.thumbnailUrl}
        />
        <Card.Header>{metadata.title}</Card.Header>
        <Card.Meta>from {metadata.author}</Card.Meta>
        <Card.Description>
          {metadata.description}
        </Card.Description>
      </Card.Content>
      <Card.Content extra>
        <Label>
          {metadata.publisher}
        </Label>
        <Label>
          {metadata.pages} pages
        </Label>
        <Label as='a' tag>
          {metadata.genre}
        </Label>
        {statistics ?
          <React.Fragment>
            <Label
              color='blue'>
              Avg. rating: {statistics.ratings === 0 ? 'n.A.': statistics.avg}
            </Label>
            <Label
              color='blue'>
              Total ratings: {statistics.ratings === 0 ? 'n.A.': statistics.ratings}
            </Label>
          </React.Fragment>
          :
          <Label
            color='blue'>
            <Icon name='line graph'/>Statistics only for logged in users
          </Label>
        }
      </Card.Content>
    </Card>
  )
}

export default BookComponent
