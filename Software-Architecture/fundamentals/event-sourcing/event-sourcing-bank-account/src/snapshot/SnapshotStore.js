"use strict";

const fs = require("fs");
const path = require("path");

/**
 * SnapshotStore — a pure OPTIMIZATION, never a source of truth.
 * Deleting every file this class manages must never change the final state the system computes;
 * it only changes how many events must be replayed to get there.
 */
class SnapshotStore {
  constructor(baseDir, { everyNEvents = 5 } = {}) {
    this.dir = path.join(baseDir, "snapshots");
    this.everyNEvents = everyNEvents;
    fs.mkdirSync(this.dir, { recursive: true });
  }

  filePath(streamId) {
    return path.join(this.dir, `${streamId}.json`);
  }

  load(streamId) {
    const file = this.filePath(streamId);
    if (!fs.existsSync(file)) return null;
    return JSON.parse(fs.readFileSync(file, "utf8"));
  }

  save(streamId, version, state) {
    fs.writeFileSync(
      this.filePath(streamId),
      JSON.stringify(
        { version, state, savedAt: new Date().toISOString() },
        null,
        2,
      ),
    );
  }

  /** Decide whether it's time to snapshot again, based on how many events have passed since the last one. */
  shouldSnapshot(streamId, currentVersion) {
    const snapshot = this.load(streamId);
    const lastSnapshotVersion = snapshot ? snapshot.version : -1;
    return currentVersion - lastSnapshotVersion >= this.everyNEvents;
  }
}

module.exports = { SnapshotStore };
