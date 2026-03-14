import React, {FormEvent, useEffect, useState} from "react";
import {
  Alert,
  Box,
  Button,
  Checkbox,
  Container,
  List,
  Paper,
  Rating,
  Select,
  Stack,
  Text,
  Textarea,
  TextInput,
  Title
} from "@mantine/core";
import {IconLock} from "@tabler/icons-react";
import {Book, RootState} from "./types";
import {connect, ConnectedProps} from "react-redux";
import {login} from "./actions";
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

type BookOption = { value: string; label: string };

const SubmitReviewContainer: React.FC<Props> = ({isAuthenticated, token}) => {

  const [bookOptions, setBookOptions] = useState<BookOption[] | undefined>(undefined);
  const [isbn, setIsbn] = useState<string | null>(null);
  const [reviewTitle, setReviewTitle] = useState("");
  const [reviewContent, setReviewContent] = useState("");
  const [rating, setRating] = useState<number>(0);
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
        setIsbn(null)
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
        const formattedBooks = result.map((book: Book) => ({
          value: book.isbn,
          label: `${book.title} - ${book.author}`,
        }));
        setBookOptions(formattedBooks);
      })
  }, []);

  return (
    <Container>
      <Title order={2} ta="center" mb="md">Submit a new book review</Title>
      {isAuthenticated ? (
        <form onSubmit={handleSubmit}>
          <Stack>
            {success && (
              <Alert color="green" title="This was a success">
                You successfully submitted a <Link to='/all-reviews'>new book review</Link>.
              </Alert>
            )}
            {errorMessage && (
              <Alert color="red" title="There was an error">
                {errorMessage}
              </Alert>
            )}

            <Select
              label="Select a book"
              id="book-selection"
              required
              placeholder="Search for a book"
              data={bookOptions || []}
              value={isbn}
              onChange={setIsbn}
              searchable
              clearable
              disabled={bookOptions === undefined}
            />

            <TextInput
              label="Title"
              placeholder="Enter the title of your review"
              id="review-title"
              value={reviewTitle}
              onChange={(e) => setReviewTitle(e.target.value)}
              required
            />

            <Box>
              <Text component="label" fw={500} size="sm">
                Your rating <Text component="span" c="red">*</Text>
              </Text>
              <Rating
                id="book-rating"
                value={rating}
                onChange={setRating}
                size="xl"
                mt={4}
              />
            </Box>

            <Textarea
              label="Your review"
              id="review-content"
              placeholder="Enter your book review..."
              value={reviewContent}
              onChange={(e) => setReviewContent(e.target.value)}
              required
              minRows={4}
            />

            <Paper withBorder p="md">
              <Text fw={600} mb="xs">Quality standards for your review</Text>
              <List size="sm" mb="sm">
                <List.Item>The review contains at least 10 words</List.Item>
                <List.Item>Swear words are not allowed</List.Item>
                <List.Item>Don't use 'I' or 'good' too often</List.Item>
              </List>
              <Button
                type="button"
                variant="outline"
                size="sm"
                onClick={() => setReviewContent("This is an excellent book. I've learned quite a lot and can recommend it to every CS student.")}
              >
                Prefill review content
              </Button>
            </Paper>

            <Checkbox
              checked={confirmation}
              onChange={() => setConfirmation(!confirmation)}
              label="I hereby affirm that I have read the book"
            />

            <Button id="review-submit" type="submit">
              Submit your review
            </Button>
          </Stack>
        </form>
      ) : (
        <Alert icon={<IconLock size={20}/>} title="Restricted area" color="blue">
          To submit a new book review, please{' '}
          <Text component="span" style={{color: "rgb(30, 112, 191)", cursor: "pointer"}} onClick={() => keycloakLogin()}>
            login
          </Text>{' '}
          first.
        </Alert>
      )}
    </Container>
  );
}

export default connector(SubmitReviewContainer);
