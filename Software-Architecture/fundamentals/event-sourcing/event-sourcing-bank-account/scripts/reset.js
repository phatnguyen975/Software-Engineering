"use strict";

const fs = require("fs");
const path = require("path");

const dataDir = path.join(__dirname, "..", "data");

for (const sub of ["events", "snapshots", "projections"]) {
  const dir = path.join(dataDir, sub);
  fs.rmSync(dir, { recursive: true, force: true });
  fs.mkdirSync(dir, { recursive: true });
}

console.log(`Data directory reset: ${dataDir}`);
