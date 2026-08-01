---
name: compatibility-matrix-runner
description: >
  Generate a cross-browser/cross-platform compatibility test matrix, define minimum
  coverage requirements, produce a prioritised test order, and maintain a live
  Pass/Fail results log updated through human feedback. Invoke when asked to
  "create a compatibility matrix", "plan cross-browser testing", "generate a browser
  compatibility test plan", or "set up cross-platform testing".
version: 1.0.0
author: phatnguyen975
---

# Compatibility Matrix Runner Skill

## Overview

This skill generates a **compatibility test matrix** for a web application across multiple operating systems, browsers, and device classes. It selects a minimum-coverage cell set, prioritises cells by rendering engine risk, generates screenshot naming conventions, and maintains a live results log updated through human feedback.

**What this skill does NOT do:**

- It does not re-run the GUI checklist or inspect DOM on each cell
- It does not perform functional or accessibility testing
- It does not access BrowserStack or any testing tool directly

Each matrix cell is a **visual + smoke test** — load the screen, observe the rendered layout and primary controls, and record Pass or Fail. See the Test Scope section for the exact check list per cell.

### Inputs

```
REQUIRED
──────────────────────────────────────────────────────────────────────
sut_url           : string (URL)
                    Base URL of the System Under Test.
                    Example: "https://promoter-starboard-prude.ngrok-free.dev/"

screens_list      : list of objects [{id, name, url_path}]
                    Screens to test in each matrix cell.
                    Each entry needs:
                      id       — short identifier (e.g. "A2")
                      name     — human-readable name (e.g. "Add/Edit Event Form")
                      url_path — path appended to sut_url (e.g. "/admin/events/create")
                    Example:
                      - id: "A2"
                        name: "Add/Edit Event Form"
                        url_path: "/admin/events/create"

os_list           : list of strings
                    Operating systems to include. Minimum 3 required.
                    Example: ["Windows 11", "macOS Ventura", "Android 14", "iOS 17"]

browser_list      : list of strings
                    Browsers to include. Minimum 5 required.
                    Example: ["Chrome", "Firefox", "Safari", "Edge", "Samsung Internet"]

device_classes    : list of objects [{name, viewport}]
                    Device classes to include. Minimum 3 required.
                    Each entry needs:
                      name     — class label (e.g. "Desktop")
                      viewport — dimensions (e.g. "1920×1080")
                    Example:
                      - name: "Desktop"
                        viewport: "1920×1080"
                      - name: "Tablet"
                        viewport: "768×1024"
                      - name: "Phone"
                        viewport: "390×844"

student_id_email  : string
                    Student email used in screenshot naming and overlay.
                    Example: "22127001@student.hcmus.edu.vn"

output_dir        : string (path)
                    Directory where all output files will be written.
                    Example: "docs/compatibility"

OPTIONAL
──────────────────────────────────────────────────────────────────────
priority_cells    : list of strings
                    Cells to test first, specified as "{OS}/{Browser}/{Device}".
                    If not provided, the skill derives priorities from rendering
                    engine risk (see Priority Guide section).
                    Example:
                      - "iOS 17/Safari/Phone"
                      - "Android 14/Samsung Internet/Phone"
                      - "macOS Ventura/Safari/Desktop"

coverage_mode     : enum  [default: "minimum"]
                    "minimum" — select the smallest cell set that satisfies:
                                each OS ≥ 1 cell, each browser ≥ 1 cell,
                                each device class ≥ 1 cell, per screen.
                    "full"    — test all valid OS × browser × device combinations
                                (only valid combinations; e.g. Safari only on macOS/iOS).
```

### Invoke Format

```
/compatibility-matrix-runner
  sut_url: "<url>"
  screens_list:
    - id: "<id>"
      name: "<name>"
      url_path: "<path>"
  os_list:
    - "<OS name>"
  browser_list:
    - "<browser name>"
  device_classes:
    - name: "<class>"
      viewport: "<WxH>"
  student_id_email: "<email>"
  output_dir: "<path>"
  priority_cells:
    - "<OS/Browser/Device>"
  coverage_mode: "<minimum|full>"
```

### Outputs

| File                                | Description                                       | Filled by                             |
| ----------------------------------- | ------------------------------------------------- | ------------------------------------- |
| `{output_dir}/matrix-template.md`   | Full matrix with all planned cells, empty results | AI                                    |
| `{output_dir}/priority-guide.md`    | Ordered list of cells to test, with rationale     | AI                                    |
| `{output_dir}/screenshot-naming.md` | Naming convention + MSSV overlay instructions     | AI                                    |
| `{output_dir}/matrix-results.md`    | Live results log — Pass/Fail/Skip + defect notes  | Human fills; AI updates from feedback |

See [`examples/example-matrix-output.md`](examples/example-matrix-output.md) for complete formatted samples of all four files.

## When to Use

- You need to verify a web application renders correctly across multiple OS, browser, and device class combinations.
- You want a prioritised test order that covers the highest-risk rendering environments first.
- You need a screenshot naming convention with a student/tester ID overlay for evidence.
- You need a live results log to track Pass/Fail status and defect notes as you test.

## When NOT to Use

- You want to re-test GUI checklist items per browser — this skill is rendering/layout only.
- You want automated visual regression (pixel diff) — use Applitools, Percy, or BackstopJS.
- You have already completed testing and only need to format results — fill the results log directly.
- You only have one OS or one browser — no matrix is needed.

## Core Principles

1. **Coverage, not exhaustion.** The goal is to verify each OS, browser, and device class at least once per screen — not to test every possible combination. A 45-cell matrix with 10 real cells is better than a 10-cell matrix that misses entire dimensions.
2. **Rendering engine first.** The most important coverage dimension is the rendering engine, not the browser brand. Chrome, Edge, and Opera all use Blink — testing all three adds less value than adding one WebKit (Safari) and one Gecko (Firefox) test.
3. **Per-screen, not per-run.** The coverage requirement applies independently per screen. Each screen must satisfy the minimum coverage criteria on its own.
4. **Visual + smoke test scope.** Each cell is a layout/rendering check plus a basic interaction smoke test. It is not a full functional test or GUI checklist re-run. See the Test Scope section for the exact checks.
5. **Human fills results; AI updates the log.** The AI generates the template and processes feedback. The human performs the actual testing in a browser and provides Pass/Fail verdicts and defect notes through feedback messages.
6. **Screenshot evidence is mandatory for Fail cells.** A Fail without a screenshot is unverifiable. The naming convention must be followed so screenshots can be matched to matrix cells.

## Coverage Selection Logic

### Minimum Coverage Requirements (per screen)

The selected cell set must satisfy all three constraints independently:

- **OS coverage:** every OS in `os_list` must appear in at least one selected cell
- **Browser coverage:** every browser in `browser_list` must appear in at least one selected cell
- **Device class coverage:** every device class in `device_classes` must appear in at least one selected cell

These are **independent** constraints — the same cell can satisfy coverage for multiple dimensions simultaneously.

### Valid OS–Browser Combinations

Not all OS–browser combinations are valid. Apply these constraints:

| Browser          | Valid OS                     | Notes                                                  |
| ---------------- | ---------------------------- | ------------------------------------------------------ |
| Safari (desktop) | macOS only                   | Safari is not available on Windows or Linux            |
| Safari (mobile)  | iOS only                     | All iOS browsers use WebKit regardless of browser name |
| Samsung Internet | Android only                 | Samsung Internet is Android-exclusive                  |
| Chrome           | Windows, macOS, Android, iOS | Available on all platforms                             |
| Firefox          | Windows, macOS, Android      | Firefox for iOS exists but is WebKit-based (not Gecko) |
| Edge             | Windows, macOS, Android, iOS | Available on all platforms                             |
| Opera            | Windows, macOS, Android      | Limited iOS availability                               |

See [`resources/rendering-engines-guide.md`](resources/rendering-engines-guide.md) for the full engine map.

### Minimum Coverage vs Full Coverage

- **`coverage_mode = "minimum"` (default):** Select the smallest valid cell set that satisfies all three coverage constraints. Use a greedy selection: pick cells that cover the most uncovered dimensions simultaneously.
- **`coverage_mode = "full"`:** Generate all valid OS × browser × device combinations from the inputs. Flag invalid combinations (e.g. Safari on Windows) as "N/A — invalid combination".

## Test Scope per Cell

Each matrix cell represents one screen × one OS-browser-device combination. The tester performs these checks for each cell — this is a **visual + smoke test**, not a full GUI audit:

| Check                        | What to observe                                                      | Pass criteria                                                          |
| ---------------------------- | -------------------------------------------------------------------- | ---------------------------------------------------------------------- |
| **Layout integrity**         | Overall page layout on load                                          | No horizontal overflow; no overlapping elements; no collapsed sections |
| **Text rendering**           | All visible text                                                     | No truncated text in primary content areas; font renders legibly       |
| **Image rendering**          | Images, icons, thumbnails                                            | No broken image icons; images at correct proportions                   |
| **Primary controls visible** | Buttons, inputs, navigation                                          | All primary interactive controls are visible and not obscured          |
| **Basic interaction**        | Click/tap one primary control (e.g. open a dropdown, click a button) | Control responds; no JavaScript error visible; no page crash           |
| **Responsive breakpoint**    | Layout at the specified viewport                                     | Layout adapts to viewport; no fixed-width overflow                     |

**What is NOT checked per cell (separate concern):**

- ARIA attributes or accessibility compliance
- Form validation behaviour
- Business logic or data accuracy
- CSS exact values or contrast ratios

See [`resources/defect-taxonomy.md`](resources/defect-taxonomy.md) for how to classify and note failures.

## Priority Guide Logic

Test cells in this order (unless `priority_cells` is provided):

**Priority 1 — Highest-risk rendering engines:**

- iOS Safari (WebKit — iOS forces all browsers to use WebKit; Safari on iOS is the only way to test real WebKit on mobile)
- Samsung Internet on Android (real Chromium fork with Samsung-specific behaviours)
- Firefox on Desktop (Gecko — the only Gecko-engine browser in common use)

**Priority 2 — Primary desktop environments:**

- Chrome on Windows/Desktop (Blink — largest global browser share)
- Safari on macOS/Desktop (WebKit — critical for macOS users)
- Edge on Windows/Desktop (Blink fork — significant enterprise share)

**Priority 3 — Secondary combinations:**

- Remaining cells that satisfy coverage requirements not yet met
- Chrome on Android (Blink mobile)
- Any remaining OS × browser × device combinations

**Rationale:** iOS Safari diverges most from desktop Chrome due to Apple's WebKit enforcement. If only one test is possible, test iOS Safari first.

See [`resources/rendering-engines-guide.md`](resources/rendering-engines-guide.md) for the full engine reference.

## Workflow

### Step 1 — Validate Inputs

Check all required inputs:

- `screens_list` has at least 1 entry with `id`, `name`, and `url_path`
- `os_list` has ≥ 3 entries
- `browser_list` has ≥ 5 entries
- `device_classes` has ≥ 3 entries
- `student_id_email` is non-empty
- `output_dir` is a valid path string

If any required input is missing → stop and ask the human to clarify.

### Step 2 — Build Valid Combination Map

From `os_list` × `browser_list` × `device_classes`, identify all valid combinations using the OS–Browser constraints in the Coverage Selection Logic section. Mark invalid combinations (e.g. Safari on Windows) explicitly so they can appear in the full matrix as "N/A — invalid combination".

### Step 3 — Select Cell Set

- **If `coverage_mode = "minimum"`:** Apply greedy selection to find the smallest valid cell set satisfying all three coverage constraints per screen. Verify the selected set independently for each screen in `screens_list`.
- **If `coverage_mode = "full"`:** Include all valid combinations. Invalid combinations marked N/A.

**In both modes:** If `priority_cells` is provided, include those cells first, then fill remaining coverage gaps.

### Step 4 — Generate `matrix-template.md`

Write the full matrix structure. Each screen gets its own matrix table. Rows = OS + Browser combination; columns = device class × screen.

- For `coverage_mode = "minimum"`: cells not selected are marked "—" (not planned).
- For `coverage_mode = "full"`: invalid cells marked "N/A". Valid planned cells left blank.

### Step 5 — Generate `priority-guide.md`

Write an ordered list of all planned cells with:

- Cell identifier: `{OS} / {Browser} / {Device} / {Screen ID}`
- Priority tier (1, 2, or 3)
- Rendering engine
- Rationale for the priority assignment

### Step 6 — Generate `screenshot-naming.md`

Write the naming convention and overlay instructions.

**Naming convention:** `{os-slug}_{browser-slug}_{device-slug}_{screen-id}_{status}.png`

**Where:**

- `os-slug`: lowercase, hyphenated OS name (e.g. `windows-11`, `ios-17`, `macos-ventura`)
- `browser-slug`: lowercase browser name (e.g. `chrome`, `safari`, `samsung-internet`)
- `device-slug`: lowercase device class (e.g. `desktop`, `tablet`, `phone`)
- `screen-id`: screen identifier from inputs (e.g. `a2`, `checkout`)
- `status`: `pass` or `fail`

**Example:** `ios-17_safari_phone_a2_fail.png`

**MSSV overlay instructions:** Include step-by-step instructions for adding the `student_id_email` as a visible overlay on each screenshot before saving.

### Step 7 — Generate `matrix-results.md`

Write the initial results log — same structure as `matrix-template.md` but with Result and Notes columns added, initially empty.

### Step 8 — Human Verification Gate #1 (Matrix Structure)

Stop. Present to the human:

```md
Compatibility matrix files written to: {output_dir}

- matrix-template.md — {N} planned cells across {S} screens
- priority-guide.md — {N} cells in priority order
- screenshot-naming.md — naming convention for {student_id_email}
- matrix-results.md — results log (empty, ready to fill)

Please review:

- Are all required OS, browser, and device classes covered?
- Is the priority order sensible for your testing environment?
- Are any planned cells that cannot be tested (no access to that OS/browser)?

Reply `APPROVED` to proceed, or `FAILED: <feedback>` to adjust.
```

Wait for `APPROVED` before Step 9.

### Step 9 — Accept and Process Human Test Feedback

As the human tests cells and provides feedback, update `matrix-results.md`.

**Feedback format the human should use:**

```
RESULT: {OS} / {Browser} / {Device} / {Screen ID}
Status: Pass | Fail | Skip
Screenshot: {filename}  (required for Fail; optional for Pass)
Notes: {defect description if Fail; reason if Skip}
```

For each feedback entry received:

1. Find the corresponding cell in `matrix-results.md`
2. Update: Result = Pass/Fail/Skip, Screenshot ref, Notes
3. Confirm update to the human: "Updated: {cell identifier} → {status}"

**Batch feedback:** The human may provide multiple results at once. Process each in order.

### Step 10 — Generate Coverage Summary (on request or when all cells reported)

When all planned cells have a result, or when the human requests a summary:

```
Coverage Summary — {output_dir}/matrix-results.md

Screens tested: {S}
Total planned cells: {N}
  Pass:  {n} ({%})
  Fail:  {n} ({%})
  Skip:  {n} ({%})
  Pending: {n}

Coverage achieved:
  OS coverage:           {list each OS and whether covered}
  Browser coverage:      {list each browser and whether covered}
  Device class coverage: {list each device class and whether covered}

Fail cells requiring screenshot evidence:
  {list any Fail cells without a screenshot reference}
```

## Output Templates

### `matrix-template.md`

```markdown
# Compatibility Test Matrix — {sut_name}

> **Generated by:** `compatibility-matrix-runner` skill (v1.0.0)  
> **Coverage mode:** {minimum | full}  
> **Screens:** {screen IDs}  
> **Coverage requirements:** each OS ≥ 1 cell, each browser ≥ 1 cell, each device class ≥ 1 cell, per screen

## Screen {screen_id}: {screen_name}

**URL:** {sut_url}{url_path}

| OS            | Browser          | Engine | Desktop ({viewport}) | Tablet ({viewport}) | Phone ({viewport}) |
| ------------- | ---------------- | ------ | -------------------- | ------------------- | ------------------ |
| Windows 11    | Chrome           | Blink  | ✓ planned            | —                   | —                  |
| Windows 11    | Firefox          | Gecko  | ✓ planned            | —                   | —                  |
| Windows 11    | Edge             | Blink  | —                    | —                   | —                  |
| macOS Ventura | Safari           | WebKit | ✓ planned            | —                   | —                  |
| macOS Ventura | Chrome           | Blink  | —                    | —                   | —                  |
| Android 14    | Chrome           | Blink  | —                    | —                   | ✓ planned          |
| Android 14    | Samsung Internet | Blink† | —                    | —                   | ✓ planned          |
| iOS 17        | Safari           | WebKit | —                    | —                   | ✓ planned          |

> † Samsung Internet uses a Chromium fork; rendering may differ from standard Blink.
> — = not planned in minimum coverage mode
> N/A = invalid OS–browser combination

{Repeat table for each additional screen}
```

### `matrix-results.md`

```markdown
# Compatibility Test Results — {sut_name}

> **Updated by:** human feedback via `compatibility-matrix-runner`
> **Last updated:** {timestamp of most recent update}

## Screen {screen_id}: {screen_name}

| OS            | Browser          | Device  | Result | Screenshot | Notes |
| ------------- | ---------------- | ------- | ------ | ---------- | ----- |
| iOS 17        | Safari           | Phone   | —      | —          | —     |
| Android 14    | Samsung Internet | Phone   | —      | —          | —     |
| Windows 11    | Firefox          | Desktop | —      | —          | —     |
| macOS Ventura | Safari           | Desktop | —      | —          | —     |
| Windows 11    | Chrome           | Desktop | —      | —          | —     |
| Android 14    | Chrome           | Phone   | —      | —          | —     |

> **Result codes:** ✓ Pass | ✗ Fail | ⊘ Skip | — Pending
> **Screenshot:** filename only (files stored in screenshots/compatibility/)
> **Notes:** defect description for Fail; reason for Skip; blank for Pass

{Repeat table for each screen}

## Coverage Status

| Dimension        | Required        | Covered by            |
| ---------------- | --------------- | --------------------- |
| Windows 11       | ≥ 1 cell/screen | {cell ID once tested} |
| macOS Ventura    | ≥ 1 cell/screen |                       |
| Android 14       | ≥ 1 cell/screen |                       |
| iOS 17           | ≥ 1 cell/screen |                       |
| Chrome           | ≥ 1 cell/screen |                       |
| Firefox          | ≥ 1 cell/screen |                       |
| Safari           | ≥ 1 cell/screen |                       |
| Edge             | ≥ 1 cell/screen |                       |
| Samsung Internet | ≥ 1 cell/screen |                       |
| Desktop          | ≥ 1 cell/screen |                       |
| Tablet           | ≥ 1 cell/screen |                       |
| Phone            | ≥ 1 cell/screen |                       |
```

## Anti-Patterns

| Anti-Pattern                                                                  | Why it fails                                                                 | Correct approach                                                                                                        |
| ----------------------------------------------------------------------------- | ---------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| Testing only Blink browsers (Chrome, Edge, Opera)                             | Misses WebKit and Gecko rendering differences entirely                       | Always include at least one WebKit (Safari) and one Gecko (Firefox) cell                                                |
| Treating OS and browser as equivalent dimensions                              | Browser rendering engine is what matters, not the OS brand                   | Prioritise by engine; select OS based on where that engine is accessible                                                |
| Testing all three screens in a single browser session without resetting state | State from one screen can affect another                                     | Open each screen in a clean tab; verify the URL matches the intended screen                                             |
| Recording Fail without a screenshot                                           | Defect becomes unverifiable; reviewer cannot see what was wrong              | Screenshot is mandatory for every Fail cell                                                                             |
| Marking N/A for untested valid combinations                                   | Hides untested coverage gaps                                                 | Only use N/A for invalid OS–browser combinations; use Pending or Skip for untested valid cells                          |
| Performing full GUI checklist checks per cell                                 | This doubles or triples the time; compatibility testing has a narrower scope | Limit to: layout integrity, text rendering, image rendering, controls visible, basic interaction, responsive breakpoint |

## Best Practices

- **Always start with iOS Safari.** It is the highest-divergence engine and the hardest to reproduce locally. If you only have time for a few cells, do iOS Safari first.
- **Use BrowserStack Live or Screenshots for environments you cannot access locally.** Local Chrome on Windows cannot simulate real iOS Safari WebKit or Samsung Internet.
- **Reset state between cells.** Clear cookies or use an incognito window when switching between cells to avoid authentication or cached state affecting results.
- **Add the student ID email overlay before saving screenshots.** An overlay-less screenshot cannot be attributed to you if the file name is lost or renamed.
- **Test at the exact viewport specified.** "Phone" means the specified dimensions (e.g. 390×844), not just "a small screen". In BrowserStack, set the exact device model to match the viewport.
- **Note the defect type in Notes.** Use the vocabulary from `resources/defect-taxonomy.md` (overflow, overlap, broken layout, unreadable text, non-responsive control, broken image) so defects can be classified consistently across cells.

## Quality Checklist

### matrix-template.md

- [ ] Every OS in `os_list` appears in at least one planned cell per screen.
- [ ] Every browser in `browser_list` appears in at least one planned cell per screen.
- [ ] Every device class in `device_classes` appears in at least one planned cell per screen.
- [ ] Invalid OS–browser combinations marked as N/A with reason.
- [ ] Engine column is present and correct for each row.
- [ ] All content written in **English**.

### priority-guide.md

- [ ] iOS Safari (WebKit) and Firefox (Gecko) appear in Priority Tier 1.
- [ ] Every planned cell is listed with its priority tier and rationale.
- [ ] All content written in **English**.

### screenshot-naming.md

- [ ] Naming convention produces unique filenames per cell.
- [ ] `student_id_email` is embedded in the naming convention.
- [ ] Step-by-step overlay instructions are present.
- [ ] All content written in **English**.

### matrix-results.md

- [ ] All planned cells from matrix-template.md are present.
- [ ] Every Fail cell has a Notes entry describing the defect.
- [ ] Every Fail cell references a screenshot filename.
- [ ] Coverage Status table is present and up to date.
- [ ] All content written in **English**.

## Common Rationalisations to Reject

| Rationalisation                                                             | Why to reject                                                                                                                                                                |
| --------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| _"Chrome covers most users so I only need to test Chrome"_                  | Chrome uses Blink; Safari uses WebKit; Firefox uses Gecko. Each engine has distinct rendering behaviours. Testing only one engine leaves two major rendering paths untested. |
| _"I tested on my Mac so macOS is covered"_                                  | macOS coverage depends on which browser you used. Safari on macOS covers WebKit; Chrome on macOS covers Blink — these are different coverage items.                          |
| _"I'll use Chrome DevTools device emulation for iOS"_                       | Chrome DevTools emulates the viewport and user-agent string but uses Blink, not WebKit. iOS-specific WebKit bugs will not be caught. Use BrowserStack or a real iOS device.  |
| _"The Fail cell has a description so I don't need a screenshot"_            | A text description is not reproducible or verifiable by a reviewer who cannot access the test environment. Screenshot is mandatory.                                          |
| _"I tested all three screens in one browser session — that covers the row"_ | Each screen must be tested independently at the specified viewport. A single session may cache styles or state from screen to screen.                                        |

## Resources

| File                                   | Purpose                                                           |
| -------------------------------------- | ----------------------------------------------------------------- |
| `resources/rendering-engines-guide.md` | Browser-to-engine mapping, OS constraints, iOS WebKit rule        |
| `resources/defect-taxonomy.md`         | Standard defect types and descriptions for compatibility failures |
| `examples/example-matrix-output.md`    | Complete sample of all four output files for a 3-screen test      |
