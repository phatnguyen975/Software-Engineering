# Observation Template (Master Reference) — EMS Usability Test

> **Purpose:** This is the MASTER template. Do NOT fill in this file. Use it to generate per-participant session files.  
> **Test:** Assessment — Scenario A, Task T1  
> **SUT:** EMS (Event Management System)  
> **Session format:** Moderated

## SECTION 0 — Session Metadata

> **How to fill:** Complete this block at the very start of the session, before the participant begins the task.

| Field                  | Value                                    |
| ---------------------- | ---------------------------------------- |
| **Participant ID**     | P{n}                                     |
| **Participant Name**   |                                          |
| **Session Date**       | YYYY-MM-DD                               |
| **Session Start Time** | HH:MM                                    |
| **Session End Time**   | HH:MM                                    |
| **Facilitator Name**   |                                          |
| **Observer Name**      |                                          |
| **Device Used**        | Desktop / Laptop                         |
| **OS / Browser**       | e.g. Windows 11 / Chrome                 |
| **Recording Consent**  | ☐ Yes ☐ No                               |
| **Recording File**     | _(filename or link to screen recording)_ |

## SECTION 1 — Pre-Task Briefing Checklist

> **How to fill:** Check each box as you complete each step. All boxes must be checked before starting the task.

- [ ] Participant welcomed and introduced to the session purpose
- [ ] **Neutrality framing delivered:** _"We are testing the system, not you. There are no right or wrong answers."_
- [ ] **Think-aloud protocol explained:** _"Please say whatever comes to mind as you work — what you see, what you're trying to do, what confuses you."_
- [ ] **Think-aloud warm-up completed** — _(Give participant a 30-second unrelated warm-up task, e.g. "Look at this news website homepage and tell me what you're thinking.")_
- [ ] **Intervention policy explained:** _"I won't be able to help you during the task, but I will let you know when we're done."_
- [ ] **Recording consent confirmed** (check box above and note Yes/No)
- [ ] **Starting screen confirmed:** A1 — Event List (`/dashboard/admin/events`) is visible; admin account is logged in

## SECTION 2 — Task T1: Create, Publish, and Manage Event Registrations

### 2a — Task Text

> **How to use:** Read this text aloud clearly, or hand a printed card to the participant. Do not paraphrase. Allow the participant to re-read if needed. Start the stopwatch when the participant indicates they are ready to begin (or when they make their first action).

_"You are a faculty administrator at the Faculty of Information Technology. Your department head has just confirmed that a **Machine Learning Hands-On Workshop** will be held on **September 20, 2026**, from **8:00 AM to 12:00 PM**. The event is open to both students and lecturers, with a limit of **30 students** and **10 lecturers**. Registration closes on **September 13, 2026**._

_Your head has asked you to get the event listed on the faculty's event management system as soon as possible — with a clear title, a short description of the workshop content, and a relevant cover image — so that students and lecturers can begin registering. You have been given admin access to the system and this is your first time using it._

_After setting up the event, a few participants have already registered. Your head needs you to look over the list of registrants and approve those who are eligible, then export the final list for the department records._

_Using the system, get the Machine Learning Hands-On Workshop set up and available for people to register. When you are done, review the registrants on an event that already has sign-ups and approve those you see fit, then save a copy of the participant list for your department."_

### 2b — Timing

> **How to fill:** Record the exact time you say "Ready — go!" (or the participant makes their first action) and when the task ends (goal achieved, abandoned, or 20-min limit reached).

| Field                           | Value      |
| ------------------------------- | ---------- |
| **Task start time**             | HH:MM:SS   |
| **Task end time**               | HH:MM:SS   |
| **Time on task (seconds)**      | ... s      |
| **Did the 20-min limit apply?** | ☐ Yes ☐ No |

### 2c — Task Result

> **How to fill:** At the end of the task, assess the outcome against the success criteria below and tick one box. Record your judgment reasoning in the notes field.

| Field                | Value                          |
| -------------------- | ------------------------------ |
| **Task Result**      | ☐ Completed ☐ Partial ☐ Failed |
| **Error Count**      | ... errors                     |
| **Hesitation Count** | ... hesitations (pauses >5s)   |

**Success Criteria Reference:**

| Level         | Observable Indicator                                                                                                                                |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Completed** | A new event appears in the event list with Published status AND participant navigated to participant management, took approval action, and exported |
| **Partial**   | Event saved as Draft (not Published), OR approval/export was not completed                                                                          |
| **Failed**    | Participant abandoned, required step-by-step help, or produced incorrect/unrecognisable outcome                                                     |

**Facilitator's result reasoning:**

> _Write 1–2 sentences explaining why you chose this result level. E.g. "Participant published the event correctly but could not locate the participant management interface within the time limit — classified as Partial."_

```
[Write here]
```

### 2d — Error Log

> **How to fill:** Every time the participant makes an error (an incorrect action they must undo, correct, or recover from), add a row. Do NOT count deliberate exploration as an error. Examples of errors: submitting the form with missing required fields, navigating to the wrong section and backtracking, uploading an image with incorrect ratio and being rejected, entering an end date before the start date.

| #   | Timestamp `[MM:SS]` | Screen | What Happened (describe the error action) | Recovery (what participant did to fix it) |
| --- | ------------------- | ------ | ----------------------------------------- | ----------------------------------------- |
| 1   |                     |        |                                           |                                           |
| 2   |                     |        |                                           |                                           |
| 3   |                     |        |                                           |                                           |
| 4   |                     |        |                                           |                                           |
| 5   |                     |        |                                           |                                           |

### 2e — Hesitation Log

> **How to fill:** Every time the participant pauses for more than 5 seconds without interacting with the screen or speaking, add a row. Note the screen/section where the hesitation occurred — this is key data for identifying confusion points.

| #   | Timestamp `[MM:SS]` | Screen / Section | Duration | Participant then did... |
| --- | ------------------- | ---------------- | -------- | ----------------------- |
| 1   |                     |                  | ~... s   |                         |
| 2   |                     |                  | ~... s   |                         |
| 3   |                     |                  | ~... s   |                         |
| 4   |                     |                  | ~... s   |                         |
| 5   |                     |                  | ~... s   |                         |

### 2f — Observation Log (Timestamped)

> **How to fill:** This is the most important data-collection section. Record significant moments throughout the session — what the participant DID (behaviour), what they SAID (think-aloud verbalisation), and your own interpretation or note. You do not need to fill every row — focus on:
>
> - Moments of confusion or hesitation
> - Errors and error recovery
> - Moments of surprise (positive or negative)
> - Key navigations (entering A2, publishing, navigating to A4)
> - Verbalisations about what they expected vs what they see
>
> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| Timestamp `[MM:SS]` | Screen | Behaviour (what participant did)             | Verbalisation | Observer Note                       |
| ------------------- | ------ | -------------------------------------------- | ------------- | ----------------------------------- |
| [00:00]             | A1     | Session starts — participant reads task card | —             | Starting screen: Event List visible |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |
|                     |        |                                              |               |                                     |

### 2g — Key Moments Summary

> **How to fill:** After the session, fill this section as a quick reference. These are the "headline" observations that will drive your analysis.

**Where did the participant first hesitate significantly?**

```
[Write here — screen and what they were looking at]
```

**Was the Draft vs. Publish distinction clear?**

```
[Write here — did they initially save as draft or go straight to publish?]
```

**Did the participant notice image upload ratio requirements?**

```
[Write here — did they get a rejection error? Did they understand why?]
```

**Did the participant find the participant management section?**

```
[Write here — how did they navigate there? Did they struggle?]
```

**Did they understand the status badges on A4 (PENDING, APPROVED, etc.)?**

```
[Write here — describe their reaction or verbalisation]
```

**Was the Export function found and used successfully?**

```
[Write here]
```

## SECTION 3 — SUS Questionnaire

> **How to fill:** After the task ends, hand the participant the SUS questionnaire. Record their responses here for your records. See `docs/task02/sus-instrument.md` for the full questionnaire and scoring formula.

| Q   | Statement (abbreviated)                    | Participant Response (1–5) |
| --- | ------------------------------------------ | -------------------------- |
| Q1  | Would like to use this system frequently   | ...                        |
| Q2  | Found the system unnecessarily complex     | ...                        |
| Q3  | Thought the system was easy to use         | ...                        |
| Q4  | Would need technical support to use system | ...                        |
| Q5  | Found various functions well integrated    | ...                        |
| Q6  | Thought there was too much inconsistency   | ...                        |
| Q7  | Most people would learn quickly            | ...                        |
| Q8  | Found the system very cumbersome to use    | ...                        |
| Q9  | Felt very confident using the system       | ...                        |
| Q10 | Needed to learn a lot before getting going | ...                        |

**Computed SUS Score:** ... (compute using formula in `sus-instrument.md`)

## SECTION 4 — Probe Questions Debrief

> **How to fill:** After the SUS is complete, ask each probe question conversationally. Record the participant's answer as accurately as possible — exact quotes where possible. See `docs/task02/probe-questions-T1.md` for full question text and facilitation notes.

**Q1 — Path Clarity (A1 → A2):**

> "When you first arrived at the events management screen, how did you figure out where to start to get the new event set up?"

**Answer:**

```
[Write participant's response here — use direct quotes where possible]
```

**Q2 — Error Recovery (A2 — Form):**

> "Was there any point during the form-filling part where something didn't work the way you expected, or where you had to stop and try something again?"

**Answer:**

```
[Write participant's response here]
```

**Q3 — Trust in Outcome (Publish):**

> "After you finished setting up the event details, how confident are you that the event is actually available for people to register right now — and what made you feel that way?"

**Answer:**

```
[Write participant's response here]
```

**Q4 — Mental Model on A4:**

> "When you were looking at the list of people who had signed up, what did the different labels or colours next to their names mean to you — and did that match what you expected?"

**Answer:**

```
[Write participant's response here]
```

**Q5 — Open Improvement:**

> "If you could change one thing about the process you just went through — from setting up the event to handling the registrations — what would it be, and why?"

**Answer:**

```
[Write participant's response here]
```

## SECTION 5 — Post-Session Open Comments

> **How to fill:** Record any additional observations, reactions, or comments from the participant that occurred outside the structured questions — during transitions, while filling the SUS, or in casual conversation after the debrief.

```
[Write any additional observations or quotes here]
```

## SECTION 6 — Facilitator Post-Session Notes

> **How to fill:** Complete this section IMMEDIATELY after the participant leaves, while your memory is fresh. This is your interpretive layer — separate from the raw observation log.

**Top 3 friction points observed in this session:**

1. `[Screen] — [What happened — 1 sentence]`
2. `[Screen] — [What happened — 1 sentence]`
3. `[Screen] — [What happened — 1 sentence]`

**Overall impression of this participant's experience:**

```
[1–2 sentences: e.g. "Participant was highly competent but struggled significantly with image upload ratio — expressed visible frustration. Otherwise fluent with navigation."]
```

**Anything unusual about this session to flag for analysis:**

```
[e.g. "Participant had accidentally seen the EMS system before — note as potential exclusion risk." OR "Recording failed in minute 5-8 — observation notes only for that period."]
```
