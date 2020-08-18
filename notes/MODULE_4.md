# Content for Module IV: Testing the web-layer


## Introduction to the problem domain

* Test HTTP layer (both RestController and Controller -> Thymeleaf Templates)
* Mock any interaction with underlying layers
* Writing a simple Unit test would leave out too much
* What is missing: Listen to HTTP requests, deserialze input, validate input,
 call business logic, serialize the output, translate exceptions
* Not a real Servlet Environment, but a mocked one
* Plays nicely with Spring Security config, you can test that your endpoints are protected
* Not starting the embedded servlet container

## First steps:

* Test for BookController
* First not use the direct class support for only one controller to make it fail
* Play around with the bean setup
* Show error with Spring Security
* Basic tests for payload and media type of result


## Next steps:

* Use the ReviewController
* Spring Security is on classpath
* Try not to use the ObjectMapper for preparing payload -> not testing the contract (think of renaming attributes)
* JSON payload with Java 14
* Test posting data and corner cases
* Ensure the endpoints are protected
* .andExpect(responseBody().containsError("name", "must not be null"));
