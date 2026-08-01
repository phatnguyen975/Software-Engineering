# Example: Bug Report

This example demonstrates the expected format and grouping of bug reports. The file name is `bug-report-A2.md`.

```markdown
# Bug Report — Add/Edit Event Form (A2)

> **Source:** `execution-A2.md` · `edge-cases-A2.md`  
> **Total bug groups:** 5  
> **Severity:** Severity 4 (1) · Severity 3 (2) · Severity 2 (1) · Severity 1 (1) · Severity 0 (0)

## BUG-A2-001 — Form validation fires only on submit, not on field blur

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-001                  |
| **Screen**              | A2: Add/Edit Event Form     |
| **Type**                | Usability                   |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA02-003                    |
| **Affected Edge Cases** | EC-A2-001                   |

### Description

All form fields on the Add/Edit Event Form only surface validation errors after the user clicks the Submit/Publish button. No inline feedback is provided when the user leaves a required field empty or enters invalid data.

### Steps to Reproduce

1. Navigate to `https://example.com/admin/events/create`.
2. Click into the "Event Title" field.
3. Leave it empty and press Tab to move to the next field.
4. Observe the field — no error appears.
5. Continue filling out remaining fields, leaving "Event Title" empty.
6. Click "Publish" button.
7. Observe errors appearing only at this point.

### Expected Behaviour

An inline error message appears adjacent to the "Event Title" field as soon as focus leaves the field (on blur), informing the user before they complete the entire form.

### Actual Behaviour

No error feedback on blur. A summary error list appears at the top of the form only after the user attempts to submit, requiring the user to scroll back up to identify which fields failed.

### Heuristic Reference

- N9 (Help Users Recognise, Diagnose, and Recover from Errors)
- S5 (Offer Simple Error Handling)

### Suggested Fix

Implement `onBlur` validation for all required fields. Display inline error messages directly below each affected field. Retain submit-time validation as a second check.

## BUG-A2-002 — Required field indicators absent on all required fields

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-002                  |
| **Screen**              | A2: Add/Edit Event Form     |
| **Type**                | Usability                   |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA02-002                    |
| **Affected Edge Cases** | None                        |

### Description

No asterisk (`*`) or equivalent required-field indicator is displayed on any of the three required fields (Event Title, Start Date, Thumbnail Image), leaving users unaware that these fields are mandatory until a submit-time error appears.

### Steps to Reproduce

1. Navigate to `https://example.com/admin/events/create`.
2. Inspect the labels for "Event Title", "Start Date", and "Thumbnail Image" fields.
3. Observe that no asterisk or "required" indicator is present.

### Expected Behaviour

Each required field displays an asterisk (`*`) adjacent to its label before the user interacts with the form.

### Actual Behaviour

No required indicator visible on any field. The requirement is only discoverable after a failed submit attempt.

### Heuristic Reference

- NOR2 (Signifiers)
- N6 (Recognition Rather Than Recall)
- WCAG3.3.2 (Labels or Instructions)

### Suggested Fix

Add a red asterisk (`*`) to the label of each required field. Include a legend note (`* Required field`) near the top or bottom of the form.

## BUG-A2-003 — File upload silently ignores invalid format and oversized files

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-003                  |
| **Screen**              | A2: Add/Edit Event Form     |
| **Type**                | Bug                         |
| **Severity**            | 3 — Major Usability Problem |
| **Priority**            | High                        |
| **Affected Items**      | IA02-006                    |
| **Affected Edge Cases** | None                        |

### Description

Uploading an unsupported file format (`.pdf`) or an oversized image to the Thumbnail upload field produces no user feedback — the file input silently clears without explaining what went wrong.

### Steps to Reproduce

1. Navigate to `https://example.com/admin/events/create`.
2. Click the Thumbnail upload area.
3. Select a `.pdf` file.
4. Observe the upload area — it clears with no error message.
5. Repeat with a valid image file exceeding the size limit.
6. Same result (silent failure).

### Expected Behaviour

An error message appears adjacent to the upload area specifying:

- Accepted formats (e.g. "Only JPG and PNG files are supported")
- Size limit (e.g. "Maximum file size is 5 MB")

### Actual Behaviour

File input clears silently. No error, no warning, no indication of what was rejected or why. The user must guess whether the upload succeeded or failed.

### Heuristic Reference

- N9 (Help Users Recognise, Diagnose, and Recover from Errors)
- N1 (Visibility of System Status)

### Suggested Fix

Validate file type and size client-side on file selection. Display an inline error message immediately below the upload control if validation fails. Do not clear the input without explanation.

## BUG-A2-004 — Success toast relies solely on colour to convey meaning

| Field                   | Value                                     |
| ----------------------- | ----------------------------------------- |
| **ID**                  | BUG-A2-004                                |
| **Screen**              | A2: Add/Edit Event Form                   |
| **Type**                | Bug                                       |
| **Severity**            | 4 — Usability Catastrophe (Accessibility) |
| **Priority**            | High                                      |
| **Affected Items**      | IA04-003                                  |
| **Affected Edge Cases** | None                                      |

### Description

The success toast notification rendered after Save Draft and Publish actions uses only a green background to indicate success. There is no accompanying icon (e.g. checkmark) or explicit text prefix (e.g. "Success:") to convey the state to colour-blind users.

### Steps to Reproduce

1. Navigate to `https://example.com/admin/events/create`.
2. Fill all required fields and click "Save Draft".
3. Observe the toast notification visually.
4. Verify the presence of icons or text prefixes.
5. Note that only colour is used to distinguish it from an error or info toast.

### Expected Behaviour

Feedback messages must not rely solely on colour to convey meaning. They should include an explicit icon or text prefix to ensure accessibility.

### Actual Behaviour

The toast is simply a solid green box with the message text, which is indistinguishable from other toast types for users with certain forms of colour blindness.

### Heuristic Reference

- WCAG1.4.1 (Use of Color)
- N1 (Visibility of System Status)
- NOR5 (Feedback)

### Suggested Fix

Add a distinct visual icon (e.g., a checkmark) to the left of the success message, and ensure error toasts also have their own distinct icons (e.g., an exclamation mark).

## BUG-A2-005 — i18n incomplete with three field labels remain in English after language switch

| Field                   | Value                       |
| ----------------------- | --------------------------- |
| **ID**                  | BUG-A2-005                  |
| **Screen**              | A2: Add/Edit Event Form     |
| **Type**                | Bug                         |
| **Severity**            | 2 — Minor Usability Problem |
| **Priority**            | Med                         |
| **Affected Items**      | IA01-003                    |
| **Affected Edge Cases** | None                        |

### Description

Switching the UI language from English to Vietnamese leaves three field labels and all placeholder text in English, breaking the i18n consistency of the form.

### Steps to Reproduce

1. Navigate to `https://example.com/admin/events/create` in English.
2. Click the language toggle in the header and select Vietnamese (VI).
3. Observe the form labels.

### Expected Behaviour

All form labels, placeholder text, button labels, and validation messages switch to Vietnamese immediately after the language toggle is activated.

### Actual Behaviour

Labels "Event Title", "Start Date", and "Registration Closes" remain in English. All placeholder text in the description field also remains in English. Other labels (e.g. "Thumbnail", "Publish") switch correctly.

### Heuristic Reference

- N4 (Consistency and Standards)
- S1 (Strive for Consistency)

### Suggested Fix

Audit the i18n translation keys for this form. The three affected labels and all placeholder strings are missing Vietnamese translations in the i18n resource file. Add the missing translations and verify all form text switches on language toggle.
```
