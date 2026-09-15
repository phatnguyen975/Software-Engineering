"use strict";

/**
 * Projector — drives one or more projections by reading the event store's global log
 * from each projection's last saved cursor position ("catch up"), or from scratch ("rebuild").
 *
 * In a real production system this loop would run continuously in a background worker,
 * polling or subscribing to the event store. Here it is invoked on demand for clarity.
 */
class Projector {
  constructor(eventStore, projections) {
    this.eventStore = eventStore;
    this.projections = projections; // array of projection instances
  }

  /** Apply only the events each projection hasn't seen yet (normal operation). */
  catchUp() {
    for (const projection of this.projections) {
      const entries = this.eventStore.loadAll(projection.getPosition());
      if (entries.length > 0) projection.applyBatch(entries);
    }
  }

  /**
   * Wipe every projection and replay the ENTIRE event log from position 0.
   * This is the concrete demonstration of "the event log is the source of truth" —
   * everything the read side knows can be thrown away and perfectly reconstructed.
   */
  rebuildAll() {
    for (const projection of this.projections) projection.reset();
    this.catchUp();
  }
}

module.exports = { Projector };
