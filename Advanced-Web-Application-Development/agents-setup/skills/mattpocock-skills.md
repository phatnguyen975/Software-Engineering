# Matt Pocock Skills — Comprehensive Reference Guide

> **Source:** [mattpocock/skills](https://github.com/mattpocock/skills)  
> **Web UI:** [aihero.dev/skills](https://www.aihero.dev/skills)

## 1. Overview & Philosophy

`mattpocock/skills` là một bộ skill dành cho agentic coding agents (Claude Code, Codex, và các agent khác), được thiết kế để giải quyết 4 failure mode phổ biến nhất khi phát triển phần mềm với AI:

| Failure Mode                      | Vấn đề                               | Skill giải quyết                             |
| --------------------------------- | ------------------------------------ | -------------------------------------------- |
| **#1 Agent không làm đúng ý**     | Communication gap giữa user và agent | `/grill-me`, `/grill-with-docs`              |
| **#2 Agent quá verbose**          | Thiếu shared language cho project    | `/grill-with-docs` → `CONTEXT.md`            |
| **#3 Code không hoạt động**       | Thiếu feedback loops, thiếu tests    | `/tdd`, `/diagnosing-bugs`                   |
| **#4 Codebase thành ball of mud** | Entropy tích lũy nhanh với AI        | `/to-spec`, `/improve-codebase-architecture` |

**Triết lý cốt lõi:** Skills này _nhỏ, dễ thích nghi, và composable_. Không giống GSD hay BMAD cố gắng "own the process", bộ này chỉ đóng gói các software engineering fundamentals vào các practice có thể lặp lại.

## 2. Cài đặt

### Phương pháp 1: Claude Code Plugin (Recommended — read-only, tự cập nhật)

```bash
# Bên ngoài session
claude plugins install mattpocock-skills

# Hoặc bên trong session
/plugin install mattpocock-skills
```

### Phương pháp 2: skills.sh (editable — bạn sở hữu file)

```bash
npx skills@latest add mattpocock/skills
```

Cách này copy skill files vào repo của bạn. Bạn có thể chỉnh sửa thoải mái. Update thủ công bằng `npx skills update`.

### Bước 3: Setup một lần per repo (BẮT BUỘC)

Sau khi cài, **phải chạy ngay**:

```
/setup-matt-pocock-skills
```

Skill này sẽ hỏi:

- Issue tracker bạn dùng (GitHub Issues, Linear, hay local files)
- Labels nào bạn dùng khi triage tickets
- Thư mục lưu docs

Các skill khác phụ thuộc vào config này. Nếu bỏ qua, `/to-spec`, `/to-tickets`, `/code-review`, v.v. sẽ báo lỗi.

## 3. Phân loại Skills: User-invoked vs Model-invoked

Đây là phân biệt **quan trọng nhất** cần nắm:

| Loại              | Ai invoke                     | Chức năng                                      |
| ----------------- | ----------------------------- | ---------------------------------------------- |
| **User-invoked**  | Chỉ bạn gõ (e.g. `/grill-me`) | Orchestrate — điều phối workflow lớn           |
| **Model-invoked** | Bạn hoặc agent tự tự động gọi | Hold reusable discipline — chứa logic chi tiết |

**Quy tắc:** User-invoked skill có thể gọi model-invoked skill, nhưng KHÔNG được gọi user-invoked skill khác.

## 4. Engineering Skills — Chi tiết từng Skill

### 4.1 `/setup-matt-pocock-skills`

- **Loại:** User-invoked
- **Khi nào dùng:** Một lần duy nhất khi bắt đầu dùng bộ skills trên một repo mới
- **Chức năng:** Configure toàn bộ hạ tầng — issue tracker, triage labels, doc location
- **Output:** Tạo file config trong `docs/agents/` để các skill khác đọc
- **Lưu ý:** Không bỏ qua bước này, dù chỉ muốn dùng 1-2 skill đơn lẻ

### 4.2 `/ask-matt`

- **Loại:** User-invoked
- **Khi nào dùng:** Khi không biết nên dùng skill nào cho tình huống hiện tại
- **Chức năng:** Router — phân tích tình huống và gợi ý skill phù hợp nhất
- **Lưu ý:** Điểm khởi đầu tốt nếu bạn còn chưa quen bộ skills này

### 4.3 `/grill-with-docs` _(Skill quan trọng nhất)_

- **Loại:** User-invoked
- **Flag:** `disable-model-invocation: true` (chỉ user gọi)
- **Khi nào dùng:** **MỌI LÚC** trước khi bắt đầu một thay đổi đáng kể
- **Chức năng:** Combination của grilling session + domain modeling
  - Hỏi bạn chi tiết về những gì bạn muốn build (từ `grilling` skill)
  - Đồng thời xây dựng shared language trong `CONTEXT.md`
  - Ghi lại các architectural decisions vào ADRs
- **Invokes internally:** `/grilling` + `/domain-modeling`
- **Output:** Hiểu rõ hơn về yêu cầu + `CONTEXT.md` được cập nhật + ADR files
- **Tại sao quan trọng:** Matt gọi đây là "the single coolest technique in this repo". Shared language từ `CONTEXT.md` mang lại:
  - Variables, functions, files được đặt tên nhất quán
  - Agent tốn ít tokens hơn để suy nghĩ
  - Codebase dễ navigate hơn

### 4.4 `/triage`

- **Loại:** User-invoked
- **Khi nào dùng:** Khi muốn phân loại và organize issues trong issue tracker
- **Chức năng:** Move issues qua state machine của triage roles
- **Prerequisite:** `/setup-matt-pocock-skills` đã chạy (cần label vocabulary)

### 4.5 `/to-spec`

- **Loại:** User-invoked
- **Flag:** `disable-model-invocation: true`
- **Khi nào dùng:** Sau khi đã thảo luận về một feature/change trong conversation, muốn formalize thành spec
- **Chức năng:** Synthesize những gì đã thảo luận → tạo PRD (Product Requirements Document) → publish lên issue tracker
- **KHÔNG làm:** Không phỏng vấn lại user — chỉ tổng hợp những gì đã biết
- **Process:**
  1. Explore repo để hiểu state hiện tại
  2. Sketch ra các seams sẽ test tại đó
  3. Confirm seams với user
  4. Viết spec theo template + publish lên issue tracker với label `ready-for-agent`
- **Template sections:** Problem Statement → Solution → User Stories → Implementation Decisions → Testing Decisions → Out of Scope → Further Notes
- **Lưu ý:** Không bao gồm file paths hay code snippets cụ thể trong spec (chúng stale nhanh)

### 4.6 `/to-tickets`

- **Loại:** User-invoked
- **Flag:** `disable-model-invocation: true`
- **Khi nào dùng:** Sau khi có spec/plan, muốn break down thành actionable tickets
- **Chức năng:** Biến plan/spec/conversation → set of **tracer-bullet tickets** với blocking edges rõ ràng
- **Process:**
  1. Gather context từ conversation hoặc spec file
  2. Explore codebase (optional)
  3. Draft vertical slices — mỗi slice phải complete (schema + API + UI + tests)
  4. Quiz user về granularity và blocking edges
  5. Publish tickets lên configured tracker
- **Nguyên tắc cốt lõi:** Mỗi ticket phải fit trong **một context window** và là **vertical slice** (không horizontal)
- **Exception:** Wide refactors dùng **expand-contract** pattern thay vì vertical slices
- **Sau đó:** Dùng `/implement` để giải quyết từng ticket theo frontier (ticket không có blocker đã done)

### 4.7 `/implement`

- **Loại:** User-invoked
- **Flag:** `disable-model-invocation: true`
- **Khi nào dùng:** Khi đã có spec hoặc set of tickets, muốn agent thực sự build code
- **Chức năng:** Implement work từ PRD hoặc issues
- **Process:**
  1. Implement code theo spec/tickets
  2. Dùng `/tdd` tại các pre-agreed seams
  3. Chạy typechecking thường xuyên, single test files thường xuyên, full test suite ở cuối
  4. Sau khi xong, chạy `/code-review`
  5. Commit lên current branch
- **Lưu ý:** Không được implement trước khi đã chạy `/grill-with-docs` và `/to-spec`/`/to-tickets`

### 4.8 `/wayfinder`

- **Loại:** User-invoked
- **Khi nào dùng:** Khi có một chunk of work quá lớn để fit trong một agent session
- **Chức năng:** Lập kế hoạch cho large-scale work bằng cách tạo "decision tickets" trên issue tracker, giải quyết từng cái cho đến khi đường đi rõ ràng
- **Dùng khi:** Scope của feature/refactor lớn hơn một context window

### 4.9 `/improve-codebase-architecture`

- **Loại:** User-invoked
- **Khi nào dùng:** Định kỳ (vài ngày một lần) để kiểm tra codebase health; sau khi fix bug phức tạp
- **Chức năng:** Scan codebase tìm "deepening opportunities" — refactors biến shallow modules thành deep modules
- **KHÔNG phải rescue operation:** Đây là survey, không untangle codebase cũ cho bạn
- **Process:**
  1. Đọc `CONTEXT.md` và ADRs
  2. Explore codebase tìm friction
  3. Tạo HTML report với before/after diagrams cho từng candidate
  4. Grilling loop: user chọn candidate, agent walk through design tree
- **Terminology quan trọng:**
  - **Module** = anything với interface + implementation
  - **Deep** = nhiều behavior ẩn sau interface nhỏ (tốt)
  - **Shallow** = interface gần phức tạp bằng implementation (xấu)
  - **Seam** = nơi interface exists; chỗ có thể alter behavior
  - **Deletion test** = xóa module đi → nếu complexity vanishes, nó là pass-through
- **Cập nhật:** Cập nhật `CONTEXT.md` inline khi naming mới; offer ADR nếu cần

## 5. Engineering Skills — Model-invoked (Bổ trợ)

### 5.1 `grilling` _(Core primitive — quan trọng nhất trong model-invoked)_

- **Loại:** Model-invoked
- **Được gọi bởi:** `/grill-me`, `/grill-with-docs`, `/triage`, `/wayfinder`, `/improve-codebase-architecture`
- **Chức năng:** Interview user relentlessly về plan/decision/idea đến khi mọi branch của design tree được resolve
- **Cơ chế hoạt động:**
  1. Map plan như một **design tree** — mỗi decision branch thành các decision con
  2. Làm việc theo **rounds** — mỗi round hỏi toàn bộ frontier (các câu hỏi không phụ thuộc nhau)
  3. Mỗi câu hỏi có format:
     ```
     ❓ **Q1** - **<question title>**: <question body>
     ➡️ <recommended answer>
     ```
  4. User trả lời → mở ra frontier mới → hỏi round tiếp theo
- **Quy tắc:** Finding facts là job của agent (dùng sub-agent), không hỏi user những gì tự tra được
- **Kết thúc:** Khi frontier empty — không còn gì silently assumed

### 5.2 `domain-modeling`

- **Loại:** Model-invoked
- **Được gọi bởi:** `/grill-with-docs`, `/improve-codebase-architecture`
- **Chức năng:** Xây dựng và sharpen project's domain model — CONTEXT.md + ADRs
- **Phân biệt với reading CONTEXT.md:** Skill này dùng khi bạn **thay đổi** model, không chỉ consume nó
- **File structure:**
  ```
  /
  ├── CONTEXT.md          ← Glossary duy nhất (chỉ domain terms, không có implementation details)
  └── docs/adr/           ← Architectural Decision Records
  ```
  Nếu có nhiều bounded contexts: `CONTEXT-MAP.md` ở root chỉ đến từng `CONTEXT.md`
- **Khi nào update CONTEXT.md:** Ngay khi term được resolve — không batch lại sau
- **Khi nào tạo ADR:** Chỉ khi đủ cả 3: hard to reverse + surprising without context + result of real trade-off
- **Khi nào KHÔNG tạo ADR:** Quyết định ephemeral hoặc self-evident

### 5.3 `tdd`

- **Loại:** Model-invoked
- **Được gọi bởi:** `/implement`
- **Khi nào dùng:** Muốn build features hoặc fix bugs test-first; red-green-refactor
- **Chức năng:** Reference discipline cho test-driven development
- **Good test là gì:**
  - Test behavior qua public interfaces, KHÔNG test implementation details
  - Code có thể thay đổi hoàn toàn; tests không nên thay đổi
  - Đọc như specification: `"user can checkout with valid cart"`
- **Seams:** Test chỉ tại pre-agreed seams (public boundary). Phải confirm seams với user trước khi viết test
- **Anti-patterns:**
  - **Implementation-coupled** — mock internal collaborators, test private methods
  - **Tautological** — assertion recompute expected value theo cách code làm (e.g. `expect(add(a,b)).toBe(a+b)`)
  - **Horizontal slicing** — viết all tests trước, rồi all implementation
- **Rules của loop:**
  - Red trước Green — write failing test first
  - One slice at a time
  - Refactoring KHÔNG phải phần của loop (thuộc về `/code-review`)

### 5.4 `code-review`

- **Loại:** Model-invoked
- **Được gọi bởi:** `/implement`
- **Chức năng:** Two-axis review — Standards + Spec — chạy parallel sub-agents
- **Hai axes:**
  - **Standards:** Có follow coding standards của repo không? (+ Fowler smell baseline)
  - **Spec:** Code có faithfully implement originating issue/PRD không?
- **Tại sao hai axes:** Code có thể pass Standards mà fail Spec (implement sai yêu cầu), hoặc pass Spec mà fail Standards (implement đúng nhưng messy code)
- **Fowler smell baseline (luôn check, dù repo không document):**
  - Mysterious Name, Duplicated Code, Feature Envy, Data Clumps
  - Primitive Obsession, Repeated Switches, Shotgun Surgery
  - Divergent Change, Speculative Generality, Message Chains
  - Middle Man, Refused Bequest
- **Process:**
  1. Pin fixed point (commit SHA, branch name, `main`, etc.)
  2. Identify spec source (từ commit messages → argument → docs/ → hỏi user)
  3. Identify standards sources (CODING_STANDARDS.md, CONTRIBUTING.md)
  4. Spawn hai sub-agents **parallel** (không sequential để không pollute nhau)
  5. Aggregate báo cáo dưới `## Standards` và `## Spec` headings

### 5.5 `diagnosing-bugs`

- **Loại:** Model-invoked
- **Trigger:** User nói "diagnose", "debug this", hoặc report something broken/failing/slow
- **Chức năng:** Disciplined diagnosis loop, 6 phases
- **Phase 1 — Build a feedback loop (QUAN TRỌNG NHẤT):**
  - Phải có tight pass/fail signal cho bug trước khi làm gì khác
  - 10 cách xây feedback loop (theo thứ tự ưu tiên): failing test → curl → CLI → headless browser → replay trace → throwaway harness → property fuzz → bisection → differential → HITL script
  - Completion criterion: **một lệnh duy nhất** mà đã chạy và output có thể share
  - **Không được nhảy sang Phase 2 nếu chưa có red-capable command**
- **Phase 2 — Reproduce + Minimise:** Shrink repro đến scenario nhỏ nhất vẫn red
- **Phase 3 — Hypothesise:** Generate 3-5 ranked hypotheses. Mỗi hypothesis phải falsifiable
- **Phase 4 — Instrument:** Probe theo specific predictions từ Phase 3. Một variable at a time. Tag debug logs với prefix unique (e.g. `[DEBUG-a4f2]`)
- **Phase 5 — Fix + regression test:** Viết regression test trước fix (nếu có correct seam). Red → fix → green → re-run Phase 1 loop
- **Phase 6 — Cleanup:** Xóa debug instrumentation, document hypothesis đúng trong commit message

### 5.6 `research`

- **Loại:** Model-invoked
- **Chức năng:** Investigate một câu hỏi dựa trên high-trust primary sources, lưu findings vào cited Markdown file trong repo
- **Được chạy như:** Background agent
- **Khi nào dùng:** Cần tìm hiểu về một library, approach, hay topic trước khi implement

### 5.7 `prototype`

- **Loại:** Model-invoked
- **Chức năng:** Build throwaway prototype để trả lời một design question
  - Single shareable HTML file cho state/logic questions
  - Multiple UI variations toggleable từ một route
- **Khi nào dùng:** Khi cần explore một approach trước khi commit vào implementation

### 5.8 `codebase-design`

- **Loại:** Model-invoked
- **Chức năng:** Shared discipline và vocabulary cho designing deep modules
- **Nguyên tắc:** Nhiều behavior ẩn sau small interface, đặt tại clean seam, testable qua interface đó

### 5.9 `resolving-merge-conflicts`

- **Loại:** Model-invoked
- **Chức năng:** Work through git merge/rebase conflicts hunk by hunk, resolve by intent traced to each side's primary source
- **Quy tắc:** Luôn finish operation — không bao giờ `--abort`

### 5.10 `wizard`

- **Loại:** Model-invoked
- **Chức năng:** Generate interactive bash wizard để hướng dẫn human qua các steps chỉ human có thể làm:
  - Provisioning infrastructure
  - Setup credentials/CI secrets
  - Navigating third-party dashboard
  - Running one-off migration

## 6. Productivity Skills

### 6.1 `/grill-me` _(Cho non-code use cases)_

- **Loại:** User-invoked
- **Flag:** `disable-model-invocation: true`
- **Khi nào dùng:** Muốn stress-test thinking về bất kỳ plan hay design nào — không nhất thiết là code
- **Chức năng:** Gọi `grilling` skill
- **Phân biệt với `/grill-with-docs`:** Không create CONTEXT.md hay ADRs — dùng cho non-engineering contexts

### 6.2 `/handoff`

- **Loại:** User-invoked
- **Khi nào dùng:** Khi cần chuyển giao work cho một agent khác (context window đầy, đổi session)
- **Chức năng:** Compact conversation hiện tại → handoff document để agent khác tiếp tục

### 6.3 `/teach`

- **Loại:** User-invoked
- **Khi nào dùng:** Muốn học một skill hoặc concept mới qua nhiều sessions
- **Chức năng:** Stateful teaching workspace trong current directory, dạy qua nhiều sessions

### 6.4 `/to-questionnaire`

- **Loại:** User-invoked
- **Khi nào dùng:** Có một decision cần input từ người khác (không thể tự trả lời)
- **Chức năng:** Biến decision → Markdown questionnaire để gửi cho người có thể trả lời, fill async hoặc qua meeting
- **Cơ chế:** Grill bạn về người nhận và cần lấy gì — không grill về subject của questionnaire

### 6.5 `/wait-what`

- **Loại:** User-invoked
- **Khi nào dùng:** Ngay khi một message của agent không hiểu được
- **Chức năng:** Agent re-pitch lại message với context bị thiếu, dùng `CONTEXT.md` vocabulary của project, bằng plain English

### 6.6 `writing-for-agents`

- **Loại:** Model-invoked
- **Chức năng:** Discipline cho viết documents dành cho agents: skills, AGENTS.md/CLAUDE.md, và các doc agent sẽ đọc theo pointer

## 7. Workflow Tổng Thể: Từ Ý Tưởng đến Code

```
┌────────────────────────────────────────────────────────┐
│ PHASE 1: UNDERSTANDING (trước khi viết một dòng code)  │
│                                                        │
│  /grill-with-docs  →  CONTEXT.md cập nhật              │
│  (Nếu không có code: /grill-me)                        │
└────────────────────────────────────────────────────────┘
                           ↓
┌────────────────────────────────────────────────────────┐
│ PHASE 2: PLANNING (formalize yêu cầu)                  │
│                                                        │
│  /to-spec  →  Spec/PRD trên issue tracker              │
│                    ↓                                   │
│  /to-tickets  →  Tracer-bullet tickets với blockers    │
└────────────────────────────────────────────────────────┘
                           ↓
┌────────────────────────────────────────────────────────┐
│ PHASE 3: IMPLEMENTATION (build theo frontier)          │
│                                                        │
│  /implement  →  TDD tại pre-agreed seams               │
│      ↓ (auto-invoke)    ↓ (auto-invoke)                │
│    /tdd                /code-review                    │
│  (red→green loop)    (standards + spec)                │
└────────────────────────────────────────────────────────┘
                           ↓
┌────────────────────────────────────────────────────────┐
│ PHASE 4: MAINTENANCE (định kỳ)                         │
│                                                        │
│  /improve-codebase-architecture  (mỗi vài ngày)        │
│  /diagnosing-bugs  (khi có bug khó)                    │
└────────────────────────────────────────────────────────┘
```

## 8. Dependency Map giữa các Skills

```
USER-INVOKED SKILLS (bạn gọi)
│
├── /setup-matt-pocock-skills
│   └── (không gọi skill khác — chỉ tạo config)
│
├── /ask-matt
│   └── (router — suggest skill phù hợp)
│
├── /grill-me
│   └── → grilling (model-invoked)
│
├── /grill-with-docs
│   ├── → grilling (model-invoked)
│   └── → domain-modeling (model-invoked)
│
├── /triage
│   └── → grilling (model-invoked)
│
├── /to-spec
│   └── (không invoke skills khác — synthesize trực tiếp)
│
├── /to-tickets
│   └── (không invoke skills khác)
│
├── /implement
│   ├── → tdd (model-invoked)
│   └── → code-review (model-invoked)
│
├── /wayfinder
│   └── → grilling (model-invoked)
│
└── /improve-codebase-architecture
    ├── → grilling (model-invoked)
    └── → domain-modeling (model-invoked)

MODEL-INVOKED SKILLS (agent tự gọi)
├── grilling         ← core primitive
├── domain-modeling  ← cập nhật CONTEXT.md + ADRs
├── tdd              ← red-green-refactor
├── code-review      ← standards + spec dual-axis
├── diagnosing-bugs  ← 6-phase debug loop
├── research         ← background fact-finding
├── prototype        ← throwaway exploration
├── codebase-design  ← deep module vocabulary
├── resolving-merge-conflicts
├── wizard
└── writing-for-agents
```

## 9. Concept Quan Trọng: CONTEXT.md

`CONTEXT.md` là **trung tâm** của toàn bộ bộ skills này.

### Mục đích

- Glossary cho project — KHÔNG phải spec, không phải scratch pad
- Tạo "ubiquitous language" giữa bạn và agent
- Giúp agent dùng vocabulary ngắn gọn, chính xác thay vì 20 từ chỗ 1 từ đủ

### Ví dụ impact

- **Trước:** "There's a problem when a lesson inside a section of a course is made 'real' (i.e. given a spot in the file system)"
- **Sau:** "There's a problem with the materialization cascade"

### Ai cập nhật?

Bất kỳ skill nào chạy `domain-modeling` đều cập nhật inline — **ngay khi term được resolve**, không batch sau.

### Content của CONTEXT.md

- Chỉ chứa domain terms và definitions
- Không chứa implementation details
- Không chứa file paths hay code snippets

### ADRs (Architectural Decision Records)

Sống trong `docs/adr/`. Chỉ tạo khi đủ 3 điều kiện:

1. Hard to reverse
2. Surprising without context
3. Result of real trade-off với genuine alternatives

## 10. Concept Quan Trọng: Tracer Bullets và Seams

### Tracer Bullet

Mỗi ticket/implementation slice phải là **vertical** — cắt qua TẤT CẢ layers:

- ✅ schema + API + UI + tests — vertical (đúng)
- ❌ "implement tất cả APIs trước" — horizontal (sai)

Một completed slice phải **demoable hoặc verifiable** trên chính nó.

### Seams

Seam = public boundary nơi bạn test behavior mà không cần reach inside.

Rules:

- Test **chỉ tại pre-agreed seams**
- Dùng seam cao nhất có thể
- Ít seams = tốt hơn (ideal: 1 seam per feature)
- Confirm seams với user trước khi viết test

## 11. Lưu Ý Thực Tế

### ⚠️ Luôn phải làm trước khi implement

1. Chạy `/setup-matt-pocock-skills` (một lần per repo)
2. Chạy `/grill-with-docs` trước MỌI thay đổi đáng kể
3. Tạo spec với `/to-spec` trước khi code
4. Break down với `/to-tickets` trước khi implement

### ⚠️ Về `/diagnosing-bugs`

- Không được jump sang Phase 2 khi chưa có red-capable feedback loop
- "Reading code to build a theory before this command exists" là failure mode mà skill này phòng ngừa
- Phase 6 (cleanup) là bắt buộc — xóa debug logs, document hypothesis trong commit message

### ⚠️ Về TDD

- Expected values phải đến từ **independent source of truth**, không phải recompute theo cách code làm
- Refactoring KHÔNG phải phần của red-green loop — nó thuộc code-review stage
- Confirm seams trước khi viết bất kỳ test nào

### ⚠️ Về skill update

- Claude Code plugin: auto-update — bạn nhận changes của Matt khi ông publish
- `skills.sh` install: bạn sở hữu file, phải manually `npx skills update`
- Không cài cả hai — sẽ có mỗi skill hai lần

### ⚠️ Về `disable-model-invocation: true`

Skill nào có flag này chỉ user mới invoke được. Agent sẽ không tự động gọi dù tình huống phù hợp.

## 12. Khi Nào Dùng Skill Nào — Quick Reference

| Tình huống                           | Skill                                                                |
| ------------------------------------ | -------------------------------------------------------------------- |
| Không biết dùng skill nào            | `/ask-matt`                                                          |
| Bắt đầu dùng skills trên repo mới    | `/setup-matt-pocock-skills`                                          |
| Trước khi bắt đầu làm feature bất kỳ | `/grill-with-docs`                                                   |
| Stress-test idea non-code            | `/grill-me`                                                          |
| Muốn formalize discussion thành spec | `/to-spec`                                                           |
| Muốn break spec thành tickets        | `/to-tickets`                                                        |
| Muốn build code từ spec/tickets      | `/implement`                                                         |
| Work quá lớn cho một session         | `/wayfinder`                                                         |
| Muốn clean up architecture           | `/improve-codebase-architecture`                                     |
| Có bug khó reproduce                 | `diagnosing-bugs` (agent tự gọi, hoặc nói "diagnose this")           |
| Muốn build TDD                       | `tdd` (agent tự gọi qua `/implement`, hoặc nói "red-green-refactor") |
| Muốn review code                     | `code-review` (agent tự gọi qua `/implement`)                        |
| Context window đầy, cần chuyển giao  | `/handoff`                                                           |
| Cần decision từ người khác           | `/to-questionnaire`                                                  |
| Không hiểu message của agent         | `/wait-what`                                                         |
| Muốn học concept qua nhiều sessions  | `/teach`                                                             |

## 13. Liên kết

- **GitHub:** https://github.com/mattpocock/skills
- **Website:** https://www.aihero.dev/skills
- **Newsletter:** https://www.aihero.dev/s/skills-newsletter
- **skills.sh:** https://skills.sh/mattpocock/skills
- **Claude Code Plugin docs:** https://code.claude.com/docs/en/plugins
