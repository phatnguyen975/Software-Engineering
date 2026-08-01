# Edge Case Reference — GUI Checklist Executor

Common edge cases by widget type for web application GUI testing. Use this reference during **Step 2** to generate the `edge-cases-{screen_id}.md` file. Edge case IDs use the format `EC-{screen_id}-{nnn}` (e.g. `EC-A2-001`).

## Edge Case File Format

```markdown
# Edge Cases — {screen_name} ({screen_id})

## IA-01 — General UI Standards

| EC ID       | Scenario   | How-to-Test | Expected Outcome | Result                  | Notes   |
| ----------- | ---------- | ----------- | ---------------- | ----------------------- | ------- |
| EC-{id}-001 | [scenario] | [steps]     | [expected]       | Pass/Fail/NA/Need Human | [notes] |

## IA-02 — Forms

...
```

## Text Box / Text Area

| Scenario                        | How-to-Test                                                        | Expected Outcome                                                                                                                                       |
| ------------------------------- | ------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Empty required field validation | Leave a required field blank → click outside (blur) or submit      | Visual error state triggers immediately (e.g., red border, error icon); inline error text appears without pushing adjacent elements out of alignment   |
| Spaces-only input rendering     | Enter only spaces in a required text field → submit                | The UI visually treats it as empty; placeholder text (if any) remains hidden; error styling applies                                                    |
| Maximum character UI limits     | Paste characters exceeding the defined max limit                   | Input physically stops accepting characters; no horizontal scrolling inside a single-line input; layout remains fully intact                           |
| Special characters rendering    | Enter `<script>`, `"`, `'`, `&`, `%20`, and Emojis in a text field | Characters render perfectly as plain text on the screen; no CSS distortion, unexpected blank spaces, or raw HTML rendering visible                     |
| Extremely long continuous text  | Enter a single, unbroken string of 100+ characters (no spaces)     | Text gracefully truncates or visually overflows smoothly (e.g., using ellipsis `...`); does not break the container width or overlap other UI elements |

## Dropdown / Combo Box

| Scenario                                 | How-to-Test                                                                | Expected Outcome                                                                                                                                                                                      |
| ---------------------------------------- | -------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Placeholder styling                      | Open screen without interacting → observe dropdown                         | Default placeholder text (e.g., "Select an option") is styled distinctly (e.g., greyed out or italicized) compared to selectable values                                                               |
| Hover and Focus states                   | Tab to dropdown → use Arrow keys to navigate options                       | Active option visually highlights with clear contrast; focus ring appears clearly around the dropdown container                                                                                       |
| Viewport overflow prevention             | Open a dropdown with a massive list of items near the bottom of the screen | Dropdown menu opens upwards if space below is insufficient, or scrollbar appears within the list; menu absolutely never bleeds off the screen                                                         |
| Long option text truncation              | Open dropdown containing an option with an extremely long label            | The long text truncates with an ellipsis (`...`) or wraps neatly; it does not widen the dropdown menu beyond its designed grid column                                                                 |
| Container sizing and overflow prevention | Open a dropdown containing search inputs, action buttons, or custom lists  | Dropdown container is appropriately sized (min/max width/height) without excessive empty whitespace. Internal content fits entirely within boundaries without overflowing or spilling over the edges. |

## Button

| Scenario                                        | How-to-Test                                                 | Expected Outcome                                                                                                                                          |
| ----------------------------------------------- | ----------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Visual lock on submit (Double-click prevention) | Double-click the submit/save button rapidly                 | Button immediately changes to a disabled visual state (greyed out or shows a loading spinner) upon the very first click, visually blocking a second click |
| Disabled state styling                          | Identify a disabled button → hover and attempt to click     | Cursor changes to `not-allowed`; no hover animation triggers; button color contrast clearly indicates it is inactive                                      |
| Keyboard active state                           | Focus the button via Tab → hold down the Enter or Space key | Button visually mimics the "active" CSS state (e.g., shrinks slightly, changes shadow) before releasing                                                   |
| Icon and Text alignment                         | Inspect buttons containing both an icon and text            | Icon and text are perfectly centered vertically and horizontally within the button container; spacing between them is consistent                          |

## Form Validation

| Scenario                       | How-to-Test                                            | Expected Outcome                                                                                                                                             |
| ------------------------------ | ------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bulk error rendering           | Leave all fields blank → click submit                  | All required fields display error styling simultaneously; page smoothly auto-scrolls to the first error field in the view                                    |
| Real-time visual correction    | Trigger an error → begin typing a valid input          | Error styling (red borders, text) disappears instantly as the user types, or immediately upon field blur; transitions smoothly back to default/success state |
| Error message layout stability | Trigger validation error → observe error placement     | Error text appears directly below/beside the field; it does not overlap the field below it or cause the entire page layout to jump violently                 |
| Partial fill submit            | Fill some required fields, leave others blank → submit | Only unfilled required fields show errors                                                                                                                    |

## Date / Time Field

| Scenario                       | How-to-Test                                                                                    | Expected Outcome                                                                                                                                                                            |
| ------------------------------ | ---------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Disabled dates rendering       | Open the calendar picker where past/future dates are restricted                                | Restricted dates are visibly greyed out, have a lower opacity, and show a `not-allowed` cursor on hover                                                                                     |
| Visual formatting (Input Mask) | Type `12122024` rapidly into the date text input                                               | The input visually auto-formats the text as the user types, automatically inserting separators (e.g., `12/12/2024`) without cursor jumping incorrectly                                      |
| Calendar popup bounding box    | Click the date field near the right edge of the screen                                         | The calendar popup aligns itself intelligently (e.g., aligns right) to ensure the entire calendar is visible without horizontal page scrolling                                              |
| Invalid manual date rendering  | Manually type an invalid date string (e.g., `30/02/2024` or `99/99/9999`) into the text input. | The UI immediately triggers a visual error state (e.g., red border, warning icon) upon field blur; the error message renders clearly without causing layout shifts to the elements below it |

## File Upload

| Scenario                     | How-to-Test                                                | Expected Outcome                                                                                                                                                   |
| ---------------------------- | ---------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Drag-and-drop visual cue     | Drag a file and hover it exactly over the upload drop zone | The drop zone visually reacts (e.g., border becomes dashed, background changes color, or an icon animates) to indicate it is ready to receive the file             |
| Invalid file visual feedback | Upload an unsupported file                                 | The upload zone visually transitions to an error state; a distinct error badge or text appears indicating the rejection                                            |
| Thumbnail/Preview rendering  | Upload a valid image file                                  | The UI generates a correct visual thumbnail of the image; the thumbnail fits perfectly within its container without aspect ratio distortion (stretching/squishing) |
| File uploaded removing       | Upload a file → click the remove / clear button            | File removed; upload area returns to empty state                                                                                                                   |

## Modal / Dialog

| Scenario                      | How-to-Test                                                           | Expected Outcome                                                                                                                         |
| ----------------------------- | --------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------- |
| Focus trap                    | Open modal → press Tab repeatedly                                     | The visual focus ring cycles only within the modal's interactive elements; it never highlights elements on the blurred background page   |
| Background overlay (Backdrop) | Open modal → observe the background                                   | The background page visually dims or blurs (backdrop overlay applied); scrolling the mouse wheel does not scroll the background page     |
| Responsive modal sizing       | Open a modal containing a lot of text → resize window to mobile width | The modal shrinks to fit the screen; internal content becomes scrollable; the close (`x`) button remains pinned and visible at all times |
| Esc key closes modal          | Open modal → press Esc                                                | Modal closes (unless it is a non-dismissible blocking dialog)                                                                            |
| Backdrop click closes modal   | Open modal → click outside the modal panel                            | Modal closes (unless blocking)                                                                                                           |
| Focus return after close      | Open modal → close it                                                 | Focus returns to the element that triggered the modal                                                                                    |

## Toast / Snackbar Notification

| Scenario             | How-to-Test                                      | Expected Outcome                                                                  |
| -------------------- | ------------------------------------------------ | --------------------------------------------------------------------------------- |
| Auto-dismiss timing  | Trigger a toast → wait                           | Toast disappears after expected interval (typically 3–5 seconds)                  |
| Manual dismiss       | Trigger a toast → click the dismiss (`x`) button | Toast closes immediately                                                          |
| Multiple toasts      | Trigger two actions in rapid succession          | Both toasts appear in sequence or stacked; neither overwrites the other invisibly |
| Error toast persists | Trigger an error toast                           | Error toast either does not auto-dismiss, or persists longer than success toasts  |

## Drag-and-Drop Reorder

| Scenario                            | How-to-Test                                                                    | Expected Outcome                                                                                                                                      |
| ----------------------------------- | ------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------- |
| Visual flow during drag (Animation) | Click and drag an item, moving it slowly up and down the list without dropping | The other items in the list visually slide out of the way smoothly (animated) to create an empty placeholder slot for the dragged item; no flickering |
| Invalid drop visual feedback        | Begin dragging → release outside the valid list boundary                       | The item animates smoothly back to its original starting position; no UI elements get stuck in a "dragging" state                                     |
| Drop snap animation                 | Release the dragged item into a new slot                                       | The item visually "snaps" into the new position smoothly; the layout instantly stabilizes                                                             |
| Other controls disabled during drag | Begin dragging → attempt to click another button                               | Other interactive controls are disabled or unresponsive during drag                                                                                   |

## Empty and Loading States

| Scenario                               | How-to-Test                                                                                                            | Expected Outcome                                                                                                                                   |
| -------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| Empty state illustration               | Navigate to a screen with no data (or delete the last item)                                                            | A visually distinct "Empty State" component renders (illustration + text); no broken tables with empty rows or orphaned column headers are visible |
| Skeleton layout accuracy               | Navigate to the screen and observe the initial load                                                                    | The Skeleton loading graphics (grey boxes/lines) visually match the layout and dimensions of the actual content that eventually renders            |
| Error state rendering                  | Simulate a network failure during load                                                                                 | A styled error graphic and fallback text appear; the screen does not collapse into a blank white page or display raw JSON/Code dumps               |
| Refresh after action                   | Perform a create/edit/delete action → observe list                                                                     | List updates to reflect the change without requiring manual page reload                                                                            |
| Submit button lock on unmodified state | Open an update modal/form (e.g., edit profile, important update) → observe the submit button before making any changes | The submit button remains in a disabled visual and functional state until a change is made to the form data                                        |

## Navigation

| Scenario                           | How-to-Test                                                     | Expected Outcome                                                                                                                                                 |
| ---------------------------------- | --------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Active state on direct load        | Paste the screen URL directly into the browser                  | The Sidebar/Navbar automatically expands and visually highlights the correct active menu item; Breadcrumbs render the correct hierarchy                          |
| Browser back button                | Navigate to the screen → press browser Back                     | Returns to expected previous state; no error page                                                                                                                |
| Preview back button data retention | Fill form data → open Preview → click Back to return to form    | Form returns to original state; all previously entered data is completely retained without loss                                                                  |
| Tab order                          | Press Tab from the first interactive element → observe sequence | Focus moves in logical reading order through all interactive elements                                                                                            |
| Text expansion on translation      | Switch UI language to one known for long words (e.g., German)   | All UI text updates; buttons and containers expand dynamically to fit longer words without text clipping, overlapping icons, or breaking into unwanted new lines |
| Responsive layout shifting         | Resize browser window from Desktop to Tablet to Mobile width    | Grid columns stack vertically gracefully; menus collapse into a hamburger icon; no horizontal scrolling is introduced                                            |
