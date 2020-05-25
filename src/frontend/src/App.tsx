import React from 'react';
import {Route, Switch} from "react-router-dom";
import {Container} from "semantic-ui-react";

import Header from "./Header";
import SubmitReviewContainer from "./SubmitReviewContainer";
import HomeContainer from "./HomeContainer";

const App: React.FC = () => {
  return (
    <Container>
      <Header/>
      <Switch>
        <Route exact path="/">
          <HomeContainer/>
        </Route>
        <Route path="/all-reviews">
        </Route>
        <Route path="/all-books">
        </Route>
        <Route path="/submit-review">
          <SubmitReviewContainer/>
        </Route>
      </Switch>
    </Container>
  );
}

export default App;
