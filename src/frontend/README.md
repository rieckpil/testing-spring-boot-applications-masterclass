# Book Reviewr Frontend ReactJS application

## Requirements

- Node > 16.11.0
- NPM > 8.0.0

## Build the frontend application

- Make sure you've configured the correct node version with `node -v`
- Install all required dependencies with `npm install`
- Run `npm run build`

Please note that the frontend application is automatically build as part of the Maven build lifecycle using the `frontend-maven-plugin`.

## Run the frontend in development mode.

Make sure the Spring Boot backend and the required infrastructure is up- and running.

Start the React application in development mode with `npm run start` and then visit `http://localhost:3000`.

All API requests will be proxied to `http://localhost:8080` to target the locally running Spring Boot application.

