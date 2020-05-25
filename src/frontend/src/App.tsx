import React from 'react';
import {Button, Container} from "semantic-ui-react";
import Header from "./Header";
import LatestBookList from "./LatestBookList";
import LatestReviewList from "./LatestReviewList";

function App() {
  return (
    <Container>
      <Header/>
      <header className="App-header">
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
      </header>
      <Button circular icon='add' />
      <LatestBookList/>
      <LatestReviewList/>
    </Container>
  );
}

export default App;
