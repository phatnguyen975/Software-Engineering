# Heuristics Reference — GUI Checklist Designer

Quick-reference card for all five heuristic frameworks used by this skill. Use the **Code** column as the `Heuristic Ref` value in checklist items.

## 1. Nielsen's 10 Usability Heuristics (1994)

Nielsen, J. (1994). _10 Usability Heuristics for User Interface Design_. Nielsen Norman Group. https://www.nngroup.com/articles/ten-usability-heuristics/

| Code | Heuristic                                               | Primary GUI checklist application                                                                      |
| ---- | ------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| N1   | Visibility of System Status                             | Loading spinners, progress bars, status badges, upload progress, real-time log updates                 |
| N2   | Match Between System and Real World                     | Labels use domain vocabulary (not internal codes), icons follow conventions, date formats match locale |
| N3   | User Control and Freedom                                | Cancel button on every form, Undo for destructive actions, Back button handled gracefully              |
| N4   | Consistency and Standards                               | Same button position across screens, same colour means same thing, same terminology throughout         |
| N5   | Error Prevention                                        | Date pickers enforce valid ranges, dropdowns prevent invalid entry, confirm before delete              |
| N6   | Recognition Rather Than Recall                          | Options shown in dropdowns/pickers, breadcrumbs show location, tooltips on icon-only controls          |
| N7   | Flexibility and Efficiency of Use                       | Keyboard shortcuts, bulk-select actions, quick filters accessible without opening a modal              |
| N8   | Aesthetic and Minimalist Design                         | No redundant information, visual hierarchy guides attention, whitespace used intentionally             |
| N9   | Help Users Recognise, Diagnose, and Recover from Errors | Error messages in plain language, adjacent to offending field, with specific fix suggestion            |
| N10  | Help and Documentation                                  | Contextual tooltips, empty-state guidance, inline hints on complex fields                              |

## 2. Shneiderman's 8 Golden Rules of Interface Design (2016)

Shneiderman, B., Plaisant, C., Cohen, M., Jacobs, S., & Elmqvist, N. (2016). _Designing the User Interface_ (6th ed.). Pearson. http://www.cs.umd.edu/hcil/DTUI6/

| Code | Rule                                   | Primary GUI checklist application                                                                                           |
| ---- | -------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| S1   | Strive for Consistency                 | Sidebar position, action button order, terminology, colour semantics uniform across screens                                 |
| S2   | Enable Frequent Users to Use Shortcuts | Tab key order, keyboard activation of buttons, bulk operations, quick-access links                                          |
| S3   | Offer Informative Feedback             | Toast on save/error, spinner on async action, confirmation message after submit                                             |
| S4   | Design Dialogues to Yield Closure      | Multi-step forms show progress, success state is explicit (not just "form cleared"), wizard has final step                  |
| S5   | Offer Simple Error Handling            | Inline error on the offending field, focus returns to field on error, one click to understand and fix                       |
| S6   | Permit Easy Reversal of Actions        | Delete requires confirmation, draft mode before publish, undo available for reorder                                         |
| S7   | Support Internal Locus of Control      | No surprise auto-saves without indication, no unsolicited navigations, user explicitly triggers all state changes           |
| S8   | Reduce Short-term Memory Load          | Current filter/sort state visible, form context (section header) visible while scrolling, no "go back to remember" patterns |

## 3. Norman's 6 Design Principles — DOET 2013

Norman, D. A. (2013). _The Design of Everyday Things_ (Revised and Expanded Edition). Basic Books.

| Code | Principle        | Primary GUI checklist application                                                                                                             |
| ---- | ---------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| NOR1 | Affordances      | Buttons look clickable (raised/bordered), links look tappable, drag handles are visible                                                       |
| NOR2 | Signifiers       | Asterisk on required fields, placeholder text in inputs, hover state on interactive elements, disabled appearance on non-interactive controls |
| NOR3 | Constraints      | Date picker blocks end < start, number field rejects alphabetic, dropdowns limit to valid options                                             |
| NOR4 | Mappings         | Drag up = item moves up; sidebar highlight = current page; longer progress bar = more progress                                                |
| NOR5 | Feedback         | Validation triggers on field-blur (not only on submit), save toast appears within 1–2s, checkin log updates without page reload               |
| NOR6 | Conceptual Model | Status flow (`DRAFT` → `PUBLISHED` → `ENDED`) matches mental model; waitlist concept matches real-world queue concept                         |

## 4. Waghmare Per-Widget Checklist (2009)

Waghmare, P. (2009). GUI Testing checklist. _GUI + Usability + Compatibility Testing_ course slide (FIT HCMUS).

Use `W-[WidgetName]` as the `Heuristic Ref` code. Full per-widget details are in [`per-widget-guidelines.md`](per-widget-guidelines.md). Summary of widget codes:

| Code         | Widget                        |
| ------------ | ----------------------------- |
| W-TextBox    | Text input / textarea         |
| W-Dropdown   | Dropdown / combo box          |
| W-Button     | Push button                   |
| W-Checkbox   | Checkbox                      |
| W-Radio      | Radio button                  |
| W-Link       | Hyperlink                     |
| W-Image      | Image                         |
| W-Grid       | Table / data grid             |
| W-ListBox    | List box                      |
| W-Date       | Date / time picker            |
| W-Modal      | Modal / dialog                |
| W-Toast      | Toast / snackbar notification |
| W-FileUpload | File upload control           |
| W-RTE        | Rich text editor              |
| W-DragDrop   | Drag-and-drop interface       |
| W-Progress   | Progress bar / indicator      |
| W-Tab        | Tab panel                     |
| W-Sidebar    | Sidebar / navigation menu     |

## 5. WCAG 2.2 Level AA (W3C, 2023)

W3C. (2023). _Web Content Accessibility Guidelines (WCAG) 2.2_. https://www.w3.org/TR/WCAG22/

Use `WCAG[criterion-number]` as the `Heuristic Ref` code. Full annotated criteria list is in [`wcag22-key-criteria.md`](wcag22-key-criteria.md).

| Code       | Criterion | Short name                   | Level |
| ---------- | --------- | ---------------------------- | ----- |
| WCAG1.1.1  | 1.1.1     | Non-text Content             | A     |
| WCAG1.3.1  | 1.3.1     | Info and Relationships       | A     |
| WCAG1.4.1  | 1.4.1     | Use of Colour                | A     |
| WCAG1.4.3  | 1.4.3     | Contrast (Minimum)           | AA    |
| WCAG1.4.4  | 1.4.4     | Resize Text                  | AA    |
| WCAG1.4.11 | 1.4.11    | Non-text Contrast            | AA    |
| WCAG2.1.1  | 2.1.1     | Keyboard                     | A     |
| WCAG2.4.3  | 2.4.3     | Focus Order                  | A     |
| WCAG2.4.7  | 2.4.7     | Focus Visible                | AA    |
| WCAG2.4.11 | 2.4.11    | Focus Not Obscured (Minimum) | AA    |
| WCAG2.5.7  | 2.5.7     | Dragging Movements           | AA    |
| WCAG2.5.8  | 2.5.8     | Target Size (Minimum)        | AA    |
| WCAG3.3.1  | 3.3.1     | Error Identification         | A     |
| WCAG3.3.2  | 3.3.2     | Labels or Instructions       | A     |
| WCAG3.3.3  | 3.3.3     | Error Suggestion             | AA    |
| WCAG4.1.2  | 4.1.2     | Name, Role, Value            | A     |
| WCAG4.1.3  | 4.1.3     | Status Messages              | AA    |
