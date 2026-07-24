package com.dannymedrano.fraudrisk.infrastructure.web.common;

import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public record RuleResultResponse(
    String ruleCode,
    String ruleName,
    boolean triggered,
    int scoreImpact,
    String explanation,
    String ruleVersion) {

  public static RuleResultResponse from(RuleResult result) {
    if (result == null) {
      throw new IllegalArgumentException(
          "Rule result must not be null");
    }

    return new RuleResultResponse(
        result.ruleCode(),
        result.ruleName(),
        result.triggered(),
        result.scoreImpact(),
        result.explanation(),
        result.ruleVersion());
  }
}
