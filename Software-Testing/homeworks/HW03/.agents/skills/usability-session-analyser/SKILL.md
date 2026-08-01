---
name: usability-session-analyser
description: >
  Analyse raw usability session data from N participants — compute SUS scores,
  aggregate performance metrics, cluster pain points by heuristic, rank findings
  by severity and frequency, and produce a complete Usability Report. This skill
  is pure analysis: it does not design tasks or instruments. Invoke when asked to
  "analyse usability session data", "compute SUS scores", "produce a usability report",
  or "summarise findings from user testing sessions".
version: 1.0.0
author: phatnguyen975
---

# Usability Session Analyser Skill

## Overview

This skill analyses raw data collected from N usability test sessions and produces three structured output files:

1. **`sus-scores-computed.md`** — computed SUS scores per participant, group mean, and interpretation
2. **`metrics-summary.md`** — aggregated performance metrics table across all participants and tasks
3. **`usability-report.md`** — complete Usability Report structured per Rubin & Chisnell (2008): executive summary, methodology, participant table, task scenario(s), metrics, findings ranked by criticality, and prioritised recommendations

The skill is **data-agnostic** — it works with any number of participants, any number of tasks, and any heuristic set. It does not make assumptions about the SUT, the scenario, or the testing format.

### Inputs

```
REQUIRED
──────────────────────────────────────────────────────────────────────
session_notes_paths       : list of strings (paths)
                            Paths to all per-participant session files.
                            These files contain observation logs, task results,
                            timing data, error/hesitation tallies, probe answers,
                            and raw SUS responses.
                            Example:
                              - "docs/usability/session-notes/P1-session.md"
                              - "docs/usability/session-notes/P2-session.md"
                              - "docs/usability/session-notes/P3-session.md"

test_plan_path            : string (path)
                            Path to the 9-part test plan file.
                            Used to populate methodology section of the report
                            (test type, session format, user profile, metrics defined).
                            Example: "docs/usability/test-plan.md"

task_scenario_paths       : list of strings (paths)
                            Paths to all task scenario files (one per task).
                            Used to include task text and success criteria in the report.
                            Example:
                              - "docs/usability/task-scenario-T1.md"

participant_table_path    : string (path)
                            Path to the filled participant table.
                            Used to populate the participant section of the report.
                            Example: "docs/usability/participant-table.md"

heuristic_set             : string
                            Name of the heuristic framework used to cluster pain points.
                            Example: "Nielsen 10" | "Shneiderman 8" | "Norman 6"

output_dir                : string (path)
                            Directory where all output files will be written.
                            Example: "docs/usability"

OPTIONAL
──────────────────────────────────────────────────────────────────────
comparison_variant_labels : list of strings
                            Required only if the test was a comparison (A/B) test.
                            Labels for each variant.
                            Example: ["Variant A: Current design", "Variant B: Redesigned nav"]

report_audience           : string [default: "academic"]
                            "academic" — structured report with appendices, suitable for
                            course submission; includes raw data references.
                            "professional" — executive-focused; shorter findings section;
                            recommendations prioritised by business impact.
```

### Invoke Format

```
/usability-session-analyser
  session_notes_paths:
    - "<path/to/P1-session.md>"
    - "<path/to/P2-session.md>"
    - "<path/to/P3-session.md>"
  test_plan_path: "<path>"
  task_scenario_paths:
    - "<path/to/task-scenario.md>"
  participant_table_path: "<path>"
  heuristic_set: "<Nielsen 10 | Shneiderman 8 | Norman 6>"
  output_dir: "<path>"
  comparison_variant_labels:
    - "<Variant A label>"
    - "<Variant B label>"
  report_audience: "<academic | professional>"
```

### Outputs

| File                                  | Description                                              | Template                                   |
| ------------------------------------- | -------------------------------------------------------- | ------------------------------------------ |
| `{output_dir}/sus-scores-computed.md` | Per-participant SUS scores + group mean + interpretation | See Output Templates below                 |
| `{output_dir}/metrics-summary.md`     | Performance metrics table + aggregate statistics         | See Output Templates below                 |
| `{output_dir}/usability-report.md`    | Complete Usability Report                                | See `examples/example-usability-report.md` |

## When to Use

- All session files have been filled and are ready for analysis.
- You need computed SUS scores with interpretation.
- You need a structured performance metrics table aggregated across participants.
- You need a Usability Report with findings ranked by severity and frequency.
- You want to identify systemic usability issues vs isolated incidents.

## When NOT to Use

- Session data is incomplete — wait until all participants have filled their session files.
- You need to design the test or task scenarios — that is a separate concern.
- You want to analyse GUI checklist results — this skill is for session-based usability data only.
- You have fewer than 3 participants — results will be too sparse to cluster meaningfully; complete remaining sessions first.

## Core Principles

1. **Separate computation from interpretation.** SUS scores and performance metrics are computed mechanically — no judgment involved. Pain point clustering and severity rating require judgment. Keep these phases distinct.
2. **Distinguish systemic issues from isolated incidents.** A pain point observed by 1 of 5 participants may be an outlier. The same pain point observed by 3 or more is systemic. Systemic issues drive recommendations; isolated incidents are noted but not prioritised.
3. **Criticality = severity × frequency.** A severe problem that only one person encountered ranks lower than a moderate problem that all five encountered. Use the criticality formula to produce a defensible ranking.
4. **Report findings, not solutions.** The analysis section describes what was observed and what heuristic it violates. The recommendations section is where solutions belong. Mixing them obscures the evidence base.
5. **Never fabricate data.** If a session file is missing a field, note it as "not recorded" in the output. Do not estimate or interpolate missing values.
6. **Human reviews the report before submission.** The skill produces a draft. Findings that require contextual judgment — particularly the recommended fix wording — must be reviewed and refined by the human before the report is finalised.

## Workflow

### Step 1 — Validate and Read All Input Files

Read all files listed in `session_notes_paths`, `test_plan_path`, `task_scenario_paths`, and `participant_table_path`.

For each session file, extract:

- Participant ID
- Task result (Completed / Partial / Failed) per task
- Time on task (seconds) per task
- Error count per task
- Hesitation count per task
- Raw SUS responses (10 values, 1–5 each)
- Probe question answers per task
- Observation log entries

If any required field is missing in a session file → note it as "not recorded" in the output. Do not skip the participant — include them with the available data only.

### Step 2 — Compute SUS Scores

For each participant, compute their SUS score:

1. Odd-numbered questions (1, 3, 5, 7, 9): adjusted = raw − 1
2. Even-numbered questions (2, 4, 6, 8, 10): adjusted = 5 − raw
3. SUS score = sum of 10 adjusted values × 2.5 (range: 0–100)

Compute group statistics:

- Mean SUS score across all participants
- Minimum and maximum individual scores
- Map mean to adjective rating using Sauro & Lewis (2012) interpretation table

For comparison (A/B) tests: compute mean SUS per variant separately.

Write results to `{output_dir}/sus-scores-computed.md`.

See `resources/sus-scoring-reference.md` for the formula, worked example, and interpretation table.

### Step 3 — Aggregate Performance Metrics

For each task across all participants, compute:

- **Task Success Rate:** % Completed, % Partial, % Failed
- **Mean Time on Task:** arithmetic mean of seconds (exclude abandoned tasks from mean, note exclusions)
- **Mean Error Count:** arithmetic mean
- **Mean Hesitation Count:** arithmetic mean

For comparison tests: compute all metrics per variant.

Write results to `{output_dir}/metrics-summary.md`.

See `resources/metrics-aggregation-guide.md` for handling edge cases (abandoned tasks, missing values, outlier times).

### Step 4 — Extract and Cluster Pain Points

Read all observation log entries and probe question answers across all session files.

**Step 4a — Extract raw pain points:** A pain point is any observation that indicates friction, confusion, error, or dissatisfaction. Extract each distinct pain point as a raw item with:

- Participant ID(s) that experienced it
- Task ID
- Source (observation log timestamp or probe question)
- Raw description (exact or paraphrased observation)

**Step 4b — Cluster into themes:** Group raw pain points that share the same underlying usability issue. Name each cluster with a short, specific title (not a heuristic label).

- Good cluster title: "Validation errors appear only on submit, not on field blur"
- Poor cluster title: "Error prevention issues"

**Step 4c — Map to heuristic:** For each cluster, identify the heuristic(s) from `heuristic_set` that the issue violates. See `resources/pain-point-clustering-guide.md` for mapping patterns.

**Step 4d — Classify as systemic or isolated:**

- **Systemic:** observed by ≥ 50% of participants (e.g. 3 of 5)
- **Isolated:** observed by < 50% of participants

**Step 4e — Rate severity:** Using Nielsen (1994) severity scale (0–4):

| Level | Label         | Definition                                                            |
| ----- | ------------- | --------------------------------------------------------------------- |
| 4     | Catastrophe   | Prevents task completion; user cannot proceed                         |
| 3     | Major         | Significant difficulty; task completed only with effort or workaround |
| 2     | Minor         | Friction or confusion; task completed without major difficulty        |
| 1     | Cosmetic      | Annoyance; no functional impact                                       |
| 0     | Not a problem | Behaviour is acceptable                                               |

See `resources/severity-frequency-reference.md` for calibration examples.

**Step 4f — Compute criticality and rank:** Criticality = Severity (0–4) × Frequency (number of participants affected / total participants)

Sort findings by criticality descending. Systemic issues of the same criticality rank above isolated issues.

### Step 5 — Generate Output Files

**Write `sus-scores-computed.md`** using the template in the Output Templates section.

**Write `metrics-summary.md`** using the template in the Output Templates section.

**Write `usability-report.md`** using the structure in the Output Templates section. See `examples/example-usability-report.md` for a complete formatted example.

The report is a **draft** — findings sections and recommendations require human review before the report is considered final.

### Step 6 — Human Verification Gate

Present to the human:

```md
Analysis complete. Files written to: {output_dir}

- `sus-scores-computed.md` — {N} participants, mean SUS: {score} ({adjective rating})
- `metrics-summary.md` — {N} participants, {T} tasks
- `usability-report.md` — {F} findings ({S4} critical, {S3} major, {S2} minor, {S1} cosmetic)

Please review `usability-report.md`, particularly:

- Are the finding titles accurate and specific?
- Are severity ratings calibrated correctly?
- Do the recommendations reflect feasible fixes?
- Is the executive summary accurate?

Reply `APPROVED` to finalise, or `FAILED: <feedback>` to request corrections.
```

Apply corrections and re-present the gate until human replies `APPROVED`.

## Output Templates

### `sus-scores-computed.md` Structure

```markdown
# SUS Scores — {sut_name}

> Computed by: usability-session-analyser v1.0.0
> Formula: Brooke (1996) | Interpretation: Sauro & Lewis (2012)

## Individual Scores

| Participant    | Q1  | Q2  | Q3  | Q4  | Q5  | Q6  | Q7  | Q8  | Q9  | Q10 | SUS Score   |
| -------------- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | ----------- |
| P1             | {r} | {r} | {r} | {r} | {r} | {r} | {r} | {r} | {r} | {r} | **{score}** |
| P2             |     |     |     |     |     |     |     |     |     |     |             |
| ...            |     |     |     |     |     |     |     |     |     |     |             |
| **Group Mean** |     |     |     |     |     |     |     |     |     |     | **{mean}**  |

## Group Summary

| Metric               | Value        |
| -------------------- | ------------ |
| Mean SUS Score       | {mean}       |
| Minimum              | {min} (P{n}) |
| Maximum              | {max} (P{n}) |
| Adjective Rating     | {rating}     |
| Grade                | {A/B/C/D/F}  |
| Percentile (approx.) | {percentile} |

## Interpretation

{1–2 sentences interpreting the group score in context of the industry average (68)
and the adjective rating. Note any wide variance between individual scores if present.}

## Scoring Formula Applied

- Odd questions (1,3,5,7,9): adjusted = raw − 1
- Even questions (2,4,6,8,10): adjusted = 5 − raw
- SUS score = sum of 10 adjusted values × 2.5
```

### `metrics-summary.md` Structure

```markdown
# Performance Metrics Summary — {sut_name}

> Computed by: usability-session-analyser v1.0.0

## Task Results by Participant

| Participant | Task T{n} Result             | Time on Task (s) | Error Count | Hesitation Count |
| ----------- | ---------------------------- | ---------------- | ----------- | ---------------- |
| P1          | Completed / Partial / Failed | {seconds}        | {n}         | {n}              |
| P2          |                              |                  |             |                  |
| ...         |                              |                  |             |                  |

## Aggregate Statistics

| Metric                     | Task T{n}           | {T{n+1} if applicable} |
| -------------------------- | ------------------- | ---------------------- |
| Success Rate (% Completed) | {%}                 |                        |
| Partial Rate               | {%}                 |                        |
| Failure Rate               | {%}                 |                        |
| Mean Time on Task          | {seconds} ({mm:ss}) |                        |
| Time Range                 | {min}s – {max}s     |                        |
| Mean Error Count           | {n}                 |                        |
| Mean Hesitation Count      | {n}                 |                        |

> Notes:
>
> - {Any abandoned tasks excluded from time mean — note participant IDs}
> - {Any missing data — note field and participant ID}

## Performance Interpretation

{2–3 sentences interpreting the aggregate results: overall success rate, typical
completion time relative to benchmark, and whether errors/hesitations suggest specific
friction points that appear in the findings.}
```

### `usability-report.md` Structure

```markdown
# Usability Report — {sut_name}

> **Prepared by:** `usability-session-analyser` skill (v1.0.0)  
> **Test type:** {test_type} | Participants: {N} | Tasks: {T}  
> **Status:** DRAFT — requires human review before submission

## Executive Summary

{3–5 sentences covering: what was tested, who participated, key metric results (success rate, mean SUS score + adjective rating), top finding by criticality, and primary recommendation. Written in plain language for a non-technical audience.}

## 1. Methodology

{Summarise from `test-plan.md`:}

- **Test type:** {exploratory / assessment / validation / comparison}
- **Session format:** {moderated in-person / moderated remote / unmoderated remote}
- **Number of participants:** {N}
- **Number of tasks:** {T}
- **Instruments:** SUS (Brooke 1996); probe questions per task; observation log
- **Heuristic framework used for analysis:** {heuristic_set}
- **Session duration (approx.):** {from `test-plan.md`}

## 2. Participants

{Include participant-table content. Contacts masked per privacy requirements.}

| P#  | Profile Summary    | Session Date | Task Result              | SUS Score |
| --- | ------------------ | ------------ | ------------------------ | --------- |
| P1  | {role, background} | {date}       | Completed/Partial/Failed | {score}   |
| ... |                    |              |                          |           |

## 3. Task Scenario(s)

### Task T1: {title}

{Include full task text from `task-scenario-T1.md`}

**Success criteria:**

| Level     | Observable indicator |
| --------- | -------------------- |
| Completed | {from task scenario} |
| Partial   | {from task scenario} |
| Failed    | {from task scenario} |

**Benchmark time:** {from task scenario}

{Repeat for each task if num_tasks > 1}

## 4. Performance Metrics

{Include full `metrics-summary.md` content here, or reference the file.}

## 5. SUS Scores

{Include sus-scores-computed.md group summary here, or reference the file.}

**Mean SUS Score: {mean} — {adjective rating}**

## 6. Findings

Findings are ranked by criticality (Severity × Frequency). Severity scale: Nielsen (1994) 0–4. Frequency: proportion of participants affected.

> See `resources/severity-frequency-reference.md` for scale definitions.

### Finding F-01 — {Short specific title}

| Field                  | Value                                                                   |
| ---------------------- | ----------------------------------------------------------------------- |
| **Severity**           | {0–4} — {label}                                                         |
| **Frequency**          | {n}/{N} participants ({%})                                              |
| **Criticality**        | {severity × frequency as decimal, e.g. 3 × 0.8 = 2.4}                   |
| **Type**               | Systemic / Isolated                                                     |
| **Heuristic violated** | {N9: Help Users Recover from Errors / S5: Simple Error Handling / etc.} |
| **Tasks affected**     | T{n}                                                                    |

**Observation:** {What was observed across sessions — specific, not subjective. Cite participant IDs: "P1, P3, and P4 all..." or "All 5 participants..."}

**Evidence:**

> _"{direct quote or paraphrase from observation log or probe answer, with P-ID and timestamp}"_

**Recommendation:** {Concrete, actionable fix. One paragraph. Specific enough for a developer to act on.}

{Repeat Finding block for each finding, in descending criticality order}

## 7. Prioritised Recommendations

| Priority | Recommendation      | Finding(s) | Estimated Impact                                        |
| -------- | ------------------- | ---------- | ------------------------------------------------------- |
| 1        | {Most critical fix} | F-01       | High — affects {N} participants, blocks task completion |
| 2        |                     |            |                                                         |
| 3        |                     |            |                                                         |
| 4        |                     |            |                                                         |
| 5        |                     |            |                                                         |

## 8. Limitations

{Note any constraints that affect the validity or generalisability of findings:}

- Sample size ({N} participants — findings indicate patterns, not statistical significance)
- {Any session format limitations — e.g. unmoderated remote sessions have less rich observation data}
- {Any missing data — e.g. "P3 SUS data not recorded; excluded from SUS analysis"}

## Appendix A — Raw Session Notes

{List paths to all P{n}-session.md files}

## Appendix B — Raw SUS Responses

{Include full SUS response table from sus-scores-computed.md}
```

## Anti-Patterns

| Anti-Pattern                                         | Why it fails                                                                              | Correct approach                                                           |
| ---------------------------------------------------- | ----------------------------------------------------------------------------------------- | -------------------------------------------------------------------------- |
| Treating every observation as a separate finding     | Produces a list of 30+ items with no clear priority                                       | Cluster observations by root cause before rating severity                  |
| Severity rated by how annoying it seems              | Results in inflated severity ratings that undermine the report's credibility              | Apply Nielsen (1994) criteria strictly: does it prevent task completion?   |
| Reporting mean SUS without individual scores         | Hides bimodal distributions where participants had opposite experiences                   | Always show individual scores alongside the mean                           |
| Fabricating or estimating missing session data       | Introduces data that was never observed                                                   | Note missing fields explicitly; exclude from relevant calculations         |
| Mixing findings and recommendations in one paragraph | Makes it hard to evaluate whether the evidence supports the fix                           | Report findings (observations) separately from recommendations (solutions) |
| Excluding outlier participants from the mean         | Selectively improves metrics; not valid without stated pre-registered criteria            | Include all participants; note outliers in the Limitations section         |
| Using only probe answers to identify pain points     | Probe answers capture retrospective perception; observation logs capture actual behaviour | Always triangulate: observation log + probe answers + task result          |

## Best Practices

- **Triangulate across three data sources for every finding.** The strongest findings are supported by: (1) observed behaviour in the log, (2) participant verbalisation, and (3) probe question answer. A finding with only one source should be noted as lower-confidence.
- **Quote participants directly in the evidence field.** Exact quotes from observation logs or probe answers are more persuasive than paraphrases and easier for reviewers to verify.
- **Report criticality numerically.** `Severity 3 × Frequency 0.8 = 2.4` is more defensible than "high priority". This also enables consistent ranking across findings.
- **Keep finding titles specific and observable.** "Validation fires only on submit" is better than "Error prevention issue". Specific titles enable developers to immediately understand the scope of the fix.
- **Separate systemic from isolated findings visually.** In the report, clearly label each finding as Systemic or Isolated. Recommendations should focus on systemic issues unless an isolated issue is Severity 4.
- **State limitations explicitly.** With 5 participants, findings indicate patterns and directions — not statistical certainty. Saying so explicitly increases the report's credibility with technically sophisticated readers.

## Quality Checklist

### SUS Scores

- [ ] Scores computed for all participants using the correct formula.
- [ ] Group mean, min, and max reported.
- [ ] Mean mapped to Sauro & Lewis adjective rating.
- [ ] For comparison tests: scores computed per variant.
- [ ] Missing SUS data (if any) noted explicitly.
- [ ] All content written in **English**.

### Performance Metrics

- [ ] Task result counts correct (Completed + Partial + Failed = N per task).
- [ ] Mean time excludes abandoned tasks (noted in output).
- [ ] All four metrics present: success rate, time, errors, hesitations.
- [ ] For comparison tests: metrics computed per variant.
- [ ] All content written in **English**.

### Findings

- [ ] Each finding has a specific, observable title (not a heuristic label).
- [ ] Each finding includes: severity, frequency, criticality, heuristic, evidence, recommendation.
- [ ] Evidence includes at least one direct quote or specific observation with participant ID.
- [ ] Systemic vs isolated classification is present for each finding.
- [ ] Findings ranked by criticality (descending).
- [ ] All content written in **English**.

### Usability Report

- [ ] All 8 sections present (Executive Summary through Appendices).
- [ ] Participant table includes masked contacts.
- [ ] Task scenario text included verbatim from input files.
- [ ] Metrics and SUS sections reference or include computed output files.
- [ ] Recommendations are prioritised and link to finding IDs.
- [ ] Limitations section is present and honest.
- [ ] Report marked as DRAFT pending human review.
- [ ] All content written in **English**.

## Common Rationalisations to Reject

| Rationalisation                                                               | Why to reject                                                                                                                                                                                       |
| ----------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| _"P3's time was an outlier so I excluded it from the mean"_                   | Exclusion requires pre-registered criteria. Include all data; note outliers in Limitations.                                                                                                         |
| _"The SUS score is 72 which is above average, so usability is fine"_          | A score above 68 does not mean the product is ready. Findings with Severity 3–4 require fixes regardless of the SUS score.                                                                          |
| _"I combined all observations into one big finding to keep the report short"_ | Combining distinct root causes hides the scope of individual problems. Cluster by root cause, not by page or feature.                                                                               |
| _"The probe answers were vague so I skipped them"_                            | Even vague answers provide signal. Report them as "low-confidence evidence" rather than omitting them.                                                                                              |
| _"5 participants is not enough to draw conclusions"_                          | Nielsen (2000) established that 5 participants surface ~85% of usability problems in task-based tests. The appropriate caveat is in the Limitations section — not a reason to abandon the analysis. |
| _"I'll skip the human review gate — the report looks complete"_               | Finding severity ratings and recommendation wording require human judgment to be credible. The gate exists for this reason.                                                                         |

## Resources

| File                                        | Purpose                                                                      |
| ------------------------------------------- | ---------------------------------------------------------------------------- |
| `resources/sus-scoring-reference.md`        | SUS formula, worked example, and Sauro & Lewis interpretation table          |
| `resources/metrics-aggregation-guide.md`    | Rules for handling missing values, abandoned tasks, and outlier times        |
| `resources/pain-point-clustering-guide.md`  | How to extract, cluster, and map pain points to heuristics                   |
| `resources/severity-frequency-reference.md` | Nielsen (1994) severity scale with calibration examples; criticality formula |
| `examples/example-usability-report.md`      | Complete sample Usability Report with all sections populated                 |
