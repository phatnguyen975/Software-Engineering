# Skills Execution Guide — HW03 GUI & Usability Testing

> **Scenario A:** Admin creates and manages events.  
> This guide provides the complete step-by-step workflow, prompt templates, and human verification gates for invoking all skills in the correct order.

## Phase 1 — Shared GUI Checklist

### Step 1.1 — Explore EMS and note widget inventory

Before invoking the skill, manually explore screens A1–A5 in the browser and note any widgets or behaviors specific to EMS that are not in the standard widget list.

**EMS-specific context to document:**

```
- EN/VI i18n toggle in header — all UI text must switch instantly
- Image upload: thumbnail enforces 4:3 ratio, banner enforces 24:9 ratio
- Drag-drop reorder: dragged item shows opacity-50; all other buttons disabled during drag; order saved correctly after drop
- Status badge colors: 6 colors for different states (active, pending, rejected, waitlist, lecturer-role, inactive)
- Date validation: registration close must be before event end date
- Rich-text editor for event content description
- Icon picker with approximately 80 icons
```

### Step 1.2 — Invoke `gui-checklist-designer`

Copy this prompt exactly, fill in the bracketed values, and send to the agent:

```
/gui-checklist-designer
  sut_name: "EMS (Event Management System)"
  sut_type: "Web admin panel for academic event lifecycle management — create, configure, publish, and manage events and participants"
  ia_categories:
    - id: "IA-01"
      name: "General UI Standards"
      scope: "layout, alignment, typography, colour, consistency, i18n EN/VI, empty/loading states"
    - id: "IA-02"
      name: "Forms"
      scope: "labels, validation, error placement, required-field handling, uploads, rich-text editor"
    - id: "IA-03"
      name: "Navigation"
      scope: "menus, breadcrumbs, tabs, sidebar, drag-and-drop reorder, back/return actions, deep links"
    - id: "IA-04"
      name: "Feedback / State"
      scope: "toasts, badges, confirmation dialogs, progress bars, status colours, real-time updates"
  min_items: 45
  output_dir: "docs/"
```

**After AI output:**

> ⏸️ **HUMAN GATE 1A — Checklist Draft Review**
>
> Open `docs/shared-gui-checklist.md` and verify:
>
> - [ ] Total items ≥ 45
> - [ ] IA-01 has ≥ 10 items covering layout, i18n, empty state, loading state
> - [ ] IA-02 has ≥ 10 items covering all form widgets (date picker, file upload, rich-text)
> - [ ] IA-03 has ≥ 10 items covering drag-drop, keyboard nav, sidebar
> - [ ] IA-04 has ≥ 10 items covering toasts, badges (6 colours), real-time updates
> - [ ] Every item passes the actionability filter (clear Pass/Fail without asking anyone)
> - [ ] No subjective descriptions ("looks good", "feels intuitive")
> - [ ] At least 5 WCAG 2.2 items distributed across categories
> - [ ] EMS-specific items present for: i18n toggle, aspect ratio upload, drag-drop opacity, status badge colours
>
> **For each item you add beyond AI output:** record in `docs/checklist/ai-prompts.md`:
>
> - Item ID and description
> - Reason AI missed it (prompt lacked context / model limitation / EMS-specific behavior)
>
> When satisfied: proceed to Step 1.3

### Step 1.3 — Finalize and log

```
/ai-audit-log --last=3
```

Commit:

```bash
git add docs/checklist/
git commit -m "feat(checklist): shared gui checklist v1 — XX items, 4 IA categories"
```

## Phase 2 — Checklist Execution + Bug Reports

### Step 2.0 — Verify BrowserMCP before starting

Send to agent:

```
Please check whether chrome-devtools MCP is configured and connected.
Check .agents/mcp_config.json first, then ~/.gemini/config/mcp_config.json.
Report the connection status before we proceed.
```

> ⏸️ **HUMAN GATE 2A — BrowserMCP Status**
>
> - If agent reports **Connected** → proceed to Step 2.1
> - If agent reports **Not configured** → follow `browsermcp-setup-guide.md` and retry
> - If agent reports **Configured but not connected** → restart Antigravity, refresh MCP servers, retry

### Step 2.1 — Select ≥3 screens with justification

Based on exploring A1–A5, write your justification in `docs/screen-selection.md`.

Suggested selection rationale:

- **A2 — Add/Edit Event Form:** Most complex form on admin side; covers file upload, date range picker, rich-text editor, validation → best coverage of IA-02
- **A4 — Participants & Reviews:** Multiple status colours, approve/reject workflow, progress bar, export → best coverage of IA-04
- **A5 — Check-in Tab:** Real-time log updates, scan-state handling, different result states → covers IA-04 real-time behavior

Commit:

```bash
git add docs/execution/screen-selection.md
git commit -m "test(execution): screen selection justified — A2, A4, A5"
```

### Step 2.2 — Invoke `gui-checklist-executor` for each screen

**Repeat this block for each screen (A2, A4, A5).**

**For Screen A2 — Add/Edit Event Form:**

```
/gui-checklist-executor
  checklist_path: "docs/shared-gui-checklist.md"
  screen_id: "A2"
  screen_name: "Add/Edit Event Form"
  screen_url: "https://prod-dev.ems-fitus.cloud/dashboard/admin/events/create"
  sut_credentials: { "username": "admin@gmail.com", "password": "Admin@123" }
  screen_description: "Admin form for creating or editing an event. Contains: event title input, date-time range pickers (start/end date, registration open/close), rich-text editor for event content, thumbnail image upload (4:3 ratio enforced), banner image upload (24:9 ratio enforced), category dropdown, campus dropdown, registration toggle switches (student/lecturer/guest), max slots inputs per role, waitlist toggle, additional role input, Save Draft button, Publish button. Validation: registration close must be before event end date. EN/VI language toggle in header applies to all labels."
  output_dir: "docs/task01/A2"
```

> ⏸️ **HUMAN GATE 2B — How-to-Test Draft Review (per screen)**
>
> Open `docs/execution/execution-A2.md` and verify:
>
> - [ ] Every checklist item has screen-specific How-to-test steps (not generic)
> - [ ] Items referencing widgets not on this screen are noted but NOT pre-marked N/A (N/A will be assigned during live testing)
> - [ ] Steps reference actual element names visible on this screen
> - [ ] Edge cases in `docs/execution/edge-cases-A2.md` cover: empty form submit, invalid date range, wrong file format upload, double-click publish, browser back
>
> Reply to agent:
>
> - `APPROVED` — to proceed to live testing
> - `FAILED: <specific feedback>` — to request corrections

**After APPROVED, agent proceeds to live BrowserMCP testing.**

During testing, monitor the agent's inline reports:

```
✓ IA01-001 [Pass] — ...
✗ IA02-003 [Fail] — ...
~ IA03-005 [Need Human] — ...
— IA01-004 [N/A] — ...
```

> ⏸️ **HUMAN GATE 2C — Execution Results Review (per screen)**
>
> After agent completes all items:
>
> - [ ] Review all **Need Human** items in `docs/task01/execution-A2.md` → manually test each one and update Result + Notes in the file
> - [ ] Spot-check 20% of **Pass** items to verify AI judgment
> - [ ] For every **Fail** item: take a screenshot → save to `screenshots/task01/A2-<item-id>-fail.png`
> - [ ] Verify edge case results in `docs/task01/edge-cases-A2.md`
>
> Reply to agent:
>
> - `APPROVED` — to proceed to bug report generation
> - `FAILED: <items to re-evaluate>` — to request re-check

**After APPROVED, agent generates bug report.**

> ⏸️ **HUMAN GATE 2D — Bug Report Review (per screen)**
>
> Open `docs/execution/bug-report-A2.md` and verify:
>
> - [ ] Every Fail item is referenced in at least one bug group
> - [ ] No two bug groups describe the same root cause
> - [ ] Severity ratings are calibrated correctly (4=blocks task, 1=cosmetic)
> - [ ] Each bug has concrete Steps to Reproduce
> - [ ] Format matches `resources/bug-report-format.md`
>
> Reply:
>
> - `APPROVED` — proceed to bug logging
> - `FAILED: <feedback>` — request corrections

Commit per screen:

```bash
git add docs/execution/
git commit -m "test(execution): screen A2 complete — XX pass, XX fail, XX need-human resolved"
```

**Repeat Steps 2.2 for A4 and A5.**

## Phase 3 — Usability Testing

### Step 3.1 — Invoke `usability-test-designer`

```
/usability-test-designer
  sut_name: "EMS (Event Management System)"
  scenario_description: "Admin creates, configures, and publishes an event on EMS, including setting registration rules with slot limits for different participant roles"
  screens_list:
    - "A1: Event List"
    - "A2: Add/Edit Event Form"
    - "A4: Participants and Reviews"
  user_profile: "Faculty staff or senior university students (Year 3+) comfortable with web-based admin tools; no prior EMS experience; not enrolled in the current software testing course"
  test_type: "assessment"
  num_tasks: 1
  num_participants: 5
  metrics_to_collect:
    - "task success (Completed / Partial / Failed)"
    - "time on task (seconds, from task start to completion or abandonment)"
    - "error count (incorrect navigations, wrong submissions, input mistakes requiring recovery)"
    - "hesitation count (pause > 5 seconds with no action and no verbalisation)"
    - "SUS score (Brooke 1996, 10-item scale administered after all tasks)"
  output_dir: "docs/task02"
  benchmark_time: "8 minutes"
  session_format: "unmoderated-remote"
```

> ⏸️ **HUMAN GATE 3A — Task Scenario Approval (CRITICAL)**
>
> Open `docs/usability/task-scenario-T1.md` and verify using the self-check list:
>
> - [ ] The task is goal-based — NO UI element names mentioned (no "click", "button", "tab", "form", "toggle")
> - [ ] Realistic context that makes sense for the target user profile
> - [ ] Success criterion is specific and has observable indicators for Completed/Partial/Failed
> - [ ] Natural completion of the task requires visiting A2, A4, and A5
> - [ ] Benchmark time stated (8 minutes)
> - [ ] Task does not reveal the correct path or feature to use
>
> Also review:
>
> - [ ] `docs/usability/probe-questions-T1.md` — 3–5 questions, task-specific, no UI language
> - [ ] `docs/usability/sus-instrument.md` — 10 SUS statements unmodified
> - [ ] `docs/usability/observation-template.md` — fields and instructions complete
> - [ ] `docs/usability/session-notes/P1-session.md` through `P5-session.md` — generated correctly
>
> **DO NOT proceed to recruit participants until this gate is APPROVED.**
> A flawed task scenario cannot be fixed after sessions have started.
>
> Reply:
>
> - `APPROVED` — proceed to recruitment
> - `FAILED: <specific feedback>` — request revision

### Step 3.2 — Recruit participants and run pilot (Day 5)

**Recruitment criteria:**

- Faculty staff or Year 3+ students comfortable with web admin tools
- No prior EMS experience
- NOT enrolled in the current software testing course
- NOT a member of your testing group
- Must have real contact (Zalo/phone) — TA may call to verify

**Pilot test (1 person, not counted in the 5):**

- Run the complete session flow with one person
- Verify: task text is understood, session file is fillable, timing is realistic
- If any issue found → revise task scenario and re-submit Gate 3A before main sessions

### Step 3.3 — Distribute session files to participants

Send each participant their `P{n}-session.md` file with the following instructions:

```
Subject: Usability Study Participation — EMS System

Dear [Name],

Thank you for agreeing to participate in our usability study.

Please complete the attached session file by following the instructions inside.
The file walks you through:
1. A brief introduction and setup (5 minutes)
2. One task to attempt on the EMS system (aim for 8 minutes; maximum 16 minutes)
3. Reflection questions after the task
4. A short questionnaire (SUS — 10 questions, 3 minutes)

Access the system at: https://promoter-starboard-prude.ngrok-free.dev/
Use this admin account: admin@gmail.com / Admin@123

Please record your actions and thoughts honestly in the file.
When complete, save the file and send it back to me.

If you have any questions, contact me at: [your contact]

Thank you!
```

After receiving all 5 completed session files:

- Save each as `docs/usability/session-notes/P{n}-session.md`
- Commit: `git commit -m "test(usability): all 5 sessions collected"`

### Step 3.4 — Invoke `usability-session-analyser`

```
/usability-session-analyser
  session_notes_paths:
    - "docs/usability/session-notes/P1-session.md"
    - "docs/usability/session-notes/P2-session.md"
    - "docs/usability/session-notes/P3-session.md"
    - "docs/usability/session-notes/P4-session.md"
    - "docs/usability/session-notes/P5-session.md"
  test_plan_path: "docs/usability/test-plan.md"
  task_scenario_paths:
    - "docs/usability/task-scenario-T1.md"
  participant_table_path: "docs/usability/participant-table.md"
  heuristic_set: "Nielsen 10"
  output_dir: "docs/usability"
  report_audience: "academic"
```

> ⏸️ **HUMAN GATE 3B — Usability Report Review**
>
> Open `docs/usability/usability-report.md` and verify:
>
> - [ ] SUS scores computed correctly (verify 1–2 manually using formula)
> - [ ] Task success rates match actual session outcomes
> - [ ] Finding titles are specific and observable (not heuristic paraphrases)
> - [ ] Severity ratings are calibrated (4=blocks task completion, 1=cosmetic)
> - [ ] Each finding has direct evidence (participant ID + quote or timestamp)
> - [ ] Recommendations are concrete and actionable
> - [ ] Limitations section present
> - [ ] Executive summary is accurate
>
> Fill in `docs/usability/participant-table.md` with real participant data (masked contacts)
>
> Reply:
>
> - `APPROVED` — finalize report
> - `FAILED: <feedback>` — request corrections

## Phase 4 — Cross-Platform Testing

### Step 4.1 — Invoke `compatibility-matrix-runner`

```
/compatibility-matrix-runner
  sut_url: "https://prod-dev.ems-fitus.cloud/"
  screens_list:
    - id: "A2"
      name: "Add/Edit Event Form"
    - id: "A4"
      name: "Participants and Reviews"
    - id: "A1"
      name: "Event List"
  os_list:
    - "Windows 11"
    - "macOS Ventura"
    - "Android 14"
  browser_list:
    - "Chrome"
    - "Firefox"
    - "Safari"
    - "Edge"
    - "Samsung Internet"
  device_classes:
    - name: "Desktop"
      viewport: "1920×1080"
    - name: "Tablet"
      viewport: "768×1024"
    - name: "Phone"
      viewport: "390×844"
  student_id_email: "MSSV@....edu.vn"
  output_dir: "docs/task03"
  coverage_mode: "minimum"
```

> ⏸️ **HUMAN GATE 4A — Matrix Structure Review**
>
> Open `docs/compatibility/matrix-template.md` and verify:
>
> - [ ] All 4 OS appear in at least one planned cell per screen
> - [ ] All 5 browsers appear in at least one planned cell per screen
> - [ ] All 3 device classes appear in at least one planned cell per screen
> - [ ] Invalid combinations (Safari on Windows, Samsung Internet on iOS) marked N/A
> - [ ] Priority guide lists iOS Safari and Firefox as Tier 1
> - [ ] Screenshot naming convention in `screenshot-naming.md` is clear
>
> Reply:
>
> - `APPROVED` — start testing
> - `FAILED: <feedback>` — adjust matrix

### Step 4.2 — Execute matrix cells (human testing)

Work through cells in priority order from `docs/compatibility/priority-guide.md`.

**For each cell:**

1. Open the environment in BrowserStack (Live for interactive, Screenshots for fan-out)
2. Navigate to each screen URL
3. Apply the 6-point visual + smoke check (see Skill SKILL.md Test Scope section)
4. Take screenshot → apply MSSV overlay → save with naming convention
5. Record result in feedback to agent

**BrowserStack Free Trial tips:**

- Use **Live** mode for iOS Safari and Samsung Internet (need interaction)
- Use **Screenshots** mode for Chrome/Firefox/Edge on Desktop (fast fan-out)
- Session limit: ~30 minutes per Live session on free trial — plan efficiently

**Report results to agent in this format:**

```
RESULT: iOS 17 / Safari / Phone / A2
Status: Fail
Screenshot: ios-17_safari_phone_a2_fail.png
Notes: OVERFLOW: Form panel extends beyond right viewport. Horizontal scrollbar visible.
Registration config section partially hidden.

RESULT: iOS 17 / Safari / Phone / A4
Status: Pass
Screenshot: ios-17_safari_phone_a4_pass.png
Notes:

RESULT: Windows 11 / Firefox / Desktop / A2
Status: Pass
Screenshot: windows-11_firefox_desktop_a2_pass.png
Notes:
```

> ⏸️ **HUMAN GATE 4B — Coverage Verification**
>
> After all planned cells are tested, ask agent for coverage summary:
>
> ```
> Please generate the coverage summary for docs/compatibility/matrix-results.md
> ```
>
> Verify from the summary:
>
> - [ ] All 4 OS have at least one Pass cell per screen
> - [ ] All 5 browsers have at least one Pass cell per screen
> - [ ] All 3 device classes have at least one Pass cell per screen
> - [ ] Every Fail cell has a screenshot reference
> - [ ] Every Fail cell has a Notes entry with defect type code

## Phase 5 — Reports + AI Audit

### Step 5.1 — Compile main-report.md

The main report compiles content from all previous phases. Draft manually or ask agent:

```
Please help me compile docs/reports/main-report.md by pulling together:
1. Scenario selection and screen justification from docs/execution/screen-selection.md
2. Checklist execution summary from docs/execution/execution-A2.md, A4.md, A5.md (summary table: screen × items tested × pass × fail × need-human)
3. Usability report summary from docs/usability/usability-report.md (methodology, participant table, metrics, top 3 findings, recommendations)
4. Cross-platform report from docs/compatibility/matrix-results.md (matrix summary, fail cells with defect descriptions)
Use the structure required by HW03 §15.
```

> ⏸️ **HUMAN GATE 5A — Main Report Review**
>
> Verify the report covers all required §15 items:
>
> - [ ] Chosen scenario and screens with justification
> - [ ] Checklist execution results per screen
> - [ ] Usability Report (task scenario, participants masked, metrics, findings, recommendations)
> - [ ] Cross-platform compatibility report (matrix, defects)
>
> Export PDF: use browser print → Save as PDF, or Pandoc:
>
> ```bash
> pandoc docs/reports/main-report.md -o docs/reports/main-report.pdf
> ```

### Step 5.2 — Write AI Critique (200–300 words)

Write manually in `docs/reports/ai-critique.md`. Must answer:

1. **What did the AI miss in the checklist?** And why — was it a prompt issue, model limitation, or EMS-specific behavior the AI couldn't know?
2. **Did the task scenario draft have any anchoring problems?** What did you have to change and why?
3. **What principle did you learn about collaborating with AI in this assignment?** Be specific — not "AI is helpful", but a concrete lesson about how to guide AI effectively.

Submit to Moodle before deadline.

## Quick Reference — All Skill Invocations

| Phase | Skill                         | Prompt file section               |
| ----- | ----------------------------- | --------------------------------- |
| 1.2   | `gui-checklist-designer`      | Step 1.2                          |
| 2.2   | `gui-checklist-executor`      | Step 2.2 (×3 screens)             |
| 3.1   | `usability-test-designer`     | Step 3.1                          |
| 3.4   | `usability-session-analyser`  | Step 3.4                          |
| 4.1   | `compatibility-matrix-runner` | Step 4.1                          |
| All   | `ai-audit-log`                | After every AI interaction        |
