# Section: Acceptance Criteria

## Purpose

Turns each Functional Requirement into something that can be run as a test with no further decisions required. This is the section that gets checked against most directly — if a scenario can't fail, it isn't testing anything, and if it needs interpretation to turn into code, it isn't done yet.

## What to include

- One scenario per distinct, testable behavior — not one scenario per FR necessarily; a single FR can need multiple scenarios (the normal case, a boundary, a rejection).
- **Concrete values** throughout — actual numbers, actual field values, not placeholders. A scenario using "the amount" instead of "50,000" cannot be run as-is; whoever implements it would have to invent a number, which reintroduces exactly the ambiguity this skill exists to remove.
- A `Satisfies:` line mapping the scenario back to exactly one `FR-NN`.

## Format: Gherkin

````markdown
### AC-NN: <short scenario title>

```gherkin
Given <a concrete starting state>
When <a concrete action>
Then <a concrete, checkable outcome>
```

**Satisfies:** FR-NN
````

- `Given` sets up state — concrete enough that there's no question what "the state" is.
- `When` is a single action, ideally one that maps directly to a Contract endpoint call.
- `Then` is the outcome, specific enough to assert on directly: a status code, a field value, an absence of a side effect.
- Add `And` lines under any of the three if genuinely needed, but keep each scenario testing one behavior — if `Then` needs several unrelated assertions, it's probably more than one scenario.

## Numbering

`AC-01`, `AC-02`, ... — same zero-padded, two-digit, sequential, permanent-once-assigned scheme as `FR-NN` and `BR-NN`.

## Minimum coverage

At minimum, Acceptance Criteria should cover: the feature's core logic/calculation, at least one failure path, and — whenever the feature involves any shared or concurrently-accessed state — at least one concurrency case. If the feature genuinely has no concurrency surface, that minimum can be dropped, but check first: most features that touch shared data do have one.

## Template

````markdown
## Acceptance Criteria

### AC-01: <scenario title>

```gherkin
Given ...
When ...
Then ...
```

**Satisfies:** FR-NN
````

## Example

````markdown
## Acceptance Criteria

### AC-01: Subscriber disables one channel for an optional category

```gherkin
Given subscriber `sub_123` has email and push enabled for category `Marketing`
When they send `PATCH /preferences` disabling `push` for `Marketing`
Then the response is `200 OK` with `push: false, email: true` for `Marketing`, and `updated_at` reflects the request time
```

**Satisfies:** FR-01

### AC-02: Subscriber attempts to disable every channel for a non-optional category

```gherkin
Given subscriber `sub_123` has `email: true` as the only channel enabled for category `Account Alerts` (marked non-optional)
When they send `PATCH /preferences` disabling `email` for `Account Alerts` with no other channel enabled
Then the response is `422 VALIDATION_FAILED` and the stored preference for `Account Alerts` is unchanged
```

**Satisfies:** FR-02

### AC-03: Two updates to the same category arrive concurrently

```gherkin
Given subscriber `sub_123` currently has `email: true` for `Marketing`
When two `PATCH /preferences` requests for `Marketing` — one disabling `email`, one enabling `sms` — are sent within the same second
Then the request with the later `updated_at` timestamp is the one reflected in the final stored state, and the response to each request reflects only its own change at the time it was applied
```

**Satisfies:** FR-01
````

## Common mistakes

- Writing "Then the preferences are updated correctly" — this cannot fail, which means it isn't an acceptance criterion at all, just a restatement of the goal.
- Using a placeholder instead of a value ("the user", "the amount") — if you don't have a concrete value yet, that's a sign grilling isn't finished for this part of the feature.
- Skipping the failure-path or concurrency scenario because the happy path "is the important one" — these are exactly the scenarios most likely to be missing from a first draft and most valuable to have explicit.
