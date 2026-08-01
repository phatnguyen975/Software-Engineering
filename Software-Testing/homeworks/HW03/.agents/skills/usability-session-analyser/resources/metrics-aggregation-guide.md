# Metrics Aggregation Guide — Usability Session Analyser

Rules for aggregating performance metrics across participants and handling edge cases.

## Metrics Definitions (as used in analysis)

| Metric           | Definition                                                                               | Unit     |
| ---------------- | ---------------------------------------------------------------------------------------- | -------- |
| Task Success     | Outcome at task end: Completed / Partial / Failed                                        | Category |
| Time on Task     | Elapsed seconds from task start to task end or abandonment                               | Seconds  |
| Error Count      | Number of incorrect navigations, wrong submissions, or input mistakes requiring recovery | Integer  |
| Hesitation Count | Number of pauses > 5 seconds with no action and no verbalisation                         | Integer  |

## Aggregation Rules

### Task Success Rate

- Count participants per outcome level: Completed, Partial, Failed
- Express as percentage: (count ÷ total participants) × 100
- Report all three rates (they must sum to 100%)

```
Example (N=5):
  Completed: 3 → 60%
  Partial:   1 → 20%
  Failed:    1 → 20%
```

### Mean Time on Task

**Include:** All participants who produced a time value (completed or partial)  
**Exclude:** Participants who abandoned the task entirely (no meaningful time endpoint)

When excluding abandoned tasks from the mean:

- Note in the output: "Mean excludes P{n} (task abandoned at {time}s)"
- Report the abandoned participant's time separately as "time to abandonment"

```
Example (N=5, one abandoned):
  P1: 312s, P2: 487s, P3: 256s, P4: abandoned at 600s (max time), P5: 388s
  Mean time on task (N=4): (312+487+256+388) ÷ 4 = 360.75s (6:01)
  Note: P4 excluded from mean; abandoned at 600s (max allowed time)
```

### Mean Error Count and Mean Hesitation Count

- Include all participants, including those who failed or abandoned
- A failed attempt with many errors is meaningful data
- Round means to one decimal place

## Edge Case Handling

| Situation                                                 | Handling                                                                                             |
| --------------------------------------------------------- | ---------------------------------------------------------------------------------------------------- |
| Participant abandoned task — no end time recorded         | Use the maximum allowed session time as time-to-abandonment; exclude from time mean; note explicitly |
| Time on task not recorded for one participant             | Exclude from time mean; note "P{n} time not recorded"                                                |
| Error count not recorded                                  | Note "P{n} error count not recorded"; exclude from error mean                                        |
| Hesitation count not recorded                             | Note "P{n} hesitation count not recorded"; exclude from hesitation mean                              |
| Participant completed task with facilitator hint          | Include in Completed count; add note "with facilitator hint"                                         |
| Extremely high outlier time (e.g. 3× the next highest)    | Include in mean; note the outlier in interpretation; investigate session notes for cause             |
| Zero errors for all participants                          | Report mean = 0; note "no errors observed — may indicate low task difficulty or limited exploration" |
| Multiple tasks: participant completed T1 but abandoned T2 | Report metrics separately per task; do not average across tasks                                      |

## Comparison (A/B) Test Aggregation

For each metric, report:

- Variant A: N participants, metric values
- Variant B: N participants, metric values
- Difference: Variant A mean vs Variant B mean
- Note: with small N (<10 per variant), differences are directional indicators, not statistically significant

## Benchmark Comparison

If `benchmark_time` was specified in the task scenario, include a benchmark comparison row:

```markdown
| Metric            | Task T1                        |
| ----------------- | ------------------------------ |
| Mean Time on Task | 487s (8:07)                    |
| Benchmark Time    | 480s (8:00)                    |
| vs Benchmark      | +7s (+1.5%) — within benchmark |
```

If mean time exceeds 2× benchmark, flag: "Mean completion time significantly exceeds benchmark — indicates substantial usability friction in this task."

## Reporting Format for metrics-summary.md

Always include:

1. **Individual data table** — one row per participant, all metrics visible
2. **Aggregate statistics table** — one column per task
3. **Notes block** — any exclusions, missing data, or outlier explanations
4. **Performance interpretation** — 2–3 sentences contextualising the numbers

Do not report only aggregate statistics without the individual data table — individual variation is as informative as the mean in small-N usability studies.
