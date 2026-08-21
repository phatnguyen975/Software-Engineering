---
name: api-contract
description: Analyze SUT documentation to produce a detailed, production-grade API Contract for a single endpoint. Use this skill when you need to define the authoritative contract for an API — including field constraints, business rules, state transitions, error responses, and security rules — before designing test cases or OpenAPI specs. Trigger this skill whenever the user mentions "contract", "api contract", "define API behavior", "document API rules", or when they want to formally specify what an endpoint should do before testing it.
---

# `api-contract` Skill

## Overview

Produce a complete, production-grade **API Contract** for a single REST endpoint. The contract is the authoritative source of truth describing every behavioral aspect of that endpoint: request schema, field constraints, business rules, all possible responses, state transitions, and applicable security rules.

When the input documentation is sparse or incomplete, the skill enriches the contract using verified industry best practices for the given system type. Every inferred detail is clearly scoped — human review confirms or overrides it.

**Primary output:** `CONTRACT.md` — a structured Markdown document following the template in [`assets/CONTRACT_TEMPLATE.md`](assets/CONTRACT_TEMPLATE.md).

## When to Use

- Before designing test cases for an endpoint
- Before generating an OpenAPI/Swagger specification
- When existing API documentation is incomplete, ambiguous, or only covers the happy path
- When onboarding to an unfamiliar codebase and needing a formal behavioral specification

## When NOT to Use

- When a complete, already-verified contract exists for this endpoint — do not regenerate it
- When the task is to run tests, generate collections, or write OpenAPI YAML — those are separate concerns
- When the endpoint is internal infrastructure (health checks, metrics scrapers) with no business logic to document

## Inputs

| Name              | Type       | Required | Description                                                                                                                        |
| ----------------- | ---------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| `api_endpoint`    | `string`   | ✅       | HTTP method + path. Example: `POST /api/register`                                                                                  |
| `input_docs`      | `string[]` | ✅       | Array of file paths to analyze: API specs, SRS, database schema, etc.                                                              |
| `output_dir`      | `string`   | ✅       | Directory where `CONTRACT.md` will be written                                                                                      |
| `system_type`     | `string`   | ❌       | Domain context for inference. Examples: `e-commerce`, `fintech`, `healthcare`, `social`. Required when `allow_inference` is `true` |
| `allow_inference` | `boolean`  | ❌       | When `true`, gaps in documentation are filled from real-world best practices for `system_type`. Default: `false`                   |

**Validation rules — reject and ask the user to correct before proceeding:**

- `api_endpoint` must include both method and path (e.g., `POST /api/register`, not just `/api/register`)
- `input_docs` must be a non-empty array; each path must exist on disk
- `output_dir` must be a valid writable path
- If `allow_inference` is `true`, `system_type` must also be provided

## Output

| File          | Location                   | Description               |
| ------------- | -------------------------- | ------------------------- |
| `CONTRACT.md` | `{output_dir}/CONTRACT.md` | The complete API contract |

See [`assets/CONTRACT_TEMPLATE.md`](assets/CONTRACT_TEMPLATE.md) for the exact structure and all required sections.

## Core Principles

1. **Source over assumption.** Always prefer explicit evidence from `input_docs` over inference. Inference is a last resort when `allow_inference = true`.
2. **Completeness over brevity.** A contract that omits an error case or a constraint will produce incomplete test cases downstream. Be thorough.
3. **No fabrication.** If a behavior cannot be confirmed from `input_docs` or from established industry standards, mark it as unknown — do not invent a value.
4. **Scope is one endpoint.** Do not mix contract details from other endpoints unless they are direct prerequisites (e.g., an auth endpoint that must be called first).

## Analysis & Drafting Process

> Follow every step in order. Do not skip steps. Read [`references/analysis-guide.md`](references/analysis-guide.md) before starting Step 2.

### Step 1 — Input Validation

Validate all inputs against the rules in the **Inputs** section above. If any validation fails, stop and ask the user to correct the input. Do not proceed until all inputs are valid.

### Step 2 — Document Ingestion

Read every file in `input_docs` in full. For each file, identify and extract:

- The target endpoint's declaration (route definition, handler function)
- Request schema: method, path params, query params, headers, body fields and their types
- Any validation logic applied to inputs
- Database interactions (INSERT, UPDATE, SELECT patterns reveal constraints and uniqueness rules)
- Authentication and authorization middleware
- All response paths: success branches, error branches, and their HTTP status codes
- Any references to business rules, state machines, or domain logic

Keep a structured extraction note per file. This feeds Step 3.

### Step 3 — Gap Analysis

Compare what was extracted against the full contract template. Identify every missing piece:

- Field constraints not stated (min/max length, regex pattern, allowed values)
- Business rules implied but not stated (uniqueness, ordering, default values)
- Specific error cases that not be handled
- Security behaviors (auth required? which roles? rate limiting?)
- State transitions (does this endpoint change any entity's status field?)
- Idempotency behavior

Remember each gap explicitly before deciding how to fill it.

### Step 4 — Gap Resolution

For each gap identified in Step 3:

- **If `allow_inference = false`:** Leave the field as `Unknown — not specified in docs`. Do not guess.
- **If `allow_inference = true`:** Apply industry best practices for `system_type`. Read [`references/inference-guide.md`](references/inference-guide.md) for sourced defaults by system type and field category. Mark every inferred value with a visible label so human review can target it precisely.

### Step 5 — Draft `CONTRACT.md`

Write the contract using the template in [`assets/CONTRACT_TEMPLATE.md`](assets/CONTRACT_TEMPLATE.md). Fill every section. For sections that are genuinely not applicable (e.g., no path parameters exist), write `N/A` — do not omit the section header.

Key authoring rules:

- **Error responses:** Use subsections with a code block per scenario, not a flat table — nested JSON does not render cleanly in tables
- **State transitions:** Cover all three kinds — HTTP response state, system data state (entity field changes), and field-level state preconditions
- **Security rules:** Map each applicable rule to its specific test vector, not just its name
- **Idempotency:** State explicitly whether repeated identical requests produce the same result or a different one (e.g., duplicate resource creation vs. idempotent update)

### Step 6 — Output Quality Review

Before presenting output to the user, verify every item in the **Contract Completeness Checklist** below. Fix any gaps found.

### Step 7 — Human Gate

Present to the user:

1. A summary table of all inferred values (if `allow_inference = true`) — one row per inferred item, with the source rationale
2. A list of fields marked `Unknown` (if `allow_inference = false`) — so the user knows what to fill in manually
3. The path to the generated `CONTRACT.md`

State clearly: **"Please review CONTRACT.md before proceeding. Confirm or correct any inferred or unknown values."**

Do not proceed to any further work until the user explicitly approves the contract.

## Contract Completeness Checklist

Run this before delivering output. Every item must be checked:

- [ ] Endpoint method and path are correct
- [ ] Auth requirement stated (yes/no, type, role)
- [ ] Idempotency stance stated
- [ ] Every request field has: type, required flag, and at least one constraint
- [ ] Every business rule has a unique ID (`BR-01`, `BR-02`, …)
- [ ] Every distinct response scenario has its own subsection with status code and body
- [ ] All three state transition types are addressed (or explicitly marked `N/A`)
- [ ] Every applicable security rule maps to a concrete test vector
- [ ] No inferred value appears without a label (when `allow_inference = true`)
- [ ] No section header is missing (use `N/A` if not applicable)

## Anti-Patterns

- **Inventing constraints.** Do not write `minLength: 8` for a password field unless you have a source — spec, or a cited industry standard.
- **Collapsing error responses into a table.** Nested JSON bodies do not render correctly in Markdown tables. Use subsections.
- **Mixing endpoints.** One invocation of this skill = one endpoint. If a prerequisite endpoint is needed, note its name — do not document its full contract here.
- **Skipping the gap analysis.** Writing the contract directly from the happy path misses the error and security cases that matter most for testing.

## Best Practices

- For database-backed APIs, the schema (column types, UNIQUE, NOT NULL, CHECK constraints) is the most reliable source of field constraints.
- For `system_type`-based inference, use only well-established references: OWASP, IETF RFCs, NIST guidelines, and widely adopted framework conventions. See [`references/inference-guide.md`](references/inference-guide.md).
- When a business rule is implicit in code (e.g., a conditional branch that returns 403), surface it as an explicit `BR-xx` rather than leaving it buried in implementation detail.
- State transitions are easy to miss. Specifically look for: `UPDATE` SQL statements, status/state field writes, and any side effects (emails sent, records created/deleted) that change system state.

## Process Quality Checklist

Verify before closing the task:

- [ ] All files in `input_docs` were read — none skipped
- [ ] Gap analysis was performed and documented before drafting
- [ ] Every gap was resolved by either evidence, inference (labeled), or `Unknown`
- [ ] Contract Completeness Checklist passed with zero unchecked items
- [ ] Human gate was presented and user approval was explicitly requested
- [ ] No other endpoint's contract details appear in this output
- [ ] No fabricated values (unsourced constraints, invented defaults) are present
- [ ] `CONTRACT.md` was written to the correct `output_dir`

## Common Rationalizations to Reject

> These are tempting shortcuts. Reject them.

- _"The spec doesn't mention rate limiting, so I won't include it."_ → Rate limiting is a security concern. If `allow_inference = true`, apply the domain default. If `false`, mark it `Unknown`.
- _"The error response is obvious — I'll just write a generic 400."_ → Each error case must be its own subsection with a specific trigger condition and body.
- _"The user said the spec is complete."_ → Specs are often incomplete for edge cases and security behaviors. Always verify against implementation.
