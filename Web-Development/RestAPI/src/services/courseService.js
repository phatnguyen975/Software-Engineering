import { prisma } from "../configs/prisma.js";

const CourseService = {
  getCourses: async ({ offset, limit }) => {
    if (!offset || !limit) {
      return await prisma.course.findMany();
    }

    const skip = (offset - 1) * limit;

    const courses = await prisma.course.findMany({
      skip: skip,
      take: limit,
      orderBy: {
        createdAt: "desc",
      },
    });

    const totalCount = await prisma.course.count();
    const totalPage = Math.ceil(totalCount / limit);

    return {
      data: courses,
      totalCount,
      totalPage,
    };
  },

  createCourse: async (data) => {
    const newCourse = await prisma.course.create({ data });
    return newCourse;
  },
};

export default CourseService;
