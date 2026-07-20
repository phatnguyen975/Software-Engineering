# Quality Attributes

## Mục lục

1. [Tổng quan & Tại sao Quality Attributes quan trọng](#1-tổng-quan--tại-sao-quality-attributes-quan-trọng)
2. [Phân loại bốn loại yêu cầu](#2-phân-loại-bốn-loại-yêu-cầu)
3. [Mô hình chất lượng: ISO/IEC 25010:2023 & SEI](#3-mô-hình-chất-lượng-isoiec-250102023--sei)
4. [Các Quality Attributes quan trọng nhất](#4-các-quality-attributes-quan-trọng-nhất)
   - 4.1 [Performance (Hiệu năng)](#41-performance-hiệu-năng)
   - 4.2 [Availability (Tính sẵn sàng)](#42-availability-tính-sẵn-sàng)
   - 4.3 [Reliability (Độ tin cậy)](#43-reliability-độ-tin-cậy)
   - 4.4 [Scalability (Khả năng mở rộng)](#44-scalability-khả-năng-mở-rộng)
   - 4.5 [Security (Bảo mật)](#45-security-bảo-mật)
   - 4.6 [Modifiability (Dễ thay đổi)](#46-modifiability-dễ-thay-đổi)
   - 4.7 [Maintainability (Dễ bảo trì)](#47-maintainability-dễ-bảo-trì)
   - 4.8 [Observability (Khả năng quan sát)](#48-observability-khả-năng-quan-sát)
   - 4.9 [Testability (Khả năng kiểm thử)](#49-testability-khả-năng-kiểm-thử)
   - 4.10 [Usability (Khả năng sử dụng)](#410-usability-khả-năng-sử-dụng)
   - 4.11 [Interoperability (Khả năng tương tác)](#411-interoperability-khả-năng-tương-tác)
   - 4.12 [Safety (An toàn)](#412-safety-an-toàn)
5. [Quality Attribute Scenarios (Kịch bản chất lượng)](#5-quality-attribute-scenarios-kịch-bản-chất-lượng)
6. [Architecturally Significant Requirements (ASR)](#6-architecturally-significant-requirements-asr)
7. [Architecture Tactics (Chiến thuật kiến trúc)](#7-architecture-tactics-chiến-thuật-kiến-trúc)
8. [Trade-offs: Không có kiến trúc tốt tuyệt đối](#8-trade-offs-không-có-kiến-trúc-tốt-tuyệt-đối)
9. [SLI / SLO / SLA: Đo lường chất lượng trong thực tế](#9-sli--slo--sla-đo-lường-chất-lượng-trong-thực-tế)
10. [ATAM: Phương pháp đánh giá kiến trúc](#10-atam-phương-pháp-đánh-giá-kiến-trúc)
11. [Quality Attributes trong hệ thống AI / GenAI](#11-quality-attributes-trong-hệ-thống-ai--genai)
12. [Best Practices từ Netflix, Amazon, Google](#12-best-practices-từ-netflix-amazon-google)
13. [Checklist thực chiến cho kiến trúc sư](#13-checklist-thực-chiến-cho-kiến-trúc-sư)
14. [Tài liệu tham khảo](#14-tài-liệu-tham-khảo)

## 1. Tổng quan & Tại sao Quality Attributes quan trọng

### 1.1 Định nghĩa

> **"Whether a system will be able to exhibit its desired quality attributes is substantially determined by its architecture."** — Bass, Clements & Kazman, _Software Architecture in Practice_ (4th ed., 2022)

**Quality Attribute (Thuộc tính chất lượng)** là một thuộc tính có thể đo lường hoặc kiểm thử của hệ thống, dùng để chỉ ra hệ thống thỏa mãn nhu cầu của stakeholder tốt đến mức nào — **vượt ra ngoài chức năng cơ bản**.

- **Chức năng** trả lời câu hỏi: _"Hệ thống làm gì?"_
- **Quality Attributes** trả lời câu hỏi: _"Hệ thống làm điều đó tốt đến mức nào, trong điều kiện nào?"_

### 1.2 Tại sao chức năng đúng vẫn chưa đủ?

Một hệ thống có thể vượt qua mọi bài kiểm tra chức năng nhưng vẫn **thất bại hoàn toàn trong môi trường thực**:

| Điều kiện thực tế            | Rủi ro nếu không thiết kế chất lượng |
| ---------------------------- | ------------------------------------ |
| 100,000 người dùng đồng thời | Hệ thống chậm hoặc sập               |
| Một service dependency lỗi   | Toàn hệ thống ngưng hoạt động        |
| Hacker tấn công              | Lộ dữ liệu người dùng                |
| Nghiệp vụ thay đổi           | Mất hàng tuần sửa code lan tỏa       |
| Lỗi xảy ra                   | Không thể truy vết nguyên nhân       |

**Ví dụ thực tế — Ứng dụng bán vé concert:**

- ✅ Người dùng có thể đăng nhập
- ✅ Người dùng có thể chọn ghế
- ✅ Hệ thống hỗ trợ thanh toán
- ✅ Hệ thống gửi email xác nhận
- ❌ **Khi mở bán, 100,000 người truy cập cùng lúc → Chậm → Sập → Trừ tiền không có vé → Không thể truy vết**

### 1.3 Quality Attributes dẫn dắt kiến trúc

Nhiều quyết định kiến trúc lớn **xuất phát từ chất lượng cần đạt**, không phải từ chức năng:

| Quality Attribute         | Quyết định kiến trúc bị ảnh hưởng                            |
| ------------------------- | ------------------------------------------------------------ |
| Performance / Scalability | Caching, load balancing, sharding database, async processing |
| Availability              | Replication, failover, health check, circuit breaker         |
| Security                  | Trust boundary, auth gateway, audit log, encryption          |
| Modifiability             | Module boundary, interface design, plugin pattern            |
| Observability             | Logging pipeline, metrics, distributed tracing               |

## 2. Phân loại bốn loại yêu cầu

Kiến trúc sư cần phân biệt rõ 4 loại yêu cầu sau:

| Loại yêu cầu                | Câu hỏi chính                  | Ví dụ                                    |
| --------------------------- | ------------------------------ | ---------------------------------------- |
| **Functional Requirements** | Hệ thống làm gì?               | "Người dùng có thể thanh toán đơn hàng"  |
| **Quality Attributes**      | Làm tốt đến mức nào?           | "95% giao dịch hoàn thành dưới 2 giây"   |
| **Constraints**             | Bị giới hạn bởi điều gì?       | "Phải dùng cloud provider hiện có (AWS)" |
| **ASR**                     | Yêu cầu nào làm đổi kiến trúc? | "Xử lý 10,000 đơn hàng mỗi phút"         |

> **Lưu ý quan trọng:** Quality Attributes tên gọi đơn thuần ("phải nhanh", "phải bảo mật") **không có giá trị thiết kế**. Chúng chỉ có ý nghĩa khi được đặt trong bối cảnh cụ thể với thước đo kiểm chứng được.

## 3. Mô hình chất lượng: ISO/IEC 25010:2023 & SEI

### 3.1 ISO/IEC 25010:2023 (SQuaRE)

Chuẩn quốc tế mới nhất (2023) định nghĩa **9 đặc tính chất lượng chính** cho sản phẩm ICT:

| #   | Đặc tính                                          | Sub-characteristics chính                                                 |
| --- | ------------------------------------------------- | ------------------------------------------------------------------------- |
| 1   | **Functional Suitability**                        | Completeness, Correctness, Appropriateness                                |
| 2   | **Performance Efficiency**                        | Time behavior, Resource utilization, Capacity                             |
| 3   | **Compatibility**                                 | Coexistence, Interoperability                                             |
| 4   | **Interaction Capability** _(trước là Usability)_ | Learnability, Operability, Accessibility, User error protection           |
| 5   | **Reliability**                                   | Availability, Fault tolerance, Recoverability                             |
| 6   | **Security**                                      | Confidentiality, Integrity, Authenticity, Accountability, Non-repudiation |
| 7   | **Maintainability**                               | Modularity, Reusability, Analysability, Modifiability, Testability        |
| 8   | **Flexibility** _(trước là Portability)_          | Adaptability, Scalability, Installability                                 |
| 9   | **Safety** _(mới trong 2023)_                     | Operational constraint, Risk identification, Fail safe, Hazard warning    |

> **Thay đổi đáng chú ý trong ISO/IEC 25010:2023:**
>
> - **Safety** được thêm vào như một đặc tính chất lượng độc lập, phản ánh xu hướng phát triển của các hệ thống safety-critical và sự gia tăng các ứng dụng AI trong những lĩnh vực có yêu cầu cao về an toàn.
> - **Usability** → **Interaction Capability**, nhằm phản ánh đầy đủ hơn chất lượng tương tác giữa người dùng và hệ thống.
> - **Portability** → **Flexibility**, mở rộng khái niệm từ khả năng chuyển đổi môi trường sang khả năng thích ứng và mở rộng của hệ thống hiện đại.

### 3.2 Mô hình SEI (Bass, Clements & Kazman — 4th Edition 2022)

SEI tập trung vào 10 quality attributes chính có ảnh hưởng đến kiến trúc:

```
Performance · Scalability · Availability · Reliability
Security · Modifiability · Testability · Usability
Deployability · Energy Efficiency
```

## 4. Các Quality Attributes quan trọng nhất

### 4.1 Performance (Hiệu năng)

**Định nghĩa:** Khả năng hệ thống phản hồi trong khoảng thời gian chấp nhận được khi nhận được một lượng tải nhất định.

**Các chỉ số đo lường:**

- **Latency:** Thời gian từ khi client gửi request đến khi nhận được response từ server.
  - **p50 (Median):** 50% request có thời gian phản hồi nhanh hơn hoặc bằng giá trị này. Đây là "trải nghiệm điển hình" của người dùng.
  - **p95:** 95% request hoàn thành trong thời gian không vượt quá giá trị này, chỉ còn 5% request chậm hơn. Thường được dùng làm chỉ số SLA/SLO.
  - **p99:** 99% request hoàn thành trong thời gian này. Giúp phát hiện các request chậm bất thường (tail latency).
  - **p99.9:** 99.9% request hoàn thành trong thời gian này. Chỉ còn 0.1% request chậm hơn, thường dùng trong các hệ thống yêu cầu độ ổn định rất cao.
- **Throughput:** Số lượng request hệ thống xử lý được trong một đơn vị thời gian (req/s, TPS).
- **Response time:** Thời gian người dùng cảm nhận (bao gồm network round trip).
- **CPU / Memory utilization** dưới các mức tải

**Vì sao p99 quan trọng hơn average?**

- Nếu average latency là 200ms nhưng p99 là 5s → 1% người dùng (có thể hàng chục nghìn người) nhận trải nghiệm cực tệ.
- Tại Netflix và Amazon, SLO thường được định nghĩa theo **p99 hoặc p99.9**

**Tactics (Chiến thuật) để cải thiện Performance:**

| Tactic                      | Mô tả                                      | Ví dụ công nghệ                   |
| --------------------------- | ------------------------------------------ | --------------------------------- |
| **Caching**                 | Lưu kết quả tính toán trước để tái sử dụng | Redis, Memcached, CDN             |
| **Indexing**                | Tăng tốc truy vấn database                 | B-tree index, composite index     |
| **Asynchronous Processing** | Tách công việc nặng ra khỏi luồng chính    | Message queue (Kafka, RabbitMQ)   |
| **Connection Pooling**      | Tái sử dụng kết nối DB thay vì tạo mới     | HikariCP, PgBouncer               |
| **Load Balancing**          | Phân phối request đều cho nhiều instance   | Nginx, AWS ALB                    |
| **Data Partitioning**       | Chia nhỏ dữ liệu để truy vấn song song     | Sharding, horizontal partitioning |
| **Lazy Loading**            | Chỉ tải dữ liệu khi cần                    | Pagination, infinite scroll       |

**Quality Attribute Scenario — Performance:**

```
Nguồn: 5,000 người dùng đồng thời
Kích thích: Gửi request tìm kiếm sản phẩm
Môi trường: Giờ cao điểm 20:00–22:00, hệ thống đang chạy bình thường
Thành phần: Search Service
Phản ứng: Search Service xử lý và trả kết quả
Thước đo: 95% request ≤ 700ms | 99% request ≤ 1,500ms
```

### 4.2 Availability (Tính sẵn sàng)

**Định nghĩa:** Xác suất hệ thống đang trong trạng thái hoạt động đúng tại bất kỳ thời điểm nào khi được yêu cầu.

**Công thức:**

```
Availability = Uptime / (Uptime + Downtime) × 100%
```

**Bảng "nines" — tiêu chuẩn ngành:**

| Availability       | Downtime/năm | Downtime/tháng | Mức phù hợp             |
| ------------------ | ------------ | -------------- | ----------------------- |
| 99% (2 nines)      | ~87.6 giờ    | ~7.3 giờ       | Internal tools          |
| 99.9% (3 nines)    | ~8.76 giờ    | ~43.8 phút     | Business apps           |
| 99.95% (3.5 nines) | ~4.38 giờ    | ~21.9 phút     | E-commerce              |
| 99.99% (4 nines)   | ~52.6 phút   | ~4.4 phút      | Payment, banking        |
| 99.999% (5 nines)  | ~5.26 phút   | ~26.3 giây     | Telecom, critical infra |

> **Lưu ý:** "Availability" và "Reliability" thường bị nhầm lẫn. Availability = hệ thống có thể dùng được không. Reliability = hệ thống có trả kết quả đúng không.

**Phân biệt Availability vs. Uptime:**

- **Uptime** = server đang chạy
- **Availability** = người dùng có thực sự sử dụng được service không (bao gồm cả degraded state)

**Tactics để cải thiện Availability:**

| Tactic                                | Mô tả                                                                          |
| ------------------------------------- | ------------------------------------------------------------------------------ |
| **Active Redundancy (Hot Standby)**   | Multiple instances chạy song song, traffic được route ngay khi có instance lỗi |
| **Passive Redundancy (Warm Standby)** | Backup instance không nhận traffic, được activate khi primary lỗi              |
| **Health Check & Auto-healing**       | Kubernetes liveness/readiness probe tự restart pod lỗi                         |
| **Circuit Breaker**                   | Ngắt kết nối đến service lỗi thay vì chờ timeout                               |
| **Retry với Exponential Backoff**     | Retry có kiểm soát để tránh amplify lỗi                                        |
| **Bulkhead Pattern**                  | Cô lập resource pool cho từng service để lỗi không lan tỏa                     |
| **Graceful Degradation**              | Khi service phụ lỗi, trả về dữ liệu cached hoặc fallback thay vì lỗi hoàn toàn |

**Quality Attribute Scenario — Availability:**

```
Nguồn: Một instance của Order Service gặp lỗi phần cứng
Kích thích: Instance ngừng phản hồi
Môi trường: Giờ hoạt động bình thường (business hours)
Thành phần: Order Service (load balancer + multiple instances)
Phản ứng: Load balancer phát hiện instance unhealthy, chuyển toàn bộ traffic sang instance còn lại
Thước đo: Failover hoàn thành trong < 10 giây | Zero loss đối với đơn hàng đã xác nhận
```

### 4.3 Reliability (Độ tin cậy)

**Định nghĩa:** Khả năng hệ thống thực hiện các chức năng yêu cầu một cách ổn định và chính xác trong một khoảng thời gian xác định, trong điều kiện xác định.

**Phân biệt với Availability:**

- Availability: "Hệ thống có UP không?"
- Reliability: "Hệ thống có **đúng** không?" (kết quả nhất quán, không mất dữ liệu, không corruption)

**Các chỉ số đo lường:**

- **MTBF (Mean Time Between Failures):** Thời gian trung bình giữa các lần lỗi
- **MTTR (Mean Time To Recovery):** Thời gian trung bình để phục hồi sau lỗi
- **Error rate:** Tỷ lệ request thất bại
- **Data consistency:** Tỷ lệ dữ liệu nhất quán giữa các node

**Tactics để cải thiện Reliability:**

| Tactic                           | Mô tả                                                                             |
| -------------------------------- | --------------------------------------------------------------------------------- |
| **Idempotency**                  | Cùng một request gọi nhiều lần vẫn cho kết quả như gọi 1 lần (critical cho retry) |
| **Transactional Outbox Pattern** | Đảm bảo message được publish khi và chỉ khi DB transaction thành công             |
| **Saga Pattern**                 | Quản lý distributed transaction qua các service                                   |
| **Event Sourcing**               | Lưu trữ trạng thái như chuỗi sự kiện, dễ audit và rebuild                         |
| **Data Replication**             | Synchronous/asynchronous replica để phục hồi sau lỗi                              |
| **Write-Ahead Logging (WAL)**    | Log mọi thay đổi trước khi apply để có thể replay                                 |

### 4.4 Scalability (Khả năng mở rộng)

**Định nghĩa:** Khả năng hệ thống xử lý được lượng tải tăng thêm mà không cần thay đổi cấu trúc cơ bản.

**Hai chiều mở rộng:**

| Loại                               | Mô tả                                         | Khi nào dùng                                          |
| ---------------------------------- | --------------------------------------------- | ----------------------------------------------------- |
| **Vertical Scaling (Scale Up)**    | Tăng tài nguyên cho một node (CPU, RAM, disk) | Đơn giản, không cần refactor code; có giới hạn vật lý |
| **Horizontal Scaling (Scale Out)** | Thêm nhiều node cùng loại                     | Không có giới hạn lý thuyết; cần stateless design     |

**Các chiều scalability cần xem xét:**

- **Data scalability:** Database có thể xử lý data volume tăng không? (Sharding, read replicas)
- **Request scalability:** Số lượng concurrent request có thể tăng không? (Stateless services, load balancer)
- **Geographic scalability:** Có thể mở rộng sang region mới không? (Multi-region, CDN)
- **Team scalability:** Kiến trúc có cho phép nhiều team làm việc độc lập không? (Microservices, clear API contracts)

**Quality Attribute Scenario — Scalability:**

```
Nguồn: Black Friday event, traffic tăng đột biến 10x
Kích thích: Số lượng concurrent users tăng từ 10,000 lên 100,000 trong 30 phút
Môi trường: Hệ thống đang vận hành bình thường
Thành phần: Web tier, API tier, Database tier
Phản ứng: Auto-scaling tự động spin up thêm instances; database read replicas xử lý read load
Thước đo: Không cần thay đổi code lõi | Latency không tăng quá 20% so với baseline
```

### 4.5 Security (Bảo mật)

**Định nghĩa:** Khả năng hệ thống bảo vệ dữ liệu và chức năng khỏi truy cập trái phép, trong khi vẫn cung cấp quyền truy cập cho người được phép.

**5 thuộc tính con của Security (CIA+):**

| Thuộc tính          | Ý nghĩa                                      | Ví dụ vi phạm                 |
| ------------------- | -------------------------------------------- | ----------------------------- |
| **Confidentiality** | Dữ liệu chỉ được truy cập bởi người có quyền | Lộ thông tin thẻ tín dụng     |
| **Integrity**       | Dữ liệu không bị thay đổi trái phép          | SQL injection sửa dữ liệu     |
| **Availability**    | Hệ thống không bị ngừng bởi tấn công         | DDoS attack                   |
| **Authenticity**    | Danh tính người dùng/hệ thống được xác thực  | Phishing, credential theft    |
| **Non-repudiation** | Không thể phủ nhận đã thực hiện hành động    | Audit log không đủ bằng chứng |

**Tactics để cải thiện Security:**

| Layer                 | Tactic                                        | Công cụ/Công nghệ                    |
| --------------------- | --------------------------------------------- | ------------------------------------ |
| **Identity**          | Multi-factor Authentication, OAuth 2.0 / OIDC | Auth0, Keycloak, AWS Cognito         |
| **Authorization**     | RBAC, ABAC, Principle of Least Privilege      | OPA (Open Policy Agent)              |
| **Data**              | Encryption at rest & in transit               | TLS 1.3, AES-256, AWS KMS            |
| **Network**           | Zero-trust architecture, VPC, WAF             | AWS WAF, Cloudflare                  |
| **Audit**             | Immutable audit log, tamper-evident logging   | CloudTrail, Audit log DB             |
| **Code**              | SAST, DAST, dependency scanning               | SonarQube, Snyk, OWASP ZAP           |
| **Secret Management** | No secrets in code/config                     | HashiCorp Vault, AWS Secrets Manager |

**Khái niệm Trust Boundary:**

- Ranh giới tin cậy là điểm mà dữ liệu hoặc control đi từ vùng tin cậy sang vùng không tin cậy (hoặc ngược lại)
- Mọi dữ liệu vượt qua trust boundary đều phải được **validate, authenticate và authorize**

**Quality Attribute Scenario — Security:**

```
Nguồn: Attacker gửi crafted input qua API công khai
Kích thích: SQL injection attempt trong search parameter
Môi trường: Hệ thống đang chạy bình thường, không có incident
Thành phần: API Gateway, Input validation layer, Database
Phản ứng: Input được sanitize và reject; attempt được log vào audit trail; alert gửi tới security team
Thước đo: 100% malicious input bị block trước khi đến DB | Alert trong < 1 phút | Zero data exposure
```

### 4.6 Modifiability (Dễ thay đổi)

**Định nghĩa:** Mức độ dễ dàng khi cần thực hiện thay đổi hệ thống — thêm tính năng mới, sửa đổi tính năng cũ, hay loại bỏ tính năng — mà không tạo ra side effects không mong muốn.

**Tại sao quan trọng:**

- Nghiệp vụ thay đổi liên tục sau khi hệ thống đã chạy
- Chi phí sửa lỗi tăng theo cấp số nhân khi code base lớn và coupled

**Coupling vs. Cohesion:**

- **High cohesion:** Mọi thứ trong một module phục vụ một mục đích rõ ràng
- **Low coupling:** Các module ít phụ thuộc vào nhau nhất có thể
- **Mục tiêu:** High cohesion + Low coupling

**Tactics để cải thiện Modifiability:**

| Tactic                    | Mô tả                                                                | Ví dụ                           |
| ------------------------- | -------------------------------------------------------------------- | ------------------------------- |
| **Encapsulation**         | Che giấu implementation detail đằng sau interface                    | Private fields + public methods |
| **Dependency Inversion**  | Module cao cấp phụ thuộc abstraction, không phụ thuộc implementation | Interface/abstract class        |
| **Adapter Pattern**       | Cô lập external dependency để dễ thay thế                            | Payment gateway adapter         |
| **Plugin Architecture**   | Cho phép mở rộng mà không sửa core code                              | Extension points                |
| **Feature Flags**         | Bật/tắt tính năng không cần deploy                                   | LaunchDarkly, Unleash           |
| **Strangler Fig Pattern** | Dần thay thế legacy system mà không cần big-bang rewrite             | Migration pattern               |

**Quality Attribute Scenario — Modifiability:**

```
Nguồn: Business team yêu cầu thêm nhà cung cấp thanh toán mới (VNPay)
Kích thích: Tích hợp payment gateway mới
Môi trường: Hệ thống đang production với MoMo và ViettelPay hiện có
Thành phần: Payment Integration Module
Phản ứng: Developer tạo VNPayAdapter implement PaymentGateway interface; không sửa Order logic
Thước đo: Hoàn thành tích hợp trong ≤ 3 ngày làm việc | Không sửa code Order Service | Không làm lỗi payment providers hiện có
```

### 4.7 Maintainability (Dễ bảo trì)

**Định nghĩa:** Mức độ dễ dàng để hiểu, sửa đổi, kiểm thử và vận hành hệ thống.

**Các sub-attributes:**

- **Analysability:** Dễ tìm nguyên nhân lỗi
- **Modifiability:** Dễ thực hiện thay đổi (xem 4.6)
- **Testability:** Dễ viết và chạy test (xem 4.9)
- **Modularity:** Code được tổ chức thành module rõ ràng

**Chỉ số đo lường:**

- **Code complexity:** Cyclomatic complexity (nên ≤ 10 per function)
- **Technical debt ratio:** Ước tính thời gian để sửa các vấn đề code quality
- **Test coverage:** % code được cover bởi automated tests
- **Documentation coverage:** % public API có doc

**Tactics:**

| Tactic                          | Mô tả                                                 |
| ------------------------------- | ----------------------------------------------------- |
| **Code Review**                 | Peer review mọi PR trước khi merge                    |
| **Static Analysis**             | Tự động phát hiện code smell, security issues         |
| **Automated Testing**           | Unit test, integration test, e2e test                 |
| **Consistent Coding Standards** | Linting, formatting tự động (ESLint, Prettier, Black) |
| **Clear Naming Conventions**    | Tên hàm, biến, class phải tự giải thích               |
| **API Versioning**              | Backward-compatible API changes                       |

### 4.8 Observability (Khả năng quan sát)

**Định nghĩa:** Khả năng suy luận về trạng thái bên trong của hệ thống dựa vào các dữ liệu đầu ra của nó, đặc biệt khi hệ thống gặp sự cố mà chưa từng thấy trước đây.

> **Monitoring vs. Observability:**
>
> - **Monitoring** = Biết khi nào hệ thống lỗi (reactive)
> - **Observability** = Hiểu **tại sao** hệ thống lỗi (proactive + investigative)

**Logs (Nhật ký):**

- Bản ghi immutable, có timestamp của các sự kiện rời rạc
- **Structured logging** (JSON) quan trọng hơn plain text → dễ query và parse
- Các cấp độ: DEBUG, INFO, WARN, ERROR, FATAL

```json
{
  "timestamp": "2025-07-18T10:30:00Z",
  "level": "ERROR",
  "service": "order-service",
  "trace_id": "abc123",
  "user_id": "u_456",
  "message": "Payment failed",
  "error_code": "GATEWAY_TIMEOUT",
  "duration_ms": 5032
}
```

**Metrics (Chỉ số):**

- Dữ liệu số học theo thời gian (time-series)
- **4 golden signals** (Google SRE):
  - **Latency:** Thời gian xử lý request
  - **Traffic:** Khối lượng request
  - **Errors:** Tỷ lệ lỗi
  - **Saturation:** Mức độ sử dụng tài nguyên
- **Tools:** Prometheus + Grafana, Datadog, CloudWatch

**Traces (Vết yêu cầu):**

- Theo dõi một request qua toàn bộ các service trong hệ thống phân tán
- Một **Trace** gồm nhiều **Spans** (mỗi span = một operation trong một service)
- **Tools:** Jaeger, Zipkin, AWS X-Ray, OpenTelemetry

**Correlation là chìa khóa:**

```
Một request lỗi → xem Trace để biết span nào chậm → xem Logs của span đó → xem Metrics của service đó tại thời điểm đó
```

**Quality Attribute Scenario — Observability:**

```
Nguồn: Một giao dịch thanh toán thất bại, user complain
Kích thích: Customer support báo cáo lỗi với transaction ID
Môi trường: Production, không có incident alert nào được trigger
Thành phần: Audit log, distributed tracing, metrics dashboard
Phản ứng: Ops engineer dùng trace_id để reconstruct toàn bộ request flow trong < 5 phút
Thước đo: Root cause xác định trong ≤ 15 phút | Toàn bộ request flow có thể replay | 100% financial transaction có audit log
```

### 4.9 Testability (Khả năng kiểm thử)

**Định nghĩa:** Mức độ dễ dàng để xác minh rằng hệ thống hoạt động đúng như yêu cầu thông qua testing.

**Testing Pyramid:**

```
          /\
         /  \  E2E Tests (ít, chậm, đắt)
        /----\
       /      \  Integration Tests
      /--------\
     /          \  Unit Tests (nhiều, nhanh, rẻ)
    /------------\
```

**Tactics để cải thiện Testability:**

| Tactic                     | Mô tả                                                         |
| -------------------------- | ------------------------------------------------------------- |
| **Dependency Injection**   | Inject dependencies thay vì hard-code → dễ mock               |
| **Interface-based Design** | Code phụ thuộc interface → dễ substitute mock/stub            |
| **Single Responsibility**  | Mỗi class/function làm 1 việc → dễ test riêng lẻ              |
| **Test Containers**        | Spin up real DB/service trong test environment                |
| **Contract Testing**       | Test API contract giữa services (Pact)                        |
| **Chaos Engineering**      | Chủ động inject lỗi để test resilience (Netflix Chaos Monkey) |

### 4.10 Usability (Khả năng sử dụng)

**Định nghĩa:** Mức độ dễ dàng mà người dùng có thể học, sử dụng hệ thống để đạt mục tiêu một cách hiệu quả và hài lòng.

**Các sub-attributes:**

- **Learnability:** Người dùng mới mất bao lâu để học cách dùng?
- **Efficiency:** Người dùng thành thạo mất bao nhiêu bước để hoàn thành task?
- **Error prevention:** Hệ thống có ngăn người dùng gây lỗi không?
- **Accessibility:** Hệ thống có dùng được bởi người khuyết tật không?

**Liên quan đến architecture:**

- **API Design:** Endpoint name và request/response format có trực quan không?
- **Error messages:** Lỗi có thông báo rõ ràng, actionable không?
- **Response format:** Consistent và predictable không?

### 4.11 Interoperability (Khả năng tương tác)

**Định nghĩa:** Khả năng hệ thống trao đổi thông tin và sử dụng thông tin đó với các hệ thống khác.

**Ví dụ thực tế:**

- ERP tích hợp với CRM và Accounting system
- Payment gateway tích hợp với nhiều ngân hàng
- Healthcare system trao đổi dữ liệu theo chuẩn HL7/FHIR

**Tactics:**

| Tactic                       | Mô tả                                                |
| ---------------------------- | ---------------------------------------------------- |
| **Standard Protocols**       | REST, gRPC, GraphQL với API documentation            |
| **Standard Data Formats**    | JSON, XML, CSV, Parquet                              |
| **API Versioning**           | Đảm bảo backward compatibility                       |
| **Anti-Corruption Layer**    | Adapter để translate giữa các domain model khác nhau |
| **Event-Driven Integration** | Publish/subscribe thay vì direct API call            |

### 4.12 Safety (An toàn)

**Định nghĩa (ISO/IEC 25010:2023):** Khả năng hệ thống tránh rơi vào trạng thái gây ra tổn hại, chấn thương hoặc mất mát cho con người, tài sản hoặc môi trường.

**Quan trọng với:**

- Hệ thống y tế (medical devices, clinical decision support)
- Hệ thống giao thông (autonomous vehicles, air traffic control)
- Hệ thống tài chính (trading systems với automatic execution)
- **Hệ thống AI** (generative AI với autonomous actions)

**Sub-characteristics:**

- **Operational Constraint:** Giới hạn hoạt động trong phạm vi an toàn
- **Risk Identification:** Phát hiện rủi ro trước khi xảy ra
- **Fail Safe:** Chuyển sang trạng thái an toàn khi có lỗi
- **Hazard Warning:** Cảnh báo khi phát hiện nguy hiểm
- **Safe Integration:** An toàn khi tích hợp với các hệ thống khác

## 5. Quality Attribute Scenarios (Kịch bản chất lượng)

### 5.1 Tại sao cần Scenarios?

Tên quality attribute đơn thuần **không có giá trị thiết kế**:

- ❌ "Hệ thống phải nhanh" — không kiểm chứng được
- ❌ "Hệ thống phải bảo mật" — không thiết kế được
- ✅ "95% request tìm kiếm trả về dưới 700ms khi có 5,000 user đồng thời trong giờ cao điểm"

### 5.2 Cấu trúc 6 phần của một Quality Attribute Scenario

```
┌────────┐   ┌────────────┐   ┌─────────────┐   ┌────────────┐   ┌──────────┐   ┌──────────┐
│ NGUỒN  │ → │ KÍCH THÍCH │ → │ MÔI TRƯỜNG  │ → │ THÀNH PHẦN │ → │ PHẢN ỨNG │ → │ THƯỚC ĐO │
│ Source │   │ Stimulus   │   │ Environment │   │ Artifact   │   │ Response │   │ Measure  │
└────────┘   └────────────┘   └─────────────┘   └────────────┘   └──────────┘   └──────────┘
```

| Thành phần                   | Câu hỏi                   | Ví dụ                                            |
| ---------------------------- | ------------------------- | ------------------------------------------------ |
| **Nguồn (Source)**           | Ai/cái gì gây ra sự kiện? | Người dùng, hệ thống ngoài, scheduler, hacker    |
| **Kích thích (Stimulus)**    | Sự kiện gì xảy ra?        | HTTP request, node failure, code change          |
| **Môi trường (Environment)** | Trong điều kiện nào?      | Giờ cao điểm, đang có incident, normal operation |
| **Thành phần (Artifact)**    | Phần nào bị tác động?     | Service cụ thể, database, toàn hệ thống          |
| **Phản ứng (Response)**      | Hệ thống làm gì?          | Xử lý, failover, reject, log, alert              |
| **Thước đo (Measure)**       | Kết quả đo bằng gì?       | Latency p99, error rate, time to recover         |

### 5.3 Quy trình chuyển từ mơ hồ sang scenario

```
Mơ hồ → Có ngữ cảnh → Có phản ứng mong muốn → Có thước đo kiểm chứng được
```

| Yêu cầu mơ hồ              | Quality Attribute Scenario tốt hơn                                                                                                                            |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| "Hệ thống phải nhanh"      | "95% request tìm kiếm dưới 700ms khi có 5,000 user đồng thời trong giờ cao điểm"                                                                              |
| "Hệ thống phải bảo mật"    | "Mọi API thay đổi dữ liệu phải xác thực, phân quyền và ghi audit log với actor + timestamp + action"                                                          |
| "Hệ thống phải dễ mở rộng" | "Khi lưu lượng tăng gấp 5 lần, hệ thống auto-scale trong < 3 phút mà không cần sửa code lõi"                                                                  |
| "Chatbot phải đáng tin"    | "Khi không có nguồn tài liệu phù hợp, chatbot phải trả lời 'không đủ thông tin' thay vì bịa đặt, với tỷ lệ hallucination < 0.1% trên test set được phê duyệt" |

### 5.4 Ví dụ Scenarios thực tế

**Scenario 1 — Availability (Order Service failover):**

```
Nguồn:      Phần cứng của một EC2 instance
Kích thích: Instance bị terminate đột ngột
Môi trường: Giờ cao điểm, 50,000 active orders đang xử lý
Thành phần: Order Service (3 instances behind load balancer)
Phản ứng:   Load balancer phát hiện unhealthy instance trong < 10s;
            re-routes traffic sang 2 instances còn lại;
            Kubernetes spins up replacement instance
Thước đo:   RTO (Recovery Time Objective) < 10 giây
            RPO (Recovery Point Objective) = 0 (không mất đơn hàng đã confirm)
```

**Scenario 2 — Modifiability (Payment provider integration):**

```
Nguồn:      Business team
Kích thích: Yêu cầu tích hợp ZaloPay
Môi trường: Production đang có MoMo, VNPay; team 2 developers
Thành phần: Payment Integration Module
Phản ứng:   Developer tạo ZaloPayAdapter implements IPaymentGateway;
            viết unit tests; không cần sửa Order Service
Thước đo:   Hoàn thành trong ≤ 3 ngày | Zero regression trên providers cũ
```

**Scenario 3 — Security (Unauthorized access attempt):**

```
Nguồn:      Attacker từ external network
Kích thích: Cố gắng truy cập API /admin/users với JWT token bị hết hạn
Môi trường: Production, 02:00 AM
Thành phần: API Gateway, Auth Service
Phản ứng:   API Gateway reject request (401); ghi audit log với IP, timestamp, endpoint;
            Nếu cùng IP có > 10 failed attempts/5min → tự động block + alert security team
Thước đo:   100% unauthorized access bị block | Alert trong < 60s | Zero data exposure
```

## 6. Architecturally Significant Requirements (ASR)

### 6.1 Định nghĩa

**ASR (Architecturally Significant Requirement)** là yêu cầu đủ quan trọng để **ảnh hưởng đến cấu trúc, công nghệ hoặc chiến lược vận hành** của hệ thống. Nếu quyết định sai về ASR, chi phí sửa chữa sau này rất lớn.

> "ASRs are the requirements that, if not met, would result in an unacceptable system." — SEI

### 6.2 Bộ lọc 5 câu hỏi để nhận diện ASR

| #   | Câu hỏi                                                                  | Ý nghĩa                             |
| --- | ------------------------------------------------------------------------ | ----------------------------------- |
| 1   | Yêu cầu có chạm nhiều ranh giới hoặc dịch vụ không?                      | Cross-cutting concern → likely ASR  |
| 2   | Nếu quyết định sai, chi phí sửa sau này có lớn không?                    | Tốn kém khi refactor → likely ASR   |
| 3   | Yêu cầu có tạo đánh đổi với quality attribute khác không?                | Trade-off → likely ASR              |
| 4   | Hậu quả khi vi phạm có nghiêm trọng (tài chính, pháp lý, đạo đức) không? | High stakes → definitely ASR        |
| 5   | Có cần pattern, tactic kiến trúc hoặc hạ tầng đặc biệt không?            | Special infrastructure → likely ASR |

### 6.3 Bảng phân loại ví dụ

| Yêu cầu                         | ASR?      | Lý do                                                  |
| ------------------------------- | --------- | ------------------------------------------------------ |
| Nút "Đăng nhập" màu xanh        | ❌ Không  | Chỉ là UI, không ảnh hưởng cấu trúc                    |
| Người dùng đổi ảnh đại diện     | ⚠️ Có thể | Nếu cần CDN, virus scan → có thể là ASR                |
| Ảnh upload phải quét malware    | ✅ Có thể | Ảnh hưởng upload flow, security pipeline               |
| Xử lý 10,000 đơn hàng/phút      | ✅ Có     | Ảnh hưởng database design, queue, scaling strategy     |
| Chatbot không lộ dữ liệu nội bộ | ✅ Có     | Ảnh hưởng retrieval pipeline, auth, audit log          |
| Hệ thống đạt 99.99% uptime      | ✅ Có     | Ảnh hưởng toàn bộ infrastructure design                |
| Hỗ trợ GDPR data deletion       | ✅ Có     | Ảnh hưởng data model, event sourcing, retention policy |

### 6.4 Nguồn để tìm ASR

1. **Business goals của tổ chức** (tăng trưởng, compliance, competitive advantage)
2. **Stakeholder concerns** (người dùng, ops team, security team, legal)
3. **Quality Attribute Workshops (QAW)** — workshop có cấu trúc để elicit ASRs từ stakeholders
4. **Architectural drivers** từ constraints (budget, timeline, existing technology)
5. **Regulations & standards** (PCI-DSS, HIPAA, GDPR, SOC 2)

### 6.5 Utility Tree — Công cụ ưu tiên ASRs

Utility Tree là cấu trúc để organize và prioritize quality attribute requirements:

```
ROOT: Utility (chất lượng tổng thể)
├── Performance
│   ├── [H,H] Search response < 700ms tại 5,000 concurrent users
│   └── [H,M] Checkout < 3s tại 1,000 concurrent users
├── Availability
│   ├── [H,H] Order service failover < 10s, zero data loss
│   └── [M,L] Admin dashboard 99.9% uptime
└── Security
    ├── [H,H] PCI-DSS compliance cho payment flow
    └── [H,M] Audit log cho mọi data modification
```

**Ký hiệu [Business Impact, Technical Risk]:** H=High, M=Medium, L=Low

## 7. Architecture Tactics (Chiến thuật kiến trúc)

### 7.1 Định nghĩa

**Architecture Tactic** là một quyết định thiết kế cụ thể ảnh hưởng đến một quality attribute response. Tactics là building blocks của architectural patterns.

```
Quality Attribute Scenario → cần đạt → Tactics → kết hợp thành → Architectural Patterns
```

### 7.2 Bảng tổng hợp Tactics theo Quality Attribute

| Quality Attribute | Tactics chính                                                                                           |
| ----------------- | ------------------------------------------------------------------------------------------------------- |
| **Performance**   | Caching, indexing, async processing, connection pooling, load balancing, data compression, lazy loading |
| **Availability**  | Replication, failover, health check, circuit breaker, retry + backoff, bulkhead, graceful degradation   |
| **Reliability**   | Idempotency, transactional outbox, saga pattern, WAL, data replication, eventual consistency            |
| **Security**      | Authentication, authorization (RBAC/ABAC), encryption, audit log, input validation, secret management   |
| **Modifiability** | Encapsulation, dependency inversion, adapter, feature flags, plugin architecture, strangler fig         |
| **Scalability**   | Horizontal scaling, database sharding, read replicas, CDN, stateless service, event-driven              |
| **Observability** | Structured logging, metrics (4 golden signals), distributed tracing, alerting, dashboards               |
| **Testability**   | Dependency injection, interface-based design, test containers, contract testing, chaos engineering      |

### 7.3 Ví dụ: Tactics cho Availability

```
Goal: 99.99% availability cho Order Service

Tactic 1: Active Redundancy
  → Chạy 3 instances, load balancer phân phối traffic
  → Khi 1 instance lỗi, traffic tự động shift sang 2 instance còn lại

Tactic 2: Health Check + Auto-healing
  → Kubernetes liveness probe check /health mỗi 5 giây
  → Sau 3 lần fail → restart container
  → Readiness probe ngăn traffic đến container chưa ready

Tactic 3: Circuit Breaker
  → Nếu downstream Payment Service fail > 50% trong 60s → open circuit
  → Thay vì chờ timeout, trả về cached response hoặc fallback ngay lập tức
  → Sau 30s → half-open → test 1 request → nếu OK thì close circuit

Tactic 4: Idempotency cho Payment
  → Mỗi payment request có idempotency_key
  → Nếu request bị retry, hệ thống detect duplicate → trả lại kết quả cũ
  → Tránh double charge
```

## 8. Trade-offs: Không có kiến trúc tốt tuyệt đối

### 8.1 Nguyên tắc cơ bản

> "A good architecture is not the one that solves all problems perfectly. It is the one that explicitly acknowledges which problems it solves and which trade-offs it accepts."

Mọi quyết định kiến trúc đều là bài toán cân bằng:

| Muốn tăng         | Có thể làm khó hơn                                                    |
| ----------------- | --------------------------------------------------------------------- |
| **Security**      | Usability (thêm MFA), Latency (encryption overhead), Chi phí vận hành |
| **Availability**  | Consistency (CAP theorem), Chi phí hạ tầng (nhiều replica hơn)        |
| **Performance**   | Consistency (cache staleness), Maintainability (phức tạp hơn)         |
| **Modifiability** | Performance (abstraction overhead), Độ phức tạp ban đầu               |
| **Scalability**   | Consistency (eventual consistency), Complexity                        |
| **AI Automation** | Safety, Controllability, Accountability                               |

### 8.2 CAP Theorem — Trade-off kinh điển trong distributed systems

Trong một distributed system, bạn **chỉ có thể đồng thời đảm bảo 2 trong 3**:

```
                Consistency
                    /\
                   /  \
                  / CA \
                 /      \
                / CP  AP \
               /__________\
 Partition Tolerance — Availability
```

- **CP (Consistency + Partition Tolerance):** HBase, Zookeeper, etcd — dùng khi tính đúng đắn của data quan trọng hơn (banking, inventory)
- **AP (Availability + Partition Tolerance):** Cassandra, DynamoDB, CouchDB — dùng khi uptime quan trọng hơn (social media, shopping cart)
- **CA (Consistency + Availability):** Chỉ khả thi trong single-node system (không scale được)

### 8.3 Ví dụ Trade-off thực tế: Giao dịch ngân hàng

**Scenario:** API chuyển tiền — nhanh nhưng vẫn phải đúng và traceable

| Quyết định                        | Lợi ích                  | Đánh đổi                          |
| --------------------------------- | ------------------------ | --------------------------------- |
| Bỏ real-time fraud check          | Giảm latency 200ms       | Tăng rủi ro gian lận              |
| Kiểm tra đầy đủ synchronous       | An toàn tối đa           | Latency tăng, UX kém              |
| Async fraud check + pending state | Cân bằng tốt             | Cần xử lý rollback phức tạp hơn   |
| Audit log đầy đủ                  | Traceability, compliance | Tăng storage cost, write overhead |

**Quyết định tốt nhất (trong bối cảnh này):** Hybrid — confirm transaction synchronously với basic validation, chạy deep fraud check async, transaction có state `PENDING` → `CONFIRMED`/`REJECTED`

### 8.4 Cách document Trade-off Decision

Template **ADR (Architecture Decision Record)**:

```markdown
# ADR-001: Dùng Event-Driven Architecture cho Payment Flow

## Bối cảnh

Payment flow hiện tại (synchronous) không đạt được SLO latency p99 < 2s khi payment gateway có latency biến động 200ms–3s.

## Quyết định

Chuyển sang async payment processing với message queue (Kafka).

## Hậu quả

### Lợi ích

- API response về ngay khi nhận order (< 200ms)
- Tách biệt order creation và payment processing
- Dễ retry khi payment gateway tạm thời lỗi

### Đánh đổi (Trade-offs)

- Payment không còn synchronous → cần trạng thái PENDING rõ ràng
- Frontend phải poll hoặc dùng WebSocket để nhận kết quả
- Logic xử lý lỗi phức tạp hơn (dead letter queue, compensation)
- Cần monitoring cho queue lag

## Thay thế đã xem xét

- Tăng timeout → không giải quyết được vấn đề UX
- Giảm tiêu chí latency → không chấp nhận được từ business
```

## 9. SLI / SLO / SLA: Đo lường chất lượng trong thực tế

### 9.1 Định nghĩa và mối quan hệ

Được phổ biến bởi Google SRE (Site Reliability Engineering):

| Khái niệm                         | Định nghĩa                                   | Ví dụ                                                |
| --------------------------------- | -------------------------------------------- | ---------------------------------------------------- |
| **SLI (Service Level Indicator)** | Phép đo thực tế về một khía cạnh của service | Tỷ lệ request thành công trong 5 phút qua            |
| **SLO (Service Level Objective)** | Mục tiêu nội bộ cần đạt với SLI              | 99.9% request thành công trong rolling 30-day window |
| **SLA (Service Level Agreement)** | Cam kết pháp lý với khách hàng, kèm hậu quả  | "Nếu uptime < 99.9%, hoàn tiền 10% bill"             |

**Quan hệ:**

```
SLI (đo lường thực tế) → so sánh với → SLO (target nội bộ) → một phần của → SLA (cam kết với khách)
```

**Best practice:**

- SLO nội bộ nên **chặt hơn** SLA với khách hàng (buffer để phản ứng trước khi vi phạm SLA)
- Ví dụ: SLA = 99.9%, SLO nội bộ = 99.95% → có 0.05% buffer để investigate và fix

### 9.2 Error Budget

**Error Budget = 100% - SLO**

Ví dụ: SLO = 99.9% → Error budget = 0.1% = **43.8 phút downtime/tháng**

```
Nếu error_budget_remaining < 25%:
  → Freeze non-critical deployments
  → Tập trung vào reliability improvements
  → Tăng monitoring coverage

Nếu error_budget dư nhiều:
  → Có thể tăng velocity của feature development
  → Thực hiện riskier experiments
```

### 9.3 4 Golden Signals (Google SRE)

| Signal         | SLI ví dụ                   | Cảnh báo khi                  |
| -------------- | --------------------------- | ----------------------------- |
| **Latency**    | p99 API response time       | p99 > 1s trong 5 phút         |
| **Traffic**    | Requests per second         | Tăng đột biến > 200% baseline |
| **Errors**     | % request trả về 5xx        | Error rate > 1% trong 5 phút  |
| **Saturation** | CPU/Memory/Disk utilization | > 80% trong 10 phút           |

## 10. ATAM: Phương pháp đánh giá kiến trúc

### 10.1 Tổng quan

**ATAM (Architecture Tradeoff Analysis Method)** — được phát triển bởi SEI/Carnegie Mellon — là phương pháp đánh giá kiến trúc phần mềm có cấu trúc, tập trung vào việc:

- Xác định **risks** (rủi ro kiến trúc)
- Xác định **sensitivity points** (điểm nhạy cảm)
- Xác định **trade-off points** (điểm đánh đổi)

### 10.2 Quy trình 9 bước của ATAM

```
Phase 1: Presentation
  Bước 1: Trình bày ATAM (mục tiêu, quy trình)
  Bước 2: Trình bày Business Drivers (mục tiêu kinh doanh, constraints)
  Bước 3: Trình bày Architecture (kiến trúc hiện tại/đề xuất)

Phase 2: Investigation & Analysis
  Bước 4: Identify Architectural Approaches (patterns đang dùng)
  Bước 5: Generate Utility Tree (ASRs + priorities)
  Bước 6: Analyze Architectural Approaches (với quality attributes)

Phase 3: Testing
  Bước 7: Brainstorm & Prioritize Scenarios
  Bước 8: Analyze Architectural Approaches (với scenarios từ bước 7)

Phase 4: Reporting
  Bước 9: Present Results (risks, non-risks, sensitivity points, trade-offs)
```

### 10.3 Ba nhóm tham gia ATAM

| Nhóm                        | Vai trò                                           |
| --------------------------- | ------------------------------------------------- |
| **Evaluation Team**         | Kiến trúc sư bên ngoài dẫn dắt quá trình đánh giá |
| **Project Decision Makers** | Kiến trúc sư, tech lead, PM của dự án             |
| **Stakeholders**            | Business owner, ops team, security team, users    |

### 10.4 Output của ATAM

- **Utility Tree** với prioritized ASRs
- **Danh sách Risks:** Kiến trúc quyết định có thể gây rủi ro
- **Danh sách Non-risks:** Quyết định đã được validated
- **Sensitivity Points:** Điểm mà thay đổi nhỏ có thể ảnh hưởng lớn đến QA
- **Trade-off Points:** Nơi một QA cải thiện gây hại cho QA khác

## 11. Quality Attributes trong hệ thống AI / GenAI

### 11.1 Tại sao AI tạo ra thách thức chất lượng mới?

Hệ thống AI truyền thống và GenAI mang đến các vấn đề chất lượng mà kiến trúc phần mềm truyền thống chưa xử lý đầy đủ:

| Khác biệt với software truyền thống         | Hệ quả                                   |
| ------------------------------------------- | ---------------------------------------- |
| Logic nằm trong model, không chỉ trong code | Không thể unit test toàn bộ behavior     |
| Chất lượng phụ thuộc vào training data      | Data drift → model drift → quality drift |
| Output không deterministic                  | Khó verify "đúng" tuyệt đối              |
| Prompt & context ảnh hưởng output           | Prompt injection là vector tấn công mới  |
| Feedback loop có thể tạo bias mới           | Cần monitoring liên tục sau deployment   |

### 11.2 Quality Attributes đặc thù cho AI Systems

#### Validity (Tính hợp lệ)

- Model có phù hợp với mục đích sử dụng thực tế không?
- **Đo bằng:** Precision, Recall, F1, Accuracy trên production distribution

#### Reliability (Độ ổn định)

- Model có cho kết quả nhất quán theo thời gian không?
- **Data drift:** Distribution của production data khác training data → monitor liên tục
- **Model drift:** Performance giảm theo thời gian → periodic retraining

#### Robustness (Độ bền vững)

- Model có handle được input bất thường, adversarial, edge cases không?
- **Test với:** Out-of-distribution inputs, adversarial examples, noisy data

#### Fairness (Công bằng)

- Model có đối xử công bằng với các nhóm người dùng khác nhau không?
- **Đo bằng:** Demographic parity, equalized odds, calibration across groups
- **Quan trọng đặc biệt trong:** hiring, lending, healthcare, criminal justice

#### Safety (An toàn)

- Model có tránh gây hại không?
- Chatbot y tế không được chẩn đoán sai; AI tự lái không được đưa ra lệnh nguy hiểm
- **Cần:** Guardrails, human-in-the-loop cho quyết định rủi ro cao

#### Accountability (Trách nhiệm giải trình)

- Khi model sai, ai chịu trách nhiệm? Làm thế nào để điều tra?
- **Cần:** Audit log, explainability, model versioning, lineage tracking

### 11.3 Rủi ro chất lượng đặc thù cho GenAI / LLM Systems

| Rủi ro                          | Mô tả                                           | Giải pháp kiến trúc                                                          |
| ------------------------------- | ----------------------------------------------- | ---------------------------------------------------------------------------- |
| **Hallucination**               | Model bịa thông tin nhưng nghe thuyết phục      | RAG với grounding, source citation requirement                               |
| **Prompt Injection**            | Attacker inject instructions qua user input     | Input sanitization, system prompt isolation                                  |
| **Data Leakage**                | Model lộ thông tin nhạy cảm từ context/training | PII scrubbing, access control trước retrieval                                |
| **Unauthorized Tool Call**      | Agent thực hiện hành động vượt quyền            | Principle of least privilege cho tools, human approval cho action rủi ro cao |
| **Bias Amplification**          | Model khuếch đại bias trong data                | Bias testing, fairness monitoring, diverse test sets                         |
| **Context Window Manipulation** | Attacker nhồi nhét context để thay đổi behavior | Context length limits, source validation                                     |

> Tham khảo: **OWASP Top 10 for LLM Applications v2025** và **NIST AI 600-1 (GenAI Profile)**

### 11.4 Kiến trúc RAG có kiểm soát chất lượng

```
User Request
    │
    ▼
[1] Access Control Check        ← Security: chỉ được truy cập tài liệu mình có quyền
    │
    ▼
[2] Retrieval từ Vector DB      ← Retrieval chỉ từ approved document corpus
    │
    ▼
[3] Context Filtering           ← Chỉ đưa tài liệu hợp lệ vào context của LLM
    │
    ▼
[4] LLM Generation              ← Prompt engineering với guardrails
    │
    ▼
[5] Output Validation           ← Check: có hallucination? Có lộ PII? Trong scope?
    │
    ├──────────────────────────► [6a] Audit Log (mọi request/response)
    │
    ├──── [Nếu rủi ro cao] ────► [6b] Human Review Queue
    │
    ▼
[7] Response + Source Citation  ← Reliability: luôn cần nguồn tham chiếu
```

### 11.5 Quality Attribute Scenarios cho AI Systems

**Scenario — Safety (Chatbot y tế):**

```
Nguồn:      Bệnh nhân
Kích thích: Hỏi chatbot về triệu chứng cấp cứu (đau ngực dữ dội)
Môi trường: Chatbot tư vấn y tế ban đầu, production
Thành phần: Chatbot AI + Safety Guardrail Layer
Phản ứng:   Chatbot nhận diện emergency keyword → từ chối chẩn đoán → ngay lập tức hướng dẫn gọi cấp cứu
Thước đo:   0 trường hợp fail to escalate trên emergency test set | 100% emergency cases được redirect trong < 1s
```

**Scenario — Reliability (Internal chatbot):**

```
Nguồn:      Nhân viên
Kích thích: Hỏi về chính sách nghỉ phép
Môi trường: Chatbot nội bộ, knowledge base được cập nhật monthly
Thành phần: RAG Pipeline, LLM, Source Citation Module
Phản ứng:   Chatbot trả lời có kèm nguồn tài liệu; nếu không có nguồn phù hợp → nói rõ "không đủ thông tin, vui lòng liên hệ HR"
Thước đo:   ≥ 95% câu trả lời có nguồn hợp lệ | 0% hallucination trên policy Q&A test set
```

## 12. Best Practices từ Netflix, Amazon, Google

### 12.1 Netflix — Availability & Resilience

Netflix phục vụ hàng trăm triệu người dùng với yêu cầu availability cực cao. Tại sự kiện boxing Tyson-Paul năm 2024, họ phục vụ 65 triệu concurrent viewers.

**Key practices:**

**Chaos Engineering (Simian Army):**

- **Chaos Monkey:** Tự động terminate random EC2 instances trong production → buộc engineer thiết kế service chịu được instance failure
- **Chaos Kong:** Simulate failure của toàn bộ một AWS Availability Zone
- **Latency Monkey:** Inject artificial delays vào RESTful communication → test upstream service behavior
- **Janitor Monkey:** Tự động dọn dẹp unused resources

**Nguyên tắc:** _"Failure is inevitable. Design for it."_ — Thay vì tránh failure, Netflix chủ động inject failure trong controlled environment để đảm bảo hệ thống handle được khi failure thật xảy ra.

**Stateless Microservices:**

- Mỗi service không lưu state → có thể scale horizontally vô hạn
- State được lưu trong external stores (Cassandra, EVCache)

**Graceful Degradation:**

- Khi recommendation service lỗi → hiển thị popular content thay vì lỗi 500
- Khi user data service chậm → sử dụng cached profile

### 12.2 Google — SRE & Observability

**4 Golden Signals** (từ Google SRE Book — đã trở thành industry standard):

1. Latency
2. Traffic
3. Errors
4. Saturation

**Error Budgets:**

- Mọi service đều có SLO. Nếu error budget cạn kiệt → development phải dừng, tập trung reliability
- Tạo ra alignment giữa product team (muốn ship nhanh) và SRE team (muốn stable)

**SLO tighter than SLA:**

- Internal SLO = 99.95% (reserve 0.04% buffer)
- External SLA = 99.9%
- Khi SLO bị vi phạm → alert và fix TRƯỚC KHI vi phạm SLA

### 12.3 Amazon — Availability & Scalability

**Two-Pizza Teams + Microservices:**

- Mỗi team nhỏ (≤ 10 người) own một hoặc vài services
- Tăng **team scalability** và **deployment independence**

**DynamoDB — AP System:**

- Chọn Availability + Partition Tolerance theo CAP theorem
- Sử dụng eventual consistency cho phần lớn use cases (shopping cart, catalog)
- Strong consistency chỉ khi thực sự cần (inventory, payment)

**Amazon Reliability Principles:**

- Every dependency là một potential failure point → defensive programming
- Health checks ở mọi level (instance, service, region)
- Multi-AZ deployment là default, không phải option

## 13. Checklist thực chiến cho kiến trúc sư

### 13.1 Khi bắt đầu một dự án mới

**Elicitation:**

- [ ] Đã tổ chức Quality Attribute Workshop (QAW) với stakeholders?
- [ ] Đã convert mọi "yêu cầu mơ hồ" thành quality attribute scenarios 6 phần?
- [ ] Đã xác định và ưu tiên ASRs trong Utility Tree?
- [ ] Đã document business drivers và constraints?

**Design:**

- [ ] Mỗi ASR đã có architecture tactic hoặc pattern tương ứng?
- [ ] Đã explicitly document các trade-offs và lý do chọn?
- [ ] Đã xem xét CAP theorem cho distributed data?
- [ ] Đã thiết kế cho failure (không phải chỉ cho happy path)?

**Documentation:**

- [ ] Đã tạo ADRs (Architecture Decision Records) cho quyết định quan trọng?
- [ ] Đã define SLIs và SLOs cho mỗi service quan trọng?
- [ ] Đã document trust boundaries và security perimeter?

### 13.2 Khi review một kiến trúc

**Performance:**

- [ ] SLO cho latency đã được định nghĩa (p95, p99)?
- [ ] Caching strategy cho hot data?
- [ ] Database query có được index đúng cách?
- [ ] Async processing cho background jobs?

**Availability:**

- [ ] Không có single point of failure (SPOF)?
- [ ] Health check + auto-healing được cấu hình?
- [ ] Circuit breaker cho external dependencies?
- [ ] Graceful degradation khi dependency lỗi?

**Security:**

- [ ] Mọi external input được validate?
- [ ] Authentication + Authorization ở mọi endpoint?
- [ ] Secrets không được hardcode?
- [ ] Audit log cho sensitive operations?
- [ ] Encryption at rest và in transit?

**Observability:**

- [ ] Structured logging với correlation ID?
- [ ] Metrics cho 4 golden signals?
- [ ] Distributed tracing được setup?
- [ ] Alerting được cấu hình với đúng threshold?

**Modifiability:**

- [ ] Dependencies được abstracted qua interface?
- [ ] External services được wrap trong adapter?
- [ ] Feature flags cho risky changes?

**Testing:**

- [ ] Unit test, integration test, e2e test coverage đủ?
- [ ] Contract testing giữa services?
- [ ] Chaos engineering cho critical paths?

### 13.3 Câu hỏi vàng trước khi go-live

1. **Performance:** "Hệ thống sẽ hoạt động thế nào khi tải tăng 10x?"
2. **Availability:** "Nếu service X chết lúc 2AM, hệ thống sẽ làm gì?"
3. **Reliability:** "Nếu request bị retry 3 lần, kết quả có đúng không?"
4. **Security:** "Nếu attacker có user token bình thường, họ có thể làm gì?"
5. **Observability:** "Nếu user complain lúc 3AM, team có tìm được root cause trong 15 phút không?"
6. **Modifiability:** "Nếu business yêu cầu thay đổi X, mất bao nhiêu ngày?"

## 14. Tài liệu tham khảo

### Sách & Standards

| Tài liệu                                          | Tác giả / Tổ chức                            | Năm  |
| ------------------------------------------------- | -------------------------------------------- | ---- |
| _Software Architecture in Practice_ (4th ed.)     | Bass, Clements & Kazman — SEI/Addison-Wesley | 2022 |
| _Site Reliability Engineering_                    | Google SRE Team                              | 2016 |
| _The Site Reliability Workbook_                   | Google SRE Team                              | 2018 |
| _Building Microservices_ (2nd ed.)                | Sam Newman — O'Reilly                        | 2021 |
| _Designing Data-Intensive Applications_           | Martin Kleppmann — O'Reilly                  | 2017 |
| ISO/IEC 25010:2023 — SQuaRE Product Quality Model | ISO/IEC                                      | 2023 |
| NIST AI Risk Management Framework (AI RMF 1.0)    | NIST                                         | 2023 |
| NIST AI 600-1 — Generative AI Profile             | NIST                                         | 2024 |
| ISO/IEC 42001:2023 — AI Management System         | ISO/IEC                                      | 2023 |
| OWASP Top 10 for LLM Applications v2025           | OWASP                                        | 2025 |

### SEI Resources

- SEI — _Reasoning About Software Quality Attributes_ (2018)
- SEI — _Attribute-Driven Design: Create Software Architectures Using ASRs_
- SEI — _Quality Attribute Workshop (QAW)_
- SEI — _ATAM: Architecture Tradeoff Analysis Method_ — Kazman, Klein, Clements (2000)

### Online Resources

- Google SRE Book: https://sre.google/sre-book/
- Netflix Tech Blog: https://netflixtechblog.com/
- AWS Well-Architected Framework: https://aws.amazon.com/architecture/well-architected/
- Chaos Engineering principles: https://principlesofchaos.org/
- OpenTelemetry (Observability standards): https://opentelemetry.io/
