package de.rieckpil.courses.book.review;

import java.sql.SQLException;

import javax.sql.DataSource;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest(
    properties = {
      "spring.flyway.enabled=false",
      "spring.jpa.hibernate.ddl-auto=create-drop",
      "spring.datasource.driver-class-name=com.p6spy.engine.spy.P6SpyDriver", // P6Spy
      "spring.datasource.url=jdbc:p6spy:h2:mem:testing;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false" // P6Spy
    })
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {

  @Autowired private EntityManager entityManager;

  @Autowired private ReviewRepository cut;

  @Autowired private DataSource dataSource;

  @Autowired private TestEntityManager testEntityManager;

  @Test
  void notNull() throws SQLException {}

  @Test
  void transactionalSupportTest() {}
}
