# Research on other courses out there

* Baeldung:
  * https://www.baeldung.com/rest-with-spring-course
  * like the structure of the course content
  * like the link on the bottom to the Q&A

* Marco Behler:
  * https://www.marcobehler.com/courses/learning-maven
  * Is this course for me?
  * How does this course work?
  * What topics are not covered? -> writing the course application from scratch (we'll walk through it)

* Petri Kainulainen
  * https://www.testwithspring.com/save-time-by-writing-less-test-code/?utm_source=petri-kainulainen&utm_medium=web&utm_content=courses-page&utm_campaign=test-with-spring-course-sales
  * Focusses on testing
  * No Bullshit terms


# To tackle

- how to test complex scenarios
- unit vs. integration test
- arrange/act/assert test setup given/when/then
- BDDMockito
- happy/corner case path for sliced test, happy path tests for integration tests
- TestEntityManager will flush first level cache
- Show missing default constructor (check JPA) if using the repository tests in a wrong way
- Test behaviour
- Cheat Sheet from Test Driven Development with Spring Boot
- Unit test pyramid: Unit test -> Unit test + Spring -> Integration Test
- Certantity that you don't break things and can push to production with confidence
- What about test coverage? Pay less attention
- add tests where risks might occur (-> not getter/setter)
- too many mocks -> chance to refactor or use a integration test (maintain both application and mocks require effort)

- https://junit.org/junit5/docs/current/user-guide/

- JUnit creates a new instance of each test class before executingeach test method (`per-method` default)
- test class will still be initiated even if the test is disabled
- JUnit 5 requires single constructor
