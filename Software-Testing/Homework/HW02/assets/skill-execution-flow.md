# HW02 — Skill Execution Flow

> Detailed prompting guide for all 13 skills across 4 FRs

## Part 1 — How to Use This Guide

This guide defines the **exact prompting sequence** to follow in Antigravity CLI for each of the 4 FRs. Every step includes:

- Which skill to invoke
- The exact prompt to type
- What to do with the agent output
- When to commit to Git

**Golden rules:**

1. Always wait for Antigravity's output and your APPROVE before proceeding to the next skill.
2. Log every session immediately using `ai-audit-logger` — do not defer.
3. If Antigravity generates something incorrect, type `EDIT: [your correction instruction]` — do not APPROVE and fix later.
4. Commit to Git after every skill completes successfully.

## Part 2 — Skill Execution Flow (Per FR)

Run this entire flow for each FR in order: FR-01 → FR-07 → FR-17 → FR-03.

### STEP 0 — Skill: `ai-audit-logger` (run after EVERY step)

**Run this after every skill session completes.**

**Prompt:**

```
Use the ai-audit-logger skill.

Log this interaction for FR-{nn}.
Skill used: {skill-name}
Task: {one-line description of what was just done}

Prompt I gave: {paste or summarize the prompt you just used}

AI output summary: {summarize in 3-5 bullets what Antigravity generated}

My review notes:
- Accepted as-is: {list}
- Modified: {list and reason}
- Added manually: {list — these are AI gaps}
- Rejected: {list and reason}

Append this entry to: qa-artifacts/ai-audit/FR{nn}-ai-audit.md
```

### STEP 1 — Skill: `requirement-analyzer`

**Purpose:** Extract all constraints, business rules, actors, and GUI/SEC requirements from the SRS before any EP or BVA work begins.

**Prompt to type in Antigravity:**

```
Use the requirement-analyzer skill.

Analyze FR-{nn} from the EShop SRS.

Feature: {Feature Name}
FR ID: FR-{nn}

Read the following context files before starting:
- .agents/context/eshop-srs.md (look for FR-{nn} section)
- .agents/context/eshop-api-spec.md (look for related endpoints)

Follow all steps in the skill (A through G) in order.
Output the result to: qa-artifacts/requirements/FR{nn}-requirement-analysis.md
```

**After agent responds:**

- Read the full output carefully
- Verify: all input fields listed? All GUI/SEC requirements captured? Business rules complete?
- If correct → type `APPROVE`
- If missing something → type `EDIT: [describe what is missing or wrong]`
- After APPROVE → git commit: `feat(FR{nn}): requirement analysis`
- Immediately run `ai-audit-logger` (see Step 0-Log below)

### STEP 2 — Skill: `domain-identifier`

**Purpose:** Step 1 of Domain Testing — identify all direct and indirect input/output variables.

**Prompt:**

```
Use the domain-identifier skill.

Feature: FR-{nn} — {Feature Name}

The requirement analysis is complete. Read it at:
qa-artifacts/requirements/FR{nn}-requirement-analysis.md

Also read: .agents/context/eshop-srs.md and .agents/context/eshop-api-spec.md

Identify ALL input variables (direct and hidden/indirect) and ALL output variables
(direct and hidden/indirect) for this feature.

Pay special attention to the Common AI Blind Spots section in the skill.

Append the output as Step 1 to: qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md
```

**After agent responds:**

- Verify: hidden inputs present? (auth tokens, DB state, session state, counters)
- Verify: hidden outputs present? (DB changes, DOM changes, auth state)
- Verify: every variable has a test channel assigned
- Especially check the blind spots table in the skill for this FR
- If correct → APPROVE → git commit: `feat(FR{nn}): domain analysis step 1 - variables`
- Run `ai-audit-logger`

### STEP 3 — Skill: `equivalence-partitioning`

**Purpose:** Steps 2+3 of Domain Testing — partition classes and select representatives.

**Prompt:**

```
Use the equivalence-partitioning skill.

Feature: FR-{nn} — {Feature Name}

The variable list is ready at:
qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md (Step 1 section)

Apply all 4 EP Guidelines to EVERY input variable identified.
Then apply the Combination Rule for valid classes and the Isolation Rule for invalid classes.

Important — do NOT miss these for FR-{nn}:
{Paste the relevant row from the "EShop-Specific EP Patterns" section of the skill}

For FR-01 add:
- Special character outside allowed set @$!%*?& (e.g., Test#123) as a separate invalid class
- confirmPassword mismatch as a separate invalid class
- Email already exists in DB as a separate invalid class

For FR-07 add:
- Duplicate product add (same product ID) as a separate valid class for merge behavior test
- Quantity = 0 as a separate invalid class (boundary case)

For FR-17 add:
- User JWT token calling admin endpoint as a separate auth invalid class
- discount_value = 0 exactly as a separate invalid class (boundary at zero)

For FR-03 add:
- OTP from a different email (cross-email attack) as a separate invalid class
- OTP already used on a previous reset attempt as a separate invalid class

Append the output as Step 2 and Step 3 to:
qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md
```

**After agent responds:**

- Check every variable has a guideline label (G1/G2/G3/G4)
- Check every required field has an `empty` class
- Check Isolation Rule: no invalid TC has 2 invalid inputs
- Check the FR-specific patterns above are all present
- If correct → APPROVE → git commit: `feat(FR{nn}): domain analysis step 2-3 - EP classes`
- Run `ai-audit-logger`

### STEP 4 — Skill: `boundary-value-analysis`

**Purpose:** Step 4 of Domain Testing — target all boundary points using 9-point strategy.

**Prompt:**

```
Use the boundary-value-analysis skill.

Feature: FR-{nn} — {Feature Name}

The EP classes are ready at:
qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md (Step 2+3 section)

From that output, identify all variables with ordered/numeric constraints and apply
the 9-point BVA strategy to each one.

Remember to apply BVA to:
- Numeric fields (quantity, discount_value, min_order_amount, max_uses_per_user)
- String LENGTH fields (password length, name length, coupon code length)
- Date fields (expired_at)
- NOT just numbers — string length is a boundary variable too

For any UB that is not specified in the SRS, note it as "unspecified" and include
a +alpha test case with a very large value.

Save the output to:
qa-artifacts/boundary-analysis/FR{nn}-boundary-analysis.md
```

**After agent responds:**

- Verify: string length fields have BVA tables (not just numeric fields)
- Verify: -alpha (empty/0) and +alpha (very large) points are present
- Verify: each BVA point is a separate TC row with a unique ID
- Verify: all other inputs in each BVA TC are VALID (not mixed invalid)
- If correct → APPROVE → git commit: `feat(FR{nn}): boundary analysis - BVA 9-point`
- Run `ai-audit-logger`

### STEP 5 — Skill: `domain-coverage-reviewer`

**Purpose:** QA Gate — detect missing classes, verify rules, perform AI gap analysis.

**Prompt:**

```
Use the domain-coverage-reviewer skill.

Feature: FR-{nn} — {Feature Name}

Review the complete domain analysis at:
qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md

And the boundary analysis at:
qa-artifacts/boundary-analysis/FR{nn}-boundary-analysis.md

Run all checks in the skill:
- EP Guidelines compliance for each variable
- Missing class detection (especially B1 through B6)
- Isolation Rule compliance scan
- Combination Rule scan
- BVA completeness check

Then write the AI gap analysis section answering:
1. What did the AI generate correctly?
2. What did the AI miss?
3. Root cause for each miss
4. Leave the "Lesson Learned" as a placeholder (I will write it in ai-critique.md)

Append the output as Step 5 to:
qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md
```

**After agent responds:**

- Read the missing classes section carefully — AI might still miss things here
- Manually add any class you know should be there but the reviewer missed
- Verify the isolation rule scan caught all violations
- If the reviewer found violations → type `EDIT: [instruction to fix]` before APPROVE
- After APPROVE → git commit: `feat(FR{nn}): domain coverage review and AI gap analysis`
- Run `ai-audit-logger`

### STEP 6 — Skill: `test-case-generator`

**Purpose:** Compile all EP classes and BVA points into the final TC table.

**Prompt:**

```
Use the test-case-generator skill.

Feature: FR-{nn} — {Feature Name}

Read the complete domain analysis (including the gap analysis) at:
qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md

And the boundary analysis at:
qa-artifacts/boundary-analysis/FR{nn}-boundary-analysis.md

Generate the full test case table with both EP TCs (FR{nn}-EP-001, 002, ...) and
BVA TCs (FR{nn}-BVA-001, 002, ...).

For every TC:
- Use the "Action + Function + Operating Condition" objective syntax
- Include all 9 mandatory columns including Test Channel and EC/BVA Ref
- Write specific concrete Expected Results citing the FR number (e.g., per FR-{nn})
- Add a Teardown step for any TC that creates persistent data
- Assign the correct Test Channel (UI / API / Role-Auth / DOM / State)
- Steps of each TC must as most detailed as

Save the output to (Do not wait for my APPROVAL):
qa-artifacts/test-cases/FR{nn}-test-cases.md
```

**After agent responds:**

- Scan the objective column: all follow `Action + Function + Condition`?
- Scan Expected Results: are they specific? Do they all have FR citations?
- Scan Test Channel: are Role-Auth tests assigned to Role-Auth (not UI)?
- Scan for teardown steps on TCs that create data
- Count: are all ECs from Step 2+3 covered? Are all BVA points covered?
- If correct → APPROVE → git commit: `feat(FR{nn}): test cases generated`
- Run `ai-audit-logger`

### STEP 7 — Skill: `test-case-reviewer`

**Purpose:** QA Gate — verify TC quality before execution begins.

**Prompt:**

```
Use the test-case-reviewer skill.

Feature: FR-{nn} — {Feature Name}

Review the test case table at:
qa-artifacts/test-cases/FR{nn}-test-cases.md

Cross-reference against:
- EC list in qa-artifacts/domain-analysis/FR{nn}-domain-analysis.md
- BVA points in qa-artifacts/boundary-analysis/FR{nn}-boundary-analysis.md

Run all 3 tiers of checks:
- Tier 1 Critical: Isolation Rule, vague Expected Results, missing FR citations, defect masking, missing TC for EC
- Tier 2 Serious: objective syntax, pre-condition completeness, concrete test data, teardown, channel correctness, missing BVA TCs
- Tier 3 Cosmetic: ID format, numbered steps, language consistency

Append the review report to (Do not wait for my APPROVAL):
qa-artifacts/test-cases/FR{nn}-test-cases.md

End with a clear verdict: APPROVED or NEEDS REVISION.
```

**After agent responds:**

- If NEEDS REVISION: fix all Critical violations before proceeding
  - Type: `EDIT: Fix the following Critical violations: [list them]`
  - After fixes are shown → APPROVE the corrected version
  - Re-run the reviewer: `Re-run the test-case-reviewer on the corrected version`
- If APPROVED → git commit: `feat(FR{nn}): test cases reviewed and approved`
- Run `ai-audit-logger`

### STEP 8 — Skill: `test-execution-assistant`

**Purpose:** Generate all execution scripts and UI checklists, then record results after manual execution.

**Phase A — Generate scripts (run this FIRST)**

```
Use the test-execution-assistant skill — Phase A.

Feature: FR-{nn} — {Feature Name}

Read the approved test cases at:
qa-artifacts/test-cases/FR{nn}-test-cases.md

First, show me the TC Classification table (SCRIPT-FULL / SCRIPT-PARTIAL / MANUAL / DOM)
and wait for my confirmation before generating any scripts.

Then generate:
1. scripts/curl/FR{nn}-api-tests.sh
   - Use start_tc/end_tc wrappers for every TC
   - Use assert_db via sqlite3 for all DB state checks and teardowns
   - Use assert_db BCRYPT for all password hash verifications (SEC-01)
   - Role-Auth TCs: test all 3 token states inside one start_tc/end_tc block
   - Pattern E for any state change triggered by a UI action

2. scripts/curl/FR{nn}-dom-checks.js
   - One check() call per DOM-channel TC
   - Include only the standard library checks relevant to this FR

3. qa-artifacts/execution-results/FR{nn}-execution-results.md
   - Pre-fill Expected Results from TC table
   - Mark each TC with its automation category
   - Leave Observed Result and Status blank

Don't need to show me the full content of all 3 deliverables for APPROVAL, generate and write directly to those files.
```

**After agent responds:**

- Review all 3 deliverables carefully
- Verify: cURL script covers ALL non-UI TCs
- Verify: Role-Auth blocks test all 3 token states
- Verify: DOM scripts use the correct DOM checks from the skill library
- If correct → APPROVE
- git commit: `feat(FR{nn}): execution scripts and blank results template generated`

**Phase A — Manual Execution (you do this)**

After approving Phase A output:

```bash
# 1. Make the script executable
chmod +x scripts/curl/FR{nn}-api-tests.sh

# 2. Run all API/DB/State tests and save output
bash scripts/curl/FR{nn}-api-tests.sh 2>&1 | tee /tmp/FR{nn}-script-output.txt

# 3. For DOM checks: open scripts/curl/FR{nn}-dom-checks.js
#    Navigate browser to the target page, open DevTools Console (F12),
#    paste the entire file content, press Enter, screenshot the console output
```

- **For MANUAL TCs:** Test in browser/Expo and note your observations.
- No per-TC screenshots needed — record the full session as one video.

**Phase B — Record results (run after ALL manual execution)**

```
Use the test-execution-assistant skill — Phase B.

Feature: FR-{nn} — {Feature Name}

SCRIPT OUTPUT (paste the full summary block from terminal):
[paste here]

DOM OUTPUT (paste the DOM CHECK RESULTS block from DevTools console):
[paste here — or "N/A: no DOM TCs for this FR"]

MANUAL UI RESULTS:
- FR{nn}-EP-{nnn}: PASS — {brief observation}
- FR{nn}-EP-{nnn}: FAIL — {exact description of what went wrong}

Update BOTH files:
1. qa-artifacts/execution-results/FR{nn}-execution-results.md
2. qa-artifacts/test-cases/FR{nn}-test-cases.md (Observed Result + Status columns)
```

**After all TCs recorded:**

- Update execution summary table with final counts
- git commit: `test(FR{nn}): execute test cases - record observed results`
- Run `ai-audit-logger`

### STEP 9 — Skill: `bug-report-writer` (run once per FAIL)

**Purpose:** Write a complete professional bug report for each failed TC.

**When to run:** Immediately after Phase B identifies a FAIL. You provide the observed results from your script output and screenshots.

**Prompt:**

```
Use the bug-report-writer skill.

Feature: FR-{nn} — {Feature Name}

Read all FAIL TCs from:
- qa-artifacts/test-cases/FR{nn}-test-cases.md  (Status = FAIL)
- qa-artifacts/execution-results/FR{nn}-execution-results.md  (Status = FAIL)
- .agents/context/eshop-srs.md  (for FR/SEC citations)
- .agents/context/eshop-api-spec.md  (for API details in Steps to Reproduce)

First, analyze all FAIL TCs and group them by root cause. Show me the "Bug Groups" list (including Bug IDs and Affected TCs)
Wait for my confirmation, then generate the complete qa-artifacts/bug-reports/FR{nn}-bugs.md covering every BUG GROUP in one pass
```

**After agent responds:**

- After generation: review the Bug Summary Table at the end of the file
- Verify: Steps to Reproduce use exact values, not `{placeholder}` text
- Verify: all Severity/Priority have written rationale
- If correct → APPROVE → git commit: `bug(FR{nn}): generate all bug reports`
- Run Step 10 immediately after

### STEP 10 — Skill: `github-issue-writer` (run once per bug)

**Purpose:** Post each bug to the group GitHub repository.

**Prompt:**

```
Use the github-issue-writer skill.

Feature: FR-{nn} — {Feature Name}

Read all pending bugs from:
- qa-artifacts/bug-reports/FR{nn}-bugs.md (process all entries where GitHub Issue is pending)
- qa-artifacts/execution-results/FR{nn}-execution-results.md

Group GitHub repo URL: {paste your group repo URL here}

Step 1: Scan the bug reports and print the list of pending bugs to process.
Step 2: STOP AND WAIT for my confirmation. DO NOT generate the guide file yet.
Step 3: Generate the complete guide file at: scripts/github-issues/FR{nn}-github-issues-guide.md

All placeholders must be filled. No {value} text may remain in any issue body.
```

**After agent responds:**

- Check every issue body — no `{placeholder}` remaining
- Check titles are ≤72 chars
- Check labels are correct per channel and severity
- If correct → APPROVE → write guide file to disk
- Follow the guide: post each issue on GitHub, note the issue numbers
- Provide sync-back: `Issue numbers for FR-{nn}: BUG-001=#X, BUG-002=#Y, ...`
- git commit: `bug(FR{nn}): github issues posted, sync-back complete`
- Run `ai-audit-logger`

### REPEAT Steps 1–10 for all 4 FRs

Once all 4 FRs are complete, proceed to the final 3 skills below.

### STEP 11 — Skill: `traceability-matrix-generator` (once, after all 4 FRs)

**Purpose:** Build the traceability matrix linking all FRs → ECs → TCs → Bugs.

**Prompt:**

```
Use the traceability-matrix-generator skill.

All 4 FRs have been completed. Build the full traceability matrix.

Read the following files:
- qa-artifacts/domain-analysis/FR01-domain-analysis.md
- qa-artifacts/domain-analysis/FR07-domain-analysis.md
- qa-artifacts/domain-analysis/FR17-domain-analysis.md
- qa-artifacts/domain-analysis/FR03-domain-analysis.md
- qa-artifacts/test-cases/FR01-test-cases.md
- qa-artifacts/test-cases/FR07-test-cases.md
- qa-artifacts/test-cases/FR17-test-cases.md
- qa-artifacts/test-cases/FR03-test-cases.md
- qa-artifacts/execution-results/FR01-execution-results.md
- qa-artifacts/execution-results/FR07-execution-results.md
- qa-artifacts/execution-results/FR17-execution-results.md
- qa-artifacts/execution-results/FR03-execution-results.md
- qa-artifacts/bug-reports/FR01-bugs.md
- qa-artifacts/bug-reports/FR07-bugs.md
- qa-artifacts/bug-reports/FR17-bugs.md
- qa-artifacts/bug-reports/FR03-bugs.md

Build all 3 matrices:
1. FR Business Rule → EC → TC → Status → Bug ID
2. Complete TC list with status
3. Bug register

Calculate coverage metrics and identify any coverage gaps.

Save to: qa-artifacts/traceability/traceability-matrix.md
```

**After agent responds:**

- Spot-check: pick 3 random TCs, verify they appear in Matrix 2 with correct status
- Verify: all bug IDs have GitHub issue numbers
- If correct → APPROVE → git commit: `docs: traceability matrix complete`
- Run `ai-audit-logger`

### STEP 12 — Skill: `test-summary-generator` (once, after all 4 FRs)

**Purpose:** Generate the Test Summary Report for hw02-submission/README.md.

**Prompt:**

```
Use the test-summary-generator skill.

All 4 FRs are complete. Generate the full test summary report.

Read from:
- qa-artifacts/execution-results/ (all 4 FR files)
- qa-artifacts/bug-reports/ (all 4 FR files)
- qa-artifacts/test-cases/ (all 4 FR files)
- qa-artifacts/traceability/traceability-matrix.md

Collect TC statistics per FR (EP count, BVA count, executed, passed, failed, blocked).
Collect bug statistics (by feature and by severity).
Calculate pass rate and TC execution coverage.

For the demo video links section, leave placeholders — I will fill those in after
recording the videos.

Write the complete file to: qa-artifacts/summary/README.md
```

**After agent responds:**

- Cross-check numbers manually against execution results files
- If correct → APPROVE → paste into `hw02-submission/README.md`
- git commit: `docs: test summary report in README`
- Run `ai-audit-logger`

### STEP 13 — Skill: `ai-audit-logger` (compile final report)

**Purpose:** Compile all per-FR logs and write the final ai-critique.md.

**Phase A — Compile audit report:**

```
Use the ai-audit-logger skill.

Compile all 4 per-FR audit logs into the final audit report.
Use your file system capabilities to read the 4 FR audit files, merge them sequentially, and save the output to: qa-artifacts/ai-audit/ai-audit-report.md

Then show me the ai-critique.md template so I can write my 200-300 word critique.
```

**Phase B — Write ai-critique:**

```
I have reviewed all AI interactions across the 4 FRs:
- FR01-ai-audit.md
- FR07-ai-audit.md
- FR17-ai-audit.md
- FR03-ai-audit.md

The most significant AI gaps I observed were:
- {list 4-5 specific gaps you found across all FRs}

Help me write a 200-300 word AI Critique for:
qa-artifacts/ai-audit/ai-critique.md

Answer these 3 questions from HW02 Section 10:
1. Where did the AI get something wrong, biased, or incomplete?
2. Why did it fail to catch the issue?
3. What principle did I learn about collaborating with AI?
```

**After agent responds:**

- Count words: must be 200-300
- Ensure all 3 questions are answered
- Ensure specific FR and EC references are included (not generic)
- If correct → APPROVE
- git commit: `docs: ai audit report and ai critique`
