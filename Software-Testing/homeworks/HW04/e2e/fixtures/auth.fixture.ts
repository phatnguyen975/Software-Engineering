import { type Browser } from "@playwright/test";
import path from "path";

const USER_STATE = path.resolve(__dirname, "../.auth/user.json");
const ADMIN_STATE = path.resolve(__dirname, "../.auth/admin.json");

export type AuthFixtures = {
  /**
   * A Page pre-loaded with the standard user's storageState.
   *
   * The session originates from `global-setup.ts` and is loaded from
   * `.auth/user.json` into a fresh browser context per test, ensuring
   * full isolation between tests.
   *
   * Use this fixture in tests that require an authenticated user session.
   * Tests that must start unauthenticated (e.g. registration) should NOT
   * request this fixture; use the default `page` fixture instead and call:
   * `test.use({ storageState: { cookies: [], origins: [] } });`
   */
  userPage: Awaited<ReturnType<Browser["newPage"]>>;

  /**
   * A Page pre-loaded with the admin user's storageState.
   *
   * Identical lifecycle to `userPage` but loads `.auth/admin.json`.
   * Use this fixture in tests that require an authenticated admin session.
   */
  adminPage: Awaited<ReturnType<Browser["newPage"]>>;
};

export const authFixtures = {
  userPage: async (
    { browser }: { browser: Browser },
    use: (page: Awaited<ReturnType<Browser["newPage"]>>) => Promise<void>,
  ) => {
    const context = await browser.newContext({ storageState: USER_STATE });
    const page = await context.newPage();
    await use(page);
    await context.close();
  },

  adminPage: async (
    { browser }: { browser: Browser },
    use: (page: Awaited<ReturnType<Browser["newPage"]>>) => Promise<void>,
  ) => {
    const context = await browser.newContext({ storageState: ADMIN_STATE });
    const page = await context.newPage();
    await use(page);
    await context.close();
  },
};
