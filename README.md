# Deploy Spring Boot Applications With More Confidence

[![Masterclass](https://rieckpil.de/wp-content/uploads/2020/09/testing-spring-boot-applications-masterclass-course-logo.png)](https://rieckpil.de/testing-spring-boot-applications-masterclass/)

[![](https://img.shields.io/badge/Spring%20Boot%20Version-3.4.5-orange)](/pom.xml)
[![](https://img.shields.io/badge/Java%20Version-21-orange)](/pom.xml)
[![](https://img.shields.io/badge/Enroll-Now-orange)](https://rieckpil.de/testing-spring-boot-applications-masterclass/)

## 😓 Tired of the Friday Afternoon Dread?
You know the feeling...

It's Friday afternoon. You're late with your feature and about to push your latest Spring Boot application to production.

Your stomach tightens.

"Did I test everything properly? What if something breaks? What if I become the reason for that 2 AM emergency call on the weekend?"

## 🔥 The Cost of Inadequate Testing is Enormous

- **Buggy** **deployments** that damage your team's reputation
- **Late** **nights** **debugging** issues that should've been caught earlier
- **Crippling** **anxiety** every time you push to production
- **Technical** **debt** that grows more painful with each sprint
- **Lost** **weekends** **fixing** what should never have broken

## 💡 What If You Could Deploy With Complete Confidence?

Imagine:

- Pushing to production on Friday afternoon **without a second thought**
- Your manager praising your code's **rock**-**solid** **reliability**
- Being the go-to person for **solving complex testing challenges**
- **Accelerating** **your** **career** as you deliver higher quality code faster
- Feeling **absolute peace** of mind knowing your tests have you covered

## 🚀 The Solution: Testing Spring Boot Applications Masterclass
Are you tired of:

- Spending countless hours debugging production issues that should have been caught in testing?
- Feeling anxious about deploying on Fridays because your test coverage isn't comprehensive?
- Wrestling with complex microservice architectures that are difficult to test properly?
- Copying and pasting test code from Stack Overflow without understanding the underlying principles?
- Missing deadlines because of unexpected bugs and regressions?
- Mindlessly applying cargo cult testing practices that don't fit your application's needs?

The [Testing Spring Boot Applications Masterclass](https://rieckpil.de/testing-spring-boot-applications-masterclass/) transforms you from feeling uncertain about your application's reliability to being confident in your testing strategy, making you a more **productive**, **efficient**, and **valuable** **developer**.

## 🎓 Why This Course?

Automated testing is (unfortunately) **often neglected**, but it’s the key to building robust, reliable Spring Boot applications and shipping features with confidence.

This Masterclass will teach you everything you need to know about testing Spring Boot applications—from unit tests to end-to-end tests—so you can stop guessing and start delivering with peace of mind.

What You’ll Gain:

-	Confidence in Every Deployment: Push to production—even on Fridays—without breaking a sweat.
-	Master Testing Best Practices: Learn to test every layer: databases, messaging, HTTP communication, and more.
-	Efficiency and Joy in Testing: Testing doesn’t have to be a frustrating chore. Let’s make it fun, fast, and effective!

## 🏆 Success Stories From Real Developers

> "After taking Philip's course, I deployed on a Friday afternoon for the first time in my career. My confidence in our test suite is that strong now."

— Senior Developer at a Fortune 500 Company


> "Philip has made a fantastic overview of the full testing landscape of Spring. The videos are clear and explain details and common pitfalls in great depth."

— Wim Deblauwe, Experienced Java Developer


> "This course was a complete game-changer for me. I went from dreading tests to actually enjoying writing them. And more importantly, my boss noticed."

— Java Developer with 5 years experience


> "I would highly recommend Masterclass for anybody working with Spring Boot applications."

— Siva, Java Developer and Tech Blogger

## What Makes This Course Different

- **Production-Grade Application**: You'll work with a real microservice architecture that mirrors actual business applications, not oversimplified examples.
- **Comprehensive Coverage**: From basic unit tests to complex integration scenarios, you'll learn testing strategies for every layer of your application.
- **Practical Approach**: Every concept is taught through hands-on examples that you can immediately apply to your projects.

Throughout the course you'll learn how to effectively use well-known testing libraries like JUnit 5, Mockito, Awaitility, LocalStack, Testcontainers, Selenide, WireMock, MockWebServer, and JsonPath.

## Course Application Architecture

To mirror a typical modern microservice architecture, the demo application uses the following tech stack and infrastructure components:

- Keycloak (open source identity and access management solution) to secure parts of the frontend and backend with OpenID Connect/OAuth 2.0
- Amazon SQS (Simple Queuing Service) to demonstrate testing asynchronous message processing
- PostgreSQL (RDBMS) to demonstrate testing with a relational database
- Single Page Application Frontend with React and TypeScript
- Spring Boot backend with Java
- Dependency on a remote REST API to demonstrate testing HTTP communication

<p align="center">
  <a href="https://rieckpil.de/testing-spring-boot-applications-masterclass/">
    <img src="https://rieckpil.de/wp-content/uploads/2021/11/book-reviewr-application-architecture-750x666-1.png" alt="Testing Spring Boot Applications Technical Architecture">
  </a>
</p>

Even though the technical setup for your day-to-day projects might differ, the testing recipes you'll learn are generic, and you can easily apply them for your tech stacks.

## More Testimonials

<p align="center">
  <a href="https://rieckpil.de/testing-spring-boot-applications-masterclass/">
    <img src="https://rieckpil.de/wp-content/uploads/2023/03/tsbam-testiomonials.png" alt="Testing Spring Boot Applications Masterclass Testimonials">
  </a>
</p>

## Ready to Transform Your Testing Skills?

» [Enroll now for the Testing Spring Boot Applications Masterclass](https://rieckpil.de/testing-spring-boot-applications-masterclass/).

💡 **Money-back guarantee**: Not satisfied? Get a full refund within 60 days, no questions asked.

## Further Resources and Links

`main` branch: [![Build & Test Maven Project (main)](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/workflows/Build%20&%20Test%20Maven%20Project/badge.svg)](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/actions/workflows/maven.yml?query=branch%3Amain)

`code-along` branch: [![Build & Test Maven Project (code-along)](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/workflows/Build%20&%20Test%20Maven%20Project/badge.svg?branch=code-along)](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/actions/workflows/maven.yml?query=branch%3Acode-along)

* [Course Landing Page with FAQ](https://rieckpil.de/testing-spring-boot-applications-masterclass/#FAQ)
* [Course Overview](https://rieckpil.de/courses/testing-spring-boot-applications-masterclass/)
* [Course Login](https://rieckpil.de/wp-login.php)
* [Password Reset](https://rieckpil.de/wp-login.php?action=lostpassword)

# Local Project Setup

## Requirements

Mandatory requirements:

* Java 21 (JDK flavour (OpenJDK/Azul/Oracle) does not matter). For the correct Java version setup I can recommend [JEnv](https://www.youtube.com/watch?v=9FVZyeFDXo0) (Mac/Linux) and the [Maven Toolchains Plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/toolchains/jdk.html) (Windows)

```
$ java -version
openjdk version "21.0.1" 2023-10-17 LTS
OpenJDK Runtime Environment Temurin-21.0.1+12 (build 21.0.1+12-LTS)
OpenJDK 64-Bit Server VM Temurin-21.0.1+12 (build 21.0.1+12-LTS, mixed mode)
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

* Maven >= 3.6 (the project also includes the Maven Wrapper).

When using Maven from the command line, make sure `./mvnw -version` reports the correct Java version:

```
$ ./mvnw -version

Apache Maven 3.8.4 (9b656c72d54e5bacbed989b64718c159fe39b537)
Maven home: /home/rieckpil/.m2/wrapper/dists/apache-maven-3.8.4-bin/52ccbt68d252mdldqsfsn03jlf/apache-maven-3.8.4
Java version: 17.0.1, vendor: Eclipse Adoptium, runtime: /usr/lib/jvm/jdk-17.0.1+12
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.4.0-92-generic", arch: "amd64", family: "unix"
```

* IntelliJ IDEA or any IDE/Code Editor (Eclipse, NetBeans, Code, Atom, etc.)

## Running the Project Locally

Assuming your local setups meets all requirements as stated above, you can now start the application:

1. Make sure your Docker Engine is up- and running
2. Start the required infrastructure components with `docker-compose up`
3. Run the application with `./mvnw spring-boot:run` or inside your IDE
4. Access http://localhost:8080 for the application frontend
5. (Optional) Access http://localhost:8888 for the Keycloak Admin interface

Valid application users:

* duke (password `dukeduke`)
* mike (password `mikemike`)

## Running the Tests

_Replace `./mvnw` with `mvnw.cmd` if you're running on Windows._

Run all **unit** tests (Maven Surefire Plugin): `./mvnw test`

Run all **integration & web** tests (Maven Failsafe plugin):

1. Make sure no conflicting Docker containers are currently running: `docker ps`
2. Make sure the test classes have been compiled and the frontend has been build and is part of the `target/classes/public` folder: `./mvnw package -DskipTest`
3. Execute `./mvnw failsafe:integration-test failsafe:verify`

Run **all tests** together:

1. Make sure no conflicting Docker container is currently running: `docker ps`
2. Execute `./mvnw verify`

Skip all tests (don't do this at home):

1. Execute `./mvnw -DskipTests=true verify`

# Troubleshooting Setup Issues

## The application fails to start on ARM64 (e.g. MacBook Pro M1)

See this [GitHub issue](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/issues/31) for resolving it.

## How to skip the Frontend Maven Plugin execution?

For skipping the frontend build, add `-Dskip.installnodenpm -Dskip.npm` to your Maven command, e.g., `./mvnw test -Dskip.installnodenpm -Dskip.npm`.

## The tests are failing, but I still want to build the project

You can pass `-DskipTests` to `./mvnw package` if you experience test failures: `./mvnw package -DskipTests` to build the application without running any unit test.

Next, make sure you have the latest version of this project (run `git pull`) and ensure the [build status is green](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/actions).

If you still encounter any test failures, please [create an issue](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/issues) and include information about your environment.

## The Keycloak Docker container terminates during startup

Adjust the `docker-compose.yml` file and remove the setup to import Keycloak configuration on the startup:

```yaml
version: '3.8'
services:
  # ...
  keycloak:
    image: quay.io/keycloak/keycloak:18.0.0-legacy
    environment:
      - KEYCLOAK_USER=keycloak
      - KEYCLOAK_PASSWORD=keycloak
      - DB_VENDOR=h2
    ports:
    - "8888:8080"
```

Next, start everything with `docker-compose up` and watch the following video to [configure Keycloak manually](https://vimeo.com/458246315).
