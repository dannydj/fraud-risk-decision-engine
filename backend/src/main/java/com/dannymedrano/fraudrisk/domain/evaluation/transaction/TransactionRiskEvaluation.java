package com.dannymedrano.fraudrisk.domain.evaluation.transaction;

import java.util.List;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public record TransactionRiskEvaluation(
    String eventId,
    RiskScore riskScore,
    Decision decision,
    List<RuleResult> ruleResults) {

  public TransactionRiskEvaluation {
    if (eventId == null || eventId.isBlank()) {
      throw new IllegalArgumentException(
          "Event ID must not be blank");
    }

    if (riskScore == null) {
      throw new IllegalArgumentException(
          "Risk score must not be null");
    }

    if (decision == null) {
      throw new IllegalArgumentException(
          "Decision must not be null");
    }

    if (ruleResults == null) {
      throw new IllegalArgumentException(
          "Rule results must not be null");
    }

    if (ruleResults.stream().anyMatch(result -> result == null)) {
      throw new IllegalArgumentException(
          "Rule results must not contain null entries");
    }

    ruleResults = List.copyOf(ruleResults);
  }
}
