package com.dannymedrano.fraudrisk.application.port.in.authentication;

import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationChannel;

import java.time.Instant;

public record EvaluateAuthenticationRiskCommand(
    String eventId,
    Instant occurredAt,
    String actorReference,
    AuthenticationChannel channel,
    boolean newDevice,
    int failedAttempts) {
}
