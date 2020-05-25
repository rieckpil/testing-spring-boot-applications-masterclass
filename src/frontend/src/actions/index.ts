import {AuthenticationActionTypes, LOGIN, LoginPayload} from '../types';

export function login(loginPayload: LoginPayload = {}): AuthenticationActionTypes {
  return {
    type: LOGIN,
    payload: loginPayload
  }
}
