package com.dannymedrano.fraudrisk.domain.rule.authentication;

import com.dannymedrano.fraudrisk.domain.event.authentication.AuthenticationEvent;
import com.dannymedrano.fraudrisk.domain.rule.RuleResult;

public interface AuthenticationRiskRule {

  RuleResult evaluate(AuthenticationEvent event);
}
