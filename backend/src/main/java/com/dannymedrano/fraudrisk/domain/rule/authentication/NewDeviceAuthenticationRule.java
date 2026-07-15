package com.dannymedrano.fraudrisk.domain.rule.authentication;

import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public final class NewDeviceAuthenticationRule implements AuthenticationRiskRule {

  private static final String RULE_CODE = "AUTH_NEW_DEVICE";
  private static final String RULE_NAME = "Authentication from a new device";
  private static final String RULE_VERSION = "1.0";
  private static final int TRIGGERED_SCORE_IMPACT = 25;

  @Override
  public RuleResult evaluate(AuthenticationEvent event) {
    if (event == null) {
      throw new IllegalArgumentException(
          "Authentication event must not be null");
    }

    if (event.newDevice()) {
      return new RuleResult(
          RULE_CODE,
          RULE_NAME,
          true,
          TRIGGERED_SCORE_IMPACT,
          "The synthetic event indicates authentication from a new device",
          RULE_VERSION);
    }

    return new RuleResult(
        RULE_CODE,
        RULE_NAME,
        false,
        0,
        "No new-device risk signal was detected",
        RULE_VERSION);
  }
}
