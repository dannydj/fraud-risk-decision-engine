package com.dannymedrano.fraudrisk.infrastructure.web.error;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldValidationErrorResponse> fieldErrors) {

  public ApiErrorResponse {
    fieldErrors = List.copyOf(fieldErrors);
  }
}
