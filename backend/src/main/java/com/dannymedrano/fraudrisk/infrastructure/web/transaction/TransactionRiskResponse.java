package com.dannymedrano.fraudrisk.infrastructure.web.transaction;

import java.util.List;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluation;
import com.dannymedrano.fraudrisk.infrastructure.web.common.RuleResultResponse;

public record TransactionRiskResponse(
    String eventId,
    int riskScore,
    Decision decision,
    List<RuleResultResponse> ruleResults) {

  public TransactionRiskResponse {
    ruleResults = List.copyOf(ruleResults);
  }

  public static TransactionRiskResponse from(
      TransactionRiskEvaluation evaluation) {
    if (evaluation == null) {
      throw new IllegalArgumentException(
          "Transaction risk evaluation must not be null");
    }

    List<RuleResultResponse> ruleResponses = evaluation.ruleResults()
        .stream()
        .map(RuleResultResponse::from)
        .toList();

    return new TransactionRiskResponse(
        evaluation.eventId(),
        evaluation.riskScore().value(),
        evaluation.decision(),
        ruleResponses);
  }
}
