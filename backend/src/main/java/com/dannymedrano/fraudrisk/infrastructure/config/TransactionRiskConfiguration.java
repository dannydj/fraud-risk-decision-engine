package com.dannymedrano.fraudrisk.infrastructure.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.dannymedrano.fraudrisk.application.port.in.transaction.EvaluateTransactionRiskUseCase;
import com.dannymedrano.fraudrisk.application.service.transaction.TransactionRiskEvaluationService;
import com.dannymedrano.fraudrisk.domain.evaluation.transaction.TransactionRiskEvaluator;
import com.dannymedrano.fraudrisk.domain.rule.transaction.NewBeneficiaryTransactionRule;
import com.dannymedrano.fraudrisk.domain.rule.transaction.TransactionVelocityRiskRule;

@Configuration
public class TransactionRiskConfiguration {

  @Bean
  NewBeneficiaryTransactionRule newBeneficiaryTransactionRule() {
    return new NewBeneficiaryTransactionRule();
  }

  @Bean
  TransactionVelocityRiskRule transactionVelocityRiskRule() {
    return new TransactionVelocityRiskRule();
  }

  @Bean
  TransactionRiskEvaluator transactionRiskEvaluator(
      NewBeneficiaryTransactionRule newBeneficiaryRule,
      TransactionVelocityRiskRule velocityRule) {
    return new TransactionRiskEvaluator(
        List.of(
            newBeneficiaryRule,
            velocityRule));
  }

  @Bean
  EvaluateTransactionRiskUseCase evaluateTransactionRiskUseCase(
      TransactionRiskEvaluator evaluator) {
    return new TransactionRiskEvaluationService(evaluator);
  }
}
