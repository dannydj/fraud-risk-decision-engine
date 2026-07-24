package com.dannymedrano.fraudrisk.application.port.in.transaction;

import java.math.BigDecimal;
import java.time.Instant;

import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionType;

public record EvaluateTransactionRiskCommand(
    String eventId,
    Instant occurredAt,
    String actorReference,
    TransactionType transactionType,
    BigDecimal amount,
    String currency,
    String destinationReference,
    boolean newBeneficiary,
    int recentTransactionCount) {
}
