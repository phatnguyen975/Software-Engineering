# The Go Backend Ecosystem: Choosing Your Stack

> A practical, opinionated guide to the libraries and tools that make up a production Go backend — grouped by concern, with the top 3–4 real options per category, a clear "best default" call, when each alternative actually makes sense, links to official docs, and usage examples for the recommended pick.

## Table of Contents

1. [Web Framework](#1-web-framework)
2. [Configuration](#2-configuration)
3. [Request Validation](#3-request-validation)
4. [Database Access: ORM, Query Builder, or Codegen](#4-database-access-orm-query-builder-or-codegen)
5. [Schema Migrations](#5-schema-migrations)
6. [Caching & Redis](#6-caching--redis)
7. [Messaging & Background Jobs](#7-messaging--background-jobs)
8. [Full-Text Search](#8-full-text-search)
9. [Object Storage](#9-object-storage)
10. [Resilience: Circuit Breakers & Rate Limiting](#10-resilience-circuit-breakers--rate-limiting)
11. [Authentication (JWT)](#11-authentication-jwt)
12. [Structured Logging](#12-structured-logging)
13. [Observability: Metrics & Tracing](#13-observability-metrics--tracing)
14. [Dependency Injection Tooling](#14-dependency-injection-tooling)
15. [Testing & Mocking](#15-testing--mocking)
16. [Linting & Static Analysis](#16-linting--static-analysis)
17. [Summary Table](#17-summary-table)
18. [References](#18-references)

## 1. Web Framework

| Option                  | Style                                                                                              | Best for                                                                                                                                                                                              |
| ----------------------- | -------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Gin** ⭐ best default | Minimalist, `net/http`-compatible, huge middleware ecosystem                                       | General-purpose REST APIs; the largest community and ecosystem, and the framework used in Go's own official [RESTful API tutorial](https://go.dev/doc/tutorial/web-service-gin)                       |
| Echo                    | Similarly minimalist, slightly more built-in structure (grouped routing, built-in binding helpers) | Teams that want a bit more out of the box than Gin without jumping to a heavier framework                                                                                                             |
| Fiber                   | Built on `fasthttp` instead of `net/http`, Express.js-inspired API                                 | Raw throughput benchmarks are the highest of the group — but it **breaks compatibility** with the standard `net/http` middleware ecosystem, a real cost since most Go HTTP tooling assumes `net/http` |
| chi                     | Extremely thin router on top of `net/http` — just routing/middleware sugar                         | Teams that want the least abstraction possible and are comfortable writing more plumbing by hand                                                                                                      |

**Why Gin is the default:** it stays compatible with `net/http`, which means `httptest`, most third-party middleware, and standard Go HTTP tooling all work without adaptation. Its middleware chain (`c.Next()`) gives a familiar request-interceptor mental model, and it has official Go-team-published tutorial material, lowering onboarding risk for a team new to the framework. Docs: https://gin-gonic.com/docs/

**Example:**

```go
router := gin.Default() // includes Logger + Recovery middleware
router.GET("/health", func(c *gin.Context) {
    c.JSON(http.StatusOK, gin.H{"status": "ok"})
})
router.POST("/orders", createOrderHandler)
router.Run(":8080")
```

## 2. Configuration

| Option                                | Style                                                                                                                   | Best for                                                                                                          |
| ------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| **`spf13/viper`** ⭐ best default     | Reads YAML/JSON/TOML/env vars/flags through one unified API; layered "profile" overlays; unmarshals into a typed struct | Services with non-trivial configuration (multiple environments, nested settings) — https://github.com/spf13/viper |
| `kelseyhightower/envconfig`           | Struct-tag-based, env-vars only                                                                                         | Small services / workers where a full config file layer is overkill                                               |
| `caarlos0/env`                        | Similar to envconfig, actively maintained, more struct-tag features                                                     | A lighter, more modern alternative to envconfig                                                                   |
| Plain `os.Getenv` + manual validation | No dependency at all                                                                                                    | Tiny single-purpose binaries where even a small config library isn't justified                                    |

**Why Viper is the default:** it's the most feature-complete option and directly supports the "base file + environment-specific override + env-var final override" layering pattern that most teams need once they have more than a couple of environments. Docs: https://pkg.go.dev/github.com/spf13/viper

**Example:**

```go
type Config struct {
    HTTPAddr    string `mapstructure:"http_addr"`
    DatabaseURL string `mapstructure:"database_url"`
}

func Load(env string) (*Config, error) {
    v := viper.New()
    v.SetConfigName("application")
    v.SetConfigType("yaml")
    v.AddConfigPath(".")
    if err := v.ReadInConfig(); err != nil { return nil, err }

    v.SetConfigName("application-" + env)
    _ = v.MergeInConfig() // optional environment-specific overlay

    v.AutomaticEnv() // env vars take final precedence

    var cfg Config
    if err := v.Unmarshal(&cfg); err != nil { return nil, err }
    return &cfg, nil
}
```

## 3. Request Validation

| Option                                        | Style                                                                                         | Best for                                                                                                                                                                                            |
| --------------------------------------------- | --------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`go-playground/validator`** ⭐ best default | Struct-tag-based declarative validation, integrated directly into Gin's `ShouldBind*` methods | Virtually every REST API — it's already the validator running under the hood when you use Gin's binding, so adopting it explicitly costs nothing extra — https://github.com/go-playground/validator |
| `ozzo-validation`                             | Fluent, code-based (non-tag) validation rules                                                 | When validation logic is too dynamic/conditional to express cleanly in struct tags                                                                                                                  |
| Hand-written validation functions             | Plain Go `if` statements                                                                      | Very small APIs, or validation rules with business-specific cross-field logic that reads more clearly as code than as a struct tag                                                                  |

**Why `go-playground/validator` is the default:** it's already present as soon as you use Gin's `ShouldBindJSON`, so there's no separate dependency decision to make for the common case, and its struct-tag syntax covers the large majority of validation needs (required fields, ranges, formats, cross-field comparisons) declaratively. Docs: https://pkg.go.dev/github.com/go-playground/validator/v10

**Example:**

```go
type CreateOrderRequest struct {
    ProductID string `json:"productId" binding:"required,uuid4"`
    Quantity  int    `json:"quantity" binding:"required,gt=0"`
    Email     string `json:"email" binding:"required,email"`
}

func createOrderHandler(c *gin.Context) {
    var req CreateOrderRequest
    if err := c.ShouldBindJSON(&req); err != nil {
        c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
        return
    }
    // req is validated
}
```

## 4. Database Access: ORM, Query Builder, or Codegen

Unlike Java's ecosystem, Go deliberately does **not** converge on one dominant "JPA/Hibernate equivalent" — it offers three genuinely different philosophies, and picking the right one per service (or even per module within a service) is itself a normal part of Go backend design.

| Option                                   | Philosophy                                                                                                                | Best for                                                                                                                                                   |
| ---------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **GORM** ⭐ best default for general use | Full-featured, code-first, runtime ORM — associations, hooks, auto-migration, a fluent query builder                      | Fastest onboarding, CRUD-heavy services, teams that want the most convenience — https://gorm.io/docs/                                                      |
| `ent`                                    | Code-first schema defined in Go, generates fully-typed, compile-time-checked query builders (originated at Facebook/Meta) | Complex relational graphs, larger teams, where compile-time-checked relationship traversal materially reduces bugs — https://entgo.io/docs/getting-started |
| `sqlc`                                   | SQL-first — you write real SQL, it generates fully-typed Go functions from it                                             | Performance-critical paths, teams comfortable writing SQL directly who want zero ORM runtime overhead — https://docs.sqlc.dev/                             |
| Plain `database/sql` + `pgx`             | No abstraction beyond the driver                                                                                          | Very small services, or code paths needing full manual control over every query                                                                            |

**Why GORM is the general-purpose default, with two legitimate exceptions:** independent comparative write-ups on this exact trade-off — Encore's [_Comparing the best Go ORMs_](https://encore.dev/articles/go-orms), Bytebase's [_Choose the Right Golang ORM or Query Builder_](https://www.bytebase.com/blog/golang-orm-query-builder/), and Rost Glukhov's [_Comparing Go ORMs for PostgreSQL_](https://www.glukhov.org/post/2025/09/comparing-go-orms-gorm-ent-bun-sqlc/) — converge on the same shape of answer: GORM wins on developer velocity and familiarity; `ent` wins when relationship correctness at compile time matters more than convenience; `sqlc` wins when query performance and full SQL control matter more than either. Reach for `ent` specifically once a module's data model has several interdependent relations that are easy to get wrong by hand; reach for `sqlc` specifically for hot read paths (dashboards, reporting queries) where you'd otherwise be hand-tuning SQL anyway.

Driver note: regardless of which of the above you choose, use **`pgx`** (https://github.com/jackc/pgx) as the underlying PostgreSQL driver rather than the older `lib/pq` — `pgx` is the actively maintained, higher-performance driver, and all three tools above support it as a first-class backend.

**Example (GORM):**

```go
type Order struct {
    ID        string `gorm:"primaryKey"`
    ProductID string
    Quantity  int
    CreatedAt time.Time
}

db.Create(&Order{ID: uuid.NewString(), ProductID: pid, Quantity: 2})

var orders []Order
db.Where("product_id = ?", pid).Find(&orders)
```

## 5. Schema Migrations

| Option                                       | Style                                                                                              | Best for                                                                                                                                                      |
| -------------------------------------------- | -------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`golang-migrate/migrate`** ⭐ best default | Versioned up/down SQL files, CLI + library, supports a wide range of database backends             | General-purpose migration management, closest in spirit to Flyway's versioned-file model — https://github.com/golang-migrate/migrate                          |
| `pressly/goose`                              | Same up/down philosophy, additionally supports writing a migration as a Go function (not just SQL) | Migrations that need to run arbitrary Go logic (e.g., a data backfill with business rules) rather than pure SQL — https://github.com/pressly/goose            |
| GORM AutoMigrate                             | Schema inferred and applied automatically from Go struct tags                                      | Prototyping only — it doesn't produce reviewable, versioned migration files and is not recommended for a real production change-management process            |
| Atlas                                        | Declarative, diff-based schema-as-code migrations                                                  | Teams that want to describe the _desired end state_ of the schema and have the tool compute the diff, rather than writing incremental migration files by hand |

**Why `golang-migrate` is the default:** it has the widest adoption, the simplest mental model (numbered up/down SQL files applied in order), and integrates cleanly into CI/CD as either a CLI step or an embedded library call at service startup. Docs: https://github.com/golang-migrate/migrate/tree/master/database

**Example:**

```text
migrations/
├── 000001_init_schema.up.sql
├── 000001_init_schema.down.sql
├── 000002_add_order_status.up.sql
└── 000002_add_order_status.down.sql
```

```bash
migrate -database "$DATABASE_URL" -path migrations up
```

## 6. Caching & Redis

| Option                                               | Style                                                                                      | Best for                                                                                                                                                        |
| ---------------------------------------------------- | ------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`redis/go-redis`** ⭐ best default                 | Full-featured client — cluster mode, pub/sub, pipelining, Lua scripting, typed command API | The default choice for essentially any Redis use case in Go — https://github.com/redis/go-redis (client docs: https://redis.io/docs/latest/develop/clients/go/) |
| `redigo` (`gomodule/redigo`)                         | Older, lower-level client, still maintained                                                | Legacy codebases already built on it; not recommended for new projects given `go-redis`'s broader feature set                                                   |
| In-process cache (`patrickmn/go-cache`, `ristretto`) | No external dependency — cache lives in process memory                                     | Single-instance services, or caching values that don't need to be shared/invalidated across multiple backend instances                                          |

**Why `go-redis` is the default:** it's the most actively maintained, most feature-complete Redis client for Go, and is what most other Go libraries assume when they offer Redis integration (rate limiters, session stores, pub/sub-based event buses). Docs: https://redis.io/docs/latest/develop/clients/go/

**Example (cache-aside pattern):**

```go
rdb := redis.NewClient(&redis.Options{Addr: addr})

val, err := rdb.Get(ctx, cacheKey).Result()
if errors.Is(err, redis.Nil) {
    val = computeExpensiveValue()
    rdb.Set(ctx, cacheKey, val, 10*time.Minute)
}
```

## 7. Messaging & Background Jobs

| Option                                                 | Style                                                                                                                       | Best for                                                                                                                                                                  |
| ------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`rabbitmq/amqp091-go`** ⭐ best default for RabbitMQ | Official low-level client for the AMQP 0-9-1 protocol — exchanges, queues, bindings, manual ack/nack, dead-letter exchanges | Direct RabbitMQ integration where you need full protocol-level control — https://github.com/rabbitmq/amqp091-go (docs: https://pkg.go.dev/github.com/rabbitmq/amqp091-go) |
| `hibiken/asynq`                                        | Redis-backed task queue with a built-in web UI, retries with backoff, scheduled/cron tasks                                  | Background job processing where you want a job-queue abstraction (not raw AMQP) and are already running Redis — https://github.com/hibiken/asynq                          |
| `ThreeDotsLabs/watermill`                              | A message-router abstraction that runs over RabbitMQ, Kafka, or an in-memory pub/sub with the _same_ application code       | Wanting one consistent programming model across an in-process event bus and a distributed broker, or planning to swap brokers later — https://watermill.io/               |
| `segmentio/kafka-go`                                   | Kafka client                                                                                                                | Only relevant if the broker is Kafka rather than RabbitMQ                                                                                                                 |

**Why `amqp091-go` is the default for RabbitMQ specifically:** it's the official client maintaining strict protocol compatibility, giving the same primitives (manual ack/nack, DLQ routing) available in any other AMQP 0-9-1 client — the right choice whenever RabbitMQ is already the broker of record. Reach for `asynq` instead when the actual need is "a job queue with retries and scheduling," since building that correctly on raw AMQP by hand is significant, easy-to-get-subtly-wrong work.

**Example (consumer with manual ack/nack and DLQ routing):**

```go
msgs, _ := ch.Consume(queueName, "", false /* manual ack */, false, false, false, nil)
for d := range msgs {
    if err := process(d.Body); err != nil {
        d.Nack(false, false) // requeue=false → routes to the dead-letter exchange
        continue
    }
    d.Ack(false)
}
```

## 8. Full-Text Search

| Option                                                                                 | Style                                         | Best for                                                                                                                                                                                  |
| -------------------------------------------------------------------------------------- | --------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`elastic/go-elasticsearch`** ⭐ best default when Elasticsearch is the search engine | Official client, full access to the Query DSL | Any service already running Elasticsearch/OpenSearch — https://github.com/elastic/go-elasticsearch (docs: https://www.elastic.co/guide/en/elasticsearch/client/go-api/current/index.html) |
| `opensearch-project/opensearch-go`                                                     | Official client for the OpenSearch fork       | Deployments specifically on OpenSearch rather than Elastic-licensed Elasticsearch                                                                                                         |
| Postgres full-text search (`tsvector`/`tsquery`)                                       | No separate search infrastructure             | Search needs that are simple enough not to justify running and operating a separate search cluster                                                                                        |
| `blevesearch/bleve`                                                                    | Pure-Go, embedded full-text search library    | Small-to-medium search needs where you want search to run in-process, with no external service to operate at all                                                                          |

**Why `go-elasticsearch` is the default when ES is already in the stack:** it's the officially maintained client, kept in sync with the server's Query DSL, with no higher-level "repository" abstraction layered on top — queries are composed directly (via raw JSON bodies or the client's typed request structs), which keeps you working with the full expressiveness of Elasticsearch's actual query language rather than a leaky abstraction over it.

**Example:**

```go
res, err := es.Search(
    es.Search.WithIndex("products"),
    es.Search.WithBody(strings.NewReader(`{"query":{"match":{"name":"laptop"}}}`)),
)
```

## 9. Object Storage

| Option                               | Style                                                                               | Best for                                                                                                                                                                                                      |
| ------------------------------------ | ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`minio/minio-go`** ⭐ best default | Official MinIO SDK, fully S3-API-compatible (works identically against real AWS S3) | Any service using MinIO, or wanting an S3-compatible client that also works unmodified against AWS S3 — https://github.com/minio/minio-go (docs: https://min.io/docs/minio/linux/developers/go/minio-go.html) |
| `aws/aws-sdk-go-v2` (S3 module)      | Official AWS SDK                                                                    | Deployments specifically and exclusively on AWS S3, wanting the full AWS SDK feature surface (IAM integration, other AWS services) beyond just S3                                                             |
| `gocloud.dev/blob`                   | Vendor-neutral abstraction over S3, GCS, Azure Blob, and local filesystem           | Wanting to abstract away the specific storage provider so it can change later with minimal code impact                                                                                                        |

**Why `minio-go` is the default here:** it covers pre-signed URLs, multipart uploads, and bucket policies, and — because it targets the S3 API surface — the same code works against MinIO in local/self-hosted environments and against real AWS S3 in production, which is a common and valuable property for dev/prod parity.

**Example:**

```go
client, _ := minio.New(endpoint, &minio.Options{Creds: creds, Secure: true})
_, err := client.PutObject(ctx, bucket, objectName, reader, size, minio.PutObjectOptions{ContentType: "application/pdf"})
```

## 10. Resilience: Circuit Breakers & Rate Limiting

| Option                                                                     | Style                                                                                               | Best for                                                                                                                      |
| -------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| **`sony/gobreaker`** ⭐ best default for circuit breaking                  | State-machine circuit breaker (`Closed → Open → Half-Open`), configurable trip conditions           | Wrapping calls to unreliable downstream dependencies (payment gateways, third-party APIs) — https://github.com/sony/gobreaker |
| `slok/goresilience`                                                        | A broader resilience toolkit — retries, circuit breaker, bulkhead, timeout — composable middlewares | Wanting several resilience patterns from one consistent, composable library rather than picking single-purpose ones           |
| `golang.org/x/time/rate` ⭐ best default for single-instance rate limiting | Token-bucket limiter, maintained by the Go team as part of the extended standard library            | In-process rate limiting on a single instance — https://pkg.go.dev/golang.org/x/time/rate                                     |
| `go-redis/redis_rate`                                                      | Redis-backed distributed token bucket                                                               | Rate limiting that must be consistent **across multiple instances** of a service behind a load balancer                       |

**Why these two specifically:** `gobreaker` is small, focused, and does exactly one thing well, matching Go's general preference for narrow libraries over broad frameworks; `x/time/rate` is effectively "part of Go" (published under the `golang.org/x` umbrella, maintained by the Go team) and is the correct default whenever rate limiting doesn't need to be coordinated across processes. Reach for `redis_rate` specifically the moment multiple backend instances need to share one limit.

**Example (circuit breaker):**

```go
cb := gobreaker.NewCircuitBreaker(gobreaker.Settings{
    Name:        "payment-gateway",
    MaxRequests: 3,
    Timeout:     10 * time.Second,
    ReadyToTrip: func(counts gobreaker.Counts) bool {
        return counts.ConsecutiveFailures > 5
    },
})
result, err := cb.Execute(func() (interface{}, error) {
    return paymentClient.Charge(ctx, amount)
})
```

## 11. Authentication (JWT)

| Option                               | Style                                                                              | Best for                                                                                                                                   |
| ------------------------------------ | ---------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| **`golang-jwt/jwt`** ⭐ best default | Sign/verify JWTs, supports HS256/RS256/ES256 and custom claims                     | The default for essentially any JWT need in Go — https://github.com/golang-jwt/jwt (docs: https://pkg.go.dev/github.com/golang-jwt/jwt/v5) |
| `lestrrat-go/jwx`                    | Broader JOSE implementation — JWT, JWS, JWE, JWK, including JWKS-endpoint handling | Needing full JOSE support (e.g., consuming rotating public keys from a JWKS endpoint, or JWE encryption) beyond plain JWT signing          |
| `go-oauth2/oauth2`                   | Full OAuth2 server implementation                                                  | Building an OAuth2 authorization server, not just verifying bearer tokens                                                                  |

**Important history to know:** the long-time most-used JWT library, `dgrijalva/jwt-go`, is **archived and unmaintained** — its original author transferred maintenance to the community-run `golang-jwt/jwt` fork, which is the actively developed continuation. A lot of older tutorials and StackOverflow answers still reference the old, archived import path — always use `golang-jwt/jwt/v5` for new code.

**Example (RS256 sign + verify):**

```go
token := jwt.NewWithClaims(jwt.SigningMethodRS256, jwt.MapClaims{
    "sub": userID,
    "exp": time.Now().Add(15 * time.Minute).Unix(),
})
signed, err := token.SignedString(rsaPrivateKey)

parsed, err := jwt.Parse(tokenString, func(t *jwt.Token) (interface{}, error) {
    return rsaPublicKey, nil
}, jwt.WithValidMethods([]string{"RS256"}))
```

## 12. Structured Logging

| Option                                            | Style                                                                                                                                              | Best for                                                                                                                                                                              |
| ------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`uber-go/zap`** ⭐ best for maximum performance | Typed-field API (`zap.String("url", url)`), extremely low allocation overhead                                                                      | High-throughput services where logging overhead is measurable, or teams that prefer explicit typed fields — https://github.com/uber-go/zap (docs: https://pkg.go.dev/go.uber.org/zap) |
| **`rs/zerolog`** ⭐ best for developer ergonomics | Fluent chaining API (`log.Info().Str("foo","bar").Msg("hello")`), benchmarked as competitive with or faster than zap by the zerolog project itself | Teams that want a more ergonomic, chainable call style day-to-day — https://github.com/rs/zerolog                                                                                     |
| Standard library `log/slog` (Go 1.21+)            | Structured logging built into the standard library, no external dependency                                                                         | Wanting zero-dependency structured logging, or a stable baseline that doesn't tie the codebase to a third-party API                                                                   |

**Which to pick:** `zap` and `zerolog` are both excellent, high-performance, JSON-structured loggers; the choice between them is genuinely more about API taste than capability — pick `zap` if the team prefers explicit typed field constructors, `zerolog` if the team prefers a fluent chaining style. `log/slog` is worth defaulting to specifically when minimizing dependencies matters more than either library's extra ergonomics or performance headroom.

**Example (zerolog):**

```go
log.Info().
    Str("method", c.Request.Method).
    Str("path", c.Request.URL.Path).
    Dur("duration", time.Since(start)).
    Msg("request completed")
```

## 13. Observability: Metrics & Tracing

| Option                                                     | Style                                                                      | Best for                                                                                                                                                                                     |
| ---------------------------------------------------------- | -------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`prometheus/client_golang`** ⭐ best default for metrics | Official Prometheus client, exposes a `/metrics` endpoint                  | The default whenever Prometheus/Grafana is the observability backend — https://github.com/prometheus/client_golang (docs: https://prometheus.io/docs/guides/go-application/)                 |
| **`go.opentelemetry.io/otel`** ⭐ best default for tracing | Vendor-neutral instrumentation SDK, exports to any OTLP-compatible backend | Distributed tracing across services, with instrumentation packages available for Gin (`otelgin`), most SQL drivers, and most messaging clients — https://opentelemetry.io/docs/languages/go/ |
| Micrometer-style vendor SDKs (Datadog, New Relic, etc.)    | Vendor-specific instrumentation libraries                                  | Only when locked into a specific vendor's proprietary agent/APM product rather than an open standard                                                                                         |

**Why these two together are the default:** Prometheus's client library is the de facto standard for metrics in cloud-native Go services (it's what the Prometheus project itself publishes and maintains), and OpenTelemetry-Go is the vendor-neutral standard for tracing, with first-class Gin middleware support so instrumenting a router is a few lines, not a rewrite.

**Example (metrics):**

```go
var httpRequestsTotal = prometheus.NewCounterVec(
    prometheus.CounterOpts{Name: "http_requests_total"},
    []string{"method", "path", "status"},
)
router.GET("/metrics", gin.WrapH(promhttp.Handler()))
```

## 14. Dependency Injection Tooling

| Option                                                     | Style                                                                                                                     | Best for                                                                                                                                                                                          |
| ---------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Manual constructor wiring** ⭐ best default              | Plain Go functions in `main.go` calling constructors explicitly                                                           | Most services — fully explicit, zero dependencies, debuggable with a normal debugger                                                                                                              |
| **`google/wire`** ⭐ best once wiring outgrows manual code | Compile-time DI **code generator** (not a runtime container) — declares "providers," generates the equivalent wiring code | Large dependency graphs (dozens of components) where manual wiring becomes repetitive boilerplate — https://github.com/google/wire (docs: https://github.com/google/wire/blob/main/docs/guide.md) |
| `uber-go/fx`                                               | Runtime DI framework with lifecycle hooks, closer in spirit to a Spring-style container                                   | Teams that specifically want a framework-managed application lifecycle and are comfortable trading some explicitness for it                                                                       |
| `samber/do`                                                | Lightweight runtime DI container/service locator                                                                          | Smaller projects wanting a simple runtime container without the full weight of `fx`                                                                                                               |

**Why manual wiring, then `wire`, is the recommended progression:** starting with explicit constructor calls keeps the "no framework magic" property Go is built around; `wire` is the natural next step because it's a code generator, not a runtime container — the _generated_ code looks exactly like the hand-written wiring you'd otherwise maintain, just produced automatically from a declarative provider set. Reach for `fx` only when the team explicitly wants Spring-container-like ergonomics (lifecycle hooks, `Invoke`-style bootstrapping) and accepts the corresponding loss of "everything is visible in `main.go`."

**Example (`wire`):**

```go
// wire.go (build-tag excluded from normal build)
func InitializeOrderService(db *sql.DB) *OrderService {
    wire.Build(NewOrderRepository, NewOrderService)
    return nil // replaced by generated code
}
// `wire` generates wire_gen.go containing the real implementation
```

## 15. Testing & Mocking

| Option                                                     | Style                                                                             | Best for                                                                                                                                                                 |
| ---------------------------------------------------------- | --------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`stretchr/testify`** ⭐ best default                     | `assert`/`require` assertion helpers, test suites, `testify/mock`                 | Virtually every Go project — the most widely used assertion/mocking library — https://github.com/stretchr/testify (docs: https://pkg.go.dev/github.com/stretchr/testify) |
| **`go.uber.org/mock`** ⭐ best default for generated mocks | Generates mock implementations of interfaces from source (`mockgen`)              | Larger interfaces where hand-writing a fake is more tedious than generating one — https://github.com/uber-go/mock                                                        |
| `testcontainers/testcontainers-go`                         | Spins up real Docker containers (Postgres, Redis, RabbitMQ) for integration tests | Integration tests that need to run against the real thing rather than a mock                                                                                             |
| Hand-written fakes                                         | Plain structs implementing an interface                                           | The idiomatic default for **small** interfaces (1–3 methods) — often clearer and less "magic" than a generated mock                                                      |

**Important history to know:** `go.uber.org/mock` is the actively maintained **successor** to `github.com/golang/mock`, which is now archived — use the new import path for any new code; older tutorials referencing `golang/mock` are describing a project that no longer receives updates.

**Example (table-driven test with testify assertions):**

```go
func TestDivide(t *testing.T) {
    result, err := Divide(10, 2)
    require.NoError(t, err)
    assert.Equal(t, 5.0, result)

    _, err = Divide(10, 0)
    assert.ErrorIs(t, err, ErrDivisionByZero)
}
```

## 16. Linting & Static Analysis

| Option                              | Style                                                                                                                      | Best for                                                                                                                             |
| ----------------------------------- | -------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------ |
| **`golangci-lint`** ⭐ best default | Aggregates `go vet`, `staticcheck`, `errcheck`, `gosec`, and dozens more linters behind one config file and one CI command | The de facto standard aggregator for essentially every Go project — https://golangci-lint.run/                                       |
| `staticcheck` alone                 | A single, very high-quality static analysis tool                                                                           | Wanting one focused tool rather than the full aggregator — mostly superseded by using it _through_ `golangci-lint`, which bundles it |
| `gosec` alone                       | Go-specific security scanner                                                                                               | Standalone security scanning in a pipeline stage separate from general linting — also bundled inside `golangci-lint`                 |

**Why `golangci-lint` is the default:** it replaces the need to individually configure and run a dozen separate tools, runs fast (parallelized, with caching), and is the tool referenced by nearly every public Go style guide, including the [Uber Go Style Guide](https://github.com/uber-go/guide), as the expected CI gate.

**Example (`.golangci.yml` excerpt):**

```yaml
linters:
  enable:
    - govet
    - staticcheck
    - errcheck
    - gosec
    - unused
```

## 17. Summary Table

| Concern         | Best default               | Reach for an alternative when...                                    |
| --------------- | -------------------------- | ------------------------------------------------------------------- |
| Web framework   | Gin                        | Raw throughput is the overriding priority → Fiber                   |
| Config          | Viper                      | Service is tiny/single-purpose → envconfig / `caarlos0/env`         |
| Validation      | go-playground/validator    | Validation logic is too dynamic for struct tags → ozzo-validation   |
| Persistence     | GORM                       | Complex relational graph → `ent`; performance-critical SQL → `sqlc` |
| Migrations      | golang-migrate             | Migration needs arbitrary Go logic → goose                          |
| Cache/Redis     | go-redis                   | Single-instance, no shared state needed → in-process cache          |
| Messaging       | amqp091-go (RabbitMQ)      | Need a job-queue abstraction, not raw AMQP → asynq                  |
| Search          | go-elasticsearch           | Simple needs, avoid extra infra → Postgres full-text search         |
| Object storage  | minio-go                   | AWS-only, need full AWS SDK → aws-sdk-go-v2                         |
| Circuit breaker | gobreaker                  | Need several resilience patterns bundled → goresilience             |
| Rate limiting   | x/time/rate                | Needs to be distributed across instances → redis_rate               |
| JWT             | golang-jwt/jwt             | Need full JOSE/JWKS support → lestrrat-go/jwx                       |
| Logging         | zap or zerolog             | Want zero third-party dependency → log/slog                         |
| Metrics         | client_golang              | — (essentially unopposed as the Prometheus-ecosystem default)       |
| Tracing         | OpenTelemetry-Go           | Locked into a proprietary vendor APM                                |
| DI              | Manual wiring              | Graph is large → google/wire; want a managed lifecycle → uber-go/fx |
| Testing/mocking | testify + go.uber.org/mock | Interfaces are tiny → hand-written fakes                            |
| Linting         | golangci-lint              | — (essentially unopposed as the aggregator standard)                |

## 18. References

All links verified reachable at the time of writing (September 2026).

1. Gin Web Framework — https://github.com/gin-gonic/gin ; docs — https://gin-gonic.com/docs/
2. Go official tutorial for Gin — https://go.dev/doc/tutorial/web-service-gin
3. `spf13/viper` — https://github.com/spf13/viper
4. `go-playground/validator` — https://github.com/go-playground/validator
5. GORM — https://gorm.io/docs/
6. `ent` — https://entgo.io/docs/getting-started
7. `sqlc` — https://docs.sqlc.dev/
8. Encore, _Comparing the best Go ORMs_ — https://encore.dev/articles/go-orms
9. Rost Glukhov, _Comparing Go ORMs for PostgreSQL: GORM vs Ent vs Bun vs sqlc_ — https://www.glukhov.org/post/2025/09/comparing-go-orms-gorm-ent-bun-sqlc/
10. Bytebase, _Choose the Right Golang ORM or Query Builder_ — https://www.bytebase.com/blog/golang-orm-query-builder/
11. `golang-migrate/migrate` — https://github.com/golang-migrate/migrate
12. `pressly/goose` — https://github.com/pressly/goose
13. `redis/go-redis` — https://redis.io/docs/latest/develop/clients/go/
14. `rabbitmq/amqp091-go` — https://github.com/rabbitmq/amqp091-go
15. `hibiken/asynq` — https://github.com/hibiken/asynq
16. `ThreeDotsLabs/watermill` — https://watermill.io/
17. `elastic/go-elasticsearch` — https://github.com/elastic/go-elasticsearch
18. `minio/minio-go` — https://min.io/docs/minio/linux/developers/go/minio-go.html
19. `sony/gobreaker` — https://github.com/sony/gobreaker
20. `golang.org/x/time/rate` — https://pkg.go.dev/golang.org/x/time/rate
21. `golang-jwt/jwt` (successor to archived `dgrijalva/jwt-go`) — https://github.com/golang-jwt/jwt
22. `uber-go/zap` — https://github.com/uber-go/zap
23. `rs/zerolog` — https://github.com/rs/zerolog
24. `prometheus/client_golang` — https://prometheus.io/docs/guides/go-application/
25. OpenTelemetry-Go — https://opentelemetry.io/docs/languages/go/
26. `google/wire` — https://github.com/google/wire
27. `stretchr/testify` — https://github.com/stretchr/testify
28. `go.uber.org/mock` (successor to archived `golang/mock`) — https://github.com/uber-go/mock
29. `golangci-lint` — https://golangci-lint.run/
30. Uber Go Style Guide — https://github.com/uber-go/guide

> Items marked with official docs/project URLs (1–7, 11–29) are primary sources for the tool itself. Items 8–10 are independent, recent (2025–2026) comparative analyses used specifically for the ORM trade-off discussion in Section 4, cross-checked against each other before inclusion.
