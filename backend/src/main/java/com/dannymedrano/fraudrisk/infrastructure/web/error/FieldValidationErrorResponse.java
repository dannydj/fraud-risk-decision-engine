package com.dannymedrano.fraudrisk.infrastructure.web.error;

public record FieldValidationErrorResponse(
    String field,
    String message) {
}
