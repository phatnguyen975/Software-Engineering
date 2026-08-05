import { RegistrationPage } from "@pages/web/registration.page";

export type RegistrationFixtures = {
  registrationPage: RegistrationPage;
};

export const registrationFixtures = {
  registrationPage: async ({ page }: any, use: any) => {
    await use(new RegistrationPage(page));
  },
};
