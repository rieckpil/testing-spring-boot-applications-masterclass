import {combineReducers} from 'redux';
import {AuthenticationActionTypes, AuthenticationState, LOGIN} from "../types";

const initialState: AuthenticationState = {
  isAuthenticated: false
}

const authentication = (state = initialState, action: AuthenticationActionTypes): AuthenticationState => {
  switch (action.type) {
    case LOGIN:
      return {
        isAuthenticated: true,
        details: action.payload
      }
    default:
      return state
  }
}

export default combineReducers({
  authentication
});
