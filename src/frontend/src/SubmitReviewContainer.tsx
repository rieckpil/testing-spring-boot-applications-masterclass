import React from "react";
import {Container} from "semantic-ui-react";
import {RootState} from "./types";
import {connect, ConnectedProps} from "react-redux";
import {login} from "./actions";

const mapState = (state: RootState) => ({
  isAuthenticated: state.authentication.isAuthenticated
});

const mapDispatch = {
  login
}

const connector = connect(mapState, mapDispatch);

type PropsFromRedux = ConnectedProps<typeof connector>
type Props = PropsFromRedux & {}

const SubmitReviewContainer: React.FC<Props> = ({isAuthenticated}) => {

  return (
    <Container>
      {isAuthenticated ?
        'Submit a review' :
        <span>To submit a new book review, please login first.</span>
      }
    </Container>
  );
}

export default connector(SubmitReviewContainer);
