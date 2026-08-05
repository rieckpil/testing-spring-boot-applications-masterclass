package de.rieckpil.courses.book.review;

import de.rieckpil.courses.book.management.BookRepository;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.InjectTestInstance;
import io.github.wouterbauweraerts.unitsocializer.core.annotations.TestSubject;
import io.github.wouterbauweraerts.unitsocializer.junit.mockito.annotations.SociableTest;
import org.junit.jupiter.api.Test;

@SociableTest
public class ReviewServiceTwoTest {

  @TestSubject
  ReviewService dummy;

  @InjectTestInstance
  private BookRepository bookRepository;

  @Test
  void setup(){

  }
}
