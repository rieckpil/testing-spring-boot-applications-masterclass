import React from 'react';
import {HashRouter, Route, Routes} from "react-router-dom";
import {Container} from "@mantine/core";

import Header from "./Header";
import SubmitReviewContainer from "./SubmitReviewContainer";
import HomeContainer from "./HomeContainer";
import AllReviewContainer from "./AllReviewContainer";

const App: React.FC = () => {
  return (
    <Container>
      <HashRouter future={{v7_startTransition: true, v7_relativeSplatPath: true}}>
        <Header/>
        <Routes>
          <Route path="/" element={<HomeContainer/>}/>
          <Route path="/all-reviews" element={<AllReviewContainer/>}/>
          <Route path="/submit-review" element={<SubmitReviewContainer/>}/>
        </Routes>
      </HashRouter>
    </Container>
  );
}

export default App;
