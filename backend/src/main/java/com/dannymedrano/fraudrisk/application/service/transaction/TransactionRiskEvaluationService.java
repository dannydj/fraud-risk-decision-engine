package com.dannymedrano.fraudrisk.application.service.transaction;

import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskCommand;
import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskUseCase;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluation;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluator;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;

public final class TransactionRiskEvaluationService
    implements EvaluateTransactionRiskUseCase {

  private final TransactionRiskEvaluator evaluator;

  public TransactionRiskEvaluationService(
      TransactionRiskEvaluator evaluator) {
    if (evaluator == null) {
      throw new IllegalArgumentException(
          "Transaction risk evaluator must not be null");
    }

    this.evaluator = evaluator;
  }

  @Override
  public TransactionRiskEvaluation evaluate(
      EvaluateTransactionRiskCommand command) {
    if (command == null) {
      throw new IllegalArgumentException(
          "Transaction risk command must not be null");
    }

    TransactionEvent event = new TransactionEvent(
        command.eventId(),
        command.occurredAt(),
        command.actorReference(),
        command.transactionType(),
        command.amount(),
        command.currency(),
        command.destinationReference(),
        command.newBeneficiary(),
        command.recentTransactionCount());

    return evaluator.evaluate(event);
  }
}
