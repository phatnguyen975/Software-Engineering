<div align="center">
  <h1>Bug Report — HW03 (GUI & Usability Testing)</h1>
  <small>
    <strong>Student:</strong> Nguyễn Tấn Phát — 23127449
  </small> <br />
  <sub>August 03, 2026</sub>
</div>

# Task 01

## Screen A1 — Events List

> **Source:** `docs/task01/A1/execution-A1.md` · `docs/task01/A1/edge-cases-A1.md`  
> **Tester:** Nguyễn Tấn Phát — 23127449  
> **Date:** 2026-07-28  
> **Total bug groups:** 10  
> **Severity:** Severity 4 (2) · Severity 3 (2) · Severity 2 (4) · Severity 1 (2) · Severity 0 (0)

### BUG-A1-001 — Missing toast notification for Delete, non-standard dialog for Important Update

| Field                   | Value                                      |
| ----------------------- | ------------------------------------------ |
| **ID**                  | BUG-A1-001                                 |
| **Screen**              | A1: Events List                            |
| **Type**                | Bug                                        |
| **Severity**            | 4 — Usability Catastrophe                  |
| **Priority**            | High                                       |
| **Affected Items**      | IA-04-001, IA-04-002, IA-04-003, IA-04-009 |
| **Affected Edge Cases** | None                                       |

#### Description

Feedback for asynchronous actions is inconsistent and violates standard patterns. The "Delete" action provides no feedback whatsoever (silent UI update). The "Important Update" action displays a success notification as a dialog, which does not auto-dismiss after 5 seconds, rather than using a standard toast notification.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Locate any event row in the Events table.
3. **Test Delete:** Click the **Delete** icon, confirm the deletion, and observe the screen for any toast notification.
4. **Test Important Update:** Click the **Important Update** icon, enter important update message, click **Send** in the dialog, and observe the resulting feedback.
5. Notice that Delete triggers no toast, while Important Update triggers a dialog instead of an auto-dismissing toast.

#### Expected Behaviour

A toast notification must appear in a consistent position (e.g., top-right corner) immediately after the deletion/updation completes, with:

- A green background and a checkmark icon for success (e.g., _"Event deleted successfully"_).
- A red background and an error icon for failure (e.g., _"Failed to delete event. Please try again."_).
- The toast must auto-dismiss after ≥ 5 seconds and include a manual `×` dismiss button.

#### Actual Behaviour

Delete action silently updates the table without any success notification. Important Update displays a success dialog (with correct green colour) instead of a toast; however, it does not auto-dismiss after 5 seconds and requires manual closing by the user.

#### Heuristic Reference

- N1 (Visibility of System Status)
- NOR5 (Feedback)
- S5 (Offer Simple Error Handling)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1BiKe5s8T0K0IAMbIc3-Zvyhi1aQy5rFF/view)

#### Suggested Fix

Implement a standardized toast/snackbar component for all CRUD actions.

- **For Delete:** Trigger a green success toast upon successful deletion.
- **For Important Update:** Replace the current success dialog with a standard success toast.
- Ensure all toasts auto-dismiss after 5 seconds (but allow manual dismissal) and include an explicit status icon (e.g., checkmark for success) so they do not rely on color alone.

### BUG-A1-002 — Insufficient colour contrast on white-on-cyan active state (WCAG AA failure)

| Field                   | Value                                               |
| ----------------------- | --------------------------------------------------- |
| **ID**                  | BUG-A1-002                                          |
| **Screen**              | A1: Events List                                     |
| **Type**                | Bug                                                 |
| **Severity**            | 4 — Usability Catastrophe (Accessibility — WCAG AA) |
| **Priority**            | High                                                |
| **Affected Items**      | IA-01-003                                           |
| **Affected Edge Cases** | None                                                |

#### Description

The primary cyan colour used for active/selected state elements (the _Add Event_ button, the currently active pagination page number, and the active sidebar item) renders white text on a cyan background that fails the WCAG AA minimum contrast ratio of 4.5:1 for normal text. This is an accessibility violation that affects all users, especially those with low vision or colour deficiency.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Open **DevTools** → **Elements** panel.
3. Inspect the **"+ Add Event** button, the currently active pagination page number chip, or the active sidebar item.
4. Extract the exact hex (or rgb) values for the text colour (white) and background colour (cyan).
5. Input these values into a standard contrast tool such as the [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/) to calculate the exact contrast ratio.
6. (Optional Verification) Hover over the text colour property directly within the DevTools Elements panel to view the browser's built-in contrast ratio tooltip and cross-reference the result.
7. Observe that the calculated ratio falls significantly below the minimum 4.5:1 threshold.

#### Expected Behaviour

All text–background combinations must achieve a contrast ratio of at least **4.5:1** for normal-sized body text (WCAG 2.2 SC 1.4.3, Level AA). The white text on the active-state cyan background must meet this requirement.

#### Actual Behaviour

The calculated contrast ratio between the white text (`#FFFFFF`) and the active cyan background is exactly **2.08:1**, which fails the WCAG 2.2 Level AA requirement of 4.5:1. This identical failing colour combination affects the _Add Event_ button, the active pagination number chip, and the active sidebar tab.

#### Heuristic Reference

- WCAG 1.4.3 (Contrast — Minimum, Level AA)
- NOR4 (Mappings / Legibility)

#### Evidences

![BUG-A1-002.png](../screenshots/task01/A1/BUG-A1-002.png)

#### Suggested Fix

Update the design system's primary cyan token to ensure sufficient luminance contrast when paired with white text. You have two primary approaches depending on the design language constraints:

- **Approach 1 (Preferred):** Darken the primary cyan background colour to a deeper shade (e.g., teal or dark cyan) until the contrast ratio against white reaches at least 4.5:1.
- **Approach 2:** If the bright cyan background must be preserved, switch the text colour from white to a dark shade (e.g., dark grey or charcoal) to achieve the required 4.5:1 contrast.

Ensure these updated tokens are applied consistently across all active state components (buttons, pagination, sidebar).

### BUG-A1-003 — Layout breaks at 200% zoom and 320 px viewport

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-003                  |
| **Screen**              | A1: Events List             |
| **Type**                | Bug                         |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-01-015                   |
| **Affected Edge Cases** | None                        |

#### Description

The Events List page is not responsive and fails under two standard accessibility stress-test conditions: 200% browser zoom (required for low-vision users) and a 320 px wide viewport (smallest common mobile width). Multiple UI components overflow their containers or disappear entirely, making the screen unusable at these sizes.

#### Steps to Reproduce

**Scenario A — 200% browser zoom:**

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Press **Ctrl + +** (or set browser zoom to 200%) to zoom the page to 200%.
3. Observe the page layout.
4. Note that a **full-page horizontal scrollbar** appears (not limited to the table).
5. Scroll down to the pagination area and observe that the **pagination component overflows** outside its container.

**Scenario B — 320 px mobile viewport:**

1. Open **DevTools** → **Toggle Device Emulation** and set the viewport width to **320 px**.
2. Refresh the page.
3. Observe the following:
   - The **Events table disappears** entirely.
   - The **pagination component** still appears but overflows its container boundaries.
   - The **header elements** (language toggle, notification bell, avatar) shift to the left side, leaving excessive white space on the right.
   - The **search bar, filter dropdowns, and "Add Event" button** row loses consistent alignment, and the "Add Event" button text wraps onto two lines.

#### Expected Behaviour

The page must remain fully functional at both 200% zoom and 320 px viewport width. Specifically:

- The events table should be horizontally scrollable internally (not cause a full-page horizontal scroll).
- All components (search bar, filters, pagination, header) must stay within their containers.
- The layout must stack or reflow gracefully on narrow viewports.

#### Actual Behaviour

At 200% zoom, a full-page horizontal scrollbar appears and the pagination component overflows. At 320 px, the events table disappears completely, header elements misalign, and the action bar layout breaks.

#### Heuristic Reference

- WCAG 1.4.10 (Reflow, Level AA)
- N4 (Consistency and Standards)
- S7 (Strive for Consistency)

#### Evidences

**Scenario A — 200% browser zoom:**

![BUG-A1-003-01.png](../screenshots/task01/A1/BUG-A1-003-01.png)

**Scenario B — 320 px mobile viewport:**

![BUG-A1-003-02.png](../screenshots/task01/A1/BUG-A1-003-02.png)

#### Suggested Fix

Enhance the screen's responsive layout architecture to support both high-zoom and narrow-viewport scenarios:

- **Events Table:** Enclose the `<table>` within a responsive container featuring horizontal scrolling (`overflow-x: auto`). This isolates table scrolling and prevents page-level horizontal scrollbars.
- **Action Bar (Filters & Add Button):** Implement a flex-wrap or CSS Grid layout that gracefully stacks the search bar, filter dropdowns, and "Add Event" button vertically on viewports below 480px.
- **Pagination Component:** Ensure the pagination container utilizes `flex-wrap: wrap` so that page number chips flow onto the next line rather than overflowing their parent container.
- **Header Elements:** Configure the header flex container to wrap its child elements (language toggle, notifications, avatar) to maintain alignment when space is constrained.

### BUG-A1-004 — Rich Text Editor: Multiple interaction defects in "Important Update" dialog

| Field                   | Value                           |
| ----------------------- | ------------------------------- |
| **ID**                  | BUG-A1-004                      |
| **Screen**              | A1: Events List                 |
| **Type**                | Bug                             |
| **Severity**            | 3 — Major Usability Problem     |
| **Priority**            | High                            |
| **Affected Items**      | IA-02-012, IA-02-013, IA-02-014 |
| **Affected Edge Cases** | None                            |

#### Description

The Rich Text Editor (RTE) inside the "Important Update" dialog has three distinct interaction defects: (1) certain toolbar buttons (paragraph, text alignment) close the dialog immediately on click instead of applying formatting; (2) after uploading an image into the editor, there is no control to remove it; and (3) pasting a very long unbroken string of text causes the editor content to overflow the dialog boundaries instead of wrapping or scrolling.

#### Steps to Reproduce

**Defect 1 — Toolbar buttons unexpectedly close the dialog:**

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Locate any event row and click the **Important Update** icon in the ACTIONS column.
3. Wait for the "Important Update" dialog to open.
4. In the RTE toolbar, click the **Paragraph** or **Text Alignment** (left/center/right) formatting buttons.
5. Observe that the dialog **closes immediately** without applying any formatting.

**Defect 2 — No way to remove an inserted image:**

1. Open the "Important Update" dialog (same steps as above).
2. Click the **Image** icon in the RTE toolbar to upload an image.
3. Select any image file from the system file picker.
4. Observe that a preview of the image appears inside the editor.
5. Attempt to click on the image and look for a **Remove** button or icon overlay.
6. Observe that **no control exists** to remove the image.

**Defect 3 — Long text overflows the dialog:**

1. Open the "Important Update" dialog.
2. Click inside the RTE text area.
3. Paste a continuous block of text with no line breaks (minimum ~500 characters).
4. Continue adding more text.
5. Observe that the content **overflows past the dialog's bottom edge** instead of revealing an internal scrollbar or expanding gracefully.

#### Expected Behaviour

- Toolbar buttons must apply formatting to selected text without dismissing the dialog.
- After inserting an image, a **Remove** button or icon overlay must appear to let the user delete it before submitting.
- When content exceeds the editor's visible height, the RTE area must either expand up to a defined maximum height and then switch to an internal vertical scrollbar, or always provide an internal scrollbar.

#### Actual Behaviour

- Clicking Paragraph / Alignment buttons closes the dialog immediately.
- No mechanism exists to remove an uploaded image from the editor.
- Pasting long text causes the editor content to overflow the dialog without scrolling.

#### Heuristic Reference

- N1 (Visibility of System Status)
- N3 (User Control and Freedom)
- N9 (Help Users Recognise, Diagnose, and Recover from Errors)
- NOR5 (Feedback)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/17249yW5i1HOloGnQjsWmu_tUUIRIVu2I/view)

#### Suggested Fix

Address the three distinct interaction defects within the Rich Text Editor integration:

- **Toolbar Close Bug:** The modal's backdrop click-listener is improperly intercepting clicks from the RTE toolbar. Ensure that click events on toolbar buttons call `event.stopPropagation()`, or bind the dialog dismissal strictly to explicit `close` buttons and the immediate backdrop overlay layer rather than bubbling events.
- **Image Removal Control:** Enhance the image insertion feature by appending a persistent, overlaying "Remove" (`×`) button on all uploaded image nodes within the editor, granting users the freedom to undo accidental uploads before submission.
- **Content Overflow:** Constrain the editor's vertical growth by setting a `max-height` (e.g., `300px` or a relative viewport height unit) on the RTE content container, coupled with `overflow-y: auto` to enable an internal vertical scrollbar when content exceeds the boundary.

### BUG-A1-005 — Inconsistent badge border styling across table columns

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-005                  |
| **Screen**              | A1: Events List             |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-04-004                   |
| **Affected Edge Cases** | None                        |

#### Description

Status badges in the **STATUS** and **TIME STATUS** columns use a border that is darker than the badge background, giving them a visually distinct outline. In contrast, badges in the **TYPE** and **PUBLIC** columns have no border, creating an inconsistent visual language for status indicators across the same screen.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Ensure at least one event row with data in all badge columns is visible.
3. Observe the badges side-by-side:
   - **STATUS** column (e.g., _Published_) — note the border.
   - **TIME STATUS** column (e.g., _Upcoming_) — note the border.
   - **TYPE** column (e.g., _Limited_) — note the absence of a border.
   - **PUBLIC** column (e.g., _Not Public_) — note the absence of a border.
4. Confirm that **STATUS / TIME STATUS** badges have a visible border while **TYPE / PUBLIC** badges do not.

#### Expected Behaviour

All badge/chip components across the Events table should share a **unified styling system** — either all have borders or none do, with consistent padding, border-radius, and font weight regardless of which column they appear in.

#### Actual Behaviour

Badges in STATUS and TIME STATUS columns have a visible border; badges in TYPE and PUBLIC columns do not, resulting in a visually inconsistent table row.

#### Heuristic Reference

- N4 (Consistency and Standards)
- S1 (Strive for Consistency)

#### Evidences

![BUG-A1-005.png](../screenshots/task01/A1/BUG-A1-005.png)

#### Suggested Fix

Establish and apply a unified styling convention for all status indicator badges across the table.

- If the design system intends for badges to possess an outlining border, update the DOM structure or CSS classes for the `TYPE` and `PUBLIC` badges to inherit those border properties.
- Conversely, if the borderless style is intended, strip the border styles from the `STATUS` and `TIME STATUS` badges.
- Consolidate the CSS classes or React/Vue component variants into a single, reusable `<Badge>` component that standardises padding, border-radius, font-weight, and border presence regardless of the column context.

### BUG-A1-006 — Filter dropdowns missing chevron affordance indicator

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-006                  |
| **Screen**              | A1: Events List             |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-02-006                   |
| **Affected Edge Cases** | None                        |

#### Description

The **"All Status"** and **"All Time"** filter buttons on the Events List screen look and behave as dropdown menus but display no chevron or downward-arrow icon. Without this standard affordance, first-time users may not recognise that clicking these buttons opens a selection list.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Look at the filter area below the page heading — observe the **"All Status"** and **"All Time"** buttons.
3. Inspect both buttons visually — note the absence of any arrow icon.
4. For comparison, click both buttons to confirm they do open a dropdown list.

#### Expected Behaviour

Dropdown trigger buttons must include a visible **downward chevron** or equivalent directional icon to signal that clicking will reveal a list of options, following standard affordance conventions.

#### Actual Behaviour

Both filter buttons display only text. No arrow or chevron icon is present, offering no visual cue that the element is a dropdown.

#### Heuristic Reference

- NOR2 (Signifiers)
- N6 (Recognition Rather Than Recall)
- N4 (Consistency and Standards)

#### Evidences

![BUG-A1-006.png](../screenshots/task01/A1/BUG-A1-006.png)

#### Suggested Fix

Introduce a clear visual signifier to explicitly communicate the dropdown affordance.

- Append a downward-pointing chevron icon (e.g., an SVG graphic or a corresponding icon font glyph) adjacent to the text labels of both the "All Status" and "All Time" filter buttons.
- For enhanced feedback, implement a subtle CSS transition that rotates the chevron 180 degrees upwards when the dropdown menu is in an open/expanded state.

### BUG-A1-007 — Sidebar collapsed state: Icon-only items have no tooltips

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-007                  |
| **Screen**              | A1: Events List             |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-03-003                   |
| **Affected Edge Cases** | None                        |

#### Description

When the left sidebar is collapsed, menu items are displayed as icon-only buttons. Hovering over any of these icons does not reveal a tooltip showing the section name. Users unfamiliar with the icon set cannot identify which section each icon navigates to without expanding the sidebar.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Locate the **Collapse** button at the bottom of the left sidebar.
3. Click it to collapse the sidebar — menu labels disappear, only icons remain.
4. Move the mouse cursor over each icon (e.g., the calendar icon for Events Management, the user icon for Users Management).
5. Wait 1–2 seconds while hovering on each icon.
6. Observe that **no tooltip appears** next to or below the hovered icon.

#### Expected Behaviour

When the sidebar is in collapsed/icon-only mode, hovering over each icon must display a tooltip (e.g., _"Event Management"_) to identify the section without requiring the sidebar to be re-expanded.

#### Actual Behaviour

Hovering over icons in the collapsed sidebar produces only a visual highlight with no tooltip text.

#### Heuristic Reference

- N6 (Recognition Rather Than Recall)
- N4 (Consistency and Standards)
- NOR2 (Signifiers)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1vkOPLtZ5V3BZakE7xKdP-aHe34QWDWmI/view)

#### Suggested Fix

Implement progressive disclosure tooltips for the collapsed sidebar navigation state.

- Attach a tooltip component (via CSS-only techniques or a JavaScript library) to every icon-only navigation item in the sidebar.
- Configure the tooltips to display the full, localized section names (e.g., "Event Management") upon a `mouseenter` or `focus` event, ideally with a brief debounce delay (e.g., 200–300ms) to prevent flickering during rapid cursor movement.
- Ensure the tooltip positioning logic anchors them to the right of the respective icons, guaranteeing they do not obscure the icon itself.

### BUG-A1-008 — LOCATION column: Map pin icon not vertically aligned with address text

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A1-008           |
| **Screen**              | A1: Events List      |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-01-017            |
| **Affected Edge Cases** | None                 |

#### Description

In the Events table, the map pin icon that precedes the location address text in the **LOCATION** column is not vertically centred with the text on the same line, creating a slight visual misalignment that reduces the polish of the interface.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Scroll right in the Events table to locate the **LOCATION** column.
3. Find a row that has a location address.
4. Visually inspect the alignment between the **map pin icon** and the adjacent **address text**.
5. Observe that the icon sits above the vertical midpoint of the text.

#### Expected Behaviour

The map pin icon and the address text must be **vertically centred** on the same horizontal axis (middle-aligned), giving the cell a clean, unified appearance.

#### Actual Behaviour

The map pin icon is visually offset from the address text's vertical centre, resulting in a jagged or uneven appearance within the table cell.

#### Heuristic Reference

- N4 (Consistency and Standards)
- S1 (Strive for Consistency)

#### Evidences

![BUG-A1-008.png](../screenshots/task01/A1/BUG-A1-008.png)

#### Suggested Fix

Correct the vertical alignment mismatch within the Location column cells.

- Transform the container holding the map pin icon and the address text into a Flexbox row (`display: flex`) and apply `align-items: center` to guarantee perfect horizontal axis alignment.
- If avoiding Flexbox, ensure both the icon vector/font and the adjacent text span share matching `vertical-align: middle` and `line-height` properties.

### BUG-A1-009 — "Add Event" button missing active/pressed visual state

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A1-009           |
| **Screen**              | A1: Events List      |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-04-014            |
| **Affected Edge Cases** | None                 |

#### Description

The primary **"+ Add Event"** button correctly shows a hover state when the cursor enters it, but does not provide any active/pressed visual feedback when the button is physically clicked and held. Other interactive elements on the page (table rows, pagination buttons) respond to click-hold, but this primary action button does not.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Move the mouse over the **"+ Add Event"** button (top-right area) — verify the hover state (colour change) is present.
3. Click and **hold** the mouse button down on the **"+ Add Event"** button without releasing.
4. Observe the button's visual state while the mouse button is held.
5. Release the mouse.
6. Observe that no **active / sunken / ripple** visual effect appeared during the click-hold.

#### Expected Behaviour

While the mouse button is held down on the "Add Event" button, a distinct **active/pressed visual state** must be visible — e.g., a slightly darker shade, an inset/sunken shadow, or a Material-style ripple effect — to confirm to the user that their click has been registered.

#### Actual Behaviour

No visual change occurs when the button is clicked and held. The hover state is the same as the pressed state, providing no confirmation that the click input was received.

#### Heuristic Reference

- N1 (Visibility of System Status)
- NOR5 (Feedback)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1A2L2xoxe8xblIDdqQTaHwE6UOhjPVkSq/view)

#### Suggested Fix

Provide immediate, tactile visual feedback when the primary action button is clicked.

- Introduce an active (pressed) visual state to the "Add Event" button utilizing the CSS `:active` pseudo-class (or equivalent framework utility variant).
- The styling adjustment should clearly differentiate a click from a hover — for instance, by briefly darkening the background colour further, applying an inset box-shadow to simulate depth, or implementing a ripple animation effect.

### BUG-A1-010 — Dropdown layout and sizing defects (Overflow and excessive whitespace)

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-010                  |
| **Screen**              | A1: Events List             |
| **Type**                | Bug                         |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | None                        |
| **Affected Edge Cases** | EC-A1-006                   |

#### Description

The dropdown menus used for table column filters exhibit noticeable layout anomalies. Specifically, the EVENT TYPES filter dropdown fails to correctly constrain its internal components, causing the search input and action buttons to overflow past the right boundary. Additionally, the TIME and ACADEMIC CONTEXT dropdowns render with excessive empty whitespace, indicating improperly configured or hardcoded container dimensions.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. In the Events table header, click the filter icon next to **EVENT TYPES**.
3. Observe the search input and Apply/Clear buttons overflowing the dropdown container's right edge.
4. Close the dropdown and click the filter icon next to **TIME** and then **ACADEMIC CONTEXT**.
5. Observe the excessive empty space within these dropdown containers.

#### Expected Behaviour

Dropdown containers must adapt dynamically to their content with appropriate constraints (e.g., `min-width`, `max-width`, `max-height`). Internal elements must remain strictly within the container bounds without overflowing, and the container footprint should be snug without unjustified or excessive empty whitespace.

#### Actual Behaviour

The EVENT TYPES content overflows its container boundaries horizontally. The TIME and ACADEMIC CONTEXT containers render with excessive empty vertical/horizontal space.

#### Heuristic Reference

- N4 (Consistency and Standards)
- N8 (Aesthetic and Minimalist Design)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1p0D4ieoHwJtt9UIB3yzIAW6_wmtwTbs_/view)

#### Suggested Fix

Review and refine the CSS architecture governing the layout of the filter dropdown containers and their descendants.

- **For Overflow (EVENT TYPES):** Ensure the parent dropdown container possesses a sufficient `min-width` to accommodate its child elements, or configure the child elements (like the search input) to utilize `width: 100%` alongside `box-sizing: border-box` to prevent them from breaking out of the parent bounds.
- **For Excessive Whitespace (TIME, ACADEMIC CONTEXT):** Remove any hardcoded `height` or `width` properties that force the container to be artificially large. Allow the container to size dynamically based on its inner content, utilizing `max-height` coupled with `overflow-y: auto` exclusively for constraining long list areas.

## Screen A2 — Add / Edit Event Form

> **Source:** `docs/task01/A2/execution-A2.md` · `docs/task01/A2/edge-cases-A2.md`  
> **Tester:** Nguyễn Tấn Phát — 23127449  
> **Date:** 2026-07-29  
> **Total bug groups:** 11  
> **Severity:** Severity 4 (1) · Severity 3 (3) · Severity 2 (5) · Severity 1 (2) · Severity 0 (0)

### BUG-A2-001 — Inline Validation Does Not Fire on Field Blur; Error Styling Inconsistent on Submit

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-001                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-02-003, IA-02-005        |
| **Affected Edge Cases** | None                        |

#### Description

Required fields (Event Title, Date pickers, Campus) do not trigger inline error messages when the user tabs out of an empty field. Additionally, when the form is submitted blank, the error styling is inconsistently applied — only the Campus field highlights its border in red and show plain text error message, while all other required fields only show plain text error messages beneath them without border highlighting.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Click into the **Event Title** field, leave it empty, then press Tab to move focus to the next field.
3. **Observe:** No inline error message or error styling appears.
4. Click **Publish** with all required fields empty.
5. **Observe:** Error text messages appear below all required fields, but only the **Campus** field additionally highlights its border in red; all other fields (Event Title, Start Date, etc.) receive no border change.

#### Expected Behaviour

- An inline error message should appear immediately when a required field loses focus (blur event) while empty.
- After a failed submission, all required field errors should be presented with visually consistent styling (e.g., all borders turn red, or none do).

#### Actual Behaviour

- No error appears on blur; validation only fires on form submission.
- On failed submission, Campus renders both red border and error text, while all other required fields render only error text without a red border — creating an inconsistent error state across the form.

#### Heuristic Reference

- N9 (Help users recognize, diagnose, and recover from errors)
- S4 (Consistency)
- WCAG 3.3.1 (Error Identification)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/17gOV13Vsbbi7yEycebGGfgRJIDmraIAo/view)

#### Suggested Fix

- Attach a `blur` event listener to all required fields that triggers inline validation immediately.
- Normalize the error state CSS across all required field types (inputs, dropdowns, date pickers) so that a failed validation consistently applies a red border to all of them.

### BUG-A2-002 — Required Field Asterisk (`*`) Is Black Instead of Red

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A2-002           |
| **Screen**              | A2: Add / Edit Event |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-02-001            |
| **Affected Edge Cases** | None                 |

#### Description

The asterisk (`*`) displayed next to required field labels is styled in black (same colour as the label text), making it ineffective at visually distinguishing mandatory fields from optional ones.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Inspect the label of the **Event Title** field and any date/time picker label.
3. **Observe:** The `*` symbol is rendered in black, matching the label colour.

#### Expected Behaviour

The required field asterisk (`*`) should be rendered in red (`#dc2626` or equivalent) to be immediately distinguishable from the label text, following standard web form convention.

#### Actual Behaviour

The asterisk (`*`) is rendered in black (same colour as surrounding label text), providing no visual distinction.

#### Heuristic Reference

- N4 (Consistency and Standards)
- S4 (Consistency)

#### Evidences

![BUG-A2-002.png](../screenshots/task01/A2/BUG-A2-002.png)

#### Suggested Fix

Apply `color: red` (or `text-[red]` in Tailwind) to the `<span>` or character wrapping the asterisk in all required field labels.

### BUG-A2-003 — Date/Time Picker: Keyboard Input Blocked and Popup Hidden When Near Screen Bottom

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-003                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Bug                         |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-02-008                   |
| **Affected Edge Cases** | EC-A2ADD-003                |

#### Description

Date/time picker fields have two distinct defects: (1) the field does not accept keyboard text input — users can only interact via the calendar icon; (2) when the date picker is opened near the bottom of the viewport, the calendar popup is clipped/hidden instead of flipping to open upward.

#### Steps to Reproduce

**Keyboard Accessibility:**

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Tab to the **Start Date & Time** field.
3. Attempt to type a date directly (e.g., `07292026`).
4. **Observe:** No text input is accepted; the field is read-only.

**Popup Overflow:**

1. Scroll to the **Registration** section.
2. Click the **Registration Close** date picker icon.
3. **Observe:** The calendar popup opens downward but is partially or fully hidden beyond the viewport bottom edge instead of flipping upward.

#### Expected Behaviour

- Users should be able to type a date/time value directly into the field.
- The calendar popup should detect its proximity to the viewport edge and intelligently open in the opposite direction (upward) to remain fully visible.

#### Actual Behaviour

- Keyboard input to date fields is entirely blocked; only the calendar icon click works.
- Calendar popups near the viewport bottom are clipped/hidden rather than flipping upward.

#### Heuristic Reference

- N7 (Flexibility and Efficiency of Use)
- WCAG 2.1.1 (Keyboard)
- NOR3 (Mental Models)

#### Evidences

![BUG-A2-003.png](../screenshots/task01/A2/BUG-A2-003.png)

#### Suggested Fix

- Remove the `readOnly` or `pointer-events: none` attribute from the date input element to enable keyboard entry with appropriate masking.
- Configure the date picker library (e.g., `placement: "auto"` or `flip: true`) so the popup auto-detects viewport boundaries and flips to open upward when needed.

### BUG-A2-004 — Drag-and-Drop File Upload Not Functional Despite UI Text Claiming Support

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-004                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Bug                         |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-02-010                   |
| **Affected Edge Cases** | None                        |

#### Description

All three file upload zones (Thumbnail, Banner, Attachments) fail to respond to OS-level file drag-and-drop, despite the Attachments zone explicitly displaying the text "Drag and drop files here or click to select". No visual feedback is provided when a file is dragged over the zones.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Open a file manager and select any image file.
3. Drag the image over the **Thumbnail** dropzone.
4. **Observe:** No border highlight, background change, or any visual cue appears.
5. Release the file over the zone.
6. **Observe:** The file is not uploaded; nothing happens.
7. Repeat for the **Banner** and **Attachments** zones.

#### Expected Behaviour

Each dropzone should visually change state (e.g., highlighted border, background colour change) when a file is dragged over it, and should accept and process the dropped file.

#### Actual Behaviour

Dragging a file over any zone produces zero visual feedback and drops the file without processing it. The Attachments zone label text "Drag and drop files here" is misleading.

#### Heuristic Reference

- N1 (Visibility of System Status)
- N4 (Consistency and Standards)
- NOR5 (Affordances)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/18H_htLzPb21Vupiw2h6ljCv9pGUW3bSI/view)

#### Suggested Fix

- Implement `dragover`, `dragenter`, and `drop` event listeners on all three upload zones.
- On `dragenter`, apply an active state class (e.g., `ring-2 ring-blue-500 bg-blue-50`) to provide immediate visual feedback.
- If drag-and-drop is not yet implemented for Thumbnail/Banner, remove or correct the "Drag and drop" instructional text from the Attachments zone to avoid misleading users.

### BUG-A2-005 — Rich Text Editor: Uploaded Image Cannot Be Removed; Editor Expands Infinitely Without Internal Scrollbar

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-005                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-02-013, IA-02-014        |
| **Affected Edge Cases** | None                        |

#### Description

The Rich Text Editor (RTE) used for the event Description field has two related UX defects: (1) after inserting an image via the RTE toolbar, there is no Remove/Trash control to delete it; (2) when large amounts of content are entered, the editor expands its height infinitely, pushing the Date & Time section far down the page with no internal scrollbar.

#### Steps to Reproduce

**No Remove Control:**

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Click into the **Description** RTE.
3. Use the toolbar image insertion button to upload an image.
4. **Observe:** The image is inserted and previewed, but no Remove/Trash icon appears adjacent to it.

**Infinite Height Expansion:**

1. In the Description RTE, paste a block of 50+ lines of text.
2. **Observe:** The RTE expands its container height to accommodate all content without limit.
3. To fill out the Date & Time fields, the user must scroll far down the page.

#### Expected Behaviour

- An image inserted into the RTE should have an accessible Remove button (e.g., a trash icon overlay) to allow it to be deleted.
- The RTE should have a defined maximum height, after which an internal scrollbar appears, preventing the editor from pushing subsequent form sections off-screen.

#### Actual Behaviour

- Uploaded images inside the RTE cannot be removed through the UI.
- The RTE height grows unboundedly, creating significant navigation friction for long descriptions.

#### Heuristic Reference

- N3 (User Control and Freedom)
- N8 (Aesthetic and Minimalist Design)
- NOR3 (Mental Models)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1kNvz6gb9aDC3-VqQxozsjrZDybPYhqt_/view)

#### Suggested Fix

- For the image removal issue: Add a click handler and remove button overlay on RTE-inserted images.
- For the infinite expansion: Apply `max-height: 400px; overflow-y: auto;` (or equivalent) to the RTE content container to introduce an internal scrollbar.

### BUG-A2-006 — No Toast Notification Displayed After Save as Draft or Publish

| Field                   | Value                           |
| ----------------------- | ------------------------------- |
| **ID**                  | BUG-A2-006                      |
| **Screen**              | A2: Add / Edit Event            |
| **Type**                | Usability                       |
| **Severity**            | 3 — Major Usability Problem     |
| **Priority**            | High                            |
| **Affected Items**      | IA-04-001, IA-04-002, IA-04-003 |
| **Affected Edge Cases** | None                            |

#### Description

After clicking **Save as Draft** or **Publish**, no toast notification is displayed to confirm the action. The application silently redirects the user to the Event List screen, providing no explicit success/failure feedback before navigation.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Fill in all required fields with valid data.
3. Click **Save as Draft**.
4. **Observe:** The page immediately redirects to the Event List. No toast message appears.
5. Repeat steps 1–3 and click **Publish** instead.
6. **Observe:** Same behavior — silent redirect, no success notification.

#### Expected Behaviour

After clicking Save as Draft or Publish, a success toast notification should appear briefly (≥ 5 seconds with auto-dismiss and manual dismiss button), using appropriate colour semantics (green for success), before or after the redirect, to confirm the action was completed.

#### Actual Behaviour

The application silently redirects to Event List with no toast, no snackbar, and no inline confirmation message. Users have no explicit confirmation that their action succeeded beyond the redirect itself.

#### Heuristic Reference

- N1 (Visibility of System Status)
- S5 (Informative Feedback)
- NOR6 (Feedback Loops)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1IwE7Uh4a01u5SV0-OTaEFpFgo3BZNhH5/view)

#### Suggested Fix

Implement a toast notification system. After a successful save or publish, trigger a green success toast (e.g., "Event saved as draft" or "Event published successfully") that auto-dismisses after 5 seconds and includes a manual dismiss button.

### BUG-A2-007 — Campus Dropdown Missing Arrow Indicator

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A2-007           |
| **Screen**              | A2: Add / Edit Event |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-02-006            |
| **Affected Edge Cases** | None                 |

#### Description

The **Campus** dropdown field displays only a placeholder text ("Select campus") without any visible dropdown arrow indicator, making it non-obvious to users that the element is a clickable dropdown rather than a read-only text display.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Scroll to the **Location & Organization** section.
3. Observe the **Campus** field.
4. **Observe:** The field shows "Select campus" text but has no visible chevron/arrow icon on the right side.

#### Expected Behaviour

Dropdown fields should display a visible arrow/chevron indicator on their right edge to communicate that the element is interactive and expandable.

#### Actual Behaviour

The Campus dropdown field displays no arrow indicator, appearing as a plain text label rather than a selectable dropdown control.

#### Heuristic Reference

- N4 (Consistency and Standards)
- NOR5 (Affordances)

#### Evidences

![BUG-A2-007.png](../screenshots/task01/A2/BUG-A2-007.png)

#### Suggested Fix

Add a `<ChevronDownIcon>` (or CSS `::after` arrow) to the right side of the Campus `<select>` element, consistent with the styling of the Event Types and Academic Context dropdown buttons.

### BUG-A2-008 — Publish Button Text Contrast Ratio Fails WCAG AA

| Field                   | Value                     |
| ----------------------- | ------------------------- |
| **ID**                  | BUG-A2-008                |
| **Screen**              | A2: Add / Edit Event      |
| **Type**                | Bug                       |
| **Severity**            | 4 — Usability Catastrophe |
| **Priority**            | High                      |
| **Affected Items**      | IA-01-003                 |
| **Affected Edge Cases** | None                      |

#### Description

The text label on the **Publish** button has a colour contrast ratio of 2.08:1 against its background, which is far below the WCAG 2.1 AA minimum requirement of 4.5:1 for normal-sized text. This constitutes a legal accessibility violation.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Scroll to the bottom of the form to locate the **Publish** button.
3. Open DevTools → inspect the button text colour and background colour.
4. Calculate or use a contrast checker: contrast ratio = **2.08:1**.
5. Compare against WCAG AA minimum: **4.5:1** (normal text).

#### Expected Behaviour

The Publish button text must achieve a minimum contrast ratio of 4.5:1 against its button background colour to comply with WCAG 2.1 Level AA (Success Criterion 1.4.3 Contrast (Minimum)).

#### Actual Behaviour

The Publish button text-to-background contrast ratio is 2.08:1 — approximately 2.2× below the minimum requirement.

#### Heuristic Reference

- WCAG 1.4.3 (Contrast Minimum — Level AA)
- N4 (Consistency and Standards)

#### Evidences

![BUG-A2-008.png](../screenshots/task01/A2/BUG-A2-008.png)

#### Suggested Fix

Darken the button text colour or lighten/darken the background colour until the contrast ratio reaches at least 4.5:1. For example, if the current background is `#1bc2f5` (light blue), use a dark text colour such as `#0a4f6b` or switch to white text `#ffffff` which achieves approximately 3.1:1 — if white is used, the background must also be darkened (e.g., `#0e86ab`) to reach 4.5:1.

### BUG-A2-009 — Sub-description Textarea Line Height Below Required 1.5× Ratio

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-009                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-01-007                   |
| **Affected Edge Cases** | None                        |

#### Description

The **Sub-description** textarea has a computed line height of 20px with a font size of 14px, giving a ratio of 1.43 — below the WCAG 1.4.12 minimum of 1.5× required for body text readability.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Click into the **Sub-description** textarea and type multiple lines of text.
3. Open DevTools → Inspect the textarea element.
4. Check computed `line-height` (20px) and `font-size` (14px).
5. Calculate ratio: 20 ÷ 14 = **1.43** (below the 1.5 threshold).

#### Expected Behaviour

Line height should be at least 1.5× the font size for body/paragraph text, per WCAG 1.4.12 (Text Spacing). For 14px font, the minimum line height should be **21px**.

#### Actual Behaviour

Line height is 20px for 14px text, giving a ratio of 1.43×, making multi-line text appear cramped.

#### Heuristic Reference

- WCAG 1.4.12 (Text Spacing)
- N8 (Aesthetic and Minimalist Design)

#### Evidences

![BUG-A2-009.png](../screenshots/task01/A2/BUG-A2-009.png)

#### Suggested Fix

Update the textarea CSS to set `line-height: 1.5` (or `line-height: 21px`). Apply this consistently to the Sub-description textarea and any other body-level text inputs on the form.

### BUG-A2-010 — Layout Breaks and Sidebar Overflows at 320px Viewport Width

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-010                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Bug                         |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-01-015                   |
| **Affected Edge Cases** | None                        |

#### Description

When the browser viewport is narrowed to 320px width, the application layout breaks: the sidebar is not collapsed into a hamburger/toggle button, causing the sidebar and form content to overflow their containers chaotically, and making the page unusable on small mobile viewports.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Open DevTools → set viewport width to **320px**.
3. **Observe:** The sidebar remains at full width and does not collapse.
4. **Observe:** Form sections overflow and stack chaotically, content extends beyond the viewport width.

#### Expected Behaviour

At 320px viewport width, the sidebar should collapse into a hidden state accessible via a hamburger/toggle button, and the form content should reflow into a single-column layout that fits within the viewport without horizontal scrolling.

#### Actual Behaviour

The sidebar remains expanded and overlaps the form content. Layout containers lose their responsive behaviour and overflow horizontally, rendering the page unusable without horizontal scrolling.

#### Heuristic Reference

- N4 (Consistency and Standards)
- WCAG 1.4.10 (Reflow)
- S5 (Universal Usability)

#### Evidences

![BUG-A2-010.png](../screenshots/task01/A2/BUG-A2-010.png)

#### Suggested Fix

- Implement a responsive breakpoint (e.g., `< 768px`) at which the sidebar collapses to a toggle-activated off-canvas menu.
- Ensure all form grid layouts use `flex-wrap` or single-column responsive grids (`grid-cols-1`) at mobile breakpoints.

### BUG-A2-011 — No Tooltip on Upload Zone Action Buttons

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-011                  |
| **Screen**              | A2: Add / Edit Event        |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-04-015                   |
| **Affected Edge Cases** | None                        |

#### Description

The action buttons (edit/upload icons) overlaid on the Thumbnail, Banner, and Attachments upload zones do not display any tooltip on hover, leaving users without clear feedback about what action those icon-only buttons will perform.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create.
2. Hover the mouse cursor over the edit/action button in the **Thumbnail** upload zone.
3. Wait for approximately 1–2 seconds.
4. **Observe:** No tooltip appears.
5. Repeat for the **Banner** upload zone action button.

#### Expected Behaviour

Icon-only buttons should display a descriptive text tooltip on hover (e.g., "Upload Thumbnail", "Upload Banner") to clearly communicate their function to users.

#### Actual Behaviour

No tooltip appears when hovering over the upload zone action buttons. Users must infer the button's purpose from context alone.

#### Heuristic Reference

- N6 (Recognition Rather Than Recall)
- NOR3 (Mental Models)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1Pvw7ZGO-YaIDe1jShlgRoM6sve-_hUwd/view)

#### Suggested Fix

Add `title="Upload thumbnail image"` attributes (HTML native tooltip) or implement a custom tooltip component triggered on `mouseenter` for each upload zone action button.

## Screen A4 — Participants & Reviews Approval

> **Source:** `docs/task01/A4/execution-A4.md` · `docs/task01/A4/edge-cases-A4.md`  
> **Tester:** Nguyễn Tấn Phát — 23127449  
> **Date:** 2026-07-30  
> **Total bug groups:** 7  
> **Severity:** Severity 4 (2) · Severity 3 (2) · Severity 2 (1) · Severity 1 (2) · Severity 0 (0)

### BUG-A4-001 — Toast notifications are completely absent after Apply actions

| Field                   | Value                                                 |
| ----------------------- | ----------------------------------------------------- |
| **ID**                  | BUG-A4-001                                            |
| **Screen**              | A4: Participants & Reviews Approval                   |
| **Type**                | Bug                                                   |
| **Severity**            | 3 — Major Usability Problem                           |
| **Priority**            | High                                                  |
| **Affected Items**      | IA-04-001, IA-04-002, IA-04-003, IA-04-009, IA-04-010 |
| **Affected Edge Cases** | None                                                  |

#### Description

No toast notification appears at all when the Admin clicks the Apply button in the Review Lecturers or Review Students tabs — whether the action succeeds or fails. The table state updates silently (rows are removed upon approval), but there is no visual confirmation to the user that anything happened.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Click the **Review Students** tab.
3. Change one student's ACTION segmented control from Pending to Approved.
4. Click the **Apply** button.
5. Observe the page — the student row disappears, but no toast notification appears anywhere on the screen.
6. To test error feedback: open DevTools → Network → block the Apply API endpoint.
7. Click **Apply** again. Observe that no error toast appears.

#### Expected Behaviour

- **On success:** a green success toast appears in a consistent screen position (e.g., top-right), auto-dismisses after ≥ 5 seconds, and includes a dismiss (×) button.
- **On failure:** a red error toast appears, stays for ≥ 10 seconds (or does not auto-dismiss), with a user-friendly message and no raw stack trace.

#### Actual Behaviour

No toast notification of any kind appears (success or error). The UI state updates silently, giving the admin zero visual confirmation that the Apply action was processed, succeeded, or failed.

#### Heuristic Reference

- N1 (Visibility of System Status)
- NOR5 (Feedback)
- S5 (Offer Simple Error Handling)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1pNbDn3dsqkO46pjmrAdqpxvAXMA3USl-/view)

#### Suggested Fix

Implement a toast/notification library call on the Apply button's API response handler. Trigger a green success toast on `200 OK` and a red error toast on any non-2xx response. Ensure the toast system is globally connected and not conditionally disabled for this screen.

### BUG-A4-002 — Text contrast ratio fails WCAG AA for inactive tab labels

| Field                   | Value                                     |
| ----------------------- | ----------------------------------------- |
| **ID**                  | BUG-A4-002                                |
| **Screen**              | A4: Participants & Reviews Approval       |
| **Type**                | Bug                                       |
| **Severity**            | 4 — Usability Catastrophe (Accessibility) |
| **Priority**            | High                                      |
| **Affected Items**      | IA-01-003                                 |
| **Affected Edge Cases** | None                                      |

#### Description

The text color of inactive (non-selected) tab labels on the A4 screen does not meet the WCAG 2.2 Level AA minimum contrast ratio of 4.5:1 for normal text. Users with low vision may be unable to read inactive tab labels clearly.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Open DevTools (F12).
3. Click on one of the non-active tab labels (e.g., "Review Lecturers" or "Review Students" when not selected).
4. Use the DevTools color picker / Elements panel to inspect the computed text color and background color.
5. Verify the contrast ratio — it fails to meet the AA standard (4.5:1).

#### Expected Behaviour

All visible text, including inactive tab labels, must achieve a minimum contrast ratio of 4.5:1 against the background (WCAG 2.2 SC 1.4.3).

#### Actual Behaviour

The inactive tab text uses a muted color that does not meet the 4.5:1 ratio threshold against the tab bar background.

#### Heuristic Reference

- WCAG1.4.3 (Contrast Minimum — Level AA)
- N4 (Consistency and Standards)

#### Evidences

![BUG-A4-002.png](../screenshots/task01/A4/BUG-A4-002.png)

#### Suggested Fix

Increase the inactive tab text color to a darker shade that achieves at least a 4.5:1 contrast ratio against the tab bar background. Use a tool such as the [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/) to verify the new color before applying.

### BUG-A4-003 — Line-height is below the WCAG 1.4.12 minimum of 1.5×

| Field                   | Value                                     |
| ----------------------- | ----------------------------------------- |
| **ID**                  | BUG-A4-003                                |
| **Screen**              | A4: Participants & Reviews Approval       |
| **Type**                | Bug                                       |
| **Severity**            | 4 — Usability Catastrophe (Accessibility) |
| **Priority**            | High                                      |
| **Affected Items**      | IA-01-007                                 |
| **Affected Edge Cases** | None                                      |

#### Description

Body text on this screen has a computed `line-height` of 20px against a `font-size` of 14px, yielding a ratio of approximately 1.43. This falls below the WCAG 2.2 SC 1.4.12 (Text Spacing) requirement that line height must be at least 1.5 times the font size.

#### Steps to Reproduce

1. Navigate to the A4 screen and open the Registrants tab.
2. Open DevTools → Elements → select a body text cell (e.g., an email address value).
3. In the Computed Styles panel, read the `font-size` and `line-height` values.
4. Calculate the ratio: `line-height / font-size` = `20 / 14` ≈ 1.43.
5. Verify this is below the required minimum of 1.5.

#### Expected Behaviour

Line height must be at least 1.5 times the font size (WCAG 2.2 SC 1.4.12). For a 14px font, line-height must be ≥ 21px.

#### Actual Behaviour

`line-height: 20px` with `font-size: 14px` gives a ratio of ~1.43, which violates the Text Spacing accessibility requirement.

#### Heuristic Reference

- WCAG1.4.12 (Text Spacing — Level AA)

#### Evidences

![BUG-A4-003.png](../screenshots/task01/A4/BUG-A4-003.png)

#### Suggested Fix

Update the global or table body text CSS class to set `line-height: 1.5` (or a fixed value ≥ 21px for 14px text). Prefer using the unitless multiplier (`1.5`) over a fixed pixel value so the ratio holds when users override font sizes.

### BUG-A4-004 — Layout breaks at 200% zoom and 320 px viewport width

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-004                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Bug                                 |
| **Severity**            | 3 — Major Usability Problem         |
| **Priority**            | High                                |
| **Affected Items**      | IA-01-015                           |
| **Affected Edge Cases** | None                                |

#### Description

At 200% browser zoom, the pagination control is hidden by its container overflow, and the tab bar and action buttons overflow their container causing a horizontal scrollbar to appear on the entire page. At 320 px viewport width, tab content disappears entirely, leaving only the button controls visible.

#### Steps to Reproduce

**Scenario A — 200% Zoom:**

1. Navigate to the A4 screen (Registrants tab).
2. In the browser, press `Ctrl + +` (or `Cmd + +`) to zoom to 200%.
3. Observe the Registrants tab — verify the pagination control is hidden/clipped.
4. Observe the Tab bar and Action buttons row — verify they overflow their container and a horizontal scrollbar appears.

**Scenario B — 320 px viewport:**

1. Open DevTools → Device Toolbar.
2. Set the viewport width to 320 px.
3. Navigate to any of the three tabs.
4. Observe — the tab content area is completely hidden; only the top action buttons remain visible.

#### Expected Behaviour

The page remains fully usable at 200% zoom (no horizontal scrollbar, all controls visible) and at 320 px viewport width (all tab content readable, possibly in a simplified layout).

#### Actual Behaviour

- **At 320 px:** entire tab content body is hidden, the screen is unusable.
- **At 200% zoom:** pagination is clipped and invisible; tabs + action buttons overflow container causing horizontal scrolling.

#### Heuristic Reference

- WCAG1.4.10 (Reflow — Level AA)
- N7 (Flexibility and Efficiency of Use)

#### Evidences

**At 320 px:**

![BUG-A4-004-01.png](../screenshots/task01/A4/BUG-A4-004-01.png)

**At 200% zoom:**

![BUG-A4-004-02.png](../screenshots/task01/A4/BUG-A4-004-02.png)

#### Suggested Fix

Audit the Tab bar and Action buttons container for `overflow: hidden` or fixed-width constraints that prevent reflow. Use `overflow-x: auto` on table containers and ensure tab content uses responsive CSS (`flex-wrap`, `min-width: 0`) rather than fixed widths. Test using the browser's built-in responsive mode before release.

### BUG-A4-005 — Inconsistent spacing and padding in the Top Section

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-005                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Usability                           |
| **Severity**            | 1 — Cosmetic Problem                |
| **Priority**            | Low                                 |
| **Affected Items**      | IA-01-017                           |
| **Affected Edge Cases** | None                                |

#### Description

The Top Section of the A4 screen contains inconsistent spacing and padding: the gap between the `PUBLISHED` badge and the Edit Event button differs from the gap between Edit Event and Important Update; the Back button (arrow-left icon) has uneven internal padding; and the container wrapping the title, badges, action buttons, and tab bar lacks uniform padding on all sides.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Visually compare the horizontal gap between: (a) the `PUBLISHED` badge and the Edit Event button, and (b) the Edit Event button and the Important Update button.
3. Open DevTools → Elements and inspect the Back (arrow-left) button — check the `padding` values on all four sides.
4. Inspect the outer container holding the title, action buttons, and tab bar — verify `padding` is uniform on all sides.

#### Expected Behaviour

All elements in the Top Section maintain consistent, uniform spacing between them. The Back button has equal padding on all sides. The container has balanced padding throughout.

#### Actual Behaviour

- Gap between `PUBLISHED` badge and Edit Event ≠ gap between Edit Event and Important Update.
- Back button has uneven padding (e.g., more padding on one axis than the other).
- The outer container has non-uniform padding causing visual imbalance.

#### Heuristic Reference

- S1 (Strive for Consistency)
- N4 (Consistency and Standards)

#### Evidences

![BUG-A4-005.png](../screenshots/task01/A4/BUG-A4-005.png)

#### Suggested Fix

Use a consistent spacing token (e.g., `gap: 8px` or `gap: 12px`) in the flex container holding the Top Section elements. Apply equal `padding` to the Back button using a shared button style. Align the container padding with the design system's standard page-level padding value.

### BUG-A4-006 — Icon-only buttons lack text tooltips on hover

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-006                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Usability                           |
| **Severity**            | 2 — Minor Usability Problem         |
| **Priority**            | Med                                 |
| **Affected Items**      | IA-04-015                           |
| **Affected Edge Cases** | None                                |

#### Description

Icon-only buttons on the A4 screen (Back arrow-left button and Filter icon in the Registrants tab) do not display any text tooltip when hovered. Without a tooltip, users cannot discern the button's action from the icon alone, especially for less-recognizable icons.

#### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Hover the mouse cursor over the **Back** (arrow-left) icon button next to the event title for 2 seconds.
3. Observe — no tooltip appears.
4. Open the Registrants tab and hover over the **Filter** icon button for 2 seconds.
5. Observe — no tooltip appears.

#### Expected Behaviour

Each icon-only button displays a concise text tooltip (via `title` attribute or a floating tooltip element) after a short hover delay, describing the button's action (e.g., "Go back to Event List", "Filter registrants").

#### Actual Behaviour

No tooltip appears on any icon-only button. Users must infer the button action from the icon shape alone with no textual confirmation.

#### Heuristic Reference

- N6 (Recognition Rather Than Recall)
- NOR2 (Signifiers)

#### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1q9bpLme0w9GX01a82NighW3x8OZ5mjIi/view)

#### Suggested Fix

Add a `title="Go back to Event List"` attribute to the Back icon button and a `title="Filter registrants"` (or equivalent) to the Filter icon button. Alternatively, implement a CSS/JS tooltip component tied to an `aria-label` attribute to ensure both visual and accessible tooltip behaviour.

### BUG-A4-007 — ACTION segmented control has inconsistent icon usage across segments

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-007                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Usability                           |
| **Severity**            | 1 — Cosmetic Problem                |
| **Priority**            | Low                                 |
| **Affected Items**      | None                                |
| **Affected Edge Cases** | EC-A4-005                           |

#### Description

In the ACTION segmented control on both the Review Lecturers and Review Students tabs, the "Reject" segment displays a leading icon, while the "Pending" and "Approved" segments contain only text. This breaks the visual consistency of the control.

#### Steps to Reproduce

1. Navigate to the A4 screen and open the **Review Students** tab.
2. Observe the ACTION segmented control for any student row.
3. Compare the three segments: **Reject** (left), **Pending** (middle), **Approved** (right).
4. Verify that "Reject" has a visible leading icon, while "Pending" and "Approved" have no icon.
5. Repeat in the **Review Lecturers** tab.

#### Expected Behaviour

All segments within the ACTION control follow a consistent design pattern — either all three segments include a leading icon, or none do.

#### Actual Behaviour

Only the "Reject" segment has a leading icon. "Pending" and "Approved" segments use text only, creating a visually asymmetric control.

#### Heuristic Reference

- N4 (Consistency and Standards)
- S1 (Strive for Consistency)

#### Evidences

![BUG-A4-007.png](../screenshots/task01/A4/BUG-A4-007.png)

#### Suggested Fix

Choose one of the following consistent approaches and apply it uniformly across all three segments:

- **Option A:** Remove the icon from the Reject segment so all three segments are text-only.
- **Option B:** Add appropriate icons to all three segments (e.g., an X icon for Reject, a clock icon for Pending, a checkmark icon for Approved).

# Task 02

### Finding F-01 — Six-field date/time cluster on A2 lacks contextual labels or inline help

| Field                  | Value                                                           |
| ---------------------- | --------------------------------------------------------------- |
| **Severity**           | 3 — Major                                                       |
| **Frequency**          | 5/5 participants (100%)                                         |
| **Criticality**        | 3 × 1.0 = **3.0**                                               |
| **Type**               | Systemic                                                        |
| **Heuristic violated** | H10: Help and Documentation; H6: Recognition Rather Than Recall |
| **Tasks affected**     | T1                                                              |

**Observation:** Participants hesitated when they reached the date and time section of the A2 form. The section presents six distinct date/time fields — event start date, event end date, event start time, event end time, registration open date, and registration close date — without grouping, separating lines, or inline tooltips. Participants had to pause and mentally re-read the task scenario to map the required values to the correct fields. P3 hesitated for approximately 40 seconds at this section alone, then submitted the form without completing these fields correctly, triggering two separate validation errors. P4 paused ~15 seconds and verbalized the confusion despite ultimately entering the data correctly.

**Evidence:**

> _"There are 6 different date/time fields. I'm not sure which is which."_ — P3, `[02:30]`, A2

> _"So many dates... let me check what the instructions say."_ — P4, `[01:20]`, A2

> _"I would strongly suggest adding more visual guidance for the date and time fields. There were too many time-related inputs."_ — P3, Probe Q5

> _"The date section is a bit heavy. Grouping those six fields better or just adding a small hover-tooltip would save new users from second-guessing themselves."_ — P4, Probe Q5

**Recommendation:** Group the six date/time fields into two clearly labelled sub-sections: **"Event Schedule"** (start date/time, end date/time) and **"Registration Window"** (open date, close date). Add a small info icon (ℹ) with a hover tooltip to each sub-section heading explaining what the dates control (e.g., "The dates during which the event takes place" vs. "The period in which users may register"). This reduces the reliance on external context and eliminates the primary hesitation point across all sessions.

### Finding F-02 — No success feedback after publishing (missing success toast)

| Field                  | Value                           |
| ---------------------- | ------------------------------- |
| **Severity**           | 2 — Minor                       |
| **Frequency**          | 5/5 participants (100%)         |
| **Criticality**        | 2 × 1.0 = **2.0**               |
| **Type**               | Systemic                        |
| **Heuristic violated** | H1: Visibility of System Status |
| **Tasks affected**     | T1                              |

**Observation:** After clicking Publish on A2, no confirmation toast, banner, or success indicator appears to acknowledge that the event has been published. All five participants had to navigate back to the Event List (A1) to visually confirm that the new event appeared with "Published" status. Three participants (P3, P4, P5) explicitly verbalized uncertainty about whether the publication had succeeded immediately after clicking Publish.

**Evidence:**

> _"Why wasn't there a success message? But I see it in the list and the status says Published, so I hope it worked."_ — P5, `[06:20]`, A1

> _"I wasn't entirely sure immediately because nothing popped up to say it was successful."_ — P3, Probe Q3

> _"At first, I didn't see a success notification, so I double-checked the main dashboard."_ — P4, Probe Q3

> _"Before that, Draft and Publish were easy to mix up."_ — P2, Probe Q3

**Recommendation:** Display a brief, non-blocking success toast notification (e.g., 3 seconds duration) in the top-right corner immediately after a successful Publish action, reading: _"Event published successfully. View it in the Event List."_ The toast should include a hyperlink to the newly published event on A1. This directly addresses the H1 violation and eliminates the mandatory return trip to A1 to confirm state.

### Finding F-03 — Required field asterisks are low-contrast and visually indistinguishable

| Field                  | Value                                               |
| ---------------------- | --------------------------------------------------- |
| **Severity**           | 2 — Minor                                           |
| **Frequency**          | 3/5 participants (60%)                              |
| **Criticality**        | 2 × 0.6 = **1.2**                                   |
| **Type**               | Systemic                                            |
| **Heuristic violated** | H5: Error Prevention; H4: Consistency and Standards |
| **Tasks affected**     | T1                                                  |

**Observation:** The required field marker (`*`) on A2 uses a black or near-black color, making it visually indistinguishable from surrounding label text. This caused P1, P4, and P5 to overlook the required "Campus" field and attempt to submit the form without it, triggering a validation error. All three participants independently identified the low-contrast asterisk as the root cause after the error occurred, citing that a red asterisk would have prevented the mistake.

**Evidence:**

> _"Oh, I missed the Campus field. The asterisk is black so it didn't stand out."_ — P1, `[01:42]`, A2

> _"Ah, there's an error. I missed the Campus field. The asterisk isn't very prominent."_ — P5, `[06:00]`, A2

> _"I was a bit unhappy because the required fields in the form don't have a distinct or highlighted asterisk color."_ — P5, Probe Q2

> _"I omitted the 'Campus' field initially because the mandatory fields were not sufficiently highlighted (the asterisk was black, not red)."_ — P1, Probe Q2

**Recommendation:** Change the required field asterisk (`*`) color from its current near-black to a high-contrast **red** (e.g., `#D32F2F`, WCAG AA compliant against white backgrounds). This is the universally established web convention for required fields (consistent with standard HTML form patterns), and the change requires a single global CSS rule update.

### Finding F-04 — Participant limit fields are hidden behind a role-toggle mechanism

| Field                  | Value                                                            |
| ---------------------- | ---------------------------------------------------------------- |
| **Severity**           | 2 — Minor                                                        |
| **Frequency**          | 2/5 participants (40%)                                           |
| **Criticality**        | 2 × 0.4 = **0.8**                                                |
| **Type**               | Isolated                                                         |
| **Heuristic violated** | H6: Recognition Rather Than Recall; H3: User Control and Freedom |
| **Tasks affected**     | T1                                                               |

**Observation:** The numeric input fields for student and lecturer slot counts are conditionally hidden and only appear after the user enables the corresponding role toggle (e.g., "Allow student registration"). P3 hesitated (~25 seconds) and P5 hesitated (~15 seconds) when they could not immediately locate where to enter the participation limits, despite the task scenario explicitly specifying "30 students and 10 lecturers." Both participants eventually discovered the toggle mechanism and proceeded, but the hidden nature of the fields required trial-and-error exploration.

**Evidence:**

> _"Hmm, 20 students, 30 lecturers... but I don't see where to input the quantities."_ — P5, `[04:45]`, A2

> _"So I need to allocate roles here..."_ — P3, `[04:10]`, A2

**Recommendation:** Display the slot count input fields in a persistently visible but disabled state when the role toggle is off, and activate them when the toggle is turned on. This "grayed-out but visible" pattern (cf. Nielsen H6) allows users to understand the structure of the form before making toggle decisions, eliminating the "fields appear from nowhere" confusion.

### Finding F-05 — Pending registrant sub-tab is not immediately obvious on A4

| Field                  | Value                                                               |
| ---------------------- | ------------------------------------------------------------------- |
| **Severity**           | 1 — Cosmetic                                                        |
| **Frequency**          | 3/5 participants (60%)                                              |
| **Criticality**        | 1 × 0.6 = **0.6**                                                   |
| **Type**               | Systemic                                                            |
| **Heuristic violated** | H1: Visibility of System Status; H6: Recognition Rather Than Recall |
| **Tasks affected**     | T1                                                                  |

**Observation:** When participants first arrived at A4 (Participants & Reviews), they landed on a default tab that did not immediately show pending registrants. P2, P3, and P4 each paused briefly (6–10 seconds) before noticing the red notification dot on the "Review Students" tab and switching to it. Once they noticed the dot, recovery was immediate. The red dot is an effective but small secondary cue; the primary landing state of A4 does not proactively surface pending actions.

**Evidence:**

> _"Where are the pending users?"_ — P2, `[04:43]`, A4

> _"Where are the requests?"_ — P3, `[08:40]`, A4

> _"Where are they?"_ — P4, `[03:40]`, A4

> _"Ah, the red dot means they are waiting."_ — P4, `[03:50]`, A4

**Recommendation:** If there are pending registrants awaiting review, default the landing tab on A4 to the review tab with pending items (or the tab with the highest pending count). Alternatively, display a dismissible inline banner at the top of A4: _"3 registrations are awaiting your review."_ with a link to the relevant tab. This surfaces the pending action state proactively and reduces reliance on the participant noticing the small notification dot.

### Finding F-06 — Image upload ratio requirement not communicated upfront on A2

| Field                  | Value                                                    |
| ---------------------- | -------------------------------------------------------- |
| **Severity**           | 1 — Cosmetic                                             |
| **Frequency**          | 2/5 participants (40%)                                   |
| **Criticality**        | 1 × 0.4 = **0.4**                                        |
| **Type**               | Isolated                                                 |
| **Heuristic violated** | H5: Error Prevention; H6: Recognition Rather Than Recall |
| **Tasks affected**     | T1                                                       |

**Observation:** The image upload area on A2 requires specific aspect ratios (4:3 for thumbnail, 24:9 for banner). P2 uploaded an incorrectly sized image and received a ratio rejection error. P2 additionally verbalized uncertainty about image size requirements when they first reached the field. Neither participant was shown the ratio requirements as instructional text before the upload attempt; the constraint only became apparent after a rejection.

**Evidence:**

> _"Do they need a specific image size here?"_ — P2, `[00:36]`, A2 (verbalised uncertainty before uploading)

> _"Ah, the image must be exactly 4:3."_ — P2, `[01:05]`, A2 (after rejection)

> _"I would make the image requirement and the publish step clearer."_ — P2, Probe Q5

**Recommendation:** Display the required aspect ratio (and optional max file size) as static placeholder text or a helper label directly inside or beneath each image upload zone before any upload is attempted (e.g., "Thumbnail: 4:3 ratio recommended. Min 400×300 px."). This is a standard pattern on upload-heavy forms and removes the need for participants to discover requirements through error-driven feedback.

### Finding F-07 — Row click targets on A1 are too small (icon-only navigation)

| Field                  | Value                                                         |
| ---------------------- | ------------------------------------------------------------- |
| **Severity**           | 1 — Cosmetic                                                  |
| **Frequency**          | 1/5 participants (20%)                                        |
| **Criticality**        | 1 × 0.2 = **0.2**                                             |
| **Type**               | Isolated                                                      |
| **Heuristic violated** | H4: Consistency and Standards; Fitts's Law (motor efficiency) |
| **Tasks affected**     | T1                                                            |

**Observation:** P1 explicitly noted that navigating to an event's details on A1 required targeting a small "eye" icon rather than being able to click anywhere on the event row. While P1 successfully navigated using this mechanism without failing, they verbalized frustration and recommended making the entire table row a clickable target. No other participant mentioned this finding explicitly, though it may reflect their differing navigation habits.

# Task 03

### BUG-COMPAT-001 — Fixed-width table layout causes column content loss and overflow on mobile viewports

| Field                     | Detail                                                                                                                                                                                                                                                                                                                                                                                                   |
| ------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                    | BUG-COMPAT-001                                                                                                                                                                                                                                                                                                                                                                                           |
| **Severity**              | Critical                                                                                                                                                                                                                                                                                                                                                                                                 |
| **Priority**              | P1                                                                                                                                                                                                                                                                                                                                                                                                       |
| **Type**                  | Layout / Responsiveness                                                                                                                                                                                                                                                                                                                                                                                  |
| **Root Cause**            | Table and main content area are rendered with a fixed minimum width (or `min-width` CSS property) that exceeds the viewport width on mobile phone screens (~360–414 px). No horizontal scroll is enabled on the table wrapper, causing columns to be clipped. Only the last column (`ACTION`) remains visible because it is positioned or rendered last in the DOM flow within the constrained viewport. |
| **Affected Screens**      | A1, A4                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Affected Environments** | Android / Chrome / Phone · Android / Samsung Internet / Phone                                                                                                                                                                                                                                                                                                                                            |

**Observed Behaviour:**

- **A1:** All event table columns except `ACTION` are hidden; the full event data (name, date, status, etc.) cannot be viewed.
- **A4:** Table columns in the Registrants, Review Lecturers, and Review Students tabs are clipped; users cannot access participant data.

**Expected Behaviour:** The table should either scroll horizontally within its container or reflow into a responsive card/list layout on viewport widths < 600 px, ensuring all column data is accessible.

**Screenshots:**

| Environment                        | Screen | File                                                               |
| ---------------------------------- | ------ | ------------------------------------------------------------------ |
| Android / Chrome / Phone           | A1     | `screenshots/task03/A1/android_chrome_phone_a1_fail.jpg`           |
| Android / Samsung Internet / Phone | A1     | `screenshots/task03/A1/android_samsung-internet_phone_a1_fail.jpg` |
| Android / Chrome / Phone           | A4     | `screenshots/task03/A4/android_chrome_phone_a4_fail.jpg`           |
| Android / Samsung Internet / Phone | A4     | `screenshots/task03/A4/android_samsung-internet_phone_a4_fail.jpg` |

**Suggested Fix:** Add `overflow-x: auto` to the table wrapper container and define responsive breakpoints (`@media (max-width: 600px)`) to switch to a card-based or horizontally scrollable table layout.

### BUG-COMPAT-002 — Left sidebar does not auto-collapse on initial load for narrow viewports

| Field                     | Detail                                                                                                                                                                                                                                                                                                                                                      |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                    | BUG-COMPAT-002                                                                                                                                                                                                                                                                                                                                              |
| **Severity**              | Critical                                                                                                                                                                                                                                                                                                                                                    |
| **Priority**              | P1                                                                                                                                                                                                                                                                                                                                                          |
| **Type**                  | Layout / Responsiveness                                                                                                                                                                                                                                                                                                                                     |
| **Root Cause**            | The left navigation sidebar lacks a responsive collapse/hide behaviour. On desktop, the sidebar occupies a fixed portion of the horizontal space. On mobile phone viewports, the sidebar remains expanded on page load, consuming a significant horizontal portion of the already narrow screen and compressing the main content area to an unusable width. |
| **Affected Screens**      | A1                                                                                                                                                                                                                                                                                                                                                          |
| **Affected Environments** | Android / Chrome / Phone · Android / Samsung Internet / Phone                                                                                                                                                                                                                                                                                               |

**Observed Behaviour:** When navigating to the Events List (A1) on a mobile phone, the left sidebar remains fully visible and occupies a large portion of the screen width, leaving the main content table with insufficient space to render.

**Expected Behaviour:** On viewport widths below a defined breakpoint (e.g., < 768 px), the sidebar should be hidden or collapsed by default, optionally toggled via a hamburger menu icon.

**Screenshots:**

| Environment                        | Screen | File                                                               |
| ---------------------------------- | ------ | ------------------------------------------------------------------ |
| Android / Chrome / Phone           | A1     | `screenshots/task03/A1/android_chrome_phone_a1_fail.jpg`           |
| Android / Samsung Internet / Phone | A1     | `screenshots/task03/A1/android_samsung-internet_phone_a1_fail.jpg` |

**Suggested Fix:** Implement a CSS media query to set `display: none` or `transform: translateX(-100%)` on the sidebar for viewport widths below the mobile breakpoint, and add a toggle button to show/hide it on demand.

### BUG-COMPAT-003 — Pagination component overflows or is hidden outside its container on mobile and tablet viewports

| Field                     | Detail                                                                                                                                                                                                                                                                                                                                                                                                     |
| ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                    | BUG-COMPAT-003                                                                                                                                                                                                                                                                                                                                                                                             |
| **Severity**              | High                                                                                                                                                                                                                                                                                                                                                                                                       |
| **Priority**              | P2                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Type**                  | Layout / Responsiveness                                                                                                                                                                                                                                                                                                                                                                                    |
| **Root Cause**            | The pagination component is rendered with a fixed width or uses `display: flex` without `flex-wrap: wrap`, causing it to extend beyond the parent container's boundary on narrow viewports. On phone-sized screens, the overflow is visible (buttons extend outside the container); on tablet Firefox, the container clips the overflow (no `overflow: visible` fallback), hiding the pagination entirely. |
| **Affected Screens**      | A1, A4                                                                                                                                                                                                                                                                                                                                                                                                     |
| **Affected Environments** | Android / Chrome / Phone · Android / Samsung Internet / Phone · Android / Firefox / Tablet                                                                                                                                                                                                                                                                                                                 |

**Observed Behaviour:**

- **Android Phone (Chrome & Samsung Internet / A1):** Pagination buttons visually overflow the container boundary.
- **Android Tablet (Firefox / A1 & A4):** Pagination buttons are hidden because they exceed the container width and the overflow is clipped.
- **Android Phone (Chrome & Samsung Internet / A4):** Pagination buttons are hidden due to exceeding container width.

**Expected Behaviour:** Pagination controls should either wrap onto a new line (`flex-wrap: wrap`) or scale down their size at smaller breakpoints so that all page controls remain visible and usable.

**Screenshots:**

| Environment                        | Screen | File                                                               |
| ---------------------------------- | ------ | ------------------------------------------------------------------ |
| Android / Chrome / Phone           | A1     | `screenshots/task03/A1/android_chrome_phone_a1_fail.jpg`           |
| Android / Samsung Internet / Phone | A1     | `screenshots/task03/A1/android_samsung-internet_phone_a1_fail.jpg` |
| Android / Firefox / Tablet         | A1     | `screenshots/task03/A1/android_firefox_tablet_a1_fail.png`         |
| Android / Chrome / Phone           | A4     | `screenshots/task03/A4/android_chrome_phone_a4_fail.jpg`           |
| Android / Samsung Internet / Phone | A4     | `screenshots/task03/A4/android_samsung-internet_phone_a4_fail.jpg` |
| Android / Firefox / Tablet         | A4     | `screenshots/task03/A4/android_firefox_tablet_a4_fail.png`         |

**Suggested Fix:** Apply `flex-wrap: wrap` and `overflow-x: auto` to the pagination wrapper. Reduce button padding/font size at breakpoints ≤ 480 px to ensure all controls fit within the viewport width.

### BUG-COMPAT-004 — Add/Edit Event Form layout breaks and dialogs/popups overflow viewport on mobile phones

| Field                     | Detail                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| ------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                    | BUG-COMPAT-004                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **Severity**              | High                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Priority**              | P2                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| **Type**                  | Layout / Responsiveness                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| **Root Cause**            | The Add/Edit Event Form (A2) uses a fixed or percentage-based multi-column layout that does not collapse into a single column on narrow viewports. This causes: (1) form labels and input fields to render too narrowly and overlap each other; (2) modal dialogs (e.g., date picker, role assignment inputs) to render at their desktop size without `max-width: 100vw` and `overflow: hidden` constraints, causing them to extend beyond the phone viewport. |
| **Affected Screens**      | A2                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| **Affected Environments** | Android / Chrome / Phone · Android / Samsung Internet / Phone                                                                                                                                                                                                                                                                                                                                                                                                  |

**Observed Behaviour:**

- Form width is too narrow, causing text labels and input fields to visually overlap.
- Date picker popup renders wider than the viewport and is partially or fully off-screen.
- Input fields for creating a new lecturer/student role overflow outside their container boundaries.

**Expected Behaviour:** The form should reflow to a single-column layout on mobile phone viewports (< 600 px). All modal dialogs and popups should constrain their width to `min(100vw - 32px, original-width)` and use scroll or resize behaviour to remain fully visible.

**Screenshots:**

| Environment                        | Screen | File                                                               |
| ---------------------------------- | ------ | ------------------------------------------------------------------ |
| Android / Chrome / Phone           | A2     | `screenshots/task03/A2/android_chrome_phone_a2_fail.jpg`           |
| Android / Samsung Internet / Phone | A2     | `screenshots/task03/A2/android_samsung-internet_phone_a2_fail.jpg` |

**Suggested Fix:** Define a single-column responsive layout for the form using `@media (max-width: 600px) { .form-grid { grid-template-columns: 1fr; } }`. For modals/popups, apply `max-width: calc(100vw - 2rem)` and `left: 50%; transform: translateX(-50%)` to ensure they remain centred and within the viewport.

### BUG-COMPAT-005 — Full-page horizontal scrollbar appears on Firefox / Android Tablet for Add/Edit Event Form

| Field                     | Detail                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **ID**                    | BUG-COMPAT-005                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **Severity**              | High                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **Priority**              | P2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Type**                  | Layout / Browser-Specific                                                                                                                                                                                                                                                                                                                                                                                                                                                            |
| **Root Cause**            | On Android Firefox (tablet viewport ~768–1024 px), the total rendered content width of the A2 page exceeds the viewport width, triggering a page-level horizontal scrollbar. This is likely caused by a component (sidebar, form, or a popup) rendering at a width wider than the viewport without `overflow: hidden` on the root layout container. Firefox's default overflow handling differs from Chrome/Safari, making the issue manifest only in Firefox at tablet breakpoints. |
| **Affected Screens**      | A2                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Affected Environments** | Android / Firefox / Tablet                                                                                                                                                                                                                                                                                                                                                                                                                                                           |

**Observed Behaviour:** A horizontal scrollbar appears at the bottom of the entire page on A2 when tested on an Android tablet in Firefox. The user must scroll horizontally to access form fields or sidebar tab items.

**Expected Behaviour:** No page-level horizontal scrollbar should appear. All content should be contained within the viewport width at tablet breakpoints.

**Screenshots:**

| Environment                | Screen | File                                                       |
| -------------------------- | ------ | ---------------------------------------------------------- |
| Android / Firefox / Tablet | A2     | `screenshots/task03/A2/android_firefox_tablet_a2_fail.png` |

**Suggested Fix:** Apply `overflow-x: hidden` on the root layout container (e.g., `body` or the main app wrapper). Audit all child elements for any component that has a hard-coded pixel width exceeding typical tablet viewport widths.

### BUG-COMPAT-006 — Event table horizontal scroll missing on macOS Safari, content columns inaccessible

| Field                     | Detail                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| ------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                    | BUG-COMPAT-006                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| **Severity**              | Medium                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **Priority**              | P3                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Type**                  | Browser-Specific / Layout                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| **Root Cause**            | Safari on macOS suppresses horizontal scrollbars by default (they are only displayed on scroll gesture). However, in this case, the table wrapper does not have `overflow-x: auto` (or `overflow-x: scroll`) explicitly set, so Safari does not render any horizontal scroll at all. Chrome and Edge auto-derive scrollability from the overflow content, but Safari requires explicit CSS declaration. As a result, the overflowing table columns are clipped with no scrolling mechanism available. |
| **Affected Screens**      | A1, A4                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                |
| **Affected Environments** | macOS / Safari / Desktop                                                                                                                                                                                                                                                                                                                                                                                                                                                                              |

**Observed Behaviour:**

- **A1:** The event table columns beyond the visible area are not accessible — no horizontal scrollbar is rendered and horizontal swipe/scroll gesture has no effect.
- **A4:** The registrants table in the Registrants tab similarly clips column content without providing a scroll mechanism.

**Expected Behaviour:** The table wrapper should allow horizontal scrolling on all browsers including Safari. Users should be able to access all table columns.

**Screenshots:**

| Environment              | Screen | File                                                     |
| ------------------------ | ------ | -------------------------------------------------------- |
| macOS / Safari / Desktop | A1     | `screenshots/task03/A1/macos_safari_desktop_a1_fail.png` |
| macOS / Safari / Desktop | A4     | `screenshots/task03/A4/macos_safari_desktop_a4_fail.png` |

**Suggested Fix:** Explicitly set `overflow-x: auto` (not just `auto` shorthand) on the table's direct wrapper `<div>`. Test cross-browser to confirm Safari picks up the declaration.

### BUG-COMPAT-007 — Insufficient contrast between Reject All / Approve All button text and background in Safari

| Field                     | Detail                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| ------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **ID**                    | BUG-COMPAT-007                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               |
| **Severity**              | Medium                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **Priority**              | P3                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Type**                  | Browser-Specific / Visual Rendering                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          |
| **Root Cause**            | The `Reject All` and `Approve All` buttons in the Review Lecturers and Review Students tabs of A4 have a background colour that is very close in luminance to the button text colour. This low-contrast rendering is visible specifically on macOS/Safari (Desktop), suggesting that Safari's colour rendering or CSS colour profile handling produces a slightly different rendered output than Chromium-based browsers, making the contrast issue visible on Safari where it may appear acceptable on Chrome/Edge. The WCAG 2.2 minimum contrast ratio of 4.5:1 for normal text (or 3:1 for large text) is likely not met. |
| **Affected Screens**      | A4                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Affected Environments** | macOS / Safari / Desktop                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |

**Observed Behaviour:** The background colour of the `Reject All` and `Approve All` buttons is almost identical to the text colour on macOS/Safari, making the button labels extremely difficult to read.

**Expected Behaviour:** Button text and background should maintain a contrast ratio of at least 4.5:1 (WCAG AA) across all browsers and operating systems. Button labels must be clearly legible at a glance.

**Screenshots:**

| Environment              | Screen | File                                                     |
| ------------------------ | ------ | -------------------------------------------------------- |
| macOS / Safari / Desktop | A4     | `screenshots/task03/A4/macos_safari_desktop_a4_fail.png` |

**Suggested Fix:** Review the CSS colour values for these buttons. Increase the contrast between background and text colours to meet WCAG AA minimum contrast ratio (4.5:1). Use a tool such as the WebAIM Contrast Checker to validate the final values across all target browsers.
