# Data File Schema — wat-design

The test data file (`fr-{xx}-data.json`) stores input values for every test case in the suite. Automation scripts load this file at runtime and iterate over its entries.

## Schema

```json
[
  {
    "tc_id": "TC-FR{XX}-{NNN}",
    "description": "Short human-readable label matching the TC Title",
    "inputs": {
      "{fieldName}": "{value}"
    },
    "expected": {
      "outcome": "success | error",
      "message": "Exact observable message or state — matches TC Expected Result",
      "redirect": "/path (include only if the TC verifies a URL change)"
    }
  }
]
```

## Field Rules

| Field               | Type   | Rules                                                                                                                                                         |
| ------------------- | ------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `tc_id`             | string | Must match the TC-ID in the test case document exactly                                                                                                        |
| `description`       | string | Should mirror the TC Title for traceability                                                                                                                   |
| `inputs`            | object | One key per input field. Keys use camelCase. Values are the representative input for this TC. Use `""` for empty string, `null` for null, never omit a field. |
| `expected.outcome`  | string | `"success"` for Positive TCs; `"error"` for Negative and most Edge TCs                                                                                        |
| `expected.message`  | string | The exact text the automation script will assert against. Must match `Expected Result` in the TC table.                                                       |
| `expected.redirect` | string | Include only when the TC verifies a URL redirect. Omit otherwise.                                                                                             |

## Naming Convention

Input field keys use **camelCase** derived from the visible label:

| Visible Label    | JSON Key          |
| ---------------- | ----------------- |
| Full Name        | `fullName`        |
| Email            | `email`           |
| Password         | `password`        |
| Confirm Password | `confirmPassword` |
| Coupon Code      | `couponCode`      |
| Discount Type    | `discountType`    |

## Example Entry

```json
{
  "tc_id": "TC-FR01-001",
  "description": "Register account with valid data",
  "inputs": {
    "fullName": "Nguyen Van A",
    "email": "validuser@example.com",
    "password": "ValidPass1",
    "confirmPassword": "ValidPass1"
  },
  "expected": {
    "outcome": "success",
    "message": "Registration successful. Please verify your email.",
    "redirect": "/login"
  }
}
```

## Constraints

- Every TC-ID in the test case document must have a corresponding entry in this file.
- No inline values in the test case table — the TC table references this file via `→ ref: fr-{xx}-data.json#{TC-ID}`.
- Values must reflect the **exact** representative chosen during EP analysis — not a randomly chosen value that happens to be valid.
- For fields not relevant to a particular TC (e.g. a TC that focuses on the email field), still include the other fields with their default valid representative values.
