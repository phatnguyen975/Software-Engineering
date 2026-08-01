# Task Scenario T1 — EMS (Event Management System)

> **Task ID:** T1  
> **Scenario:** A — Admin creates and manages events  
> **Screens expected in path:** A1 (Event List) → A2 (Add / Edit Event Form) → A1 (verify) → A4 (Participants & Reviews Approval)  
> **Test type:** Assessment  
> **Benchmark time:** 12 minutes

## Context

You are a faculty administrator at the Faculty of Information Technology. Your department head has just confirmed that a **Machine Learning Hands-On Workshop** will be held on **September 20, 2026**, from **8:00 AM to 12:00 PM**. The event is open to both students and lecturers, with a limit of **30 students** and **10 lecturers**. Registration closes on **September 13, 2026**.

Your head has asked you to get the event listed on the faculty's event management system as soon as possible — with a clear title, a short description of the workshop content, and a relevant cover image — so that students and lecturers can begin registering. You have been given admin access to the system and this is your first time using it.

After setting up the event, a few participants have already registered. Your head needs you to look over the list of registrants and approve those who are eligible, then export the final list for the department records.

## Task

A test account with admin access has already been logged in on the screen in front of you. Using the system, **get the Machine Learning Hands-On Workshop set up and available for people to register**. When you are done, **review the registrants on an event that already has sign-ups and approve those you see fit, then save a copy of the participant list for your department**.

## Success Criteria

| Level         | Observable Indicator                                                                                                                                                                                                                                                                      |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Completed** | (1) A new event named "Machine Learning Hands-On Workshop" appears in the event list with a **Published** status. AND (2) The participant has navigated to the registrant management section of any event, taken at least one approval or rejection action, AND used the export function. |
| **Partial**   | The event was created and saved but NOT published (still in Draft), OR published but registration configuration is missing/incorrect. OR participant reached the registrant list but did not complete approval or export.                                                                 |
| **Failed**    | Participant abandons the task, requests step-by-step guidance to proceed, or produces an event that is not identifiable as the intended workshop (wrong title, wrong status, etc.).                                                                                                       |

## Benchmark Time

Expected completion time for a competent first-time admin user is **12 minutes**. Sessions will be allowed up to **20 minutes** before facilitator intervention.

## Screens Expected in Path

The natural completion path for this task traverses:

1. **A1 — Event List:** Starting screen. Participant must locate the entry point to create a new event.
2. **A2 — Add/Edit Event Form:** Participant fills in all event details and publishes.
3. **A1 — Event List (return):** Participant verifies the event appears with Published status.
4. **A4 — Participants & Reviews Approval:** Participant navigates to registrant management of an existing event, reviews registrants across relevant tabs (Registrants, Review Lecturers, Review Students), approves/rejects, and exports.

## Self-Check (completed before finalising)

- [x] **Goal-based** — No UI element names mentioned in the task text
- [x] **Realistic context** — The scenario gives a plausible department head directive with specific details that a university faculty administrator would recognise
- [x] **Success criterion is specific and observable** — The facilitator can verify Published status on A1, observe the approval and export action on A4
- [x] **No embedded answer** — The task does not reveal whether to use draft or publish; the slot configuration details are in the context not as UI instructions
- [x] **Natural path covers all listed screens** — Completing this task requires visiting A1 (to start and verify), A2 (to create), and A4 (to approve and export)

## Data Provided to Participant

The following information is given to the participant as part of the context above:

| Field                   | Value                              |
| ----------------------- | ---------------------------------- |
| Event name              | Machine Learning Hands-On Workshop |
| Date                    | September 20, 2026                 |
| Time                    | 8:00 AM – 12:00 PM                 |
| Student slots           | 30                                 |
| Lecturer slots          | 10                                 |
| Registration close date | September 13, 2026                 |
