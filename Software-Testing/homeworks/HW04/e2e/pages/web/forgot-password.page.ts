import { type Locator, type Page } from "@playwright/test";
import { BasePage } from "../base.page";

export class ForgotPasswordPage extends BasePage {
  // Step 1
  readonly heading: Locator;
  readonly emailInput: Locator;
  readonly submitStep1Button: Locator;

  // Step 2
  readonly otpMessage: Locator;
  readonly otpInput: Locator;
  readonly newPasswordInput: Locator;
  readonly confirmPasswordInput: Locator;
  readonly submitStep2Button: Locator;
  readonly backButton: Locator;

  // Generic
  readonly errorMessage: Locator;

  constructor(page: Page) {
    super(page);
    this.heading = page.getByRole("heading", { name: "Quên Mật Khẩu" });
    this.emailInput = page.locator('input[type="text"]').first();
    this.submitStep1Button = page.getByRole("button", { name: "Lấy mã OTP" });

    this.otpMessage = page.getByText(/Mã OTP của bạn là:/);
    this.otpInput = page.locator('input[type="text"]').last();
    this.newPasswordInput = page.locator('input[type="password"]').first();
    this.confirmPasswordInput = page.getByLabel(/Xác nhận mật khẩu/i);

    this.submitStep2Button = page.getByRole("button", {
      name: "Đặt lại mật khẩu",
    });
    this.backButton = page.getByRole("button", { name: "Quay lại" });

    this.errorMessage = page
      .locator('.text-red-500, [role="alert"], .text-sm.text-red-600')
      .first();
  }

  async goto() {
    await this.navigate("/forgot-password");
  }

  async submitEmail(email: string) {
    if (email !== "") {
      await this.emailInput.fill(email);
    }
    await this.submitStep1Button.click();
  }

  async submitNewPassword(
    otp: string,
    newPassword: string,
    confirmPassword?: string,
  ) {
    if (otp !== "") {
      await this.otpInput.fill(otp);
    }
    if (newPassword !== "") {
      await this.newPasswordInput.fill(newPassword);
    }
    if (
      confirmPassword !== undefined &&
      confirmPassword !== "" &&
      newPassword !== confirmPassword
    ) {
      await this.confirmPasswordInput.fill(confirmPassword);
    }
    await this.submitStep2Button.click();
  }

  async getDisplayedOtp(): Promise<string | null> {
    const text = await this.otpMessage.textContent();
    if (text) {
      const match = text.match(/Mã OTP của bạn là:\s*(\d+)/);
      return match ? match[1] : null;
    }
    return null;
  }
}
