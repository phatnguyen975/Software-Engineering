# Test Case Template

Each test case in the test case table uses the following column set. Render as a Markdown table row. Multi-line fields use `<br>` as the line separator.

## Column Definitions

| Column              | Required | Rules                                                                                           |
| ------------------- | -------- | ----------------------------------------------------------------------------------------------- |
| **TC-ID**           | Yes      | Format: `TC-FR{XX}-{NNN}`. Assign only after deduplication. Sequential, no gaps.                |
| **Title**           | Yes      | Format: `Action + Function + Operating Condition`. Must be unique within the suite.             |
| **Type**            | Yes      | One of: `Positive` / `Negative` / `Edge`                                                        |
| **Technique**       | Yes      | One of: `Domain Testing` / `Error Guessing`                                                     |
| **Priority**        | Yes      | `High` (blocks core flow) / `Medium` (important variation) / `Low` (edge case, low probability) |
| **Preconditions**   | Yes      | System/data state required before execution. "None" if no preconditions.                        |
| **Test Steps**      | Yes      | Numbered steps separated by `<br>`. Begin each step with `{N}.`                                 |
| **Input Data**      | Yes      | `→ ref: fr-{xx}-data.json#{TC-ID}` — never inline values                                        |
| **Expected Result** | Yes      | Specific, observable, falsifiable. Name the exact message, field, URL, or state change.         |
| **Actual Result**   | Yes      | Leave blank — filled after execution                                                            |
| **Status**          | Yes      | Leave blank — filled after execution                                                            |
| **Notes**           | No       | Optional. Clarifications, known risks, or test environment specifics.                           |

## Example Row

| TC-ID       | Title                            | Type     | Technique      | Priority | Preconditions                                                        | Test Steps                                                                                                                                         | Input Data                         | Expected Result                                                                                                                                                       | Actual Result | Status | Notes |
| ----------- | -------------------------------- | -------- | -------------- | -------- | -------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------- | ------ | ----- |
| TC-FR01-001 | Register account with valid data | Positive | Domain Testing | High     | User is not logged in. No account exists for the test email address. | 1. Navigate to `/register`<br>2. Enter Full Name<br>3. Enter Email<br>4. Enter Password<br>5. Enter matching Confirm Password<br>6. Click Register | → ref: fr-01-data.json#TC-FR01-001 | System creates the account, dispatches a verification email, and redirects the user to `/login` with the message "Registration successful. Please verify your email." |               |        |       |

## Title Format — Action + Function + Operating Condition

| Component           | Definition                               | Examples                                                                        |
| ------------------- | ---------------------------------------- | ------------------------------------------------------------------------------- |
| Action              | The verb describing what the actor does  | Register, Submit, Add, Remove, Update, Delete, Login                            |
| Function            | The feature or object being acted upon   | account, cart item, coupon, password                                            |
| Operating Condition | The distinguishing context or data state | with valid data, with duplicate email, with empty password, at maximum quantity |

**Correct examples:**

- `Register account with valid data`
- `Register account with duplicate email address`
- `Add item to cart with maximum quantity`
- `Create coupon with already-used coupon code`

**Incorrect examples:**

- `Test registration` — missing condition
- `Valid registration` — missing action verb
- `TC-001 positive case` — not descriptive
