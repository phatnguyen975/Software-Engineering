"use strict";

// Event type name constants — always past tense, since these describe facts that already happened.
const EventTypes = Object.freeze({
  ACCOUNT_OPENED: "AccountOpened",
  MONEY_DEPOSITED: "MoneyDeposited",
  MONEY_WITHDRAWN: "MoneyWithdrawn",
  ACCOUNT_CLOSED: "AccountClosed",
});

// "Draft" events: produced by the aggregate, NOT yet persisted (no version/eventId/timestamp yet).
// The EventStore is responsible for stamping version, eventId and timestamp at append time.

function accountOpened({ ownerName, initialBalance }) {
  return {
    eventType: EventTypes.ACCOUNT_OPENED,
    data: { ownerName, initialBalance },
  };
}

function moneyDeposited({ amount }) {
  return { eventType: EventTypes.MONEY_DEPOSITED, data: { amount } };
}

function moneyWithdrawn({ amount }) {
  return { eventType: EventTypes.MONEY_WITHDRAWN, data: { amount } };
}

function accountClosed() {
  return { eventType: EventTypes.ACCOUNT_CLOSED, data: {} };
}

module.exports = {
  EventTypes,
  accountOpened,
  moneyDeposited,
  moneyWithdrawn,
  accountClosed,
};
