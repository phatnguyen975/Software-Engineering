# Bug Report — Events List (A1)

> **Source:** `execution-A1.md` · `edge-cases-A1.md`  
> **Tester:** Nguyễn Tấn Phát — 23127449  
> **Date:** 2026-07-28  
> **Total bug groups:** 10  
> **Severity:** Severity 4 (2) · Severity 3 (2) · Severity 2 (4) · Severity 1 (2) · Severity 0 (0)

## BUG-A1-001 — Missing toast notification for Delete, non-standard dialog for Important Update

| Field                   | Value                                      |
| ----------------------- | ------------------------------------------ |
| **ID**                  | BUG-A1-001                                 |
| **Screen**              | A1: Events List                            |
| **Type**                | Bug                                        |
| **Severity**            | 4 — Usability Catastrophe                  |
| **Priority**            | High                                       |
| **Affected Items**      | IA-04-001, IA-04-002, IA-04-003, IA-04-009 |
| **Affected Edge Cases** | None                                       |

### Description

Feedback for asynchronous actions is inconsistent and violates standard patterns. The "Delete" action provides no feedback whatsoever (silent UI update). The "Important Update" action displays a success notification as a dialog, which does not auto-dismiss after 5 seconds, rather than using a standard toast notification.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Locate any event row in the Events table.
3. **Test Delete:** Click the **Delete** icon, confirm the deletion, and observe the screen for any toast notification.
4. **Test Important Update:** Click the **Important Update** icon, enter important update message, click **Send** in the dialog, and observe the resulting feedback.
5. Notice that Delete triggers no toast, while Important Update triggers a dialog instead of an auto-dismissing toast.

### Expected Behaviour

A toast notification must appear in a consistent position (e.g., top-right corner) immediately after the deletion/updation completes, with:

- A green background and a checkmark icon for success (e.g., _"Event deleted successfully"_).
- A red background and an error icon for failure (e.g., _"Failed to delete event. Please try again."_).
- The toast must auto-dismiss after ≥ 5 seconds and include a manual `×` dismiss button.

### Actual Behaviour

Delete action silently updates the table without any success notification. Important Update displays a success dialog (with correct green colour) instead of a toast; however, it does not auto-dismiss after 5 seconds and requires manual closing by the user.

### Heuristic Reference

- N1 (Visibility of System Status)
- NOR5 (Feedback)
- S5 (Offer Simple Error Handling)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1BiKe5s8T0K0IAMbIc3-Zvyhi1aQy5rFF/view)

### Suggested Fix

Implement a standardized toast/snackbar component for all CRUD actions.

- **For Delete:** Trigger a green success toast upon successful deletion.
- **For Important Update:** Replace the current success dialog with a standard success toast.
- Ensure all toasts auto-dismiss after 5 seconds (but allow manual dismissal) and include an explicit status icon (e.g., checkmark for success) so they do not rely on color alone.

## BUG-A1-002 — Insufficient colour contrast on white-on-cyan active state (WCAG AA failure)

| Field                   | Value                                               |
| ----------------------- | --------------------------------------------------- |
| **ID**                  | BUG-A1-002                                          |
| **Screen**              | A1: Events List                                     |
| **Type**                | Bug                                                 |
| **Severity**            | 4 — Usability Catastrophe (Accessibility — WCAG AA) |
| **Priority**            | High                                                |
| **Affected Items**      | IA-01-003                                           |
| **Affected Edge Cases** | None                                                |

### Description

The primary cyan colour used for active/selected state elements (the _Add Event_ button, the currently active pagination page number, and the active sidebar item) renders white text on a cyan background that fails the WCAG AA minimum contrast ratio of 4.5:1 for normal text. This is an accessibility violation that affects all users, especially those with low vision or colour deficiency.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Open **DevTools** → **Elements** panel.
3. Inspect the **"+ Add Event** button, the currently active pagination page number chip, or the active sidebar item.
4. Extract the exact hex (or rgb) values for the text colour (white) and background colour (cyan).
5. Input these values into a standard contrast tool such as the [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/) to calculate the exact contrast ratio.
6. (Optional Verification) Hover over the text colour property directly within the DevTools Elements panel to view the browser's built-in contrast ratio tooltip and cross-reference the result.
7. Observe that the calculated ratio falls significantly below the minimum 4.5:1 threshold.

### Expected Behaviour

All text–background combinations must achieve a contrast ratio of at least **4.5:1** for normal-sized body text (WCAG 2.2 SC 1.4.3, Level AA). The white text on the active-state cyan background must meet this requirement.

### Actual Behaviour

The calculated contrast ratio between the white text (`#FFFFFF`) and the active cyan background is exactly **2.08:1**, which fails the WCAG 2.2 Level AA requirement of 4.5:1. This identical failing colour combination affects the _Add Event_ button, the active pagination number chip, and the active sidebar tab.

### Heuristic Reference

- WCAG 1.4.3 (Contrast — Minimum, Level AA)
- NOR4 (Mappings / Legibility)

### Evidences

![BUG-A1-002.png](../../../screenshots/task01/A1/BUG-A1-002.png)

### Suggested Fix

Update the design system's primary cyan token to ensure sufficient luminance contrast when paired with white text. You have two primary approaches depending on the design language constraints:

- **Approach 1 (Preferred):** Darken the primary cyan background colour to a deeper shade (e.g., teal or dark cyan) until the contrast ratio against white reaches at least 4.5:1.
- **Approach 2:** If the bright cyan background must be preserved, switch the text colour from white to a dark shade (e.g., dark grey or charcoal) to achieve the required 4.5:1 contrast.

Ensure these updated tokens are applied consistently across all active state components (buttons, pagination, sidebar).

## BUG-A1-003 — Layout breaks at 200% zoom and 320 px viewport

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-003                  |
| **Screen**              | A1: Events List             |
| **Type**                | Bug                         |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA-01-015                   |
| **Affected Edge Cases** | None                        |

### Description

The Events List page is not responsive and fails under two standard accessibility stress-test conditions: 200% browser zoom (required for low-vision users) and a 320 px wide viewport (smallest common mobile width). Multiple UI components overflow their containers or disappear entirely, making the screen unusable at these sizes.

### Steps to Reproduce

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

### Expected Behaviour

The page must remain fully functional at both 200% zoom and 320 px viewport width. Specifically:

- The events table should be horizontally scrollable internally (not cause a full-page horizontal scroll).
- All components (search bar, filters, pagination, header) must stay within their containers.
- The layout must stack or reflow gracefully on narrow viewports.

### Actual Behaviour

At 200% zoom, a full-page horizontal scrollbar appears and the pagination component overflows. At 320 px, the events table disappears completely, header elements misalign, and the action bar layout breaks.

### Heuristic Reference

- WCAG 1.4.10 (Reflow, Level AA)
- N4 (Consistency and Standards)
- S7 (Strive for Consistency)

### Evidences

**Scenario A — 200% browser zoom:**

![BUG-A1-003-01.png](../../../screenshots/task01/A1/BUG-A1-003-01.png)

**Scenario B — 320 px mobile viewport:**

![BUG-A1-003-02.png](../../../screenshots/task01/A1/BUG-A1-003-02.png)

### Suggested Fix

Enhance the screen's responsive layout architecture to support both high-zoom and narrow-viewport scenarios:

- **Events Table:** Enclose the `<table>` within a responsive container featuring horizontal scrolling (`overflow-x: auto`). This isolates table scrolling and prevents page-level horizontal scrollbars.
- **Action Bar (Filters & Add Button):** Implement a flex-wrap or CSS Grid layout that gracefully stacks the search bar, filter dropdowns, and "Add Event" button vertically on viewports below 480px.
- **Pagination Component:** Ensure the pagination container utilizes `flex-wrap: wrap` so that page number chips flow onto the next line rather than overflowing their parent container.
- **Header Elements:** Configure the header flex container to wrap its child elements (language toggle, notifications, avatar) to maintain alignment when space is constrained.

## BUG-A1-004 — Rich Text Editor: Multiple interaction defects in "Important Update" dialog

| Field                   | Value                           |
| ----------------------- | ------------------------------- |
| **ID**                  | BUG-A1-004                      |
| **Screen**              | A1: Events List                 |
| **Type**                | Bug                             |
| **Severity**            | 3 — Major Usability Problem     |
| **Priority**            | High                            |
| **Affected Items**      | IA-02-012, IA-02-013, IA-02-014 |
| **Affected Edge Cases** | None                            |

### Description

The Rich Text Editor (RTE) inside the "Important Update" dialog has three distinct interaction defects: (1) certain toolbar buttons (paragraph, text alignment) close the dialog immediately on click instead of applying formatting; (2) after uploading an image into the editor, there is no control to remove it; and (3) pasting a very long unbroken string of text causes the editor content to overflow the dialog boundaries instead of wrapping or scrolling.

### Steps to Reproduce

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

### Expected Behaviour

- Toolbar buttons must apply formatting to selected text without dismissing the dialog.
- After inserting an image, a **Remove** button or icon overlay must appear to let the user delete it before submitting.
- When content exceeds the editor's visible height, the RTE area must either expand up to a defined maximum height and then switch to an internal vertical scrollbar, or always provide an internal scrollbar.

### Actual Behaviour

- Clicking Paragraph / Alignment buttons closes the dialog immediately.
- No mechanism exists to remove an uploaded image from the editor.
- Pasting long text causes the editor content to overflow the dialog without scrolling.

### Heuristic Reference

- N1 (Visibility of System Status)
- N3 (User Control and Freedom)
- N9 (Help Users Recognise, Diagnose, and Recover from Errors)
- NOR5 (Feedback)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/17249yW5i1HOloGnQjsWmu_tUUIRIVu2I/view)

### Suggested Fix

Address the three distinct interaction defects within the Rich Text Editor integration:

- **Toolbar Close Bug:** The modal's backdrop click-listener is improperly intercepting clicks from the RTE toolbar. Ensure that click events on toolbar buttons call `event.stopPropagation()`, or bind the dialog dismissal strictly to explicit `close` buttons and the immediate backdrop overlay layer rather than bubbling events.
- **Image Removal Control:** Enhance the image insertion feature by appending a persistent, overlaying "Remove" (`×`) button on all uploaded image nodes within the editor, granting users the freedom to undo accidental uploads before submission.
- **Content Overflow:** Constrain the editor's vertical growth by setting a `max-height` (e.g., `300px` or a relative viewport height unit) on the RTE content container, coupled with `overflow-y: auto` to enable an internal vertical scrollbar when content exceeds the boundary.

## BUG-A1-005 — Inconsistent badge border styling across table columns

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-005                  |
| **Screen**              | A1: Events List             |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-04-004                   |
| **Affected Edge Cases** | None                        |

### Description

Status badges in the **STATUS** and **TIME STATUS** columns use a border that is darker than the badge background, giving them a visually distinct outline. In contrast, badges in the **TYPE** and **PUBLIC** columns have no border, creating an inconsistent visual language for status indicators across the same screen.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Ensure at least one event row with data in all badge columns is visible.
3. Observe the badges side-by-side:
   - **STATUS** column (e.g., _Published_) — note the border.
   - **TIME STATUS** column (e.g., _Upcoming_) — note the border.
   - **TYPE** column (e.g., _Limited_) — note the absence of a border.
   - **PUBLIC** column (e.g., _Not Public_) — note the absence of a border.
4. Confirm that **STATUS / TIME STATUS** badges have a visible border while **TYPE / PUBLIC** badges do not.

### Expected Behaviour

All badge/chip components across the Events table should share a **unified styling system** — either all have borders or none do, with consistent padding, border-radius, and font weight regardless of which column they appear in.

### Actual Behaviour

Badges in STATUS and TIME STATUS columns have a visible border; badges in TYPE and PUBLIC columns do not, resulting in a visually inconsistent table row.

### Heuristic Reference

- N4 (Consistency and Standards)
- S1 (Strive for Consistency)

### Evidences

![BUG-A1-005.png](../../../screenshots/task01/A1/BUG-A1-005.png)

### Suggested Fix

Establish and apply a unified styling convention for all status indicator badges across the table.

- If the design system intends for badges to possess an outlining border, update the DOM structure or CSS classes for the `TYPE` and `PUBLIC` badges to inherit those border properties.
- Conversely, if the borderless style is intended, strip the border styles from the `STATUS` and `TIME STATUS` badges.
- Consolidate the CSS classes or React/Vue component variants into a single, reusable `<Badge>` component that standardises padding, border-radius, font-weight, and border presence regardless of the column context.

## BUG-A1-006 — Filter dropdowns missing chevron affordance indicator

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-006                  |
| **Screen**              | A1: Events List             |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-02-006                   |
| **Affected Edge Cases** | None                        |

### Description

The **"All Status"** and **"All Time"** filter buttons on the Events List screen look and behave as dropdown menus but display no chevron or downward-arrow icon. Without this standard affordance, first-time users may not recognise that clicking these buttons opens a selection list.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Look at the filter area below the page heading — observe the **"All Status"** and **"All Time"** buttons.
3. Inspect both buttons visually — note the absence of any arrow icon.
4. For comparison, click both buttons to confirm they do open a dropdown list.

### Expected Behaviour

Dropdown trigger buttons must include a visible **downward chevron** or equivalent directional icon to signal that clicking will reveal a list of options, following standard affordance conventions.

### Actual Behaviour

Both filter buttons display only text. No arrow or chevron icon is present, offering no visual cue that the element is a dropdown.

### Heuristic Reference

- NOR2 (Signifiers)
- N6 (Recognition Rather Than Recall)
- N4 (Consistency and Standards)

### Evidences

![BUG-A1-006.png](../../../screenshots/task01/A1/BUG-A1-006.png)

### Suggested Fix

Introduce a clear visual signifier to explicitly communicate the dropdown affordance.

- Append a downward-pointing chevron icon (e.g., an SVG graphic or a corresponding icon font glyph) adjacent to the text labels of both the "All Status" and "All Time" filter buttons.
- For enhanced feedback, implement a subtle CSS transition that rotates the chevron 180 degrees upwards when the dropdown menu is in an open/expanded state.

## BUG-A1-007 — Sidebar collapsed state: Icon-only items have no tooltips

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-007                  |
| **Screen**              | A1: Events List             |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA-03-003                   |
| **Affected Edge Cases** | None                        |

### Description

When the left sidebar is collapsed, menu items are displayed as icon-only buttons. Hovering over any of these icons does not reveal a tooltip showing the section name. Users unfamiliar with the icon set cannot identify which section each icon navigates to without expanding the sidebar.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Locate the **Collapse** button at the bottom of the left sidebar.
3. Click it to collapse the sidebar — menu labels disappear, only icons remain.
4. Move the mouse cursor over each icon (e.g., the calendar icon for Events Management, the user icon for Users Management).
5. Wait 1–2 seconds while hovering on each icon.
6. Observe that **no tooltip appears** next to or below the hovered icon.

### Expected Behaviour

When the sidebar is in collapsed/icon-only mode, hovering over each icon must display a tooltip (e.g., _"Event Management"_) to identify the section without requiring the sidebar to be re-expanded.

### Actual Behaviour

Hovering over icons in the collapsed sidebar produces only a visual highlight with no tooltip text.

### Heuristic Reference

- N6 (Recognition Rather Than Recall)
- N4 (Consistency and Standards)
- NOR2 (Signifiers)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1vkOPLtZ5V3BZakE7xKdP-aHe34QWDWmI/view)

### Suggested Fix

Implement progressive disclosure tooltips for the collapsed sidebar navigation state.

- Attach a tooltip component (via CSS-only techniques or a JavaScript library) to every icon-only navigation item in the sidebar.
- Configure the tooltips to display the full, localized section names (e.g., "Event Management") upon a `mouseenter` or `focus` event, ideally with a brief debounce delay (e.g., 200–300ms) to prevent flickering during rapid cursor movement.
- Ensure the tooltip positioning logic anchors them to the right of the respective icons, guaranteeing they do not obscure the icon itself.

## BUG-A1-008 — LOCATION column: Map pin icon not vertically aligned with address text

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A1-008           |
| **Screen**              | A1: Events List      |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-01-017            |
| **Affected Edge Cases** | None                 |

### Description

In the Events table, the map pin icon that precedes the location address text in the **LOCATION** column is not vertically centred with the text on the same line, creating a slight visual misalignment that reduces the polish of the interface.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Scroll right in the Events table to locate the **LOCATION** column.
3. Find a row that has a location address.
4. Visually inspect the alignment between the **map pin icon** and the adjacent **address text**.
5. Observe that the icon sits above the vertical midpoint of the text.

### Expected Behaviour

The map pin icon and the address text must be **vertically centred** on the same horizontal axis (middle-aligned), giving the cell a clean, unified appearance.

### Actual Behaviour

The map pin icon is visually offset from the address text's vertical centre, resulting in a jagged or uneven appearance within the table cell.

### Heuristic Reference

- N4 (Consistency and Standards)
- S1 (Strive for Consistency)

### Evidences

![BUG-A1-008.png](../../../screenshots/task01/A1/BUG-A1-008.png)

### Suggested Fix

Correct the vertical alignment mismatch within the Location column cells.

- Transform the container holding the map pin icon and the address text into a Flexbox row (`display: flex`) and apply `align-items: center` to guarantee perfect horizontal axis alignment.
- If avoiding Flexbox, ensure both the icon vector/font and the adjacent text span share matching `vertical-align: middle` and `line-height` properties.

## BUG-A1-009 — "Add Event" button missing active/pressed visual state

| Field                   | Value                |
| ----------------------- | -------------------- |
| **ID**                  | BUG-A1-009           |
| **Screen**              | A1: Events List      |
| **Type**                | Usability            |
| **Severity**            | 1 — Cosmetic Problem |
| **Priority**            | Low                  |
| **Affected Items**      | IA-04-014            |
| **Affected Edge Cases** | None                 |

### Description

The primary **"+ Add Event"** button correctly shows a hover state when the cursor enters it, but does not provide any active/pressed visual feedback when the button is physically clicked and held. Other interactive elements on the page (table rows, pagination buttons) respond to click-hold, but this primary action button does not.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. Move the mouse over the **"+ Add Event"** button (top-right area) — verify the hover state (colour change) is present.
3. Click and **hold** the mouse button down on the **"+ Add Event"** button without releasing.
4. Observe the button's visual state while the mouse button is held.
5. Release the mouse.
6. Observe that no **active / sunken / ripple** visual effect appeared during the click-hold.

### Expected Behaviour

While the mouse button is held down on the "Add Event" button, a distinct **active/pressed visual state** must be visible — e.g., a slightly darker shade, an inset/sunken shadow, or a Material-style ripple effect — to confirm to the user that their click has been registered.

### Actual Behaviour

No visual change occurs when the button is clicked and held. The hover state is the same as the pressed state, providing no confirmation that the click input was received.

### Heuristic Reference

- N1 (Visibility of System Status)
- NOR5 (Feedback)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1A2L2xoxe8xblIDdqQTaHwE6UOhjPVkSq/view)

### Suggested Fix

Provide immediate, tactile visual feedback when the primary action button is clicked.

- Introduce an active (pressed) visual state to the "Add Event" button utilizing the CSS `:active` pseudo-class (or equivalent framework utility variant).
- The styling adjustment should clearly differentiate a click from a hover — for instance, by briefly darkening the background colour further, applying an inset box-shadow to simulate depth, or implementing a ripple animation effect.

## BUG-A1-010 — Dropdown layout and sizing defects (Overflow and excessive whitespace)

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A1-010                  |
| **Screen**              | A1: Events List             |
| **Type**                | Bug                         |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | None                        |
| **Affected Edge Cases** | EC-A1-006                   |

### Description

The dropdown menus used for table column filters exhibit noticeable layout anomalies. Specifically, the EVENT TYPES filter dropdown fails to correctly constrain its internal components, causing the search input and action buttons to overflow past the right boundary. Additionally, the TIME and ACADEMIC CONTEXT dropdowns render with excessive empty whitespace, indicating improperly configured or hardcoded container dimensions.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events.
2. In the Events table header, click the filter icon next to **EVENT TYPES**.
3. Observe the search input and Apply/Clear buttons overflowing the dropdown container's right edge.
4. Close the dropdown and click the filter icon next to **TIME** and then **ACADEMIC CONTEXT**.
5. Observe the excessive empty space within these dropdown containers.

### Expected Behaviour

Dropdown containers must adapt dynamically to their content with appropriate constraints (e.g., `min-width`, `max-width`, `max-height`). Internal elements must remain strictly within the container bounds without overflowing, and the container footprint should be snug without unjustified or excessive empty whitespace.

### Actual Behaviour

The EVENT TYPES content overflows its container boundaries horizontally. The TIME and ACADEMIC CONTEXT containers render with excessive empty vertical/horizontal space.

### Heuristic Reference

- N4 (Consistency and Standards)
- N8 (Aesthetic and Minimalist Design)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1p0D4ieoHwJtt9UIB3yzIAW6_wmtwTbs_/view)

### Suggested Fix

Review and refine the CSS architecture governing the layout of the filter dropdown containers and their descendants.

- **For Overflow (EVENT TYPES):** Ensure the parent dropdown container possesses a sufficient `min-width` to accommodate its child elements, or configure the child elements (like the search input) to utilize `width: 100%` alongside `box-sizing: border-box` to prevent them from breaking out of the parent bounds.
- **For Excessive Whitespace (TIME, ACADEMIC CONTEXT):** Remove any hardcoded `height` or `width` properties that force the container to be artificially large. Allow the container to size dynamically based on its inner content, utilizing `max-height` coupled with `overflow-y: auto` exclusively for constraining long list areas.
