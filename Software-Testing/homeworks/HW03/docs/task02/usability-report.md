# Usability Report — EMS (Event Management System)

> **Prepared by:** `usability-session-analyser` skill (v1.0.0)  
> **Test type:** Assessment | Participants: 5 | Tasks: 1  
> **Heuristic framework:** Nielsen's 10 Usability Heuristics (1994)  
> **Status:** APPROVED

## Executive Summary

This report presents the findings of a moderated usability assessment of the EMS (Event Management System) administered to five first-time admin users at the Faculty of Information Technology, HCMUS. Participants completed a single end-to-end task (T1) covering event creation, publication, and registration management across three screens (A1, A2, A4). All five participants completed the task successfully within the 12-minute benchmark, yielding a 100% task success rate and a mean completion time of 6 minutes 36 seconds. The group mean SUS score was **80.0 ("Good")**, indicating that the system is broadly usable. The most critical finding is that the A2 form presents a high cognitive load due to six clustered date/time fields lacking contextual labels or inline help, causing hesitation or errors in all five sessions. The primary recommendation is to add inline tooltip labels to these fields and to display a success toast/notification after publishing to close the feedback gap.

## 1. Methodology

- **Test type:** Assessment (mid-stage; product is functional, core concepts fixed)
- **Session format:** Moderated in-person
- **Number of participants:** 5
- **Number of tasks:** 1 (T1 — Create, Publish, and Manage Event Registrations)
- **Instruments:** SUS (Brooke 1996); 5 probe questions per task; observation log with timestamps; error and hesitation tally
- **Heuristic framework used for analysis:** Nielsen's 10 Usability Heuristics (1994)
- **Session duration (approx.):** 30–40 minutes per participant
  - Pre-task briefing: 5 min
  - Think-aloud practice: 3 min
  - Main task T1 (benchmark 12 min, max 20 min): up to 20 min
  - SUS questionnaire: 3 min
  - Probe question debrief: 5–7 min
- **Pilot session:** 1 pilot session conducted prior to main sessions (not counted)
- **Data collection:** Manual notes in per-participant `Pn-session.md` files; no screen recordings (all participants declined consent)

## 2. Participants

| P#  | Profile Summary                                        | Session Date | Task T1 Result | SUS Score |
| --- | ------------------------------------------------------ | ------------ | -------------- | --------- |
| P1  | IT background; comfortable with web admin tools        | 2026-08-02   | Completed      | 92.5      |
| P2  | Non-IT background; moderate web experience             | 2026-08-02   | Completed      | 70.0      |
| P3  | Non-IT background; less experienced with admin forms   | 2026-08-02   | Completed      | 75.0      |
| P4  | IT background; experienced with web-based systems      | 2026-08-02   | Completed      | 82.5      |
| P5  | Mixed background; proactive; switched UI to Vietnamese | 2026-08-03   | Completed      | 80.0      |

> Full participant table with masked contacts: see [`participant-table.md`](./participant-table.md)

## 3. Task Scenario

### Task T1: Create, Publish, and Manage Event Registrations

> **Benchmark time:** 12 minutes | **Maximum allowed:** 20 minutes

_"You are a faculty administrator at the Faculty of Information Technology. Your department head has just confirmed that a **Machine Learning Hands-On Workshop** will be held on **September 20, 2026**, from **8:00 AM to 12:00 PM**. The event is open to both students and lecturers, with a limit of **30 students** and **10 lecturers**. Registration closes on **September 13, 2026**._

_Your head has asked you to get the event listed on the faculty's event management system as soon as possible — with a clear title, a short description of the workshop content, and a relevant cover image — so that students and lecturers can begin registering. You have been given admin access to the system and this is your first time using it._

_After setting up the event, a few participants have already registered. Your head needs you to look over the list of registrants and approve those who are eligible, then export the final list for the department records._

_Using the system, get the Machine Learning Hands-On Workshop set up and available for people to register. When you are done, review the registrants on an event that already has sign-ups and approve those you see fit, then save a copy of the participant list for your department."_

**Success Criteria:**

| Level         | Observable Indicator                                                                                                                                                                                          |
| ------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Completed** | A new event "Machine Learning Hands-On Workshop" appears in the Event List with **Published** status; AND participant navigated to registrant management, took at least one approval action, AND used Export. |
| **Partial**   | Event created but not Published (still Draft), OR published but registration configuration is incorrect; OR participant reached the registrant list but did not complete approval or export.                  |
| **Failed**    | Participant abandons the task, requests step-by-step guidance, or produces an event not identifiable as the intended workshop.                                                                                |

**Screens in path:** A1 (Event List) → A2 (Add/Edit Form) → A1 (verify) → A4 (Participants & Reviews)

## 4. Performance Metrics

> Full table: see [`metrics-summary.md`](./metrics-summary.md)

### Task Results by Participant

| Participant | Task T1 Result | Time on Task (s) | Error Count | Hesitation Count |
| ----------- | -------------- | ---------------- | ----------- | ---------------- |
| P1          | Completed      | 240              | 1           | 1                |
| P2          | Completed      | 360              | 3           | 4                |
| P3          | Completed      | 600              | 2           | 4                |
| P4          | Completed      | 300              | 1           | 2                |
| P5          | Completed      | 480              | 1           | 2                |

### Aggregate Statistics — Task T1

| Metric                     | Value             |
| -------------------------- | ----------------- |
| Success Rate (% Completed) | **100%** (5/5)    |
| Partial Rate               | 0%                |
| Failure Rate               | 0%                |
| Mean Time on Task          | **396 s** (06:36) |
| Time Range                 | 240 s – 600 s     |
| Mean Error Count           | **1.6**           |
| Mean Hesitation Count      | **2.6**           |

## 5. SUS Scores

> Full computation: see [`sus-scores-computed.md`](./sus-scores-computed.md)

| Participant | SUS Score |
| ----------- | --------- |
| P1          | 92.5      |
| P2          | 70.0      |
| P3          | 75.0      |
| P4          | 82.5      |
| P5          | 80.0      |

**Mean SUS Score: 80.0 — "Good" (Sauro & Lewis, 2012)**

| Metric           | Value     |
| ---------------- | --------- |
| Mean             | 80.0      |
| Minimum          | 70.0 (P2) |
| Maximum          | 92.5 (P1) |
| Adjective Rating | Good      |
| Grade            | B         |
| Percentile       | ~72nd     |

The mean SUS of 80.0 places the EMS above the industry average of 68, reflecting a system that first-time admin users find broadly usable. The 22.5-point spread between P1 and P2 suggests that usability is notably sensitive to the user's technical background.

## 6. Findings

Findings are ranked by criticality (Severity × Frequency). Severity scale: Nielsen (1994) 0–4. Frequency: proportion of participants affected.

> **Systemic:** observed by ≥ 3 of 5 participants (≥ 60%). **Isolated:** observed by < 3 participants.

### Finding F-01 — Six-field date/time cluster on A2 lacks contextual labels or inline help

| Field                  | Value                                                           |
| ---------------------- | --------------------------------------------------------------- |
| **Severity**           | 3 — Major                                                       |
| **Frequency**          | 5/5 participants (100%)                                         |
| **Criticality**        | 3 × 1.0 = **3.0**                                               |
| **Type**               | Systemic                                                        |
| **Heuristic violated** | H10: Help and Documentation; H6: Recognition Rather Than Recall |
| **Tasks affected**     | T1                                                              |

**Observation:** Participants hesitated when they reached the date and time section of the A2 form. The section presents six distinct date/time fields — event start date, event end date, event start time, event end time, registration open date, and registration close date — without grouping, separating lines, or inline tooltips. Participants had to pause and mentally re-read the task scenario to map the required values to the correct fields. P3 hesitated for approximately 40 seconds at this section alone, then submitted the form without completing these fields correctly, triggering two separate validation errors. P4 paused ~15 seconds and verbalized the confusion despite ultimately entering the data correctly.

**Evidence:**

> _"There are 6 different date/time fields. I'm not sure which is which."_ — P3, `[02:30]`, A2

> _"So many dates... let me check what the instructions say."_ — P4, `[01:20]`, A2

> _"I would strongly suggest adding more visual guidance for the date and time fields. There were too many time-related inputs."_ — P3, Probe Q5

> _"The date section is a bit heavy. Grouping those six fields better or just adding a small hover-tooltip would save new users from second-guessing themselves."_ — P4, Probe Q5

**Recommendation:** Group the six date/time fields into two clearly labelled sub-sections: **"Event Schedule"** (start date/time, end date/time) and **"Registration Window"** (open date, close date). Add a small info icon (ℹ) with a hover tooltip to each sub-section heading explaining what the dates control (e.g., "The dates during which the event takes place" vs. "The period in which users may register"). This reduces the reliance on external context and eliminates the primary hesitation point across all sessions.

### Finding F-02 — No success feedback after publishing (missing success toast)

| Field                  | Value                           |
| ---------------------- | ------------------------------- |
| **Severity**           | 2 — Minor                       |
| **Frequency**          | 5/5 participants (100%)         |
| **Criticality**        | 2 × 1.0 = **2.0**               |
| **Type**               | Systemic                        |
| **Heuristic violated** | H1: Visibility of System Status |
| **Tasks affected**     | T1                              |

**Observation:** After clicking Publish on A2, no confirmation toast, banner, or success indicator appears to acknowledge that the event has been published. All five participants had to navigate back to the Event List (A1) to visually confirm that the new event appeared with "Published" status. Three participants (P3, P4, P5) explicitly verbalized uncertainty about whether the publication had succeeded immediately after clicking Publish.

**Evidence:**

> _"Why wasn't there a success message? But I see it in the list and the status says Published, so I hope it worked."_ — P5, `[06:20]`, A1

> _"I wasn't entirely sure immediately because nothing popped up to say it was successful."_ — P3, Probe Q3

> _"At first, I didn't see a success notification, so I double-checked the main dashboard."_ — P4, Probe Q3

> _"Before that, Draft and Publish were easy to mix up."_ — P2, Probe Q3

**Recommendation:** Display a brief, non-blocking success toast notification (e.g., 3 seconds duration) in the top-right corner immediately after a successful Publish action, reading: _"Event published successfully. View it in the Event List."_ The toast should include a hyperlink to the newly published event on A1. This directly addresses the H1 violation and eliminates the mandatory return trip to A1 to confirm state.

### Finding F-03 — Required field asterisks are low-contrast and visually indistinguishable

| Field                  | Value                                               |
| ---------------------- | --------------------------------------------------- |
| **Severity**           | 2 — Minor                                           |
| **Frequency**          | 3/5 participants (60%)                              |
| **Criticality**        | 2 × 0.6 = **1.2**                                   |
| **Type**               | Systemic                                            |
| **Heuristic violated** | H5: Error Prevention; H4: Consistency and Standards |
| **Tasks affected**     | T1                                                  |

**Observation:** The required field marker (`*`) on A2 uses a black or near-black color, making it visually indistinguishable from surrounding label text. This caused P1, P4, and P5 to overlook the required "Campus" field and attempt to submit the form without it, triggering a validation error. All three participants independently identified the low-contrast asterisk as the root cause after the error occurred, citing that a red asterisk would have prevented the mistake.

**Evidence:**

> _"Oh, I missed the Campus field. The asterisk is black so it didn't stand out."_ — P1, `[01:42]`, A2

> _"Ah, there's an error. I missed the Campus field. The asterisk isn't very prominent."_ — P5, `[06:00]`, A2

> _"I was a bit unhappy because the required fields in the form don't have a distinct or highlighted asterisk color."_ — P5, Probe Q2

> _"I omitted the 'Campus' field initially because the mandatory fields were not sufficiently highlighted (the asterisk was black, not red)."_ — P1, Probe Q2

**Recommendation:** Change the required field asterisk (`*`) color from its current near-black to a high-contrast **red** (e.g., `#D32F2F`, WCAG AA compliant against white backgrounds). This is the universally established web convention for required fields (consistent with standard HTML form patterns), and the change requires a single global CSS rule update.

### Finding F-04 — Participant limit fields are hidden behind a role-toggle mechanism

| Field                  | Value                                                            |
| ---------------------- | ---------------------------------------------------------------- |
| **Severity**           | 2 — Minor                                                        |
| **Frequency**          | 2/5 participants (40%)                                           |
| **Criticality**        | 2 × 0.4 = **0.8**                                                |
| **Type**               | Isolated                                                         |
| **Heuristic violated** | H6: Recognition Rather Than Recall; H3: User Control and Freedom |
| **Tasks affected**     | T1                                                               |

**Observation:** The numeric input fields for student and lecturer slot counts are conditionally hidden and only appear after the user enables the corresponding role toggle (e.g., "Allow student registration"). P3 hesitated (~25 seconds) and P5 hesitated (~15 seconds) when they could not immediately locate where to enter the participation limits, despite the task scenario explicitly specifying "30 students and 10 lecturers." Both participants eventually discovered the toggle mechanism and proceeded, but the hidden nature of the fields required trial-and-error exploration.

**Evidence:**

> _"Hmm, 20 students, 30 lecturers... but I don't see where to input the quantities."_ — P5, `[04:45]`, A2

> _"So I need to allocate roles here..."_ — P3, `[04:10]`, A2

**Recommendation:** Display the slot count input fields in a persistently visible but disabled state when the role toggle is off, and activate them when the toggle is turned on. This "grayed-out but visible" pattern (cf. Nielsen H6) allows users to understand the structure of the form before making toggle decisions, eliminating the "fields appear from nowhere" confusion.

### Finding F-05 — Pending registrant sub-tab is not immediately obvious on A4

| Field                  | Value                                                               |
| ---------------------- | ------------------------------------------------------------------- |
| **Severity**           | 1 — Cosmetic                                                        |
| **Frequency**          | 3/5 participants (60%)                                              |
| **Criticality**        | 1 × 0.6 = **0.6**                                                   |
| **Type**               | Systemic                                                            |
| **Heuristic violated** | H1: Visibility of System Status; H6: Recognition Rather Than Recall |
| **Tasks affected**     | T1                                                                  |

**Observation:** When participants first arrived at A4 (Participants & Reviews), they landed on a default tab that did not immediately show pending registrants. P2, P3, and P4 each paused briefly (6–10 seconds) before noticing the red notification dot on the "Review Students" tab and switching to it. Once they noticed the dot, recovery was immediate. The red dot is an effective but small secondary cue; the primary landing state of A4 does not proactively surface pending actions.

**Evidence:**

> _"Where are the pending users?"_ — P2, `[04:43]`, A4

> _"Where are the requests?"_ — P3, `[08:40]`, A4

> _"Where are they?"_ — P4, `[03:40]`, A4

> _"Ah, the red dot means they are waiting."_ — P4, `[03:50]`, A4

**Recommendation:** If there are pending registrants awaiting review, default the landing tab on A4 to the review tab with pending items (or the tab with the highest pending count). Alternatively, display a dismissible inline banner at the top of A4: _"3 registrations are awaiting your review."_ with a link to the relevant tab. This surfaces the pending action state proactively and reduces reliance on the participant noticing the small notification dot.

### Finding F-06 — Image upload ratio requirement not communicated upfront on A2

| Field                  | Value                                                    |
| ---------------------- | -------------------------------------------------------- |
| **Severity**           | 1 — Cosmetic                                             |
| **Frequency**          | 2/5 participants (40%)                                   |
| **Criticality**        | 1 × 0.4 = **0.4**                                        |
| **Type**               | Isolated                                                 |
| **Heuristic violated** | H5: Error Prevention; H6: Recognition Rather Than Recall |
| **Tasks affected**     | T1                                                       |

**Observation:** The image upload area on A2 requires specific aspect ratios (4:3 for thumbnail, 24:9 for banner). P2 uploaded an incorrectly sized image and received a ratio rejection error. P2 additionally verbalized uncertainty about image size requirements when they first reached the field. Neither participant was shown the ratio requirements as instructional text before the upload attempt; the constraint only became apparent after a rejection.

**Evidence:**

> _"Do they need a specific image size here?"_ — P2, `[00:36]`, A2 (verbalised uncertainty before uploading)

> _"Ah, the image must be exactly 4:3."_ — P2, `[01:05]`, A2 (after rejection)

> _"I would make the image requirement and the publish step clearer."_ — P2, Probe Q5

**Recommendation:** Display the required aspect ratio (and optional max file size) as static placeholder text or a helper label directly inside or beneath each image upload zone before any upload is attempted (e.g., "Thumbnail: 4:3 ratio recommended. Min 400×300 px."). This is a standard pattern on upload-heavy forms and removes the need for participants to discover requirements through error-driven feedback.

### Finding F-07 — Row click targets on A1 are too small (icon-only navigation)

| Field                  | Value                                                         |
| ---------------------- | ------------------------------------------------------------- |
| **Severity**           | 1 — Cosmetic                                                  |
| **Frequency**          | 1/5 participants (20%)                                        |
| **Criticality**        | 1 × 0.2 = **0.2**                                             |
| **Type**               | Isolated                                                      |
| **Heuristic violated** | H4: Consistency and Standards; Fitts's Law (motor efficiency) |
| **Tasks affected**     | T1                                                            |

**Observation:** P1 explicitly noted that navigating to an event's details on A1 required targeting a small "eye" icon rather than being able to click anywhere on the event row. While P1 successfully navigated using this mechanism without failing, they verbalized frustration and recommended making the entire table row a clickable target. No other participant mentioned this finding explicitly, though it may reflect their differing navigation habits.

**Evidence:**

> _"Why can't I click the entire row? I have to target this tiny eye icon."_ — P1, `[02:18]`, A1

> _"Instead of requiring users to target and click a very small eye icon to view event details, the system should allow clicking anywhere on the event row."_ — P1, Probe Q5

**Recommendation:** Make the entire event table row in A1 a clickable link to that event's detail/management page, following the standard convention for data table row navigation. Retain the explicit eye icon as a secondary visual affordance. This is a minor CSS/JS change with high perceived quality impact.

## 7. Prioritised Recommendations

| Priority | Recommendation                                                           | Finding(s) | Estimated Impact                                                             |
| -------- | ------------------------------------------------------------------------ | ---------- | ---------------------------------------------------------------------------- |
| 1        | Group date/time fields and add hover-tooltips on A2                      | F-01       | High — affects 5/5 participants; eliminates primary hesitation cluster       |
| 2        | Display success toast notification after Publish action                  | F-02       | High — affects 5/5 participants; closes critical feedback gap (H1)           |
| 3        | Change required field asterisk (`*`) to red                              | F-03       | Medium — affects 3/5 participants; 1-line CSS fix, prevents avoidable errors |
| 4        | Show participant limit fields in disabled state before role toggle is on | F-04       | Medium — affects 2/5 participants; improves form transparency                |
| 5        | Default A4 landing to the review tab with pending items (or add banner)  | F-05       | Low-Medium — affects 3/5 participants; cosmetic but improves discoverability |
| 6        | Display image ratio requirements as inline helper text before upload     | F-06       | Low — affects 2/5 participants; prevents upload rejection loop               |
| 7        | Make entire A1 table row clickable (not icon-only)                       | F-07       | Low — affects 1/5 participants; quality-of-life improvement                  |

## 8. Limitations

- **Sample size (N = 5):** Findings indicate consistent patterns across participants but cannot be treated as statistically significant. The 5-participant approach (Nielsen 2000) is appropriate for identifying major and moderate usability issues; rarer edge-case problems may not surface.
- **Recording:** All five participants declined screen and audio recording consent. Data relies on real-time manual notes, which may have introduced minor timing imprecision in timestamps and verbalisation capture.
- **Single task:** Only one task (T1) was tested, covering the A1 → A2 → A4 flow. Other admin workflows (event editing, deletion, role management) were not assessed and may present additional usability issues.
- **Participant profile skew:** Three of five participants have IT or near-IT backgrounds, potentially inflating the mean SUS score and mean task speed relative to a general university staff population.
- **Language:** One participant (P5) switched the UI to Vietnamese to improve comprehension. Results from P5 reflect a partially localised experience and may not directly compare to the English-language experience of P1–P4.

## Appendix A — Raw Session Notes

| Participant | Session File                                     |
| ----------- | ------------------------------------------------ |
| P1          | [`P1-session.md`](./session-notes/P1-session.md) |
| P2          | [`P2-session.md`](./session-notes/P2-session.md) |
| P3          | [`P3-session.md`](./session-notes/P3-session.md) |
| P4          | [`P4-session.md`](./session-notes/P4-session.md) |
| P5          | [`P5-session.md`](./session-notes/P5-session.md) |

## Appendix B — Raw SUS Responses

> Full scoring computation: see [`sus-scores-computed.md`](./sus-scores-computed.md)

| P#       | Q1  | Q2  | Q3  | Q4  | Q5  | Q6  | Q7  | Q8  | Q9  | Q10 | **Score** |
| -------- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --------- |
| P1       | 4   | 1   | 5   | 1   | 4   | 1   | 5   | 2   | 5   | 1   | **92.5**  |
| P2       | 4   | 2   | 4   | 2   | 4   | 3   | 4   | 2   | 4   | 3   | **70.0**  |
| P3       | 5   | 1   | 4   | 2   | 3   | 2   | 4   | 3   | 4   | 2   | **75.0**  |
| P4       | 5   | 1   | 5   | 1   | 3   | 2   | 4   | 3   | 4   | 1   | **82.5**  |
| P5       | 4   | 2   | 5   | 2   | 3   | 2   | 4   | 1   | 4   | 1   | **80.0**  |
| **Mean** |     |     |     |     |     |     |     |     |     |     | **80.0**  |
