# Section: Actors

## Purpose

Establishes a single, fixed vocabulary for "who" before any other section uses it. Flow refers to actors by name. Contract's `Required role / Auth` field refers to them. Edge Cases' permission scenarios refer to them. If this section doesn't exist first, those three sections drift — one calls someone "the customer", another calls the same person "the user", and a reader can no longer tell if that's the same actor or two different ones.

## What to include

- Every actor that **initiates or receives** an interaction covered by this feature: human roles (end users, staff roles with distinct permissions) and non-human actors (another internal service, an external provider calling back via webhook, a scheduled job, a background worker).
- For each actor: a short description of who/what they are, and how the system authenticates or authorizes them (session type, token type, role claim, signature scheme — whatever applies).
- Only actors relevant to **this feature**. Don't inventory every role the whole system has if most of them never touch this feature.

## What NOT to include

- Internal implementation detail that isn't about who is calling (e.g. "the database" is not an actor).
- Actors from the wider system that this feature doesn't interact with at all.

## Template

```markdown
## Actors

| Actor  | Description                   | Access / Auth                                        |
| :----- | :---------------------------- | :--------------------------------------------------- |
| <name> | <one line: who/what they are> | <how the system authenticates/authorizes this actor> |
```

## Example

```markdown
## Actors

| Actor                | Description                                                                         | Access / Auth                               |
| :------------------- | :---------------------------------------------------------------------------------- | :------------------------------------------ |
| Subscriber           | An authenticated end user managing their own notification preferences               | session cookie, scoped to their own account |
| Support Agent        | Staff member who can view (but not edit) a subscriber's preferences on their behalf | staff JWT, role `support`                   |
| Notification Service | Internal service that reads preferences before sending a notification               | internal service token                      |
```

## Common mistakes

- Defining an actor here and then calling it something slightly different later ("Subscriber" here, "user" in Flow). Use the exact string everywhere.
- Omitting a non-human actor (a webhook sender, a scheduled job) because it "doesn't feel like a user" — if it calls an endpoint or triggers behavior, it needs an entry so Contract's auth field has something to point to.
