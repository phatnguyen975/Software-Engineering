# AI Critique

During the automation of the E-Commerce SUT (FR-01, FR-03, FR-17), the AI demonstrated strong capabilities in applying Domain Testing techniques and generating boilerplate Playwright code, but it also exhibited several critical blind spots.

**Where did the AI get something wrong, biased, or incomplete?**

The AI frequently struggled with application-specific context and architectural nuances that were not explicitly spelled out in the prompt. For instance, in FR-03, it assumed Playwright's `APIRequestContext` would automatically include JWT tokens stored in `localStorage` from the `storageState`, causing teardown requests to fail with a `401 Unauthorized` error. Additionally, the AI occasionally wrote brittle logic; it attempted to distinguish between Step 1 and Step 2 validation errors by checking if input fields were empty. This incorrectly flagged a valid "Step 2 empty-submission" test case as a Step 1 error, stalling the test suite.

**Why did it fail to catch the issue?**

The AI operates strictly on the textual data and code patterns provided to it, lacking the holistic "common sense" of how a specific React SPA manages state (like JWTs in `localStorage` vs. cookies). Furthermore, it tends to over-optimize code logic (e.g., using input emptiness to branch test logic) without fully simulating edge cases, such as tests explicitly designed to leave fields empty. It cannot intuitively debug a failure without human intervention guiding it to the exact point of logical conflict.

**What principle have you learned about collaborating with AI during this assignment?**

Collaborating with AI requires strict oversight, precise context injection, and acting as a meticulous reviewer. While AI excels at generating test data, applying equivalence partitioning, and formatting reports, it cannot be blindly trusted with complex state management or cross-step dependencies. The human engineer must enforce boundary conditions, provide architectural context, and constantly verify the AI's logical assumptions against the actual system behavior.
