package com.dannymedrano.fraudrisk.infrastructure.web.authentication;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        .andExpect(status().isBadRequest());
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
        .andExpect(status().isBadRequest());
  }
}
