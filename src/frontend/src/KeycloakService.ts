import Keycloak from "keycloak-js";
import {Store} from "redux";
import {login} from "./actions";

export const keycloak = Keycloak({
    "realm": "spring",
    "url": window.location.href.includes("localhost") ? "http://localhost:8888/auth" : "http://172.17.0.1:8888/auth",
    "clientId": "react-client"
  }
);

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
          roles: keycloak.tokenParsed?.realm_access?.roles,
          refresh_token: keycloak.refreshToken
        }));
      });
    }
    onInitCallback();
  })
};

export const keycloakLogin = keycloak.login;
export const keycloakLogout = keycloak.logout;
