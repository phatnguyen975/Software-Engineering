# Self-Review Checklist

Run this against the full draft before output. It is a backstop, not the primary way ambiguity gets resolved — most of it should already be clean because grilling happened before and during drafting (`references/grilling.md`). If this checklist finds something, that's a sign to go back to grilling for that specific point, not to patch it with a guess.

## 1. Traceability

- [ ] Every `FR-NN` appears at least once in Flow or in a Contract endpoint's `Satisfies:` line.
- [ ] Every `AC-NN` maps back to exactly one `FR-NN` via its `Satisfies:` line.
- [ ] Every status code listed in a Contract endpoint has a corresponding line in Errors (or is a success code and appears in the Success Response).
- [ ] Every `BR-NN` in Data → Invariants is cited at least once outside of Data (in a Contract validation rule, an Error condition, or an Edge Case).
- [ ] No `FR-NN`, `BR-NN`, or `AC-NN` number is skipped or reused.

## 2. Concreteness

- [ ] No section contains "etc.", "and so on", "other errors may occur", "handles edge cases appropriately", or similar gestures instead of enumeration.
- [ ] Every Contract endpoint's request/response has a full JSON schema, not a prose description of it.
- [ ] Every Acceptance Criterion uses concrete values, not abstract placeholders like "the amount" or "some user".
- [ ] No sentence in Functional Requirements, Data → Invariants, or Contract validation rules hedges with "presumably", "likely", "should probably", "for simplicity assume".

## 3. Consistency

- [ ] Every actor name used in Flow, Contract (`Required role / Auth`), and Edge Cases (permissions-related entries) matches a name defined in Actors exactly — no synonyms.
- [ ] Status codes, field names, and error-shape conventions used in this Contract match the system's existing conventions stated in Global Standards Applied (or a deliberate deviation is called out explicitly, not silent).
- [ ] If an existing endpoint's behavior changes, the Contract states both **Before** and **After** — not just the new behavior.

## 4. Completeness

- [ ] Every heading from `references/spec-template.md` (or the user-supplied template) is present in the output, even if its content is short (e.g. "None." for Schema Changes).
- [ ] There is no `Assumptions` or `Open Questions` section anywhere in the output. If one exists, every item in it needs to go back through grilling, not stay in the spec.
- [ ] Acceptance Criteria covers at minimum: the feature's core logic, one failure path, and one concurrency or duplicate-action case, where the feature involves concurrent access at all.

## 5. The implementer read-through

Read the finished spec once more as if you were the engineer (or agent) about to implement it with no further access to the user. For every question that occurs to you during that read — "wait, what happens if...", "which endpoint handles...", "is this field required?" — check whether the spec already answers it unambiguously.

- If yes, move on.
- If no, this is a real gap: return to `references/grilling.md`, resolve it with the user, and update the relevant section(s) before finalizing. Do not add a note instead of resolving it.
