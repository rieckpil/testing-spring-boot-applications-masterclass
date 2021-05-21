[![Masterclass](https://rieckpil.de/wp-content/uploads/2020/09/testing-spring-boot-applications-masterclass-course-logo.png)](https://rieckpil.de/testing-spring-boot-applications-masterclass/)

# Local Project Setup

[![Build & Test Maven Project](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/workflows/Build%20&%20Test%20Maven%20Project/badge.svg)](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/actions)

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

## Running the project

Assuming your local setups meets all requirements stated above, you can now start the application:

1. Make sure your Docker Engine is up- and running
2. Start the required infrastructure components with `docker-compose up`
3. Run the application with `mvn spring-boot:run` or inside your IDE
4. Access http://localhost:8080 for the application frontend
5. (Optional) Access http://localhost:8888 for the Keycloak Admin interface

Valid application users:

* duke (password `dukeduke`)
* mike (password `mikemike`)

## Running the tests

To run all **unit** tests: `mvn test`

To run all **integration & web** tests:

1. Make sure no Docker container is currently running: `docker ps`
2. Execute `mvn failsafe:integration-test && mvn failsafe:verify`

To run **all tests** together:

1. Make sure no Docker container is currently running: `docker ps`
2. Execute `mvn verify`

# Further resources and links

* [Course Landing Page with FAQ](https://rieckpil.de/testing-spring-boot-applications-masterclass/#FAQ)
* [Course overview](https://rieckpil.de/courses/testing-spring-boot-applications-masterclass/)
* [Course login](https://rieckpil.de/wp-login.php)
* [Password reset](https://rieckpil.de/wp-login.php?action=lostpassword)

# Troubleshooting setup issues

## The tests are failing, but I still want to build the project

You can pass `-DskipTests` to `mvn clean verify` if you experience test failures: `mvn clean verify -DskipTests`

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
