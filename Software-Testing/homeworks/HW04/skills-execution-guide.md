# Skills Execution Guide

## Trước khi bắt đầu — Prerequisites

Đảm bảo các điều kiện sau đây đã sẵn sàng trước khi invoke bất kỳ skill nào:

| #   | Điều kiện           | Cách kiểm tra                                                                                                                                             |
| --- | ------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1   | SUT đang chạy       | Mở trình duyệt vào `http://localhost:5173` (web) và `http://localhost:5174` (admin)                                                                       |
| 2   | File `.env` đã tạo  | Kiểm tra `e2e/.env` tồn tại; nếu chưa có thì copy từ `e2e/.env.example` và điền `STUDENT_ID`                                                              |
| 3   | Dependencies đã cài | Chạy `cd e2e && npm install` nếu chưa cài                                                                                                                 |
| 4   | Các skill đã tải về | Kiểm tra thư mục `skills/` có đủ `wat-spec/`, `wat-design/`, `wat-build/`, `wat-report/`, `functional-test-design/`, `ai-audit-log/`, `playwright-skill/` |
| 5   | Auth state chưa có  | Thư mục `e2e/.auth/` chưa có `user.json` / `admin.json` — sẽ được tạo tự động khi chạy test lần đầu                                                       |

## Flow tổng quát

Mỗi FR đi qua đúng tuần tự sau. **Không được nhảy bước.** Hoàn thành một FR trước khi bắt đầu FR tiếp theo.

```
┌─────────────────────────────────────────────────────────────────────────┐
│  FR-01 Account Registration                                             │
│  FR-03 Forgot Password & Password Reset          (làm từng FR riêng)    │
│  FR-17 Coupon Management (CRUD)                                         │
└─────────────────────────────────────────────────────────────────────────┘

Với mỗi FR, chạy theo thứ tự:

  BƯỚC 1   /wat-spec   →  Gate #1 Review  →  APPROVED
     ↓
  BƯỚC 2   /wat-design →  Gate #2 Review  →  APPROVED
     ↓
  BƯỚC 3   /wat-build (Mode 1: Build)  →  AI self-review  →  Gate #3 Review  →  APPROVED
     ↓
  BƯỚC 4   Chạy test thực tế (npm run test:fr{xx}:debug → :chromium → full)
     ↓
  BƯỚC 5   /wat-build (Mode 3: Record)  →  Gate #4 Review  →  APPROVED
     ↓
  BƯỚC 6   /wat-report (chỉ khi có TC Fail)  →  Gate #5 Review  →  APPROVED
     ↓
  BƯỚC 7   /ai-audit-log  (human tự invoke sau mỗi phiên AI)

Nếu gate nào trả về FAILED:
  → Cung cấp feedback cụ thể cho AI
  → AI sửa lại
  → Review lại đúng gate đó (không phải gate tiếp theo)
  → Lặp đến khi APPROVED mới đi tiếp
```

## Quy tắc gate — đọc kỹ trước khi bắt đầu

Mỗi gate chỉ có **hai kết quả**:

**APPROVED** → gõ vào chat: `APPROVED` — AI sẽ tự động biết chuyển sang bước tiếp theo.

**FAILED** → gõ vào chat theo đúng format sau:

```
FAILED

Vấn đề 1: [sai ở đâu] — [sai như thế nào] — [cần sửa thành gì]
Vấn đề 2: [sai ở đâu] — [sai như thế nào] — [cần sửa thành gì]
```

> AI **không được tự chuyển bước** khi chưa có `APPROVED`. Nếu AI tự chuyển bước mà bạn chưa gõ `APPROVED`, đó là lỗi — yêu cầu AI quay lại.

## BƯỚC 1 — `/wat-spec`

### Mục đích

Phân tích một FR trong file SRS và tạo ra file spec document đầy đủ. Đây là nguyên liệu đầu vào cho bước thiết kế test case.

### Khi nào invoke

Khi bắt đầu một FR mới và chưa có file `docs/fr-{xx}/fr-{xx}-spec.md`.

### Template prompt

```
/wat-spec

FR_ID: FR-01
SRS_PATH: docs/system-requirements-specification.md
OUTPUT_DIR: docs/fr-01/
```

> Thay `FR-01` / `fr-01` bằng FR thực tế đang làm.
>
> Ví dụ cho FR-03:
>
> ```
> /wat-spec
>
> FR_ID: FR-03
> SRS_PATH: docs/system-requirements-specification.md
> OUTPUT_DIR: docs/fr-03/
> ```

### Sau khi AI hoàn thành

AI sẽ tạo file `docs/fr-{xx}/fr-{xx}-spec.md`. Mở file đó và kiểm tra theo **Gate #1 Checklist** bên dưới.

### Gate #1 — Spec Review Checklist

Đọc file spec vừa tạo và tick từng mục:

- [ ] **Feature Overview** đủ thông tin: FR ID, Feature Name, Actor, Auth Required, Entry Point (URL), Pool
- [ ] **Input Fields & Constraints** — mỗi field có hàng riêng, constraint lấy từ SRS (không phải tự bịa), các field bị thiếu hoặc không rõ phải có ghi chú ⚠️
- [ ] **Business Rules** — được đánh số `BR-01`, `BR-02`, ... mỗi rule có trích dẫn nguồn từ SRS
- [ ] **Success Paths** — viết theo dạng xen kẽ Actor/System step, có Outcome cuối
- [ ] **Failure Paths** — mỗi path có Trigger rõ ràng, Outcome là trạng thái người dùng thấy
- [ ] **Acceptance Criteria** — đúng format `Given / When / Then`, mỗi AC falsifiable (có thể xác định pass/fail)
- [ ] **Out of Scope** — có ít nhất 1 boundary rõ ràng
- [ ] **Dependencies** — liệt kê đúng FR/component phụ thuộc
- [ ] **Test Notes** — có đề cập seed data hoặc ghi "None required"
- [ ] Không có thông tin bịa đặt không có trong SRS
- [ ] Output bằng tiếng Anh

**Nếu tất cả tick** → gõ `APPROVED`

**Nếu có vấn đề** → gõ `FAILED` kèm feedback chi tiết theo format trên

## BƯỚC 2 — `/wat-design`

### Mục đích

Thiết kế toàn bộ test case cho FR bằng kỹ thuật Domain Testing (EP + BVA) và Error Guessing. Tạo ra file test case document và file data JSON.

### Khi nào invoke

Sau khi Gate #1 đã `APPROVED`.

### Template prompt

```
/wat-design

FR_ID: FR-01
SPEC_PATH: docs/fr-01/fr-01-spec.md
OUTPUT_DIR: docs/fr-01/
```

> Lưu ý: AI sẽ tự động invoke `/domain-testing` và `/error-guessing` trong quá trình chạy — bạn không cần làm gì thêm.

### Sau khi AI hoàn thành

AI sẽ tạo 2 file:

- `docs/fr-{xx}/fr-{xx}-test-cases.md` — toàn bộ analysis trail + bảng test case
- `e2e/data/fr-{xx}-data.json` — file data cho automation

Kiểm tra cả hai file theo **Gate #2 Checklist**.

### Gate #2 — Test Case Review Checklist

**Kiểm tra file `fr-{xx}-test-cases.md`:**

- [ ] **Part 1 — Analysis Trail** có đủ: EP tables cho từng field có constraint, BVA table cho từng field có min/max, Error Guessing catalogue với Applied/N/A rõ ràng
- [ ] **EP tables** — mỗi field có đủ Valid và Invalid partitions, mỗi partition có representative value
- [ ] **Combination rule** — các valid class được combine lại, không test từng valid class riêng lẻ (tránh test case thừa)
- [ ] **Isolation rule** — mỗi invalid class có TC riêng, các field khác trong TC đó đều dùng valid value
- [ ] **Tổng số TC** — ít nhất 12 TC, nên có 15–18 để đủ coverage
- [ ] **Title của TC** đúng format: `Action + Function + Operating Condition`
  - ✅ Đúng: `Register account with duplicate email address`
  - ❌ Sai: `Test registration`, `Valid case 1`
- [ ] **Expected Result** đủ cụ thể để viết assertion — không được mơ hồ như "error is shown"
  - ✅ Đúng: `Field-level error 'Email already registered' appears on the Email field`
  - ❌ Sai: `An error appears`
- [ ] **Test Steps** dùng `<br>` để xuống dòng (không phải `\n` hay Markdown list)
- [ ] **Input Data** mỗi TC đều có `→ ref: fr-{xx}-data.json#{TC-ID}` (không hardcode inline)
- [ ] **Actual Result** và **Status** để trống (chưa chạy)

**Kiểm tra file `fr-{xx}-data.json`:**

- [ ] Số lượng entry khớp với số TC trong file test-cases
- [ ] Mỗi `tc_id` trong JSON khớp chính xác với TC-ID trong bảng TC
- [ ] `expected.message` trong JSON khớp với Expected Result trong TC table
- [ ] Không có TC-ID nào trong bảng mà thiếu entry trong JSON

**Nếu tất cả tick** → gõ `APPROVED`

**Nếu có vấn đề** → gõ `FAILED` kèm feedback

## BƯỚC 3 — `/wat-build` (Mode 1: Build)

### Mục đích

Tạo automation script Playwright cho FR: Page Object Model, fixture (nếu cần), và spec file.

### Khi nào invoke

Sau khi Gate #2 đã APPROVED.

### Lưu ý quan trọng trước khi invoke

**SUT phải đang chạy.** AI sẽ tự động dispatch subagent `ui-explorer` để mở trình duyệt, navigate đến các screen, và capture accessibility tree. Nếu SUT không chạy, subagent sẽ báo lỗi ngay.

### Template prompt

```
/wat-build

FR_ID: FR-01
TC_PATH: docs/fr-01/fr-01-test-cases.md
E2E_DIR: e2e/
Mode: Build
```

> Bạn không cần chỉ định Mode — AI sẽ tự detect là Mode 1 (Build) khi không có script nào tồn tại và không có feedback được cung cấp.

### Những gì AI sẽ làm (theo thứ tự)

1. Đọc TC document và data JSON
2. Dispatch `ui-explorer` subagent để khám phá UI thực tế của SUT
3. Đọc các Playwright guides liên quan
4. Tạo/cập nhật POM file (`*.page.ts`)
5. Tạo fixture file (`*.fixture.ts`) nếu cần
6. Viết spec file (`fr-{xx}-*.spec.ts`)
7. Tự review theo Code Review Checklist
8. Xuất các file và báo cáo những gì đã tạo

### Sau khi AI hoàn thành

AI sẽ tạo/cập nhật các file trong `e2e/`. Kiểm tra theo **Gate #3 Checklist**.

### Gate #3 — Script Review Checklist

**Kiểm tra POM file (`*.page.ts`):**

- [ ] Class extend `BasePage` từ `pages/base.page.ts`
- [ ] Locator getters dùng tên danh từ: `emailInput`, `submitButton`, `errorMessage`
- [ ] Locator dùng đúng priority: `getByRole` > `getByLabel` > `getByTestId` > `getByText` > CSS
- [ ] **Không có assertion nào** bên trong POM (không có `expect()` trong POM)
- [ ] Action methods dùng tên động từ: `fillForm()`, `submit()`, `clickAddToCart()`
- [ ] Không có CSS class selector (`.btn-primary`, `.error-msg`) — chỉ dùng role/label/testId

**Kiểm tra spec file (`*.spec.ts`):**

- [ ] Import `{ test, expect }` từ `@fixtures/base.fixture` (không phải từ `@playwright/test`)
- [ ] Import `{ loadTestData }` từ `@helpers/data-loader`
- [ ] Nếu FR không cần auth (như FR-01, FR-03): có `test.use({ storageState: { cookies: [], origins: [] } })` ở đầu file
- [ ] Có `test.describe('FR-{XX}: {Feature Name}', ...)` bao quanh toàn bộ tests
- [ ] Dùng `for...of` loop để iterate qua data — **không có** `test.each()`
- [ ] Tên mỗi test có TC-ID prefix: `TC-FR01-001: Register account with valid data`
- [ ] Có ít nhất **3 loại assertion pattern khác nhau** trong toàn bộ spec file
  - Ví dụ: `toHaveURL`, `toContainText`, `toBeVisible`, `toHaveValue`, ...
- [ ] Không có `waitForTimeout()` — chỉ dùng `expect(locator).toBeVisible()` hoặc `waitFor()`
- [ ] Không có giá trị hardcode inline — tất cả dữ liệu đọc từ `loadTestData()`
- [ ] Mỗi test độc lập — không phụ thuộc vào test trước đã chạy

**Nếu tất cả tick** → gõ `APPROVED`

**Nếu có vấn đề** → gõ `FAILED` kèm feedback

> Sau `APPROVED` ở Gate #3, chuyển sang Bước 4 để chạy test.

### Vòng lặp Fix (nếu Gate #3 FAILED)

```
/wat-build

FR_ID: FR-01
TC_PATH: docs/fr-01/fr-01-test-cases.md
E2E_DIR: e2e/
Mode: Fix

Feedback:
- [File] e2e/pages/web/register.page.ts, dòng 12: selector dùng CSS class `.submit-btn` — sửa thành getByRole('button', { name: /register/i })
- [File] e2e/tests/web/fr-01-registration.spec.ts, dòng 45: import từ @playwright/test trực tiếp — sửa thành @fixtures/base.fixture
```

> Sau khi AI fix xong, review lại đúng Gate #3 — không phải Gate #4.

## BƯỚC 4 — Chạy test thực tế

> Tất cả lệnh bên dưới chạy từ thư mục `e2e/` — nhớ `cd e2e` trước.

### Bước 4a — Xem script chạy trên UI (debug mode)

Dùng trước tiên để quan sát từng bước diễn ra trên màn hình, phát hiện selector sai hoặc flow không đúng.

```bash
npm run test:fr01:debug   # FR-01: mở browser, chạy tuần tự, dừng ngay khi fail
npm run test:fr03:debug   # FR-03
npm run test:fr17:debug   # FR-17
```

Ba flag được kích hoạt tự động: `--headed` (mở browser thật), `--workers=1` (chạy tuần tự từng test), `--retries=0` (không retry — dừng ngay khi fail để thấy lỗi rõ hơn).

Nếu muốn **dừng tay tại từng dòng code** và step qua từng action — dùng lệnh sau (chạy trực tiếp, không qua npm scripts):

```bash
# Mở Playwright Inspector cho FR-01
npx playwright test tests/web/fr-01-registration.spec.ts --project=web-chromium --debug

# Chỉ debug 1 TC cụ thể (thay TC-FR01-001 bằng TC-ID muốn kiểm tra)
npx playwright test tests/web/fr-01-registration.spec.ts --project=web-chromium --debug --grep "TC-FR01-001"
```

**Script crash (lỗi TypeScript, selector không tìm thấy, import lỗi)** → Copy thông báo lỗi → gửi cho AI theo format Mode 2 (Fix) ở trên → re-run → tiếp tục

### Bước 4b — Chạy nhanh 1 browser để kiểm tra kết quả (development)

Khi script đã không còn crash ở debug mode, chạy headless để kiểm tra pass/fail nhanh hơn — không cần đợi 3 browsers.

```bash
npm run test:fr01:chromium   # FR-01, Chromium only
npm run test:fr03:chromium   # FR-03, Chromium only
npm run test:fr17:chromium   # FR-17, Chromium only (admin panel)
```

### Bước 4c — Chạy full 3 browsers để lấy evidence (bắt buộc trước khi nộp)

Khi kết quả đã ổn định ở bước 4b, chạy đủ 3 browsers để tạo HTML report có đủ browser run evidence.

```bash
npm run test:fr01   # FR-01: Chromium + Firefox + WebKit
npm run test:fr03   # FR-03: Chromium + Firefox + WebKit
npm run test:fr17   # FR-17: Chromium + Firefox + WebKit (admin)

npm test            # Chạy toàn bộ suite — tất cả FR, tất cả browser cùng lúc
```

### Bước 4d — Xem HTML report

```bash
npm run report
```

Report mở trong browser. **Verify thấy `"Run by: {StudentID}"`** trong phần header của report — đây là yêu cầu bắt buộc khi nộp bài.

### Thu thập kết quả

Từ HTML report, ghi lại cho mỗi TC:

- TC-ID
- Actual Result — copy từ error message, hoặc mô tả lại những gì thực sự xảy ra trên UI
- Status: Pass / Fail

## BƯỚC 5 — `/wat-build` (Mode 3: Record)

### Mục đích

Điền Actual Result và Status vào file test case document dựa trên kết quả chạy thực tế.

### Template prompt

```
/wat-build

FR_ID: FR-01
TC_PATH: docs/fr-01/fr-01-test-cases.md
E2E_DIR: e2e/
Mode: Record

Kết quả thực thi:
TC-FR01-001: PASS — Redirected to /login. Toast displayed: "Registration successful. Please verify your email."
TC-FR01-002: PASS — Accepted 2-character name, registration completed.
TC-FR01-003: PASS — Accepted 8-character password, registration completed.
TC-FR01-004: PASS — Field error shown: "This email address is already registered."
TC-FR01-005: PASS — Field error shown: "Please enter a valid email address."
TC-FR01-006: FAIL — No error displayed on password field. Page stayed on /register but gave no feedback. Server returned 422.
TC-FR01-007: PASS — Field error shown: "Password must contain at least one digit."
TC-FR01-008: FAIL — No error displayed on Confirm Password field. Browser console showed HTTP 422 from POST /api/register.
...
```

> Ghi đúng những gì **thực sự xảy ra** — không phải "same as expected". Với các TC Pass, mô tả ngắn gọn những gì thấy trên UI. Với TC Fail, mô tả chi tiết hành vi sai, kèm HTTP status hoặc console log nếu có.

### Sau khi AI hoàn thành

AI sẽ cập nhật cột `Actual Result` và `Status` trong file `docs/fr-{xx}/fr-{xx}-test-cases.md`. Kiểm tra theo **Gate #4 Checklist**.

### Gate #4 — Actual Results Review Checklist

- [ ] Mỗi TC-ID trong bảng đã có `Actual Result` — không còn ô trống
- [ ] `Actual Result` mô tả điều **thực sự xảy ra**, không phải "same as expected" hay "works correctly"
- [ ] `Status` là `Pass` hoặc `Fail` (không có giá trị khác)
- [ ] TC nào Fail: `Actual Result` có đủ thông tin để phân tích (ví dụ: HTTP status, message text, missing element)
- [ ] Không có TC Pass nào bị đánh Fail và ngược lại (double-check với kết quả Playwright report)

**Nếu tất cả tick** → gõ `APPROVED`

**Sau APPROVED:** Kiểm tra xem có TC nào có `Status = Fail` không.

- **Không có Fail** → workflow cho FR này kết thúc. Chuyển sang Bước 7 (`/ai-audit-log`).
- **Có ít nhất 1 Fail** → chuyển sang Bước 6 (`/wat-report`).

## BƯỚC 6 — `/wat-report`

### Mục đích

Group các TC Fail theo root cause và tạo bug report chuẩn production. Sau đó human tạo GitHub Issues thủ công.

### Khi nào invoke

Chỉ khi có ít nhất 1 TC có `Status = Fail` sau Gate #4.

### Template prompt

```
/wat-report

FR_ID: FR-01
TC_PATH: docs/fr-01/fr-01-test-cases.md
```

> AI sẽ tự đọc file, lọc TC Fail, phân tích root cause, group lại và viết bug report.

### Sau khi AI hoàn thành

AI sẽ tạo file `docs/fr-{xx}/fr-{xx}-bug-report.md`. Kiểm tra theo **Gate #5 Checklist**.

### Gate #5 — Bug Report Review Checklist

- [ ] **Grouping đúng** — các TC Fail cùng root cause được group vào 1 bug entry, không phải 1 TC = 1 bug
- [ ] Mỗi bug entry có `Bug ID` format `BUG-FR{XX}-{NNN}`
- [ ] **Title** theo format `Action + Function + Condition` mô tả defect (không phải symptom)
  - ✅ Đúng: `Registration form fails to display error when server returns HTTP 422`
  - ❌ Sai: `Bug in registration`, `Error message missing`
- [ ] **Root Cause** là câu phân tích kỹ thuật cụ thể (không phải "something is wrong")
- [ ] `Affects TCs` liệt kê đủ tất cả TC-ID được group vào bug này
- [ ] **Severity** và **Priority** mỗi cái đều có cột `Reason` giải thích
- [ ] **Environment section** đủ: Browser, OS, URL, SUT Version
- [ ] **Steps to Reproduce** là danh sách numbered steps có thể reproduce được
- [ ] **Expected Result** khớp chính xác với cột Expected Result trong TC document
- [ ] **Actual Result** khớp chính xác với cột Actual Result trong TC document
- [ ] **Evidence** có ít nhất 1 item (path tới screenshot hoặc report)
- [ ] **GitHub Issue** field có giá trị (điền `— to be created` nếu chưa tạo)
- [ ] Không có TC Pass nào xuất hiện trong report
- [ ] Tất cả TC Fail đều được accounted for (hoặc là primary TC hoặc trong `Affects TCs`)

**Nếu tất cả tick** → gõ `APPROVED`

**Sau APPROVED:** Tạo GitHub Issues thủ công:

1. Mở trang GitHub Issues của repo
2. Tạo Issue mới cho mỗi bug entry trong report
3. Copy nội dung từ bug report vào Issue
4. Đính kèm screenshot từ Playwright report
5. Điền link GitHub Issue vào trường `GitHub Issue` trong file bug report

## BƯỚC 7 — `/ai-audit-log`

### Mục đích

Log lại toàn bộ AI interaction trong phiên làm việc vừa rồi để tạo AI Audit Report theo yêu cầu.

### Khi nào invoke

Sau mỗi phiên làm việc với AI (sau mỗi `/wat-spec`, `/wat-design`, `/wat-build`, `/wat-report`). **Human tự invoke — AI không tự động làm.**

### Template prompt

```
/ai-audit-log
```

> Skill sẽ tự đọc lịch sử conversation và log ra file `docs/audit/ai/<fullname>-YYYY-MM.log.md`.

## Tóm tắt nhanh — Quick Reference

| Bước | Skill / Action                                   | Input chính                        | Output                                | Gate    |
| ---- | ------------------------------------------------ | ---------------------------------- | ------------------------------------- | ------- |
| 1    | `/wat-spec`                                      | `FR_ID`, `SRS_PATH`, `OUTPUT_DIR`  | `fr-{xx}-spec.md`                     | Gate #1 |
| 2    | `/wat-design`                                    | `FR_ID`, `SPEC_PATH`, `OUTPUT_DIR` | `fr-{xx}-test-cases.md` + `data.json` | Gate #2 |
| 3    | `/wat-build` Build                               | `FR_ID`, `TC_PATH`, `E2E_DIR`      | `*.page.ts` + `*.spec.ts`             | Gate #3 |
| 4    | `npm run test:fr{xx}:debug` → `:chromium` → full | —                                  | HTML report + kết quả                 | —       |
| 5    | `/wat-build` Record                              | kết quả thực tế                    | cập nhật `fr-{xx}-test-cases.md`      | Gate #4 |
| 6    | `/wat-report` _(nếu có Fail)_                    | `FR_ID`, `TC_PATH`                 | `fr-{xx}-bug-report.md`               | Gate #5 |
| 7    | `/ai-audit-log`                                  | —                                  | log file                              | —       |

### Lệnh chạy test nhanh

```bash
cd e2e

# ── Debug mode (xem trên UI, tuần tự) ────────────────────
npm run test:fr01:debug      # FR-01 headed + sequential + no retry
npm run test:fr03:debug      # FR-03
npm run test:fr17:debug      # FR-17

# ── 1 browser headless (development) ─────────────────────
npm run test:fr01:chromium   # FR-01, Chromium only
npm run test:fr03:chromium   # FR-03, Chromium only
npm run test:fr17:chromium   # FR-17, Chromium only

# ── Full 3 browsers (evidence for submission) ─────────────
npm run test:fr01            # FR-01: Chromium + Firefox + WebKit
npm run test:fr03            # FR-03: Chromium + Firefox + WebKit
npm run test:fr17            # FR-17: Chromium + Firefox + WebKit

npm test                     # Toàn bộ suite

# ── Report ───────────────────────────────────────────────
npm run report               # Mở HTML report trong browser
```

### Feedback format khi FAILED

```
FAILED

Vấn đề 1: [tên file, dòng số hoặc section] — [mô tả sai] — [cần sửa thành gì]
Vấn đề 2: [tên file, dòng số hoặc section] — [mô tả sai] — [cần sửa thành gì]
```
