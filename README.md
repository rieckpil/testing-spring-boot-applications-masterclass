# Course Structure

## Application Presentation (~ 20 min)

* Show the architectural diagram (messaging, network call, database)
* Show some code examples
* Explain uses cases of the app

## Introduction to Testing with Spring Boot (~ 45 min)

* Project setup
    * Show `spring-boot-starter-test`

* Introduction to some test dependencies
    * JUnit 5 (with short note on JUnit 4)
    * Mocktio
    * Hamcrest, AssertJ, Json Assertions
    * Testcontainers
    * Awailibility
    * WireMock
    * Selenium

## Testing with JUnit 5 (~ 30 min)

* Explain some concepts using the simplest code
* Parameter
* Lifecycle
* Extensions (explain SpringExtension/MockitoExtension)

## Mocking with Mockito (~ 30 min)

* Mock out classes (doThrow, doReturn, etc.)
* Verify the execution on them
* Stubs vs. Mocks

## Testing database access with @DataJpaTest (~ 30 min)

* Use embedded database pros/cons
* Show usage of PostgreSQL as containers using Testcontainers
* Prepare data with @Sql
* Cleanup data after tests

## Testing the REST layer (~ 30 min)

* MockMvc
* Show how to include security
* Test some calls

## Booting the whole application context with @SpringBootTest (~ 60 min)

* Making sure Resource Server is working
* Mock external call to WireMock
* Prepare AWS infrastructure with LocalStack
* Write WT with Selenium
    * Write extension for screenshot failures
    * Show how to record the screen

* Testing SQS message consumption
* Use WebTestClient to model a use case
* Happy-path using a WebTest

## Best practices (~ 20 min)

* Use Failsafe and Surefire
* Avoid @MockBean and lot of new contexts to be loaded
* Make test reproducible and platform independent
* Make test setup simple
