import express from "express";
import CategoryController from "../controllers/categoryController.js";
import { validateApiKey } from "../middlewares/apiMiddleware.js";

const router = express.Router();

router.get(
  "/",
  validateApiKey,
  CategoryController.getAll
);

export default router;
