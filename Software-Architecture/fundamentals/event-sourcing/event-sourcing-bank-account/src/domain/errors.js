"use strict";

class DomainError extends Error {
  constructor(message) {
    super(message);
    this.name = this.constructor.name;
  }
}

class AccountNotFoundError extends DomainError {
  constructor(accountId) {
    super(`Account not found: ${accountId}`);
  }
}

class AccountAlreadyExistsError extends DomainError {
  constructor(accountId) {
    super(`Account already exists: ${accountId}`);
  }
}

class AccountNotOpenError extends DomainError {
  constructor(accountId, status) {
    super(`Account ${accountId} is not open (status=${status})`);
  }
}

class InsufficientFundsError extends DomainError {
  constructor(accountId, balance, amount) {
    super(
      `Account ${accountId} has insufficient funds: balance=${balance}, requested=${amount}`,
    );
  }
}

class ConcurrencyConflictError extends DomainError {
  constructor(streamId, expectedVersion, actualVersion) {
    super(
      `Concurrency conflict on stream '${streamId}': expected version ${expectedVersion}, actual version ${actualVersion}`,
    );
    this.code = "CONCURRENCY_CONFLICT";
    this.streamId = streamId;
    this.expectedVersion = expectedVersion;
    this.actualVersion = actualVersion;
  }
}

module.exports = {
  DomainError,
  AccountNotFoundError,
  AccountAlreadyExistsError,
  AccountNotOpenError,
  InsufficientFundsError,
  ConcurrencyConflictError,
};
