import { test, expect } from "@fixtures/base.fixture";
import { loadTestData } from "@helpers/data-loader";

interface ForgotPasswordTestData {
  tc_id: string;
  description: string;
  inputs: {
    step1_email: string;
    step2_otp: string;
    step2_newPassword: string;
    step2_confirmNewPassword?: string;
  };
  expected: {
    outcome: "success" | "error";
    message: string;
    redirect?: string;
    generated_otp_length?: number;
  };
}

const testData = loadTestData<ForgotPasswordTestData>("fr-03-data.json");

test.describe("FR-03: Forgot Password & Password Reset", () => {
  // Ensure completely clean unauthenticated state
  test.use({ storageState: { cookies: [], origins: [] } });

  for (const data of testData) {
    test(`[${data.tc_id}] ${data.description}`, async ({
      page,
      forgotPasswordPage,
    }) => {
      let dialogMessage = "";
      page.on("dialog", (dialog) => {
        dialogMessage = dialog.message();
        dialog.dismiss();
      });

      await forgotPasswordPage.goto();

      // Special case: OTP from different email
      if (data.inputs.step2_otp.includes("<OTP from another account flow>")) {
        // 1. Submit email for a different account to get its OTP
        await forgotPasswordPage.submitEmail("test@eshop.com");
        await expect(forgotPasswordPage.otpMessage).toBeVisible();
        const otherOtp = (await forgotPasswordPage.getDisplayedOtp()) || "";

        // 2. Go back to Step 1
        await forgotPasswordPage.backButton.click();

        // 3. Submit target email
        await forgotPasswordPage.submitEmail(data.inputs.step1_email);
        await expect(forgotPasswordPage.otpMessage).toBeVisible();

        // 4. Submit Step 2 using otherOtp
        dialogMessage = "";
        await forgotPasswordPage.submitNewPassword(
          otherOtp,
          data.inputs.step2_newPassword,
          data.inputs.step2_confirmNewPassword,
        );

        // 5. Verify expected error
        await expect
          .poll(() => dialogMessage, { timeout: 3000 })
          .toContain(data.expected.message);
        return;
      }

      // Step 1
      await forgotPasswordPage.submitEmail(data.inputs.step1_email);

      const isStep2ErrorMsg = /OTP|Password|Mật khẩu/i.test(
        data.expected.message,
      );
      const isStep1Error =
        data.expected.outcome === "error" &&
        data.inputs.step2_otp.trim() === "" &&
        data.inputs.step2_newPassword.trim() === "" &&
        (data.inputs.step2_confirmNewPassword || "").trim() === "" &&
        !isStep2ErrorMsg;

      if (isStep1Error) {
        if (data.expected.message === "Email is required.") {
          const validationMsg = await forgotPasswordPage.emailInput.evaluate(
            (el: any) => el.validationMessage,
          );
          expect(validationMsg).toBeTruthy();
        } else {
          await expect
            .poll(() => dialogMessage, { timeout: 3000 })
            .toContain(data.expected.message);
        }
        return;
      }

      // Step 2
      await expect(forgotPasswordPage.otpMessage).toBeVisible();
      let displayedOtp = (await forgotPasswordPage.getDisplayedOtp()) || "";

      if (data.expected.generated_otp_length !== undefined) {
        expect(
          displayedOtp.length,
          `System generated OTP length is incorrect. Expected ${data.expected.generated_otp_length}, but got ${displayedOtp.length}`,
        ).toBe(data.expected.generated_otp_length);
        return;
      }

      // Special case: Resubmit Step 1 to get new OTP
      if (data.inputs.step2_otp.includes("<new OTP displayed")) {
        await forgotPasswordPage.backButton.click();
        await forgotPasswordPage.submitEmail(data.inputs.step1_email);
        await expect(forgotPasswordPage.otpMessage).toBeVisible();
        const newOtp = await forgotPasswordPage.getDisplayedOtp();
        expect(newOtp).toBeTruthy();
        expect(newOtp).not.toEqual(displayedOtp);
        return;
      }

      // Determine final OTP to use
      let finalOtp = "";
      if (data.inputs.step2_otp !== "") {
        if (data.inputs.step2_otp.includes("<OTP displayed on screen>")) {
          finalOtp = displayedOtp;
        } else if (
          !data.inputs.step2_otp.startsWith("<") &&
          !data.inputs.step2_otp.endsWith(">")
        ) {
          finalOtp = data.inputs.step2_otp;
        }
      }

      // Step 2 submission
      dialogMessage = "";
      await forgotPasswordPage.submitNewPassword(
        finalOtp,
        data.inputs.step2_newPassword,
        data.inputs.step2_confirmNewPassword,
      );

      if (data.expected.outcome === "success") {
        await forgotPasswordPage.waitForURL(data.expected.redirect!);
        await expect(page).toHaveURL(new RegExp(data.expected.redirect!));
      } else {
        if (data.expected.message.includes("are required")) {
          // Verify HTML5 validation native message on one of the required fields
          const validationMsg = await forgotPasswordPage.otpInput.evaluate(
            (el: any) => el.validationMessage,
          );
          expect(validationMsg).toBeTruthy();
        } else {
          await expect
            .poll(() => dialogMessage, { timeout: 3000 })
            .toContain(data.expected.message);
        }
      }
    });
  }
});
