import { prisma } from "../configs/prisma.js";

const DishService = {
  createDish: async (dishData) => {
    if (dishData.original_price < dishData.current_price) {
      throw new Error(
        "Original price must be greater than or equal to current price"
      );
    }

    const category = await prisma.category.findUnique({
      where: { category_id: dishData.category_id },
    });

    if (!category) {
      throw new Error("Category not found");
    }

    const chef = await prisma.chef.findUnique({
      where: { chef_id: dishData.chef_id },
    });

    if (!chef) {
      throw new Error("Chef not found");
    }

    const newDish = await prisma.dish.create({
      data: {
        dish_name: dishData.dish_name,
        description: dishData.description,
        image_url: dishData.image_url,
        chef_id: dishData.chef_id,
        category_id: dishData.category_id,
        rating: dishData.rating,
        total_reviews: dishData.total_reviews,
        preparation_time_minutes: dishData.preparation_time_minutes,
        serving_size: dishData.serving_size,
        current_price: dishData.current_price,
        original_price: dishData.original_price,
        is_featured: dishData.is_featured,
      },
    });

    return await prisma.dish.findUnique({
      where: { dish_id: newDish.dish_id },
    });
  },

  getDishes: async (query) => {
    const { page = null, category_id, chef_id } = query;

    const where = {};

    if (category_id) {
      where.category_id = Number(category_id);
    }

    if (chef_id) {
      where.chef_id = Number(chef_id);
    }

    const pageNumber = Math.max(Number(page) || 1, 1);
    const limitNumber = 9;
    const skip = (pageNumber - 1) * limitNumber;

    const [data, total] = await Promise.all([
      prisma.dish.findMany({
        where,
        skip,
        take: limitNumber,
        include: {
          category: {
            select: {
              category_id: true,
              name: true,
            }
          },
          chef: {
            select: {
              chef_id: true,
              name: true,
              specialty: true,
            }
          },
        },
        omit: {
          category_id: true,
          chef_id: true,
        },
      }),
      prisma.dish.count({ where }),
    ]);

    return {
      data,
      metadata: {
        current_page: pageNumber,
        total_pages: Math.ceil(total / limitNumber),
        total_dishes: total,
        can_navigate_next: pageNumber * limitNumber < total,
        can_navigate_prev: pageNumber > 1,
        from_offset: skip,
        to_offset: skip + data.length - 1,
      },
    };
  },
};

export default DishService;
