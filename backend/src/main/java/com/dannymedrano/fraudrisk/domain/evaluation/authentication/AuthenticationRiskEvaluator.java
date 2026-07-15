package com.dannymedrano.fraudrisk.domain.evaluation.authentication;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.evaluation.ThresholdDecisionPolicy;
import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;
import com.dannymedrano.fraudrisk.domain.rule.authentication.AuthenticationRiskRule;

import java.util.List;

public final class AuthenticationRiskEvaluator {

  private final List<AuthenticationRiskRule> rules;
  private final ThresholdDecisionPolicy decisionPolicy;

  public AuthenticationRiskEvaluator(
      List<AuthenticationRiskRule> rules) {
    if (rules == null) {
      throw new IllegalArgumentException(
          "Authentication rules must not be null");
    }

    if (rules.stream().anyMatch(rule -> rule == null)) {
      throw new IllegalArgumentException(
          "Authentication rules must not contain null entries");
    }

    this.rules = List.copyOf(rules);
    this.decisionPolicy = new ThresholdDecisionPolicy();
  }

  public AuthenticationRiskEvaluation evaluate(
      AuthenticationEvent event) {
    if (event == null) {
      throw new IllegalArgumentException(
          "Authentication event must not be null");
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

    return new AuthenticationRiskEvaluation(
        event.eventId(),
        riskScore,
        decision,
        ruleResults);
  }
}
