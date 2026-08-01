# Examples — wat-build

Illustrative code snippets for the three output types produced by this skill. Each section shows the essential structure with GOOD / BAD comparisons. These are excerpts — not runnable files.

## 1. Page Object Model (`*.page.ts`)

A POM class encapsulates locators and interaction methods for one screen. It extends `BasePage`, exposes locators as getters, and exposes actions as async methods. **No assertions inside the POM.**

### Structure

```typescript
import { type Page, type Locator } from "@playwright/test";
import { BasePage } from "../base.page";

export class RegisterPage extends BasePage {
  readonly url = "/register";

  // Locator getters — noun names, semantic selectors
  get emailInput(): Locator {
    return this.page.getByLabel(/email/i);
  }
  get submitButton(): Locator {
    return this.page.getByRole("button", { name: /register/i });
  }
  get emailError(): Locator {
    return this.page.getByRole("alert").filter({ hasText: /email/i });
  }

  // Action methods — verb names, no assertions
  async fillForm(data: {
    fullName: string;
    email: string;
    password: string;
    confirmPassword: string;
  }): Promise<void> {
    await this.page.getByLabel(/full name/i).fill(data.fullName);
    await this.emailInput.fill(data.email);
    await this.page.getByLabel(/^password$/i).fill(data.password);
    await this.page.getByLabel(/confirm password/i).fill(data.confirmPassword);
  }

  async submit(): Promise<void> {
    await this.submitButton.click();
  }
}
```

### GOOD vs BAD

```typescript
// ✅ GOOD — semantic locator, resilient to styling changes
get submitButton(): Locator {
  return this.page.getByRole("button", { name: /register/i });
}

// ❌ BAD — CSS class breaks when styling is refactored
get submitButton(): Locator {
  return this.page.locator(".btn-primary");
}
```

```typescript
// ✅ GOOD — action method does one thing, no assertion
async submit(): Promise<void> {
  await this.submitButton.click();
}

// ❌ BAD — assertion inside POM couples interaction with expectation
async submitAndAssert(): Promise<void> {
  await this.submitButton.click();
  await expect(this.page).toHaveURL("/login"); // ← never assert in POM
}
```

## 2. Spec File (`fr-{xx}-{feature}.spec.ts`)

### Essential structure

```typescript
import { test, expect } from "@fixtures/base.fixture"; // ← always from fixture, not @playwright/test
import { RegisterPage } from "@pages/web/register.page";
import { loadTestData } from "@helpers/data-loader";

// Auth override (unauthenticated features only)
test.use({ storageState: { cookies: [], origins: [] } });

// Type definition for data records
interface RegistrationCase {
  tc_id: string;
  description: string;
  inputs: {
    fullName: string;
    email: string;
    password: string;
    confirmPassword: string;
  };
  expected: {
    outcome: "success" | "error";
    message: string;
    redirect?: string;
  };
}

const cases = loadTestData<RegistrationCase>("fr-01-registration.json");

// Test Suite
test.describe("FR-01: Account Registration", () => {
  let registerPage: RegisterPage;

  test.beforeEach(async ({ page }) => {
    registerPage = new RegisterPage(page);
    await registerPage.navigate(registerPage.url);
    await expect(page).toHaveURL(/\/register/); // assertion pattern 1 — URL
  });

  for (const tc of cases) {
    test(`${tc.tc_id}: ${tc.description}`, async ({ page }) => {
      await registerPage.fillAndSubmit(tc.inputs);

      if (tc.expected.outcome === "success") {
        await expect(page).toHaveURL(new RegExp(tc.expected.redirect!)); // pattern 1 — URL
        await expect(page.getByRole("alert")).toContainText(
          tc.expected.message,
        ); // pattern 2 — text
      } else {
        await expect(page).toHaveURL(/\/register/); // pattern 1 — URL
        await expect(page.getByRole("alert").first()).toBeVisible(); // pattern 3 — visibility
        await expect(page.getByRole("alert").first()).toContainText(
          tc.expected.message,
        ); // pattern 2 — text
      }
    });
  }
});
```

### GOOD vs BAD — imports

```typescript
// ✅ GOOD — always import from the project fixture entry point
import { test, expect } from "@fixtures/base.fixture";

// ❌ BAD — bypasses the custom fixture chain
import { test, expect } from "@playwright/test";
```

### GOOD vs BAD — iteration

```typescript
// ✅ GOOD — for...of loop
for (const tc of cases) {
  test(`${tc.tc_id}: ${tc.description}`, async ({ page }) => { ... });
}

// ❌ BAD — test.each() is not supported in this project
test.each(cases)("...", async (tc, { page }) => { ... });
```

### GOOD vs BAD — waiting

```typescript
// ✅ GOOD — web-first assertion auto-retries
await expect(page.getByRole("alert")).toBeVisible();

// ❌ BAD — arbitrary delay, hides real timing issue
await page.waitForTimeout(2000);
```

### GOOD vs BAD — auth override

```typescript
// ✅ GOOD — unauthenticated feature declares override at file level
test.use({ storageState: { cookies: [], origins: [] } });

// ❌ BAD — manually logging in inside a test
test("...", async ({ page }) => {
  await page.goto("/login");
  await page.fill("[name=email]", "user@test.com"); // ← never do this
});
```

### GOOD vs BAD — test naming

```typescript
// ✅ GOOD — TC-ID prefix ensures traceability to test case document
test("TC-FR01-001: Register account with valid data", async ({ page }) => {});

// ❌ BAD — no TC-ID, untrackable
test("valid registration", async ({ page }) => {});
```

### GOOD vs BAD — hardcoded data

```typescript
// ✅ GOOD — all values from data file
const cases = loadTestData<RegistrationCase>("fr-01-registration.json");
for (const tc of cases) {
  test(`${tc.tc_id}: ${tc.description}`, async ({ page }) => {
    await registerPage.fillForm(tc.inputs);
  });
}

// ❌ BAD — input hardcoded in spec
test("TC-FR01-001: ...", async ({ page }) => {
  await page.getByLabel("Email").fill("test@example.com"); // ← hardcoded
});
```

## 3. Assertion Pattern Diversity

The spec file must use **at least 3 distinct assertion pattern types**. Below are the available patterns — mix at least 3 in every spec file.

```typescript
// Pattern 1 — URL
await expect(page).toHaveURL("/login");
await expect(page).toHaveURL(/\/register/);

// Pattern 2 — Text content
await expect(locator).toHaveText("Exact text");
await expect(locator).toContainText("Partial text");

// Pattern 3 — Visibility
await expect(locator).toBeVisible();
await expect(locator).toBeHidden();

// Pattern 4 — Element state
await expect(locator).toBeEnabled();
await expect(locator).toBeDisabled();
await expect(locator).toBeChecked();

// Pattern 5 — Input value
await expect(locator).toHaveValue("Expected value");

// Pattern 6 — Count
await expect(locator).toHaveCount(3);

// Pattern 7 — Attribute
await expect(locator).toHaveAttribute("aria-invalid", "true");
```

## 4. Mode 3 — Record: TC Document Update

After test execution, fill in `Actual Result` and `Status` columns only. All other columns remain unchanged.

### GOOD entry (Pass)

```markdown
| TC-FR01-001 | Register account with valid data | ... | ... | Redirected to `/login`. Toast displayed: "Registration successful. Please verify your email." | **Pass** | |
```

### GOOD entry (Fail — with diagnostic detail)

```markdown
| TC-FR01-008 | Register account with non-matching confirm password | ... | ... | Page stayed on `/register`. No error shown on Confirm Password field. Server returned HTTP 422 but UI did not surface the message. | **Fail** | Client-side mismatch validation not triggered; see browser console. |
```

### BAD entries

```markdown
// ❌ BAD — "same as expected" is not an actual result
| TC-FR01-001 | ... | Same as expected | Pass | |

// ❌ BAD — vague, no observable evidence recorded
| TC-FR01-008 | ... | Error appeared | Fail | |
```

### Rules applied in Mode 3

- `Actual Result` records what **actually happened** — exact message text, URL, visible element, or HTTP status observed.
- `Status` is `Pass` if Actual matches Expected exactly; `Fail` for any discrepancy.
- `Notes` records diagnostic evidence for Fail entries (console errors, HTTP status, missing element).
- **No other columns are modified.**

## 5. Feature Fixture (`*.fixture.ts`) — when needed

Create a fixture only when `beforeEach` + `goto()` is insufficient (see `resources/file-conventions.md` for the decision rule).

### Structure

```typescript
import { test as base } from "@playwright/test";
import { CartPage } from "@pages/web/cart.page";

type CartFixtures = { cartPage: CartPage };

export const cartFixtures = {
  cartPage: async (
    { page }: { page: import("@playwright/test").Page },
    use: (r: CartPage) => Promise<void>,
  ) => {
    const cartPage = new CartPage(page);
    await use(cartPage);
    // teardown — e.g. clear cart via API after each test
  },
};
```

### Registering in `base.fixture.ts`

```typescript
// Before
export const test = base.extend<AuthFixtures>({ ...authFixtures });

// After — import and merge
import { cartFixtures, type CartFixtures } from "./cart.fixture";
export const test = base.extend<AuthFixtures & CartFixtures>({
  ...authFixtures,
  ...cartFixtures,
});
```

### GOOD vs BAD

```typescript
// ✅ GOOD — fixture teardown cleans up after each test
cartPage: async ({ page }, use) => {
  const cartPage = new CartPage(page);
  await use(cartPage);
  await apiContext.delete("/api/cart"); // teardown
},

// ❌ BAD — no teardown; test data bleeds into the next test
cartPage: async ({ page }, use) => {
  const cartPage = new CartPage(page);
  await use(cartPage);
  // nothing here — cart state persists
},
```
