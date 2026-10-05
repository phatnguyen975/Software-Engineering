# Grilling — Resolving Ambiguity Before Drafting

This is the mechanism that replaces an "Assumptions" or "Open Questions" section. Instead of writing down a guess (or flagging a gap for someone to resolve later), the skill asks the user directly, in a structured way, before the relevant part of the spec gets written.

## When a question is required

Ask when a point in the input has **two or more reasonable interpretations, and each one would make some part of the spec come out differently** — any section. The bar is always "would the spec come out differently depending on the answer", not "does this happen to be a section with a strict schema".

Do **not** ask about:

- Things that only affect wording or presentation, not behavior.
- Things with one obviously correct reading given the rest of the input (don't ask what you can safely infer — see Core Principle 1 in `SKILL.md`).
- Things genuinely out of scope for this feature (belongs in Scope → Out of Scope, not a question).

## Building the question set

Don't ask questions one at a time as they're noticed while reading — that produces a scattered, exhausting back-and-forth and re-litigates the same decision multiple times. Instead:

1. **Read the whole input first**, and list every point that meets the bar above.
2. **Rank by blast radius.** A question that would change several sections at once goes first (e.g. "does each participant enter their own amount, or is it split evenly?" touches Goal, Flow, Contract, Data, and Acceptance Criteria all at once). A question that only affects one small detail in one section goes later, or may turn out to be moot once an earlier one is answered.
3. **Prune as you go.** Once a higher-ranked question is answered, drop any lower-ranked question it already resolves. Don't ask something the user effectively already answered.
4. **Batch tightly related questions** into the same round rather than asking them one message at a time — but don't dump an unrelated grab-bag together just because they were both noticed early. A "round" should feel like one coherent decision, not an interrogation.
5. **Repeat.** After each round of answers, re-scan the now-more-specific picture for any new ambiguity the answers themselves introduced, and continue until nothing left would change the spec's behavior.

This is a decision tree, not a flat checklist: later questions can depend on earlier answers, and branches that become irrelevant should be dropped rather than asked anyway.

## Question format

Every question has three parts. Don't skip the recommendation — a bare open-ended question is slower for the user to answer than one with a sensible default to confirm or override.

```
Question: <the question, phrased so it's clear what part of the spec depends on the answer>
Recommended options (best first):
  1. <option> — recommended because <one short, honest reason>
  2. <alternative option>
  3. <alternative option>
Or: tell me your own answer if none of these fit.
```

Guidelines for the options themselves:

- Base the ranking on what's actually implied by the input (existing conventions, similar features already in the system, what the stated goal suggests) — not on which option is easiest to spec.
- Keep it to 2–4 options. More than that is usually a sign the question should be split in two.
- If the environment has a structured choice UI available (e.g. a tool that renders selectable options), use it. If not, ask the same three-part structure as plain conversational text. The mechanism doesn't depend on any particular tool — it depends on always offering a ranked recommendation plus a free-form escape hatch.

## Stopping condition

Grilling is done when **every section of the spec** can be written with a specific value or behavior, not a placeholder, not a hedge. This is a strict bar: if you notice yourself about to write "presumably", "likely", "typically", or "for simplicity, assume" in _any_ section, that's the signal to ask one more question instead of continuing.

Note that this also means a single grilling pass before step 3 of the main workflow often isn't enough by itself — drafting a section can surface a new ambiguity that wasn't visible from the raw input. When that happens mid-draft, stop, ask, get the answer, and only then continue that section. The self-review pass (`references/self-review-checklist.md`) exists as a final backstop for anything that still slipped through, not as the primary mechanism.
