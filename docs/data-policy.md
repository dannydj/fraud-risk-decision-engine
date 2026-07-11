# Synthetic Data and Confidentiality Policy

## Purpose

The Fraud Risk Decision Engine is a public educational and professional portfolio project.

All data, rules, examples, identifiers, configurations, documentation, screenshots, tests, and demonstrations included in this repository must be fictional, synthetic, and created specifically for this project.

The repository must remain independent from any employer, client, financial institution, fraud-prevention vendor, or private system.

## Core Requirement

Only synthetic data may be committed to this repository.

Synthetic data must:

* be invented specifically for this project;
* avoid resemblance to known real customers, employees, accounts, or transactions;
* avoid using production-derived values;
* avoid exposing internal system structures;
* be safe to publish in a public repository;
* be clearly identifiable as test or demonstration data.

## Prohibited Information

The repository must not contain real or confidential information, including:

### Personal and Financial Data

* customer or employee names;
* email addresses or phone numbers;
* government-issued identifiers;
* addresses or precise locations;
* account or card numbers;
* transaction records;
* balances;
* beneficiary information;
* device fingerprints;
* authentication credentials;
* session identifiers;
* access tokens;
* security questions;
* biometric information.

### Technical and Operational Information

* private source code;
* internal libraries;
* proprietary algorithms;
* private API endpoints;
* internal hostnames or IP addresses;
* database connection details;
* message-queue names;
* production configuration;
* certificates or private keys;
* passwords or secrets;
* internal architecture diagrams;
* logs obtained from private environments;
* screenshots from employer or client systems.

### Fraud-Detection Information

* proprietary fraud rules;
* real fraud thresholds;
* vendor-specific rule names;
* private decision policies;
* production risk scores;
* real decline reasons;
* confidential fraud scenarios;
* internal monitoring or alerting criteria.

## Employer and Client Independence

The project must not copy or recreate:

* code from current or previous employers;
* client-specific implementations;
* vendor-specific integrations;
* private naming conventions;
* internal data models;
* confidential workflows;
* proprietary configuration structures;
* implementation details learned from restricted documentation.

General software-engineering concepts may be used, but their implementation must be independently designed for this repository.

## Synthetic Identifiers

Synthetic identifiers should use explicit fictional patterns.

Examples:

```text
synthetic-user-001
synthetic-device-014
synthetic-event-2026-0001
synthetic-beneficiary-007
```

Avoid identifiers that look like real:

* bank-account numbers;
* card numbers;
* government identifiers;
* customer IDs;
* transaction references;
* production UUIDs copied from logs.

Randomly generated UUIDs may be used when they are generated specifically for this project.

## Synthetic Monetary Data

All amounts must be fictional.

Examples may use public currency codes such as:

```text
USD
EUR
GBP
```

Amounts and thresholds must be chosen only to demonstrate application behavior and must not represent real financial-institution policies.

## Synthetic Risk Rules

Every rule must be created specifically for this project.

Each rule must:

* have a generic name;
* use a fictional threshold;
* provide a clear explanation;
* avoid references to proprietary vendors or institutions;
* be documented as a demonstration rule;
* remain deterministic for the MVP.

Example:

```text
A transaction above a fictional demonstration threshold adds a synthetic score impact.
```

The repository must not imply that these rules are sufficient for production fraud prevention.

## Test Data

Automated tests must use fixtures or builders that produce synthetic data.

Test data must not be copied from:

* production logs;
* support tickets;
* screenshots;
* databases;
* network traces;
* internal documents;
* employer or client test environments.

Tests should use obvious fictional values so their purpose is clear during review.

## Logging

Application logs must avoid storing unnecessary event content.

The MVP should prefer logging:

* event identifiers;
* evaluation identifiers;
* event type;
* decision;
* evaluation status;
* technical error categories.

The application should avoid logging complete payloads, even when the data is synthetic, to model safer logging practices.

## Secrets and Local Configuration

Secrets must never be committed.

Local configuration should use environment variables or ignored local files.

The repository may include an `.env.example` file only when:

* it contains placeholder values;
* it contains no active credentials;
* each variable is documented;
* the example can be safely published.

## Screenshots and Demonstrations

Screenshots included in documentation must show only:

* the public repository;
* the locally running application;
* synthetic events;
* fictional decisions;
* public development tools without private information.

Before committing a screenshot, verify that it does not expose:

* browser bookmarks;
* open tabs containing private systems;
* usernames;
* local filesystem paths containing personal information;
* tokens or credentials;
* private repository names;
* employer or client information.

## Review Checklist

Before opening a pull request, verify:

* [ ] All included data is synthetic.
* [ ] No secrets or credentials are present.
* [ ] No employer or client names are used in implementation examples.
* [ ] No private source code or configuration has been copied.
* [ ] Risk rules and thresholds are fictional.
* [ ] Test fixtures are independently created.
* [ ] Logs and screenshots contain no sensitive information.
* [ ] Documentation does not reveal confidential implementation details.

## Incident Response

If prohibited or sensitive information is committed:

1. Stop further distribution of the affected content.
2. Remove the information from the active branch.
3. Rotate any exposed credentials immediately.
4. Determine whether Git history must be rewritten.
5. Document the remediation without repeating the sensitive value.
6. Review the workflow that allowed the information to be introduced.

Deleting a file in a later commit may not remove it from Git history. Exposed secrets must always be considered compromised.

## Disclaimer

This policy reduces the risk of exposing confidential or personal information, but it does not make the project suitable for production financial use.

Any production implementation would require formal privacy, security, legal, compliance, data-governance, and risk reviews.
