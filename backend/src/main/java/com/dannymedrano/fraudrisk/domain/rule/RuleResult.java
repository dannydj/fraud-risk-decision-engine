package com.dannymedrano.fraudrisk.domain.rule;

public record RuleResult(
    String ruleCode,
    String ruleName,
    boolean triggered,
    int scoreImpact,
    String explanation,
    String ruleVersion) {

  public RuleResult {
    if (ruleCode == null || ruleCode.isBlank()) {
      throw new IllegalArgumentException("Rule code must not be blank");
    }

    if (ruleName == null || ruleName.isBlank()) {
      throw new IllegalArgumentException("Rule name must not be blank");
    }

    if (scoreImpact < 0) {
      throw new IllegalArgumentException("Score impact must not be negative");
    }

    if (explanation == null || explanation.isBlank()) {
      throw new IllegalArgumentException("Explanation must not be blank");
    }

    if (ruleVersion == null || ruleVersion.isBlank()) {
      throw new IllegalArgumentException("Rule version must not be blank");
    }
  }
}
