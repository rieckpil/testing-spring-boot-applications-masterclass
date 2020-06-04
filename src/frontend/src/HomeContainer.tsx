import React, {useEffect, useState} from "react";
import {Container} from "semantic-ui-react";
import LatestBookListComponent from "./LatestBookListComponent";
import LatestReviewComponent from "./LatestReviewComponent";
import {Book, BookReview} from "./types";

const HomeContainer = () => {

  const [recentReviews, setRecentReviews] = useState<BookReview[]>([])
  const [bestRatedReviews, setBestRatedReviews] = useState<BookReview[]>([])
  const [availableBooks, setAvailableBooks] = useState<Book[]>([])

  useEffect(() => {
    fetch(`http://localhost:8080/api/books/reviews?size=5`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: BookReview[]) => {
        setRecentReviews(result)
      })

    fetch(`http://localhost:8080/api/books/reviews?groupBy=book`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: BookReview[]) => {
        setBestRatedReviews(result)
      })

    fetch(`http://localhost:8080/api/books`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: Book[]) => {
        setAvailableBooks(result)
      })

  }, [])


  return (
    <Container>
      <LatestBookListComponent availableBooks={availableBooks}/>
      <LatestReviewComponent recentReviews={recentReviews} bestRatedReviews={bestRatedReviews}/>
    </Container>
  );
}

export default HomeContainer;
