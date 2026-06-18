---
name: slice-testing-webmvc
description: >-
  Write Spring MVC web-layer slice tests with @WebMvcTest and MockMvc. Use when
  testing controllers in isolation — request routing, path/query binding, JSON
  serialization, bean validation, HTTP status codes, error handling, and Spring
  Security rules — without starting a server or loading the persistence layer.
  Covers @MockitoBean for service collaborators, importing the security config,
  MockMvc request/response assertions, and testing secured endpoints with JWT.
---

# Web MVC Slice Testing (`@WebMvcTest`)

Loads only the web layer — controllers, `@ControllerAdvice`, converters,
filters, `WebMvcConfigurer`, security — and nothing else. Service and repository
beans are absent, so you provide them as `@MockitoBean`. No server starts;
requests go through `MockMvc` in-process.

## When this is the right level

Testing controller behavior: routing, request binding, content negotiation,
JSON shape, validation errors, status codes, and security. For business logic,
drop to [unit-testing](../unit-testing/SKILL.md). For the full wired stack with a
real DB, climb to [integration-testing](../integration-testing/SKILL.md).

## Core recipe

```java
@WebMvcTest(BookController.class)
@Import(WebSecurityConfig.class) // load the real security rules into the slice
class BookControllerTest {

  @MockitoBean private BookManagementService bookManagementService;
  @Autowired private MockMvc mockMvc;

  @Test
  void shouldReturnEmptyArrayWhenNoBooksExist() throws Exception {
    this.mockMvc
        .perform(get("/api/books").header(ACCEPT, APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON))
        .andExpect(jsonPath("$.size()", is(0)));
  }
}
```

Key import (Spring Boot 4): `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`.

## Rules

- **Scope the slice**: `@WebMvcTest(BookController.class)` loads one controller. `@WebMvcTest` with no argument loads *all* controllers — slower and a context-cache splitter. Prefer naming the controller.
- **Mock every collaborator the controller needs** with `@MockitoBean`. A missing bean fails context startup.
- **Security is part of the web layer.** If the app has a `SecurityFilterChain`, `@Import` its config so rules are exercised; otherwise endpoints behave unsecured and tests lie. Test both authorized and unauthorized paths.
- **Assert the contract, not the internals**: status, content type, and `jsonPath` on the body. Assert that internal fields (e.g. database `id`) are *absent* when they shouldn't be exposed: `jsonPath("$[0].id").doesNotExist()`.
- **Use `MockMvcResultMatchers` statically.** Add `.andDo(print())` only while debugging — remove it before committing.
- **Keep `@MockitoBean` sets identical across web tests** for the same controller so they share one cached context. See [test-setup-reviewer](../test-setup-reviewer/SKILL.md).

## More detail

- Validation errors, `@ControllerAdvice`, POST with JSON body, file uploads, content negotiation, and securing endpoints with a JWT (`SecurityMockMvcRequestPostProcessors.jwt()`): see [examples.md](examples.md).
