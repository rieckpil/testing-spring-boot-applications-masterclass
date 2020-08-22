## Course Structure

## Module 1: Introduction

### Required setup

Estimated duration: _~ 10 min_

* Docker and Docker-Compose
* Java 14
* Node 12
* Maven 3.6 is used, there'll be an own section about Gradle
* TypeScript and ReactJS

### Application Presentation

Estimated duration: _~ 20 min_

* Show the architectural diagram (messaging, network call, database)
* Show some code examples
* Show how it is configured (security, AWS)
* Show Keycloak admin interface
* Explain uses cases of the app
* Users:
  * `duke` with `dukeduke`
  * `mike` with `mikemike`

### Testing techniques

* Show tree with unit/integration test

## Module 2: Testing with Spring Boot Starter Test

* Project setup
    * Show `spring-boot-starter-test`


### Testing with JUnit 5

Estimated duration: _~ 30 min_

* How to write a first test
* Run the test
* Lifecycle
* Explain some concepts using the simplest code (our validator)
* ParameterizedTest
* Show extension based on UUID generator
* Parallelize unit tests

### Mocking with Mockito

Estimated duration: _~ 20 min_

* Mock out classes (doThrow, doReturn, etc.)
* MockitoExtension
* Verify the execution on them
* Stubs vs. Mocks

### Other test dependencies

Estimated duration: _~ 20 min_

* Show Hamcrest
* Show AssertJ
* JsonPath and JSONAssert

## Module 3: ?

## Module 4: Testing database access with @DataJpaTest

Estimated duration: _~ 30 min_

* Introduction to Testcontainers
* Use embedded database pros/cons
* Show usage of PostgreSQL as containers using Testcontainers
* Prepare data with @Sql
* Show `TestEntityManager` and pitfall of first level cache
* Cleanup data after tests

## Module 5: Testing the REST layer

Estimated duration: _~ 20 - 30 min_

* MockMvc
* Show how to include security
* Test some calls

## Module 6: Writing tests with the whole Spring Context

Estimated duration: _~ 60 min_

* Introduction to WireMock
* Making sure Resource Server is working
* Mock external call to WireMock
* Prepare AWS infrastructure with LocalStack

* Testing SQS message consumption
* Use WebTestClient to model a use case

## Module 7: Writing web tests

Estimated duration: _~ 20 - 30 min_

* Happy-path using a WebTest
* Take a look at Selenide
* Write WT with Selenium
    * Write extension for screenshot failures
    * Show how to record the screen


## Module 8: Best practices for testing

Estimated duration: _20 - 30 min_

* Use Failsafe and Surefire and their reports for visualization in CI pipelines
* Context Caching
* Avoid @MockBean and lot of new contexts to be loaded
* given/when/then
* CUT naming
* Make test reproducible and platform independent
* Make test setup simple
