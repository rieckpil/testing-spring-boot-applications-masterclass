package de.rieckpil.courses.book.management;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceRefactoredTest {

  @Mock
  private Clock clock;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserServiceRefactored cut;

  @Test
  void shouldIncludeCurrentDateTimeWhenCreatingNewUser() {

    when(userRepository.findByNameAndEmail("duke", "duke@spring.io")).thenReturn(null);
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return user;
    });

    LocalDateTime defaultLocalDateTime = LocalDateTime.of(2020, 1, 1, 12, 0);
    Clock fixedClock = Clock.fixed(defaultLocalDateTime.toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
    when(clock.instant()).thenReturn(fixedClock.instant());
    when(clock.getZone()).thenReturn(fixedClock.getZone());

    User result = cut.getOrCreateUser("duke", "duke@spring.io");

    assertEquals(defaultLocalDateTime, result.getCreatedAt());
  }

}
