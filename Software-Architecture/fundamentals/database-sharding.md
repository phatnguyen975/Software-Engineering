# Database Sharding: Deep Dive for Production Systems

## Table of Contents

1. [Tổng quan về Sharding](#1-tổng-quan-về-sharding)
2. [Khi nào cần Sharding?](#2-khi-nào-cần-sharding)
3. [Kiến trúc Sharding cơ bản](#3-kiến-trúc-sharding-cơ-bản)
4. [Sharding Strategies (Kỹ thuật phân mảnh)](#4-sharding-strategies-kỹ-thuật-phân-mảnh)
5. [Sharding Key — Trọng tâm thiết kế](#5-sharding-key--trọng-tâm-thiết-kế)
6. [Sharding trong SQL vs NoSQL](#6-sharding-trong-sql-vs-nosql)
7. [Cross-Shard Operations](#7-cross-shard-operations)
8. [Rebalancing & Resharding](#8-rebalancing--resharding)
9. [Case Studies: Netflix, Amazon, Uber, Discord](#9-case-studies-netflix-amazon-uber-discord)
10. [Anti-Patterns](#10-anti-patterns)
11. [Best Practices tổng hợp](#11-best-practices-tổng-hợp)
12. [Monitoring & Observability](#12-monitoring--observability)
13. [Checklist Production](#13-checklist-production)

## 1. Tổng quan về Sharding

### 1.1 Sharding là gì?

**Database Sharding** là kỹ thuật phân vùng ngang (horizontal partitioning) dữ liệu ra nhiều database độc lập, mỗi database được gọi là một **shard**. Mỗi shard chứa một tập con (subset) dữ liệu của toàn bộ dataset, và cùng nhau chúng tạo thành toàn bộ dữ liệu của hệ thống.

> Sharding khác với Replication: Replication sao chép toàn bộ dữ liệu sang nhiều node (mục đích: HA, read scaling). Sharding chia dữ liệu ra nhiều node (mục đích: write scaling, storage scaling).

### 1.2 Phân biệt các kỹ thuật scaling DB

| Kỹ thuật                     | Mô tả                                     | Dùng khi                                       |
| ---------------------------- | ----------------------------------------- | ---------------------------------------------- |
| **Vertical Scaling**         | Nâng cấp phần cứng (CPU, RAM, SSD)        | Dataset < vài TB, budget cho hardware          |
| **Read Replica**             | Replica chỉ phục vụ read traffic          | Read >> Write (ratio > 10:1)                   |
| **Caching Layer**            | Redis/Memcached trước DB                  | Hot data có thể cache được                     |
| **Partitioning**             | Chia bảng trong cùng 1 DB instance        | Quản lý data lifecycle (e.g., archive by date) |
| **Sharding**                 | Chia data ra nhiều DB instance khác nhau  | Scale vượt giới hạn 1 máy, write-heavy         |
| **Functional Decomposition** | Tách DB theo domain (orders DB, users DB) | Microservices, domain isolation                |

### 1.3 Ví dụ hệ thống ShopX

Xuyên suốt tài liệu này, chúng ta sẽ dùng **ShopX** — một nền tảng e-commerce B2C với các đặc điểm:

- **10 triệu** active users/tháng
- **5 triệu** đơn hàng/ngày (peak: 50 triệu/ngày — ngày Sale)
- **100 triệu** sản phẩm trong catalog
- **1 tỷ** records trong bảng `orders` sau 3 năm vận hành
- MySQL cluster đang gặp bottleneck: query `orders` bảng 1 tỷ row, write latency tăng lên 200ms, CPU DB luôn > 80%

```mermaid
graph TB
    subgraph "ShopX - Trước khi Sharding (Vấn đề)"
        U[Users<br/>10M active] -->|All traffic| LB[Load Balancer]
        LB --> APP[App Servers]
        APP --> DB[(Single MySQL<br/>1 Billion rows<br/>❌ CPU 80%+<br/>❌ Write 200ms<br/>❌ 8TB data)]
        APP --> CACHE[Redis Cache]
    end
```

## 2. Khi nào cần Sharding?

### 2.1 Các dấu hiệu cần Sharding

**Dấu hiệu về Performance:**

- Write latency tăng dù đã optimize index, query
- Single DB node CPU/Memory thường xuyên > 70-80%
- Table size > 50-100GB và tiếp tục tăng
- `VACUUM`, `ANALYZE`, `ALTER TABLE` timeout hoặc mất hàng giờ

**Dấu hiệu về Storage:**

- Dataset sắp vượt dung lượng tối đa của 1 server
- Cost phần cứng tăng phi tuyến (vertical scaling lên AWS r7g.16xlarge rất đắt)
- Backup/restore window quá dài (> 8 tiếng không chấp nhận được)

**Dấu hiệu về Business:**

- Transaction throughput vượt ~5,000-10,000 TPS trên 1 node MySQL/PostgreSQL
- Cần geographic distribution (data ở gần user về mặt địa lý — GDPR, latency)

### 2.2 Quy tắc: Sharding là giải pháp cuối cùng

```mermaid
flowchart TD
    A[DB bắt đầu có vấn đề] --> B{Đã tối ưu Query,<br/>Index chưa?}
    B -->|Chưa| C[Optimize Queries & Indexes<br/>EXPLAIN ANALYZE, composite index]
    B -->|Rồi| D{Đã thêm Read Replica?}
    C --> D
    D -->|Chưa| E[Thêm Read Replica<br/>Phân tải 70-80% read traffic]
    D -->|Rồi| F{Đã dùng Caching?}
    E --> F
    F -->|Chưa| G[Redis/Memcached<br/>Cache hot data, session]
    F -->|Rồi| H{Đã Vertical Scale?}
    G --> H
    H -->|Chưa| I[Nâng cấp phần cứng<br/>Đánh giá cost/benefit]
    H -->|Rồi| J{Đã tách DB theo<br/>Functional Domain?}
    I --> J
    J -->|Chưa| K[Functional Decomposition<br/>orders_db, users_db, catalog_db]
    J -->|Rồi| L[✅ Bây giờ mới đến Sharding]
    K --> L

    style L fill:#2ecc71,color:#fff
    style A fill:#e74c3c,color:#fff
```

> **ShopX thực tế:** Trước khi sharding, team đã: (1) thêm 3 Read Replica cho query reporting, (2) dùng Redis cache product catalog, (3) tách `catalog_db` riêng. Bottleneck còn lại là bảng `orders` với write-heavy workload — đây là lúc cần sharding.

## 3. Kiến trúc Sharding cơ bản

### 3.1 Các thành phần trong Sharding Architecture

```mermaid
graph TD
    APP[Application Layer] --> SR[Shard Router / Proxy]

    SR --> |"shard_key % 4 == 0"| S0[(Shard 0<br/>DB Instance)]
    SR --> |"shard_key % 4 == 1"| S1[(Shard 1<br/>DB Instance)]
    SR --> |"shard_key % 4 == 2"| S2[(Shard 2<br/>DB Instance)]
    SR --> |"shard_key % 4 == 3"| S3[(Shard 3<br/>DB Instance)]

    S0 --> R0[(Replica 0)]
    S1 --> R1[(Replica 1)]
    S2 --> R2[(Replica 2)]
    S3 --> R3[(Replica 3)]

    CM[Config / Metadata Server<br/>Shard Map] --> SR

    style SR fill:#3498db,color:#fff
    style CM fill:#9b59b6,color:#fff
```

**Thành phần chính:**

| Component                     | Vai trò                                         | Ví dụ thực tế                           |
| ----------------------------- | ----------------------------------------------- | --------------------------------------- |
| **Shard Router / Proxy**      | Nhận query, đọc shard map, route đến đúng shard | Vitess VTGate, MongoDB mongos, ProxySQL |
| **Shard Map / Config Server** | Lưu metadata: key range → shard ID              | MongoDB Config Server, ZooKeeper, etcd  |
| **Shard (DB Instance)**       | Database instance thực sự chứa data             | MySQL instance, PostgreSQL instance     |
| **Shard Replica**             | Replica của từng shard (HA + read scaling)      | Mỗi shard nên có ít nhất 1 replica      |

### 3.2 Application-level vs Middleware-level vs Database-native

**Cách 1: Application-level Sharding**

```mermaid
graph LR
    APP[Application Code] -->|"Tự tính shard_id<br/>shard = user_id % 4"| DB0[(DB Shard 0)]
    APP --> DB1[(DB Shard 1)]
    APP --> DB2[(DB Shard 2)]
    APP --> DB3[(DB Shard 3)]
```

- Ứng dụng tự chứa logic routing
- Dùng trong: các hệ thống tự build (Instagram ban đầu, Pinterest)
- **Ưu điểm:** Linh hoạt, không overhead middleware
- **Nhược điểm:** Shard logic nằm trong code, khó thay đổi, mỗi service phải implement lại

**Cách 2: Proxy / Middleware Sharding**

```mermaid
graph LR
    APP[Application Code] --> PROXY[Vitess VTGate<br/>ProxySQL<br/>Citus Coordinator]
    PROXY --> DB0[(Shard 0)]
    PROXY --> DB1[(Shard 1)]
    PROXY --> DB2[(Shard 2)]
    PROXY --> DB3[(Shard 3)]
```

- Proxy transparent với application (app chỉ biết 1 connection string)
- Dùng trong: YouTube (Vitess), các hệ thống muốn tách logic routing
- **Ưu điểm:** App không cần biết sharding logic, centralized routing
- **Nhược điểm:** Thêm 1 network hop, proxy có thể là SPOF nếu không HA

**Cách 3: Database-native Sharding**

```mermaid
graph LR
    APP[Application] --> COORD[Coordinator Node<br/>PostgreSQL Citus<br/>CockroachDB Gateway]
    COORD --> W0[Worker Node 0]
    COORD --> W1[Worker Node 1]
    COORD --> W2[Worker Node 2]
```

- DB engine tự xử lý sharding internally
- Dùng trong: CockroachDB, Google Spanner, TiDB, Citus (PostgreSQL extension), MongoDB Atlas
- **Ưu điểm:** Transparent hoàn toàn, tự động rebalancing
- **Nhược điểm:** Vendor lock-in, có thể phức tạp trong self-hosted

## 4. Sharding Strategies (Kỹ thuật phân mảnh)

### 4.1 Range-based Sharding

Chia data dựa trên **khoảng giá trị** của shard key.

```mermaid
graph TD
    R[Shard Router] -->|"order_id: 1 → 250M"| S0[(Shard 0<br/>orders 1-250M)]
    R -->|"order_id: 250M → 500M"| S1[(Shard 1<br/>orders 250M-500M)]
    R -->|"order_id: 500M → 750M"| S2[(Shard 2<br/>orders 500M-750M)]
    R -->|"order_id: 750M → 1B"| S3[(Shard 3<br/>orders 750M-1B)]
```

**ShopX example — Range by order_id:**

```sql
-- Routing logic (pseudo-code)
def get_shard(order_id):
    if order_id < 250_000_000:   return "shard_0"  -- DB host: db-shard-0.shopx.internal
    elif order_id < 500_000_000: return "shard_1"
    elif order_id < 750_000_000: return "shard_2"
    else:                         return "shard_3"
```

**Ưu điểm:**

- Range query hiệu quả: `SELECT * FROM orders WHERE order_id BETWEEN 1M AND 2M` → chỉ query 1 shard
- Data locality tốt: orders của 1 khoảng thời gian nằm gần nhau

**Nhược điểm:**

- **Hot Shard:** Nếu key là `created_at` (time-based), shard mới nhất nhận toàn bộ write traffic
- Uneven distribution: 1 shard có thể chứa nhiều row hơn shard khác

**Khi nào dùng:**

- Workload cần range scan nhiều
- Data có natural ordering (time-series)
- Biết trước phân bố data

### 4.2 Hash-based Sharding

Dùng **hash function** trên shard key để xác định shard.

```mermaid
graph TD
    R[Shard Router] -->|"hash(user_id) % 4 == 0"| S0[(Shard 0)]
    R -->|"hash(user_id) % 4 == 1"| S1[(Shard 1)]
    R -->|"hash(user_id) % 4 == 2"| S2[(Shard 2)]
    R -->|"hash(user_id) % 4 == 3"| S3[(Shard 3)]

    subgraph "Hash Function"
        K[user_id = 12345] -->|"MD5/SHA1/MurmurHash"| H["hash = 0x7F3A..."]
        H -->|"% 4"| SID["shard_id = 2"]
    end
```

**ShopX example:**

```python
import hashlib

def get_shard(user_id: int, num_shards: int = 4) -> int:
    # Dùng consistent hashing hoặc simple modulo
    hash_val = int(hashlib.md5(str(user_id).encode()).hexdigest(), 16)
    return hash_val % num_shards

# user_id=1001 → shard 2
# user_id=1002 → shard 0
# user_id=1003 → shard 3
```

**Ưu điểm:**

- **Even distribution:** Data phân bố đều các shard (không có hot shard về storage)
- Đơn giản implement

**Nhược điểm:**

- **Range query kém:** `SELECT * FROM orders WHERE user_id BETWEEN 1000 AND 2000` → phải query ALL shards
- **Resharding khó:** Thêm shard mới → hash % N thay đổi → gần như toàn bộ data phải migrate

**Khi nào dùng:**

- Point lookup là chính (query theo ID)
- Data distribution đồng đều là ưu tiên
- Số shard ít thay đổi

### 4.3 Consistent Hashing

Giải quyết vấn đề resharding của simple hash bằng cách dùng **virtual ring**.

```mermaid
graph TD
    subgraph "Consistent Hash Ring"
        direction LR
        N0["Node 0<br/>(0-90°)"] --- N1["Node 1<br/>(90-180°)"]
        N1 --- N2["Node 2<br/>(180-270°)"]
        N2 --- N3["Node 3<br/>(270-360°)"]
        N3 --- N0
    end

    subgraph "Key Placement"
        K1["Key A → hash → 45°<br/>→ Node 0"]
        K2["Key B → hash → 120°<br/>→ Node 1"]
        K3["Key C → hash → 200°<br/>→ Node 2"]
    end
```

**Cách hoạt động:**

1. Tạo một vòng hash (0 → 2^32 - 1)
2. Map mỗi shard/node vào nhiều điểm trên vòng (virtual nodes — vnodes)
3. Mỗi key được hash vào điểm trên vòng, thuộc về node tiếp theo theo chiều kim đồng hồ
4. Thêm node mới: chỉ một phần nhỏ data cần di chuyển (thay vì toàn bộ)

**Tại sao cần Virtual Nodes (vnodes)?**

```mermaid
graph LR
    subgraph "Không có vnodes - Uneven"
        N0A["Node 0: 33% data"]
        N1A["Node 1: 5% data ❌"]
        N2A["Node 2: 62% data ❌"]
    end

    subgraph "Có vnodes (150 vnodes/node) - Even"
        N0B["Node 0: ~33% data ✅"]
        N1B["Node 1: ~33% data ✅"]
        N2B["Node 2: ~34% data ✅"]
    end
```

**Dùng trong thực tế:**

- **Amazon DynamoDB:** Consistent hashing với vnodes
- **Apache Cassandra:** Murmur3 hash, 256 vnodes mặc định mỗi node
- **Riak**, **Couchbase**

### 4.4 Directory-based Sharding (Lookup Table)

Dùng **bảng tra cứu** (lookup table) để ánh xạ key → shard.

```mermaid
graph LR
    APP[Application] --> SR[Shard Router]
    SR --> LT[(Lookup Table<br/>user_id → shard_id<br/>1001 → shard_2<br/>1002 → shard_0<br/>1003 → shard_1)]
    LT --> SR
    SR --> S0[(Shard 0)]
    SR --> S1[(Shard 1)]
    SR --> S2[(Shard 2)]
```

**ShopX example — Lookup table:**

```sql
-- Metadata DB (separate, small, heavily cached)
CREATE TABLE shard_map (
    entity_id    BIGINT PRIMARY KEY,
    entity_type  VARCHAR(50),  -- 'user', 'merchant', 'order'
    shard_id     TINYINT NOT NULL,
    created_at   TIMESTAMP DEFAULT NOW()
);

-- Khi tạo user mới: chọn shard ít data nhất, ghi vào lookup table
INSERT INTO shard_map (entity_id, entity_type, shard_id) VALUES (1001, 'user', 2);
```

**Ưu điểm:**

- **Linh hoạt nhất:** Có thể move entity sang shard khác mà không đổi logic
- Không bị ràng buộc bởi hash/range algorithm

**Nhược điểm:**

- Lookup table là **SPOF và bottleneck** nếu không cache
- Tăng 1 network round-trip
- Lookup table phải được replicate và highly available

**Khi nào dùng:**

- Cần khả năng rebalancing linh hoạt mà không rekey data
- Entity count không quá lớn (vài trăm triệu entries có thể fit vào Redis)
- Shopify dùng cách này: mỗi merchant có 1 shard_id trong lookup table

### 4.5 Geographic (Geo) Sharding

Phân chia data theo **vùng địa lý**.

```mermaid
graph TD
    GLB[Global Load Balancer<br/>GeoDNS / Anycast]
    GLB -->|"User IP: Vietnam/SEA"| APAC[APAC Shard<br/>Singapore DC<br/>Users: VN, TH, ID, MY]
    GLB -->|"User IP: Europe"| EU[EU Shard<br/>Frankfurt DC<br/>Users: DE, FR, UK, ...]
    GLB -->|"User IP: Americas"| US[US Shard<br/>Virginia DC<br/>Users: US, CA, BR, ...]

    APAC --> APAC_DB[(MySQL Cluster<br/>APAC)]
    EU --> EU_DB[(MySQL Cluster<br/>EU — GDPR)]
    US --> US_DB[(MySQL Cluster<br/>US)]
```

**Dùng trong thực tế:**

- **GDPR compliance:** Data người dùng EU phải ở EU (AWS Frankfurt)
- **Latency reduction:** User VN query DB ở Singapore thay vì US (~20ms vs ~200ms)
- Uber: Trip data được lưu ở shard của city/region tương ứng

### 4.6 So sánh các Strategies

```mermaid
quadrantChart
    title Sharding Strategy Comparison
    x-axis "Dễ Implement" --> "Phức tạp"
    y-axis "Kém Linh Hoạt" --> "Linh Hoạt Cao"
    quadrant-1 Linh hoạt, Phức tạp
    quadrant-2 Linh hoạt, Đơn giản
    quadrant-3 Kém linh hoạt, Đơn giản
    quadrant-4 Kém linh hoạt, Phức tạp
    Range-based: [0.25, 0.35]
    Hash-based: [0.3, 0.2]
    Consistent Hashing: [0.65, 0.55]
    Directory-based: [0.7, 0.85]
    Geo Sharding: [0.6, 0.7]
```

| Strategy            | Distribution            | Range Query       | Resharding               | Use Case                    |
| ------------------- | ----------------------- | ----------------- | ------------------------ | --------------------------- |
| **Range**           | Uneven (hot shard risk) | ✅ Tốt            | ✅ Dễ add shard          | Time-series, ordered data   |
| **Hash (modulo)**   | ✅ Even                 | ❌ Scatter-gather | ❌ Khó (toàn bộ re-hash) | Point lookup, random access |
| **Consistent Hash** | ✅ Even                 | ❌ Scatter-gather | ✅ Minimal data move     | Distributed caches, NoSQL   |
| **Directory-based** | Configurable            | Depends           | ✅ Linh hoạt nhất        | Multi-tenant SaaS           |
| **Geographic**      | By region               | Within region     | Medium                   | Global apps, compliance     |

## 5. Sharding Key — Trọng tâm thiết kế

### 5.1 Tại sao Sharding Key quan trọng?

Chọn sai shard key là lỗi **không thể sửa dễ dàng** (cần migrate toàn bộ data). Đây là quyết định kiến trúc quan trọng nhất khi implement sharding.

### 5.2 Tiêu chí chọn Shard Key tốt

**Tiêu chí 1: High Cardinality (Độ đa dạng cao)**

```sql
-- ❌ BAD: Shard key có ít giá trị duy nhất
-- Chỉ có 3 giá trị → chỉ 3 shard tối đa, uneven
ALTER TABLE orders SHARD KEY (status); -- 'pending', 'completed', 'cancelled'

-- ✅ GOOD: Cardinality cao, phân bố đều
ALTER TABLE orders SHARD KEY (user_id); -- Hàng triệu unique users
```

**Tiêu chí 2: Even Distribution (Phân bố đều)**

```
-- ❌ BAD: user_country → 80% orders từ Vietnam → 80% load vào 1 shard
-- ❌ BAD: is_premium_user → 95% users không premium → uneven

-- ✅ GOOD: user_id (phân bố random, đều)
-- ✅ GOOD: order_id (auto-increment hoặc UUID)
```

**Tiêu chí 3: Query Pattern Alignment (Khớp với query patterns)**

```sql
-- ShopX query patterns phổ biến nhất:
-- 1. "Lấy tất cả đơn hàng của user X"  → user_id là shard key tốt
-- 2. "Tìm đơn hàng theo order_id"       → order_id cũng OK
-- 3. "Báo cáo doanh thu theo tháng"     → date-based → phải scatter-gather anyway

-- ✅ Chọn: user_id (vì query #1 là hot path nhất)
```

**Tiêu chí 4: Tránh monotonically increasing key trong Hash sharding**

```sql
-- ❌ BAD với Range sharding: auto_increment order_id
-- → Tất cả write đổ vào shard cuối (hot shard)

-- ✅ GOOD: Dùng UUID v4 hoặc Snowflake ID
-- Snowflake ID: 64-bit = timestamp(41) + datacenter(5) + worker(5) + sequence(12)
-- Đủ ngẫu nhiên để phân bố đều với Hash sharding
```

### 5.3 ShopX — Phân tích chọn Shard Key cho bảng Orders

```mermaid
flowchart TD
    Q[Câu hỏi: Chọn shard key nào cho bảng orders?]

    Q --> C1{user_id?}
    Q --> C2{order_id?}
    Q --> C3{created_at?}
    Q --> C4{merchant_id?}

    C1 -->|Pro| P1["✅ Co-locate orders của 1 user<br/>✅ Query order history fast<br/>✅ High cardinality"]
    C1 -->|Con| N1["❌ Cross-user reports vẫn scatter-gather<br/>❌ Whale user có nhiều orders"]

    C2 -->|Pro| P2["✅ Rất high cardinality<br/>✅ Insert phân bố đều (nếu UUID)"]
    C2 -->|Con| N2["❌ Query 'all orders of user' → scatter-gather<br/>❌ Auto-increment → hot shard"]

    C3 -->|Pro| P3["✅ Range query theo time tốt"]
    C3 -->|Con| N3["❌ HOT SHARD: shard tháng hiện tại nhận 100% writes<br/>❌ Low cardinality về shard"]

    C4 -->|Pro| P4["✅ Tốt cho B2B: isolate data từng merchant"]
    C4 -->|Con| N4["❌ Uneven: big merchant vs small merchant<br/>❌ ShopX là B2C, merchant_id ít relevant"]

    P1 --> WINNER["🏆 Chọn: user_id\nHợp với hot path: order history per user\nCo-location: user data + order data cùng shard"]

    style WINNER fill:#27ae60,color:#fff
```

### 5.4 Compound Shard Key

Đôi khi cần kết hợp nhiều field:

```sql
-- Multi-tenant SaaS: (tenant_id, user_id) — tenant_id trước để isolate tenants
-- E-commerce B2B: (merchant_id, order_id)
-- Messaging: (conversation_id, message_id) — messages của 1 conversation luôn cùng shard

-- ShopX: dùng user_id đơn giản đủ tốt cho B2C
-- Nhưng nếu mở rộng sang B2B marketplace: (merchant_id, order_id) sẽ tốt hơn
```

### 5.5 Vấn đề Hot Shard và cách giải quyết

```mermaid
graph LR
    subgraph "Hot Shard Problem"
        U1[Seller VinFast<br/>1M orders/day] --> S0[(Shard 0<br/>🔥 HOT)]
        U2[User thường<br/>5 orders/day] --> S1[(Shard 1<br/>Normal)]
        U3[User thường<br/>3 orders/day] --> S1
    end

    subgraph "Solutions"
        SOL1["1. Sub-sharding VinFast:<br/>VinFast orders → multiple sub-shards"]
        SOL2["2. Separate dedicated shard<br/>cho big accounts (whales)"]
        SOL3["3. Rate limiting + Queue<br/>buffer trước DB"]
    end
```

## 6. Sharding trong SQL vs NoSQL

### 6.1 Tổng quan sự khác biệt

Đây là một trong những điểm quan trọng nhất: SQL và NoSQL có triết lý sharding **căn bản khác nhau**.

```mermaid
graph LR
    subgraph "SQL Sharding - Manual & Complex"
        SQL_APP[Application / ORM] --> SQL_PROXY[Vitess / ProxySQL<br/>Manual routing]
        SQL_PROXY --> SQL_S0[(MySQL Shard 0)]
        SQL_PROXY --> SQL_S1[(MySQL Shard 1)]
        SQL_PROXY --> SQL_S2[(MySQL Shard 2)]
        SQL_NOTES["⚠️ Cross-shard JOIN = application join\n⚠️ Distributed transactions = 2PC\n⚠️ Schema change = coordinate across shards"]
    end

    subgraph "NoSQL Sharding - Native & Transparent"
        NOSQL_APP[Application] --> NOSQL_DRIVER[Driver / SDK]
        NOSQL_DRIVER --> NOSQL_CLUSTER[MongoDB Cluster / Cassandra Ring]
        NOSQL_CLUSTER --> NOSQL_S0[(Node 0)]
        NOSQL_CLUSTER --> NOSQL_S1[(Node 1)]
        NOSQL_CLUSTER --> NOSQL_S2[(Node 2)]
        NOSQL_NOTES["✅ Sharding là first-class feature\n✅ Driver tự route\n✅ Auto rebalancing (thường)"]
    end
```

### 6.2 Sharding trong SQL (MySQL, PostgreSQL)

SQL databases không được thiết kế với sharding trong mind, nên phải dùng thêm tooling.

#### 6.2.1 Vitess (MySQL Sharding — YouTube/YouTube)

Vitess là middleware layer cho MySQL sharding, được Google/YouTube phát triển và open-source.

```mermaid
graph TD
    APP[Application<br/>MySQL Protocol] --> VTG[VTGate<br/>Query Router<br/>Stateless, scale horizontally]
    VTG --> VTS0[VTTablet + MySQL<br/>Shard 0]
    VTG --> VTS1[VTTablet + MySQL<br/>Shard 1]
    VTG --> VTS2[VTTablet + MySQL<br/>Shard 2]
    ETCD[(etcd / Topology<br/>Shard Map)] --> VTG
    VTS0 --> R0[(Replica)]
    VTS1 --> R1[(Replica)]
    VTS2 --> R2[(Replica)]
```

**Vitess features:**

- Application kết nối qua MySQL protocol bình thường (transparent)
- VTGate parse SQL, xác định shard từ WHERE clause
- Cross-shard query được scatter và merge tại VTGate
- Online schema change: `vtctlclient ApplySchema` không lock table
- **Dùng bởi:** YouTube, GitHub, Slack, Square

**ShopX với Vitess:**

```sql
-- Vitess Vschema definition
{
  "tables": {
    "orders": {
      "column_vindexes": [{
        "column": "user_id",
        "name": "hash"  -- hash vindex
      }]
    },
    "order_items": {
      "column_vindexes": [{
        "column": "user_id",  -- same shard key → co-located với orders
        "name": "hash"
      }]
    }
  }
}

-- Query này Vitess tự route đến đúng shard:
SELECT o.*, oi.*
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
WHERE o.user_id = 12345;  -- ✅ Single shard, fast

-- Query này Vitess scatter-gather:
SELECT COUNT(*), SUM(total_amount)
FROM orders
WHERE created_at >= '2025-01-01';  -- ❌ All shards
```

#### 6.2.2 Citus (PostgreSQL Sharding)

Citus là PostgreSQL extension biến PostgreSQL cluster thành distributed database.

```mermaid
graph TD
    APP[Application<br/>PostgreSQL Protocol] --> COORD[Coordinator Node<br/>Citus Coordinator]
    COORD --> W0[Worker Node 0<br/>Shards 1-4]
    COORD --> W1[Worker Node 1<br/>Shards 5-8]
    COORD --> W2[Worker Node 2<br/>Shards 9-12]
```

```sql
-- Citus: Khai báo distributed table
SELECT create_distributed_table('orders', 'user_id');
SELECT create_distributed_table('order_items', 'user_id');  -- Co-locate với orders

-- Citus tự tạo 32 shards mặc định, phân bố trên các worker nodes
-- Query bình thường, Citus tự route:
SELECT * FROM orders WHERE user_id = 12345;  -- Single shard
SELECT AVG(total_amount) FROM orders;         -- Parallel query trên tất cả shards
```

#### 6.2.3 Manual Application-level Sharding (PostgreSQL/MySQL)

Cách truyền thống mà nhiều công ty dùng:

```python
# ShopX — Manual sharding với SQLAlchemy
SHARD_COUNT = 16
SHARD_CONFIGS = {
    0: "postgresql://user:pass@db-shard-0.shopx.internal/shopx",
    1: "postgresql://user:pass@db-shard-1.shopx.internal/shopx",
    # ...
    15: "postgresql://user:pass@db-shard-15.shopx.internal/shopx",
}

class ShardRouter:
    def __init__(self):
        self.engines = {
            shard_id: create_engine(dsn)
            for shard_id, dsn in SHARD_CONFIGS.items()
        }

    def get_shard(self, user_id: int) -> int:
        return int(hashlib.md5(str(user_id).encode()).hexdigest(), 16) % SHARD_COUNT

    def get_session(self, user_id: int) -> Session:
        shard_id = self.get_shard(user_id)
        return sessionmaker(bind=self.engines[shard_id])()

# Usage
router = ShardRouter()
with router.get_session(user_id=12345) as session:
    orders = session.query(Order).filter(Order.user_id == 12345).all()
```

#### 6.2.4 Challenges đặc thù với SQL Sharding

**Cross-shard JOINs:**

```sql
-- ❌ KHÔNG THỂ làm trực tiếp trong sharded MySQL:
SELECT u.name, o.total_amount
FROM users u                -- có thể ở shard khác
JOIN orders o ON u.id = o.user_id
WHERE u.country = 'VN';

-- ✅ Giải pháp: Application-side join
-- Bước 1: Query users shard (hoặc tất cả users shards)
user_ids = db.query("SELECT id FROM users WHERE country='VN'")
-- Bước 2: Fan-out query orders
orders = []
for shard_id, uid_group in group_by_shard(user_ids):
    orders += shard_sessions[shard_id].query("SELECT * FROM orders WHERE user_id IN (%s)", uid_group)
```

**Distributed Transactions:**

```mermaid
sequenceDiagram
    participant APP as Application
    participant S0 as Shard 0 (user wallet)
    participant S1 as Shard 1 (order)

    APP->>S0: BEGIN
    APP->>S1: BEGIN
    APP->>S0: UPDATE wallet SET balance = balance - 100 WHERE user_id=1
    APP->>S1: INSERT INTO orders (user_id, amount) VALUES (1, 100)

    Note over APP,S1: ⚠️ Nếu S1 fail sau khi S0 commit → Inconsistency!

    APP->>S0: COMMIT  -- ✅
    APP->>S1: COMMIT  -- ❌ CRASH HERE → S0 đã commit, S1 chưa
```

**Giải pháp cho Distributed Transactions:**

```mermaid
flowchart LR
    A[Approach 1:<br/>Saga Pattern<br/>Eventual Consistency]
    B[Approach 2:<br/>2-Phase Commit 2PC<br/>Strong Consistency<br/>High Latency]
    C[Approach 3:<br/>Outbox Pattern<br/>+ Message Queue]
    D[Approach 4:<br/>Design để tránh<br/>Cross-shard TX<br/>Co-location!]

    D -->|"Best Practice"| STAR["⭐ Preferred"]
    style STAR fill:#f39c12,color:#fff
```

### 6.3 Sharding trong NoSQL

NoSQL databases được thiết kế với horizontal scaling trong mind từ đầu. Sharding là **first-class feature**.

#### 6.3.1 MongoDB Sharding

```mermaid
graph TD
    APP[Application] --> MR[mongos<br/>Query Router]
    MR --> CS[(Config Servers<br/>3-node ReplicaSet<br/>Shard Map)]
    CS --> MR
    MR --> RS0[ReplicaSet 0<br/>Primary + 2 Secondary<br/>Chunk: user_id 1-1M]
    MR --> RS1[ReplicaSet 1<br/>Primary + 2 Secondary<br/>Chunk: user_id 1M-5M]
    MR --> RS2[ReplicaSet 2<br/>Primary + 2 Secondary<br/>Chunk: user_id 5M+]
```

**MongoDB Sharding concepts:**

- **Chunk:** Unit of data được move giữa shards (default 128MB)
- **Balancer:** Background process tự động move chunks khi shard mất cân bằng
- **mongos:** Stateless router, có thể deploy nhiều instance

```javascript
// ShopX với MongoDB sharding
// Enable sharding on database
sh.enableSharding("shopx");

// Shard collection by user_id (hashed)
sh.shardCollection("shopx.orders", { user_id: "hashed" });

// Hoặc range-based:
sh.shardCollection("shopx.orders", { user_id: 1 });

// Compound shard key (MongoDB 4.4+):
sh.shardCollection("shopx.orders", { user_id: 1, _id: 1 });

// Query bình thường — mongos tự route:
db.orders.find({ user_id: 12345 }); // Targeted query → 1 shard
db.orders.find({ status: "pending" }); // Broadcast → all shards
```

**MongoDB Jumbo Chunks Problem:**

```
Jumbo chunk: Chunk quá lớn, không thể split (do tất cả docs trong chunk có cùng shard key value)
→ Balancer không thể move → hot shard

Ví dụ: user_id=1 (bot/test user) có 10 triệu orders
→ Toàn bộ nằm trong 1 chunk không split được
→ 1 shard bị hot
```

#### 6.3.2 Apache Cassandra Sharding

Cassandra dùng consistent hashing với virtual nodes, sharding hoàn toàn tự động.

```mermaid
graph TD
    subgraph "Cassandra Ring - 3 nodes, RF=3"
        N1["Node 1<br/>Token: 0-42"]
        N2["Node 2<br/>Token: 43-84"]
        N3["Node 3<br/>Token: 85-127"]
        N1 --> N2 --> N3 --> N1
    end

    subgraph "Write Path - user_id=12345"
        C[Cassandra Driver] -->|"Partition Key: user_id<br/>Murmur3(12345) = 45<br/>→ Node 2 is coordinator"| N2
        N2 -->|"Replicate"| N3
        N2 -->|"Replicate"| N1
        N2 -->|"ACK quorum (2/3)"| C
    end
```

**Cassandra data model cho ShopX:**

```sql
-- Cassandra Schema: Thiết kế theo query pattern, không theo relational
-- Query: "Lấy orders của user X, sorted by date DESC"
CREATE TABLE orders_by_user (
    user_id     BIGINT,
    created_at  TIMESTAMP,
    order_id    UUID,
    total       DECIMAL,
    status      TEXT,
    PRIMARY KEY ((user_id), created_at, order_id)  -- user_id là partition key
) WITH CLUSTERING ORDER BY (created_at DESC);

-- Partition key = shard key trong Cassandra
-- Mọi query PHẢI có partition key để targeted (không full scan)
SELECT * FROM orders_by_user WHERE user_id = 12345;  -- ✅ Targeted
SELECT * FROM orders_by_user WHERE status = 'pending'; -- ❌ ALLOW FILTERING, không khuyến khích
```

**Cassandra Partition Hotspot:**

```
❌ HOT PARTITION: 1 partition key nhận quá nhiều writes
Ví dụ: ShopX dùng user_id=0 cho guest users → millions of orders/day → hot!

✅ Fix: Composite partition key với bucket
PRIMARY KEY ((user_id, bucket), created_at)
-- bucket = order_id % 10 → spread 1 user's orders across 10 partitions
```

#### 6.3.3 Amazon DynamoDB Sharding

DynamoDB hoàn toàn managed, sharding ẩn phía sau nhưng cần hiểu để tránh pitfalls.

```mermaid
graph LR
    APP[Application] --> DDB[DynamoDB<br/>Fully Managed]
    DDB --> P0[Partition 0<br/>user_id: A-G]
    DDB --> P1[Partition 1<br/>user_id: H-N]
    DDB --> P2[Partition 2<br/>user_id: O-Z]

    subgraph "Auto Scaling"
        P0 -->|"Hot Partition"| SPLIT[Auto-split partition]
    end
```

```python
# ShopX với DynamoDB
# Table: shopx-orders
# Partition Key: user_id (String)
# Sort Key: created_at#order_id (String — composite, for range query)

# Write
dynamodb.put_item(
    TableName='shopx-orders',
    Item={
        'user_id': {'S': '12345'},
        'created_at_order_id': {'S': '2025-01-15T10:30:00#ord_abc123'},
        'total_amount': {'N': '299000'},
        'status': {'S': 'completed'}
    }
)

# Query orders of user, sorted by date (all in 1 partition → fast)
dynamodb.query(
    TableName='shopx-orders',
    KeyConditionExpression='user_id = :uid AND begins_with(created_at_order_id, :date)',
    ExpressionAttributeValues={
        ':uid': {'S': '12345'},
        ':date': {'S': '2025-01'}  # Orders in January 2025
    }
)
```

**DynamoDB Hot Partition Problem:**

```
DynamoDB: 1 partition = 1000 WCU, 3000 RCU
→ Nếu 1 user có quá nhiều requests/sec → throttling

Giải pháp: Write Sharding với random suffix
user_id = "12345_" + random.randint(0, 9)  → spread across 10 partitions
→ Đọc phải query 10 partitions rồi merge → trade-off
```

### 6.4 Bảng so sánh SQL vs NoSQL Sharding

| Khía cạnh              | SQL (MySQL/PG)           | MongoDB             | Cassandra                | DynamoDB                  |
| ---------------------- | ------------------------ | ------------------- | ------------------------ | ------------------------- |
| **Sharding nature**    | Manual / Middleware      | Native, semi-auto   | Native, auto             | Fully auto (managed)      |
| **Setup complexity**   | ★★★★★ Cao                | ★★★ Trung bình      | ★★★ Trung bình           | ★ Thấp (managed)          |
| **Shard key change**   | Rất khó, migration lớn   | Khó, cần migration  | Rất khó                  | Khó                       |
| **Cross-shard JOIN**   | Application-side join    | `$lookup` (limited) | Không hỗ trợ             | Không hỗ trợ              |
| **Transactions**       | Distributed TX (2PC)     | Multi-doc TX (4.0+) | Lightweight TX (LWT)     | Transactional (limited)   |
| **Rebalancing**        | Manual / semi-auto       | Auto (balancer)     | Auto (vnodes)            | Fully auto                |
| **Schema flexibility** | Rigid, migrate cẩn thận  | Flexible            | Rigid (per query)        | Flexible                  |
| **Consistency**        | Strong (ACID)            | Configurable        | Eventual (tuneable)      | Eventual / Strong         |
| **Best for**           | Complex queries, reports | General purpose     | Write-heavy, time-series | Serverless, variable load |

## 7. Cross-Shard Operations

### 7.1 Scatter-Gather Pattern

```mermaid
sequenceDiagram
    participant APP as Application
    participant SR as Shard Router
    participant S0 as Shard 0
    participant S1 as Shard 1
    participant S2 as Shard 2

    APP->>SR: SELECT SUM(amount) FROM orders WHERE date > '2025-01'
    SR->>S0: Fan-out query
    SR->>S1: Fan-out query (parallel)
    SR->>S2: Fan-out query (parallel)
    S0-->>SR: partial_sum = 1,000,000
    S1-->>SR: partial_sum = 2,500,000
    S2-->>SR: partial_sum = 1,800,000
    SR->>SR: Merge & aggregate: total = 5,300,000
    SR-->>APP: 5,300,000
```

**Vấn đề với Scatter-Gather:**

- Latency = max(latency of slowest shard) — 1 shard chậm ảnh hưởng tất cả
- Load tăng N lần (N = số shards)
- **Tail latency** cao hơn nhiều so với single-shard query

**ShopX giải pháp cho reporting queries (cross-shard):**

```mermaid
graph LR
    OLTP[OLTP Shards<br/>4x MySQL] -->|"CDC / Binlog<br/>Debezium"| KAFKA[Kafka]
    KAFKA -->|"Stream"| ES[Elasticsearch<br/>hoặc ClickHouse<br/>Analytics DB]
    ANALYTICS_APP[Admin Dashboard] --> ES
    USER_APP[User App] --> OLTP

    style OLTP fill:#3498db,color:#fff
    style ES fill:#e67e22,color:#fff
```

> **Best Practice:** Không dùng OLTP shards cho analytics. Dùng CQRS: write đến sharded OLTP, sync qua CDC sang analytics store (Elasticsearch, ClickHouse, BigQuery).

### 7.2 Global Secondary Index (GSI) Problem

```sql
-- ShopX: User muốn tìm order theo email (không phải user_id)
-- Vấn đề: email không phải shard key → không biết order nằm ở shard nào

-- Option 1: Scatter-gather (query tất cả shards)
for shard in all_shards:
    results += shard.query("SELECT * FROM orders WHERE customer_email = 'a@b.com'")

-- Option 2: Separate Index Table (Global Index)
-- Có 1 bảng riêng (không sharded hoặc sharded by email):
-- email_to_user_id: { email → user_id }
-- Lookup user_id từ email → dùng user_id để route đến đúng shard

-- Option 3: Denormalize email vào lookup store (Redis)
-- Redis: "email:a@b.com" → "user_id:12345"
```

### 7.3 Distributed Aggregation

```python
# ShopX: Tính total revenue tháng này (cross-shard)
import asyncio

async def get_monthly_revenue(month: str) -> float:
    """Fan-out query với asyncio"""
    tasks = [
        query_shard(shard_id, f"SELECT SUM(total_amount) FROM orders WHERE DATE_FORMAT(created_at, '%Y-%m') = '{month}'")
        for shard_id in range(NUM_SHARDS)
    ]
    results = await asyncio.gather(*tasks)  # Parallel!
    return sum(r['sum'] for r in results if r['sum'])

# Tốt hơn: Pre-aggregate vào summary table
# Dùng event-driven: mỗi khi order completed → publish event → aggregate service cập nhật Redis/PostgreSQL summary
```

## 8. Rebalancing & Resharding

### 8.1 Vấn đề khi cần thêm Shard

```mermaid
graph TD
    subgraph "Trước: 4 shards, hash % 4"
        S0_OLD[(Shard 0<br/>user_id % 4 = 0)]
        S1_OLD[(Shard 1<br/>user_id % 4 = 1)]
        S2_OLD[(Shard 2<br/>user_id % 4 = 2)]
        S3_OLD[(Shard 3<br/>user_id % 4 = 3)]
    end

    subgraph "Sau: 5 shards, hash % 5 ❌"
        S0_NEW[(Shard 0)]
        S1_NEW[(Shard 1)]
        S2_NEW[(Shard 2)]
        S3_NEW[(Shard 3)]
        S4_NEW[(Shard 4 - New)]
        WARNING["⚠️ ~80% data cần di chuyển sang shard mới!\nuser_id=4 → cũ: shard 0, mới: shard 4"]
    end
```

**Simple modulo (% N) → Thêm 1 shard = di chuyển (N-1)/N data. Thảm họa!**

### 8.2 Consistent Hashing — Giải pháp resharding

```mermaid
graph TD
    subgraph "Consistent Hash Ring - Before (4 nodes)"
        direction LR
        A1["Node A: 0-25%"] --- B1["Node B: 25-50%"]
        B1 --- C1["Node C: 50-75%"]
        C1 --- D1["Node D: 75-100%"]
        D1 --- A1
    end

    subgraph "Add Node E - After (5 nodes)"
        direction LR
        A2["Node A: 0-20%"] --- B2["Node B: 20-40%"]
        B2 --- E2["Node E: 40-60% ← NEW"]
        E2 --- C2["Node C: 60-80%"]
        C2 --- D2["Node D: 80-100%"]
        D2 --- A2
        MOVED["Chỉ 20% data cần di chuyển:\nB→E portion và C→E portion"]
    end
```

### 8.3 Double-Write Strategy — Zero-downtime Resharding

```mermaid
sequenceDiagram
    participant APP as Application
    participant OLD as Old Shards (4)
    participant NEW as New Shards (8)
    participant BG as Background Migration

    Note over APP,NEW: Phase 1: Dual Write (2-4 tuần)
    APP->>OLD: Write (primary)
    APP->>NEW: Write (async secondary)
    BG->>OLD: Read old data
    BG->>NEW: Backfill to new shards

    Note over APP,NEW: Phase 2: Verify Consistency
    BG->>BG: Compare checksums OLD vs NEW

    Note over APP,NEW: Phase 3: Cutover
    APP->>NEW: Write (primary)
    APP->>OLD: Write (async, shadow mode)

    Note over APP,NEW: Phase 4: Decommission OLD
    APP->>NEW: Write only
    APP->>OLD: ✅ Stop writing
```

**ShopX Resharding Plan (từ 4 lên 8 shards):**

```python
# Phase 1: Dual write
class DualWriteOrderRepository:
    def create_order(self, order: Order) -> Order:
        # 1. Write to old shards (primary)
        old_shard = self.old_router.get_session(order.user_id)
        created = old_shard.add(order)

        # 2. Async write to new shards
        asyncio.create_task(
            self.new_router.get_session(order.user_id).add(order)
        )

        return created

# Background migration
async def migrate_historical_orders():
    """
    Migrate 1 tỷ existing orders sang 8 shards mới
    Rate limit: 10k records/second để không ảnh hưởng production
    """
    batch_size = 1000
    rate_limiter = RateLimiter(max_rate=10_000)  # 10k/sec

    for old_shard_id in range(4):
        cursor = None
        while True:
            batch = await old_shards[old_shard_id].fetch_batch(
                cursor=cursor, limit=batch_size
            )
            if not batch:
                break

            # Fan-out to new shards
            by_new_shard = group_by_shard(batch, num_shards=8)
            await asyncio.gather(*[
                new_shards[new_id].bulk_insert(records)
                for new_id, records in by_new_shard.items()
            ])

            await rate_limiter.acquire(len(batch))
            cursor = batch[-1].id
```

### 8.4 Shard Splitting (MongoDB approach)

```javascript
// MongoDB: Manual split chunk khi có hot shard
sh.splitAt("shopx.orders", { user_id: NumberLong(5000000) });

// Move chunk sang shard khác
sh.moveChunk("shopx.orders", { user_id: NumberLong(0) }, "shard2");

// Kiểm tra chunk distribution
sh.status();
db.adminCommand({ listShards: 1 });
```

## 9. Case Studies: Netflix, Amazon, Uber, Discord

### 9.1 Amazon — DynamoDB và Sharding

Amazon xây dựng DynamoDB sau khi nhận ra rằng 70% operations trên hệ thống quan hệ của họ là single-table queries không cần JOINs.

```mermaid
graph TD
    subgraph "Amazon Shopping Cart — Dynamo Inspired"
        USER[User Request] --> CLB[CloudFront / ALB]
        CLB --> CART[Cart Service]
        CART --> DDB[DynamoDB<br/>cart_items table<br/>PK: user_id<br/>SK: product_id]
        DDB --> NODE1[Storage Node 1<br/>Shard: user A-M]
        DDB --> NODE2[Storage Node 2<br/>Shard: user N-Z]
        NODE1 -->|"Sync replica"| REP1[Replica]
        NODE2 -->|"Sync replica"| REP2[Replica]
    end

    subgraph "Key Design Insights"
        I1["• Sharding by user_id → cart data co-located"]
        I2["• Eventual consistency acceptable for cart"]
        I3["• No SQL JOINs needed for cart operations"]
        I4["• Auto-sharding: Amazon team không quản lý shard"]
    end
```

**Amazon Learnings:**

- **Consistent Hashing** với virtual nodes để auto-rebalancing
- **Sloppy Quorum:** Write đến bất kỳ N nodes healthy, không cần đúng responsible nodes → higher availability
- **Vector Clocks** cho conflict resolution khi nodes merge

### 9.2 Netflix — Cassandra và Viewing History

Netflix lưu viewing history của 200M+ subscribers trong Cassandra.

```mermaid
graph LR
    subgraph "Netflix Viewing History Architecture"
        PLAY[Play Event<br/>user watches episode] --> GW[API Gateway]
        GW --> VS[Viewing History Service]
        VS --> CASS[Cassandra Cluster<br/>3 DCs: US-East, EU, Asia]

        CASS --> DC1[US-East DC<br/>72 nodes]
        CASS --> DC2[EU DC<br/>72 nodes]
        CASS --> DC3[Asia DC<br/>36 nodes]
    end

    subgraph "Cassandra Schema"
        SCHEMA["CREATE TABLE viewing_history (
  account_id UUID,   ← Partition Key (shard key)
  show_id    UUID,
  episode_id UUID,
  watched_at TIMESTAMP,
  progress   INT,
  PRIMARY KEY ((account_id), watched_at DESC)
)"]
    end
```

**Netflix Key Decisions:**

- **Partition key = account_id:** Tất cả viewing history của 1 user trong 1 partition → fast query
- **Replication Factor = 3** (mỗi DC): Mất 1-2 node không ảnh hưởtic
- **Write to LOCAL_QUORUM:** Đảm bảo write đến majority của local DC trước khi ACK
- **Tunable consistency:** Reading playback state → `LOCAL_ONE` (fast); Billing → `QUORUM`
- **Cassandra không có JOINs:** Netflix denormalize hoàn toàn — mỗi query pattern có 1 table riêng

**Scale:**

- 1 tỷ+ write operations/ngày
- Latency < 10ms ở P99

### 9.3 Uber — Schemaless (MySQL-backed Sharding)

Uber xây dựng **Schemaless** — hệ thống sharding tự xây dựng trên MySQL.

```mermaid
graph TD
    subgraph "Uber Schemaless Architecture"
        TRIP[Trip Service] --> SL[Schemaless Client Library]
        SL --> ROUTER[Shard Router<br/>Consistent Hashing]
        ROUTER --> SHARD0[MySQL Shard 0<br/>Trip IDs: A-D]
        ROUTER --> SHARD1[MySQL Shard 1<br/>Trip IDs: E-H]
        ROUTER --> SHARD2[MySQL Shard 2<br/>Trip IDs: I-P]
        ROUTER --> SHARD3[MySQL Shard 3<br/>Trip IDs: Q-Z]
    end

    subgraph "Schemaless Data Model"
        MODEL["Table: entities
Column: row_key (VARCHAR) -- shard key = trip_uuid
Column: column_key (VARCHAR) -- type of data
Column: ref_key (BIGINT)
Column: body (LONGBLOB) -- JSON payload
Column: created_at"]
    end
```

**Uber Key Innovations:**

- **UUID-based shard key** cho trips: UUID first 4 chars → alphabetic sharding → even distribution
- **Schemaless columns:** Mỗi row key + column key = 1 entity, không cần ALTER TABLE
- **Append-only:** Không UPDATE, chỉ INSERT → simpler replication, audit trail
- **Cell indices:** Secondary indices được maintain separately để avoid cross-shard scatter

**Vấn đề gặp phải và giải pháp:**

| Vấn đề                                | Giải pháp của Uber                                                 |
| ------------------------------------- | ------------------------------------------------------------------ |
| Cross-shard trip data cho analytics   | Apache Kafka + Hadoop ETL pipeline                                 |
| Driver location updates (write-heavy) | Separate Redis Geo store, không shard MySQL cho real-time location |
| Growing beyond MySQL limits           | Migration sang custom Docstore (tiếp theo sau Schemaless)          |

### 9.4 Discord — Messages và Cassandra Sharding

Discord lưu hàng trăm tỷ messages trong Cassandra.

```mermaid
graph TD
    subgraph "Discord Message Storage"
        MSG[Message Sent] --> MSG_SVC[Message Service]
        MSG_SVC --> CASS[Cassandra Cluster]

        CASS --> S1[Shard 1: Channel A-F]
        CASS --> S2[Shard 2: Channel G-M]
        CASS --> S3[Shard 3: Channel N-Z]
    end

    subgraph "Schema Design"
        SCHEMA["CREATE TABLE messages (
  channel_id  BIGINT,   ← Partition Key
  bucket      INT,      ← Time bucket (10 days)
  message_id  BIGINT,   ← Snowflake ID, Clustering Key
  author_id   BIGINT,
  content     TEXT,
  PRIMARY KEY ((channel_id, bucket), message_id DESC)
)"]
    end

    subgraph "Why Bucket?"
        B["channel_id alone → hot partition\nfor popular channels (#general)\n\nBucket = message_id / BUCKET_SIZE\n→ Data spread across buckets\n→ No single hot partition"]
    end
```

**Discord Problems và Solutions:**

- **Hot Partition** ở channels phổ biến: Giải quyết bằng composite partition key `(channel_id, bucket)` với bucket = time window 10 ngày
- **Cassandra tombstones:** Discord cho phép xóa messages → cassandra tombstones tích tụ → performance degradation → giải quyết bằng time-to-live và compaction tuning
- **ScyllaDB Migration (2023):** Discord migrate từ Cassandra sang ScyllaDB (Cassandra-compatible, viết bằng C++) để giảm latency và operational cost — cùng sharding model nhưng engine khác

### 9.5 Shopify — Directory-based Sharding (Multi-tenant)

Shopify có hàng triệu merchants, mỗi merchant cần isolation.

```mermaid
graph TD
    subgraph "Shopify Pod Architecture"
        REQ[Merchant Request<br/>shop=mymerchandise.myshopify.com] --> LB[Load Balancer]
        LB --> LOOKUP[(Lookup Service<br/>Redis Cache<br/>shop_id → pod_id)]
        LOOKUP -->|"pod_id = 42"| APP[App Servers<br/>Pod 42]
        APP --> DB[(MySQL Pod 42<br/>1000 merchants)]
    end

    subgraph "Shard Map (MySQL backed)"
        MAP["shop_id=1234 → pod_id=42
shop_id=5678 → pod_id=7
shop_id=9999 → pod_id=42
..."]
    end
```

**Shopify Key Design:**

- **Pod = unit of deployment:** 1 pod gồm app servers + DB cluster + Redis phục vụ ~1000 merchants
- **Directory-based routing:** Lookup service cho biết merchant thuộc pod nào
- **Shop isolation:** Mỗi merchant có DB schema riêng trong pod → zero cross-tenant data leakage
- **Uneven pods:** Big merchants (Gymshark, Kylie Cosmetics) có dedicated pod riêng

## 10. Anti-Patterns

### 10.1 Anti-Pattern #1: Sharding Too Early

```mermaid
graph LR
    WRONG["❌ SAI: Shard DB ngay từ đầu\n'Chắc sau này sẽ cần thôi'\n→ Complexity tăng ngay lập tức\n→ Cross-shard joins phức tạp\n→ Khó debug, khó test\n→ Lãng phí engineering effort"]

    RIGHT["✅ ĐÚNG: Monolith DB trước\n→ Optimize queries\n→ Add read replicas\n→ Add caching\n→ Shard khi THẬT SỰ cần"]
```

> Rule of thumb: Chỉ shard khi single-server solution không còn đủ dùng, dù đã vertical scale.

### 10.2 Anti-Pattern #2: Bad Shard Key (Low Cardinality)

```sql
-- ❌ BAD: Shard key chỉ có vài giá trị
SHARD KEY = order_status  -- ('pending', 'completed', 'cancelled') = 3 giá trị
-- → Chỉ 3 shards, không scale thêm được
-- → completed status nhận 90% data → hot shard

-- ❌ BAD: Shard key = boolean
SHARD KEY = is_active  -- true/false = 2 giá trị

-- ✅ GOOD: High cardinality
SHARD KEY = user_id  -- Hàng triệu unique values
```

### 10.3 Anti-Pattern #3: Monotonically Increasing Key với Range Sharding

```sql
-- ❌ BAD: Auto-increment ID + Range sharding
-- order_id 1 → 1B: shard_0 (1-250M), shard_1 (250M-500M), ...
-- Tất cả WRITE đổ vào shard hiện tại (shard có range lớn nhất)
-- → 1 hot write shard tại mọi thời điểm

-- ✅ FIX: Dùng UUID hoặc Snowflake ID
-- UUID: Phân bố ngẫu nhiên → even distribution với hash sharding
-- Snowflake: Có timestamp nhưng có worker_id → phân bố đều hơn auto-increment
```

### 10.4 Anti-Pattern #4: Cross-Shard Transactions thường xuyên

```python
# ❌ BAD: Order tạo ra cần update cả wallet (khác shard) và inventory (khác shard)
def create_order(user_id, product_id, amount):
    user_shard = get_shard(user_id)        # Shard 2
    product_shard = get_shard(product_id)  # Shard 5 (khác!)

    # Distributed transaction: 2PC across shard 2 và shard 5
    # → Complex, high latency, risk of distributed deadlock
    with distributed_transaction():
        user_shard.execute("UPDATE wallet SET balance = balance - %s", amount)
        product_shard.execute("UPDATE inventory SET stock = stock - 1", product_id)
        # ... what if shard 5 is down?

# ✅ GOOD: Design để tránh cross-shard TX
# Co-locate wallet với user (cùng user_id shard key)
# Dùng Saga pattern cho inventory (eventual consistency)
# Inventory service: separate DB, không cần shard-consistent với orders
```

### 10.5 Anti-Pattern #5: Không chuẩn bị cho Rebalancing

```
❌ BAD: Dùng simple hash % N mà không có plan cho N thay đổi
→ Khi cần thêm shard: 80%+ data cần migrate
→ Downtime dài, risky migration

✅ GOOD: Dùng consistent hashing từ đầu
✅ GOOD: Dùng số shard là bội số của 2 (4 → 8 → 16) để split đơn giản
✅ GOOD: Document resharding plan TRƯỚC khi cần
```

### 10.6 Anti-Pattern #6: Quên Shard Key trong mọi Query

```sql
-- ❌ RẤT BAD: Quên WHERE user_id → Scatter-gather toàn bộ cluster
SELECT * FROM orders WHERE order_id = '12345';
-- order_id không phải shard key → query TẤT CẢ shards!

-- ✅ GOOD: Luôn include shard key
SELECT * FROM orders WHERE user_id = 99 AND order_id = '12345';
-- Router biết đây là shard của user_id=99 → single shard query

-- ✅ Enforce at application layer: Mọi DB repository method PHẢI nhận user_id
class OrderRepository:
    def find_by_order_id(self, user_id: int, order_id: str) -> Order:
        # user_id bắt buộc, không phải optional
        shard = self.router.get_shard(user_id)
        return shard.query(
            "SELECT * FROM orders WHERE user_id=? AND order_id=?",
            user_id, order_id
        )
```

### 10.7 Anti-Pattern #7: Không có Shard-aware Monitoring

```
❌ BAD: Monitoring chỉ nhìn aggregated metrics
"Average DB latency: 50ms" → trông ổn
→ Thực tế: Shard 0: 5ms, Shard 3: 800ms (HOT SHARD) → không thấy!

✅ GOOD: Per-shard metrics
- CPU per shard
- Query latency per shard (P50, P95, P99)
- Writes per shard per second
- Data size per shard
Alert: "Shard 3 latency P99 > 500ms" → immediate investigation
```

## 11. Best Practices tổng hợp

### 11.1 Design Phase

```mermaid
graph TD
    BP1["1. Chọn Shard Key trước tiên\nAnalyze query patterns\nHigh cardinality, even distribution\nAligned với hot path queries"]
    BP2["2. Plan Co-location\nRelated data → cùng shard\norder + order_items → cùng user_id shard\nTránh cross-shard joins"]
    BP3["3. Start với ít shard hơn\n4 hoặc 8 shards đủ dùng lâu\nDễ manage hơn 64 shards\nCó thể split sau"]
    BP4["4. Số shard = bội số 2\n4→8→16→32\nSplit shard dễ dàng hơn\nKhông cần rehash toàn bộ"]

    BP1 --> BP2 --> BP3 --> BP4
```

### 11.2 Implementation Phase

```
✅ Shard key không bao giờ thay đổi (immutable)
   → Nếu user đổi region, không đổi shard key, chỉ di chuyển data nếu cần

✅ Mỗi shard phải có replica (ít nhất 1)
   → Shard down = mất data nếu không có replica

✅ Test resharding trước trong staging với production-like data volume

✅ Implement circuit breaker per shard
   → 1 shard slow/down không kéo down toàn bộ hệ thống

✅ Connection pooling per shard, không share pool

✅ Shard-aware query logging: log shard_id trong mọi DB query
   → Debug dễ hơn nhiều
```

### 11.3 Operational Phase

```
✅ Regular shard rebalancing check (monthly)
   → Kiểm tra data size và load per shard

✅ Capacity planning: Biết khi nào cần thêm shard TRƯỚC khi hit limits
   → Plan resharding khi shard đạt 70% capacity, không đợi 100%

✅ Backup per shard: Độc lập, không backup toàn cluster cùng lúc
   → Restore 1 shard mà không ảnh hưởng shard khác

✅ Runbook cho shard failure:
   → Failover to replica
   → Promote replica to primary
   → Alert on-call
   → RTO/RPO định nghĩa rõ

✅ Chaos testing: Kill shard và verify system vẫn phục vụ partial traffic
```

### 11.4 Quy tắc vàng

```
1. Query always includes shard key
2. Never do cross-shard transactions in hot path
3. Use eventual consistency for cross-shard aggregations
4. Co-locate data that is queried together
5. Shard key must have high cardinality and even distribution
6. Plan for N*2 shards, not N+1
7. Monitor per-shard, not just aggregate
8. Document the shard key rationale — it outlives the original engineers
```

## 12. Monitoring & Observability

### 12.1 Key Metrics cần monitor

```mermaid
graph TD
    subgraph "Shard Health Dashboard"
        M1["📊 Storage per shard\nAlert: > 70% capacity"]
        M2["📊 Write TPS per shard\nAlert: > 80% of max TPS"]
        M3["📊 Query latency P99 per shard\nAlert: > SLA threshold"]
        M4["📊 Replication lag per shard\nAlert: > 10 seconds"]
        M5["📊 Connection count per shard\nAlert: > 80% of max_connections"]
        M6["📊 Shard imbalance ratio\nAlert: max_shard_size / avg_shard_size > 1.5"]
    end
```

### 12.2 ShopX Monitoring Stack

```yaml
# Prometheus metrics (exported by application)
shopx_db_query_duration_seconds{shard_id="0", operation="select"} histogram
shopx_db_query_duration_seconds{shard_id="1", operation="insert"} histogram
shopx_db_shard_connections{shard_id="0", state="active"} gauge
shopx_db_shard_size_bytes{shard_id="2"} gauge

# Grafana Dashboard alerts
- name: "Hot Shard Alert"
  condition: |
    max(rate(shopx_db_query_duration_seconds_bucket{le="0.5"}[5m])) by (shard_id)
    / avg(rate(shopx_db_query_duration_seconds_bucket{le="0.5"}[5m]))
    > 3  # 1 shard 3x slower than average
  severity: critical
```

### 12.3 Shard Imbalance Detection

```python
# ShopX: Weekly shard balance check
def check_shard_balance():
    sizes = [get_shard_size(i) for i in range(NUM_SHARDS)]
    avg_size = sum(sizes) / len(sizes)
    max_size = max(sizes)

    imbalance_ratio = max_size / avg_size

    if imbalance_ratio > 1.5:
        alert(f"SHARD IMBALANCE: max={max_size:.2f}GB, avg={avg_size:.2f}GB, ratio={imbalance_ratio:.2f}")
        report_hottest_shard(sizes.index(max_size))

    tps_per_shard = [get_shard_tps(i) for i in range(NUM_SHARDS)]
    if max(tps_per_shard) / (sum(tps_per_shard) / len(tps_per_shard)) > 2:
        alert(f"HOT SHARD (TPS): shard {tps_per_shard.index(max(tps_per_shard))}")
```

## 13. Checklist Production

### Pre-implementation

- [ ] Đã đánh giá các giải pháp thay thế (indexing, caching, read replica, vertical scaling)?
- [ ] Đã xác định shard key dựa trên query pattern analysis?
- [ ] Shard key có high cardinality? (> 1M unique values)
- [ ] Shard key có even distribution? (không có whale values?)
- [ ] Đã plan co-location cho related tables?
- [ ] Đã chọn sharding strategy phù hợp (hash, range, consistent hash)?
- [ ] Số shard ban đầu là bội số của 2?
- [ ] Đã có resharding plan khi cần scale thêm?

### Implementation

- [ ] Mỗi shard có ít nhất 1 replica?
- [ ] Có circuit breaker per shard?
- [ ] Connection pooling per shard được cấu hình đúng?
- [ ] Mọi query đều include shard key?
- [ ] Cross-shard joins được handle ở application layer?
- [ ] Distributed transactions dùng Saga/Outbox pattern thay vì 2PC?
- [ ] Shard ID được log trong mọi DB operation?
- [ ] Unit test cho shard routing logic?
- [ ] Load test với production-like data distribution?

### Operations

- [ ] Per-shard metrics được export đến monitoring system (Prometheus/Datadog)?
- [ ] Alerts được set up cho hot shard, shard imbalance, replication lag?
- [ ] Runbook cho shard failure đã được viết và tested?
- [ ] Backup strategy per shard được xác định?
- [ ] Capacity planning: biết khi nào cần thêm shard?
- [ ] Resharding procedure đã được documented và tested trong staging?
- [ ] On-call team được training về shard-specific operations?

## Tổng kết

Sharding là một trong những kỹ thuật phức tạp nhất trong system design. Đây là những điểm cốt lõi cần nhớ:

```mermaid
mindmap
  root((Database Sharding))
    Khi nào dùng
      Sau khi đã optimize query, index, cache, replica
      Write throughput vượt giới hạn 1 node
      Dataset vượt capacity 1 server
    Shard Key
      High cardinality
      Even distribution
      Aligned với hot query path
      Immutable
    Strategies
      Hash: Even distribution, khó range query
      Range: Tốt cho range scan, hot shard risk
      Consistent Hash: Tốt cho resharding
      Directory: Linh hoạt nhất
    SQL vs NoSQL
      SQL: Manual, phức tạp hơn, cần middleware
      NoSQL: Native, auto-sharding thường có sẵn
    Pitfalls
      Too early sharding
      Bad shard key
      Cross-shard TX thường xuyên
      Không monitor per-shard
    Bài học từ Big Tech
      Amazon: DynamoDB, co-locate by user_id
      Netflix: Cassandra, account_id partition key
      Uber: Consistent hashing, UUID shard key
      Discord: Compound partition key, avoid hot partition
      Shopify: Directory-based, pod isolation
```

> **Lời khuyên cuối:** Sharding là một commitment lâu dài. Một khi đã shard, việc thay đổi shard key hoặc merge shards lại là cực kỳ tốn kém. Hãy đầu tư thời gian thiết kế đúng ngay từ đầu, bao gồm cả data model, query patterns, và growth projections cho ít nhất 3-5 năm tới.

_Tài liệu này được tổng hợp từ kinh nghiệm thực chiến và các engineering blog của Amazon, Netflix, Uber, Discord, Shopify, YouTube. Phiên bản 1.0 — 2026._
