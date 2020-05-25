import Keycloak from "keycloak-js";
import {Store} from "redux";
import {login} from "./actions";

export const keycloak = Keycloak('/keycloak.json');

export const initKeycloak = (onInitCallback: Function, store: Store) => {
  keycloak.init({
    onLoad: 'check-sso',
  }).then(authenticated => {
    if (authenticated) {
      keycloak.loadUserProfile().then(profile => {
        store.dispatch(login({
          username: profile.username,
          email: profile.email,
          token: keycloak.token,
          refresh_token: keycloak.refreshToken
        }));
      });
    }
    onInitCallback();
  })
};

export const keycloakLogin = keycloak.login;
export const keycloakLogout = keycloak.logout;
