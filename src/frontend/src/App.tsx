import React, {useState} from 'react';
import {Container} from "semantic-ui-react";
import Keycloak from 'keycloak-js';
import {Route, Switch} from "react-router-dom";

import Header from "./Header";
import SubmitReviewContainer from "./SubmitReviewContainer";
import HomeContainer from "./HomeContainer";

function App() {

  const [keycloak, setKeycloak] = useState();
  const [authenticated, setAuthenticated] = useState();

  const login = () => {
    const keycloakInstance = Keycloak('/keycloak.json');
    keycloakInstance.init({onLoad: 'login-required'}).then(authenticated => {
      console.log("CALLBACK");
      console.log(authenticated)
      setAuthenticated(authenticated);
      setKeycloak(keycloakInstance);
    });
  }

  const logout = () => {
    keycloak.logout().then(() => {
      setAuthenticated(false);
    });
  }

  return (
    <Container>
      <Header onLogin={login} onLogout={logout} isAuthenticated={authenticated}/>
      <Switch>
        <Route exact path="/">
          <HomeContainer/>
        </Route>
        <Route path="/all-reviews">
        </Route>
        <Route path="/all-books">
        </Route>
        <Route path="/submit-review">
          <SubmitReviewContainer isAuthenticated={authenticated}/>
        </Route>
      </Switch>
    </Container>
  );
}

export default App;
