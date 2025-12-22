import express from "express";
import DishController from "../controllers/dishController.js";
import { validate } from "../middlewares/validateMiddleware.js";
import { DishCreateSchema, DishQuerySchema } from "../schemas/dishSchema.js";
import { validateApiKey } from "../middlewares/apiMiddleware.js";

const router = express.Router();

router.post(
  "/",
  validateApiKey,
  validate({ body: DishCreateSchema }),
  DishController.create
);

router.get(
  "/",
  validateApiKey,
  validate({ query: DishQuerySchema }),
  DishController.getAll
);

export default router;
