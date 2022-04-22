import React from 'react';
import ReactDOM from 'react-dom/client';
import {Provider} from 'react-redux';
import {createStore, StoreEnhancer} from 'redux';
import 'semantic-ui-css/semantic.min.css';

import * as serviceWorker from './serviceWorker';
import App from './App';
import {initKeycloak} from './KeycloakService';
import reducers from './reducers';

declare global {
  interface Window {
    __REDUX_DEVTOOLS_EXTENSION__?: () => StoreEnhancer;
  }
}

const store = createStore(reducers, window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__());


const root = ReactDOM.createRoot(document.getElementById('root')!!);

const renderApp = () => root.render(
  <Provider store={store}>
    <App/>
  </Provider>
);

initKeycloak(renderApp, store);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
