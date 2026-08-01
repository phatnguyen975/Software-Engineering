import { chromium, request } from "@playwright/test";
import dotenv from "dotenv";
import path from "path";
import fs from "fs";

dotenv.config({ path: path.resolve(__dirname, ".env") });

const AUTH_DIR = path.resolve(__dirname, ".auth");
const USER_STATE_PATH = path.resolve(AUTH_DIR, "user.json");
const ADMIN_STATE_PATH = path.resolve(AUTH_DIR, "admin.json");

const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:3000";
const WEB_BASE_URL = process.env.WEB_BASE_URL ?? "http://localhost:5173";
const ADMIN_BASE_URL = process.env.ADMIN_BASE_URL ?? "http://localhost:5174";

/**
 * Global setup — runs once before the entire test suite.
 *
 * Authenticates each role via direct API call (POST /api/login) and injects
 * the returned JWT into the browser context's localStorage, then persists the
 * full storageState to `.auth/`. All test projects load these files via the
 * `storageState` option in `playwright.config.ts`, so no test ever performs a
 * manual UI login.
 *
 * Tests that must start unauthenticated (e.g. registration) override
 * storageState at the test level:
 * `test.use({ storageState: { cookies: [], origins: [] } });`
 */
export default async function globalSetup(): Promise<void> {
  // Ensure .auth/ directory exists before writing state files
  if (!fs.existsSync(AUTH_DIR)) {
    fs.mkdirSync(AUTH_DIR, { recursive: true });
  }

  await authenticateAndSave({
    apiBaseURL: API_BASE_URL,
    appBaseURL: WEB_BASE_URL,
    email: process.env.USER_EMAIL ?? "test@eshop.com",
    password: process.env.USER_PASSWORD ?? "Test1234!",
    stateFile: USER_STATE_PATH,
    localStorageKey: "token", // frontend-web stores JWT under 'token'
    label: "standard-user",
  });

  await authenticateAndSave({
    apiBaseURL: API_BASE_URL,
    appBaseURL: ADMIN_BASE_URL,
    email: process.env.ADMIN_EMAIL ?? "admin@eshop.com",
    password: process.env.ADMIN_PASSWORD ?? "Admin123!",
    stateFile: ADMIN_STATE_PATH,
    localStorageKey: "adminToken", // frontend-admin stores JWT under 'adminToken'
    label: "admin-user",
  });
}

/**
 * Obtains a JWT via direct API login, injects it into a browser context's
 * `localStorage`, and persists the resulting storageState to disk.
 *
 * `localStorage` injection is necessary because both frontends initialise their
 * auth state from `localStorage` on page load. Simply setting a cookie is not
 * sufficient for these React SPA applications.
 */
async function authenticateAndSave(options: {
  apiBaseURL: string;
  appBaseURL: string;
  email: string;
  password: string;
  stateFile: string;
  localStorageKey: string;
  label: string;
}): Promise<void> {
  const {
    apiBaseURL,
    appBaseURL,
    email,
    password,
    stateFile,
    localStorageKey,
    label,
  } = options;

  console.log(`[global-setup] Authenticating ${label} (${email})...`);

  // Step 1: Obtain JWT via API — no browser needed for this step
  const apiContext = await request.newContext({ baseURL: apiBaseURL });
  const loginResponse = await apiContext.post("/api/login", {
    data: { email, password },
  });

  if (!loginResponse.ok()) {
    const body = await loginResponse.text();
    throw new Error(
      `[global-setup] Login failed for ${label} (${email}). ` +
        `Status: ${loginResponse.status()}. Body: ${body}`,
    );
  }

  const { token } = (await loginResponse.json()) as { token: string };
  await apiContext.dispose();

  // Step 2: Inject token into browser localStorage so the SPA recognises
  // the session, then capture the full storageState (cookies + localStorage)
  const browser = await chromium.launch();
  const context = await browser.newContext({ baseURL: appBaseURL });
  const page = await context.newPage();

  // Navigate to the app root to initialise the origin in the browser context
  await page.goto("/");

  await page.evaluate(
    ({ jwtToken, key }: { jwtToken: string; key: string }) => {
      localStorage.setItem(key, jwtToken);
    },
    { jwtToken: token, key: localStorageKey },
  );

  // Reload so the SPA picks up the token and transitions to the authenticated state
  await page.reload();

  await context.storageState({ path: stateFile });
  console.log(`[global-setup] ✓ ${label} storageState saved to ${stateFile}`);

  await browser.close();
}
