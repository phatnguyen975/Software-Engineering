# Probe Question Guide — Usability Test Designer

## Purpose of Probe Questions

Probe questions collect the subjective experience of the participant after each task — the "why" behind the performance data. They surface:

- Mental model mismatches (what the participant expected vs what happened)
- Trust and confidence levels after completing the task
- Friction points that were not captured in the observation log
- One concrete improvement suggestion per task

## The 5 Coverage Categories (per task)

Design 3–5 questions covering as many of these categories as possible:

| Category             | What it surfaces                                                           | Example question stem                                                   |
| -------------------- | -------------------------------------------------------------------------- | ----------------------------------------------------------------------- |
| **Path clarity**     | Whether the participant understood where to go and why                     | "Was there any point where you were unsure what to do next?"            |
| **Error recovery**   | How the participant responded to mistakes or dead ends                     | "Was there a moment where something didn't work as you expected?"       |
| **Trust in outcome** | Whether the participant is confident the task completed correctly          | "How confident are you that [goal outcome] actually happened?"          |
| **Expectation gap**  | Where the system behaved differently from what the participant anticipated | "Was there anything that surprised you about how the system responded?" |
| **Improvement**      | One concrete, open suggestion                                              | "If you could change one thing about that process, what would it be?"   |

## Rules for Valid Probe Questions

### Must be open-ended

Closed questions (yes/no) shut down elaboration. Open questions produce usable qualitative data.

| ✗ Closed                                  | ✓ Open                                                     |
| ----------------------------------------- | ---------------------------------------------------------- |
| "Did you find the publish button easily?" | "How did you decide where to go to make the event public?" |
| "Was the form confusing?"                 | "Was there any part of the form that made you hesitate?"   |

### Must not name UI elements

Naming a UI element anchors the participant to it and leads them away from their actual experience.

| ✗ Names UI element                             | ✓ No UI element                                             |
| ---------------------------------------------- | ----------------------------------------------------------- |
| "Did the Publish button do what you expected?" | "Did making the event available work the way you expected?" |
| "What did you think of the date picker?"       | "Was setting the event dates straightforward?"              |

### Must not be leading

Leading questions suggest a desired answer and bias the response.

| ✗ Leading                                             | ✓ Neutral                                                        |
| ----------------------------------------------------- | ---------------------------------------------------------------- |
| "Don't you think the layout was a bit confusing?"     | "How would you describe the layout of that page?"                |
| "Was it difficult to find the registration settings?" | "How easy or difficult was it to set up the registration rules?" |

### Must be task-specific

Generic questions produce generic answers. Each question should be anchored to something specific that happened in this task.

| ✗ Generic                           | ✓ Task-specific                                                                                 |
| ----------------------------------- | ----------------------------------------------------------------------------------------------- |
| "What did you think of the system?" | ← save this for post-session, not per-task                                                      |
| "Was anything confusing?"           | "Was there a point in setting up the event where you weren't sure your changes had been saved?" |

## Worked Examples by Task Type

**For a task involving form completion + publishing:**

1. "When you finished filling in the event details, how did you know it was ready to make public?"
2. "Was there any part of the form where you weren't sure what information to enter?"
3. "After you made the event available, how confident are you that people can see and register for it right now?"
4. "Was there anything the system did during that process that surprised you?"
5. "If you could change one thing about how you set up that event, what would it be?"

**For a task involving reviewing and approving submissions:**

1. "How did you decide which submissions to approve or reject?"
2. "Was there a moment where you weren't sure what would happen after you made a decision?"
3. "How confident are you that the people you approved will receive a notification?"
4. "Was there any information you wished you had available when reviewing the submissions?"
5. "What, if anything, would you change about how this approval process works?"

**For a task involving real-time operations (check-in):**

1. "When you scanned the first code, how did you know whether the check-in was successful?"
2. "Was there any check-in result that confused you or that you weren't sure how to handle?"
3. "How confident are you in the accuracy of the check-in log you just created?"
4. "Was the pace of the check-in process comfortable, or did anything feel rushed or slow?"
5. "If you had to check in 200 people using this system, what would you change first?"

## Anti-Patterns to Avoid

| Anti-Pattern                                               | Why it fails                                                                              |
| ---------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| "What did you think overall?" as a per-task question       | Too broad; produces vague answers. Reserve for post-session only.                         |
| Asking about specific features ("the toggle switch")       | Anchors the answer; participant may not have noticed or used it.                          |
| More than 5 probe questions per task                       | Causes fatigue; diminishing returns after question 5.                                     |
| Asking the same question after every task                  | Participants give increasingly shorter, less thoughtful answers. Vary the focus.          |
| Probing a friction point the participant did not encounter | May plant a problem that did not exist in their experience. Only probe what was observed. |
