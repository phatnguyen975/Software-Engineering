# Section: Constraints

## Purpose

States the boundaries the implementation must work within — things that are true regardless of how the feature is built, as opposed to Functional Requirements (what the feature must do) or Invariants (what must always hold about the data).

## What to include

### Technical Constraints

Real constraints only — taken from the input, not invented. Typical examples: no new external dependency, must work on the current schema without a migration, must be backward-compatible with an existing client, must reuse an existing library/service rather than introduce a new one, a specific deadline or resource limit that actually affects design choices.

### Non-Functional Requirements (conditional)

Include this subheading **only if** the feature genuinely introduces a non-functional requirement: a latency budget, an availability target, a data-retention or deletion requirement, a specific security requirement (encryption at rest, audit logging) beyond what the system already does. Omit the subheading entirely if none apply — don't manufacture an NFR just to fill the section.

## Template

```markdown
## Constraints

### Technical Constraints

- ...

### Non-Functional Requirements

<!-- omit this subheading entirely if none apply -->

- ...
```

## Example

```markdown
## Constraints

### Technical Constraints

- No new external dependency; reuse the existing event bus for the confirmation event.
- Must work against the current `notification_preferences` schema plus the one additive column in Data → Schema Changes — no destructive migration.
- Must remain backward-compatible with the mobile app's current `GET /preferences` response shape (additive changes only).

### Non-Functional Requirements

- Preference updates must be reflected for the Notification Service within 5 seconds of a successful write (read-after-write within that window, not necessarily immediate consistency).
```

## Common mistakes

- Listing constraints that are actually Functional Requirements in disguise ("must validate the input" — that's an FR, not a constraint).
- Adding an NFR subsection with generic, unverifiable items ("must be fast", "must be secure") instead of a concrete, checkable target — if there's no concrete target in the input and none was established through grilling, leave the subsection out rather than filling it with vague language.
