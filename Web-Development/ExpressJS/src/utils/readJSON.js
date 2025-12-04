import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export const readJSON = (filePath) => {
  const fullPath = path.join(__dirname, "..", "data", filePath);
  const json = fs.readFileSync(fullPath, "utf8");
  return JSON.parse(json);
};
