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
- One application-context test.
- Health endpoint exposure through Actuator.

The following capabilities are **not** implemented yet:

- Risk-event models.
- Fraud-risk rules.
- Risk scoring.
- Decision logic.
- PostgreSQL persistence.
- Idempotency.
- Fail-open and fail-closed behavior.
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
