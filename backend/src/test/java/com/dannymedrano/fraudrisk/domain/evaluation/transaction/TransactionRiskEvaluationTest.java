package com.dannymedrano.fraudrisk.domain.evaluation.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

class TransactionRiskEvaluationTest {

  @Test
  void shouldCreateTransactionRiskEvaluation() {
    RuleResult ruleResult = new RuleResult(
        "SYNTHETIC_TRANSACTION_RULE",
        "Synthetic transaction rule",
        true,
        30,
        "Synthetic transaction signal detected",
        "1.0");

    TransactionRiskEvaluation evaluation = new TransactionRiskEvaluation(
        "synthetic-transaction-001",
        new RiskScore(30),
        Decision.ALLOW,
        List.of(ruleResult));

    assertEquals(
        "synthetic-transaction-001",
        evaluation.eventId());
    assertEquals(new RiskScore(30), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(List.of(ruleResult), evaluation.ruleResults());
  }

  @Test
  void shouldRejectBlankEventId() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluation(
            " ",
            new RiskScore(30),
            Decision.ALLOW,
            List.of()));

    assertEquals(
        "Event ID must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRiskScore() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluation(
            "synthetic-transaction-001",
            null,
            Decision.ALLOW,
            List.of()));

    assertEquals(
        "Risk score must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullDecision() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluation(
            "synthetic-transaction-001",
            new RiskScore(30),
            null,
            List.of()));

    assertEquals(
        "Decision must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRuleResults() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluation(
            "synthetic-transaction-001",
            new RiskScore(30),
            Decision.ALLOW,
            null));

    assertEquals(
        "Rule results must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRuleResultEntry() {
    List<RuleResult> ruleResults = new ArrayList<>();
    ruleResults.add(null);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluation(
            "synthetic-transaction-001",
            new RiskScore(30),
            Decision.ALLOW,
            ruleResults));

    assertEquals(
        "Rule results must not contain null entries",
        exception.getMessage());
  }

  @Test
  void shouldDefensivelyCopyRuleResults() {
    RuleResult ruleResult = new RuleResult(
        "SYNTHETIC_TRANSACTION_RULE",
        "Synthetic transaction rule",
        true,
        30,
        "Synthetic transaction signal detected",
        "1.0");

    List<RuleResult> originalRuleResults = new ArrayList<>();
    originalRuleResults.add(ruleResult);

    TransactionRiskEvaluation evaluation = new TransactionRiskEvaluation(
        "synthetic-transaction-001",
        new RiskScore(30),
        Decision.ALLOW,
        originalRuleResults);

    originalRuleResults.clear();

    assertEquals(
        List.of(ruleResult),
        evaluation.ruleResults());
  }

  @Test
  void shouldExposeImmutableRuleResults() {
    RuleResult ruleResult = new RuleResult(
        "SYNTHETIC_TRANSACTION_RULE",
        "Synthetic transaction rule",
        true,
        30,
        "Synthetic transaction signal detected",
        "1.0");

    TransactionRiskEvaluation evaluation = new TransactionRiskEvaluation(
        "synthetic-transaction-001",
        new RiskScore(30),
        Decision.ALLOW,
        List.of(ruleResult));

    assertThrows(
        UnsupportedOperationException.class,
        () -> evaluation.ruleResults().add(ruleResult));
  }
}
