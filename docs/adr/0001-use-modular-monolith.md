# ADR-0001: Use a Modular Monolith for the Initial Architecture

* **Status:** Accepted
* **Date:** 2026-07-10
* **Decision owners:** Project maintainer

## Context

The Fraud Risk Decision Engine must demonstrate professional software-engineering practices while remaining understandable, testable, and feasible for a first public portfolio project.

The MVP will include:

* authentication-event evaluation;
* transaction-event evaluation;
* deterministic risk rules;
* risk scoring;
* explainable decisions;
* idempotency;
* failure policies;
* audit persistence;
* a REST API;
* a minimal React frontend.

These capabilities require clear separation between domain logic, application orchestration, persistence, and delivery mechanisms.

However, the initial project does not require:

* independent service deployment;
* separate scaling strategies;
* distributed transactions;
* asynchronous messaging;
* multiple engineering teams;
* independent service ownership;
* complex operational infrastructure.

Starting with microservices would introduce additional complexity without providing meaningful value for the MVP.

## Decision

The initial system will be implemented as a **modular monolith** using a lightweight hexagonal architecture.

The backend will be deployed as a single Spring Boot application, but its internal code will be organized into clearly separated architectural areas:

* **Domain:** risk events, rules, scores, decisions, and policies;
* **Application:** use cases, orchestration, and ports;
* **Infrastructure:** REST controllers, persistence, configuration, and technical adapters.

The React frontend and PostgreSQL database will remain separate runtime components, while the backend business capabilities will remain within one deployable application.

## Architectural Direction

The intended dependency direction is:

```text
Infrastructure → Application → Domain
```

The domain must not depend on:

* Spring Framework;
* HTTP;
* PostgreSQL;
* JPA;
* React;
* Docker;
* external services.

The application layer may depend on the domain and define ports for infrastructure concerns.

The infrastructure layer may implement those ports using Spring Boot, REST, PostgreSQL, and other technical frameworks.

## Rationale

A modular monolith was selected because it:

* keeps the MVP operationally simple;
* allows the project to be developed incrementally;
* reduces deployment and debugging complexity;
* supports clear domain boundaries;
* enables fast automated testing;
* avoids premature distributed-system decisions;
* remains appropriate for a single-maintainer portfolio project;
* provides a path for future service extraction if justified.

This approach demonstrates architectural discipline without presenting unnecessary complexity as sophistication.

## Alternatives Considered

### Microservices

The backend could be divided into services such as:

* event ingestion;
* rule evaluation;
* decision management;
* audit storage.

This option was rejected for the MVP because it would require additional concerns:

* service discovery;
* inter-service communication;
* distributed tracing;
* network-failure handling;
* multiple deployment pipelines;
* contract versioning;
* distributed data consistency;
* increased infrastructure and testing effort.

The project currently has no scaling, ownership, or deployment requirement that justifies these costs.

### Traditional Layered Monolith

The system could use a conventional controller-service-repository structure.

This option was not selected as the primary direction because it may allow domain behavior to become tightly coupled to Spring, persistence, or API concerns.

A lightweight hexagonal structure provides clearer boundaries for the risk-decision domain.

### Serverless Functions

Each evaluation capability could be implemented as an independent function.

This option was rejected because it would complicate local development, persistence, audit consistency, and end-to-end demonstration without providing significant MVP benefits.

## Consequences

### Positive Consequences

* The application can be built and deployed as one backend artifact.
* Domain rules can be tested without Spring or PostgreSQL.
* Infrastructure can be replaced without rewriting domain behavior.
* Pull requests can remain small and focused.
* Local development will require fewer moving parts.
* Architectural boundaries will remain visible in the repository.
* The design can evolve without an early distributed-system commitment.

### Negative Consequences

* All backend modules share the same deployment lifecycle.
* Poor discipline could allow boundaries to erode over time.
* One backend failure may affect all capabilities.
* Independent scaling of internal modules is not initially available.
* The repository must enforce dependency direction through reviews and tests.

## Boundary Rules

The implementation must follow these rules:

1. Domain classes must not import Spring or persistence annotations.
2. REST request and response objects must not be used as domain objects.
3. JPA entities must not be exposed directly through the API.
4. Application services must coordinate use cases rather than contain infrastructure logic.
5. Infrastructure adapters must implement ports defined by the application layer.
6. Risk rules must remain independently testable.
7. Cross-module access must occur through explicit public contracts.
8. New modules must be introduced only when they represent a meaningful responsibility.

## Future Reassessment

This decision may be reviewed if the project later requires:

* independent deployment of capabilities;
* significantly different scaling requirements;
* separate data ownership;
* asynchronous high-volume event processing;
* multiple teams with independent ownership;
* strict availability isolation;
* external consumers requiring independently versioned services.

Any transition from the modular monolith must be supported by measured requirements rather than architectural preference alone.

## Compliance

Future pull requests should be reviewed for consistency with this decision.

A change that materially alters the deployment model, dependency direction, or module boundaries must introduce a new ADR that supersedes or amends this decision.
