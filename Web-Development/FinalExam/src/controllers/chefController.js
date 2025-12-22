import ChefService from "../services/chefService.js";

const ChefController = {
  getAll: async (req, res) => {
    try {
      const chefs = await ChefService.getChefs();
      res.ok(chefs);
    } catch (error) {
      res.error("Validation failed", [error.message]);
    }
  },
};

export default ChefController;
