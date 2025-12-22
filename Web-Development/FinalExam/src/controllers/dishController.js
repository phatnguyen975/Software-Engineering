import DishService from "../services/dishService.js";

const DishController = {
  create: async (req, res) => {
    try {
      const newDish = await DishService.createDish(req.validated.body);
      res.created(newDish);
    } catch (error) {
      res.error("Validation failed", [error.message]);
    }
  },

  getAll: async (req, res) => {
    try {
      const { data, metadata } = await DishService.getDishes(req.validated.query);
      res.ok(data, metadata);
    } catch (error) {
      res.error("Validation failed", [error.message]);
    }
  },
};

export default DishController;
