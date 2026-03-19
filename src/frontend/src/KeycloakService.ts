import Keycloak from "keycloak-js";
import {Store} from "redux";
import {login} from "./actions";
import {AuthenticationActionTypes, RootState} from "./types";

// Polyfill crypto.randomUUID for insecure HTTP contexts (e.g., test environments on non-localhost).
// keycloak-js 26 requires crypto.randomUUID which is only available in secure contexts by default.
// crypto.getRandomValues is available in all contexts and can be used as a fallback.
if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'undefined') {
  Object.defineProperty(crypto, 'randomUUID', {
    value: (): `${string}-${string}-${string}-${string}-${string}` => {
      const bytes = new Uint8Array(16);
      crypto.getRandomValues(bytes);
      bytes[6] = (bytes[6] & 0x0f) | 0x40;
      bytes[8] = (bytes[8] & 0x3f) | 0x80;
      const hex = Array.from(bytes).map(b => b.toString(16).padStart(2, '0')).join('');
      return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20)}` as `${string}-${string}-${string}-${string}-${string}`;
    },
    writable: false,
    configurable: true,
  });
}

export const keycloak = new Keycloak({
    "realm": "spring",
    "url": "http://" + window.location.hostname + ":8888/auth",
    "clientId": "react-client"
  }
);

export const initKeycloak = (onInitCallback: Function, store: Store<RootState, AuthenticationActionTypes>) => {
  keycloak.init({
    onLoad: 'check-sso',
    pkceMethod: false,
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

export const keycloakLogin = (options?: Parameters<typeof keycloak.login>[0]) => keycloak.login(options);
export const keycloakLogout = (options?: Parameters<typeof keycloak.logout>[0]) => keycloak.logout(options);
