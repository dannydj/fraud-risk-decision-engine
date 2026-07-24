package com.dannymedrano.fraudrisk.infrastructure.web.transaction;

import static org.hamcrest.Matchers.hasItem;
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
class TransactionRiskControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldEvaluateValidTransactionRequest() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-007",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-007",
          "transactionType": "TRANSFER",
          "amount": 1500.00,
          "currency": "USD",
          "destinationReference": "synthetic-destination-007",
          "newBeneficiary": true,
          "recentTransactionCount": 5
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(
            jsonPath("$.eventId")
                .value("synthetic-transaction-007"))
        .andExpect(jsonPath("$.riskScore").value(65))
        .andExpect(jsonPath("$.decision").value("REVIEW"))
        .andExpect(jsonPath("$.ruleResults.length()").value(2))
        .andExpect(
            jsonPath("$.ruleResults[0].ruleCode")
                .value("NEW_BENEFICIARY"))
        .andExpect(
            jsonPath("$.ruleResults[0].scoreImpact")
                .value(30))
        .andExpect(
            jsonPath("$.ruleResults[1].ruleCode")
                .value("TRANSACTION_VELOCITY"))
        .andExpect(
            jsonPath("$.ruleResults[1].scoreImpact")
                .value(35));
  }

  @Test
  void shouldRejectRequestWithBlankEventId() throws Exception {
    String requestBody = """
        {
          "eventId": "",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-008",
          "transactionType": "TRANSFER",
          "amount": 500.00,
          "currency": "USD",
          "destinationReference": "synthetic-destination-008",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
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
                .value("/api/v1/evaluations/transactions"))
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
  void shouldRejectRequestWithZeroAmount() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-009",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-009",
          "transactionType": "TRANSFER",
          "amount": 0,
          "currency": "USD",
          "destinationReference": "synthetic-destination-009",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(1))
        .andExpect(
            jsonPath("$.fieldErrors[0].field")
                .value("amount"))
        .andExpect(
            jsonPath("$.fieldErrors[0].message")
                .value("Amount must be greater than zero"));
  }

  @Test
  void shouldRejectRequestWithInvalidCurrency() throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-010",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-010",
          "transactionType": "PAYMENT",
          "amount": 125.50,
          "currency": "usd",
          "destinationReference": "synthetic-destination-010",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(1))
        .andExpect(
            jsonPath("$.fieldErrors[0].field")
                .value("currency"))
        .andExpect(
            jsonPath("$.fieldErrors[0].message")
                .value(
                    "Currency must be a three-letter uppercase code"));
  }

  @Test
  void shouldRejectRequestWithNegativeRecentTransactionCount()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-011",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-011",
          "transactionType": "TRANSFER",
          "amount": 300.00,
          "currency": "USD",
          "destinationReference": "synthetic-destination-011",
          "newBeneficiary": false,
          "recentTransactionCount": -1
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(1))
        .andExpect(
            jsonPath("$.fieldErrors[0].field")
                .value("recentTransactionCount"))
        .andExpect(
            jsonPath("$.fieldErrors[0].message")
                .value(
                    "Recent transaction count must not be negative"));
  }

  @Test
  void shouldRejectRequestWithMissingRequiredValues()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-012",
          "occurredAt": null,
          "actorReference": "synthetic-actor-012",
          "transactionType": null,
          "amount": null,
          "currency": "USD",
          "destinationReference": "synthetic-destination-012",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(3))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'occurredAt')].message")
                .value(hasItem("Occurred at must not be null")))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'transactionType')].message")
                .value(hasItem("Transaction type must not be null")))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'amount')].message")
                .value(hasItem("Amount must not be null")));
  }

  @Test
  void shouldReturnAllTransactionValidationErrors()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-013",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "",
          "transactionType": "TRANSFER",
          "amount": 0,
          "currency": "usd",
          "destinationReference": "",
          "newBeneficiary": false,
          "recentTransactionCount": -1
        }
        """;

    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"))
        .andExpect(
            jsonPath("$.message")
                .value("Request validation failed"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(5))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'actorReference')].message")
                .value(hasItem("Actor reference must not be blank")))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'amount')].message")
                .value(hasItem("Amount must be greater than zero")))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'currency')].message")
                .value(hasItem(
                    "Currency must be a three-letter uppercase code")))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'destinationReference')].message")
                .value(hasItem(
                    "Destination reference must not be blank")))
        .andExpect(
            jsonPath(
                "$.fieldErrors[?(@.field == 'recentTransactionCount')].message")
                .value(hasItem(
                    "Recent transaction count must not be negative")));
  }

  @Test
  void shouldReturnStructuredErrorForMalformedJson()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-014"
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-014",
          "transactionType": "TRANSFER",
          "amount": 200.00,
          "currency": "USD",
          "destinationReference": "synthetic-destination-014",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    assertMalformedRequest(requestBody);
  }

  @Test
  void shouldReturnStructuredErrorForInvalidOccurredAt()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-015",
          "occurredAt": "not-a-valid-date",
          "actorReference": "synthetic-actor-015",
          "transactionType": "TRANSFER",
          "amount": 200.00,
          "currency": "USD",
          "destinationReference": "synthetic-destination-015",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    assertMalformedRequest(requestBody);
  }

  @Test
  void shouldReturnStructuredErrorForUnsupportedTransactionType()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-016",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-016",
          "transactionType": "WITHDRAWAL",
          "amount": 200.00,
          "currency": "USD",
          "destinationReference": "synthetic-destination-016",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    assertMalformedRequest(requestBody);
  }

  @Test
  void shouldReturnStructuredErrorForIncorrectAmountType()
      throws Exception {
    String requestBody = """
        {
          "eventId": "synthetic-transaction-017",
          "occurredAt": "2026-07-24T03:30:00Z",
          "actorReference": "synthetic-actor-017",
          "transactionType": "PAYMENT",
          "amount": {
            "value": 200.00
          },
          "currency": "USD",
          "destinationReference": "synthetic-destination-017",
          "newBeneficiary": false,
          "recentTransactionCount": 0
        }
        """;

    assertMalformedRequest(requestBody);
  }

  @Test
  void shouldReturnStructuredErrorForEmptyRequestBody()
      throws Exception {
    assertMalformedRequest("");
  }

  private void assertMalformedRequest(String requestBody)
      throws Exception {
    mockMvc.perform(
        post("/api/v1/evaluations/transactions")
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
                .value("/api/v1/evaluations/transactions"))
        .andExpect(
            jsonPath("$.fieldErrors.length()")
                .value(0));
  }
}
