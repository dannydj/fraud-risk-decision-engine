package com.dannymedrano.fraudrisk.infrastructure.web.authentication;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationRiskControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldEvaluateValidAuthenticationRequest() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-auth-event-006",
          "occurredAt": "2026-07-15T15:00:00Z",
          "actorReference": "synthetic-user-006",
          "channel": "MOBILE",
          "newDevice": true,
          "failedAttempts": 3
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.eventId")
                .value("synthetic-auth-event-006"))
        .andExpect(jsonPath("$.riskScore").value(60))
        .andExpect(jsonPath("$.decision").value("REVIEW"))
        .andExpect(jsonPath("$.ruleResults.length()").value(2))
        .andExpect(
            jsonPath("$.ruleResults[0].ruleCode")
                .value("AUTH_NEW_DEVICE"))
        .andExpect(
            jsonPath("$.ruleResults[0].scoreImpact")
                .value(25))
        .andExpect(
            jsonPath("$.ruleResults[1].ruleCode")
                .value("AUTH_REPEATED_FAILURES"))
        .andExpect(
            jsonPath("$.ruleResults[1].scoreImpact")
                .value(35));
  }

  @Test
  void shouldRejectRequestWithBlankEventId() throws Exception {
    String requestBody = """
        {
          "eventId": "",
          "occurredAt": "2026-07-15T15:00:00Z",
          "actorReference": "synthetic-user-007",
          "channel": "WEB",
          "newDevice": false,
          "failedAttempts": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.message")
                .value("Request validation failed"))
        .andExpect(
            jsonPath("$.path")
                .value("/api/v1/evaluations/authentication"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(1))
        .andExpect(
            jsonPath("$.fieldErrors[0].field")
                .value("eventId"))
        .andExpect(
            jsonPath("$.fieldErrors[0].message")
                .value("Event ID must not be blank"));
  }

  @Test
  void shouldRejectRequestWithNegativeFailedAttempts() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-auth-event-008",
          "occurredAt": "2026-07-15T15:00:00Z",
          "actorReference": "synthetic-user-008",
          "channel": "WEB",
          "newDevice": false,
          "failedAttempts": -1
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.message")
                .value("Request validation failed"))
        .andExpect(
            jsonPath("$.path")
                .value("/api/v1/evaluations/authentication"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(1))
        .andExpect(
            jsonPath("$.fieldErrors[0].field")
                .value("failedAttempts"))
        .andExpect(
            jsonPath("$.fieldErrors[0].message")
                .value("Failed attempts must not be negative"));
  }

  @Test
  void shouldReturnAllValidationErrorsInFieldOrder() throws Exception {
    String requestBody = """
        {
          "eventId": "",
          "occurredAt": "2026-07-15T15:00:00Z",
          "actorReference": "synthetic-user-009",
          "channel": "WEB",
          "newDevice": false,
          "failedAttempts": -1
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(2))
        .andExpect(
            jsonPath("$.fieldErrors[0].field")
                .value("eventId"))
        .andExpect(
            jsonPath("$.fieldErrors[0].message")
                .value("Event ID must not be blank"))
        .andExpect(
            jsonPath("$.fieldErrors[1].field")
                .value("failedAttempts"))
        .andExpect(
            jsonPath("$.fieldErrors[1].message")
                .value("Failed attempts must not be negative"));
  }

  @Test
  void shouldReturnStructuredErrorForMalformedJson() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-auth-event-010"
          "occurredAt": "2026-07-22T20:00:00Z",
          "actorReference": "synthetic-user-010",
          "channel": "WEB",
          "newDevice": false,
          "failedAttempts": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("MALFORMED_REQUEST"))
        .andExpect(
            jsonPath("$.message")
                .value("Request body is missing or malformed"))
        .andExpect(
            jsonPath("$.path")
                .value("/api/v1/evaluations/authentication"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(0));
  }

  @Test
  void shouldReturnStructuredErrorForInvalidOccurredAt() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-auth-event-011",
          "occurredAt": "not-a-valid-date",
          "actorReference": "synthetic-user-011",
          "channel": "WEB",
          "newDevice": false,
          "failedAttempts": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("MALFORMED_REQUEST"))
        .andExpect(
            jsonPath("$.message")
                .value("Request body is missing or malformed"))
        .andExpect(
            jsonPath("$.path")
                .value("/api/v1/evaluations/authentication"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(0));
  }

  @Test
  void shouldReturnStructuredErrorForUnsupportedAuthenticationChannel()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-auth-event-012",
          "occurredAt": "2026-07-22T20:00:00Z",
          "actorReference": "synthetic-user-012",
          "channel": "DESKTOP",
          "newDevice": false,
          "failedAttempts": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("MALFORMED_REQUEST"))
        .andExpect(
            jsonPath("$.message")
                .value("Request body is missing or malformed"))
        .andExpect(
            jsonPath("$.path")
                .value("/api/v1/evaluations/authentication"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(0));
  }

  @Test
  void shouldReturnStructuredErrorForIncorrectValueType() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-auth-event-013",
          "occurredAt": "2026-07-22T20:00:00Z",
          "actorReference": "synthetic-user-013",
          "channel": "WEB",
          "newDevice": false,
          "failedAttempts": {
            "value": 3
          }
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("MALFORMED_REQUEST"))
        .andExpect(
            jsonPath("$.message")
                .value("Request body is missing or malformed"))
        .andExpect(
            jsonPath("$.path")
                .value("/api/v1/evaluations/authentication"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(0));
  }

  @Test
  void shouldReturnStructuredErrorForEmptyRequestBody() throws Exception {
    mockMvc.perform(
        post("/api/v1/evaluations/authentication")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(
            jsonPath("$.error")
                .value("MALFORMED_REQUEST"))
        .andExpect(
            jsonPath("$.message")
                .value("Request body is missing or malformed"))
        .andExpect(
            jsonPath("$.path")
                .value("/api/v1/evaluations/authentication"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(0));
  }
}
