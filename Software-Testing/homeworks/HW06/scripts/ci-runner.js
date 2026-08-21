const { execSync } = require("child_process");
const fs = require("fs");
const path = require("path");

const changedFilesStr = process.env.CHANGED_FILES || "";
// Filter out empty strings
const changedFiles = changedFilesStr
  .split(" ")
  .map((s) => s.trim())
  .filter(Boolean);

let runAll = false;
const collectionsToRun = new Set();

for (const file of changedFiles) {
  if (file.startsWith("backend/")) {
    runAll = true;
    break;
  }
  if (file.startsWith("postman/collections/")) {
    const parts = file.split("/");
    if (parts.length >= 3) {
      collectionsToRun.add(parts[2]);
    }
  }
}

const allCollections = fs
  .readdirSync(path.join(__dirname, "../postman/collections"), {
    withFileTypes: true,
  })
  .filter((dirent) => dirent.isDirectory())
  .map((dirent) => dirent.name);

let targetCollections = [];

// If there are no specific changed files provided, run all (e.g. manual trigger)
if (runAll || changedFiles.length === 0) {
  targetCollections = allCollections;
  console.log(
    "Backend changed or run all forced. Running all collections:",
    targetCollections,
  );
} else {
  targetCollections = Array.from(collectionsToRun).filter((c) =>
    allCollections.includes(c),
  );
  if (targetCollections.length === 0) {
    console.log("No backend or collection changes detected. Skipping tests.");
    process.exit(0);
  }
  console.log(
    "Specific collection changes detected. Running:",
    targetCollections,
  );
}

let hasError = false;

for (const collection of targetCollections) {
  console.log(`\n======================================================`);
  console.log(`Running Newman for: ${collection}`);
  console.log(`======================================================\n`);

  // Ensure output directory exists
  if (!fs.existsSync(path.join(__dirname, "../postman/reports"))) {
    fs.mkdirSync(path.join(__dirname, "../postman/reports"), {
      recursive: true,
    });
  }

  let cmd =
    `npx newman run postman/collections/${collection}/collection.json ` +
    `-e postman/environments/local.json ` +
    `--reporters cli,htmlextra,json ` +
    `--reporter-htmlextra-export postman/reports/${collection}-report.html ` +
    `--reporter-json-export postman/reports/${collection}-summary.json`;

  // Add data file if exists
  if (
    fs.existsSync(
      path.join(
        __dirname,
        `../postman/collections/${collection}/data-domain.csv`,
      ),
    )
  ) {
    cmd += ` --iteration-data postman/collections/${collection}/data-domain.csv`;
  }

  try {
    execSync(cmd, { stdio: "inherit" });
  } catch (err) {
    hasError = true;
    console.error(`\n❌ Tests failed for collection: ${collection}`);
  }
}

if (hasError) {
  console.error("\n❌ One or more collections failed the tests.");
  process.exit(1);
} else {
  console.log("\n✅ All specified collections passed the tests.");
}
