---
name: unit-testing
description: >-
  Write fast, context-free unit tests for Spring Boot business logic using
  JUnit 5, AssertJ, and Mockito. Use when testing a single class or method whose
  collaborators can be mocked and that needs no ApplicationContext — services,
  validators, mappers, domain logic. Covers @ExtendWith(MockitoExtension),
  @Mock/@InjectMocks, stubbing, argument capture, static mocking, and
  parameterized tests. Not for controllers, repositories, or HTTP clients (use a
  slice test instead).
---

# Unit Testing

The fastest, most numerous layer of the pyramid. No Spring, no Testcontainers,
no I/O. Milliseconds per test.

## When this is the right level

Use a unit test when the class under test has **no Spring infrastructure
dependency** that you can't satisfy by hand-constructing it or mocking it:
services, validators, mappers, calculators, domain entities. If you need a
`MockMvc`, an `EntityManager`, or HTTP wiring, you want a slice test instead —
see [slice-testing](../slice-testing/SKILL.md) or
[slice-testing-webmvc](../slice-testing-webmvc/SKILL.md).

## Core recipe

1. Annotate the class with `@ExtendWith(MockitoExtension.class)`.
2. Declare collaborators as `@Mock` fields; the class under test as `@InjectMocks` named `cut`.
3. Arrange (stub with `when(...).thenReturn(...)`), act (call the method), assert (AssertJ `assertThat(...)`).
4. Verify interactions only when the interaction *is* the behavior (e.g. "saves exactly once").

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @InjectMocks private UserService cut;

  @Test
  void shouldCreateUserWhenNoneExists() {
    when(userRepository.findByNameAndEmail("duke", "duke@spring.io")).thenReturn(null);
    when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

    User result = cut.getOrCreateUser("duke", "duke@spring.io");

    assertThat(result.getEmail()).isEqualTo("duke@spring.io");
    verify(userRepository).save(any(User.class));
  }
}
```

## Rules that keep unit tests fast and honest

- **One behavior per test.** The method name states the behavior: `shouldThrowWhenEmailIsBlank`.
- **AssertJ for assertions** (`assertThat(x).isEqualTo(...)`, `.isTrue()`, `.hasSize(n)`); JUnit `assertThrows` for exceptions.
- **Stub only what the test uses.** `MockitoExtension` is strict by default — unnecessary stubs fail the test, which is good. Don't switch to lenient to silence it; delete the stub.
- **Never mock the class under test.** Mock its collaborators only.
- **No `@SpringBootTest`, no `@MockitoBean`.** Those load a context. A unit test constructs `cut` with `new` or `@InjectMocks`.
- **Mock time and randomness** so tests are deterministic (`Mockito.mockStatic(LocalDateTime.class)`, inject a `Clock`).

## More detail

- Worked examples — stubbing, `thenAnswer`, `ArgumentCaptor`, static mocking, parameterized and repeated tests, custom extensions: see [examples.md](examples.md).
