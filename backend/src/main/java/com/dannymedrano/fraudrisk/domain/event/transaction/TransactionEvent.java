package com.dannymedrano.fraudrisk.domain.event.transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.regex.Pattern;

public record TransactionEvent(
    String eventId,
    Instant occurredAt,
    String actorReference,
    TransactionType transactionType,
    BigDecimal amount,
    String currency,
    String destinationReference,
    boolean newBeneficiary,
    int recentTransactionCount) {

  private static final Pattern CURRENCY_PATTERN = Pattern.compile("[A-Z]{3}");

  public TransactionEvent {
    validateRequiredText(eventId, "Event ID");
    validateRequiredText(actorReference, "Actor reference");
    validateRequiredText(currency, "Currency");
    validateRequiredText(
        destinationReference,
        "Destination reference");

    if (occurredAt == null) {
      throw new IllegalArgumentException(
          "Occurred at must not be null");
    }

    if (transactionType == null) {
      throw new IllegalArgumentException(
          "Transaction type must not be null");
    }

    if (amount == null) {
      throw new IllegalArgumentException(
          "Amount must not be null");
    }

    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException(
          "Amount must be greater than zero");
    }

    if (!CURRENCY_PATTERN.matcher(currency).matches()) {
      throw new IllegalArgumentException(
          "Currency must be a three-letter uppercase code");
    }

    if (recentTransactionCount < 0) {
      throw new IllegalArgumentException(
          "Recent transaction count must not be negative");
    }
  }

  private static void validateRequiredText(
      String value,
      String fieldName) {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException(
          fieldName + " must not be blank");
    }
  }
}
