import express from "express";
import cors from "cors";

import { logger } from "./configs/logger.js";
import { responseWrapper } from "./middlewares/responseMiddleware.js";
import {
  errorHandler,
  notFoundHandler,
} from "./middlewares/errorMiddleware.js";

import courseRouter from "./routes/courseRoute.js";

export const app = express();

// Middlewares
app.use(cors({ origin: process.env.CLIENT_URL }));
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Logger
app.use(logger);

// Response Wrapper
app.use(responseWrapper);

// Routes
app.use("/api/courses", courseRouter);

// Error Handler
app.use(notFoundHandler);
app.use(errorHandler);
