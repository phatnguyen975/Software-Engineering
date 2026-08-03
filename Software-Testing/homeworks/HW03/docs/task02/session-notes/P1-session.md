# Session Notes — Participant P1

> **SUT:** EMS (Event Management System)  
> **Participant ID:** P1  
> **Task:** T1 — Create, Publish, and Manage Event Registrations  
> **Mode:** Moderated

## SECTION 0 — Session Metadata

| Field                  | Value            |
| ---------------------- | ---------------- |
| **Participant ID**     | P1               |
| **Participant Name**   | Nguyễn Gia Huy   |
| **Session Date**       | 2026-08-02       |
| **Session Start Time** | 09:00 AM         |
| **Session End Time**   | 09:12 AM         |
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
| **Task start time**         | 09:03 AM   |
| **Task end time**           | 09:07 AM   |
| **Time on task (seconds)**  | 240 s      |
| **Did 20-min limit apply?** | ☐ Yes ☑ No |

### 2c — Task Result

| Field                | Value                          |
| -------------------- | ------------------------------ |
| **Task Result**      | ☑ Completed ☐ Partial ☐ Failed |
| **Error Count**      | 1 error                        |
| **Hesitation Count** | 1 hesitation (pauses >5s)      |

**Facilitator's result reasoning:**

```
The participant completed the task smoothly within the benchmark time. Being from an IT background, they navigated the system with high confidence. There was one minor validation error due to a missed required field, and a brief hesitation on an optional field, but they recovered independently without facilitator intervention.
```

### 2d — Error Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen | What Happened                                                                                              | Recovery                                                                                                                                       |
| --- | ------------------- | ------ | ---------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | `[01:42]`           | A2     | The participant omitted the required "Campus" field and attempted to submit the form to publish the event. | The system displayed a validation error at the "Campus" field; the participant promptly selected a campus option and successfully resubmitted. |

### 2e — Hesitation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen / Section | Duration | Participant then did...                                                                                                                                                         |
| --- | ------------------- | ---------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | `[01:31]`           | A2               | ~8 s     | The participant paused at the "Album Link" field under Additional Options. They were unsure of its purpose, but correctly deduced it was optional, left it blank, and moved on. |

### 2f — Observation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| Timestamp `[MM:SS]` | Screen | Behaviour                                                      | Verbalisation                                                                  | Observer Note                                                       |
| ------------------- | ------ | -------------------------------------------------------------- | ------------------------------------------------------------------------------ | ------------------------------------------------------------------- |
| `[00:00]`           | A1     | Session starts — participant reads task card                   | —                                                                              | Starting screen confirmed: Event List visible.                      |
| `[00:11]`           | A1     | Quickly locates and clicks "Create Event" button               | "Alright, let's create a new event right away."                                | Very fast initial scan and navigation.                              |
| `[00:17]`           | A2     | Reviews form layout, begins uploading Thumbnail and Banner     | "Let me get the cover images uploaded first."                                  | Spent about 30 seconds finding and uploading the correct images.    |
| `[00:49]`           | A2     | Fills out main details efficiently                             | "30 students, 10 lecturers..."                                                 | Typed quickly and used mouse to navigate sections.                  |
| `[01:31]`           | A2     | Scrolls down to Additional Options, pauses at "Album Link"     | "Album Link? Is this for post-event photos? I will just skip it."              | Hesitated briefly but logically bypassed the non-required field.    |
| `[01:42]`           | A2     | Clicks Submit and encounters validation error                  | "Oh, I missed the Campus field. The asterisk is black so it didn't stand out." | UI feedback was clear enough for immediate self-correction.         |
| `[01:58]`           | A2     | Selects Campus and clicks Submit again, successfully publishes | "Okay, saved and published."                                                   | Did not struggle with the Publish toggle mechanism.                 |
| `[02:18]`           | A1     | Navigates to participant list of an existing event             | "Why can't I click the entire row? I have to target this tiny eye icon."       | Identified a valid UX friction point regarding click targets on A1. |
| `[03:03]`           | A4     | Clicks "Approve All" after filtering users                     | "Glad there is an 'Approve All' button, saves me from clicking one by one."    | Efficiently utilized bulk actions.                                  |
| `[04:00]`           | A4     | Exports list and completes task                                | "Now exporting to CSV and view it. Done!"                                      | Task completed successfully at exactly 240 seconds.                 |

### 2g — Key Moments Summary

**Where did the participant first hesitate significantly?**

```
At the "Album Link" field in the Additional Options section (A2). The participant was slightly confused about its specific purpose but correctly assumed they could skip it since it was not marked as mandatory.
```

**Was the Draft vs. Publish distinction clear?**

```
Yes, the participant noticed the publish toggle immediately and had no issues activating it before submitting the form.
```

**Did the participant notice image upload ratio requirements?**

```
Yes, they prepared and uploaded an appropriately sized image without requiring any prompts.
```

**Did the participant find the participant management section?**

```
Yes, they found the icon to view participants quickly. However, they explicitly noted that the click target was too small and suggested making the entire table row clickable.
```

**Did they understand the status badges on A4 (PENDING, APPROVED, etc.)?**

```
Yes, the participant understood the badges perfectly and filtered by "Pending" immediately to isolate the users needing approval.
```

**Was the Export function found and used successfully?**

```
Yes, the export function was found immediately and used without issue.
```

## SECTION 3 — SUS Questionnaire

> 1 = Strongly Disagree, 5 = Strongly Agree

| Q   | Statement (abbreviated)                    | Response (1–5) |
| --- | ------------------------------------------ | -------------- |
| Q1  | Would like to use this system frequently   | 4              |
| Q2  | Found the system unnecessarily complex     | 1              |
| Q3  | Thought the system was easy to use         | 5              |
| Q4  | Would need technical support               | 1              |
| Q5  | Found various functions well integrated    | 4              |
| Q6  | Too much inconsistency                     | 1              |
| Q7  | Most people would learn quickly            | 5              |
| Q8  | Found the system very cumbersome           | 2              |
| Q9  | Felt very confident using the system       | 5              |
| Q10 | Needed to learn a lot before getting going | 1              |

**Computed SUS Score:** 92.5

## SECTION 4 — Probe Questions Debrief

**Q1 — Path Clarity:**

> "When you first arrived at the events management screen, how did you figure out where to start to get the new event set up?"

**Answer:**

```
"The interface is straightforward. The 'Create Event' button is prominently located at the top corner, making it highly visible and easy to access."
```

**Q2 — Error Recovery:**

> "Was there any point during the form-filling part where something didn't work the way you expected, or where you had to stop and try something again?"

**Answer:**

```
"I omitted the 'Campus' field initially because the mandatory fields were not sufficiently highlighted (the asterisk was black, not red). I also experienced minor confusion regarding the 'Album Link' field under Additional Options—I was unsure whether it required a thumbnail, a banner, or another link—but since it was not marked as mandatory, I left it blank."
```

**Q3 — Trust in Outcome:**

> "After you finished setting up the event details, how confident are you that the event is actually available for people to register right now — and what made you feel that way?"

**Answer:**

```
"I am very confident because I observed that the newly created event's status on the event list page had updated to 'Published' and featured a green background indicator."
```

**Q4 — Mental Model on A4:**

> "When you were looking at the list of people who had signed up, what did the different labels or colours next to their names mean to you — and did that match what you expected?"

**Answer:**

```
"It was highly intuitive. Within the registration list, the status column clearly displays 'Pending' in yellow and 'Approved' in green, which matches standard conventions perfectly."
```

**Q5 — Open Improvement:**

> "If you could change one thing about the process you just went through — from setting up the event to handling the registrations — what would it be, and why?"

**Answer:**

```
"On the event list screen (A1), instead of requiring users to target and click a very small eye icon to view event details, the system should allow clicking anywhere on the event row. This would make navigation significantly faster and more user-friendly."
```

## SECTION 5 — Post-Session Open Comments

```
The participant has an IT background, which allowed them to navigate the system extremely fast and comfortably using a natural mix of mouse and keyboard. Their feedback was highly practical, focusing heavily on everyday usability and quality-of-life improvements (such as click targets and mandatory field indicators) rather than overly technical aspects.
```

## SECTION 6 — Facilitator Post-Session Notes

**Top 3 friction points observed:**

1. `A1` — Small click targets: The system forces users to click a tiny eye icon to view details instead of allowing the entire row to be clickable.
2. `A2` — Unclear mandatory fields: The asterisk (`*`) indicating required fields is black, causing the participant to miss the Campus field.
3. `A2` — Ambiguous optional fields: Hesitation occurred at the "Album Link" field due to an unclear purpose.

**Overall impression:**

```
The system is highly intuitive. The participant finished the core tasks flawlessly within the benchmark time. The few suggestions made (allowing entire row clicks, coloring the required asterisk red, clarifying the Album Link) are solid quality-of-life improvements that would reduce cognitive load.
```

**Anything unusual to flag:**

```
None.
```
