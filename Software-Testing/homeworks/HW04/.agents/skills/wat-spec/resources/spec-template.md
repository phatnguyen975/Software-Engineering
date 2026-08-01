# Feature Specification — {FR_ID}: {Feature Name}

> **Source SRS:** `{SRS_PATH}`  
> **Generated:** {YYYY-MM-DD}  
> **Status:** DRAFT | APPROVED

## 1. Feature Overview

| Field                   | Value                                                        |
| ----------------------- | ------------------------------------------------------------ |
| FR ID                   | {FR_ID}                                                      |
| Feature Name            | {Feature Name}                                               |
| Primary Actor           | {e.g. Guest User, Registered User, Admin}                    |
| Secondary Actors        | {e.g. Email Service — or "None"}                             |
| Authentication Required | {Yes / No}                                                   |
| Entry Point             | {URL path or screen name — "Not specified in SRS" if absent} |
| Actor Goal              | {One sentence: what the actor is trying to achieve}          |

## 2. Input Fields & Constraints

<!-- One row per user-supplied input field. -->
<!-- Record only constraints stated in the SRS. -->
<!-- Flag implied constraints with ⚠. -->

| Field Name | Data Type | Required             | Min      | Max      | Format / Pattern | Allowed Values | Notes        |
| ---------- | --------- | -------------------- | -------- | -------- | ---------------- | -------------- | ------------ |
| {field}    | {type}    | {Yes/No/Conditional} | {n or —} | {n or —} | {pattern or —}   | {list or —}    | {notes or —} |

## 3. Business Rules

<!-- Numbered sequentially. Each rule is one declarative sentence. -->
<!-- Every rule must cite a source location in the SRS. -->

**BR-01:** {Rule statement.}

> **Source:** SRS §{sub-section}

**BR-02:** {Rule statement.}

> **Source:** SRS §{sub-section}

<!-- Add BR-NN entries as needed. -->

## 4. Success Paths

<!-- Alternating actor/system steps. One block per scenario. -->

### SP-01: {Path Name}

```
Actor:   {action}
System:  {response}
Actor:   {action}
System:  {response}
Outcome: {final observable state}
```

<!-- Add SP-NN blocks as needed. -->

## 5. Failure Paths

<!-- One block per named error condition. -->

### FP-01: {Error Condition Name}

```
Trigger: {condition that causes this path}
Actor:   {action that triggers the error}
System:  {error response}
Outcome: {observable error state — what the actor sees}
```

<!-- Add FP-NN blocks as needed. -->

## 6. Acceptance Criteria

<!-- BDD format: Given / When / Then. -->
<!-- One observable outcome per AC. -->
<!-- Each AC maps to at least one BR or flow path. -->

**AC-01:** {Short title}

```
Given  {pre-condition}
When   {actor action}
Then   {observable outcome}
```

_Maps to: BR-01, SP-01_

**AC-02:** {Short title}

```
Given  {pre-condition}
When   {actor action}
Then   {observable outcome}
```

_Maps to: BR-02, FP-01_

<!-- Add AC-NN entries as needed. -->

## 7. Out of Scope

<!-- Explicit boundaries. Each item is one declarative sentence. -->

- Does not cover {behaviour that belongs to another FR or is out of scope for functional testing}.
- Does not cover {UI appearance, layout, or visual styling}.
- Does not cover {security, performance, or accessibility concerns}.

## 8. Dependencies

| Dependency                | Type                               | Hard / Soft   | Notes                        |
| ------------------------- | ---------------------------------- | ------------- | ---------------------------- |
| {FR-XX or component name} | {FR / System / Data / Environment} | {Hard / Soft} | {why this dependency exists} |

## 9. Test Notes

<!-- Seed data, environment pre-conditions, known risks, open questions. -->

- **Seed data:** {What must exist before testing — or "None required"}
- **Environment:** {Configuration flags, feature toggles, mocks needed — or "Standard environment"}
- **Risks:** {Ambiguous or volatile requirements}
- **Open questions:** {Items needing clarification before test design begins}
