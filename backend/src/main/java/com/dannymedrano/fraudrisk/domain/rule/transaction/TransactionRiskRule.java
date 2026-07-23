package com.dannymedrano.fraudrisk.domain.rule.transaction;

import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public interface TransactionRiskRule {

  RuleResult evaluate(TransactionEvent event);
}
