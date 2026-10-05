# Addy Osmani — Agent Skills: Comprehensive Reference Guide

> **Source:** [addyosmani/agent-skills](https://github.com/addyosmani/agent-skills)  
> **Web:** [skills.addy.ie](https://skills.addy.ie)  
> **Stars:** ~89k ⭐ (August 2026)  
> **Analyzed:** August 2026

## 1. Overview & Philosophy

`addyosmani/agent-skills` là một bộ **24 production-grade engineering skills** dành cho AI coding agents, được xây dựng bởi Addy Osmani (Engineering Manager tại Google, tác giả của _Learning JavaScript Design Patterns_).

**Triết lý cốt lõi:** AI agents mặc định chọn đường ngắn nhất — bỏ qua spec, test, security review, và các practices tạo ra phần mềm đáng tin cậy. Agent Skills đóng gói bộ **senior engineer workflow** vào structured workflows có thể áp dụng nhất quán trên toàn bộ vòng đời phát triển.

**Nguồn gốc best practices:** Bộ này bake-in engineering culture từ:

- _Software Engineering at Google_ (SWE Book)
- Google's Engineering Practices Guide
- Hyrum's Law, Beyoncé Rule, Chesterton's Fence, One-Version Rule
- Shift Left, Trunk-based development, Faster is Safer

**Lifecycle đầy đủ:**

```
 DEFINE     PLAN      BUILD     VERIFY    REVIEW     SHIP
┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐
│ Idea │→ │ Spec │→ │ Code │→ │ Test │→ │  QA  │→ │  Go  │
│Refine│  │  PRD │  │ Impl │  │Debug │  │ Gate │  │ Live │
└──────┘  └──────┘  └──────┘  └──────┘  └──────┘  └──────┘
 /spec      /plan    /build     /test    /review    /ship
```

## 2. Cài đặt

### Phương pháp 1: skills CLI (Universal — 70+ agents)

```bash
# Cài tất cả 24 skills
npx skills add addyosmani/agent-skills

# Xem danh sách trước khi cài
npx skills add addyosmani/agent-skills --list

# Cài một skill cụ thể
npx skills add addyosmani/agent-skills --skill code-review-and-quality
```

> **Lưu ý quan trọng khi cài một skill đơn lẻ:** Cài lẻ sẽ không copy thư mục `references/` ở root. Skill vẫn hoạt động nhưng các shared checklists bị thiếu. Workaround: clone repo đầy đủ hoặc copy `references/` thủ công vào thư mục skill.

### Phương pháp 2: Claude Code (Native — Recommended)

```bash
# Qua marketplace
/plugin marketplace add addyosmani/agent-skills
/plugin install agent-skills@addy-agent-skills

# Hoặc local clone
git clone https://github.com/addyosmani/agent-skills.git
claude --plugin-dir /path/to/agent-skills
```

> **SSH error?** Dùng HTTPS: `/plugin marketplace add https://github.com/addyosmani/agent-skills.git`  
> Hoặc: `git config --global url."https://github.com/".insteadOf git@github.com:`

### Phương pháp 3: Codex

```bash
codex plugin marketplace add addyosmani/agent-skills
codex plugin add agent-skills@agent-skills
# Invoke bằng @ syntax: @spec-driven-development
```

### Phương pháp 4: Cursor

Đặt workflow skills vào `.cursor/skills/`, short policies vào `.cursor/rules/*.mdc`. **Không paste** full skill content vào rules.

### Phương pháp 5: Gemini CLI

```bash
gemini skills install https://github.com/addyosmani/agent-skills.git --path skills
```

### Phương pháp 6: Antigravity, Windsurf, GitHub Copilot, Kiro, OpenCode

Mỗi tool có setup guide riêng tại `docs/<tool>-setup.md`. Xem [docs/](https://github.com/addyosmani/agent-skills/tree/main/docs).

## 3. Cấu trúc Repository

```
agent-skills/
├── skills/                           # 24 skills (23 lifecycle + 1 meta)
├── agents/                           # 4 specialist review personas
├── references/                       # 7 shared checklists (được skill pull in)
├── hooks/                            # Session lifecycle hooks
├── .claude/commands/                 # 8 slash commands cho Claude Code
├── .gemini/commands/                 # 8 slash commands cho Gemini CLI
├── .codex-plugin/                    # Codex plugin manifest
├── .claude-plugin/                   # Claude Code plugin manifest
├── commands/                         # 8 slash commands cho Antigravity
├── evals/                            # Framework eval skills
├── docs/                             # Setup guides per tool
├── plugin.json                       # Plugin manifest
├── CLAUDE.md                         # Context cho Claude
└── AGENTS.md                         # Context cho OpenCode/Codex
```

### Skill Anatomy (mọi SKILL.md đều có cấu trúc này)

```
┌─────────────────────────────────────────────────────┐
│  SKILL.md                                           │
│                                                     │
│  Frontmatter: name + description                    │
│  Overview       → What this skill does              │
│  When to Use    → Triggering conditions             │
│  Process        → Step-by-step workflow             │
│  Rationalizations → Excuses + rebuttals (UNIQUE)   │
│  Red Flags      → Signs something's wrong (UNIQUE) │
│  Verification   → Evidence requirements             │
└─────────────────────────────────────────────────────┘
```

**Key design choices:**

- **Anti-rationalization tables** — mỗi skill có bảng "Common Rationalizations" liệt kê các lý do phổ biến để bỏ qua skill + rebuttal từng cái
- **Red Flags** — dấu hiệu nhận biết khi process đang sai
- **Verification là bắt buộc** — mọi skill kết thúc bằng evidence requirements; "seems right" không đủ
- **Progressive disclosure** — SKILL.md là entry point, references load chỉ khi cần → token usage tối thiểu

## 4. 8 Slash Commands (Entry Points)

| Command          | Phase  | Chức năng                                            |
| ---------------- | ------ | ---------------------------------------------------- |
| `/spec`          | Define | Tạo PRD trước khi viết code                          |
| `/plan`          | Plan   | Break spec thành atomic tasks                        |
| `/build`         | Build  | Implement incremental, test-first                    |
| `/build auto`    | Build  | Autonomous mode — approve plan một lần, chạy toàn bộ |
| `/test`          | Verify | Enforce red-green-refactor                           |
| `/review`        | Review | Five-axis quality gate                               |
| `/webperf`       | Review | Web performance audit                                |
| `/code-simplify` | Review | Reduce complexity                                    |
| `/ship`          | Ship   | Fan-out to specialist personas → go/no-go            |

**Skills auto-activate:** Không cần gọi thủ công — thiết kế API triggers `api-and-interface-design`, build UI triggers `frontend-ui-engineering`, v.v.

## 5. Tất cả 24 Skills — Chi Tiết

### META SKILL

#### `using-agent-skills` 🗺️

- **Vai trò:** Router — maps incoming work to the right skill
- **Khi nào dùng:** Đầu session hoặc khi không biết nên dùng skill nào
- **Chức năng:** Xác định phase hiện tại → activate đúng skill

### DEFINE — Clarify What to Build

#### `interview-me` 🎤

- **Khi nào dùng:** Ask bị underspecified; user nói "interview me" / "grill me"
- **Chức năng:** One-question-at-a-time interview đến khi đạt ~95% confidence về yêu cầu
- **Phân biệt với Matt's `/grill-me`:** Cùng concept nhưng Addy's version có explicit confidence threshold (95%)
- **Output:** Requirements đủ rõ để viết spec

#### `idea-refine` 💡

- **Khi nào dùng:** Có concept mơ hồ cần explore trước khi commit
- **Chức năng:** Divergent/convergent thinking — mở rộng ý tưởng rồi thu hẹp thành concrete proposal
- **Không có tương đương** trong mattpocock/skills

#### `spec-driven-development` 📋 _(Skill quan trọng nhất — entry point /spec)_

- **Khi nào dùng:** Bắt đầu project/feature mới; thay đổi touches nhiều file; task > 30 phút
- **KHÔNG dùng khi:** Single-line fix, typo, thay đổi self-contained rõ ràng
- **Chức năng:** Gated 4-phase workflow — SPECIFY → PLAN → TASKS → IMPLEMENT
- **Phase 1 - Specify:** 6 core areas bắt buộc:
  1. **Objective** — What + Why + Who + Success criteria
  2. **Commands** — Full executable commands (`npm run build`, `npm test -- --coverage`)
  3. **Project Structure** — Directory layout
  4. **Code Style** — One real snippet beats three paragraphs
  5. **Testing Strategy** — Framework, locations, coverage, levels
  6. **Boundaries** — Always/Ask First/Never
- **Surface assumptions trước:** List tất cả assumptions trước khi viết spec, xin confirm từ human
- **Output path:** Lưu plan vào `tasks/plan.md`, task list vào `tasks/todo.md`
- **Spec là living document:** Commit vào version control; update khi decisions change; reference trong PRs

### PLAN — Break It Down

#### `planning-and-task-breakdown` 📊

- **Khi nào dùng:** Đã có spec, cần implementation units
- **Chức năng:** Decompose specs → small, verifiable tasks với acceptance criteria + dependency ordering
- **Quy tắc sizing:** Mỗi task = completable trong một focused session, touches ≤ 5 files
- **Task template:**
  ```
  - [ ] Task: [Description]
    - Acceptance: [What must be true when done]
    - Verify: [How to confirm — test command, build, manual check]
    - Files: [Which files will be touched]
  ```
- **Vertical slices** (giống Matt's tracer bullets): Mỗi slice phải complete và verifiable
- **Dependency ordering** — không phải perceived importance

### BUILD — Write the Code

#### `incremental-implementation` 🔨

- **Khi nào dùng:** Bất kỳ thay đổi nào touching > 1 file
- **Chức năng:** Thin vertical slices — implement, test, verify, commit; feature flags, safe defaults, rollback-friendly
- **Entry point:** `/build`

#### `test-driven-development` ✅

- **Khi nào dùng:** Implementing logic, fixing bugs, changing behavior
- **Chức năng:** Red-Green-Refactor với:
  - **Test pyramid:** 80% unit / 15% integration / 5% E2E
  - **DAMP over DRY** (Descriptive And Meaningful Phrases) trong test code
  - **Beyoncé Rule:** "If you liked it, then you shoulda put a test on it"
  - **Test sizes:** Small (unit), Medium (integration), Large (E2E)
  - **Browser testing integration** nếu có UI
- **Anti-pattern rõ ràng:** Không test implementation details

#### `context-engineering` 🧠

- **Khi nào dùng:** Bắt đầu session, đổi task, khi output quality giảm
- **Chức năng:** Feed agents đúng thông tin tại đúng thời điểm — rules files, context packing, MCP integrations
- **Không có tương đương trực tiếp** trong mattpocock/skills

#### `source-driven-development` 📚

- **Khi nào dùng:** Muốn code có authoritative, cited source cho mọi framework decision
- **Chức năng:** Ground mọi framework decision trong official documentation — verify, cite sources, flag what's unverified
- **Đặc biệt hữu ích** khi dùng framework ít quen thuộc

#### `doubt-driven-development` 🤔

- **Khi nào dùng:** Stakes cao (production, security, irreversible); unfamiliar code; confident output cần verify
- **Chức năng:** Adversarial fresh-context review của mọi non-trivial decision in-flight
- **Process:** CLAIM → EXTRACT → DOUBT → RECONCILE → STOP
- **Optional:** User-authorized cross-model escalation
- **Unique feature:** Có thể leo thang sang model khác để double-check

#### `frontend-ui-engineering` 🎨

- **Khi nào dùng:** Build hoặc modify user-facing interfaces
- **Chức năng:** Component architecture, design systems, state management, responsive design, WCAG 2.1 AA accessibility
- **Coverage rộng hơn** Matt's skills về frontend

#### `api-and-interface-design` 🔌

- **Khi nào dùng:** Design APIs, module boundaries, public interfaces
- **Chức năng:** Contract-first design với:
  - **Hyrum's Law** — All observable behaviors will be depended on
  - **One-Version Rule** — Không maintain nhiều versions
  - Error semantics, boundary validation

### VERIFY — Prove It Works

#### `browser-testing-with-devtools` 🌐

- **Khi nào dùng:** Build hoặc debug anything chạy trong browser
- **Chức năng:** Chrome DevTools MCP cho live runtime data — DOM inspection, console logs, network traces, performance profiling
- **Unique:** Tích hợp trực tiếp với browser DevTools qua MCP

#### `debugging-and-error-recovery` 🐛

- **Khi nào dùng:** Tests fail, builds break, behavior unexpected
- **Chức năng:** Five-step triage:
  1. **Reproduce** — Tạo reliable reproduction
  2. **Localize** — Isolate điểm thất bại
  3. **Reduce** — Minimize repro đến case nhỏ nhất
  4. **Fix** — Apply targeted fix
  5. **Guard** — Viết regression test
- **Stop-the-line rule** — Không tiếp tục khi build/tests đỏ
- **Safe fallbacks** — Không break unrelated features

### REVIEW — Quality Gates Before Merge

#### `code-review-and-quality` 👀 _(Entry point /review)_

- **Khi nào dùng:** Trước khi merge BẤT KỲ change nào — không exception
- **Chức năng:** Five-axis review với severity labels:
  - **Axis 1: Correctness** — Spec match, edge cases, error paths, tests
  - **Axis 2: Readability & Simplicity** — Names, control flow, dead code, "clever tricks"
  - **Axis 3: Architecture** — Patterns, boundaries, duplication, dependencies, complexity
  - **Axis 4: Security** — Input validation, secrets, auth, SQL injection, XSS
  - **Axis 5: Performance** — N+1, unbounded loops, async, re-renders, pagination
- **Severity labels:**
  - _(no prefix)_ = Required change
  - **Critical:** = Blocks merge
  - **Nit:** = Minor, optional
  - **Optional:** / **Consider:** = Suggestion
  - **FYI** = Informational
- **Change sizing:**
  - ~100 lines = Good
  - ~300 lines = Acceptable
  - ~1000 lines = Too large, split it
- **Splitting strategies:** Stack / By file group / Horizontal / Vertical
- **Multi-model pattern:** Model A viết code → Model B review → Model A address → Human final call
- **Approval standard:** Approve khi definitely improves code health, dù không perfect

#### `code-simplification` ✂️

- **Khi nào dùng:** Code hoạt động nhưng khó đọc/maintain
- **Chức năng:** Áp dụng **Chesterton's Fence** (hiểu WHY trước khi xóa) + **Rule of 500** (file > 500 lines = red flag) → reduce complexity mà preserve exact behavior
- **Entry point:** `/code-simplify`

#### `security-and-hardening` 🔒

- **Khi nào dùng:** Handle user input, auth, data storage, external integrations
- **Chức năng:** OWASP Top 10 prevention, auth patterns, secrets management, dependency auditing
- **Three-tier boundary system:** Classify mọi data source theo trust level
- **Reference:** `references/security-checklist.md`

#### `performance-optimization` ⚡

- **Khi nào dùng:** Performance requirements tồn tại hoặc suspect regressions
- **Chức năng:** Measure-first approach:
  - **Core Web Vitals targets** — LCP < 2.5s, FID < 100ms, CLS < 0.1
  - Profiling workflows, bundle analysis, anti-pattern detection
- **Reference:** `references/performance-checklist.md`

### SHIP — Deploy with Confidence

#### `git-workflow-and-versioning` 🌿

- **Khi nào dùng:** Mọi code change (luôn luôn)
- **Chức năng:** Trunk-based development, atomic commits, change sizing (~100 lines), commit-as-save-point pattern
- **Commit message standard:** Imperative, standalone, informative

#### `ci-cd-and-automation` 🤖

- **Khi nào dùng:** Setup hoặc modify build/deploy pipelines
- **Chức năng:** Shift Left, Faster is Safer, feature flags, quality gate pipelines, failure feedback loops

#### `deprecation-and-migration` 🗑️

- **Khi nào dùng:** Remove old systems, migrate users, sunset features
- **Chức năng:** Code-as-liability mindset, compulsory vs advisory deprecation, migration patterns, zombie code removal
- **Unique:** Explicit framework cho việc xóa code cũ — không có trong Matt's skills

#### `documentation-and-adrs` 📝

- **Khi nào dùng:** Architectural decisions, API changes, shipping features
- **Chức năng:** Architecture Decision Records + API docs + inline documentation standards — document the WHY
- **ADR focus:** Tương tự Matt's `domain-modeling` nhưng document-centric hơn

#### `observability-and-instrumentation` 📊

- **Khi nào dùng:** Add telemetry; ship anything running in production
- **Chức năng:** Structured logging, RED metrics, OpenTelemetry tracing, symptom-based alerting — instrument as you build
- **Reference:** `references/observability-checklist.md`

#### `shipping-and-launch` 🚀 _(Entry point /ship)_

- **Khi nào dùng:** Prepare to deploy to production
- **Chức năng:** Pre-launch checklists, feature flag lifecycle, staged rollouts, rollback procedures, monitoring setup
- **Fan-out to personas:** Spawn 4 review agents parallel → merge thành go/no-go

## 6. 4 Agent Personas (Specialist Reviewers)

Được `/ship` fan-out và chạy **parallel**:

| Persona                   | Role                     | Tiêu chuẩn                                      |
| ------------------------- | ------------------------ | ----------------------------------------------- |
| `code-reviewer`           | Senior Staff Engineer    | "Would a staff engineer approve this?"          |
| `test-engineer`           | QA Specialist            | Test strategy, coverage, Prove-It pattern       |
| `security-auditor`        | Security Engineer        | OWASP, threat modeling, vulnerability detection |
| `web-performance-auditor` | Web Performance Engineer | Core Web Vitals audit (Quick/Deep modes)        |

**Quy tắc orchestration:** Personas KHÔNG gọi persona khác. Fan-out từ `/ship` → parallel → merge result.

## 7. 7 Reference Checklists (Được Skills Pull In)

| File                         | Covers                                                                                |
| ---------------------------- | ------------------------------------------------------------------------------------- |
| `definition-of-done.md`      | Standing bar mọi change phải đạt, vs per-task acceptance criteria                     |
| `testing-patterns.md`        | Test structure, naming, mocking, React/API/E2E examples, anti-patterns (JS/TS)        |
| `security-checklist.md`      | Pre-commit checks, auth, input validation, headers, CORS, OWASP Top 10                |
| `performance-checklist.md`   | Core Web Vitals targets, frontend/backend checklists, measurement commands            |
| `accessibility-checklist.md` | Keyboard nav, screen readers, visual design, ARIA, testing tools                      |
| `observability-checklist.md` | On-call questions, structured logging, RED/USE metrics, tracing, alerting             |
| `orchestration-patterns.md`  | Endorsed multi-persona patterns, anti-patterns, "personas don't invoke personas" rule |

## 8. Workflow Tổng Thể

```
┌──────────────────────────────────────────────────────────────┐
│ BƯỚC 1: DEFINE (Trước khi viết một dòng code)               │
│                                                              │
│  [Nếu yêu cầu mơ hồ]  →  interview-me                      │
│  [Có idea cần explore] →  idea-refine                       │
│  /spec                 →  spec-driven-development            │
│                             → tasks/plan.md                  │
│                             → tasks/todo.md                  │
└──────────────────────────────────────────────────────────────┘
                               ↓
┌──────────────────────────────────────────────────────────────┐
│ BƯỚC 2: PLAN                                                 │
│                                                              │
│  /plan  →  planning-and-task-breakdown                      │
│               → Vertical slices với acceptance criteria      │
│               → Dependency ordering                          │
└──────────────────────────────────────────────────────────────┘
                               ↓
┌──────────────────────────────────────────────────────────────┐
│ BƯỚC 3: BUILD                                                │
│                                                              │
│  /build  →  incremental-implementation                      │
│               ↳ test-driven-development (auto)               │
│               ↳ context-engineering (auto)                   │
│               ↳ source-driven-development (if needed)        │
│               ↳ doubt-driven-development (high-stakes)       │
│               ↳ frontend-ui-engineering (if UI)              │
│               ↳ api-and-interface-design (if API)            │
└──────────────────────────────────────────────────────────────┘
                               ↓
┌──────────────────────────────────────────────────────────────┐
│ BƯỚC 4: VERIFY                                               │
│                                                              │
│  /test  →  debugging-and-error-recovery                     │
│               ↳ browser-testing-with-devtools (if browser)  │
└──────────────────────────────────────────────────────────────┘
                               ↓
┌──────────────────────────────────────────────────────────────┐
│ BƯỚC 5: REVIEW                                               │
│                                                              │
│  /review       →  code-review-and-quality (5 axes)         │
│  /webperf      →  performance-optimization                  │
│  /code-simplify →  code-simplification                      │
│                    + security-and-hardening (auto)          │
└──────────────────────────────────────────────────────────────┘
                               ↓
┌──────────────────────────────────────────────────────────────┐
│ BƯỚC 6: SHIP                                                 │
│                                                              │
│  /ship  →  shipping-and-launch                              │
│               → code-reviewer      (parallel fan-out)       │
│               → test-engineer      (parallel fan-out)       │
│               → security-auditor   (parallel fan-out)       │
│               → web-performance-auditor (parallel fan-out)  │
│               → go / no-go decision                         │
└──────────────────────────────────────────────────────────────┘
```

## 9. Framework & Tool Support

### AI Agents được hỗ trợ (Native integration)

| Agent               | Status                              |
| ------------------- | ----------------------------------- |
| **Claude Code**     | ✅ First-class — plugin marketplace |
| **Codex**           | ✅ Native plugin (v0.122+)          |
| **Cursor**          | ✅ `.cursor/skills/` directory      |
| **Gemini CLI**      | ✅ Native skills                    |
| **Antigravity CLI** | ✅ Native plugin                    |
| **GitHub Copilot**  | ✅ Via `agents/` personas           |
| **OpenCode**        | ✅ Via AGENTS.md + skill tool       |
| **Windsurf**        | ✅ Via rules config                 |
| **Kiro IDE**        | ✅ `.kiro/skills/`                  |
| **Command Code**    | ✅ `cmd skills add`                 |
| **70+ others**      | ✅ Qua skills CLI (plain Markdown)  |

### Development Framework Support

Skills này **language/framework-agnostic** theo thiết kế — plain Markdown workflows áp dụng cho mọi tech stack. Tuy nhiên, reference files thiên về **JavaScript/TypeScript ecosystem**:

- `testing-patterns.md` — Ví dụ cụ thể cho React, JavaScript/TypeScript, Jest
- `performance-checklist.md` — Core Web Vitals, bundle analysis (webpack/vite)
- `accessibility-checklist.md` — Web accessibility (WCAG 2.1 AA)
- `browser-testing-with-devtools` — Chrome DevTools MCP

**Các project type phù hợp nhất:**

- ✅ Web applications (React, Vue, Angular, Next.js)
- ✅ Node.js / TypeScript backends
- ✅ Full-stack JavaScript projects
- ✅ APIs (REST, GraphQL)
- ✅ Bất kỳ project nào cần production-grade practices

## 10. Lưu Ý Thực Tế

### ⚠️ `/build auto` — Autonomous Mode

Phê duyệt plan một lần rồi agent chạy toàn bộ không cần step giữa. **Không bỏ verification** — vẫn test-driven, commit từng task riêng, pause khi fail hoặc risky step. Dùng cho tasks đã rõ ràng, spec đầy đủ.

### ⚠️ Context loading

Không load tất cả 24 skills cùng lúc — token waste. Meta-skill `using-agent-skills` route đến đúng skill. Skills còn lại chỉ load khi cần.

### ⚠️ Thiếu `references/` khi cài lẻ

Cài một skill riêng lẻ qua `npx skills add --skill <name>` sẽ thiếu `references/`. Nếu dùng security, performance, testing skills — cần cài toàn bộ repo hoặc copy `references/` thủ công.

### ⚠️ Spec là sống — không phải artifact

Commit spec vào version control. Update khi decisions thay đổi. Reference trong PRs. Outdated spec vẫn tốt hơn không có spec.

### ⚠️ Về `doubt-driven-development`

Chỉ kích hoạt khi stakes cao. Không dùng routine — có overhead. Cross-model escalation cần user authorization.

### ⚠️ Dead code hygiene sau mọi refactor

`code-review-and-quality` yêu cầu list orphaned code và hỏi trước khi xóa. Không silent-delete.

## 11. Quick Reference: Khi Nào Dùng Skill Nào

| Tình huống                     | Skill                                  |
| ------------------------------ | -------------------------------------- |
| Không biết bắt đầu từ đâu      | `using-agent-skills`                   |
| Yêu cầu mơ hồ, cần làm rõ      | `interview-me`                         |
| Có ý tưởng mờ, muốn explore    | `idea-refine`                          |
| Bắt đầu feature/project mới    | `spec-driven-development` (/spec)      |
| Có spec, cần break thành tasks | `planning-and-task-breakdown` (/plan)  |
| Bắt đầu implement              | `incremental-implementation` (/build)  |
| Viết code test-first           | `test-driven-development` (/test)      |
| Session đầu hoặc quality drops | `context-engineering`                  |
| Dùng unfamiliar framework      | `source-driven-development`            |
| Stakes cao, cần double-check   | `doubt-driven-development`             |
| Build React/Vue UI             | `frontend-ui-engineering` (auto)       |
| Design API/interface           | `api-and-interface-design` (auto)      |
| Bug in browser                 | `browser-testing-with-devtools`        |
| Tests fail / build broken      | `debugging-and-error-recovery`         |
| Trước khi merge                | `code-review-and-quality` (/review)    |
| Code complex, khó đọc          | `code-simplification` (/code-simplify) |
| Security sensitive change      | `security-and-hardening`               |
| Performance concern            | `performance-optimization` (/webperf)  |
| Making commits                 | `git-workflow-and-versioning`          |
| Setup CI/CD                    | `ci-cd-and-automation`                 |
| Xóa legacy code                | `deprecation-and-migration`            |
| Architectural decision         | `documentation-and-adrs`               |
| Adding logging/metrics         | `observability-and-instrumentation`    |
| Deploy to production           | `shipping-and-launch` (/ship)          |

## 12. Links

- **GitHub:** https://github.com/addyosmani/agent-skills
- **Website:** https://skills.addy.ie
- **Getting Started:** https://skills.addy.ie/docs/getting-started/
- **Skills Catalog:** https://skills.addy.ie/skills/
- **Lifecycle:** https://skills.addy.ie/lifecycle/
- **Loops (Agentic):** https://skills.addy.ie/loops/
- **Comparison:** https://skills.addy.ie/compare/
- **Comparison doc:** https://github.com/addyosmani/agent-skills/blob/main/docs/comparison.md
