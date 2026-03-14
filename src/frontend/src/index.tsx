import React from 'react';
import ReactDOM from 'react-dom/client';
import {Provider} from 'react-redux';
import {createStore, Reducer, StoreEnhancer} from 'redux';
import {AuthenticationActionTypes, RootState} from './types';
import '@mantine/core/styles.css';
import {MantineProvider} from '@mantine/core';

import * as serviceWorker from './serviceWorker';
import App from './App';
import {initKeycloak} from './KeycloakService';
import reducers from './reducers';

declare global {
  interface Window {
    __REDUX_DEVTOOLS_EXTENSION__?: () => StoreEnhancer;
  }
}

// combineReducers returns Reducer<S, A, Partial<S>> (3 type params) but createStore's
// first overload expects Reducer<S, A> (2 params, PreloadedState=S). Cast to bridge the gap.
const store = createStore(
  reducers as unknown as Reducer<RootState, AuthenticationActionTypes>,
  window.__REDUX_DEVTOOLS_EXTENSION__?.()
);

const root = ReactDOM.createRoot(document.getElementById('root')!!);

const renderApp = () => root.render(
  <Provider store={store}>
    <MantineProvider>
      <App/>
    </MantineProvider>
  </Provider>
);

initKeycloak(renderApp, store);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
