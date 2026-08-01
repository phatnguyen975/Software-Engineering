# Need Human Triggers — GUI Checklist Executor

Comprehensive list of scenarios that MUST be marked `Need Human` regardless of execution mode. Apply these triggers consistently when assigning Result verdicts.

## Category 1 — Requires Complex Visual Measurement

While the AI can use DevTools to inspect basic CSS (like computed colours for contrast ratios), simulate slow networks (for loading states), and resize the viewport (for responsive layouts), some visual aspects are still too complex to verify without human eyes:

| Trigger                                             | Why Need Human                                                                               |
| --------------------------------------------------- | -------------------------------------------------------------------------------------------- |
| Complex sub-pixel alignments or jagged offsets      | Hard to verify via AI vision or bounding boxes alone; often requires human visual judgement. |
| Focus indicator contrast ratio on complex gradients | Requires manual measurement against a multi-coloured background.                             |

## Category 2 — Requires Timed or Dynamic Observation

| Trigger                                         | Why Need Human                                                                                                 |
| ----------------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| Toast auto-dismiss timing                       | Requires timing the exact duration; AI cannot reliably measure elapsed time                                    |
| Loading state visibility during fetch           | Requires observing a transient state. (Mark Need Human if unable to simulate via DevTools Network throttling). |
| Real-time update / WebSocket data refresh       | Requires waiting for external event; timing unpredictable                                                      |
| Animation and transition correctness            | Requires observing motion, which screenshots cannot capture                                                    |
| Scroll behaviour (smooth scroll, sticky header) | Requires observing while scrolling in real-time                                                                |

## Category 3 — Requires Specific Hardware or Permissions

| Trigger                            | Why Need Human                                                                  |
| ---------------------------------- | ------------------------------------------------------------------------------- |
| File download verification         | Verifying a file was downloaded to disk requires access to the local filesystem |
| Camera or microphone access        | Requires real hardware device                                                   |
| Print dialog or PDF export         | Requires OS print subsystem                                                     |
| Clipboard copy functionality       | Requires verifying clipboard content after paste                                |
| Drag-and-drop from OS file manager | Requires dragging a file from the desktop into the browser                      |

## Category 4 — Mode 2 (Screenshot) Specific Triggers

In Mode 2 (screenshot fallback), the following categories are always `Need Human` because they require dynamic interaction that a static screenshot cannot provide.

| Trigger                           | Example items                                           |
| --------------------------------- | ------------------------------------------------------- |
| Any hover state                   | Tooltip on hover, hover highlight on button             |
| Any keyboard interaction          | Tab order, keyboard shortcut, Esc to close modal        |
| Any form submission               | Validation on submit, success/error states after submit |
| Any navigation                    | Browser back button, link navigation, tab switching     |
| Any modal / dialog behaviour      | Focus trap, Esc close, backdrop click                   |
| Any toggle or switch state change | Toggle switch, checkbox, radio button interaction       |
| Any async operation state         | Loading spinner, toast notification, real-time update   |

## Category 5 — Ambiguous or Subjective Criteria

Mark `Need Human` (not `Fail`) when the criterion cannot be evaluated objectively:

| Trigger                                                                                         | Correct action                                                                       |
| ----------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| "Error message uses plain language" — AI is uncertain whether the message is plain enough       | Need Human; note the actual message text so human can judge                          |
| "Navigation is logically ordered" — tab order appears unusual but may be intentional            | Need Human; describe the observed order and flag for human review                    |
| "Empty state message is present" — page content is ambiguous (could be loaded or still loading) | Need Human if timing is uncertain; Fail only if confirmed empty area with no message |

## Quick Reference Decision Tree

```
Can I observe the criterion's outcome directly?
│
├─ YES, clearly meets criterion ───────────────────> PASS
│
├─ YES, clearly violates criterion ────────────────> FAIL
│
├─ Widget/area structurally absent on this screen ─> N/A
│
└─ NO — requires one of the following:
    - DevTools inspection ─────────────────────────> NEED HUMAN
    - Timed observation ───────────────────────────> NEED HUMAN
    - Hardware/OS access ──────────────────────────> NEED HUMAN
    - Dynamic interaction (Mode 2 only) ───────────> NEED HUMAN
    - Subjective judgment ─────────────────────────> NEED HUMAN
```

## Writing Need Human Notes

When marking an item Need Human, the Notes field must explain:

1. **What needs to be checked** — the specific criterion to verify
2. **How to check it** — the specific tool or action required
3. **Where to look** — element, panel, or location to inspect

**Good Note example:**

> "Need Human: Verify sub-pixel alignment between the 'Publish' button text and the adjacent icon. Visually inspect the button at 100% zoom. Expected: The baseline of the text perfectly aligns with the center of the icon."

**Poor Note example:**

> "Need Human: check accessibility"
