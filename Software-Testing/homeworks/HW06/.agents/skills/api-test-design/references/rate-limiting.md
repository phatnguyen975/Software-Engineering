# Instruction: Rate Limiting Testing

**Technique:** Rate Limit Verification  
**Source:** [OWASP API Security Top 10 2023 — API4: Unrestricted Resource Consumption](https://owasp.org/API-Security/editions/2023/en/0xa4-unrestricted-resource-consumption/), [RFC 6585 §4 — 429 Too Many Requests](https://www.rfc-editor.org/rfc/rfc6585#section-4)  
**Delegates to:** Standalone — not delegated to a sub-routine

## Purpose

Generate TCs that verify the API enforces rate limiting as documented in the contract — returning `429 Too Many Requests` after the threshold is exceeded and recovering correctly after the limit window resets.

## When to Apply

**Conditional.** Only generate TC-RL entries if the contract explicitly specifies a rate limiting rule (threshold, window, or lockout behavior) in Section 3 (Business Rules) or Section 6 (Security Rules). If absent, mark this section `N/A — rate limiting not specified in contract`.

Do not assume a rate limit exists based on the endpoint type alone.

## TC Generation Rules

For each rate limiting rule in the contract, generate the following TCs:

### TC-RL-001: Requests Within Limit

- Send N requests where N = documented limit
- Each request should succeed with the documented success response
- Verifies the limit is not triggered prematurely

### TC-RL-002: Request Exceeding Limit

- Send N+1 requests (or burst rapidly to exceed the threshold)
- The final request (or requests beyond the threshold) must return `429 Too Many Requests`
- Verify `Retry-After` header is present if documented

### TC-RL-003: Recovery After Limit Window

- After receiving `429`, wait for the documented cooldown window to expire
- Send one more request
- Verify it succeeds with the normal response (limit has reset)

### TC-RL-004: Per-User vs Per-IP Scoping (if documented)

- If the contract specifies the limit is per-user: verify that two different authenticated users each get their own independent limit counter
- If per-IP: verify limit applies regardless of which user account is used

## Newman Execution Note

Rate limiting TCs **cannot be parameterized as data-driven rows**. They require controlled timing and sequential execution:

- Use Newman's `--delay-request` flag to control request intervals when testing within-limit behavior
- For burst testing (exceeding limit), send requests without delay
- Mark all TC-RL entries as `Data-driven: No`

## Data-Driven Eligibility

Rate limiting TCs are **not data-driven**. The test logic depends on request sequencing and timing, not input parameter variation.

## Output Format

Produce rows for the TC-RL table in `test-cases.md`:

| ID                     | Title                           | Request Pattern                                      | Expected Behavior after Limit                             | Status | Actual Result |
| ---------------------- | ------------------------------- | ---------------------------------------------------- | --------------------------------------------------------- | ------ | ------------- |
| TC-{feature_id}-RL-001 | {Action + Function + Condition} | {e.g., "Send 6 requests within 1 hour from same IP"} | {e.g., "6th request returns 429 with Retry-After header"} | —      | —             |
