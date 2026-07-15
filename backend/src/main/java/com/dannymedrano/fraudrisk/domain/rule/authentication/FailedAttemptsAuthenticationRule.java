package com.dannymedrano.fraudrisk.domain.rule.authentication;

import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public final class FailedAttemptsAuthenticationRule implements AuthenticationRiskRule {

  private static final String RULE_CODE = "AUTH_REPEATED_FAILURES";
  private static final String RULE_NAME = "Repeated failed authentication attempts";
  private static final String RULE_VERSION = "1.0";
  private static final int TRIGGER_THRESHOLD = 3;
  private static final int TRIGGERED_SCORE_IMPACT = 35;

  @Override
  public RuleResult evaluate(AuthenticationEvent event) {
    if (event == null) {
      throw new IllegalArgumentException(
          "Authentication event must not be null");
    }

    if (event.failedAttempts() >= TRIGGER_THRESHOLD) {
      return new RuleResult(
          RULE_CODE,
          RULE_NAME,
          true,
          TRIGGERED_SCORE_IMPACT,
          "The synthetic event indicates repeated failed authentication attempts",
          RULE_VERSION);
    }

    return new RuleResult(
        RULE_CODE,
        RULE_NAME,
        false,
        0,
        "No repeated-failure risk signal was detected",
        RULE_VERSION);
  }
}
