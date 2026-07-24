# Fraud Risk Decision Engine Backend

Spring Boot backend for the Fraud Risk Decision Engine.

This module will expose APIs for evaluating synthetic authentication and transaction events using deterministic and explainable risk rules.

The current implementation contains only the initial Spring Boot application foundation.

## Requirements

- Java 17
- No system-wide Maven installation is required because the project includes the Maven Wrapper.

## Verify the Java Version

From the repository root or the `backend` directory:

    java -version

The active Java version must be Java 17.

On macOS, Java 17 can be activated temporarily in the current terminal with:

    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
    export PATH="$JAVA_HOME/bin:$PATH"

## Run the Tests

From the `backend` directory:

    ./mvnw test

## Run the Application

From the `backend` directory:

    ./mvnw spring-boot:run

The application starts on:

    http://localhost:8080

## Health Check

While the application is running:

    curl -i http://localhost:8080/actuator/health

Expected response:

    {
      "status": "UP"
    }

## Current Scope

This backend currently includes:

- Spring Boot application bootstrap.
- Spring Web.
- Spring Boot Actuator.
- Bean Validation.
- Synthetic authentication and transaction event models.
- Explainable authentication and transaction risk rules.
- Risk-score calculation.
- Threshold-based decisions: `ALLOW`, `REVIEW`, and `DENY`.
- Authentication and transaction risk evaluation use cases.
- Versioned REST endpoints for authentication and transaction evaluations.
- Structured validation and malformed-request error responses.
- Unit, application, and Spring MVC integration tests.

The following capabilities are **not** implemented yet:

- PostgreSQL persistence.
- Evaluation audit history.
- Idempotency storage.
- Configurable rules or decision thresholds.
- Fail-open and fail-closed behavior.
- Endpoint authentication or authorization.
- External fraud-provider integrations.
- Frontend integration.

## Data Policy

Only synthetic and fictional data may be used.

Do not add:

- real customer information;
- financial data;
- proprietary fraud rules;
- employer or client source code;
- confidential implementation details;
- private endpoints;
- credentials;
- secrets.

All examples in this repository must be independently created and safe for publication.

## Authentication risk evaluation endpoint

The backend exposes an HTTP endpoint for evaluating synthetic authentication
events through the configured risk rules.

### Start the backend

```bash
./mvnw spring-boot:run
```

### Evaluate an authentication event

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/evaluations/authentication \
  --header 'Content-Type: application/json' \
  --data '{
    "eventId": "synthetic-auth-event-006",
    "occurredAt": "2026-07-15T15:00:00Z",
    "actorReference": "synthetic-user-006",
    "channel": "MOBILE",
    "newDevice": true,
    "failedAttempts": 3
  }'
```

Example response:

```json
{
  "eventId": "synthetic-auth-event-006",
  "riskScore": 60,
  "decision": "REVIEW",
  "ruleResults": [
    {
      "ruleCode": "AUTH_NEW_DEVICE",
      "ruleName": "Authentication from a new device",
      "triggered": true,
      "scoreImpact": 25,
      "explanation": "The synthetic event indicates authentication from a new device",
      "ruleVersion": "1.0"
    },
    {
      "ruleCode": "AUTH_REPEATED_FAILURES",
      "ruleName": "Repeated failed authentication attempts",
      "triggered": true,
      "scoreImpact": 35,
      "explanation": "The synthetic event contains repeated failed authentication attempts",
      "ruleVersion": "1.0"
    }
  ]
}
```

All examples use fictional and synthetic data.

### Validation error response

Invalid requests return HTTP `400 Bad Request` with a structured error body.

Example request with an invalid event ID:

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/evaluations/authentication \
  --header 'Content-Type: application/json' \
  --data '{
    "eventId": "",
    "occurredAt": "2026-07-15T15:00:00Z",
    "actorReference": "synthetic-user-007",
    "channel": "WEB",
    "newDevice": false,
    "failedAttempts": 0
  }'
```

Example response:

```json
{
  "timestamp": "2026-07-15T21:30:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/evaluations/authentication",
  "fieldErrors": [
    {
      "field": "eventId",
      "message": "Event ID must not be blank"
    }
  ]
}
```

The timestamp is generated when the error response is created. When multiple
fields are invalid, all field-level validation errors are returned in
alphabetical order by field name.

### Malformed request response

Requests that cannot be converted into an authentication risk request return
HTTP `400 Bad Request` with a structured error body.

This includes malformed JSON, invalid date formats, unsupported authentication
channels, incorrect value types, and missing request bodies.

Example request with an unsupported authentication channel:

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/evaluations/authentication \
  --header 'Content-Type: application/json' \
  --data '{
    "eventId": "synthetic-auth-event-012",
    "occurredAt": "2026-07-22T20:00:00Z",
    "actorReference": "synthetic-user-012",
    "channel": "DESKTOP",
    "newDevice": false,
    "failedAttempts": 0
  }'
```

Example response:

```json
{
  "timestamp": "2026-07-22T20:00:00Z",
  "status": 400,
  "error": "MALFORMED_REQUEST",
  "message": "Request body is missing or malformed",
  "path": "/api/v1/evaluations/authentication",
  "fieldErrors": []
}
```

Malformed request responses do not expose internal parser or conversion details.
The `fieldErrors` array is empty because field-level validation is not reached
when the request body cannot be converted into a Java object.

## Transaction risk evaluation endpoint

The backend exposes an HTTP endpoint for evaluating synthetic transaction
events through the configured transaction risk rules.

### Evaluate a transaction event

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/evaluations/transactions \
  --header 'Content-Type: application/json' \
  --data '{
    "eventId": "synthetic-transaction-007",
    "occurredAt": "2026-07-24T03:30:00Z",
    "actorReference": "synthetic-actor-007",
    "transactionType": "TRANSFER",
    "amount": 1500.00,
    "currency": "USD",
    "destinationReference": "synthetic-destination-007",
    "newBeneficiary": true,
    "recentTransactionCount": 5
  }'
```

Example response:

```json
{
  "eventId": "synthetic-transaction-007",
  "riskScore": 65,
  "decision": "REVIEW",
  "ruleResults": [
    {
      "ruleCode": "NEW_BENEFICIARY",
      "ruleName": "Transaction to a new beneficiary",
      "triggered": true,
      "scoreImpact": 30,
      "explanation": "The synthetic transaction targets a newly added beneficiary",
      "ruleVersion": "1.0"
    },
    {
      "ruleCode": "TRANSACTION_VELOCITY",
      "ruleName": "Elevated recent transaction activity",
      "triggered": true,
      "scoreImpact": 35,
      "explanation": "The synthetic transaction count meets or exceeds the velocity threshold",
      "ruleVersion": "1.0"
    }
  ]
}
```

The example activates both configured transaction rules:

- a transaction to a newly added beneficiary contributes `30` points;
- five or more recent transactions contribute `35` points;
- the combined score of `65` produces a `REVIEW` decision.

All examples use fictional and synthetic data.

### Transaction request validation

Transaction requests are validated before reaching the application use case.

The endpoint requires:

- a non-blank event ID;
- a valid occurrence timestamp;
- a non-blank actor reference;
- a supported transaction type;
- an amount greater than zero;
- a three-letter uppercase currency code;
- a non-blank destination reference;
- a recent transaction count greater than or equal to zero.

Invalid fields return HTTP `400 Bad Request` using the existing structured
validation-error format.

Example request with an invalid currency:

```bash
curl --request POST \
  --url http://localhost:8080/api/v1/evaluations/transactions \
  --header 'Content-Type: application/json' \
  --data '{
    "eventId": "synthetic-transaction-010",
    "occurredAt": "2026-07-24T03:30:00Z",
    "actorReference": "synthetic-actor-010",
    "transactionType": "PAYMENT",
    "amount": 125.50,
    "currency": "usd",
    "destinationReference": "synthetic-destination-010",
    "newBeneficiary": false,
    "recentTransactionCount": 0
  }'
```

Example response:

```json
{
  "timestamp": "2026-07-24T03:31:00Z",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/evaluations/transactions",
  "fieldErrors": [
    {
      "field": "currency",
      "message": "Currency must be a three-letter uppercase code"
    }
  ]
}
```

### Malformed transaction requests

Requests that cannot be converted into a transaction risk request return
HTTP `400 Bad Request` with the existing `MALFORMED_REQUEST` response.

This includes:

- malformed JSON;
- invalid date formats;
- unsupported transaction types;
- incorrect value types;
- missing request bodies.

Parser and conversion details are not exposed in the response.
