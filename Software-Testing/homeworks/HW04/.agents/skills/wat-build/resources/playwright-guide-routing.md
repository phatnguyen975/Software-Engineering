# playwright-skill Guide Routing — wat-build

This file defines which guides within `playwright-skill` to read and when. Apply the routing table before writing any code in Mode 1 (Build).

## Always Read (Mode 1 — every feature)

| Guide                            | Why it is always required                                                          |
| -------------------------------- | ---------------------------------------------------------------------------------- |
| `core/locators.md`               | Defines the selector hierarchy and anti-patterns; every spec and POM uses locators |
| `core/assertions-and-waiting.md` | Defines web-first assertion patterns and the prohibition on `waitForTimeout`       |
| `pom/page-object-model.md`       | Defines POM class structure, method patterns, and the no-assertion rule            |

## Conditional Guides (read when the condition applies)

| Condition                                                                           | Guide to read                       |
| ----------------------------------------------------------------------------------- | ----------------------------------- |
| Writing or updating a `*.fixture.ts` file                                           | `core/fixtures-and-hooks.md`        |
| Configuring or changing how test data is loaded                                     | `core/test-data-management.md`      |
| Feature requires unauthenticated context OR storageState handling                   | `core/authentication.md`            |
| Feature includes a form with client-side validation (registration, login, checkout) | `core/forms-and-validation.md`      |
| SUT is a React application (dynamic rendering, async state updates)                 | `core/react.md`                     |
| Deciding whether to create a POM, fixture, or helper function                       | `pom/pom-vs-fixtures-vs-helpers.md` |

## Fallback Rule

If you cannot find the information you need in any guide listed above:

1. Identify the **specific problem** you are trying to solve (e.g. "how to intercept a network request", "how to handle file uploads").
2. Search for a guide in `playwright-skill` whose **file name and stated purpose** match that problem — do not scan file contents blindly.
3. Read **only** the matching guide.
4. Prioritise `core/` over `pom/` when both could apply.

## Out of Scope — Do Not Read

These guides are unrelated to functional web automation and should never be loaded during `wat-build` sessions:

- `ci/` — CI pipeline configuration
- `migration/` — migration from other frameworks
- `playwright-cli/` — CLI tooling
- `core/visual-regression.md` — screenshot diffing
- `core/accessibility.md` — a11y testing
- `core/security-testing.md` — security scanning

## Common Routing Decisions

| Situation                                            | Guides to read                                                                                                                |
| ---------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| Registration or login form (unauthenticated)         | core/locators, core/assertions-and-waiting, pom/page-object-model, core/forms-and-validation, core/authentication, core/react |
| Authenticated shopping cart (add/remove items)       | core/locators, core/assertions-and-waiting, pom/page-object-model, core/react                                                 |
| Admin CRUD panel (create, read, update, delete)      | core/locators, core/assertions-and-waiting, pom/page-object-model, core/react, pom/pom-vs-fixtures-vs-helpers                 |
| Feature requiring seed data created before each test | core/locators, core/assertions-and-waiting, pom/page-object-model, core/fixtures-and-hooks, core/test-data-management         |
