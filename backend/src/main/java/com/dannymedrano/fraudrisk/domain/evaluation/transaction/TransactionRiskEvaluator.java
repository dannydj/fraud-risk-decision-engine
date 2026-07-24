package com.dannymedrano.fraudrisk.domain.evaluation.transaction;

import java.util.List;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.evaluation.ThresholdDecisionPolicy;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;
import com.dannymedrano.fraudrisk.domain.rule.transaction.TransactionRiskRule;

public final class TransactionRiskEvaluator {

  private final List<TransactionRiskRule> rules;
  private final ThresholdDecisionPolicy decisionPolicy;

  public TransactionRiskEvaluator(
      List<TransactionRiskRule> rules) {
    if (rules == null) {
      throw new IllegalArgumentException(
          "Transaction rules must not be null");
    }

    if (rules.stream().anyMatch(rule -> rule == null)) {
      throw new IllegalArgumentException(
          "Transaction rules must not contain null entries");
    }

    this.rules = List.copyOf(rules);
    this.decisionPolicy = new ThresholdDecisionPolicy();
  }

  public TransactionRiskEvaluation evaluate(
      TransactionEvent event) {
    if (event == null) {
      throw new IllegalArgumentException(
          "Transaction event must not be null");
    }

    List<RuleResult> ruleResults = rules.stream()
        .map(rule -> rule.evaluate(event))
        .toList();

    long totalScore = 0;

    for (RuleResult result : ruleResults) {
      if (result.triggered()) {
        totalScore = Math.min(
            RiskScore.MAX_VALUE,
            totalScore + result.scoreImpact());
      }
    }

    RiskScore riskScore = new RiskScore((int) totalScore);
    Decision decision = decisionPolicy.decide(riskScore);

    return new TransactionRiskEvaluation(
        event.eventId(),
        riskScore,
        decision,
        ruleResults);
  }
}