# Fraud Risk Decision Engine

An explainable fraud-risk decision engine for evaluating synthetic authentication and transaction events.

This project is aligned with **Secure Digital Banking & Fraud Prevention Engineering** and is designed to demonstrate how deterministic risk rules, scoring, auditability, and resilient decision policies can be implemented using a generic architecture.

## Project Status

**Current phase:** Repository foundation and MVP definition.

Application code has not been implemented yet.

## Purpose

The Fraud Risk Decision Engine will receive synthetic digital-banking events and produce an explainable risk decision.

Each evaluation will return:

* a risk score;
* an `ALLOW`, `REVIEW`, or `DENY` decision;
* the rules that were triggered;
* human-readable explanations;
* evaluation and ruleset version information;
* an auditable record of the decision.

The initial implementation will focus on deterministic and transparent rules rather than machine-learning models.

## MVP Capabilities

The MVP is expected to support:

* authentication-event evaluation;
* transaction-event evaluation;
* explainable risk rules;
* risk scoring from `0` to `100`;
* `ALLOW`, `REVIEW`, and `DENY` decisions;
* triggered-rule reporting;
* audit-trail persistence;
* idempotent processing using `eventId`;
* fail-open and fail-closed behavior;
* automated backend and frontend tests;
* a minimal interface for submitting and reviewing synthetic events.

## Architecture Direction

The project will begin as a **modular monolith** using a lightweight hexagonal architecture.

The main architectural areas will be:

* **Domain:** risk events, rules, scores, decisions, and policies;
* **Application:** evaluation use cases and orchestration;
* **Infrastructure:** REST APIs, persistence, configuration, and external adapters;
* **Frontend:** synthetic-event submission and evaluation-history visualization.

This approach keeps the MVP understandable and deployable while maintaining clear boundaries between business logic and technical infrastructure.

## Technology Stack

### Backend

* Java 17
* Spring Boot
* PostgreSQL
* JUnit

### Frontend

* React
* TypeScript
* Vitest
* React Testing Library

### Engineering and Delivery

* Docker Compose
* GitHub Actions
* GitHub Issues
* Feature branches
* Pull requests
* Semantic versioning and GitHub Releases

## Synthetic Data Policy

This repository must use exclusively fictional and synthetic data created specifically for this project.

It must not contain:

* real customer or employee information;
* real account, card, transaction, device, or session identifiers;
* proprietary source code;
* private endpoints or credentials;
* employer or client configurations;
* proprietary fraud-detection rules;
* screenshots or documents from private systems;
* confidential implementation details.

Any resemblance between sample data and real persons, organizations, systems, or transactions is unintended.

## Non-Production Disclaimer

This project is an educational and professional portfolio implementation.

It is not intended to:

* process real financial transactions;
* make real customer-risk decisions;
* replace a production fraud-management platform;
* provide regulatory, compliance, financial, or security guarantees.

Additional security, privacy, availability, regulatory, operational, and governance controls would be required before using a similar system in a production environment.

## Initial Roadmap

1. Define the MVP, architecture, and data policy.
2. Initialize the Spring Boot backend.
3. Implement an authentication-event evaluation slice.
4. Add explainable risk rules and scoring.
5. Add transaction-event evaluation.
6. Persist evaluations and audit records in PostgreSQL.
7. Implement idempotency and failure policies.
8. Build the React interface.
9. Add Docker Compose and automated CI checks.
10. Complete security documentation and publish the first MVP release.

## License

This project is licensed under the Apache License 2.0.
