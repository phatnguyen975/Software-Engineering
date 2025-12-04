import fs from "fs";
import path from "path";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export const writeJSON = (filePath, data) => {
  const fullPath = path.join(__dirname, "..", "data", filePath);
  fs.writeFileSync(fullPath, JSON.stringify(data, null, 2), "utf8");
};
