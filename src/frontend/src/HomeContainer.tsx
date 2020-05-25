import React from "react";
import {Button, Container} from "semantic-ui-react";
import LatestBookList from "./LatestBookList";
import LatestReviewList from "./LatestReviewList";
import {Link} from "react-router-dom";

const HomeContainer = () => {
  return (
    <Container>
      <Button
        as={Link}
        to='/submit-review'
        circular icon='add'/>
      <LatestBookList/>
      <LatestReviewList/>
    </Container>
  );
}

export default HomeContainer;
