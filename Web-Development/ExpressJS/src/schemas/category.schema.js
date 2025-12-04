import { z } from "zod";

export const CategoryCreateSchema = z.object({
  name: z.string("Name is required").min(2),
});

export const CategoryIdSchema = z.object({
  id: z.string().regex(/^\d+$/, "ID is a number").transform(Number),
});
