import React, {FormEvent, useEffect, useState} from "react";
import {Button, Checkbox, Container, Dropdown, Form, Header, Rating, RatingProps, TextArea} from "semantic-ui-react";
import {RootState} from "./types";
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

  const [bookOptions, setBookOptions] = useState([]);
  const [isbn, setIsbn] = useState();
  const [reviewTitle, setReviewTitle] = useState();
  const [reviewContent, setReviewContent] = useState();
  const [rating, setRating] = useState();

  const handleSubmit = (evt: FormEvent) => {
    evt.preventDefault();

    fetch(`http://localhost:8080/api/books/${isbn}/reviews`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authentication': `Bearer ${token}`
      },
      body: JSON.stringify({
        reviewTitle,
        reviewContent,
        rating
      })
    }).then(result => {
      if (result.status === 201) {
        console.log('success');
      } else {
        console.log('error');
      }
    });
  }

  useEffect(() => {
    fetch('http://localhost:8080/api/books')
      .then(result => result.json())
      .then(result => setBookOptions(result));
  }, []);

  return (
    <Container>
      <Header as='h1' textAlign='center'>Submit a new book review</Header>
      {isAuthenticated ?
        <Form size='large' onSubmit={(e: FormEvent) => handleSubmit(e)}>
          <Form.Dropdown
            label='Select a book'
            required
            control={Dropdown}
            placeholder='Search for a book'
            fluid
            clearable
            search
            selection
            onChange={(event: React.SyntheticEvent<HTMLElement>, data: DropdownProps) => setIsbn(data.value)}
            options={bookOptions}
          />

          <Form.Field
            label='Title'
            control='input'
            placeholder='Enter the title of your review'
            onChange={(e: any) => setReviewTitle(e.target.value)}
            required
          />

          <Form.Field
            label='Your rating'
            control={Rating}
            icon='star'
            defaultRating={0}
            onRate={(event: React.MouseEvent<HTMLDivElement>, data: RatingProps) => setRating(data.rating)}
            maxRating={5}
          />

          <Form.Field
            control={TextArea}
            label='Your review'
            placeholder='Enter your book review...'
            onChange={(e: any) => setReviewContent(e.target.value)}
            required
          />

          <Form.Field
            control={Checkbox}
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
