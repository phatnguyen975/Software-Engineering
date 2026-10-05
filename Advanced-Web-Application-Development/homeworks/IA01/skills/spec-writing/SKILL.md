---
name: spec-writing
description: Write an implementation-ready feature specification (spec.md) for a feature being added to an existing system — with a traceable FR/BR/AC numbering scheme, concrete API contracts, explicit data changes, and every ambiguity resolved through structured clarifying questions. Use this skill whenever the user asks to "write a spec", "draft a spec", "spec out this feature", "create an API contract", wants a one-pager that a developer or an AI coding agent could implement without asking follow-up questions, mentions spec-driven development, or hands over a system brief / existing docs / codebase description plus a feature description and wants a formal spec produced from it — even if they just say "write up what this feature needs" without using the word "spec" explicitly. Also use it to review or tighten a spec someone already drafted.
---

# Spec Writing

## Overview

This skill turns a feature idea plus a description of an existing system into a single, implementation-ready `spec.md`. The spec is built so that a developer — or an AI coding agent — can implement the feature without needing to come back and ask anything. It does this through two things working together: a strict, numbered template (Functional Requirements, Contract, Data, Acceptance Criteria, all cross-referenced), and a mandatory clarification step — see `references/grilling.md` — that resolves every ambiguous point with the user _before_ it gets written down.

The skill is domain-agnostic: it works the same way for a REST API feature, a batch job, a UI-only change, or a data-pipeline addition. Domain-specific judgment (is this field a string or an enum? does this system use webhooks?) always comes from the input the user provides, never from this skill's own assumptions.

## When to Use

- The user asks to write, draft, or review a feature spec, an API contract, or a "what needs to be built" document for something being added to an existing system.
- The user wants a document precise enough that someone else (a teammate, a contractor, an AI agent) could implement the feature without asking clarifying questions.
- The user mentions spec-driven development, RFC-style specs, or hands over a system brief / README / schema / existing API docs and describes a feature to add.
- The user has a rubric, style guide, or template they want the spec held to — this skill should use it as the output shape, not just as inspiration.

## When NOT to Use

- **Greenfield product definition.** If there is no existing system to extend — the user wants a product charter, a PRD for a brand-new product, or a pitch — this skill's "existing system" framing (Contract changes, Data changes, "Before/After" behavior) doesn't fit. A lighter product-brief format is more appropriate.
- **The user wants code, not a document.** If they want the feature implemented now rather than specified first, go straight to implementation; offer this skill only if they'd benefit from specifying first.
- **High-level architecture or system design docs** that aren't scoped to one feature (e.g. "design our whole notification system") — those need a different shape (alternatives considered, trade-off analysis) that this template doesn't cover well.
- **Pure documentation of existing behavior** with nothing new being added — this skill specifies change, not current-state documentation.

## Inputs

| Input                           | Type                                                                                                    | Required?                                                                                                                                         |
| :------------------------------ | :------------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------------------------------------------------ |
| System context                  | Free text in the prompt, and/or attached files (brief, README, schema, existing API docs, OpenAPI spec) | **Required** — at least one source of ground truth about the system being extended                                                                |
| Feature description             | Free text in the prompt, and/or attached files                                                          | **Required**                                                                                                                                      |
| Existing spec template          | Attached file                                                                                           | Optional — if provided, use it instead of `references/spec-template.md`, but still apply the numbering, grilling, and section-content rules below |
| Grading criteria / rubric       | Attached file                                                                                           | Optional — used only to self-check the draft; never copied into the spec itself                                                                   |
| Answers to clarifying questions | Conversational, gathered during the workflow                                                            | Required whenever an ambiguity is found — this is not a file, it's an interactive step (see Workflow, step 2)                                     |

## Output

A single `spec.md` following `references/spec-template.md`. Every section in that template is mandatory in structure (the heading exists), but a section's _content_ can legitimately be short — e.g. "None." for Schema Changes — when the feature genuinely doesn't touch it. What's never acceptable is a missing section or a vague one ("the API returns the shares").

## Core Principles

1. **Never invent a fact.** Anything about the existing system (schema, endpoints, conventions, business rules) must come from the input or be a direct, safe inference from it (e.g. following a naming convention the input already establishes). If it isn't there and can't be safely inferred, it's a question, not a guess.
2. **No silent assumptions, no "Open Questions" section.** Ambiguity is resolved by asking — see `references/grilling.md` — before the relevant part of the spec is written. The template has no place to park unresolved questions, by design.
3. **Everything traces.** Every `FR-NN` shows up in Flow or Contract. Every `AC-NN` maps back to exactly one FR. Every status code in Contract has a matching line in Errors. This is what makes the spec checkable rather than just readable.
4. **Enumerate, don't gesture.** "Other errors may occur", "etc.", "handles edge cases appropriately" are all failure states. If it's a real possibility, it gets a line.
5. **Stay generic as a skill, concrete as an output.** The _skill_ must not bake in one project's vocabulary or assumptions. The _spec_ it produces should be as concrete and domain-specific as the input allows.

## Workflow

1. **Ingest.** Read everything provided. Sort it into: system context (facts — never alter or embellish), grading criteria if any (self-check only, never quoted into the spec), and an existing template if any (use it as the output shape instead of the bundled one).
2. **Resolve ambiguity before drafting.** Follow `references/grilling.md`: build the set of ambiguous points, rank them by how much of the spec they'd change, and ask the user — one well-scoped round at a time, each question with recommended options plus room for a free-form answer. Do not start drafting a section that still has an open ambiguity in it.
3. **Draft section by section.** Start from `references/spec-template.md` (or the user's own template, per step 1). For each section, read the matching file in `references/sections/` for the exact content checklist and sentence structure, then write it:
   - `references/sections/actors.md`
   - `references/sections/functional-requirements.md`
   - `references/sections/flow.md`
   - `references/sections/contract.md`
   - `references/sections/data.md`
   - `references/sections/edge-cases.md`
   - `references/sections/errors.md`
   - `references/sections/acceptance-criteria.md`
   - `references/sections/constraints.md`
4. **Number consistently.** `FR-01, FR-02, ...` and `BR-01, BR-02, ...` (zero-padded, two digits, sequential, never reused or renumbered later) and `AC-01, AC-02, ...` for Gherkin scenarios. Cross-reference them as you go rather than as an afterthought — it's much cheaper to keep IDs straight while writing than to reconcile them after.
5. **Self-review.** Run `references/self-review-checklist.md` against the draft. Any gap it finds is a reason to go back to step 2 and grill further — never to patch the gap with a guess or a note.
6. **Finalize.** Strip any leftover template comments, confirm every heading from the template is present, and output the final `spec.md`.

## Anti-Patterns

- Writing a vague Contract ("the API returns the shares") instead of concrete payloads and status codes.
- "Other errors may occur" / "etc." / "handles edge cases appropriately" instead of enumerating.
- Combining two distinct behaviors into a single `FR-NN` because it's faster to write.
- Adding an `Assumptions` or `Open Questions` section to work around not having asked — the template intentionally has no place for this.
- Padding Edge Cases with categories that don't actually apply to this feature just to hit a count.
- Inserting a Mermaid diagram by default — only add one when a state transition or a multi-table flow genuinely needs a picture to be unambiguous (see `references/sections/data.md`).
- Copying wording from a grading rubric or style guide into the spec body instead of using it only to self-check.

## Best Practices

- Define every actor once in Actors, then reuse the exact same name in Flow, Contract, and Edge Cases — don't let "customer" in one section become "user" in another.
- Cite a `BR-NN` from Contract or Errors instead of restating the rule in different words each time.
- Keep Goal to one sentence — if it won't fit, the scope isn't settled yet.
- Use concrete values in Acceptance Criteria (`"the split amount is 50,000"`, not `"the amount"`).
- Prefer asking one well-scoped question that resolves several downstream details over several shallow ones.

## Common Rationalizations to Reject

| Rationalization                                                             | Why it's rejected                                                                                                                                                                            |
| :-------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| "The user seems busy, I'll just pick a reasonable default."                 | Grilling exists precisely for this. A quick, well-scoped question with a recommended option costs the user almost nothing to confirm — far less than discovering the wrong assumption later. |
| "I'll just note this as an assumption so I don't have to stop and ask."     | The template has no Assumptions section on purpose. An assumption silently baked into a spec is indistinguishable from a wrong one until someone implements it.                              |
| "The brief doesn't say, so I'll invent a plausible name/shape and move on." | Invent nothing that isn't a direct, safe inference from what's given (e.g. matching an existing naming convention). Anything else is a question.                                             |
| "I already asked three questions, I should stop bothering them."            | Stop when ambiguity is resolved, not when a round number of questions has been asked. One more well-scoped question is cheaper than a wrong spec.                                            |
| "A rubric or style guide said N things are enough, so N is the target."     | A rubric is for self-checking the output, never a ceiling or floor baked into the skill's own judgment of what's "enough." Cover what the feature actually needs.                            |

## Reference Files

| File                                  | Read it when                                                                         |
| :------------------------------------ | :----------------------------------------------------------------------------------- |
| `references/grilling.md`              | Before drafting, whenever an ambiguous point is found                                |
| `references/spec-template.md`         | At the start of drafting, as the skeleton to copy                                    |
| `references/sections/*.md`            | While writing the matching section, for its content checklist and sentence structure |
| `references/self-review-checklist.md` | After the first full draft, before output                                            |
