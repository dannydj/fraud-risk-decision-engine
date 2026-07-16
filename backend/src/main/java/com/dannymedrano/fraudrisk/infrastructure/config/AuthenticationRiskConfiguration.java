package com.dannymedrano.fraudrisk.infrastructure.config;

import com.dannymedrano.fraudrisk.application.port.in.authentication.EvaluateAuthenticationRiskUseCase;
import com.dannymedrano.fraudrisk.application.service.authentication.AuthenticationRiskEvaluationService;
import com.dannymedrano.fraudrisk.domain.evaluation.authentication.AuthenticationRiskEvaluator;
import com.dannymedrano.fraudrisk.domain.rule.authentication.FailedAttemptsAuthenticationRule;
import com.dannymedrano.fraudrisk.domain.rule.authentication.NewDeviceAuthenticationRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AuthenticationRiskConfiguration {

  @Bean
  NewDeviceAuthenticationRule newDeviceAuthenticationRule() {
    return new NewDeviceAuthenticationRule();
  }

  @Bean
  FailedAttemptsAuthenticationRule failedAttemptsAuthenticationRule() {
    return new FailedAttemptsAuthenticationRule();
  }

  @Bean
  AuthenticationRiskEvaluator authenticationRiskEvaluator(
      NewDeviceAuthenticationRule newDeviceRule,
      FailedAttemptsAuthenticationRule failedAttemptsRule) {
    return new AuthenticationRiskEvaluator(
        List.of(
            newDeviceRule,
            failedAttemptsRule));
  }

  @Bean
  EvaluateAuthenticationRiskUseCase evaluateAuthenticationRiskUseCase(
      AuthenticationRiskEvaluator evaluator) {
    return new AuthenticationRiskEvaluationService(evaluator);
  }
}
