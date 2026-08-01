---
name: wat-spec
description: >
  Analyses a single functional requirement (FR) from a Software Requirements
  Specification (SRS) and produces a structured spec document ready for test
  design. Use this skill whenever the user invokes /wat-spec, provides an FR
  identifier (e.g. FR-01) together with a path to an SRS file, and asks to
  analyse, break down, or document that requirement. Trigger on phrases such as
  "analyse FR-XX", "generate spec for FR-XX", "break down requirement FR-XX",
  or any request to produce a spec artifact from a requirements document.
---

# wat-spec Skill

Produces a structured **Feature Specification** document for one functional requirement extracted from an SRS file. The output captures every artefact a test designer needs — actors, constraints, business rules, acceptance criteria, dependency map — without creating any test cases. Test design is out of scope for this skill.

## Quick Reference

| Item                | Value                                                        |
| ------------------- | ------------------------------------------------------------ |
| Input               | `FR_ID`, `SRS_PATH`, `OUTPUT_DIR`                            |
| Primary output      | `{OUTPUT_DIR}/fr-{xx}-spec.md`                               |
| Output language     | **English**                                                  |
| Template            | [`resources/spec-template.md`](resources/spec-template.md)   |
| Full analysis guide | [`resources/analysis-guide.md`](resources/analysis-guide.md) |
| Example output      | [`examples/fr-01-spec.md`](examples/fr-01-spec.md)           |

## When to Use

- You have an SRS (or equivalent requirements document) and a specific FR identifier to analyse.
- You need a structured artefact that consolidates scattered requirement statements into one authoritative spec before test design begins.
- The requirement has not yet been analysed, or a previous spec needs to be regenerated from updated requirements.

## When NOT to Use

- To create test cases, equivalence partitions, or boundary values — those belong in a later test-design step.
- To analyse multiple FRs in a single invocation — run the skill once per FR.
- When no SRS or equivalent requirements source exists; do not fabricate requirements.
- To document non-functional requirements (NFR-XX), security requirements (SEC-XX), or UI specifications (UI-XX) that are explicitly out of scope for functional testing.

## Inputs

All three inputs are **required** and must be supplied by the human at invocation time.

| Parameter    | Description                                         | Example                                     |
| ------------ | --------------------------------------------------- | ------------------------------------------- |
| `FR_ID`      | Identifier of the functional requirement to analyse | `FR-01`                                     |
| `SRS_PATH`   | Path to the SRS or requirements document            | `docs/system-requirements-specification.md` |
| `OUTPUT_DIR` | Directory where the spec file will be written       | `docs/fr-01/`                               |

## Outputs

A single Markdown file written to `{OUTPUT_DIR}/fr-{xx}-spec.md`. The file follows the template in [`resources/spec-template.md`](resources/spec-template.md).

Sections produced (mandatory unless marked optional):

| Section                    | Purpose                                                         |
| -------------------------- | --------------------------------------------------------------- |
| Feature Overview           | Identity, actor(s), auth requirement, entry point               |
| Input Fields & Constraints | Every user-supplied field with its data rules                   |
| Business Rules             | Numbered BR list extracted from the requirement                 |
| Success Paths              | Happy-path flows written as numbered actor–system steps         |
| Failure Paths              | Error conditions and their triggers                             |
| Acceptance Criteria        | BDD-style Given/When/Then statements                            |
| Out of Scope               | Explicit boundary of what this FR does NOT cover                |
| Dependencies               | Other FRs or system components this FR relies on                |
| Test Notes                 | Tester-facing notes: seed data, env pre-conditions, known risks |

> All output must be written in **English**, including section headings, field names, and body text. Do not use the source document's language (e.g. Vietnamese) in the output artefact.

## Analysis Process

Read [`resources/analysis-guide.md`](resources/analysis-guide.md) for the complete step-by-step analysis procedure before producing any output.

**Summary of steps (do not skip any):**

1. **Locate** — Find and read the exact FR_ID section in SRS_PATH.
2. **Scope** — Identify actors, entry points, and auth requirement.
3. **Decompose inputs** — List every input field with its data-type constraints.
4. **Extract BRs** — Derive numbered business rules from constraint statements.
5. **Trace flows** — Write success paths and failure paths as step sequences.
6. **Write ACs** — Convert each BR and flow into a Given/When/Then statement.
7. **Map boundaries** — State what is explicitly out of scope.
8. **Resolve dependencies** — List FR/component dependencies.
9. **Add test notes** — Record seed data needs, environment pre-conditions, risks.
10. **Self-review** — Run the **Output Quality Checklist** section before writing the file.

## Output Quality Checklist

Run this checklist mentally before writing the output file. Do not proceed if any item is unchecked.

- [ ] Every section listed in the template is present and non-empty.
- [ ] Feature Overview uniquely identifies the FR (ID, name, actor, auth, URL/entry point).
- [ ] Every input field visible in the requirement has a row in Input Fields & Constraints.
- [ ] Each constraint is grounded in the SRS — not inferred or invented.
- [ ] Business Rules are numbered (`BR-01`, `BR-02`, ...) and each maps to at least one source statement in the SRS.
- [ ] Success Paths are written as alternating actor/system steps, not as prose paragraphs.
- [ ] Failure Paths each have a named trigger condition.
- [ ] Every Acceptance Criterion uses exact `Given / When / Then` format.
- [ ] Out of Scope lists at least one explicit boundary.
- [ ] Dependencies reference real FR IDs or system components present in the SRS.
- [ ] Test Notes mention seed data needs or states "No seed data required."
- [ ] No test cases, equivalence partitions, or boundary values appear anywhere.
- [ ] Output is written entirely in English.
- [ ] No information has been fabricated — every claim traces back to the SRS.

## Core Principles

- **Source fidelity** — Every statement in the output must be traceable to the SRS. Never infer rules that are not stated or clearly implied.
- **Completeness over brevity** — A missed constraint causes untested behaviour. When in doubt, include the item and flag it in Test Notes.
- **Separation of concerns** — Spec analysis and test design are distinct activities. This skill performs only the former.
- **Precision in acceptance criteria** — Each Given/When/Then must be specific enough to be falsifiable in a test.
- **One FR per invocation** — Mixing requirements from multiple FRs produces an incoherent spec.

## Anti-Patterns

| Anti-pattern                                                                | Why it is harmful                                                                 |
| --------------------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| Inventing constraints not in the SRS                                        | Produces test cases for non-existent rules; wastes effort                         |
| Writing acceptance criteria as vague prose instead of Given/When/Then       | Cannot be translated directly into test assertions                                |
| Including test cases or equivalence partitions in the spec                  | Violates separation of concerns; creates confusion about which step produced what |
| Merging multiple FRs into one output file                                   | Makes the artefact unusable as a scoped input for test design                     |
| Omitting the Out of Scope section                                           | Leaves test designers guessing at boundaries                                      |
| Translating output into the SRS source language                             | Reduces reusability and breaks downstream tooling expectations                    |
| Describing "how the system should be built" rather than "what it should do" | Confuses design with specification                                                |

## Best Practices

- Read the **entire** FR section (including sub-sections, tables, and notes) before starting the decomposition — constraints are often buried in footnotes.
- When a constraint is ambiguous, record it verbatim in Test Notes with a note that clarification is needed rather than guessing.
- Write Success Paths and Failure Paths using the **actor–system alternating step** format (see `resources/analysis-guide.md`) so they can be converted directly into test steps.
- Number all Business Rules sequentially within the FR (`BR-01` ... `BR-XX`) to make them referenceable from later artefacts.
- Keep each Acceptance Criterion focused on a single observable outcome — compound ACs are harder to map to individual test cases.
- Validate that every Acceptance Criterion is falsifiable: there must be a concrete condition under which it would fail.

## Process Quality Checklist

Use this checklist to verify the overall execution of the skill, independent of output content.

- [ ] `FR_ID`, `SRS_PATH`, and `OUTPUT_DIR` were all provided before starting.
- [ ] The correct FR section was located in the SRS before any writing began.
- [ ] The analysis steps were followed in order (no steps skipped).
- [ ] The Output Quality Checklist was completed before the file was written.
- [ ] The output file was written to the correct path: `{OUTPUT_DIR}/fr-{xx}-spec.md`.
- [ ] No content from other FR sections was included in the output.
- [ ] No references to other skills, downstream workflows, or tool names appear in the output file.

## Common Rationalisations to Reject

- _"The SRS implies this constraint, so I'll include it."_ → Only include constraints that are explicitly stated. Record implied rules in Test Notes with a flag.
- _"I'll add a quick test case here to save time later."_ → Test design is a separate step. Adding test cases here conflates two distinct activities and pollutes the spec artefact.
- _"The SRS is in Vietnamese, so I'll write the output in Vietnamese too."_ → Output language is always English regardless of the source document's language.
- _"I'll combine FR-01 and FR-02 since they're related."_ → One invocation, one FR. Run the skill twice.
- _"The Out of Scope section is obvious, I'll skip it."_ → Explicit boundaries prevent scope creep in test design. Always include it.
- _"I'll infer the entry-point URL from the feature name."_ → Only record URLs that appear in the SRS. If absent, write "Not specified in SRS."
