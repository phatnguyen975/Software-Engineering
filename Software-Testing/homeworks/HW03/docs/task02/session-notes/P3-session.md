# Session Notes — Participant P3

> **SUT:** EMS (Event Management System)  
> **Participant ID:** P3  
> **Task:** T1 — Create, Publish, and Manage Event Registrations  
> **Mode:** Moderated

## SECTION 0 — Session Metadata

| Field                  | Value            |
| ---------------------- | ---------------- |
| **Participant ID**     | P3               |
| **Participant Name**   | Thái Minh Huy    |
| **Session Date**       | 2026-08-02       |
| **Session Start Time** | 10:30 AM         |
| **Session End Time**   | 10:50 AM         |
| **Facilitator Name**   | Nguyễn Tấn Phát  |
| **Observer Name**      | Nguyễn Tấn Phát  |
| **Device Used**        | Laptop           |
| **OS / Browser**       | Windows / Chrome |
| **Recording Consent**  | ☐ Yes ☑ No       |
| **Recording File**     | —                |

## SECTION 1 — Pre-Task Briefing Checklist

- [x] Participant welcomed and introduced to the session purpose
- [x] Neutrality framing delivered: _"We are testing the system, not you."_
- [x] Think-aloud protocol explained
- [x] Think-aloud warm-up completed
- [x] Intervention policy explained
- [x] Starting screen confirmed: A1 — Event List visible; admin logged in

## SECTION 2 — Task T1: Create, Publish, and Manage Event Registrations

### 2a — Task Text Presented to Participant

> **Benchmark time:** 12 minutes  
> **Maximum allowed:** 20 minutes

_"You are a faculty administrator at the Faculty of Information Technology. Your department head has just confirmed that a **Machine Learning Hands-On Workshop** will be held on **September 20, 2026**, from **8:00 AM to 12:00 PM**. The event is open to both students and lecturers, with a limit of **30 students** and **10 lecturers**. Registration closes on **September 13, 2026**._

_Your head has asked you to get the event listed on the faculty's event management system as soon as possible — with a clear title, a short description of the workshop content, and a relevant cover image — so that students and lecturers can begin registering. You have been given admin access to the system and this is your first time using it._

_After setting up the event, a few participants have already registered. Your head needs you to look over the list of registrants and approve those who are eligible, then export the final list for the department records._

_Using the system, get the Machine Learning Hands-On Workshop set up and available for people to register. When you are done, review the registrants on an event that already has sign-ups and approve those you see fit, then save a copy of the participant list for your department."_

### 2b — Timing

| Field                       | Value      |
| --------------------------- | ---------- |
| **Task start time**         | 10:33 AM   |
| **Task end time**           | 10:43 AM   |
| **Time on task (seconds)**  | 600 s      |
| **Did 20-min limit apply?** | ☐ Yes ☑ No |

### 2c — Task Result

| Field                | Value                          |
| -------------------- | ------------------------------ |
| **Task Result**      | ☑ Completed ☐ Partial ☐ Failed |
| **Error Count**      | 2 errors                       |
| **Hesitation Count** | 4 hesitations (pauses >5s)     |

**Facilitator's result reasoning:**

```
Participant struggled significantly with the complex form, especially the date/time fields and role configuration. They eventually completed the task after multiple validation errors and prolonged hesitations, taking significantly longer than previous participants.
```

### 2d — Error Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen | What Happened                                                                   | Recovery                                                                                                 |
| --- | ------------------- | ------ | ------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------- |
| 1   | `[05:30]`           | A2     | Attempted to publish without filling out required Date & Time fields correctly. | The system scrolled to the top and showed validation errors; participant re-read the labels to fix them. |
| 2   | `[07:45]`           | A2     | Missed the "Close Registration" date and clicked publish again.                 | System validated the field; participant corrected it immediately.                                        |

### 2e — Hesitation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen / Section | Duration | Participant then did...                                                                                       |
| --- | ------------------- | ---------------- | -------- | ------------------------------------------------------------------------------------------------------------- |
| 1   | `[00:45]`           | A2               | ~15 s    | Looked at the long form and did not know where to start.                                                      |
| 2   | `[02:30]`           | A2               | ~40 s    | Confused about the 6 different Date and Time fields. Stopped to consider how to map them to the task details. |
| 3   | `[04:10]`           | A2               | ~25 s    | Confused about assigning roles for participants (slots for students vs lecturers).                            |
| 4   | `[08:40]`           | A4               | ~10 s    | Paused at the empty registrants list, did not notice the review notification dot immediately.                 |

### 2f — Observation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| Timestamp `[MM:SS]` | Screen | Behaviour                                                   | Verbalisation                                                          | Observer Note                                                  |
| ------------------- | ------ | ----------------------------------------------------------- | ---------------------------------------------------------------------- | -------------------------------------------------------------- |
| `[00:00]`           | A1     | Session starts — participant reads task card                | —                                                                      | Starting screen confirmed: Event List visible.                 |
| `[00:30]`           | A1     | Locates and clicks "Add Event" button                       | "There is an add event button on the right."                           | Took a bit of time to locate the entry point.                  |
| `[00:45]`           | A2     | Pauses at the form, overwhelmed by the fields               | "Wow, that's a lot of fields."                                         | High initial cognitive load.                                   |
| `[01:00]`           | A2     | Uploads cover image correctly by clicking the upload area   | "Clicking to select the image file is straightforward."                | Standard file browser upload mechanism was intuitive.          |
| `[02:30]`           | A2     | Reaches the Date & Time section and pauses for a long time  | "There are 6 different date/time fields. I'm not sure which is which." | Significant friction point mapping scenario data to UI inputs. |
| `[04:10]`           | A2     | Pauses at the participant limits (Students/Lecturers)       | "So I need to allocate roles here..."                                  | Mild hesitation regarding role configuration.                  |
| `[05:30]`           | A2     | Clicks Publish and encounters major validation errors       | "Oh, the screen just jumped to the top. I guess I missed something."   | Auto-scroll to error worked effectively.                       |
| `[06:20]`           | A2     | Corrects the start and end dates                            | "Ah, this is the event date, not the registration date."               | Recovered after carefully reading the labels.                  |
| `[07:45]`           | A2     | Clicks Publish again and gets another validation error      | "Missed the close date."                                               | Another validation prompt triggered.                           |
| `[08:00]`           | A2     | Fixes the close date and successfully publishes             | "Okay, finally saved."                                                 | Persistence paid off; task completed.                          |
| `[08:15]`           | A1     | Verifies the event is on the list                           | "I see it on the list now."                                            | Confirmed success via A1.                                      |
| `[08:40]`           | A4     | Enters participant management but hesitates to find pending | "Where are the requests?"                                              | Initial confusion on A4.                                       |
| `[08:50]`           | A4     | Notices the red dot and switches to the pending tab         | "Ah, this red dot shows who registered."                               | Red dot served as a successful visual cue.                     |
| `[09:30]`           | A4     | Approves users based on status labels                       | "Green is approved, yellow is pending. Makes sense."                   | Status labels correctly interpreted.                           |
| `[09:45]`           | A4     | Exports the participant list to Excel                       | "And exporting it here."                                               | Export function easily found.                                  |
| `[10:00]`           | A4     | Completes the task                                          | "Okay, I'm done."                                                      | Task completed at exactly 600 seconds.                         |

### 2g — Key Moments Summary

**Where did the participant first hesitate significantly?**

```
On A2, the participant felt overwhelmed by the numerous form fields, specifically hesitating for a long time (~40s) when encountering the 6 different date and time fields. They were unsure how to map the scenario data to these inputs.
```

**Was the Draft vs. Publish distinction clear?**

```
Yes. The participant stated that the two modes were distinct, though they noted the lack of a confirmation message after clicking 'Publish'. They had to rely on seeing the event on the list (A1) to be reassured that it was live.
```

**Did the participant notice image upload ratio requirements?**

```
Yes. They clicked the upload area to select the cover image from their file browser and found it very easy, explicitly choosing to ignore other optional image fields without confusion.
```

**Did the participant find the participant management section?**

```
Yes, but only after noticing a small red notification dot indicating new registrations. Once clicked, they found the list clear and comprehensible.
```

**Did they understand the status badges on A4 (PENDING, APPROVED, etc.)?**

```
Yes. They correctly identified the meaning behind the color-coded badges (e.g., green for approved, yellow for pending), finding this visual coding intuitive.
```

**Was the Export function found and used successfully?**

```
Yes. They successfully exported the Excel file and appreciated that the exported document clearly showed participant statuses.
```

## SECTION 3 — SUS Questionnaire

> 1 = Strongly Disagree, 5 = Strongly Agree

| Q   | Statement (abbreviated)                    | Response (1–5) |
| --- | ------------------------------------------ | -------------- |
| Q1  | Would like to use this system frequently   | 5              |
| Q2  | Found the system unnecessarily complex     | 1              |
| Q3  | Thought the system was easy to use         | 4              |
| Q4  | Would need technical support               | 2              |
| Q5  | Found various functions well integrated    | 3              |
| Q6  | Too much inconsistency                     | 2              |
| Q7  | Most people would learn quickly            | 4              |
| Q8  | Found the system very cumbersome           | 3              |
| Q9  | Felt very confident using the system       | 4              |
| Q10 | Needed to learn a lot before getting going | 2              |

**Computed SUS Score:** 75.0

## SECTION 4 — Probe Questions Debrief

**Q1 — Path Clarity:**

> "When you first arrived at the events management screen, how did you figure out where to start to get the new event set up?"

**Answer:**

```
"When I first looked at the event management screen, I saw the 'Add Event' button on the right side. It was clearly displayed, making it easy to transition to creating a new event. I clicked it, and once the form appeared, I began filling in the fields."
```

**Q2 — Error Recovery:**

> "Was there any point during the form-filling part where something didn't work the way you expected, or where you had to stop and try something again?"

**Answer:**

```
"While filling out the form, whenever I entered something incorrectly or missed a step, the page automatically scrolled up to the first erroneous field and displayed a small validation message below it. That helped me know exactly what needed to be fixed."
```

**Q3 — Trust in Outcome:**

> "After you finished setting up the event details, how confident are you that the event is actually available for people to register right now — and what made you feel that way?"

**Answer:**

```
"I wasn't entirely sure immediately because nothing popped up to say it was successful. However, I went back to the event list and saw it displayed there with a 'Published' status, which gave me confidence that it was successfully published."
```

**Q4 — Mental Model on A4:**

> "When you were looking at the list of people who had signed up, what did the different labels or colours next to their names mean to you — and did that match what you expected?"

**Answer:**

```
"The colored badges made sense. Green meant approved, yellow meant pending, and red meant rejected. This matched my expectations perfectly and was very intuitive."
```

**Q5 — Open Improvement:**

> "If you could change one thing about the process you just went through — from setting up the event to handling the registrations — what would it be, and why?"

**Answer:**

```
"I would strongly suggest adding more visual guidance for the date and time fields. There were too many time-related inputs, which confused me. Having a small info icon or tooltip next to the fields explaining what to enter would be much better than having to read a separate manual."
```

## SECTION 5 — Post-Session Open Comments

```
The participant struggled with the form's complexity, specifically the date/time inputs, but remained positive overall (reflected in a solid SUS score). They found the standard image upload and the participant review interface (A4) intuitive. The primary requests were for inline tooltips to reduce cognitive load and a clear success message after publishing.
```

## SECTION 6 — Facilitator Post-Session Notes

**Top 3 friction points observed:**

1. `A2` — Excessive number of date and time fields caused significant confusion and cognitive overload.
2. `A2` — Lack of a success toast/confirmation message after publishing caused temporary uncertainty.
3. `A4` — Required a visual cue (red dot) to notice the pending registrants tab.

**Overall impression:**

```
The participant found the core workflow intuitive but was temporarily hindered by the complexity of the A2 form and the lack of a clear success toast after publishing. Despite the lack of inline help (tooltips) for complex fields, they persevered and remained satisfied overall. Once past A2, their performance improved significantly.
```

**Anything unusual to flag:**

```
The participant strongly recommended adding inline tooltips (info icons) next to complex fields rather than relying on external documentation. This is a critical usability finding that should be prioritized.
```
