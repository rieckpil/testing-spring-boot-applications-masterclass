# Writing test including the whole Spring Context

## Get the setup right:

- we'll now start the whole context, and therefore need to be able to create all our beans
- overview of what is required during application startup and runtime (AWS services, Keycloak, database, remote services)
- what is already covered with previous modules and what is missing
- test/it-profile
- bean definition override
- rework HTTP client to inject URL from config file
- introduction to WireMock (setup with an Initializer), good for initial calls, e.g. security, SalesForce
- how to provide the remaining infrastructure?
    * Using WireMock to mock the initial OAuth2 call for the JWKS
    * Provide Keycloak as a container (more close to production, but setup required)
    * Docker-Compose everything?
- name them with the IT prefix (you can separate unit & integration -> see best practices)
- what to focus on testing? Happy-path end-to-end (no UI yet) paths, IT are _expensive_ to maintain and slower than unit test
- can be also just integration of two components, but with the Spring Test support you usually already have this in place
- custom annotation vs. extending from abstract class

## Happy path testing

- Processing an incoming book master data request (SQS -> App -> Network -> Database -> retrievable via UI)
- Creating and retrieving a review (POST to create -> inspect Location header -> retrieve)
- Creating multiple reviews from different users and then expecting the statistics
- TestWebClient & TestRestTemplate explanation (starting the embedded servlet container)
    * Providing the JWT while using them
    * Security rules can be verified using MockMvc
    * MockMvc and @SpringBootTest rationale compared to these clients
- First glance to context caching, will be tackled in best-practices section
