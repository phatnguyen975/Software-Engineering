import { Router } from "express";
import { CategoryController } from "../controllers/category.controller.js";
import {
  CategoryCreateSchema,
  CategoryIdSchema,
} from "../schemas/category.schema.js";
import { validate } from "../middlewares/validate.middleware.js";

const router = Router();

router.get("/", CategoryController.getAll);
router.get(
  "/:id",
  validate({ params: CategoryIdSchema }),
  CategoryController.getById
);

router.post(
  "/",
  validate({ body: CategoryCreateSchema }),
  CategoryController.create
);

router.put(
  "/:id",
  validate({ body: CategoryCreateSchema, params: CategoryIdSchema }),
  CategoryController.update
);

router.delete(
  "/:id",
  validate({ params: CategoryIdSchema }),
  CategoryController.delete
);

export default router;
