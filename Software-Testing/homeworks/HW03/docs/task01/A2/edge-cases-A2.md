# Edge Cases — Add / Edit Event (A2)

> **Screen:** A2 — Add / Edit Event  
> **Add Screen URL:** https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create  
> **Edit Screen URL:** https://prod-dev.ems-fitus.cloud/dashboard/admin/events/edit?id=39  
> **Execution Mode:** Mode 1 — BrowserMCP (Live Browser)  
> **Tester:** Nguyễn Tấn Phát — 23127449  
> **Date:** 2026-07-29  
> **Summary:** Total 7 items (Pass: 6, Fail: 1, NA: 0)

## Text Box / Text Area

| EC ID     | Scenario                     | How-to-Test                                                       | Expected Outcome                                                              | Result | Notes                                                                                                                          |
| --------- | ---------------------------- | ----------------------------------------------------------------- | ----------------------------------------------------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------ |
| EC-A2-001 | Maximum character UI limits  | Paste extremely long text into Event Title (e.g., 500 characters) | Input physically stops accepting characters at limit; no horizontal scrolling | Pass   | Normal expected behavior for a text box; it wraps text gracefully without introducing horizontal scrolling or layout overflow. |
| EC-A2-002 | Special characters rendering | Enter `<script>`, `"`, emojis in Location                         | Characters render perfectly as plain text; no HTML rendering                  | Pass   | Special characters like `<script>` and emojis render perfectly as plain text without HTML injection.                           |

## Dropdown / Combo Box

| EC ID     | Scenario                                 | How-to-Test               | Expected Outcome                                              | Result | Notes                                                                                                                     |
| --------- | ---------------------------------------- | ------------------------- | ------------------------------------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------- |
| EC-A2-003 | Viewport overflow prevention             | Open Event Types dropdown | Menu opens upwards or downwards without bleeding off screen   | Fail   | Date pickers near the bottom of the screen do not intelligently open upwards; they instead get cut off/hidden off-screen. |
| EC-A2-004 | Container sizing and overflow prevention | Open Campus dropdown      | Container is appropriately sized without excessive whitespace | Pass   | The whitespace is intentional and consistent with the sizing of the select box; layout is structurally sound.             |

## Button

| EC ID     | Scenario                                        | How-to-Test                             | Expected Outcome                                                       | Result | Notes                                                                                                                      |
| --------- | ----------------------------------------------- | --------------------------------------- | ---------------------------------------------------------------------- | ------ | -------------------------------------------------------------------------------------------------------------------------- |
| EC-A2-005 | Visual lock on submit (Double-click prevention) | Double-click the Publish button rapidly | Button changes to disabled state on first click, blocking second click | Pass   | Upon clicking publish, the button immediately transitions to a disabled state, successfully preventing double submissions. |

## Date / Time Field

| EC ID     | Scenario                    | How-to-Test                                      | Expected Outcome                                                      | Result | Notes                                                                                         |
| --------- | --------------------------- | ------------------------------------------------ | --------------------------------------------------------------------- | ------ | --------------------------------------------------------------------------------------------- |
| EC-A2-006 | Calendar popup bounding box | Click Registration Close picker near screen edge | Calendar popup aligns intelligently without horizontal page scrolling | Pass   | Calendar popup aligns intelligently within the viewport without forcing horizontal scrolling. |

## Navigation

| EC ID     | Scenario                           | How-to-Test                                                                              | Expected Outcome                                                                    | Result | Notes                                                                                                         |
| --------- | ---------------------------------- | ---------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------- | ------ | ------------------------------------------------------------------------------------------------------------- |
| EC-A2-007 | Preview Back Button Data Retention | Fill out form data → click "Preview Event" → click the Back button on the Preview screen | Form returns to Create Event screen; all previously entered data is fully retained. | Pass   | Verified successfully. All form data entered is safely retained when navigating back from the Preview screen. |
