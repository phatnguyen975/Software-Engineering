# Task Scenario Rules — Usability Test Designer

## Core Rule: Tasks Come From the User Model, Not the Code

Tasks describe what users want to accomplish in the real world. They do not map 1-to-1 with application features or developer stories.

> "Tasks come from a user model, not the code." — Rubin & Chisnell (2008)

The Rubin task component model:

| Component     | Definition                         | Example                                                    |
| ------------- | ---------------------------------- | ---------------------------------------------------------- |
| **Task**      | The goal the user wants to achieve | Publish a new event so attendees can register              |
| **State**     | Starting condition at task begin   | Admin is logged in; no event exists yet                    |
| **Success**   | Observable indicator of completion | Event appears in the public listing with registration open |
| **Benchmark** | Expected time for a competent user | 8 minutes                                                  |

## Screen Coverage Rule for Multiple Tasks

When `num_tasks > 1`, distribute screens across tasks so that:

- All screens in `screens_list` are covered across the full task set
- Each task covers a coherent user sub-goal (not an arbitrary screen split)
- Tasks can be performed independently (no task depends on completing a previous task first, unless the test specifically aims to test a sequential workflow)

**Example with 3 tasks across 5 screens:**

- **Task T1:** covers A2 (event creation form) + A3 (registration config)
  → **Sub-goal:** "Create and configure a new event"
- **Task T2:** covers A4 (participants review)
  → **Sub-goal:** "Review and approve submitted registrations"
- **Task T3:** covers A5 (check-in tab)
  → **Sub-goal:** "Check in an attendee at the event"

**When `num_tasks = 1`:** The single task must naturally traverse all listed screens as part of completing one coherent goal. If this is not possible, recommend increasing `num_tasks` to the human before generating.

## Validity Rules with Examples

### Rule 1 — Goal-Based (Not Step-Based)

| ✗ Step-based (invalid)                                         | ✓ Goal-based (valid)                                                  |
| -------------------------------------------------------------- | --------------------------------------------------------------------- |
| "Click the Add Event button and fill in the Title field"       | "Set up a new event so that faculty and students can register for it" |
| "Go to the Events tab and use the Publish button"              | "Make the event available for public registration"                    |
| "Navigate to the Registration section and set max slots to 30" | "Configure the event so that no more than 30 students can register"   |

### Rule 2 — No UI Language

Words to avoid: click, button, tab, menu, dropdown, toggle, field, form, screen, page,
navigate, scroll, link, icon, checkbox, sidebar, dashboard, panel.

| ✗ Contains UI language                                     | ✓ No UI language                                              |
| ---------------------------------------------------------- | ------------------------------------------------------------- |
| "Use the Publish button to make the event live"            | "Make the event available so people can sign up"              |
| "Go to the Participants tab and approve the registrations" | "Review the people who have signed up and decide who gets in" |

### Rule 3 — No Embedded Answer

The task must not reveal the correct path, feature name, or outcome.

| ✗ Embedded answer                                           | ✓ No embedded answer                                             |
| ----------------------------------------------------------- | ---------------------------------------------------------------- |
| "Use the draft feature to save your work before publishing" | "Save your progress and come back to finish later"               |
| "Set the registration deadline using the date picker"       | "Make sure registrations close one week before the event starts" |

### Rule 4 — Specific Success Criterion

| ✗ Vague criterion    | ✓ Specific criterion                                                                |
| -------------------- | ----------------------------------------------------------------------------------- |
| "Complete the task"  | Completed: event appears in the public event listing with a Register button visible |
| "Make sure it works" | Completed: participant receives a confirmation and appears in the attendee list     |

### Rule 5 — Realistic Context

The context sentence(s) should:

- Make the goal feel necessary and authentic to the target user
- Establish a plausible situation, not a test scenario
- Use the target user's domain vocabulary (not tech/developer vocabulary)

| ✗ Artificial context                      | ✓ Realistic context                                                                                                                   |
| ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| "For this test, you will create an event" | "The Faculty of IT has confirmed a Machine Learning workshop on August 20. You need to get it up on the system before the week ends." |
| "Imagine you are an admin"                | "You manage academic events for your department and use this system to announce them to students and staff."                          |

## Worked Example: Valid Task Scenario

**Scenario description input:** "Admin creates, configures, and publishes an event with registration"

**Context:**

> The Faculty of Information Technology has just confirmed dates for an upcoming Machine Learning workshop scheduled for late August. The department head has asked you to get the event listed on the system as soon as possible so that faculty and students can begin registering. The workshop is limited to 30 students and 10 lecturers.

**Task:**

> Get the workshop set up and ready so that faculty and students can sign up for it. When you are done, show us where you would go to check who has registered.

**Success Criteria:**

| Level     | Observable indicator                                                                                             |
| --------- | ---------------------------------------------------------------------------------------------------------------- |
| Completed | Event is publicly visible with registration open; slot limits reflect the brief (30 students, 10 lecturers)      |
| Partial   | Event is saved but not yet published, OR published with incorrect slot limits                                    |
| Failed    | Participant abandons, asks for help to complete, or produces an outcome that does not resemble a published event |

**Benchmark time:** 8 minutes

**Self-check result:**

- [x] Goal-based — no button/tab names mentioned
- [x] Realistic context for an admin user profile
- [x] Success criterion is specific and observable
- [x] No embedded answer (draft vs publish decision is left to participant)
- [x] Natural completion path covers A2 (creation form) + A3 (registration config) + publish action

## Common Task Writing Mistakes and Fixes

| Mistake                                                  | Fix                                                                             |
| -------------------------------------------------------- | ------------------------------------------------------------------------------- |
| Using "you" in the third person ("The user should...")   | Write in second person: "You are..."                                            |
| Over-specifying the goal ("Set slots to exactly 30")     | Keep constraints in the context, not as instructions ("limited to 30 students") |
| Making the task too long (> 4 sentences)                 | Split into two tasks or simplify the goal                                       |
| No clear end state ("Use the system to manage an event") | Add a visible end state: "...so that a student could register for it right now" |
| Task requires data the participant doesn't have          | Provide necessary data in the context (event name, date, participant limits)    |
