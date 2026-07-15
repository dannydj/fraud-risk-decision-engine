package com.dannymedrano.fraudrisk.domain.event.authentication;

import java.time.Instant;

public record AuthenticationEvent(
    String eventId,
    Instant occuredAt,
    String actorReference,
    AuthenticationChannel channel,
    boolean newDevice,
    int failedAttempts) {

  public AuthenticationEvent {
    if (eventId == null || eventId.isBlank()) {
      throw new IllegalArgumentException("Event ID must not be blank");
    }

    if (occuredAt == null) {
      throw new IllegalArgumentException("Occurred at must not be null");
    }

    if (actorReference == null || actorReference.isBlank()) {
      throw new IllegalArgumentException("Actor reference must not be blank");
    }

    if (channel == null) {
      throw new IllegalArgumentException("Authentication channel must not be null");
    }

    if (failedAttempts < 0) {
      throw new IllegalArgumentException("Failed attempts must not be negative");
    }
  }
}
