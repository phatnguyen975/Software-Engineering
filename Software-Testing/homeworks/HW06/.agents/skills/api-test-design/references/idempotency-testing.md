# Instruction: Idempotency Testing

**Technique:** Idempotency Verification  
**Source:** [RFC 9110 §9.2.2 — Idempotent Methods](https://www.rfc-editor.org/rfc/rfc9110#section-9.2.2), [REST API Design Best Practices — Microsoft Azure](https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design#define-api-operations-in-terms-of-http-methods)  
**Delegates to:** Standalone — not delegated to a sub-routine

## Purpose

Generate TCs that verify the API's idempotency behavior matches what is documented in the contract — ensuring that repeated identical requests produce the expected outcome (either the same result or a documented error).

## When to Apply

**Conditional.** Only generate TC-IDP entries if the contract explicitly states an idempotency rule in Section 1 (Overview → Idempotency) or Section 3 (Business Rules). If the contract does not mention idempotency, mark this section `N/A — idempotency not specified in contract`.

Do not infer idempotency from the HTTP method alone.

## Idempotency Patterns by Method

| HTTP Method              | Expected idempotency      | What to verify                                                                                          |
| ------------------------ | ------------------------- | ------------------------------------------------------------------------------------------------------- |
| `GET`                    | Idempotent by definition  | Two identical GET requests return same data (assuming no state change between calls)                    |
| `PUT` (full replace)     | Idempotent                | Second call with same body returns same result, no duplicate records created                            |
| `PATCH` (partial update) | Usually idempotent        | Second call produces same final state                                                                   |
| `DELETE`                 | Idempotent                | Second call returns `404` or `204` — never a server error                                               |
| `POST` (create)          | NOT idempotent by default | Second identical call creates duplicate OR returns `409 Conflict` — verify which behavior is documented |

## TC Generation Rules

For each idempotency rule in the contract:

1. **First-call TC:** Send the request once, verify the documented success response
2. **Repeat-call TC:** Send the identical request a second time immediately after, verify:
   - If documented as idempotent: same response, no duplicate resource created
   - If documented as non-idempotent: `409 Conflict` or documented error — never a `500`
3. **Concurrent-call TC** (if contract specifies): Send two identical requests simultaneously (or in rapid succession), verify no race condition produces unexpected state

## Data-Driven Eligibility

Idempotency TCs are **not data-driven**. Each TC requires a specific sequence of calls (first call then repeat call), which cannot be parameterized into simple row-based data.

## Output Format

Produce rows for the TC-IDP table in `test-cases.md`:

| ID                      | Title                           | Description              | First Call Result    | Repeat Call Result                            | Status | Actual Result |
| ----------------------- | ------------------------------- | ------------------------ | -------------------- | --------------------------------------------- | ------ | ------------- |
| TC-{feature_id}-IDP-001 | {Action + Function + Condition} | {What is being verified} | {HTTP status + body} | {HTTP status + body on second identical call} | —      | —             |
