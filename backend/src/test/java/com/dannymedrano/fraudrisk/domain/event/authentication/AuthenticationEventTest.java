package com.dannymedrano.fraudrisk.domain.event.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class AuthenticationEventTest {

	private static final Instant OCCURRED_AT = Instant.parse("2026-07-12T15:00:00Z");

	@Test
	void shouldCreateValidAuthenticationEvent() {
		AuthenticationEvent event = new AuthenticationEvent("synthetic-auth-event-001", OCCURRED_AT,
				"synthetic-user-001", AuthenticationChannel.WEB, false, 2);

		assertEquals("synthetic-auth-event-001", event.eventId());
		assertEquals(OCCURRED_AT, event.occuredAt());
		assertEquals("synthetic-user-001", event.actorReference());
		assertEquals(AuthenticationChannel.WEB, event.channel());
		assertFalse(event.newDevice());
		assertEquals(2, event.failedAttempts());
	}

	@Test
	void shouldRejectNullEventId() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> new AuthenticationEvent(
						null,
						OCCURRED_AT,
						"synthetic-user-001",
						AuthenticationChannel.WEB,
						false,
						0));

		assertEquals("Event ID must not be blank", exception.getMessage());
	}

	@Test
	void shouldRejectBlankEventId() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> new AuthenticationEvent(
						"   ",
						OCCURRED_AT,
						"synthetic-user-001",
						AuthenticationChannel.WEB,
						false,
						0));

		assertEquals("Event ID must not be blank", exception.getMessage());
	}

	@Test
	void shouldRejectNullOccurredAt() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> new AuthenticationEvent(
						"synthetic-auth-event-001",
						null,
						"synthetic-user-001",
						AuthenticationChannel.WEB,
						false,
						0));

		assertEquals("Occurred at must not be null", exception.getMessage());
	}

	@Test
	void shouldRejectBlankActorReference() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> new AuthenticationEvent(
						"synthetic-auth-event-001",
						OCCURRED_AT,
						"   ",
						AuthenticationChannel.WEB,
						false,
						0));

		assertEquals(
				"Actor reference must not be blank",
				exception.getMessage());
	}

	@Test
	void shouldRejectNullActorReference() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> new AuthenticationEvent(
						"synthetic-auth-event-001",
						OCCURRED_AT,
						null,
						AuthenticationChannel.WEB,
						false,
						0));

		assertEquals(
				"Actor reference must not be blank",
				exception.getMessage());
	}

	@Test
	void shouldRejectNullAuthenticationChannel() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> new AuthenticationEvent(
						"synthetic-auth-event-001",
						OCCURRED_AT,
						"synthetic-user-001",
						null,
						false,
						0));

		assertEquals(
				"Authentication channel must not be null",
				exception.getMessage());
	}

	@Test
	void shouldRejectNegativeFailedAttempts() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> new AuthenticationEvent(
						"synthetic-auth-event-001",
						OCCURRED_AT,
						"synthetic-user-001",
						AuthenticationChannel.MOBILE,
						true,
						-1));

		assertEquals(
				"Failed attempts must not be negative",
				exception.getMessage());
	}
}
