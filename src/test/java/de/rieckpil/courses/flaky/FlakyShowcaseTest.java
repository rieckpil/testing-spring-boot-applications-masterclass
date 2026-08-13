package de.rieckpil.courses.flaky;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Intentionally flaky unit tests to experiment with TestLens flaky test detection. Do not fix or
 * stabilize them, the non-determinism is the point.
 */
class FlakyShowcaseTest {

  private int visitCounter = 0;

  @Test
  void shouldCountAllVisitsFromConcurrentThreads() throws InterruptedException {
    int threadCount = 4;
    int incrementsPerThread = 100;
    ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startSignal = new CountDownLatch(1);

    for (int threadIndex = 0; threadIndex < threadCount; threadIndex++) {
      executorService.submit(
          () -> {
            startSignal.await();
            for (int incrementIndex = 0; incrementIndex < incrementsPerThread; incrementIndex++) {
              // unsynchronized increment, concurrent updates can get lost
              visitCounter++;
            }
            return null;
          });
    }

    startSignal.countDown();
    executorService.shutdown();
    assertTrue(executorService.awaitTermination(5, TimeUnit.SECONDS));

    assertEquals(threadCount * incrementsPerThread, visitCounter);
  }

  @Test
  void shouldFinishSlowComputationWithinDeadline() throws InterruptedException {
    long deadlineInMilliseconds = 50;
    long simulatedWorkInMilliseconds = ThreadLocalRandom.current().nextLong(20, 80);

    long startTime = System.nanoTime();
    Thread.sleep(simulatedWorkInMilliseconds);
    long elapsedInMilliseconds = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);

    assertTrue(
        elapsedInMilliseconds <= deadlineInMilliseconds,
        "Computation took "
            + elapsedInMilliseconds
            + " ms but the deadline is "
            + deadlineInMilliseconds
            + " ms");
  }
}
