# Content for Module V: Further test slices

## Test slice API clients (both RestTemplate and WebClient)

- focus on testing components that reach out to remote APIs over HTTP
- plain unit test lack again HTTP semantics
- how does our client react to failures or slow responses of the remote system
- use both WebClient and RestTemplate
- show retry behaviour of WebClient on slow responses (do it TDD? First test then implementation)

## Test slice "service layer"

- focus on the review service
- mock external dependencies

## Self-made test slice for the messaging endpoint

- LocalStack usage
- Consuming the message and processing it
