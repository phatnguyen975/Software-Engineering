import { defineConfig, devices } from "@playwright/test";
import dotenv from "dotenv";
import path from "path";

dotenv.config({ path: path.resolve(__dirname, ".env") });

const STUDENT_ID = process.env.STUDENT_ID ?? "UNKNOWN_STUDENT_ID";

/**
 * Playwright configuration — EShop SUT.
 *
 * Multi-project structure:
 *   web-chromium / web-firefox / web-webkit       → Frontend Web (WEB_BASE_URL), authenticated as standard user
 *   admin-chromium / admin-firefox / admin-webkit → Web Admin (ADMIN_BASE_URL), authenticated as admin user
 *
 * Auth strategy:
 *   global-setup.ts authenticates both roles via API and saves storageState to
 *   .auth/user.json and .auth/admin.json before any test runs. Each project
 *   loads the appropriate file so individual tests skip the login step entirely.
 */
export default defineConfig({
  // Maximum time a single test may run before it is marked as timed out
  timeout: 30_000,

  // Maximum time for expect() assertions to auto-retry before failing
  expect: { timeout: 5_000 },

  // Run tests within each file in parallel
  fullyParallel: true,

  // Prevent accidental test.only() from being committed on CI
  forbidOnly: !!process.env.CI,

  // Retry failed tests on CI to surface genuine failures vs. flakiness
  retries: process.env.CI ? 2 : 0,

  // Controlled concurrency on CI; let Playwright decide locally
  workers: process.env.CI ? 4 : undefined,

  // HTML report locally; list on CI for clean log output
  reporter: process.env.CI
    ? [["list"]]
    : [["html", { open: "never" }], ["./metadata-reporter.ts"]],

  // Global setup & teardown — run once before/after the entire test suite
  globalSetup: "./global-setup.ts",
  globalTeardown: "./global-teardown.ts",

  // Shared use settings
  use: {
    // Capture trace on first retry of a failed test
    trace: "on-first-retry",

    // Capture screenshot only when a test fails
    screenshot: "only-on-failure",

    // Record video only for failed tests
    video: "retain-on-failure",

    // Match the SUT's Vietnamese locale
    locale: "vi-VN",

    // Consistent date/time handling across environments
    timezoneId: "Asia/Ho_Chi_Minh",
  },

  // Multi-project structure — each project runs in its own isolated browser context
  projects: [
    // Frontend Web — standard user session
    {
      name: "web-chromium",
      testMatch: "tests/web/**/*.spec.ts",
      use: {
        ...devices["Desktop Chrome"],
        baseURL: process.env.WEB_BASE_URL ?? "http://localhost:5173",
        storageState: ".auth/user.json",
      },
    },
    {
      name: "web-firefox",
      testMatch: "tests/web/**/*.spec.ts",
      use: {
        ...devices["Desktop Firefox"],
        baseURL: process.env.WEB_BASE_URL ?? "http://localhost:5173",
        storageState: ".auth/user.json",
      },
    },
    {
      name: "web-webkit",
      testMatch: "tests/web/**/*.spec.ts",
      use: {
        ...devices["Desktop Safari"],
        baseURL: process.env.WEB_BASE_URL ?? "http://localhost:5173",
        storageState: ".auth/user.json",
      },
    },

    // Web Admin — admin session
    {
      name: "admin-chromium",
      testMatch: "tests/admin/**/*.spec.ts",
      use: {
        ...devices["Desktop Chrome"],
        baseURL: process.env.ADMIN_BASE_URL ?? "http://localhost:5174",
        storageState: ".auth/admin.json",
      },
    },
    {
      name: "admin-firefox",
      testMatch: "tests/admin/**/*.spec.ts",
      use: {
        ...devices["Desktop Firefox"],
        baseURL: process.env.ADMIN_BASE_URL ?? "http://localhost:5174",
        storageState: ".auth/admin.json",
      },
    },
    {
      name: "admin-webkit",
      testMatch: "tests/admin/**/*.spec.ts",
      use: {
        ...devices["Desktop Safari"],
        baseURL: process.env.ADMIN_BASE_URL ?? "http://localhost:5174",
        storageState: ".auth/admin.json",
      },
    },
  ],
});
