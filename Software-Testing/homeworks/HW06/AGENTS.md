# AGENTS.md — EShop API Testing Project

> This file is the project-specific configuration for the AI agent working on this repository. It maps the SUT, documentation, skills, and workflow for the API Testing assignment. All general skill behavior is defined in `.agents/skills/*/SKILL.md` — this file provides the project context that makes those skills actionable.

## Project Overview

- **Project name:** API Testing with AI Agent
- **SUT name:** EShop
- **Description:** A Vietnamese e-commerce demo application for software testing practice. Provides REST APIs for user registration, product browsing, shopping cart, checkout, coupon management, and admin operations.
- **Base URL (local):** `http://localhost:3000`
- **API documentation UI:** `http://localhost:3000/api-docs`

## Tech Stack

| Layer        | Technology                       | Notes                                                                       |
| ------------ | -------------------------------- | --------------------------------------------------------------------------- |
| Runtime      | Node.js                          | CommonJS modules (`"type": "commonjs"` in `backend/package.json`)           |
| Framework    | Express 5.x                      | `express@^5.2.1`                                                            |
| Database     | SQLite 3                         | File-based, located at `backend/database.js` — DB file created at first run |
| Auth         | JWT                              | `jsonwebtoken@^9.0.3`                                                       |
| Body parsing | `body-parser@^2.2.2`             | JSON body parsing                                                           |
| CORS         | `cors@^2.8.6`                    | All origins allowed                                                         |
| API docs     | `swagger-ui-express` + `js-yaml` | Loads YAML files from `docs/openapi/` dynamically                           |

**Important behavioral notes:**

- No input validation middleware is used — validation (or lack thereof) is implemented per-handler
- SQLite constraint errors (e.g., UNIQUE violation) may propagate as raw 500 errors rather than structured 4xx responses
- No rate limiting is implemented on any endpoint

## Documentation Map

| File                  | Path                                            | Purpose                                                                | When to use                                                                                       |
| --------------------- | ----------------------------------------------- | ---------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------- |
| API Specification     | `docs/sut/api-spec.md`                          | Raw API spec: endpoints, request/response examples, brief descriptions | First reference when creating a contract — provides endpoint listing and basic schema             |
| Security Requirements | `docs/sut/srs.md`                               | Security rules SEC-01 through SEC-07 and functional requirements       | Use when populating Section 6 (Security Rules) of a contract and when designing TC-SEC test cases |
| API Contract          | `docs/apis/{endpoint-slug}/CONTRACT.md`         | Authoritative behavioral spec for one endpoint                         | Primary input for test case design and bug report expected results                                |
| OpenAPI Spec (index)  | `docs/openapi/openapi.yaml`                     | Index file referencing all path and component YAMLs                    | Used by Swagger UI; reference for schema validation TC design                                     |
| OpenAPI Path files    | `docs/openapi/paths/{category}/{api}.yaml`      | Endpoint-specific OpenAPI schema                                       | Use when designing TC-SCH schema validation test cases                                            |
| Test Cases            | `docs/apis/{endpoint-slug}/test-cases.md`       | Full TC suite with audit log and actual results                        | Primary input for collection building and bug report generation                                   |
| Bug Report            | `docs/apis/{endpoint-slug}/bug-report.md`       | Grouped bug entries from failed TCs                                    | Reference when creating GitHub Issues                                                             |
| Newman Summary        | `docs/apis/{endpoint-slug}/newman-summary.json` | Raw Newman execution output                                            | Parsed by `api-newman` skill to populate TC actual results                                        |

**Decision guide — which doc to read first:**

```
Starting contract for an endpoint?
  → Read docs/sut/api-spec.md (endpoint overview) + docs/sut/srs.md (security rules)

Designing test cases?
  → Read docs/apis/{slug}/CONTRACT.md + docs/openapi/paths/{category}/{api}.yaml

Expected result for a bug?
  → Read docs/apis/{slug}/CONTRACT.md — always verify against contract, not TC

Security test vectors?
  → Read docs/sut/srs.md → map SEC-01 through SEC-07 to contract Section 6
```

## APIs in Scope

| #   | Method | Path                 | Feature ID | Pool | Endpoint Slug        | Contract path                              | TC path                                      |
| --- | ------ | -------------------- | ---------- | ---- | -------------------- | ------------------------------------------ | -------------------------------------------- |
| 1   | POST   | `/api/register`      | FR-01      | A    | `post-register`      | `docs/apis/post-register/CONTRACT.md`      | `docs/apis/post-register/test-cases.md`      |
| 2   | POST   | `/api/cart`          | FR-07      | B    | `post-cart`          | `docs/apis/post-cart/CONTRACT.md`          | `docs/apis/post-cart/test-cases.md`          |
| 3   | POST   | `/api/admin/coupons` | FR-17      | C    | `post-admin-coupons` | `docs/apis/post-admin-coupons/CONTRACT.md` | `docs/apis/post-admin-coupons/test-cases.md` |

**Feature ID mapping for TC and Bug IDs:**

| Endpoint Slug        | feature_id (used in TC IDs and Bug IDs) |
| -------------------- | --------------------------------------- |
| `post-register`      | `FR01`                                  |
| `post-cart`          | `FR07`                                  |
| `post-admin-coupons` | `FR17`                                  |

## Skills Catalog

| Skill                    | One-line function                                                      | Invoke when                                                                       |
| ------------------------ | ---------------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| `api-contract`           | Analyze SUT docs and produce a detailed API Contract                   | Starting work on a new endpoint                                                   |
| `api-openapi`            | Convert an approved CONTRACT.md to OpenAPI 3.0 YAML                    | CONTRACT.md has been approved                                                     |
| `api-test-design`        | Design a full TC suite from CONTRACT.md and OpenAPI YAML               | CONTRACT.md and OpenAPI YAML are both approved                                    |
| `api-collection`         | Generate Postman collection JSON and data files from test-cases.md     | test-cases.md has been audited and approved                                       |
| `api-newman`             | Execute collection via Newman, update TC actual results                | Collection has been reviewed and approved                                         |
| `api-postman`            | Sync collections to Postman cloud via MCP                              | Newman verification is complete                                                   |
| `api-bug-report`         | Analyze FAIL TCs and produce a structured bug report                   | TC actual results are populated and human has confirmed which FAILs are real bugs |
| `functional-test-design` | Sub-routine for domain, state transition, and error guessing TC design | Invoked silently by `api-test-design` — do not invoke directly                    |

**Correct invocation order for one complete API:**

```
api-contract → api-openapi → api-test-design → api-collection → api-newman → api-postman → api-bug-report
```

Each step requires human approval before the next begins.

## Workflow

Process one API at a time, in the order listed in APIs in Scope. Complete all steps for API 1 before starting API 2.

### Step 1 — Contract (`api-contract`)

**Invoke with:**

```
api_endpoint:     "{METHOD} {PATH}"
input_docs:       ["docs/sut/api-spec.md", "docs/sut/srs.md"]
output_dir:       "docs/apis/{endpoint-slug}/"
system_type:      "e-commerce"
allow_inference:  true
```

**Output:** `docs/apis/{endpoint-slug}/CONTRACT.md`

**Human gate — review before proceeding:**

- All inferred values are reasonable for an e-commerce system
- Business rules are complete — no gaps in uniqueness, defaults, or conditional rules
- All three state transition types are addressed (or explicitly N/A)
- Security rules map correctly to SEC-01 through SEC-07 from `docs/sut/srs.md`
- ✅ Approve contract → commit: `docs: add contract for {endpoint}`

### Step 2 — OpenAPI (`api-openapi`)

**Invoke with:**

```
contract_file:  "docs/apis/{endpoint-slug}/CONTRACT.md"
output_dir:     "docs/openapi/"
api_tag:        "{Authentication | Cart | Admin}"
```

**Output:** `docs/openapi/paths/{category}/{api}.yaml` + updated `docs/openapi/openapi.yaml`

**Human gate — review before proceeding:**

- Restart backend server: `cd backend && node server.js`
- Verify `http://localhost:3000/api-docs` shows the new endpoint correctly
- Schema types and constraints match the contract
- Required fields are in the `required` array
- Standard error responses (`$ref`) are used for 400, 401, 403, etc.
- ✅ Approve OpenAPI → commit: `docs: add OpenAPI spec for {endpoint}`

### Step 3 — Test Case Design (`api-test-design`)

**Invoke with:**

```
contract_file:   "docs/apis/{endpoint-slug}/CONTRACT.md"
openapi_file:    "docs/openapi/paths/{category}/{api}.yaml"
feature_id:      "{FR01 | FR07 | FR17}"
output_dir:      "docs/apis/{endpoint-slug}/"
min_tc_count:    35
```

**Output:** `docs/apis/{endpoint-slug}/test-cases.md`

**Human gate — review before proceeding:**

- Audit every TC: mark VALID / INVALID / INCOMPLETE in Audit Log
- Correct or remove INVALID TCs
- Complete INCOMPLETE TCs
- Add at least 5 manual TCs the AI missed (especially security and state transition edge cases)
- Record why the AI missed them (for AI Critique section of final report)
- Total TC count ≥ 40 after manual additions
- ✅ Approve test cases → commit: `test: add test cases for {endpoint}`

### Step 4 — Collection Building (`api-collection`)

**Invoke with:**

```
tc_file:              "docs/apis/{endpoint-slug}/test-cases.md"
contract_file:        "docs/apis/{endpoint-slug}/CONTRACT.md"
output_dir:           "postman/"
feature_id:           "{fr01 | fr07 | fr17}"
inject_header_name:   "X-Student-Id"
inject_header_value:  "{YOUR_STUDENT_ID}"
base_url_var:         "baseUrl"
```

**Output:**

- `postman/collections/{feature_id}/collection.json`
- `postman/collections/{feature_id}/data-domain.csv`
- `postman/collections/{feature_id}/data-security.json` (if needed)
- Updated `postman/environments/local.json`

**Human gate — review before proceeding:**

- Open `postman/collections/{feature_id}/collection.json` and verify:
  - `X-Student-Id` header appears in Collection-level Pre-request Script
  - Auth flow (login) is correct for endpoint's auth requirement
  - Folder setup/teardown uses `pm.sendRequest()`, not visible requests
  - All data-driven TCs use `{{variable}}` placeholders
  - Test assertions match Expected Result column in `test-cases.md`
- ✅ Approve collection → commit: `feat: add Postman collection for {endpoint}`

### Step 5 — Newman Verification (`api-newman`)

**Ensure SUT is running before invoking:** `cd backend && node server.js`

**Invoke with:**

```
collection_file:    "postman/collections/{feature_id}/collection.json"
environment_file:   "postman/environments/local.json"
summary_dir:        "docs/apis/{endpoint-slug}/"
report_output:      "postman/reports/{feature_id}-report.html"
tc_file:            "docs/apis/{endpoint-slug}/test-cases.md"
data_file:          "postman/collections/{feature_id}/data-domain.csv"
```

**Output:**

- `docs/apis/{endpoint-slug}/newman-summary.json`
- `postman/reports/{feature_id}-report.html`
- Updated `docs/apis/{endpoint-slug}/test-cases.md` with Actual Result and Status

**Human gate — review before proceeding:**

- Review every FAIL result in the updated `test-cases.md`
- Classify each FAIL as: (1) real SUT bug, (2) collection script error, (3) environment issue
- For collection script errors: fix the collection and re-run before committing
- For real bugs: keep the FAIL — it will be picked up by `api-bug-report`
- Verify `X-Student-Id` header appears in Newman CLI output (pre-request console log)
- Verify Newman report hostname is `localhost` or `127.0.0.1`
- ✅ Confirm classifications → commit: `test: verified Newman run for {endpoint}`

### Step 6 — Postman App Setup (`api-postman`)

> Run this step once after all 3 APIs are complete, not per-API.

**Invoke with:**

```
workspace_name:    "API Testing"
collection_files:  [
  "postman/collections/fr01/collection.json",
  "postman/collections/fr07/collection.json",
  "postman/collections/fr17/collection.json"
]
environment_file:  "postman/environments/local.json"
create_mock:       true
create_monitor:    true
```

**Output:** Resources created on Postman cloud (workspace, 3 collections, environment, mock server, monitor)

**Human gate — actions required in Postman App:**

- Open Postman App, verify workspace and all 3 collections are visible
- Run each collection using Collection Runner with `local` environment and data file
- Capture screenshots of:
  - Workspace view (showing all 3 collections)
  - Collection Runner executing with data file
  - Environment variables panel (showing `baseUrl`, `studentId`, etc.)
  - Mock server dashboard
  - Monitor dashboard
  - Pre-request Script console showing `X-Student-Id` header being set
- ✅ Confirm screenshots captured

### Step 7 — Bug Report (`api-bug-report`)

> Run per-API, after Step 5 human gate is confirmed for that API.

**Invoke with:**

```
tc_file:         "docs/apis/{endpoint-slug}/test-cases.md"
contract_file:   "docs/apis/{endpoint-slug}/CONTRACT.md"
feature_id:      "{FR01 | FR07 | FR17}"
output_dir:      "docs/apis/{endpoint-slug}/"
```

**Output:** `docs/apis/{endpoint-slug}/bug-report.md`

**Human gate — review before proceeding:**

- Verify bug grouping is correct (same root cause → one bug)
- Confirm or override AI-suggested Severity and Priority for each bug
- Verify Steps to Reproduce are self-contained and accurate
- Verify Expected Results are grounded in `CONTRACT.md`
- Add screenshots to Evidence sections
- Copy each bug to GitHub Issues with screenshot attached
- ✅ Confirm GitHub Issues created → commit: `docs: add bug report for {endpoint}`

## Conventions

### File and Folder Naming

| Resource          | Convention                            | Example                                                       |
| ----------------- | ------------------------------------- | ------------------------------------------------------------- |
| API docs folder   | `{http-method}-{path-slug}`           | `post-register`, `post-cart`, `post-admin-coupons`            |
| Collection folder | `{feature_id}` (lowercase)            | `fr01`, `fr07`, `fr17`                                        |
| OpenAPI path file | `{resource}-{verb}.yaml` (kebab-case) | `register.yaml`, `cart-add.yaml`, `admin-coupons-create.yaml` |
| Newman report     | `{feature_id}-report.html`            | `fr01-report.html`                                            |
| Newman summary    | `newman-summary.json`                 | (fixed name, stored in `docs/apis/{slug}/`)                   |

### TC ID Format

```
TC-{FEATURE_ID}-{CATEGORY}-{zero-padded-3-digit}
```

| Category            | Code  | Example           |
| ------------------- | ----- | ----------------- |
| Functional / Domain | `FR`  | `TC-FR01-FR-001`  |
| State Transition    | `ST`  | `TC-FR01-ST-001`  |
| Security            | `SEC` | `TC-FR01-SEC-001` |
| Schema Validation   | `SCH` | `TC-FR01-SCH-001` |
| Error Handling      | `ERR` | `TC-FR01-ERR-001` |
| Idempotency         | `IDP` | `TC-FR01-IDP-001` |
| Rate Limiting       | `RL`  | `TC-FR01-RL-001`  |

### Bug ID Format

```
BUG-{FEATURE_ID}-{zero-padded-3-digit}
```

Example: `BUG-FR01-001`, `BUG-FR07-003`

### Custom Header

Every request in every collection must include:

```
X-Student-Id: {YOUR_STUDENT_ID}
```

Injected via Collection-level Pre-request Script using `pm.request.headers.add()`. The value is read from `pm.environment.get("studentId")`.

### TC Title Format

```
{Action} + {Function} + {Operating Condition}
```

Example: `Register account with duplicate email` / `Add item to cart when unauthenticated`

## Guardrails

The following actions are **absolutely prohibited** without explicit human instruction:

| Prohibited action                                                     | Reason                                               |
| --------------------------------------------------------------------- | ---------------------------------------------------- |
| `git add`, `git commit`, `git push`                                   | Human controls all git operations                    |
| Deleting any file                                                     | Irreversible — always ask first                      |
| Modifying `collection.json` after Newman verification                 | Collection is source of truth post-verification      |
| Modifying `CONTRACT.md` after it is approved                          | Contract is locked once approved                     |
| Modifying `test-cases.md` columns other than Status and Actual Result | Only `api-newman` skill may update those two columns |
| Overwriting `postman/environments/local.json`                         | Must merge — never replace                           |
| Proceeding to next workflow step without human approval               | Human gate is mandatory at every step                |
| Invoking `functional-test-design` skill with visible output           | Must be invoked silently — no analysis steps printed |
| Running Newman against an unverified collection                       | Collection must be human-approved before execution   |
| Creating GitHub Issues                                                | Human creates all GitHub Issues manually             |

## Environment Variables

Variables required in `postman/environments/local.json`:

| Variable        | Type    | Description                                     | Example value            |
| --------------- | ------- | ----------------------------------------------- | ------------------------ |
| `baseUrl`       | static  | Base URL of the SUT                             | `http://localhost:3000`  |
| `userEmail`     | static  | Email for test user account                     | `test@eshop.com`         |
| `userPassword`  | static  | Password for test user account                  | `Test1234!`              |
| `adminEmail`    | static  | Email for admin account                         | `admin@eshop.com`        |
| `adminPassword` | static  | Password for admin account                      | `Admin123!`              |
| `studentId`     | static  | Student ID injected as `X-Student-Id` header    | Your actual student ID   |
| `userToken`     | runtime | JWT token for user — set by Pre-request Script  | (empty — set at runtime) |
| `adminToken`    | runtime | JWT token for admin — set by Pre-request Script | (empty — set at runtime) |

**Runtime variables** (set by collection scripts during execution — do not set manually):

| Variable        | Set by                                      | Unset by                           |
| --------------- | ------------------------------------------- | ---------------------------------- |
| `userToken`     | Collection Pre-request Script (login)       | Not unset — reused across requests |
| `adminToken`    | Collection Pre-request Script (admin login) | Not unset — reused across requests |
| `prerequisite*` | Folder Pre-request Script (setup)           | Folder Test Script (teardown)      |

## Known Constraints

These SUT limitations are known and should inform test design and result interpretation:

| Constraint                                                           | Impact on testing                                                                  |
| -------------------------------------------------------------------- | ---------------------------------------------------------------------------------- |
| No rate limiting implemented                                         | TC-RL sections will be N/A for all 3 APIs                                          |
| JWT secret is hardcoded and public                                   | Forged tokens can be created — useful for auth bypass TCs                          |
| SQLite UNIQUE constraint violations may return 500 instead of 409    | Duplicate email/coupon code TCs may produce unexpected status codes — expected bug |
| No input validation middleware — validation is per-handler or absent | Many validation TCs may pass through and cause DB errors — potential bugs          |
| Cart is stored in-memory (array in `server.js`, not in DB)           | Cart state does not persist across server restarts; teardown may be unnecessary    |
| Swagger UI is only available after `docs/openapi/` files are created | `localhost:3000/api-docs` returns 404 until at least one YAML file exists          |

## Human Gate Checklist

Quick reference — every gate must be explicitly confirmed before AI proceeds:

| Step            | Gate                                 | What to confirm                                                         |
| --------------- | ------------------------------------ | ----------------------------------------------------------------------- |
| 1 — Contract    | After `CONTRACT.md` is generated     | Inferred values correct, business rules complete, security rules mapped |
| 2 — OpenAPI     | After YAML files are generated       | Swagger UI shows correctly, schema types match contract                 |
| 3 — Test Design | After `test-cases.md` is generated   | All TCs audited, ≥5 manual TCs added, total ≥40                         |
| 4 — Collection  | After `collection.json` is generated | Header injection, auth flow, assertions, data files all correct         |
| 5 — Newman      | After Newman run completes           | FAIL classifications confirmed (bug / script error / env issue)         |
| 6 — Postman     | After cloud setup via MCP            | Workspace verified in Postman App, screenshots captured                 |
| 7 — Bug Report  | After `bug-report.md` is generated   | Severity/priority confirmed, GitHub Issues created with screenshots     |
