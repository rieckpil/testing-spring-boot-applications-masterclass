import React, {useEffect, useState} from "react";
import {Container} from "semantic-ui-react";
import LatestBookListComponent from "./LatestBookListComponent";
import LatestReviewComponent from "./LatestReviewComponent";
import {Book, BookReview, ReviewStatistic, RootState} from "./types";
import {connect, ConnectedProps} from "react-redux";

const mapState = (state: RootState) => ({
  isAuthenticated: state.authentication.isAuthenticated,
  token: state.authentication.details?.token
});

const connector = connect(mapState);

type Props = ConnectedProps<typeof connector>

const HomeContainer: React.FC<Props> = ({isAuthenticated, token}) => {

  const [recentReviews, setRecentReviews] = useState<BookReview[]>([])
  const [bestRatedReviews, setBestRatedReviews] = useState<BookReview[]>([])
  const [availableBooks, setAvailableBooks] = useState<Book[]>([])
  const [reviewStatistics, setReviewStatistics] = useState<ReviewStatistic[]>([])

  useEffect(() => {
    fetch(`/api/books/reviews?size=5`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: BookReview[]) => {
        setRecentReviews(result)
      })

    fetch(`/api/books/reviews?orderBy=rating`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: BookReview[]) => {
        setBestRatedReviews(result)
      })

    fetch(`/api/books`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      }
    }).then(result => result.json())
      .then((result: Book[]) => {
        setAvailableBooks(result)
      })

    if (isAuthenticated) {
      fetch(`/api/books/reviews/statistics`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }).then(result => result.json())
        .then((result: ReviewStatistic[]) => {
          setReviewStatistics(result)
        })
    }
  }, [isAuthenticated, token])


  return (
    <Container>
      <LatestBookListComponent reviewStatistics={reviewStatistics}
                               availableBooks={availableBooks}/>
      <LatestReviewComponent recentReviews={recentReviews} bestRatedReviews={bestRatedReviews}/>
    </Container>
  );
}

export default connector(HomeContainer);
