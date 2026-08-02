# Feature Specification — FR-17: Coupon Management (CRUD)

> **Source SRS:** `docs/system-requirements-specification.md`  
> **Generated:** 2026-08-02  
> **Status:** APPROVED

## 1. Feature Overview

| Field                   | Value                                                                                  |
| ----------------------- | -------------------------------------------------------------------------------------- |
| FR ID                   | FR-17                                                                                  |
| Feature Name            | Coupon Management (CRUD)                                                               |
| Primary Actor           | Admin                                                                                  |
| Secondary Actors        | None                                                                                   |
| Authentication Required | Yes — valid JWT Token with `role = "admin"`                                            |
| Entry Point             | Web Admin panel at `http://localhost:5174` (coupon management section)                 |
| Actor Goal              | Create, view, and delete discount coupons to control promotional pricing in the e-shop |

## 2. Input Fields & Constraints

| Field Name        | Data Type | Required | Min  | Max              | Format / Pattern    | Allowed Values     | Notes                                                     |
| ----------------- | --------- | -------- | ---- | ---------------- | ------------------- | ------------------ | --------------------------------------------------------- |
| code              | string    | Yes      | —    | —                | —                   | —                  | Must be unique across all coupons in the system           |
| type              | enum      | Yes      | —    | —                | —                   | `percent`, `fixed` | Determines the discount calculation formula               |
| discount_value    | number    | Yes      | > 0  | 100 (if percent) | Positive number     | —                  | For `percent` type: max 100; for `fixed` type: no max     |
| expired_at        | date      | Yes      | —    | —                | Date format         | —                  | Coupon is valid only before this date                     |
| min_order_amount  | number    | Yes      | >= 0 | —                | Non-negative number | —                  | Minimum order total required to apply the coupon          |
| max_uses_per_user | integer   | Yes      | >= 1 | —                | Positive integer    | —                  | Maximum number of times a single user can use this coupon |

## 3. Business Rules

**BR-01:** The Admin must be authenticated with a valid JWT Token containing `role = "admin"` to access coupon management operations (Create, Read, Delete).

> **Source:** SRS §FR-12 — "All Admin APIs (`/api/admin/*`) and data-modifying APIs (`POST/PUT/DELETE /api/coupons`) require: (1) valid JWT Token, (2) `role = "admin"` in Token."

**BR-02:** The Admin can perform three operations on coupons: Create (Add), Read (View list), and Delete.

> **Source:** SRS §FR-17 — "Admin có thể Thêm / Xem / Xóa mã giảm giá."

**BR-03:** The coupon `code` field is required and must be unique across all coupons in the system.

> **Source:** SRS §FR-17 — "Các trường bắt buộc: `code` (duy nhất)"

**BR-04:** The coupon `type` field is required and must be one of two allowed values: `percent` or `fixed`.

> **Source:** SRS §FR-17 — "Các trường bắt buộc: … `type` (percent/fixed)"

**BR-05:** The `discount_value` field is required and must be a positive number (strictly greater than zero). If `type` is `percent`, `discount_value` must not exceed 100.

> **Source:** SRS §FR-17 — "Các trường bắt buộc: … `discount_value` (dương)" (Max 100 rule added via human feedback).

**BR-06:** The `expired_at` field is required.

> **Source:** SRS §FR-17 — "Các trường bắt buộc: … `expired_at`"

**BR-07:** The `min_order_amount` field is required and must be greater than or equal to zero.

> **Source:** SRS §FR-17 — "Các trường bắt buộc: … `min_order_amount` (>= 0)"

**BR-08:** The `max_uses_per_user` field is required and must be greater than or equal to 1.

> **Source:** SRS §FR-17 — "Các trường bắt buộc: … `max_uses_per_user` (>= 1)"

**BR-09:** The SRS defines only Create, Read, and Delete operations for coupons. The Update (Edit) operation is not specified for FR-17.

> **Source:** SRS §FR-17 — "Admin có thể Thêm / Xem / Xóa mã giảm giá." (No mention of Edit/Update.)

**BR-10:** Deleting a coupon requires a confirmation dialog before the action is executed.

> **Source:** Standard default behavior.

**BR-11:** Upon successful creation, the coupon is automatically set to active state (`is_active = 1`).

> **Source:** Resolving `is_active` dependency for FR-09.

## 4. Success Paths

### SP-01: Successfully Create a New Coupon

```
Actor:   Logs in to the Admin panel with valid admin credentials
System:  Authenticates the admin and grants access to the admin dashboard
Actor:   Navigates to the coupon management section
System:  Displays the coupon list page with existing coupons
Actor:   Initiates the "Add new coupon" action
System:  Displays the coupon creation form with fields: code, type, discount_value, expired_at, min_order_amount, max_uses_per_user
Actor:   Fills in all required fields with valid values (unique code, valid type, positive discount_value, valid expired_at, non-negative min_order_amount, max_uses_per_user >= 1)
Actor:   Submits the form
System:  Validates all fields against constraints (BR-03 through BR-08)
System:  Creates the new coupon in the database and automatically sets it as active (is_active = 1) (BR-11)
System:  Displays confirmation of successful creation
Outcome: The new coupon appears in the coupon list; it is available for use in checkout
```

### SP-02: View Coupon List

```
Actor:   Logs in to the Admin panel with valid admin credentials
System:  Authenticates the admin and grants access
Actor:   Navigates to the coupon management section
System:  Displays the list of all existing coupons with their details (code, type, discount_value, expired_at, min_order_amount, max_uses_per_user)
Outcome: Admin can see all coupons currently in the system
```

### SP-03: Successfully Delete a Coupon

```
Actor:   Logs in to the Admin panel with valid admin credentials
System:  Authenticates the admin and grants access
Actor:   Navigates to the coupon management section
System:  Displays the coupon list
Actor:   Selects a coupon and initiates the delete action
System:  Displays a confirmation dialog (BR-10)
Actor:   Confirms the deletion
System:  Removes the coupon from the database
System:  Updates the coupon list to reflect the deletion
Outcome: The deleted coupon no longer appears in the list and cannot be used in checkout
```

## 5. Failure Paths

### FP-01: Duplicate Coupon Code

```
Trigger: The submitted coupon code already exists in the system (BR-03)
Actor:   Fills in the coupon creation form with a code that already exists
Actor:   Submits the form
System:  Validates the code against existing coupon records
System:  Rejects the submission and displays an error indicating the code is not unique
Outcome: No coupon is created; the form remains displayed with the error message
```

### FP-02: Invalid Coupon Type

```
Trigger: The type field contains a value other than 'percent' or 'fixed' (BR-04)
Actor:   Attempts to submit the form with an invalid type value
System:  Validates the type field against allowed values
System:  Rejects the submission and displays a validation error
Outcome: No coupon is created; the actor sees a validation error on the type field
```

### FP-03: Non-Positive Discount Value

```
Trigger: The discount_value field is zero, negative, or not a number (BR-05)
Actor:   Submits the form with discount_value <= 0 or non-numeric
System:  Validates the discount_value against the positive-number constraint
System:  Rejects the submission and displays a validation error
Outcome: No coupon is created; the actor sees a validation error on the discount_value field
```

### FP-04: Missing Required Fields

```
Trigger: One or more required fields are left empty on submission (BR-03 through BR-08)
Actor:   Submits the form with one or more required fields blank
System:  Validates presence of all required fields
System:  Displays validation errors for each empty required field
Outcome: No coupon is created; all errors are shown
```

### FP-05: Negative Min Order Amount

```
Trigger: The min_order_amount field is a negative number (BR-07)
Actor:   Submits the form with min_order_amount < 0
System:  Validates min_order_amount against the >= 0 constraint
System:  Rejects the submission and displays a validation error
Outcome: No coupon is created; the actor sees a validation error on the min_order_amount field
```

### FP-06: Max Uses Per User Less Than 1

```
Trigger: The max_uses_per_user field is zero or negative (BR-08)
Actor:   Submits the form with max_uses_per_user < 1
System:  Validates max_uses_per_user against the >= 1 constraint
System:  Rejects the submission and displays a validation error
Outcome: No coupon is created; the actor sees a validation error on the max_uses_per_user field
```

### FP-07: Unauthenticated Access Attempt

```
Trigger: A user without a valid admin JWT Token attempts to access coupon management (BR-01)
Actor:   Attempts to access the coupon management section without logging in or with a non-admin account
System:  Detects missing or invalid admin credentials
System:  Denies access and redirects to the login page or displays an unauthorized error
Outcome: The coupon management section is not accessible; no CRUD operations can be performed
```

### FP-08: Percent Discount Value Exceeds 100

```
Trigger: The type is 'percent' and discount_value > 100 (BR-05)
Actor:   Submits the form with type 'percent' and discount_value > 100
System:  Validates discount_value against the max 100 constraint for percent type
System:  Rejects the submission and displays a validation error
Outcome: No coupon is created; the actor sees a validation error on the discount_value field
```

## 6. Acceptance Criteria

### AC-01: Admin can create a coupon with all valid fields

```
Given  an admin user is logged in to the Admin panel
When   the admin fills in the coupon creation form with a unique code, valid type (percent or fixed), positive discount_value, valid expired_at date, min_order_amount >= 0, and max_uses_per_user >= 1, then submits the form
Then   the system creates the coupon and it appears in the coupon list
```

_Maps to: BR-02, BR-03, BR-04, BR-05, BR-06, BR-07, BR-08, SP-01_

### AC-02: Admin can view the list of all coupons

```
Given  an admin user is logged in to the Admin panel
When   the admin navigates to the coupon management section
Then   the system displays a list of all existing coupons with their details
```

_Maps to: BR-02, SP-02_

### AC-03: Admin can delete an existing coupon after confirmation

```
Given  an admin user is logged in to the Admin panel and at least one coupon exists
When   the admin selects a coupon, initiates the delete action, and confirms the deletion in the dialog
Then   the system removes the coupon from the database and it no longer appears in the coupon list
```

_Maps to: BR-02, BR-10, SP-03_

### AC-04: Duplicate coupon code is rejected

```
Given  an admin user is on the coupon creation form and a coupon with code "EXISTING_CODE" already exists
When   the admin submits the form with code "EXISTING_CODE"
Then   the system displays an error indicating the code must be unique and does not create the coupon
```

_Maps to: BR-03, FP-01_

### AC-05: Invalid coupon type is rejected

```
Given  an admin user is on the coupon creation form
When   the admin submits the form with a type value other than 'percent' or 'fixed'
Then   the system displays a validation error and does not create the coupon
```

_Maps to: BR-04, FP-02_

### AC-06: Non-positive discount value is rejected

```
Given  an admin user is on the coupon creation form
When   the admin submits the form with discount_value of zero or a negative number
Then   the system displays a validation error on the discount_value field and does not create the coupon
```

_Maps to: BR-05, FP-03_

### AC-07: Missing required fields produce validation errors

```
Given  an admin user is on the coupon creation form
When   the admin submits the form with one or more required fields left blank
Then   the system displays validation errors for each empty required field and does not create the coupon
```

_Maps to: BR-03, BR-04, BR-05, BR-06, BR-07, BR-08, FP-04_

### AC-08: Negative min_order_amount is rejected

```
Given  an admin user is on the coupon creation form
When   the admin submits the form with min_order_amount set to a negative value
Then   the system displays a validation error on the min_order_amount field and does not create the coupon
```

_Maps to: BR-07, FP-05_

### AC-09: max_uses_per_user less than 1 is rejected

```
Given  an admin user is on the coupon creation form
When   the admin submits the form with max_uses_per_user set to 0 or a negative number
Then   the system displays a validation error on the max_uses_per_user field and does not create the coupon
```

_Maps to: BR-08, FP-06_

### AC-10: Non-admin users cannot access coupon management

```
Given  a user is not authenticated, or is authenticated with a non-admin role
When   the user attempts to access the coupon management section of the Admin panel
Then   the system denies access and does not display coupon management functionality
```

_Maps to: BR-01, FP-07_

### AC-11: Coupon with type 'percent' is created correctly

```
Given  an admin user is on the coupon creation form
When   the admin creates a coupon with type set to 'percent' and all other fields valid
Then   the system creates the coupon with type 'percent' and it appears in the coupon list with the correct type
```

_Maps to: BR-04, SP-01_

### AC-12: Coupon with type 'fixed' is created correctly

```
Given  an admin user is on the coupon creation form
When   the admin creates a coupon with type set to 'fixed' and all other fields valid
Then   the system creates the coupon with type 'fixed' and it appears in the coupon list with the correct type
```

_Maps to: BR-04, SP-01_

### AC-13: expired_at field is required

```
Given  an admin user is on the coupon creation form
When   the admin submits the form with all fields filled except expired_at
Then   the system displays a validation error for the expired_at field and does not create the coupon
```

_Maps to: BR-06, FP-04_

### AC-14: Percent discount value exceeding 100 is rejected

```
Given  an admin user is on the coupon creation form
When   the admin creates a coupon with type set to 'percent' and discount_value > 100
Then   the system displays a validation error on the discount_value field and does not create the coupon
```

_Maps to: BR-05, FP-08_

### AC-15: Newly created coupon is active by default

```
Given  an admin user is on the coupon creation form
When   the admin successfully creates a new coupon
Then   the system automatically sets the coupon's active state (is_active = 1)
```

_Maps to: BR-11, SP-01_

## 7. Out of Scope

- Does not cover the Update (Edit) operation for existing coupons — the SRS specifies only Create, Read, and Delete for FR-17.
- Does not cover coupon application at checkout — applying coupons during checkout is covered by FR-09.
- Does not cover the coupon discount calculation formula (`percent` vs `fixed` computation) — this is defined in FR-09.
- Does not cover the 5-condition coupon validation at checkout (code existence, expiry check, order threshold, authentication, usage limit) — these are defined in FR-09.
- Does not cover UI layout, typography, colour scheme, or responsive design concerns (FR-21 / GUI testing is out of scope).
- Does not cover security testing such as JWT tampering, SQL injection, or brute-force attacks (SEC-01 through SEC-07 are out of scope).
- Does not cover performance testing under concurrent admin operations.
- Does not cover accessibility testing.

## 8. Dependencies

| Dependency             | Type        | Hard / Soft | Notes                                                                                                                             |
| ---------------------- | ----------- | ----------- | --------------------------------------------------------------------------------------------------------------------------------- |
| FR-12 (Access Control) | FR          | Hard        | Admin authentication with JWT Token and `role = "admin"` is a prerequisite for all coupon management operations                   |
| Admin User Account     | Data        | Hard        | A pre-existing admin account (`admin@eshop.com` / `Admin123!`) must exist for login; defined as a default in the SRS              |
| Coupon Database Table  | Data        | Hard        | Must be accessible and writable; pre-seeded coupons (`SAVE10`, `BIGBUY`, `VIP100`, `EXPIRED`) are available for Read/Delete tests |
| Web Admin Application  | Environment | Hard        | The Admin panel at `http://localhost:5174` must be running and accessible                                                         |
| Backend API            | System      | Hard        | The backend API at `http://localhost:3000` must be running to process CRUD operations                                             |

## 9. Test Notes

- **Seed data:** The SRS defines four pre-seeded coupons in the system: `SAVE10` (percent, 10%, min 300,000₫), `BIGBUY` (fixed, 50,000₫, min 500,000₫), `VIP100` (fixed, 100,000₫, min 300,000₫), and `EXPIRED` (percent, 20%, expired 2020-01-01). These can be used for Read and Delete test cases. Note: these coupons are defined in §FR-09 and may or may not be present in the admin coupon list — verify during UI exploration.
- **Environment:** Both the Backend API (`http://localhost:3000`) and the Web Admin (`http://localhost:5174`) must be running. Admin login credentials: `admin@eshop.com` / `Admin123!`.
- **Risks:**
  - The SRS title says "CRUD" but the body text only specifies Create, Read, and Delete (no Update). This inconsistency should be noted — test design should cover only the three operations explicitly described.
  - The SRS does not specify maximum value constraints for `min_order_amount` or `max_uses_per_user`. Boundary values will need to be explored during test design. (Note: Human feedback confirmed no max length for `code`, and `discount_value` max 100 is applied only when type is `percent`).
  - The SRS does not specify the exact date format for `expired_at` — the UI implementation determines this.
  - The human has confirmed that deleting a coupon requires a confirmation dialog (standard behavior) and that newly created coupons are active by default (`is_active = 1`).
- **Open questions:** None.
