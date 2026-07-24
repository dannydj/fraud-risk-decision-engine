package com.dannymedrano.fraudrisk.application.port.in.transaction;

import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluation;

public interface EvaluateTransactionRiskUseCase {

  TransactionRiskEvaluation evaluate(
      EvaluateTransactionRiskCommand command);
}
