import CourseService from "../services/courseService.js";

const CourseController = {
  getAll: async (req, res) => {
    try {
      const result = await CourseService.getCourses(req.validated.query);
      res.ok(result, "Get courses successfully");
    } catch (error) {
      res.error(error.message);
    }
  },

  create: async (req, res) => {
    try {
      const newCourse = await CourseService.createCourse(req.validated.body);
      res.created("Product created successfully", newCourse);
    } catch (error) {
      res.error(error.message);
    }
  },
};

export default CourseController;
