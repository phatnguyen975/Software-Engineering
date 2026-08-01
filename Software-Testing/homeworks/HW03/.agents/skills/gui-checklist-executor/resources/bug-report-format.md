# Bug Report Format — GUI Checklist Executor

Canonical field set and severity scale for bug reports produced by this skill. Each bug group in `bug-report-{screen_id}.md` uses this structure.

## Bug Group Structure

```markdown
## BUG-{screen_id}-{nnn} — {Short descriptive title}

| Field                   | Value                                                         |
| ----------------------- | ------------------------------------------------------------- |
| **ID**                  | BUG-{screen_id}-{nnn}                                         |
| **Screen**              | {screen_id}: {screen_name}                                    |
| **Type**                | Bug / Usability                                               |
| **Severity**            | {0–4} — {label}                                               |
| **Priority**            | High / Med / Low                                              |
| **Affected Items**      | {comma-separated checklist item IDs, e.g. IA02-003, IA02-005} |
| **Affected Edge Cases** | {comma-separated EC IDs, or "None"}                           |

### Description

{One to two sentences summarising the defect — what is wrong and where.}

### Steps to Reproduce

1. {Step 1}
2. {Step 2}
3. {Step 3 — observe the defect}

### Expected Behaviour

{What should happen according to the checklist item description and/or heuristic.}

### Actual Behaviour

{What actually happens — specific, observable, not subjective.}

### Heuristic Reference

{One or more codes: N1–N10, S1–S8, NOR1–NOR6, WCAG{n.n.n}}

### Evidences

{Screenshots for this bug}

### Suggested Fix

{Concrete, actionable suggestion for the development team.}
```

## Severity Scale (Nielsen 1994)

> **Source:** Nielsen, J. (1994). _Severity Ratings for Usability Problems. Nielsen Norman Group_. https://www.nngroup.com/articles/how-to-rate-the-severity-of-usability-problems/

| Level | Label                   | Definition                                                                                                                           | Action                                             |
| ----- | ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------- |
| **4** | Usability Catastrophe   | Prevents task completion entirely; user cannot proceed without fix. Violates a legal accessibility requirement (WCAG Level A or AA). | Must fix before release                            |
| **3** | Major Usability Problem | Causes significant difficulty; user can complete the task only with substantial effort, workaround, or assistance.                   | High priority fix                                  |
| **2** | Minor Usability Problem | Causes friction or confusion but user can complete the task.                                                                         | Fix if time permits                                |
| **1** | Cosmetic Problem        | Visual inconsistency or minor annoyance with no functional impact.                                                                   | Fix only if extra time available                   |
| **0** | Not a Bug               | Behaviour is as designed; item was incorrectly marked `Fail` during execution.                                                       | No action — update execution result to Pass or N/A |

**Calibration notes:**

- Accessibility violations (WCAG Level A or AA) are always Severity 4, regardless of whether users can workaround them — legal compliance is non-negotiable.
- Do not inflate severity. A missing tooltip is Severity 1. A form that cannot be submitted is Severity 4. When in doubt between two levels, choose the lower one.
- Severity 0 is valid — use it when human review reveals the AI's `Fail` verdict was incorrect.

## Grouping Rules

**Group items into one bug when:**

- Two or more `Fail` items share the same root cause (e.g. multiple form fields all lack inline validation — one bug: "Validation fires only on submit across all form fields")
- The same component type fails the same criterion in multiple instances (e.g. three toast notifications all lack a distinct visual icon — one bug)

**Create separate bugs when:**

- The root causes are distinct, even if the symptoms look similar (e.g. "Button has no visible label" and "Button has no focus indicator" are different root causes)
- The affected components are different types (e.g. a missing label on a text input vs a missing label on a file upload control)

## Type Definitions

| Type          | When to use                                                                                                                                                                                                                                 |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Bug**       | The implementation deviates from standard behaviour, accepted convention, or a verifiable specification (e.g. a required field accepts empty input; a date picker allows Feb 30)                                                            |
| **Usability** | The implementation is technically functional but violates a usability heuristic or creates unnecessary friction (e.g. error message appears at page top instead of adjacent to the field; status badge uses colour only with no text label) |
