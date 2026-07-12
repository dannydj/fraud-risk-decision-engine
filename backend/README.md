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