"use strict";

const fs = require("fs");
const path = require("path");

/**
 * Read model answering: "show me the transaction history for account X."
 * A second, independent projection fed from the SAME event stream as AccountBalanceProjection —
 * this is the payoff of event sourcing: one write model, many purpose-built read models.
 */
class TransactionHistoryProjection {
  constructor(baseDir) {
    this.name = "transaction-history";
    this.filePath = path.join(baseDir, "projections", `${this.name}.json`);
    fs.mkdirSync(path.dirname(this.filePath), { recursive: true });
    if (!fs.existsSync(this.filePath))
      this._write({ position: -1, transactions: {} });
  }

  _read() {
    return JSON.parse(fs.readFileSync(this.filePath, "utf8"));
  }

  _write(state) {
    fs.writeFileSync(this.filePath, JSON.stringify(state, null, 2));
  }

  getPosition() {
    return this._read().position;
  }

  reset() {
    this._write({ position: -1, transactions: {} });
  }

  applyBatch(entries) {
    const state = this._read();
    for (const { event, position } of entries) {
      if (!state.transactions[event.aggregateId])
        state.transactions[event.aggregateId] = [];
      const entry = {
        type: event.eventType,
        timestamp: event.timestamp,
        version: event.version,
      };
      if (
        event.eventType === "MoneyDeposited" ||
        event.eventType === "MoneyWithdrawn"
      ) {
        entry.amount = event.data.amount;
      }
      state.transactions[event.aggregateId].push(entry);
      state.position = position;
    }
    this._write(state);
  }

  getHistory(accountId) {
    return this._read().transactions[accountId] || [];
  }
}

module.exports = { TransactionHistoryProjection };
