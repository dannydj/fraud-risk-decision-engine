package com.dannymedrano.fraudrisk.application.port.in.authentication;

import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluation;

public interface EvaluateAuthenticationRiskUseCase {

  AuthenticationRiskEvaluation evaluate(
      EvaluateAuthenticationRiskCommand command);
}
