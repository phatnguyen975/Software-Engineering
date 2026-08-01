/**
 * Global teardown — runs once after the entire test suite completes.
 *
 * Design principle:
 *   Individual test fixtures are responsible for cleaning up the data they
 *   create (via the code after `await use(...)` in each fixture). This file
 *   acts as a safety net for cases where fixture teardown was skipped due to
 *   a test runner crash or forced termination.
 *
 * Current implementation:
 *   No global cleanup is required at this stage — all test data lifecycle
 *   is managed by individual fixtures. Add shared cleanup logic here only
 *   if seed data or shared resources are introduced in the future.
 */
async function globalTeardown(): Promise<void> {
  console.log("[global-teardown] Suite complete. No global cleanup required.");
}

export default globalTeardown;
