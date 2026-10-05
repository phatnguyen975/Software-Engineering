# Section: Edge Cases

## Purpose

Captures the scenarios that aren't the happy path and aren't a straightforward error either — the places a spec is most often thin, because they require imagining what happens outside normal operation rather than just describing normal operation.

## What to include

Not a fixed checklist, and not a quota. The following categories are a **starting point** for thinking through a feature — consider each one, include it if it genuinely applies to this feature, and add any other edge case the feature surfaces that isn't covered by these categories. Do not pad the list with a category that doesn't actually apply just to reach a number, and do not stop early just because a few categories were covered — the goal is genuine coverage of what this specific feature needs, not a checklist tally.

- **Empty / first-run state** — no data yet, a brand-new entity, the very first time this path is exercised.
- **Partial failure** — one part of a multi-step operation succeeds and another doesn't (e.g. a payment succeeds but the confirmation write fails).
- **Permissions** — an actor attempts something outside what they're authorized for, referencing the exact actor names from Actors.
- **Concurrency & duplicates** — two actions happen at (or near) the same time, or the same action is repeated (retry, double-submit, duplicate webhook delivery).
- **Limits** — size, count, rate, or time limits, and what happens when one is hit.

Other categories to consider when relevant to the feature: out-of-order events, clock/timezone edge cases, very large or very small input values, an actor or entity that disappears mid-flow (e.g. account deleted while an operation is in progress).

## Sentence structure

```
- **<short name>:** <scenario> → <expected system behavior>.
```

The scenario should be specific enough to turn directly into a test case. "Handles concurrency correctly" is not a scenario — "two requests to update the same field arrive within the same second" is.

## Template

```markdown
## Edge Cases

- **<short name>:** <scenario> → <expected behavior>.
```

## Example

```markdown
## Edge Cases

- **First preference update ever:** a subscriber with no existing notification_preferences row updates a setting → the system creates the row with defaults for every other category, then applies the requested change.
- **Partial failure on save:** the preferences write succeeds but the confirmation event fails to publish → the preference change is still considered saved; the system retries the event publish separately and does not roll back the write.
- **Support agent attempts an edit:** a Support Agent (read-only per Actors) sends a write request → rejected with 403 FORBIDDEN; no row is modified.
- **Concurrent updates to the same category:** two updates to the same subscriber's same category arrive within the same second → the later `updated_at` timestamp wins; the earlier write is silently superseded, not merged.
- **Duplicate submit:** the same update request is sent twice with the same idempotency key → the second request returns the same response as the first without writing again.
```

## Common mistakes

- Treating the five starter categories as mandatory boxes to tick even when one plainly doesn't apply to this feature — write "Not applicable: <why>" only if the category was genuinely considered and ruled out, don't silently skip it either.
- Writing a scenario vague enough that two different engineers would implement it differently ("handles duplicates gracefully").
- Confusing an edge case with an ordinary error path that already belongs in Errors (e.g. "invalid input" is an Errors entry, not an Edge Case, unless there's something genuinely unusual about when/how it occurs).
