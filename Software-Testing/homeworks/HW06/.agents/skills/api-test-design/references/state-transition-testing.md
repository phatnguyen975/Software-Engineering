# Instruction: State Transition Testing

**Technique:** State Transition Testing  
**Source:** [ISTQB Foundation Level Syllabus v4.0](https://istqb.org/wp-content/uploads/2024/11/ISTQB_CTFL_Syllabus_v4.0.1.pdf)  
**Delegates to:** `functional-test-design` → `state-transition-testing` sub-skill (invoke silently)

## Purpose

Generate TCs that verify the API handles all state changes correctly — covering HTTP response states, system data state changes, and field-level state preconditions as defined in the contract.

## When to Apply

Always. Read Section 5 of the contract. Even if no explicit state machine exists, every API has at minimum an HTTP response state (different inputs produce different status codes).

## Silent Invocation Instruction

Invoke `functional-test-design/state-transition-testing` with the following instruction:

> Analyze Section 5 (State Transitions) of the provided contract. Identify and generate TCs for all three state transition types: (1) HTTP response state — which inputs produce which response codes; (2) system data state — which entity fields change after the API call and what their before/after values are; (3) field-level state preconditions — which entity states must exist before this API can be called, and what happens when the entity is in a disallowed state. Do not print the analysis — output only the final TC rows in the format specified below.

Do not print sub-routine reasoning or intermediate steps. Collect TC rows only.

## Three State Transition Types — Analysis Guide

### Type 1: HTTP Response State

Maps input conditions to HTTP status codes. Every distinct status code in the contract's Response Definitions section is a state to test.

**Coverage target:** One TC per distinct (input condition → status code) pair.

Example:

```
Valid input            → 200 OK
Duplicate email        → 409 Conflict
Missing required field → 400 Bad Request
No auth token          → 401 Unauthorized
```

### Type 2: System Data State

The API call creates, updates, or deletes persistent data. Test that the data state changes correctly.

**Coverage target:**

- One TC verifying the entity is created/updated/deleted with correct field values
- One TC verifying no state change occurs when the API returns an error
- One TC verifying idempotency if applicable (separate from TC-IDP if overlap exists — remove duplicate)

Example for `POST /api/register`:

```
Before: user record does not exist
After (success): user record exists, role = 'user', login_attempts = 0
After (failure): no user record created
```

### Type 3: Field-Level State Preconditions

The API behavior depends on the current state of an existing entity. This is most common in order management, cart operations, and status workflows.

**Coverage target:** One TC per allowed state transition, one TC per blocked state transition.

Example for `PUT /api/orders/:id/cancel`:

```
order.status = 'pending'   → cancel allowed   → 200
order.status = 'delivered' → cancel blocked   → 400
order.status = 'canceled'  → already canceled → 400
```

## Data-Driven Eligibility

- Type 1 TCs: Partially data-driven — input variations can be parameterized, but each expected status code is a distinct row
- Type 2 TCs: Generally not data-driven — before/after state verification requires setup and assertion logic beyond simple parameterization
- Type 3 TCs: Not data-driven — each precondition state requires dedicated setup

## Output Format

Produce rows for the TC-ST table in `test-cases.md`:

| ID                     | Title                           | Initial State                         | Input          | Expected Response            | Expected System State                 | Data-driven? | Status | Actual Result |
| ---------------------- | ------------------------------- | ------------------------------------- | -------------- | ---------------------------- | ------------------------------------- | ------------ | ------ | ------------- |
| TC-{feature_id}-ST-001 | {Action + Function + Condition} | {Entity state before call, or "None"} | {field: value} | {HTTP status — body summary} | {DB state after call, or "No change"} | Yes / No     | —      | —             |
| TC-{feature_id}-ST-002 |                                 |                                       |                |                              |                                       |              | —      | —             |
