# Instruction: Schema Validation Testing

**Technique:** Response Schema Validation  
**Source:** [OpenAPI 3.0.3 Specification](https://spec.openapis.org/oas/v3.0.3), [JSON Schema — draft-07](https://json-schema.org/draft-07)  
**Delegates to:** Standalone — not delegated to a sub-routine

## Purpose

Generate TCs that verify every response returned by the API matches its documented schema exactly — correct field names, correct types, correct presence/absence of fields, and correct HTTP headers.

## When to Apply

Always. Read Section 4 (Response Definitions) of the contract and the OpenAPI YAML `responses` object. Generate TCs for every distinct response shape.

## Validation Dimensions

For each response scenario defined in the contract, verify:

### 1. HTTP Status Code

- The actual status code matches the documented code exactly
- A success request does not return a 2xx other than the documented one (e.g., returns 200 but spec says 201)

### 2. Response Body — Field Presence

- All required fields are present in the response
- No undocumented fields appear in the response (unless `additionalProperties` is explicitly allowed)
- Optional fields are present when conditions are met, absent when not

### 3. Response Body — Field Types

- String fields are strings (not numbers, not booleans)
- Integer fields are integers (not strings like `"1"`)
- Boolean fields are `true`/`false` (not `"true"`/`"false"` strings)
- Array fields are arrays (not null, not a single object)
- Object fields are objects (not null, not a primitive)
- Nested object fields follow their own schema recursively

### 4. Response Body — Field Values

- Enum fields contain only allowed values
- ID fields are positive integers (if spec defines them as such)
- Date/datetime fields match the documented format (`YYYY-MM-DD`, ISO 8601, etc.)
- Non-nullable fields are never `null`

### 5. Response Headers

- `Content-Type: application/json` (or `application/json; charset=utf-8`) is present on all JSON responses
- `Content-Type: text/html` is not returned when JSON is expected
- For rate-limited responses: `Retry-After` header is present when documented

### 6. Error Response Schema Consistency

- All error responses follow the same error body shape (e.g., `{"error": "string"}`)
- Error messages are human-readable strings, not raw exception objects or stack traces
- Error bodies do not expose internal implementation details (table names, file paths, query strings)

## Data-Driven Eligibility

Schema validation TCs are **not data-driven**. Each TC targets a specific response scenario and asserts structural properties — not input variations.

## TC Generation Rules

- Generate one TC per distinct **response scenario** (not per field)
- For the success response: generate one TC asserting all required fields are present with correct types
- For each error response: generate one TC asserting the error body structure matches and no extra fields leak
- Add a TC for each documented header (e.g., one TC asserting `Content-Type` header on success, one for `Retry-After` on 429 if documented)
- If the success response contains a nested object, add one TC specifically asserting the nested object's field types

## Output Format

Produce rows for the TC-SCH table in `test-cases.md`:

| ID                      | Title                           | Scenario                               | Field / Header Checked                     | Expected Value / Type                       | Status | Actual Result |
| ----------------------- | ------------------------------- | -------------------------------------- | ------------------------------------------ | ------------------------------------------- | ------ | ------------- |
| TC-{feature_id}-SCH-001 | {Action + Function + Condition} | {e.g., "Successful creation response"} | {e.g., "id field" / "Content-Type header"} | {e.g., "integer, > 0" / "application/json"} | —      | —             |
| TC-{feature_id}-SCH-002 |                                 |                                        |                                            |                                             | —      | —             |
