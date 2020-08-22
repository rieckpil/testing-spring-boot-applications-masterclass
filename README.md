# Testing Spring Boot Applications [Masterclass](https://rieckpil.de/testing-spring-boot-applications-masterclass/)

[![Build & Test Maven Project](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/workflows/Build%20&%20Test%20Maven%20Project/badge.svg)](https://github.com/rieckpil/testing-spring-boot-applications-masterclass/actions)

## Local Project Setup

### Requirements

Mandatory requirements:

* Java 14 (JDK flavour (OpenJDK/Azul/Oracle) does not matter). For the correct Java version setup I can recommend [JEnv](https://www.youtube.com/watch?v=9FVZyeFDXo0) (Mac/Linux) and the [Maven Toolchains Plugin](https://maven.apache.org/plugins/maven-toolchains-plugin/toolchains/jdk.html) (Windows)

```
$ java -version
openjdk version "14.0.1" 2020-04-14
OpenJDK Runtime Environment AdoptOpenJDK (build 14.0.1+7)
OpenJDK 64-Bit Server VM AdoptOpenJDK (build 14.0.1+7, mixed mode, sharing)
```

* Docker Engine (Community Edition is enough) and Docker Compose

```
$ docker version
Client: Docker Engine - Community
 Version:           19.03.12
 API version:       1.40
 Go version:        go1.13.10
 Git commit:        48a66213fe
 Built:             Mon Jun 22 15:41:33 2020
 OS/Arch:           darwin/amd64
 Experimental:      false

$ docker-compose version
docker-compose version 1.26.2, build eefe0d31
docker-py version: 4.2.2
CPython version: 3.7.7
OpenSSL version: OpenSSL 1.1.1g  21 Apr 2020
```

Optional requirements:

* Maven 3.6 (Project includes also the Maven Wrapper)
* IntelliJ IDEA or any IDE/Code Editor (Eclipse, NetBeans, Code, Atom, etc.)

### Running the project

Assuming your local setups meets all requirments stated above, you can now start the application:

1. Ensure you can build the project `mvn clean verify`
2. Start the required infrastructure components with `docker-compose up`
3. Run the application with `mvn spring-boot:run` or inside your IDE
4. Access http://localhost:8080 for the application frontend
5. (Optional) Access http://localhost:8888 for the Keycloak Admin interface

## Further resources and links

* [Course Landing Page with FAQ](rieckpil.de/testing-spring-boot-applications-masterclass/)
* [Course overview](rieckpil.de/courses/testing-spring-boot-applications-masterclass/)
* [Course login](https://rieckpil.de/wp-login.php)
* [Password reset](https://rieckpil.de/wp-login.php?action=lostpassword)
