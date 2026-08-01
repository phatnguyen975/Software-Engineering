# Analysis Guide — wat-spec

This guide documents the complete requirement analysis procedure used by `wat-spec`. Follow the steps in order. Do not produce any output until Step 10 (Self-review) is complete.

## Background: Requirement Analysis Techniques

`wat-spec` applies a subset of standard requirement engineering techniques drawn from IEEE 29148 (Systems and Software Engineering — Requirements Engineering) and ISTQB Foundation Level (Chapter 4 — Test Analysis):

| Technique                       | Applied in step | Purpose                                                                 |
| ------------------------------- | --------------- | ----------------------------------------------------------------------- |
| **Structured decomposition**    | Steps 2–4       | Break a requirement narrative into atomic, testable elements            |
| **Actor–goal modelling**        | Step 2          | Identify who interacts with the feature and what they intend to achieve |
| **Data dictionary extraction**  | Step 3          | Enumerate every input field, its type, and its constraints              |
| **Business rule extraction**    | Step 4          | Separate constraint statements from narrative and number them           |
| **Use case flow tracing**       | Step 5          | Write alternating actor/system step sequences for each path             |
| **BDD acceptance criteria**     | Step 6          | Express each rule and flow as a falsifiable Given/When/Then statement   |
| **Boundary and scope analysis** | Steps 7–8       | Define what is and is not covered; resolve inter-FR dependencies        |

These are established RE/testing techniques with published references. Do not substitute or invent alternative techniques.

## Step 1 — Locate the FR in the SRS

1. Open `SRS_PATH`.
2. Search for the exact `FR_ID` heading (e.g. `## FR-01` or `### FR-01`).
3. Read the **entire** FR section from its heading to the next FR heading, including all sub-sections, tables, bullet lists, notes, and footnotes.
4. If the FR is not found, stop and report: `"FR_ID not found in SRS_PATH"`. Do not proceed.

## Step 2 — Scope the Feature (Actor–Goal Modelling)

Extract the following from the FR text:

| Field                       | What to record                                                                                                      |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------- |
| **FR ID**                   | As given (e.g. `FR-01`)                                                                                             |
| **Feature Name**            | The short name stated in the SRS (e.g. "Account Registration")                                                      |
| **Primary Actor**           | The user role that initiates the feature (e.g. Guest, Registered User, Admin)                                       |
| **Secondary Actors**        | Other roles or systems involved (e.g. Email Service, Payment Gateway)                                               |
| **Authentication Required** | Yes / No — does the actor need to be logged in?                                                                     |
| **Entry Point**             | URL path or screen name where the feature begins (record verbatim from SRS; write "Not specified in SRS" if absent) |
| **Actor Goal**              | One sentence: what the primary actor is trying to achieve                                                           |

## Step 3 — Decompose Input Fields & Constraints (Data Dictionary)

For every field that the actor supplies as input:

| Column                     | Guidance                                                                  |
| -------------------------- | ------------------------------------------------------------------------- |
| **Field Name**             | Visible label on the UI or parameter name in the SRS                      |
| **Data Type**              | `string`, `integer`, `email`, `password`, `boolean`, `enum`, `date`, etc. |
| **Required**               | `Yes` / `No` / `Conditional: <condition>`                                 |
| **Min Length / Min Value** | Numeric lower bound if stated                                             |
| **Max Length / Max Value** | Numeric upper bound if stated                                             |
| **Format / Pattern**       | Regex pattern, format rule, or named standard (e.g. RFC 5321 for email)   |
| **Allowed Values**         | Enumerated list if applicable                                             |
| **Notes**                  | Any additional constraint not covered by the columns above                |

Record only constraints that appear in the SRS. If a constraint is implied but not stated, add a row with the note: "⚠ Implied — not stated in SRS. Clarification needed."

## Step 4 — Extract Business Rules

Scan the FR section for constraint statements — sentences that use modal verbs ("must", "shall", "should", "cannot", "only if", "unless") or that specify system behaviour conditionally.

For each rule found:

1. Assign a sequential identifier: `BR-01`, `BR-02`, ...
2. Write the rule as a single, declarative sentence in active voice.
3. Record the source line or sub-section from the SRS so the rule is traceable.

**Format:**

```
BR-01: <Rule statement.>
       Source: SRS §<sub-section>
```

If the same constraint is stated in multiple places, record it once and list all source references.

## Step 5 — Trace Flows (Use Case Flow Format)

Write flows as **alternating actor/system steps** — a standard use-case notation from Cockburn (2001) and adopted by IEEE 29148.

**Notation:**

```
Actor:  <what the actor does>
System: <how the system responds>
Actor:  <next actor action>
System: <next system response>
...
Outcome: <final observable state>
```

**Success Paths:**

- Write one numbered path per distinct happy-path scenario.
- Include the normal flow and any significant alternative flows that end successfully.

**Failure Paths:**

- Write one numbered path per named error condition.
- Begin each with: `Trigger: <condition that causes this path>`
- End each with: `Outcome: <observable error state>`

## Step 6 — Write Acceptance Criteria (BDD Format)

Convert each Business Rule and each flow into one or more Given/When/Then statements using the Behaviour-Driven Development format (Chelimsky et al., 2010; Cucumber documentation).

**Format:**

```
AC-01: <Short title>
  Given <pre-condition — system or actor state before the action>
  When  <action performed by the actor>
  Then  <observable outcome that can be verified>
```

**Rules:**

- Each AC must be **falsifiable** — there must be a concrete condition under which it fails.
- Each AC must map to at least one BR or flow step (record the mapping as `[BR-XX]` or `[Success Path N, Step M]` in a comment if helpful).
- One observable outcome per AC. If an action produces two observable outcomes, write two ACs.
- Do not write compound `Then` clauses joined by "and" when both outcomes are independently testable.

## Step 7 — Define Out of Scope

State explicitly what this FR does **not** cover. Sources for Out of Scope entries:

- Behaviours that belong to a related FR (e.g. "Login is covered by FR-02, not FR-01").
- Non-functional concerns (performance, security, accessibility) — these are out of scope for functional testing.
- UI appearance and layout (visual/GUI testing is out of scope).
- Edge cases that require infrastructure not available in the test environment.

Write each item as a single declarative sentence beginning with "Does not cover..."

## Step 8 — Resolve Dependencies

List every external dependency the feature relies on:

| Type                 | Example                                                              |
| -------------------- | -------------------------------------------------------------------- |
| **FR dependency**    | "FR-07 (Shopping Cart) requires FR-01 (Registration) to be complete" |
| **System component** | Email delivery service, payment gateway, SMS provider                |
| **Shared data**      | Product catalogue, user account table                                |
| **Environment**      | Specific configuration, feature flag, seed data                      |

For each dependency, note whether it is a **hard dependency** (feature cannot be tested without it) or a **soft dependency** (feature can be partially tested without it).

## Step 9 — Add Test Notes

Record information that a test designer will need but that does not belong in any other section:

- **Seed data requirements** — What data must exist in the system before testing begins (e.g. "At least one product must exist in category ID 1").
- **Environment pre-conditions** — Configuration flags, feature toggles, third-party service mocks.
- **Known risks** — Behaviours in the SRS that are ambiguous, contradictory, or likely to change.
- **Clarifications needed** — Open questions that should be resolved before test design.

If none of the above apply, write: "No seed data or special environment pre-conditions required."

## Step 10 — Self-Review (Output Quality Checklist)

Before writing the output file, verify every item in the [Output Quality Checklist](../SKILL.md#output-quality-checklist).

Only proceed to write the file when all items pass.
