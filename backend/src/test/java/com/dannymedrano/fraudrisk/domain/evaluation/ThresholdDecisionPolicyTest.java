package com.dannymedrano.fraudrisk.domain.evaluation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ThresholdDecisionPolicyTest {
  
  private final ThresholdDecisionPolicy policy = new ThresholdDecisionPolicy();

  @Test
  void shouldAllowMinimumScore() {
    Decision decision = policy.decide(new RiskScore(0));

    assertEquals(Decision.ALLOW, decision);
  }

  @Test
  void shouldAllowScoreImmediatelyBelowReviewThreshold() {
    Decision decision = policy.decide(new RiskScore(39));

    assertEquals(Decision.ALLOW, decision);
  }

  @Test
  void shouldReviewScoreAtReviewThreshold() {
    Decision decision = policy.decide(new RiskScore(40));

    assertEquals(Decision.REVIEW, decision);
  }

  @Test
  void shouldReviewScoreImmediatelyBelowDenyThreshold() {
    Decision decision = policy.decide(new RiskScore(69));

    assertEquals(Decision.REVIEW, decision);
  }

  @Test
  void shouldDenyScoreAtDenyThreshold() {
    Decision decision = policy.decide(new RiskScore(70));

    assertEquals(Decision.DENY, decision);
  }

  @Test
  void shouldDenyMaximumScore() {
    Decision decision = policy.decide(new RiskScore(100));

    assertEquals(Decision.DENY, decision);
  }
}
