import { type Page } from "@playwright/test";

/**
 * BasePage — abstract base class for all Page Object Model classes.
 *
 * Provides shared navigation helpers available to every page.
 * All concrete POM classes extend this class.
 *
 * Design rules:
 *   - No assertions inside this class or any POM class.
 *   - No hardcoded URLs — all navigation uses relative paths with baseURL from config.
 *   - Locators are defined as readonly properties in concrete subclasses.
 *   - Do not call `waitForLoadState('networkidle')` — it is unreliable with React
 *     apps that maintain persistent WebSocket or polling connections. Use
 *     web-first assertions `expect(locator).toBeVisible()` in tests instead.
 */
export abstract class BasePage {
  constructor(protected readonly page: Page) {}

  /**
   * Navigate to a relative path within the application.
   * Playwright auto-waits for the load event after `goto()`.
   */
  async navigate(path: string): Promise<void> {
    await this.page.goto(path);
  }

  /**
   * Wait for the page URL to match a given string or pattern.
   * Use after form submissions or actions that trigger a redirect.
   */
  async waitForURL(urlPattern: string | RegExp): Promise<void> {
    await this.page.waitForURL(urlPattern);
  }

  /**
   * Return the current page title.
   * Useful for title-based assertions: `expect(await basePage.getTitle()).toBe('...')`
   */
  async getTitle(): Promise<string> {
    return this.page.title();
  }
}
