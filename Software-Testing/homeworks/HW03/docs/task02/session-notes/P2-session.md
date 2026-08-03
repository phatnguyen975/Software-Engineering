# Session Notes — Participant P2

> **SUT:** EMS (Event Management System)  
> **Participant ID:** P2  
> **Task:** T1 — Create, Publish, and Manage Event Registrations  
> **Mode:** Moderated

## SECTION 0 — Session Metadata

| Field                  | Value            |
| ---------------------- | ---------------- |
| **Participant ID**     | P2               |
| **Participant Name**   | Lương Linh Khôi  |
| **Session Date**       | 2026-08-02       |
| **Session Start Time** | 09:15 AM         |
| **Session End Time**   | 09:29 AM         |
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
| **Task start time**         | 09:18 AM   |
| **Task end time**           | 09:24 AM   |
| **Time on task (seconds)**  | 360 s      |
| **Did 20-min limit apply?** | ☐ Yes ☑ No |

### 2c — Task Result

| Field                | Value                          |
| -------------------- | ------------------------------ |
| **Task Result**      | ☑ Completed ☐ Partial ☐ Failed |
| **Error Count**      | 3 errors                       |
| **Hesitation Count** | 4 hesitations (pauses >5s)     |

**Facilitator's result reasoning:**

```
Participant created the workshop, returned to the event list with Published status, then navigated to participant management, approved eligible registrants, and exported the list successfully. The task was completed with minor friction around image upload constraints, required date validation, and locating the export action.
```

### 2d — Error Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen | What Happened                                                                              | Recovery                                                     |
| --- | ------------------- | ------ | ------------------------------------------------------------------------------------------ | ------------------------------------------------------------ |
| 1   | `[01:05]`           | A2     | Uploaded a square cover image and the system rejected it for the wrong ratio.              | Choose the provided 4:3 image and uploaded again.            |
| 2   | `[03:02]`           | A2     | Tried to save the form before entering the registration close date, triggering validation. | Filled in the close date and resubmitted the form.           |
| 3   | `[04:43]`           | A4     | Stayed on the first tab and could not immediately locate the pending registrants.          | Switched to the review tabs and found the eligible requests. |

### 2e — Hesitation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen / Section | Duration | Participant then did...                                                      |
| --- | ------------------- | ---------------- | -------- | ---------------------------------------------------------------------------- |
| 1   | `[00:15]`           | A1               | ~7 s     | Scanned the top area and then clicked into the create flow.                  |
| 2   | `[00:36]`           | A2               | ~8 s     | Paused at the cover image section and asked if a specific size was expected. |
| 3   | `[03:32]`           | A2               | ~6 s     | Compared save options before deciding to publish.                            |
| 4   | `[05:06]`           | A4               | ~6 s     | Paused after switching tabs to interpret the status labels before approving. |

### 2f — Observation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| Timestamp `[MM:SS]` | Screen | Behaviour                                                 | Verbalisation                                | Observer Note                                         |
| ------------------- | ------ | --------------------------------------------------------- | -------------------------------------------- | ----------------------------------------------------- |
| `[00:00]`           | A1     | Session starts — participant reads task card              | —                                            | Starting screen confirmed: Event List visible.        |
| `[00:15]`           | A1     | Scans the page layout and locates "Create Event"          | "I suppose the create button is at the top." | Used the page layout to orient quickly after a scan.  |
| `[00:36]`           | A2     | Pauses at the cover image section                         | "Do they need a specific image size here?"   | Hesitated before deciding to upload an image first.   |
| `[01:05]`           | A2     | Uploads a square image and receives a ratio rejection     | "Ah, the image must be exactly 4:3."         | Understood the validation after seeing the error.     |
| `[01:28]`           | A2     | Replaces the image with the correct 4:3 cover file        | "Okay, this one is correct."                 | Recovery was immediate once the constraint was clear. |
| `[02:14]`           | A2     | Fills in title, date, time, and slot counts               | "Let me fill out the main details first."    | Worked top-to-bottom without help.                    |
| `[03:02]`           | A2     | Tries to save before entering registration close date     | "Why can't I save this?"                     | Encountered required field validation for close date. |
| `[03:19]`           | A2     | Fills in the close date and scrolls down                  | "Ah, forgot the close date."                 | Self-corrected quickly.                               |
| `[03:32]`           | A2     | Compares save options before deciding to publish          | "Publish, not save as draft."                | Clear decision once the distinction was noticed.      |
| `[03:45]`           | A2     | Clicks Publish and successfully submits                   | "Okay, event is live."                       | Did not struggle with the Publish toggle mechanism.   |
| `[04:08]`           | A1     | Returns to the list and verifies the new event status     | "It shows Published now."                    | Confirmation on A1 increased confidence.              |
| `[04:22]`           | A1     | Navigates to participant management for an existing event | "The review section should be here."         | Navigation was found, but not instantly.              |
| `[04:43]`           | A4     | Stays on the Details tab, unable to find registrants      | "Where are the pending users?"               | Missed the sub-tabs initially.                        |
| `[05:06]`           | A4     | Switches tabs and interprets the status labels            | "Pending means waiting for review."          | Status labels were interpreted correctly.             |
| `[05:27]`           | A4     | Approves one lecturer and one student registrant          | "This is pretty clear to select."            | Good understanding of the review workflow.            |
| `[06:00]`           | A4     | Exports the participant list                              | "Done, exporting the file here."             | Export was successful. Task completed at 360 seconds. |

### 2g — Key Moments Summary

**Where did the participant first hesitate significantly?**

```
On A2, when the cover image upload was rejected for the wrong ratio. The participant stopped to ask what size was expected before trying a different file.
```

**Was the Draft vs. Publish distinction clear?**

```
It became clear only after a short pause. The participant initially looked for a save button, then chose Publish and checked A1 to confirm the event was live.
```

**Did the participant notice image upload ratio requirements?**

```
Yes, but only after the first rejection. The participant did not notice the ratio requirement up front, then adapted quickly once the error appeared.
```

**Did the participant find the participant management section?**

```
Yes. The participant found it from the existing event details, but needed a brief pause to locate the review area after entering the page.
```

**Did they understand the status badges on A4 (PENDING, APPROVED, etc.)?**

```
Mostly yes. The participant interpreted PENDING as waiting for review and APPROVED as accepted, and used the labels correctly when deciding what to do next.
```

**Was the Export function found and used successfully?**

```
Yes. The participant needed a few seconds to locate it, then exported the participant list without help.
```

## SECTION 3 — SUS Questionnaire

> 1 = Strongly Disagree, 5 = Strongly Agree

| Q   | Statement (abbreviated)                    | Response (1–5) |
| --- | ------------------------------------------ | -------------- |
| Q1  | Would like to use this system frequently   | 4              |
| Q2  | Found the system unnecessarily complex     | 2              |
| Q3  | Thought the system was easy to use         | 4              |
| Q4  | Would need technical support               | 2              |
| Q5  | Found various functions well integrated    | 4              |
| Q6  | Too much inconsistency                     | 3              |
| Q7  | Most people would learn quickly            | 4              |
| Q8  | Found the system very cumbersome           | 2              |
| Q9  | Felt very confident using the system       | 4              |
| Q10 | Needed to learn a lot before getting going | 3              |

**Computed SUS Score:** 70.0

## SECTION 4 — Probe Questions Debrief

**Q1 — Path Clarity:**

> "When you first arrived at the events management screen, how did you figure out where to start to get the new event set up?"

**Answer:**

```
"I looked at the top area first because that is usually where the add or create action lives. I noticed the tabs, but I used the page layout and went straight into creating the event."
```

**Q2 — Error Recovery:**

> "Was there any point during the form-filling part where something didn't work the way you expected, or where you had to stop and try something again?"

**Answer:**

```
"The image upload was the only part that stopped me. I tried one file, saw it was wrong, and then switched to the other file."
```

**Q3 — Trust in Outcome:**

> "After you finished setting up the event details, how confident are you that the event is actually available for people to register right now — and what made you feel that way?"

**Answer:**

```
"After I published it and came back to the list, I could see Published, so I felt sure it was live. Before that, Draft and Publish were easy to mix up."
```

**Q4 — Mental Model on A4:**

> "When you were looking at the list of people who had signed up, what did the different labels or colours next to their names mean to you — and did that match what you expected?"

**Answer:**

```
"The labels made sense to me. Pending meant waiting to be checked, and Approved meant it was already accepted."
```

**Q5 — Open Improvement:**

> "If you could change one thing about the process you just went through — from setting up the event to handling the registrations — what would it be, and why?"

**Answer:**

```
"I would make the image requirement and the publish step clearer. Those were the two places where I had to stop and think."
```

## SECTION 5 — Post-Session Open Comments

```
Participant said the workflow felt straightforward overall, but the event image constraint and the publish confirmation should be more obvious for first-time admins.
```

## SECTION 6 — Facilitator Post-Session Notes

**Top 3 friction points observed:**

1. `A2` — Image upload rejected because the first file had the wrong ratio.
2. `A2` — Registration close date had to be completed before the form could be saved.
3. `A4` — Participant needed a short pause to find the export action after switching to the review area.

**Overall impression:**

```
Competent first-time admin user with good recovery behavior. The participant completed the task without assistance and understood the main workflow after one or two validation cues.
```

**Anything unusual to flag:**

```
No unusual issues. Recording consent was declined, so only manual notes were taken.
```
