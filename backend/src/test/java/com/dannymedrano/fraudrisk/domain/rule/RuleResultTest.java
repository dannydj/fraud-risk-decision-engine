package com.dannymedrano.fraudrisk.domain.rule;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RuleResultTest {

  @Test
  void shouldCreateValidRuleResult() {
    RuleResult result = new RuleResult(
        "AUTH_NEW_DEVICE",
        "Authentication from a new device",
        false,
        0,
        "No new-device risk signal was detected",
        "1.0");

    assertEquals("AUTH_NEW_DEVICE", result.ruleCode());
    assertEquals("Authentication from a new device", result.ruleName());
    assertFalse(result.triggered());
    assertEquals(0, result.scoreImpact());
    assertEquals(
        "No new-device risk signal was detected",
        result.explanation());
    assertEquals("1.0", result.ruleVersion());
  }

  @Test
  void shouldRejectBlankRuleCode() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new RuleResult(
            "   ",
            "Authentication from a new device",
            true,
            25,
            "The synthetic event indicates authentication from a new device",
            "1.0"));

    assertEquals("Rule code must not be blank", exception.getMessage());
  }

  @Test
  void shouldRejectBlankRuleName() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new RuleResult(
            "AUTH_NEW_DEVICE",
            "   ",
            true,
            25,
            "The synthetic event indicates authentication from a new device",
            "1.0"));

    assertEquals("Rule name must not be blank", exception.getMessage());
  }

  @Test
  void shouldRejectNegativeScoreImpact() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new RuleResult(
            "AUTH_NEW_DEVICE",
            "Authentication from a new device",
            true,
            -1,
            "The synthetic event indicates authentication from a new device",
            "1.0"));

    assertEquals(
        "Score impact must not be negative",
        exception.getMessage());
  }

  @Test
  void shouldRejectBlankExplanation() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new RuleResult(
            "AUTH_NEW_DEVICE",
            "Authentication from a new device",
            true,
            25,
            "   ",
            "1.0"));

    assertEquals("Explanation must not be blank", exception.getMessage());
  }

  @Test
  void shouldRejectBlankRuleVersion() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new RuleResult(
            "AUTH_NEW_DEVICE",
            "Authentication from a new device",
            true,
            25,
            "The synthetic event indicates authentication from a new device",
            "   "));

    assertEquals("Rule version must not be blank", exception.getMessage());
  }
}
