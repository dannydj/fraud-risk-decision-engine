package com.dannymedrano.fraudrisk.infrastructure.web.authentication;

import com.dannymedrano.fraudrisk.application.port.in.authentication.EvaluateAuthenticationRiskCommand;
import com.dannymedrano.fraudrisk.application.port.in.authentication.EvaluateAuthenticationRiskUseCase;
import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/evaluations/authentication")
public class AuthenticationRiskController {

  private final EvaluateAuthenticationRiskUseCase useCase;

  public AuthenticationRiskController(
      EvaluateAuthenticationRiskUseCase useCase) {
    if (useCase == null) {
      throw new IllegalArgumentException(
          "Authentication risk use case must not be null");
    }

    this.useCase = useCase;
  }

  @PostMapping
  public AuthenticationRiskResponse evaluate(
      @Valid @RequestBody AuthenticationRiskRequest request) {
    EvaluateAuthenticationRiskCommand command = new EvaluateAuthenticationRiskCommand(
        request.eventId(),
        request.occurredAt(),
        request.actorReference(),
        request.channel(),
        request.newDevice(),
        request.failedAttempts());

    AuthenticationRiskEvaluation evaluation = useCase.evaluate(command);

    return AuthenticationRiskResponse.from(evaluation);
  }
}
