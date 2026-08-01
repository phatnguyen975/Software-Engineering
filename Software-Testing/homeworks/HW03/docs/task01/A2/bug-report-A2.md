# Bug Report — Add / Edit Event Form (A2)

> **Source:** `execution-A2.md` · `edge-cases-A2.md`  
> **Tester:** Nguyễn Tấn Phát — 23127449  
> **Date:** 2026-07-29  
> **Total bug groups:** 11  
> **Severity:** Severity 4 (1) · Severity 3 (3) · Severity 2 (5) · Severity 1 (2) · Severity 0 (0)

## BUG-A2-001 — Inline Validation Does Not Fire on Field Blur; Error Styling Inconsistent on Submit

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-001                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-02-003, IA-02-005        |
| **Affected Edge Cases** | None                        |

### Description

Required fields (Event Title, Date pickers, Campus) do not trigger inline error messages when the user tabs out of an empty field. Additionally, when the form is submitted blank, the error styling is inconsistently applied — only the Campus field highlights its border in red and show plain text error message, while all other required fields only show plain text error messages beneath them without border highlighting.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Click into the **Event Title** field, leave it empty, then press Tab to move focus to the next field.
3. **Observe:** No inline error message or error styling appears.
4. Click **Publish** with all required fields empty.
5. **Observe:** Error text messages appear below all required fields, but only the **Campus** field additionally highlights its border in red; all other fields (Event Title, Start Date, etc.) receive no border change.

### Expected Behaviour

- An inline error message should appear immediately when a required field loses focus (blur event) while empty.
- After a failed submission, all required field errors should be presented with visually consistent styling (e.g., all borders turn red, or none do).

### Actual Behaviour

- No error appears on blur; validation only fires on form submission.
- On failed submission, Campus renders both red border and error text, while all other required fields render only error text without a red border — creating an inconsistent error state across the form.

### Heuristic Reference

- N9 (Help users recognize, diagnose, and recover from errors)
- S4 (Consistency)
- WCAG 3.3.1 (Error Identification)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/17gOV13Vsbbi7yEycebGGfgRJIDmraIAo/view)

### Suggested Fix

- Attach a `blur` event listener to all required fields that triggers inline validation immediately.
- Normalize the error state CSS across all required field types (inputs, dropdowns, date pickers) so that a failed validation consistently applies a red border to all of them.

## BUG-A2-002 — Required Field Asterisk (`*`) Is Black Instead of Red

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A2-002           |
| **Screen**              | A2: Add / Edit Event |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-02-001            |
| **Affected Edge Cases** | None                 |

### Description

The asterisk (`*`) displayed next to required field labels is styled in black (same colour as the label text), making it ineffective at visually distinguishing mandatory fields from optional ones.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Inspect the label of the **Event Title** field and any date/time picker label.
3. **Observe:** The `*` symbol is rendered in black, matching the label colour.

### Expected Behaviour

The required field asterisk (`*`) should be rendered in red (`#dc2626` or equivalent) to be immediately distinguishable from the label text, following standard web form convention.

### Actual Behaviour

The asterisk (`*`) is rendered in black (same colour as surrounding label text), providing no visual distinction.

### Heuristic Reference

- N4 (Consistency and Standards)
- S4 (Consistency)

### Evidences

![BUG-A2-002.png](../../../screenshots/task01/A2/BUG-A2-002.png)

### Suggested Fix

Apply `color: red` (or `text-[red]` in Tailwind) to the `<span>` or character wrapping the asterisk in all required field labels.

## BUG-A2-003 — Date/Time Picker: Keyboard Input Blocked and Popup Hidden When Near Screen Bottom

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-003                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Bug                         |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-02-008                   |
| **Affected Edge Cases** | EC-A2ADD-003                |

### Description

Date/time picker fields have two distinct defects: (1) the field does not accept keyboard text input — users can only interact via the calendar icon; (2) when the date picker is opened near the bottom of the viewport, the calendar popup is clipped/hidden instead of flipping to open upward.

### Steps to Reproduce

**Keyboard Accessibility:**

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Tab to the **Start Date & Time** field.
3. Attempt to type a date directly (e.g., `07292026`).
4. **Observe:** No text input is accepted; the field is read-only.

**Popup Overflow:**

1. Scroll to the **Registration** section.
2. Click the **Registration Close** date picker icon.
3. **Observe:** The calendar popup opens downward but is partially or fully hidden beyond the viewport bottom edge instead of flipping upward.

### Expected Behaviour

- Users should be able to type a date/time value directly into the field.
- The calendar popup should detect its proximity to the viewport edge and intelligently open in the opposite direction (upward) to remain fully visible.

### Actual Behaviour

- Keyboard input to date fields is entirely blocked; only the calendar icon click works.
- Calendar popups near the viewport bottom are clipped/hidden rather than flipping upward.

### Heuristic Reference

- N7 (Flexibility and Efficiency of Use)
- WCAG 2.1.1 (Keyboard)
- NOR3 (Mental Models)

### Evidences

![BUG-A2-003.png](../../../screenshots/task01/A2/BUG-A2-003.png)

### Suggested Fix

- Remove the `readOnly` or `pointer-events: none` attribute from the date input element to enable keyboard entry with appropriate masking.
- Configure the date picker library (e.g., `placement: "auto"` or `flip: true`) so the popup auto-detects viewport boundaries and flips to open upward when needed.

## BUG-A2-004 — Drag-and-Drop File Upload Not Functional Despite UI Text Claiming Support

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-004                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Bug                         |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-02-010                   |
| **Affected Edge Cases** | None                        |

### Description

All three file upload zones (Thumbnail, Banner, Attachments) fail to respond to OS-level file drag-and-drop, despite the Attachments zone explicitly displaying the text "Drag and drop files here or click to select". No visual feedback is provided when a file is dragged over the zones.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Open a file manager and select any image file.
3. Drag the image over the **Thumbnail** dropzone.
4. **Observe:** No border highlight, background change, or any visual cue appears.
5. Release the file over the zone.
6. **Observe:** The file is not uploaded; nothing happens.
7. Repeat for the **Banner** and **Attachments** zones.

### Expected Behaviour

Each dropzone should visually change state (e.g., highlighted border, background colour change) when a file is dragged over it, and should accept and process the dropped file.

### Actual Behaviour

Dragging a file over any zone produces zero visual feedback and drops the file without processing it. The Attachments zone label text "Drag and drop files here" is misleading.

### Heuristic Reference

- N1 (Visibility of System Status)
- N4 (Consistency and Standards)
- NOR5 (Affordances)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/18H_htLzPb21Vupiw2h6ljCv9pGUW3bSI/view)

### Suggested Fix

- Implement `dragover`, `dragenter`, and `drop` event listeners on all three upload zones.
- On `dragenter`, apply an active state class (e.g., `ring-2 ring-blue-500 bg-blue-50`) to provide immediate visual feedback.
- If drag-and-drop is not yet implemented for Thumbnail/Banner, remove or correct the "Drag and drop" instructional text from the Attachments zone to avoid misleading users.

## BUG-A2-005 — Rich Text Editor: Uploaded Image Cannot Be Removed; Editor Expands Infinitely Without Internal Scrollbar

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-005                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-02-013, IA-02-014        |
| **Affected Edge Cases** | None                        |

### Description

The Rich Text Editor (RTE) used for the event Description field has two related UX defects: (1) after inserting an image via the RTE toolbar, there is no Remove/Trash control to delete it; (2) when large amounts of content are entered, the editor expands its height infinitely, pushing the Date & Time section far down the page with no internal scrollbar.

### Steps to Reproduce

**No Remove Control:**

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Click into the **Description** RTE.
3. Use the toolbar image insertion button to upload an image.
4. **Observe:** The image is inserted and previewed, but no Remove/Trash icon appears adjacent to it.

**Infinite Height Expansion:**

1. In the Description RTE, paste a block of 50+ lines of text.
2. **Observe:** The RTE expands its container height to accommodate all content without limit.
3. To fill out the Date & Time fields, the user must scroll far down the page.

### Expected Behaviour

- An image inserted into the RTE should have an accessible Remove button (e.g., a trash icon overlay) to allow it to be deleted.
- The RTE should have a defined maximum height, after which an internal scrollbar appears, preventing the editor from pushing subsequent form sections off-screen.

### Actual Behaviour

- Uploaded images inside the RTE cannot be removed through the UI.
- The RTE height grows unboundedly, creating significant navigation friction for long descriptions.

### Heuristic Reference

- N3 (User Control and Freedom)
- N8 (Aesthetic and Minimalist Design)
- NOR3 (Mental Models)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1kNvz6gb9aDC3-VqQxozsjrZDybPYhqt_/view)

### Suggested Fix

- For the image removal issue: Add a click handler and remove button overlay on RTE-inserted images.
- For the infinite expansion: Apply `max-height: 400px; overflow-y: auto;` (or equivalent) to the RTE content container to introduce an internal scrollbar.

## BUG-A2-006 — No Toast Notification Displayed After Save as Draft or Publish

| Field                   | Value                           |
| ----------------------- | ------------------------------- |
| **ID**                  | BUG-A2-006                      |
| **Screen**              | A2: Add / Edit Event            |
| **Type**                | Usability                       |
| **Severity**            | 3 — Major Usability Problem     |
| **Priority**            | High                            |
| **Affected Items**      | IA-04-001, IA-04-002, IA-04-003 |
| **Affected Edge Cases** | None                            |

### Description

After clicking **Save as Draft** or **Publish**, no toast notification is displayed to confirm the action. The application silently redirects the user to the Event List screen, providing no explicit success/failure feedback before navigation.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Fill in all required fields with valid data.
3. Click **Save as Draft**.
4. **Observe:** The page immediately redirects to the Event List. No toast message appears.
5. Repeat steps 1–3 and click **Publish** instead.
6. **Observe:** Same behavior — silent redirect, no success notification.

### Expected Behaviour

After clicking Save as Draft or Publish, a success toast notification should appear briefly (≥ 5 seconds with auto-dismiss and manual dismiss button), using appropriate colour semantics (green for success), before or after the redirect, to confirm the action was completed.

### Actual Behaviour

The application silently redirects to Event List with no toast, no snackbar, and no inline confirmation message. Users have no explicit confirmation that their action succeeded beyond the redirect itself.

### Heuristic Reference

- N1 (Visibility of System Status)
- S5 (Informative Feedback)
- NOR6 (Feedback Loops)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1IwE7Uh4a01u5SV0-OTaEFpFgo3BZNhH5/view)

### Suggested Fix

Implement a toast notification system. After a successful save or publish, trigger a green success toast (e.g., "Event saved as draft" or "Event published successfully") that auto-dismisses after 5 seconds and includes a manual dismiss button.

## BUG-A2-007 — Campus Dropdown Missing Arrow Indicator

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A2-007           |
| **Screen**              | A2: Add / Edit Event |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-02-006            |
| **Affected Edge Cases** | None                 |

### Description

The **Campus** dropdown field displays only a placeholder text ("Select campus") without any visible dropdown arrow indicator, making it non-obvious to users that the element is a clickable dropdown rather than a read-only text display.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Scroll to the **Location & Organization** section.
3. Observe the **Campus** field.
4. **Observe:** The field shows "Select campus" text but has no visible chevron/arrow icon on the right side.

### Expected Behaviour

Dropdown fields should display a visible arrow/chevron indicator on their right edge to communicate that the element is interactive and expandable.

### Actual Behaviour

The Campus dropdown field displays no arrow indicator, appearing as a plain text label rather than a selectable dropdown control.

### Heuristic Reference

- N4 (Consistency and Standards)
- NOR5 (Affordances)

### Evidences

![BUG-A2-007.png](../../../screenshots/task01/A2/BUG-A2-007.png)

### Suggested Fix

Add a `<ChevronDownIcon>` (or CSS `::after` arrow) to the right side of the Campus `<select>` element, consistent with the styling of the Event Types and Academic Context dropdown buttons.

## BUG-A2-008 — Publish Button Text Contrast Ratio Fails WCAG AA

| Field                   | Value                     |
| ----------------------- | ------------------------- |
| **ID**                  | BUG-A2-008                |
| **Screen**              | A2: Add / Edit Event      |
| **Type**                | Bug                       |
| **Severity**            | 4 — Usability Catastrophe |
| **Priority**            | High                      |
| **Affected Items**      | IA-01-003                 |
| **Affected Edge Cases** | None                      |

### Description

The text label on the **Publish** button has a colour contrast ratio of 2.08:1 against its background, which is far below the WCAG 2.1 AA minimum requirement of 4.5:1 for normal-sized text. This constitutes a legal accessibility violation.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Scroll to the bottom of the form to locate the **Publish** button.
3. Open DevTools → inspect the button text colour and background colour.
4. Calculate or use a contrast checker: contrast ratio = **2.08:1**.
5. Compare against WCAG AA minimum: **4.5:1** (normal text).

### Expected Behaviour

The Publish button text must achieve a minimum contrast ratio of 4.5:1 against its button background colour to comply with WCAG 2.1 Level AA (Success Criterion 1.4.3 Contrast (Minimum)).

### Actual Behaviour

The Publish button text-to-background contrast ratio is 2.08:1 — approximately 2.2× below the minimum requirement.

### Heuristic Reference

- WCAG 1.4.3 (Contrast Minimum — Level AA)
- N4 (Consistency and Standards)

### Evidences

![BUG-A2-008.png](../../../screenshots/task01/A2/BUG-A2-008.png)

### Suggested Fix

Darken the button text colour or lighten/darken the background colour until the contrast ratio reaches at least 4.5:1. For example, if the current background is `#1bc2f5` (light blue), use a dark text colour such as `#0a4f6b` or switch to white text `#ffffff` which achieves approximately 3.1:1 — if white is used, the background must also be darkened (e.g., `#0e86ab`) to reach 4.5:1.

## BUG-A2-009 — Sub-description Textarea Line Height Below Required 1.5× Ratio

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-009                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-01-007                   |
| **Affected Edge Cases** | None                        |

### Description

The **Sub-description** textarea has a computed line height of 20px with a font size of 14px, giving a ratio of 1.43 — below the WCAG 1.4.12 minimum of 1.5× required for body text readability.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Click into the **Sub-description** textarea and type multiple lines of text.
3. Open DevTools → Inspect the textarea element.
4. Check computed `line-height` (20px) and `font-size` (14px).
5. Calculate ratio: 20 ÷ 14 = **1.43** (below the 1.5 threshold).

### Expected Behaviour

Line height should be at least 1.5× the font size for body/paragraph text, per WCAG 1.4.12 (Text Spacing). For 14px font, the minimum line height should be **21px**.

### Actual Behaviour

Line height is 20px for 14px text, giving a ratio of 1.43×, making multi-line text appear cramped.

### Heuristic Reference

- WCAG 1.4.12 (Text Spacing)
- N8 (Aesthetic and Minimalist Design)

### Evidences

![BUG-A2-009.png](../../../screenshots/task01/A2/BUG-A2-009.png)

### Suggested Fix

Update the textarea CSS to set `line-height: 1.5` (or `line-height: 21px`). Apply this consistently to the Sub-description textarea and any other body-level text inputs on the form.

## BUG-A2-010 — Layout Breaks and Sidebar Overflows at 320px Viewport Width

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-010                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Bug                         |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-01-015                   |
| **Affected Edge Cases** | None                        |

### Description

When the browser viewport is narrowed to 320px width, the application layout breaks: the sidebar is not collapsed into a hamburger/toggle button, causing the sidebar and form content to overflow their containers chaotically, and making the page unusable on small mobile viewports.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Open DevTools → set viewport width to **320px**.
3. **Observe:** The sidebar remains at full width and does not collapse.
4. **Observe:** Form sections overflow and stack chaotically, content extends beyond the viewport width.

### Expected Behaviour

At 320px viewport width, the sidebar should collapse into a hidden state accessible via a hamburger/toggle button, and the form content should reflow into a single-column layout that fits within the viewport without horizontal scrolling.

### Actual Behaviour

The sidebar remains expanded and overlaps the form content. Layout containers lose their responsive behaviour and overflow horizontally, rendering the page unusable without horizontal scrolling.

### Heuristic Reference

- N4 (Consistency and Standards)
- WCAG 1.4.10 (Reflow)
- S5 (Universal Usability)

### Evidences

![BUG-A2-010.png](../../../screenshots/task01/A2/BUG-A2-010.png)

### Suggested Fix

- Implement a responsive breakpoint (e.g., `< 768px`) at which the sidebar collapses to a toggle-activated off-canvas menu.
- Ensure all form grid layouts use `flex-wrap` or single-column responsive grids (`grid-cols-1`) at mobile breakpoints.

## BUG-A2-011 — No Tooltip on Upload Zone Action Buttons

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-011                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-04-015                   |
| **Affected Edge Cases** | None                        |

### Description

The action buttons (edit/upload icons) overlaid on the Thumbnail, Banner, and Attachments upload zones do not display any tooltip on hover, leaving users without clear feedback about what action those icon-only buttons will perform.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Hover the mouse cursor over the edit/action button in the **Thumbnail** upload zone.
3. Wait for approximately 1–2 seconds.
4. **Observe:** No tooltip appears.
5. Repeat for the **Banner** upload zone action button.

### Expected Behaviour

Icon-only buttons should display a descriptive text tooltip on hover (e.g., "Upload Thumbnail", "Upload Banner") to clearly communicate their function to users.

### Actual Behaviour

No tooltip appears when hovering over the upload zone action buttons. Users must infer the button's purpose from context alone.

### Heuristic Reference

- N6 (Recognition Rather Than Recall)
- NOR3 (Mental Models)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1Pvw7ZGO-YaIDe1jShlgRoM6sve-_hUwd/view)

### Suggested Fix

Add `title="Upload thumbnail image"` attributes (HTML native tooltip) or implement a custom tooltip component triggered on `mouseenter` for each upload zone action button.
