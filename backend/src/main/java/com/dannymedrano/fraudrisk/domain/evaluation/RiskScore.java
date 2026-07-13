package com.dannymedrano.fraudrisk.domain.evaluation;

public record RiskScore(int value) {
  
  public static final int MIN_VALUE = 0;
  public static final int MAX_VALUE = 100;

  public RiskScore {
    if (value < MIN_VALUE || value > MAX_VALUE) {
      throw new IllegalArgumentException("Risk score must be between 0 and 100");
    }
  }
}
