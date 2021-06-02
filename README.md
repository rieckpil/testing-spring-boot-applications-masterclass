[![Masterclass](https://rieckpil.de/wp-content/uploads/2020/09/testing-spring-boot-applications-masterclass-course-logo.png)](https://rieckpil.de/testing-spring-boot-applications-masterclass/)

# About the Masterclass


[![Build & Test Maven Project](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/workflows/Build%20&%20Test%20Maven%20Project/badge.svg)](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/actions)

## Introduction

The Testing Spring Boot Applications Masterclass is a deep-dive course on testing your Spring Boot applications. You'll learn how to effectively write unit, integration, and end-to-end tests while utilizing Spring Boot's excellent test support.

TL;DR:

- testing recipes for several layers of your application (e.g., database, messaging, HTTP communication)
- simple and straightforward explanations
- real-world course application with up-to-date testing library versions

After working through the online course ...

- your technical testing skills will improve by understanding the ins-and-outs of testing Spring Boot applications
- you'll deploy to production with more confidence (even on Friday afternoons)
- sleep better at night thanks to a sophisticated test suite.

Enroll [here](https://github.com/rieckpil/testing-spring-boot-applications-masterclass) for the Testing Spring Boot Applications Masterclass.

PS: You can watch four preview lessons by [subscribing to the mailing list](https://rieckpil.de/tsbam-preview-lessons/).

## Course Application Architecture

[![Course Application Architecture](https://rieckpil.de/wp-content/uploads/2020/06/book-reviewr-application-architecture.png)](https://rieckpil.de/testing-spring-boot-applications-masterclass/)

## Testimonials

From [Wim Deblauwe](https://www.wimdeblauwe.com/):

_Philip has made a fantastic overview of the full testing landscape of Spring. The videos are clear and explain details and common pitfalls in great depth. Looking forward to the rest of the course._

From [Siva](https://www.sivalabs.in/2020/10/philip-testing-spring-boot-applications-masterclass-course-review/):

_I got an opportunity to review the course I find it wonderful for learning how to test Spring Boot applications leveraging modern testing frameworks and libraries...._

_I would highly recommend Masterclass for anybody working with Spring Boot applications._

From [Anton Ždanov](https://www.linkedin.com/in/thezdanov/):

_For me testing a Spring application seemed like a challenge involving digging through numerous blog posts, documentation for JUnit, Mockito, and Spring Testing Reference which provide valuable information but are spread out and don't necessarily show the best practices._

_After watching the Testing Spring Boot Applications Masterclass course I feel more confident in writing different types of tests for my apps. The course, videos, and the GitHub repository were of invaluable use to me demonstrating various testing mechanics the Spring ecosystem provides, and I will keep referencing the course materials in the future._

_P.S. The application that is tested in the course is quite complex and covers a lot of real-world testing challenges one might encounter, which I found immensely useful for seeing the bigger picture._

## Further Resources and Links

* [Course Landing Page with FAQ](https://rieckpil.de/testing-spring-boot-applications-masterclass/#FAQ)
* [Course Overview](https://rieckpil.de/courses/testing-spring-boot-applications-masterclass/)
* [Course Login](https://rieckpil.de/wp-login.php)
* [Password Reset](https://rieckpil.de/wp-login.php?action=lostpassword)

# Local Project Setup

## Requirements

Mandatory requirements:

* Java 16 (JDK flavour (OpenJDK/Azul/Oracle) does not matter). For the correct Java version setup I can recommend [JEnv](https://www.youtube.com/watch?v=9FVZyeFDXo0) (Mac/Linux) and the [Maven Toolchains Plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/toolchains/jdk.html) (Windows)

```
$ java -version
openjdk version "16.0.1" 2021-04-20
OpenJDK Runtime Environment AdoptOpenJDK-16.0.1+9 (build 16.0.1+9)
OpenJDK 64-Bit Server VM AdoptOpenJDK-16.0.1+9 (build 16.0.1+9, mixed mode, sharing)
```

* Docker Engine (Community Edition is enough) and Docker Compose:

```
$ docker version
Client: Docker Engine - Community
 Version:           20.10.6
 API version:       1.41
 Go version:        go1.13.15
 Git commit:        370c289
 Built:             Fri Apr  9 22:47:17 2021
 OS/Arch:           linux/amd64
 Context:           default
 Experimental:      true

Server: Docker Engine - Community
 Engine:
  Version:          20.10.6
  API version:      1.41 (minimum version 1.12)
  Go version:       go1.13.15
  Git commit:       8728dd2
  Built:            Fri Apr  9 22:45:28 2021
  OS/Arch:          linux/amd64
  Experimental:     false

$ docker-compose version
docker-compose version 1.26.2, build eefe0d31
docker-py version: 4.2.2
CPython version: 3.7.7
OpenSSL version: OpenSSL 1.1.1g  21 Apr 2020
```

Optional requirements:

* Maven 3.6 (the project also includes the Maven Wrapper).

When using Maven from the command line, make sure `mvn -version` reports the correct Java version:

```
$ mvn -version

Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /home/rieckpil/.m2/wrapper/dists/apache-maven-3.6.3-bin/1iopthnavndlasol9gbrbg6bf2/apache-maven-3.6.3
Java version: 16.0.1, vendor: AdoptOpenJDK, runtime: /usr/lib/jvm/jdk-16.0.1+9
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-73-generic", arch: "amd64", family: "unix"
```

* IntelliJ IDEA or any IDE/Code Editor (Eclipse, NetBeans, Code, Atom, etc.)

## Running the Project Locally

Assuming your local setups meets all requirements as stated above, you can now start the application:

1. Make sure your Docker Engine is up- and running
2. Start the required infrastructure components with `docker-compose up`
3. Run the application with `mvn spring-boot:run` or inside your IDE
4. Access http://localhost:8080 for the application frontend
5. (Optional) Access http://localhost:8888 for the Keycloak Admin interface

Valid application users:

* duke (password `dukeduke`)
* mike (password `mikemike`)

## Running the Tests

Run all **unit** tests with: `mvn test`

Run all **integration & web** tests:

1. Make sure no Docker containers are currently running: `docker ps`
2. Execute `mvn failsafe:integration-test failsafe:verify`

Run **all tests** together:

1. Make sure no Docker container is currently running: `docker ps`
2. Execute `mvn verify`

Skip all tests (don't do this at home):

1. Execute `mvn -Dmaven.test.skip=true verify`

# Troubleshooting Setup Issues

## The tests are failing, but I still want to build the project

You can pass `-DskipTests` to `mvn clean package` if you experience test failures: `mvn clean package -DskipTests` to build the application without running any unit test.

Next, make sure you have the latest version of this project (run `git pull`) and ensure the [build status is green](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/actions).

If you still encounter any test failures, please [create an issue](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/issues) and include information about your environment.

## The Keycloak Docker container terminates during startup

Adjust the `docker-compose.yml` file and remove the setup to import Keycloak configuration on the startup:

```yaml
version: '3'
services:
  # ...
  keycloak:
    image: jboss/keycloak:11.0.0
    environment:
      - KEYCLOAK_USER=keycloak
      - KEYCLOAK_PASSWORD=keycloak
      - DB_VENDOR=h2
    ports:
    - 8888:8080
```

Next, start everything with `docker-compose up` and watch the following video to [configure Keycloak manually](https://vimeo.com/458246315).
