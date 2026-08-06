<div align="center">
  <h1>Main Report — HW04 (Automation Testing)</h1>
  <small>
    <strong>Student:</strong> Nguyễn Tấn Phát — 23127449
  </small> <br />
  <sub>August 06, 2026</sub>
</div>

## 1. Introduction

This report details the automation testing effort for the EShop E-Commerce System Under Test (SUT). The objective was to design, automate, and execute a robust black-box functional test suite using Playwright and TypeScript, driven by AI agent collaboration.

**System Under Test (SUT):**

- Frontend Web App (`http://localhost:5173`)
- Frontend Admin App (`http://localhost:5174`)

**Features Selected for Automation:**

1. **FR-01:** Account Registration (Guest User, Web)
2. **FR-03:** Forgot Password & Password Reset (Guest User, Web)
3. **FR-17:** Coupon Management (Admin, Admin Panel)

## 2. Execution Summary

The test suite was executed locally across three major browser engines (Chromium, Firefox, WebKit). The table below summarizes the execution results:

| Metric                    | Value                   |
| ------------------------- | ----------------------- |
| **Features Automated**    | 3 (FR-01, FR-03, FR-17) |
| **Test Cases Automated**  | 58                      |
| **Test Cases Executed**   | 58                      |
| **Test Cases Passed**     | 30                      |
| **Test Cases Failed**     | 28                      |
| **Browser Runs**          | 174                     |
| **Total Bugs Identified** | 11                      |

## 3. AI Automation Review & Gap Analysis of Scripts

The automation lifecycle followed a strict process: `/wat-spec` (Specification) → `/wat-design` (Test Design) → `/wat-build` (Script Generation) → Human Review/Feedback → `/wat-report` (Bug Reporting).

While the AI demonstrated strong capabilities in generating boilerplate and applying Domain Testing techniques, significant human oversight was required.

### 3.1. General Observations & AI Limitations

- **UI Context Blindness:** Although the `playwright-mcp` (via the `ui-explorer` subagent) was utilized to pre-inspect the DOM, the AI still struggled to fully comprehend complex layouts and dynamic dialogs. Locators and custom validation messages frequently required human correction to ensure stability.
- **Context Window Saturation & Anti-Patterns:** Despite explicit guidelines against flaky patterns in the provided `playwright-skill`, the AI occasionally resorted to using `waitForTimeout` or attempted to hardcode `if/else` logic based on specific `TC-ID`s. This occurred most often when the conversation history grew long, indicating a loss of context.
- **API Context for Setup/Teardown:** The AI lacked intrinsic knowledge of the SUT's backend APIs needed for state isolation (e.g., deleting a created user or coupon). Human guidance was necessary to construct proper API requests and inject authentication tokens (`adminToken` from `localStorage`) into the Playwright `APIRequestContext`.

### 3.2. FR-01: Account Registration

- **Process Flow:** `wat-spec` → `wat-design` (18 TCs) → `wat-build` → Human Review → `wat-report`.
- **AI Review Analysis:** In this feature, the AI struggled the most with initial UI analysis. The first iteration of the scripts lacked correct locators and missed several user interactions. I had to manually interact with the UI and provide strict feedback loops to correct the Page Object Models. However, the AI successfully detected a critical SUT anomaly (the total absence of the "Confirm Password" field in certain DOM states) and, with human guidance, implemented a robust assertion to verify its absence without causing a flaky timeout crash.

### 3.3. FR-03: Forgot Password & Password Reset

- **Process Flow:** `wat-spec` → `wat-design` (22 TCs) → `wat-build` → Human Review → `wat-report`.
- **AI Review Analysis:** The AI produced highly stable scripts for individual components here. However, it struggled with the stateful transition between Step 1 (Email) and Step 2 (OTP & New Password). The AI initially wrote brittle logic that checked if input fields were empty to differentiate between Step 1 and Step 2 validation errors. This logic incorrectly failed tests designed to submit empty fields in Step 2. I had to instruct the AI to refactor the logic to dynamically parse the expected validation message via regex to accurately determine the current step context.

### 3.4. FR-17: Coupon Management (CRUD)

- **Process Flow:** `wat-spec` → `wat-design` (18 TCs) → `wat-build` → Human Review → `wat-report`.
- **AI Review Analysis:** This feature was the most prone to flakiness due to a poorly implemented UI in the SUT, which lacks success/failure toast notifications upon coupon creation. To verify creation, the script had to assert the presence of the new coupon in a data table. The AI instinctively utilized `waitForTimeout` to wait for the table to refresh, violating best practices. I forced the AI to analyze the specific DOM changes and replace the static timeouts with robust, event-driven assertions (e.g., waiting for row counts to increase or specific text nodes to attach to the DOM).

## 4. Gap Analysis: Non-Automated Scope

While 100% of the designed functional test cases were automated, certain aspects of the system fell into the "un-automatable" or "out-of-scope" category for this specific framework:

1. **External System Dependencies (Email Delivery):** In a production environment, FR-03 (Forgot Password) would require verifying that the OTP is actually delivered to the user's inbox. Because the test environment lacks a mocked SMTP server or external mail API integration, the automation script reads the OTP directly from a UI element (which the SUT exposes for debugging purposes).
2. **Visual & Accessibility Testing:** Strict visual regressions (e.g., verifying the exact hex color of error messages or layout alignments) and accessibility audits were intentionally excluded, as the scope of this assignment is strictly black-box functional testing.
