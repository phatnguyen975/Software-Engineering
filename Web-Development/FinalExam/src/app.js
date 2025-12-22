import express from "express";
import cors from "cors";

import { logger } from "./configs/logger.js";
import { responseWrapper } from "./middlewares/responseMiddleware.js";
import {
  errorHandler,
  notFoundHandler,
} from "./middlewares/errorMiddleware.js";

import categoryRouter from "./routes/categoryRoute.js";
import chefRouter from "./routes/chefRoute.js";
import dishRouter from "./routes/dishRoute.js";

export const app = express();

// Middlewares
app.use(cors({ origin: process.env.CLIENT_URL, credentials: true }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Logger
app.use(logger);

// Response Wrapper
app.use(responseWrapper);

// Routes
app.use("/api/categories", categoryRouter);
app.use("/api/chefs", chefRouter);
app.use("/api/dishes", dishRouter);

// Error Handler
app.use(notFoundHandler);
app.use(errorHandler);
