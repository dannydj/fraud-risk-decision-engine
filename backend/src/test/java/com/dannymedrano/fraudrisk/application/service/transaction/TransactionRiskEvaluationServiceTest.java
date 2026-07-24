package com.dannymedrano.fraudrisk.application.service.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskCommand;
import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluation;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluator;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionType;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;
import com.dannymedrano.fraudrisk.domain.rule.transaction.NewBeneficiaryTransactionRule;
import com.dannymedrano.fraudrisk.domain.rule.transaction.TransactionRiskRule;
import com.dannymedrano.fraudrisk.domain.rule.transaction.TransactionVelocityRiskRule;

class TransactionRiskEvaluationServiceTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-24T03:00:00Z");

  private final TransactionRiskEvaluator evaluator = new TransactionRiskEvaluator(
      List.of(
          new NewBeneficiaryTransactionRule(),
          new TransactionVelocityRiskRule()));

  private final TransactionRiskEvaluationService service = new TransactionRiskEvaluationService(evaluator);

  @Test
  void shouldEvaluateTransactionRiskCommand() {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "synthetic-transaction-002",
        OCCURRED_AT,
        "synthetic-actor-002",
        TransactionType.TRANSFER,
        new BigDecimal("1500.00"),
        "USD",
        "synthetic-destination-002",
        true,
        5);

    TransactionRiskEvaluation evaluation = service.evaluate(command);

    assertEquals(
        "synthetic-transaction-002",
        evaluation.eventId());
    assertEquals(new RiskScore(65), evaluation.riskScore());
    assertEquals(Decision.REVIEW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(
        "NEW_BENEFICIARY",
        evaluation.ruleResults().get(0).ruleCode());
    assertEquals(
        "TRANSACTION_VELOCITY",
        evaluation.ruleResults().get(1).ruleCode());
  }

  @Test
  void shouldRejectNullEvaluator() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionRiskEvaluationService(null));

    assertEquals(
        "Transaction risk evaluator must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullCommand() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.evaluate(null));

    assertEquals(
        "Transaction risk command must not be null",
        exception.getMessage());
  }

  @Test
  void shouldPropagateDomainValidationForBlankEventId() {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "   ",
        OCCURRED_AT,
        "synthetic-actor-002",
        TransactionType.TRANSFER,
        new BigDecimal("1500.00"),
        "USD",
        "synthetic-destination-002",
        false,
        0);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.evaluate(command));

    assertEquals(
        "Event ID must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldPropagateDomainValidationForNegativeRecentTransactionCount() {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "synthetic-transaction-002",
        OCCURRED_AT,
        "synthetic-actor-002",
        TransactionType.TRANSFER,
        new BigDecimal("1500.00"),
        "USD",
        "synthetic-destination-002",
        false,
        -1);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.evaluate(command));

    assertEquals(
        "Recent transaction count must not be negative",
        exception.getMessage());
  }

  @Test
  void shouldMapAllCommandFieldsToTransactionEvent() {
    AtomicReference<TransactionEvent> capturedEvent = new AtomicReference<>();

    TransactionRiskRule capturingRule = event -> {
      capturedEvent.set(event);

      return new RuleResult(
          "CAPTURE_TRANSACTION",
          "Capture transaction event",
          false,
          0,
          "Captures the event for testing",
          "1.0");
    };

    TransactionRiskEvaluationService mappingService = new TransactionRiskEvaluationService(
        new TransactionRiskEvaluator(
            List.of(capturingRule)));

    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "synthetic-transaction-mapping-001",
        OCCURRED_AT,
        "synthetic-actor-mapping-001",
        TransactionType.PAYMENT,
        new BigDecimal("275.50"),
        "EUR",
        "synthetic-destination-mapping-001",
        true,
        7);

    mappingService.evaluate(command);

    TransactionEvent event = capturedEvent.get();

    assertNotNull(event);
    assertEquals(command.eventId(), event.eventId());
    assertEquals(command.occurredAt(), event.occurredAt());
    assertEquals(command.actorReference(), event.actorReference());
    assertEquals(
        command.transactionType(),
        event.transactionType());
    assertEquals(command.amount(), event.amount());
    assertEquals(command.currency(), event.currency());
    assertEquals(
        command.destinationReference(),
        event.destinationReference());
    assertEquals(
        command.newBeneficiary(),
        event.newBeneficiary());
    assertEquals(
        command.recentTransactionCount(),
        event.recentTransactionCount());
  }

  @Test
  void shouldAllowTransactionWithoutRiskSignals() {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "synthetic-transaction-003",
        OCCURRED_AT,
        "synthetic-actor-003",
        TransactionType.PAYMENT,
        new BigDecimal("80.00"),
        "USD",
        "synthetic-destination-003",
        false,
        1);

    TransactionRiskEvaluation evaluation = service.evaluate(command);

    assertEquals(new RiskScore(0), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(false, evaluation.ruleResults().get(0).triggered());
    assertEquals(false, evaluation.ruleResults().get(1).triggered());
  }

  @Test
  void shouldAllowTransactionWithOnlyNewBeneficiaryRisk() {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "synthetic-transaction-004",
        OCCURRED_AT,
        "synthetic-actor-004",
        TransactionType.TRANSFER,
        new BigDecimal("500.00"),
        "USD",
        "synthetic-destination-004",
        true,
        1);

    TransactionRiskEvaluation evaluation = service.evaluate(command);

    assertEquals(new RiskScore(30), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(true, evaluation.ruleResults().get(0).triggered());
    assertEquals(false, evaluation.ruleResults().get(1).triggered());
  }

  @Test
  void shouldAllowTransactionWithOnlyVelocityRisk() {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "synthetic-transaction-005",
        OCCURRED_AT,
        "synthetic-actor-005",
        TransactionType.PAYMENT,
        new BigDecimal("120.00"),
        "USD",
        "synthetic-destination-005",
        false,
        5);

    TransactionRiskEvaluation evaluation = service.evaluate(command);

    assertEquals(new RiskScore(35), evaluation.riskScore());
    assertEquals(Decision.ALLOW, evaluation.decision());
    assertEquals(false, evaluation.ruleResults().get(0).triggered());
    assertEquals(true, evaluation.ruleResults().get(1).triggered());
  }

  @Test
  void shouldReturnEvaluationProducedByEvaluator() {
    EvaluateTransactionRiskCommand command = new EvaluateTransactionRiskCommand(
        "synthetic-transaction-006",
        OCCURRED_AT,
        "synthetic-actor-006",
        TransactionType.TRANSFER,
        new BigDecimal("900.00"),
        "USD",
        "synthetic-destination-006",
        true,
        5);

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

    TransactionRiskEvaluation expectedEvaluation = evaluator.evaluate(event);

    TransactionRiskEvaluation actualEvaluation = service.evaluate(command);

    assertEquals(expectedEvaluation, actualEvaluation);
  }
}
