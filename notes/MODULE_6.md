# Writing test inculding the whole Spring Context

## First steps:

- overview of what is required during application startup and runtime (AWS services, Keycloak, database, remote services)
- what is already covered with previous modules and what is missing
- introduction to WireMock
- Testcontainers with Docker-Compose vs. Keycloak mocked via WireMock


## Use LocalStack to mock AWS services (SQS, SNS, S3, etc.)

- Introduction to what LocalStack provides
- Overriding our bean definitions
- Dynamically create queues for each context (-> hint to upcoming Spring Context section)
- The _problem_ with testing asychronous code (-> Awaitlity)
- Running integration tests in parallel?


## Happy path testing

- Processing an incoming book masterdata request (SQS -> App -> Network -> Database)
- Creating and retrieving a review (POST to create -> inspect Location header -> retrieve)
- TestWebClient explanation (starting the embedded servlet container)
- MockMvc and @SpringBootTest rationale
