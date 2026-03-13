import React from 'react';
import ReactDOM from 'react-dom/client';
import {Provider} from 'react-redux';
import {createStore, StoreEnhancer} from 'redux';
import 'semantic-ui-css/semantic.min.css';

import App from './App';
import {initKeycloak} from './KeycloakService';
import reducers from './reducers';

declare global {
  interface Window {
    __REDUX_DEVTOOLS_EXTENSION__?: () => StoreEnhancer;
  }
}

const store = createStore(reducers, window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());

const root = ReactDOM.createRoot(document.getElementById('root')!);

const renderApp = () => root.render(
  <Provider store={store}>
    <App/>
  </Provider>
);

initKeycloak(renderApp, store);
