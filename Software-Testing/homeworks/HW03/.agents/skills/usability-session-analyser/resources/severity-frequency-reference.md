# Severity & Frequency Reference — Usability Session Analyser

## Nielsen Severity Scale (0–4)

| Level | Label                   | Definition                                                                                                                         | Example                                                                                                                |
| ----- | ----------------------- | ---------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------- |
| **4** | Usability Catastrophe   | Prevents task completion; user cannot proceed without fix. Also applies to any legal accessibility violation (WCAG Level A or AA). | Participant cannot submit the form because required field validation blocks without indicating which field is wrong    |
| **3** | Major Usability Problem | Significant difficulty or delay; task completed only with substantial effort, workaround, or facilitator assistance                | Participant takes 3× the benchmark time due to unclear navigation; completes only after retracing steps multiple times |
| **2** | Minor Usability Problem | Causes friction or confusion; participant eventually completes the task without major difficulty                                   | Participant hesitates twice before finding the correct menu item but reaches it unassisted                             |
| **1** | Cosmetic Problem        | Visual inconsistency or minor annoyance; no functional impact on task completion                                                   | Inconsistent capitalisation on button labels across screens                                                            |
| **0** | Not a Problem           | Behaviour is as designed and acceptable; initial classification as a pain point was incorrect                                      | Participant read all options before selecting — this is expected and acceptable behaviour                              |

## Calibration Rules

**When choosing between two adjacent severity levels, choose the lower one.** Severity inflation is more harmful to report credibility than severity deflation. Reserve Severity 4 for genuine task blockers and verified WCAG violations.

**Severity 4 criteria (at least one must be true):**

- Task cannot be completed at all without fixing the issue
- Issue is a confirmed WCAG 2.2 Level A or Level AA violation
- Issue causes data loss or irreversible consequences for the user

**Severity 3 criteria (at least one must be true):**

- Task completed but required facilitator intervention or workaround
- Mean time on task exceeds 2× the benchmark due to this specific issue
- Multiple participants made the same error at the same point and had difficulty recovering

**Severity 2 criteria:**

- Task completed without assistance
- Participant hesitated or took a wrong path but self-corrected
- Issue caused mild confusion but did not require recovery effort

**Severity 1 criteria:**

- Task completed smoothly
- Issue noticed only upon debrief, not during the task
- No measurable impact on time, errors, or hesitations

## Criticality Formula

**Criticality = Severity (0–4) × Frequency (proportion of participants affected)**

| Severity | Frequency (N=5) | Criticality                                                 |
| -------- | --------------- | ----------------------------------------------------------- |
| 4        | 5/5 = 1.0       | 4.0 — top priority                                          |
| 4        | 3/5 = 0.6       | 2.4                                                         |
| 3        | 5/5 = 1.0       | 3.0                                                         |
| 3        | 4/5 = 0.8       | 2.4                                                         |
| 3        | 2/5 = 0.4       | 1.2                                                         |
| 2        | 5/5 = 1.0       | 2.0                                                         |
| 2        | 3/5 = 0.6       | 1.2                                                         |
| 1        | 5/5 = 1.0       | 1.0                                                         |
| 4        | 1/5 = 0.2       | 0.8 — despite high severity, low frequency reduces priority |

**Tiebreaker:** At equal criticality, systemic findings rank above isolated findings.

## Reporting Severity in the Usability Report

For each finding, report:

| Field       | Value                       |
| ----------- | --------------------------- |
| Severity    | 3 — Major Usability Problem |
| Frequency   | 4/5 participants (80%)      |
| Criticality | 3 × 0.8 = 2.4               |
| Type        | Systemic                    |

This format makes the ranking transparent and auditable.
