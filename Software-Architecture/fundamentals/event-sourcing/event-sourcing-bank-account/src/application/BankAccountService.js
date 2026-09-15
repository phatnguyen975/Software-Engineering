"use strict";

const { BankAccount } = require("../domain/BankAccount");
const { AccountNotFoundError } = require("../domain/errors");

class BankAccountService {
  constructor(eventStore, snapshotStore) {
    this.eventStore = eventStore;
    this.snapshotStore = snapshotStore;
  }

  /** Load current aggregate state = latest snapshot (if any) + replay of events since that snapshot. */
  loadAccount(accountId) {
    const snapshot = this.snapshotStore.load(accountId);
    const account = new BankAccount(accountId);
    if (snapshot) {
      Object.assign(account, snapshot.state);
      account.version = snapshot.version;
    }
    const eventsSinceSnapshot = this.eventStore.loadStream(
      accountId,
      account.version,
    );
    for (const event of eventsSinceSnapshot) account.apply(event);
    return account;
  }

  /** After a successful command, persist a snapshot if the policy says it's time. */
  _maybeSnapshot(account) {
    if (this.snapshotStore.shouldSnapshot(account.id, account.version)) {
      this.snapshotStore.save(account.id, account.version, {
        id: account.id,
        ownerName: account.ownerName,
        balance: account.balance,
        status: account.status,
      });
    }
  }

  /**
   * Shared command-handling flow, made idempotent via `idempotencyKey`:
   *   1. If this exact command was already processed, return its already-recorded result — don't reapply.
   *   2. Otherwise load the aggregate, run the business method, append the resulting events
   *      with an optimistic-concurrency check against the version we loaded.
   */
  _handle(accountId, idempotencyKey, businessMethod) {
    if (idempotencyKey) {
      const existing = this.eventStore.findByIdempotencyKey(idempotencyKey);
      if (existing) return { alreadyProcessed: true, event: existing };
    }

    const account = this.loadAccount(accountId);
    const draftEvents = businessMethod(account); // may throw a domain error -> nothing persisted
    const stamped = this.eventStore.append(
      accountId,
      account.version,
      draftEvents,
      { idempotencyKey },
    );
    for (const event of stamped) account.apply(event);
    this._maybeSnapshot(account);
    return { alreadyProcessed: false, account, events: stamped };
  }

  openAccount({ accountId, ownerName, initialBalance, idempotencyKey }) {
    return this._handle(accountId, idempotencyKey, (account) =>
      account.open(ownerName, initialBalance),
    );
  }

  deposit({ accountId, amount, idempotencyKey }) {
    return this._handle(accountId, idempotencyKey, (account) =>
      account.deposit(amount),
    );
  }

  withdraw({ accountId, amount, idempotencyKey }) {
    return this._handle(accountId, idempotencyKey, (account) =>
      account.withdraw(amount),
    );
  }

  close({ accountId, idempotencyKey }) {
    return this._handle(accountId, idempotencyKey, (account) =>
      account.close(),
    );
  }

  getAccountOrThrow(accountId) {
    const account = this.loadAccount(accountId);
    if (account.status === "NONEXISTENT")
      throw new AccountNotFoundError(accountId);
    return account;
  }
}

module.exports = { BankAccountService };
