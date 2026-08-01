# WCAG 2.2 Key Criteria — GUI Checklist Designer

Annotated reference for WCAG 2.2 Level A and AA criteria most relevant to web UI testing. Use the **Code** column as the `Heuristic Ref` value in checklist items.

W3C. (2023). _Web Content Accessibility Guidelines (WCAG) 2.2_. https://www.w3.org/TR/WCAG22/

## Principle 1 — Perceivable

| Code       | Criterion              | Level | Checklist application                                                                                    |
| ---------- | ---------------------- | ----- | -------------------------------------------------------------------------------------------------------- |
| WCAG1.1.1  | Non-text Content       | A     | Images have meaningful alt text; decorative images have empty `alt=""`                                   |
| WCAG1.3.1  | Info and Relationships | A     | Table headers use `<th>`; form inputs have programmatic label association; groups have fieldset + legend |
| WCAG1.4.1  | Use of Colour          | A     | Status badges include text or icon in addition to colour; error states not indicated by colour alone     |
| WCAG1.4.3  | Contrast (Minimum)     | AA    | Normal text contrast ≥ 4.5:1; large text (≥ 18pt or 14pt bold) contrast ≥ 3:1                            |
| WCAG1.4.4  | Resize Text            | AA    | Page remains usable at 200% zoom without horizontal scrolling                                            |
| WCAG1.4.10 | Reflow                 | AA    | Content reflows to single-column at 320 CSS px wide without horizontal scroll                            |
| WCAG1.4.11 | Non-text Contrast      | AA    | UI components (button borders, input borders, icons) contrast ≥ 3:1 against adjacent background          |

## Principle 2 — Operable

| Code       | Criterion                    | Level | Checklist application                                                                                  |
| ---------- | ---------------------------- | ----- | ------------------------------------------------------------------------------------------------------ |
| WCAG2.1.1  | Keyboard                     | A     | All functionality operable by keyboard alone; no keyboard trap (except where documented and escapable) |
| WCAG2.1.2  | No Keyboard Trap             | A     | Focus can always be moved away from a component using keyboard                                         |
| WCAG2.4.3  | Focus Order                  | A     | Tab order follows logical reading order; focus sequence preserves meaning and operability              |
| WCAG2.4.6  | Headings and Labels          | AA    | Form labels and section headings are descriptive                                                       |
| WCAG2.4.7  | Focus Visible                | AA    | Keyboard focus indicator visible on every interactive element                                          |
| WCAG2.4.11 | Focus Not Obscured (Minimum) | AA    | Focused component not entirely hidden by sticky headers, overlays, or cookie banners                   |
| WCAG2.5.7  | Dragging Movements           | AA    | Drag-and-drop operations have a single-pointer (click) alternative                                     |
| WCAG2.5.8  | Target Size (Minimum)        | AA    | Interactive targets ≥ 24×24 CSS pixels (except inline links and where spacing compensates)             |

## Principle 3 — Understandable

| Code      | Criterion                                 | Level | Checklist application                                                                            |
| --------- | ----------------------------------------- | ----- | ------------------------------------------------------------------------------------------------ |
| WCAG3.3.1 | Error Identification                      | A     | Error messages identify the specific field by name and describe the error in text                |
| WCAG3.3.2 | Labels or Instructions                    | A     | Every form input has a visible label (not placeholder-only); required field indicator explained  |
| WCAG3.3.3 | Error Suggestion                          | AA    | Error messages suggest how to correct the mistake (where possible without compromising security) |
| WCAG3.3.4 | Error Prevention (Legal, Financial, Data) | AA    | For transactions: review/confirm step provided before submission; or submission is reversible    |

## IA Category Mapping

Suggested distribution of WCAG items across common IA categories:

| WCAG Code               | Suggested IA category                                        |
| ----------------------- | ------------------------------------------------------------ |
| WCAG1.1.1               | General UI Standards (images), Forms (file upload)           |
| WCAG1.3.1               | Forms (labels, grouping), Navigation (landmark regions)      |
| WCAG1.4.1               | Feedback/State (status colours, error indicators)            |
| WCAG1.4.3, 1.4.11       | General UI Standards (visual design)                         |
| WCAG1.4.4, 1.4.10       | General UI Standards (responsive/zoom)                       |
| WCAG2.1.1, 2.1.2        | Navigation (keyboard operability), Forms (keyboard submit)   |
| WCAG2.4.3               | Navigation (focus/tab order)                                 |
| WCAG2.4.6               | Forms (labels), Navigation (headings)                        |
| WCAG2.4.7, 2.4.11       | Navigation (focus visibility), General UI Standards          |
| WCAG2.5.7               | Navigation (drag-and-drop alternatives)                      |
| WCAG2.5.8               | General UI Standards (touch targets)                         |
| WCAG3.3.1, 3.3.2, 3.3.3 | Forms (validation and error handling)                        |
| WCAG4.1.2               | Forms (ARIA on custom controls), Navigation (custom widgets) |
| WCAG4.1.3               | Feedback/State (status announcements)                        |

This mapping is a guide — place items in whichever IA category best fits the human's defined scopes for this particular run.
