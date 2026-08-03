# Session Notes — Participant P5

> **SUT:** EMS (Event Management System)  
> **Participant ID:** P5  
> **Task:** T1 — Create, Publish, and Manage Event Registrations  
> **Mode:** Moderated

## SECTION 0 — Session Metadata

| Field                  | Value             |
| ---------------------- | ----------------- |
| **Participant ID**     | P5                |
| **Participant Name**   | Nguyễn Thành Tiến |
| **Session Date**       | 2026-08-03        |
| **Session Start Time** | 11:00 AM          |
| **Session End Time**   | 11:16 AM          |
| **Facilitator Name**   | Nguyễn Tấn Phát   |
| **Observer Name**      | Nguyễn Tấn Phát   |
| **Device Used**        | Laptop            |
| **OS / Browser**       | Windows / Chrome  |
| **Recording Consent**  | ☐ Yes ☑ No        |
| **Recording File**     | —                 |

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
| **Task start time**         | 11:03 AM   |
| **Task end time**           | 11:11 AM   |
| **Time on task (seconds)**  | 480 s      |
| **Did 20-min limit apply?** | ☐ Yes ☑ No |

### 2c — Task Result

| Field                | Value                          |
| -------------------- | ------------------------------ |
| **Task Result**      | ☑ Completed ☐ Partial ☐ Failed |
| **Error Count**      | 1 errors                       |
| **Hesitation Count** | 2 hesitations (pauses >5s)     |

**Facilitator's result reasoning:**

```
The participant completed the task efficiently and achieved a high SUS score. They encountered a minor validation error due to low-contrast required field markers and hesitated briefly when searching for hidden participant limit fields, but recovered quickly and utilized bulk actions well.
```

### 2d — Error Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen | What Happened                                         | Recovery                                                               |
| --- | ------------------- | ------ | ----------------------------------------------------- | ---------------------------------------------------------------------- |
| 1   | `[05:45]`           | A2     | Clicked Publish but missed the required Campus field. | Corrected the missing field after noticing the subtle required marker. |

### 2e — Hesitation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen / Section | Duration | Participant then did...                                                                         |
| --- | ------------------- | ---------------- | -------- | ----------------------------------------------------------------------------------------------- |
| 1   | `[01:00]`           | A2               | ~20 s    | Scanned the form fields to understand the requirements before proceeding.                       |
| 2   | `[04:45]`           | A2               | ~15 s    | Looked for the participant limit fields before realizing they were hidden behind a role toggle. |

### 2f — Observation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| Timestamp `[MM:SS]` | Screen | Behaviour                                             | Verbalisation                                                                                                         | Observer Note                                         |
| ------------------- | ------ | ----------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------- |
| `[00:00]`           | A1     | Session starts — participant reads task card          | —                                                                                                                     | Starting screen confirmed: Event List visible.        |
| `[00:15]`           | A1     | Clicks language switcher and selects Vietnamese       | "I see a language toggle; I'll switch to Vietnamese for easier navigation."                                           | Preferred native language interface.                  |
| `[00:30]`           | A1     | Clicks the "Add Event" button                         | "Great, the button to create an event is right here on the list view. I'll click it."                                 | Navigation was immediate.                             |
| `[01:00]`           | A2     | Pauses to scan all form fields and descriptions       | "Wow, there is a lot of information needed. I should read through to see what is required."                           | High initial cognitive load; cautious approach.       |
| `[01:45]`           | A2     | Uploads thumbnail and banner images                   | "I will upload the necessary images for the event."                                                                   | Image upload intuitive.                               |
| `[02:30]`           | A2     | Enters event title and basic descriptions             | "Next, I'll fill in the basic information section."                                                                   | Smooth progress on standard text fields.              |
| `[03:45]`           | A2     | Enters date and time information                      | "I will continue filling in the date and time as requested."                                                          | No major friction observed mapping dates.             |
| `[04:45]`           | A2     | Pauses, unable to find the participant limit fields   | "Hmm, 20 students, 30 lecturers... but I don't see where to input the quantities."                                    | Friction: Fields hidden behind toggles.               |
| `[05:15]`           | A2     | Toggles the roles to reveal and fill the limit fields | "I'll enable student and lecturer registration. Ah, now I see the quantity fields."                                   | Recovered successfully.                               |
| `[05:45]`           | A2     | Clicks the Publish button                             | "Looks like I've filled everything. I'll hit Publish to create the event."                                            | Attempted submission.                                 |
| `[06:00]`           | A2     | Corrects missing Campus field and republishes         | "Ah, there's an error. I missed the Campus field. The asterisk isn't very prominent. I'll fill it and publish again." | Validation error due to low-contrast required marker. |
| `[06:20]`           | A1     | Returns to Event List to verify publication           | "Why wasn't there a success message? But I see it in the list and the status says Published, so I hope it worked."    | Lack of success toast caused slight uncertainty.      |
| `[06:40]`           | A1     | Clicks on the event to view details                   | "Next, I'll view the details of this event."                                                                          | Transitioning to management task.                     |
| `[07:15]`           | A4     | Switches to the Review Students tab                   | "I see a red dot on the Review Students tab. I assume there are new registrations. Let's check."                      | Visual cue (red dot) effective.                       |
| `[07:30]`           | A4     | Clicks Approve All                                    | "Good, there's an 'Approve All' option so I don't have to do it individually."                                        | Bulk action feature highly appreciated.               |
| `[07:45]`           | A4     | Switches to the Registrants tab                       | "Now I'll check the list of students who were just approved."                                                         | Validated outcome.                                    |
| `[08:00]`           | A4     | Clicks Export                                         | "Great, now I'll export this list."                                                                                   | Task completed at exactly 480 seconds.                |

### 2g — Key Moments Summary

**Where did the participant first hesitate significantly?**

```
The participant first paused upon reaching the A2 form (~20 seconds) to mentally process the large number of fields. Later, they hesitated again when trying to find the participant limit inputs, which were conditionally hidden behind the role toggles.
```

**Was the Draft vs. Publish distinction clear?**

```
Yes. However, the participant noted the lack of a success toast after publishing, leading them to rely solely on the "Published" status badge in the Event List (A1) for confirmation.
```

**Did the participant notice image upload ratio requirements?**

```
Yes. The participant seamlessly uploaded the necessary thumbnail and banner images without any issues.
```

**Did the participant find the participant management section?**

```
Yes. The red notification dot on the "Review Students" tab effectively guided them to the pending registrations. They also successfully utilized the "Approve All" button.
```

**Did they understand the status badges on A4 (PENDING, APPROVED, etc.)?**

```
Yes. The participant correctly associated the green color with the "Approved" status and found the visual mapping logical.
```

**Was the Export function found and used successfully?**

```
Yes. The participant located the Export button immediately after approving the users and downloaded the list without issue.
```

## SECTION 3 — SUS Questionnaire

> 1 = Strongly Disagree, 5 = Strongly Agree

| Q   | Statement (abbreviated)                    | Response (1–5) |
| --- | ------------------------------------------ | -------------- |
| Q1  | Would like to use this system frequently   | 4              |
| Q2  | Found the system unnecessarily complex     | 2              |
| Q3  | Thought the system was easy to use         | 5              |
| Q4  | Would need technical support               | 2              |
| Q5  | Found various functions well integrated    | 3              |
| Q6  | Too much inconsistency                     | 2              |
| Q7  | Most people would learn quickly            | 4              |
| Q8  | Found the system very cumbersome           | 1              |
| Q9  | Felt very confident using the system       | 4              |
| Q10 | Needed to learn a lot before getting going | 1              |

**Computed SUS Score:** 80.0

## SECTION 4 — Probe Questions Debrief

**Q1 — Path Clarity:**

> "When you first arrived at the events management screen, how did you figure out where to start to get the new event set up?"

**Answer:**

```
"I saw the 'Add Event' button right in front of me; it was prominently displayed on the event list table."
```

**Q2 — Error Recovery:**

> "Was there any point during the form-filling part where something didn't work the way you expected, or where you had to stop and try something again?"

**Answer:**

```
"I missed the Campus field initially. I was a bit unhappy because the required fields in the form don't have a distinct or highlighted asterisk color to make them stand out."
```

**Q3 — Trust in Outcome:**

> "After you finished setting up the event details, how confident are you that the event is actually available for people to register right now — and what made you feel that way?"

**Answer:**

```
"I only knew it was successful because the new event appeared in the list with a 'Published' status, but I wasn't completely sure right away since there was no notification."
```

**Q4 — Mental Model on A4:**

> "When you were looking at the list of people who had signed up, what did the different labels or colours next to their names mean to you — and did that match what you expected?"

**Answer:**

```
"For the students who were registered, their status showed as Approved with a green color, which makes perfect sense to me."
```

**Q5 — Open Improvement:**

> "If you could change one thing about the process you just went through — from setting up the event to handling the registrations — what would it be, and why?"

**Answer:**

```
"I would want to highlight or emphasize the required fields in the event creation form, like making the asterisk red."
```

## SECTION 5 — Post-Session Open Comments

```
The participant performed very well, achieving an 80.0 SUS score. They navigated the system efficiently and effectively utilized advanced features like language switching and bulk actions ("Approve All"). The main friction points were related to minor UI presentation details rather than core logic.
```

## SECTION 6 — Facilitator Post-Session Notes

**Top 3 friction points observed:**

1. `A2` — Missing Campus field triggered a validation error due to the low-contrast asterisk for required fields.
2. `A2` — Participant limit fields were initially difficult to find because they were hidden behind a role toggle.
3. `A1` — Lack of a success toast upon publishing caused slight uncertainty.

**Overall impression:**

```
The participant is highly competent and proactive (e.g., switching to their native language immediately). They handled the task smoothly in 8 minutes. The feedback regarding the required field asterisks is highly actionable and represents a quick usability win.
```

**Anything unusual to flag:**

```
The participant specifically highlighted the need to make the red asterisk (*) for required fields more prominent (higher contrast) to prevent easily avoidable validation errors.
```
