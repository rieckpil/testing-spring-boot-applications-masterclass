package de.rieckpil.courses.book.management;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getOrCreateUser(String name, String email) {
    User user = userRepository.findByNameAndEmail(name, email);

    if (user == null) {
      user = new User();
      user.setName(name);
      user.setEmail(email);
      user.setCreatedAt(LocalDateTime.now()); // UUID.randomUUID();
      user = userRepository.save(user);
    }

    return user;
  }
}
