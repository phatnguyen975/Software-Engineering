# Test Case Document — FR-17: Coupon Management (CRUD)

> **Source Spec:** `docs/fr-17/fr-17-spec.md`  
> **Generated:** 2026-08-03  
> **Status:** APPROVED  
> **Total TCs:** 18

## Section 1 — Analysis Trail

### 1.1 Equivalence Partition Tables

#### Field: `code` (string, required, must be unique)

| Class ID | Partition Description                | Type    | Representative          |
| -------- | ------------------------------------ | ------- | ----------------------- |
| EP-C1    | Non-empty string, unique in system   | Valid   | `"SUMMER25"`            |
| EP-C2    | Non-empty string that already exists | Invalid | `"SAVE10"` (pre-seeded) |
| EP-C3    | Empty string                         | Invalid | `""`                    |

#### Field: `type` (enum, required)

| Class ID | Partition Description      | Type    | Representative |
| -------- | -------------------------- | ------- | -------------- |
| EP-T1    | Value = `"percent"`        | Valid   | `"percent"`    |
| EP-T2    | Value = `"fixed"`          | Valid   | `"fixed"`      |
| EP-T3    | Value outside allowed enum | Invalid | `"flat"`       |

#### Field: `discount_value` (number, required, > 0; max = 100 when type = percent)

| Class ID | Partition Description                            | Type    | Representative |
| -------- | ------------------------------------------------ | ------- | -------------- |
| EP-D1    | Positive number ≤ 100 (valid for percent type)   | Valid   | `10`           |
| EP-D2    | Positive number, no upper bound (for fixed type) | Valid   | `50000`        |
| EP-D3    | Zero                                             | Invalid | `0`            |
| EP-D4    | Negative number                                  | Invalid | `-1`           |
| EP-D5    | Value > 100 when type is `percent`               | Invalid | `101`          |

#### Field: `expired_at` (date, required)

| Class ID | Partition Description        | Type    | Representative |
| -------- | ---------------------------- | ------- | -------------- |
| EP-E1    | Valid date string (any date) | Valid   | `"2099-12-31"` |
| EP-E2    | Empty/missing value          | Invalid | `""`           |

#### Field: `min_order_amount` (number, required, >= 0)

| Class ID | Partition Description  | Type    | Representative |
| -------- | ---------------------- | ------- | -------------- |
| EP-M1    | Zero (minimum allowed) | Valid   | `0`            |
| EP-M2    | Positive number        | Valid   | `300000`       |
| EP-M3    | Negative number        | Invalid | `-1`           |

#### Field: `max_uses_per_user` (integer, required, >= 1)

| Class ID | Partition Description        | Type    | Representative |
| -------- | ---------------------------- | ------- | -------------- |
| EP-U1    | Exactly 1 (boundary minimum) | Valid   | `1`            |
| EP-U2    | Integer greater than 1       | Valid   | `2`            |
| EP-U3    | Zero                         | Invalid | `0`            |
| EP-U4    | Negative integer             | Invalid | `-1`           |

### 1.2 Boundary Value Analysis

#### Field: `discount_value` for `type = "percent"` (min exclusive = 0, max inclusive = 100)

| BVA Point | Value  | EP Class | TC Type              |
| --------- | ------ | -------- | -------------------- |
| min (0)   | `0`    | Invalid  | Negative (EP-D3)     |
| min + 1   | `0.01` | Valid    | Edge (boundary low)  |
| max − 1   | `99`   | Valid    | Positive             |
| max       | `100`  | Valid    | Edge (boundary high) |
| max + 1   | `101`  | Invalid  | Negative (EP-D5)     |

#### Field: `min_order_amount` (min = 0, no defined max)

| BVA Point | Value | EP Class | TC Type             |
| --------- | ----- | -------- | ------------------- |
| min − 1   | `-1`  | Invalid  | Negative (EP-M3)    |
| min       | `0`   | Valid    | Edge (boundary low) |
| min + 1   | `1`   | Valid    | Positive            |

#### Field: `max_uses_per_user` (min = 1, no defined max)

| BVA Point | Value | EP Class | TC Type             |
| --------- | ----- | -------- | ------------------- |
| min − 1   | `0`   | Invalid  | Negative (EP-U3)    |
| min       | `1`   | Valid    | Edge (boundary low) |
| min + 1   | `2`   | Valid    | Positive            |

### 1.3 Error Guessing Catalogue

| #     | Fault Class                       | Status  | Applied As                                                                          |
| ----- | --------------------------------- | ------- | ----------------------------------------------------------------------------------- |
| EG-01 | Empty / blank inputs              | Applied | Empty `code` (TC-FR17-007), empty `expired_at` (TC-FR17-013) — merged with EP cases |
| EG-02 | Boundary-adjacent special chars   | N/A     | `code` has no format constraint; special chars not restricted by spec               |
| EG-03 | Maximum-length exact string       | N/A     | No max length defined for `code` (human-confirmed)                                  |
| EG-04 | Overlong input                    | N/A     | No max length defined for `code`                                                    |
| EG-05 | Leading / trailing whitespace     | Applied | `code` with leading space `" NEWCODE"` → TC-FR17-017                                |
| EG-06 | Duplicate / already-existing data | Applied | Duplicate `code` — merged with EP-C2 → TC-FR17-008                                  |
| EG-07 | Incorrect data type               | Applied | `discount_value` = `"abc"` (non-numeric string) → TC-FR17-018                       |
| EG-08 | Case sensitivity                  | N/A     | Spec does not specify case-sensitivity rules for `code`                             |
| EG-09 | Multi-field interaction           | Applied | `type=percent` + `discount_value=101` — merged with EP-D5 → TC-FR17-012             |
| EG-10 | Dependency violation              | N/A     | Unauthenticated access is handled outside this spec (Auth Suite).                   |
| EG-11 | SQL / script injection            | Applied | `code` = `"'; DROP TABLE coupons; --"` → TC-FR17-019                                |
| EG-12 | Unicode / multi-byte characters   | N/A     | `code` has no format restriction; emoji input not guarded by spec                   |
| EG-13 | Repeated submission               | N/A     | Covered by duplicate code case (EP-C2)                                              |
| EG-14 | Session / auth state mismatch     | N/A     | Handled in Auth Suite.                                                              |

### 1.4 Valid-Class Combination Matrix (Combination Rule)

All valid EP representatives combined into the minimum number of positive test cases:

| TC (positive) | code  | type              | discount_value  | expired_at         | min_order_amount | max_uses_per_user |
| ------------- | ----- | ----------------- | --------------- | ------------------ | ---------------- | ----------------- |
| C1            | EP-C1 | EP-T1 (`percent`) | EP-D1 (`10`)    | EP-E1 (2099-12-31) | EP-M2 (`300000`) | EP-U2 (`2`)       |
| C2            | EP-C1 | EP-T2 (`fixed`)   | EP-D2 (`50000`) | EP-E1 (2099-12-31) | EP-M1 (`0`)      | EP-U1 (`1`)       |

2 TCs cover all valid EP classes (EP-C1, EP-T1, EP-T2, EP-D1, EP-D2, EP-E1, EP-M1, EP-M2, EP-U1, EP-U2).

Additional BVA edge cases (not covered by C1/C2):

- BVA Edge: `discount_value = 0.01` (percent type, boundary min+1) → TC-FR17-003
- BVA Edge: `discount_value = 100` (percent type, boundary max) → TC-FR17-004

## Section 2 — Test Case Table

> **Total TCs:** 18  
> **Passed:** 12 · **Failed:** 6 · **Skipped:** 0

| TC-ID       | Title                                                                  | Type     | Technique      | Priority | Preconditions                                                            | Test Steps                                                                                                                                                                                                                                                          | Input Data                         | Expected Result                                                                                                                                                       | Actual Result                                                                                                                                  | Status | Notes                                                                                |
| ----------- | ---------------------------------------------------------------------- | -------- | -------------- | -------- | ------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | ------ | ------------------------------------------------------------------------------------ |
| TC-FR17-001 | Create coupon with valid data using percent type                       | Positive | Domain Testing | High     | Admin is logged in. Code "SUMMER25" does not exist in the system.        | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter all fields as per input data<br>5. Submit the form                                                                     | → ref: fr-17-data.json#TC-FR17-001 | Coupon created successfully. "SUMMER25" appears in the list with type "percent", discount_value 10, min_order_amount 300000, max_uses_per_user 2.                     | The system successfully created the coupon, and "SUMMER25" is visible in the list with all provided attributes matching across all 3 browsers. | Pass   | Maps to AC-01, AC-11, AC-15. Covers EP-C1, EP-T1, EP-D1, EP-E1, EP-M2, EP-U2.        |
| TC-FR17-002 | Create coupon with valid data using fixed type and zero min order      | Positive | Domain Testing | High     | Admin is logged in. Code "FIXEDJUL" does not exist in the system.        | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter all fields as per input data<br>5. Submit the form                                                                     | → ref: fr-17-data.json#TC-FR17-002 | Coupon created successfully. "FIXEDJUL" appears in the list with type "fixed", discount_value 50000, min_order_amount 0, max_uses_per_user 1.                         | The system successfully created the coupon, and "FIXEDJUL" is visible in the list with all provided attributes matching across all 3 browsers. | Pass   | Maps to AC-01, AC-12. Covers EP-T2, EP-D2, EP-M1, EP-U1.                             |
| TC-FR17-003 | Create coupon with discount_value at minimum boundary 0.01 for percent | Edge     | Domain Testing | Medium   | Admin is logged in. Code "MINVAL01" does not exist in the system.        | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter all fields as per input data (discount_value = 0.01, type = percent)<br>5. Submit                                      | → ref: fr-17-data.json#TC-FR17-003 | Coupon created successfully. "MINVAL01" appears in the list with discount_value 0.01.                                                                                 | The system successfully created the coupon, and "MINVAL01" is visible in the list with discount_value 0.01 across all 3 browsers.              | Pass   | BVA min+1 for discount_value (percent). Maps to AC-01.                               |
| TC-FR17-004 | Create coupon with discount_value at maximum boundary 100 for percent  | Edge     | Domain Testing | Medium   | Admin is logged in. Code "FULL100" does not exist in the system.         | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter all fields as per input data (discount_value = 100, type = percent)<br>5. Submit                                       | → ref: fr-17-data.json#TC-FR17-004 | Coupon created successfully. "FULL100" appears in the list with discount_value 100.                                                                                   | The system successfully created the coupon, and "FULL100" is visible in the list with discount_value 100 across all 3 browsers.                | Pass   | BVA max for discount_value (percent). Maps to AC-11, AC-14.                          |
| TC-FR17-005 | View coupon list as authenticated admin                                | Positive | Domain Testing | High     | Admin is logged in. At least one coupon exists (e.g. pre-seeded SAVE10). | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section                                                                                                                                                              | → ref: fr-17-data.json#TC-FR17-005 | Coupon management page displays a list of all existing coupons. Each entry shows: code, type, discount_value, expired_at, min_order_amount, max_uses_per_user.        | The system correctly displays the list of all existing coupons with their respective details across all 3 browsers.                            | Pass   | Maps to AC-02, SP-02.                                                                |
| TC-FR17-006 | Delete coupon with confirmation dialog                                 | Positive | Domain Testing | High     | Admin is logged in. A coupon with code "TODELETE" exists in the system.  | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Locate coupon "TODELETE" and click the delete action<br>4. Confirm deletion in the confirmation dialog                                                 | → ref: fr-17-data.json#TC-FR17-006 | Coupon "TODELETE" is removed. It no longer appears in the coupon list.                                                                                                | The system successfully deleted the coupon after confirmation, and it no longer appears in the list across all 3 browsers.                     | Pass   | Maps to AC-03, SP-03, BR-10.                                                         |
| TC-FR17-007 | Create coupon with empty code field                                    | Negative | Domain Testing | High     | Admin is logged in.                                                      | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Leave code field empty; fill remaining fields with valid values<br>5. Submit the form                                        | → ref: fr-17-data.json#TC-FR17-007 | Form rejected. Validation error displayed on the code field indicating it is required. No coupon is created.                                                          | The system rejected the form and correctly displayed a validation error on the code field across all 3 browsers. No coupon was created.        | Pass   | Maps to AC-07, FP-04. EP-C3.                                                         |
| TC-FR17-008 | Create coupon with duplicate coupon code                               | Negative | Domain Testing | High     | Admin is logged in. Coupon "SAVE10" already exists (pre-seeded).         | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter code "SAVE10"; fill remaining fields with valid values<br>5. Submit the form                                           | → ref: fr-17-data.json#TC-FR17-008 | Form rejected. Validation error displayed indicating the coupon code already exists. No new coupon is created.                                                        | The system rejected the form and correctly displayed an error message stating the code already exists across all 3 browsers.                   | Pass   | Maps to AC-04, FP-01. EP-C2.                                                         |
| TC-FR17-009 | Create coupon with invalid type value                                  | Negative | Domain Testing | High     | Admin is logged in.                                                      | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Attempt to set type to invalid value "flat" (via form manipulation or API); fill other fields with valid values<br>5. Submit | → ref: fr-17-data.json#TC-FR17-009 | Form rejected. Validation error displayed on the type field. No coupon is created.                                                                                    | The system failed to reject the input and successfully created a coupon with an invalid type across all 3 browsers.                            | Fail   | Maps to AC-05, FP-02. EP-T3. If UI uses a dropdown, verify only valid values appear. |
| TC-FR17-010 | Create coupon with discount_value equal to zero                        | Negative | Domain Testing | High     | Admin is logged in. Code "ZERODSC" does not exist in the system.         | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter discount_value = 0; fill remaining fields with valid values<br>5. Submit the form                                      | → ref: fr-17-data.json#TC-FR17-010 | Form rejected. Validation error displayed on the discount_value field indicating it must be greater than zero. No coupon is created.                                  | The system failed to reject the input and successfully created a coupon with a discount_value equal to zero across all 3 browsers.             | Fail   | Maps to AC-06, FP-03. EP-D3, BVA min (exclusive).                                    |
| TC-FR17-011 | Create coupon with negative discount_value                             | Negative | Domain Testing | Medium   | Admin is logged in. Code "NEGDISC" does not exist in the system.         | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter discount_value = -1; fill remaining fields with valid values<br>5. Submit the form                                     | → ref: fr-17-data.json#TC-FR17-011 | Form rejected. Validation error displayed on the discount_value field indicating it must be a positive number. No coupon is created.                                  | The system failed to reject the input and successfully created a coupon with a negative discount_value across all 3 browsers.                  | Fail   | Maps to AC-06, FP-03. EP-D4.                                                         |
| TC-FR17-012 | Create coupon with percent type and discount_value exceeding 100       | Negative | Domain Testing | High     | Admin is logged in. Code "OVER100" does not exist in the system.         | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Set type = "percent", discount_value = 101; fill remaining fields with valid values<br>5. Submit                             | → ref: fr-17-data.json#TC-FR17-012 | Form rejected. Validation error displayed on the discount_value field indicating it cannot exceed 100 for percent type. No coupon is created.                         | The system failed to reject the input and successfully created a coupon with discount_value exceeding 100 across all 3 browsers.               | Fail   | Maps to AC-14, FP-08. EP-D5, BVA max+1.                                              |
| TC-FR17-013 | Create coupon with empty expired_at field                              | Negative | Domain Testing | High     | Admin is logged in. Code "NOEXPIRY" does not exist in the system.        | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Leave expired_at empty; fill remaining fields with valid values<br>5. Submit the form                                        | → ref: fr-17-data.json#TC-FR17-013 | Form rejected. Validation error displayed on the expired_at field indicating it is required. No coupon is created.                                                    | The system rejected the form and correctly displayed a validation error on the expired_at field across all 3 browsers. No coupon was created.  | Pass   | Maps to AC-13, FP-04. EP-E2.                                                         |
| TC-FR17-014 | Create coupon with negative min_order_amount                           | Negative | Domain Testing | High     | Admin is logged in. Code "NEGMIN" does not exist in the system.          | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter min_order_amount = -1; fill remaining fields with valid values<br>5. Submit the form                                   | → ref: fr-17-data.json#TC-FR17-014 | Form rejected. Validation error displayed on the min_order_amount field indicating it must be >= 0. No coupon is created.                                             | The system failed to reject the input and successfully created a coupon with a negative min_order_amount across all 3 browsers.                | Fail   | Maps to AC-08, FP-05. EP-M3, BVA min-1.                                              |
| TC-FR17-015 | Create coupon with max_uses_per_user equal to zero                     | Negative | Domain Testing | High     | Admin is logged in. Code "ZEROUSE" does not exist in the system.         | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter max_uses_per_user = 0; fill remaining fields with valid values<br>5. Submit the form                                   | → ref: fr-17-data.json#TC-FR17-015 | Form rejected. Validation error displayed on the max_uses_per_user field indicating it must be >= 1. No coupon is created.                                            | The system rejected the form and correctly displayed a validation error on the max_uses_per_user field across all 3 browsers.                  | Pass   | Maps to AC-09, FP-06. EP-U3, BVA min-1.                                              |
| TC-FR17-016 | Create coupon with code containing leading whitespace                  | Edge     | Error Guessing | Medium   | Admin is logged in. No coupon with code " NEWCODE" or "NEWCODE" exists.  | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter code " NEWCODE" (one leading space); fill remaining fields with valid values<br>5. Submit                              | → ref: fr-17-data.json#TC-FR17-016 | System either trims whitespace and creates coupon with code "NEWCODE", or rejects with a validation error. A coupon with a space-prefixed code must not be persisted. | The system successfully created and persisted a coupon with a code containing leading whitespace across all 3 browsers.                        | Fail   | EG-05. Tests whitespace handling in code field.                                      |
| TC-FR17-017 | Create coupon with non-numeric discount_value                          | Negative | Error Guessing | Medium   | Admin is logged in. Code "STRDISC" does not exist in the system.         | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter discount_value = "abc"; fill remaining fields with valid values<br>5. Submit the form                                  | → ref: fr-17-data.json#TC-FR17-017 | Form rejected. Validation error displayed on the discount_value field indicating it must be a number. No coupon is created.                                           | The system rejected the form and correctly displayed a validation error on the discount_value field across all 3 browsers. No coupon created.  | Pass   | EG-07. Tests type enforcement on numeric field.                                      |
| TC-FR17-018 | Create coupon with SQL injection string in code field                  | Edge     | Error Guessing | Low      | Admin is logged in. No coupon with the injection string as code exists.  | 1. Navigate to `http://localhost:5174` and log in as admin<br>2. Navigate to coupon management section<br>3. Click "Mã Giảm Giá"<br>4. Enter code = `'; DROP TABLE coupons; --`; fill remaining fields with valid values<br>5. Submit                               | → ref: fr-17-data.json#TC-FR17-018 | System safely handles the input — accepts it as a literal string or rejects with a validation error. The coupon table remains intact. No database error is exposed.   | The system safely handled the SQL injection input by rejecting it with a validation error without exposing any database errors.                | Pass   | EG-11. Functional scope only: verifies safe input handling, not security testing.    |
