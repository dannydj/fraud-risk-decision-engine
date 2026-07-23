package com.dannymedrano.fraudrisk.domain.event.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;

import org.junit.jupiter.api.Test;

class TransactionEventTest {

  @Test
  void shouldCreateValidTransactionEvent() {
    Instant occurredAt = Instant.parse("2026-07-22T20:00:00Z");

    TransactionEvent event = new TransactionEvent(
        "synthetic-transaction-001",
        occurredAt,
        "synthetic-actor-001",
        TransactionType.TRANSFER,
        new BigDecimal("1250.00"),
        "USD",
        "synthetic-destination-001",
        true,
        4);

    assertEquals(
        "synthetic-transaction-001",
        event.eventId());
    assertEquals(occurredAt, event.occurredAt());
    assertEquals(
        "synthetic-actor-001",
        event.actorReference());
    assertEquals(
        TransactionType.TRANSFER,
        event.transactionType());
    assertEquals(
        new BigDecimal("1250.00"),
        event.amount());
    assertEquals("USD", event.currency());
    assertEquals(
        "synthetic-destination-001",
        event.destinationReference());
    assertTrue(event.newBeneficiary());
    assertEquals(4, event.recentTransactionCount());
  }

  @Test
  void shouldRejectBlankEventId() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "   ",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "USD",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Event ID must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullOccurredAt() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            null,
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "USD",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Occurred at must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullTransactionType() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            null,
            new BigDecimal("1250.00"),
            "USD",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Transaction type must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullAmount() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            null,
            "USD",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Amount must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectZeroAmount() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            BigDecimal.ZERO,
            "USD",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Amount must be greater than zero",
        exception.getMessage());
  }

  @Test
  void shouldRejectNegativeAmount() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("-10.00"),
            "USD",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Amount must be greater than zero",
        exception.getMessage());
  }

  @Test
  void shouldRejectBlankActorReference() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "   ",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "USD",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Actor reference must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldRejectBlankCurrency() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "   ",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Currency must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldRejectLowercaseCurrency() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "usd",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Currency must be a three-letter uppercase code",
        exception.getMessage());
  }

  @Test
  void shouldRejectCurrencyWithInvalidLength() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "US",
            "synthetic-destination-001",
            true,
            4));

    assertEquals(
        "Currency must be a three-letter uppercase code",
        exception.getMessage());
  }

  @Test
  void shouldRejectBlankDestinationReference() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "USD",
            "   ",
            true,
            4));

    assertEquals(
        "Destination reference must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldRejectNegativeRecentTransactionCount() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new TransactionEvent(
            "synthetic-transaction-001",
            Instant.parse("2026-07-22T20:00:00Z"),
            "synthetic-actor-001",
            TransactionType.TRANSFER,
            new BigDecimal("1250.00"),
            "USD",
            "synthetic-destination-001",
            true,
            -1));

    assertEquals(
        "Recent transaction count must not be negative",
        exception.getMessage());
  }
}
