---
name: gui-checklist-designer
description: >
  Design a structured, heuristic-grounded GUI testing checklist for any web application.
  Use this skill when you need to create a reusable shared GUI checklist grounded in Nielsen's
  10 heuristics, Shneiderman's 8 golden rules, Norman's 6 DOET-2013 principles, Waghmare
  per-widget guidelines, and WCAG 2.2 — organised by Interface Aspect (IA) categories.
  Invoke when asked to "design a GUI checklist", "create a UI testing checklist",
  "build a heuristic checklist", or "generate a checklist for GUI/UI testing".
version: 1.0.0
author: phatnguyen975
---

# GUI Checklist Designer Skill

## Overview

This skill generates a **structured, heuristic-grounded GUI testing checklist** for any web application. It synthesises five established frameworks into actionable, Pass/Fail-testable checklist items organised by Interface Aspect (IA) categories defined by the human.

The checklist produced by this skill is **shared** — it is designed to be used by a whole team across multiple screens. It captures _what_ to check and _why_, not _how to test on a specific screen_ — that screen-level detail is the responsibility of the execution layer.

**Heuristic foundations this skill always applies:**

- Nielsen's 10 Usability Heuristics (1994) — see [`resources/heuristics-reference.md`](resources/heuristics-reference.md)
- Shneiderman's 8 Golden Rules of Interface Design (2016) — see [`resources/heuristics-reference.md`](resources/heuristics-reference.md)
- Norman's 6 Design Principles, DOET 2013 — see [`resources/heuristics-reference.md`](resources/heuristics-reference.md)
- Waghmare Per-Widget Checklists (2009) — see [`resources/per-widget-guidelines.md`](resources/per-widget-guidelines.md)
- WCAG 2.2 Level AA (W3C, 2023) — see [`resources/wcag22-key-criteria.md`](resources/wcag22-key-criteria.md)

### Inputs

Invoke this skill by providing the following values. Required inputs must be supplied before the skill can run. Optional inputs enrich the output but are not mandatory.

```
REQUIRED
───────────────────────────────────────────────────────────────────────────────
sut_name      : string
                Full name of the System Under Test.
                Example: "EMS (Event Management System)"

sut_type      : string
                Application category and key characteristics.
                Example: "Web admin panel for CRUD-based event management"

ia_categories : list of objects [{id, name, scope}]
                Interface Aspect categories to generate items for.
                Each entry must have:
                  id    — short identifier (e.g. "IA-01")
                  name  — category name (e.g. "General UI Standards")
                  scope — comma-separated areas this IA covers (e.g. "layout, alignment, empty states, loading states")
                Provide at least one IA category.

min_items     : integer ≥ number of IAs
                Minimum total checklist items to generate across all IA categories.
                Each IA category receives at least ⌈min_items / number of IAs⌉ items.

output_dir    : string (path)
                Directory where output files will be written.
                Example: "docs/checklist"

OPTIONAL
───────────────────────────────────────────────────────────────────────────────
extra_context : string
                Specific widgets, interaction patterns, or constraints not covered by the widget list in per-widget-guidelines.md.
                Use this for i18n requirements, custom UI feedback constraints, non-standard widgets, or product-specific edge cases.
                Example: "EN/VI i18n toggle in header — all UI text must switch instantly; image upload enforces 4:3 aspect ratio
                for thumbnails; drag-drop reorder must show opacity-50 on dragged item and disable other controls during drag."
```

### Invoke Format

```
/gui-checklist-designer
  sut_name: "<value>"
  sut_type: "<value>"
  ia_categories:
    - id: "<IA-ID>"
      name: "<Category Name>"
      scope: "<comma-separated areas>"
    - id: "<IA-ID>"
      name: "<Category Name>"
      scope: "<comma-separated areas>"
  min_items: <integer>
  output_dir: "<path>"
  extra_context: "<text>"
```

### Outputs

| File                                   | Description                                | See Template                                                                   |
| -------------------------------------- | ------------------------------------------ | ------------------------------------------------------------------------------ |
| `{output_dir}/shared-gui-checklist.md` | Main checklist — one table per IA category | [`examples/example-checklist-output.md`](examples/example-checklist-output.md) |

## When to Use

- You need to create a **new shared GUI checklist** that a team will use across multiple screens.
- No approved UI checklist exists and you want one grounded in recognised industry standards.
- You are starting heuristic evaluation and need a systematic, documented coverage framework.
- You need to demonstrate which heuristics are covered and where (for audit or academic review).

## When NOT to Use

- A shared checklist already exists — use that instead.
- You are doing automated visual regression — use a dedicated visual testing tool.
- You need a usability test plan with real participants — use a dedicated usability testing framework.

## Output Template

> The `shared-gui-checklist.md` uses this exact structure for output format.

```markdown
# GUI Testing Checklist — {sut_name}

> **Generated by:** `gui-checklist-designer` skill (v1.0.0)  
> **SUT type:** {sut_type}  
> **Total items:** {N}

## Reference Sources

1. Nielsen, J. (1994). _10 Usability Heuristics for User Interface Design_. Nielsen Norman Group. https://www.nngroup.com/articles/ten-usability-heuristics/
2. Shneiderman, B. et al. (2016). _Designing the User Interface_ (6th ed.). Pearson. http://www.cs.umd.edu/hcil/DTUI6/
3. Norman, D. A. (2013). _The Design of Everyday Things_ (Revised ed.). Basic Books.
4. Waghmare, P. (2009). GUI Testing checklist. _GUI + Usability + Compatibility Testing_ course slide (FIT HCMUS).
5. W3C. (2023). _Web Content Accessibility Guidelines (WCAG) 2.2_. https://www.w3.org/TR/WCAG22/
6. [List any additional sources used for SUT-specific items derived from `extra_context`]

## {IA-ID} — {Category Name}

> **Scope:** {scope from `ia_categories` input}

| ID          | Description | Heuristic Ref | Widget / Area | Priority         |
| ----------- | ----------- | ------------- | ------------- | ---------------- |
| {IA-ID}-001 | {item}      | {ref}         | {widget}      | High / Med / Low |
| {IA-ID}-002 | ...         | ...           | ...           | ...              |

...
```

**Column definitions:**

| Column            | Format                 | Description                                                                                                                                                                                                                                    |
| ----------------- | ---------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**            | `{IA-ID}-{nnn}`        | Unique identifier scoped to the IA category. No gaps; no duplicates. Example: `IA01-003`.                                                                                                                                                      |
| **Description**   | Declarative statement  | States what must be true for the item to Pass. Written in the affirmative, specific enough to produce a consistent verdict across testers. Example: `"Each required form field displays a visible asterisk (*) adjacent to its label."`        |
| **Heuristic Ref** | Code(s) from reference | One or more framework codes: `N1–N10` (Nielsen), `S1–S8` (Shneiderman), `NOR1–NOR6` (Norman DOET-2013), `W-{Widget}` (Waghmare), `WCAG{n.n.n}` (WCAG 2.2). See [`resources/heuristics-reference.md`](resources/heuristics-reference.md).       |
| **Widget / Area** | Short noun phrase      | The UI element or screen area being evaluated. Examples: `Text input`, `Toast notification`, `Sidebar nav`, `Page layout`.                                                                                                                     |
| **Priority**      | `High` / `Med` / `Low` | **High** — failure prevents task completion or violates a legal accessibility requirement. **Med** — failure degrades experience or violates a usability heuristic. **Low** — cosmetic / minor inconsistency; does not affect task completion. |

## Core Principles

These principles govern item generation. They exist because a checklist that cannot be consistently graded by different testers produces inconsistent results and erodes trust.

1. **Every item must be independently verifiable.** A tester who has never seen the SUT must read the Description and reach a clear Pass, Fail, or N/A verdict — without asking anyone. If the verdict requires judgment about "good enough", rewrite the item.
2. **Description must be self-explanatory without How-to-Test steps.** The Description states what must be true. The tester infers how to verify it from the widget type and their professional knowledge.
3. **Widget coverage drives completeness, not heuristic titles.** Iterate systematically over the widget list (see [`resources/per-widget-guidelines.md`](resources/per-widget-guidelines.md)) before adding cross-cutting items. Ten items derived from "N5 Error Prevention" without widget specificity is a weaker checklist than one item per widget category that maps naturally to N5.
4. **Accessibility is a first-class concern, not an appendix.** WCAG 2.2 Level AA items are distributed throughout the IA categories — not isolated in a separate section — because accessibility defects appear in forms, navigation, and feedback just as much as in layout.
5. **`extra_context` enriches, it does not replace.** Non-standard widgets and SUT-specific constraints from `extra_context` add items alongside the standard widget sweep. They never substitute for it.
6. **Items must be evenly distributed.** Each IA category receives at least ⌈`min_items` / number of IAs⌉ items. If a category cannot reach this threshold from its scope, widen the widget sweep for that category before reducing the target.
7. **Strictly Blackbox GUI Testing.** Do NOT generate functional logic checks, performance/timing constraints, or DOM/HTML code-level checks (e.g., `aria-*`, `role`). All generated items must evaluate the visually observable state or interactive feedback of the UI.

## Workflow

### Step 1 — Validate Inputs

Check all required inputs are present and coherent:

- `sut_name` and `sut_type` are clear enough to contextualise items.
- `ia_categories` has at least one entry with `id`, `name`, and `scope`.
- `min_items` is a positive integer.
- `output_dir` is a valid path string.

If any required input is missing or ambiguous → stop and ask the human to clarify. Do not invent context.

Parse `extra_context` (if provided) and note:

- Non-standard widget types to add to the widget sweep.
- SUT-specific constraints that will enrich Description wording for standard widgets.

### Step 2 — Compile Widget Scope

Build the complete widget list for this run:

1. Start with the **standard widget inventory** from [`resources/per-widget-guidelines.md`](resources/per-widget-guidelines.md).
2. Append any **non-standard widgets** identified in `extra_context`.
3. Note SUT-specific behaviors that affect how standard widget items are described (e.g. "drag-drop must show opacity-50" → enriches the W-DragDrop item Description).

### Step 3 — Generate Items per IA Category

For each IA category in `ia_categories`, in order:

1. Read the `scope` field — it defines which widget types and UI areas belong to this category.
2. Select widgets from the compiled widget list that fall within this scope.
3. For each selected widget, generate items covering its key test dimensions (see [`resources/per-widget-guidelines.md`](resources/per-widget-guidelines.md) for dimensions per widget type).
4. Add cross-cutting items that apply to this category but are not widget-specific (e.g. layout alignment, i18n text switching, semantic colour use).
5. Add WCAG items relevant to this category's scope — distribute them as peer items, not as a sub-section.
6. Apply `extra_context` items that fall within this category's scope.

**Note:** Minimum items per category = ⌈`min_items` / number of IA categories⌉

### Step 4 — Apply the Actionability Filter

For every generated item, apply this test:

> _Can a tester who has never seen this SUT read this Description and produce a clear Pass, Fail, or N/A verdict within 2 minutes, without asking anyone?_

- **Yes** → keep the item as-is
- **No** → rewrite the Description to be more specific, or split into sub-items, or discard if the item is genuinely untestable without design specifications

**Common failures:**

- "The UI looks good" → untestable; discard or rewrite to a specific observable criterion
- "Colours are appropriate" → untestable without specs; rewrite as a contrast ratio check
- "Navigation is intuitive" → usability judgment, not a GUI check; out of scope for this skill

### Step 5 — Verify Coverage

Before proceeding, count items per IA category. Verify:

- Total ≥ `min_items`
- Each IA category has ≥ ⌈`min_items` / number of IAs⌉ items
- All widgets in the compiled scope have at least one item
- At least one WCAG item is present in each IA category (where applicable)
- All five heuristic frameworks appear across the full checklist

If any constraint fails → generate additional items before proceeding.

### Step 6 — Assign Priorities

For each item:

| Priority | Assign when                                                                                                       |
| -------- | ----------------------------------------------------------------------------------------------------------------- |
| **High** | Failure prevents a core user task from completing, or violates a WCAG Level A or AA requirement                   |
| **Med**  | Failure degrades experience, causes confusion, or violates a usability heuristic without blocking task completion |
| **Low**  | Cosmetic issue, minor visual inconsistency, or polish item that does not affect task completion                   |

When in doubt between High and Med, assign Med. Reserve High for genuine blockers.

### Step 7 — Self-Review

Run the Quality Checklist (see below) before presenting output. Fix any failures silently — do not present a checklist that fails its own quality check.

## Anti-Patterns

| Anti-Pattern                                                              | Why it fails                                                  | Correct approach                                                                  |
| ------------------------------------------------------------------------- | ------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| Subjective Descriptions ("UI looks clean", "feels balanced")              | Different testers reach different verdicts                    | Rewrite as observable, objective criterion                                        |
| One item per heuristic only                                               | Produces 10–26 items; misses most widget-level issues         | Iterate over widget types, not just heuristic titles                              |
| Descriptions as heuristic paraphrases ("System status is always visible") | Not testable without specifying where and how                 | Add widget name and a specific observable criterion                               |
| Colour-only status items ("badge is green when approved")                 | Fails WCAG 1.4.1; inaccessible                                | Add "…and includes a text or icon label to convey the same meaning"               |
| WCAG items isolated in a separate section                                 | Testers skip appendices; accessibility is treated as optional | Distribute WCAG items throughout IA categories as peer items                      |
| SUT-hardcoded names in standard items                                     | Checklist unusable for other projects                         | Keep standard items generic; put SUT specifics in `extra_context` enrichment only |

## Best Practices

- **Write Descriptions in the affirmative.** "Required fields display a red asterisk" is clearer than "Required fields should not be missing their asterisk".
- **One criterion per item.** An item testing both label placement and error colour is two items — split it. This enables precise, unambiguous grading.
- **Use consistent Heuristic Ref codes.** Always use the codes defined in this skill (`N1`, `S3`, `NOR2`, `W-Button`, `WCAG1.4.3`). This enables cross-document traceability.
- **Calibrate priority conservatively.** Inflating High items trains testers to ignore the priority column. Reserve High for genuine blockers and legal requirements.

## Quality Checklist

Run this before presenting output. All items must pass.

### Coverage

- [ ] Total item count ≥ `min_items`.
- [ ] Each IA category has ≥ ⌈`min_items` / number of IAs⌉ items.
- [ ] All widgets in the compiled scope (Step 2) have at least one item.
- [ ] At least one WCAG item present in each IA category (where applicable).

### Heuristic Grounding

- [ ] Every item has at least one Heuristic Ref code from the defined set.
- [ ] All five heuristic frameworks are represented across the full checklist.
- [ ] WCAG items are distributed across IA categories, not isolated in one section.

### Item Quality

- [ ] Every Description passes the actionability filter (clear Pass/Fail without asking anyone).
- [ ] No Description uses subjective language ("looks good", "feels intuitive", "appropriate").
- [ ] No two items are semantically identical.
- [ ] Status/state items do not rely on colour alone (WCAG 1.4.1 complied).
- [ ] All items with `extra_context` enrichment are correctly attributed to the right IA category.

### Format and Language

- [ ] All output content is written in English.
- [ ] ID format is `{IA-ID}-{nnn}` with no gaps or duplicates within each category.
- [ ] Priority column contains only `High`, `Med`, or `Low`.
- [ ] Table formatting is valid Markdown (header separator row present).
- [ ] Output files are written to `{output_dir}` as specified in inputs.

## Common Rationalisations to Reject

| Rationalisation                                                   | Why to reject                                                                                                          |
| ----------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| _"The heuristic is obvious; I don't need a specific item for it"_ | Heuristics are abstract. Testers need concrete, observable items. Generate the item.                                   |
| _"This is a standard widget; testers know how to verify it"_      | Checklists exist precisely because experienced testers forget standard items under time pressure. Include it.          |
| _"WCAG is for accessibility teams, not GUI testers"_              | WCAG Level AA is a legal requirement in many jurisdictions and catches real usability bugs. Include the items.         |
| _"I'll combine two criteria to save space"_                       | One item = one criterion. Combining hides one criterion behind the other at grading time.                              |
| _"The human will add missing items during review"_                | The human reviews for quality, not completeness. Generate a complete first pass.                                       |
| _"`min_items` is just 20, so I'll keep items thin"_               | `min_items` is a floor, not an excuse for shallow items. If 20 items cannot be well-targeted, the scope is too narrow. |

## Resources

| File                                   | Purpose                                                                                                                               |
| -------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| `resources/ia-definitions.md`          | Background on Interface Aspect (IA) category concept and how to define scopes well                                                    |
| `resources/heuristics-reference.md`    | Full reference for all five heuristic frameworks with codes and checklist application guidance                                        |
| `resources/per-widget-guidelines.md`   | Per-widget test dimensions (Waghmare 2009); used to drive widget sweep in item generation                                             |
| `resources/wcag22-key-criteria.md`     | Annotated list of WCAG 2.2 Level AA criteria relevant to web UI; includes codes and checklist application                             |
| `examples/example-checklist-output.md` | Complete sample output for a generic web admin panel — demonstrates all column formats, heuristic ref codes, and priority assignments |
