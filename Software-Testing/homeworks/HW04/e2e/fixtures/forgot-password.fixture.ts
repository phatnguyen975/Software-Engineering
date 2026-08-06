import { type Fixtures, request } from "@playwright/test";
import { ForgotPasswordPage } from "@pages/web/forgot-password.page";
import fs from "fs";
import path from "path";
import dotenv from "dotenv";

dotenv.config({ path: path.resolve(__dirname, "../.env") });
const API_BASE_URL = process.env.API_BASE_URL ?? "http://localhost:3000";

export type ForgotPasswordFixtures = {
  forgotPasswordPage: ForgotPasswordPage;
};

export const forgotPasswordFixtures: Fixtures<ForgotPasswordFixtures> = {
  forgotPasswordPage: async ({ page }: any, use, testInfo) => {
    const tcMatch = testInfo.title.match(/\[(TC-FR03-\d{3})\]/);
    let createdUserId: number | null = null;

    if (tcMatch) {
      const tcId = tcMatch[1];
      const dataPath = path.join(__dirname, "../data/fr-03-data.json");
      if (fs.existsSync(dataPath)) {
        const data = JSON.parse(fs.readFileSync(dataPath, "utf-8"));
        const tcData = data.find((d: any) => d.tc_id === tcId);

        if (tcData) {
          const email = tcData.inputs.step1_email;
          if (email && email.startsWith("user")) {
            const reqContext = await request.newContext({
              baseURL: API_BASE_URL,
            });
            const res = await reqContext.post("/api/register", {
              data: {
                fullName: "Test User",
                email: email,
                password: "TestPassword123!",
              },
            });

            if (res.ok()) {
              const resJson = await res.json();
              createdUserId = resJson.id;
            }
            await reqContext.dispose();
          }
        }
      }
    }

    await use(new ForgotPasswordPage(page));

    if (createdUserId) {
      const adminAuthPath = path.join(__dirname, "../.auth/admin.json");
      if (fs.existsSync(adminAuthPath)) {
        const adminState = JSON.parse(fs.readFileSync(adminAuthPath, "utf-8"));
        let token = "";
        for (const origin of adminState.origins || []) {
          const item = origin.localStorage?.find(
            (i: any) => i.name === "adminToken",
          );
          if (item) {
            token = item.value;
            break;
          }
        }

        const adminContext = await request.newContext({
          baseURL: API_BASE_URL,
          extraHTTPHeaders: token ? { Authorization: `Bearer ${token}` } : {},
        });
        await adminContext.delete(`/api/admin/users/${createdUserId}`);
        await adminContext.dispose();
      }
    }
  },
};
