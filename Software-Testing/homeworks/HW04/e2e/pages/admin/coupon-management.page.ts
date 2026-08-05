import { type Page, type Locator } from "@playwright/test";
import { BasePage } from "../base.page";

export class CouponManagementPage extends BasePage {
  readonly codeInput: Locator;
  readonly typeSelect: Locator;
  readonly minOrderAmountInput: Locator;
  readonly expiredAtInput: Locator;
  readonly maxUsesInput: Locator;
  readonly submitButton: Locator;

  constructor(page: Page) {
    super(page);

    this.codeInput = page.getByPlaceholder("Mã coupon (VD: SAVE10)");
    this.typeSelect = page.locator("select");
    this.minOrderAmountInput = page.getByPlaceholder("Đơn tối thiểu (₫)");
    this.expiredAtInput = page.getByPlaceholder("Ngày hết hạn");
    this.maxUsesInput = page.getByPlaceholder("Số lần dùng tối đa/người");
    this.submitButton = page.getByRole("button", { name: "Tạo mã" });
  }

  async goto() {
    await this.navigate("/");
    await this.page.getByText("Mã Giảm Giá").click();
  }

  async fillCouponForm(data: {
    code?: string;
    type?: "percent" | "fixed";
    discountValue?: number | string | null;
    minOrderAmount?: number | null;
    expiredAt?: string;
    maxUsesPerUser?: number | null;
  }) {
    if (data.code !== undefined && data.code !== null) {
      await this.codeInput.fill(data.code);
    }
    if (data.type) {
      await this.typeSelect
        .selectOption(data.type, { timeout: 1500 })
        .catch(() => {});
    }
    if (data.discountValue !== undefined && data.discountValue !== null) {
      const discountLocator = this.page.getByPlaceholder(
        data.type === "fixed" ? "Số tiền (VD: 50000)" : "Giá trị % (VD: 10)",
      );
      try {
        await discountLocator.fill(String(data.discountValue), {
          timeout: 1500,
        });
      } catch (e) {
        await discountLocator
          .evaluate((el: any, val: string) => {
            el.value = val;
            el.dispatchEvent(new Event("input", { bubbles: true }));
            el.dispatchEvent(new Event("change", { bubbles: true }));
          }, String(data.discountValue))
          .catch(() => {});
      }
    }
    if (data.minOrderAmount !== undefined && data.minOrderAmount !== null) {
      await this.minOrderAmountInput
        .fill(String(data.minOrderAmount), { timeout: 1500 })
        .catch(() => {});
    }
    if (data.expiredAt !== undefined && data.expiredAt !== null) {
      await this.expiredAtInput
        .fill(data.expiredAt, { timeout: 1500 })
        .catch(() => {});
    }
    if (data.maxUsesPerUser !== undefined && data.maxUsesPerUser !== null) {
      await this.maxUsesInput
        .fill(String(data.maxUsesPerUser), { timeout: 1500 })
        .catch(() => {});
    }
  }

  async submitForm() {
    await this.submitButton.click();
  }

  getCouponRow(code: string): Locator {
    // Locates the table row containing the specific coupon code
    return this.page.locator("tr").filter({ hasText: code });
  }

  getDeleteButton(code: string): Locator {
    return this.getCouponRow(code).getByRole("button", { name: "Xóa" });
  }

  async clickDelete(code: string) {
    // Automatically accept any native confirmation dialogs if they appear
    this.page.once("dialog", (dialog) => dialog.accept().catch(() => {}));
    await this.getDeleteButton(code).first().click();
  }
}
