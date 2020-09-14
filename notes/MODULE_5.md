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
- too much mocks might indicate a god class -> try to outsource
- when to test?
- cyclomatic complexity

## Self-made test slice for the messaging endpoint

- introduce LocalStack and its Testcontainers module
- For messaging there are no test slices currently available
- We'll write our own
- Consuming the message and processing it
- Show Autoconfiguration with break point
- always question if this requires a separated test or should be rather included in a bigger integration test
- Step by step approach with failing steps in between to explain the concept

## Use LocalStack to mock AWS services (SQS, SNS, S3, etc.)

- Introduction to what LocalStack provides
- Overriding our bean definitions
- Dynamically create queues for each context (-> hint to upcoming Spring Context section)
- The _problem_ with testing asychronous code (-> Awaitlity)
- Running integration tests in parallel?
