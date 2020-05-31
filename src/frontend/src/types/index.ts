export const LOGOUT = 'LOGOUT'
export const LOGIN = 'LOGIN'

export interface RootState {
  authentication: AuthenticationState
}

export interface AuthenticationState {
  isAuthenticated: boolean,
  details?: LoginPayload
}

export interface LoginPayload {
  username?: string,
  email?: string,
  token?: string,
  refresh_token?: string
}

interface LoginAction {
  type: typeof LOGIN
  payload: LoginPayload
}

interface LogoutAction {
  type: typeof LOGOUT
}

export type AuthenticationActionTypes = LoginAction | LogoutAction

export interface Book {
  title: string,
  isbn: string,
  author: string,
  genre: string,
  thumbnailUrl: string,
  description: string,
  publisher: string,
  pages: number
}

export interface BookReview {
  reviewContent: string,
  reviewTitle: string,
  rating: number,
  bookIsbn: string,
  bookTitle: string,
  bookThumbnailUrl: string,
  submittedBy: string,
  submittedAt: number
}
