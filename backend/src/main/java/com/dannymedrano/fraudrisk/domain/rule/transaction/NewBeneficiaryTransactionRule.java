package com.dannymedrano.fraudrisk.domain.rule.transaction;

import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public final class NewBeneficiaryTransactionRule
    implements TransactionRiskRule {

  private static final String RULE_CODE = "NEW_BENEFICIARY";
  private static final String RULE_NAME = "Transaction to a new beneficiary";
  private static final String RULE_VERSION = "1.0";
  private static final int TRIGGERED_SCORE_IMPACT = 30;

  @Override
  public RuleResult evaluate(TransactionEvent event) {
    if (event == null) {
      throw new IllegalArgumentException(
          "Transaction event must not be null");
    }

    if (event.newBeneficiary()) {
      return new RuleResult(
          RULE_CODE,
          RULE_NAME,
          true,
          TRIGGERED_SCORE_IMPACT,
          "The synthetic transaction targets a newly added beneficiary",
          RULE_VERSION);
    }

    return new RuleResult(
        RULE_CODE,
        RULE_NAME,
        false,
        0,
        "No new-beneficiary risk signal was detected",
        RULE_VERSION);
  }
}
