# Test Design Guide — wat-design

Complete procedure for designing a black-box test suite from a feature specification. Follow steps in order. Do not write output files until Step 6 (Self-review) passes.

## Techniques Applied

This guide applies two ISTQB-recognised black-box test design techniques (ISTQB Foundation Level Syllabus v4.0):

| Technique                         | Purpose in this skill                                                                                                         |
| --------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| **Equivalence Partitioning (EP)** | Divide the input domain into classes where all values are expected to behave identically; select one representative per class |
| **Boundary Value Analysis (BVA)** | Test at and around the edges of each partition where defects cluster                                                          |
| **Error Guessing**                | Supplement systematic coverage with experience-based fault-attack cases targeting known defect types                          |

EP and BVA are applied via `/domain-testing`. Error Guessing is applied via `/error-guessing`.

## Step 1 — Read the Spec

1. Open `SPEC_PATH` and read **all sections** before beginning any analysis.
2. Identify every input field listed in Section 2 (Input Fields & Constraints).
3. Note all Business Rules (Section 3), Success Paths (Section 4), and Failure Paths (Section 5).
4. Confirm the Acceptance Criteria (Section 6) — these are the primary coverage targets.

## Step 2 — Build Equivalence Partition Tables (Domain Testing)

Invoke `/domain-testing` for each input field that has at least one explicit constraint.

For each field, produce an EP table:

```markdown
#### Field: {Field Name}

| Class ID | Partition Description  | Type    | Representative |
| -------- | ---------------------- | ------- | -------------- |
| EP-01    | {valid range / format} | Valid   | {value}        |
| EP-02    | {another valid class}  | Valid   | {value}        |
| EP-03    | {below minimum}        | Invalid | {value}        |
| EP-04    | {above maximum}        | Invalid | {value}        |
| EP-05    | {wrong format}         | Invalid | {value}        |
| EP-06    | {empty / null}         | Invalid | {value}        |
```

Rules:

- Every explicit constraint in the spec generates at least one Invalid partition.
- Every allowed range or format generates at least one Valid partition.
- Enum fields: each allowed value is its own Valid partition; values outside the enum form one Invalid partition.

## Step 3 — Apply Boundary Value Analysis

For every field with a numeric range or length constraint, list the six canonical BVA values:

```markdown
#### Field: {Field Name} — Boundary (min={n}, max={m})

| BVA Point | Value | EP Class | TC Type             |
| --------- | ----- | -------- | ------------------- |
| min − 1   | {n-1} | Invalid  | Negative            |
| min       | {n}   | Valid    | Positive (boundary) |
| min + 1   | {n+1} | Valid    | Positive            |
| max − 1   | {m-1} | Valid    | Positive            |
| max       | {m}   | Valid    | Positive (boundary) |
| max + 1   | {m+1} | Invalid  | Negative            |
```

If `min − 1` or `max + 1` falls outside the data type's representable range, note this and skip that point.

## Step 4 — Error Guessing (Fault-Attack Catalogue)

Invoke `/error-guessing` and work through the catalogue below systematically. For each fault class, determine whether it is applicable to this feature; if applicable, generate at least one test case.

### Fault-Attack Catalogue

| #     | Fault class                          | Typical input examples                                                         |
| ----- | ------------------------------------ | ------------------------------------------------------------------------------ |
| EG-01 | Empty / blank inputs                 | `""`, `" "` (whitespace only), null                                            |
| EG-02 | Boundary-adjacent special characters | `@` at start/end of string, `\0`, control characters                           |
| EG-03 | Maximum-length exact string          | A string of exactly `max` characters                                           |
| EG-04 | Overlong input                       | A string of `max + 1` and `max × 2` characters                                 |
| EG-05 | Leading / trailing whitespace        | `" value"`, `"value "`, `" value "`                                            |
| EG-06 | Duplicate / already-existing data    | Submitting a value that already exists when uniqueness is required             |
| EG-07 | Incorrect data type                  | Integer where string expected, string where enum expected                      |
| EG-08 | Case sensitivity                     | Uppercase version of a value that should be unique in a case-insensitive store |
| EG-09 | Multi-field interaction              | Field A valid but Field B invalid; fields that must match (password + confirm) |
| EG-10 | Dependency violation                 | Submitting the feature without satisfying a prerequisite (e.g. not logged in)  |
| EG-11 | SQL / script injection attempt       | `'; DROP TABLE users; --`, `<script>alert(1)</script>`                         |
| EG-12 | Unicode / multi-byte characters      | Emoji, CJK characters, RTL text in a text field                                |
| EG-13 | Repeated submission                  | Submit valid form twice in rapid succession                                    |
| EG-14 | Session / auth state mismatch        | Attempt action with expired or invalid session                                 |

> Record each applied fault class as `EG-{N}: {description}` in the analysis trail. Record inapplicable ones as `EG-{N}: N/A — {reason}`.

## Step 5 — Consolidate and Deduplicate

### 5a — Combine valid classes (Combination Rule)

Arrange valid EP representatives into a minimal set of test cases such that every valid partition appears in at least one test case.

**Algorithm:**

1. List all valid EP representatives for all fields in a matrix.
2. Find the minimum number of rows (test cases) that achieves full coverage (analogous to pairwise for two-value domains, but full for independent fields).
3. Each row becomes one Positive test case.

**Example:**

```
Field A valid classes: EP-A1, EP-A2
Field B valid classes: EP-B1, EP-B2

Minimum coverage — 2 TCs:
  TC-positive-1: EP-A1 + EP-B1
  TC-positive-2: EP-A2 + EP-B2
```

### 5b — Isolate invalid classes (Isolation Rule)

Each invalid EP class (including each BVA boundary violation) becomes one dedicated Negative test case. All other fields in that TC carry the default valid representative value.

### 5c — Add error-guessing cases

Append all error-guessing TCs that are not already covered by EP/BVA cases. A case is already covered if its input values and expected result match an existing EP or BVA test case exactly.

### 5d — Assign TC-IDs

Only after deduplication is complete, assign sequential TC-IDs: `TC-FR{XX}-001`, `TC-FR{XX}-002`, ...

### 5e — Classify each TC

| Type     | When to use                                                       |
| -------- | ----------------------------------------------------------------- |
| Positive | All inputs valid; exercises a success path                        |
| Negative | At least one input deliberately invalid; exercises a failure path |
| Edge     | Input is at or near a boundary; may be valid or invalid           |

## Step 6 — Self-Review

Before writing any output file, verify every item in the [Output Quality Checklist](../SKILL.md#output-quality-checklist).

## Step 7 — Write the Test Case Document

Write `{OUTPUT_DIR}/fr-{xx}-test-cases.md` with two sections:

### Section 1 — Analysis Trail

Include, in order:

1. EP tables for each constrained field (from Step 2)
2. BVA tables for each bounded field (from Step 3)
3. Error-guessing catalogue with applied / N/A status (from Step 4)
4. Combination matrix for valid classes (from Step 5a)

### Section 2 — Test Case Table

One row per TC using the template in `tc-template.md`. Present as a Markdown table with all columns.

## Step 8 — Write the Test Data File

Write `{OUTPUT_DIR}/fr-{xx}-data.json` in parallel with or immediately after the TC table.

Follow the schema in `data-schema.md`. Each TC-ID maps to one JSON object containing all input field values and the expected result identifier.
