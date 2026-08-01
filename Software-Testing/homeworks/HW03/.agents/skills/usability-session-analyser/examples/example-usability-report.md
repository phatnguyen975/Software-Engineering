# Example: Usability Report

> **SUT:** Generic Web Admin Panel | N=5 participants | T=1 task | Assessment test  
> This example demonstrates the complete Usability Report format.

## File: usability-report.md

```markdown
# Usability Report — Generic Web Admin Panel

> **Prepared by:** `usability-session-analyser` skill (v1.0.0)  
> **Test type:** Assessment | Participants: 5 | Tasks: 1  
> **Status:** DRAFT — requires human review before submission

## Executive Summary

This usability assessment evaluated whether first-time admin users could create and publish an event using the web admin panel's event management interface. Five participants matching the target user profile completed one task scenario covering the event creation form and registration configuration screens. The overall mean SUS score was 64.5 (Grade D+, Poor), falling below the industry average of 68. Task success rate was 60% (3 of 5 participants completed the task); mean time on task was 9 minutes 12 seconds against an 8-minute benchmark. The most critical finding was that form validation errors appear only after submission — not on field blur — causing all five participants to fill the entire form before discovering mistakes. The highest-priority recommendation is to implement inline validation on the event creation form to surface errors as participants fill each field.

## 1. Methodology

- **Test type:** Assessment (mid-stage — product functional; quantitative metrics collected)
- **Session format:** Moderated in-person
- **Number of participants:** 5
- **Number of tasks:** 1
- **Screens covered by task:** A2: Add/Edit Event Form; A3: Registration Configuration
- **Instruments:** SUS (Brooke 1996); 5 probe questions per task; structured observation log
- **Heuristic framework used for analysis:** Nielsen's 10 Usability Heuristics (1994)
- **Estimated session duration:** 45–60 minutes per session

## 2. Participants

All participant contacts masked per privacy requirements. TA verification contact available upon request from the facilitator.

| P#  | Profile Summary                                                               | Session Date | Task T1 Result | SUS Score |
| --- | ----------------------------------------------------------------------------- | ------------ | -------------- | --------- |
| P1  | Faculty staff, 35, comfortable with web tools, no prior SUT experience        | 2026-07-10   | Completed      | 72.5      |
| P2  | Senior CS student (Year 4), web-savvy, no prior SUT experience                | 2026-07-10   | Partial        | 57.5      |
| P3  | Faculty staff, 42, moderate web experience, no prior SUT experience           | 2026-07-11   | Completed      | 65.0      |
| P4  | Senior CS student (Year 3), moderate web experience, no prior SUT experience  | 2026-07-11   | Failed         | 52.5      |
| P5  | Administrative staff, 28, comfortable with web tools, no prior SUT experience | 2026-07-12   | Completed      | 75.0      |

## 3. Task Scenario

### Task T1: Create and Publish an Event

**Context presented to participant:**

> The Faculty of Information Technology has just confirmed dates for an upcoming Machine Learning workshop scheduled for late August. The department head has asked you to get the event listed on the system as soon as possible so that faculty and students can begin registering. The workshop is limited to 30 students and 10 lecturers.

**Task:**

> Get the workshop set up and ready so that faculty and students can sign up for it. When you are done, show us where you would go to check who has registered.

**Success Criteria:**

| Level     | Observable indicator                                                                                        |
| --------- | ----------------------------------------------------------------------------------------------------------- |
| Completed | Event publicly visible with registration open; slot limits reflect brief (30 students, 10 lecturers)        |
| Partial   | Event saved but not published, OR published with incorrect slot limits                                      |
| Failed    | Participant abandons, requires facilitator help to complete, or outcome does not resemble a published event |

**Benchmark time:** 8 minutes (480 seconds)

## 4. Performance Metrics

### Task Results by Participant

| Participant | Task T1 Result | Time on Task (s) | Error Count | Hesitation Count |
| ----------- | -------------- | ---------------- | ----------- | ---------------- |
| P1          | Completed      | 423              | 2           | 1                |
| P2          | Partial        | 600 (abandoned)  | 7           | 5                |
| P3          | Completed      | 512              | 3           | 2                |
| P4          | Failed         | 600 (abandoned)  | 9           | 8                |
| P5          | Completed      | 387              | 1           | 1                |

### Aggregate Statistics — Task T1

| Metric                   | Value                     | Notes                                        |
| ------------------------ | ------------------------- | -------------------------------------------- |
| Success Rate (Completed) | 60% (3/5)                 |                                              |
| Partial Rate             | 20% (1/5)                 | P2: event saved as draft, not published      |
| Failure Rate             | 20% (1/5)                 | P4: abandoned at max time; event not created |
| Mean Time on Task        | 440.7s (7:21)             | Excludes P2 and P4 (abandoned); N=3 for mean |
| Time Range (completers)  | 387s–512s                 |                                              |
| Time to Abandonment      | 600s (both P2 and P4)     | Maximum allowed time reached                 |
| Benchmark Time           | 480s (8:00)               |                                              |
| vs Benchmark             | −39s (−8%) for completers | Completers finished within benchmark         |
| Mean Error Count         | 4.4                       | All 5 participants included                  |
| Mean Hesitation Count    | 3.4                       | All 5 participants included                  |

### Performance Interpretation

Three of five participants completed the task within the benchmark time, suggesting the core workflow is learnable for users with strong web tool experience. However, the two participants who did not complete the task both abandoned at the maximum allowed time — indicating complete task failure rather than marginal difficulty. Error and hesitation counts are elevated across all participants (mean 4.4 errors, 3.4 hesitations), pointing to consistent friction in the form interaction and registration configuration steps. Participants who expressed uncertainty about whether their actions had been saved (P2, P4) also had the highest error and hesitation counts.

## 5. SUS Scores

| Participant    | SUS Score |
| -------------- | --------- |
| P1             | 72.5      |
| P2             | 57.5      |
| P3             | 65.0      |
| P4             | 52.5      |
| P5             | 75.0      |
| **Group Mean** | **64.5**  |
| Minimum        | 52.5 (P4) |
| Maximum        | 75.0 (P5) |

**Mean SUS Score: 64.5 — Grade D+, "Poor" (Sauro & Lewis 2012)**

The mean score of 64.5 falls below the industry average of 68, placing this system in approximately the 35th percentile. The score range of 52.5–75.0 (spread of 22.5 points) indicates moderate variance in user experience — participants with stronger web tool backgrounds (P1, P5) rated the system more positively than those who struggled with the task (P2, P4). The poor score is consistent with the task success rate of 60% and elevated error counts.

## 6. Findings

Findings ranked by criticality (Severity × Frequency, descending). Severity scale: Nielsen (1994) 0–4.

### Finding F-01 — Form validation fires only on submit, not on field blur

| Field                  | Value                                                                                            |
| ---------------------- | ------------------------------------------------------------------------------------------------ |
| **Severity**           | 3 — Major Usability Problem                                                                      |
| **Frequency**          | 5/5 participants (100%)                                                                          |
| **Criticality**        | 3 × 1.0 = 3.0                                                                                    |
| **Type**               | Systemic                                                                                         |
| **Heuristic violated** | N9: Help Users Recognise, Diagnose, and Recover from Errors; <br>S5: Offer Simple Error Handling |
| **Tasks affected**     | T1                                                                                               |

**Observation:** All five participants filled out the entire event creation form before discovering that required fields were invalid. Errors appeared only after clicking Submit, and were displayed as a summary list at the top of the page — not adjacent to the offending fields. All participants had to scroll up, read the error list, scroll back down to find the relevant field, and re-enter data.

**Evidence:**

> _"I thought I was done. Then I got all these errors at the top and had to figure out which fields they meant." — P3, probe Q1_

> _[02:34] P4 clicks Submit → error summary appears at page top → P4 scrolls up → reads for 18s → scrolls back down → cannot identify which field is "Event Title" because the field is not highlighted_ — P4, T1 observation log

> _[03:12] P2 submits → 5 errors shown → "I have to go through all of these now?" → hesitation 11s_ — P2, T1 observation log

**Confidence:** High — supported by observation behaviour, verbalisation, and probe answers across all 5 participants.

**Recommendation:** Implement `onBlur` (field exit) validation for all required fields. Display an inline error message directly below each field when the user leaves it empty or enters invalid data. Retain submit-time validation as a final check. Error messages must name the specific issue and suggest a fix (e.g. "Event title is required — enter up to 150 characters").

### Finding F-02 — Participants uncertain whether Save Draft succeeded

| Field                  | Value                                               |
| ---------------------- | --------------------------------------------------- |
| **Severity**           | 3 — Major Usability Problem                         |
| **Frequency**          | 4/5 participants (80%)                              |
| **Criticality**        | 3 × 0.8 = 2.4                                       |
| **Type**               | Systemic                                            |
| **Heuristic violated** | N1: Visibility of System Status; <br>NOR5: Feedback |
| **Tasks affected**     | T1                                                  |

**Observation:** Four of five participants used Save Draft at least once during the task. Three of these four (P2, P3, P4) expressed uncertainty about whether the save had succeeded — two navigated away and then returned to check whether the event still existed.

**Evidence:**

> _"Did that actually save? I'm not sure." — P2, verbalisation at [04:15]_

> _[04:18] P3 clicks Save Draft → no visible confirmation → waits 5s → clicks again → duplicate save attempt_ — P3, T1 observation log

> _"After I saved I went back to the list to check it was there. I wasn't confident." — P3, probe Q3_

**Recommendation:** Display a persistent, visible save confirmation immediately after Save Draft succeeds — for example, a toast notification ("Draft saved") that persists for at least 4 seconds, and a "Last saved: HH:MM" indicator in the form header that updates on each save.

### Finding F-03 — "Max Slots" label not understood by non-technical participants

| Field                  | Value                                                               |
| ---------------------- | ------------------------------------------------------------------- |
| **Severity**           | 2 — Minor Usability Problem                                         |
| **Frequency**          | 3/5 participants (60%)                                              |
| **Criticality**        | 2 × 0.6 = 1.2                                                       |
| **Type**               | Systemic                                                            |
| **Heuristic violated** | N2: Match Between System and Real World; <br>NOR6: Conceptual Model |
| **Tasks affected**     | T1                                                                  |

**Observation:** Three participants hesitated at the "Max Slots" field in the registration configuration section. Two entered a combined total (40) rather than separate values per role (30 students + 10 lecturers), requiring correction.

**Evidence:**

> _[05:30] P5 pauses 12s at Max Slots → "I don't know if this is total or per type"_ — P5, T1 observation log

> _"Max Slots sounds like a technical term. I wasn't sure if it meant total spots or spots per group." — P4, probe Q2_

**Recommendation:** Rename "Max Slots" to "Maximum Registrations" and add a helper text label directly below each field: "Maximum number of [students / lecturers / guests] who can register. Leave blank for unlimited." Consider adding a total-capacity summary that auto-calculates as users fill in each role's limit.

### Finding F-04 — Required field indicator absent on all required fields

| Field                  | Value                                                   |
| ---------------------- | ------------------------------------------------------- |
| **Severity**           | 2 — Minor Usability Problem                             |
| **Frequency**          | 3/5 participants (60%)                                  |
| **Criticality**        | 2 × 0.6 = 1.2                                           |
| **Type**               | Systemic                                                |
| **Heuristic violated** | NOR2: Signifiers; <br>WCAG3.3.2: Labels or Instructions |
| **Tasks affected**     | T1                                                      |

**Observation:** Three participants expressed surprise when submit-time validation revealed that fields they had skipped were required — they had no prior indication the fields were mandatory.

**Evidence:**

> _"There's no star or anything. I had no idea that was required." — P1, probe Q2_

**Recommendation:** Add a red asterisk (`*`) adjacent to every required field label and a legend note at the top of the form: "Fields marked `*` are required."

### Finding F-05 — Thumbnail upload silently rejects invalid file formats

| Field                  | Value                                                                   |
| ---------------------- | ----------------------------------------------------------------------- |
| **Severity**           | 2 — Minor Usability Problem                                             |
| **Frequency**          | 2/5 participants (40%)                                                  |
| **Criticality**        | 2 × 0.4 = 0.8                                                           |
| **Type**               | Isolated                                                                |
| **Heuristic violated** | N9: Help Users Recover from Errors; <br>N1: Visibility of System Status |
| **Tasks affected**     | T1                                                                      |

**Observation:** Two participants (P2, P4) attempted to upload a PNG file to the Thumbnail field. The upload silently failed — the file input cleared with no error message. Both participants re-attempted the upload, then moved on, unsure whether the field was optional or whether they had made a mistake.

**Evidence:**

> _[06:45] P2 selects file → upload area clears → P2 waits 4s → "Did it not take it?" → tries again → same result → moves on_ — P2, T1 observation log

**Recommendation:** On file selection, validate format and size client-side. Display an inline error immediately if rejected: "This file format is not supported. Please upload a JPG or PNG image." Do not clear the input without explanation.

## 7. Prioritised Recommendations

| Priority | Recommendation                                                                | Finding(s) | Estimated Impact                                                     |
| -------- | ----------------------------------------------------------------------------- | ---------- | -------------------------------------------------------------------- |
| 1        | Implement inline (on-blur) form validation with field-adjacent error messages | F-01       | High — eliminates the #1 friction point; affects all 5 participants  |
| 2        | Add save confirmation feedback (toast + "Last saved" timestamp)               | F-02       | High — eliminates uncertainty for 4 of 5 participants                |
| 3        | Rename "Max Slots" to "Maximum Registrations" with per-role helper text       | F-03       | Medium — reduces input errors in registration config; affects 3 of 5 |
| 4        | Add required field asterisks and legend to event creation form                | F-04       | Medium — prevents surprise at submit time; low implementation cost   |
| 5        | Add inline error message for rejected file uploads                            | F-05       | Low-Medium — affects 2 of 5; prevents silent data loss               |

## 8. Limitations

- **Sample size:** 5 participants. Findings indicate patterns and directions — they are not statistically significant. Results should be treated as a qualitative signal to guide design decisions, not as population-level measurements.
- **Session format:** All sessions were moderated in-person, which may have slightly reduced observable friction (participants who would normally abandon in an unobserved setting may have persisted longer due to social presence).
- **Single task:** One task scenario was used. Screens A4 (Participants Review) and A5 (Check-in Tab) were not tested in this study. Usability issues on those screens are not captured here.
- **No missing data** — all 5 session files were complete.

## Appendix A — Raw Session Notes

- docs/usability/session-notes/P1-session.md
- docs/usability/session-notes/P2-session.md
- docs/usability/session-notes/P3-session.md
- docs/usability/session-notes/P4-session.md
- docs/usability/session-notes/P5-session.md

## Appendix B — Raw SUS Responses

→ docs/usability/sus-scores-computed.md (full individual response table)
```
