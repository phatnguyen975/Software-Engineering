"use strict";

const fs = require("fs");
const path = require("path");
const crypto = require("crypto");
const { ConcurrencyConflictError } = require("../domain/errors");

/**
 * FileEventStore — an append-only event store backed by JSON-Lines files.
 *
 * Layout on disk:
 *   data/events/<streamId>.jsonl   one line per event for that aggregate, in order
 *   data/events/_global.jsonl      one line per event, in GLOBAL write order (feeds projections)
 *
 * This is intentionally simple (no external DB) so the mechanics of event sourcing are visible
 * as plain readable files. In production you would typically use PostgreSQL (an append-only
 * `events` table with a UNIQUE(stream_id, version) constraint for concurrency control) or a
 * purpose-built store like EventStoreDB — the *interface* below stays the same either way.
 */
class FileEventStore {
  constructor(baseDir) {
    this.eventsDir = path.join(baseDir, "events");
    this.globalLogPath = path.join(this.eventsDir, "_global.jsonl");
    fs.mkdirSync(this.eventsDir, { recursive: true });
    if (!fs.existsSync(this.globalLogPath))
      fs.writeFileSync(this.globalLogPath, "");
  }

  streamPath(streamId) {
    return path.join(this.eventsDir, `${streamId}.jsonl`);
  }

  /** Current version of a stream = index of the last event, or -1 if the stream has no events yet. */
  currentVersion(streamId) {
    const events = this._readJsonLines(this.streamPath(streamId));
    return events.length === 0 ? -1 : events[events.length - 1].version;
  }

  /**
   * Append draft events to a stream, enforcing optimistic concurrency:
   * the caller must state the version it BELIEVES the stream is currently at.
   * If another writer has appended in the meantime, this throws ConcurrencyConflictError
   * and nothing is written — the caller must reload and retry.
   */
  append(streamId, expectedVersion, draftEvents, { idempotencyKey } = {}) {
    const actualVersion = this.currentVersion(streamId);
    if (actualVersion !== expectedVersion) {
      throw new ConcurrencyConflictError(
        streamId,
        expectedVersion,
        actualVersion,
      );
    }

    const now = new Date().toISOString();
    const stamped = draftEvents.map((draft, i) => ({
      eventId: crypto.randomUUID(),
      eventType: draft.eventType,
      aggregateId: streamId,
      version: expectedVersion + 1 + i,
      timestamp: now,
      idempotencyKey: idempotencyKey ?? null,
      data: draft.data,
    }));

    const lines = stamped.map((e) => JSON.stringify(e)).join("\n") + "\n";
    fs.appendFileSync(this.streamPath(streamId), lines);
    fs.appendFileSync(this.globalLogPath, lines);

    return stamped;
  }

  /** Load all events for one stream, optionally only those AFTER a given version (for snapshot resume). */
  loadStream(streamId, afterVersion = -1) {
    return this._readJsonLines(this.streamPath(streamId)).filter(
      (e) => e.version > afterVersion,
    );
  }

  /** Load the full global event log, optionally only events written after a given global line offset. */
  loadAll(afterPosition = -1) {
    return this._readJsonLines(this.globalLogPath)
      .map((event, position) => ({ event, position }))
      .filter(({ position }) => position > afterPosition);
  }

  /** Look up an already-processed command by idempotency key, to make command handling idempotent. */
  findByIdempotencyKey(idempotencyKey) {
    if (!idempotencyKey) return null;
    return (
      this._readJsonLines(this.globalLogPath).find(
        (e) => e.idempotencyKey === idempotencyKey,
      ) || null
    );
  }

  _readJsonLines(filePath) {
    if (!fs.existsSync(filePath)) return [];
    const content = fs.readFileSync(filePath, "utf8");
    return content
      .split("\n")
      .filter((line) => line.trim().length > 0)
      .map((line) => JSON.parse(line));
  }
}

module.exports = { FileEventStore };
