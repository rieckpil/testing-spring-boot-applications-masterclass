import React from "react";
import {Button, Container} from "semantic-ui-react";
import LatestBookList from "./LatestBookList";
import LatestReviewContainer from "./LatestReviewContainer";
import {Link} from "react-router-dom";

const HomeContainer = () => {
  return (
    <Container>
      <Button
        as={Link}
        to='/submit-review'
        circular icon='add'/>
      <LatestBookList/>
      <LatestReviewContainer/>
    </Container>
  );
}

export default HomeContainer;
