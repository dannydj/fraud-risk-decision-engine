package com.dannymedrano.fraudrisk.infrastructure.web.transaction;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskCommand;
import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskUseCase;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluation;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/evaluations/transactions")
public class TransactionRiskController {

  private final EvaluateTransactionRiskUseCase useCase;

  public TransactionRiskController(
      EvaluateTransactionRiskUseCase useCase) {
    if (useCase == null) {
      throw new IllegalArgumentException(
          "Transaction risk use case must not be null");
    }

    this.useCase = useCase;
  }

  @PostMapping
  public TransactionRiskResponse evaluate(
      @Valid @RequestBody TransactionRiskRequest request) {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        request.eventId(),
        request.occurredAt(),
        request.actorReference(),
        request.transactionType(),
        request.amount(),
        request.currency(),
        request.destinationReference(),
        request.newBeneficiary(),
        request.recentTransactionCount());

    TransactionRiskEvaluation evaluation = useCase.evaluate(command);

    return TransactionRiskResponse.from(evaluation);
  }
}
