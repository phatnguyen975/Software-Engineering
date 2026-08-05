import { type Page, type Locator } from "@playwright/test";
import { BasePage } from "../base.page";

export class RegistrationPage extends BasePage {
  readonly fullNameInput: Locator;
  readonly emailInput: Locator;
  readonly passwordInput: Locator;
  readonly confirmPasswordInput: Locator;
  readonly passwordInlineError: Locator;
  readonly registerButton: Locator;

  constructor(page: Page) {
    super(page);
    this.fullNameInput = page
      .locator("div")
      .filter({ has: page.getByText("Họ Tên", { exact: true }) })
      .last()
      .locator("input");

    this.emailInput = page
      .locator("div")
      .filter({ has: page.getByText("Email", { exact: true }) })
      .last()
      .locator("input");

    this.passwordInput = page
      .locator("div")
      .filter({ has: page.getByText("Mật khẩu", { exact: true }) })
      .last()
      .locator("input");

    this.confirmPasswordInput = page
      .locator("div")
      .filter({ has: page.getByText("Xác nhận mật khẩu", { exact: true }) })
      .last()
      .locator("input");

    this.passwordInlineError = page.getByText(
      "Mật khẩu quá yếu! Phải dài tối thiểu 8 ký tự, gồm chữ hoa, chữ thường, số và KÝ TỰ ĐẶC BIỆT.",
    );

    this.registerButton = page.getByRole("button", { name: "Đăng Ký" });
  }

  async goto() {
    await this.navigate("/register");
  }

  async fillRegistrationForm(data: {
    fullName?: string;
    email?: string;
    password?: string;
    confirmPassword?: string;
  }) {
    if (data.fullName !== undefined) {
      await this.fullNameInput.fill(data.fullName);
    }
    if (data.email !== undefined) {
      await this.emailInput.fill(data.email);
    }
    if (data.password !== undefined) {
      await this.passwordInput.fill(data.password);
    }

    if (
      data.password &&
      data.confirmPassword !== undefined &&
      data.password !== data.confirmPassword
    ) {
      // Intentionally let it timeout if missing, to reflect actual SUT functional bugs
      await this.confirmPasswordInput.fill(data.confirmPassword);
    }
  }

  async submit() {
    await this.registerButton.click();
  }
}
