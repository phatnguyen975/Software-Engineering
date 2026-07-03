# AI Critique — HW02 Domain Testing

## Where AI Got It Wrong or Incomplete

During the testing of FR-03, Antigravity exhibited a strong web-centric bias, incorrectly applying HTML DOM attributes (`type="email"`, `type="password"`) instead of their React Native equivalents (`keyboardType="email-address"`, `secureTextEntry`). Furthermore, the AI struggled with execution environments, specifically hallucinating database schemas by querying non-existent tables like `password_resets` or `cart_items` in bash scripts. It also failed to isolate test state, leading to RAM-based state pollution during FR-07 cart testing, and made mathematical arithmetic errors when calculating exact string lengths for boundary value analysis (BVA).

## Why AI Failed to Catch the Issue

These failures highlight inherent LLM limitations. The web-centric bias stems from the model's probabilistic training, where standard web development patterns heavily outweigh React Native syntax. Without explicit schema mapping, the AI relies on popular framework conventions (like Laravel or Django defaults) to hallucinate table names. Additionally, test isolation failures occurred because the AI processes testing logic purely as static text; it lacks an intrinsic understanding of live execution race conditions, state persistence, or bash escaping edge cases for extreme boundary JSON payloads.

## Lesson Learned About AI Collaboration

The primary principle derived from this collaboration is "Zero-Trust Verification." While AI excels at generating comprehensive Equivalence Partitions, its execution scripts and cross-platform assumptions must never be trusted blindly. Effective collaboration requires "Constraint-based Prompting": explicitly defining environmental rules upfront (e.g., "This is React Native, use explicit props") and enforcing strict test isolation mechanisms (e.g., a "Fresh Product ID per TC" strategy). Ultimately, the human tester must act as the execution architect, bridging the gap between the AI’s theoretical test design and the SUT’s literal runtime environment.
