# File Storage Architecture

## Mục lục

1. [File Storage là gì? Tại sao không lưu file trong Database?](#1-file-storage-là-gì)
2. [So sánh: Database vs File System vs Object Storage](#2-so-sánh-database-vs-file-system-vs-object-storage)
3. [Các loại Object Storage phổ biến](#3-các-loại-object-storage-phổ-biến)
4. [Metadata của file là gì?](#4-metadata-của-file-là-gì)
5. [Cách lưu file: File System vs Object Storage](#5-cách-lưu-file-file-system-vs-object-storage)
6. [File Validation & Security — Bảo mật khi nhận file từ người dùng](#6-file-validation--security)
7. [Multipart Upload — Upload file lớn](#7-multipart-upload)
8. [Image Processing Pipeline](#8-image-processing-pipeline)
9. [Kiểm soát quyền truy cập file (Access Control)](#9-kiểm-soát-quyền-truy-cập-file)
10. [Signed URL — Khái niệm và Ứng dụng](#10-signed-url)
11. [CDN — Content Delivery Network](#11-cdn--content-delivery-network)
12. [S3 Lifecycle Policy — Quản lý vòng đời file](#12-s3-lifecycle-policy)
13. [Luồng Upload và Download File (Mermaid Diagrams)](#13-luồng-upload-và-download-file)
14. [Ví dụ Áp dụng: Hệ thống E-Commerce ShopVN](#14-ví-dụ-áp-dụng-hệ-thống-e-commerce-shopvn)
15. [Storage Cost Optimization](#15-storage-cost-optimization)
16. [Monitoring & Observability](#16-monitoring--observability)
17. [Disaster Recovery & Data Durability](#17-disaster-recovery--data-durability)
18. [Kết luận — Best Practices](#18-kết-luận--best-practices)

## 1. File Storage là gì?

### 1.1 Định nghĩa

**File Storage** (hay còn gọi là blob storage / object storage) là hệ thống được thiết kế chuyên biệt để **lưu trữ và phục vụ các đối tượng nhị phân (binary objects)** như hình ảnh, video, tài liệu PDF, audio, và các file dữ liệu thô khác.

Không giống như **Database** lưu trữ dữ liệu có cấu trúc (structured data) theo dạng bảng, hàng, cột và hỗ trợ truy vấn phức tạp (SQL/NoSQL), File Storage tập trung vào:

- **Lưu trữ hiệu quả** các đối tượng có kích thước từ vài KB đến hàng TB
- **Phục vụ (serve) file** với throughput cao và latency thấp
- **Quản lý vòng đời file** (lifecycle: tạo → truy cập → archive → xóa)
- **Phân phối nội dung** qua CDN toàn cầu

### 1.2 Vì sao không lưu file trực tiếp trong Database?

Câu trả lời ngắn: **bạn có thể lưu được, nhưng không nên.**

Về mặt kỹ thuật, bạn có thể lưu file trong Database qua kiểu dữ liệu `BLOB` (Binary Large Object) trong MySQL, `BYTEA` trong PostgreSQL, hay `varbinary(max)` trong SQL Server. Tuy nhiên:

| Vấn đề                                  | Giải thích                                                                                                                                                                                |
| --------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Memory & Buffer Pool bị lãng phí**    | Database engine (InnoDB, PostgreSQL) giữ dữ liệu trong buffer pool (RAM) để tăng tốc truy vấn. Khi BLOB chiếm buffer pool, các query bình thường (tìm kiếm, join) bị chậm vì thiếu cache. |
| **Backup trở nên khổng lồ**             | Một bản backup DB 500GB (chứa ảnh) cần thời gian dài hơn, nhiều storage hơn, và restore lâu hơn so với DB 500MB thuần dữ liệu.                                                            |
| **Replication lag tăng mạnh**           | Trong cụm MySQL master-replica, mỗi file ảnh đều phải replicate qua binary log. File 5MB ảnh sẽ làm replication lag tăng đáng kể.                                                         |
| **Không scale được**                    | Database scale theo chiều dọc (vertical) tốn kém. Object Storage scale theo chiều ngang (horizontal) gần như vô hạn và rẻ hơn hàng chục lần.                                              |
| **Database không phải HTTP server**     | Để serve ảnh cho user, bạn phải đọc BLOB từ DB, truyền qua application server, rồi trả về client. Object Storage phục vụ file trực tiếp qua HTTP/CDN, không tốn tài nguyên application.   |
| **Thiếu tính năng storage chuyên dụng** | Object Storage có CDN integration, presigned URL, multipart upload, versioning, lifecycle policy — Database không có.                                                                     |

> **Trường hợp ngoại lệ hợp lý duy nhất:** Lưu file rất nhỏ (< 256KB) như icon, config file trong DB có thể chấp nhận được trong hệ thống nhỏ, ít traffic. Ngay cả trong trường hợp này, hầu hết kiến trúc sư vẫn khuyến nghị tách ra.

## 2. So sánh: Database vs File System vs Object Storage

### 2.1 Bảng so sánh tổng quan

| Tiêu chí                   | Database (BLOB)          | Local File System           | Object Storage              |
| -------------------------- | ------------------------ | --------------------------- | --------------------------- |
| **Mục đích chính**         | Dữ liệu có cấu trúc      | File cục bộ trên server     | File quy mô lớn, phân tán   |
| **Khả năng scale**         | ❌ Khó, tốn kém          | ❌ Giới hạn ở 1 server      | ✅ Horizontal, gần vô hạn   |
| **Chi phí lưu trữ**        | ❌ Rất cao ($$$)         | ⚠️ Trung bình ($$)          | ✅ Thấp ($)                 |
| **Tốc độ đọc file**        | ❌ Chậm (qua DB engine)  | ⚠️ Nhanh nhưng 1 node       | ✅ Nhanh + CDN toàn cầu     |
| **Tính sẵn sàng (HA)**     | ⚠️ Cần cấu hình thêm     | ❌ Single point of failure  | ✅ 99.999% built-in         |
| **Tích hợp CDN**           | ❌ Không native          | ❌ Phức tạp                 | ✅ Native                   |
| **Metadata quản lý**       | ✅ SQL queries           | ❌ Filesystem metadata only | ✅ Custom metadata + tags   |
| **Versioning**             | ❌ Cần tự implement      | ❌ Cần tự implement         | ✅ Built-in                 |
| **Backup & Recovery**      | ⚠️ Nặng khi có BLOB      | ⚠️ Cần công cụ riêng        | ✅ Automated, geo-redundant |
| **Access Control**         | ✅ Qua application layer | ⚠️ OS permissions           | ✅ IAM, Bucket Policy, ACL  |
| **Phù hợp với Docker/K8s** | ✅ (nếu không có BLOB)   | ❌ Ephemeral container      | ✅ Stateless, cloud-native  |

### 2.2 Cây quyết định — Khi nào dùng cái nào?

```mermaid
flowchart TD
    A[Cần lưu file?] --> B{File size?}
    B -->|< 256KB và ít thay đổi| C{Cần transactional\nconsistency tuyệt đối?}
    B -->|Bất kỳ size nào| D{App có scale\nhorizontal không?}

    C -->|Có| E[Database BLOB\nchấp nhận được]
    C -->|Không| F[Object Storage\nvẫn tốt hơn]

    D -->|Có - hoặc chạy Docker/K8s| G[Object Storage\n✅ Default choice]
    D -->|Không - single server| H{File cần\nxử lý tại chỗ?}

    H -->|Có - video encode,\nimage processing| I[Local File System\nlàm temp buffer\nrồi upload Object Storage]
    H -->|Không| G

    G --> J{Loại file?}
    J -->|Ảnh sản phẩm, avatar,\nbanner - public| K[Object Storage\n+ CDN\nBucket public-read]
    J -->|Hóa đơn, KYC,\nbáo cáo - private| L[Object Storage\nBucket private\n+ Signed URL]
    J -->|Import/Export CSV,\ntemp files| M[Object Storage\n+ Lifecycle Policy\nauto-delete sau 7 ngày]
    J -->|Video| N[Object Storage\n+ Transcoding Pipeline\n+ CDN Streaming]
```

### 2.3 Các loại file và phương pháp lưu trữ phù hợp

| Loại file                 | Ví dụ cụ thể               | Phương pháp khuyến nghị             |
| ------------------------- | -------------------------- | ----------------------------------- |
| **Ảnh sản phẩm**          | JPEG, PNG, WebP            | Object Storage + CDN                |
| **Video review sản phẩm** | MP4, MOV                   | Object Storage + CDN + Transcoding  |
| **Tài liệu PDF**          | Hóa đơn, hướng dẫn sử dụng | Object Storage (private)            |
| **File xuất báo cáo**     | CSV, Excel                 | Object Storage (private, temporary) |
| **Avatar người dùng**     | JPEG, PNG                  | Object Storage + CDN                |
| **File import hàng loạt** | CSV, JSON                  | Object Storage (temporary)          |
| **Log files**             | .log, .gz                  | Object Storage / Log management     |
| **File config nhỏ**       | JSON < 64KB                | Database hoặc Config Service        |

## 3. Các loại Object Storage phổ biến

### 3.1 Amazon S3 (Simple Storage Service)

**Nhà cung cấp:** Amazon Web Services (AWS)

**Tổng quan:** Dịch vụ object storage tiên phong (ra mắt 2006), là tiêu chuẩn công nghiệp. Hầu hết các object storage khác đều tương thích với S3 API.

**Storage Classes và Chi phí (tham khảo, vùng us-east-1):**

| Storage Class                | Chi phí lưu trữ/GB/tháng     | Retrieval cost | Use case                       |
| ---------------------------- | ---------------------------- | -------------- | ------------------------------ |
| S3 Standard                  | ~$0.023                      | $0             | File truy cập thường xuyên     |
| S3 Intelligent-Tiering       | ~$0.023 + $0.0025/1K objects | Tự động        | Không rõ access pattern        |
| S3 Standard-IA               | ~$0.0125                     | $0.01/GB       | Truy cập không thường xuyên    |
| S3 Glacier Instant Retrieval | ~$0.004                      | $0.03/GB       | Archive, truy xuất ms          |
| S3 Glacier Deep Archive      | ~$0.00099                    | $0.0025/GB     | Archive lâu dài, truy xuất giờ |

**Tính năng nổi bật:**

- **Lifecycle Policies:** Tự động chuyển file sang class rẻ hơn sau N ngày
- **Versioning:** Giữ lại nhiều phiên bản của cùng một file
- **Server-Side Encryption:** SSE-S3, SSE-KMS, SSE-C
- **Presigned URLs:** Thời gian truy cập có hạn
- **S3 Event Notifications:** Trigger Lambda/SQS khi file được upload
- **Replication:** Cross-region và same-region replication
- **S3 Object Lock:** WORM (Write Once Read Many) cho compliance

**Khi nào chọn S3:** Đang dùng AWS ecosystem, cần tích hợp sâu với AWS services, cần compliance enterprise-grade. Đây là lựa chọn số 1 cho hầu hết doanh nghiệp.

### 3.2 Google Cloud Storage (GCS)

**Nhà cung cấp:** Google Cloud Platform (GCP)

**Storage Classes:**

| Storage Class | Chi phí lưu trữ/GB/tháng | Minimum storage duration |
| ------------- | ------------------------ | ------------------------ |
| Standard      | ~$0.020                  | Không giới hạn           |
| Nearline      | ~$0.010                  | 30 ngày                  |
| Coldline      | ~$0.004                  | 90 ngày                  |
| Archive       | ~$0.0012                 | 365 ngày                 |

**Tính năng nổi bật:** Strong consistency, tích hợp native với BigQuery và Vertex AI, Cloud CDN integration, Uniform bucket-level access (IAM bắt buộc).

**Khi nào chọn GCS:** Đang dùng GCP ecosystem, cần tích hợp BigQuery (phân tích file), ứng dụng AI/ML.

### 3.3 Azure Blob Storage

**Nhà cung cấp:** Microsoft Azure

**Storage Tiers:**

| Tier    | Chi phí lưu trữ/GB/tháng | Latency truy xuất     |
| ------- | ------------------------ | --------------------- |
| Hot     | ~$0.018                  | Milliseconds          |
| Cool    | ~$0.01                   | Milliseconds          |
| Cold    | ~$0.0045                 | Milliseconds          |
| Archive | ~$0.00099                | Giờ (cần rehydration) |

**Khi nào chọn Azure Blob:** Doanh nghiệp dùng Microsoft/Azure ecosystem, ứng dụng .NET, cần Azure Active Directory integration.

### 3.4 Cloudflare R2

**Nhà cung cấp:** Cloudflare (ra mắt 2022)

**Chi phí:**

| Mục                             | Chi phí                |
| ------------------------------- | ---------------------- |
| Lưu trữ                         | $0.015/GB/tháng        |
| Class A operations (write)      | $4.50/million requests |
| Class B operations (read)       | $0.36/million requests |
| **Egress (bandwidth ra ngoài)** | **$0 — MIỄN PHÍ**      |

**Điểm khác biệt:** Zero egress cost là lợi thế lớn nhất. AWS S3 tính $0.09/GB egress — với hệ thống media-heavy, đây là chi phí khổng lồ. R2 tương thích S3 API nên migrate dễ.

**Khi nào chọn R2:** Chi phí bandwidth là vấn đề, đang dùng Cloudflare CDN/Workers, startup cần tối ưu chi phí với public media traffic cao.

### 3.5 MinIO (Self-hosted)

**Nhà cung cấp:** MinIO Inc. (Open Source — Apache/AGPL License)

**Chi phí:** Miễn phí tự deploy (cần tự quản lý infrastructure)

**Tính năng nổi bật:** Tương thích 100% S3 API, high performance, Kubernetes-native, Erasure Coding, phù hợp cho dev/staging local.

**Khi nào chọn MinIO:** On-premise requirement (data không được lên cloud), development và testing (thay thế S3 local), air-gapped environment.

### 3.6 So sánh tổng hợp — Khi nào chọn gì?

```mermaid
flowchart TD
    A[Chọn Object Storage?] --> B{Data có thể\nlên Cloud không?}
    B -->|Không - On-premise| C[MinIO\nSelf-hosted]
    B -->|Có| D{Cloud Provider\nhiện tại?}

    D -->|AWS| E[Amazon S3]
    D -->|GCP| F[Google Cloud Storage]
    D -->|Azure| G[Azure Blob Storage]
    D -->|Chưa có / Multi-cloud| H{Vấn đề lớn nhất\nlà gì?}

    H -->|Chi phí bandwidth\negress cao| I[Cloudflare R2\nZero egress fee]
    H -->|Tích hợp AI/ML\nData analytics| F
    H -->|Enterprise security\nCompliance| E
    H -->|Cost-performance\nbalance| E

    E --> J[ap-southeast-1\nSingapore region\ncho thị trường VN/SEA]
    F --> J
    I --> K[Cloudflare network\nGần người dùng VN]
    C --> L[On-premise DC\nhoặc private cloud]
```

## 4. Metadata của file là gì?

### 4.1 Định nghĩa

**Metadata** là "dữ liệu về dữ liệu" — thông tin mô tả file mà không phải là nội dung file đó. Khi Seller upload ảnh sản phẩm, nội dung ảnh (binary data) được lưu trong Object Storage, nhưng để hệ thống biết file đó là gì, của ai, trạng thái thế nào — cần có metadata trong Database.

### 4.2 Phân loại Metadata theo nhóm

#### A. Technical Metadata — Metadata kỹ thuật

Tự động tạo bởi hệ thống khi nhận file.

| Field             | Ví dụ                  | Nguồn                          |
| ----------------- | ---------------------- | ------------------------------ |
| `file_size`       | 245760 (bytes)         | Server tính khi nhận file      |
| `mime_type`       | `image/jpeg`           | Detect từ magic bytes của file |
| `file_extension`  | `.jpg`                 | Từ tên file gốc                |
| `checksum_sha256` | `a1b2c3...`            | Hash của toàn bộ file content  |
| `created_at`      | `2024-01-15T08:30:00Z` | Timestamp khi upload hoàn tất  |
| `updated_at`      | `2024-01-15T09:00:00Z` | Timestamp khi cập nhật         |

#### B. Storage Metadata — Thông tin lưu trữ

| Field              | Ví dụ                         | Mô tả                   |
| ------------------ | ----------------------------- | ----------------------- |
| `storage_key`      | `products/2024/01/abc123.jpg` | Object key trong S3/GCS |
| `bucket_name`      | `shopvn-product-images`       | Tên bucket              |
| `storage_provider` | `s3`, `gcs`, `r2`             | Nhà cung cấp            |
| `storage_region`   | `ap-southeast-1`              | Region                  |
| `cdn_url`          | `https://cdn.shopvn.vn/...`   | URL CDN public          |

#### C. Business Metadata — Metadata nghiệp vụ

| Field           | Ví dụ                          | Mô tả                    |
| --------------- | ------------------------------ | ------------------------ |
| `uploader_id`   | `user_456`                     | User thực hiện upload    |
| `entity_type`   | `product`, `order`, `user`     | Loại đối tượng liên quan |
| `entity_id`     | `product_789`                  | ID đối tượng liên quan   |
| `file_purpose`  | `main_image`, `invoice`        | Mục đích file            |
| `visibility`    | `public`, `private`            | Quyền truy cập           |
| `status`        | `pending`, `active`, `deleted` | Trạng thái               |
| `display_order` | 1, 2, 3                        | Thứ tự hiển thị          |
| `alt_text`      | "Áo thun trắng size L"         | Alt text cho ảnh         |

#### D. Image-specific Metadata

| Field                 | Ví dụ           | Lưu ý                            |
| --------------------- | --------------- | -------------------------------- |
| `width`               | 1920            | Pixels                           |
| `height`              | 1080            | Pixels                           |
| `color_space`         | `sRGB`          |                                  |
| `has_alpha`           | `false`         | Có kênh alpha (PNG transparency) |
| EXIF: `camera_model`  | `iPhone 14 Pro` | **Cần strip trước khi lưu**      |
| EXIF: `gps_latitude`  | `10.776`        | **Cần strip — privacy risk!**    |
| EXIF: `gps_longitude` | `106.700`       | **Cần strip — privacy risk!**    |

> ⚠️ **Cảnh báo bảo mật quan trọng:** Ảnh chụp bằng điện thoại thường chứa EXIF GPS data — địa điểm chụp ảnh. Khi lưu ảnh do người dùng upload, phải **strip toàn bộ EXIF data** để bảo vệ quyền riêng tư, đặc biệt với ảnh public.

#### E. Video-specific Metadata

| Field              | Ví dụ       |
| ------------------ | ----------- |
| `duration_seconds` | 125         |
| `resolution`       | `1920x1080` |
| `fps`              | 30          |
| `bitrate_kbps`     | 5000        |
| `video_codec`      | `H.264`     |
| `audio_codec`      | `AAC`       |
| `has_audio`        | `true`      |

#### F. Document (PDF/Office) Metadata

| Field           | Ví dụ                                       |
| --------------- | ------------------------------------------- |
| `page_count`    | 12                                          |
| `author`        | "ShopVN System"                             |
| `title`         | "Hóa đơn #INV-2024-001"                     |
| `creation_date` | `2024-01-15`                                |
| `is_searchable` | `true` (có text layer, không phải ảnh scan) |

### 4.3 Metadata lưu ở đâu?

```mermaid
graph LR
    subgraph DB["Database (PostgreSQL)"]
        direction TB
        D1["storage_key, bucket_name"]
        D2["entity_type, entity_id"]
        D3["uploader_id, visibility"]
        D4["file_size, mime_type"]
        D5["width, height"]
        D6["status, created_at"]
        D7["cdn_url, checksum"]
    end

    subgraph S3["Object Storage (S3 Headers)"]
        direction TB
        S1["Content-Type: image/webp"]
        S2["Cache-Control: max-age=31536000"]
        S3H["Content-Disposition: inline"]
        S4["x-amz-meta-uploader-id"]
    end

    Rule["Nguyên tắc:\nMọi metadata cần QUERY / FILTER / JOIN\n→ lưu trong Database\n\nMetadata chỉ cần khi SERVE HTTP response\n→ lưu trong Object Storage headers"]

    DB -.-> Rule
    S3 -.-> Rule
```

## 5. Cách lưu file: File System vs Object Storage

### 5.1 Vấn đề với Local File System

Trong cách tiếp cận truyền thống, file được lưu vào thư mục trên server. Khi deploy với Docker/Kubernetes, đây là vấn đề nghiêm trọng:

```mermaid
graph TD
    subgraph Problem["❌ Vấn đề: File lưu trong Container"]
        LB[Load Balancer]
        C1["Container 1\n/uploads/avatar_123.jpg ✅"]
        C2["Container 2\n/uploads/ TRỐNG ❌"]
        C3["Container 3\n/uploads/ TRỐNG ❌"]

        LB --> C1
        LB --> C2
        LB --> C3

        Note1["User upload vào Container 1\nRequest tiếp theo → Container 2\n→ 404 Not Found 💥"]
    end

    subgraph Solution["✅ Giải pháp: Object Storage ngoài Container"]
        LB2[Load Balancer]
        C4["Container 1\n(Stateless)"]
        C5["Container 2\n(Stateless)"]
        C6["Container 3\n(Stateless)"]
        S3["Object Storage S3\nshopvn-assets/\n  products/...\n  avatars/...\n✅ Tất cả container đều truy cập được"]

        LB2 --> C4
        LB2 --> C5
        LB2 --> C6
        C4 --> S3
        C5 --> S3
        C6 --> S3
    end
```

### 5.2 Cấu trúc Object Key trong S3

Object Storage không có thư mục thực sự (không phải hierarchical filesystem). File được định danh bằng **object key** — một string duy nhất trong bucket. Dấu `/` trong key chỉ là ký tự thông thường, nhưng Console hiển thị như thư mục để dễ quản lý.

**Naming convention chuẩn production:**

```
{entity_type}/{year}/{month}/{entity_id}/{purpose}_{uuid}.{ext}
```

Ví dụ thực tế trong ShopVN:

```
shopvn-product-images/
├── products/2024/01/prod_789abc/main_f7a3b2c1.webp
├── products/2024/01/prod_789abc/thumb_f7a3b2c1.webp
├── products/2024/01/prod_789abc/angle2_f7a3b2c1.webp
├── avatars/2024/01/usr_456def/avatar_q8r2s4t6.webp
└── shops/2024/01/shop_111aaa/banner_m3n4o5p6.webp

shopvn-private-docs/
├── invoices/2024/01/order_ORD12345/invoice_9k2m4p.pdf
├── reports/2024/01/shop_111aaa/revenue_jan_x1y2z3.csv
└── imports/2024/01/seller_456/products_import_a7b8c9.csv

shopvn-kyc-documents/
└── kyc/sellers/seller_456def/cccd_front_p9q0r1.jpg
```

**Tại sao dùng UUID trong tên file thay vì tên gốc?**

- Tránh collision khi 2 user upload file cùng tên
- Tránh path traversal attacks
- Không lộ thông tin nội bộ về hệ thống

## 6. File Validation & Security

Đây là phần **cực kỳ quan trọng** mà nhiều hệ thống bỏ qua, dẫn đến các lỗ hổng bảo mật nghiêm trọng.

### 6.1 Các loại tấn công qua file upload

| Loại tấn công                | Mô tả                                      | Hậu quả                           |
| ---------------------------- | ------------------------------------------ | --------------------------------- |
| **Malicious File Execution** | Upload file `.php`, `.js` giả dạng `.jpg`  | RCE (Remote Code Execution)       |
| **Path Traversal**           | Tên file `../../../etc/passwd`             | Đọc/ghi file nhạy cảm trên server |
| **SSRF via SVG**             | File SVG chứa external entity references   | Internal network scan             |
| **XXE via SVG/XML**          | XML External Entity injection              | Đọc file hệ thống                 |
| **Zip Bomb**                 | File nén 1KB khi giải nén thành 1GB        | DoS — disk full                   |
| **Polyglot Files**           | File vừa là JPEG hợp lệ, vừa là script độc | Bypass validation                 |
| **Malware/Virus**            | File đính kèm chứa mã độc                  | Lây nhiễm khi download            |

### 6.2 Validation Pipeline — Quy trình kiểm tra file

```mermaid
flowchart TD
    A[Client gửi file upload request] --> B{Bước 1:\nKiểm tra Request Headers}
    B -->|Content-Length > max_size| REJECT1[❌ Reject 413\nFile quá lớn]
    B -->|OK| C{Bước 2:\nKiểm tra File Extension}
    C -->|Extension không trong whitelist\n.exe .php .sh .bat ...| REJECT2[❌ Reject 400\nExtension không hợp lệ]
    C -->|OK| D{Bước 3:\nKiểm tra Magic Bytes\nFile signature thực sự}
    D -->|Magic bytes không khớp\nvới MIME type khai báo| REJECT3[❌ Reject 400\nFile type không hợp lệ]
    D -->|OK| E{Bước 4:\nKiểm tra File Size thực tế}
    E -->|Size > limit| REJECT4[❌ Reject 413]
    E -->|OK| F[Bước 5:\nLưu vào temp storage\n/tmp/ trong container]
    F --> G{Bước 6 - Async:\nVirus/Malware Scan\nClamAV / AWS GuardDuty}
    G -->|Phát hiện virus| QUARANTINE[Quarantine file\nAlert security team\nStatus = quarantined]
    G -->|Clean| H{Bước 7 - Async:\nImage/PDF processing\nStrip EXIF\nResize\nConvert WebP}
    H -->|Processing error| ERROR[⚠️ Mark status = processing_failed]
    H -->|OK| I[Bước 8:\nUpload lên Object Storage\nfinal bucket]
    I --> J[Bước 9:\nUpdate DB metadata\nstatus = active]
    J --> K[✅ File sẵn sàng sử dụng]
```

### 6.3 Kiểm tra Magic Bytes — File Type Detection

Không bao giờ tin vào Content-Type header hay file extension từ client. Phải kiểm tra **magic bytes** (file signature) — vài byte đầu tiên của file xác định loại thực sự:

| File Type | Magic Bytes (Hex)             | Mô tả       |
| --------- | ----------------------------- | ----------- |
| JPEG      | `FF D8 FF`                    | 3 byte đầu  |
| PNG       | `89 50 4E 47 0D 0A 1A 0A`     | 8 byte đầu  |
| WebP      | `52 49 46 46 ... 57 45 42 50` | RIFF...WEBP |
| PDF       | `25 50 44 46`                 | `%PDF`      |
| ZIP       | `50 4B 03 04`                 | PK header   |
| GIF       | `47 49 46 38`                 | `GIF8`      |
| MP4       | `... 66 74 79 70`             | ftyp box    |

```python
# Python: Dùng python-magic library để detect
import magic

def validate_file_type(file_bytes: bytes, allowed_types: list) -> bool:
    detected_mime = magic.from_buffer(file_bytes[:2048], mime=True)
    return detected_mime in allowed_types

# Ví dụ sử dụng
allowed = ['image/jpeg', 'image/png', 'image/webp']
is_valid = validate_file_type(file_content, allowed)
```

### 6.4 Whitelist Extension theo loại upload

```python
UPLOAD_CONFIGS = {
    'product_image': {
        'allowed_extensions': ['.jpg', '.jpeg', '.png', '.webp'],
        'allowed_mime_types': ['image/jpeg', 'image/png', 'image/webp'],
        'max_size_bytes': 5 * 1024 * 1024,  # 5MB
    },
    'product_video': {
        'allowed_extensions': ['.mp4', '.mov'],
        'allowed_mime_types': ['video/mp4', 'video/quicktime'],
        'max_size_bytes': 500 * 1024 * 1024,  # 500MB
    },
    'kyc_document': {
        'allowed_extensions': ['.jpg', '.jpeg', '.png', '.pdf'],
        'allowed_mime_types': ['image/jpeg', 'image/png', 'application/pdf'],
        'max_size_bytes': 10 * 1024 * 1024,  # 10MB
    },
    'product_import': {
        'allowed_extensions': ['.csv'],
        'allowed_mime_types': ['text/csv', 'application/csv'],
        'max_size_bytes': 50 * 1024 * 1024,  # 50MB
    },
}
```

### 6.5 Tách biệt bucket upload và bucket serve

Một best practice quan trọng: **không serve file trực tiếp từ bucket upload**.

```mermaid
graph LR
    Client -->|Upload file| UploadBucket["Upload Bucket\nshopvn-uploads-raw\n(Không public)\nCHỈ để nhận file"]
    UploadBucket -->|Trigger Event| Lambda["Processing Lambda\n1. Virus scan\n2. Validate content\n3. Strip EXIF\n4. Resize/Convert\n5. Generate thumbnails"]
    Lambda -->|File hợp lệ, đã xử lý| ServeBucket["Serve Bucket\nshopvn-product-images\n(Public hoặc Private)\nĐã qua xử lý"]
    Lambda -->|File độc hại| Quarantine["Quarantine Bucket\nshopvn-quarantine\n(Private, Alert team)"]
    ServeBucket --> CDN["CloudFront CDN\nhttps://cdn.shopvn.vn"]
    CDN --> EndUser["End User"]
```

## 7. Multipart Upload — Upload File Lớn

### 7.1 Vấn đề với file lớn

Khi upload file lớn (video 500MB, file import 100MB) qua HTTP thông thường:

- Nếu mạng bị ngắt giữa chừng → phải upload lại từ đầu
- File phải buffer hoàn toàn trong RAM trước khi gửi lên S3
- Không thể track tiến độ upload chính xác

### 7.2 Multipart Upload là gì?

S3 Multipart Upload cho phép chia file lớn thành nhiều phần nhỏ (parts), upload song song, rồi S3 ghép lại. Mỗi part tối thiểu 5MB (trừ part cuối).

```mermaid
sequenceDiagram
    participant C as Client
    participant API as API Server
    participant S3 as S3

    C->>API: POST /uploads/multipart/initiate\n{filename, file_size, content_type}
    API->>S3: CreateMultipartUpload
    S3-->>API: upload_id = "VXBsb2FkSWQ..."
    API-->>C: {upload_id, file_id}

    Note over C,S3: Upload từng part (song song)

    C->>API: GET /uploads/multipart/presign\n{upload_id, part_number: 1}
    API-->>C: presigned_url_part_1 (10 min)
    C->>S3: PUT presigned_url_part_1\n[Bytes 0 - 5MB]
    S3-->>C: ETag: "etag_part_1"

    C->>API: GET /uploads/multipart/presign\n{upload_id, part_number: 2}
    API-->>C: presigned_url_part_2 (10 min)
    C->>S3: PUT presigned_url_part_2\n[Bytes 5MB - 10MB]
    S3-->>C: ETag: "etag_part_2"

    Note over C,S3: ... (upload các part còn lại song song) ...

    C->>API: POST /uploads/multipart/complete\n{upload_id, parts: [{part_num, etag},...]}
    API->>S3: CompleteMultipartUpload\n{upload_id, parts}
    S3-->>API: 200 OK - File được ghép lại
    API-->>C: {status: "success", file_url: "..."}
```

### 7.3 Khi nào dùng Multipart Upload?

| File size   | Phương pháp                                                 |
| ----------- | ----------------------------------------------------------- |
| < 5MB       | Single PUT upload (đơn giản hơn)                            |
| 5MB - 100MB | Có thể dùng single PUT, nhưng Multipart tốt hơn             |
| > 100MB     | **Bắt buộc dùng Multipart Upload**                          |
| > 5GB       | S3 giới hạn single PUT là 5GB, Multipart Upload là bắt buộc |

### 7.4 Xử lý upload bị gián đoạn (Resume Upload)

Multipart Upload hỗ trợ resume: nếu mạng bị ngắt, chỉ cần upload lại các part chưa hoàn thành, không cần bắt đầu từ đầu.

```python
# Cleanup các multipart upload bị bỏ dở (tốn storage, tính phí)
# Nên dùng S3 Lifecycle Policy để auto-abort sau N ngày
{
    "Rules": [{
        "ID": "abort-incomplete-multipart-uploads",
        "Status": "Enabled",
        "Filter": {"Prefix": ""},
        "AbortIncompleteMultipartUpload": {
            "DaysAfterInitiation": 3  # Hủy upload bỏ dở sau 3 ngày
        }
    }]
}
```

## 8. Image Processing Pipeline

### 8.1 Tại sao cần xử lý ảnh sau upload?

Ảnh do người dùng upload thường:

- Kích thước quá lớn (ảnh 4K từ điện thoại 12MP = 6-8MB)
- Định dạng không tối ưu (PNG 24-bit thay vì JPEG/WebP)
- Chứa EXIF metadata nhạy cảm (GPS, thiết bị)
- Không có các kích thước phù hợp cho từng context (thumbnail, medium, full)

### 8.2 Pipeline xử lý ảnh sản phẩm ShopVN

```mermaid
flowchart LR
    Upload["Seller upload\nanh_san_pham.jpg\n6MB - 4032x3024px\nEXIF: GPS 10.776, 106.700"] --> Raw["Raw Bucket\nshopvn-uploads-raw\noriginal file"]

    Raw -->|S3 Event Trigger| Lambda["Image Processing\nLambda/Worker"]

    Lambda --> Step1["1️⃣ Strip EXIF\nXóa GPS, thiết bị\ncamera info"]
    Step1 --> Step2["2️⃣ Auto-orient\nXoay đúng chiều\ntheo EXIF orientation"]
    Step2 --> Step3["3️⃣ Convert to WebP\nGiảm 30-40% size\nvẫn giữ chất lượng"]
    Step3 --> Step4["4️⃣ Resize variants"]

    Step4 --> V1["original\n1200x900px\nWebP - ~180KB"]
    Step4 --> V2["medium\n600x450px\nWebP - ~60KB"]
    Step4 --> V3["thumbnail\n200x150px\nWebP - ~12KB"]
    Step4 --> V4["square_thumb\n200x200px crop\nWebP - ~15KB\ndùng cho grid view"]

    V1 --> S3Prod["S3 Production\nshopvn-product-images\nserve qua CDN"]
    V2 --> S3Prod
    V3 --> S3Prod
    V4 --> S3Prod

    S3Prod --> CDN["CloudFront CDN\nhttps://cdn.shopvn.vn"]
    CDN --> DB["Update Database\n4 records trong files table\n(original, medium, thumb, square)"]
```

### 8.3 Responsive Images — Serve đúng kích thước cho từng context

```html
<!-- HTML: Serve đúng ảnh cho từng màn hình -->
<img
  src="https://cdn.shopvn.vn/products/2024/01/prod_789/thumb_f7a3.webp"
  srcset="
    https://cdn.shopvn.vn/products/2024/01/prod_789/thumb_f7a3.webp     200w,
    https://cdn.shopvn.vn/products/2024/01/prod_789/medium_f7a3.webp    600w,
    https://cdn.shopvn.vn/products/2024/01/prod_789/original_f7a3.webp 1200w
  "
  sizes="(max-width: 600px) 200px, (max-width: 1200px) 600px, 1200px"
  alt="Áo thun trắng size L"
/>
```

### 8.4 Video Transcoding Pipeline

Video cần xử lý phức tạp hơn nhiều so với ảnh:

```mermaid
flowchart TD
    Upload["Seller upload\nvideo_review.mov\n500MB - 4K 60fps"] --> S3Raw["S3 Raw Bucket"]

    S3Raw -->|Trigger| MediaConvert["AWS MediaConvert\nhoặc FFmpeg Worker"]

    MediaConvert --> T1["360p MP4\nH.264 - Mobile 3G"]
    MediaConvert --> T2["720p MP4\nH.264 - Standard"]
    MediaConvert --> T3["1080p MP4\nH.264 - HD"]
    MediaConvert --> Thumb["Video Thumbnail\nFrame đầu tiên\nJPEG 1280x720"]
    MediaConvert --> HLS["HLS Playlist\n.m3u8 adaptive streaming\nTự động chọn quality\ntheo bandwidth"]

    T1 --> S3Video["S3 Video Bucket\nshopvn-videos"]
    T2 --> S3Video
    T3 --> S3Video
    Thumb --> S3Images["S3 Image Bucket"]
    HLS --> S3Video

    S3Video --> CF["CloudFront\n+ OAC Origin Access Control"]
    CF --> Player["Video Player\nHLS.js / Video.js\nAdaptive Bitrate Streaming"]
```

## 9. Kiểm soát quyền truy cập file (Access Control)

### 9.1 Các mô hình Access Control

#### A. Public Access

File có thể truy cập bởi bất kỳ ai có URL.

**Phù hợp với:** Ảnh sản phẩm, avatar, banner shop, file marketing.

**Cách implement:** Bucket public-read, serve qua CDN, URL dạng `https://cdn.shopvn.vn/products/abc123.webp`.

#### B. Private Access

File chỉ truy cập được bởi người được cấp phép.

**Phù hợp với:** Hóa đơn, CMND/hộ chiếu KYC, báo cáo tài chính, file import/export.

**Cách implement:** Bucket private + Signed URL với TTL ngắn.

#### C. Role-based Access trong ShopVN

| File                   | Buyer               | Seller (chủ)        | Seller (khác) | Admin        |
| ---------------------- | ------------------- | ------------------- | ------------- | ------------ |
| Ảnh sản phẩm           | ✅ Public           | ✅ Public           | ✅ Public     | ✅ Public    |
| Hóa đơn đơn hàng       | ✅ (order của mình) | ✅ (order của mình) | ❌            | ✅           |
| Báo cáo doanh thu shop | ❌                  | ✅ (shop của mình)  | ❌            | ✅           |
| File import sản phẩm   | ❌                  | ✅ (file của mình)  | ❌            | ✅           |
| CMND/KYC               | ❌                  | ✅ Chỉ xem          | ❌            | ✅ Xem + tải |
| Giấy phép kinh doanh   | ❌                  | ✅ Chỉ xem          | ❌            | ✅ Xem + tải |

### 9.2 Các tầng bảo mật (Defense in Depth)

```mermaid
graph TD
    Client["Client Request"]

    Client --> L1["Layer 1: CDN / Edge\nCloudFront Signed Cookies\nWAF Rules\nGeo-restriction\nDDoS Protection"]

    L1 --> L2["Layer 2: Application Layer\nJWT Authentication\nRole-based Authorization\nBusiness logic check\n(user có quyền với file này không?)"]

    L2 --> L3["Layer 3: Object Storage IAM\nS3 Bucket Policy\nIAM Roles & Permissions\nResource-based policies\nBlock Public Access settings"]

    L3 --> L4["Layer 4: Storage Encryption\nSSE-S3 (AES-256)\nSSE-KMS (Customer Managed Key)\nClient-side encryption\nIn-transit TLS 1.2+"]

    L4 --> File["File Content"]
```

### 9.3 S3 Bucket Policy — Ví dụ thực tế

**Bucket public cho ảnh sản phẩm:**

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowCloudFrontServicePrincipal",
      "Effect": "Allow",
      "Principal": {
        "Service": "cloudfront.amazonaws.com"
      },
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::shopvn-product-images/*",
      "Condition": {
        "StringEquals": {
          "AWS:SourceArn": "arn:aws:cloudfront::123456789:distribution/EDFDVBD6EXAMPLE"
        }
      }
    }
  ]
}
```

> **Lưu ý:** Đây là pattern **Origin Access Control (OAC)** — chỉ CloudFront mới được phép đọc S3 trực tiếp, client không gọi thẳng S3 URL được. Tốt hơn `public-read` vì bắt buộc traffic đi qua CDN.

**Bucket private cho tài liệu:**

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DenyAllPublicAccess",
      "Effect": "Deny",
      "Principal": "*",
      "Action": "s3:*",
      "Resource": [
        "arn:aws:s3:::shopvn-private-docs",
        "arn:aws:s3:::shopvn-private-docs/*"
      ],
      "Condition": {
        "StringNotEquals": {
          "aws:PrincipalArn": "arn:aws:iam::123456789:role/shopvn-app-role"
        }
      }
    }
  ]
}
```

## 10. Signed URL

### 10.1 Định nghĩa và cơ chế hoạt động

**Signed URL** là một URL đặc biệt có chứa **chữ ký số (cryptographic signature)** và **thời hạn**, được tạo bởi application server, cho phép client tạm thời truy cập một file **private** trong Object Storage **mà không cần credentials**.

```mermaid
sequenceDiagram
    participant C as Client
    participant API as API Server
    participant S3 as S3 (Private Bucket)

    C->>API: GET /files/{file_id}/download-url\nAuthorization: Bearer jwt_token

    API->>API: 1. Verify JWT token\n2. Load file metadata từ DB\n3. Check quyền truy cập\n   (user có phải owner/admin?)

    alt Không có quyền
        API-->>C: 403 Forbidden
    end

    API->>S3: GeneratePresignedUrl\n(object_key, expires=300s)
    S3-->>API: signed_url (chứa signature + expiry)

    API->>API: 4. Ghi audit log\n   (user_id, file_id, timestamp, ip)

    API-->>C: {download_url: "https://s3.../...\n?X-Amz-Expires=300\n&X-Amz-Signature=...",\n expires_at: "2024-01-15T09:05:00Z"}

    Note over C,S3: Client dùng URL trực tiếp (không qua API server)

    C->>S3: GET signed_url
    S3->>S3: Verify signature\nVerify expiry\nCheck not tampered

    alt URL hợp lệ và chưa hết hạn
        S3-->>C: 200 OK + File content
    else URL hết hạn hoặc invalid
        S3-->>C: 403 Forbidden
    end
```

### 10.2 Cấu trúc Signed URL (AWS S3)

```
https://shopvn-private-docs.s3.ap-southeast-1.amazonaws.com/invoices/2024/01/invoice.pdf
  ?X-Amz-Algorithm=AWS4-HMAC-SHA256
  &X-Amz-Credential=AKIAIOSFODNN7EXAMPLE%2F20240115%2Fap-southeast-1%2Fs3%2Faws4_request
  &X-Amz-Date=20240115T083000Z
  &X-Amz-Expires=300                     ← Hết hạn sau 300 giây
  &X-Amz-SignedHeaders=host
  &X-Amz-Signature=a3b4c5d6e7f8a9b0...   ← HMAC-SHA256 signature
```

### 10.3 Các loại Signed URL

#### Presigned GET URL — Download/xem file

```python
import boto3

s3_client = boto3.client('s3', region_name='ap-southeast-1')

presigned_url = s3_client.generate_presigned_url(
    ClientMethod='get_object',
    Params={
        'Bucket': 'shopvn-private-docs',
        'Key': 'invoices/2024/01/order_ORD12345/invoice_9k2m4p.pdf',
        'ResponseContentDisposition': 'attachment; filename="Invoice-ORD12345.pdf"',
        'ResponseContentType': 'application/pdf'
    },
    ExpiresIn=300  # 5 phút
)
```

#### Presigned PUT URL — Upload trực tiếp từ client lên S3

```python
presigned_url = s3_client.generate_presigned_url(
    ClientMethod='put_object',
    Params={
        'Bucket': 'shopvn-uploads-raw',
        'Key': f'products/2024/01/prod_789abc/main_{uuid}.jpg',
        'ContentType': 'image/jpeg',
    },
    ExpiresIn=600  # 10 phút để upload
)
```

#### Presigned POST — Form-based upload với điều kiện phức tạp

```python
presigned_post = s3_client.generate_presigned_post(
    Bucket='shopvn-uploads-raw',
    Key=f'products/2024/01/prod_789abc/main_{uuid}.jpg',
    Conditions=[
        {'Content-Type': 'image/jpeg'},
        ['content-length-range', 1024, 5 * 1024 * 1024],  # 1KB - 5MB
        ['starts-with', '$key', 'products/'],             # Key phải bắt đầu bằng products/
    ],
    ExpiresIn=600
)
```

### 10.4 Bảng TTL khuyến nghị theo use case

| Use case                       | Loại            | TTL khuyến nghị | Lý do                            |
| ------------------------------ | --------------- | --------------- | -------------------------------- |
| User tải hóa đơn (manual)      | GET             | 5-15 phút       | Thao tác đơn giản, không cần lâu |
| User xem ảnh CCCD              | GET             | 1-5 phút        | Rất nhạy cảm                     |
| Admin xuất báo cáo lớn         | GET             | 30-60 phút      | File lớn, tải lâu                |
| User upload ảnh sản phẩm       | PUT             | 10-15 phút      | Đủ để upload từ mobile           |
| Upload video lớn               | PUT (Multipart) | 60 phút/part    | Video 500MB cần thời gian        |
| Link trong email transactional | GET             | 24-48 giờ       | User mở email sau vài tiếng      |
| Link share nội bộ              | GET             | 7 ngày          | Chia sẻ trong team               |

### 10.5 Lưu ý quan trọng

- **Không log Signed URL** — nếu log bị leak, URL có thể bị dùng trong thời gian còn hiệu lực
- **Không revoke được** — Sau khi cấp, không thể thu hồi trước khi hết hạn (trừ khi xóa object hoặc rotate signing key)
- **Không nhúng vào HTML cache** — Browser cache có thể phục vụ URL đã hết hạn
- **CloudFront Signed Cookies** thay vì Signed URL khi cần bảo vệ nhiều file cùng lúc (ví dụ: toàn bộ thư mục video khóa học của một user)
- **URL có thể share** — Bất kỳ ai có URL đều truy cập được trong thời hạn, thiết kế TTL phù hợp

## 11. CDN — Content Delivery Network

### 11.1 CDN là gì và tại sao cần?

**CDN** là mạng lưới server phân tán toàn cầu (Points of Presence - PoP), lưu cache bản sao của file tại server gần người dùng nhất. Thay vì mọi request đều phải về S3 tại Singapore, user ở Hà Nội sẽ được phục vụ từ PoP tại Hà Nội hoặc TP.HCM.

```mermaid
graph TD
    subgraph NoCDN["❌ Không có CDN - Mọi request đến S3 Singapore"]
        U1["User Hà Nội"] -->|200ms latency| S3SG["S3 Singapore"]
        U2["User Đà Nẵng"] -->|180ms latency| S3SG
        U3["User TP.HCM"] -->|100ms latency| S3SG
        U4["User Bangkok"] -->|150ms latency| S3SG
    end

    subgraph WithCDN["✅ Có CDN - Phục vụ từ PoP gần nhất"]
        U5["User Hà Nội"] -->|5ms latency| PoP1["PoP Hà Nội\n(Cache hit)"]
        U6["User Đà Nẵng"] -->|8ms latency| PoP2["PoP TP.HCM\n(Cache hit)"]
        U7["User TP.HCM"] -->|3ms latency| PoP2
        U8["User Bangkok"] -->|10ms latency| PoP3["PoP Bangkok\n(Cache hit)"]
        PoP1 -.->|Cache MISS: fetch origin| S3SG2["S3 Singapore\n(Origin)"]
        PoP2 -.->|Cache MISS| S3SG2
        PoP3 -.->|Cache MISS| S3SG2
    end
```

### 11.2 Cache-Control Headers — Cấu hình cache đúng cách

Đây là phần quan trọng mà nhiều người bỏ qua, dẫn đến user thấy ảnh cũ sau khi update.

```mermaid
graph LR
    FileType["Loại file"] --> Decision{Có fingerprint\ntrong tên file?}

    Decision -->|Có\ne.g. main_f7a3b2c1.webp| Immutable["Cache-Control:\npublic, max-age=31536000, immutable\n\nCó thể cache 1 năm\nNếu file thay đổi → tên file mới\n→ URL mới → cache bust tự động"]

    Decision -->|Không\ne.g. avatar.webp\ntên cố định| Short["Cache-Control:\npublic, max-age=86400\nstale-while-revalidate=3600\n\nCache 1 ngày\nSau 1 ngày browser check lại"]

    Private["File Private\n(Signed URL)"] --> NoCache["Cache-Control:\nprivate, no-store\n\nKhông cache ở CDN\nBrowser không lưu"]
```

**Quy tắc chung:**

- **Fingerprinted files** (content hash trong tên): `public, max-age=31536000, immutable` — cache vĩnh viễn, khi thay đổi nội dung thì đổi tên file
- **Mutable files** (tên cố định, nội dung có thể thay đổi): `public, max-age=86400` — cache ngắn hơn
- **Private files**: `private, no-store` — không cache ở CDN

### 11.3 CloudFront Distribution Setup cho ShopVN

```mermaid
graph TD
    subgraph ShopVN_CDN["CloudFront Distribution: d1example.cloudfront.net"]
        direction TB
        Behavior1["Behavior 1: /products/*\nOrigin: S3 shopvn-product-images\nCache: TTL 1 năm\nCompress: Gzip + Brotli\nHTTPS only"]
        Behavior2["Behavior 2: /avatars/*\nOrigin: S3 shopvn-product-images\nCache: TTL 1 ngày\nHTTPS only"]
        Behavior3["Behavior 3: /videos/*\nOrigin: S3 shopvn-videos\nCache: TTL 1 tuần\nHTTP/2 streaming"]
        WAF["WAF Rules\n- Rate limiting\n- Bot detection\n- Geo restriction"]
    end

    Custom["Custom Domain:\ncdn.shopvn.vn\n(CNAME to CloudFront)"] --> ShopVN_CDN
    S3Products["S3: shopvn-product-images"] -.->|Origin pull| Behavior1
    S3Products -.->|Origin pull| Behavior2
    S3Videos["S3: shopvn-videos"] -.->|Origin pull| Behavior3
```

### 11.4 CDN Cache Invalidation

Khi file public thay đổi (seller đổi ảnh đại diện), cần xóa cache CDN:

```python
import boto3

cloudfront = boto3.client('cloudfront')

# Invalidate specific files
cloudfront.create_invalidation(
    DistributionId='EDFDVBD6EXAMPLE',
    InvalidationBatch={
        'Paths': {
            'Quantity': 1,
            'Items': ['/avatars/2024/01/usr_456def/avatar_q8r2s4t6.webp']
        },
        'CallerReference': str(time.time())
    }
)
```

> **Lưu ý chi phí:** CloudFront tính phí invalidation sau 1,000 paths/tháng đầu miễn phí. Hệ thống dùng **fingerprinted filename** (hash trong tên) để tránh cần invalidation — đây là best practice.

## 12. S3 Lifecycle Policy — Quản lý vòng đời file

### 12.1 Tại sao cần Lifecycle Policy?

Không phải file nào cũng cần lưu trữ mãi mãi với cùng storage class. Ảnh sản phẩm của đơn hàng 3 năm trước ít được xem hơn ảnh mới. Hóa đơn cũ không cần truy xuất nhanh như hóa đơn mới.

```mermaid
graph LR
    Upload["File được upload\nDay 0"] --> Standard["S3 Standard\n$0.023/GB\nTruy cập thường xuyên\nDay 0-30"]
    Standard -->|"Sau 30 ngày\n(ít truy cập hơn)"| IA["S3 Standard-IA\n$0.0125/GB\nDay 30-365"]
    IA -->|"Sau 1 năm\n(archive)"| Glacier["S3 Glacier\n$0.004/GB\nDay 365-2555"]
    Glacier -->|"Sau 7 năm\n(compliance done)"| Delete["Xóa\nDay 2555+"]

    Note1["Ảnh sản phẩm\nhóa đơn thông thường"]
    Note2["KYC documents:\nKhông xóa, giữ vĩnh viễn\nở Glacier Deep Archive\n$0.00099/GB"]
```

### 12.2 Lifecycle Policy theo loại file trong ShopVN

```json
{
  "Rules": [
    {
      "ID": "product-images-tiering",
      "Status": "Enabled",
      "Filter": { "Prefix": "products/" },
      "Transitions": [
        { "Days": 90, "StorageClass": "STANDARD_IA" },
        { "Days": 730, "StorageClass": "GLACIER" }
      ]
    },
    {
      "ID": "temp-imports-cleanup",
      "Status": "Enabled",
      "Filter": { "Prefix": "imports/" },
      "Expiration": { "Days": 7 }
    },
    {
      "ID": "temp-exports-cleanup",
      "Status": "Enabled",
      "Filter": { "Prefix": "reports/" },
      "Expiration": { "Days": 30 }
    },
    {
      "ID": "abort-incomplete-multipart",
      "Status": "Enabled",
      "Filter": { "Prefix": "" },
      "AbortIncompleteMultipartUpload": { "DaysAfterInitiation": 3 }
    }
  ]
}
```

## 13. Luồng Upload và Download File

### 13.1 Luồng Upload Ảnh Sản Phẩm — Direct Upload via Presigned URL

```mermaid
sequenceDiagram
    participant Seller as Seller App
    participant API as API Server
    participant DB as PostgreSQL
    participant S3Raw as S3 Raw Bucket
    participant Worker as Processing Worker
    participant S3Prod as S3 Production
    participant CDN as CloudFront CDN

    Seller->>API: POST /products/{id}/images/upload-url\nBody: {filename, file_size, content_type}\nAuth: Bearer jwt_token

    API->>API: 1. Verify JWT\n2. Validate: extension in whitelist?\n3. Validate: file_size <= 5MB?\n4. Generate object_key với UUID

    API->>DB: INSERT INTO files\n(status='pending', storage_key, entity_id...)
    DB-->>API: file_id = "f7a3b2c1..."

    API->>S3Raw: GeneratePresignedUrl\n(PUT, object_key, 10min, ContentType check)
    S3Raw-->>API: presigned_url

    API-->>Seller: {file_id, presigned_url, expires_in: 600}

    Note over Seller,S3Raw: Client upload TRỰC TIẾP lên S3, không qua API Server

    Seller->>S3Raw: PUT presigned_url\n[Binary file content]\nContent-Type: image/jpeg
    S3Raw-->>Seller: 200 OK (ETag: "abc123")

    Seller->>API: POST /products/{id}/images/{file_id}/confirm\nBody: {upload_status: "success"}

    API->>S3Raw: HeadObject(object_key)
    Note right of API: Verify file thực sự tồn tại\nKhông tin tưởng client

    S3Raw-->>API: 200 OK\n{ContentLength: 245760, ContentType: image/jpeg}

    API->>DB: UPDATE files SET status='processing'
    API->>Worker: Publish message to SQS\n{file_id, object_key, bucket}

    API-->>Seller: {status: "processing", file_id}

    Note over Worker,CDN: Async processing (không block response)

    Worker->>S3Raw: GetObject (download original)
    Worker->>Worker: 1. Read magic bytes - verify JPEG\n2. Strip EXIF (GPS, camera info)\n3. Auto-orient\n4. Convert to WebP\n5. Resize: 1200px, 600px, 200px, 200px-square

    Worker->>S3Prod: PutObject (4 variants)
    S3Prod-->>Worker: OK

    Worker->>DB: UPDATE files SET\nstatus='active', cdn_url, width, height,\nmime_type='image/webp'

    Note over CDN: File sẵn sàng serve qua CDN

    CDN-->>Seller: Webhook/Polling:\n{status: "active",\nurls: {original, medium, thumbnail}}
```

### 13.2 Luồng Download File Private — Hóa Đơn

```mermaid
sequenceDiagram
    participant Buyer as Buyer App
    participant API as API Server
    participant DB as PostgreSQL
    participant AuditLog as Audit Log
    participant S3 as S3 Private Bucket

    Buyer->>API: GET /orders/{order_id}/invoice/download\nAuthorization: Bearer jwt_token

    API->>API: 1. Verify JWT → extract user_id

    API->>DB: SELECT f.*, o.buyer_id, o.seller_id\nFROM files f\nJOIN orders o ON f.entity_id = o.id\nWHERE f.entity_id = order_id\nAND f.file_purpose = 'invoice'\nAND f.status = 'active'
    DB-->>API: file record + order info

    alt File không tồn tại
        API-->>Buyer: 404 Not Found
    end

    API->>API: 2. Authorization check:\nuser_id == buyer_id OR\nuser_id == seller_id OR\nuser.role == 'admin'?

    alt Không có quyền
        API-->>Buyer: 403 Forbidden
    end

    API->>S3: GeneratePresignedUrl\n(GET, object_key, expires=300s,\nResponseContentDisposition: attachment)
    S3-->>API: signed_url

    API->>AuditLog: INSERT INTO file_access_logs\n{file_id, user_id, action:'download',\nip_address, user_agent, timestamp}

    API-->>Buyer: {download_url: signed_url,\nfilename: "Invoice-ORD12345.pdf",\nexpires_in: 300,\nexpires_at: "2024-01-15T09:05:00Z"}

    Note over Buyer,S3: Client download trực tiếp từ S3

    Buyer->>S3: GET signed_url
    S3->>S3: Verify HMAC-SHA256 signature\nCheck X-Amz-Expires not expired\nCheck not tampered

    alt URL hợp lệ
        S3-->>Buyer: 200 OK [PDF content]<br>Content-Type: application/pdf<br>Content-Disposition: attachment<br>filename="Invoice-ORD12345.pdf"
    else URL hết hạn
        S3-->>Buyer: 403 Forbidden\n(Request URL expired)
        Note right of Buyer: Client phải request\nAPI lấy URL mới
    end
```

### 13.3 Luồng Serve File Public — Ảnh Sản Phẩm qua CDN

```mermaid
sequenceDiagram
    participant Browser as Browser
    participant CDN as CloudFront PoP<br>(TP.HCM)
    participant S3 as S3 Singapore<br>(Origin)

    Browser->>CDN: GET https://cdn.shopvn.vn/products/<br>2024/01/prod_789/main_f7a3.webp<br>Accept: image/webp,*/*

    CDN->>CDN: Check cache by cache key:<br>(URL + Accept-Encoding)

    alt Cache HIT
        CDN-->>Browser: 200 OK [Image]<br>X-Cache: Hit from cloudfront<br>Age: 86400<br>Cache-Control: public, max-age=31536000<br>< 10ms latency
    else Cache MISS - Lần đầu tiên
        CDN->>S3: GET s3://shopvn-product-images/<br>products/2024/01/prod_789/main_f7a3.webp

        S3-->>CDN: 200 OK [Image]<br>Content-Type: image/webp<br>Content-Length: 180000<br>Cache-Control: public, max-age=31536000

        CDN->>CDN: Lưu vào CDN cache<br>(giữ 1 năm hoặc đến khi invalidate)

        CDN-->>Browser: 200 OK [Image]<br>X-Cache: Miss from cloudfront<br>(lần sau sẽ là cache HIT)
    end
```

### 13.4 Tổng quan kiến trúc hệ thống file ShopVN

```mermaid
graph TB
    subgraph Clients["Clients"]
        BuyerApp["Buyer App\n(Web/Mobile)"]
        SellerApp["Seller App\n(Web/Mobile)"]
        AdminApp["Admin App"]
    end

    subgraph ApplicationLayer["Application Layer"]
        APIGW["API Gateway\n+ Rate Limiting\n+ Auth"]
        AppServer["App Servers\n(Stateless Pods)\n- Auth/Authz\n- Presigned URL gen\n- Metadata CRUD"]
        Worker["Async Workers\n- Image processing\n- Virus scan\n- PDF generation\n- Video transcoding"]
    end

    subgraph DataLayer["Data Layer"]
        PostgreSQL["PostgreSQL\n- files table\n- file_access_logs\n- orders, users..."]
        Redis["Redis\n- Session cache\n- Rate limit counters"]
        SQS["SQS Queue\n- file.uploaded\n- file.process\n- file.delete"]
    end

    subgraph StorageLayer["Storage Layer (AWS S3)"]
        S3Raw["shopvn-uploads-raw\nTemp upload bucket\n(files chưa xử lý)"]
        S3Public["shopvn-product-images\nPublic bucket\n(ảnh, video, avatar)"]
        S3Private["shopvn-private-docs\nPrivate bucket\n(hoá đơn, báo cáo)"]
        S3KYC["shopvn-kyc-documents\nPrivate + KMS encrypt\n(CMND, giấy phép)"]
        S3Quarantine["shopvn-quarantine\nFile độc hại\n(cách ly)"]
    end

    subgraph CDNLayer["CDN Layer"]
        CloudFront["CloudFront\ncdn.shopvn.vn\n- Cache public files\n- Edge locations SEA\n- WAF protection"]
    end

    BuyerApp --> APIGW
    SellerApp --> APIGW
    AdminApp --> APIGW
    APIGW --> AppServer
    AppServer --> PostgreSQL
    AppServer --> Redis
    AppServer --> SQS
    SQS --> Worker
    Worker --> S3Raw
    Worker --> S3Public
    Worker --> S3Private
    Worker --> S3KYC
    Worker --> S3Quarantine
    AppServer -->|"Generate Presigned URL"| S3Raw
    AppServer -->|"Generate Presigned URL"| S3Private
    AppServer -->|"Generate Presigned URL"| S3KYC
    S3Public --> CloudFront
    CloudFront --> BuyerApp
    CloudFront --> SellerApp
```

## 14. Ví dụ Áp dụng: Hệ thống E-Commerce ShopVN

### 14.1 Tổng quan các loại file trong ShopVN

| File                 | Ai upload    | Loại          | Bucket                | Visibility    | Ai được xem            |
| -------------------- | ------------ | ------------- | --------------------- | ------------- | ---------------------- |
| Ảnh sản phẩm         | Seller       | JPEG/PNG/WebP | shopvn-product-images | Public        | Tất cả                 |
| Video demo sản phẩm  | Seller       | MP4           | shopvn-videos         | Public        | Tất cả                 |
| Avatar người dùng    | Buyer/Seller | JPEG/PNG      | shopvn-product-images | Public        | Tất cả                 |
| Banner shop          | Seller       | JPEG/PNG      | shopvn-product-images | Public        | Tất cả                 |
| Hóa đơn PDF          | System       | PDF           | shopvn-private-docs   | Private       | Buyer + Seller + Admin |
| CMND/Hộ chiếu (KYC)  | Seller       | JPEG/PDF      | shopvn-kyc-documents  | Private (KMS) | Admin only             |
| File import sản phẩm | Seller       | CSV           | shopvn-uploads-raw    | Private       | Seller (chủ) + Admin   |
| File xuất đơn hàng   | Admin/Seller | CSV/Excel     | shopvn-private-docs   | Private       | Seller (chủ) + Admin   |
| Giấy phép kinh doanh | Seller       | PDF           | shopvn-kyc-documents  | Private (KMS) | Admin only             |

### 14.2 Database Schema

```sql
-- Bảng chính lưu metadata tất cả files
CREATE TABLE files (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Storage info
    storage_key       VARCHAR(500) NOT NULL,
    bucket_name       VARCHAR(100) NOT NULL,
    storage_provider  VARCHAR(20)  NOT NULL DEFAULT 's3',
    storage_region    VARCHAR(50)  NOT NULL DEFAULT 'ap-southeast-1',
    cdn_url           VARCHAR(500),                    -- NULL nếu private

    -- File info
    original_name     VARCHAR(255) NOT NULL,
    file_size         BIGINT       NOT NULL,           -- bytes
    mime_type         VARCHAR(100) NOT NULL,
    file_extension    VARCHAR(20)  NOT NULL,
    checksum_sha256   VARCHAR(64),

    -- Image/Video dimensions
    width             INTEGER,                         -- pixels
    height            INTEGER,
    duration_seconds  INTEGER,                         -- video duration

    -- Business info
    uploader_id       UUID         NOT NULL REFERENCES users(id),
    entity_type       VARCHAR(50)  NOT NULL,           -- product, order, user, shop
    entity_id         UUID         NOT NULL,
    file_purpose      VARCHAR(50)  NOT NULL,           -- main_image, thumb, invoice, kyc_front...
    display_order     SMALLINT     DEFAULT 0,
    alt_text          VARCHAR(500),

    -- Access control
    visibility        VARCHAR(20)  NOT NULL DEFAULT 'private',  -- public, private

    -- Processing
    status            VARCHAR(30)  NOT NULL DEFAULT 'pending',
    -- pending → processing → active
    -- pending → processing → failed
    -- active → deleted

    processing_error  TEXT,

    -- Timestamps
    created_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    deleted_at        TIMESTAMPTZ  -- Soft delete

    CONSTRAINT files_visibility_check
        CHECK (visibility IN ('public', 'private'))
    CONSTRAINT files_status_check
        CHECK (status IN ('pending', 'processing', 'active', 'failed',
                          'quarantined', 'deleted'))
);

-- Bảng audit log: ai đã truy cập file nào
CREATE TABLE file_access_logs (
    id          BIGSERIAL    PRIMARY KEY,
    file_id     UUID         NOT NULL REFERENCES files(id),
    user_id     UUID         REFERENCES users(id),   -- NULL nếu public access
    action      VARCHAR(20)  NOT NULL,               -- download, view, upload, delete
    ip_address  INET,
    user_agent  VARCHAR(500),
    accessed_at TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_files_entity        ON files(entity_type, entity_id);
CREATE INDEX idx_files_uploader      ON files(uploader_id);
CREATE INDEX idx_files_status        ON files(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_files_storage_key   ON files(storage_key);
CREATE INDEX idx_files_entity_purpose ON files(entity_type, entity_id, file_purpose);
CREATE INDEX idx_access_logs_file    ON file_access_logs(file_id, accessed_at DESC);
CREATE INDEX idx_access_logs_user    ON file_access_logs(user_id, accessed_at DESC);
```

### 14.3 Use Case 1 — Seller upload ảnh sản phẩm

**User upload file gì?** Seller upload ảnh sản phẩm (JPEG/PNG), tối đa 5MB/ảnh, tối đa 9 ảnh/sản phẩm.

**File được lưu ở đâu?**

- **Tạm thời:** `shopvn-uploads-raw` → `products/2024/01/prod_789abc/main_uuid.jpg`
- **Sau xử lý:** `shopvn-product-images` → 4 variants WebP

**Metadata lưu trong Database:**

```sql
-- Record sau khi processing xong
{
  id:              'f7a3b2c1-d9e5-4f12-a8b3-c1d2e3f4a5b6',
  storage_key:     'products/2024/01/prod_789abc/main_f7a3b2c1.webp',
  bucket_name:     'shopvn-product-images',
  cdn_url:         'https://cdn.shopvn.vn/products/2024/01/prod_789abc/main_f7a3b2c1.webp',
  original_name:   'ao-thun-trang.jpg',
  file_size:       180224,            -- 176KB sau convert WebP
  mime_type:       'image/webp',      -- Đã convert
  file_extension:  'webp',
  width:           1200,
  height:          900,
  uploader_id:     'seller_456def',
  entity_type:     'product',
  entity_id:       'prod_789abc',
  file_purpose:    'main_image',
  display_order:   1,
  alt_text:        'Áo thun trắng size L',
  visibility:      'public',
  status:          'active'
}
```

**Ai được quyền xem?** Tất cả mọi người — public file, serve qua CDN không cần authentication. URL: `https://cdn.shopvn.vn/products/2024/01/prod_789abc/main_f7a3b2c1.webp`

### 14.4 Use Case 2 — Hóa đơn mua hàng (PDF)

**User upload file gì?** Không phải user upload — system tự động generate PDF sau khi đơn hàng được xác nhận thanh toán bằng thư viện như WeasyPrint hoặc Puppeteer.

**File được lưu ở đâu?** `shopvn-private-docs` → `invoices/2024/01/order_ORD12345/invoice_9k2m4p.pdf`

**Metadata trong Database:**

```sql
{
  storage_key:   'invoices/2024/01/order_ORD12345/invoice_9k2m4p7r.pdf',
  bucket_name:   'shopvn-private-docs',
  cdn_url:       NULL,              -- Không có CDN, file private
  original_name: 'invoice_ORD12345.pdf',
  file_size:     125952,
  mime_type:     'application/pdf',
  uploader_id:   'system',          -- Được generate bởi system
  entity_type:   'order',
  entity_id:     'ORD12345',
  file_purpose:  'invoice',
  visibility:    'private',
  status:        'active'
}
```

**Ai được quyền xem?**

- Buyer của đơn hàng ORD12345 → Signed URL 5 phút
- Seller của đơn hàng ORD12345 → Signed URL 5 phút
- Admin → Signed URL 30 phút
- Mọi người khác → 403 Forbidden

### 14.5 Use Case 3 — CCCD/CMND Seller (KYC)

**User upload file gì?** Seller upload ảnh CCCD 2 mặt để xác minh danh tính. JPEG hoặc PDF, tối đa 10MB.

**File được lưu ở đâu?** `shopvn-kyc-documents` → `kyc/sellers/seller_456def/cccd_front_p9q0r1.jpg`

**Bảo mật bổ sung:**

- Bucket Block All Public Access: enabled
- Server-Side Encryption: SSE-KMS với Customer Managed Key (CMK)
- S3 Object Lock: WORM, giữ tối thiểu 7 năm (compliance)
- VPC Endpoint: Chỉ accessible từ trong VPC

**Ai được quyền xem?**

- Seller (chủ nhân): Chỉ xem inline (không tải), Signed URL 2 phút
- Admin/Compliance team: Xem và tải, Signed URL 15 phút
- Kỹ sư không có IAM role: Không có quyền dù có access S3 console

## 15. Storage Cost Optimization

### 15.1 Các khoản phí cần hiểu

Chi phí object storage không chỉ là phí lưu trữ. Cần hiểu toàn bộ các khoản:

| Khoản phí                   | AWS S3 (us-east-1)         | Ghi chú                      |
| --------------------------- | -------------------------- | ---------------------------- |
| **Storage**                 | $0.023/GB/tháng (Standard) | Theo từng storage class      |
| **PUT/COPY/POST**           | $0.005/1,000 requests      | Mỗi lần upload/copy          |
| **GET/SELECT**              | $0.0004/1,000 requests     | Mỗi lần download/đọc         |
| **Egress**                  | $0.09/GB (đầu tiên 10TB)   | **Thường là khoản lớn nhất** |
| **Inter-region transfer**   | $0.02/GB                   | Transfer giữa regions        |
| **CloudFront origin fetch** | $0.0085/GB                 | Transfer S3 → CloudFront     |
| **CloudFront egress**       | $0.0085/GB (SEA)           | Transfer CloudFront → User   |

**Ví dụ tính chi phí tháng cho ShopVN:**

```
Giả sử:
- 50TB ảnh sản phẩm (S3 Standard)
- 5TB video (S3 Standard)
- 1TB tài liệu private (S3 Standard)
- Traffic: 100TB egress qua CloudFront/tháng

Storage: (50 + 5 + 1) TB × $0.023 = ~$1,288/tháng
GET requests: 50M requests × $0.0004/1K = $20/tháng
CloudFront egress (SEA): 100TB × $0.085/GB = ~$8,500/tháng  ← Khoản lớn nhất!
S3 → CloudFront: 100TB × $0.0085/GB = $850/tháng

Total: ~$10,658/tháng

Nếu dùng Cloudflare R2 + CDN:
Storage: 56TB × $0.015 = $840/tháng
Egress: $0 (zero egress fee)
→ Tiết kiệm ~$9,800/tháng
```

### 15.2 Các chiến lược tối ưu chi phí

```mermaid
graph TD
    Optimize["Cost Optimization\nStrategies"] --> A["1️⃣ Lifecycle Policy\nAuto-transition sang\nclass rẻ hơn sau N ngày"]
    Optimize --> B["2️⃣ Intelligent-Tiering\nS3 tự động chuyển class\ntheo access pattern\n(tiết kiệm 40-70%)"]
    Optimize --> C["3️⃣ Compress files\nGzip text files (CSV, JSON)\nWebP thay JPEG/PNG\n(giảm 30-40% size)"]
    Optimize --> D["4️⃣ Delete unused files\nCleanup temp/expired files\nSoft-delete → hard-delete sau 30 ngày"]
    Optimize --> E["5️⃣ CloudFront caching\nCache hit rate cao\n→ ít origin fetch\n→ giảm S3 GET + transfer"]
    Optimize --> F["6️⃣ Evaluate R2 cho\npublic media-heavy files\nNếu egress là vấn đề lớn"]
    Optimize --> G["7️⃣ S3 Storage Lens\nAnalyze và visualize\nusage patterns\nTìm cold data"]
```

### 15.3 Multipart Upload Cleanup

Multipart upload bị bỏ dở (client crash giữa chừng) tốn storage phí mà không có file hoàn chỉnh. Cần lifecycle policy để cleanup:

```json
{
  "Rules": [
    {
      "ID": "abort-incomplete-multipart-uploads",
      "Status": "Enabled",
      "Filter": { "Prefix": "" },
      "AbortIncompleteMultipartUpload": {
        "DaysAfterInitiation": 3
      }
    }
  ]
}
```

## 16. Monitoring & Observability

### 16.1 Các metrics cần theo dõi

```mermaid
graph TD
    Monitoring["File Storage\nMonitoring"] --> A["Upload Metrics\n- Upload success rate\n- Upload latency (p50, p95, p99)\n- Upload error rate theo loại lỗi\n- File size distribution\n- MIME type distribution"]

    Monitoring --> B["Download Metrics\n- Download success rate\n- Signed URL expiry rate\n  (user mở link quá muộn)\n- Bandwidth usage\n- CDN cache hit rate"]

    Monitoring --> C["Processing Metrics\n- Processing queue depth\n- Processing latency\n- Processing error rate\n- Virus detection rate"]

    Monitoring --> D["Storage Metrics\n- Total storage used\n- Storage growth rate\n- Cost per GB\n- Files by status\n  (pending/active/deleted)"]

    Monitoring --> E["Security Metrics\n- 403 Forbidden rate\n- Suspicious access patterns\n- Virus quarantine count\n- Failed upload attempts"]
```

### 16.2 Alerts quan trọng cần thiết lập

| Alert                        | Condition                  | Severity    | Action               |
| ---------------------------- | -------------------------- | ----------- | -------------------- |
| Upload error rate cao        | > 5% trong 5 phút          | 🔴 Critical | Page on-call         |
| Processing queue backed up   | > 1000 messages, > 30 phút | 🔴 Critical | Scale workers        |
| Virus detected               | Bất kỳ detection nào       | 🔴 Critical | Alert security team  |
| CDN cache hit rate thấp      | < 70% trong 1 giờ          | 🟡 Warning  | Review cache headers |
| S3 bucket size tăng đột biến | > 20% trong 1 ngày         | 🟡 Warning  | Check for abuse      |
| Signed URL expiry rate cao   | > 20%                      | 🟡 Warning  | Tăng TTL             |

### 16.3 CloudWatch Dashboard cho S3

```python
# Terraform/CDK: Tạo CloudWatch dashboard
metrics_to_track = [
    # S3 Request metrics (cần enable S3 request metrics)
    "s3.GetRequests",           # Số GET requests
    "s3.PutRequests",           # Số PUT requests
    "s3.4xxErrors",             # Client errors
    "s3.5xxErrors",             # Server errors
    "s3.FirstByteLatency",      # Time to first byte (P99)
    "s3.TotalRequestLatency",   # Total latency

    # CloudFront metrics
    "cloudfront.Requests",
    "cloudfront.CacheHitRate",
    "cloudfront.BytesDownloaded",
    "cloudfront.4xxErrorRate",
    "cloudfront.5xxErrorRate",

    # Application metrics (custom)
    "app.file.upload.success",
    "app.file.upload.failure",
    "app.file.processing.duration",
    "app.file.virus.detected",
]
```

### 16.4 S3 Server Access Logging vs CloudTrail

|                 | S3 Server Access Logs                  | AWS CloudTrail                    |
| --------------- | -------------------------------------- | --------------------------------- |
| **Ghi lại**     | Mọi HTTP request đến S3                | API calls đến AWS services        |
| **Dùng cho**    | Audit ai đã access file nào, debug CDN | Audit thay đổi bucket policy, IAM |
| **Chi phí**     | Chỉ trả phí storage log                | $2/100K management events         |
| **Latency**     | Vài phút delay                         | Vài phút delay                    |
| **Khuyến nghị** | Enable cho private buckets             | Enable cho compliance             |

## 17. Disaster Recovery & Data Durability

### 17.1 S3 Data Durability

Amazon S3 Standard được thiết kế với **99.999999999% (11 nines) durability** — tức là nếu bạn lưu 10 triệu file, trung bình mất 1 file trong 10,000 năm. Điều này đạt được bằng cách lưu dữ liệu trên tối thiểu 3 Availability Zones.

Tuy nhiên, durability không đồng nghĩa với availability. Nếu cần high availability cross-region, cần cấu hình thêm.

### 17.2 Cross-Region Replication (CRR)

```mermaid
graph LR
    subgraph Primary["Primary Region\n(ap-southeast-1 Singapore)"]
        S3SG["S3 Bucket\nshopvn-product-images\n(Source)"]
    end

    subgraph DR["DR Region\n(ap-southeast-2 Sydney)"]
        S3SYD["S3 Bucket\nshopvn-product-images-dr\n(Replica)"]
    end

    S3SG -->|"Cross-Region Replication\n(CRR)\nAsync, < 15 min SLA\n$0.015/GB replicated"| S3SYD

    FailoverNote["Failover:\nJika Singapore down,\nRoute53 redirect ke\nS3 Sydney bucket"]
    S3SYD -.-> FailoverNote
```

### 17.3 Versioning — Bảo vệ khỏi xóa nhầm và ghi đè

S3 Versioning giữ lại tất cả các phiên bản của một file. Khi "xóa" file, S3 chỉ thêm **delete marker** — file thực sự vẫn tồn tại và có thể restore.

```mermaid
timeline
    title S3 Versioning — Lịch sử phiên bản file
    Day 1 : Upload ảnh v1
          : version_id = "abc1"
          : Nội dung: ảnh gốc
    Day 5 : Seller đổi ảnh (upload lại cùng key)
          : version_id = "def2"
          : v1 "abc1" vẫn còn, không bị xóa
    Day 10 : Admin xóa nhầm file
           : Delete marker được thêm
           : File "trông như đã xóa"
    Day 11 : Phát hiện xóa nhầm
           : Xóa delete marker
           : File khôi phục về v2 "def2"
```

**Lifecycle policy kết hợp versioning:**

```json
{
  "Rules": [
    {
      "ID": "expire-old-versions",
      "Status": "Enabled",
      "NoncurrentVersionExpiration": {
        "NoncurrentDays": 90
      },
      "NoncurrentVersionTransitions": [
        {
          "NoncurrentDays": 30,
          "StorageClass": "STANDARD_IA"
        }
      ]
    }
  ]
}
```

### 17.4 Recovery Time Objective (RTO) và Recovery Point Objective (RPO)

| Scenario                       | RPO                   | RTO                 | Giải pháp               |
| ------------------------------ | --------------------- | ------------------- | ----------------------- |
| File bị xóa nhầm               | 0 (versioning)        | Vài phút            | S3 Versioning + restore |
| Bucket bị xóa (operator error) | 0 - 15 phút (CRR lag) | 30 phút             | CRR + MFA Delete        |
| Region AWS bị outage           | 0 - 15 phút (CRR lag) | 30-60 phút          | CRR + Route53 failover  |
| Ransomware / Object Lock       | 0                     | N/A (không thể xóa) | S3 Object Lock WORM     |

## 18. Kết luận — Best Practices

### 18.1 Với file PUBLIC — Xử lý thế nào?

File public là ảnh sản phẩm, avatar, banner — cần serve nhanh, tối ưu bandwidth.

**Kiến trúc chuẩn:**

```
Client → CloudFront CDN → S3 (Origin Access Control)
```

**Checklist:**

- ✅ Dùng **Origin Access Control (OAC)** thay vì `public-read` bucket — bắt buộc traffic qua CDN
- ✅ Serve qua CDN với custom domain (`cdn.shopvn.vn`)
- ✅ **Content-hash trong tên file** (`main_f7a3b2c1.webp`) để cache vĩnh viễn, không cần invalidation
- ✅ `Cache-Control: public, max-age=31536000, immutable` cho fingerprinted files
- ✅ **Strip EXIF metadata** khi nhận ảnh từ người dùng (bảo vệ GPS privacy)
- ✅ **Convert sang WebP** để giảm 30-40% dung lượng
- ✅ **Resize về multiple dimensions** — không serve ảnh 4K cho thumbnail
- ✅ **Validate bằng magic bytes** — không tin vào extension hay Content-Type từ client
- ✅ **Tách upload bucket và serve bucket** — file đi qua processing pipeline trước khi serve
- ❌ Không để client gọi thẳng S3 URL (bypass CDN, tốn phí egress cao hơn)
- ❌ Không commit ảnh người dùng upload vào Git/Docker image

### 18.2 Với file PRIVATE — Xử lý thế nào?

File private là hóa đơn, KYC, báo cáo — cần bảo mật và audit trail.

**Kiến trúc chuẩn:**

```
Client → API Server (Auth + Authz) → Generate Signed URL → Client → S3 (trực tiếp)
```

**Checklist:**

- ✅ **Block All Public Access** enabled hoàn toàn trên bucket
- ✅ **Luôn kiểm tra quyền** trong application trước khi generate Signed URL
- ✅ **Thời hạn Signed URL ngắn** — 5-15 phút cho download thông thường
- ✅ **Generate Signed URL mới mỗi request** — không cache Signed URL
- ✅ **Audit log** mọi lần truy cập (user_id, file_id, timestamp, IP, action)
- ✅ **Server-Side Encryption** — SSE-KMS với Customer Managed Key cho sensitive files (KYC)
- ✅ **Virus scan** trước khi status = `active`
- ✅ **Soft delete** thay vì hard delete — giữ file theo retention policy
- ✅ **Lifecycle policy** cho temp files — auto-delete sau 7-30 ngày
- ✅ **S3 Object Lock WORM** cho tài liệu compliance (hóa đơn, KYC)
- ❌ Không log Signed URL vào application log
- ❌ Không nhúng Signed URL vào email marketing
- ❌ Không trả về Signed URL cho user chưa authenticated

### 18.3 Nếu app chạy bằng Docker — Vì sao không lưu file trong container?

```mermaid
graph TD
    subgraph Wrong["❌ SAI: Lưu file trong container"]
        App1["App Container\n/app/uploads/avatar.jpg"]
        App1 --> Restart["docker restart\nKubernetes reschedule\nDeploy version mới"]
        Restart --> Lost["💥 File mất!\nKhông thể recover"]
    end

    subgraph Right["✅ ĐÚNG: Container stateless, file ở Object Storage"]
        App2["App Container\n(Stateless, không có file)"]
        App2 -->|"Temp processing\n/tmp/ (ephemeral OK)"| Temp["/tmp/\nbuffer xử lý\nrồi xóa"]
        App2 -->|"Tất cả file\ncần lưu lâu dài"| S3["S3 Object Storage\n(Persistent, HA, scalable)"]
        S3 --> Benefit["✅ Container restart → OK\n✅ Scale 10 containers → OK\n✅ Deploy mới → OK\n✅ Kubernetes reschedule → OK"]
    end
```

**3 lý do chính không lưu file trong container:**

1. **Ephemeral nature:** Container được design để bất biến và tạm thời. Restart/redeploy xóa hết dữ liệu ghi vào container layer.

2. **Horizontal scaling:** Khi scale lên nhiều containers, file chỉ tồn tại ở container đã upload. Load balancer route request sang container khác → 404.

3. **12-Factor App principle:** Factor VI (Processes) yêu cầu processes phải stateless. Mọi dữ liệu cần persist phải lưu trong "stateful backing service" — Object Storage là backing service cho file.

**Nếu bắt buộc dùng filesystem** (ML model weights, shared config):

- Dùng **Docker Volume** mount đến Network Attached Storage (EFS trên AWS, Filestore trên GCP)
- Nhưng với file do người dùng upload → **luôn luôn dùng Object Storage**

### 18.4 Tổng kết — Decision Framework

```mermaid
flowchart TD
    Start["Cần lưu/serve file?"] --> Q1{File có thể\nthay đổi sau upload?}

    Q1 -->|Không - immutable| Fingerprint["✅ Dùng content hash\ntrong tên file\n→ Cache vĩnh viễn"]
    Q1 -->|Có - mutable| ShortCache["⚠️ Cache ngắn hơn\nhoặc dùng versioning\n+ invalidation"]

    Start --> Q2{Public hay Private?}

    Q2 -->|Public| PublicPath["Object Storage\n+ CDN\n+ Long cache\n+ WebP/optimize\n+ Strip EXIF"]

    Q2 -->|Private| PrivatePath["Object Storage\nPrivate bucket\n+ Signed URL\n+ Auth check\n+ Audit log\n+ Encryption"]

    Start --> Q3{Chạy trong\nDocker/K8s?}

    Q3 -->|Có| ContainerPath["🚫 KHÔNG lưu\ntrong container!\n→ Object Storage\ncho mọi persistent file"]

    Q3 -->|Không| ServerPath["Local filesystem\nCó thể dùng\nnhưng cân nhắc\nkhi scale sau"]

    Start --> Q4{File > 100MB?}

    Q4 -->|Có| MultipartPath["✅ Multipart Upload\n+ Resume capability"]
    Q4 -->|Không| SinglePath["Single upload\nhoặc Presigned PUT"]
```

## Tài liệu tham khảo

- [AWS S3 Developer Guide](https://docs.aws.amazon.com/s3/index.html)
- [AWS — Sharing objects with presigned URLs](https://docs.aws.amazon.com/AmazonS3/latest/userguide/ShareObjectPreSignedURL.html)
- [AWS — Using multipart upload](https://docs.aws.amazon.com/AmazonS3/latest/userguide/mpuoverview.html)
- [AWS — S3 Object Lock](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lock.html)
- [Google Cloud Storage Documentation](https://cloud.google.com/storage/docs)
- [Cloudflare R2 Documentation](https://developers.cloudflare.com/r2/)
- [MinIO Documentation](https://min.io/docs/minio/linux/index.html)
- [The Twelve-Factor App — Factor VI: Processes](https://12factor.net/processes)
- [OWASP — File Upload Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html)
- [AWS — CloudFront Origin Access Control](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/private-content-restricting-access-to-s3.html)
- [AWS — S3 Lifecycle configuration](https://docs.aws.amazon.com/AmazonS3/latest/userguide/object-lifecycle-mgmt.html)
- [AWS — S3 Cross-Region Replication](https://docs.aws.amazon.com/AmazonS3/latest/userguide/replication.html)
