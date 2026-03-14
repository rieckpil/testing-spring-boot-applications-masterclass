import React from 'react';
import {render, screen, waitFor, act} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {vi, beforeEach, afterEach, describe, test, expect} from 'vitest';
import {MantineProvider} from '@mantine/core';
import {Provider} from 'react-redux';
import {MemoryRouter} from 'react-router-dom';
import configureStore from 'redux-mock-store';
import SubmitReviewContainer from './SubmitReviewContainer';
import {Book} from './types';

const mockStore = configureStore([]);

const unauthenticatedStore = mockStore({
  authentication: {isAuthenticated: false}
});

const authenticatedStore = mockStore({
  authentication: {
    isAuthenticated: true,
    details: {token: 'test-token', roles: []}
  }
});

const mockBooks: Book[] = [
  {
    isbn: '9781234567890',
    title: 'Clean Code',
    author: 'Robert C. Martin',
    genre: 'Software Engineering',
    thumbnailUrl: 'https://example.com/cover.jpg',
    description: 'A handbook of agile software craftsmanship.',
    publisher: 'Prentice Hall',
    pages: 464
  },
  {
    isbn: '9780134685991',
    title: 'Effective Java',
    author: 'Joshua Bloch',
    genre: 'Software Engineering',
    thumbnailUrl: 'https://example.com/effective-java.jpg',
    description: 'Best practices for the Java platform.',
    publisher: 'Addison-Wesley',
    pages: 412
  }
];

function makeFetch(submitStatus = 201) {
  return vi.fn().mockImplementation((url: string, options?: RequestInit) => {
    if (options?.method === 'POST') {
      return Promise.resolve({
        ok: submitStatus < 400,
        status: submitStatus,
        json: () => Promise.resolve({}),
      });
    }
    return Promise.resolve({
      ok: true,
      status: 200,
      json: () => Promise.resolve(mockBooks),
    });
  });
}

async function renderForm(store = authenticatedStore) {
  let utils: ReturnType<typeof render>;
  await act(async () => {
    utils = render(
      <MemoryRouter>
        <MantineProvider>
          <Provider store={store}>
            <SubmitReviewContainer/>
          </Provider>
        </MantineProvider>
      </MemoryRouter>
    );
  });
  return utils!;
}

async function fillAndSubmitForm(user: ReturnType<typeof userEvent.setup>) {
  // Select a book from the combobox
  const bookCombobox = screen.getByPlaceholderText(/search for a book/i);
  await user.click(bookCombobox);
  await user.click(screen.getByText('Clean Code - Robert C. Martin'));

  // Fill in the review title
  await user.type(screen.getByRole('textbox', {name: /title/i}), 'A must-read for every developer');

  // Mantine Rating: onClick that calls onChange is on the <label> (star icon), not
  // the hidden <input>. The label is the next sibling of the radio with aria-label="5".
  const fiveStarRadio = screen.getByRole('radio', {name: '5'});
  await user.click(fiveStarRadio.nextElementSibling as HTMLElement);

  // Fill in the review content
  await user.type(
    screen.getByRole('textbox', {name: /your review/i}),
    'This is an excellent book that every software engineer should read.'
  );

  // Check the confirmation checkbox
  await user.click(screen.getByRole('checkbox'));

  // Submit the form
  await user.click(screen.getByRole('button', {name: /submit your review/i}));
}

describe('SubmitReviewContainer — unauthenticated', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', makeFetch());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  test('shows restricted area alert when not logged in', async () => {
    await renderForm(unauthenticatedStore);
    expect(screen.getByText('Restricted area')).toBeInTheDocument();
    expect(screen.getByText(/to submit a new book review/i)).toBeInTheDocument();
  });

  test('does not render the review form when not logged in', async () => {
    await renderForm(unauthenticatedStore);
    expect(screen.queryByRole('button', {name: /submit your review/i})).not.toBeInTheDocument();
  });
});

describe('SubmitReviewContainer — form rendering', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', makeFetch());
  });

  afterEach(() => {
    vi.unstubAllGlobals();
  });

  test('renders the page title', async () => {
    await renderForm();
    expect(screen.getByText('Submit a new book review')).toBeInTheDocument();
  });

  test('renders all form fields', async () => {
    await renderForm();
    expect(screen.getByPlaceholderText(/search for a book/i)).toBeInTheDocument();
    expect(screen.getByRole('textbox', {name: /title/i})).toBeInTheDocument();
    expect(screen.getByRole('textbox', {name: /your review/i})).toBeInTheDocument();
    expect(screen.getByRole('checkbox')).toBeInTheDocument();
    expect(screen.getByRole('button', {name: /submit your review/i})).toBeInTheDocument();
  });

  test('book select is enabled after books are loaded', async () => {
    await renderForm();
    expect(screen.getByPlaceholderText(/search for a book/i)).not.toBeDisabled();
  });

  test('shows quality standards section', async () => {
    await renderForm();
    expect(screen.getByText('Quality standards for your review')).toBeInTheDocument();
    expect(screen.getByText(/at least 10 words/i)).toBeInTheDocument();
    expect(screen.getByText(/swear words are not allowed/i)).toBeInTheDocument();
  });

  test('prefill button populates the review content field', async () => {
    const user = userEvent.setup();
    await renderForm();

    await user.click(screen.getByRole('button', {name: /prefill review content/i}));

    expect(screen.getByRole('textbox', {name: /your review/i})).toHaveValue(
      "This is an excellent book. I've learned quite a lot and can recommend it to every CS student."
    );
  });

  test('book options are searchable — filters by title', async () => {
    const user = userEvent.setup();
    await renderForm();

    const combobox = screen.getByPlaceholderText(/search for a book/i);
    await user.click(combobox);
    await user.type(combobox, 'Effective');

    expect(screen.getByText('Effective Java - Joshua Bloch')).toBeInTheDocument();
    expect(screen.queryByText('Clean Code - Robert C. Martin')).not.toBeInTheDocument();
  });
});

describe('SubmitReviewContainer — submission', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  test('shows success alert after a successful submission (201)', async () => {
    vi.stubGlobal('fetch', makeFetch(201));
    const user = userEvent.setup();
    await renderForm();

    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(screen.getByText('This was a success')).toBeInTheDocument();
    });
    expect(screen.getByText(/you successfully submitted a/i)).toBeInTheDocument();
  });

  test('resets the form fields after a successful submission', async () => {
    vi.stubGlobal('fetch', makeFetch(201));
    const user = userEvent.setup();
    await renderForm();

    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(screen.getByText('This was a success')).toBeInTheDocument();
    });
    expect(screen.getByRole('textbox', {name: /title/i})).toHaveValue('');
    expect(screen.getByRole('textbox', {name: /your review/i})).toHaveValue('');
    expect(screen.getByRole('checkbox')).not.toBeChecked();
  });

  test('shows quality standards error message on 418 response', async () => {
    vi.stubGlobal('fetch', makeFetch(418));
    const user = userEvent.setup();
    await renderForm();

    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(screen.getByText('There was an error')).toBeInTheDocument();
    });
    expect(screen.getByText(/does not meet the quality standards/i)).toBeInTheDocument();
  });

  test('shows generic error message on unexpected server error (500)', async () => {
    vi.stubGlobal('fetch', makeFetch(500));
    const user = userEvent.setup();
    await renderForm();

    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(screen.getByText('There was an error')).toBeInTheDocument();
    });
    expect(screen.getByText(/we could not store your review/i)).toBeInTheDocument();
    expect(screen.getByText(/500/)).toBeInTheDocument();
  });

  test('shows error message when fetch throws a network error', async () => {
    vi.stubGlobal('fetch', vi.fn().mockImplementation((url: string, options?: RequestInit) => {
      if (options?.method === 'POST') {
        return Promise.reject(new Error('Network failure'));
      }
      return Promise.resolve({ok: true, status: 200, json: () => Promise.resolve(mockBooks)});
    }));
    const user = userEvent.setup();
    await renderForm();

    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(screen.getByText('There was an error')).toBeInTheDocument();
    });
    expect(screen.getByText(/we could not store your review/i)).toBeInTheDocument();
  });

  test('sends the correct payload to the API', async () => {
    const fetchMock = makeFetch(201);
    vi.stubGlobal('fetch', fetchMock);
    const user = userEvent.setup();
    await renderForm();

    await fillAndSubmitForm(user);

    await waitFor(() => {
      expect(screen.getByText('This was a success')).toBeInTheDocument();
    });

    const postCall = fetchMock.mock.calls.find(
      ([, options]) => (options as RequestInit)?.method === 'POST'
    );
    expect(postCall).toBeDefined();

    const [url, options] = postCall!;
    expect(url).toContain('/api/books/9781234567890/reviews');
    expect((options as RequestInit).headers).toMatchObject({
      'Authorization': 'Bearer test-token',
      'Content-Type': 'application/json',
    });

    const body = JSON.parse((options as RequestInit).body as string);
    expect(body.reviewTitle).toBe('A must-read for every developer');
    expect(body.reviewContent).toBe(
      'This is an excellent book that every software engineer should read.'
    );
    expect(body.rating).toBe(5);
  });
});
