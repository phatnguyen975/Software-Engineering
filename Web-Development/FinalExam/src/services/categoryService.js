import { prisma } from "../configs/prisma.js";

const CategoryService = {
  getCategories: async () => {
    const categories = await prisma.category.findMany({
      select: {
        category_id: true,
        name: true,
      },
    });
    return categories;
  },
};

export default CategoryService;
