---
name: usability-test-designer
description: >
  Design a complete usability test plan for any web application scenario. Use this skill when
  you need to create a 9-part test plan, one or more goal-based task scenarios, SUS instrument,
  probe questions per task, participant table template, and per-participant session templates
  ready for either moderated or unmoderated remote testing. Invoke when asked to "design a
  usability test", "create a usability test plan", "write task scenarios for user testing",
  or "prepare usability testing materials".
version: 1.0.0
author: phatnguyen975
---

# Usability Test Designer Skill

## Overview

This skill produces a complete usability test design package for a given web application scenario. It follows the 9-part test plan structure from Rubin & Chisnell (2008) and supports four test types: exploratory, assessment, validation, and comparison.

All output files are designed so that:

- **Moderated sessions:** The facilitator uses them as guides during live observation
- **Unmoderated remote sessions:** Participants can self-complete the session template with enough guidance to produce analysable data

### Inputs

```
REQUIRED
───────────────────────────────────────────────────────────────────────────────
sut_name              : string
                        Full name of the System Under Test.
                        Example: "EMS (Event Management System)"

scenario_description  : string
                        Description of the functional area or user journey being tested.
                        Should cover what the target user would want to accomplish.
                        Example: "Admin creates, configures, and publishes an event,
                        including setting registration rules with slot limits"

screens_list          : list of strings
                        Screens the task scenario(s) should naturally pass through.
                        Example: ["A2: Add/Edit Event Form",
                                  "A4: Participants & Reviews",
                                  "A5: Check-in Tab"]

user_profile          : string
                        Description of the target participant: role, technical background,
                        prior experience with the SUT or similar tools.
                        Example: "Faculty staff or senior university students (Year 3+),
                        comfortable with web-based admin tools, no prior EMS experience"

test_type             : enum
                        One of: "exploratory" | "assessment" | "validation" | "comparison"
                        - exploratory: Early stage (sketches, wireframes, prototypes).
                          Informal; evaluates basic concept with shallow tasks;
                          heavy facilitator interaction and probing.
                        - assessment: Mid stage (concepts fixed, product functional).
                          Well-defined tasks; quantitative metrics collected;
                          less facilitator interaction. Most common for HW03.
                        - validation: Pre-release ("disaster insurance").
                          Full product including help & documentation tested against
                          predetermined benchmarks.
                        - comparison: A/B design comparison at any stage.
                          Two variants tested with objective measures; requires
                          counterbalancing if within-subjects design.

num_tasks             : integer ≥ 1
                        Number of task scenarios to generate.

num_participants      : integer ≥ 1
                        Number of participants. Used to generate P{n}-session.md files.

metrics_to_collect    : list of strings
                        Performance and preference metrics to measure.
                        Example: ["task success (Completed/Partial/Failed)",
                                  "time on task (seconds)",
                                  "error count",
                                  "hesitation count (pause > 5s without action)",
                                  "SUS score"]

output_dir            : string (path)
                        Directory where all output files will be written.
                        Example: "docs/usability"

OPTIONAL
───────────────────────────────────────────────────────────────────────────────
comparison_variants   : list of strings [required if test_type = "comparison"]
                        Names of the two variants being compared.
                        Example: ["Variant A: Current design", "Variant B: Redesigned nav"]

benchmark_time        : string
                        Expected task completion time for a competent user.
                        Used as the success benchmark. Example: "8 minutes"

session_format        : enum [default: "moderated"]
                        "moderated" | "unmoderated-remote"
                        Affects the level of guidance written into P{n}-session.md files.
                        With "unmoderated-remote", session files include detailed
                        self-instruction text for participants to follow without a facilitator.
```

### Invoke Format

```
/usability-test-designer
  sut_name: "<value>"
  scenario_description: "<value>"
  screens_list:
    - "<Screen ID: Screen Name>"
    - "<Screen ID: Screen Name>"
  user_profile: "<value>"
  test_type: "<exploratory|assessment|validation|comparison>"
  num_tasks: <integer>
  num_participants: <integer>
  metrics_to_collect:
    - "<metric>"
    - "<metric>"
  output_dir: "<path>"
  comparison_variants:
    - "<Variant A description>"
    - "<Variant B description>"
  benchmark_time: "<value>"
  session_format: "<moderated|unmoderated-remote>"
```

### Outputs

| File                                         | Description                                           | Filled by                                                   | Notes                                                    |
| -------------------------------------------- | ----------------------------------------------------- | ----------------------------------------------------------- | -------------------------------------------------------- |
| `{output_dir}/test-plan.md`                  | 9-part test plan                                      | AI → Human reviews                                          | Human fills Part 6 (environment details)                 |
| `{output_dir}/task-scenario-T{n}.md`         | One file per task scenario (×num_tasks)               | AI → Human reviews                                          | Human must approve before sessions run                   |
| `{output_dir}/probe-questions-T{n}.md`       | 3–5 probe questions per task (×num_tasks)             | AI → Human reviews                                          | Task-specific; non-leading                               |
| `{output_dir}/sus-instrument.md`             | SUS questionnaire + scoring guide                     | AI                                                          | Do not modify SUS statement wording                      |
| `{output_dir}/observation-template.md`       | Master reference with full instructions + field guide | AI                                                          | Blueprint only; used to generate `P{n}-session.md` files |
| `{output_dir}/participant-table.md`          | Participant roster (masked contacts)                  | Human fills entirely                                        | AI generates structure only                              |
| `{output_dir}/session-notes/P{n}-session.md` | Per-participant session file (×num_participants)      | Facilitator (moderated) or Participant (unmoderated remote) | After filling, becomes raw input for analysis            |

See [`examples/example-test-plan.md`](examples/example-test-plan.md) and [`examples/example-session-template.md`](examples/example-session-template.md) for complete formatted samples.

## When to Use

- You need to plan and prepare materials for a usability test on a web application.
- You want goal-based task scenarios that reveal friction without scripting the user's path.
- You need session templates that participants can self-complete (unmoderated remote).
- You need a SUS questionnaire and scoring guide ready for distribution.
- You want a structured 9-part test plan to document your methodology.

## When NOT to Use

- You already have a test plan and only need to analyse collected session data.
- You want to run automated performance testing or load testing.
- You need a GUI/UI conformance checklist — that is a different concern.
- You want to test a native mobile app — this skill is designed for web applications.

## Core Principles

1. **Tasks describe goals, never steps.** A task scenario tells the participant what to achieve — not how to achieve it. Any instruction that specifies a UI element or action sequence is an anchoring error and must be rewritten.
2. **One success criterion per task.** Every task must have a clearly defined, binary success criterion: either the participant achieved the goal or they did not (or partially). Ambiguous success criteria produce unanalysable data.
3. **Probe questions are task-specific, not generic.** Generic questions like "What did you think?" produce shallow data. Each probe question should target a specific aspect of the task experience — a friction point, a trust moment, an expectation gap.
4. **Session templates must work without a facilitator.** Even for moderated sessions, the session file must be complete enough that a participant can understand what is asked. This is critical for unmoderated remote testing.
5. **Participant data is always provided by humans — never generated.** Names, contacts, and consent information are filled by the human. The AI produces only the template rows.
6. **Metrics are defined before sessions run.** Measurement definitions must be agreed before data collection begins. This prevents post-hoc interpretation bias.
7. **Human reviews task scenarios before any participant sees them.** A scenario with anchoring language or an unclear success criterion cannot be fixed after the fact. Gate the task scenarios behind explicit human approval.

## Task Scenario Design Rules

These rules derive from Rubin & Chisnell (2008) and Nielsen (1993). They are the most critical quality criteria for usability test design.

### Screen Coverage Rule

**When `num_tasks = 1`:** The single task must naturally traverse **all screens in `screens_list`** as part of completing one coherent user goal. Before writing, verify the goal is complex enough to require all listed screens. If not possible with one coherent goal, recommend increasing `num_tasks` and wait for human confirmation before proceeding.

**When `num_tasks ≥ 2`:** Distribute screens across tasks so that:

- All screens in `screens_list` are covered across the complete task set.
- Each task covers a coherent user sub-goal — not an arbitrary screen split.
- Tasks can be performed independently where possible (avoid task `N` requiring task `N-1`).
- The distribution follows logical workflow order (creation → configuration → review → operation).

**Distribution example (3 tasks, 5 screens):**

| Task | Sub-goal                                   | Screens covered                                  |
| ---- | ------------------------------------------ | ------------------------------------------------ |
| T1   | Create and configure a new event           | A2: Add/Edit Event Form, A3: Registration Config |
| T2   | Review and approve submitted registrations | A4: Participants & Reviews                       |
| T3   | Check in an attendee at the event          | A5: Check-in Tab                                 |

After distributing, verify that every screen in `screens_list` appears in exactly one task.

### Rules for Writing Valid Task Scenarios

| Rule                           | Explanation                                                         | Example of violation                                        |
| ------------------------------ | ------------------------------------------------------------------- | ----------------------------------------------------------- |
| **Goal-based**                 | State what the participant wants to achieve, not what to click      | "Click the Add Event button and fill in the form"           |
| **Realistic context**          | Set a plausible real-world situation that motivates the goal        | "Test the event creation feature"                           |
| **No UI language**             | Never name UI elements (buttons, tabs, menus) in the task           | "Go to the Events tab and use the Publish button"           |
| **No embedded answer**         | The task must not reveal the correct path or outcome                | "Use the draft feature to save your work before publishing" |
| **Specific success criterion** | Define exactly what counts as task completion                       | "Complete the task" (too vague)                             |
| **Benchmark time**             | Provide an expected time for competent completion                   | (absent)                                                    |
| **Covers target screens**      | The natural path through the goal passes through the listed screens | Task that only touches one screen                           |

### Self-check Before Finalising a Task Scenario

After writing each task, apply this checklist:

- [ ] Can the participant read this and know what success looks like?
- [ ] Does the task avoid naming any UI element or screen?
- [ ] Could a participant complete this task in more than one way?
- [ ] Does the context feel realistic for the target user profile?
- [ ] Does naturally completing this task require visiting the screens in `screens_list`?

## Workflow

### Step 1 — Validate Inputs

Check all required inputs are present and coherent:

- `test_type` is one of the four valid values
- If `test_type = "comparison"`, `comparison_variants` is provided with exactly 2 entries
- `num_tasks` and `num_participants` are positive integers
- `screens_list` has at least one entry
- `metrics_to_collect` has at least one metric

If any required input is missing → stop and ask the human to clarify.

### Step 2 — Generate the 9-Part Test Plan

Write `{output_dir}/test-plan.md` using the 9-part structure from Rubin & Chisnell (2008). See [`resources/9part-plan-reference.md`](resources/9part-plan-reference.md) for the full field set and descriptions.

The nine parts are:

1. Purpose — why this test is being run
2. Problem statement — specific questions to answer
3. Test objectives & tasks — what will be tested and how
4. User profile — who the participants are and recruitment criteria
5. Method & test design — test type, session format, between/within-subjects
6. Environment / equipment — tools, setup, recording method
7. Test-monitor role — facilitator responsibilities and neutrality rules
8. Evaluation measures & data to collect — all metrics with definitions
9. Report contents — what the final report will include

For `test_type = "comparison"`, include both variants in the method section and specify whether the design is within-subjects (each participant tests both) or between-subjects (each participant tests one variant).

### Step 3 — Generate Task Scenario(s)

For each task (`1` to `num_tasks`), write `{output_dir}/task-scenario-T{n}.md`.

**Process for each task:**

1. Read `scenario_description` and `screens_list`
2. Write a realistic context — a 1–2 sentence situation that gives the participant a reason to perform the goal
3. Write the task goal — 1–3 sentences, goal-based, no UI language, no embedded answer
4. Define the success criterion — a binary or three-point scale (Completed / Partial / Failed) with specific observable indicators for each level
5. State the benchmark time (from `benchmark_time` input if provided, else derive from `scenario_description` and screen complexity)
6. Apply the self-check from the **Task Scenario Design Rules** section
7. If the self-check fails on any point → rewrite before writing to file

Write `{output_dir}/probe-questions-T{n}.md` alongside each task:

- Write 3–5 probe questions specific to this task
- Cover clarity of path, error recovery moments, trust in outcome, expectation gaps, and one open improvement question
- Questions must be open-ended and non-leading
- See [`resources/probe-question-guide.md`](resources/probe-question-guide.md) for patterns and anti-patterns

**Human verification gate after Step 3:**

```markdown
Task scenario(s) written to: `{output_dir}/task-scenario-T{n}.md`

IMPORTANT: Please review each task scenario before proceeding.

- Is it goal-based with no UI language?
- Is the success criterion clear and binary/three-point?
- Does the natural completion path pass through the listed screens?

Reply `APPROVED` to continue, or `FAILED: <feedback>` to revise.
```

Do not proceed to Step 4 until human replies `APPROVED`.

### Step 4 — Generate SUS Instrument

Write `{output_dir}/sus-instrument.md` containing:

- The standard 10 SUS statements (Brooke 1996) — do not modify the wording
- **Response scale:** 1 (Strongly Disagree) to 5 (Strongly Agree)
- **Scoring formula:**
  - Odd-numbered questions (1,3,5,7,9): score = response − 1
  - Even-numbered questions (2,4,6,8,10): score = 5 − response
  - SUS score = sum of 10 adjusted scores × 2.5 (range: 0–100)
- Interpretation table from Sauro & Lewis (2012)

See [`resources/sus-reference.md`](resources/sus-reference.md) for the exact 10 statements and full interpretation table.

### Step 5 — Generate Observation Template (Master)

Write `{output_dir}/observation-template.md` as a master reference document. This is the template structure that will be replicated into each `P{n}-session.md` file.

The master template defines:

- Session metadata fields (participant ID, date, time, mode)
- For each task: the task scenario text, timing fields, error/hesitation tally, result field
- Per-task structured observation table: `[MM:SS] | Behaviour | Verbalization | Observer Note`
- SUS questionnaire block (to be filled after tasks)
- Per-task probe question block (3–5 questions per task)
- Post-session open comments

For `session_format = "unmoderated-remote"`, add participant-facing instruction text above each section explaining what to record and how, so participants can self-guide.

### Step 6 — Generate Per-Participant Session Files

For each participant (`P1` to `P{num_participants}`), write `{output_dir}/session-notes/P{n}-session.md`.

Each file is a copy of the observation template, personalised with:

- Participant ID: `P{n}`
- Empty fields ready to fill in
- For `unmoderated-remote`, full self-instruction text included

These files are filled in during or after each session — either by the facilitator (moderated) or by the participant (unmoderated remote).

### Step 7 — Generate Participant Table

Write `{output_dir}/participant-table.md` with `{num_participants}` placeholder rows.

The table structure:

```markdown
| P#  | Name          | Profile       | Contact (masked)                     | Session Date | Session Format   | Consent |
| --- | ------------- | ------------- | ------------------------------------ | ------------ | ---------------- | ------- |
| P1  | [Human fills] | [Human fills] | [Human fills — mask middle 4 digits] | [Date]       | Moderated/Remote | Yes/No  |
```

This file is filled entirely by the human — the AI generates only the structure and column headers with instructions.

### Step 8 — Self-Review

Run the **Quality Checklist** section before presenting any output to the human.

## Output Templates

### `test-plan.md` Structure

```markdown
# Usability Test Plan — {sut_name}

> **Test type:** {test_type}  
> **Generated by:** `usability-test-designer` skill (v1.0.0)

## 1. Purpose

{Why this test is being run and what decisions it will inform.}

## 2. Problem Statement

{Specific research questions this test must answer. Bullet list.}

## 3. Test Objectives & Tasks

- **Objective:** {what we want to learn}
- **Tasks:** {list of task IDs and brief descriptions}
- **Screens in scope:** {screens_list}

## 4. User Profile

{Who the participants are, recruitment criteria, exclusion criteria.}

- **Recruitment profile:** {user_profile}
- **Exclusion criteria:** {e.g. "current users of this SUT", "members of the testing team"}

## 5. Method & Test Design

- **Test type:** {test_type}
- **Session format:** {moderated | unmoderated-remote}
- **Design:** within-subjects / between-subjects [for comparison only]
- **Number of participants:** {num_participants}
- **Session duration (estimated):** {derived from num_tasks × benchmark_time + 15 min SUS/debrief}

## 6. Environment & Equipment

- **SUT URL:** [Human fills]
- **Device:** [Human fills — desktop/laptop/mobile]
- **Browser:** [Human fills]
- **Screen recording:** [Human fills — OBS/Loom/Zoom/other]
- **SUS delivery:** [print / Google Form / in-session]

## 7. Test-Monitor Role

{Facilitator responsibilities, neutrality rules, when to intervene.}

Key rules:

- Do not guide or hint unless participant has been fully stuck for > 5 minutes
- Prompt think-aloud with neutral phrases only: "What are you thinking right now?"
- Record observations without interpreting or reacting visibly

## 8. Evaluation Measures & Data to Collect

### Performance Metrics (Objective)

| Metric           | Definition                                                                      | How Measured                  |
| ---------------- | ------------------------------------------------------------------------------- | ----------------------------- |
| Task Success     | Completed / Partial / Failed per task                                           | Observer judgment at task end |
| Time on Task     | Seconds from task start to task end or abandonment                              | Stopwatch                     |
| Error Count      | Number of incorrect clicks, wrong paths, or input mistakes requiring correction | Observer tally                |
| Hesitation Count | Number of pauses > 5 seconds with no action                                     | Observer tally                |

### Preference Metrics (Subjective)

| Instrument      | Description                                  | When Administered        |
| --------------- | -------------------------------------------- | ------------------------ |
| SUS             | 10-item System Usability Scale (Brooke 1996) | After all tasks complete |
| Probe Questions | 3–5 open questions per task                  | After each task          |

[Add any additional metrics from {metrics_to_collect} not already listed above]

## 9. Report Contents

The usability report will include:

- Test objectives and methodology summary
- Participant table (masked contacts)
- Task scenario(s) used
- Performance metrics summary table (all participants × all tasks)
- SUS scores table and interpretation
- Findings list ranked by severity (Nielsen 0–4)
- Prioritised recommendations
- Appendix: raw session notes (P1–P{n})
```

### `task-scenario-T{n}.md` Structure

```markdown
# Task Scenario T{n} — {sut_name}

## Context

{1–2 sentences describing a realistic situation that gives the participant a reason to perform the goal. Should feel authentic to the target user profile.}

## Task

{1–3 sentences describing the goal. Goal-based. No UI language. No embedded answer.}

## Success Criteria

| Level         | Definition                                                      |
| ------------- | --------------------------------------------------------------- |
| **Completed** | {Specific observable indicator of full success}                 |
| **Partial**   | {Specific observable indicator of partial success}              |
| **Failed**    | {Participant abandons, requests help, or reaches wrong outcome} |

## Benchmark Time

Expected completion time for a competent user: {benchmark_time}

## Screens Expected in Path

{screens_list — natural completion path should visit these screens}

## Self-Check (completed by AI before finalising)

- [ ] Goal-based — no UI element names mentioned
- [ ] Realistic context for the user profile
- [ ] Success criterion is specific and observable
- [ ] No embedded answer or path hints
- [ ] Natural completion path covers the listed screens
```

### `P{n}-session.md` Structure (Per-participant Session File)

```markdown
# Session Notes — Participant P{n}

> **SUT:** {sut_name}  
> **Participant ID:** P{n}  
> **Date:** {YYYY-MM-DD} · **Time:** {HH:MM}  
> **Mode:** {moderated | unmoderated-remote}

{If unmoderated-remote, include participant instruction block here}

## Pre-Task Briefing Confirmation

- [ ] Participant briefed on purpose (testing the product, not the person)
- [ ] Think-aloud protocol explained and practised
- [ ] Recording consent obtained
- [ ] Questions answered before starting

## Task T{n}: {brief task title}

**Task text presented to participant:**

{full task scenario text — copied from `task-scenario-T{n}`.md}

**Benchmark time:** {benchmark_time}

### Timing

| Field                      | Value   |
| -------------------------- | ------- |
| Task start time            | _blank_ |
| Task end time              | _blank_ |
| **Time on task (seconds)** | _blank_ |

### Result

| Field                | Value                          |
| -------------------- | ------------------------------ |
| **Task Result**      | ☐ Completed ☐ Partial ☐ Failed |
| **Error count**      | _blank_                        |
| **Hesitation count** | _blank_                        |

> **Guidance for recording errors:** Count each incorrect click, wrong navigation path, or input mistake that the participant had to recover from. Do not count deliberate exploration as an error.

> **Guidance for recording hesitations:** Count each pause of more than 5 seconds where the participant stopped interacting without speaking or acting. Note the timestamp.

### Observation Log

| Timestamp `[MM:SS]` | Behaviour | Verbalization (think-aloud) | Observer / Self Note |
| ------------------- | --------- | --------------------------- | -------------------- |
|                     |           |                             |                      |
|                     |           |                             |                      |
|                     |           |                             |                      |

> **Guidance for self-completion (unmoderated):** As you work through the task, record what you did at each step, what you said or thought, and any moments of confusion or surprise. You do not need to fill in every row — record the moments that felt significant.

### Probe Questions — T{n}

_Answer these questions after completing (or abandoning) the task above._

**Q1:** {probe question 1}  
**Answer:** _blank_

**Q2:** {probe question 2}  
**Answer:** _blank_

**Q3:** {probe question 3}  
**Answer:** _blank_

[Q4 and Q5 if applicable]

[Repeat Task section for each additional task]

## SUS Questionnaire

_Complete this after all tasks are finished. For each statement, circle or write the number that best describes your reaction: 1 = Strongly Disagree, 5 = Strongly Agree._

| #   | Statement                                                                                  | 1   | 2   | 3   | 4   | 5   |
| --- | ------------------------------------------------------------------------------------------ | --- | --- | --- | --- | --- |
| 1   | I think that I would like to use this system frequently.                                   | ☐   | ☐   | ☐   | ☐   | ☐   |
| 2   | I found the system unnecessarily complex.                                                  | ☐   | ☐   | ☐   | ☐   | ☐   |
| 3   | I thought the system was easy to use.                                                      | ☐   | ☐   | ☐   | ☐   | ☐   |
| 4   | I think that I would need the support of a technical person to be able to use this system. | ☐   | ☐   | ☐   | ☐   | ☐   |
| 5   | I found the various functions in this system were well integrated.                         | ☐   | ☐   | ☐   | ☐   | ☐   |
| 6   | I thought there was too much inconsistency in this system.                                 | ☐   | ☐   | ☐   | ☐   | ☐   |
| 7   | I would imagine that most people would learn to use this system very quickly.              | ☐   | ☐   | ☐   | ☐   | ☐   |
| 8   | I found the system very cumbersome to use.                                                 | ☐   | ☐   | ☐   | ☐   | ☐   |
| 9   | I felt very confident using the system.                                                    | ☐   | ☐   | ☐   | ☐   | ☐   |
| 10  | I needed to learn a lot of things before I could get going with this system.               | ☐   | ☐   | ☐   | ☐   | ☐   |

## Post-Session Open Comments

_Any other observations, suggestions, or reactions you would like to share._
```

## Anti-Patterns

| Anti-Pattern                                                   | Why it fails                                                                                  | Correct approach                                                                       |
| -------------------------------------------------------------- | --------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------- |
| Step-based task ("Click Add Event, then fill the Title field") | Removes the usability test — participants follow instructions instead of revealing friction   | Write goal-based tasks: "Set up the event so it is ready for participants to register" |
| Generic probe questions ("What did you think?")                | Produces shallow, unanalysable data                                                           | Write task-specific questions targeting observed friction points and expectation gaps  |
| SUS administered before tasks                                  | SUS measures overall system perception after exposure — administering it first biases results | Always administer SUS after all tasks are complete                                     |
| Recruiting current users of the SUT                            | They already know the interface; their performance does not reveal discoverability issues     | Recruit participants who match the profile but have no prior SUT experience            |
| Participant table with unmasked contact details                | Privacy violation                                                                             | Mask middle 4 digits of phone numbers; use first name + last initial only              |
| Measuring only task success without time and errors            | Success alone does not reveal efficiency problems                                             | Always measure at least: task success, time on task, and error count                   |
| Within-subjects comparison without counterbalancing            | Order effects bias results (second variant benefits from learning the first)                  | Counterbalance: half participants test A then B; other half test B then A              |

## Best Practices

- **Write task scenarios in the second person ("You are...").** This helps participants adopt the role and engage with the scenario realistically.
- **Pilot the task scenario with one person before the main sessions.** The pilot reveals anchoring language, unclear success criteria, and timing errors. Adjust before the real sessions. The pilot participant is not counted in the study sample.
- **3–5 probe questions per task is optimal.** Fewer than 3 leaves gaps; more than 5 causes fatigue, especially in remote sessions.
- **For unmoderated sessions, add an instruction paragraph above each section.** Remote participants need explicit guidance on what to record and how. Without it, observation logs will be inconsistent and hard to analyse.
- **Include a think-aloud practice task in the briefing.** Before the real task, give participants a short unrelated warm-up task (30–60 seconds) to practise thinking aloud. This normalises the behaviour before data collection begins.
- **Store raw session notes in a consistent directory structure.** Name files with participant IDs so they can be easily batch-processed in analysis.

## Quality Checklist

### Test Plan

- [ ] All 9 parts are present and non-empty.
- [ ] User profile includes both inclusion and exclusion criteria.
- [ ] All metrics from `metrics_to_collect` are defined in Part 8.
- [ ] Session duration estimate is present in Part 5.
- [ ] All content is written in English.

### Task Scenarios

- [ ] Each task is goal-based — no UI element names or action verbs referencing UI.
- [ ] Each task has a realistic context sentence.
- [ ] Each task has a three-level success criterion (Completed / Partial / Failed) with specific, observable indicators for each level.
- [ ] Each task has a benchmark time.
- [ ] Natural completion of each task passes through the listed screens.
- [ ] The task self-check was applied and all items passed.
- [ ] All content is written in English.

### Probe Questions

- [ ] 3–5 questions per task.
- [ ] All questions are open-ended and non-leading.
- [ ] Questions cover: path clarity, error recovery, trust in outcome, expectation gaps, and at least one open improvement question.
- [ ] No question names a specific UI element.
- [ ] All content is written in English.

### SUS Instrument

- [ ] All 10 standard SUS statements are present and unmodified.
- [ ] Scoring formula is included.
- [ ] Interpretation table is included.
- [ ] All content is written in English.

### Session Files

- [ ] `P{n}-session.md` files exist for all `{num_participants}` participants.
- [ ] Each file includes: task text, timing fields, result fields, observation log table, probe questions, SUS questionnaire, and post-session comments.
- [ ] Guidance text for error and hesitation recording is present.
- [ ] For unmoderated-remote: participant instruction text is present in each section.
- [ ] All content is written in English.

### Participant Table

- [ ] Correct number of placeholder rows.
- [ ] Contact masking instruction is present.
- [ ] Consent column is present.
- [ ] All content is written in English.

## Common Rationalisations to Reject

| Rationalisation                                                                                          | Why to reject                                                                                                                                                                                                              |
| -------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| _"The task scenario is clear enough — participants will figure out the UI language is just for context"_ | UI language in a task directly anchors participant behaviour. Rewrite it. There is no "just for context" — every word shapes behaviour.                                                                                    |
| _"I'll use the same 5 generic probe questions for all tasks"_                                            | Generic questions produce generic answers. Each task has unique friction points that probe questions must target specifically.                                                                                             |
| _"The participant table doesn't need exclusion criteria — anyone who fits the profile will do"_          | Without exclusion criteria, SUT experts, team members, and instructors may slip through and invalidate the data.                                                                                                           |
| _"I'll administer SUS before the tasks to save time at the end"_                                         | SUS measures post-exposure perception. Administering it before tasks produces meaningless scores.                                                                                                                          |
| _"5 participants is too few to make statistically significant claims"_                                   | Usability testing is not statistical sampling. Nielsen (2000) established that 5 participants typically surface ~85% of usability problems in a task-based test. The goal is problem discovery, not statistical inference. |
| _"We don't need a pilot session — the task scenario looks fine"_                                         | A scenario that looks fine to the designer frequently fails in participant hands. Always pilot.                                                                                                                            |

## Resources

| File                                   | Purpose                                                                       |
| -------------------------------------- | ----------------------------------------------------------------------------- |
| `resources/9part-plan-reference.md`    | Full field descriptions for the 9-part test plan (Rubin & Chisnell)           |
| `resources/probe-question-guide.md`    | Probe question patterns, anti-patterns, and examples by category              |
| `resources/sus-reference.md`           | Exact SUS statements, scoring formula, and Sauro & Lewis interpretation table |
| `resources/task-scenario-rules.md`     | Detailed rules and worked examples for writing valid task scenarios           |
| `examples/example-test-plan.md`        | Complete sample 9-part test plan                                              |
| `examples/example-session-template.md` | Complete sample P{n}-session.md for one participant                           |
