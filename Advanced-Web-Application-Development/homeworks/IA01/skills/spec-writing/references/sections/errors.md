# Section: Errors

## Purpose

While Contract specifies the API-level shape of each error (status code, JSON schema, per endpoint), this section specifies the **user-facing and system-level view**: for a given failure condition, what does the person on the other end actually see, and what does the system do internally (what state changes, what stays unchanged). It's a compact cross-reference, not a restatement of Contract's payloads.

## What to include

One row per distinct failure condition that's meaningful at the feature level — this can span multiple endpoints, or cover conditions that aren't tied to a single API call at all (e.g. a webhook arriving late, a background job failing).

- **Condition** — precise enough to map back to a specific Contract error block or Edge Case.
- **User sees** — the actual message or UI state, not "an error is shown."
- **System does** — what changes (or explicitly doesn't change) as a result: is the operation retried, is partial state rolled back, is anything logged or escalated.

## Format

A table, deliberately — unlike Contract's per-endpoint errors, these rows are a condition → outcome mapping, not a payload, so a compact table stays readable here.

```markdown
## Errors

| Condition | User sees | System does |
| :-------- | :-------- | :---------- |
```

## Example

```markdown
## Errors

| Condition                                                       | User sees                                                                          | System does                                                                                   |
| :-------------------------------------------------------------- | :--------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------- |
| Subscriber disables every channel for a non-optional category   | Inline validation message: "At least one channel must stay on for Account Alerts." | Request rejected (422 VALIDATION_FAILED, see Contract); no write occurs.                      |
| Support Agent attempts to edit preferences                      | "You don't have permission to change this."                                        | Request rejected (403 FORBIDDEN, see Contract); no write occurs; attempt is logged for audit. |
| Notification Service reads preferences for a deleted subscriber | N/A — no end user involved                                                         | Notification is silently dropped; no retry; logged at warning level.                          |
```

## Common mistakes

- Duplicating Contract's JSON payloads here instead of pointing back to them — this section is the user/business view, Contract already owns the schema.
- Leaving "User sees" generic ("an error message") when the actual copy or UI state is knowable from the input or from a reasonable, confirmed decision.
- Omitting conditions that don't map to a single HTTP call (webhook timing, background job failures) just because they don't fit neatly into "an endpoint returned an error."
