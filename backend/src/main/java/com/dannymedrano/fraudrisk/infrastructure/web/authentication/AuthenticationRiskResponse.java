package com.dannymedrano.fraudrisk.infrastructure.web.authentication;

import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluation;

import java.util.List;

public record AuthenticationRiskResponse(
    String eventId,
    int riskScore,
    Decision decision,
    List<RuleResultResponse> ruleResults) {

  public AuthenticationRiskResponse {
    ruleResults = List.copyOf(ruleResults);
  }

  public static AuthenticationRiskResponse from(
      AuthenticationRiskEvaluation evaluation) {
    if (evaluation == null) {
      throw new IllegalArgumentException(
          "Authentication risk evaluation must not be null");
    }

    List<RuleResultResponse> ruleResponses = evaluation.ruleResults()
        .stream()
        .map(RuleResultResponse::from)
        .toList();

    return new AuthenticationRiskResponse(
        evaluation.eventId(),
        evaluation.riskScore().value(),
        evaluation.decision(),
        ruleResponses);
  }
}
