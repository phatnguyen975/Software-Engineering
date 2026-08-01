# SUS Reference — Usability Test Designer

**IMPORTANT: Do not modify the wording of the 10 SUS statements.** The scale has been validated with this exact wording. Paraphrasing invalidates the benchmark.

## The 10 SUS Statements (exact, unmodified)

| #   | Statement                                                                                  |
| --- | ------------------------------------------------------------------------------------------ |
| 1   | I think that I would like to use this system frequently.                                   |
| 2   | I found the system unnecessarily complex.                                                  |
| 3   | I thought the system was easy to use.                                                      |
| 4   | I think that I would need the support of a technical person to be able to use this system. |
| 5   | I found the various functions in this system were well integrated.                         |
| 6   | I thought there was too much inconsistency in this system.                                 |
| 7   | I would imagine that most people would learn to use this system very quickly.              |
| 8   | I found the system very cumbersome to use.                                                 |
| 9   | I felt very confident using the system.                                                    |
| 10  | I needed to learn a lot of things before I could get going with this system.               |

**Response scale:** 1 (Strongly Disagree) → 5 (Strongly Agree)

## Scoring Formula

For each participant:

1. **Odd-numbered questions (1, 3, 5, 7, 9):** adjusted score = response − 1
2. **Even-numbered questions (2, 4, 6, 8, 10):** adjusted score = 5 − response
3. **SUS score = sum of 10 adjusted scores × 2.5**

**Range:** 0 (worst) to 100 (best).

**Example calculation:**

- Q1=4, Q2=2, Q3=4, Q4=1, Q5=4, Q6=2, Q7=4, Q8=1, Q9=4, Q10=1
- Odd adjusted: (4-1)+(4-1)+(4-1)+(4-1)+(4-1) = 3+3+3+3+3 = 15
- Even adjusted: (5-2)+(5-1)+(5-2)+(5-1)+(5-1) = 3+4+3+4+4 = 18
- Sum = 33, SUS score = 33 × 2.5 = **82.5**

## Interpretation Table (Sauro & Lewis 2012)

| SUS Score Range | Grade | Adjective Rating      | Percentile (approx.) |
| --------------- | ----- | --------------------- | -------------------- |
| 90–100          | A+    | Excellent             | Top 10%              |
| 85–89           | A     | Excellent             | Top 15%              |
| 80–84           | B+    | Good                  | Top 25%              |
| 75–79           | B     | Good                  | Top 35%              |
| 70–74           | C+    | OK                    | Top 45%              |
| 68              | C     | OK (industry average) | 50th percentile      |
| 65–67           | C-    | OK                    | 40th percentile      |
| 60–64           | D     | Poor                  | ~30th percentile     |
| 51–59           | D-    | Poor                  | ~20th percentile     |
| ≤ 50            | F     | Awful                 | Bottom 15%           |

Industry average: **68** (Sauro, MeasuringU benchmark database, n > 10,000 studies). A score of 80.3 represents the top quartile.

## Administration Rules

- Administer SUS **after all tasks are complete**, not between tasks and not before
- Do not explain what the statements mean — instruct participants to answer based on first impression
- Allow approximately 2–3 minutes for completion
- SUS measures overall system perception formed through the task experience — it is not a task-specific instrument
- For comparison tests, administer a separate SUS after each variant

## Reporting SUS Results

When reporting SUS scores across participants:

- Report **mean SUS score** for the group
- Report **individual scores** in the participant table
- Map mean score to the adjective rating from the interpretation table
- If comparing two variants: report mean scores for each variant and state which is higher
- With `{N}` participants, do not report statistical significance — report the pattern and recommend follow-up with larger sample if decision is close
