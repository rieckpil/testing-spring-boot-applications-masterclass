# Unit Testing — Examples

Concrete patterns for context-free JUnit 5 + Mockito + AssertJ tests.

## Contents
- Stubbing return values and exceptions
- Dynamic stubbing with `thenAnswer`
- Capturing arguments with `ArgumentCaptor`
- Mocking static methods (time, `UUID`)
- Parameterized and repeated tests
- A custom `ParameterResolver` extension

## Stubbing return values and exceptions

```java
when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
when(userRepository.findById(99L)).thenReturn(Optional.empty());
when(paymentGateway.charge(any())).thenThrow(new GatewayDownException());

assertThatThrownBy(() -> cut.checkout(cart))
    .isInstanceOf(CheckoutFailedException.class)
    .hasMessageContaining("gateway");
```

## Dynamic stubbing with `thenAnswer`

Use when the return value depends on the argument — e.g. simulating a generated id on save:

```java
when(userRepository.save(any(User.class)))
    .thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return user;
    });
```

## Capturing arguments with `ArgumentCaptor`

Assert on *what* was passed to a collaborator, not just that it was called:

```java
@Captor private ArgumentCaptor<User> userCaptor;

@Test
void shouldPersistUserWithCurrentTimestamp() {
  cut.getOrCreateUser("duke", "duke@spring.io");

  verify(userRepository).save(userCaptor.capture());
  assertThat(userCaptor.getValue().getCreatedAt()).isNotNull();
}
```

## Mocking static methods (time, UUID)

`mockito-inline` (or Mockito 5's default inline maker) enables static mocking.
Keep the scope tight with try-with-resources so the mock is uninstalled
immediately:

```java
LocalDateTime fixed = LocalDateTime.of(2020, 1, 1, 12, 0);

try (MockedStatic<LocalDateTime> mocked = Mockito.mockStatic(LocalDateTime.class)) {
  mocked.when(LocalDateTime::now).thenReturn(fixed);

  User result = cut.getOrCreateUser("duke", "duke@spring.io");

  assertThat(result.getCreatedAt()).isEqualTo(fixed);
}
```

Prefer injecting a `java.time.Clock` over static mocking when you control the
production class — it is cleaner and parallel-safe.

## Parameterized and repeated tests

```java
@ParameterizedTest
@CsvFileSource(resources = "/badReview.csv")
void shouldRejectLowQualityReviews(String review) {
  assertThat(cut.doesMeetQualityStandards(review)).isFalse();
}

@ParameterizedTest
@ValueSource(strings = {"", " ", "\t"})
void shouldRejectBlankInput(String input) {
  assertThatThrownBy(() -> cut.validate(input)).isInstanceOf(IllegalArgumentException.class);
}

@RepeatedTest(5)
void shouldHoldForRandomizedInput() { /* ... */ }
```

## A custom `ParameterResolver` extension

Inject generated test data via a JUnit 5 extension instead of duplicating setup:

```java
public class RandomReviewParameterResolverExtension implements ParameterResolver {

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.PARAMETER)
  public @interface RandomReview {}

  @Override
  public boolean supportsParameter(ParameterContext pc, ExtensionContext ec) {
    return pc.isAnnotated(RandomReview.class);
  }

  @Override
  public Object resolveParameter(ParameterContext pc, ExtensionContext ec) {
    return RandomReviewGenerator.next();
  }
}
```

Usage: `@ExtendWith(RandomReviewParameterResolverExtension.class)` on the class,
then `void test(@RandomReview String review)`.
