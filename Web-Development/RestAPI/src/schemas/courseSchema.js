import { z } from "zod";

// GET /courses?offset=&limit=
export const CourseQuerySchema = z.object({
  offset: z
    .string()
    .regex(/^\d+$/, "Offset must be a number")
    .transform(Number)
    .optional(),

  limit: z
    .string()
    .regex(/^\d+$/, "Limit must be a number")
    .transform(Number)
    .optional(),
});

// POST /courses/create
export const CourseCreateSchema = z.object({
  name: z
    .string()
    .min(2, "Name must be at least 2 characters")
    .max(100, "Name must be at most 100 characters"),

  description: z
    .string()
    .min(5, "Description must be at least 5 characters")
    .optional(),

  courseImage: z
    .string()
    .url("Course image must be a valid URL"),

  instructor: z
    .string()
    .min(2, "Instructor name must be at least 2 characters"),

  price: z
    .number()
    .positive("Price must be greater than 0"),

  rating: z
    .number()
    .min(0, "Rating must be between 0 and 5")
    .max(5, "Rating must be between 0 and 5")
    .default(0),
});
