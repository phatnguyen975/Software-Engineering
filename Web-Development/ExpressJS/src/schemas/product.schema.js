import { z } from "zod";

export const ProductIdSchema = z.object({
  id: z.string().regex(/^\d+$/, "ID must be a number").transform(Number),
});

export const ProductCreateSchema = z.object({
  name: z
    .string("Name is required")
    .min(2, "Name must be at least 2 characters"),

  description: z
    .string("Description is required")
    .min(5, "Description must be at least 5 characters"),

  price: z.number("Price is required").positive("Price must be greater than 0"),
});

export const ProductUpdateSchema = ProductCreateSchema.partial();

export const ProductQuerySchema = z.object({
  page: z
    .string()
    .regex(/^\d+$/, "page must be a number")
    .transform(Number)
    .optional(),

  limit: z
    .string()
    .regex(/^\d+$/, "limit must be a number")
    .transform(Number)
    .optional(),

  q: z.string().optional(),
});
