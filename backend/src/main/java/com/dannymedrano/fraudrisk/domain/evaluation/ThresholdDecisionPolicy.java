package com.dannymedrano.fraudrisk.domain.evaluation;

/**
 * Converts a validated risk score into a decision using fictional
 * thresholds created exclusively for this demonstration project.
 */
public class ThresholdDecisionPolicy {
  
  private static final int REVIEW_THRESHOLD = 40;
  private static final int DENY_THRESHOLD = 70;

  public Decision decide(RiskScore riskScore) {
    if (riskScore.value() >= DENY_THRESHOLD) {
      return Decision.DENY;
    }

    if (riskScore.value() >= REVIEW_THRESHOLD) {
      return Decision.REVIEW;
    }

    return Decision.ALLOW;
  }
}
