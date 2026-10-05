# So Sánh: mattpocock/skills vs addyosmani/agent-skills

## TL;DR — Quyết định nhanh

| Bạn muốn...                                               | Chọn                        |
| --------------------------------------------------------- | --------------------------- |
| Daily toolkit gọn nhẹ cho TypeScript/Claude Code          | **mattpocock/skills**       |
| Full production lifecycle có human checkpoint mỗi phase   | **addyosmani/agent-skills** |
| Shared language giữa dev và agent (CONTEXT.md)            | **mattpocock/skills**       |
| Security / performance / CI/CD / observability đầy đủ     | **addyosmani/agent-skills** |
| Làm việc trên nhiều AI tools (Cursor, Copilot, Gemini...) | **addyosmani/agent-skills** |
| Sửa architecture entropy của codebase hiện tại            | **mattpocock/skills**       |
| Greenfield project cần đi production                      | **addyosmani/agent-skills** |
| Codebase TypeScript với context window tight              | **mattpocock/skills**       |

## 1. Tổng Quan Định Vị

|                          | **mattpocock/skills**                                   | **addyosmani/agent-skills**                       |
| ------------------------ | ------------------------------------------------------- | ------------------------------------------------- |
| **Tác giả**              | Matt Pocock (TypeScript educator, Total TypeScript)     | Addy Osmani (Engineering Manager, Google Chrome)  |
| **Stars**                | ~niche/nhỏ                                              | ~89k ⭐ (Aug 2026)                                |
| **Triết lý cốt lõi**     | Sharp personal toolkit — một expert's daily workflow    | Production lifecycle đầy đủ với quality gates     |
| **Nguồn gốc**            | Từ `.agents` directory cá nhân của Matt                 | Engineering culture từ Google SWE Book            |
| **Số lượng skills**      | ~15 skills                                              | 24 skills                                         |
| **Organizing principle** | Toolbox of focused commands                             | SDLC phases: Define→Plan→Build→Verify→Review→Ship |
| **Entry points**         | Slash commands (`/grill-with-docs`, `/implement`, v.v.) | 8 slash commands map 1:1 với lifecycle phases     |
| **Automation**           | `/build auto` không có                                  | `/build auto` — approve once, run autonomously    |

## 2. So Sánh Coverage

### Lifecycle Coverage

| Phase                        | mattpocock/skills                                             | addyosmani/agent-skills                                 |
| ---------------------------- | ------------------------------------------------------------- | ------------------------------------------------------- |
| **Requirements / Grilling**  | ✅ `/grill-me`, `/grill-with-docs` (rất mạnh — rounds system) | ✅ `interview-me` (95% confidence threshold)            |
| **Idea Exploration**         | ❌ Không có                                                   | ✅ `idea-refine`                                        |
| **Domain Modeling**          | ✅ `domain-modeling` → CONTEXT.md + ADRs                      | ✅ `documentation-and-adrs` (document-centric hơn)      |
| **Spec / PRD**               | ✅ `/to-spec` → lên issue tracker                             | ✅ `spec-driven-development` → tasks/plan.md            |
| **Task Breakdown**           | ✅ `/to-tickets` (tracer bullets)                             | ✅ `planning-and-task-breakdown`                        |
| **Implementation**           | ✅ `/implement`                                               | ✅ `incremental-implementation`                         |
| **TDD**                      | ✅ `tdd` (strict seam-based)                                  | ✅ `test-driven-development` (pyramid + Beyoncé Rule)   |
| **Bug Diagnosis**            | ✅ `diagnosing-bugs` (6 phases, rất chi tiết)                 | ✅ `debugging-and-error-recovery` (5 steps)             |
| **Code Review**              | ✅ `code-review` (Standards + Spec dual-axis)                 | ✅ `code-review-and-quality` (5-axis + severity labels) |
| **Architecture Cleanup**     | ✅ `/improve-codebase-architecture` (deep modules)            | ❌ Không có skill riêng                                 |
| **Security**                 | ⚠️ Mention trong code-review                                  | ✅ `security-and-hardening` (OWASP full)                |
| **Performance**              | ⚠️ Có `/webperf` command                                      | ✅ `performance-optimization` + Core Web Vitals         |
| **Frontend/UI**              | ❌ Không có                                                   | ✅ `frontend-ui-engineering` (WCAG 2.1 AA)              |
| **API Design**               | ❌ Không có                                                   | ✅ `api-and-interface-design` (Hyrum's Law)             |
| **CI/CD**                    | ❌ Không có                                                   | ✅ `ci-cd-and-automation`                               |
| **Observability**            | ❌ Không có                                                   | ✅ `observability-and-instrumentation`                  |
| **Deprecation**              | ❌ Không có                                                   | ✅ `deprecation-and-migration`                          |
| **Launch / Ship**            | ❌ Không có                                                   | ✅ `shipping-and-launch` (fan-out personas)             |
| **Context Engineering**      | ❌ Không có                                                   | ✅ `context-engineering`                                |
| **Source Verification**      | ❌ Không có                                                   | ✅ `source-driven-development`                          |
| **Adversarial Review**       | ❌ Không có                                                   | ✅ `doubt-driven-development`                           |
| **Browser DevTools**         | ❌ Không có                                                   | ✅ `browser-testing-with-devtools` (Chrome MCP)         |
| **Session Handoff**          | ✅ `/handoff`                                                 | ❌ Không có                                             |
| **Learning/Teaching**        | ✅ `/teach`                                                   | ❌ Không có                                             |
| **Cross-team Questionnaire** | ✅ `/to-questionnaire`                                        | ❌ Không có                                             |
| **Prototype**                | ✅ `prototype` (throwaway HTML)                               | ❌ Không có                                             |
| **Merge Conflict**           | ✅ `resolving-merge-conflicts`                                | ❌ Không có                                             |
| **Human Wizard**             | ✅ `wizard` (bash wizard cho human steps)                     | ❌ Không có                                             |

## 3. So Sánh Cơ Chế Hoạt Động

### Grilling / Requirements Interrogation

|                     | **mattpocock/skills**                                          | **addyosmani/agent-skills**                                   |
| ------------------- | -------------------------------------------------------------- | ------------------------------------------------------------- |
| **Cơ chế**          | Design tree + rounds — hỏi toàn bộ frontier trong mỗi round    | One-question-at-a-time đến ~95% confidence                    |
| **Approach**        | Parallel questions per round (tất cả câu không phụ thuộc nhau) | Sequential, một câu tại một thời điểm                         |
| **Output**          | CONTEXT.md được update + ADRs                                  | Requirements document đủ để viết spec                         |
| **Kết hợp với doc** | `/grill-with-docs` = grilling + domain modeling cùng lúc       | Riêng biệt: `interview-me` → sau đó `spec-driven-development` |
| **Depth**           | Rất sâu — map toàn bộ design tree, không bỏ sót branch         | Rõ ràng hơn về khi nào "đủ" (95% threshold)                   |

### TDD

|                       | **mattpocock/skills**                                   | **addyosmani/agent-skills**                       |
| --------------------- | ------------------------------------------------------- | ------------------------------------------------- |
| **Paradigm**          | Seam-based — test chỉ tại pre-agreed public boundaries  | Test pyramid: 80% unit / 15% integration / 5% E2E |
| **Seam confirmation** | Bắt buộc confirm seams với user trước khi viết test     | Không yêu cầu explicit seam discussion            |
| **Style**             | DAMP không có; focus vào "behavior, not implementation" | DAMP over DRY cho test code; Beyoncé Rule         |
| **Scope**             | Ít seams hơn = tốt hơn (minimalist)                     | Pyramid rõ ràng hơn cho từng loại test            |
| **Expected values**   | Phải từ independent source of truth                     | Test sizes (Small/Medium/Large) được define       |

### Code Review

|                        | **mattpocock/skills**                             | **addyosmani/agent-skills**                                               |
| ---------------------- | ------------------------------------------------- | ------------------------------------------------------------------------- |
| **Axes**               | 2-axis: Standards (Fowler smells) + Spec          | 5-axis: Correctness / Readability / Architecture / Security / Performance |
| **Parallel execution** | Dual sub-agents song song để không "pollute" nhau | Multi-model pattern: Model A viết → Model B review                        |
| **Severity labels**    | Không có explicit labels                          | Critical / Required / Nit / Optional / FYI                                |
| **Change sizing**      | Không có guideline cụ thể                         | ~100 lines good / ~300 acceptable / ~1000 too large                       |
| **Security focus**     | Mention trong Fowler baseline                     | Full OWASP axis riêng                                                     |

### Bug Diagnosis

|                       | **mattpocock/skills**                                                        | **addyosmani/agent-skills**                           |
| --------------------- | ---------------------------------------------------------------------------- | ----------------------------------------------------- |
| **Phases**            | 6 phases (Build loop → Reproduce → Hypothesise → Instrument → Fix → Cleanup) | 5 steps (Reproduce → Localize → Reduce → Fix → Guard) |
| **Phase 1 emphasis**  | **Cực kỳ mạnh** — KHÔNG được sang phase 2 khi chưa có red-capable command    | Stop-the-line rule nhưng ít prescriptive hơn          |
| **Instrumentation**   | Tag debug logs với unique prefix `[DEBUG-xxxx]`                              | Không có tagging requirement                          |
| **Cleanup**           | Phase 6 bắt buộc — xóa debug logs, document hypothesis trong commit          | Regression test là guard chính                        |
| **Hypothesis format** | 3-5 ranked, falsifiable hypotheses                                           | Ít structured hơn                                     |

### Architecture

|                | **mattpocock/skills**                                                         | **addyosmani/agent-skills**                                         |
| -------------- | ----------------------------------------------------------------------------- | ------------------------------------------------------------------- |
| **Approach**   | `/improve-codebase-architecture` — Deep modules, HTML report với before/after | Không có dedicated architecture improvement skill                   |
| **Vocabulary** | Deep/Shallow modules, Deletion test, Seams                                    | Chesterton's Fence (code-simplification), architecture trong review |
| **Timing**     | Định kỳ, proactive (mỗi vài ngày)                                             | Reactive (khi review hoặc simplify)                                 |
| **CONTEXT.md** | Central artifact — ubiquitous language                                        | Không có tương đương                                                |

## 4. Ưu Điểm và Nhược Điểm

### mattpocock/skills

**✅ Ưu điểm:**

- **Shared language (CONTEXT.md)** — Đặc tính độc đáo nhất. Tạo vocabulary chung giữa dev và agent, giảm tokens, tạo consistency trong naming
- **Design tree grilling** — Cơ chế rounds cho phép đặt câu hỏi parallel, không bỏ sót bất kỳ branch nào
- **User-invoked vs Model-invoked distinction** — Kiến trúc rõ ràng giúp không bị agent gọi nhầm skills
- **Bug diagnosis 6 phases** — Cực kỳ rigorous, đặc biệt Phase 1 (feedback loop bắt buộc trước tiên)
- **Deep module architecture** — `/improve-codebase-architecture` là skill không ai khác có, cho phép proactive codebase health check
- **Session management** — `/handoff`, `/teach`, `/wait-what` giải quyết thực tế của long-running projects
- **Prototype & wizard** — Throwaway exploration và human-step automation
- **Lightweight** — Ít skills hơn, ít token overhead hơn
- **Opinionated codebase design** — Deep/Shallow module vocabulary rất hữu ích khi refactor

**❌ Nhược điểm:**

- **Claude Code-first** — Ít support cho Cursor, Gemini CLI, Copilot
- **TypeScript/JavaScript bias** — Designed cho Matt's workflow cụ thể
- **Thiếu security skill riêng** — Security chỉ là một phần của code-review
- **Thiếu production concerns** — Không có CI/CD, observability, launch skills
- **Không có frontend/API specific skills** — Generic hơn
- **Issue tracker dependency** — `/setup-matt-pocock-skills` cần configure, `/to-spec` và `/to-tickets` phụ thuộc vào config này
- **Cộng đồng nhỏ hơn** — Ít tài liệu, ít ví dụ, ít integrations

### addyosmani/agent-skills

**✅ Ưu điểm:**

- **Full lifecycle coverage** — Define đến Ship, không gap
- **Multi-tool support** — 70+ agents, first-class cho Claude Code, Codex, Cursor, Gemini, Copilot, Kiro
- **Anti-rationalization tables** — Mỗi skill có bảng excuses + rebuttals — ngăn agent shortcut
- **Red Flags** — Explicit signals khi process đi sai
- **Specialist personas** — 4 review agents parallel (code, test, security, performance)
- **Production-grade** — Security (OWASP), performance (Core Web Vitals), observability, CI/CD
- **Reference checklists** — 7 shared checklists được pull in khi cần
- **Severity labels trong review** — Critical/Required/Nit/Optional/FYI rõ ràng
- **`/build auto`** — Autonomous mode cho tasks đã spec rõ
- **Google engineering principles** — Hyrum's Law, Beyoncé Rule, Chesterton's Fence, v.v.
- **Cộng đồng lớn** — 89k stars, active maintenance, nhiều contributions

**❌ Nhược điểm:**

- **Không có CONTEXT.md / shared language** — Thiếu cơ chế xây shared vocabulary
- **Grilling ít sophisticated hơn** — One-question-at-a-time thiếu cái rounds/frontier system của Matt
- **Không có architecture improvement** — Không có skill định kỳ check codebase health
- **Thiếu session management** — Không có `/handoff`, `/teach`, `/wait-what`
- **JS/TS bias trong references** — `testing-patterns.md` có ví dụ React/Jest cụ thể
- **Token overhead tiềm năng** — 24 skills, 7 references — nếu không careful về context loading
- **Không có prototype skill** — Không có throwaway exploration mechanism
- **`references/` portability gap** — Cài lẻ skill mất checklists (known issue #361)
- **Complexity cao hơn** — Nhiều skills, nhiều concepts cần nắm

## 5. Đánh Giá Theo Loại Project

### Frontend Web Application (React/Vue/Next.js)

|             | Matt                          | Addy                                                                               |
| ----------- | ----------------------------- | ---------------------------------------------------------------------------------- |
| **Verdict** | ⭐⭐⭐ Tốt                    | ⭐⭐⭐⭐⭐ Xuất sắc                                                                |
| **Lý do**   | Thiếu frontend-specific skill | `frontend-ui-engineering` (WCAG), `browser-testing-with-devtools`, Core Web Vitals |

### TypeScript Library / Package

|             | Matt                                               | Addy                                          |
| ----------- | -------------------------------------------------- | --------------------------------------------- |
| **Verdict** | ⭐⭐⭐⭐⭐ Xuất sắc                                | ⭐⭐⭐⭐ Rất tốt                              |
| **Lý do**   | Matt là TypeScript expert, workflow khớp hoàn toàn | Đầy đủ lifecycle nhưng ít TypeScript-specific |

### Backend API / Microservice

|             | Matt                                             | Addy                                                                                      |
| ----------- | ------------------------------------------------ | ----------------------------------------------------------------------------------------- |
| **Verdict** | ⭐⭐⭐ Tốt                                       | ⭐⭐⭐⭐⭐ Xuất sắc                                                                       |
| **Lý do**   | Thiếu API design, security, observability skills | `api-and-interface-design`, `security-and-hardening`, `observability-and-instrumentation` |

### Full-Stack Production App

|             | Matt                                   | Addy                              |
| ----------- | -------------------------------------- | --------------------------------- |
| **Verdict** | ⭐⭐⭐ Tốt                             | ⭐⭐⭐⭐⭐ Xuất sắc               |
| **Lý do**   | Thiếu deployment, CI/CD, launch skills | Full lifecycle từ Define đến Ship |

### Legacy Codebase Refactoring

|             | Matt                                                                           | Addy                                     |
| ----------- | ------------------------------------------------------------------------------ | ---------------------------------------- |
| **Verdict** | ⭐⭐⭐⭐⭐ Xuất sắc                                                            | ⭐⭐⭐ Tốt                               |
| **Lý do**   | `/improve-codebase-architecture` + `domain-modeling` = bộ đôi không có đối thủ | Thiếu proactive architecture improvement |

### Greenfield Project

|             | Matt                                   | Addy                                                |
| ----------- | -------------------------------------- | --------------------------------------------------- |
| **Verdict** | ⭐⭐⭐⭐ Rất tốt                       | ⭐⭐⭐⭐⭐ Xuất sắc                                 |
| **Lý do**   | Tốt nhưng thiếu launch/deploy coverage | Full lifecycle, human checkpoints, production-ready |

### Solo Developer / Small Team

|             | Matt                                    | Addy                                            |
| ----------- | --------------------------------------- | ----------------------------------------------- |
| **Verdict** | ⭐⭐⭐⭐⭐ Xuất sắc                     | ⭐⭐⭐⭐ Rất tốt                                |
| **Lý do**   | Lightweight, ít ceremony, fast workflow | Tốt nhưng có thể overkill cho solo projects nhỏ |

### Enterprise / Team Project

|             | Matt                               | Addy                                                            |
| ----------- | ---------------------------------- | --------------------------------------------------------------- |
| **Verdict** | ⭐⭐⭐ Tốt                         | ⭐⭐⭐⭐⭐ Xuất sắc                                             |
| **Lý do**   | Ít structure cho team coordination | Security gates, review personas, CI/CD, ADRs — enterprise-ready |

## 6. Framework Support

| Framework/Tech               | Matt                           | Addy                                                         |
| ---------------------------- | ------------------------------ | ------------------------------------------------------------ |
| **TypeScript**               | ✅ First-class                 | ✅ Supported                                                 |
| **React**                    | ✅ Implied                     | ✅ `frontend-ui-engineering`, `testing-patterns.md` examples |
| **Next.js**                  | ✅ Generic                     | ✅ Better với performance + Core Web Vitals                  |
| **Node.js**                  | ✅ Generic                     | ✅ Supported                                                 |
| **Python**                   | ✅ Generic (language-agnostic) | ✅ Generic (JS references irrelevant)                        |
| **Go / Rust**                | ✅ Generic                     | ✅ Generic                                                   |
| **REST API**                 | ❌ Không có skill riêng        | ✅ `api-and-interface-design`                                |
| **GraphQL**                  | ❌ Không có skill riêng        | ✅ `api-and-interface-design`                                |
| **Browser-based**            | ❌ Không có                    | ✅ `browser-testing-with-devtools`                           |
| **CI/CD (GH Actions, etc.)** | ❌ Không có                    | ✅ `ci-cd-and-automation`                                    |

## 7. Compatibility: Dùng Cả Hai Cùng Lúc?

**Official guidance từ Addy:** Bạn CÓ THỂ cherry-pick skills từ hai bộ (plain Markdown), nhưng **KHÔNG nên chạy cả hai làm active router cùng lúc** vì:

- Conflict về command names (e.g., `/tdd` có thể define ở cả hai)
- Hai routing logic cạnh tranh nhau
- Hai TDD philosophies khác nhau → unpredictable behavior

**Cách kết hợp an toàn:**

```
[Primary router]  +  [Cherry-picked skills]
addyosmani        +  Matt's grill-me (grilling rounds)
addyosmani        +  Matt's domain-modeling (CONTEXT.md)
mattpocock        +  Addy's security-and-hardening
mattpocock        +  Addy's observability-and-instrumentation
```

Chọn **một framework làm primary router**, borrowing individual skills từ bên kia à la carte.

## 8. Kết Luận: Nên Chọn Gì?

### Chọn **mattpocock/skills** nếu:

- Đang làm TypeScript project, đặc biệt libraries/packages
- Cần daily toolkit nhỏ gọn, ít ceremony
- Muốn proactive architecture health check
- Đang refactor codebase cũ → CONTEXT.md giúp xây shared language
- Chỉ dùng Claude Code
- Solo developer hoặc team nhỏ < 3 người
- Cần session continuity tốt (handoff, teach)
- Design tree grilling là priority (rounds-based interrogation)

### Chọn **addyosmani/agent-skills** nếu:

- Cần full production lifecycle với quality gates
- Làm frontend web app hoặc API cần security/performance
- Team dùng nhiều tools khác nhau (Cursor, Copilot, Gemini...)
- Enterprise project cần audit trail (ADRs, review personas)
- Greenfield app cần go production
- Cần security review nghiêm túc (OWASP)
- Muốn `/build auto` autonomous mode
- Team > 3 người với code review workflow

### Kết hợp tốt nhất (không conflict):

```
Primary: addyosmani/agent-skills  (lifecycle + production concerns)
Add-on:  Matt's grilling rounds   (cho requirements interrogation sâu hơn)
Add-on:  Matt's CONTEXT.md        (cho shared language building)
Add-on:  Matt's /improve-codebase-architecture  (cho architecture health)
```

## 9. So Sánh Nhanh: Khi Nào Invoke Gì

| Tình huống                | mattpocock                                                     | addyosmani                   |
| ------------------------- | -------------------------------------------------------------- | ---------------------------- |
| Yêu cầu mơ hồ             | `/grill-with-docs`                                             | `/spec` → `interview-me`     |
| Bắt đầu feature           | `/grill-with-docs` → `/to-spec` → `/to-tickets` → `/implement` | `/spec` → `/plan` → `/build` |
| Bug khó diagnose          | Nói "diagnose this"                                            | `/test`                      |
| Muốn review code          | Auto qua `/implement` → `code-review`                          | `/review`                    |
| Deploy production         | Không có                                                       | `/ship`                      |
| Architecture cleanup      | `/improve-codebase-architecture`                               | `/review` + `/code-simplify` |
| Không biết dùng skill nào | `/ask-matt`                                                    | `using-agent-skills` (auto)  |
| Context window đầy        | `/handoff`                                                     | Không có tương đương         |
