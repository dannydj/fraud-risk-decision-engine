package com.dannymedrano.fraudrisk.infrastructure.web.transaction;

import java.math.BigDecimal;
import java.time.Instant;

import com.dannymedrano.fraudrisk.domain.event.transaction.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record TransactionRiskRequest(
    @NotBlank(message = "Event ID must not be blank") String eventId,

    @NotNull(message = "Occurred at must not be null") Instant occurredAt,

    @NotBlank(message = "Actor reference must not be blank") String actorReference,

    @NotNull(message = "Transaction type must not be null") TransactionType transactionType,

    @NotNull(message = "Amount must not be null") @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero") BigDecimal amount,

    @NotBlank(message = "Currency must not be blank") @Pattern(regexp = "[A-Z]{3}", message = "Currency must be a three-letter uppercase code") String currency,

    @NotBlank(message = "Destination reference must not be blank") String destinationReference,

    boolean newBeneficiary,

    @PositiveOrZero(message = "Recent transaction count must not be negative") int recentTransactionCount) {
}
