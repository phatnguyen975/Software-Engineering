import { test, expect } from "@fixtures/base.fixture";
import { loadTestData } from "@helpers/data-loader";

interface RegistrationTestData {
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

const testData = loadTestData<RegistrationTestData>("fr-01-data.json");

test.describe("FR-01: Account Registration", () => {
  // Ensure completely clean unauthenticated state for guest registration flow
  test.use({ storageState: { cookies: [], origins: [] } });

  for (const data of testData) {
    test(`[${data.tc_id}] ${data.description}`, async ({
      page,
      registrationPage,
    }) => {
      let dialogMessage = "";
      page.on("dialog", (dialog) => {
        dialogMessage = dialog.message();
        dialog.dismiss();
      });

      await registrationPage.goto();
      await registrationPage.fillRegistrationForm(data.inputs);
      await expect(registrationPage.registerButton).toBeEnabled();
      await registrationPage.submit();

      if (data.expected.outcome === "success") {
        await registrationPage.waitForURL(data.expected.redirect!);
        await expect(page).toHaveURL(new RegExp(data.expected.redirect!));
      } else {
        const emptyFieldMessages = [
          "Full name is required.",
          "Email is required.",
          "Password is required.",
        ];

        if (emptyFieldMessages.includes(data.expected.message)) {
          let targetField = null;
          if (data.expected.message === "Full name is required.") {
            targetField = registrationPage.fullNameInput;
          } else if (data.expected.message === "Email is required.") {
            targetField = registrationPage.emailInput;
          } else if (data.expected.message === "Password is required.") {
            targetField = registrationPage.passwordInput;
          }

          if (targetField) {
            // Verify HTML5 validation native message
            const validationMsg = await targetField.evaluate(
              (el: any) => el.validationMessage,
            );
            expect(validationMsg).toBeTruthy();
          }
        } else {
          // For format, duplicate, or weak password errors, ensure exact message match
          if (dialogMessage) {
            expect(dialogMessage).toContain(data.expected.message);
          } else {
            const errorLocator = page.getByText(data.expected.message);
            await expect(errorLocator).toBeVisible();
            await expect(errorLocator).toContainText(data.expected.message);
          }
        }
      }
    });
  }
});
