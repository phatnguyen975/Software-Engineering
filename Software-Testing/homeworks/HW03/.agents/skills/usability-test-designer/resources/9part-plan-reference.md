# 9-Part Test Plan Reference — Usability Test Designer

## The 9 Parts and What Each Must Contain

### Part 1 — Purpose

- **What it is:** The overarching reason for running this test.
- **Must answer:** What decision will this test inform? Who commissioned it?
- **Common mistake:** Writing objectives here instead of purpose. Purpose is strategic; objectives (Part 3) are tactical.

### Part 2 — Problem Statement

- **What it is:** Specific research questions the test must answer.
- **Format:** Bullet list of 3–6 questions.
- **Must answer:** What do we not yet know about this product's usability that we need to find out from real users?
- **Example questions:**
  - Can a first-time admin create and publish an event without assistance?
  - Where do users get confused during the registration configuration step?
  - How long does it take a competent user to complete the event creation flow?

### Part 3 — Test Objectives & Tasks

- **What it is:** The specific tasks participants will perform, linked to the problem statement.
- **Must include:**
  - Objective (what we want to learn from this task)
  - Task ID and brief title
  - Reference to the full task scenario file
  - Screens the task is expected to traverse

### Part 4 — User Profile

- **What it is:** A precise description of who the participants are.
- **Must include:**
  - Primary characteristics (role, technical background, domain knowledge)
  - Prior SUT experience: none required / some acceptable / must have used before
  - Inclusion criteria: who qualifies
  - Exclusion criteria: who must be excluded (e.g. team members, current SUT users, people with prior knowledge of the test tasks)
  - Recruitment method (how participants will be found)

### Part 5 — Method & Test Design

- **What it is:** How the test will be conducted.
- **Must include:**
  - Test type: exploratory / assessment / validation / comparison
  - Session format: moderated in-person / moderated remote / unmoderated remote
  - For comparison (A/B): design type (within-subjects or between-subjects) and counterbalancing plan
  - Number of participants (and why)
  - Estimated session duration

### Part 6 — Environment & Equipment

- **What it is:** The physical/digital setup for each session.
- **Must include:**
  - SUT URL and access method
  - Device type and OS
  - Browser
  - Screen and audio recording tool
  - SUS delivery method (print / Google Form / in-tool)
  - Any special setup required (e.g. test account to use, data to pre-populate)

### Part 7 — Test-Monitor Role

- **What it is:** What the facilitator will and will not do during sessions.
- **Must include:**
  - Facilitator responsibilities before the session (setup, briefing)
  - During the session: neutrality rules, permitted prompts, intervention threshold
  - Standard prompt for think-aloud reminder: "What are you thinking right now?"
  - Intervention rule: step in only if participant has been fully stuck > 5 minutes; record as partial completion
  - Post-session: SUS administration, probe questions

### Part 8 — Evaluation Measures & Data to Collect

- **What it is:** The complete list of what will be measured and how.
- **Performance measures (objective):**
  - Task success: Completed / Partial / Failed (define each level per task)
  - Time on task: seconds from task start to end or abandonment
  - Error count: definition of what counts as an error for this test
  - Hesitation count: definition (e.g. pause > 5s without action or verbalisation)
- **Preference measures (subjective):**
  - SUS: 10-item scale, administered after all tasks
  - Probe questions: 3–5 open questions per task, administered after each task

### Part 9 — Report Contents

- **What it is:** A preview of what the final usability report will contain.
- **Standard contents:**
  - Executive summary
  - Methodology overview
  - Participant table (masked)
  - Task scenario(s) used
  - Performance metrics summary table
  - SUS scores and interpretation
  - Findings list ranked by severity (Nielsen 0–4)
  - Prioritised recommendations
  - Appendix: raw session notes

## Test Type Reference

| Test Type       | Stage                               | Characteristic                                                                       | Participant interaction                               |
| --------------- | ----------------------------------- | ------------------------------------------------------------------------------------ | ----------------------------------------------------- |
| **Exploratory** | Early (sketches, wireframes)        | Informal; tests basic concept; tasks are shallow                                     | Heavy — facilitator probes continuously               |
| **Assessment**  | Mid (concepts fixed, product built) | Well-defined tasks; quantitative metrics collected                                   | Moderate — facilitator observes, minimal intervention |
| **Validation**  | Pre-release                         | Full product including help & documentation; tested against predetermined benchmarks | Minimal — "disaster insurance" test                   |
| **Comparison**  | Any stage                           | Two variants tested; objective comparison of measures                                | Depends on format; counterbalancing required          |

## Within-Subjects vs Between-Subjects (for Comparison tests)

| Design               | Each participant tests | Advantage                                    | Risk                                                               |
| -------------------- | ---------------------- | -------------------------------------------- | ------------------------------------------------------------------ |
| **Within-subjects**  | Both variants          | Fewer participants needed; direct comparison | Order/learning effects — must counterbalance                       |
| **Between-subjects** | One variant only       | No order effects                             | Needs more participants; participant variability confounds results |

**Counterbalancing for within-subjects:**

- If `num_participants` is even, assign half to A-then-B, half to B-then-A.
- If `num_participants` is odd, assign the extra participant to the variant expected to perform worse.
