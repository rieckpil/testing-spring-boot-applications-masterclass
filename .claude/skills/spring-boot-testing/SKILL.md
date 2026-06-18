---
name: spring-boot-testing
description: >-
  Central router for writing and reviewing automated tests in Spring Boot
  applications. Use when creating, fixing, or reviewing Spring Boot tests of any
  kind — plain unit tests, slice tests (@WebMvcTest, @DataJpaTest,
  @RestClientTest, @JsonTest), full @SpringBootTest integration tests,
  Testcontainers setup, or auditing a suite for speed, flakiness,
  parallelizability, and ApplicationContext-cache health. Picks the cheapest
  test level that proves the behavior and hands off to a focused subskill.
---

# Spring Boot Testing

This is a router. Identify the right test level, then open the matching
subskill's `SKILL.md` and follow it. Don't bootstrap a Spring context to test
logic that needs none.

## Stack assumptions (verify in `pom.xml` before relying on them)

This project: **Spring Boot 4.0.2**, **Java 21**, **JUnit 5**, **AssertJ**,
**Mockito 5**, **Testcontainers 2.x**, **WireMock 3**.

Spring Boot 4 **modularized** the test autoconfigure packages. Use the new
import paths, not the Boot 3 `org.springframework.boot.test.autoconfigure.*`
ones:

- `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`
- `org.springframework.boot.jpa.test.autoconfigure.TestEntityManager`
- `org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase`
- `org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient`

Mock Spring beans with **`@MockitoBean`**
(`org.springframework.test.context.bean.override.mockito.MockitoBean`) —
`@MockBean` is removed. Wire Testcontainers with **`@ServiceConnection`** where a
starter supports it.

## Pick the level (cheapest that proves the behavior wins)

| Testing... | Use | Subskill |
| --- | --- | --- |
| Pure logic in one class, no Spring context | JUnit + Mockito | [unit-testing](unit-testing/SKILL.md) |
| Web/MVC controller layer (routing, JSON, validation, status, security) | `@WebMvcTest` | [slice-testing-webmvc](slice-testing-webmvc/SKILL.md) |
| Persistence repository / JPA mappings / queries | `@DataJpaTest` | [slice-testing](slice-testing/SKILL.md) |
| HTTP client or JSON (de)serialization | `@RestClientTest` / `@JsonTest` / MockWebServer | [slice-testing](slice-testing/SKILL.md) |
| End-to-end behavior on the wired app with real infra | `@SpringBootTest` + Testcontainers | [integration-testing](integration-testing/SKILL.md) |
| Standing up real databases/brokers/services | Testcontainers | [testcontainers-setup](testcontainers-setup/SKILL.md) |
| Auditing a suite for speed/flakiness/parallel safety/context-cache splits | Scanner + checklist | [test-setup-reviewer](test-setup-reviewer/SKILL.md) |

## Conventions that apply at every level

- **Naming**: unit and slice tests end in `Test`; full integration tests end in `IT`. The class-under-test field is named `cut`.
- **Test pyramid**: many fast unit tests, fewer slice tests, fewest `@SpringBootTest` tests.
- **The ApplicationContext cache is the single biggest lever on suite speed.** Each distinct test configuration — different `@MockitoBean` set, `properties`, `@ActiveProfiles`, `@Import`, or any `@DirtiesContext` — forces Spring to build and cache a *new* context. Keep configurations identical so contexts are reused. Details in [test-setup-reviewer](test-setup-reviewer/SKILL.md).
- **Determinism**: no ordering dependence, no shared mutable static state, clean DB state per test (`@Transactional` rollback or explicit `@BeforeEach`/`@AfterEach`).

## When the request is broad ("add tests for X")

Start at the lowest level that covers the logic and climb only for behavior that
genuinely needs a wired context. A typical feature ends up with: unit tests for
logic, a `@WebMvcTest` for the controller, a `@DataJpaTest` for the repository,
and one `IT` for the critical end-to-end path.
