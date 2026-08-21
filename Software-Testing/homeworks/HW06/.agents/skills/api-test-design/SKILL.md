---
name: api-test-design
description: Design a complete, multi-dimensional test case suite for a single REST API endpoint from its approved Contract and OpenAPI spec. Covers functional/domain, state transition, security, schema validation, error handling, idempotency, and rate limiting dimensions. Trigger this skill when the user says "design test cases", "generate TCs", "write test cases for", "test design for API", or references a CONTRACT.md and wants test coverage designed from it.
---

# `api-test-design` Skill

## Overview

Design a comprehensive, production-grade **test case suite** for a single REST API endpoint, covering all applicable testing dimensions. The output is a structured Markdown file (`test-cases.md`) containing categorized, uniquely identified test cases ready for human audit and downstream execution.

The skill applies multiple testing techniques in sequence — using the `functional-test-design` skill silently as a sub-routine for functional dimensions, and dedicated instruction sets for security, schema, idempotency, and rate limiting dimensions.

**Primary output:** `test-cases.md` — see [`assets/TEST_CASES_TEMPLATE.md`](assets/TEST_CASES_TEMPLATE.md) for the exact structure.

## When to Use

- After a `CONTRACT.md` has been approved and an OpenAPI YAML file exists for the endpoint
- When starting test execution planning for an API endpoint
- When the existing test suite needs to be regenerated or significantly extended

## When NOT to Use

- When the contract has not been approved — do not design TCs from a draft contract
- When the task is to execute tests or generate a Postman collection — those are separate concerns
- When only a partial re-run of one testing dimension is needed (invoke the relevant instruction file directly instead)

## Inputs

| Name            | Type      | Required | Description                                                                              |
| --------------- | --------- | -------- | ---------------------------------------------------------------------------------------- |
| `contract_file` | `string`  | ✅       | Path to the approved `CONTRACT.md`                                                       |
| `openapi_file`  | `string`  | ✅       | Path to the OpenAPI YAML file for this endpoint                                          |
| `feature_id`    | `string`  | ✅       | Feature identifier used as TC ID prefix (e.g., `FR01`, `FR07`). Alphanumeric, no spaces. |
| `output_dir`    | `string`  | ✅       | Directory where `test-cases.md` will be written                                          |
| `min_tc_count`  | `integer` | ❌       | Minimum number of TCs required. Default: `35`                                            |

**Validation rules — reject and ask the user to correct before proceeding:**

- `contract_file` and `openapi_file` must exist on disk
- `feature_id` must be non-empty and contain no spaces or special characters
- `output_dir` must be a valid writable path
- `min_tc_count` must be a positive integer if provided

## Output

| File            | Location                     | Description                                     |
| --------------- | ---------------------------- | ----------------------------------------------- |
| `test-cases.md` | `{output_dir}/test-cases.md` | Full test case suite, categorized and auditable |

See [`assets/TEST_CASES_TEMPLATE.md`](assets/TEST_CASES_TEMPLATE.md) for the exact structure, column definitions, and ID conventions.

## Core Principles

1. **Contract is the single source of truth.** All TCs must derive from documented behavior in `contract_file` and `openapi_file`. Do not invent scenarios not grounded in the contract.
2. **No dimension skipped without explicit justification.** Every dimension in the table below must be addressed. If a dimension is not applicable (e.g., no rate limiting rule in contract), mark the section `N/A — not specified in contract` in the output.
3. **Silent execution of sub-routines.** When invoking `functional-test-design` sub-skills, do not print/save analysis steps, intermediate reasoning, or partial results. Surface only the final TC rows that follow the test cases template.
4. **Zero duplication across categories.** A TC that tests the same condition as another TC in a different category must be removed or merged. Each TC must test a distinct condition.
5. **Minimum count is a floor, not a target.** If the contract warrants more than `min_tc_count` TCs, generate them all. Artificial reduction to hit a round number is not acceptable.
6. **Data-driven eligibility must be explicit.** Every TC must be marked Yes or No for data-driven eligibility. A TC is data-driven eligible if it tests the same logic with different input values (domain partition rows).

## Testing Dimensions

Read the full instruction for each dimension before generating TCs for that dimension:

| Dimension           | Instruction                                                                        | Sub-routine                                                    | Data-driven eligible |
| ------------------- | ---------------------------------------------------------------------------------- | -------------------------------------------------------------- | -------------------- |
| Functional / Domain | [`references/domain-testing.md`](references/domain-testing.md)                     | `functional-test-design` → `domain-testing` (silent)           | ✅ Yes               |
| State Transition    | [`references/state-transition-testing.md`](references/state-transition-testing.md) | `functional-test-design` → `state-transition-testing` (silent) | Partial              |
| Security            | [`references/security-testing.md`](references/security-testing.md)                 | Standalone — not delegated                                     | Partial              |
| Schema Validation   | [`references/schema-validation.md`](references/schema-validation.md)               | Standalone — not delegated                                     | ❌ No                |
| Error Handling      | [`references/error-handling.md`](references/error-handling.md)                     | `functional-test-design` → `error-guessing` (silent)           | Partial              |
| Idempotency         | [`references/idempotency-testing.md`](references/idempotency-testing.md)           | Standalone — not delegated                                     | ❌ No                |
| Rate Limiting       | [`references/rate-limiting.md`](references/rate-limiting.md)                       | Standalone — not delegated                                     | ❌ No                |

> **Conditional dimensions:** Idempotency and Rate Limiting sections are generated **only if** the contract explicitly defines a rule for them. If absent from contract, mark section `N/A` — do not infer or assume.

## TC ID and Title Conventions

### ID Format

`TC-{feature_id}-{CATEGORY}-{zero-padded-3-digit-number}`

| Category            | Code  | Example           |
| ------------------- | ----- | ----------------- |
| Functional / Domain | `FR`  | `TC-FR01-FR-001`  |
| State Transition    | `ST`  | `TC-FR01-ST-001`  |
| Security            | `SEC` | `TC-FR01-SEC-001` |
| Schema Validation   | `SCH` | `TC-FR01-SCH-001` |
| Error Handling      | `ERR` | `TC-FR01-ERR-001` |
| Idempotency         | `IDP` | `TC-FR01-IDP-001` |
| Rate Limiting       | `RL`  | `TC-FR01-RL-001`  |

### Title Format

Every TC title must follow: **Action + Function + Operating Condition**

| ✅ Good                                             | ❌ Bad                 |
| --------------------------------------------------- | ---------------------- |
| `Register account with duplicate email`             | `Test duplicate email` |
| `Add item to cart when unauthenticated`             | `Cart auth test`       |
| `Create coupon with discount value 0`               | `Invalid discount`     |
| `Verify response schema on successful registration` | `Schema check`         |

## Test Design Process

> Follow every step in order. Do not merge steps or skip any dimension.

### Step 1 — Input Validation

Validate all inputs. If any fails, stop and ask the user to correct before proceeding.

### Step 2 — Contract and Spec Parsing

Read `contract_file` and `openapi_file` in full. Extract and organize:

- All request fields, types, constraints, and required/optional status
- All response scenarios (status codes, bodies, trigger conditions)
- All business rules (BR-xx)
- State transition definitions (all three types)
- Security rules (SEC-xx) and their test vectors
- Idempotency stance
- Rate limiting rules (if any)

### Step 3 — Functional / Domain Testing

Read [`references/domain-testing.md`](references/domain-testing.md). Invoke `functional-test-design` → `domain-testing` **silently**.

- Do not print analysis steps or intermediate output
- Collect final TC rows only

### Step 4 — State Transition Testing

Read [`references/state-transition-testing.md`](references/state-transition-testing.md). Invoke `functional-test-design` → `state-transition-testing` **silently**, instructing it to analyze all three state transition types from the contract.

- Do not print analysis steps or intermediate output
- Collect final TC rows only

### Step 5 — Security Testing

Read [`references/security-testing.md`](references/security-testing.md). Apply each applicable security rule from the contract's Security Rules section. Generate TC-SEC entries directly — do not delegate to a sub-routine.

### Step 6 — Schema Validation Testing

Read [`references/schema-validation.md`](references/schema-validation.md). Verify response structure against `openapi_file` schema definitions. Generate TC-SCH entries directly.

### Step 7 — Error Handling Testing

Read [`references/error-handling.md`](references/error-handling.md). Invoke `functional-test-design` → `error-guessing` **silently**.

- Do not print analysis steps or intermediate output
- Collect final TC rows only

### Step 8 — Idempotency Testing (conditional)

Check contract for idempotency rules. If present: read [`references/idempotency-testing.md`](references/idempotency-testing.md) and generate TC-IDP entries. If absent: mark section `N/A — idempotency not specified in contract`.

### Step 9 — Rate Limiting Testing (conditional)

Check contract for rate limiting rules. If present: read [`references/rate-limiting.md`](references/rate-limiting.md) and generate TC-RL entries. If absent: mark section `N/A — rate limiting not specified in contract`.

### Step 10 — Merge, Deduplicate, and Assign IDs

1. Combine all TC rows from Steps 3–9
2. Check for duplicate test conditions across categories — remove or merge any duplicates
3. Assign sequential IDs within each category using the convention above
4. Mark data-driven eligibility for each TC

### Step 11 — Count Verification

Count total TCs. If total < `min_tc_count`:

- Identify which dimensions have thin coverage
- Add supplementary TCs with clear justification
- Do not pad with trivially similar TCs — each addition must cover a distinct condition

### Step 12 — Output Quality Review

Run the **TC Suite Completeness Checklist** below. Fix any gaps before proceeding.

### Step 13 — Write Output

Write `test-cases.md` to `{output_dir}` following [`assets/TEST_CASES_TEMPLATE.md`](assets/TEST_CASES_TEMPLATE.md).

### Step 14 — Human Gate

Present to the user:

1. TC count summary by category
2. List of any dimensions marked N/A and why
3. List of TCs added in Step 11 (if any) with justification
4. Path to `test-cases.md`

State clearly: **"Please audit each TC and mark it VALID, INVALID, or INCOMPLETE in the Audit Log section. Add any TCs the AI missed. Confirm when done."**

Do not proceed to any further work until the user explicitly confirms audit completion.

## TC Suite Completeness Checklist

Run before writing output. Every item must pass:

- [ ] All contract fields have at least one valid-input TC and one invalid-input TC
- [ ] Every boundary value identified in domain testing has its own TC
- [ ] All three state transition types are addressed (or explicitly N/A)
- [ ] Every security rule in the contract has at least one TC
- [ ] Response schema is validated for every distinct success response shape
- [ ] Every error response scenario in the contract has at least one TC
- [ ] No two TCs test the same condition (zero duplicates across categories)
- [ ] Every TC has a title following Action + Function + Operating Condition
- [ ] Every TC has data-driven eligibility marked (Yes / No)
- [ ] Total TC count ≥ `min_tc_count`
- [ ] Audit Log section contains a row for every TC ID
- [ ] N/A sections are explicitly marked — no dimension silently omitted

## Anti-Patterns

- **Generating only happy-path TCs.** Domain testing alone must cover invalid partitions, boundary values, and missing required fields.
- **Duplicating TCs across categories.** If a TC for "missing auth token" appears in both TC-SEC and TC-ERR, merge it into TC-SEC (security intent takes precedence).
- **Inventing scenarios not in the contract.** Do not generate a TC for a business rule that does not appear in the contract. Flag it as a gap instead.
- **Printing sub-routine analysis steps.** When invoking `functional-test-design` sub-skills, output only the resulting TC rows — never the analysis narrative.
- **Marking everything as data-driven.** Security and schema TCs are almost never data-driven. Only mark Yes when input variation is the primary test variable.
- **Skipping the human gate.** The audit step is mandatory. The user must review every TC before execution.
- **Padding to meet min count.** Adding near-identical TCs to reach `min_tc_count` inflates the suite without increasing coverage. Each TC must test a distinct condition.

## Best Practices

- **Read the full contract before generating any TC.** Partial reading leads to missed business rules and incomplete error coverage.
- **Design TCs category by category, in the order of the process above.** Context from earlier categories informs later ones (e.g., knowing the valid input space from domain testing helps spot edge cases for security testing).
- **Use equivalence classes, not exhaustive enumeration.** For a field accepting 2–100 characters: one TC for length 1 (below min), one for length 2 (at min), one for length 100 (at max), one for length 101 (above max). Not one TC per character count.
- **State transition TCs must be ordered.** When the test depends on a sequence of states, the TC precondition must explicitly state the initial state and how to reach it.
- **Security TCs must reference the contract's security rule ID.** `TC-FR01-SEC-001` should cite `SEC-01` in its Security Rule column — not just a generic attack type.
- Follow [ISTQB Foundation Level Syllabus](https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf) for equivalence partitioning and boundary value analysis terminology.
- Follow [OWASP API Security Top 10 (2023)](https://owasp.org/API-Security/editions/2023/en/0x00-header/) for security test vector taxonomy.

## Process Quality Checklist

Verify before closing the task:

- [ ] All inputs validated before any TC was generated
- [ ] `contract_file` and `openapi_file` were both read in full
- [ ] Every dimension was addressed or explicitly marked N/A
- [ ] `functional-test-design` sub-skills were invoked silently — no analysis steps printed
- [ ] Idempotency and rate limiting were only generated if contract specified them
- [ ] Duplicate TCs were removed after merge step
- [ ] TC Suite Completeness Checklist passed with zero unchecked items
- [ ] Human gate was presented and audit explicitly requested
- [ ] No TC was generated from a scenario not grounded in the contract

## Common Rationalizations to Reject

- _"The contract doesn't mention SQL injection, so I won't include it."_ → Security rules are listed in the contract's Security Rules section. If a rule is listed there, generate a TC for it. If the section is empty, mark TC-SEC as N/A.
- _"I'll combine valid and invalid inputs in one TC to save space."_ → Each TC must test exactly one condition. Combining inputs makes failures ambiguous.
- _"I already covered error handling in domain testing, so I'll skip TC-ERR."_ → Error Handling TCs focus on malformed requests, missing fields, and wrong types — distinct from domain partition boundaries. Both categories are required.
- _"I'll just generate 35 TCs and stop."_ → `min_tc_count` is a floor. If the contract warrants 50 TCs for full coverage, generate 50.
- _"The sub-routine printed its analysis, so I'll include it in the output."_ → Never include sub-routine intermediate output. The user receives TC rows only.
