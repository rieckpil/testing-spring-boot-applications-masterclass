import React, {FormEvent, useEffect, useState} from "react";
import {
  Button,
  Checkbox,
  Container,
  Dropdown,
  Form,
  Header,
  Message,
  Rating,
  RatingProps,
  TextArea
} from "semantic-ui-react";
import {Book, RootState} from "./types";
import {connect, ConnectedProps} from "react-redux";
import {login} from "./actions";
import {DropdownProps} from "semantic-ui-react/dist/commonjs/modules/Dropdown/Dropdown";
import {Link} from "react-router-dom";
import {keycloakLogin} from "./KeycloakService";

const mapState = (state: RootState) => ({
  isAuthenticated: state.authentication.isAuthenticated,
  token: state.authentication.details?.token
});

const mapDispatch = {
  login
}

const connector = connect(mapState, mapDispatch);

type PropsFromRedux = ConnectedProps<typeof connector>
type Props = PropsFromRedux & {}

const SubmitReviewContainer: React.FC<Props> = ({isAuthenticated, token}) => {

  const [bookOptions, setBookOptions] = useState<any>();
  const [isbn, setIsbn] = useState<string | number | boolean | (string | number | boolean)[] | undefined>("");
  const [reviewTitle, setReviewTitle] = useState("");
  const [reviewContent, setReviewContent] = useState("");
  const [rating, setRating] = useState<number | string | undefined>(0);
  const [confirmation, setConfirmation] = useState<boolean>(false);
  const [success, setSuccess] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>("");

  const handleSubmit = (evt: FormEvent) => {
    evt.preventDefault();

    setSuccess(false)
    setErrorMessage("")

    fetch(`/api/books/${isbn}/reviews`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        reviewTitle,
        reviewContent,
        rating
      })
    }).then(result => {
      if (result.status === 201) {
        setSuccess(true)
        setConfirmation(false)
        setRating(0)
        setReviewTitle("")
        setReviewContent("")
        setIsbn("")
      } else if (result.status === 418) {
        setErrorMessage('Your review does not meet the quality standards, please read them carefully and submit again.')
      } else {
        setErrorMessage(`We could not store your review, please try again later: ${result.status}`)
      }
    }).catch(error => {
      setErrorMessage(`We could not store your review, please try again later: ${error}`)
    });
  }

  useEffect(() => {
    fetch('/api/books')
      .then(result => result.json())
      .then((result: Book[]) => {
        const formattedBooks = result.map((book: Book) => {
          return {
            "key": book.isbn,
            "text": `${book.title} - ${book.author}`,
            "value": book.isbn,
            "image": {"src": book.thumbnailUrl}
          }
        });
        setBookOptions(formattedBooks);
      })
  }, []);

  return (
    <Container>
      <Header as='h2' textAlign='center'>Submit a new book review</Header>
      {isAuthenticated ?
        <Form size='large' onSubmit={(e: FormEvent) => handleSubmit(e)} success={success} error={errorMessage !== ""}>
          <Message
            success
            header='This was a success'
            content={<span>You successfully submitted a <Link to='/all-reviews'>new book review</Link>.</span>}
          />
          <Message
            error
            header='There was an error'
            content={errorMessage}
          />

          <Form.Dropdown
            loading={bookOptions == null}
            label='Select a book'
            id='book-selection'
            required
            control={Dropdown}
            placeholder='Search for a book'
            fluid
            clearable
            search
            selection
            value={isbn}
            onChange={(event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => setIsbn(data.value)}
            options={bookOptions}
          />

          <Form.Input
            label='Title'
            placeholder='Enter the title of your review'
            id='review-title'
            value={reviewTitle}
            onChange={(e: any) => setReviewTitle(e.target.value)}
            required
          />

          <Form.Field
            label='Your rating'
            control={Rating}
            id='book-rating'
            icon='star'
            size='huge'
            rating={rating}
            maxRating={5}
            onRate={(event: React.MouseEvent<HTMLDivElement>, data: RatingProps) => setRating(data.rating)}
            required
            clearable
          />

          <Form.Field
            control={TextArea}
            label='Your review'
            id='review-content'
            placeholder='Enter your book review...'
            value={reviewContent}
            onChange={(e: any) => setReviewContent(e.target.value)}
            required
          />

          <Message>
            <Message.Header>Quality standards for your review</Message.Header>
            <Message.List>
              <Message.Item>The review contains at least 10 words</Message.Item>
              <Message.Item>Swear words are not allowed</Message.Item>
              <Message.Item>Don't use 'I' or 'good' too often</Message.Item>
            </Message.List>
          </Message>

          <Form.Field
            control={Checkbox}
            checked={confirmation}
            onChange={() => setConfirmation(!confirmation)}
            label='I hereby affirm that I have read the book'
          />
          <Button
            id='review-submit'
            type='submit'>Submit your review
          </Button>
        </Form>
        :
        <Message
          icon='lock'
          header='Restricted area'
          content={<span>To submit a new book review, please <span
            style={{color: "rgb(30, 112, 191)", cursor: "pointer"}} onClick={() => keycloakLogin()}>login</span> first.</span>}
        />

      }
    </Container>
  );
}

export default connector(SubmitReviewContainer);
