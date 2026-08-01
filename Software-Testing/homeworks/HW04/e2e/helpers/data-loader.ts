import fs from "fs";
import path from "path";

/**
 * Loads test data from a JSON file located in the `data/` directory.
 *
 * @param filename - The JSON filename to load
 * @returns A typed array of test data records
 *
 * @example
 * const cases = loadTestData<RegistrationCase>('product-data.json');
 */
export function loadTestData<T>(filename: string): T[] {
  const filePath = path.resolve(__dirname, "..", "data", filename);

  if (!fs.existsSync(filePath)) {
    throw new Error(`[data-loader] Test data file not found: ${filePath}`);
  }

  const raw = fs.readFileSync(filePath, "utf-8");

  try {
    const parsed: unknown = JSON.parse(raw);
    if (!Array.isArray(parsed)) {
      throw new Error(
        `[data-loader] Expected an array in ${filename}, got: ${typeof parsed}`,
      );
    }
    return parsed as T[];
  } catch (err) {
    throw new Error(
      `[data-loader] Failed to parse ${filename}: ${(err as Error).message}`,
    );
  }
}
