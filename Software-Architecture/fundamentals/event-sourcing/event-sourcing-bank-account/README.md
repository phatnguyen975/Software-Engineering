# Event Sourcing Reference Implementation — Bank Account

A complete, runnable, production-style example of **Event Sourcing** in plain Node.js — **zero external dependencies**, so there is nothing to install and no framework to configure. Every mechanic described in the companion tutorial (`event-sourcing-tutorial.md`) is demonstrated here with real, working code you can run and inspect.

Domain: a simple **Bank Account** that can be opened, deposited into, withdrawn from, and closed.

## Requirements

- Node.js >= 16 (check with `node --version`)
- No `npm install` needed — the project has no external dependencies.

## How to Run

```bash
# 1. Run the full guided demo (resets data, then walks through every concept step by step)
node scripts/run-demo.js

# 2. Prove the read models are fully rebuildable from the event log alone
node scripts/replay.js

# 3. (Optional) Wipe all generated data to start over
node scripts/reset.js
```

Or, if you prefer npm scripts:

```bash
npm run demo
npm run replay
npm run reset
```

## What `run-demo.js` Does, Step by Step

The script prints a numbered section for each concept. Here is what to look for in the console output for each one:

| Step | Concept                  | What to observe                                                                                                                                                             |
| ---- | ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | Basic command → event    | An `AccountOpened` event is appended; state is shown as _rebuilt by replay_, not read from a cached field.                                                                  |
| 2    | Normal command flow      | Deposit/withdraw each append exactly one event; balance updates by folding.                                                                                                 |
| 3    | Rejected command         | An over-withdrawal throws `InsufficientFundsError`. The stream **version does not change** — proof that rejected commands never touch the event log.                        |
| 4    | Optimistic concurrency   | Two "writers" read the same version; the second one's `append()` call is rejected with `ConcurrencyConflictError` because the stream moved on underneath it.                |
| 5    | Idempotency              | The exact same `idempotencyKey` is sent twice (simulating a network retry). The second call reports `alreadyProcessed: true` and the balance is **not** double-incremented. |
| 6    | Closing & guarding state | Once closed, a further deposit is rejected by the aggregate's own business rule.                                                                                            |
| 7    | Snapshot                 | With `everyNEvents: 3` configured, a snapshot file should already exist by this point — printed directly from `data/snapshots/account-alice.json`.                          |
| 8    | Projections              | `AccountBalanceProjection` and `TransactionHistoryProjection` are both built by folding the _same_ global event log — two independent read models, one source of truth.     |
| 9    | Raw event log            | The actual JSON-Lines file on disk (`data/events/account-alice.jsonl`) is printed — this is the literal source of truth the whole system is derived from.                   |

## What `replay.js` Does

This is the single most important proof-of-concept in event sourcing: it **deletes both projections** (but never touches `data/events/*`, which is the only source of truth) and rebuilds them from position 0 by replaying the entire event log. Run it after `run-demo.js` and compare the output — it will exactly match what the demo already showed you, just reconstructed from scratch.

```bash
node scripts/run-demo.js   # generates history + live projections
node scripts/replay.js     # wipes projections, rebuilds them purely from data/events/
```

## Where to Look on Disk After Running the Demo

```
data/
├── events/
│   ├── account-alice.jsonl     # one line per event for THIS account, in order — the source of truth
│   └── _global.jsonl           # every event, across all accounts, in global write order — feeds projections
├── snapshots/
│   └── account-alice.json      # periodic optimization: { version, state } — safe to delete anytime
└── projections/
    ├── account-balance.json        # read model: current balance/status per account
    └── transaction-history.json    # read model: chronological list of transactions per account
```

Open `data/events/account-alice.jsonl` in a text editor after running the demo — every line is a real, immutable, timestamped fact. That file, and nothing else, is what the entire system's state is derived from.

## Extending This Example

- **Add a new event type** (e.g. `InterestApplied`): add it to `src/domain/events.js`, handle it in `BankAccount.apply()`, add a command method, then update `AccountBalanceProjection` to react to it — no changes needed to the event store or existing events.
- **Add a brand-new projection** (e.g. "daily deposit totals"): create a new class following the same `applyBatch(entries)` shape as the existing projections, register it with `Projector`, then run `node scripts/replay.js` — it will retroactively compute totals for all historical data, even though it didn't exist when the data was written. This is the payoff described in the tutorial's "Event Replay" section.
- **Swap the storage backend**: `FileEventStore` implements a small interface (`append`, `loadStream`, `loadAll`, `findByIdempotencyKey`). Replacing it with a PostgreSQL- or EventStoreDB-backed implementation requires no changes anywhere else in the codebase — this is the same Dependency Inversion principle covered in the Clean/Hexagonal Architecture tutorial.

## Project Structure

```
event-sourcing-bank-account/
├── package.json
├── README.md
├── src/
│   ├── domain/
│   │   ├── events.js              # event type constants + draft-event factory functions
│   │   ├── errors.js              # domain error types (InsufficientFundsError, ConcurrencyConflictError, ...)
│   │   └── BankAccount.js         # the aggregate: apply() + command-handling methods
│   ├── eventstore/
│   │   └── FileEventStore.js      # append-only JSONL event store with optimistic concurrency control
│   ├── snapshot/
│   │   └── SnapshotStore.js       # periodic state snapshots (pure optimization)
│   ├── projections/
│   │   ├── AccountBalanceProjection.js       # read model #1
│   │   ├── TransactionHistoryProjection.js   # read model #2
│   │   └── Projector.js                      # generic catch-up / rebuild runner
│   └── application/
│       └── BankAccountService.js  # command handlers: load -> validate -> append -> snapshot
├── scripts/
│   ├── run-demo.js                # guided, narrated walkthrough of every concept
│   ├── replay.js                  # rebuild all projections from the event log alone
│   └── reset.js                   # wipe all generated data
└── data/                          # generated at runtime — see table above
    ├── events/
    ├── snapshots/
    └── projections/
```
