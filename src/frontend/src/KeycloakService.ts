import Keycloak from "keycloak-js";
import {Store} from "redux";
import {login} from "./actions";

let keycloak: Keycloak;

export const getKeycloak = () => keycloak;

export const keycloakLogin = () => keycloak.login();
export const keycloakLogout = () => keycloak.logout();

export const initKeycloak = (onInitCallback: Function, store: Store) => {
  fetch("/api/config")
    .then(res => res.json())
    .then(config => {
      keycloak = new Keycloak({
        realm: "spring",
        url: config.keycloakUrl,
        clientId: "react-client",
      });
      return keycloak.init({onLoad: "check-sso"});
    })
    .then(authenticated => {
      if (authenticated) {
        keycloak.loadUserProfile().then(profile => {
          store.dispatch(login({
            username: profile.username,
            email: profile.email,
            token: keycloak.token,
            roles: keycloak.tokenParsed?.realm_access?.roles,
            refresh_token: keycloak.refreshToken,
          }));
        });
      }
      onInitCallback();
    });
};
