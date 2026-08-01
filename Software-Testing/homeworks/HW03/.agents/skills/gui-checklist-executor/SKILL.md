---
name: gui-checklist-executor
description: >
  Execute a shared GUI checklist against a specific screen of a web application. Use this skill
  when you need to test a screen against an existing GUI checklist, generate screen-specific
  How-to-Test steps, evaluate each item as Pass/Fail/NA/Need Human, produce edge case tests,
  and group failures into structured bug reports. Invoke when asked to "run the GUI checklist
  on a screen", "execute GUI testing for a page", "test a screen against the checklist", or
  "evaluate this screen's UI".
version: 1.0.0
author: phatnguyen975
---

# GUI Checklist Executor Skill

## Overview

This skill executes a shared GUI checklist against a **single screen** of a web application. It operates in two modes depending on browser connectivity, and produces three artifacts per screen that include an execution checklist, an edge case test list, and a grouped bug report.

**Execution modes:**

- **Mode 1 — BrowserMCP (preferred):** AI navigates the live screen, interacts with widgets, reads DOM state, and evaluates each checklist item directly against the running application.
- **Mode 2 — Screenshot (fallback):** AI analyses a provided overview screenshot and screen description, evaluating what is visually observable and marking the rest as `Need Human`.

### Inputs

```
REQUIRED
───────────────────────────────────────────────────────────────────────────────
checklist_path      : string (path)
                      Path to the shared GUI checklist file.
                      Example: "docs/checklist/shared-gui-checklist.md"

screen_id           : string
                      Short identifier for this screen.
                      Example: "A2", "checkout", "user-profile"

screen_name         : string
                      Human-readable name of the screen being tested.
                      Example: "Add/Edit Event Form"

screen_url          : string (URL)
                      Full URL of the screen. AI navigates here via BrowserMCP.
                      Example: "https://example.com/admin/events/create"

sut_credentials     : object — MUST be provided by human, never hardcoded
                      Login credentials if authentication is required to reach the screen.
                      Format: { "email": "<value>", "password": "<value>" }
                      Leave empty object {} if the screen is publicly accessible.

screen_description  : string
                      Description of the screen's purpose and visible components.
                      Used as context in both modes. In Mode 2, this is the primary source for How-to-Test step generation.
                      Example: "Admin form for creating/editing an event. Contains title input, date-time range pickers,
                      rich text editor, thumbnail upload (4:3 ratio), banner upload (24:9 ratio), registration toggle switches,
                      max slots inputs, and a Save Draft / Publish action bar."

output_dir          : string (path)
                      Directory where all output files will be written.
                      Example: "docs/execution"

OPTIONAL
───────────────────────────────────────────────────────────────────────────────
screen_screenshot   : string (path)
                      Path to an overview screenshot of the screen.
                      Required if BrowserMCP is unavailable (Mode 2 fallback).
                      Also used in Mode 1 to provide initial visual context before AI navigates the live screen.
                      Example: "screenshots/execution/A2-overview.png"
```

### Invoke Format

```
/gui-checklist-executor
  checklist_path: "<path>"
  screen_id: "<id>"
  screen_name: "<name>"
  screen_url: "<url>"
  sut_credentials: { "email": "<value>", "password": "<value>" }
  screen_description: "<description>"
  output_dir: "<path>"
  screen_screenshot: "<path>"
```

### Outputs

| File                                     | Description                                      | See Template                                                                   |
| ---------------------------------------- | ------------------------------------------------ | ------------------------------------------------------------------------------ |
| `{output_dir}/execution-{screen_id}.md`  | Per-item execution checklist with Result + Notes | [`examples/example-execution-output.md`](examples/example-execution-output.md) |
| `{output_dir}/edge-cases-{screen_id}.md` | Edge case scenarios with expected outcomes       | [`examples/example-execution-output.md`](examples/example-execution-output.md) |
| `{output_dir}/bug-report-{screen_id}.md` | Grouped bug report for all Fail items            | [`examples/example-bug-report.md`](examples/example-bug-report.md)             |

## When to Use

- You have a shared GUI checklist and want to evaluate a specific screen against it.
- You need per-item Pass/Fail results with traceability to checklist items.
- You want to generate screen-specific How-to-Test steps grounded in the live UI.
- You need a structured, grouped bug report for discovered UI defects.

## When NOT to Use

- You do not yet have a shared GUI checklist — create one first.
- You want to test functionality or business logic — this skill covers UI/GUI only.
- You want to run automated regression — this skill requires AI judgment per item.
- You want cross-browser compatibility testing — use a compatibility matrix tool instead.

## Core Principles

1. **BrowserMCP is the primary source of truth.** Live DOM state is more reliable than a static screenshot. Always prefer Mode 1 when BrowserMCP is connected.
2. **Each checklist item is tested independently.** Do not batch items. Test one item, record its result, then move to the next. This ensures no item's result is influenced by the context of testing another, and the human can follow progress in real-time.
3. **How-to-Test steps must reflect the actual screen.** Steps generated from the screenshot and screen description are drafts. During Mode 1 execution, refine each item's How-to-Test to match what you actually did on the live page — including exact navigation paths, element labels, and interaction sequences observed.
4. **Credentials are always provided by the human — never hardcoded.** The skill must not store, suggest, or infer credentials. Accept them only through the `sut_credentials` input.
5. **Result verdicts follow strict criteria.** See the **Result Verdict Criteria** section. Do not assign `Pass` when uncertain — default to `Need Human` when in doubt.
6. **Bugs are grouped by root cause, not per item.** Multiple `Fail` items with the same underlying issue form one bug group. This prevents report inflation and surfaces the systemic nature of defects.
7. **Human verification gates every phase.** AI produces; human approves. No phase begins until the human explicitly signals `APPROVED`.

## Result Verdict Criteria

Apply these criteria consistently to every checklist item. Consistency across testers is more valuable than optimistic grading.

### PASS

Assign `Pass` when **all** of the following are true:

- The item's criterion is verifiably met based on direct DOM inspection (Mode 1) or unambiguous visual evidence in the screenshot (Mode 2).
- The observed behaviour matches the expected behaviour stated in the item `Description`.
- No edge case or variant of the interaction produces a different result.

### FAIL

Assign `Fail` when **any** of the following is true:

- The item's criterion is verifiably not met based on direct DOM inspection or visual evidence.
- The observed behaviour contradicts the expected behaviour stated in the item `Description`.
- A required element is absent (e.g. no error message, no focus indicator, no label).
- Always record a specific observation in `Notes` — never leave `Notes` blank for a `Fail`.

### NA (Not Applicable)

Assign `NA` when **all** of the following are true:

- The widget or UI area referenced in the item does not exist on this screen at all.
- The item's criterion is structurally impossible to evaluate on this screen (e.g. a drag-and-drop item on a screen that has no draggable elements).
- Do **not** assign `NA` simply because the item is hard to test or requires interaction.

### NEED HUMAN

Assign `Need Human` when **any** of the following is true:

- Verification requires manual visual measurement tools not available to the AI (e.g. complex sub-pixel alignment). _(Note: AI CAN use DevTools for contrast ratios, responsive viewports, and network throttling)._
- Verification requires a real interaction sequence that cannot be reliably reproduced via BrowserMCP (e.g. testing file download).
- The item involves audio, haptics, or other non-visual feedback.
- The item requires observing a timed behaviour (e.g. toast auto-dismiss after exactly 3s).
- Mode 2 (screenshot only): any item that requires clicking, typing, hovering, or navigating to a different state to evaluate.

See [`resources/need-human-triggers.md`](resources/need-human-triggers.md) for a comprehensive trigger list.

## Workflow

### Step 0 — Check BrowserMCP Connectivity

Before generating any output, check whether BrowserMCP is connected:

1. Check `.agents/mcp_config.json` in the current project directory
2. If not found, check `~/.gemini/config/mcp_config.json` (global fallback)

Look for an entry with key `"browsermcp"` inside `"mcpServers"`. If found, attempt to connect.

- **BrowserMCP is available?** → Proceed with Mode 1. Confirm to the human: "BrowserMCP is connected. Proceeding in live browser mode."
- **BrowserMCP is NOT available?** → Present two options to the human:
  ```
  BrowserMCP is not connected. Please choose:
  [A] I will fix the BrowserMCP connection now — let me know when it is ready and we will proceed in live browser mode.
  [B] Proceed in screenshot fallback mode — I will use the provided overview screenshot to evaluate visible items. Items requiring interaction will be marked Need Human.
  ```
  Wait for the human's choice before proceeding. If the human chooses `[A]`, wait for their confirmation that BrowserMCP is connected before retrying. If the human chooses `[B]`, confirm that `screen_screenshot` is provided — if not, stop and request it.

### Step 1 — Read the Shared Checklist

Read `checklist_path` in full. Extract all items across all IA categories. Do not filter or pre-select items at this stage. Every item will be considered for this screen.

### Step 2 — Generate the Execution Checklist (Draft)

Using `screen_description` and `screen_screenshot` (if provided), generate a draft execution checklist. At this stage, **only write `How-to-Test` steps** — do not assign `Result` yet.

**For each checklist item:**

1. Read the item's `Description` and `Widget / Area`.
2. Using `screen_description` and `screen_screenshot`, write screen-specific `How-to-Test` steps — concrete, numbered steps referencing actual element names/labels visible on this screen.
3. If the widget or area is clearly absent from both `screen_description` and the screenshot, note this in `How-to-Test` as "Widget not observed in description or screenshot — verify during execution". Do not pre-assign `NA` at this stage.
4. If the description is ambiguous or a widget's presence is uncertain, ask the human to clarify before proceeding to the next item.
5. Keep items grouped by their original IA category.

Write the draft execution checklist to `{output_dir}/execution-{screen_id}.md` with `Result` and `Notes` columns left blank.

Also generate the edge case list (if any) and write to `{output_dir}/edge-cases-{screen_id}.md`. Note that the edge case list must not overlap with the main checklist items. Edge cases are additional scenarios that may not be covered by the main checklist but are relevant to the widgets present on this screen. Use the widget codes from the Waghmare Per-Widget Checklist to identify applicable edge cases and each item in the edge case list must be used for GUI testing, not for functional testing.

See [`resources/edge-cases-reference.md`](resources/edge-cases-reference.md) for common edge cases by widget type. At this stage, edge cases also only need the `How-to-Test` and `Expected Outcome` columns filled.

### Step 3 — Human Verification Gate 1 (Checklist Draft)

Stop and present the following to the human:

```markdown
Execution checklist draft written to: `{output_dir}/execution-{screen_id}.md`
Edge case list written to: `{output_dir}/edge-cases-{screen_id}.md`

Please review:

- Are the `How-to-Test` steps accurate for this screen?
- Are any items pre-marked `NA` incorrectly?
- Are there missing edge cases?

Reply `APPROVED` to proceed, or `FAILED: <feedback>` to request corrections.
```

- If human replies `APPROVED`, proceed to Step 4.
- If human replies `FAILED: <feedback>`, apply corrections to the files, then re-present this gate. Repeat until `APPROVED`.

### Step 4 — Execute Each Item (Mode 1: BrowserMCP)

> If in **Mode 2 (screenshot only)**, skip to Step 5.

Navigate to `screen_url` via BrowserMCP. Login using `sut_credentials` if required.

For each item in the execution checklist **in order**, one at a time:

1. **Read the item** — ID, Description, How-to-Test steps
2. **Perform the How-to-Test steps** on the live page using BrowserMCP
3. **During execution, also verify widget presence:**
   - If a widget referenced in the item requires navigating to a different state (e.g. opening a dialog, clicking a button to reveal a form section), do so before assigning `NA`. A widget is only `NA` when it is confirmed absent even after triggering the relevant interaction path.
   - If unsure whether the widget should be present, ask the human before marking `NA`.
4. **Observe the result** — inspect DOM state, visible UI, ARIA attributes where accessible
5. **Refine the How-to-Test steps** in the file if the actual interaction differed from the draft, leaving blank if `Result` is `NA`
6. **Assign a Result verdict** using the criteria in the **Result Verdict Criteria** section
7. **Write Notes** immediately:
   - `Fail` — specific observation of what was wrong
   - `Need Human` — what needs checking and why it cannot be verified by AI in this context
   - `NA` — brief confirmation that the widget/area is confirmed absent on this screen
   - `Pass` — brief confirmation of what was observed
8. **Update the item in the file immediately** — do not batch results at the end
9. **Report to the human inline** after each item:
   ```
   ✓ IA01-001 [Pass] — Label present above field, not placeholder-only.
   ✗ IA02-003 [Fail] — Validation fires only on submit, not on field blur.
   ~ IA03-005 [Need Human] — Colour contrast ratio requires visual measurement tool.
   — IA01-004 [NA] — No drag-and-drop component on this screen.
   ```

Also execute each edge case item in `edge-cases-{screen_id}.md` using the same approach, reporting inline after each one and persisting it to the file immediately.

**Note:** While executing, you can use Browser DevTools to simulate different responsive viewports (e.g. resizing to mobile screens), throttle the network to 'Slow 3G/4G' to observe loading states (spinners/skeletons), and inspect computed CSS to verify colour contrast ratios. **DO NOT** use DevTools to inspect HTML DOM code (like `aria-*`, `role`), network API responses, or console logs, as GUI Testing strictly evaluates visual output and interactive feedback. And you can scroll the page or components vertically or horizontally to reveal hidden content, but do not use DevTools to bypass visual verification.

### Step 5 — Execute Each Item (Mode 2: Screenshot Fallback)

Load `screen_screenshot` and `screen_description`. Analyse the screenshot carefully before evaluating items — many criteria can be determined from visual evidence alone.

For each item in the execution checklist **in order**, one at a time:

1. **Read the item** — ID, Description, How-to-Test steps
2. **Assess using screenshot and description — apply this decision logic:**
   - **Pass:** the criterion is clearly met from visual evidence in the screenshot (e.g. labels are visibly above fields, asterisks are present on required fields, status badges have both colour and text, the empty state message is visible)
   - **Fail:** the criterion is clearly violated from visual evidence (e.g. no label visible above a field, no focus indicator visible on any element, required field indicator absent)
   - **NA:** the widget or area is absent from both the screenshot and `screen_description`. When uncertain between `NA` and `Need Human`, choose `Need Human`.
   - **Need Human:** the criterion requires dynamic interaction, timing observation, or information not available in the static screenshot (e.g. validation behaviour on submit, hover states, keyboard navigation, loading states)
3. If assessment is ambiguous, ask the human to clarify before assigning `Result`
4. **Write Notes** immediately:
   - `Fail` — specific observation of what was wrong
   - `Need Human` — what needs checking and why it cannot be verified by AI in this context
   - `NA` — brief confirmation that the widget/area is confirmed absent on this screen
   - `Pass` — brief confirmation of what was observed
5. **Update the item in the file immediately**
6. **Report inline after each item** (same format as Step 4)

For edge cases in Mode 2, evaluate what is visually determinable from the screenshot. Mark `Need Human` for any edge case that requires triggering an interaction to observe the result.

### Step 6 — Human Verification Gate 2 (Execution Results)

Stop and present the following to the human:

```markdown
Execution complete for screen: {screen_name} ({screen_id})

Summary:

- Pass: [N]
- Fail: [N]
- NA: [N]
- Need Human: [N]
- Edge Cases — Pass: [N] | Fail: [N] | Need Human: [N]

Files updated:

- `{output_dir}/execution-{screen_id}.md`
- `{output_dir}/edge-cases-{screen_id}.md`

For `Need Human` items, please perform manual verification and update the `Result` and `Notes` fields directly in the execution file.

Please review the results and reply:

- `APPROVED` — proceed to bug report generation
- `FAILED: <feedback>` — flag specific items to re-evaluate
```

- If human replies `APPROVED`, proceed to Step 7.
- If human replies `FAILED: <feedback>`, re-evaluate flagged items, update the file, then re-present this gate.

### Step 7 — Generate Bug Report

Read all items with `Result` is `Fail` from both `execution-{screen_id}.md` and `edge-cases-{screen_id}.md`.

**Group Fail items by root cause:**

- Items that share the same underlying UI defect form one bug group
- A single widget type failing the same criterion across multiple items = one bug
- Distinct defects with different root causes = separate bugs

For each bug group, write a structured bug report entry. Write all entries to `{output_dir}/bug-report-{screen_id}.md`.

See [`resources/bug-report-format.md`](resources/bug-report-format.md) for the canonical field set and severity scale, and [`examples/example-bug-report.md`](examples/example-bug-report.md) for a complete formatted example.

### Step 8 — Human Verification Gate 3 (Bug Report)

Stop and present the following to the human:

```markdown
Bug report written to: `{output_dir}/bug-report-{screen_id}.md`

Total bug groups: [N]

- Severity 4 (Critical): [N]
- Severity 3 (Major): [N]
- Severity 2 (Minor): [N]
- Severity 1 (Cosmetic): [N]
- Severity 0 (Not a bug): [N]

Please review:

- Are the severity ratings appropriate?
- Are any distinct defects incorrectly merged into one group?
- Are any duplicate groups that should be merged?

Reply `APPROVED` to finalise, or `FAILED: <feedback>` to request corrections.
```

- If human replies `APPROVED`, execution for this screen is complete.
- If human replies `FAILED: <feedback>`, apply corrections to the bug report, then re-present this gate.

## Anti-Patterns

| Anti-Pattern                                              | Why it fails                                                                       | Correct approach                                                       |
| --------------------------------------------------------- | ---------------------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| Batching all results before reporting                     | Human cannot follow progress; errors accumulate silently                           | Report result after each individual item                               |
| Assigning `Pass` when outcome is uncertain                | Inflates `Pass` rate; hides real defects                                           | Default to `Need Human` when evidence is incomplete                    |
| Creating one bug per `Fail` item                          | Bug list becomes unmanageably long; duplicate root causes obscure the real problem | Group items by root cause; one group per distinct defect               |
| Skipping `How-to-Test` refinement after live testing      | Steps may be wrong or incomplete; human cannot verify AI's work                    | Update `How-to-Test` steps to reflect actual interaction performed     |
| Proceeding past a verification gate without `APPROVED`    | Human loses control of output quality                                              | Always wait for explicit `APPROVED` signal                             |
| Using `sut_credentials` from memory or hardcoded defaults | Security risk; credential management is the human's responsibility                 | Always require credentials via input at invoke time                    |
| Marking `NA` for hard-to-test items                       | Silently drops coverage                                                            | `NA` only for structurally absent widgets; hard-to-test → `Need Human` |
| Running Mode 2 without `screen_screenshot`                | AI has no visual basis for evaluation                                              | Stop and request screenshot before proceeding in Mode 2                |

## Best Practices

- **Test one item at a time and report immediately.** This gives the human real-time visibility and allows early course-correction if BrowserMCP behaves unexpectedly.
- **Refine How-to-Test during execution, not after.** Update each item's steps as you perform them — the final file should reflect exactly what was done, not the pre-execution draft.
- **Keep Notes specific and observable.** "Validation fires only on submit" is useful. "Validation seems wrong" is not. Notes must be specific enough to reproduce the issue.
- **Strictly Blackbox Visual Verification.** Evaluate items based entirely on visual appearance and interactive feedback. You may use DevTools for responsive testing, network throttling, or contrast, but DO NOT inspect HTML DOM elements, roles, or aria attributes to bypass visual verification.
- **Respect the NA threshold strictly.** When in doubt whether a widget is present or absent, navigate to the screen state where it would appear before assigning `NA`.
- **Do not fabricate observations.** If BrowserMCP returns unexpected output or a step cannot be completed, record what happened honestly and mark the item `Need Human`.

## Quality Checklist

Run this before presenting any output file to the human.

### Execution Checklist File

- [ ] Every item from the shared checklist is present — no items skipped.
- [ ] Items are grouped by their original IA category.
- [ ] Every item has a Result value (`Pass` / `Fail` / `NA` / `Need Human`).
- [ ] Every non-Pass item has a specific, non-empty Note.
- [ ] How-to-test steps are screen-specific (not generic), numbered, and reflect actual interaction performed (Mode 1) or screenshot evidence (Mode 2).
- [ ] All content is written in English.

### Edge Case File

- [ ] At least one edge case per widget type present on the screen.
- [ ] Each edge case has an `EC-ID`, `How-to-Test` steps, `Expected Outcome`, `Result`, and `Notes`.
- [ ] `Need Human` edge cases include the reason BrowserMCP/screenshot cannot verify them.
- [ ] All content is written in English.

### Bug Report File

- [ ] Every `Fail` item from the execution checklist and edge case list is referenced in at least one bug group.
- [ ] No two bug groups describe the same root cause.
- [ ] Every bug group has: ID, title, affected item IDs, steps to reproduce, expected vs actual, severity (0–4), suggested fix.
- [ ] Severity ratings follow the defined scale.
- [ ] All content is written in English.

## Common Rationalisations to Reject

| Rationalisation                                                                       | Why to reject                                                                                                                              |
| ------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| _"I'll batch all results and update the file at the end — it's faster"_               | The human cannot monitor progress; errors in early items affect interpretation of later ones. Test and record one item at a time.          |
| _"This item is basically the same as the last one — I'll just copy the result"_       | UI defects are often item-specific. Each item tests a distinct criterion. Evaluate independently.                                          |
| _"The screenshot looks fine, so I'll mark it Pass without verifying the interaction"_ | Screenshots capture one state. Pass requires the criterion to be met across all relevant states. Mark Need Human if interaction is needed. |
| _"I'll skip the verification gate — the output looks good to me"_                     | The verification gate exists precisely because AI judgment can be wrong. Wait for human APPROVED.                                          |
| _"One bug per failing item makes the report more traceable"_                          | It makes the report unreadable. Group by root cause and reference item IDs within each group.                                              |
| _"The credentials are obvious — I'll use the defaults from the screen description"_   | Credentials must never be assumed or hardcoded. Request them via sut_credentials input every time.                                         |

## Resources

| File                                   | Purpose                                                                           |
| -------------------------------------- | --------------------------------------------------------------------------------- |
| `resources/edge-cases-reference.md`    | Common edge cases by widget type for web applications                             |
| `resources/need-human-triggers.md`     | Comprehensive list of scenarios that must be marked Need Human                    |
| `resources/bug-report-format.md`       | Canonical bug report field set and severity scale (0–4)                           |
| `examples/example-execution-output.md` | Complete sample execution checklist and edge case list with all four Result types |
| `examples/example-bug-report.md`       | Complete sample bug report with grouped entries at multiple severity levels       |
