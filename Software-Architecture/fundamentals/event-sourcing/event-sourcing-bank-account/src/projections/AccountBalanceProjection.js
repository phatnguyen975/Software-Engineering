"use strict";

const fs = require("fs");
const path = require("path");

/**
 * Read model answering: "what is account X's current balance/status right now?"
 * Rebuilt by folding over the GLOBAL event stream. Disposable: delete the file, replay, done.
 */
class AccountBalanceProjection {
  constructor(baseDir) {
    this.name = "account-balance";
    this.filePath = path.join(baseDir, "projections", `${this.name}.json`);
    fs.mkdirSync(path.dirname(this.filePath), { recursive: true });
    if (!fs.existsSync(this.filePath))
      this._write({ position: -1, accounts: {} });
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
    this._write({ position: -1, accounts: {} });
  }

  /** Apply a batch of {event, position} entries and persist the new state + cursor position. */
  applyBatch(entries) {
    const state = this._read();
    for (const { event, position } of entries) {
      const acc = state.accounts[event.aggregateId] || {
        balance: 0,
        ownerName: null,
        status: "NONEXISTENT",
      };
      switch (event.eventType) {
        case "AccountOpened":
          acc.ownerName = event.data.ownerName;
          acc.balance = event.data.initialBalance;
          acc.status = "OPEN";
          break;
        case "MoneyDeposited":
          acc.balance += event.data.amount;
          break;
        case "MoneyWithdrawn":
          acc.balance -= event.data.amount;
          break;
        case "AccountClosed":
          acc.status = "CLOSED";
          break;
      }
      state.accounts[event.aggregateId] = acc;
      state.position = position;
    }
    this._write(state);
  }

  getBalance(accountId) {
    return this._read().accounts[accountId] || null;
  }

  getAll() {
    return this._read().accounts;
  }
}

module.exports = { AccountBalanceProjection };
