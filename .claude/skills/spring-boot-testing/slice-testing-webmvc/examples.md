# Web MVC Slice Testing — Examples

`@WebMvcTest` + `MockMvc` patterns. Static imports assumed:

```java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
```

## Contents
- Stubbed service returning data
- POST with a JSON body
- Bean validation failures
- Global exception handling (`@ControllerAdvice`)
- Content negotiation (reject XML)
- Secured endpoints with a JWT
- Unauthorized / forbidden paths

## Stubbed service returning data

```java
when(bookManagementService.getAllBooks()).thenReturn(List.of(bookOne, bookTwo));

this.mockMvc
    .perform(get("/api/books").header(ACCEPT, APPLICATION_JSON_VALUE))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.size()", is(2)))
    .andExpect(jsonPath("$[0].isbn", is("42")))
    .andExpect(jsonPath("$[0].title", is("Java 14")))
    .andExpect(jsonPath("$[0].id").doesNotExist()); // internal id not leaked
```

## POST with a JSON body

```java
this.mockMvc
    .perform(post("/api/books")
        .contentType(APPLICATION_JSON)
        .content("""
            { "isbn": "42", "title": "Effective Java" }
            """))
    .andExpect(status().isCreated())
    .andExpect(header().string(LOCATION, containsString("/api/books/")));
```

## Bean validation failures

A `@Valid` body with a violated constraint should yield 400 without ever
touching the service:

```java
this.mockMvc
    .perform(post("/api/books").contentType(APPLICATION_JSON).content("{}"))
    .andExpect(status().isBadRequest());

verifyNoInteractions(bookManagementService);
```

## Global exception handling (`@ControllerAdvice`)

A `@ControllerAdvice` in the controller package is picked up by the slice. Stub
the service to throw and assert the mapped problem response:

```java
when(bookManagementService.getBook("42")).thenThrow(new BookNotFoundException("42"));

this.mockMvc
    .perform(get("/api/books/42"))
    .andExpect(status().isNotFound())
    .andExpect(jsonPath("$.detail", containsString("42")));
```

## Content negotiation (reject XML)

```java
this.mockMvc
    .perform(get("/api/books").header(ACCEPT, APPLICATION_XML_VALUE))
    .andExpect(status().isNotAcceptable());
```

## Secured endpoints with a JWT

For OAuth2 resource-server apps, inject an authenticated principal with the
`jwt()` post-processor instead of building a real token:

```java
this.mockMvc
    .perform(post("/api/books")
        .with(jwt().jwt(builder -> builder.claim("email", "duke@spring.io")))
        .contentType(APPLICATION_JSON)
        .content(payload))
    .andExpect(status().isCreated());
```

Add authorities when the rule checks them: `jwt().authorities(new SimpleGrantedAuthority("SCOPE_book:write"))`.

## Unauthorized / forbidden paths

```java
// no credentials -> 401
this.mockMvc.perform(post("/api/books").contentType(APPLICATION_JSON).content(payload))
    .andExpect(status().isUnauthorized());

// authenticated but missing authority -> 403
this.mockMvc.perform(post("/api/books").with(jwt()).contentType(APPLICATION_JSON).content(payload))
    .andExpect(status().isForbidden());
```
