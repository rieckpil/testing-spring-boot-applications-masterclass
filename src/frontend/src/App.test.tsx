import React from 'react';
import {render, screen} from '@testing-library/react';
import {vi, beforeEach, afterEach, describe, test, expect} from 'vitest';
import {MantineProvider} from '@mantine/core';
import {Provider} from 'react-redux';
import configureStore from 'redux-mock-store';
import App from './App';
import {BookReview, Book} from './types';

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

const mockReviews: BookReview[] = [
  {
    reviewId: 1,
    reviewTitle: 'Great read',
    reviewContent: 'This book changed the way I think about software.',
    rating: 5,
    bookIsbn: '9781234567890',
    bookTitle: 'Clean Code',
    bookThumbnailUrl: 'https://example.com/cover.jpg',
    submittedBy: 'alice',
    submittedAt: 1700000000000
  }
];

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
  }
];

function mockFetch(urlResponses: Record<string, unknown>) {
  return vi.fn().mockImplementation((url: string) => {
    const matchingKey = Object.keys(urlResponses).find(key => url.includes(key));
    const data = matchingKey ? urlResponses[matchingKey] : [];
    return Promise.resolve({
      ok: true,
      status: 200,
      json: () => Promise.resolve(data),
    });
  });
}

function renderApp(store = unauthenticatedStore) {
  return render(
    <MantineProvider>
      <Provider store={store}>
        <App/>
      </Provider>
    </MantineProvider>
  );
}

describe('App navigation', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', mockFetch({
      '/api/books/reviews': mockReviews,
      '/api/books': mockBooks,
    }));
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  test('renders the Book Reviewr brand name', () => {
    renderApp();
    expect(screen.getByText('Book Reviewr')).toBeInTheDocument();
  });

  test('shows login button when unauthenticated', () => {
    renderApp();
    expect(screen.getByRole('button', {name: /login/i})).toBeInTheDocument();
  });

  test('shows logout button when authenticated', () => {
    renderApp(authenticatedStore);
    expect(screen.getByRole('button', {name: /logout/i})).toBeInTheDocument();
  });

  test('renders All Reviews navigation button', () => {
    renderApp();
    expect(screen.getByRole('button', {name: /all reviews/i})).toBeInTheDocument();
  });

  test('renders Submit a new review navigation button', () => {
    renderApp();
    expect(screen.getByRole('button', {name: /submit a new review/i})).toBeInTheDocument();
  });
});

describe('App home page', () => {
  beforeEach(() => {
    vi.stubGlobal('fetch', mockFetch({
      '/api/books/reviews': mockReviews,
      '/api/books': mockBooks,
    }));
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  test('renders section headings on home page', () => {
    renderApp();
    expect(screen.getByText('Latest books')).toBeInTheDocument();
    expect(screen.getByText('Best rated reviews')).toBeInTheDocument();
    expect(screen.getByText('Recently submitted reviews')).toBeInTheDocument();
  });
});
