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

class NewBeneficiaryTransactionRuleTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-22T20:00:00Z");

  private final NewBeneficiaryTransactionRule rule = new NewBeneficiaryTransactionRule();

  @Test
  void shouldTriggerWhenTransactionTargetsNewBeneficiary() {
    TransactionEvent event = createEvent(true);

    RuleResult result = rule.evaluate(event);

    assertEquals("NEW_BENEFICIARY", result.ruleCode());
    assertEquals(
        "Transaction to a new beneficiary",
        result.ruleName());
    assertTrue(result.triggered());
    assertEquals(30, result.scoreImpact());
    assertEquals(
        "The synthetic transaction targets a newly added beneficiary",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  private TransactionEvent createEvent(boolean newBeneficiary) {
    return new TransactionEvent(
        "synthetic-transaction-001",
        OCCURRED_AT,
        "synthetic-actor-001",
        TransactionType.TRANSFER,
        new BigDecimal("1250.00"),
        "USD",
        "synthetic-destination-001",
        newBeneficiary,
        4);
  }

  @Test
  void shouldNotTriggerWhenBeneficiaryAlreadyExists() {
    TransactionEvent event = createEvent(false);

    RuleResult result = rule.evaluate(event);

    assertEquals("NEW_BENEFICIARY", result.ruleCode());
    assertEquals(
        "Transaction to a new beneficiary",
        result.ruleName());
    assertFalse(result.triggered());
    assertEquals(0, result.scoreImpact());
    assertEquals(
        "No new-beneficiary risk signal was detected",
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
}
