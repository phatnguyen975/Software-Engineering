# Session Notes — Participant P4

> **SUT:** EMS (Event Management System)  
> **Participant ID:** P4  
> **Task:** T1 — Create, Publish, and Manage Event Registrations  
> **Mode:** Moderated

## SECTION 0 — Session Metadata

| Field                  | Value              |
| ---------------------- | ------------------ |
| **Participant ID**     | P4                 |
| **Participant Name**   | Nguyễn Hồ Anh Quốc |
| **Session Date**       | 2026-08-02         |
| **Session Start Time** | 02:10 PM           |
| **Session End Time**   | 02:23 PM           |
| **Facilitator Name**   | Nguyễn Tấn Phát    |
| **Observer Name**      | Nguyễn Tấn Phát    |
| **Device Used**        | Laptop             |
| **OS / Browser**       | Windows / Chrome   |
| **Recording Consent**  | ☐ Yes ☑ No         |
| **Recording File**     | —                  |

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
| **Task start time**         | 02:13 PM   |
| **Task end time**           | 02:18 PM   |
| **Time on task (seconds)**  | 300 s      |
| **Did 20-min limit apply?** | ☐ Yes ☑ No |

### 2c — Task Result

| Field                | Value                          |
| -------------------- | ------------------------------ |
| **Task Result**      | ☑ Completed ☐ Partial ☐ Failed |
| **Error Count**      | 1 errors                       |
| **Hesitation Count** | 2 hesitations (pauses >5s)     |

**Facilitator's result reasoning:**

```
Participant navigated the system smoothly and efficiently, achieving a high SUS score. They encountered mild friction mapping the date/time fields and experienced one validation error, but recovered quickly.
```

### 2d — Error Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen | What Happened                                                                        | Recovery                                                                                          |
| --- | ------------------- | ------ | ------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------- |
| 1   | `[02:20]`           | A2     | Attempted to publish without filling out all required fields, triggering validation. | Auto-scrolled to the error; participant corrected the missing field and successfully resubmitted. |

### 2e — Hesitation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| #   | Timestamp `[MM:SS]` | Screen / Section | Duration | Participant then did...                                                                                       |
| --- | ------------------- | ---------------- | -------- | ------------------------------------------------------------------------------------------------------------- |
| 1   | `[01:20]`           | A2               | ~15 s    | Paused at the Date & Time section to carefully interpret which of the 6 fields corresponded to the task data. |
| 2   | `[03:40]`           | A4               | ~10 s    | Paused on the default tab before noticing the red notification dot for pending registrations.                 |

### 2f — Observation Log

> **Screen codes:** A1 (Event List) · A2 (Add / Edit Form) · A4 (Participants & Reviews)

| Timestamp `[MM:SS]` | Screen | Behaviour                                                 | Verbalisation                                              | Observer Note                                                |
| ------------------- | ------ | --------------------------------------------------------- | ---------------------------------------------------------- | ------------------------------------------------------------ |
| `[00:00]`           | A1     | Session starts — participant reads task card              | —                                                          | Starting screen confirmed: Event List visible.               |
| `[00:10]`           | A1     | Quickly locates and clicks the "Add Event" button         | "Add event is right here."                                 | Navigation was immediate.                                    |
| `[00:20]`           | A2     | Clicks the upload area and uploads the cover image        | "Selecting the image first."                               | Image upload mechanism was intuitive.                        |
| `[00:45]`           | A2     | Fills out Title, Description, and Participant limit       | "Typing in the basics..."                                  | Confident progression through text inputs.                   |
| `[01:20]`           | A2     | Reaches the Date & Time section and pauses                | "So many dates... let me check what the instructions say." | Mild friction due to cognitive load of multiple date fields. |
| `[01:35]`           | A2     | Maps the scenario dates to the form fields                | "Okay, this is for the event, this is for registration."   | Recovered without errors.                                    |
| `[02:20]`           | A2     | Clicks Publish but encounters a validation error          | "Oops, missed a required field."                           | The form validated correctly on submit.                      |
| `[02:40]`           | A2     | Corrects the field and successfully publishes             | "Done, publishing now."                                    | Auto-scroll functionality aided quick recovery.              |
| `[02:50]`           | A1     | Verifies the "Published" status on the event list         | "It says Published, so we are good."                       | Relied on A1 status badge as there was no success toast.     |
| `[03:10]`           | A1     | Navigates to participant management for an existing event | "Now to approve users."                                    | Found the management section easily.                         |
| `[03:40]`           | A4     | Pauses briefly while searching for the pending users      | "Where are they?"                                          | Initial confusion regarding sub-tabs.                        |
| `[03:50]`           | A4     | Notices the red dot and switches to the pending tab       | "Ah, the red dot means they are waiting."                  | Visual cue was effective.                                    |
| `[04:30]`           | A4     | Approves users and exports the participant list to Excel  | "Approving these... and exporting to Excel."               | Bulk actions and export were seamless.                       |
| `[05:00]`           | A4     | Completes the task                                        | "Task complete."                                           | Task completed at exactly 300 seconds.                       |

### 2g — Key Moments Summary

**Where did the participant first hesitate significantly?**

```
The participant paused briefly at the date/time cluster on A2, taking about 15 seconds to parse the six distinct fields before confidently entering the data.
```

**Was the Draft vs. Publish distinction clear?**

```
Yes, the distinction was clear. The participant didn't receive a publish toast, but they proactively returned to the dashboard and used the 'Published' tag as their visual confirmation.
```

**Did the participant notice image upload ratio requirements?**

```
Yes. They clicked to upload the mandatory cover image without any friction and deliberately bypassed the extra image slots, understanding they were optional.
```

**Did the participant find the participant management section?**

```
Yes. Upon entering the participant view, they briefly scanned the layout before the red notification dot drew their attention directly to the pending users tab.
```

**Did they understand the status badges on A4 (PENDING, APPROVED, etc.)?**

```
Yes, they found the color-coded badges highly conventional and self-explanatory, recognizing the standard 'traffic-light' metaphor instantly.
```

**Was the Export function found and used successfully?**

```
Yes. The export button was easily spotted and executed. They noted the resulting Excel sheet was well-formatted and displayed the necessary status columns clearly.
```

## SECTION 3 — SUS Questionnaire

> 1 = Strongly Disagree, 5 = Strongly Agree

| Q   | Statement (abbreviated)                    | Response (1–5) |
| --- | ------------------------------------------ | -------------- |
| Q1  | Would like to use this system frequently   | 5              |
| Q2  | Found the system unnecessarily complex     | 1              |
| Q3  | Thought the system was easy to use         | 5              |
| Q4  | Would need technical support               | 1              |
| Q5  | Found various functions well integrated    | 3              |
| Q6  | Too much inconsistency                     | 2              |
| Q7  | Most people would learn quickly            | 4              |
| Q8  | Found the system very cumbersome           | 3              |
| Q9  | Felt very confident using the system       | 4              |
| Q10 | Needed to learn a lot before getting going | 1              |

**Computed SUS Score:** 82.5

## SECTION 4 — Probe Questions Debrief

**Q1 — Path Clarity:**

> "When you first arrived at the events management screen, how did you figure out where to start to get the new event set up?"

**Answer:**

```
"It was pretty straightforward. As soon as the page loaded, the 'Add Event' button caught my eye on the right panel. I clicked it right away to get started."
```

**Q2 — Error Recovery:**

> "Was there any point during the form-filling part where something didn't work the way you expected, or where you had to stop and try something again?"

**Answer:**

```
"Yeah, I actually skipped one of the date boxes by accident. When I hit submit, the screen jumped straight back to the blank field with a red warning text. I really liked that feature because I didn't have to hunt for my mistake."
```

**Q3 — Trust in Outcome:**

> "After you finished setting up the event details, how confident are you that the event is actually available for people to register right now — and what made you feel that way?"

**Answer:**

```
"At first, I didn't see a success notification, so I double-checked the main dashboard. Seeing the word 'Published' next to my new event was enough to assure me it went through."
```

**Q4 — Mental Model on A4:**

> "When you were looking at the list of people who had signed up, what did the different labels or colours next to their names mean to you — and did that match what you expected?"

**Answer:**

```
"They were very standard and easy to read. The traffic-light color coding—green for accepted, yellow for waiting—is universally understood, so I knew exactly what they meant without thinking twice."
```

**Q5 — Open Improvement:**

> "If you could change one thing about the process you just went through — from setting up the event to handling the registrations — what would it be, and why?"

**Answer:**

```
"Honestly, the layout is good, but the date section is a bit heavy. Grouping those six fields better or just adding a small hover-tooltip would save new users from second-guessing themselves."
```

## SECTION 5 — Post-Session Open Comments

```
The participant completed the task efficiently and achieved a very high SUS score (82.5). The primary area for improvement remains the cognitive load of the date/time fields and the lack of a success toast after publishing. Overall, the participant found the system highly usable.
```

## SECTION 6 — Facilitator Post-Session Notes

**Top 3 friction points observed:**

1. `A2` — The density of date and time fields caused a brief cognitive pause.
2. `A2` — Missing fields triggered validation errors, though the auto-scroll recovery was highly effective.
3. `A2` — The lack of a success toast after publishing caused a momentary lack of confidence until the participant verified the status on A1.

**Overall impression:**

```
The participant is a competent user who navigated the system with ease. While the A2 form's complexity (specifically dates) caused minor hesitation, it did not significantly impact their overall satisfaction or success. The system's usability is solid, but adding tooltips and success toasts would elevate the experience further.
```

**Anything unusual to flag:**

```
Consistent with previous participants, this user explicitly requested inline tooltips (info icons) for the complex fields to avoid reading external manuals. Additionally, the lack of a confirmation toast upon publishing is a recurring friction point.
```
