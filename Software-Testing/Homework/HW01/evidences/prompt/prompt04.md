<!-- message time: 2026-05-28 02:57:17 -->

# ROLE & CONTEXT

Act as a strict QA/QC Engineer. I am testing a physical home appliance for my software testing assignment. 
The Device Under Test (DUT) is a Box Fan (Quạt hộp).

- **Brand:** Lifan
- **Model:** HV-148
- **Year:** 2018
- **Serial Number:** `HO-24****-148`

The typical features of this box fan include:

1. Power plug (220V).
2. Speed control buttons (0/Off, 1/Low, 2/Medium, 3/High).
3. A rotating front grill/louver (Tản gió / Swing) controlled by a dedicated button or switch.
4. A mechanical tip-over safety switch (Rơ-le an toàn tự ngắt) located at the bottom base.

# TASK

Generate exactly **12 standard functional test cases** for this specific physical box fan. Focus on standard usability, basic functionality, and normal operation (Happy paths and common negative paths). 

**DO NOT** generate extreme edge cases, destructive testing, or highly unusual scenarios (I will manually create 3 edge cases myself later).

# REQUIREMENTS

Format the 12 test cases into a structured table with the exact following columns:

1. **Test Case ID:** (e.g., TC-01, TC-02)
2. **Objective:** What are we testing?
3. **Input / Pre-condition:** Initial state of the fan.
4. **Steps:** Step-by-step physical actions to perform.
5. **Expected Result:** The expected physical response from the fan.
6. **Actual Result:** (Leave this blank or put "[To be executed on physical device]" so I can fill it in later).
7. **Verdict:** (Leave this blank or put "[Pending]").

# OUTPUT FORMAT

You MUST output the table strictly inside a single raw markdown code block using 3 backticks (```markdown). Do not output any conversational text outside of the block. Ensure the table is clean so I can easily copy-paste it into Microsoft Excel.
