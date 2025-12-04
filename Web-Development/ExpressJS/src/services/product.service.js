import { readJSON } from "../utils/readJSON.js";
import { writeJSON } from "../utils/writeJSON.js";

let products = readJSON("products.json");

export const ProductService = {
  getAll({ page, limit, q }) {
    let filtered = [...products];

    // Filter
    if (q != null) {
      const searchQuery = q.toLowerCase();
      filtered = filtered.filter((product) =>
        product.name.toLowerCase().includes(searchQuery)
      );
    }

    // Return all products
    if (!page || !limit) {
      return {
        page: 1,
        limit: filtered.length,
        totalCount: filtered.length,
        totalPages: 1,
        data: filtered,
      };
    }

    // Pagination
    const start = (page - 1) * limit;
    const data = filtered.slice(start, start + limit);

    return {
      page,
      limit,
      totalCount: filtered.length,
      totalPages: Math.ceil(filtered.length / limit),
      data,
    };
  },

  getById(id) {
    return products.find((product) => product.id == id);
  },

  create(data) {
    const newProduct = {
      id: Math.max(...products.map((product) => product.id)) + 1,
      ...data,
    };
    products.push(newProduct);
    writeJSON("products.json", products);
    return newProduct;
  },

  update(id, data) {
    const product = products.find((product) => product.id == id);
    if (!product) {
      return null;
    }

    Object.assign(product, data);
    writeJSON("products.json", products);
    return product;
  },

  delete(id) {
    const exists = products.some((product) => product.id == id);
    if (!exists) {
      return false;
    }

    products = products.filter((product) => product.id != id);
    writeJSON("products.json", products);
    return true;
  },
};
