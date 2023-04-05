package de.rieckpil.courses.book.management;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  User findByNameAndEmail(String name, String email);
}
