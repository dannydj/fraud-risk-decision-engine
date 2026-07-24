package com.dannymedrano.fraudrisk.infrastructure.web.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskCommand;
import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskUseCase;
import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluation;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionType;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

class TransactionRiskControllerTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-24T04:00:00Z");

  @Test
  void shouldRejectNullUseCase() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskController(null));

    assertEquals(
        "Transaction risk use case must not be null",
        exception.getMessage());
  }

  @Test
  void shouldMapAllRequestFieldsAndReturnEvaluationResponse() {
    AtomicReference<EvaluateTransactionRiskCommand> capturedCommand = new AtomicReference<>();

    RuleResult ruleResult = new RuleResult(
        "NEW_BENEFICIARY",
        "Transaction to a new beneficiary",
        true,
        30,
        "The transaction targets a new beneficiary",
        "1.0");

    TransactionRiskEvaluation evaluation = new TransactionRiskEvaluation(
        "synthetic-transaction-controller-001",
        new RiskScore(30),
        Decision.ALLOW,
        List.of(ruleResult));

    EvaluateTransactionRiskUseCase useCase = command -> {
      capturedCommand.set(command);
      return evaluation;
    };

    TransactionRiskController controller = new TransactionRiskController(useCase);

    TransactionRiskRequest request = new TransactionRiskRequest(
        "synthetic-transaction-controller-001",
        OCCURRED_AT,
        "synthetic-actor-controller-001",
        TransactionType.PAYMENT,
        new BigDecimal("275.50"),
        "EUR",
        "synthetic-destination-controller-001",
        true,
        7);

    TransactionRiskResponse response = controller.evaluate(request);

    EvaluateTransactionRiskCommand command = capturedCommand.get();

    assertEquals(request.eventId(), command.eventId());
    assertEquals(request.occurredAt(), command.occurredAt());
    assertEquals(
        request.actorReference(),
        command.actorReference());
    assertEquals(
        request.transactionType(),
        command.transactionType());
    assertEquals(request.amount(), command.amount());
    assertEquals(request.currency(), command.currency());
    assertEquals(
        request.destinationReference(),
        command.destinationReference());
    assertEquals(
        request.newBeneficiary(),
        command.newBeneficiary());
    assertEquals(
        request.recentTransactionCount(),
        command.recentTransactionCount());

    assertEquals(evaluation.eventId(), response.eventId());
    assertEquals(
        evaluation.riskScore().value(),
        response.riskScore());
    assertEquals(evaluation.decision(), response.decision());
    assertEquals(1, response.ruleResults().size());
    assertEquals(
        ruleResult.ruleCode(),
        response.ruleResults().get(0).ruleCode());
    assertEquals(
        ruleResult.ruleName(),
        response.ruleResults().get(0).ruleName());
    assertEquals(
        ruleResult.triggered(),
        response.ruleResults().get(0).triggered());
    assertEquals(
        ruleResult.scoreImpact(),
        response.ruleResults().get(0).scoreImpact());
    assertEquals(
        ruleResult.explanation(),
        response.ruleResults().get(0).explanation());
    assertEquals(
        ruleResult.ruleVersion(),
        response.ruleResults().get(0).ruleVersion());
  }
}
