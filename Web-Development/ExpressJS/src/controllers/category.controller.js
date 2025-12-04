import { CategoryService } from "../services/category.service.js";

export const CategoryController = {
  async getAll(req, res) {
    const data = await CategoryService.getAll();
    return res.ok(data, "Get categories successfully");
  },

  async getById(req, res) {
    const id = Number(req.validated.params.id);
    const data = await CategoryService.getById(id);
    return res.ok(data, "Get category by id successfully");
  },

  async create(req, res) {
    const newCategory = await CategoryService.create(req.validated.body);
    return res.created("Category created", newCategory);
  },

  async update(req, res) {
    const id = Number(req.validated.params.id);
    const updatedCategory = await CategoryService.update(
      id,
      req.validated.body
    );
    return res.ok(updatedCategory, "Category updated");
  },

  async delete(req, res) {
    const id = Number(req.validated.params.id);
    await CategoryService.delete(id);
    return res.ok(null, "Category deleted");
  },
};
