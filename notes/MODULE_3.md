# Content for Module III: Testing database access

Start writing efficient tests for your database layer while utilizing Spring Boot test features like @DataJpaTest, TestEntityManager, @Sql, etc.

Next, To continue, furthermore, in contrast

## Introduction to the _problem domain_

* How to effectively write tests for your database layer?
* Unit test level you can mock the Spring Data JPA repository
* Direct usage of the EntityManager, your custom SQL queries
* Custom queries on your repository
* You want certainty

## Using an in-memory database for this: H2

* downside of managing two schema scripts or using ddl-auto
* not as close to production as it could, perfect would be to use the same database (same setup, version, config, etc.)
* show how to use @DataJpaTest, what is bootstrapped with it and config for H2 database
* teaser Testcontainers to solve the issues of in-memory database usage

## Introduction to Testcontainers

* using it to start a database
* available modules
* adjusting database version
* dynamically inject Spring Data JPA attributes
* Reuse containers
* JUnit Jupiter Extension
* More Testcontainers usage in further lessons

## Using Testcontainers for the test

* Turn on SQL statements during tests (e.g. demo n+1 problem)
* override the default behaviour of using an in-memory database
* first test to inject beans and using PostgreSQL
* demonstrate @Transactional support of the test
* Not worth validating that the Spring Data repos are working

* Show setup of @DataJpaTest with a service layer
* Verify custom query of the repository
* Demo Pitfalls of the normal EntityManager (first-level cache), missing default constructor
* Using the `TestEntityManager`

* P6SPY to detect database commits
* Using @Sql to prepare data
* Downside of maintaining the scripts, can make sense at some places
