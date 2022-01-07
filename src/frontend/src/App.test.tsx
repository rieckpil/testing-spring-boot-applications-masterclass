import React from 'react';
import {render} from '@testing-library/react';
import App from './App';
import {Provider} from 'react-redux';
import configureStore from 'redux-mock-store';

const mockStore = configureStore([]);

const store = mockStore({
  authentication: {
    isAuthenticated: false
  }
});

test('renders react app', () => {
  render(<Provider store={store}><App/></Provider>);
});
