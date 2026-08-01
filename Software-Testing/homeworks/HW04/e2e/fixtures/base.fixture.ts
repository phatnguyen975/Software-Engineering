import { test as base, expect } from "@playwright/test";
import { authFixtures, type AuthFixtures } from "./auth.fixture";

/**
 * Central fixture entry point for the entire test suite.
 *
 * All spec files import `test` and `expect` exclusively from this module —
 * never directly from `@playwright/test`. This ensures every fixture and
 * future extension flows through a single controlled entry point.
 *
 * Fixtures are implemented in dedicated `*.fixture.ts` files and merged here.
 * To add a new fixture:
 *   1. Create `fixtures/<name>.fixture.ts` — implement and export the fixture
 *      functions and its type.
 *   2. Import and merge it into the `extend()` call below.
 *   3. Add the type to the generic parameter list.
 */
export const test = base.extend<AuthFixtures>({
  ...authFixtures,
});

export { expect };
