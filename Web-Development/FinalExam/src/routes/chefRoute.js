import express from "express";
import ChefController from "../controllers/chefController.js";
import { validateApiKey } from "../middlewares/apiMiddleware.js";

const router = express.Router();

router.get(
  "/",
  validateApiKey,
  ChefController.getAll
);

export default router;
