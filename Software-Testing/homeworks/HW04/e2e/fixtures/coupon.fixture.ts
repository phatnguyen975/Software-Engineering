import {
  request as playwrightRequest,
  type Page,
  expect,
} from "@playwright/test";
import fs from "fs";
import path from "path";
import { CouponManagementPage } from "../pages/admin/coupon-management.page";

export interface CouponApiFixture {
  couponApi: {
    /** Setup data by creating a coupon via API. Automatically tracked for cleanup. */
    create: (data: any) => Promise<void>;
    /** Manually delete a coupon via API. Removes it from automatic cleanup. */
    delete: (code: string) => Promise<void>;
  };
}

export const couponFixtures = {
  couponApi: async (
    { page }: { page: Page },
    use: (r: CouponApiFixture["couponApi"]) => Promise<void>,
  ) => {
    // Extract the admin token from the saved storage state
    const stateFile = path.resolve(__dirname, "..", ".auth", "admin.json");
    let token = "";
    if (fs.existsSync(stateFile)) {
      const state = JSON.parse(fs.readFileSync(stateFile, "utf-8"));
      // The token is stored in localStorage under 'adminToken'
      const origins = state.origins || [];
      for (const origin of origins) {
        const tokenItem = origin.localStorage?.find(
          (item: any) => item.name === "adminToken",
        );
        if (tokenItem) {
          token = tokenItem.value;
          break;
        }
      }
    }

    // Create a dedicated API request context pointing directly to the backend API
    const apiBaseURL = process.env.API_BASE_URL ?? "http://localhost:3000";
    const apiContext = await playwrightRequest.newContext({
      baseURL: apiBaseURL,
      extraHTTPHeaders: token ? { Authorization: `Bearer ${token}` } : {},
    });

    const api = {
      create: async (data: any) => {
        const res = await apiContext.post("/api/admin/coupons", { data });
        if (!res.ok()) {
          const body = await res.text();
          throw new Error(
            `Failed to create coupon via API: ${res.status()} ${body}`,
          );
        }
      },
      delete: async (code: string) => {
        const couponPage = new CouponManagementPage(page);
        await couponPage.goto();
        const row = couponPage.getCouponRow(code);
        if (await row.isVisible()) {
          await couponPage.clickDelete(code);
          await expect(row)
            .toBeHidden({ timeout: 2000 })
            .catch(() => {});
        }
      },
    };

    await use(api);

    await apiContext.dispose();
  },
};
