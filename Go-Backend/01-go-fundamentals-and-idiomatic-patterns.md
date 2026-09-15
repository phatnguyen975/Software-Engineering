# Go Fundamentals & Idiomatic Design: From Basics to Advanced

> A structured reference to how Go is actually meant to be written — the type system, concurrency model, error handling, and the design patterns the community has converged on. Comparisons to Java are included throughout to highlight _why_ Go idioms look the way they do, and to flag mindset shifts that matter when moving from a Java/OOP background.

## Table of Contents

1. [Go's Design Philosophy](#1-gos-design-philosophy)
2. [Structs, Pointers, and Value Semantics](#2-structs-pointers-and-value-semantics)
3. [Interfaces: Structural, Implicit, Small](#3-interfaces-structural-implicit-small)
4. [Composition Over Inheritance](#4-composition-over-inheritance)
5. [Error Handling as Values](#5-error-handling-as-values)
6. [Concurrency: Goroutines, Channels, `context`](#6-concurrency-goroutines-channels-context)
7. [Generics (Go 1.18+)](#7-generics-go-118)
8. [SOLID Principles in a Language Without Classes](#8-solid-principles-in-a-language-without-classes)
9. [Idiomatic Design Patterns in Go](#9-idiomatic-design-patterns-in-go)
10. [Testing Culture](#10-testing-culture)
11. [Tooling & Style](#11-tooling--style)
12. [Mindset Shifts When Moving From Java to Go](#12-mindset-shifts-when-moving-from-java-to-go)
13. [References](#13-references)

## 1. Go's Design Philosophy

**Key points:**

- Go optimizes for **simplicity, readability, and explicitness** over expressiveness or cleverness. Code should be easy to read by someone who didn't write it, months later.
- There is deliberately **no metaprogramming magic** — no annotations that alter runtime behavior, no reflection-based dependency injection, no bytecode weaving. What you see in the source is what executes.
- Go favors **one obvious way to do a thing** over many flexible ways. This is enforced culturally (a single official formatter, `gofmt`, that removes all bikeshedding about style) and architecturally (no operator overloading, no implicit type conversions, no exceptions).
- The language was built for **large codebases maintained by many engineers over long periods** — every design trade-off (no generics until 1.18, no exceptions, explicit error handling) favors long-term maintainability over short-term terseness.

**Example — the philosophy in practice:**

```go
// Idiomatic Go: explicit, linear, no hidden control flow
func GetUser(ctx context.Context, db *sql.DB, id string) (*User, error) {
    row := db.QueryRowContext(ctx, "SELECT id, email FROM users WHERE id = $1", id)
    var u User
    if err := row.Scan(&u.ID, &u.Email); err != nil {
        if errors.Is(err, sql.ErrNoRows) {
            return nil, fmt.Errorf("user %s: %w", id, ErrNotFound)
        }
        return nil, fmt.Errorf("scan user %s: %w", id, err)
    }
    return &u, nil
}
```

Every failure path is visible in the function body — there's no annotation, no framework-managed transaction boundary, no exception bubbling up from three layers away. This traceability is the central value proposition of Go's philosophy, and it's the opposite of the Spring/Hibernate style, which favors declarative annotations (`@Transactional`, `@Repository`) that hide control flow behind framework machinery.

The two canonical references for this philosophy are the official [_Effective Go_](https://go.dev/doc/effective_go) guide and Dave Cheney's [_The Zen of Go_](https://dave.cheney.net/2020/02/23/the-zen-of-go), a widely cited community checklist ("Simplicity is prerequisite for reliability", "Handle errors explicitly", "In the face of ambiguity, refuse the temptation to guess").

**Java comparison:** Java's ecosystem (especially Spring) optimizes for _developer convenience through abstraction_ — annotations, reflection, and proxies do a lot of invisible work so you write less code. Go inverts this trade-off: you write more explicit code in exchange for nothing being invisible. Neither is objectively better; they optimize for different failure modes (Spring optimizes to reduce boilerplate, Go optimizes to reduce "spooky action at a distance" when debugging).

## 2. Structs, Pointers, and Value Semantics

### 2.1 Structs are plain data, not classes

**Key points:**

- A `struct` has no constructor, no `this`, and no access modifiers beyond **capitalization** (exported vs. unexported), which applies at the **package** level, not per-type.
- "Constructors" are a naming convention (`NewX`), not a language feature.
- Methods are declared separately from the struct definition, attached via a **receiver**.

```go
package user

type User struct {
    ID    string
    Email string
    plan  string // unexported: visible only inside package "user", not per-type
}

func NewUser(id, email string) *User {
    return &User{ID: id, Email: email, plan: "free"}
}

func (u *User) Upgrade(plan string) { u.plan = plan } // method with a receiver
```

**Java comparison:** In Java, `private` is enforced per-class; a sibling class in the same package still cannot touch another class's private fields. In Go, visibility is enforced per-**package**, so any file inside `package user` can freely read/write `plan` on any `User` value. This makes package boundaries an architectural decision in Go in a way `private` fields never force in Java — poor package layout leaks internal state far more easily than poor class layout does in Java.

### 2.2 Value types vs. pointer types

**Key points:**

- Assigning or passing a struct **by value** copies it entirely; passing **by pointer** shares the same memory.
- Choose a **pointer receiver** when a method mutates the receiver, the struct is large, or it contains non-copyable fields (e.g. `sync.Mutex`).
- Choose a **value receiver** for small, immutable-style value objects.
- **Never mix** receiver types across a type's methods — Go's method-set rules differ between `T` and `*T`, and inconsistency silently breaks interface satisfaction.

```go
type Point struct{ X, Y int }

func moveByValue(p Point)  { p.X += 10 } // mutates a COPY
func moveByPointer(p *Point) { p.X += 10 } // mutates the ORIGINAL

p := Point{X: 1, Y: 1}
moveByValue(p)
fmt.Println(p.X)   // 1 — unchanged

moveByPointer(&p)
fmt.Println(p.X)   // 11 — changed
```

Method-set gotcha:

```go
type Animal interface{ Speak() string }
type Dog struct{}
func (d *Dog) Speak() string { return "Woof" }

var a Animal = Dog{}   // compile error: Dog does not implement Animal (Speak has pointer receiver)
var a Animal = &Dog{}  // OK
```

**Java comparison:** Java has no equivalent decision to make — every object is always accessed through a reference. This is the single most common source of subtle bugs for engineers new to Go, because "pass by value" silently produces a full copy with no warning.

### 2.3 There is no `null` — but `nil` is more dangerous, not less

**Key points:**

- Pointers, interfaces, maps, slices, channels, and functions all have a zero value of `nil`.
- A `nil`-valued **concrete pointer wrapped inside a non-nil interface** is **not** a nil interface — this is the most common beginner trap in Go and has no Java analogue.

```go
type MyError struct{}
func (e *MyError) Error() string { return "boom" }

func doSomething() error {
    var err *MyError = nil
    return err // returns a NON-nil error interface wrapping a nil *MyError
}

if err := doSomething(); err != nil {
    fmt.Println("error!") // prints — surprisingly
}
```

An interface value is internally a `(type, value)` pair; it's only `== nil` when **both** are nil. **Rule of thumb:** never declare a typed error variable and return it directly — return `nil` explicitly when nothing went wrong.

**Java comparison:** Java's `null` is a single universal value with one failure mode (`NullPointerException`). Go's `nil` has per-type semantics and this extra subtlety around interfaces — worth internalizing early rather than debugging it in production.

## 3. Interfaces: Structural, Implicit, Small

### 3.1 Implicit (structural) satisfaction

**Key points:**

- There is **no `implements` keyword**. Any type with matching method signatures automatically satisfies an interface.
- This is **structural typing**, fully checked at compile time (unlike Python's duck typing, which is runtime-only).
- You can define an interface in your own package for a type that lives in a completely different package — including the standard library — without that package knowing or caring.

```go
type Reader interface {
    Read(p []byte) (n int, err error)
}
// os.File satisfies io.Reader simply by having a matching Read method —
// there is no "implements io.Reader" declaration anywhere in os.File's source.
```

**Java comparison:** In Java, a class must explicitly declare `implements Comparable<T>` at definition time — the relationship is nominal and fixed. In Go, the relationship is discovered structurally wherever it's needed, which decouples consumers from producers far more aggressively: a consumer package can define exactly the interface it needs, even for types it doesn't own.

### 3.2 "Accept interfaces, return structs"

**Key points:**

- Function/constructor **parameters** should be interfaces, so callers can substitute any implementation (including test doubles).
- **Return types** should be concrete structs, so callers get full access and the implementation can grow without breaking anyone.

```go
// Good: consumer decides which interface to depend on
func NewOrderService(repo OrderRepository, clock Clock) *OrderService { ... }

// Usually unnecessary indirection — avoid returning an interface unless you
// genuinely need to hide multiple implementations from the caller
func NewOrderService(...) OrderService { ... }
```

### 3.3 Keep interfaces small

**Key points:**

- Idiomatic Go interfaces are tiny — the standard library's `io.Reader`, `io.Writer`, `io.Closer` are 1 method each, composed when more behavior is needed (`io.ReadWriteCloser`).
- Small interfaces are trivially fakeable in tests and trivially satisfied by types that only do one thing.
- **Define interfaces at the point of consumption** (the package that _needs_ the behavior), not at the point of implementation (the package that _provides_ it).

```go
type UserReader interface {
    GetByID(ctx context.Context, id string) (*User, error)
}
type UserWriter interface {
    Save(ctx context.Context, u *User) error
}
type UserRepository interface { // composed only where both are actually needed
    UserReader
    UserWriter
}
```

**Java comparison:** A typical Spring Data repository (`interface UserRepository extends JpaRepository<User, Long>`) inherits dozens of methods whether a given consumer needs them or not, and is conventionally declared next to its implementation in the persistence layer. Go's convention is the reverse on both counts: interfaces are minimal, and they live with the _consumer_, not the _provider_ — this is what makes Dependency Inversion (Section 8) fall out naturally instead of requiring discipline to enforce.

## 4. Composition Over Inheritance

**Key points:**

- Go has **no class inheritance**. Instead, **struct embedding** promotes an embedded type's fields and methods into the outer struct.
- Embedding gives you field/method promotion, but **not virtual dispatch** — there is no polymorphic override resolution or `super` mechanism.
- Embedding is most powerful for **interface composition** and for **decorating/wrapping** behavior, not for building deep type hierarchies.

```go
type BaseEntity struct {
    ID        string
    CreatedAt time.Time
}
func (b BaseEntity) Describe() string { return "Entity " + b.ID }

type Product struct {
    BaseEntity // embedded, not "extends"
    Name       string
}

p := Product{BaseEntity: BaseEntity{ID: "1"}, Name: "Keyboard"}
fmt.Println(p.ID)         // promoted field
fmt.Println(p.Describe()) // promoted method
```

If `Product` defines its own `Describe()`, calling `p.Describe()` picks the outer one — but if `BaseEntity.Describe()` internally calls another method that `Product` also overrides, it still calls `BaseEntity`'s version. There's no virtual dispatch chain the way `super.method()` interacts with overrides in Java.

**Java comparison:** "Favor composition over inheritance" is a _recommendation_ in Effective Java; in Go it's the _only option_, which the Go community treats as a feature — it removes an entire category of design debate (deep hierarchies, fragile base class problems, diamond inheritance) by simply not offering the tool that causes it.

## 5. Error Handling as Values

### 5.1 The core idiom

**Key points:**

- Every call that can fail returns `(value, error)` — there is no `try/catch`, and errors are **ordinary values**, not a separate control-flow channel.
- Convention: check `if err != nil` **immediately** after the call, not several stack frames away.
- This trades visual repetition for making success and failure paths equally visible — a deliberate choice, not an oversight, as explained in the official Go team post [_Error handling and Go_](https://go.dev/blog/error-handling-and-go) and the companion [_Errors are values_](https://go.dev/blog/errors-are-values).

```go
func Divide(a, b float64) (float64, error) {
    if b == 0 {
        return 0, errors.New("division by zero")
    }
    return a / b, nil
}

result, err := Divide(10, 0)
if err != nil {
    return fmt.Errorf("divide failed: %w", err)
}
```

### 5.2 Wrapping and inspecting errors (Go 1.13+)

**Key points:**

- `fmt.Errorf("...: %w", err)` wraps an error while preserving the original for later inspection.
- `errors.Is(err, target)` walks the wrap chain checking for a specific sentinel value.
- `errors.As(err, &target)` walks the wrap chain looking for a specific error **type** to extract.
- `errors.Join` (1.20+) combines multiple errors into one — useful for aggregating validation failures.

```go
var ErrNotFound = errors.New("not found")

func (r *pgUserRepo) GetByID(ctx context.Context, id string) (*User, error) {
    row := r.db.QueryRowContext(ctx, "SELECT ... WHERE id = $1", id)
    var u User
    if err := row.Scan(&u.ID, &u.Email); err != nil {
        if errors.Is(err, sql.ErrNoRows) {
            return nil, fmt.Errorf("user %s: %w", id, ErrNotFound)
        }
        return nil, fmt.Errorf("scanning user %s: %w", id, err)
    }
    return &u, nil
}

// caller
u, err := repo.GetByID(ctx, id)
if errors.Is(err, ErrNotFound) {
    respondNotFound()
}
```

These conventions are documented in the official [_Working with Errors in Go 1.13_](https://go.dev/blog/go1.13-errors) post.

### 5.3 Custom error types

```go
type ValidationError struct {
    Field, Message string
}
func (e *ValidationError) Error() string {
    return fmt.Sprintf("%s: %s", e.Field, e.Message)
}

var verr *ValidationError
if errors.As(err, &verr) {
    respondBadRequest(verr.Field)
}
```

### 5.4 `panic`/`recover` are not exceptions

**Key points:**

- `panic` is for truly unrecoverable programmer errors (nil dereference, out-of-bounds access, explicit invariant violations) — **not** for expected failure conditions.
- Using `panic`/`recover` for ordinary control flow (the way Java uses exceptions for validation failures) is a well-known anti-pattern.
- The one common legitimate use of `recover()` in a server is a top-level middleware that prevents one handler's panic from crashing the whole process.

**Java comparison summary:**

| Java                                         | Go                                                                         |
| -------------------------------------------- | -------------------------------------------------------------------------- |
| Checked exception for expected failure       | Sentinel error + `errors.Is`                                               |
| Custom exception carrying fields             | Custom error type + `errors.As`                                            |
| `RuntimeException` for programmer bugs       | `panic` (rare, always recovered at a boundary)                             |
| `@ControllerAdvice` global exception mapping | Small explicit error-mapping function/middleware inspecting `errors.Is/As` |

## 6. Concurrency: Goroutines, Channels, `context`

### 6.1 Goroutines

**Key points:**

- A goroutine is a function executing concurrently, scheduled by the **Go runtime** (an M:N scheduler multiplexing many goroutines onto fewer OS threads), not the OS directly.
- Goroutines start at ~2KB of growable stack — spawning tens of thousands is normal.
- **Goroutine leaks are the #1 concurrency bug in Go services**: an unbounded `go func(){ ... }()` that never returns (e.g., blocked forever on a channel nobody writes to) leaks memory silently, with no compiler warning. Every spawned goroutine needs a clear termination condition, almost always tied to `context.Context` cancellation or a channel close.

```go
func handleRequest(ctx context.Context) {
    go auditLog(ctx) // must still respect ctx cancellation internally, or it can leak
}
```

**Java comparison:** This model is architecturally closer to Java's newer **virtual threads** (Project Loom, JDK 21+) than to classic `Thread`/`ExecutorService` — except Go has had it natively as the default concurrency primitive since 2009. Unlike a bounded Java thread pool with a queue, there is no built-in limit on goroutine count — bounding concurrency is something you design explicitly (e.g., a buffered channel used as a semaphore).

### 6.2 Channels (CSP model)

**Key points:**

- Go's model follows Tony Hoare's **Communicating Sequential Processes (CSP)**: "Do not communicate by sharing memory; instead, share memory by communicating."
- **Unbuffered channel** (`make(chan int)`): send blocks until a receiver is ready — pure synchronization.
- **Buffered channel** (`make(chan int, N)`): send blocks only once the buffer is full — a lightweight bounded queue.
- `select` waits on multiple channel operations at once.

```go
func worker(jobs <-chan int, results chan<- int) {
    for j := range jobs { // ranges until the channel is closed
        results <- j * 2
    }
}

jobs := make(chan int, 100)
results := make(chan int, 100)
for w := 1; w <= 3; w++ {
    go worker(jobs, results)
}
for j := 1; j <= 9; j++ { jobs <- j }
close(jobs)
```

For fan-out/fan-in and worker-pool patterns, `sync.WaitGroup` (join semantics, similar to `CountDownLatch`/`Thread.join()`) and `golang.org/x/sync/errgroup` (a `WaitGroup` variant that also propagates the first error and can cancel a shared context) are the standard building blocks.

### 6.3 `context.Context`

**Key points:**

- Go has **no implicit thread-local propagation**. Instead, **every function on a request's call path takes an explicit `context.Context` as its first parameter**.
- `context.Context` carries three things: **cancellation signals**, **deadlines/timeouts**, and (sparingly) **request-scoped values** — never business data or optional parameters, which is a common misuse to avoid.

```go
func (s *OrderService) Place(ctx context.Context, orderID string) error {
    ctx, cancel := context.WithTimeout(ctx, 3*time.Second)
    defer cancel()
    return s.repo.Insert(ctx, orderID) // propagated all the way to the DB driver
}
```

This is documented in depth in the official [_Go Concurrency Patterns: Context_](https://go.dev/blog/context) post.

**Java comparison:** Spring relies on `ThreadLocal` (request-scoped beans, MDC for tracing) to propagate request-scoped state _implicitly_ — any code on the thread can reach into it without a parameter. Go rejects this entirely in favor of explicit passing: you always see, from a function's signature, whether it participates in cancellation/timeout propagation.

### 6.4 Data races and the race detector

**Key points:**

- Concurrent, unsynchronized access to shared state (e.g., a map written from two goroutines) is a **data race** — undefined behavior, not a checked exception.
- Go ships a first-class detector: run with `-race` (`go test -race ./...`, `go run -race main.go`). This should be a standard CI gate, not an occasional debugging tool.

**Concurrency tool selection cheat-sheet:**

| Scenario                                                      | Tool                                      |
| ------------------------------------------------------------- | ----------------------------------------- |
| Bounded worker pool processing jobs                           | buffered channel + N goroutines           |
| Wait for N goroutines to finish                               | `sync.WaitGroup`                          |
| Wait for N goroutines, propagate first error, cancel siblings | `golang.org/x/sync/errgroup`              |
| Request timeout / cancellation propagation                    | `context.Context` + `context.WithTimeout` |
| Protect a shared in-memory cache                              | `sync.RWMutex` or `sync.Map`              |
| One-time initialization                                       | `sync.Once`                               |

## 7. Generics (Go 1.18+)

**Key points:**

- Go added parametric polymorphism in 1.18 (2022), reified via constraint-based type parameters (unlike Java's type-erasure generics).
- Use generics for **type-agnostic data structures/algorithms** (a `Set[T]`, a paginated `Page[T]` wrapper, a `Result[T]` type).
- Continue using **interfaces** for **behavioral polymorphism** (things that do genuinely different things depending on the concrete implementation, e.g. `PaymentGateway`).
- Overusing generics to recreate Java-style generic service hierarchies is explicitly discouraged by the Go team and community.

```go
type Number interface {
    ~int | ~int64 | ~float64
}

func Sum[T Number](items []T) T {
    var total T
    for _, v := range items {
        total += v
    }
    return total
}

type Repository[T any, ID comparable] interface {
    GetByID(ctx context.Context, id ID) (T, error)
    Save(ctx context.Context, entity T) error
}
```

See the official [_Getting Started With Generics_](https://go.dev/doc/tutorial/generics) tutorial for the intended scope.

## 8. SOLID Principles in a Language Without Classes

Go is not "anti-OOP" — SOLID applies, but the _mechanism_ differs because there is no inheritance and no framework-managed wiring.

**S — Single Responsibility.** Expressed at the **package** level as much as the type level: idiomatic Go favors many small, focused packages over one large `service`/`util` package. A package should have exactly one reason to change.

**O — Open/Closed.** Achieved through interfaces + composition, not inheritance. A `NotificationSender` interface accepts new implementations (email, SMS, push) without modifying existing code — no `implements` keyword or abstract base class required.

**L — Liskov Substitution.** Because interfaces are structurally typed, violations show up differently than in Java: if a type's method doesn't honor the interface's implied contract (e.g., a `Read` that panics instead of returning `io.EOF`), reliant code breaks at runtime, not at the type-checking level. Go leans on documented behavioral contracts (godoc comments on interfaces) plus tests, since the compiler can't check semantic substitutability.

**I — Interface Segregation.** Go arguably enforces this _more naturally_ than Java, because idiomatic interfaces are tiny by convention (Section 3.3) — a one-method `UserReader` is trivially substitutable and trivially fakeable.

**D — Dependency Inversion.** The principle most consequential for backend architecture:

- High-level code (business/use-case logic) **defines the interface it needs**, in its own package.
- Low-level code (database adapters, HTTP clients) **implements** that interface, in a separate package.
- Wiring happens in one explicit place (commonly `main.go`) — never via a framework container.

```go
// package usecase — defines the abstraction it needs
type ProductRepository interface {
    Save(ctx context.Context, p *Product) error
}
type CreateProductUseCase struct {
    repo ProductRepository
}

// package postgres — implements the abstraction
type ProductRepo struct{ db *sql.DB }
func (r *ProductRepo) Save(ctx context.Context, p *Product) error { /* ... */ }

// package main — composition root, explicit wiring
func main() {
    repo := postgres.NewProductRepo(db)
    uc := usecase.NewCreateProductUseCase(repo) // satisfies the interface implicitly
}
```

There's no `@Autowired`, no classpath scanning, no runtime proxy generation — wiring is plain Go code, readable top-to-bottom and debuggable with a normal debugger.

## 9. Idiomatic Design Patterns in Go

Classic GoF patterns aren't banned, but many collapse into simpler idioms given first-class functions, structural interfaces, and the absence of constructors/overloading. Below are the patterns that appear constantly in real Go services, with a concrete "when to use it" for each.

### 9.1 Functional Options — replaces telescoping constructors / builder pattern

Go has **no method overloading and no named/default parameters**, so a struct with many optional fields can't use the overloaded-constructor idiom. The standard solution, popularized by Dave Cheney's [_Functional options for friendly APIs_](https://dave.cheney.net/2014/10/17/functional-options-for-friendly-apis):

```go
type ServerOption func(*Server)

func WithTimeout(d time.Duration) ServerOption {
    return func(s *Server) { s.timeout = d }
}
func WithLogger(l Logger) ServerOption {
    return func(s *Server) { s.logger = l }
}

func NewServer(addr string, opts ...ServerOption) *Server {
    s := &Server{addr: addr, timeout: 30 * time.Second, logger: defaultLogger}
    for _, opt := range opts {
        opt(s)
    }
    return s
}

srv := NewServer(":8080", WithTimeout(5*time.Second), WithLogger(myLogger))
```

**Use when:** a constructor has more than ~3 optional parameters, or a public API needs to add new options later **without breaking backward compatibility** (a new `With...` function is purely additive).

### 9.2 Decorator — via interface/function wrapping

```go
type Handler func(w http.ResponseWriter, r *http.Request)

func WithLogging(next Handler) Handler {
    return func(w http.ResponseWriter, r *http.Request) {
        start := time.Now()
        next(w, r)
        log.Printf("%s %s took %v", r.Method, r.URL.Path, time.Since(start))
    }
}
```

**Use when:** cross-cutting concerns (logging, auth, rate limiting, tracing) — the structural equivalent of Java's proxy-based AOP, but as plain function composition instead of runtime proxies and annotations.

### 9.3 Strategy — an interface with multiple implementations selected at runtime

```go
type PricingStrategy interface {
    CalculatePrice(base float64) float64
}
type EarlyBirdPricing struct{ Discount float64 }
func (p EarlyBirdPricing) CalculatePrice(base float64) float64 { return base * (1 - p.Discount) }

type StandardPricing struct{}
func (p StandardPricing) CalculatePrice(base float64) float64 { return base }
```

**Use when:** an algorithm varies independently of the client using it (payment methods, pricing rules, notification channels) — same trigger condition as in Java.

### 9.4 Repository — the standard persistence abstraction

```go
type UserRepository interface {
    GetByID(ctx context.Context, id string) (*User, error)
    Save(ctx context.Context, u *User) error
}
```

**Use when:** at the boundary between business logic and the database/ORM — this is what lets you swap ORMs, or substitute an in-memory fake in tests, without touching business logic.

### 9.5 Adapter — reconciling an external API's shape with your own interface

```go
type PaymentGateway interface {
    Charge(ctx context.Context, amountCents int64, token string) (string, error)
}
type StripeAdapter struct{ client *stripe.Client }
func (s *StripeAdapter) Charge(ctx context.Context, amountCents int64, token string) (string, error) {
    // translate to stripe.ChargeParams{...}
}
```

**Use when:** integrating any third-party SDK (payment gateway, object storage, AI provider) where you don't want its types leaking into your business logic.

### 9.6 Singleton — via `sync.Once`, not a private constructor

```go
var (
    instance *ConfigLoader
    once     sync.Once
)
func GetConfigLoader() *ConfigLoader {
    once.Do(func() { instance = &ConfigLoader{} })
    return instance
}
```

**Use when:** rarely — mostly process-wide singletons like a metrics registry or a DB connection pool. Prefer explicit constructor injection over global singletons for anything related to business logic.

### 9.7 Observer / Pub-Sub — via channels or a small in-process event bus

```go
type Event interface{ Topic() string }

type Bus struct {
    mu   sync.RWMutex
    subs map[string][]func(context.Context, Event)
}
func (b *Bus) Subscribe(topic string, h func(context.Context, Event)) {
    b.mu.Lock(); defer b.mu.Unlock()
    b.subs[topic] = append(b.subs[topic], h)
}
func (b *Bus) Publish(ctx context.Context, e Event) {
    b.mu.RLock(); handlers := b.subs[e.Topic()]; b.mu.RUnlock()
    for _, h := range handlers { h(ctx, e) }
}
```

**Use when:** decoupled, in-process notification between independent parts of a system — one component publishes without knowing (or caring) who's listening.

### 9.8 Builder — used sparingly

Less common than in Java because functional options cover most "optional configuration" needs. Reach for an explicit builder only when construction has **required sequential steps with validation between them** (e.g., building a complex SQL query), not just optional fields.

## 10. Testing Culture

**Key points:**

- `go test` is a first-class, batteries-included language feature — not a third-party framework.
- **Table-driven tests** are the dominant idiom: one test function iterates a slice of input/expected-output cases, instead of one method per case.
- Because interfaces are small, hand-written fakes are common and often preferred over generated mocks for simple cases.

```go
func TestDivide(t *testing.T) {
    tests := []struct {
        name    string
        a, b    float64
        want    float64
        wantErr bool
    }{
        {"simple division", 10, 2, 5, false},
        {"division by zero", 10, 0, 0, true},
    }
    for _, tt := range tests {
        t.Run(tt.name, func(t *testing.T) {
            got, err := Divide(tt.a, tt.b)
            if (err != nil) != tt.wantErr {
                t.Fatalf("unexpected error state: %v", err)
            }
            if got != tt.want {
                t.Errorf("got %v, want %v", got, tt.want)
            }
        })
    }
}
```

**Java comparison:** JUnit requires an external dependency and typically one `@Test` method per case; Go's table-driven style keeps all cases for one behavior in a single, scannable place, and `go test -race` gives you race detection for free during the same run.

## 11. Tooling & Style

| Concern               | Tool                                                    | Notes                                                                                                                                                                 |
| --------------------- | ------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Formatting            | `gofmt`                                                 | Not optional — one canonical style, enforced by the toolchain itself                                                                                                  |
| Static analysis       | `go vet` (built-in), `staticcheck`                      | Catches suspicious constructs the compiler allows                                                                                                                     |
| Aggregated linting    | `golangci-lint`                                         | Runs 50+ linters from one config file — the de facto CI standard                                                                                                      |
| Style guide           | [Uber Go Style Guide](https://github.com/uber-go/guide) | The most widely adopted community style guide; documents real architectural conventions (error-wrapping, mutex placement, avoiding global state), not just formatting |
| Race/concurrency bugs | `go test -race`                                         | Runtime detector — highly effective, should run in CI                                                                                                                 |
| Dependency management | Go Modules (`go.mod`/`go.sum`)                          | No central repository server required beyond a module proxy                                                                                                           |

## 12. Mindset Shifts When Moving From Java to Go

**Key points to internalize before writing production Go coming from a Java/Spring background:**

- **Nothing is automatic.** There is no reflection-based DI container, no ORM proxy generation, no AOP. Anything that felt automatic in Spring must be written explicitly — budget real time for this during any migration; it is consistently underestimated.
- **Errors are data, not control flow.** Stop reaching for a "throw and catch three layers up" mental model; design each function's error return as part of its public contract.
- **Interfaces belong to consumers.** Define an interface where it's _used_, not where it's _implemented_ — this single habit change does more to keep a Go codebase decoupled than any amount of upfront architecture diagramming.
- **Concurrency is cheap but not free.** Goroutines are much lighter than Java threads, but every one still needs a termination story; "just spawn a goroutine" without a cancellation path is the most common source of production memory leaks in Go services.
- **Composition, not hierarchy.** When the Java instinct is "let me create an abstract base class," the Go equivalent instinct should be "what's the minimal interface, and what small struct can I embed."
- **The compiler is stricter about some things and looser about others.** Unused imports and unused local variables are compile errors (not warnings); but there is no compiler help at all for verifying that a `nil` check was needed, or that a goroutine was properly joined.

## 13. References

All links verified reachable at the time of writing (September 2026).

1. Effective Go (official language guide) — https://go.dev/doc/effective_go
2. Dave Cheney, _The Zen of Go_ — https://dave.cheney.net/2020/02/23/the-zen-of-go
3. Dave Cheney, _Functional options for friendly APIs_ — https://dave.cheney.net/2014/10/17/functional-options-for-friendly-apis
4. Uber Go Style Guide — https://github.com/uber-go/guide
5. Go Blog, _Error handling and Go_ — https://go.dev/blog/error-handling-and-go
6. Go Blog, _Errors are values_ — https://go.dev/blog/errors-are-values
7. Go Blog, _Working with Errors in Go 1.13_ — https://go.dev/blog/go1.13-errors
8. Go Blog, _Go Concurrency Patterns: Context_ — https://go.dev/blog/context
9. Go official tutorial, _Getting Started With Generics_ — https://go.dev/doc/tutorial/generics
10. Go language specification — https://go.dev/ref/spec
11. `go.uber.org/mock` (maintained successor to the archived `golang/mock`) — https://github.com/uber-go/mock

> Items 1–3, 5–10 are primary Go-project sources. Item 4 and 11 are high-reputation, widely adopted community/industry references used throughout the Go ecosystem for style and tooling guidance.
