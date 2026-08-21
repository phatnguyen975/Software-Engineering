---
name: api-newman
description: Execute a Postman collection via Newman CLI, generate an HTML report, and write actual results back into test-cases.md. Acts as the orchestrating wrapper for the newman-executor agent. Trigger this skill when the user says "run Newman", "execute collection", "run tests with Newman", "generate HTML report", or "update actual results in test cases".
---

# `api-newman` Skill

## Overview

Orchestrate the execution of a Postman collection using Newman CLI and update the test case file with observed results. This skill delegates the actual CLI execution to the `newman-executor` agent, then parses the output, maps results back to TC IDs, updates the `test-cases.md` file, and presents a pass/fail summary for human review.

**This skill does not execute Newman directly** — it coordinates the `newman-executor` agent and handles all pre/post-processing: input validation, result parsing, TC file updating, and human gate.

**Primary outputs:**

- `{summary_dir}/newman-summary.json` — raw Newman execution summary (written by `newman-executor`)
- HTML report at `{report_output}` (written by `newman-executor`)
- `{tc_file}` — updated with Actual Result and Status for every TC

## When to Use

- After a collection has been reviewed and approved, and is ready for test execution
- When re-running a collection after fixing script errors or environment issues
- When the `test-cases.md` needs Actual Result and Status columns populated from a Newman run

## When NOT to Use

- When the collection has not yet been reviewed and approved — do not execute an unreviewed collection
- When the SUT is not running — verify the server is accessible at `{baseUrl}` before invoking
- When only a single request needs to be tested — use Postman GUI for one-off debugging

## Inputs

| Name               | Type     | Required | Description                                                                                                                       |
| ------------------ | -------- | -------- | --------------------------------------------------------------------------------------------------------------------------------- |
| `collection_file`  | `string` | ✅       | Path to `collection.json`                                                                                                         |
| `environment_file` | `string` | ✅       | Path to the shared environment file (e.g., `postman/environments/local.json`)                                                     |
| `summary_dir`      | `string` | ✅       | Directory where `newman-summary.json` will be saved. Use the same directory as the API's `test-cases.md` for easy cross-reference |
| `report_output`    | `string` | ✅       | Full path for the HTML report file (e.g., `postman/reports/fr01-report.html`)                                                     |
| `tc_file`          | `string` | ✅       | Path to `test-cases.md` — Actual Result and Status columns will be updated here                                                   |
| `data_file`        | `string` | ❌       | Path to CSV or JSON data file for data-driven iterations. Omit if the collection has no data-driven TCs                           |

**Validation rules — reject and ask the user to correct before proceeding:**

- `collection_file` must exist on disk
- `environment_file` must exist on disk
- `summary_dir` must be a valid writable directory path
- `report_output` must be a valid writable file path ending in `.html`
- `tc_file` must exist and contain TC rows with a Status column
- `data_file`, if provided, must exist on disk

## Outputs

| Output                  | Location          | Description                                                 |
| ----------------------- | ----------------- | ----------------------------------------------------------- |
| `newman-summary.json`   | `{summary_dir}/`  | Raw Newman execution summary — written by `newman-executor` |
| HTML report             | `{report_output}` | Human-readable test report — written by `newman-executor`   |
| Updated `test-cases.md` | `{tc_file}`       | Actual Result and Status filled in for every executed TC    |

See [`references/summary-parsing.md`](references/summary-parsing.md) for details on how `newman-summary.json` is parsed and mapped to TC IDs.

## Core Principles

1. **Delegate execution, own the result.** This skill is the orchestrator. `newman-executor` handles CLI execution. This skill handles everything before and after: validation, parsing, TC file update, human gate.
2. **Never modify `collection.json`.** If the collection produces unexpected results, report them — do not silently fix the collection.
3. **TC name is the mapping key.** Newman reports results by request name. For static requests, the name must be `TC-{feature_id}-{CATEGORY}-{number} — {title}`. For the `Data-Driven Template` request, the TC ID is extracted dynamically from its test assertion names. If a request/assertion cannot be mapped to a TC ID, flag it rather than guessing.
4. **Every TC gets a result.** After parsing, every TC row in `tc_file` must have an entry in Status and Actual Result — either from the Newman run or marked `SKIPPED` with a reason.
5. **Human gate is non-negotiable.** The human must confirm which FAILs are real bugs, which are script errors, and which are environment issues before this skill's task is complete.

## Execution Process

> Follow every step in order. Read [`references/summary-parsing.md`](references/summary-parsing.md) before starting Step 4.

### Step 1 — Input Validation

Validate all inputs per the rules above. Stop and ask the user to correct any invalid input before proceeding.

### Step 2 — Pre-flight Check

Before invoking `newman-executor`, verify:

- The SUT is reachable: if `baseUrl` is accessible (not a hard requirement to block on, but flag a warning if unreachable)
- `summary_dir` and `report_output` parent directory both exist — create them if they do not
- If `data_file` is provided, confirm the column headers in the CSV match variable names used in the collection

### Step 3 — Invoke `newman-executor`

Pass the following to the `newman-executor` agent:

- `collection_file`
- `environment_file`
- `summary_dir`
- `report_output`
- `data_file` (optional)

Wait for `newman-executor` to complete and return the path to `{summary_dir}/newman-summary.json`.

If `newman-executor` reports an execution error (Newman not found, collection JSON invalid, environment file malformed): surface the error to the user and stop. Do not attempt to parse partial output.

### Step 4 — Parse Newman Summary

Read `{summary_dir}/newman-summary.json`. Follow the parsing logic in [`references/summary-parsing.md`](references/summary-parsing.md) to:

- Extract per-request results: pass/fail, response status code, response body excerpt
- Map each result to its TC ID (via request name for static requests, or via assertion name for the Data-Driven Template)
- Identify any requests whose names or assertions do not match a TC ID in `tc_file` — flag these

### Step 5 — Update `tc_file`

For each TC row in `tc_file`:

- If a matching Newman result exists: write the Actual Result (observed status code + response body summary) and set Status to `PASS` or `FAIL`
- If no matching result (TC was not executed): write `SKIPPED — {reason}` in Actual Result and set Status to `SKIP`

Do not modify any other columns (Title, Input, Expected Result, Data-driven, Audit Log).

### Step 6 — Execution Results Review

Before presenting to the user, verify every item in the **Execution Results Checklist** below.

### Step 7 — Human Gate

Present to the user:

1. Execution summary: `Total: {N} | PASS: {P} | FAIL: {F} | SKIP: {S}`
2. List of all FAIL TCs with their Actual Result
3. List of any SKIP TCs with reason
4. List of any request names that could not be mapped to a TC ID
5. Paths to the HTML report and updated `tc_file`

State clearly: **"Please review the FAIL results and classify each one: (1) real SUT bug, (2) collection script error, (3) environment issue. Confirm when done so the results can be used for bug reporting."**

Do not proceed to any further work until the user explicitly confirms classification of all failures.

## Execution Results Checklist

Run before presenting to the user. Every item must pass:

- [ ] `newman-summary.json` exists and is valid JSON
- [ ] HTML report exists at `{report_output}`
- [ ] Every TC row in `tc_file` has Status set (PASS / FAIL / SKIP) — no empty Status cells
- [ ] Every TC row in `tc_file` has Actual Result filled — no empty Actual Result cells
- [ ] All FAIL entries include specific HTTP status code and response body summary
- [ ] Any unmapped request names are flagged in the human gate summary
- [ ] SKIP entries include a reason

## Anti-Patterns

- **Modifying `collection.json` to fix failures.** If assertions fail, report it — do not silently update the collection.
- **Guessing TC mapping when names don't match.** Flag unmapped requests explicitly. Do not infer which TC a request corresponds to based on similarity.
- **Writing vague Actual Results.** "Request failed" is not acceptable. Write the HTTP status code and a summary of the response body.
- **Marking all FAILs as bugs without human review.** A FAIL may be caused by a script error or a down server — the human gate exists to distinguish these cases.
- **Skipping pre-flight checks.** Running Newman against an unreachable server produces misleading results. Flag connectivity issues before execution.
- **Overwriting TC columns other than Status and Actual Result.** The Title, Input, Expected Result, and Audit Log columns must not be modified.

## Best Practices

- Name directories consistently: `summary_dir` should be the same directory as the API's `contract_file` and `tc_file` so all artifacts for one API are co-located.
- For data-driven runs, Newman produces one result per iteration per request. When mapping results back to TCs, aggregate all iterations for a given request: if any iteration fails, mark the TC as FAIL and note which iteration.
- If a collection run produces zero results (Newman exits with an error before running any request), treat this as an execution error — do not write empty results to `tc_file`.
- Reference [Newman CLI documentation](https://learning.postman.com/docs/collections/using-newman-cli/command-line-integration-with-newman/) for flag reference and exit codes.
- Reference [newman-reporter-htmlextra documentation](https://github.com/DannyDainton/newman-reporter-htmlextra) for HTML report customization options.

## Process Quality Checklist

Verify before closing the task:

- [ ] All inputs validated before invoking `newman-executor`
- [ ] Pre-flight checks completed — SUT reachability and directory existence verified
- [ ] `newman-executor` was invoked with all required parameters
- [ ] `newman-summary.json` was parsed using the logic in `references/summary-parsing.md`
- [ ] Every TC in `tc_file` has Status and Actual Result populated
- [ ] Unmapped request names were identified and flagged
- [ ] Execution Results Checklist passed with zero unchecked items
- [ ] Human gate was presented with full FAIL/SKIP breakdown and explicit confirmation requested
- [ ] `collection.json` was not modified

## Common Rationalizations to Reject

- _"The request name doesn't exactly match the TC ID format, but it's close enough — I'll map it."_ → Do not guess. Flag the mismatch and ask the user to fix the request name in the collection.
- _"Several TCs failed because the server was down — I'll mark them all as environment issues."_ → Only the human can classify failures. Present all failures and let the user decide.
- _"The Actual Result is just a 500 error — no need to include the response body."_ → Always include the response body summary. A 500 with `{"error": "SQLITE_CONSTRAINT"}` is more informative than just "500".
- _"I'll skip the pre-flight check since the user said the server is running."_ → Always verify. Server state can change between the user's statement and the skill execution.
- _"Newman ran successfully so I don't need to check if all TCs were mapped."_ → Newman running successfully does not mean all TCs were executed. Always check for unmapped request names and SKIP TCs.
