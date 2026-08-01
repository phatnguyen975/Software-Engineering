# Probe Questions — Task T1 (EMS Admin Event Lifecycle)

> **Task:** T1 — Create, publish, and manage event registrations  
> **Screens covered:** A1 · A2 · A4  
> **When to ask:** After the participant completes the SUS questionnaire, in a conversational debrief.  
> **Format:** Open-ended, non-leading. Allow the participant to answer fully before asking the next question.  
> **Time:** ~5–7 minutes total.

## Facilitation Notes

- Ask questions in the order listed, but feel free to follow up naturally if a participant gives a rich answer.
- **Do NOT** read questions robotically — integrate them as conversation.
- If the participant answers a later question spontaneously while answering an earlier one, mark it as answered and move on.
- Do **not** name UI elements in your follow-up probes (avoid "the Publish button", "the Thumbnail field", "the Registrants tab").

## Q1 — Path Clarity (Screen A1 → A2 transition)

**Question:**

> "When you first arrived at the events management screen, how did you figure out where to start to get the new event set up?"

**What this surfaces:**

The discoverability of the "Add Event" / "Create Event" entry point from A1. Were they able to orient themselves on the event list and find the creation path without instruction? Did the layout communicate the next action clearly?

**Watch for in the answer:**

- Did they immediately spot the entry point, or did they have to scan the page?
- Did they use any visual cue (colour, position, icon) or did they find it by process of elimination?
- Any mention of confusion with the status filter tabs (`ALL` / `DRAFT` / `PUBLISHED`) at the top of the list.

## Q2 — Error Recovery (Screen A2 — Form Validation & Image Upload)

**Question:**

> "Was there any point during the form-filling part where something didn't work the way you expected, or where you had to stop and try something again?"

**What this surfaces:**

Where validation failures, image upload confusion (4:3 and 24:9 ratio requirements), Rich-Text editor friction, or date picker constraint errors occurred on A2. This also captures moments the participant may not have verbalised aloud during the task.

**Watch for in the answer:**

- Image upload ratio rejection — did they understand why it was rejected?
- Date/time validation — did the end date or registration close date constraint confuse them?
- Rich-Text editor — did they struggle to find formatting tools or understand what the field expected?
- Required field markers — did they notice asterisks or only discover missing fields on submit?

**Follow-up if needed (still non-leading):**

> "Was there anything that felt particularly tricky about getting the event details filled in?"

## Q3 — Trust in Outcome (Screen A2 → A1 — Publishing)

**Question:**

> "After you finished setting up the event details, how confident are you that the event is actually available for people to register right now — and what made you feel that way?"

**What this surfaces:**

Whether the participant understood the Draft vs. Publish distinction and whether the system's feedback (success toast, status badge change on A1) was sufficient to build confidence that the event was live. This is a high-risk friction point: users who saved as Draft but thought they published will answer differently from those who correctly published.

**Watch for in the answer:**

- Mentions a status indicator or confirmation message as the confidence signal → good feedback visibility.
- Unable to articulate what told them it was published → feedback was insufficient or missed.
- Mentions uncertainty about whether Draft was the same as Published → high-priority finding.

## Q4 — Mental Model on A4 (Participant Management Interface)

**Question:**

> "When you were looking at the list of people who had signed up, what did the different labels or colours next to their names mean to you — and did that match what you expected?"

**What this surfaces:**

Whether the status badges (PENDING, APPROVED, WAITLISTED, REJECTED) on A4 communicate their meaning intuitively. Whether the three-tab structure (Registrants, Review Lecturers, Review Students) was understood — or whether participants were confused about which tab to use for which type of registrant.

**Watch for in the answer:**

- Correct interpretation of badge semantics → labels are clear.
- Confusion about what "Waitlisted" means or what happens after approval → label or flow is not self-explanatory.
- Uncertainty about the purpose of the three tabs → tab navigation structure is confusing.

## Q5 — Open Improvement (Any Screen)

**Question:**

> "If you could change one thing about the process you just went through — from setting up the event to handling the registrations — what would it be, and why?"

**What this surfaces:**

Participant's highest-priority pain point in their own words. Often reveals issues that were not observed because the participant worked around them silently, or confirms that an observed friction point was significant enough to remember after the full task.

**Watch for in the answer:**

- Targets a specific screen or action → directly actionable.
- General comments about complexity or confusion → useful for executive summary framing.
- Positive feedback ("I would keep X") → record as a design strength to preserve.

## Post-Debrief Note for Facilitator

After all probe questions are answered:

1. Thank the participant warmly.
2. Briefly explain the purpose of the test (you can now reveal it was about evaluating admin usability).
3. Answer any questions they have about the system.
4. Confirm their contact details are correctly recorded (for TA verification) and ensure they understand the middle-4-digits masking policy.
