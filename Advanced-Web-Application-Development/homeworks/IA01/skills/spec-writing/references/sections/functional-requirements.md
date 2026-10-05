# Section: Functional Requirements

## Purpose

This is the atomic list of capabilities the system must have — the thing every other section points back to. Contract endpoints cite the FR they satisfy; Acceptance Criteria cite the FR they test; Flow steps cite the FR they fulfill. Without a numbered FR list, there's nothing for those cross-references to point at, and a reviewer can no longer check that every requirement actually got implemented (or tested).

## What to include

- One entry per **discrete, independently testable capability**. "Discrete" means: if you can imagine implementing or testing half of it without the other half, it's two FRs, not one.
- Written from the system's point of view — what the system must do, not what the user does (that belongs in Flow).

## Sentence structure

Use RFC 2119-style requirement keywords, written in full caps, exactly as below:

```
FR-NN: The system SHALL <capability>.
FR-NN: The system MUST <capability>.
```

- `SHALL` / `MUST` — mandatory, no exceptions. Use this for the overwhelming majority of FRs in a spec; a feature spec is describing required behavior, not nice-to-haves.
- `SHOULD` — a recommended behavior that could reasonably be overridden with justification. Rare in a feature spec; if you reach for it, add a one-clause reason inline (`SHOULD ... unless <case>`).
- `MAY` — genuinely optional behavior the system is permitted but not required to have. Also rare.

Don't use `SHALL`/`MUST`/`SHOULD`/`MAY` for anything except this keyword's defined meaning — not as ordinary English verbs elsewhere in the sentence.

## Numbering

- `FR-01`, `FR-02`, ... — sequential, two digits, zero-padded.
- IDs are permanent once assigned. If an FR is cut later, leave the number retired rather than reusing it or renumbering everything after it — this keeps external references (Contract, AC) stable.

## Template

```markdown
## Functional Requirements

- **FR-01:** The system SHALL <capability>.
- **FR-02:** The system MUST <capability>.
```

## Example

```markdown
## Functional Requirements

- **FR-01:** The system SHALL allow a subscriber to update their notification channel preferences (email, SMS, push) independently for each notification category.
- **FR-02:** The system MUST reject a preference update that disables every channel for a category marked as non-optional.
- **FR-03:** The system SHALL apply an updated preference to notifications queued after the update, without affecting notifications already in flight.
```

## Common mistakes

- Writing an FR as a user action ("The subscriber updates their preferences") instead of a system obligation ("The system SHALL allow a subscriber to update..."). The user's action belongs in Flow; the FR is what the system must support.
- Bundling two capabilities into one FR because they're related — if Contract or Acceptance Criteria would need to test them separately, they're separate FRs.
- Using lowercase "must"/"shall" as ordinary language elsewhere in the spec — reserve the fully capitalized keyword for FR/BR statements specifically, so it stays meaningful where it appears.
