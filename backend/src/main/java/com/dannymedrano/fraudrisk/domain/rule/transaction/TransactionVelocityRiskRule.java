package com.dannymedrano.fraudrisk.domain.rule.transaction;

import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public final class TransactionVelocityRiskRule
    implements TransactionRiskRule {

  private static final String RULE_CODE = "TRANSACTION_VELOCITY";
  private static final String RULE_NAME = "Elevated recent transaction activity";
  private static final String RULE_VERSION = "1.0";
  private static final int TRIGGER_THRESHOLD = 5;
  private static final int TRIGGERED_SCORE_IMPACT = 35;

  @Override
  public RuleResult evaluate(TransactionEvent event) {
    if (event == null) {
      throw new IllegalArgumentException(
          "Transaction event must not be null");
    }

    if (event.recentTransactionCount() >= TRIGGER_THRESHOLD) {
      return new RuleResult(
          RULE_CODE,
          RULE_NAME,
          true,
          TRIGGERED_SCORE_IMPACT,
          "The synthetic transaction count meets or exceeds the velocity threshold",
          RULE_VERSION);
    }

    return new RuleResult(
        RULE_CODE,
        RULE_NAME,
        false,
        0,
        "No elevated transaction-velocity risk signal was detected",
        RULE_VERSION);
  }
}
