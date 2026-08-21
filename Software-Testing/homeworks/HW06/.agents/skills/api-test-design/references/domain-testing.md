# Instruction: Domain / Functional Testing

**Technique:** Equivalence Partitioning + Boundary Value Analysis  
**Source:** [ISTQB Foundation Level Syllabus v4.0](https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf)  
**Delegates to:** `functional-test-design` → `domain-testing` sub-skill (invoke silently)

## Purpose

Generate TCs that verify the API handles all input value ranges correctly — both valid and invalid — for every request field defined in the contract.

## When to Apply

Always. Every API endpoint with at least one input field requires domain testing.

## Silent Invocation Instruction

Invoke `functional-test-design/domain-testing` with the following instruction:

> Analyze each field in the request schema from the provided contract. For each field, identify equivalence partitions (valid and invalid classes) and boundary values, then generate final TCs. Do not print the analysis — output only the final TC rows in the format specified below.

Do not print sub-routine reasoning, partition lists, or intermediate steps. Collect TC rows only.

## Analysis Approach

For each request field, identify:

### Valid Partitions

- Typical valid value (nominal case)
- Minimum allowed value / length (at-boundary)
- Maximum allowed value / length (at-boundary)

### Invalid Partitions

- Value just below minimum (below-boundary)
- Value just above maximum (above-boundary)
- Wrong type (string where integer expected, etc.)
- Null / empty string / missing field (if field is required)
- Values violating format constraints (invalid email format, invalid date format, etc.)
- Values violating allowed-value constraints (value not in enum list)

### Special Cases

- Leading/trailing whitespace in string fields
- Unicode characters, special characters, very long strings
- Negative numbers for fields expecting positive values
- Zero for fields with minimum > 0
- Fields with interdependencies (field A valid only when field B has value X)

## Data-Driven Eligibility

Domain testing TCs are the **primary candidates for data-driven execution**. Mark all partition/boundary TCs as `Data-driven: Yes` — they will be parameterized into a CSV data file where each row is one partition.

**Exception:** TCs testing field interactions or complex preconditions may not be suitable for data-driven execution — mark those `Data-driven: No` with a note.

## Output Format

Produce rows for the TC-FR table in `test-cases.md`:

| ID                     | Title                           | Precondition                       | Input                        | Expected Result                       | Data-driven? | Status | Actual Result |
| ---------------------- | ------------------------------- | ---------------------------------- | ---------------------------- | ------------------------------------- | ------------ | ------ | ------------- |
| TC-{feature_id}-FR-001 | {Action + Function + Condition} | {System state required, or "None"} | {field: value, field: value} | {HTTP status — response body summary} | Yes / No     | —      | —             |
| TC-{feature_id}-FR-002 |                                 |                                    |                              |                                       |              | —      | —             |
