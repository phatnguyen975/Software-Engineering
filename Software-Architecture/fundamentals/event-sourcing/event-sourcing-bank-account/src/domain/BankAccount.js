"use strict";

const {
  EventTypes,
  accountOpened,
  moneyDeposited,
  moneyWithdrawn,
  accountClosed,
} = require("./events");
const {
  AccountAlreadyExistsError,
  AccountNotOpenError,
  InsufficientFundsError,
} = require("./errors");

const STATUS = Object.freeze({
  NONEXISTENT: "NONEXISTENT",
  OPEN: "OPEN",
  CLOSED: "CLOSED",
});

class BankAccount {
  constructor(id) {
    this.id = id;
    this.ownerName = null;
    this.balance = 0;
    this.status = STATUS.NONEXISTENT;
    this.version = -1; // -1 = no events applied yet (brand new / does not exist)
  }

  /**
   * Rebuild an aggregate purely by folding over its event history.
   * This is THE core mechanic of event sourcing: current state = fold(events).
   */
  static fromHistory(id, events) {
    const account = new BankAccount(id);
    for (const event of events) account.apply(event);
    return account;
  }

  /**
   * Mutates in-memory state from ONE event. Used both:
   *   (a) when replaying history to rebuild state, and
   *   (b) right after a command produces new events, to keep the in-memory instance current.
   * This method must be a pure, deterministic function of (currentState, event) -> newState.
   */
  apply(event) {
    switch (event.eventType) {
      case EventTypes.ACCOUNT_OPENED:
        this.ownerName = event.data.ownerName;
        this.balance = event.data.initialBalance;
        this.status = STATUS.OPEN;
        break;
      case EventTypes.MONEY_DEPOSITED:
        this.balance += event.data.amount;
        break;
      case EventTypes.MONEY_WITHDRAWN:
        this.balance -= event.data.amount;
        break;
      case EventTypes.ACCOUNT_CLOSED:
        this.status = STATUS.CLOSED;
        break;
      default:
        throw new Error(`Unknown event type: ${event.eventType}`);
    }
    this.version = event.version;
  }

  // ---- Command handlers: validate business rules, return DRAFT events (not yet persisted) ----

  open(ownerName, initialBalance) {
    if (this.status !== STATUS.NONEXISTENT)
      throw new AccountAlreadyExistsError(this.id);
    if (initialBalance < 0)
      throw new Error("Initial balance cannot be negative");
    return [accountOpened({ ownerName, initialBalance })];
  }

  deposit(amount) {
    this.assertOpen();
    if (amount <= 0) throw new Error("Deposit amount must be positive");
    return [moneyDeposited({ amount })];
  }

  withdraw(amount) {
    this.assertOpen();
    if (amount <= 0) throw new Error("Withdrawal amount must be positive");
    if (amount > this.balance)
      throw new InsufficientFundsError(this.id, this.balance, amount);
    return [moneyWithdrawn({ amount })];
  }

  close() {
    this.assertOpen();
    return [accountClosed()];
  }

  assertOpen() {
    if (this.status !== STATUS.OPEN)
      throw new AccountNotOpenError(this.id, this.status);
  }
}

module.exports = { BankAccount, STATUS };
