package com.dannymedrano.fraudrisk.domain.rule.authentication;

import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationChannel;
import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailedAttemptsAuthenticationRuleTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-14T21:00:00Z");

  private final FailedAttemptsAuthenticationRule rule = new FailedAttemptsAuthenticationRule();

  @Test
  void shouldNotTriggerWhenThereAreNoFailedAttempts() {
    AuthenticationEvent event = createEvent(0);

    RuleResult result = rule.evaluate(event);

    assertFalse(result.triggered());
    assertEquals(0, result.scoreImpact());
  }

  @Test
  void shouldNotTriggerImmediatelyBelowThreshold() {
    AuthenticationEvent event = createEvent(2);

    RuleResult result = rule.evaluate(event);

    assertEquals("AUTH_REPEATED_FAILURES", result.ruleCode());
    assertEquals(
        "Repeated failed authentication attempts",
        result.ruleName());
    assertFalse(result.triggered());
    assertEquals(0, result.scoreImpact());
    assertEquals(
        "No repeated-failure risk signal was detected",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldTriggerAtThreshold() {
    AuthenticationEvent event = createEvent(3);

    RuleResult result = rule.evaluate(event);

    assertEquals("AUTH_REPEATED_FAILURES", result.ruleCode());
    assertEquals(
        "Repeated failed authentication attempts",
        result.ruleName());
    assertTrue(result.triggered());
    assertEquals(35, result.scoreImpact());
    assertEquals(
        "The synthetic event indicates repeated failed authentication attempts",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldTriggerAboveThreshold() {
    AuthenticationEvent event = createEvent(5);

    RuleResult result = rule.evaluate(event);

    assertTrue(result.triggered());
    assertEquals(35, result.scoreImpact());
  }

  @Test
  void shouldRejectNullAuthenticationEvent() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> rule.evaluate(null));

    assertEquals(
        "Authentication event must not be null",
        exception.getMessage());
  }

  private AuthenticationEvent createEvent(int failedAttempts) {
    return new AuthenticationEvent(
        "synthetic-auth-event-002",
        OCCURRED_AT,
        "synthetic-user-002",
        AuthenticationChannel.WEB,
        false,
        failedAttempts);
  }
}
