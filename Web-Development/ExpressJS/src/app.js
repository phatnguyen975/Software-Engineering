import express from "express";
import morgan from "morgan";
import cors from "cors";
import { responseWrapper } from "./middlewares/response.middleware.js";
import {
  errorHandler,
  notFoundHandler,
} from "./middlewares/error.middleware.js";
import categoryRoutes from "./routes/category.routes.js";
import productRoutes from "./routes/product.routes.js";

export const app = express();

// chấp nhận request từ các domain khác (Cross-Origin Resource Sharing)
app.use(cors());

// parse dữ liệu JSON và dữ liệu form gửi từ client vào req.body
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// log
app.use(morgan("dev"));

// Response Wrapper
app.use(responseWrapper);

// Routes
// API End points
app.use("/api/categories", categoryRoutes);
app.use("/api/products", productRoutes);

// Error Handler
app.use(notFoundHandler);
app.use(errorHandler);
