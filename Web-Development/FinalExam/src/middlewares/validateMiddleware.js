import { ZodError } from "zod";

export const validate = (schemas) => (req, res, next) => {
  try {
    req.validated = {};

    // Validate Body
    if (schemas.body) {
      req.validated.body = schemas.body.parse(req.body);
    }

    // Validate Params
    if (schemas.params) {
      req.validated.params = schemas.params.parse(req.params);
    }

    // Validate Query
    if (schemas.query) {
      req.validated.query = schemas.query.parse(req.query);
    }

    next();
  } catch (err) {
    if (err instanceof ZodError) {
      const details = err.issues.map((e) => e.message);

      return res.error(
        "Validation failed",
        details,
        400
      );
    }

    return res.error(err.message || "Unknown Error", [], 500);
  }
};
