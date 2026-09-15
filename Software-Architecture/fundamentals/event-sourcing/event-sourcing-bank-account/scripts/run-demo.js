"use strict";

const path = require("path");
const fs = require("fs");
const { execSync } = require("child_process");
const { FileEventStore } = require("../src/eventstore/FileEventStore");
const { SnapshotStore } = require("../src/snapshot/SnapshotStore");
const { BankAccountService } = require("../src/application/BankAccountService");
const {
  AccountBalanceProjection,
} = require("../src/projections/AccountBalanceProjection");
const {
  TransactionHistoryProjection,
} = require("../src/projections/TransactionHistoryProjection");
const { Projector } = require("../src/projections/Projector");

const DATA_DIR = path.join(__dirname, "..", "data");

function section(title) {
  console.log("\n" + "=".repeat(70));
  console.log(title);
  console.log("=".repeat(70));
}

function main() {
  // Start from a clean slate every time this demo is run.
  execSync(`node ${path.join(__dirname, "reset.js")}`, { stdio: "inherit" });

  const eventStore = new FileEventStore(DATA_DIR);
  // everyNEvents = 3 so you will actually SEE a snapshot file appear during this short demo.
  const snapshotStore = new SnapshotStore(DATA_DIR, { everyNEvents: 3 });
  const service = new BankAccountService(eventStore, snapshotStore);

  const balanceProjection = new AccountBalanceProjection(DATA_DIR);
  const historyProjection = new TransactionHistoryProjection(DATA_DIR);
  const projector = new Projector(eventStore, [
    balanceProjection,
    historyProjection,
  ]);

  const accountId = "account-alice";

  // ---------------------------------------------------------------------
  section("1) OPEN ACCOUNT — first command, produces the first event");
  // ---------------------------------------------------------------------
  service.openAccount({ accountId, ownerName: "Alice", initialBalance: 100 });
  console.log("Opened account for Alice with initial balance 100.");
  console.log(
    "Current state (rebuilt by replay):",
    service.loadAccount(accountId),
  );

  // ---------------------------------------------------------------------
  section("2) DEPOSIT & WITHDRAW — normal command flow");
  // ---------------------------------------------------------------------
  service.deposit({ accountId, amount: 50 });
  service.withdraw({ accountId, amount: 30 });
  console.log("Deposited 50, then withdrew 30.");
  console.log("Current state:", service.loadAccount(accountId));

  // ---------------------------------------------------------------------
  section("3) REJECTED COMMAND — business rule violation writes NOTHING");
  // ---------------------------------------------------------------------
  const versionBeforeRejectedCommand = service.loadAccount(accountId).version;
  try {
    service.withdraw({ accountId, amount: 999999 });
  } catch (err) {
    console.log(
      `Withdrawal of 999999 rejected as expected: ${err.name}: ${err.message}`,
    );
  }
  const versionAfterRejectedCommand = service.loadAccount(accountId).version;
  console.log(
    `Stream version unchanged by the rejected command: before=${versionBeforeRejectedCommand}, after=${versionAfterRejectedCommand}`,
  );

  // ---------------------------------------------------------------------
  section("4) OPTIMISTIC CONCURRENCY CONFLICT — simulating two racing writers");
  // ---------------------------------------------------------------------
  // Imagine two requests both read the account at the SAME version (a "race").
  const staleView = service.loadAccount(accountId); // both readers see this version
  service.deposit({ accountId, amount: 10 }); // "writer A" commits first, stream advances
  try {
    // "writer B" now tries to append based on the OLD version it read before writer A committed.
    eventStore.append(accountId, staleView.version, [
      { eventType: "MoneyDeposited", data: { amount: 5 } },
    ]);
  } catch (err) {
    console.log(`Writer B correctly rejected: ${err.name}: ${err.message}`);
    console.log(
      "=> In real code, writer B would reload the aggregate and retry the command.",
    );
  }

  // ---------------------------------------------------------------------
  section("5) IDEMPOTENT COMMAND HANDLING — safe retries over the network");
  // ---------------------------------------------------------------------
  const idemKey = "client-retry-key-001";
  const first = service.deposit({
    accountId,
    amount: 20,
    idempotencyKey: idemKey,
  });
  const balanceAfterFirst = service.loadAccount(accountId).balance;
  const second = service.deposit({
    accountId,
    amount: 20,
    idempotencyKey: idemKey,
  }); // simulated network retry
  const balanceAfterRetry = service.loadAccount(accountId).balance;
  console.log(
    `First call:  alreadyProcessed=${first.alreadyProcessed}, balance afterwards=${balanceAfterFirst}`,
  );
  console.log(
    `Retry call:  alreadyProcessed=${second.alreadyProcessed}, balance afterwards=${balanceAfterRetry}`,
  );
  console.log(
    balanceAfterFirst === balanceAfterRetry
      ? "=> Balance is IDENTICAL after the retry — the duplicate deposit was correctly ignored."
      : "=> BUG: balance changed on retry!",
  );

  // ---------------------------------------------------------------------
  section("6) CLOSE ACCOUNT, then attempt a command on a closed account");
  // ---------------------------------------------------------------------
  service.close({ accountId });
  try {
    service.deposit({ accountId, amount: 1 });
  } catch (err) {
    console.log(
      `Deposit into a closed account correctly rejected: ${err.name}: ${err.message}`,
    );
  }

  // ---------------------------------------------------------------------
  section("7) SNAPSHOT — check whether a snapshot was taken during this run");
  // ---------------------------------------------------------------------
  const snapshot = snapshotStore.load(accountId);
  console.log(
    snapshot
      ? "Snapshot found:"
      : "No snapshot yet (fewer than 3 events since last one):",
    snapshot,
  );

  // ---------------------------------------------------------------------
  section("8) PROJECTIONS — build read models by folding the event log");
  // ---------------------------------------------------------------------
  projector.catchUp();
  console.log(
    "AccountBalanceProjection ->",
    balanceProjection.getBalance(accountId),
  );
  console.log(
    "TransactionHistoryProjection ->",
    JSON.stringify(historyProjection.getHistory(accountId), null, 2),
  );

  // ---------------------------------------------------------------------
  section("9) RAW EVENT LOG — the actual source of truth on disk");
  // ---------------------------------------------------------------------
  const rawStream = fs.readFileSync(
    path.join(DATA_DIR, "events", `${accountId}.jsonl`),
    "utf8",
  );
  console.log(`data/events/${accountId}.jsonl:\n` + rawStream);

  // ---------------------------------------------------------------------
  section(
    "DONE — try: node scripts/replay.js  (rebuild projections from scratch)",
  );
  // ---------------------------------------------------------------------
}

main();
