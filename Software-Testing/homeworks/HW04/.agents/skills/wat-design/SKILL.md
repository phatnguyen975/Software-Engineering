---
name: wat-design
description: >
  Designs a complete, optimised test case suite for one functional requirement
  using black-box test design techniques (Domain Testing and Error Guessing),
  then extracts all input data into a structured JSON file. Use this skill
  whenever the user invokes /wat-design, provides an FR identifier and a path
  to a feature specification document, and asks to design, generate, or produce
  test cases. Trigger on phrases such as "design test cases for FR-XX",
  "generate test suite for FR-XX", "create test cases from spec", or any
  request to produce a test case artefact from a feature specification.
---

# wat-design Skill

Produces two artefacts from a feature specification document:

1. **Test Case Document** — a structured Markdown file containing the full analysis trail (EP tables, BVA values, error-guessing catalogue) followed by a consolidated test case table.
2. **Test Data File** — a JSON file containing every input dataset referenced by the test cases, ready for data-driven test execution.

## Quick Reference

| Item              | Value                                                                                                                   |
| ----------------- | ----------------------------------------------------------------------------------------------------------------------- |
| Inputs            | `FR_ID`, `SPEC_PATH`, `OUTPUT_DIR`                                                                                      |
| Output 1          | `{OUTPUT_DIR}/fr-{xx}-test-cases.md`                                                                                    |
| Output 2          | `{OUTPUT_DIR}/fr-{xx}-data.json`                                                                                        |
| Output language   | **English**                                                                                                             |
| TC-ID format      | `TC-FR{XX}-{NNN}` (e.g. `TC-FR01-001`)                                                                                  |
| Minimum TCs       | 12 per FR                                                                                                               |
| Test design guide | [`resources/test-design-guide.md`](resources/test-design-guide.md)                                                      |
| TC template       | [`resources/tc-template.md`](resources/tc-template.md)                                                                  |
| Data file schema  | [`resources/data-schema.md`](resources/data-schema.md)                                                                  |
| Full example      | [`examples/fr-01-test-cases.md`](examples/fr-01-test-cases.md) · [`examples/fr-01-data.json`](examples/fr-01-data.json) |

## When to Use

- A feature specification document exists for `FR_ID` and you need a test case suite designed from it using systematic black-box techniques.
- The requirement has input fields with constraints (lengths, formats, enums, business rules) that benefit from equivalence partitioning and boundary analysis.
- You need a data-driven JSON file to accompany an automation spec.

## When NOT to Use

- No feature specification exists — do not design test cases directly from an SRS or narrative description without a structured spec first.
- To write automation scripts or page objects — test design and implementation are separate activities.
- To design tests for non-functional concerns (performance, security, accessibility) — these require different techniques outside this skill's scope.
- To design tests for multiple FRs in a single invocation — run once per FR.

## Inputs

All three inputs are **required** and must be supplied by the human.

| Parameter    | Description                                | Example                    |
| ------------ | ------------------------------------------ | -------------------------- |
| `FR_ID`      | Identifier of the feature to test          | `FR-01`                    |
| `SPEC_PATH`  | Path to the feature specification document | `docs/fr-01/fr-01-spec.md` |
| `OUTPUT_DIR` | Directory for output artefacts             | `docs/fr-01/`              |

## Outputs

> Both outputs must be written in **English**.

### Output 1 — Test Case Document

`{OUTPUT_DIR}/fr-{xx}-test-cases.md`

Structure (all sections mandatory):

1. **Analysis Trail** — EP tables, BVA results, error-guessing catalogue (shows reasoning; do not omit even if lengthy)
2. **Test Case Table** — consolidated, numbered test cases using the template in [`resources/tc-template.md`](resources/tc-template.md)

### Output 2 — Test Data File

`{OUTPUT_DIR}/fr-{xx}-data.json` (written alongside `OUTPUT_DIR` or to the data directory if specified — confirm with context)

For schema, see [`resources/data-schema.md`](resources/data-schema.md).

## Technique Routing

Read [`resources/test-design-guide.md`](resources/test-design-guide.md) for the complete procedure. The routing logic below determines which `functional-test-design` sub-skills to invoke.

```
For each input field in the spec:
  Has enumerable values or explicit constraints (length, range, format)?
    YES → invoke /domain-testing for that field
    NO  → skip EP/BVA; cover via error-guessing only

After domain-testing is complete:
  Does the feature have complex multi-field interactions, BR combinations, security-sensitive inputs, or state-dependent behaviour?
    YES → invoke /error-guessing to augment the suite
    NO  → still invoke /error-guessing (minimum: empty inputs, null values, boundary-adjacent special characters)
```

- **Primary technique:** `/domain-testing` (Equivalence Partitioning + BVA)
- **Augmentation technique:** `/error-guessing` (fault-attack catalogue)

Other sub-skills within `functional-test-design` (decision tables, state-transition, use-case testing) are **not** invoked by this skill unless the spec explicitly describes combinatorial rule logic or a state machine — in which case, read the relevant sub-skill and apply it before domain-testing.

## Test Case Design Summary

> For full procedure with worked examples, see [`resources/test-design-guide.md`](resources/test-design-guide.md).

### Combination rule (valid classes)

Combine valid equivalence classes across fields into the **minimum number of test cases** such that every valid class appears in at least one test. This avoids redundant positive cases.

### Isolation rule (invalid classes)

Each invalid equivalence class generates its **own dedicated test case**. All other fields in that test case carry valid values. This isolates the failure condition.

### Deduplication

After merging domain-testing results with error-guessing results, remove any test case whose input/expected-result combination is identical to another. Assign TC-IDs only after deduplication.

### Minimum coverage

At minimum, the suite must include:

- At least one test case per valid equivalence class combination (SP coverage)
- One test case per invalid equivalence class (FP coverage)
- At least one BVA test case per numeric or length-bounded field
- At least one error-guessing test case per business rule

## Test Case Format

Each test case must use the template in [`resources/tc-template.md`](resources/tc-template.md).

Key formatting rules:

- **Title:** `Action + Function + Operating Condition`
  - Example: `"Register account with valid data"`
  - Example: `"Register account with duplicate email"`
- **Test Steps:** multi-line steps separated by `<br>` tags
- **Expected Result:** specific enough to write an assertion — not "error is shown" but "field-level error 'Email already registered' appears on the Email field"
- **Input Data ref:** always `→ ref: fr-{xx}-data.json#{TC-ID}`
- **Actual Result:** leave blank — filled after execution
- **Status:** leave blank — filled after execution

## Output Quality Checklist

Verify every item before writing the output files.

- [ ] Analysis trail is present: EP tables for every constrained field, BVA values, error-guessing catalogue.
- [ ] Combination rule applied: valid classes combined, not tested one-at-a-time redundantly.
- [ ] Isolation rule applied: each invalid class has its own TC with all other fields valid.
- [ ] Every test case title follows `Action + Function + Operating Condition` format.
- [ ] Every test case has a non-empty, specific Expected Result (falsifiable).
- [ ] Test Steps use `<br>` separators.
- [ ] Input Data field contains `→ ref: fr-{xx}-data.json#{TC-ID}` for every TC.
- [ ] At least 12 test cases produced (aim for 15–18 for adequate coverage).
- [ ] No two test cases have identical input + expected-result combinations.
- [ ] Every TC type classification is correct (Positive / Negative / Edge).
- [ ] `fr-{xx}-data.json` contains one entry per TC-ID, with all input fields present.
- [ ] Expected result in the JSON matches the Expected Result in the TC table.
- [ ] Both outputs are written in English.
- [ ] No automation code, selectors, or script logic appears in either output.

## Core Principles

- **Traceability** — every test case must trace back to at least one AC, BR, or flow path in the spec. If it cannot, remove it.
- **Falsifiability** — every Expected Result must be specific enough that it is possible to determine, unambiguously, whether the system passes or fails.
- **Non-redundancy** — two test cases that exercise the same equivalence class under the same conditions are one test case too many. Apply the combination rule ruthlessly.
- **Completeness over brevity for analysis** — the analysis trail must be fully documented even when it is long; omitting it obscures the reasoning and makes review impossible.
- **Separation of concerns** — test cases describe _what_ to verify; automation scripts describe _how_ to verify it. No script logic belongs in this artefact.

## Anti-Patterns

| Anti-pattern                                                                 | Why it is harmful                                                                                                                  |
| ---------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| Writing test cases directly without EP/BVA analysis                          | Produces ad-hoc coverage with unknown gaps                                                                                         |
| Testing each valid class in its own test case (no combination)               | Produces redundant positive tests; inflates TC count without improving coverage                                                    |
| Testing multiple invalid classes in a single test case                       | Makes it impossible to isolate the root cause when the test fails                                                                  |
| Vague Expected Results ("error appears", "form is submitted")                | Cannot be used as an assertion; forces the tester to guess what "pass" looks like                                                  |
| Hardcoding input values in the TC table instead of referencing the data file | Creates drift between the TC document and the data file; breaks data-driven execution                                              |
| Skipping error-guessing because domain-testing covered "enough"              | Error-guessing targets fault classes (injection, boundary-adjacent specials, race conditions) that EP/BVA by design does not cover |
| Producing fewer than 12 test cases                                           | Insufficient coverage; does not meet the minimum acceptance threshold                                                              |

## Best Practices

- Derive EP tables **field by field** before combining them — rushing to write TCs before the partitions are defined produces inconsistent coverage.
- Apply BVA at **every** numeric or length boundary: `min-1`, `min`, `min+1`, `max-1`, `max`, `max+1`. Record all six values even if some collapse into the same EP.
- Use the error-guessing **fault-attack catalogue** in `resources/test-design-guide.md` as a checklist — work through it systematically rather than brainstorming ad hoc.
- Write the JSON data file **in parallel** with the TC table — do not write all TCs first and then extract data, as this introduces transcription errors.
- Assign TC-IDs **only after** deduplication is complete — renumbering after the fact is error-prone.
- Classify each TC as Positive, Negative, or Edge **at authoring time**, not retrospectively — classification drives prioritisation during execution.

## Process Quality Checklist

Verify the overall execution of the skill, independent of output content.

- [ ] `FR_ID`, `SPEC_PATH`, and `OUTPUT_DIR` were provided before starting.
- [ ] The spec document at `SPEC_PATH` was read before any analysis began.
- [ ] `/domain-testing` was invoked for every field with explicit constraints.
- [ ] `/error-guessing` was invoked to augment the suite.
- [ ] EP tables and BVA results are documented in the analysis trail section.
- [ ] Combination rule was applied to valid classes.
- [ ] Isolation rule was applied to invalid classes.
- [ ] Deduplication was performed before TC-IDs were assigned.
- [ ] The Output Quality Checklist was completed before writing any file.
- [ ] Both output files were written to `OUTPUT_DIR`.
- [ ] No references to other skills, downstream workflows, or tool names appear in either output file.

## Common Rationalisations to Reject

- _"I'll skip the EP table and write test cases directly — it's faster."_ → The EP table is the analytical foundation. Without it, coverage is unknown and the combination/isolation rules cannot be applied correctly.
- _"I'll test all fields together to save TC count."_ → Mixing multiple invalid classes in one TC violates the isolation rule and makes failures ambiguous.
- _"The expected result is obvious — 'an error is shown'."_ → Obvious to whom? The exact error message, its location, and the field it is associated with must all be specified.
- _"12 test cases is too many for this simple feature."_ → If systematic EP/BVA and error-guessing produce fewer than 12, the analysis is incomplete. Re-examine the spec for missed constraints.
- _"I'll add the data file later."_ → The data file and the TC table must be consistent. Writing them in parallel is the only reliable way to ensure they stay in sync.
- _"Error-guessing is subjective — I'll skip it."_ → Error-guessing uses a structured fault-attack catalogue, not intuition. It is a recognised ISTQB technique with well-defined fault classes.
