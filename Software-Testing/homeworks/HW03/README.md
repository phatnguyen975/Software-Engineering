<div align="center">
  <h1>Homework 03 — GUI & Usability Testing on EMS</h1>
  <small>
    <strong>Student:</strong> Nguyễn Tấn Phát — 23127449
  </small> <br />
  <sub>August 03, 2026</sub>
</div>

## Self-Assessment Table

| **No.** | **Criteria**                                                                                    | **Grade** | **Self-Assessed Grade** |
| ------- | ----------------------------------------------------------------------------------------------- | --------- | ----------------------- |
| **1a**  | Task 1A — Shared checklist (> 40 items, IA-01…IA-04) + reference sources + AI prompts _(group)_ | 15        | 15                      |
| **1b**  | Task 1B — Checklist execution on ≥ 3 screens + bug reports _(individual)_                       | 15        | 15                      |
| **2**   | Task 2 — User testing with 5 real users (scenario + 5 sessions + analysis → Usability Report)   | 25        | 25                      |
| **3**   | Task 3 — Cross-Browser / Cross-Platform matrix (3 OS × 5 browsers × 3 device classes)           | 25        | 25                      |
| **4**   | Bug & Usability Findings submission (Google Form) + aggregated log                              | 10        | 10                      |
| **5**   | Agent Skills                                                                                    | 10        | 10                      |
|         | **Total**                                                                                       | **100**   | **100**                 |

## Test Summary

- **Scenario Chosen:** Scenario A (Admin creates and manages events)
- **Screens Tested:** A1 (Event List), A2 (Add/Edit Event Form), A4 (Participants & Reviews Approval)
- **Checklist Summary:**
  - Designed: 62 items
  - Executed: 186 executions (62 items per screen)
  - Passed: 22 + 25 + 19 = 66
  - Failed: 14 + 15 + 10 = 39
  - Not applicable: 26 + 22 + 33 = 81
- **Total Bugs Logged:** 28 GUI bugs (10 in A1, 11 in A2, 7 in A4) + 7 Compatibility bugs
- **User Testing:**
  - Participants: 5
  - Usability Issues: 7 findings (1 Critical, 2 Major, 2 Minor, 2 Cosmetic)
  - Average SUS Score: 80.0
- **Compatibility Testing:** 15 cells covered across 3 OS (Windows, macOS, Android), 5 Browsers (Edge, Chrome, Safari, Firefox, Samsung Internet), and 3 Device classes (Desktop, Tablet, Phone).
- **Demo Videos:** [Link to Skills Execution Flow Demo](https://youtu.be/HgIFJLFLMtI)

## Directory Structure

```text
23127449_HW03_AI_GUIUsability_EMS_100/
├── .agents/                                      # AI Agent skills for GUI testing, compatibility testing, and auditing
│   ├── mcp_config.json
│   └── skills/
│       ├── ai-audit-log/
│       ├── compatibility-matrix-runner/
│       ├── gui-checklist-designer/
│       ├── gui-checklist-executor/
│       ├── usability-session-analyser/
│       └── usability-test-designer/
├── README.md                                     # Entry point and test summary
├── docs/                                         # Main workspace containing detailed outputs for each testing phase
│   ├── audit/
│   │   └── ai/
│   │       ├── 23127449-task02-2026-07.log.md
│   │       ├── 23127449-task03-2026-08.log.md
│   │       ├── 23127449-task1a-2026-07.log.md
│   │       └── 23127449-task1b-2026-07.log.md
│   ├── requirements.md
│   ├── screen-selection.md
│   ├── shared-gui-checklist.md
│   ├── task01/
│   │   ├── A1/
│   │   │   ├── bug-report-A1.md
│   │   │   ├── edge-cases-A1.md
│   │   │   └── execution-A1.md
│   │   ├── A2/
│   │   │   ├── bug-report-A2.md
│   │   │   ├── edge-cases-A2.md
│   │   │   └── execution-A2.md
│   │   ├── A4/
│   │   │   ├── bug-report-A4.md
│   │   │   ├── edge-cases-A4.md
│   │   │   └── execution-A4.md
│   │   └── gui-report.md
│   ├── task02
│   │   ├── metrics-summary.md
│   │   ├── observation-template.md
│   │   ├── participant-table.md
│   │   ├── probe-questions-T1.md
│   │   ├── session-notes/
│   │   │   ├── P1-session.md
│   │   │   ├── P2-session.md
│   │   │   ├── P3-session.md
│   │   │   ├── P4-session.md
│   │   │   └── P5-session.md
│   │   ├── sus-instrument.md
│   │   ├── sus-scores-computed.md
│   │   ├── task-scenario-T1.md
│   │   ├── test-plan.md
│   │   └── usability-report.md
│   └── task03/
│       ├── compatibility-report.md
│       ├── matrix-results.md
│       ├── matrix-template.md
│       ├── priority-guide.md
│       └── screenshot-naming.md
├── reports/                                      # Final compiled markdown reports ready for PDF export
│   ├── ai-audit-report.md
│   ├── ai-critique.md
│   ├── bug-report.md
│   └── main-report.md
├── screenshots                                   # Visual evidences
│   ├── task01
│   │   ├── A1
│   │   ├── A2
│   │   └── A4
│   └── task03
│       ├── A1
│       ├── A2
│       └── A4
└── skills-execution-guide.md
```
