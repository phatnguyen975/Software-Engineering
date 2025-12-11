import express from "express";
import CourseController from "../controllers/courseController.js";
import { validateApiKey } from "../middlewares/validateApiKey.js"
import { validate } from "../middlewares/validateMiddleware.js";
import {
  CourseCreateSchema,
  CourseQuerySchema
} from "../schemas/courseSchema.js";

const courseRouter = express.Router();

courseRouter.get(
  "/",
  validateApiKey,
  validate({ query: CourseQuerySchema }),
  CourseController.getAll
);

courseRouter.post(
  "/create",
  validateApiKey,
  validate({ body: CourseCreateSchema }),
  CourseController.create
);

export default courseRouter;
