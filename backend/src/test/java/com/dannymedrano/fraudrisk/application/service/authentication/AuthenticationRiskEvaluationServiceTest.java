package com.dannymedrano.fraudrisk.application.service.authentication;

import com.dannymedrano.fraudrisk.application.port.in.authentication.EvaluateAuthenticationRiskCommand;
import com.dannymedrano.fraudrisk.domain.evaluation.Decision;
import com.dannymedrano.fraudrisk.domain.evaluation.RiskScore;
import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluation;
import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluator;
import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationChannel;
import com.dannymedrano.fraudrisk.domain.rule.authentication.FailedAttemptsAuthenticationRule;
import com.dannymedrano.fraudrisk.domain.rule.authentication.NewDeviceAuthenticationRule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthenticationRiskEvaluationServiceTest {

  private static final Instant OCCURRED_AT = Instant.parse("2026-07-14T23:00:00Z");

  private final AuthenticationRiskEvaluator evaluator = new AuthenticationRiskEvaluator(
      List.of(
          new NewDeviceAuthenticationRule(),
          new FailedAttemptsAuthenticationRule()));

  private final AuthenticationRiskEvaluationService service = new AuthenticationRiskEvaluationService(evaluator);

  @Test
  void shouldEvaluateAuthenticationRiskCommand() {
    EvaluateAuthenticationRiskCommand command = new EvaluateAuthenticationRiskCommand(
        "synthetic-auth-event-005",
        OCCURRED_AT,
        "synthetic-user-005",
        AuthenticationChannel.MOBILE,
        true,
        3);

    AuthenticationRiskEvaluation evaluation = service.evaluate(command);

    assertEquals(
        "synthetic-auth-event-005",
        evaluation.eventId());
    assertEquals(new RiskScore(60), evaluation.riskScore());
    assertEquals(Decision.REVIEW, evaluation.decision());
    assertEquals(2, evaluation.ruleResults().size());
    assertEquals(
        "AUTH_NEW_DEVICE",
        evaluation.ruleResults().get(0).ruleCode());
    assertEquals(
        "AUTH_REPEATED_FAILURES",
        evaluation.ruleResults().get(1).ruleCode());
  }

  @Test
  void shouldRejectNullEvaluator() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> new AuthenticationRiskEvaluationService(null));

    assertEquals(
        "Authentication risk evaluator must not be null",
        exception.getMessage());
  }

  @Test
  void shouldRejectNullCommand() {
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.evaluate(null));

    assertEquals(
        "Authentication risk command must not be null",
        exception.getMessage());
  }

  @Test
  void shouldPropagateDomainValidationForBlankEventId() {
    EvaluateAuthenticationRiskCommand command = new EvaluateAuthenticationRiskCommand(
        "   ",
        OCCURRED_AT,
        "synthetic-user-005",
        AuthenticationChannel.WEB,
        false,
        0);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.evaluate(command));

    assertEquals(
        "Event ID must not be blank",
        exception.getMessage());
  }

  @Test
  void shouldPropagateDomainValidationForNegativeFailedAttempts() {
    EvaluateAuthenticationRiskCommand command = new EvaluateAuthenticationRiskCommand(
        "synthetic-auth-event-005",
        OCCURRED_AT,
        "synthetic-user-005",
        AuthenticationChannel.WEB,
        false,
        -1);

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.evaluate(command));

    assertEquals(
        "Failed attempts must not be negative",
        exception.getMessage());
  }
}
