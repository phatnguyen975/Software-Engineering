# SUS Scoring Reference — Usability Session Analyser

## Scoring Formula

For each participant, given raw responses Q1–Q10 (each 1–5):

| Step                   | Odd questions (1,3,5,7,9) | Even questions (2,4,6,8,10) |
| ---------------------- | ------------------------- | --------------------------- |
| Adjusted score         | raw − 1                   | 5 − raw                     |
| Range after adjustment | 0–4                       | 0–4                         |

**SUS score = (sum of all 10 adjusted scores) × 2.5**

**Range:** 0 (worst possible) to 100 (best possible).

## Worked Example

Raw responses: Q1=4, Q2=2, Q3=4, Q4=1, Q5=4, Q6=2, Q7=4, Q8=1, Q9=4, Q10=1

| Q       | Raw | Type | Adjusted |
| ------- | --- | ---- | -------- |
| 1       | 4   | Odd  | 4−1 = 3  |
| 2       | 2   | Even | 5−2 = 3  |
| 3       | 4   | Odd  | 4−1 = 3  |
| 4       | 1   | Even | 5−1 = 4  |
| 5       | 4   | Odd  | 4−1 = 3  |
| 6       | 2   | Even | 5−2 = 3  |
| 7       | 4   | Odd  | 4−1 = 3  |
| 8       | 1   | Even | 5−1 = 4  |
| 9       | 4   | Odd  | 4−1 = 3  |
| 10      | 1   | Even | 5−1 = 4  |
| **Sum** |     |      | **33**   |

**SUS score = 33 × 2.5 = 82.5**

## Interpretation Table (Sauro & Lewis 2012)

| SUS Score | Grade | Adjective Rating      | Percentile (approx.) |
| --------- | ----- | --------------------- | -------------------- |
| ≥ 90      | A     | Excellent             | Top 10%              |
| 85–89.9   | A-    | Excellent             | Top 15%              |
| 80–84.9   | B+    | Good                  | Top 25%              |
| 75–79.9   | B     | Good                  | Top 35%              |
| 70–74.9   | C+    | OK                    | Top 45%              |
| 68        | C     | OK — Industry average | 50th percentile      |
| 65–67.9   | C-    | OK                    | ~40th percentile     |
| 60–64.9   | D+    | Poor                  | ~30th percentile     |
| 51–59.9   | D     | Poor                  | ~20th percentile     |
| ≤ 50      | F     | Awful                 | Bottom 15%           |

Industry average: **68** (Sauro, MeasuringU benchmark database, n > 10,000 studies).

## Group Statistics to Report

| Statistic        | How to compute                                                                |
| ---------------- | ----------------------------------------------------------------------------- |
| Mean SUS         | Sum of all individual SUS scores ÷ number of participants with valid SUS data |
| Minimum          | Lowest individual score; note participant ID                                  |
| Maximum          | Highest individual score; note participant ID                                 |
| Adjective rating | Map mean score to interpretation table above                                  |

## Edge Cases

**Missing SUS data for one participant:**

- Exclude that participant from SUS mean calculation
- Note: "P{n} SUS data not recorded; excluded from SUS analysis. N={N−1} for SUS mean."
- Do not interpolate or estimate the missing score

**Participant answered outside 1–5 range:**

- Flag as data quality issue; note in the output
- If clearly a typo (e.g. "55" likely means "5"), correct with a note
- Otherwise exclude that participant's SUS from the mean and note the reason

**Comparison (A/B) test:**

- Compute a separate SUS mean for each variant
- Report both means with their adjective ratings side by side
- Do not average the two variant means together

**Wide variance (max − min > 30 points):**

- Note in the interpretation section: "Wide score variance suggests participants had substantially different experiences — investigate individual session notes to understand outlier cases."
