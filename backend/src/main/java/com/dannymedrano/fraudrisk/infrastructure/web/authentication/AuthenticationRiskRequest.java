package com.dannymedrano.fraudrisk.infrastructure.web.authentication;

import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;

public record AuthenticationRiskRequest(
    @NotBlank(message = "Event ID must not be blank") String eventId,

    @NotNull(message = "Occurred at must not be null") Instant occurredAt,

    @NotBlank(message = "Actor reference must not be blank") String actorReference,

    @NotNull(message = "Authentication channel must not be null") AuthenticationChannel channel,

    boolean newDevice,

    @PositiveOrZero(message = "Failed attempts must not be negative") int failedAttempts) {
}
