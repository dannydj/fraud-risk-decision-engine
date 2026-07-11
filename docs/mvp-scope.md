# MVP Scope

## Objective

The first version of the Fraud Risk Decision Engine will demonstrate how a generic system can evaluate synthetic digital-banking events using deterministic, explainable, and auditable risk rules.

The MVP will focus on clarity, testability, and architectural separation rather than production scale or advanced fraud-detection techniques.

## Supported Event Types

The MVP will evaluate two types of synthetic events:

* authentication events;
* transaction events.

## Core Capabilities

The MVP will support:

* receiving a synthetic risk event through a REST API;
* validating the event structure;
* evaluating applicable deterministic risk rules;
* calculating a risk score between `0` and `100`;
* issuing an `ALLOW`, `REVIEW`, or `DENY` decision;
* returning the rules that were triggered;
* returning a human-readable explanation for each triggered rule;
* persisting an auditable evaluation record;
* retrieving previous evaluations;
* processing events idempotently using `eventId`;
* detecting reuse of an `eventId` with a different payload;
* supporting configurable fail-open and fail-closed behavior;
* exposing a minimal React interface;
* running the application locally with Docker Compose;
* validating backend and frontend changes through automated tests.

## Initial Decision Policy

The initial synthetic decision thresholds will be:

| Risk score | Decision |
| ---------- | -------- |
| `0–39`     | `ALLOW`  |
| `40–69`    | `REVIEW` |
| `70–100`   | `DENY`   |

The final score will be capped at `100`.

These thresholds are fictional and exist only to demonstrate the design of an explainable decision engine.

## Initial Risk Rules

The first implementation will contain a small set of synthetic rules.

### Authentication Rules

* authentication from a new device;
* repeated failed authentication attempts.

### Transaction Rules

* transaction amount above a synthetic threshold;
* transaction involving a recently added beneficiary.

Each rule result will expose:

* rule code;
* rule name;
* triggered status;
* score impact;
* human-readable explanation;
* rule version.

## Idempotency

Every event must include an `eventId`.

The expected behavior is:

1. The first request for an `eventId` is evaluated and persisted.
2. A repeated request with the same `eventId` and equivalent payload returns the stored evaluation.
3. A repeated request with the same `eventId` and a different payload returns an HTTP `409 Conflict` response.

The implementation will store a normalized request hash to detect conflicting payloads.

## Failure Modes

The engine will support two configurable failure policies.

### Fail Open

When an unexpected internal evaluation error occurs:

* the resulting decision is `ALLOW`;
* the evaluation status is marked as degraded;
* the failure reason is recorded in the audit trail.

### Fail Closed

When an unexpected internal evaluation error occurs:

* the resulting decision is `DENY`;
* the evaluation status is marked as degraded;
* the failure reason is recorded in the audit trail.

The API response must distinguish a degraded decision from a normal risk-rule decision.

## Audit Trail

Each persisted evaluation should contain at least:

* evaluation identifier;
* event identifier;
* event type;
* request hash;
* risk score;
* decision;
* triggered rules;
* rule explanations;
* evaluation status;
* configured failure mode;
* engine version;
* ruleset version;
* event occurrence time;
* event reception time;
* evaluation completion time.

Audit records will be read-only through the MVP API.

## Minimal User Interface

The frontend will provide:

* a form for submitting a synthetic authentication event;
* a form for submitting a synthetic transaction event;
* a result view showing the decision, score, and triggered rules;
* a simple list of recent evaluations.

The MVP will not include authentication, user administration, rule editing, or advanced dashboards.

## Out of Scope

The following capabilities are explicitly excluded from the MVP:

* machine-learning models;
* behavioral biometrics;
* real banking integrations;
* real customer or transaction data;
* proprietary fraud rules;
* payment processing;
* message brokers such as Kafka;
* microservices;
* distributed transaction processing;
* multi-tenant support;
* rule management through the frontend;
* rules stored dynamically in the database;
* OAuth or identity-provider integration;
* advanced authorization;
* cloud deployment;
* high-availability architecture;
* regulatory or compliance certification;
* production fraud-decision guarantees.

## Success Criteria

The MVP will be considered complete when:

* both supported event types can be evaluated;
* decisions are deterministic and explainable;
* the score and triggered rules are visible;
* evaluations are persisted in PostgreSQL;
* idempotency behavior is verified;
* fail-open and fail-closed behavior is tested;
* the frontend can submit and display synthetic evaluations;
* Docker Compose starts the required services;
* backend and frontend tests pass in GitHub Actions;
* architecture and security documentation are available;
* release `v0.1.0` is published.
