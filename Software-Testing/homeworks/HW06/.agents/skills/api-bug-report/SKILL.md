---
name: api-bug-report
description: Analyze failed test cases from an executed test-cases.md, group them by root cause, and produce a production-grade bug report in a single Markdown file. Trigger this skill when the user says "generate bug report", "create bug report", "report bugs from test results", or "analyze failed TCs".
---

# `api-bug-report` Skill

## Overview

Produce a complete, production-grade **bug report** from a set of failed test cases. The skill reads an executed `test-cases.md` (with Actual Result and Status columns populated), cross-references failures against the approved `CONTRACT.md`, groups related failures by root cause, and writes a structured `bug-report.md` ready for human review and GitHub Issues submission.

Each bug entry is uniquely identified, categorized by root cause, and includes all information needed for a developer to reproduce and fix the issue.

**Primary output:** `bug-report.md` — see [`assets/BUG_REPORT_TEMPLATE.md`](assets/BUG_REPORT_TEMPLATE.md) for the exact structure.

## When to Use

- After test execution is complete and `test-cases.md` has been updated with Actual Result and Status for every TC
- When human has reviewed Newman output and confirmed which FAILs represent real bugs (vs. collection script errors or environment issues)
- When a formal bug report is needed for GitHub Issues or a defect tracking system

## When NOT to Use

- When `test-cases.md` has not been fully executed — Status and Actual Result columns must be populated for all TCs before running this skill
- When all TCs passed — no bugs to report
- When failures have not yet been reviewed by a human to distinguish real bugs from script/environment errors

## Inputs

| Name            | Type     | Required | Description                                                                          |
| --------------- | -------- | -------- | ------------------------------------------------------------------------------------ |
| `tc_file`       | `string` | ✅       | Path to the executed `test-cases.md` with Status and Actual Result columns populated |
| `contract_file` | `string` | ✅       | Path to the approved `CONTRACT.md` — used to determine expected behavior             |
| `feature_id`    | `string` | ✅       | Used as Bug ID prefix (e.g., `FR01` → `BUG-FR01-001`)                                |
| `output_dir`    | `string` | ✅       | Directory where `bug-report.md` will be written                                      |

**Validation rules — reject and ask the user to correct before proceeding:**

- `tc_file` must exist and must have at least one TC with `Status: FAIL`
- `contract_file` must exist
- `feature_id` must be non-empty with no spaces
- `output_dir` must be a valid writable path

## Output

| File            | Location                     | Description                            |
| --------------- | ---------------------------- | -------------------------------------- |
| `bug-report.md` | `{output_dir}/bug-report.md` | All bugs for this endpoint in one file |

See [`assets/BUG_REPORT_TEMPLATE.md`](assets/BUG_REPORT_TEMPLATE.md) for the exact structure and all required fields per bug entry.

## Core Principles

1. **One bug per distinct root cause, not one bug per TC.** Multiple TCs failing for the same underlying reason produce one bug entry with multiple Related TCs — not separate bug entries per TC.
2. **Contract is the expected behavior reference.** Every Expected Result in a bug entry must be grounded in `CONTRACT.md`. Do not use the TC's Expected Result column as the authoritative source — verify it against the contract.
3. **Actual Result must be specific.** Actual Result must state the observed HTTP status code and the exact or summarized response body. "It failed" is not acceptable.
4. **Severity and Priority are AI-suggested, human-confirmed.** AI assigns an initial severity and priority based on the bug category and impact. Human must confirm or override at the human gate.
5. **No fabrication.** Steps to Reproduce must be derivable from the TC's Input column. Do not invent reproduction steps not present in the test data.

## Bug Analysis Process

> Follow every step in order. Read [`references/root-cause-guide.md`](references/root-cause-guide.md) before starting Step 3.

### Step 1 — Input Validation

Validate all inputs per the rules above. Stop and ask the user to fix any invalid input before proceeding.

### Step 2 — FAIL TC Extraction

Read `tc_file`. Extract all TC rows where `Status = FAIL`. Organize by TC category (TC-FR, TC-ST, TC-SEC, TC-SCH, TC-ERR, TC-IDP, TC-RL).

If no FAILs found: report this to the user and stop — do not create an empty bug report.

### Step 3 — Root Cause Analysis

For each FAIL TC:

1. Read the TC's Expected Result and Actual Result columns
2. Cross-reference with `contract_file` to confirm the Expected Result is correctly derived from the contract
3. Identify the root cause category using the guide in [`references/root-cause-guide.md`](references/root-cause-guide.md)
4. Note the specific contract clause (BR-xx, SEC-xx, or section reference) that is violated

### Step 4 — Bug Grouping

Group FAIL TCs that share the same root cause into a single bug:

- Same root cause category AND same failing behavior AND same contract clause violated → one bug, multiple Related TCs
- Same root cause category but different behavior or different contract clause → separate bugs

Example: TC-SEC-001 (SQL injection in email) and TC-SEC-002 (SQL injection in name) both return 500 when they should return 400 → one bug `BUG-FR01-001` with Related TCs: `TC-FR01-SEC-001`, `TC-FR01-SEC-002`.

### Step 5 — Severity and Priority Assignment

Assign initial severity and priority per the guide in [`references/severity-guide.md`](references/severity-guide.md). Mark assignments clearly as AI-suggested so the human reviewer knows to confirm them.

### Step 6 — Bug Entry Writing

Write each bug entry following [`assets/BUG_REPORT_TEMPLATE.md`](assets/BUG_REPORT_TEMPLATE.md). For each entry:

- **Title:** Concise, < 80 characters, describes the behavior not the symptom
- **Steps to Reproduce:** Derived directly from TC Input column — exact field values and request structure
- **Expected Result:** Quoted or paraphrased from the relevant contract section
- **Actual Result:** Exact observed status code + summarized response body from Actual Result column
- **Impact:** Describe the risk to users or system security based on the root cause category

### Step 7 — Bug Report Completeness Review

Run the **Bug Report Completeness Checklist** below. Fix any gaps.

### Step 8 — Human Gate

Present to the user:

1. Summary table: Bug ID, Title, Root Cause Category, Severity (AI-suggested), Related TCs
2. Any grouping decisions made (TCs merged into one bug) — explain the rationale
3. Path to `bug-report.md`

State clearly: **"Please review severity and priority assignments, verify accuracy of Steps to Reproduce and Expected Results, and add screenshots to Evidence sections before submitting to GitHub Issues."**

Do not finalize the report until the user explicitly confirms.

## Bug Report Completeness Checklist

Run before delivering output. Every item must pass:

- [ ] Every FAIL TC is accounted for — either in a bug entry's Related TCs or explicitly noted as a non-bug (script error or environment issue) in the report header
- [ ] No two bug entries cover the same root cause and behavior (no duplicates)
- [ ] Every bug entry has a unique `BUG-{feature_id}-xxx` ID
- [ ] Expected Result is grounded in `contract_file` — not just copied from the TC
- [ ] Actual Result includes specific HTTP status code and response body summary
- [ ] Steps to Reproduce can be executed by someone who has not seen the TC
- [ ] Severity and Priority are assigned for every bug (AI-suggested, labeled for human confirmation)
- [ ] Impact section is non-empty and describes concrete risk
- [ ] Evidence section has the Newman report path placeholder filled in
- [ ] Screenshot placeholder is present and labeled for human addition

## Anti-Patterns

- **One bug per failing TC.** If 5 TCs fail for the same reason, that is one bug with 5 Related TCs — not 5 bugs.
- **Using TC Expected Result as the contract reference.** The TC may have an incorrect Expected Result (that is what INVALID audit status is for). Always verify against `contract_file`.
- **Vague Actual Result.** "The API returned an error" is not acceptable. State the status code and the response body content.
- **Inventing Steps to Reproduce.** Steps must be derived from the TC's recorded Input. Do not add steps not present in the test data.
- **Reporting environment failures as bugs.** If a TC failed because the server was not running or the auth token expired, that is not a bug. Only report failures confirmed as real SUT behavioral defects.
- **Skipping the human gate.** Severity and priority require human judgment — AI assignments are starting points, not final decisions.

## Best Practices

- Write bug titles that describe the incorrect behavior, not the test that found it. "Registration accepts SQL injection in email field without sanitization" is better than "TC-FR01-SEC-001 failed".
- For security bugs, the Impact section must explicitly state the attack surface and potential consequence (data breach, privilege escalation, denial of service, etc.).
- Group bugs by root cause category in the report — all AUTH bugs together, all VALIDATION bugs together. This makes the report easier to triage.
- Steps to Reproduce should be self-contained: include the full request (method, URL, headers, body) so a developer can reproduce without needing to read the original TC.
- Reference the specific contract clause violated (e.g., "Violates BR-01: email must be unique") in the Description section — this makes the bug unambiguous.
- Follow [IEEE 829 Standard for Software Test Documentation](https://standards.ieee.org/ieee/829/3787/) for bug report structure conventions.

## Process Quality Checklist

Verify before closing the task:

- [ ] All inputs validated before analysis began
- [ ] All FAIL TCs were read and analyzed — none skipped
- [ ] Root cause was determined by cross-referencing contract, not just reading the TC
- [ ] Grouping decisions are documented in the report or in the human gate summary
- [ ] Bug Report Completeness Checklist passed with zero unchecked items
- [ ] Severity and Priority are labeled as AI-suggested
- [ ] Human gate was presented and confirmation explicitly requested
- [ ] No bug entry was created for a non-bug failure (script error, environment issue)

## Common Rationalizations to Reject

- _"The TC expected result says 400, so I'll use that as the contract reference."_ → Always verify against `contract_file`. The TC may have been incorrectly designed.
- _"These two failures look different, so I'll create two separate bugs."_ → First check if they share the same root cause. If so, one bug with two Related TCs is correct.
- _"I'll skip the Impact section since it's obvious for security bugs."_ → Impact is mandatory for every bug. For security bugs it is especially critical — state the specific attack consequence explicitly.
- _"The steps to reproduce are the same as the TC input, so I'll just reference the TC ID."_ → Steps to Reproduce must be self-contained. A developer must be able to reproduce without reading the TC file.
- _"I'll assign P1 to everything to ensure it gets attention."_ → Severity inflation undermines triage. Assign severity objectively per the guide in [`references/severity-guide.md`](references/severity-guide.md).
