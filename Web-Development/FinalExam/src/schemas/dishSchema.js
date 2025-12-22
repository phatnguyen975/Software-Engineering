import { z } from "zod";

export const DishIdSchema = z.object({
  id: z.string().regex(/^\d+$/, "ID must be a number").transform(Number),
});

export const DishCreateSchema = z.object({
  dish_name: z
    .string({ required_error: "Dish name is required" })
    .max(500, "Dish name must be less than or equal to 500 characters"),

  description: z.string({ required_error: "Description is required" }).min(50, "Description must be at least 50 characters"),

  image_url: z
    .string({ required_error: "Image URL is required" })
    .url()
    .max(1000, "Image URL must be less than or equal to 1000 characters"),

  chef_id: z.number().int().positive(),
  category_id: z.number().int().positive(),

  rating: z
    .number()
    .min(0, "Rating must be greater than or equal to 0")
    .max(5, "Rating must be less than or equal to 5")
    .default(0),
  total_reviews: z.number().int().min(0).default(0).optional(),

  preparation_time_minutes: z.number().int().min(1),
  serving_size: z.number().int().min(1).default(1).optional(),

  current_price: z.number().int().min(0),
  original_price: z.number().int().min(0),

  is_featured: z.boolean().default(false).optional(),
});

export const DishQuerySchema = z.object({
  page: z
    .string()
    .regex(/^\d+$/, "Page must be a number")
    .transform(Number)
    .optional(),

  category_id: z
    .string()
    .regex(/^\d+$/, "Category ID must be a number")
    .transform(Number)
    .optional(),

  chef_id: z
    .string()
    .regex(/^\d+$/, "Chef ID must be a number")
    .transform(Number)
    .optional(),
});
