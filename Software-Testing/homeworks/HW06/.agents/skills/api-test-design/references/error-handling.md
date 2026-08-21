# Instruction: Error Handling Testing

**Technique:** Error Guessing  
**Source:** [ISTQB Foundation Level Syllabus v4.0](https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf)  
**Delegates to:** `functional-test-design` → `error-guessing` sub-skill (invoke silently)

## Purpose

Generate TCs that verify the API handles malformed, unexpected, and boundary-breaking requests gracefully — returning correct error codes and informative but non-leaking error messages.

## When to Apply

Always. Every API endpoint must handle bad inputs without crashing, exposing internals, or returning misleading responses.

## Silent Invocation Instruction

Invoke `functional-test-design/error-guessing` with the following instruction:

> Based on the provided contract's request schema and response definitions, generate TCs for common error conditions: missing required fields, wrong field types, malformed request body (invalid JSON), empty request body, extra unexpected fields, out-of-range values, and any other conditions likely to cause errors based on the contract's business rules. Do not print the analysis — output only the final TC rows in the format specified below.

Do not print sub-routine reasoning or intermediate steps. Collect TC rows only.

## Error Condition Categories

### Malformed Request Structure

- Empty request body `{}`
- Non-JSON body (plain text, XML, form-data when JSON is expected)
- Invalid JSON syntax (missing closing brace, unquoted key)
- Wrong `Content-Type` header (e.g., `text/plain` instead of `application/json`)

### Missing Required Fields

- Omit each required field individually (one TC per field)
- Omit all required fields simultaneously
- Send `null` for a required field

### Wrong Field Types

- String field receives an integer (`{"email": 12345}`)
- Integer field receives a string (`{"quantity": "two"}`)
- Boolean field receives a string (`{"active": "true"}`)
- Array field receives an object or scalar

### Out-of-Range Values

- Numeric field below minimum (if not already covered in domain testing)
- Numeric field above maximum
- String field exceeding maximum length with exactly max+1 characters

### Business Rule Violations

- For each BR-xx in the contract, identify the corresponding error input and verify the correct error response is returned
- Example: BR-01 states email must be unique → verify correct error on duplicate (this may overlap with domain testing — keep only in the category where intent is clearest)

### Deduplication Note

Error handling TCs that duplicate a TC-FR or TC-ST entry must be removed here. Keep the TC in whichever category best describes its intent. If a domain partition TC already covers "missing required field → 400", do not duplicate it in TC-ERR.

## Data-Driven Eligibility

- Missing field TCs: Partially data-driven — each missing field is a row in a data file
- Wrong type TCs: Partially data-driven
- Malformed structure TCs: Not data-driven — each requires a distinct request body setup

## Output Format

Produce rows for the TC-ERR table in `test-cases.md`:

| ID                      | Title                           | Scenario                     | Expected Status | Expected Error                        | Status | Actual Result |
| ----------------------- | ------------------------------- | ---------------------------- | --------------- | ------------------------------------- | ------ | ------------- |
| TC-{feature_id}-ERR-001 | {Action + Function + Condition} | {e.g., "Empty request body"} | {HTTP status}   | {Expected error message or structure} | —      | —             |
| TC-{feature_id}-ERR-002 |                                 |                              |                 |                                       | —      | —             |
