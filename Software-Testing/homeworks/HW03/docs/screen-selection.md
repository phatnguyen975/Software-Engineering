# Screen Selection — Scenario A: Admin Creates and Manages Events

> **Student:** Nguyễn Tấn Phát — 23127449  
> **Scenario:** A — Admin creates and manages events  
> **Task scope:** Task 1B (Checklist Execution) · Task 2 (User Testing) · Task 3 (Cross-Platform)  
> **Date:** 2026-07-26  
> **Checklist base:** `docs/shared-gui-checklist.md` (56 items, IA-01…IA-04)

## 1. Candidate Screens

Per `docs/requirements.md §5`, Scenario A covers **Pool A — Event Administration** and offers five candidate screens:

| ID  | Screen Name                              | Core Functionality                                                                       |
| --- | ---------------------------------------- | ---------------------------------------------------------------------------------------- |
| A1  | Events List                              | Status filters, notification dots, empty/loading states, pagination                      |
| A2  | Add / Edit Event Form                    | Image upload (4:3 / 24:9), Rich-Text editor, date/time validation, Draft/Publish actions |
| A3  | Registration & Roles Configuration Panel | Max Slots, Waitlist toggle, additional roles, student/lecturer/guest toggles             |
| A4  | Participants & Reviews Approval          | Status colour badges, attendance progress bar, Export button, approval actions           |
| A5  | Check-in Tab                             | QR scan-state handling, real-time check-in log                                           |

## 2. Selection Framework

Screens were evaluated against **three weighted dimensions** to maximise coverage quality and evidence value across all four tasks:

| Dimension                      | Rationale                                                                                                                                                                                                                                      | Weight |
| ------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------ |
| **D1 — IA Coverage Breadth**   | How many of the 4 IA categories (IA-01/02/03/04) does this screen exercise? Screens touching more IAs produce denser, more useful execution tables.                                                                                            | 40%    |
| **D2 — Functional Complexity** | Number of distinct widgets, states, and edge cases present. Higher complexity → higher defect yield and richer usability data.                                                                                                                 | 35%    |
| **D3 — Cross-Task Utility**    | How well does this screen serve all three tasks simultaneously? A screen that is testable for checklist execution (Task 1B), observable by real users (Task 2), and renderable across browsers/devices (Task 3) maximises ROI per screen slot. | 25%    |

Scores are 1–5 per dimension; weighted total out of 5.

| Screen | D1 (×0.4) | D2 (×0.35) | D3 (×0.25) | **Weighted Total** | Decision          |
| ------ | --------- | ---------- | ---------- | ------------------ | ----------------- |
| A1     | 4         | 3          | 5          | **3.85**           | ✅ Selected (3rd) |
| A2     | 5         | 5          | 5          | **5.00**           | ✅ Selected (1st) |
| A3     | 3         | 3          | 3          | **3.00**           | ❌ Not selected   |
| A4     | 4         | 4          | 5          | **4.25**           | ✅ Selected (2nd) |
| A5     | 2         | 2          | 2          | **2.00**           | ❌ Not selected   |

## 3. Selected Screens

### ✅ Screen A2 — Add / Edit Event Form

**Priority:** #1 (Highest value, tested first)

**What this screen contains:**

- Text input fields (Event Title, Location, Description via Rich-Text Editor)
- Date/time pickers with range constraints (Start Date ≤ End Date; Registration Close ≤ Start Date)
- Thumbnail image upload with enforced **4:3 aspect ratio**
- Banner image upload with enforced **24:9 aspect ratio**
- Rich-Text Editor toolbar (Bold, Italic, Lists, Insert Link)
- Required-field asterisks and inline validation
- Save as Draft / Publish action buttons (must be visually distinct — IA-02-015)
- Back/Cancel control with unsaved-data guard (IA-03-010)
- i18n EN/VI label switching (IA-01-013)
- Loading/success/error toast feedback on save actions (IA-04-001…IA-04-003)

**Justification — D1 (IA Coverage = 5/5):**  
A2 is the only screen in Pool A that simultaneously exercises **all four IA categories in depth**:

- **IA-01:** Typography, colour contrast, layout consistency, i18n toggle behaviour, loading states.
- **IA-02:** Every form concern — labels, required-field markers, inline validation, error placement, date-picker range enforcement, file-upload aspect ratio validation, RTE functionality, data preservation on server error (IA-02-010, IA-02-011, IA-02-007, IA-02-015).
- **IA-03:** Breadcrumb (Events → Add Event), Back/Cancel with unsaved-data guard, deep link to edit URL.
- **IA-04:** Toast feedback on save/publish, confirmation before publish action distinction, error toast persistence.

**Justification — D2 (Functional Complexity = 5/5):**  
A2 is the most widget-dense screen in the entire EMS Admin interface. It contains all seven widget types from the Waghmare per-widget taxonomy covered by our checklist (W-TextBox, W-Dropdown, W-Date, W-FileUpload, W-RTE, W-Button, W-Toast), plus EMS-specific constraints (4:3 / 24:9 ratio enforcement) that no other screen has. The date-range validation (end ≥ start ≥ registration-close) creates multiple edge cases that are high-yield for defect discovery.

**Justification — D3 (Cross-Task Utility = 5/5):**  
For Task 2 (User Testing): The task scenario "create and publish a workshop event" naturally lands users on this screen, making it the primary interaction surface. Friction here is directly observable (where does the admin get confused by the form? How do they discover the image ratio requirement?).  
For Task 3 (Cross-Platform): Form rendering differences across browsers are well-known high-risk areas — date-picker widgets behave differently on Firefox vs. Chrome, file upload dialogs differ on iOS, and Rich-Text Editors are notoriously inconsistent on mobile browsers.

### ✅ Screen A4 — Participants & Reviews Approval

**Priority:** #2

**What this screen contains:**

- Status colour badges (`PENDING`, `APPROVED`, `WAITLISTED`, `REJECTED`, `CHECKED-IN`) — all must include text labels (IA-04-004)
- Attendance progress bar with numerical ratio (e.g. 12/30 checked-in) — (IA-04-007)
- Participant list with approve/reject/waitlist action controls
- Reviews sub-tab with star ratings and moderation actions
- Export to Excel (.xlsx) button with download feedback toast
- Tab panel navigation (Participants | Reviews | Check-in) — (IA-03-006, IA-03-007)
- Empty state handling when no participants have registered (IA-01-010)
- Confirmation dialogs for destructive approval actions (IA-04-005)

**Justification — D1 (IA Coverage = 4/5):**  
A4 is the richest screen for **IA-04 (Feedback/State)** coverage in the entire Pool A, and it also exercises IA-01 and IA-03 substantially:

- **IA-01:** Status badge legibility at 200% zoom, colour contrast of badge colours, empty state message when participant list is empty.
- **IA-03:** Tab panel keyboard accessibility (Tab → Arrow keys → Enter), breadcrumb accuracy on the event detail deep link.
- **IA-04:** Every status badge must have a text label in addition to colour (WCAG 1.4.1), progress bar must display numerical value alongside the bar, export must show toast feedback, confirmation dialogs for approval/rejection actions must trap focus and respond to Esc.

**Justification — D2 (Functional Complexity = 4/5):**  
A4 presents multiple co-existing UI states simultaneously (a mix of PENDING, APPROVED, WAITLISTED participants in the same list), making it excellent for verifying colour-semantic consistency and badge text-label compliance across states. The progress bar provides a testable numeric readout. The tab panel adds keyboard navigation checks. The Export button adds async feedback (download toast/progress) to the mix.

**Justification — D3 (Cross-Task Utility = 5/5):**  
For Task 2 (User Testing): A realistic admin task scenario ("approve 2 participants and export the list for your department head") brings users to this screen, allowing observation of how admins interpret status badges and navigate between tabs.  
For Task 3 (Cross-Platform): Tab panels are known to have rendering differences on mobile viewports (often collapsed into a dropdown or overflowing horizontally). Progress bars render differently across browsers (WebKit vs. Gecko styling). Badge colours require checking on high-contrast mode/forced-colors across OS.

### ✅ Screen A1 — Events List

**Priority:** #3

**What this screen contains:**

- Event list/table with thumbnail, title, status badge, date, and action buttons (Edit, Delete, Preview)
- Status filter tabs (ALL / DRAFT / PUBLISHED / CANCELLED / ENDED)
- Notification dots on event items (e.g. pending participant requests)
- Search/filter input for events
- Empty state when no events match the filter (IA-01-010)
- Loading state while the list fetches (IA-01-009)
- Sidebar navigation with active-page indicator (IA-03-001…IA-03-004)
- Confirmation dialog before Delete action (IA-04-005)
- Toast feedback after Delete action success/failure (IA-04-001…IA-04-003)
- Breadcrumb: Events (current page, no link needed)

**Justification — D1 (IA Coverage = 4/5):**  
A1 covers three of four IA categories well:

- **IA-01:** Thumbnail 4:3 rendering in list view — verify at 1× and 200% zoom, status badge colour contrast, empty/loading states, layout at 320 px viewport, i18n toggle for column headers.
- **IA-03:** Sidebar active-page state, breadcrumb, deep-link to event detail from a list row.
- **IA-04:** Status badges (DRAFT/PUBLISHED/CANCELLED) must include text labels, confirmation dialog before Delete, success/error toast after Delete, notification dot semantics.

**Justification — D2 (Functional Complexity = 3/5):**  
A1 is moderately complex: the list itself is a data table with multiple column types and interactive elements per row, and the filter/search combination adds state. However, it lacks form widgets (no validation) and the RTE, making it simpler than A2 and A4.

**Justification — D3 (Cross-Task Utility = 5/5):**  
For Task 2 (User Testing): A1 is always the first screen a participant lands on after login. First impressions, discoverability of the "Add Event" button, and understanding of status badges are observable here. Observing whether a user can orient themselves on A1 is critical usability data.  
For Task 3 (Cross-Platform): List/table layouts are high-risk for responsive design failures — columns may collapse, overflow, or become unreadable at phone viewport widths. The notification dot may disappear at small sizes. This makes A1 a high-yield screen for compatibility defects.  
**Strategic reason:** Selecting A1 establishes the natural sequential flow A1 → A2 → A4 that mirrors the real admin workflow (browse the list, create a new event, then manage participants). This flow coherence strengthens the Task 2 user-testing scenario and allows Task 3 screenshots to tell a complete, credible story.

## 4. Rejected Screens

### ❌ Screen A3 — Registration & Roles Configuration Panel\

**What this screen contains:**

- Toggles for who can register: Student / Lecturer / Guest (boolean switches)
- Max Slots numeric input
- Waitlist toggle
- Additional roles section (custom role names and slot limits)

**Why not selected:**

**A3 is not a standalone screen** — it has no independent URL, no breadcrumb of its own, and is always accessed as a sub-panel within the event creation flow. This makes it untestable independently for Task 2 and Task 3.

Furthermore, **GUI coverage is heavily skewed toward IA-02 only**, and even within IA-02, A3 covers a narrow subset — primarily toggle switches and a numeric input, with minimal validation complexity compared to A2. Specifically:

- **No image upload:** A2 already covers the EMS-specific upload constraints (4:3 / 24:9) fully. Adding A3 provides no incremental coverage of this high-priority area.
- **No Rich-Text Editor:** A3 has no RTE, which is the most complex widget in Pool A.
- **Limited IA-03 coverage:** A3 is always a sub-panel accessed from the event detail page, not a standalone navigable screen — it does not add meaningful breadcrumb, deep-link, or sidebar tests beyond what A2 and A1 already cover.
- **Limited IA-04 coverage:** The feedback interactions on A3 (saving registration config) are a subset of the feedback already covered by A2 (saving the event form).
- **Low Task 2 utility:** The registration config step is not a standalone navigable screen — it cannot anchor an independent Task 2 scenario or a Task 3 deep-link test, which makes it structurally unsuitable as a selected screen regardless of its widget content.
- **Low Task 3 utility:** A3 is a simple toggle+numeric-input panel. It is unlikely to produce responsive layout failures or cross-browser rendering differences beyond what A2's form already reveals.

> **In summary:** A3's coverage is almost entirely a **strict subset** of what A2 already tests. Selecting A3 would create significant overlap without adding unique IA coverage or usability signal. The limited slot budget (≥3 screens) is better spent on screens with distinct, non-overlapping coverage.

### ❌ Screen A5 — Check-in Tab

**What this screen contains:**

- QR scan-state display (waiting for scan, scan received, error state)
- Real-time check-in log (list of participant entries as they scan)
- Entry format: participant name, check-in time, check-in method

**Why not selected:**

**A5 is the screen with the lowest testability score** in Pool A for this assignment's context, and this is not a question of difficulty — it is a question of the type of testing it requires:

- **Inherently functional/performance, not GUI:** The primary observable behaviour of A5 — the real-time log updating within seconds of a QR scan — requires a physical QR scanner, a registered participant, and timing measurement infrastructure. These are **functional testing and performance testing concerns**, not GUI concerns testable via the shared checklist. Crucially, our own review of the shared checklist (IA-04-012, corrected) concluded that "updates in real time within 3 seconds" cannot be verified by GUI testing alone; the testable GUI artifact is only the log entry format.
- **Limited UI surface area:** Once the real-time behaviour is excluded from scope, A5's GUI-testable surface reduces to only one concern: the format and layout of each log entry (participant name, timestamp, method). This is a single checklist item (IA-04-012 as revised), not enough to warrant a full screen slot.
- **Low Task 2 utility:** Real-time QR check-in cannot be realistically simulated in a controlled usability-testing session without physical QR codes and a live participant queue. Asking a participant to "test the check-in screen" with no actual scanning activity does not produce valid usability data.
- **Low Task 3 utility:** A5 has virtually no form elements, navigation complexity, or layout-intensive content. Cross-browser and cross-platform differences on a simple log list are negligible compared to A1's data table or A2's form.

> **In summary:** A5's core value proposition (real-time QR scan validation) sits outside the scope of GUI/Usability testing as taught in this course. The remaining GUI-only surface is too thin to justify a screen slot. This is a considered judgment about test scope, not a reflection of difficulty.

## 5. Final Selection Summary

| Priority | Screen ID | Screen Name            | IA-01 | IA-02 | IA-03 | IA-04 | Weighted Score |
| -------- | --------- | ---------------------- | ----- | ----- | ----- | ----- | -------------- |
| 1        | **A2**    | Add / Edit Event Form  | ✅    | ✅✅  | ✅    | ✅    | 5.00           |
| 2        | **A4**    | Participants & Reviews | ✅    | —     | ✅    | ✅✅  | 4.75           |
| 3        | **A1**    | Events List            | ✅    | —     | ✅✅  | ✅    | 3.85           |

> ✅✅ = primary coverage area for that screen in that IA category  
> ✅ = meaningful secondary coverage  
> — = minimal or no coverage

### Collective IA Coverage

| IA Category                | Covered by   | Primary Screen    |
| -------------------------- | ------------ | ----------------- |
| IA-01 General UI Standards | A1 + A2 + A4 | A2                |
| IA-02 Forms                | A2           | A2 (sole primary) |
| IA-03 Navigation           | A1 + A2 + A4 | A1                |
| IA-04 Feedback / State     | A1 + A2 + A4 | A4                |

All four IA categories are covered by the selected screen set. No checklist item in the shared checklist is entirely untestable across the three screens.

### Execution Order

Tests will be executed in the natural admin workflow order:

```
A1 (Events List) → A2 (Add/Edit Event Form) → A4 (Participants & Reviews)
```

This order mirrors the real admin journey (browse → create → manage), which also makes it the most coherent and realistic task scenario for Task 2 user testing.
