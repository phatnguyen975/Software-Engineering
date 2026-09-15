# Designing a Modular Monolith with Clean Architecture in Go (Gin)

> How to structure a Go + Gin backend so it stays a single deployable service while its internals behave like well-isolated modules with enforced boundaries — the folder layout, the layering rules, how modules talk to each other, and how to keep the architecture from eroding as the codebase grows.

## Table of Contents

1. [What "Modular Monolith" and "Clean Architecture" Actually Mean](#1-what-modular-monolith-and-clean-architecture-actually-mean)
2. [Why Go Needs a Different Enforcement Strategy Than Framework-Heavy Stacks](#2-why-go-needs-a-different-enforcement-strategy-than-framework-heavy-stacks)
3. [The Dependency Rule, Expressed in Go Packages](#3-the-dependency-rule-expressed-in-go-packages)
4. [Recommended Root Structure](#4-recommended-root-structure)
5. [The Module Template](#5-the-module-template)
6. [A Module's Public Surface](#6-a-modules-public-surface)
7. [Inter-Module Communication](#7-inter-module-communication)
8. [Event-Driven Patterns Inside a Modular Monolith](#8-event-driven-patterns-inside-a-modular-monolith)
9. [Dependency Injection Without a Container](#9-dependency-injection-without-a-container)
10. [Enforcing the Architecture in CI](#10-enforcing-the-architecture-in-ci)
11. [When to Deviate From This Structure](#11-when-to-deviate-from-this-structure)
12. [Mindset Notes for Teams Coming From Java/Spring](#12-mindset-notes-for-teams-coming-from-javaspring)
13. [References](#13-references)

## 1. What "Modular Monolith" and "Clean Architecture" Actually Mean

These are two independent, complementary ideas, worth separating clearly before designing folder structure:

- **Modular Monolith** is a _deployment and boundary_ pattern: a single deployable unit, internally organized into modules that own their own data and logic, communicate through explicit contracts, and could — in principle — be extracted into separate services later without a rewrite. The most widely cited definition of this pattern is Kamil Grzybek's [_Modular Monolith: A Primer_](https://www.kamilgrzybek.com/blog/posts/modular-monolith-primer): a monolith is modular when a change to one module rarely requires understanding or touching another module's internals.
- **Clean Architecture** (Robert C. Martin) is a _layering_ pattern: business rules (domain, use cases) sit at the center and know nothing about delivery mechanisms (HTTP, databases, message brokers); everything technical sits at the edges and depends inward, never the other way around.

They compose naturally: Clean Architecture defines the layering **inside** each module; Modular Monolith defines the boundaries **between** modules. A codebase can have one without the other — a single-module app with perfect Clean Architecture layering, or a multi-module app where each module is a disorganized ball of mud internally — so both need to be designed for deliberately.

## 2. Why Go Needs a Different Enforcement Strategy Than Framework-Heavy Stacks

**Key points:**

- Go has no framework-level module system. There is no annotation that declares "this is a bounded context," no reflection-based scanner that fails a build when one module reaches into another's internals.
- Gin itself is a **router**, not an application framework — it has no IoC container, no ORM, no AOP, no module concept. Its entire job is HTTP routing, middleware, and request binding, as shown in the official [Gin RESTful API tutorial](https://go.dev/doc/tutorial/web-service-gin) hosted on go.dev. Everything architectural is the responsibility of how you organize your own packages.
- Go does give you **one compiler-enforced primitive** that framework-heavy stacks don't have at the language level: the `internal/` directory convention (Section 3.1). Everything beyond that is convention plus a lint tool run in CI.

This means a Go Modular Monolith is enforced through a **combination of three things**, none of which alone is sufficient:

1. Folder layout + `internal/` visibility (compiler-enforced).
2. A public "facade" package per module (convention).
3. An import-graph linter run in CI (tool-enforced), since `internal/` only blocks _external_ repositories from reaching in — it does nothing to stop one internal module from importing another module's internals directly.

## 3. The Dependency Rule, Expressed in Go Packages

### 3.1 `internal/` — Go's only compiler-enforced boundary

Any package under a directory named `internal/` can be imported **only** by code rooted at the parent of that `internal/` directory — enforced by `go build` itself, not a linter. Attempting to violate it from outside the module is a compile error.

```
myservice/
└── internal/
    └── billing/
        └── domain/        # importable only from within myservice/...
```

This is precisely why almost every serious Go service puts its application code under `internal/`: it guarantees the codebase can never accidentally leak a stable public API it didn't intend to commit to. This convention is documented in the community-maintained [golang-standards/project-layout](https://github.com/golang-standards/project-layout) reference — explicitly _not_ an official Go team standard, but the most widely recognized shared vocabulary for Go repository layout, and a reasonable starting skeleton to adapt for a project of meaningful size.

### 3.2 What `internal/` does _not_ give you

`internal/billing` can still freely import `internal/shipping` — the `internal/` rule only protects against _external_ imports, not cross-module coupling within the same repository. Closing this gap requires an explicit import-graph rule checked in CI (Section 10).

### 3.3 The Dependency Rule inside one module

Within a single module, dependencies point **inward**: infrastructure implements interfaces (ports) declared by the inner layers, never the reverse.

```
Presentation (HTTP handlers, DTOs)
      │  calls
      ▼
Application / Use Case (orchestration, port interfaces)
      │  executes
      ▼
Domain (entities, business rules — zero external imports)
      ▲  implements ports
      │
Infrastructure (database, cache, message broker, external HTTP clients)
```

Go gives you a useful, free guardrail here: it does not allow circular package imports. If `domain` ever imports `infrastructure`, and `infrastructure` imports `domain`, the build fails outright with an import-cycle error — a small but real compiler-level check on top of convention.

## 4. Recommended Root Structure

For a backend service of meaningful scope (several bounded contexts, a shared platform layer, background processing), a structure like this balances discoverability with enforceability:

```text
service/
├── cmd/
│   └── api/
│       └── main.go              # composition root — wiring only, no business logic
├── internal/
│   ├── platform/                 # shared kernel: config, db pool, logger, middleware, eventbus
│   ├── identity/                 # MODULE
│   ├── catalog/                  # MODULE
│   ├── ordering/                 # MODULE
│   ├── payments/                 # MODULE
│   └── notifications/            # MODULE
├── migrations/                   # versioned SQL migration files
├── api/                          # OpenAPI/Swagger specs (optional but recommended)
├── go.mod / go.sum
├── .go-arch-lint.yml              # architecture boundary rules (Section 10)
└── Makefile
```

**Why `cmd/` + `internal/` at the top level, rather than flattening everything:**

- `cmd/` isolates "how this binary starts" from "what the application does" — useful the moment you need a second entry point (a CLI admin tool, a one-off migration runner) without duplicating business logic.
- `internal/platform` is a _narrow_, stable "shared kernel" — things every module genuinely needs (DB connection pool, structured logger, base HTTP middleware). Keep it small; it's the one package whose churn ripples across every module, so treat additions to it with real scrutiny.

## 5. The Module Template

Each bounded context gets the same internal shape, so any engineer can navigate an unfamiliar module by pattern-matching against a familiar one:

```text
internal/ordering/
├── domain/            # entities, value objects, domain errors, domain events — pure Go
│   ├── order.go
│   ├── errors.go
│   └── events.go
├── usecase/            # orchestration + port interfaces (this layer owns the interfaces it needs)
│   ├── ports.go
│   ├── place_order.go
│   └── cancel_order.go
├── repository/          # implements usecase ports against a real datastore
│   ├── postgres_order_repo.go
│   └── redis_idempotency_store.go
├── delivery/             # HTTP handlers + request/response DTOs
│   └── http/
│       ├── handler.go
│       └── dto.go
├── doc.go                 # module purpose + allowed dependencies, in prose (Section 6.1)
└── module.go               # the module's PUBLIC SURFACE (Section 6)
```

**Layer responsibilities, briefly:**

- `domain/` — business rules and invariants, with **zero imports** outside the standard library and other domain packages. No Gin, no ORM, no HTTP status codes here. This is the layer that should still make sense if you deleted the web framework entirely.
- `usecase/` — orchestrates domain objects to fulfil one application operation; declares the interfaces (`ports.go`) it needs from the outside world (a repository, a payment gateway, an event publisher) without knowing which concrete implementation will satisfy them.
- `repository/` — concrete adapters implementing the `usecase` ports against a real database/cache/external system.
- `delivery/` — translates HTTP (or gRPC, or a message-queue consumer) into calls on the use cases, and use-case results back into responses. Contains no business logic — if a `delivery` handler needs an `if` statement deciding business outcomes, that logic belongs one layer down.

Naming note: the Go community's most cited reference implementation of this layering, [`bxcodec/go-clean-arch`](https://github.com/bxcodec/go-clean-arch), uses exactly this vocabulary (`usecase`, `repository`, `delivery`) rather than the `application`/`infrastructure`/`presentation` naming more common in Java Clean Architecture write-ups. Either vocabulary is equally valid — **consistency across every module matters far more than which words you pick**; write the choice down once and never mix conventions between modules.

## 6. A Module's Public Surface

### 6.1 Documenting module intent

Go has no package-level annotation mechanism, so a module's purpose and allowed dependencies are documented in prose, in a `doc.go` file, which `go doc`/pkg.go.dev renders as the package's top-level documentation:

```go
// Package ordering implements the Ordering bounded context: placing,
// tracking, and cancelling orders.
//
// Allowed dependencies: internal/platform, internal/catalog (read-only,
// via catalog.API).
package ordering
```

### 6.2 The facade pattern

Other modules should depend on a **narrow public interface**, never on another module's `domain`/`usecase`/`repository` packages directly. The cleanest way to express this in Go is a small `API` interface plus a constructor that wires the module's own internals and returns only that narrow surface:

```go
// internal/catalog/module.go — this module's entire public surface
package catalog

type API interface {
    GetByID(ctx context.Context, id string) (ProductSummary, error)
}

type ProductSummary struct { // a flat, immutable DTO — never a domain entity
    ID, Name string
    Price    int64
}

func New(db *sql.DB, bus *eventbus.Bus) API {
    repo := repository.NewPostgresProductRepo(db)
    uc := usecase.NewProductService(repo, bus)
    return &facade{uc: uc}
}

type facade struct{ uc *usecase.ProductService }
func (f *facade) GetByID(ctx context.Context, id string) (ProductSummary, error) {
    return f.uc.GetByID(ctx, id) // maps domain type -> DTO here if needed
}
```

Any other module that needs product data depends on `catalog.API` (an interface, satisfied implicitly per Go's structural typing), never on `internal/catalog/domain` or `internal/catalog/repository`. This single convention is what makes the rest of the enforcement story (Section 10) tractable — there is exactly one import path other modules are allowed to use.

## 7. Inter-Module Communication

Two patterns cover almost every real scenario:

### 7.1 Synchronous queries → the facade, called directly

When `ordering` needs to check product availability before placing an order, it depends on `catalog.API`, injected as a plain interface value at wiring time (Section 9). This is a normal, synchronous function call — no framework proxy, no network hop, just a Go interface method call within the same process.

### 7.2 Asynchronous, decoupled notification → an in-process event bus

When a module needs to notify _unknown, possibly multiple_ interested parties without depending on them directly (e.g., "an order was placed" — `notifications` and `analytics` both care, but `ordering` shouldn't need to know either exists), a minimal in-process publish/subscribe bus is enough — roughly 40 lines of code, written once, in the shared platform layer:

```go
type Event interface{ Topic() string }

type Bus struct {
    mu   sync.RWMutex
    subs map[string][]func(context.Context, Event)
}

func (b *Bus) Subscribe(topic string, handler func(context.Context, Event)) {
    b.mu.Lock(); defer b.mu.Unlock()
    b.subs[topic] = append(b.subs[topic], handler)
}

func (b *Bus) Publish(ctx context.Context, e Event) {
    b.mu.RLock(); handlers := b.subs[e.Topic()]; b.mu.RUnlock()
    for _, h := range handlers {
        h(ctx, e) // synchronous, in-process by default
    }
}
```

`notifications` subscribes to `"order.placed"` at wiring time in `main.go`; `ordering` publishes without knowing who (if anyone) is listening. For projects that outgrow a hand-rolled bus (need for retries, persistence, or eventually moving some subscribers out-of-process), `ThreeDotsLabs/watermill` provides a more complete pub/sub abstraction with the same mental model, covered further with tool comparisons in the ecosystem/stack guide for this stack.

**What _not_ to do:** modules calling each other's HTTP handlers over loopback, or reaching into each other's database tables directly. Both defeat the entire purpose of modularity — the first adds needless network/serialization overhead for an in-process call, the second creates the exact hidden coupling a Modular Monolith exists to prevent.

## 8. Event-Driven Patterns Inside a Modular Monolith

It's worth distinguishing four different "kinds of event," because conflating them leads to over- or under-engineered solutions:

| Kind                             | What it is                                                                                                                              | Where it lives                                                                                                                                                                                                                                                                |
| -------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Domain event**                 | Something that happened inside one aggregate's business logic, relevant only within that transaction (e.g., "order total recalculated") | A plain struct in `domain/events.go`, handled synchronously inside the same use case before it returns — no framework, no bus involved                                                                                                                                        |
| **In-process integration event** | Something one module wants to tell _other modules in the same process_ about, without knowing who's listening                           | Published via the in-process event bus (Section 7.2)                                                                                                                                                                                                                          |
| **Transactional outbox event**   | Something that must reliably reach an **external** system (a message broker, another service) even across process restarts              | Written to an `outbox_events` table in the **same database transaction** as the domain change; a separate relay process/goroutine reads unpublished rows and publishes them, then marks them dispatched                                                                       |
| **Real-time push event**         | Something a connected client (browser, mobile) needs to see immediately                                                                 | Server-Sent Events (one-way) or WebSocket (bidirectional) fed by a Pub/Sub layer (commonly Redis), so any backend instance can broadcast to any connected client regardless of which instance accepted the original write — see Section 8.1 for how to choose between the two |

**Transactional outbox, concretely:**

```go
func (uc *OrderUsecase) Place(ctx context.Context, order Order) error {
    return uc.db.WithTx(ctx, func(tx *sql.Tx) error {
        if err := uc.repo.Insert(ctx, tx, order); err != nil {
            return err
        }
        event := OrderPlacedEvent{OrderID: order.ID, At: time.Now()}
        payload, _ := json.Marshal(event)
        // Same transaction as the domain write — this is the crux of the pattern:
        // the event can never be "lost" relative to the state change it describes.
        return uc.outbox.Insert(ctx, tx, "orders.events", "order.placed", payload)
    })
}
```

A separate relay (a polling goroutine, Postgres `LISTEN/NOTIFY`, or a CDC tool for lower latency) reads unpublished outbox rows and publishes them to the broker. This pattern exists specifically to solve the "dual write" problem — writing to a database and separately publishing to a broker are not atomic unless you route through a single transactional log like this.

### 8.1 Real-time push: SSE vs. WebSocket

Both are valid transports for the "real-time push event" row above; they solve different shapes of problem, and picking the wrong one is a common source of unnecessary complexity.

|                          | Server-Sent Events (SSE)                                                                                          | WebSocket                                                                                                                                  |
| ------------------------ | ----------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| Direction                | One-way: server → client only                                                                                     | Full-duplex: server ↔ client                                                                                                               |
| Transport                | Plain HTTP (a long-lived response stream)                                                                         | A separate protocol, upgraded from an HTTP handshake                                                                                       |
| Reconnection             | Built into the browser `EventSource` API automatically, including a `Last-Event-ID` resume mechanism              | Not built in — the application must implement reconnect/backoff and resume logic itself                                                    |
| Proxy/infra friendliness | Just HTTP — works through virtually any proxy, load balancer, or corporate firewall with no special configuration | Needs the infra (load balancer, ingress, reverse proxy) to explicitly support the `Upgrade: websocket` handshake and hold connections open |
| Browser support          | No IE11, otherwise universal; not natively available in React Native / most non-browser clients                   | Universal, including native mobile clients                                                                                                 |
| Message format           | Text only (UTF-8)                                                                                                 | Text or binary frames                                                                                                                      |
| Typical Go server cost   | Cheap — it's an HTTP handler holding the response open and writing to it                                          | Slightly more bookkeeping — a dedicated read loop and (if the client can send messages) a write goroutine per connection                   |

**Use SSE when:** the data only flows server → client (live notifications, order-status updates, dashboards, activity feeds, streaming AI responses) and the clients are all browsers or environments with HTTP streaming support. It is the simpler choice whenever the client never needs to send anything back over the same channel — no protocol upgrade, automatic reconnection, and it survives typical corporate proxies without extra configuration.

**Use WebSocket when:** the client genuinely needs to send messages back over the _same_ long-lived connection — chat, collaborative editing, multiplayer/game state, live bidirectional control channels — or when a native mobile client needs a persistent connection outside a browser context (`EventSource` is a browser API; React Native and native mobile clients typically use WebSocket instead).

**Recommended library for WebSocket in Go:** [`coder/websocket`](https://github.com/coder/websocket) (the actively maintained continuation of the formerly popular `nhooyr.io/websocket`, published under Coder's GitHub organization). The long-time default, `gorilla/websocket`, is now **archived and unmaintained** by its own project notice — still functional and safe to keep in an existing codebase, but not the right choice to start a new project on. `coder/websocket` uses `context.Context` for cancellation/timeouts consistently with the rest of the language's idioms, handles concurrent writes safely without extra synchronization code, and is referenced directly from the Go team's own `golang.org/x/net/websocket` package documentation as the more actively maintained alternative. Docs: https://pkg.go.dev/github.com/coder/websocket

**Real-time push via SSE, concretely (Gin's native SSE support):**

```go
router.GET("/stream/events", func(c *gin.Context) {
    sub := redisClient.Subscribe(c.Request.Context(), "service:events")
    defer sub.Close()
    ch := sub.Channel()
    c.Stream(func(w io.Writer) bool {
        select {
        case msg, ok := <-ch:
            if !ok { return false }
            c.SSEvent("message", msg.Payload)
            return true
        case <-c.Request.Context().Done():
            return false
        }
    })
})
```

**Real-time push via WebSocket, concretely (`coder/websocket`, fed by the same Redis Pub/Sub layer):**

```go
router.GET("/ws/events", func(c *gin.Context) {
    conn, err := websocket.Accept(c.Writer, c.Request, nil)
    if err != nil {
        return // Accept already wrote an appropriate HTTP error response
    }
    defer conn.CloseNow()

    ctx := c.Request.Context()
    sub := redisClient.Subscribe(ctx, "service:events")
    defer sub.Close()

    for {
        select {
        case msg, ok := <-sub.Channel():
            if !ok {
                return
            }
            if err := conn.Write(ctx, websocket.MessageText, []byte(msg.Payload)); err != nil {
                return // client disconnected or write failed
            }
        case <-ctx.Done():
            conn.Close(websocket.StatusNormalClosure, "")
            return
        }
    }
})
```

Note the shape: one goroutine per connection, a read side (implicit here, since this example only pushes — a bidirectional handler would add a concurrent read loop via `conn.Read(ctx)`), and a `context.Context` that ties the connection's lifetime to the request's cancellation. Because a WebSocket connection is stateful and held in memory on whichever backend instance accepted it, broadcasting to _all_ connected clients across multiple instances still needs the same Redis Pub/Sub fan-out shown above — the WebSocket layer only changes how a single instance talks to its own connected clients, not how state is synchronized across instances.

## 9. Dependency Injection Without a Container

`main.go` (the **composition root**) is the one place in the entire codebase allowed to know about every module and every concrete adapter:

```go
func main() {
    cfg := config.Load()
    db := database.MustConnect(cfg.DatabaseURL)
    redisClient := redis.NewClient(&redis.Options{Addr: cfg.RedisAddr})
    bus := eventbus.New()

    catalogAPI := catalog.New(db, bus)
    orderingAPI := ordering.New(db, catalogAPI, bus) // depends on catalog only via its API
    notificationsAPI := notifications.New(db, redisClient)

    bus.Subscribe("order.placed", notificationsAPI.OnOrderPlaced)

    router := httpserver.New(cfg)
    catalog.RegisterRoutes(router, catalogAPI)
    ordering.RegisterRoutes(router, orderingAPI)

    router.Run(cfg.HTTPAddr)
}
```

**Key points:**

- For a monolith with a handful to a dozen modules, **hand-written wiring like this is the recommended default** — it's readable top-to-bottom, debuggable with a normal debugger, and has zero magic.
- Once the wiring graph grows large enough that this becomes tedious (dozens of modules, complex multi-environment configuration), a **compile-time DI code generator** such as `google/wire` generates equivalent code from a declarative provider set — it is _not_ a runtime container; it produces exactly the kind of function shown above, preserving the "no framework magic" property while removing manual repetition.
- Avoid runtime reflection-based DI containers in Go. They exist as libraries, but they reintroduce exactly the "invisible wiring" cost Go's ecosystem is designed to avoid, without the ecosystem-wide tooling maturity that makes it manageable elsewhere.

## 10. Enforcing the Architecture in CI

A layout convention that nobody checks mechanically erodes within a few sprints under deadline pressure — this is true in any language, and Go has no compiler-level module system to catch it automatically (Section 2). The standard tool for this purpose is [`fe3dback/go-arch-lint`](https://github.com/fe3dback/go-arch-lint), an actively maintained import-graph linter built specifically "for hexagonal / onion / ddd / mvc and other architectural patterns":

```yaml
# .go-arch-lint.yml
version: 3
workdir: internal
components:
  platform: { in: platform/** }
  identity: { in: identity/** }
  catalog: { in: catalog/** }
  ordering: { in: ordering/** }
  payments: { in: payments/** }

commonComponents: [platform]

deps:
  ordering:
    mayDependOn: [platform, catalog] # only via catalog's public API surface
  payments:
    mayDependOn: [platform, ordering]
```

```bash
go install github.com/fe3dback/go-arch-lint@latest
go-arch-lint check --project-path .
```

A violation (e.g., `payments` importing `internal/ordering/repository` directly instead of going through `ordering.API`) fails the build. For finer-grained rules ("nothing under `domain/` may import an HTTP framework or an ORM"), pair `go-arch-lint` with either a small custom `go/analysis`-based linter or a simple `go list -deps | grep` check in a Makefile target — both are common, lightweight complements.

**A complete CI gate set for a service structured this way:**

```bash
gofmt -l .                       # fail if anything is unformatted
go vet ./...
golangci-lint run
go build ./...
go test -race -cover ./...
go-arch-lint check --project-path .
```

## 11. When to Deviate From This Structure

This is a starting skeleton, not a rigid template — a few explicit "it depends" notes:

- **Small, low-complexity modules** (a simple audit-log writer with one write path and no reads) don't need the full `domain/usecase/repository/delivery` four-way split — a single file with 2–3 functions is more honest than four near-empty packages. Reserve the full split for modules with real business rules and multiple use cases.
- **Read-heavy, query-oriented modules** (a search or reporting module backed by a specialized read store) often don't need a _write_ repository at all — structure them as `usecase` (query orchestration) + `repository` (read-store client) only, and let them subscribe to integration events from write-side modules to keep a read model in sync, rather than forcing a symmetric CRUD shape they don't need. This is the essence of applying CQRS _locally within a module_, without needing it everywhere.
- **Genuinely shared value objects** used by three or more modules (e.g., `Money`, `DateRange`) can live in `internal/platform/types` — but keep this package extremely small and stable, since it's the one place where a change ripples across every module.
- **A module that is likely to become its own service later** benefits from being stricter about depending _only_ on peer modules' public `API` interfaces from day one, never reaching into their packages even indirectly — this keeps the eventual extraction to a separate service a mechanical exercise (change how the interface is satisfied, e.g. over HTTP/gRPC instead of an in-process call) rather than a redesign.

## 12. Mindset Notes for Teams Coming From Java/Spring

**Key points:**

- **There is no framework enforcing your architecture for you.** Spring Modulith-style module boundary checks, or ArchUnit-based layering tests, have no drop-in equivalent — you get the same guarantee only by combining `internal/` visibility with an explicit lint tool (Section 10) that someone has to configure and keep in CI. Skipping this step is the most common way a Go Modular Monolith silently degrades into a ball of mud.
- **Transactions are explicit values, not annotations.** There is no `@Transactional` — a transaction boundary is a function that begins and commits a `*sql.Tx` (or an interface abstracting it) and passes it down explicitly to whatever needs to participate. This is more verbose, but it eliminates an entire category of propagation-mode confusion (`REQUIRES_NEW` vs. `REQUIRED`) that simply doesn't exist as a concept once the boundary is just "the function that has the `*sql.Tx` in scope."
- **Persistence mapping is not automatic.** There is no JPA-style lazy-loading proxy. Every join or secondary query is explicit code you write, which makes N+1 query problems more visible up front — most teams treat this as a net benefit despite the extra verbosity.
- **Security and cross-cutting concerns are explicit middleware, not a declarative filter-chain DSL.** Authentication, CORS, rate limiting, and panic recovery are each a small, independently testable function in the middleware chain, composed explicitly when the router is built — there's no separate configuration surface to reconcile with the code.
- **Plan real time for "gluing things together."** A large share of what a DI container/ORM/AOP framework does invisibly in a Java stack becomes visible, hand-written code in Go. This is not wasted effort — it's the trade-off the language makes deliberately — but it changes where implementation time goes during a migration, and should be estimated accordingly rather than assumed to shrink.

## 13. References

All links verified reachable at the time of writing (September 2026).

1. Kamil Grzybek, _Modular Monolith: A Primer_ — https://www.kamilgrzybek.com/blog/posts/modular-monolith-primer
2. `bxcodec/go-clean-arch` — Clean Architecture reference implementation in Go — https://github.com/bxcodec/go-clean-arch
3. Go official tutorial, _Developing a RESTful API with Go and Gin_ — https://go.dev/doc/tutorial/web-service-gin
4. `golang-standards/project-layout` — community project layout conventions — https://github.com/golang-standards/project-layout
5. `fe3dback/go-arch-lint` — architecture/boundary linter for Go — https://github.com/fe3dback/go-arch-lint
6. `google/wire` — compile-time dependency injection for Go — https://github.com/google/wire
7. Gin Web Framework official docs — https://gin-gonic.com/docs/
8. Go documentation on the `internal/` package convention — https://go.dev/doc/go1.4#internalpackages
9. `coder/websocket` (actively maintained continuation of `nhooyr.io/websocket`) — https://github.com/coder/websocket
10. `gorilla/websocket` project archival notice — https://pkg.go.dev/github.com/gorilla/websocket

> Item 1 is the most widely cited definition of the Modular Monolith pattern in the industry (language-agnostic; its application to Go package structure here is original synthesis, presented as such rather than as an official Go-team recommendation). Items 3, 7, 8 are primary Go-project/framework sources. Items 2, 4–6 are high-reputation, widely adopted open-source references used as structural evidence for the conventions described.
