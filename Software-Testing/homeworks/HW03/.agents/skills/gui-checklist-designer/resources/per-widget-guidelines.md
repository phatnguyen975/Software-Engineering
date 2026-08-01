# Per-Widget Guidelines — GUI Checklist Designer

Detailed test criteria for each widget type in the standard inventory. Always generate at least one checklist item per widget type present in the SUT.

## Text Box / Text Area (W-TextBox)

**State & Content**

- Enabled/disabled state matches spec: disabled = greyed background, no cursor, not editable
- Pre-population or blank state is correct per context
- Blinking cursor appears on focus
- Red asterisk (or equivalent) on mandatory fields; no asterisk on optional fields
- Stops accepting input at max character width; max-width matches spec
- Rejects spaces-only input where content is required

**Input Classes & Behaviour**

- Accepts valid characters: letters, numbers, special chars (1 $ # + −), alphanumeric + sign
- Formatted mask (if applicable, e.g. phone XXX-XXXX) is displayed; short input is rejected
- Correct label; alignment consistent with sibling fields
- Scrollbar appears when content exceeds visible box area
- Double-click selects all text; cursor changes to I-beam over editable area

**Accessibility (WCAG)**

- Visible label above or beside field (not placeholder-only) — WCAG 3.3.2
- Placeholder text is supplemental, not the only label
- Error message adjacent to field, identifies field by name — WCAG 3.3.1, 3.3.3

## Dropdown / Combo Box (W-Dropdown)

**Select-Only Dropdown**

- Arrow indicator shown; opens list on click
- Default or blank value shown correctly; mandatory field can accept blank?
- Rejects values outside the predefined list
- List is non-empty; scrollbar appears for long lists
- Typing first letter jumps to matching item
- Sorted alphabetically by default (unless order is meaningful)
- Selected item shown at top when list is reopened

**Editable Combo Box (type-ahead)**

- Allows alphanumeric and special character entry
- Can delete selection; switch between text and list modes
- A typed value equal to a list value is not treated as different
- Pure dropdown variant cannot accept free text
- Alt+Down or Ctrl+F4 opens the list (keyboard access)
- Blank/none option sits at top or bottom consistently

**Accessibility (WCAG)**

- Keyboard accessible (Tab to focus, Enter/Space to open, Arrow to navigate, Enter to select)
- ARIA role="combobox" or role="listbox" with accessible name — WCAG 4.1.2

## Button (W-Button)

**Behaviour & Keyboard**

- Enable/disable state per spec; Tab reaches button in logical order; image/icon changes on click
- Single / double / multi-click behave as specified (debounce on submit buttons)
- Hover state highlights the button and shows tooltip (if applicable)
- One default button per form/dialog (thick border / filled style) fires on Enter key
- Cancel / Close button fires on Esc key
- Destructive button (Delete, Remove) is visually distinct (red / danger style)

**Labels & Conventions**

- Alt + underlined letter activates button (if keyboard shortcuts are used)
- No duplicate hot-key letters on a single screen
- Uncorrectable/irreversible actions trigger a Yes/No confirmation dialog
- All buttons on a screen share similar size, shape, and font
- Cancel acts as Close (discards changes) when changes cannot be undone
- Search/Reset and Add/Delete pairs share size, font, and spacing

**Accessibility (WCAG)**

- Accessible name provided (visible label or aria-label) — WCAG 4.1.2
- Focus indicator visible when tabbed to — WCAG 2.4.7
- Disabled button not reachable by Tab (or if reachable, announces disabled state)
- Minimum target size ≥ 24×24 CSS px — WCAG 2.5.8

## Checkbox (W-Checkbox)

- Enable/disable state correct; correct default checked/unchecked state per spec
- Appropriate label; Tab selects checkbox in reading order
- Mouse click, Space, Enter, and Alt+letter all toggle the checkbox
- Clicking the label text also toggles the checkbox
- Correct events fire on state change (form reacts if needed)
- Alignment: checkbox and label vertically centred

**Accessibility (WCAG)**

- Programmatic label association (label element or aria-labelledby) — WCAG 4.1.2
- Group of checkboxes wrapped in fieldset + legend — WCAG 1.3.1

## Radio Button (W-Radio)

- Correct default selection; only ONE radio selectable per group
- Arrow keys move selection within the group
- Mouse click, Space, Enter, and Alt+letter change selection
- Handles blank value from DB for a fixed set (does not crash if no default is set)
- Group label (question) is visually associated with the radio options

**Accessibility (WCAG)**

- Fieldset + legend grouping — WCAG 1.3.1
- Focus moves into group on Tab; Arrow keys move within group — WCAG 2.1.1

## Hyperlink (W-Link)

- Standard link colour (or clearly distinguished from body text)
- Underlined across every character of the link text
- Opens correct target on click, Enter, and Ctrl+click
- Tab key navigates to the link (included in natural focus order)
- Goes to the correct page / performs the correct action

**Accessibility (WCAG)**

- Link text is descriptive (not "click here" or "read more") — WCAG 2.4.6
- Focus indicator visible on Tab — WCAG 2.4.7
- If link opens new tab, indicated in link text or via aria-label — WCAG 3.2.2

## Image (W-Image)

- Not blurred; correct size; correctly aligned
- Rotate / zoom functionality works if applicable
- No broken images (no broken-image icon / alt text fallback)
- Image is properly aligned with surrounding content

**Accessibility (WCAG)**

- Meaningful images have descriptive alt text — WCAG 1.1.1
- Decorative images have empty alt="" — WCAG 1.1.1
- Images containing text meet contrast requirements — WCAG 1.4.3

## Grid / Data Table (W-Grid)

- Column headers and grid headings are present and labelled
- Clicking a column header sorts ascending; clicking again sorts descending; triangle indicator shows direction
- Scroll via keyboard (Arrow/Page keys), mouse wheel, and scrollbar buttons
- Double-click on a row fires the row action (open detail / edit)
- Empty state shown when no data (not a blank white area)
- Pagination or "load more" functions correctly

**Accessibility (WCAG)**

- Table has `<caption>` or aria-label — WCAG 1.3.1
- Column headers use `<th scope="col">` — WCAG 1.3.1
- Keyboard navigation through cells — WCAG 2.1.1

## List Box (W-ListBox)

- Single select by mouse click; letter key jumps to matching item
- Double-click = select + open / perform action
- Scrollbar appears when list exceeds visible height
- Multi-select variant (if applicable): Ctrl+click, Shift+click work correctly

## Date / Time Field (W-Date)

- Leap years handled correctly (Feb 29 valid in leap year, invalid otherwise)
- Rejects invalid month values (00, 13+)
- Rejects invalid day values (00, 32+; Feb 30 always invalid)
- Century change handled (year 2000 boundary)
- Correct entry format displayed and enforced (DD/MM/YYYY vs YYYY-MM-DD per locale)
- Range constraints enforced (end date ≥ start date, registration close < event end)
- Keyboard entry works in addition to picker

**Accessibility (WCAG)**

- Calendar picker is keyboard accessible — WCAG 2.1.1
- Input has visible label — WCAG 3.3.2
- Format instructions provided — WCAG 3.3.2

## Modal / Dialog (W-Modal)

- Focus moves to modal when it opens; focus trapped inside modal while open
- Esc key closes the modal (for non-critical dialogs)
- Clicking the backdrop / overlay closes the modal (for non-critical dialogs)
- Confirmation dialog shown before destructive / irreversible actions
- All interactive elements inside modal reachable by keyboard
- After modal closes, focus returns to the triggering element

**Accessibility (WCAG)**

- aria-modal="true" and role="dialog" with aria-labelledby — WCAG 4.1.2
- Focus management on open and close — WCAG 2.4.3

## Toast / Snackbar Notification (W-Toast)

- Appears in consistent position across the application (top-right or bottom-center)
- Auto-dismisses after an appropriate interval (3–5 seconds typical)
- Manual dismiss (×) button available for important messages
- Colour semantics are consistent: green=success, red=error, yellow=warning, blue=info
- Message text is specific and actionable (not just "Error occurred")
- Does not overlap interactive controls

**Accessibility (WCAG)**

- Role="status" (for success/info) or role="alert" (for error) — WCAG 4.1.3
- Not auto-dismissed before user can read it (or dismissal is pauseable) — WCAG 2.2.1

## File Upload (W-FileUpload)

- Accepted file formats validated; unsupported format triggers clear error message
- File size limit enforced; over-limit triggers clear error message with limit stated
- Upload progress indicator shown during upload
- Preview shown after successful upload (thumbnail for images)
- Uploaded file can be removed / replaced before form submission
- Drag-and-drop upload works in addition to browse button (if designed)
- Aspect ratio constraint enforced if applicable (e.g. 4:3 for thumbnail, 24:9 for banner)

**Accessibility (WCAG)**

- Upload button has accessible label — WCAG 4.1.2
- Error messages identify the field and describe the issue — WCAG 3.3.1, 3.3.3

## Rich Text Editor (W-RTE)

- Toolbar buttons (bold, italic, list, link, etc.) function correctly
- Formatted output renders correctly in preview / published view
- Content is preserved when saving as draft and reopening
- Pasted content from external sources (Word, browser) is sanitised
- Character/word limit (if any) is enforced and indicated
- Keyboard shortcuts for formatting (Ctrl+B, Ctrl+I) work

**Accessibility (WCAG)**

- Editor canvas is keyboard accessible — WCAG 2.1.1
- Editor has accessible label — WCAG 4.1.2

## Drag-and-Drop Interface (W-DragDrop)

- Drag handle (if any) is visually distinct and obvious (NOR1 Affordances)
- Dragged item shows visual feedback during drag (e.g. opacity reduced to 50%, ghost image)
- Other interactive controls on the page are disabled while drag is in progress
- Drop target provides visual feedback (highlight, insertion line)
- Dropped order is preserved after the action completes
- Order is correctly saved when the user confirms / saves the new arrangement
- Keyboard alternative provided for reordering (e.g. move up/down buttons) — WCAG 2.5.7

**Accessibility (WCAG)**

- All drag functionality operable with keyboard only — WCAG 2.1.1
- Dragging Movements: pointer-based drag has single-pointer alternative — WCAG 2.5.7

## Progress Bar (W-Progress)

- Percentage is accurate relative to actual progress
- Updates in real-time (not only on completion)
- Accessible label identifies what is progressing
- Visual only as fallback: shows completion even if exact percentage unknown (indeterminate variant)

**Accessibility (WCAG)**

- role="progressbar" with aria-valuenow, aria-valuemin, aria-valuemax — WCAG 4.1.2
- Status announced to screen readers on completion — WCAG 4.1.3

## Tab Panel (W-Tab)

- Active tab is visually distinct from inactive tabs
- Tab content switches correctly when a tab is clicked
- Arrow keys (Left/Right) navigate between tabs — WCAG 2.1.1
- Tab panel content is accessible when the tab is active

**Accessibility (WCAG)**

- role="tablist", role="tab", role="tabpanel" with aria-selected — WCAG 4.1.2
- Keyboard: Tab enters the tablist, Arrow keys move between tabs, Enter/Space selects — WCAG 2.1.1

## Sidebar / Navigation Menu (W-Sidebar)

- Active/current page item is visually distinct (highlighted, bolded, or indicated)
- Sidebar position is consistent across all screens
- All menu items are reachable by keyboard (Tab or Arrow keys)
- Collapsible sidebar (if present): toggle works, state persists across navigation
- Sub-menus expand/collapse correctly and indicate their state

**Accessibility (WCAG)**

- nav landmark (role="navigation" or `<nav>`) — WCAG 1.3.1
- Aria-current="page" on active item — WCAG 4.1.2
- Focus visible on all items — WCAG 2.4.7
