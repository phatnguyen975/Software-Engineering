# IA#1 — Feature specification · rubric

**CSC13114 · 23KTPM1 · individual assignment · 3% of the course · 100 points**

Feature to specify: **split the bill at a table**, in `table-order` — the system described in the brief attached to this assignment. You have not built it; the brief is everything you know about it. Hand in `spec.md` — one page, on your own.

## What is scored

| #   | Criterion                | Points  |
| :-- | :----------------------- | :------ |
| 1   | Contract                 | 35      |
| 2   | Acceptance criteria      | 35      |
| 3   | Edge cases and non-goals | 30      |
|     | **Total**                | **100** |

The 100 points are worth 3% of the course: a mark of 80 adds 2.4 to your final grade out of 100.

### 1. Contract — 35 points

| Points    | What it looks like                                                                                                                                                                                                                                                                                     |
| :-------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **29–35** | Endpoints, request and response payloads, and status codes are all concrete. Error responses use a named shape. Changes to the existing API and data (new endpoints, new tables or columns, changed behaviour of `/pay` or the webhook) are stated. Someone could write the client without asking you. |
| **18–28** | The happy path is precise; error responses are vague or partly missing, or the spec does not say what changes in the existing system.                                                                                                                                                                  |
| **7–17**  | Prose only — "the API returns the shares" — no payloads, no codes.                                                                                                                                                                                                                                     |
| **0–6**   | No contract section.                                                                                                                                                                                                                                                                                   |

### 2. Acceptance criteria — 35 points

| Points    | What it looks like                                                                                                                                                            |
| :-------- | :---------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **29–35** | Every criterion names concrete values and can be turned into a test with no further decisions. Covers at least: the split arithmetic, one failure path, one concurrency case. |
| **18–28** | Most criteria are runnable; one or two are wishes ("works correctly").                                                                                                        |
| **7–17**  | Criteria are restatements of the goal.                                                                                                                                        |
| **0–6**   | None, or none that can fail.                                                                                                                                                  |

### 3. Edge cases and non-goals — 30 points

| Points    | What it looks like                                                                                                                                                     |
| :-------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **25–30** | Addresses at least four of the five thin places (empty state, partial failure, permissions, concurrency and duplicates, limits) and has an explicit non-goals section. |
| **16–24** | Two or three of them, or non-goals are implied rather than written.                                                                                                    |
| **6–15**  | Happy path only, with one edge case mentioned in passing.                                                                                                              |
| **0–5**   | Happy path only.                                                                                                                                                       |

## Before you submit — read it as the implementer (not scored)

Nobody will implement your spec this time, so do it to yourself. Open a fresh session with an assistant, give it only the system brief and your spec, and ask it to list every question it would need answered before writing code — and not to guess. Each question that changes behaviour is a hole; fix it in the spec.

If you do this, note it in `AI-LOG.md`. It costs nothing and it is the cheapest review you will ever get.

## How to submit

One zip file, named after your student ID and the total you give yourself:

- **Individual work:** `<StudentID>_<total>.zip`, for example `23120001_82.zip`
- **Group work:** `<StudentID1>-<StudentID2>-<StudentID3>_<total>.zip`, IDs in ascending order, for example `23120001-23120042-23120117_75.zip`

The zip must contain `SELF_ASSESSMENT_REPORT.md`: one row per criterion in this rubric, the marks you claim, and evidence pointing at a file, a section, a commit or a test name plus a short _what I did not manage_. The total in that table is the number in the file name. Template on Classroom.

_A wrong file name costs no marks but delays your result — the grading script matches submissions to the class list by that name._

## Honesty adjustment

Your self-assessment is compared with the mark you actually earn. The gap is your total minus the mark, on the same 100-point scale.

| Gap                            | Adjustment                                          |
| :----------------------------- | :-------------------------------------------------- |
| within ±10                     | none — this is normal calibration                   |
| +11 to +20                     | -3                                                  |
| +21 to +30                     | -6                                                  |
| more than +30                  | -10                                                 |
| -21 or worse                   | -3 — read the rubric before you score yourself down |
| no `SELF_ASSESSMENT_REPORT.md` | -10, and the file-name total is ignored             |

- A criterion you claim with no evidence line counts as claimed-and-not-done for this comparison.
- The adjustment never takes a submission below 0.
- Scoring yourself honestly low costs you nothing inside ±10. Claiming marks you did not earn costs more than the marks would have been worth.

## Notes

- A long spec is not a better spec. One well-aimed page scores higher than four vague ones.
- You may use an assistant to draft or review the spec. Say so in `AI-LOG.md`; it costs no marks. What costs marks is a spec you cannot defend.
- Write the spec as if it will be implemented by someone who cannot ask you anything. That is how it is read when it is marked.
