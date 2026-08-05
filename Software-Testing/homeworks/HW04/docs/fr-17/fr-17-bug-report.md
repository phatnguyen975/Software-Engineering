# Bug Report — FR-17: Coupon Management (CRUD)

> **Feature:** FR-17 — Coupon Management (CRUD)  
> **Test Case Source:** `docs/fr-17/fr-17-test-cases.md`  
> **Total Failing TCs:** 6  
> **Bugs Identified:** 4  
> **Generated:** 2026-08-05

## BUG-FR17-001

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                              |
| ------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR17-001                                                                                                                                                                                                                                                                                       |
| Title        | Coupon creation allows invalid `discount_value` without server-side rejection                                                                                                                                                                                                                      |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                                                                                   |
| Root Cause   | The coupon creation API endpoint does not enforce the `discount_value` field constraints (must be > 0; must be ≤ 100 when `type` is `percent`). Requests with `discount_value = 0`, `-1`, or `101` (percent type) are accepted and persisted, allowing semantically invalid coupons to be created. |
| Affects TCs  | TC-FR17-010, TC-FR17-011, TC-FR17-012                                                                                                                                                                                                                                                              |
| GitHub Issue | [#32](https://github.com/phatnguyen975/eshop-sut/issues/32)                                                                                                                                                                                                                                        |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                                                                            |
| -------- | ----- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major | Coupons with a zero, negative, or out-of-range percentage discount can be created and applied, resulting in incorrect price calculations for customers. The feature partially works but a key business rule is silently violated. |
| Priority | High  | `discount_value` validation is a core acceptance criterion (AC-06, AC-14) and must be enforced before any production release of the coupon feature.                                                                               |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

**Variant A — discount_value = 0 (TC-FR17-010):**

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. Fill in all required fields with valid values; set `discount_value` to `0`.
5. Submit the form.

**Observe:** The system accepts the submission, creates a new coupon with `discount_value = 0`, and displays it in the coupon list. No validation error is shown.

**Variant B — discount_value = -1 (TC-FR17-011):**

Steps 1–3 same as above; at step 4 set `discount_value` to `-1` and submit.

**Observe:** The system accepts the submission and persists a coupon with a negative discount value.

**Variant C — type = percent, discount_value = 101 (TC-FR17-012):**

Steps 1–3 same as above; at step 4 set `type` to `percent` and `discount_value` to `101`, then submit.

**Observe:** The system accepts the submission and persists a coupon where the percentage discount exceeds 100%.

### Expected Result

Form rejected. Validation error displayed on the `discount_value` field indicating it must be greater than zero (Variants A & B) / cannot exceed 100 for percent type (Variant C). No coupon is created.

### Actual Result

The system failed to reject the input and successfully created a coupon with a `discount_value` equal to zero (Variant A), a negative value (Variant B), or a value exceeding 100 (Variant C) across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-001.png](../../screenshots/fr-17/BUG-FR17-001.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

All three variants reproduce consistently across Chromium, Firefox, and WebKit. The defect is confined to server-side validation — the frontend form does not prevent submission of these invalid values, and the API silently accepts them. TC-FR17-003 (`discount_value = 0.01`) and TC-FR17-004 (`discount_value = 100`) both pass, confirming the boundary endpoints themselves are not the issue; only the rejection logic for out-of-range values is missing.

## BUG-FR17-002

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                            |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Bug ID       | BUG-FR17-002                                                                                                                                                                                                                                                                                     |
| Title        | Coupon creation accepts invalid `type` enum value without server-side rejection                                                                                                                                                                                                                  |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                                                                                 |
| Root Cause   | The coupon creation API endpoint does not validate the `type` field against the allowed enum values (`percent`, `fixed`). When an invalid value such as `"flat"` is submitted directly (bypassing the UI dropdown), the API accepts and persists the record without returning an error response. |
| Affects TCs  | TC-FR17-009                                                                                                                                                                                                                                                                                      |
| GitHub Issue | [#33](https://github.com/phatnguyen975/eshop-sut/issues/33)                                                                                                                                                                                                                                      |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                         |
| -------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Severity | Major | A coupon with an unrecognised type value can be persisted in the database, which may cause runtime errors or undefined behaviour when the coupon is later applied at checkout. |
| Priority | High  | Enum validation at the API level is a mandatory guard against malformed data and is required by AC-05. Must be fixed before release.                                           |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. Using browser DevTools or a direct API call, set the `type` field to the value `"flat"` (bypassing the UI dropdown which restricts to valid options).
5. Fill all remaining fields with valid values.
6. Submit the form / send the API request.

   ```bash
   TOKEN=$(curl -s -X POST http://localhost:3000/api/login \
     -H "Content-Type: application/json" \
     -d '{"email": "admin@eshop.com", "password": "Admin123!"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

   curl -i -X POST http://localhost:3000/api/admin/coupons \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
       "code": "FLAT123",
       "type": "flat",
       "discount_value": 10,
       "expired_at": "2099-12-31",
       "min_order_amount": 0,
       "max_uses_per_user": 1
     }'
   ```

**Observe:** The system accepts the request, creates a coupon with `type = "flat"`, and displays it in the coupon list. No validation error is returned.

### Expected Result

Form rejected. Validation error displayed on the `type` field indicating only `"percent"` and `"fixed"` are accepted values. No coupon is created.

### Actual Result

The system failed to reject the input and successfully created a coupon with an invalid type value (`"flat"`) across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-002.png](../../screenshots/fr-17/BUG-FR17-002.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

The UI dropdown naturally prevents a user from selecting an invalid type in normal browser usage. This defect is exploitable via direct API access or form manipulation (e.g. DevTools, curl). Server-side enum validation is necessary regardless of UI constraints.

## BUG-FR17-003

### Summary

| Field        | Value                                                                                                                                                                                                                                          |
| ------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR17-003                                                                                                                                                                                                                                   |
| Title        | Coupon creation allows negative `min_order_amount` without server-side rejection                                                                                                                                                               |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                               |
| Root Cause   | The coupon creation API endpoint does not enforce the constraint that `min_order_amount` must be ≥ 0. A request with `min_order_amount = -1` is accepted and persisted, creating a coupon with a semantically invalid minimum order threshold. |
| Affects TCs  | TC-FR17-014                                                                                                                                                                                                                                    |
| GitHub Issue | [#34](https://github.com/phatnguyen975/eshop-sut/issues/34)                                                                                                                                                                                    |

### Severity & Priority

| Field    | Value  | Reason                                                                                                                                                                                        |
| -------- | ------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Major  | A coupon with a negative minimum order amount could be applicable to any order regardless of value, bypassing the intended minimum order restriction and causing unintended discounts.        |
| Priority | Medium | The defect affects an edge case (manually entering a negative value) rather than the primary creation flow. The impact is real but lower than `discount_value` or `type` validation failures. |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. Fill in all required fields with valid values; set `min_order_amount` to `-1`.
5. Submit the form.

**Observe:** The system accepts the submission, creates a new coupon with `min_order_amount = -1`, and displays it in the coupon list. No validation error is shown.

### Expected Result

Form rejected. Validation error displayed on the `min_order_amount` field indicating it must be ≥ 0. No coupon is created.

### Actual Result

The system failed to reject the input and successfully created a coupon with a negative `min_order_amount` across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-003.png](../../screenshots/fr-17/BUG-FR17-003.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

TC-FR17-002 (`min_order_amount = 0`) and the positive tests confirm the field itself works correctly for valid values. The defect is isolated to the absence of a lower-bound check (< 0) on the server side.

## BUG-FR17-004

### Summary

| Field        | Value                                                                                                                                                                                                                                                                                                                                 |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Bug ID       | BUG-FR17-004                                                                                                                                                                                                                                                                                                                          |
| Title        | Coupon creation persists `code` with leading whitespace without trimming or rejection                                                                                                                                                                                                                                                 |
| Feature      | FR-17 — Coupon Management (CRUD)                                                                                                                                                                                                                                                                                                      |
| Root Cause   | The coupon creation API endpoint does not sanitise the `code` field by trimming leading or trailing whitespace before persisting the value. A `code` submitted as `" NEWCODE"` (with one leading space) is stored as-is, creating a coupon that differs from `"NEWCODE"` in storage but is visually indistinguishable in the list UI. |
| Affects TCs  | TC-FR17-016                                                                                                                                                                                                                                                                                                                           |
| GitHub Issue | [#35](https://github.com/phatnguyen975/eshop-sut/issues/35)                                                                                                                                                                                                                                                                           |

### Severity & Priority

| Field    | Value | Reason                                                                                                                                                                                      |
| -------- | ----- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Severity | Minor | The feature remains functional for typical admin usage; the defect only manifests when whitespace is deliberately or accidentally included in the code field. No data loss or crash occurs. |
| Priority | Low   | This is an edge-case input sanitisation issue that can be deferred to a future cycle without blocking core coupon creation or application flows.                                            |

### Environment

| Field       | Value                     |
| ----------- | ------------------------- |
| Browser     | Chromium, Firefox, WebKit |
| OS          | Windows 11                |
| Web URL     | N/A                       |
| Admin URL   | http://localhost:5174     |
| SUT Version | Not recorded              |

### Steps to Reproduce

1. Navigate to `http://localhost:5174` and log in as admin.
2. Navigate to the coupon management section.
3. Click "Mã Giảm Giá".
4. In the `code` field, enter `" NEWCODE"` (a single leading space followed by "NEWCODE").
5. Fill all remaining fields with valid values.
6. Submit the form.

**Observe:** The system accepts the submission and displays a coupon in the list. The persisted code includes the leading space character, which is not trimmed.

### Expected Result

System either trims the whitespace and creates coupon with code `"NEWCODE"`, or rejects the input with a validation error. A coupon with a space-prefixed code must not be persisted.

### Actual Result

The system successfully created and persisted a coupon with a code containing leading whitespace across all 3 browsers.

### Evidence

- **Screenshot:**

![BUG-FR17-004.png](../../screenshots/fr-17/BUG-FR17-004.png)

- **Playwright Report:** `playwright-report/fr-17/index.html`

### Notes

The two acceptable resolutions are: (1) server trims whitespace silently and stores `"NEWCODE"`, or (2) server rejects the input with a validation error. The current behaviour — persisting the space-prefixed value — is incorrect under either interpretation. A whitespace-only `code` should also be rejected, though that case was not explicitly tested in this suite.
