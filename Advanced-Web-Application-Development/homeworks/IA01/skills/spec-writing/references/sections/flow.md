# Section: Flow

## Purpose

A numbered walkthrough of the happy path — what actually happens, step by step, when the feature works as intended. This is where a reader builds the mental model before diving into the Contract's technical detail. It also ties Functional Requirements to a concrete sequence, which is often where a missing or wrongly-scoped FR becomes obvious.

## What to include

- Only the **happy path**. Failure paths belong in Errors; edge cases belong in Edge Cases. Mixing them into Flow makes the main sequence hard to follow.
- Every step names an explicit actor (using the exact name from Actors) and one action.
- The FR each step fulfills, so Flow and Functional Requirements stay in sync.

## Sentence structure

```
<N>. <Actor> <active-voice, present-tense verb> <object/outcome>. (FR-NN)
```

- One action per step. If a step needs "and" to describe what happens, it's probably two steps.
- Active voice, present tense, throughout — not "the amount will be validated" but "the system validates the amount".
- End each step with the FR reference in parentheses. A system-internal step (no end-user action) still gets a step and still names its actor — usually "The system."

## Template

```markdown
## Flow

1. <Actor> <action>. (FR-NN)
2. <Actor> <action>. (FR-NN)
```

## Example

```markdown
## Flow

1. The subscriber opens their notification preferences screen. (FR-01)
2. The subscriber toggles the email channel off for the "Marketing" category. (FR-01)
3. The system validates that at least one channel remains enabled for every non-optional category. (FR-02)
4. The system saves the updated preferences and confirms the change to the subscriber. (FR-01)
5. The Notification Service reads the updated preferences the next time it queues a notification for this subscriber. (FR-03)
```

## Common mistakes

- Slipping a failure branch into the middle of Flow ("...unless the category is required, in which case..."). Keep Flow to the single happy path; the failure case goes in Errors.
- Leaving the actor implicit ("the preferences get validated") instead of naming who or what does it ("the system validates...").
- Describing implementation mechanics (which function gets called, which table gets written) rather than observable behavior — that level of detail belongs in Contract and Data, not Flow.
