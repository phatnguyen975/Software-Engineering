# Defect Taxonomy — Compatibility Matrix Runner

Standard defect types for compatibility testing. Use these exact terms in the Notes field of `matrix-results.md` to ensure consistent classification across cells.

## Defect Type Reference

| Defect Type                       | Code       | Definition                                                                                            | Example                                                                               |
| --------------------------------- | ---------- | ----------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| **Layout overflow**               | OVERFLOW   | Content extends beyond its container or the viewport, causing horizontal scrollbar or clipped content | Form exceeds viewport width on Phone; user must scroll horizontally to see all fields |
| **Element overlap**               | OVERLAP    | Two or more elements render on top of each other, making one or both unusable                         | Dropdown menu renders behind a sticky header; options are partially obscured          |
| **Broken layout**                 | LAYOUT     | Page structure collapses or rearranges in an unintended way                                           | Three-column grid renders as single column on Desktop at 1920px; sidebar disappears   |
| **Unreadable text**               | TEXT       | Text is too small, too low contrast, or clipped to be read                                            | Button label truncated to "Pub..." instead of "Publish"; no tooltip available         |
| **Broken image**                  | IMAGE      | Image fails to load or renders at wrong dimensions                                                    | Thumbnail shows broken image icon; banner image stretches to full page width          |
| **Non-responsive control**        | CONTROL    | Interactive element does not respond to click/tap or is completely invisible                          | Submit button invisible on iOS Safari; date picker not openable on Firefox Android    |
| **Responsive breakpoint failure** | BREAKPOINT | Layout does not adapt correctly at the specified viewport width                                       | "Desktop" layout remains at 390px Phone viewport; no mobile layout applied            |
| **Font rendering**                | FONT       | Text renders with wrong typeface, weight, or spacing                                                  | Headings display in system serif font instead of the intended sans-serif              |
| **Z-index error**                 | ZINDEX     | Element is hidden behind another due to incorrect stacking order                                      | Modal dialog renders behind the sidebar navigation overlay                            |
| **JavaScript error**              | JSERROR    | A visible JavaScript error or blank section caused by a script failure                                | White empty section where the event list should appear; console error present         |
| **CSS property not supported**    | CSS-COMPAT | A CSS feature used by the site is not supported by this browser engine                                | Flexbox gap renders incorrectly in older WebKit; CSS Grid not honoured                |

## How to Write Notes in matrix-results.md

**Format:** `{DEFECT_CODE}: {specific description of what is wrong and where}`

**Examples:**

| Cell                                  | Notes                                                                                                                                                         |
| ------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| iOS 17 / Safari / Phone / A2          | `OVERFLOW: Form panel extends 40px beyond right viewport edge. Horizontal scrollbar appears. Registration config section partially hidden.`                   |
| Windows 11 / Firefox / Desktop / A4   | `OVERLAP: Status badge overlaps with participant name in the first column of the approvals table. Badge covers the first 3 characters of the name.`           |
| Android 14 / Chrome / Phone / A5      | `CONTROL: "Scan QR" button not visible at 390×844 viewport. Appears to be hidden below the fold with no scroll indicator.`                                    |
| macOS Ventura / Safari / Desktop / A2 | `CSS-COMPAT: Date range picker calendar does not render. Only a text input is shown. Confirmed: <input type="date"> renders as native picker on this engine.` |

## Severity Mapping for Compatibility Defects

| Defect Type                           | Typical Severity | Notes                                         |
| ------------------------------------- | ---------------- | --------------------------------------------- |
| CONTROL (non-responsive or invisible) | High             | Prevents core interaction on this environment |
| LAYOUT (structure collapses)          | High             | Makes content unusable                        |
| OVERFLOW (horizontal scroll)          | Medium-High      | Degrades mobile experience significantly      |
| OVERLAP (element obscured)            | Medium           | May or may not affect core function           |
| BREAKPOINT (wrong layout)             | Medium           | Content accessible but experience degraded    |
| ZINDEX (element hidden)               | Medium           | Depends on which element is hidden            |
| JSERROR (blank section)               | High             | Content missing; function unavailable         |
| TEXT (unreadable)                     | Medium           | Content accessible but degraded               |
| IMAGE (broken)                        | Low-Medium       | Visual quality degraded                       |
| FONT (wrong rendering)                | Low              | Visual quality; no functional impact          |
| CSS-COMPAT (minor property)           | Low-Medium       | Depends on extent of visual impact            |

## Pass Criteria Summary

A cell receives **Pass** when all of the following are true after loading the screen:

- [ ] No horizontal overflow or scrollbar at the specified viewport
- [ ] No elements overlapping or obscuring each other
- [ ] All primary text is legible at the specified font size
- [ ] No broken image icons
- [ ] All primary interactive controls are visible and respond to click/tap
- [ ] Layout adapts correctly to the specified viewport width
- [ ] No JavaScript errors visible on the page surface (blank sections, error banners)

A cell receives **Fail** when **any** of the above criteria is not met. Note the defect using the taxonomy above.

A cell receives **Skip** when:

- The environment is inaccessible (no BrowserStack access, no physical device)
- The OS–browser combination is invalid (should have been marked N/A in template)
- A prerequisite for testing (e.g. network access to SUT) is unavailable

Always note the reason for Skip.
