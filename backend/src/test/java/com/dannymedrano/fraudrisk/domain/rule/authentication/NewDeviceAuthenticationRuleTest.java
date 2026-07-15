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

class NewDeviceAuthenticationRuleTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-14T20:00:00Z");

  private final NewDeviceAuthenticationRule rule = new NewDeviceAuthenticationRule();

  @Test
  void shouldTriggerWhenAuthenticationUsesNewDevice() {
    AuthenticationEvent event = createEvent(true);

    RuleResult result = rule.evaluate(event);

    assertEquals("AUTH_NEW_DEVICE", result.ruleCode());
    assertEquals(
        "Authentication from a new device",
        result.ruleName());
    assertTrue(result.triggered());
    assertEquals(25, result.scoreImpact());
    assertEquals(
        "The synthetic event indicates authentication from a new device",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldNotTriggerWhenDeviceIsRecognized() {
    AuthenticationEvent event = createEvent(false);

    RuleResult result = rule.evaluate(event);

    assertEquals("AUTH_NEW_DEVICE", result.ruleCode());
    assertEquals(
        "Authentication from a new device",
        result.ruleName());
    assertFalse(result.triggered());
    assertEquals(0, result.scoreImpact());
    assertEquals(
        "No new-device risk signal was detected",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
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

  private AuthenticationEvent createEvent(boolean newDevice) {
    return new AuthenticationEvent(
        "synthetic-auth-event-001",
        OCCURRED_AT,
        "synthetic-user-001",
        AuthenticationChannel.MOBILE,
        newDevice,
        0);
  }
}
