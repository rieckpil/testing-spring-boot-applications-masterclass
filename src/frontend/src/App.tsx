import React from 'react';
import {HashRouter, Route, Switch} from "react-router-dom";
import {Container, Grid} from "semantic-ui-react";

import Header from "./Header";
import SubmitReviewContainer from "./SubmitReviewContainer";
import HomeContainer from "./HomeContainer";
import AllReviewContainer from "./AllReviewContainer";

const App: React.FC = () => {
  return (
    <Container>
      <HashRouter>
        <Header/>
        <Grid centered>
          <Grid.Column width={10}>
            <Switch>
              <Route exact path="/">
                <HomeContainer/>
              </Route>
              <Route path="/all-reviews">
                <AllReviewContainer/>
              </Route>
              <Route path="/all-books">
              </Route>
              <Route path="/submit-review">
                <SubmitReviewContainer/>
              </Route>
            </Switch>
          </Grid.Column>
        </Grid>
      </HashRouter>
    </Container>
  );
}

export default App;
