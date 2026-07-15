package com.dannymedrano.fraudrisk.application.service.authentication;

import com.dannymedrano.fraudrisk.application.port.in.authentication.EvaluateAuthenticationRiskCommand;
import com.dannymedrano.fraudrisk.application.port.in.authentication.EvaluateAuthenticationRiskUseCase;
import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluation;
import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluator;
import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationEvent;

public final class AuthenticationRiskEvaluationService
    implements EvaluateAuthenticationRiskUseCase {

  private final AuthenticationRiskEvaluator evaluator;

  public AuthenticationRiskEvaluationService(
      AuthenticationRiskEvaluator evaluator) {
    if (evaluator == null) {
      throw new IllegalArgumentException(
          "Authentication risk evaluator must not be null");
    }

    this.evaluator = evaluator;
  }

  @Override
  public AuthenticationRiskEvaluation evaluate(
      EvaluateAuthenticationRiskCommand command) {
    if (command == null) {
      throw new IllegalArgumentException(
          "Authentication risk command must not be null");
    }

    AuthenticationEvent event = new AuthenticationEvent(
        command.eventId(),
        command.occurredAt(),
        command.actorReference(),
        command.channel(),
        command.newDevice(),
        command.failedAttempts());

    return evaluator.evaluate(event);
  }
}
