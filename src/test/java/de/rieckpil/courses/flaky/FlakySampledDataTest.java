package de.rieckpil.courses.flaky;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Intentionally flaky unit test to experiment with TestLens flaky test detection. The assertion
 * depends on randomly sampled data. Do not fix or stabilize it, the non-determinism is the point.
 */
class FlakySampledDataTest {

  @Test
  void shouldContainHighRatingInRandomReviewSample() {
    List<Integer> sampledRatings =
        IntStream.range(0, 5)
            .map(sampleIndex -> ThreadLocalRandom.current().nextInt(1, 6))
            .boxed()
            .toList();

    assertTrue(
        sampledRatings.contains(5),
        "Expected at least one five-star rating in the sample but got " + sampledRatings);
  }
}
