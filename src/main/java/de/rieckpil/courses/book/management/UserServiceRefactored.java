package de.rieckpil.courses.book.management;

import java.time.Clock;
import java.time.LocalDateTime;

public class UserServiceRefactored {

  private final UserRepository userRepository;
  private final Clock clock;

  public UserServiceRefactored(UserRepository userRepository, Clock clock) {
    this.userRepository = userRepository;
    this.clock = clock;
  }

  public User getOrCreateUser(String name, String email) {
    User user = userRepository.findByNameAndEmail(name, email);

    if (user == null) {
      user = new User();
      user.setName(name);
      user.setEmail(email);
      user.setCreatedAt(LocalDateTime.now(clock));
      user = userRepository.save(user);
    }

    return user;
  }
}
