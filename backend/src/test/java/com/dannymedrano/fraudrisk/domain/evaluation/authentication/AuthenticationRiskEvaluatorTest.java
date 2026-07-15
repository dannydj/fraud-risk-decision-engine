package com.dannymedrano.fraudrisk.domain.evaluation.authentication;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationChannel;
import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;
import com.dannymedrano.fraudrisk.domain.rule.authentication.AuthenticationRiskRule;
import com.dannymedrano.fraudrisk.domain.rule.authentication.FailedAttemptsAuthenticationRule;
import com.dannymedrano.fraudrisk.domain.rule.authentication.NewDeviceAuthenticationRule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationRiskEvaluatorTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-14T22:00:00Z");

  private final AuthenticationRiskEvaluator evaluator = new AuthenticationRiskEvaluator(
      List.of(
          new NewDeviceAuthenticationRule(),
          new FailedAttemptsAuthenticationRule()));

  @Test
  void shouldAllowWhenNoRuleIsTriggered() {
    AuthenticationRiskEvaluation evaluation = evaluator.evaluate(createEvent(false, 0));

    assertEquals(new RiskScore(0), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(
        "AUTH_NEW_DEVICE",
        evaluation.ruleResults().get(0).ruleCode());
    assertEquals(
        "AUTH_REPEATED_FAILURES",
        evaluation.ruleResults().get(1).ruleCode());
  }

  @Test
  void shouldAllowWhenOnlyNewDeviceRuleIsTriggered() {
    AuthenticationRiskEvaluation evaluation = evaluator.evaluate(createEvent(true, 0));

    assertEquals(new RiskScore(25), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
  }

  @Test
  void shouldAllowWhenOnlyFailedAttemptsRuleIsTriggered() {
    AuthenticationRiskEvaluation evaluation = evaluator.evaluate(createEvent(false, 3));

    assertEquals(new RiskScore(35), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
  }

  @Test
  void shouldReviewWhenBothAuthenticationRulesAreTriggered() {
    AuthenticationRiskEvaluation evaluation = evaluator.evaluate(createEvent(true, 3));

    assertEquals(
        "synthetic-auth-event-004",
        evaluation.eventId());
    assertEquals(new RiskScore(60), evaluation.riskScore());
    assertEquals(Decision.REVIEW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
  }

  @Test
  void shouldIncludeNonTriggeredRuleResults() {
    AuthenticationRiskEvaluation evaluation = evaluator.evaluate(createEvent(true, 0));

    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(true, evaluation.ruleResults().get(0).triggered());
    assertEquals(false, evaluation.ruleResults().get(1).triggered());
  }

  @Test
  void shouldPreserveConfiguredRuleOrder() {
    AuthenticationRiskEvaluator reversedEvaluator = new AuthenticationRiskEvaluator(
        List.of(
            new FailedAttemptsAuthenticationRule(),
            new NewDeviceAuthenticationRule()));

    AuthenticationRiskEvaluation evaluation = reversedEvaluator.evaluate(createEvent(true, 3));

    assertEquals(
        "AUTH_REPEATED_FAILURES",
        evaluation.ruleResults().get(0).ruleCode());
    assertEquals(
        "AUTH_NEW_DEVICE",
        evaluation.ruleResults().get(1).ruleCode());
  }

  @Test
  void shouldCapCombinedScoreAtMaximum() {
    AuthenticationRiskRule firstStubRule = event -> triggeredResult("SYNTHETIC_RULE_ONE", 70);

    AuthenticationRiskRule secondStubRule = event -> triggeredResult("SYNTHETIC_RULE_TWO", 60);

    AuthenticationRiskEvaluator cappedEvaluator = new AuthenticationRiskEvaluator(
        List.of(firstStubRule, secondStubRule));

    AuthenticationRiskEvaluation evaluation = cappedEvaluator.evaluate(createEvent(false, 0));

    assertEquals(new RiskScore(100), evaluation.riskScore());
    assertEquals(Decision.DENY, evaluation.decision());
  }

  @Test
  void shouldAllowWhenNoRulesAreConfigured() {
    AuthenticationRiskEvaluator evaluatorWithoutRules = new AuthenticationRiskEvaluator(List.of());

    AuthenticationRiskEvaluation evaluation = evaluatorWithoutRules.evaluate(createEvent(false, 0));

    assertEquals(new RiskScore(0), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(List.of(), evaluation.ruleResults());
  }

  @Test
  void shouldDefensivelyCopyConfiguredRules() {
    List<AuthenticationRiskRule> originalRules = new ArrayList<>();

    originalRules.add(new NewDeviceAuthenticationRule());

    AuthenticationRiskEvaluator copiedRulesEvaluator = new AuthenticationRiskEvaluator(originalRules);

    originalRules.add(new FailedAttemptsAuthenticationRule());

    AuthenticationRiskEvaluation evaluation = copiedRulesEvaluator.evaluate(createEvent(true, 3));

    assertEquals(1, evaluation.ruleResults().size());
    assertEquals(
        "AUTH_NEW_DEVICE",
        evaluation.ruleResults().get(0).ruleCode());
  }

  @Test
  void shouldRejectNullRulesCollection() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluator(null));

    assertEquals(
        "Authentication rules must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRuleEntry() {
    List<AuthenticationRiskRule> rulesWithNull = new ArrayList<>();

    rulesWithNull.add(new NewDeviceAuthenticationRule());
    rulesWithNull.add(null);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluator(rulesWithNull));

    assertEquals(
        "Authentication rules must not contain null entries",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullAuthenticationEvent() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> evaluator.evaluate(null));

    assertEquals(
        "Authentication event must not be null",
        exception.getMessage());
  }

  @Test
  void shouldIgnoreScoreImpactFromNonTriggeredRules() {
    AuthenticationRiskRule nonTriggeredStubRule = event -> new RuleResult(
        "SYNTHETIC_NON_TRIGGERED_RULE",
        "Synthetic non-triggered rule",
        false,
        80,
        "Synthetic non-triggered result for score validation",
        "1.0");

    AuthenticationRiskEvaluator evaluatorWithNonTriggeredRule = new AuthenticationRiskEvaluator(
        List.of(nonTriggeredStubRule));

    AuthenticationRiskEvaluation evaluation = evaluatorWithNonTriggeredRule.evaluate(
        createEvent(false, 0));

    assertEquals(new RiskScore(0), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(1, evaluation.ruleResults().size());
  }

  private AuthenticationEvent createEvent(
      boolean newDevice,
      int failedAttempts) {
    return new AuthenticationEvent(
        "synthetic-auth-event-004",
        OCCURRED_AT,
        "synthetic-user-004",
        AuthenticationChannel.MOBILE,
        newDevice,
        failedAttempts);
  }

  private RuleResult triggeredResult(
      String ruleCode,
      int scoreImpact) {
    return new RuleResult(
        ruleCode,
        "Synthetic test rule",
        true,
        scoreImpact,
        "Synthetic rule triggered for score-capping validation",
        "1.0");
  }
}
