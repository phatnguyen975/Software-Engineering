import { readJSON } from "../utils/readJSON.js";

let categories = readJSON("categories.json");

export const CategoryService = {
  getAll() {
    return categories;
  },

  getById(id) {
    return categories.filter((c) => c.id == id);
  },

  create(data) {
    const newCategory = {
      id: Math.max(...categories.map((c) => c.id)) + 1,
      name: data.name,
    };
    categories.push(newCategory);
    return newCategory;
  },

  update(id, data) {
    const updatedCategory = categories.find((c) => c.id == id);
    updatedCategory.name = data.name;
    return updatedCategory;
  },

  delete(id, data) {
    categories = categories.filter((c) => c.id !== id);
  },
};
