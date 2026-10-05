# Section: Contract

## Purpose

The Contract is what makes a spec implementable without follow-up questions: it is the part a client developer or an API-consuming agent would actually read to write code against. This is where "the happy path is precise, errors are vague" (a known way specs fall short) gets fixed by being equally precise about every status code, not just the success case.

## What to include, per endpoint

Repeat the block below for **every new endpoint, and every existing endpoint whose behavior changes** because of this feature. If an existing endpoint's behavior changes, add an explicit **Before / After** note under its description — don't just describe the new behavior and leave the reader to infer what used to happen.

- Method + path.
- Required role/auth, using the exact actor name defined in Actors.
- Whether the endpoint needs to be idempotent, and why (or why not) — one line is enough, but don't skip it. This matters most for any endpoint that can plausibly be retried or double-submitted (payment, state-changing actions triggered by a possibly-flaky client).
- Path parameters, query parameters (as tables — these are short, structured lookups, a table is the clearest format).
- Request body as an actual JSON schema, not a prose description of the fields.
- Validation rules for every field that has one (format, required/optional, range, max length) — either stated directly or by citing a `BR-NN` from Data if the rule is a business invariant defined there.
- The success response, as a full JSON schema, with its exact status code and reason phrase.
- **Every** error response the endpoint can return, enumerated individually (see format below) — not summarized, not left as "other errors may occur."
- Side effects beyond the response itself: an event published, another table written, a downstream system notified. State "None." if there genuinely are none — don't omit the heading.
- A `Satisfies:` line citing the `FR-NN` (and `AC-NN`, once Acceptance Criteria exists) this endpoint fulfills.

## Error response format

Do **not** put error responses in a table. A table forces every error's payload into one cell, which either truncates detail or produces an unreadable cell. Instead, repeat a block structurally identical to the Success Response, once per status/condition:

````markdown
**Success Response — <code> <reason phrase>:**

```json
{ "fieldName": "type — description" }
```

**Error Response — <code> <ERROR_IDENTIFIER>:** <condition that triggers it>

```json
{ "error": "<ERROR_IDENTIFIER>", "message": "<text>" }
```

**Error Response — <code> <ERROR_IDENTIFIER>:** <another condition>

```json
{ "error": "<ERROR_IDENTIFIER>", "message": "<text>" }
```
````

If the system already has a standard error envelope (check the input for one), reuse that exact shape — don't invent a new one for this feature.

## Global Standards Applied

Before the per-endpoint blocks, state which of the system's _existing_ conventions apply here: auth header convention, success envelope, error envelope, pagination (if any collection endpoints are involved), idempotency-key convention (if applicable — e.g. a client-supplied key stored and checked against, matching how systems like Stripe define idempotent requests), date/time format. List only what's relevant to this feature; this is a pointer to existing conventions, not a place to define new ones.

## Template

````markdown
## Contract

### Global Standards Applied

- Auth: ...
- Success envelope: ...
- Error envelope: ...
- Idempotency-key convention (if applicable): ...

### <METHOD> <path>

**Required role / Auth:** <actor name from Actors>

**Idempotency:** Required | Not Required — <one-line reason>

**Request:**

Path parameters:

| Name | Type | Description |
| :--- | :--- | :---------- |

Query parameters (if any):

| Name | Type | Required | Description |
| :--- | :--- | :------- | :---------- |

Body:

```json
{ "fieldName": "type — description" }
```

Validation rules:

- `fieldName` — <format / required / range / max length, or cite BR-NN>

**Success Response — <code> <reason phrase>:**

```json
{ "fieldName": "type — description" }
```

**Error Response — <code> <ERROR_IDENTIFIER>:** <condition>

```json
{ "error": "<ERROR_IDENTIFIER>", "message": "<text>" }
```

**Side Effects:** <event published / other record changed, or "None.">
**Satisfies:** FR-NN, AC-NN
````

## Common mistakes

- Describing the response in prose ("returns the updated preferences") instead of an actual JSON schema — this is the single most common way a Contract section scores low.
- Listing only the 2xx response and one generic "400 if invalid" — every distinct error condition needs its own block with its own status code and identifier.
- Inventing a new error envelope shape instead of reusing the system's existing one (check the input before assuming there isn't one).
- Forgetting **Before/After** when an existing endpoint's behavior changes — readers will assume nothing about the old endpoint changed unless told otherwise.
