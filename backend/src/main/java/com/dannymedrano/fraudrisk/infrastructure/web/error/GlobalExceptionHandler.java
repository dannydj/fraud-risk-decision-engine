package com.dannymedrano.fraudrisk.infrastructure.web.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpServletRequest request) {
    List<FieldValidationErrorResponse> fieldErrors = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .sorted(Comparator.comparing(FieldError::getField))
        .map(fieldError -> new FieldValidationErrorResponse(
            fieldError.getField(),
            getValidationMessage(fieldError)))
        .toList();

    HttpStatus status = HttpStatus.BAD_REQUEST;

    ApiErrorResponse response = new ApiErrorResponse(
        Instant.now(),
        status.value(),
        "VALIDATION_ERROR",
        "Request validation failed",
        request.getRequestURI(),
        fieldErrors);

    return ResponseEntity
        .status(status)
        .body(response);
  }

  private String getValidationMessage(FieldError fieldError) {
    String defaultMessage = fieldError.getDefaultMessage();

    if (defaultMessage == null || defaultMessage.isBlank()) {
      return "Invalid value";
    }

    return defaultMessage;
  }
}
