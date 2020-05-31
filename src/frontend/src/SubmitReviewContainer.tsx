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
  const [error, setError] = useState<boolean>(false);

  const handleSubmit = (evt: FormEvent) => {
    evt.preventDefault();

    setSuccess(false)
    setError(false)

    fetch(`http://localhost:8080/api/books/${isbn}/reviews`, {
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
      } else {
        setError(true)
      }
    }).catch(error => {
      setError(true)
    });
  }

  useEffect(() => {
    fetch('http://localhost:8080/api/books')
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
      <Header as='h1' textAlign='center'>Submit a new book review</Header>
      {isAuthenticated ?
        <Form size='large' onSubmit={(e: FormEvent) => handleSubmit(e)} success={success} error={error}>
          <Message
            success
            header='This was a success'
            content='You successfully submitted a new book review'
          />
          <Message
            error
            header='There was an error'
            content='We could not store your review, please try again later'
          />

          <Form.Dropdown
            loading={bookOptions == null}
            label='Select a book'
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
            value={reviewTitle}
            onChange={(e: any) => setReviewTitle(e.target.value)}
            required
          />

          <Form.Field
            label='Your rating'
            control={Rating}
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
            placeholder='Enter your book review...'
            value={reviewContent}
            onChange={(e: any) => setReviewContent(e.target.value)}
            required
          />

          <Form.Field
            control={Checkbox}
            checked={confirmation}
            onChange={() => setConfirmation(!confirmation)}
            label='I hereby affirm that I have read the book'
          />
          <Button type='submit'>Submit your review</Button>
        </Form>
        :
        <span>To submit a new book review, please login first.</span>
      }
    </Container>
  );
}

export default connector(SubmitReviewContainer);
