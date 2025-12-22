import { prisma } from "../configs/prisma.js";

const ChefService = {
  getChefs: async () => {
    const chefs = await prisma.chef.findMany({
      select: {
        chef_id: true,
        name: true,
        specialty: true,
      }
    });
    return chefs;
  },
};

export default ChefService;
