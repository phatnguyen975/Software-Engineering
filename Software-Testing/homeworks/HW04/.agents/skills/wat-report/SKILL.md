---
name: wat-report
description: >
  Analyses a completed test case document for a single functional requirement,
  groups failing test cases by root cause, and produces a structured bug report.
  Use this skill whenever the user invokes /wat-report, provides an FR identifier
  and a path to a test case document that has Actual Result and Status columns
  filled, and asks to generate, produce, or write a bug report. Trigger on phrases
  such as "report bugs for FR-XX", "generate bug report from FR-XX results",
  "create defect report for FR-XX", or any request to produce a bug report
  artefact from an executed test case document.
---

# wat-report Skill

Reads a completed test case document, groups failing test cases by shared root cause, and produces a production-quality bug report file. One root cause = one bug entry, regardless of how many test cases failed because of it.

## Quick Reference

| Item                | Value                                                                  |
| ------------------- | ---------------------------------------------------------------------- |
| Inputs              | `FR_ID`, `TC_PATH`                                                     |
| Output              | `{TC_PATH}/fr-{xx}-bug-report.md`                                      |
| Output language     | **English**                                                            |
| Bug ID format       | `BUG-FR{XX}-{NNN}` (e.g. `BUG-FR01-001`)                               |
| Root cause guide    | [`resources/root-cause-guide.md`](resources/root-cause-guide.md)       |
| Bug report template | [`resources/bug-report-template.md`](resources/bug-report-template.md) |
| Full example        | [`examples/fr-01-bug-report.md`](examples/fr-01-bug-report.md)         |

## When to Use

- The test case document for `FR_ID` has been executed and the `Actual Result` and `Status` columns are filled for all test cases.
- At least one test case has `Status = Fail`.
- You need a structured, traceable bug report grouped by root cause.

## When NOT to Use

- No test cases have `Status = Fail` — there is nothing to report.
- The `Actual Result` column is still blank — execution has not completed.
- To report performance, security, or accessibility defects — this skill covers functional defects only.
- To create a bug report from an SRS, spec, or code review — this skill operates exclusively from executed test case results.

## Inputs

Both inputs are **required**.

| Parameter | Description                                 | Example                          |
| --------- | ------------------------------------------- | -------------------------------- |
| `FR_ID`   | Identifier of the feature being reported    | `FR-01`                          |
| `TC_PATH` | Path to the **executed** test case document | `docs/fr-01/fr-01-test-cases.md` |

The output file is written to the same directory as `TC_PATH`.

## Output

A single Markdown file: `{TC_PATH}/fr-{xx}-bug-report.md`

The file contains one bug entry per distinct root cause, using the template in [`resources/bug-report-template.md`](resources/bug-report-template.md).

> The output must be written in **English**. Human will use this file to create GitHub Issues manually after review.

## Reporting Process

Read [`resources/root-cause-guide.md`](resources/root-cause-guide.md) for the complete root cause analysis procedure.

Summary of steps:

1. **Collect failures** — Read `TC_PATH`; extract all rows where `Status = Fail`.
2. **Analyse each failure** — For each failing TC, identify the defective system behaviour from `Actual Result` and determine the technical cause.
3. **Group by root cause** — Apply the grouping rules in [`resources/root-cause-guide.md`](resources/root-cause-guide.md) to cluster TCs that share the same underlying defect.
4. **Assign Bug IDs** — After grouping is complete, assign sequential IDs: `BUG-FR{XX}-001`, `BUG-FR{XX}-002`, ...
5. **Write each bug entry** — Use the template in [`resources/bug-report-template.md`](resources/bug-report-template.md).
6. **Self-review** — Run the Bug Report Quality Checklist.
7. **Write output file.**

## Bug Report Quality Checklist

Verify every item before writing the output file.

- [ ] Every failing TC is accounted for — either as the primary TC in a bug entry or listed under `Affects TCs` of an existing entry.
- [ ] No two bug entries share the same root cause (grouping is complete).
- [ ] Each bug entry has a `Bug ID` in `BUG-FR{XX}-{NNN}` format.
- [ ] Each `Title` follows `Action + Function + Condition` format describing the defect.
- [ ] `Root Cause` is a specific technical statement — not "something is wrong" or "validation failed".
- [ ] `Affects TCs` lists every TC-ID grouped under this bug.
- [ ] `Severity` and `Priority` are each assigned with a written `Reason`.
- [ ] `Environment` section is complete (Browser, OS, URL, SUT Version).
- [ ] `Steps to Reproduce` are numbered, actionable steps derived from the failing TC's Test Steps.
- [ ] `Expected Result` matches the TC's Expected Result column exactly.
- [ ] `Actual Result` matches the TC's Actual Result column exactly.
- [ ] `Evidence` section references at least one item (screenshot, report path, or trace).
- [ ] `GitHub Issue` field is present (value: `— to be created` if not yet filed).
- [ ] Output is written entirely in English.
- [ ] No test cases with `Status = Pass` are included in the report.

## Core Principles

- **Root cause, not symptom** — Group by what caused the failure, not by what was observed. Two TCs that show different error messages may share the same root cause (e.g. missing server-side validation).
- **One root cause, one bug** — Never create one bug entry per failing TC. Multiple TCs failing because of the same defect inflate the bug count and obscure the real scope of the problem.
- **Traceability** — Every bug entry must link back to the TC-IDs that revealed it. This creates an auditable chain from defect → test case → requirement.
- **Evidence over assertion** — Every claim in the bug report must be supported by observable evidence from the test run (Actual Result text, screenshot, HTTP status, console log).
- **Severity ≠ Priority** — Severity describes the technical impact on the system; Priority describes the urgency of fixing it. Both must be justified with a written reason.

## Severity & Priority Reference

> **Source:** IEEE 1044-2009 (Software Anomalies Classification Standard) and common industry practice.

### Severity Levels

| Level        | Definition                                                                                  | Example                                                         |
| ------------ | ------------------------------------------------------------------------------------------- | --------------------------------------------------------------- |
| **Critical** | System crash, data loss, security breach, or complete feature failure with no workaround    | Registration API returns 500; no accounts can be created        |
| **Major**    | Feature partially works but a key function is broken; workaround exists but is unacceptable | Duplicate email check always passes; duplicate accounts created |
| **Minor**    | Feature works but produces incorrect output in a non-critical path                          | Error message text differs from spec but error is shown         |
| **Trivial**  | Cosmetic issue with no functional impact                                                    | Incorrect capitalisation in an error message                    |

### Priority Levels

| Level      | Definition                                                                      |
| ---------- | ------------------------------------------------------------------------------- |
| **High**   | Must be fixed before release; blocks core user flows or acceptance criteria     |
| **Medium** | Should be fixed in the current cycle; degrades UX but does not block core flows |
| **Low**    | Can be deferred to a future cycle; minor or edge-case impact                    |

## Anti-Patterns

| Anti-pattern                                                         | Why it is harmful                                                                |
| -------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| One bug entry per failing TC regardless of shared cause              | Inflates bug count; obscures the real scope; wastes developer investigation time |
| Root cause stated as "validation is broken" or "bug in registration" | Too vague to investigate or reproduce; developer cannot locate the defect        |
| Copying Expected Result as Actual Result when the test failed        | Defeats the purpose of the report; Evidence section is left meaningless          |
| Assigning Severity = Critical to every bug                           | Destroys triage signal; everything becomes noise                                 |
| Leaving GitHub Issue field blank instead of "— to be created"        | Creates ambiguity about whether the issue was filed or simply forgotten          |
| Including passing TCs in the Affects TCs list                        | Misleads the developer about the blast radius of the defect                      |
| Writing Steps to Reproduce as prose instead of numbered steps        | Makes reproduction harder; increases round-trip time with the developer          |

## Best Practices

- Determine root cause **before** writing any bug entry — writing while still unsure of the cause produces vague reports.
- Derive `Steps to Reproduce` directly from the failing TC's `Test Steps` column — this ensures they are accurate and reproducible.
- When multiple browsers are listed in the `Environment`, note whether the defect reproduces on **all** browsers or only specific ones.
- If the `Actual Result` from the TC document lacks enough detail to write a clear bug report, note the gap in the `Notes` section and record what additional information is needed.
- Assign `Severity` based on **user impact**, not on how difficult the fix appears to be.
- Assign `Priority` based on **business impact** and release timeline, not on severity alone.
- Keep `Root Cause` entries to 1–2 sentences. If the analysis requires more, it belongs in `Notes`.

## Process Quality Checklist

Verify overall execution of the skill.

- [ ] `FR_ID` and `TC_PATH` were provided before starting.
- [ ] `TC_PATH` was read in full before any analysis began.
- [ ] All rows with `Status = Fail` were identified before grouping.
- [ ] Root cause grouping was completed before Bug IDs were assigned.
- [ ] Bug IDs were assigned only after grouping, sequentially with no gaps.
- [ ] The Bug Report Quality Checklist was completed before writing the output file.
- [ ] Output was written to the correct path: `{TC_PATH}/fr-{xx}-bug-report.md`.
- [ ] No references to other skills, downstream workflows, or tool names appear in the output file.
- [ ] Output is written in English.

## Common Rationalisations to Reject

- _"Each failing TC is a different bug — I'll create one entry per TC."_ → Group by root cause first. Two TCs failing because the same API endpoint ignores input validation are one bug, not two.
- _"The root cause is obvious — I don't need to state it."_ → Root cause must always be written explicitly. What is obvious to you today is unclear to a developer reading this report tomorrow.
- _"I'll set Severity = Critical for everything to get it fixed faster."_ → Severity is a factual classification, not a negotiating tactic. Use the IEEE 1044 definitions consistently.
- _"The GitHub Issue field can stay blank — we'll fill it in later."_ → Write `— to be created` to signal intent explicitly. A blank field is ambiguous.
- _"Actual Result is the same as Expected Result — the test just failed for a different reason."_ → If Actual ≠ Expected, they are never the same. Re-read the `Actual Result` column in the TC document and report what was actually observed.
