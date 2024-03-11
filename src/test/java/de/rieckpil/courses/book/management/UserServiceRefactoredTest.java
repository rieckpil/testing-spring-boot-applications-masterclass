package de.rieckpil.courses.book.management;

import java.time.Clock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceRefactoredTest {

  @Mock private Clock clock;

  @Mock private UserRepository userRepository;

  @InjectMocks private UserServiceRefactored cut;

  @Test
  void shouldIncludeCurrentDateTimeWhenCreatingNewUser() {}
}
