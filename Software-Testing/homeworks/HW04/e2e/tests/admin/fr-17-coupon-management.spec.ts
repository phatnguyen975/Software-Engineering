import { test, expect } from "@fixtures/base.fixture";
import { loadTestData } from "@helpers/data-loader";
import { CouponManagementPage } from "@pages/admin/coupon-management.page";

interface CouponInputs {
  code: string;
  type: "percent" | "fixed";
  discountValue: number | string | null;
  expiredAt: string;
  minOrderAmount: number | null;
  maxUsesPerUser: number | null;
}

interface CouponExpected {
  outcome: "success" | "error";
  message: string;
}

interface CouponTestCase {
  tc_id: string;
  description: string;
  mode: "create" | "view" | "delete" | "duplicate";
  inputs: CouponInputs;
  expected: CouponExpected;
}

const testCases = loadTestData<CouponTestCase>("fr-17-data.json");

test.describe("FR-17: Coupon Management (CRUD)", () => {
  let couponPage: CouponManagementPage;

  test.beforeEach(async ({ page }) => {
    couponPage = new CouponManagementPage(page);
    await couponPage.goto();
  });

  for (const tc of testCases) {
    test(`${tc.tc_id}: ${tc.description}`, async ({ page, couponApi }) => {
      if (tc.mode === "view") {
        await expect(page.locator("table")).toBeVisible();
        await expect(page.getByText("Quản lý Mã Giảm Giá")).toBeVisible();
      } else if (tc.mode === "create") {
        await expect(page.locator("table")).toBeVisible();

        const rowLocator =
          tc.inputs.code.trim() !== ""
            ? couponPage.getCouponRow(tc.inputs.code)
            : null;
        const initialCount = rowLocator ? await rowLocator.count() : 0;

        await couponPage.fillCouponForm(tc.inputs);
        
        // Setup promise BEFORE clicking submit to catch the POST request if it happens
        const postPromise = page.waitForResponse(
          (res) => res.url().includes("/api/admin/coupons") && res.request().method() === "POST",
          { timeout: 1500 }
        ).catch(() => null);

        await couponPage.submitForm();
        await postPromise;

        try {
          if (tc.expected.outcome === "success") {
            if (rowLocator) {
              await expect(rowLocator).toHaveCount(initialCount + 1);

              const expectedRaw = String(tc.inputs.discountValue);
              const expectedFormatted = Number(
                tc.inputs.discountValue,
              ).toLocaleString("vi-VN");

              const rowText = await rowLocator.first().innerText();
              expect(
                rowText.includes(expectedRaw) ||
                  rowText.includes(expectedFormatted),
              ).toBeTruthy();

              if (tc.inputs.type === "percent") {
                await expect(rowLocator.first()).toContainText("%");
              } else {
                await expect(rowLocator.first()).toContainText("₫");
              }
            }
          } else if (tc.expected.outcome === "error") {
            if (rowLocator) {
              await expect(couponPage.submitButton).toBeVisible();
              
              await couponPage.goto();
              
              // Wait for table to render at least one row (seed data) before checking count
              await expect(page.locator("tbody tr").first()).toBeVisible();
              
              // Safely assert the count hasn't increased using auto-waiting toHaveCount
              await expect(rowLocator).toHaveCount(initialCount);
            } else {
              await expect(couponPage.submitButton).toBeVisible();
            }
          }
        } finally {
          if (rowLocator) {
            await couponPage.goto();
            
            // Wait for table to render before calling .count() instantly
            await expect(page.locator("tbody tr").first()).toBeVisible();

            while ((await rowLocator.count()) > initialCount) {
              const currentCount = await rowLocator.count();
              const newlyCreatedRow = rowLocator.last();

              if (await newlyCreatedRow.isVisible()) {
                await newlyCreatedRow
                  .getByRole("button", { name: "Xóa" })
                  .click();
                await expect(newlyCreatedRow).toBeHidden();
              } else {
                await couponPage.clickDelete(tc.inputs.code);
                await expect(rowLocator).toHaveCount(currentCount - 1);
              }
            }
          }
        }
      } else if (tc.mode === "delete") {
        await couponApi.create(tc.inputs);
        await couponPage.goto();

        await expect(couponPage.getCouponRow(tc.inputs.code)).toBeVisible();

        await couponPage.clickDelete(tc.inputs.code);
        await expect(couponPage.getCouponRow(tc.inputs.code)).toBeHidden();
      } else if (tc.mode === "duplicate") {
        await expect(page.locator("tbody tr").first()).toBeVisible();

        const rowLocator =
          tc.inputs.code.trim() !== ""
            ? couponPage.getCouponRow(tc.inputs.code)
            : null;
        const initialCount = rowLocator ? await rowLocator.count() : 0;

        expect(initialCount).toBeGreaterThan(0);

        await couponPage.fillCouponForm(tc.inputs);
        
        // Setup promise BEFORE clicking submit to catch the POST request if it happens
        const postPromise = page.waitForResponse(
          (res) => res.url().includes("/api/admin/coupons") && res.request().method() === "POST",
          { timeout: 1500 }
        ).catch(() => null);

        await couponPage.submitForm();
        await postPromise;

        try {
          if (rowLocator) {
            await expect(couponPage.submitButton).toBeVisible();

            // Force a refresh to get the absolute truth from the backend
            await couponPage.goto();

            // Assert that no duplicate was created
            await expect(rowLocator).toHaveCount(initialCount);
          } else {
            await expect(couponPage.submitButton).toBeVisible();
          }
        } finally {
          if (rowLocator) {
            await couponPage.goto();

            // Wait for table to render before calling .count() instantly
            await expect(page.locator("tbody tr").first()).toBeVisible();

            while ((await rowLocator.count()) > initialCount) {
              const currentCount = await rowLocator.count();
              const newlyCreatedRow = rowLocator.last();

              if (await newlyCreatedRow.isVisible()) {
                await newlyCreatedRow
                  .getByRole("button", { name: "Xóa" })
                  .click();
                await expect(newlyCreatedRow).toBeHidden();
              } else {
                await couponPage.clickDelete(tc.inputs.code);
                await expect(rowLocator).toHaveCount(currentCount - 1);
              }
            }
          }
        }
      }
    });
  }
});
