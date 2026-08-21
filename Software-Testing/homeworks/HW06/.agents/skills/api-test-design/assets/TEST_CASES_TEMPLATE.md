# Test Cases: {METHOD} {PATH}

**Feature:** {feature_id} | **Endpoint:** `{METHOD} {PATH}`  
**Total:** {N} TCs | **Data-driven eligible:** {M} TCs  
**Contract:** `{contract_file}` | **OpenAPI:** `{openapi_file}`  
**Generated:** {YYYY-MM-DD}

## TC-FR — Functional / Domain Testing

> Tests input equivalence partitions and boundary values for every request field.

| ID                     | Title                           | Precondition                       | Input                        | Expected Result                       | Data-driven? | Status | Actual Result |
| ---------------------- | ------------------------------- | ---------------------------------- | ---------------------------- | ------------------------------------- | ------------ | ------ | ------------- |
| TC-{feature_id}-FR-001 | {Action + Function + Condition} | {System state required, or "None"} | {field: value, field: value} | {HTTP status — response body summary} | Yes / No     | —      | —             |
| TC-{feature_id}-FR-002 |                                 |                                    |                              |                                       |              | —      | —             |

## TC-ST — State Transition Testing

> Tests HTTP response states, system data state changes, and field-level state preconditions.

| ID                     | Title                           | Initial State                         | Input          | Expected Response            | Expected System State                 | Data-driven? | Status | Actual Result |
| ---------------------- | ------------------------------- | ------------------------------------- | -------------- | ---------------------------- | ------------------------------------- | ------------ | ------ | ------------- |
| TC-{feature_id}-ST-001 | {Action + Function + Condition} | {Entity state before call, or "None"} | {field: value} | {HTTP status — body summary} | {DB state after call, or "No change"} | Yes / No     | —      | —             |
| TC-{feature_id}-ST-002 |                                 |                                       |                |                              |                                       |              | —      | —             |

## TC-SEC — Security Testing

> Tests resistance to OWASP API Security Top 10 attack vectors as specified in the contract's security rules.

| ID                      | Title                           | Attack Type                                                                          | Payload / Vector                      | Expected Result                   | Security Rule                    | Status | Actual Result |
| ----------------------- | ------------------------------- | ------------------------------------------------------------------------------------ | ------------------------------------- | --------------------------------- | -------------------------------- | ------ | ------------- |
| TC-{feature_id}-SEC-001 | {Action + Function + Condition} | {SQL Injection / Auth Bypass / Mass Assignment / IDOR / XSS / Role Escalation / ...} | {Exact payload or attack description} | {HTTP status — expected behavior} | {SEC-01 / OWASP API1:2023 / ...} | —      | —             |
| TC-{feature_id}-SEC-002 |                                 |                                                                                      |                                       |                                   |                                  | —      | —             |

## TC-SCH — Schema Validation

> Tests that every response matches its documented schema: field presence, types, and headers.

| ID                      | Title                           | Scenario                               | Field / Header Checked                     | Expected Value / Type                       | Status | Actual Result |
| ----------------------- | ------------------------------- | -------------------------------------- | ------------------------------------------ | ------------------------------------------- | ------ | ------------- |
| TC-{feature_id}-SCH-001 | {Action + Function + Condition} | {e.g., "Successful creation response"} | {e.g., "id field" / "Content-Type header"} | {e.g., "integer, > 0" / "application/json"} | —      | —             |
| TC-{feature_id}-SCH-002 |                                 |                                        |                                            |                                             | —      | —             |

## TC-ERR — Error Handling

> Tests graceful handling of malformed, missing, and type-incorrect inputs.

| ID                      | Title                           | Scenario                     | Expected Status | Expected Error                        | Status | Actual Result |
| ----------------------- | ------------------------------- | ---------------------------- | --------------- | ------------------------------------- | ------ | ------------- |
| TC-{feature_id}-ERR-001 | {Action + Function + Condition} | {e.g., "Empty request body"} | {HTTP status}   | {Expected error message or structure} | —      | —             |
| TC-{feature_id}-ERR-002 |                                 |                              |                 |                                       | —      | —             |

## TC-IDP — Idempotency

> Tests repeated identical request behavior. **Only included if contract specifies idempotency rules.**

<!-- If not applicable, replace this section with: N/A — idempotency not specified in contract -->

| ID                      | Title                           | Description              | First Call Result    | Repeat Call Result                            | Status | Actual Result |
| ----------------------- | ------------------------------- | ------------------------ | -------------------- | --------------------------------------------- | ------ | ------------- |
| TC-{feature_id}-IDP-001 | {Action + Function + Condition} | {What is being verified} | {HTTP status + body} | {HTTP status + body on second identical call} | —      | —             |

## TC-RL — Rate Limiting

> Tests rate limit enforcement and recovery. **Only included if contract specifies rate limiting rules.**

<!-- If not applicable, replace this section with: N/A — rate limiting not specified in contract -->

| ID                     | Title                           | Request Pattern                                      | Expected Behavior after Limit                             | Status | Actual Result |
| ---------------------- | ------------------------------- | ---------------------------------------------------- | --------------------------------------------------------- | ------ | ------------- |
| TC-{feature_id}-RL-001 | {Action + Function + Condition} | {e.g., "Send 6 requests within 1 hour from same IP"} | {e.g., "6th request returns 429 with Retry-After header"} | —      | —             |

## Audit Log

> Human fills this section during audit. Mark each TC as VALID, INVALID, or INCOMPLETE.
>
> - **VALID** — TC is correct and ready for execution.
> - **INVALID** — TC has incorrect expected result, wrong input, or tests a non-existent scenario. Must be corrected or removed.
> - **INCOMPLETE** — TC is partially correct but missing information. Must be completed before execution.
>
> The Final Status column indicates the final decision after any fixes are applied. Only TCs with a Final Status of VALID will be used.

| ID                      | Audit Status                 | Notes (Original)      | Final Status | Notes (Fix)                |
| ----------------------- | ---------------------------- | --------------------- | ------------ | -------------------------- |
| TC-{feature_id}-FR-001  | VALID / INVALID / INCOMPLETE | {Reason if not VALID} | VALID        | {How it was fixed, if any} |
| TC-{feature_id}-FR-002  |                              |                       |              |                            |
| TC-{feature_id}-ST-001  |                              |                       |              |                            |
| TC-{feature_id}-SEC-001 |                              |                       |              |                            |
| TC-{feature_id}-SCH-001 |                              |                       |              |                            |
| TC-{feature_id}-ERR-001 |                              |                       |              |                            |
| TC-{feature_id}-IDP-001 |                              |                       |              |                            |
| TC-{feature_id}-RL-001  |                              |                       |              |                            |
