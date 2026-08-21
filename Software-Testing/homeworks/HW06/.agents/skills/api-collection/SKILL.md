---
name: api-collection
description: Generate a production-ready Postman collection JSON, data files, and environment updates from an audited test-cases.md file. Handles setup/teardown at collection, folder, and request levels. Trigger this skill when the user says "generate collection", "create Postman collection", "build collection from test cases", or "create collection.json".
---

# `api-collection` Skill

## Overview

Generate a complete, executable **Postman collection** and associated data files from an audited test case suite. The collection is structured for immediate execution via Newman CLI or Postman Collection Runner — no manual editing required.

The skill handles:

- Collection-level auth setup (auto-login pre-request script)
- Custom header injection on every request
- Folder-level setup/teardown for prerequisite data management
- Request-level assertions mapping every TC's expected result
- Data files (CSV/JSON) for data-driven TCs
- Environment variable merge into the shared `local.json`

**Primary outputs:**

- `{output_dir}/collections/{feature_id}/collection.json`
- `{output_dir}/collections/{feature_id}/data-domain.csv` (when data-driven TCs exist)
- `{output_dir}/collections/{feature_id}/data-security.json` (when parameterized security TCs exist)
- Updated `{output_dir}/environments/local.json` (merge only)

See [`references/collection-structure.md`](references/collection-structure.md) for the full JSON structure and [`references/scripting-guide.md`](references/scripting-guide.md) for Pre-request and Test Script patterns.

## When to Use

- After a `test-cases.md` has been fully audited (all TCs marked VALID or INCOMPLETE resolved) and is ready for execution
- When generating a fresh collection for a new endpoint
- When regenerating a collection after significant TC changes

## When NOT to Use

- When the `test-cases.md` still has TCs marked INVALID or unresolved INCOMPLETE in Final Status — fix the TCs first
- When the task is to run the collection — that is handled separately
- When only minor edits to an existing collection are needed — edit the JSON directly rather than regenerating

## Inputs

| Name                  | Type     | Required | Description                                                                 |
| --------------------- | -------- | -------- | --------------------------------------------------------------------------- |
| `tc_file`             | `string` | ✅       | Path to the audited `test-cases.md`                                         |
| `contract_file`       | `string` | ✅       | Path to the approved `CONTRACT.md` — used to determine setup/teardown needs |
| `output_dir`          | `string` | ✅       | Root Postman directory (e.g., `postman/`)                                   |
| `feature_id`          | `string` | ✅       | Used to name the collection subfolder under `collections/`                  |
| `inject_header_name`  | `string` | ❌       | Name of a custom header to inject into every request (e.g., `X-Student-Id`) |
| `inject_header_value` | `string` | ❌       | Value for the custom header. Required if `inject_header_name` is provided   |
| `base_url_var`        | `string` | ❌       | Environment variable name for the base URL. Default: `baseUrl`              |

**Validation rules — reject and ask the user to correct before proceeding:**

- `tc_file` must exist and must not contain any TCs marked INVALID
- `contract_file` must exist
- `output_dir` must be a valid writable path
- `feature_id` must be non-empty with no spaces
- If `inject_header_name` is provided, `inject_header_value` must also be provided

## Outputs

| File                 | Location                                 | Notes                                              |
| -------------------- | ---------------------------------------- | -------------------------------------------------- |
| `collection.json`    | `{output_dir}/collections/{feature_id}/` | Full Postman collection                            |
| `data-domain.csv`    | `{output_dir}/collections/{feature_id}/` | Only created when data-driven TCs exist            |
| `data-security.json` | `{output_dir}/collections/{feature_id}/` | Only created when parameterized security TCs exist |
| `local.json`         | `{output_dir}/environments/`             | Merged — new variables added, existing preserved   |

See [`assets/collection-skeleton.json`](assets/collection-skeleton.json) for the base collection JSON structure.

## Core Principles

1. **One collection per feature, one request per TC.** Every TC in `test-cases.md` maps to exactly one request in the collection. No TC is skipped; no extra requests are added.
2. **Setup via `pm.sendRequest()`, not extra requests.** Prerequisite data creation (e.g., creating a product before testing cart) is done inside Folder Pre-request Scripts using `pm.sendRequest()` — never as standalone requests in the collection.
3. **Teardown only when the contract has a delete endpoint.** Never create a teardown request for an endpoint not documented in the contract.
4. **Runtime variables are unset after teardown.** Any `pm.environment.set()` variable created during setup must be unset in the Folder Test Script teardown.
5. **Environment file is merged, never replaced.** `local.json` is read first; only new variables are added. Existing variables and values are preserved.
6. **Assertions must match expected results exactly.** Every request's Test Script must assert the exact status code and key response body fields documented in the TC's Expected Result column.

## Collection Building Process

> Follow every step in order. Read [`references/collection-structure.md`](references/collection-structure.md) and [`references/scripting-guide.md`](references/scripting-guide.md) before starting Step 3.

### Step 1 — Input Validation

Validate all inputs per the rules above. Stop and ask the user to fix any invalid input before proceeding.

### Step 2 — TC and Contract Analysis

1. Read `tc_file` — parse all TC rows where `Final Status` is `VALID` by category (TC-FR, TC-ST, TC-SEC, TC-SCH, TC-ERR, TC-IDP, TC-RL)
2. Classify each TC as data-driven or hardcoded (from the `Data-driven?` column)
3. Identify dependencies: which TCs require a previous TC's response data (e.g., an ID from a create response)
4. Read `contract_file` — identify:
   - Auth requirements (determines Collection-level Pre-request Script)
   - Prerequisite resources needed before test execution (e.g., existing product for cart tests)
   - Available delete/cleanup endpoints for teardown

### Step 3 — Collection Skeleton & Iteration Router Architecture

To prevent Postman from multiplying static requests by the number of CSV rows (e.g., 64 requests × 23 rows = 1472 executions), you MUST implement the **Iteration Router Architecture**:

1. Create a dummy request `[Control] Iteration Router` as the **first request** in the collection (not inside any folder).
   - Method: GET, URL: `https://postman-echo.com/get`
   - Test Script: `if (pm.info.iteration > 0) { postman.setNextRequest("Data-Driven Template"); }`
2. Create `Folder 1: Static TCs` containing subfolders for each category (`TC-FR`, `TC-ST`, etc.). Place all non-data-driven requests here (hardcoded bodies).
3. Create `Folder 2: Data-Driven TCs` containing **exactly one request** named `Data-Driven Template`.
   - Body uses `{{variable}}` placeholders.
   - Test script must dynamically assert the status code and extract `tc_id` from `pm.iterationData.get("tc_id")`.

### Step 4 — Collection-Level Pre-request Script

Write the script that runs before every request. Must include:

- **Auth setup:** If the endpoint requires auth, auto-login using `pm.sendRequest()` and store token as environment variable. Re-use existing token if already set.
- **Custom header injection:** If `inject_header_name` is provided, inject it via `pm.request.headers.add()`

See pattern in [`references/scripting-guide.md`](references/scripting-guide.md) → Section 1.

### Step 5 — Folder Pre-request and Test Scripts (Setup/Teardown)

For each TC category folder that requires setup:

1. Identify prerequisite data needed (from contract analysis in Step 2)
2. Write Folder Pre-request Script using `pm.sendRequest()` to create prerequisites, store returned IDs
3. Write Folder Test Script to teardown: delete prerequisites (only if contract has a documented delete endpoint), unset environment variables

See patterns in [`references/scripting-guide.md`](references/scripting-guide.md) → Section 2.

### Step 6 — Request Generation

For each TC in each category folder:

1. Set method, URL (using `{{baseUrl}}`), headers, and body per TC's Input column
2. For data-driven TCs: replace concrete values with `{{variable_name}}` references
3. Write Test Script asserting:
   - `pm.response.to.have.status({expected_status})`
   - Key response body field assertions from TC's Expected Result column
   - For TC-SCH: full schema assertion using `pm.response.to.have.jsonSchema({schema})`
4. For chained TCs: add `pm.environment.set("varName", pm.response.json().field)` to extract values for subsequent requests

### Step 7 — Data File Generation

For TCs marked data-driven:

- **CSV (`data-domain.csv`):** One column per variable, one row per TC partition. First row is headers.
- **JSON (`data-security.json`):** Array of objects when payload structure is complex (nested fields, special characters)

See format examples in [`references/scripting-guide.md`](references/scripting-guide.md) → Section 3.

### Step 8 — Environment Merge

1. Read `{output_dir}/environments/local.json`
2. Identify variables required by this collection but not yet in `local.json`
3. Add only new variables (with empty values for tokens, correct values for static config)
4. Write updated file — preserve all existing entries

### Step 9 — Collection Readiness Review

Run the **Collection Completeness Checklist** below. Fix any gaps before presenting to user.

### Step 10 — Human Gate

Present to the user:

1. Summary: total requests generated, data-driven TCs, folders with setup/teardown
2. List of new environment variables added to `local.json`
3. Any TCs that could not be automated (explain why) — these need manual execution
4. Path to all generated files

State clearly: **"Please review the collection before running Newman. Check setup/teardown logic, header injection, auth flow, and test assertions. Confirm when ready."**

Do not proceed until the user explicitly confirms.

## Collection Completeness Checklist

Run before delivering output. Every item must pass:

- [ ] The collection uses the Iteration Router Architecture to separate Static TCs from the Data-Driven Template
- [ ] The Data-Driven Template uses dynamic Test Scripts reading `tc_id` and `expected_status` from iteration data
- [ ] Collection-level Pre-request Script handles auth and custom header injection
- [ ] Every folder with prerequisite needs has a Folder Pre-request Script
- [ ] Every Folder Pre-request Script has a matching teardown in Folder Test Script
- [ ] Teardown only calls endpoints documented in the contract
- [ ] All runtime variables are unset in teardown
- [ ] Every request's Test Script asserts the correct status code
- [ ] TC-SCH requests assert response schema structure
- [ ] Data-driven TCs use `{{variable}}` placeholders — not hardcoded values
- [ ] `data-domain.csv` has correct headers matching all variable names used
- [ ] `local.json` was merged (not replaced) — existing variables preserved
- [ ] Collection runs in correct order: setup → TC-FR → TC-ST → TC-SEC → TC-SCH → TC-ERR → TC-IDP → TC-RL

## Anti-Patterns

- **Adding requests not in test-cases.md.** The ONLY exception is the `[Control] Iteration Router` required for workflow routing. Do not add exploratory requests.
- **Creating setup as standalone requests.** Setup calls (login, create prerequisite) belong in Pre-request Scripts using `pm.sendRequest()`, not as visible requests in the collection.
- **Writing teardown for undocumented endpoints.** If the contract does not document a DELETE endpoint, do not call one in teardown.
- **Hardcoding values in data-driven requests.** Any TC marked `Data-driven: Yes` must use `{{variable}}` — never a literal value in the request body.
- **Overwriting `local.json`.** Always read first and merge. Replacing the file destroys variables set by other collections.
- **Skipping assertions.** A request without a Test Script is a request that always "passes" regardless of the response. Every request must assert at minimum the expected status code.
- **Leaving runtime variables in environment after test.** Variables created during setup must be cleaned up in teardown to avoid polluting subsequent runs.

## Best Practices

- Name each request in the collection exactly as the TC ID + title: `TC-FR01-FR-001 — Register account with valid inputs`. This makes Newman output directly traceable to test-cases.md.
- Use `pm.environment.get("varName")` in scripts rather than `{{varName}}` — the scripting context does not interpolate `{{}}` syntax.
- For auth flows, check whether the token already exists before re-requesting it — avoids unnecessary login calls on every pre-request execution.
- Use `pm.test()` with descriptive names: `pm.test("Status is 200", ...)` not `pm.test("test1", ...)`.
- For TC-SCH assertions, use `pm.response.to.have.jsonSchema()` with a schema object derived from the OpenAPI spec — do not assert field-by-field manually.
- Follow [Postman Collection Format v2.1 specification](https://schema.postman.com/collection/json/v2.1.0/draft-07/collection.json) for valid JSON structure.
- Reference [Postman Sandbox API documentation](https://learning.postman.com/docs/writing-scripts/script-references/postman-sandbox-api-reference/) for `pm.*` scripting APIs.

## Process Quality Checklist

Verify before closing the task:

- [ ] All inputs validated before any file was created
- [ ] The collection uses the Iteration Router architecture to efficiently map TCs without iteration multiplication
- [ ] Contract was analyzed for auth requirements, prerequisites, and available delete endpoints
- [ ] Setup/teardown is present only for folders that need it and only uses documented endpoints
- [ ] Collection Completeness Checklist passed with zero unchecked items
- [ ] `local.json` was merged — not replaced
- [ ] Human gate was presented and explicit confirmation requested
- [ ] The only extra request allowed is the `[Control] Iteration Router`

## Common Rationalizations to Reject

- _"I'll add a health check request at the start to verify the server is running."_ → Not a TC. Do not add it to the collection.
- _"The teardown endpoint isn't in the contract, but I'll add it anyway to keep the DB clean."_ → Teardown only uses documented endpoints. If none exists, skip teardown and note it in the human gate summary.
- _"I'll write the setup as the first request in the folder so it's visible."_ → Setup belongs in the Folder Pre-request Script using `pm.sendRequest()`. It must not be a visible request.
- _"The test assertion is obvious, I'll just check the status code."_ → Every TC-SCH request must assert the response schema. Every TC-SEC request must assert the expected rejection behavior. Status code alone is insufficient.
- _"I'll replace local.json with just the new variables for simplicity."_ → Always merge. Replacing destroys variables needed by other collections.
