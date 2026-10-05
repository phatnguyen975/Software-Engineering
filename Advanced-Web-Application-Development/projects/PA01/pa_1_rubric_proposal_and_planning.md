# PA#1 — Proposal and planning · rubric

**CSC13114 · project milestone, teams of up to 3 · 4% of the course · 100 points**

Hand in two pages plus your repository link. No specification in this milestone — the spec is IA#1 and, for your core feature, a later project milestone.

## What is scored

|     #     | Criterion                                       | Points  |
| :-------: | :---------------------------------------------- | :-----: |
|     1     | Problem and users                               |   20    |
|     2     | The LLM feature, and the cost of it being wrong |   25    |
|     3     | Scope: in and out                               |   15    |
|     4     | Plan and ownership                              |   20    |
|     5     | Risks                                           |   10    |
|     6     | Technology choices                              |   10    |
| **Total** |                                                 | **100** |

### 1. Problem and users — 20 points

| Points    | What it looks like                                                                                               |
| :-------- | :--------------------------------------------------------------------------------------------------------------- |
| **17–20** | A named user in a named situation, and one problem stated in a sentence. You can say what they do today instead. |
| **11–16** | The user is a category ("restaurant staff"); the problem is real but broad.                                      |
| **4–10**  | A product description with no user in it.                                                                        |
| **0–3**   | Missing.                                                                                                         |

### 2. The LLM feature and the cost of being wrong — 25 points

| Points    | What it looks like                                                                                                                         |
| :-------- | :----------------------------------------------------------------------------------------------------------------------------------------- |
| **21–25** | One feature, described concretely, where a wrong answer has a cost you can name — who is affected, how much, and whether it can be undone. |
| **13–20** | A real feature, but the consequence of a wrong answer is stated in general terms.                                                          |
| **5–12**  | A chatbot bolted onto the corner of the product.                                                                                           |
| **0–4**   | No LLM feature, or one that cannot be wrong in any interesting way.                                                                        |

> _A feature where being wrong costs nothing will also score badly in PA#4 later, when you have to evaluate it. Choose one where you can answer: who is harmed, and how would we know?_

### 3. Scope — 15 points

| Points    | What it looks like                                                                                                          |
| :-------- | :-------------------------------------------------------------------------------------------------------------------------- |
| **13–15** | What is in for this semester, and an explicit list of what you are leaving out. The two lists are consistent with the plan. |
| **8–12**  | In-scope is clear; out-of-scope is implied.                                                                                 |
| **3–7**   | A feature wish list.                                                                                                        |
| **0–2**   | Missing.                                                                                                                    |

### 4. Plan and ownership — 20 points

| Points    | What it looks like                                                                                                             |
| :-------- | :----------------------------------------------------------------------------------------------------------------------------- |
| **17–20** | Each of the six checkpoints has work assigned, with a named owner and a date. The plan is compatible with the course calendar. |
| **11–16** | A plan with dates, but ownership is "the team".                                                                                |
| **4–10**  | A list of phases with no dates.                                                                                                |
| **0–3**   | Missing.                                                                                                                       |

### 5. Risks — 10 points

| Points   | What it looks like                                                                                 |
| :------- | :------------------------------------------------------------------------------------------------- |
| **9–10** | Two risks that would actually sink this project, each with a mitigation you could start this week. |
| **5–8**  | Generic risks ("time pressure") with generic mitigations.                                          |
| **1–4**  | One line.                                                                                          |
| **0**    | Missing.                                                                                           |

### 6. Technology choices — 10 points

| Points   | What it looks like                                                                                              |
| :------- | :-------------------------------------------------------------------------------------------------------------- |
| **9–10** | Each choice has one line of justification tied to this project, including the model provider and what it costs. |
| **5–8**  | Choices listed, justification generic.                                                                          |
| **1–4**  | A stack list.                                                                                                   |
| **0**    | Missing.                                                                                                        |

## How to submit

One zip file, named after your student ID and the total you give yourself:

- **Individual work:** `<StudentID>_<total>.zip` (for example: `23120001_82.zip`)
- **Group work:** `<StudentID1>-<StudentID2>-<StudentID3>_<total>.zip`, IDs in ascending order (for example: `23120001-23120042-23120117_75.zip`)

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

- Two pages is a limit, not a target.
- Ambition is not scored. A small product with a sharp LLM feature scores higher than a large product with a vague one.
- You may change the plan later. You will be asked what changed and why — that is a normal engineering answer, not a failure.
