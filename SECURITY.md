# Security Policy

Security is a core concern of the Fraud Risk Decision Engine.

This repository is a public educational and professional portfolio project. It is not intended to process real financial information or make production fraud decisions.

## Supported Versions

The project is currently under initial development.

Security fixes will be applied only to the latest version available on the `main` branch and to the most recent published release.

| Version        | Supported |
| -------------- | --------- |
| Latest release | Yes       |
| `main` branch  | Yes       |
| Older releases | No        |

## Reporting a Vulnerability

Do not report suspected security vulnerabilities through a public GitHub Issue, pull request, discussion, or comment.

Use the repository's private vulnerability-reporting feature when it is available.

When reporting a vulnerability, include:

* a clear description of the issue;
* the affected component or endpoint;
* the steps required to reproduce it;
* the potential security impact;
* any suggested mitigation;
* synthetic proof-of-concept data only.

Do not include:

* real customer or employee information;
* real account, card, transaction, device, or session data;
* active credentials, tokens, certificates, or secrets;
* information obtained from private employer or client systems;
* proprietary source code or internal documentation.

## Response Process

A reported vulnerability will be reviewed to determine:

1. whether it can be reproduced;
2. which project components are affected;
3. its potential confidentiality, integrity, or availability impact;
4. whether an immediate mitigation is required;
5. whether documentation, tests, or architectural changes are needed.

Confirmed vulnerabilities should be resolved through a private remediation process before public disclosure whenever possible.

## Security Scope

Security work for this project may include:

* request validation;
* safe error handling;
* idempotency controls;
* audit-record integrity;
* dependency management;
* secret detection;
* secure configuration defaults;
* authorization boundaries;
* logging and data-minimization practices;
* automated security checks;
* threat modeling.

## Out of Scope

The following are outside the current project scope:

* attacks against real financial institutions;
* testing private or production systems;
* social engineering;
* denial-of-service testing against public infrastructure;
* reports based exclusively on unsupported or obsolete versions;
* vulnerabilities in third-party services that are not caused by this repository;
* findings that require real financial or personal data.

## Data and Confidentiality

All security tests, examples, payloads, screenshots, and demonstrations must use fictional and synthetic information created specifically for this repository.

No confidential information from employers, clients, vendors, or financial institutions may be included.

## Production Disclaimer

This project does not claim compliance with any banking, financial, privacy, security, or regulatory standard.

A production-grade fraud decision system would require additional controls, including authentication, authorization, encryption, monitoring, availability engineering, privacy governance, regulatory review, incident response, and independent security assessment.
