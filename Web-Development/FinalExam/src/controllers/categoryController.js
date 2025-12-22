import CategoryService from "../services/categoryService.js";

const CategoryController = {
  getAll: async (req, res) => {
    try {
      const categories = await CategoryService.getCategories();
      res.ok(categories);
    } catch (error) {
      res.error("Validation failed", [error.message]);
    }
  },
};

export default CategoryController;
