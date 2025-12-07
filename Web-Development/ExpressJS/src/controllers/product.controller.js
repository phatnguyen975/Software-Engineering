import { ProductService } from "../services/product.service.js";

export const ProductController = {
  async getAll(req, res) {
    const query = req.validated.query;
    const data = await ProductService.getAll(query);
    return res.ok(data, "Get products successfully");
  },

  async getById(req, res) {
    const id = Number(req.validated.params.id);
    const product = await ProductService.getById(id);

    if (!product) {
      return res.notFound("Product not found");
    }

    return res.ok(product, "Get product successfully");
  },

  async create(req, res) {
    const newProduct = await ProductService.create(req.validated.body);
    return res.created("Product created", newProduct);
  },

  async update(req, res) {
    const id = Number(req.validated.params.id);
    const updated = await ProductService.update(id, req.validated.body);

    if (!updated) {
      return res.notFound("Product not found");
    }

    return res.ok(updated, "Product updated");
  },

  async delete(req, res) {
    const id = Number(req.validated.params.id);
    const deleted = await ProductService.delete(id);

    if (!deleted) {
      return res.notFound("Product not found");
    }

    return res.ok(null, "Product deleted");
  },
};
