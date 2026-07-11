# Contributing

Thank you for your interest in the Fraud Risk Decision Engine.

This project is developed incrementally through GitHub Issues, dedicated branches, pull requests, automated checks, and documented technical decisions.

## Development Principles

Contributions must preserve the following principles:

* use only synthetic and fictional data;
* keep risk decisions deterministic and explainable;
* separate domain logic from infrastructure concerns;
* prefer small, reviewable changes;
* include automated tests for relevant behavior;
* document significant architectural decisions;
* avoid unnecessary complexity.

## Confidentiality Requirements

Do not include:

* proprietary source code;
* internal documentation;
* real customer, employee, account, card, device, transaction, or session data;
* private endpoints, credentials, secrets, certificates, or tokens;
* employer-specific or client-specific configurations;
* proprietary fraud rules or risk thresholds;
* screenshots from private systems;
* confidential implementation details.

All examples and test data must be created specifically for this repository.

## Contribution Workflow

1. Select or create a GitHub Issue.
2. Create a dedicated branch from the latest `main` branch.
3. Implement only the scope defined in the issue.
4. Add or update tests when application behavior changes.
5. Run the relevant local checks.
6. Commit the changes using a clear conventional commit message.
7. Push the branch to GitHub.
8. Open a pull request.
9. Link the pull request to the corresponding issue.
10. Merge only after the acceptance criteria are satisfied.

## Branch Naming

Use the following format:

```text
<type>/<issue-number>-<short-description>
```

Examples:

```text
chore/1-repository-foundation
feat/2-backend-bootstrap
fix/15-idempotency-conflict
docs/21-update-threat-model
test/24-add-rule-engine-tests
```

## Commit Messages

Use conventional commit prefixes:

```text
feat: add a new capability
fix: correct application behavior
docs: update documentation
test: add or update tests
refactor: improve structure without changing behavior
chore: update tooling or repository configuration
ci: update continuous integration workflows
```

Examples:

```text
chore: establish repository foundation
feat: add authentication risk evaluation
test: cover transaction scoring rules
docs: document fail-closed policy
```

## Pull Requests

Each pull request should:

* address a focused scope;
* reference its GitHub Issue;
* describe the implemented changes;
* explain how the changes were validated;
* identify relevant security or architectural considerations;
* avoid unrelated modifications.

Use `Closes #<issue-number>` when the pull request fully resolves an issue.

## Testing Expectations

Application changes should include tests at the appropriate level.

The project will use:

* JUnit for backend tests;
* Vitest for frontend unit tests;
* React Testing Library for frontend component behavior.

Tests must use synthetic data and must not depend on private systems or external production services.

## Documentation Expectations

Update documentation when a change affects:

* public API behavior;
* architecture;
* domain concepts;
* security assumptions;
* data handling;
* development or deployment procedures.

Significant technical decisions should be documented as Architecture Decision Records under `docs/adr`.

## Code Review

Reviewers should verify:

* correctness;
* readability;
* test coverage;
* explainability of risk behavior;
* adherence to the synthetic-data policy;
* absence of confidential information;
* consistency with the issue acceptance criteria.

## Security

Do not report security vulnerabilities through a public GitHub Issue.

Follow the instructions defined in `SECURITY.md`.
