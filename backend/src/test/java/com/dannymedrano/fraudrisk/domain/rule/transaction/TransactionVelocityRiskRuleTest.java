package com.dannymedrano.fraudrisk.domain.rule.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionEvent;
import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionType;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

class TransactionVelocityRiskRuleTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-23T20:00:00Z");

  private final TransactionVelocityRiskRule rule = new TransactionVelocityRiskRule();

  @Test
  void shouldTriggerAtTransactionCountThreshold() {
    TransactionEvent event = createEvent(5);

    RuleResult result = rule.evaluate(event);

    assertEquals("TRANSACTION_VELOCITY", result.ruleCode());
    assertEquals(
        "Elevated recent transaction activity",
        result.ruleName());
    assertTrue(result.triggered());
    assertEquals(35, result.scoreImpact());
    assertEquals(
        "The synthetic transaction count meets or exceeds the velocity threshold",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldTriggerAboveTransactionCountThreshold() {
    TransactionEvent event = createEvent(8);

    RuleResult result = rule.evaluate(event);

    assertEquals("TRANSACTION_VELOCITY", result.ruleCode());
    assertEquals(
        "Elevated recent transaction activity",
        result.ruleName());
    assertTrue(result.triggered());
    assertEquals(35, result.scoreImpact());
    assertEquals(
        "The synthetic transaction count meets or exceeds the velocity threshold",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldNotTriggerBelowTransactionCountThreshold() {
    TransactionEvent event = createEvent(4);

    RuleResult result = rule.evaluate(event);

    assertEquals("TRANSACTION_VELOCITY", result.ruleCode());
    assertEquals(
        "Elevated recent transaction activity",
        result.ruleName());
    assertFalse(result.triggered());
    assertEquals(0, result.scoreImpact());
    assertEquals(
        "No elevated transaction-velocity risk signal was detected",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldNotTriggerWhenTransactionCountIsZero() {
    TransactionEvent event = createEvent(0);

    RuleResult result = rule.evaluate(event);

    assertEquals("TRANSACTION_VELOCITY", result.ruleCode());
    assertEquals(
        "Elevated recent transaction activity",
        result.ruleName());
    assertFalse(result.triggered());
    assertEquals(0, result.scoreImpact());
    assertEquals(
        "No elevated transaction-velocity risk signal was detected",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldRejectNullTransactionEvent() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> rule.evaluate(null));

    assertEquals(
        "Transaction event must not be null",
        exception.getMessage());
  }

  private TransactionEvent createEvent(int recentTransactionCount) {
    return new TransactionEvent(
        "synthetic-transaction-001",
        OCCURRED_AT,
        "synthetic-actor-001",
        TransactionType.TRANSFER,
        new BigDecimal("1250.00"),
        "USD",
        "synthetic-destination-001",
        false,
        recentTransactionCount);
  }
}
