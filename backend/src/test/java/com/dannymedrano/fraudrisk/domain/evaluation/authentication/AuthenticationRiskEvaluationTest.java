package com.dannymedrano.fraudrisk.domain.evaluation.authentication;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationRiskEvaluationTest {

  private static final RuleResult NEW_DEVICE_RESULT = new RuleResult(
      "AUTH_NEW_DEVICE",
      "Authentication from a new device",
      true,
      25,
      "The synthetic event indicates authentication from a new device",
      "1.0");

  private static final RuleResult FAILED_ATTEMPTS_RESULT = new RuleResult(
      "AUTH_REPEATED_FAILURES",
      "Repeated failed authentication attempts",
      false,
      0,
      "No repeated-failure risk signal was detected",
      "1.0");

  @Test
  void shouldCreateValidEvaluationAndPreserveRuleOrder() {
    AuthenticationRiskEvaluation evaluation = new AuthenticationRiskEvaluation(
        "synthetic-auth-event-003",
        new RiskScore(25),
        Decision.ALLOW,
        List.of(
            NEW_DEVICE_RESULT,
            FAILED_ATTEMPTS_RESULT));

    assertEquals(
        "synthetic-auth-event-003",
        evaluation.eventId());
    assertEquals(new RiskScore(25), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(
        List.of(
            NEW_DEVICE_RESULT,
            FAILED_ATTEMPTS_RESULT),
        evaluation.ruleResults());
  }

  @Test
  void shouldDefensivelyCopyRuleResults() {
    List<RuleResult> originalResults = new ArrayList<>();
    originalResults.add(NEW_DEVICE_RESULT);

    AuthenticationRiskEvaluation evaluation = new AuthenticationRiskEvaluation(
        "synthetic-auth-event-003",
        new RiskScore(25),
        Decision.ALLOW,
        originalResults);

    originalResults.add(FAILED_ATTEMPTS_RESULT);

    assertEquals(
        List.of(NEW_DEVICE_RESULT),
        evaluation.ruleResults());
  }

  @Test
  void shouldReturnUnmodifiableRuleResults() {
    AuthenticationRiskEvaluation evaluation = new AuthenticationRiskEvaluation(
        "synthetic-auth-event-003",
        new RiskScore(25),
        Decision.ALLOW,
        List.of(NEW_DEVICE_RESULT));

    assertThrows(
        UnsupportedOperationException.class,
        () -> evaluation.ruleResults().add(FAILED_ATTEMPTS_RESULT));
  }

  @Test
  void shouldRejectNullEventId() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluation(
            null,
            new RiskScore(25),
            Decision.ALLOW,
            List.of(NEW_DEVICE_RESULT)));

    assertEquals(
        "Event ID must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldRejectBlankEventId() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluation(
            "   ",
            new RiskScore(25),
            Decision.ALLOW,
            List.of(NEW_DEVICE_RESULT)));

    assertEquals(
        "Event ID must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRiskScore() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluation(
            "synthetic-auth-event-003",
            null,
            Decision.ALLOW,
            List.of(NEW_DEVICE_RESULT)));

    assertEquals(
        "Risk score must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullDecision() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluation(
            "synthetic-auth-event-003",
            new RiskScore(25),
            null,
            List.of(NEW_DEVICE_RESULT)));

    assertEquals(
        "Decision must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRuleResults() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluation(
            "synthetic-auth-event-003",
            new RiskScore(25),
            Decision.ALLOW,
            null));

    assertEquals(
        "Rule results must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRuleResultEntry() {
    List<RuleResult> resultsWithNull = new ArrayList<>();
    resultsWithNull.add(NEW_DEVICE_RESULT);
    resultsWithNull.add(null);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluation(
            "synthetic-auth-event-003",
            new RiskScore(25),
            Decision.ALLOW,
            resultsWithNull));

    assertEquals(
        "Rule results must not contain null entries",
        exception.getMessage());
  }
}
