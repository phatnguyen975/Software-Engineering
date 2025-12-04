import { Router } from "express";
import { ProductController } from "../controllers/product.controller.js";
import {
  ProductCreateSchema,
  ProductUpdateSchema,
  ProductIdSchema,
  ProductQuerySchema,
} from "../schemas/product.schema.js";
import { validate } from "../middlewares/validate.middleware.js";

const router = Router();

router.get(
  "/",
  validate({ query: ProductQuerySchema }),
  ProductController.getAll
);

router.get(
  "/:id",
  validate({ params: ProductIdSchema }),
  ProductController.getById
);

router.post(
  "/",
  validate({ body: ProductCreateSchema }),
  ProductController.create
);

router.put(
  "/:id",
  validate({ body: ProductUpdateSchema, params: ProductIdSchema }),
  ProductController.update
);

router.delete(
  "/:id",
  validate({ params: ProductIdSchema }),
  ProductController.delete
);

export default router;
