<!--
This is the skeleton to copy when starting a new spec.md. For what belongs in each section and
the exact sentence structure to use, read the matching file in references/sections/ before
filling that section in. Remove every HTML comment (like this one) from the final output.
-->

# <Feature Name>

> **Status:** Draft | Approved  
> **Owner:** <name>  
> **Feature of:** <system name>  
> **Based on:** <input source(s)>

## Goal

<!-- One sentence. The rule is simply: one sentence, externally observable outcome, not
     implementation detail. If it doesn't fit in one sentence, the scope isn't settled
     — go back to grilling. -->

## Scope

### In Scope

- ...

### Out of Scope

- ...

## Actors

<!-- See references/sections/actors.md -->

| Actor | Description | Access / Auth |
| :---- | :---------- | :------------ |

## Functional Requirements

<!-- See references/sections/functional-requirements.md -->

- **FR-01:** ...

## Flow

<!-- See references/sections/flow.md -->

1. ...

## Contract

<!-- See references/sections/contract.md -->

### Global Standards Applied

- ...

### <METHOD> <path>

## Data

<!-- See references/sections/data.md -->

### Tables Accessed

| Table | Read / Write | Notes |
| :---- | :----------- | :---- |

### Schema Changes

| Table | Change |
| :---- | :----- |

### Invariants

- **BR-01:** ...

## Edge Cases

<!-- See references/sections/edge-cases.md -->

- ...

## Errors

<!-- See references/sections/errors.md -->

| Condition | User sees | System does |
| :-------- | :-------- | :---------- |

## Acceptance Criteria

<!-- See references/sections/acceptance-criteria.md -->

### AC-01: ...

```gherkin
Given ...
When ...
Then ...
```

**Satisfies:** ...

## Constraints

<!-- See references/sections/constraints.md -->

### Technical Constraints

- ...

### Non-Functional Requirements

<!-- omit this subheading entirely if the feature introduces none -->

- ...
