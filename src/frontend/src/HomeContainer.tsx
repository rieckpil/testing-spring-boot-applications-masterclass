import React from "react";
import {Button, Container} from "semantic-ui-react";
import LatestBookList from "./LatestBookList";
import LatestReviewList from "./LatestReviewList";

const HomeContainer = () => {
  return (
    <Container>
      <Button circular icon='add'/>
      <LatestBookList/>
      <LatestReviewList/>
    </Container>
  );
}

export default HomeContainer;
