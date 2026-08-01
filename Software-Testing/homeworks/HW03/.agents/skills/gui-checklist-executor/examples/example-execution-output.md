# Example: Execution Checklist & Edge Cases

> **Screen:** Add/Edit Event Form (A2)  
> **SUT:** Generic web admin panel  
> **Mode:** BrowserMCP (Mode 1)

This example demonstrates the expected format and quality of the execution checklist and edge case list. It shows all four `Result` types with appropriate `Notes`.

## File: execution-A2.md

```markdown
# Execution Checklist — Add/Edit Event Form (A2)

> **Checklist source:** docs/checklist/shared-gui-checklist.md  
> **Executed:** 2026-07-20

## IA-01 — General UI Standards

| Item ID | Description | How-to-Test | Result | Notes |
|---|---|---|---|---|
| IA01-001 | All form sections and fields are aligned on a consistent visual grid — no element is visually offset from its group. | 1. Navigate to https://example.com/admin/events/create.<br>2. Observe the layout of the Title, Date Range, and Registration sections.<br>3. Verify all input fields and their labels share a consistent left margin. | Pass | All input fields left-aligned at a consistent 24px margin. Section headers align with field labels. |
| IA01-002 | While the page is loading or an async operation is in progress, a visible loading indicator is present. | 1. Navigate to https://example.com/admin/events/create on a slow connection (or throttle in DevTools).<br>2. Observe the area between navigation and form render. | Need Human | Loading state is transient and requires network throttling to observe. Use DevTools Network panel to simulate slow 3G. |
| IA01-003 | All UI text switches language completely when the language toggle is activated. | 1. Locate the language toggle in the header.<br>2. Switch from EN to VI.<br>3. Inspect all visible labels, placeholders, and button text on this form. | Fail | Three field labels ("Event Title", "Start Date", "Registration Closes") remained in English after switching to VI. Placeholder text in the description field also did not switch. |
| IA01-004 | All body text and interactive control labels meet WCAG 2.2 contrast minimum (≥ 4.5:1 for normal text). | 1. Identify the lowest-contrast text element visible on the form (typically disabled field text or helper text).<br>2. Measure contrast ratio using a contrast checker tool against its background. | Need Human | Contrast ratio measurement requires a dedicated tool (browser extension or axe-core). BrowserMCP cannot compute contrast ratios from rendered colours. |

## IA-02 — Forms

| Item ID | Description | How-to-Test | Result | Notes |
|---|---|---|---|---|
| IA02-001 | Every form input has a visible text label positioned above or beside the field — placeholder text alone is not used as the label. | 1. Navigate to https://example.com/admin/events/create.<br>2. For each input field, verify a visible label text element exists outside the field boundary.<br>3. Click into a field and verify the label does not disappear. | Pass | All 8 input fields have visible persistent labels above the field. No label-as-placeholder instances found. |
| IA02-002 | Required form fields display a consistent required indicator (e.g. asterisk *) adjacent to the label. | 1. Inspect each field label on the form.<br>2. Verify required fields display an asterisk or equivalent indicator.<br>3. Verify optional fields do not display the indicator. | Fail | Fields "Event Title", "Start Date", and "Thumbnail Image" are required (confirmed by submit-time validation) but display no asterisk or required indicator. |
| IA02-003 | Validation feedback triggers on field blur — not solely on form submission. | 1. Click into the "Event Title" field.<br>2. Leave it empty and click into the next field (tab away).<br>3. Observe whether an inline error appears before submitting. | Fail | No validation feedback on blur. Error appears only after clicking the Submit button. Affects all required fields on this form. |
| IA02-004 | The date picker rejects logically invalid date ranges (end date before start date). | 1. Set the Start Date to 2026-08-10.<br>2. Set the End Date to 2026-08-05 (before start).<br>3. Attempt to save or observe whether an error is shown. | Pass | Date picker prevents selecting an end date earlier than start date. Calendar highlights invalid dates in grey and prevents selection. |
| IA02-005 | File upload displays a preview after a file is selected, and allows the user to remove it before submission. | 1. Click the Thumbnail upload area.<br>2. Select a valid image file.<br>3. Verify a preview thumbnail appears.<br>4. Click the remove/clear button.<br>5. Verify the upload area returns to empty state. | Pass | Preview appears immediately after upload. Remove button (×) is visible and returns the area to initial state correctly. |
| IA02-006 | File upload validates format and size — unsupported formats and oversized files are rejected with a clear error. | 1. Attempt to upload a .pdf file to the Thumbnail field.<br>2. Attempt to upload an image file exceeding the stated size limit. | Fail | Uploading a .pdf file is silently ignored — no error message shown. File input clears without feedback. Oversized image also silently fails. |

## IA-03 — Navigation

| Item ID | Description | How-to-Test | Result | Notes |
|---|---|---|---|---|
| IA03-001 | All interactive elements are reachable and operable using the Tab key, in a logical order following the visual layout. | 1. Click into the page (outside any input).<br>2. Press Tab repeatedly and observe focus movement.<br>3. Verify focus moves through: Title → Start Date → End Date → Thumbnail → Save Draft → Publish, in reading order. | Pass | Tab order follows the visual top-to-bottom layout. All 12 interactive elements are reachable by Tab. |
| IA03-002 | A visible keyboard focus indicator is present on whichever element currently holds focus. | 1. Tab through the form elements.<br>2. Observe whether a visible outline or highlight is present on the focused element at each step. | Pass | Blue outline (2px solid) visible on all focusable elements. Outline does not disappear at any point in the Tab sequence. |
| IA03-003 | Drag-and-drop reorder shows visual feedback during drag and persists the new order after drop. | 1. Identify any drag-and-drop reorderable list on this screen.<br>2. Drag an item to a new position and observe visual feedback.<br>3. Drop and verify the new order is reflected. | NA | Confirmed: the Add/Edit Event Form does not contain a drag-and-drop reorderable list. The Registration Roles section uses toggle switches, not drag-and-drop. |

## IA-04 — Feedback / State

| Item ID | Description | How-to-Test | Result | Notes |
|---|---|---|---|---|
| IA04-001 | After clicking Save Draft or Publish, a toast or notification confirms the outcome with specific language. | 1. Fill all required fields with valid data.<br>2. Click "Save Draft".<br>3. Observe whether a notification appears and read its content. | Pass | Green toast appears top-right: "Event saved as draft." Auto-dismisses after 4 seconds. Also tested Publish: "Event published successfully." Both messages are specific. |
| IA04-002 | Destructive actions require explicit confirmation before executing. | 1. Look for any delete or irreversible action on this screen.<br>2. Click it and observe whether a confirmation dialog appears. | NA | The Add/Edit Event Form does not contain a delete action. Delete is performed from the Events list screen. |
| IA04-003 | Success and error messages do not rely solely on colour and include distinct icons. | 1. Trigger a success toast notification.<br>2. Visually verify it contains an icon (e.g. checkmark).<br>3. Repeat for error toasts (e.g. exclamation mark). | Fail | Success toast relies only on a green background. There is no icon or text prefix to distinguish it for colour-blind users. |
```

## File: edge-cases-A2.md

```markdown
# Edge Cases — Add/Edit Event Form (A2)

> **Executed:** 2026-07-20

## IA-02 — Forms

| EC ID | Scenario | How-to-Test | Expected Outcome | Result | Notes |
|---|---|---|---|---|---|
| EC-A2-001 | All required fields left empty on submit | 1. Navigate to https://example.com/admin/events/create.<br>2. Leave all fields empty.<br>3. Click "Publish". | All required fields display inline error messages; form does not submit. | Fail | Errors appear at top of form as a summary list, not adjacent to individual fields. Fields are not highlighted. |
| EC-A2-002 | Event title with only whitespace | 1. Enter three spaces in the Event Title field.<br>2. Click Save Draft. | Validation error: field treated as empty. | Fail | Form saves successfully with a title of "   " (three spaces). Backend does not sanitise whitespace-only input. |
| EC-A2-003 | Thumbnail upload with incorrect aspect ratio | 1. Upload a square (1:1) image to the Thumbnail field (which requires 4:3). | Upload rejected or warning shown indicating the required 4:3 ratio. | Need Human | Could not determine aspect ratio enforcement from BrowserMCP alone. Upload accepted a square image without warning — requires human confirmation that aspect ratio validation is expected. |
| EC-A2-004 | Double-click on Publish button | 1. Fill all required fields.<br>2. Double-click the Publish button rapidly. | Event created only once; no duplicate. | Pass | Button is disabled immediately after first click. Second click has no effect. Only one event created (confirmed by navigating to events list). |
| EC-A2-005 | Browser Back after partial form fill | 1. Fill 3 of 5 required fields.<br>2. Press browser Back button.<br>3. Return using browser Forward. | Either unsaved-changes warning is shown, or form state is preserved. | Fail | No unsaved-changes warning. On Back, form state is lost without notification. Forward returns to an empty form. |
```
