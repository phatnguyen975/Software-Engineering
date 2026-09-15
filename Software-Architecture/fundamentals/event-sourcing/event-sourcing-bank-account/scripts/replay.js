"use strict";

const path = require("path");
const { FileEventStore } = require("../src/eventstore/FileEventStore");
const {
  AccountBalanceProjection,
} = require("../src/projections/AccountBalanceProjection");
const {
  TransactionHistoryProjection,
} = require("../src/projections/TransactionHistoryProjection");
const { Projector } = require("../src/projections/Projector");

const DATA_DIR = path.join(__dirname, "..", "data");

/**
 * This script proves the central claim of event sourcing:
 * "the read models are 100% disposable and can always be perfectly reconstructed
 *  from the append-only event log — nothing is lost."
 *
 * Run `node scripts/run-demo.js` first to generate some event history, then run this script.
 * It wipes BOTH projections (but never touches data/events/*, the source of truth) and
 * rebuilds them from position 0 by replaying the entire global event log.
 */
function main() {
  const eventStore = new FileEventStore(DATA_DIR);
  const balanceProjection = new AccountBalanceProjection(DATA_DIR);
  const historyProjection = new TransactionHistoryProjection(DATA_DIR);
  const projector = new Projector(eventStore, [
    balanceProjection,
    historyProjection,
  ]);

  const totalEvents = eventStore.loadAll(-1).length;
  console.log(
    `Global event log contains ${totalEvents} event(s). Wiping projections and replaying...\n`,
  );

  projector.rebuildAll();

  console.log("Rebuilt AccountBalanceProjection:");
  console.log(JSON.stringify(balanceProjection.getAll(), null, 2));

  console.log("\nRebuilt TransactionHistoryProjection (per account):");
  const allAccountIds = Object.keys(balanceProjection.getAll());
  for (const accountId of allAccountIds) {
    console.log(
      `  ${accountId}:`,
      JSON.stringify(historyProjection.getHistory(accountId)),
    );
  }

  console.log(
    "\n=> Both read models were fully reconstructed from data/events/*.jsonl alone.",
  );
}

main();
