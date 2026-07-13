package com.dannymedrano.fraudrisk.domain.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class RiskScoreTest {
  
  @Test
  void shouldAcceptMinimumScore() {
    RiskScore riskScore = new RiskScore(0);

    assertEquals(0, riskScore.value());
  }

  @Test
  void shouldAcceptMaximumScore() {
    RiskScore riskScore = new RiskScore(100);

    assertEquals(100, riskScore.value());
  }

  @Test
  void shouldRejectScoreBelowMinimum() {
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new RiskScore(-1)
    );

    assertEquals("Risk score must be between 0 and 100", exception.getMessage());
  }

  @Test
  void shouldRejectScoreAboveMaximum() {
    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> new RiskScore(101)
    );

    assertEquals("Risk score must be between 0 and 100", exception.getMessage());
  }
}
