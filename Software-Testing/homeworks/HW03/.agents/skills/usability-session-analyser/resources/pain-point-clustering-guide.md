# Pain Point Clustering Guide — Usability Session Analyser

## What Counts as a Pain Point

A pain point is any evidence of friction, confusion, error, or dissatisfaction observed in session data. Sources:

| Source                            | Examples                                                                                      |
| --------------------------------- | --------------------------------------------------------------------------------------------- |
| Observation log — behaviour       | Wrong navigation path taken; participant backtracked; form submitted with missing fields      |
| Observation log — verbalisation   | "Where is that button?", "I'm not sure if that saved", "This is confusing"                    |
| Error tally                       | Each logged error that has a corresponding observation                                        |
| Hesitation tally                  | Long pauses before decisions, especially if repeated across participants                      |
| Probe question answers            | "The most confusing part was...", "I wasn't sure whether..."                                  |
| Task failure / partial completion | Any task not completed is evidence of a pain point; investigate the session log for the cause |

## Extraction Process

For each session file, read the observation log and probe answers sequentially. Extract each pain point as a raw item:

```
Raw pain point format:
  Source: P{n} | T{task} | [MM:SS] obs log OR probe Q{n}
  Description: {what happened or what was said}
```

Example extractions:

```
P1 | T1 | [02:14] obs — Clicked "Users" instead of "Events"; backtracked after 40s
P1 | T1 | probe Q1 — "I didn't know where to start, the menu wasn't clear"
P3 | T1 | [05:30] obs — Submitted form without uploading thumbnail; error appeared at top of page
P3 | T1 | [05:45] obs — Had to scroll up to find the error message
P4 | T1 | probe Q2 — "I wasn't confident it had actually saved"
P5 | T1 | [03:10] obs — Paused 12s on registration config section; said "I don't know what Max Slots means"
```

## Clustering Process

Group raw pain points that share the same **root cause** — not the same page or feature.

| Cluster basis               | Example                                                                              |
| --------------------------- | ------------------------------------------------------------------------------------ |
| Same root cause             | P1, P3, P5 all had trouble with the sidebar navigation — cluster as one finding      |
| Same widget, same criterion | P2 and P4 both didn't notice validation errors until submit — cluster as one finding |
| Same mental model gap       | P1, P2, P3 were uncertain whether "Save Draft" had worked — cluster as one finding   |

**Do NOT cluster:**

- Issues that are superficially similar but have different root causes (e.g. "couldn't find the button" + "didn't understand what the button does" = two different issues)
- Issues on different tasks that happen to have similar symptoms (investigate whether the root cause is genuinely shared before merging)

**Cluster naming:** Write cluster titles as specific, observable problem statements:

- ✓ "Validation errors appear only after form submit, not on field blur"
- ✗ "Error prevention issues"
- ✓ "Participants could not confirm whether Save Draft action succeeded"
- ✗ "System status visibility problem"

## Heuristic Mapping (Nielsen 10)

| Heuristic                                               | Code | Map when the pain point involves...                                                                    |
| ------------------------------------------------------- | ---- | ------------------------------------------------------------------------------------------------------ |
| Visibility of System Status                             | N1   | Uncertainty about whether an action succeeded; missing loading/progress indicators; unclear save state |
| Match Between System and Real World                     | N2   | Unfamiliar terminology; icons without labels; status labels that don't match user vocabulary           |
| User Control and Freedom                                | N3   | No undo/cancel; unexpected navigation away; unable to go back                                          |
| Consistency and Standards                               | N4   | Inconsistent button positions, labels, or colours across screens                                       |
| Error Prevention                                        | N5   | Form accepts invalid data; no constraints on date ranges; no confirmation before destructive action    |
| Recognition Rather Than Recall                          | N6   | Participant had to remember information from a previous screen; options not visible without searching  |
| Flexibility and Efficiency of Use                       | N7   | No bulk actions; repetitive steps for experienced users; no shortcuts                                  |
| Aesthetic and Minimalist Design                         | N8   | Information overload; irrelevant content competing for attention; cluttered layout                     |
| Help Users Recognise, Diagnose, and Recover from Errors | N9   | Error message unclear, generic, or not adjacent to the error; no fix suggestion                        |
| Help and Documentation                                  | N10  | No tooltips; empty state has no guidance; participant needed external help to understand a concept     |

For Shneiderman 8 or Norman 6: see `resources/heuristics-mapping-extended.md`

## Systemic vs Isolated Classification

| Classification | Threshold                             | Action                                           |
| -------------- | ------------------------------------- | ------------------------------------------------ |
| **Systemic**   | ≥ 50% of participants (e.g. ≥ 3 of 5) | Prioritise in recommendations                    |
| **Isolated**   | < 50% of participants                 | Note in findings; deprioritise unless Severity 4 |

With N=5 participants:

- 3+ participants → Systemic
- 1–2 participants → Isolated

With N < 5: adjust threshold proportionally. With N=3, 2 participants = Systemic (67%).

## Criticality Formula

**Criticality = Severity × Frequency**

Where:

- Severity: Nielsen (1994) scale 0–4 (see `resources/severity-frequency-reference.md`)
- Frequency: number of affected participants ÷ total participants (decimal 0–1)

```
Example:
  Finding: "Validation errors appear only on submit"
  Severity: 3 (major — causes significant difficulty; not catastrophic)
  Frequency: 4/5 = 0.8
  Criticality: 3 × 0.8 = 2.4

  Finding: "Drag-and-drop reorder has no keyboard alternative"
  Severity: 4 (catastrophe for keyboard-only users)
  Frequency: 1/5 = 0.2 (only one participant used keyboard navigation)
  Criticality: 4 × 0.2 = 0.8
```

Sort findings by criticality descending. At equal criticality, systemic findings rank above isolated findings.

## Triangulation: Strengthening Finding Evidence

The most credible findings are supported by three independent data sources:

| Source                          | Evidence type                                                      |
| ------------------------------- | ------------------------------------------------------------------ |
| Observation log — behaviour     | What the participant _did_ (objective)                             |
| Observation log — verbalisation | What the participant _said_ during the task (think-aloud)          |
| Probe question answer           | What the participant _reflected on_ after the task (retrospective) |

**Confidence levels:**

- **High confidence:** Supported by all three sources across multiple participants
- **Medium confidence:** Supported by two sources, or one source across multiple participants
- **Low confidence:** Only one source for one participant

Note the confidence level in the finding's evidence field.
