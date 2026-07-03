<div align="center">
  <h1>Homework 02 — Domain Testing on EShop</h1>
  <small>
    <strong>Student:</strong> Nguyễn Tấn Phát - 23127449
  </small> <br />
  <sub>June 20, 2026</sub>
</div>

**Student:** 23127449 - Nguyễn Tấn Phát (Group 06)  
**Submission Date:** 2026-06-20  
**Eshop SUT:** https://github.com/ttbhanh/eshop-sut  
**Testing Technique:** Domain Testing — Equivalence Partitioning + Boundary Value Analysis  
**AI Agent:** Antigravity CLI

## 1. Features Tested

| Pool | FR    | Feature                          | Test Layer         | Status    |
| ---- | ----- | -------------------------------- | ------------------ | --------- |
| A    | FR-01 | Account Registration             | Web UI + API       | Completed |
| B    | FR-07 | Shopping Cart                    | Web UI + API       | Completed |
| C    | FR-17 | Coupon Management (Admin)        | Web Admin UI + API | Completed |
| D    | FR-03 | Forgot Password & Reset Password | Mobile UI + API    | Completed |

## 2. Test Execution Summary

### 2.1 TC Statistics by Feature

| FR        | Feature              | EP TCs  | BVA TCs | Total Designed | Executed | Passed | Failed  | Blocked | Skipped | Pass Rate  |
| --------- | -------------------- | ------- | ------- | -------------- | -------- | ------ | ------- | ------- | ------- | ---------- |
| FR-01     | Account Registration | 20      | 18      | 38             | 38       | 0      | 38      | 0       | 0       | 0%         |
| FR-07     | Shopping Cart        | 22      | 20      | 42             | 42       | 3      | 39      | 0       | 0       | 7.14%      |
| FR-17     | Coupon Management    | 38      | 35      | 73             | 73       | 30     | 43      | 0       | 0       | 41.1%      |
| FR-03     | Forgot Password      | 31      | 15      | 46             | 46       | 24     | 22      | 0       | 0       | 52.17%     |
| **Total** |                      | **111** | **88**  | **199**        | **199**  | **57** | **142** | **0**   | **0**   | **28.64%** |

### 2.2 Key Metrics

| Metric                    | Value               |
| ------------------------- | ------------------- |
| **Pass Rate**             | 28.64% (57/199 TCs) |
| **TC Execution Coverage** | 100% (199/199 TCs)  |
| **Total Bugs Found**      | 29                  |

## 3. Defect Statistics

### 3.1 By Feature

| FR        | Feature              | Total Bugs | Fatal | Serious | Medium | Cosmetic |
| --------- | -------------------- | ---------- | ----- | ------- | ------ | -------- |
| FR-01     | Account Registration | 10         | 0     | 3       | 3      | 4        |
| FR-07     | Shopping Cart        | 7          | 2     | 2       | 2      | 1        |
| FR-17     | Coupon Management    | 6          | 0     | 1       | 5      | 0        |
| FR-03     | Forgot Password      | 6          | 0     | 4       | 2      | 0        |
| **Total** |                      | **29**     | **2** | **10**  | **12** | **5**    |

### 3.2 By Severity

| Severity  | Count  | Percentage |
| --------- | ------ | ---------- |
| Fatal     | 2      | 6.9%       |
| Serious   | 10     | 34.48%     |
| Medium    | 12     | 41.38%     |
| Cosmetic  | 5      | 17.24%     |
| **Total** | **29** | **100%**   |

## 4. Test Evidence

### 4.1 Session Recordings

| FR    | Feature         | Recording                                   |
| ----- | --------------- | ------------------------------------------- |
| FR-01 | Registration    | [Demo Pool A](https://youtu.be/64_G5GuGFKw) |
| FR-07 | Shopping Cart   | [Demo Pool B](https://youtu.be/uMYZascaxao) |
| FR-17 | Coupon CRUD     | [Demo Pool C](https://youtu.be/d1ST9Mz7LPI) |
| FR-03 | Forgot Password | [Demo Pool D](https://youtu.be/fkqbDcrvHHU) |

### 4.2 GitHub Issues

All bugs have been reported to the group repository:

| FR    | Bugs Reported | GitHub Issues                           |
| ----- | ------------- | --------------------------------------- |
| FR-01 | 10            | #1, #2, #3, #4, #5, #6, #7, #8, #9, #10 |
| FR-07 | 7             | #11, #12, #13, #14, #15, #16, #17       |
| FR-17 | 6             | #18, #19, #20, #21, #22, #23            |
| FR-03 | 6             | #24, #25, #26, #27, #28, #29            |

## 5. Agent Skills Demonstration

| #   | Skill                         | Purpose                                                      |
| --- | ----------------------------- | ------------------------------------------------------------ |
| 1   | requirement-analyzer          | Extract FR constraints, business rules, GUI/SEC requirements |
| 2   | domain-identifier             | Identify all input/output variables (direct and hidden)      |
| 3   | equivalence-partitioning      | Apply 4 EP guidelines; Combination and Isolation rules       |
| 4   | boundary-value-analysis       | Apply 9-point BVA to all ordered/numeric variables           |
| 5   | domain-coverage-reviewer      | QA gate: detect missing classes; AI gap analysis             |
| 6   | test-case-generator           | Compile final TC table from EP + BVA                         |
| 7   | test-case-reviewer            | QA gate: verify 7 characteristics, coverage completeness     |
| 8   | test-execution-assistant      | Generate bash scripts + DOM checks; record Pass/Fail         |
| 9   | bug-report-writer             | Batch-generate all bug reports from FAIL TCs                 |
| 10  | github-issue-writer           | Generate GitHub Issues guide; sync issue numbers back        |
| 11  | traceability-matrix-generator | Build FR → EC → TC → Bug traceability matrix                 |
| 12  | test-summary-generator        | Generate this README                                         |
| 13  | ai-audit-logger               | Log all AI interactions for the AI Audit Report              |

**Skill execution flow:** requirement-analyzer → domain-identifier → equivalence-partitioning → boundary-value-analysis → domain-coverage-reviewer → test-case-generator → test-case-reviewer → test-execution-assistant → bug-report-writer → github-issue-writer.  
**How to apply these skill?** → Follow the instructions in [skill-execution-flow.md](./assets/skill-execution-flow.md).  
**Link to demo video:** [Youtube URL](https://youtu.be/_ZgOWfymjdI).

## 6. Submission Contents

| Artifact                                   | Location                           | Status |
| ------------------------------------------ | ---------------------------------- | ------ |
| Main report (Domain Testing + BVA, 4 FRs)  | `reports/main-report.md` + PDF     | PASS   |
| Bug report (All bugs + GitHub Issue links) | `reports/bug-report.md` + PDF      | PASS   |
| AI Audit Report                            | `reports/ai-audit-report.md` + PDF | PASS   |
| AI Critique (200–300 words)                | `reports/ai-critique.md` + PDF     | PASS   |
| Test cases (4 × MD)                        | `test-cases/`                      | PASS   |
| Git commit log                             | `git-commit-log.txt`               | PASS   |
| EShop SUT workspace                        | `eshop-sut/`                       | PASS   |
| Demo videos                                | Section 4.1 + Section 5            | PASS   |

## 7. Self-Assessment

| No. | Criteria                                        | Max Grade | Self-Assessed Grade |
| --- | ----------------------------------------------- | --------- | ------------------- |
| 1   | Feature A: FR-01 — Domain Testing + BVA         | 25        | 25                  |
| 2   | Feature B: FR-07 — Domain Testing + BVA         | 25        | 25                  |
| 3   | Feature C: FR-17 — Domain Testing + BVA         | 25        | 25                  |
| 4   | Feature D: FR-03 — Mobile, Domain Testing + BVA | 15        | 15                  |
| 5   | Agent Skills                                    | 10        | 10                  |
|     | **Total**                                       | **100**   | **100**             |
