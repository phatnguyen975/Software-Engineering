# Severity and Priority Guide — `api-bug-report`

This guide defines how to assign Severity and Priority to each bug entry. All assignments made by AI are labeled as "AI-suggested" and must be confirmed by a human reviewer.

**Source:** [ISTQB Foundation Level Syllabus v4.0 — Defect Management](https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf), [IEEE 1044-2009 — Standard Classification for Software Anomalies](https://standards.ieee.org/ieee/1044/3448/)

## Severity Levels

Severity describes the **technical impact** of the defect on the system.

### Critical

The defect causes complete loss of a core function, data corruption, or a severe security vulnerability. The system cannot be used safely.

**Assign Critical when:**

- Security vulnerability that allows unauthorized data access, privilege escalation, or remote code execution (e.g., SQL injection returns data, admin endpoint accessible without auth)
- Core business transaction fails entirely (e.g., checkout endpoint always returns 500)
- Data corruption or unintended permanent data deletion

### High

The defect significantly degrades a major feature. A workaround may exist but is not acceptable for production.

**Assign High when:**

- Security issue that exposes information without direct exploitation (e.g., stack trace in error response, XSS payload stored)
- Core validation missing (e.g., no uniqueness check on email → any number of duplicate accounts possible)
- Business rule violation that affects financial calculations (e.g., discount applied incorrectly)
- State transition logic wrong for a primary flow (e.g., order cannot be canceled when it should be)

### Medium

The defect affects a secondary feature or produces an incorrect response that does not compromise security or data integrity.

**Assign Medium when:**

- Response schema missing a non-critical field (e.g., `message` field absent but `id` is present)
- Wrong HTTP status code returned but correct action taken (e.g., returns 200 instead of 201 on creation)
- Error message non-informative but not leaking sensitive data
- Validation present but with wrong boundary (e.g., accepts 7 characters when minimum should be 8)

### Low

The defect is cosmetic, inconsistent, or has minimal functional impact.

**Assign Low when:**

- Wrong `Content-Type` header on a response that is otherwise correct
- Error message wording inconsistent with contract but not misleading
- Optional field missing from response
- Case sensitivity inconsistency (e.g., email comparison is case-sensitive when contract says case-insensitive, but this rarely causes real user problems)

## Priority Levels

Priority describes **how urgently the defect should be fixed**, based on business impact and risk. Priority may differ from Severity.

### P1 — Fix Immediately

Must be resolved before any release or further testing. Blocks critical user flows or poses immediate security risk.

**Assign P1 when:**

- Severity is Critical
- Defect blocks all other testing (e.g., auth endpoint always fails)

### P2 — Fix in Current Iteration

Should be resolved in the current sprint/release cycle. Significant impact on users or security.

**Assign P2 when:**

- Severity is High
- Defect affects a primary user flow but a workaround exists

### P3 — Fix in Next Iteration

Should be planned for the next release. Moderate impact, workaround available.

**Assign P3 when:**

- Severity is Medium
- Defect is in a secondary feature or edge case

### P4 — Fix When Convenient

Low urgency. Can be deferred to backlog.

**Assign P4 when:**

- Severity is Low
- Cosmetic or consistency issue

## Severity/Priority Matrix — Common Combinations

| Root Cause Category                           | Typical Severity | Typical Priority | Notes                            |
| --------------------------------------------- | ---------------- | ---------------- | -------------------------------- |
| `SECURITY` — SQL injection, XSS exploitable   | Critical         | P1               | Immediate fix required           |
| `SECURITY` — Mass assignment (role field)     | Critical         | P1               | Privilege escalation risk        |
| `SECURITY` — Auth bypass (no token check)     | Critical         | P1               |                                  |
| `SECURITY` — Info exposure in error response  | High             | P2               |                                  |
| `AUTH` — Missing role check                   | Critical         | P1               | Admin data accessible to users   |
| `AUTH` — IDOR                                 | High             | P1               | User data exposed to other users |
| `VALIDATION` — Required field not validated   | High             | P2               |                                  |
| `VALIDATION` — Boundary constraint wrong      | Medium           | P3               |                                  |
| `BUSINESS_LOGIC` — Core rule violated         | High             | P2               |                                  |
| `BUSINESS_LOGIC` — Edge case rule missed      | Medium           | P3               |                                  |
| `SCHEMA` — Required field missing in response | Medium           | P3               |                                  |
| `SCHEMA` — Wrong HTTP status code             | Low–Medium       | P3–P4            | Depends on impact                |
| `STATE` — Wrong transition allowed            | High             | P2               |                                  |
| `STATE` — Correct transition blocked          | High             | P2               |                                  |

## Override Rules

Priority may be raised above what severity suggests when:

- The defect is on a high-traffic endpoint (e.g., login, checkout)
- The defect is in a feature under active user acceptance testing
- A security defect is in a publicly exposed endpoint

Priority may be lowered below what severity suggests when:

- The affected feature is not yet released to users
- A temporary workaround is in place and documented
