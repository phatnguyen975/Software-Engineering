# Bug Report — Participants & Reviews Approval (A4)

> **Source:** `execution-A4.md` · `edge-cases-A4.md`  
> **Tester:** Nguyễn Tấn Phát — 23127449  
> **Date:** 2026-07-30  
> **Total bug groups:** 7  
> **Severity:** Severity 4 (2) · Severity 3 (2) · Severity 2 (1) · Severity 1 (2) · Severity 0 (0)

## BUG-A4-001 — Toast notifications are completely absent after Apply actions

| Field                   | Value                                                 |
| ----------------------- | ----------------------------------------------------- |
| **ID**                  | BUG-A4-001                                            |
| **Screen**              | A4: Participants & Reviews Approval                   |
| **Type**                | Bug                                                   |
| **Severity**            | 3 — Major Usability Problem                           |
| **Priority**            | High                                                  |
| **Affected Items**      | IA-04-001, IA-04-002, IA-04-003, IA-04-009, IA-04-010 |
| **Affected Edge Cases** | None                                                  |

### Description

No toast notification appears at all when the Admin clicks the Apply button in the Review Lecturers or Review Students tabs — whether the action succeeds or fails. The table state updates silently (rows are removed upon approval), but there is no visual confirmation to the user that anything happened.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Click the **Review Students** tab.
3. Change one student's ACTION segmented control from Pending to Approved.
4. Click the **Apply** button.
5. Observe the page — the student row disappears, but no toast notification appears anywhere on the screen.
6. To test error feedback: open DevTools → Network → block the Apply API endpoint.
7. Click **Apply** again. Observe that no error toast appears.

### Expected Behaviour

- **On success:** a green success toast appears in a consistent screen position (e.g., top-right), auto-dismisses after ≥ 5 seconds, and includes a dismiss (×) button.
- **On failure:** a red error toast appears, stays for ≥ 10 seconds (or does not auto-dismiss), with a user-friendly message and no raw stack trace.

### Actual Behaviour

No toast notification of any kind appears (success or error). The UI state updates silently, giving the admin zero visual confirmation that the Apply action was processed, succeeded, or failed.

### Heuristic Reference

- N1 (Visibility of System Status)
- NOR5 (Feedback)
- S5 (Offer Simple Error Handling)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1pNbDn3dsqkO46pjmrAdqpxvAXMA3USl-/view)

### Suggested Fix

Implement a toast/notification library call on the Apply button's API response handler. Trigger a green success toast on `200 OK` and a red error toast on any non-2xx response. Ensure the toast system is globally connected and not conditionally disabled for this screen.

## BUG-A4-002 — Text contrast ratio fails WCAG AA for inactive tab labels

| Field                   | Value                                     |
| ----------------------- | ----------------------------------------- |
| **ID**                  | BUG-A4-002                                |
| **Screen**              | A4: Participants & Reviews Approval       |
| **Type**                | Bug                                       |
| **Severity**            | 4 — Usability Catastrophe (Accessibility) |
| **Priority**            | High                                      |
| **Affected Items**      | IA-01-003                                 |
| **Affected Edge Cases** | None                                      |

### Description

The text color of inactive (non-selected) tab labels on the A4 screen does not meet the WCAG 2.2 Level AA minimum contrast ratio of 4.5:1 for normal text. Users with low vision may be unable to read inactive tab labels clearly.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Open DevTools (F12).
3. Click on one of the non-active tab labels (e.g., "Review Lecturers" or "Review Students" when not selected).
4. Use the DevTools color picker / Elements panel to inspect the computed text color and background color.
5. Verify the contrast ratio — it fails to meet the AA standard (4.5:1).

### Expected Behaviour

All visible text, including inactive tab labels, must achieve a minimum contrast ratio of 4.5:1 against the background (WCAG 2.2 SC 1.4.3).

### Actual Behaviour

The inactive tab text uses a muted color that does not meet the 4.5:1 ratio threshold against the tab bar background.

### Heuristic Reference

- WCAG1.4.3 (Contrast Minimum — Level AA)
- N4 (Consistency and Standards)

### Evidences

![BUG-A4-002.png](../../../screenshots/task01/A4/BUG-A4-002.png)

### Suggested Fix

Increase the inactive tab text color to a darker shade that achieves at least a 4.5:1 contrast ratio against the tab bar background. Use a tool such as the [WebAIM Contrast Checker](https://webaim.org/resources/contrastchecker/) to verify the new color before applying.

## BUG-A4-003 — Line-height is below the WCAG 1.4.12 minimum of 1.5×

| Field                   | Value                                     |
| ----------------------- | ----------------------------------------- |
| **ID**                  | BUG-A4-003                                |
| **Screen**              | A4: Participants & Reviews Approval       |
| **Type**                | Bug                                       |
| **Severity**            | 4 — Usability Catastrophe (Accessibility) |
| **Priority**            | High                                      |
| **Affected Items**      | IA-01-007                                 |
| **Affected Edge Cases** | None                                      |

### Description

Body text on this screen has a computed `line-height` of 20px against a `font-size` of 14px, yielding a ratio of approximately 1.43. This falls below the WCAG 2.2 SC 1.4.12 (Text Spacing) requirement that line height must be at least 1.5 times the font size.

### Steps to Reproduce

1. Navigate to the A4 screen and open the Registrants tab.
2. Open DevTools → Elements → select a body text cell (e.g., an email address value).
3. In the Computed Styles panel, read the `font-size` and `line-height` values.
4. Calculate the ratio: `line-height / font-size` = `20 / 14` ≈ 1.43.
5. Verify this is below the required minimum of 1.5.

### Expected Behaviour

Line height must be at least 1.5 times the font size (WCAG 2.2 SC 1.4.12). For a 14px font, line-height must be ≥ 21px.

### Actual Behaviour

`line-height: 20px` with `font-size: 14px` gives a ratio of ~1.43, which violates the Text Spacing accessibility requirement.

### Heuristic Reference

- WCAG1.4.12 (Text Spacing — Level AA)

### Evidences

![BUG-A4-003.png](../../../screenshots/task01/A4/BUG-A4-003.png)

### Suggested Fix

Update the global or table body text CSS class to set `line-height: 1.5` (or a fixed value ≥ 21px for 14px text). Prefer using the unitless multiplier (`1.5`) over a fixed pixel value so the ratio holds when users override font sizes.

## BUG-A4-004 — Layout breaks at 200% zoom and 320 px viewport width

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-004                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Bug                                 |
| **Severity**            | 3 — Major Usability Problem         |
| **Priority**            | High                                |
| **Affected Items**      | IA-01-015                           |
| **Affected Edge Cases** | None                                |

### Description

At 200% browser zoom, the pagination control is hidden by its container overflow, and the tab bar and action buttons overflow their container causing a horizontal scrollbar to appear on the entire page. At 320 px viewport width, tab content disappears entirely, leaving only the button controls visible.

### Steps to Reproduce

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

### Expected Behaviour

The page remains fully usable at 200% zoom (no horizontal scrollbar, all controls visible) and at 320 px viewport width (all tab content readable, possibly in a simplified layout).

### Actual Behaviour

- **At 320 px:** entire tab content body is hidden, the screen is unusable.
- **At 200% zoom:** pagination is clipped and invisible; tabs + action buttons overflow container causing horizontal scrolling.

### Heuristic Reference

- WCAG1.4.10 (Reflow — Level AA)
- N7 (Flexibility and Efficiency of Use)

### Evidences

**At 320 px:**

![BUG-A4-004-01.png](../../../screenshots/task01/A4/BUG-A4-004-01.png)

**At 200% zoom:**

![BUG-A4-004-02.png](../../../screenshots/task01/A4/BUG-A4-004-02.png)

### Suggested Fix

Audit the Tab bar and Action buttons container for `overflow: hidden` or fixed-width constraints that prevent reflow. Use `overflow-x: auto` on table containers and ensure tab content uses responsive CSS (`flex-wrap`, `min-width: 0`) rather than fixed widths. Test using the browser's built-in responsive mode before release.

## BUG-A4-005 — Inconsistent spacing and padding in the Top Section

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-005                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Usability                           |
| **Severity**            | 1 — Cosmetic Problem                |
| **Priority**            | Low                                 |
| **Affected Items**      | IA-01-017                           |
| **Affected Edge Cases** | None                                |

### Description

The Top Section of the A4 screen contains inconsistent spacing and padding: the gap between the `PUBLISHED` badge and the Edit Event button differs from the gap between Edit Event and Important Update; the Back button (arrow-left icon) has uneven internal padding; and the container wrapping the title, badges, action buttons, and tab bar lacks uniform padding on all sides.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Visually compare the horizontal gap between: (a) the `PUBLISHED` badge and the Edit Event button, and (b) the Edit Event button and the Important Update button.
3. Open DevTools → Elements and inspect the Back (arrow-left) button — check the `padding` values on all four sides.
4. Inspect the outer container holding the title, action buttons, and tab bar — verify `padding` is uniform on all sides.

### Expected Behaviour

All elements in the Top Section maintain consistent, uniform spacing between them. The Back button has equal padding on all sides. The container has balanced padding throughout.

### Actual Behaviour

- Gap between `PUBLISHED` badge and Edit Event ≠ gap between Edit Event and Important Update.
- Back button has uneven padding (e.g., more padding on one axis than the other).
- The outer container has non-uniform padding causing visual imbalance.

### Heuristic Reference

- S1 (Strive for Consistency)
- N4 (Consistency and Standards)

### Evidences

![BUG-A4-005.png](../../../screenshots/task01/A4/BUG-A4-005.png)

### Suggested Fix

Use a consistent spacing token (e.g., `gap: 8px` or `gap: 12px`) in the flex container holding the Top Section elements. Apply equal `padding` to the Back button using a shared button style. Align the container padding with the design system's standard page-level padding value.

## BUG-A4-006 — Icon-only buttons lack text tooltips on hover

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-006                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Usability                           |
| **Severity**            | 2 — Minor Usability Problem         |
| **Priority**            | Med                                 |
| **Affected Items**      | IA-04-015                           |
| **Affected Edge Cases** | None                                |

### Description

Icon-only buttons on the A4 screen (Back arrow-left button and Filter icon in the Registrants tab) do not display any text tooltip when hovered. Without a tooltip, users cannot discern the button's action from the icon alone, especially for less-recognizable icons.

### Steps to Reproduce

1. Navigate to https://prod-dev.ems-fitus.cloud/dashboard/admin/events/views?id=39 and log in with admin credentials.
2. Hover the mouse cursor over the **Back** (arrow-left) icon button next to the event title for 2 seconds.
3. Observe — no tooltip appears.
4. Open the Registrants tab and hover over the **Filter** icon button for 2 seconds.
5. Observe — no tooltip appears.

### Expected Behaviour

Each icon-only button displays a concise text tooltip (via `title` attribute or a floating tooltip element) after a short hover delay, describing the button's action (e.g., "Go back to Event List", "Filter registrants").

### Actual Behaviour

No tooltip appears on any icon-only button. Users must infer the button action from the icon shape alone with no textual confirmation.

### Heuristic Reference

- N6 (Recognition Rather Than Recall)
- NOR2 (Signifiers)

### Evidences

[Link to Demo Video](https://drive.google.com/file/d/1q9bpLme0w9GX01a82NighW3x8OZ5mjIi/view)

### Suggested Fix

Add a `title="Go back to Event List"` attribute to the Back icon button and a `title="Filter registrants"` (or equivalent) to the Filter icon button. Alternatively, implement a CSS/JS tooltip component tied to an `aria-label` attribute to ensure both visual and accessible tooltip behaviour.

## BUG-A4-007 — ACTION segmented control has inconsistent icon usage across segments

| Field                   | Value                               |
| ----------------------- | ----------------------------------- |
| **ID**                  | BUG-A4-007                          |
| **Screen**              | A4: Participants & Reviews Approval |
| **Type**                | Usability                           |
| **Severity**            | 1 — Cosmetic Problem                |
| **Priority**            | Low                                 |
| **Affected Items**      | None                                |
| **Affected Edge Cases** | EC-A4-005                           |

### Description

In the ACTION segmented control on both the Review Lecturers and Review Students tabs, the "Reject" segment displays a leading icon, while the "Pending" and "Approved" segments contain only text. This breaks the visual consistency of the control.

### Steps to Reproduce

1. Navigate to the A4 screen and open the **Review Students** tab.
2. Observe the ACTION segmented control for any student row.
3. Compare the three segments: **Reject** (left), **Pending** (middle), **Approved** (right).
4. Verify that "Reject" has a visible leading icon, while "Pending" and "Approved" have no icon.
5. Repeat in the **Review Lecturers** tab.

### Expected Behaviour

All segments within the ACTION control follow a consistent design pattern — either all three segments include a leading icon, or none do.

### Actual Behaviour

Only the "Reject" segment has a leading icon. "Pending" and "Approved" segments use text only, creating a visually asymmetric control.

### Heuristic Reference

- N4 (Consistency and Standards)
- S1 (Strive for Consistency)

### Evidences

![BUG-A4-007.png](../../../screenshots/task01/A4/BUG-A4-007.png)

### Suggested Fix

Choose one of the following consistent approaches and apply it uniformly across all three segments:

- **Option A:** Remove the icon from the Reject segment so all three segments are text-only.
- **Option B:** Add appropriate icons to all three segments (e.g., an X icon for Reject, a clock icon for Pending, a checkmark icon for Approved).
