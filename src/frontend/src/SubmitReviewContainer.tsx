import React from "react";
import {Container} from "semantic-ui-react";

const SubmitReviewContainer: React.FC<{ isAuthenticated: boolean }> = (isAuthenticated) => {
  return (
    <Container>
      {isAuthenticated ? 'Submit a review' : 'Please login first'}
    </Container>
  );
}

export default SubmitReviewContainer;
