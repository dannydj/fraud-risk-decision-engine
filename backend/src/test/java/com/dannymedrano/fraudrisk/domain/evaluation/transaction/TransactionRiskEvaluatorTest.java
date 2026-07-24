package com.dannymedrano.fraudrisk.domain.evaluation.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionType;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;
import com.dannymedrano.fraudrisk.domain.rule.transaction.NewBeneficiaryTransactionRule;
import com.dannymedrano.fraudrisk.domain.rule.transaction.TransactionRiskRule;
import com.dannymedrano.fraudrisk.domain.rule.transaction.TransactionVelocityRiskRule;

class TransactionRiskEvaluatorTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-24T02:00:00Z");

  private final TransactionRiskEvaluator evaluator = new TransactionRiskEvaluator(
      List.of(
          new NewBeneficiaryTransactionRule(),
          new TransactionVelocityRiskRule()));

  @Test
  void shouldAllowWhenNoRulesAreConfigured() {
    TransactionRiskEvaluator evaluator = new TransactionRiskEvaluator(List.of());

    TransactionRiskEvaluation evaluation = evaluator.evaluate(createEvent(false, 0));

    assertEquals(
        "synthetic-transaction-001",
        evaluation.eventId());
    assertEquals(new RiskScore(0), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(List.of(), evaluation.ruleResults());
  }

  private TransactionEvent createEvent(
      boolean newBeneficiary,
      int recentTransactionCount) {
    return new TransactionEvent(
        "synthetic-transaction-001",
        OCCURRED_AT,
        "synthetic-actor-001",
        TransactionType.TRANSFER,
        new BigDecimal("1250.00"),
        "USD",
        "synthetic-destination-001",
        newBeneficiary,
        recentTransactionCount);
  }

  @Test
  void shouldAllowWhenNoTransactionRuleIsTriggered() {
    TransactionRiskEvaluation evaluation = evaluator.evaluate(createEvent(false, 0));

    assertEquals(new RiskScore(0), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(
        "NEW_BENEFICIARY",
        evaluation.ruleResults().get(0).ruleCode());
    assertEquals(
        "TRANSACTION_VELOCITY",
        evaluation.ruleResults().get(1).ruleCode());
  }

  @Test
  void shouldAllowWhenOnlyNewBeneficiaryRuleIsTriggered() {
    TransactionRiskEvaluation evaluation = evaluator.evaluate(createEvent(true, 0));

    assertEquals(new RiskScore(30), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(true, evaluation.ruleResults().get(0).triggered());
    assertEquals(false, evaluation.ruleResults().get(1).triggered());
  }

  @Test
  void shouldAllowWhenOnlyTransactionVelocityRuleIsTriggered() {
    TransactionRiskEvaluation evaluation = evaluator.evaluate(createEvent(false, 5));

    assertEquals(new RiskScore(35), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(false, evaluation.ruleResults().get(0).triggered());
    assertEquals(true, evaluation.ruleResults().get(1).triggered());
  }

  @Test
  void shouldReviewWhenBothTransactionRulesAreTriggered() {
    TransactionRiskEvaluation evaluation = evaluator.evaluate(createEvent(true, 5));

    assertEquals(
        "synthetic-transaction-001",
        evaluation.eventId());
    assertEquals(new RiskScore(65), evaluation.riskScore());
    assertEquals(Decision.REVIEW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(true, evaluation.ruleResults().get(0).triggered());
    assertEquals(true, evaluation.ruleResults().get(1).triggered());
  }

  @Test
  void shouldPreserveConfiguredRuleOrder() {
    TransactionRiskEvaluator reversedEvaluator = new TransactionRiskEvaluator(
        List.of(
            new TransactionVelocityRiskRule(),
            new NewBeneficiaryTransactionRule()));

    TransactionRiskEvaluation evaluation = reversedEvaluator.evaluate(createEvent(true, 5));

    assertEquals(
        "TRANSACTION_VELOCITY",
        evaluation.ruleResults().get(0).ruleCode());
    assertEquals(
        "NEW_BENEFICIARY",
        evaluation.ruleResults().get(1).ruleCode());
  }

  @Test
  void shouldCapCombinedScoreAtMaximum() {
    TransactionRiskRule firstStubRule = event -> triggeredResult(
        "SYNTHETIC_TRANSACTION_RULE_ONE",
        70);

    TransactionRiskRule secondStubRule = event -> triggeredResult(
        "SYNTHETIC_TRANSACTION_RULE_TWO",
        60);

    TransactionRiskEvaluator cappedEvaluator = new TransactionRiskEvaluator(
        List.of(firstStubRule, secondStubRule));

    TransactionRiskEvaluation evaluation = cappedEvaluator.evaluate(createEvent(false, 0));

    assertEquals(new RiskScore(100), evaluation.riskScore());
    assertEquals(Decision.DENY, evaluation.decision());
  }

  @Test
  void shouldIgnoreScoreImpactFromNonTriggeredRules() {
    TransactionRiskRule nonTriggeredStubRule = event -> new RuleResult(
        "SYNTHETIC_NON_TRIGGERED_RULE",
        "Synthetic non-triggered transaction rule",
        false,
        80,
        "Synthetic rule result for score validation",
        "1.0");

    TransactionRiskEvaluator evaluatorWithNonTriggeredRule = new TransactionRiskEvaluator(
        List.of(nonTriggeredStubRule));

    TransactionRiskEvaluation evaluation = evaluatorWithNonTriggeredRule.evaluate(
        createEvent(false, 0));

    assertEquals(new RiskScore(0), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(1, evaluation.ruleResults().size());
  }

  @Test
  void shouldDefensivelyCopyConfiguredRules() {
    List<TransactionRiskRule> originalRules = new ArrayList<>();

    originalRules.add(new NewBeneficiaryTransactionRule());

    TransactionRiskEvaluator copiedRulesEvaluator = new TransactionRiskEvaluator(originalRules);

    originalRules.add(new TransactionVelocityRiskRule());

    TransactionRiskEvaluation evaluation = copiedRulesEvaluator.evaluate(createEvent(true, 5));

    assertEquals(1, evaluation.ruleResults().size());
    assertEquals(
        "NEW_BENEFICIARY",
        evaluation.ruleResults().get(0).ruleCode());
  }

  @Test
  void shouldRejectNullRulesCollection() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluator(null));

    assertEquals(
        "Transaction rules must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullRuleEntry() {
    List<TransactionRiskRule> rulesWithNull = new ArrayList<>();

    rulesWithNull.add(new NewBeneficiaryTransactionRule());
    rulesWithNull.add(null);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluator(rulesWithNull));

    assertEquals(
        "Transaction rules must not contain null entries",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullTransactionEvent() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> evaluator.evaluate(null));

    assertEquals(
        "Transaction event must not be null",
        exception.getMessage());
  }

  private RuleResult triggeredResult(
      String ruleCode,
      int scoreImpact) {
    return new RuleResult(
        ruleCode,
        "Synthetic transaction test rule",
        true,
        scoreImpact,
        "Synthetic transaction rule triggered for score-capping validation",
        "1.0");
  }
}