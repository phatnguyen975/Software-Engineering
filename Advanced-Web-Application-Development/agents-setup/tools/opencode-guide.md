# OpenCode — Complete Guide for Ubuntu (WSL) + Neovim + Tmux Workflow

> **Audience:** Developer familiar with the terminal who wants to integrate OpenCode as an AI agent into a Neovim + Tmux workflow, using Tmux panes/windows instead of any Neovim plugin.
> **Sources:** Official docs at `opencode.ai/docs` (verified against the live docs, last updated mid-June 2026), the official `Agent Skills` specification, and the OpenCode GitHub repository (`anomalyco/opencode`).
> **Note on versions:** OpenCode ships frequent releases. Where the docs reference a specific behavior version (e.g. "as of v1.1.1"), that's noted inline. Always cross-check with `opencode --version` and `/help` in your installed build, since flags can shift between releases.

## Table of Contents

1. [What is OpenCode?](#1-what-is-opencode)
2. [Installation on Ubuntu / WSL2](#2-installation-on-ubuntu--wsl2)
3. [First Run & Provider Login](#3-first-run--provider-login)
4. [Configuration Files — Where & What](#4-configuration-files--where--what)
5. [Project Directory Structure](#5-project-directory-structure)
6. [Global Directory Structure](#6-global-directory-structure)
7. [Rules — AGENTS.md](#7-rules--agentsmd)
8. [Agents — Creating & Configuring](#8-agents--creating--configuring)
9. [Skills — Reusable AI Instructions](#9-skills--reusable-ai-instructions)
10. [Custom Commands](#10-custom-commands)
11. [Permissions — Whitelisting & Denying](#11-permissions--whitelisting--denying)
12. [Session Management](#12-session-management)
13. [All TUI Slash Commands](#13-all-tui-slash-commands)
14. [Full CLI Reference](#14-full-cli-reference)
15. [Environment Variables](#15-environment-variables)
16. [MCP Servers](#16-mcp-servers)
17. [Neovim + Tmux Integration Workflow](#17-neovim--tmux-integration-workflow)
18. [Tmux Layout Setup Script](#18-tmux-layout-setup-script)
19. [Best Practices & Pro Tips](#19-best-practices--pro-tips)
20. [Troubleshooting](#20-troubleshooting)

## 1. What is OpenCode?

OpenCode (by Anomaly, formerly under the SST umbrella) is an **open-source, terminal-first AI coding agent**. It gives you:

- A **TUI** (Terminal User Interface) for interactive sessions — the primary way most people use it day to day.
- A **CLI** for scripted / automation / headless use (`opencode run "..."`).
- A **Web UI** (`opencode web`) and a **server mode** (`opencode serve`) for programmatic access.
- Support for a wide range of LLM providers (Anthropic Claude, OpenAI GPT, Google Gemini, DeepSeek, local models via Ollama/LM Studio, and OpenCode's own hosted "Zen" models).
- A **multi-agent architecture**: primary agents you talk to directly (`build`, `plan`), and subagents they can delegate to (`general`, `explore`, `scout`).
- **Skills, custom commands, custom agents, plugins, and MCP servers** — the extensibility layer that lets you turn one-off prompts into reusable, shareable workflows.
- **Session persistence** via a local SQLite database, with parent/child session trees for subagent work.
- **Git-based snapshots** so file edits made by the agent can be undone/redone like a checkpoint system.
- **Compatibility with Claude Code's file conventions** (`AGENTS.md` falls back to `CLAUDE.md`, skills are also discovered under `.claude/skills/`), which matters if you or your team already uses Claude Code.

Because it lives entirely in the terminal and doesn't require an editor plugin, OpenCode pairs naturally with a Neovim + Tmux setup: you treat it as a separate, file-system-aware collaborator running in its own pane.

## 2. Installation on Ubuntu / WSL2

### 2.1 Prerequisites

```bash
# Confirm you're on WSL2, not WSL1 (run from PowerShell):
# wsl -l -v   →  should show VERSION 2

sudo apt update && sudo apt upgrade -y
sudo apt install -y curl git build-essential unzip

# Node.js via NVM (only needed if you install via npm/bun)
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
source ~/.bashrc
nvm install 20
nvm use 20
node --version   # v18+ required, v20 LTS recommended
```

> **WSL filesystem tip:** Always keep your projects inside the Linux filesystem (`~/projects/...`), not under `/mnt/c/...`. File I/O is dramatically faster, file-watchers behave correctly, and git hooks won't intermittently fail.

### 2.2 Install OpenCode (pick one)

**Method A — install script (simplest, recommended):**

```bash
curl -fsSL https://opencode.ai/install | bash
```

Auto-detects your platform and installs the binary, typically to `~/.local/bin/opencode` or `~/.opencode/bin/opencode` depending on version — the installer prints the exact path at the end.

**Method B — npm:**

```bash
npm install -g opencode-ai
```

**Method C — bun:**

```bash
bun install -g opencode-ai
```

**Method D — Homebrew (Linuxbrew):**

```bash
brew install sst/tap/opencode
```

### 2.3 Verify installation

```bash
opencode --version
opencode --help
```

### 2.4 Make sure it's on PATH

```bash
# Add to ~/.bashrc (or ~/.zshrc) if the installer didn't already:
export PATH="$HOME/.local/bin:$PATH"
source ~/.bashrc
```

### 2.5 Set your editor (important for this workflow)

```bash
# ~/.bashrc
export EDITOR="nvim"
```

This makes `/editor` (compose a long prompt) and `/export` (export a session) open in Neovim instead of falling back to `vi`/`nano`.

### 2.6 Keep it updated

```bash
opencode upgrade            # upgrade to latest
opencode upgrade v1.x.x     # pin to a specific version
```

## 3. First Run & Provider Login

### 3.1 Authenticate with a provider

```bash
opencode auth login
```

This launches an interactive picker: choose your provider (Anthropic, OpenAI, Google, etc.), then paste your API key (or complete an OAuth flow if the provider supports it, e.g. "Claude Pro/Max" login). Credentials are written to:

```
~/.local/share/opencode/auth.json
```

**Log in to a specific provider directly:**

```bash
opencode auth login --provider anthropic
```

**List authenticated providers:**

```bash
opencode auth list
```

**Log out:**

```bash
opencode auth logout
opencode auth logout --provider openai
```

### 3.2 Alternative — environment variables

```bash
# ~/.bashrc
export ANTHROPIC_API_KEY="sk-ant-..."
export OPENAI_API_KEY="sk-..."
export GEMINI_API_KEY="..."
```

A project-local `.env` file is also picked up automatically when present.

### 3.3 Launch the TUI

```bash
cd ~/projects/my-app
opencode
```

Or point it at a specific directory without `cd`-ing first:

```bash
opencode ~/projects/my-app
```

### 3.4 Verify model access

Inside the TUI:

```
/models
```

This lists every model your authenticated providers expose, confirming the login actually worked end-to-end.

## 4. Configuration Files — Where & What

### 4.1 Config precedence (lowest → highest priority; later entries override conflicting keys, everything else merges)

| Priority | Location                                | Purpose                                         |
| -------- | --------------------------------------- | ----------------------------------------------- |
| 1        | Remote `.well-known/opencode`           | Org-wide defaults pushed by an enterprise admin |
| 2        | `~/.config/opencode/opencode.json`      | **Global user config**                          |
| 3        | `OPENCODE_CONFIG` env var (custom path) | Alternate/override config file                  |
| 4        | `opencode.json` in the project root     | **Project-specific config**                     |
| 5        | `OPENCODE_CONFIG_CONTENT` env var       | Inline JSON injected at runtime                 |
| 6        | `/etc/opencode/opencode.json`           | System/admin-managed config (rare, enterprise)  |

> Configs are **merged** key by key, not replaced wholesale — a project's `opencode.json` only needs to specify what it wants to override.

### 4.2 Key files and where they live

| File                     | Location                                                | Purpose                                                                   |
| ------------------------ | ------------------------------------------------------- | ------------------------------------------------------------------------- |
| `opencode.json`          | `~/.config/opencode/opencode.json`                      | Global runtime config: model defaults, permissions, MCP, agents, commands |
| `opencode.json`          | `<project>/opencode.json`                               | Project overrides (model, permissions, MCP specific to this repo)         |
| `AGENTS.md`              | `~/.config/opencode/AGENTS.md`                          | Global rules applied to every session on this machine                     |
| `AGENTS.md`              | `<project>/AGENTS.md`                                   | Project rules — committed to git, shared with your team                   |
| `auth.json`              | `~/.local/share/opencode/auth.json`                     | API keys/OAuth tokens (managed via `opencode auth login`)                 |
| `mcp-auth.json`          | `~/.local/share/opencode/mcp-auth.json`                 | OAuth tokens for remote MCP servers                                       |
| `opencode.db`            | `~/.local/share/opencode/opencode.db`                   | SQLite database holding **all session history**                           |
| `agents/*.md`            | `~/.config/opencode/agents/` or `.opencode/agents/`     | Markdown-defined custom agents                                            |
| `commands/*.md`          | `~/.config/opencode/commands/` or `.opencode/commands/` | Markdown-defined custom slash commands                                    |
| `skills/<name>/SKILL.md` | `~/.config/opencode/skills/` or `.opencode/skills/`     | Reusable, on-demand skill definitions                                     |

### 4.3 Global `opencode.json` — annotated full example

```jsonc
// ~/.config/opencode/opencode.json
{
  "$schema": "https://opencode.ai/config.json",

  // --- Default model used by primary agents unless an agent overrides it ---
  "model": "anthropic/claude-sonnet-4-20250514",

  // --- Cheaper/faster model used internally for title-generation, compaction, etc. ---
  "small_model": "anthropic/claude-haiku-4-20250514",

  // --- Provider-level tuning ---
  "provider": {
    "anthropic": {
      "options": {
        "timeout": 600000, // 10 minutes, useful for very long agentic runs
      },
    },
  },

  // --- Shell used for the bash tool ---
  "shell": "/bin/bash",

  // --- Update behavior: true | false | "notify" ---
  "autoupdate": true,

  // --- Session sharing: "manual" | "auto" | "disabled" ---
  "share": "manual",

  // --- Permissions: see Section 11 for the full reference ---
  "permission": {
    "*": "ask",
    "bash": {
      "*": "ask",
      "git status*": "allow",
      "git diff*": "allow",
      "git log*": "allow",
      "rm -rf *": "deny",
      "sudo *": "deny",
    },
    "read": {
      "*": "allow",
      "*.env": "deny",
      "*.env.*": "deny",
      "*.env.example": "allow",
    },
  },

  // --- Extra files merged into every AGENTS.md context (Section 7) ---
  "instructions": [],

  // --- MCP servers (Section 16) ---
  "mcp": {},

  // --- Custom agents / commands can also be declared here instead of Markdown (Sections 8 & 10) ---
  "agent": {},
  "command": {},
}
```

> There is no separate global `tui.json` requirement — TUI-only settings (theme, keybinds) can live in the same `opencode.json` under their own keys, or you can keep them split across files if you prefer; what matters is that both live under `~/.config/opencode/` and merge the same way.

## 5. Project Directory Structure

This is the canonical layout for a project using OpenCode. The `.opencode/` directory holds everything project-specific.

```
my-project/
├── .opencode/                        # OpenCode project config root
│   │
│   ├── agents/                       # Project-specific custom agents
│   │   ├── reviewer.md
│   │   └── debugger.md
│   │
│   ├── commands/                     # Project-specific custom slash commands
│   │   ├── test.md
│   │   ├── review-pr.md
│   │   └── deploy.md
│   │
│   ├── skills/                       # Project-specific reusable skills
│   │   ├── git-release/
│   │   │   ├── SKILL.md              # Required — must be named exactly SKILL.md
│   │   │   ├── scripts/              # Optional — executable helper scripts
│   │   │   │   ├── bump-version.sh
│   │   │   │   └── generate-changelog.py
│   │   │   ├── references/           # Optional — extra docs loaded only when needed
│   │   │   │   ├── versioning-policy.md
│   │   │   │   └── changelog-format.md
│   │   │   └── assets/               # Optional — templates, static files
│   │   │       └── release-notes.template.md
│   │   │
│   │   └── api-docs/
│   │       ├── SKILL.md
│   │       └── references/
│   │           └── openapi-conventions.md
│   │
│   └── plugins/                      # Project-specific plugins (.js/.ts)
│       └── my-plugin.js
│
├── AGENTS.md                         # Project rules — MOST IMPORTANT FILE
│                                      # Always loaded; describes architecture,
│                                      # conventions, and commands to run.
│                                      # Generate/update with /init.
│
├── opencode.json                     # Project-level config overrides (safe to commit)
│
├── src/                              # Your actual project code
├── tests/
├── README.md
└── package.json
```

### 5.1 Important correction on the skills subdirectory

A skill is **not just a single `SKILL.md` file** — it's a folder, and that folder can contain arbitrary supporting material alongside `SKILL.md`. The official structure (matching the Agent Skills spec used by OpenCode, Claude Code, and Codex) is:

```
<skill-name>/
├── SKILL.md           # Required. Frontmatter (name, description, ...) + instructions body.
├── scripts/           # Optional. Executable code (bash/python/node) the agent can run.
│                      # OpenCode discovers scripts recursively (up to depth 10),
│                      # identified by the executable bit — they don't strictly
│                      # need to live in a folder literally named scripts/,
│                      # but doing so keeps things organized and discoverable.
├── references/        # Optional. Extra documentation loaded ONLY when the agent
│                      # decides it needs the detail — keeps the main SKILL.md short.
└── assets/            # Optional. Templates, boilerplate files, static config,
                       # anything the skill's instructions tell the agent to copy/use.
```

**Why this matters (progressive disclosure):** the agent only ever sees the skill's `name` + `description` until it decides to load the skill (via the `skill` tool). Once loaded, it gets the full body of `SKILL.md`. Anything inside `references/`, `scripts/`, or `assets/` is **not** auto-injected — the agent reads/runs those files explicitly, by path, only if `SKILL.md`'s instructions point it there. This is what keeps a "fat" skill (with 5 reference docs and 3 scripts) cheap on context: you only pay for what's actually used in a given task.

> Folder names `scripts/`, `references/`, `assets/` are **conventions**, not hard requirements — OpenCode's discovery doesn't enforce specific subfolder names for non-SKILL.md files (only `SKILL.md` itself must be named exactly that, in all caps). Using these names is still strongly recommended for consistency with Claude Code/Codex-compatible skills and for human readability.

### 5.2 File/folder meaning summary

| Path                                  | Meaning                                                            |
| ------------------------------------- | ------------------------------------------------------------------ |
| `.opencode/agents/*.md`               | Custom agents available only in this project                       |
| `.opencode/commands/*.md`             | Custom `/commands` available only in this project                  |
| `.opencode/skills/<name>/SKILL.md`    | Required entry point for a skill                                   |
| `.opencode/skills/<name>/scripts/`    | Executable helpers the skill's instructions can invoke             |
| `.opencode/skills/<name>/references/` | Deep-dive docs loaded on demand, not by default                    |
| `.opencode/skills/<name>/assets/`     | Templates/boilerplate the skill tells the agent to use             |
| `.opencode/plugins/`                  | Plugin files or references to npm packages                         |
| `AGENTS.md` (project root)            | Always-loaded project rules: architecture, standards, commands     |
| `opencode.json` (project root)        | Project config: model override, permissions, MCP, agents, commands |

## 6. Global Directory Structure

Global config applies to **every project** on your machine, layered underneath whatever the project defines.

```
~/.config/opencode/                   # Global OpenCode config root
├── opencode.json                     # Global runtime config
├── AGENTS.md                         # Global rules applied to ALL sessions
│                                      # (personal preferences, never committed to any repo)
│
├── agents/                           # Global custom agents (available in every project)
│   ├── security-auditor.md
│   ├── docs-writer.md
│   └── code-reviewer.md
│
├── commands/                         # Global custom slash commands
│   ├── standup.md
│   └── explain-error.md
│
├── skills/                           # Global reusable skills (full subfolder support)
│   ├── conventional-commits/
│   │   └── SKILL.md
│   ├── code-review-checklist/
│   │   ├── SKILL.md
│   │   └── references/
│   │       └── owasp-top-10.md
│   └── docker-best-practices/
│       ├── SKILL.md
│       ├── scripts/
│       │   └── lint-dockerfile.sh
│       └── assets/
│           └── Dockerfile.template
│
└── plugins/                          # Global plugins
    └── my-global-plugin.js

~/.local/share/opencode/              # OpenCode runtime data (not config — don't hand-edit)
├── auth.json                         # API keys / OAuth tokens (managed by `opencode auth login`)
├── mcp-auth.json                     # OAuth tokens for remote MCP servers
├── opencode.db                       # SQLite database — ALL session history
└── logs/                             # Debug logs
```

### 6.1 Claude Code compatibility paths (fallbacks, useful if you also use Claude Code)

| Type          | Project path                                     | Global path                                                                   |
| ------------- | ------------------------------------------------ | ----------------------------------------------------------------------------- |
| Rules         | `CLAUDE.md` (used only if no `AGENTS.md` exists) | `~/.claude/CLAUDE.md` (used only if no `~/.config/opencode/AGENTS.md` exists) |
| Skills        | `.claude/skills/<name>/SKILL.md`                 | `~/.claude/skills/<name>/SKILL.md`                                            |
| Skills (alt.) | `.agents/skills/<name>/SKILL.md`                 | `~/.agents/skills/<name>/SKILL.md`                                            |

To disable these fallbacks entirely:

```bash
export OPENCODE_DISABLE_CLAUDE_CODE=1          # disable all .claude/* support
export OPENCODE_DISABLE_CLAUDE_CODE_PROMPT=1   # disable only ~/.claude/CLAUDE.md
export OPENCODE_DISABLE_CLAUDE_CODE_SKILLS=1   # disable only .claude/skills
```

### 6.2 How global vs. project config works together

- Global config = your personal defaults across all projects (model preference, communication style, personal skills).
- Project config = overrides/additions specific to that repo (stricter permissions, project-specific agents/skills/commands).
- `AGENTS.md` files are **all loaded and combined** — global + project + any `instructions` you've added — they don't override each other, they stack.
- `opencode.json` files **merge** key by key, with project values winning on conflict.

## 7. Rules — AGENTS.md

`AGENTS.md` is OpenCode's equivalent of Cursor's rules files. It's **always loaded into context** at session start, so it's the right place for things every single task needs to know — never for deep-dive reference material (that belongs in a skill, see Section 9).

### 7.1 Generate or update it with `/init`

```
/init
```

`/init` scans your repo, may ask a couple of targeted questions when the codebase itself can't answer them, and then **creates or updates `AGENTS.md` in place** (it won't blindly overwrite an existing file — it improves it). It specifically looks for:

- build, lint, and test commands
- command order and focused verification steps when they matter
- architecture/repo structure not obvious from filenames alone
- project-specific conventions, setup quirks, operational gotchas
- references to existing instruction sources like Cursor or Copilot rules

> **Commit `AGENTS.md` to git.** It's meant to be shared with your whole team, exactly like a Cursor rules file.

### 7.2 Discovery order & precedence

When OpenCode starts, it looks for rule files in this order, and **the first match in each category wins** (they don't stack within a category, but project + global + Claude fallback all combine across categories):

1. **Local files**, by traversing up from the current directory: `AGENTS.md`, then `CLAUDE.md` as fallback if no `AGENTS.md` exists.
2. **Global file**: `~/.config/opencode/AGENTS.md`.
3. **Claude Code fallback**: `~/.claude/CLAUDE.md`, used only if no `~/.config/opencode/AGENTS.md` exists.

So if you have both `AGENTS.md` and `CLAUDE.md` in a project, only `AGENTS.md` is read. Likewise, `~/.config/opencode/AGENTS.md` always wins over `~/.claude/CLAUDE.md` at the global level.

### 7.3 `AGENTS.md` template (project-level) — what sections to include

A good `AGENTS.md` reads like an onboarding doc for a new senior engineer joining the team for one afternoon. Recommended sections, in order of how often the agent actually needs them:

```markdown
# My Project — OpenCode Rules

## Project Overview

One paragraph: what this project is, who it's for, what it does.

## Repository Structure

- `src/api/` — Route handlers
- `src/db/` — Database models & migrations (Drizzle ORM)
- `infra/` — SST infrastructure definitions
- `tests/` — Unit and integration tests (Vitest)

## Tech Stack

- Runtime: Node.js 20, TypeScript 5 (strict mode)
- Framework: Express 5
- Database: PostgreSQL 16 via Drizzle ORM
- Package manager: pnpm — never npm or yarn
- Linter/Formatter: ESLint + Prettier

## Critical Commands

- `pnpm dev` — start dev server
- `pnpm build` — build for production
- `pnpm test` — run tests
- `pnpm lint` — lint code
- `pnpm db:migrate` — run DB migrations

## Code Standards

- Strict TypeScript everywhere — never use `any`
- Always handle async errors explicitly
- Named exports only, no default exports
- Every API endpoint needs Zod input validation
- SQL queries live in `src/db/queries/`, never inline in route handlers

## Git Conventions

- Conventional Commits: feat|fix|chore|docs|test|refactor
- Branch naming: `feat/description`, `fix/description`
- Never commit directly to `main`
- Always run `pnpm lint && pnpm test` before committing

## Things to NEVER Do

- Never delete migrations — always create a new one
- Never bypass `config.ts` to read `process.env` directly
- Never run `pnpm install` without explicit approval
- Never modify `.env` files
```

Keep this file **under ~200 lines**. It's injected on every single message in every session — bloat here is a recurring tax on every turn's token usage and cost. Anything you'd only need occasionally (a deep API reference, a migration runbook, a security checklist) belongs in a **skill** instead (Section 9), which loads only on demand.

### 7.4 Global `AGENTS.md` — personal preferences (not shared, not committed)

```markdown
# ~/.config/opencode/AGENTS.md

# Personal rules applied to ALL my projects, on this machine only.

## My Preferences

- Prefer functional style over OOP where it's a reasonable fit
- Always use async/await, never raw .then() chains
- Prefer explicit types over inference in public function signatures
- When proposing code changes, always include error handling, don't leave it as a TODO
- Keep functions small — flag anything over ~30 lines for a possible split

## Communication Style

- Be concise — prefer showing a diff over a long prose explanation
- Ask before making large structural changes (renames across many files, new dependencies)
- Summarize what changed at the end of a multi-step task
```

### 7.5 Custom instruction files via `opencode.json`

If you have existing rule files (Cursor rules, contribution guidelines, monorepo package-level docs) you don't want to duplicate into `AGENTS.md`, reference them directly:

```jsonc
// opencode.json
{
  "$schema": "https://opencode.ai/config.json",
  "instructions": [
    "CONTRIBUTING.md",
    "docs/guidelines.md",
    ".cursor/rules/*.md",
    "packages/*/AGENTS.md", // glob — great for monorepos
    "https://raw.githubusercontent.com/my-org/shared-rules/main/style.md",
  ],
}
```

Remote URLs are fetched with a 5-second timeout. All instruction files listed here are combined with whatever `AGENTS.md` files were discovered — they stack, they don't replace each other. For monorepos, prefer this glob-based approach over manually listing every package; it's far more maintainable as the repo grows.

### 7.6 Lazy-loading large reference docs from inside AGENTS.md

OpenCode does **not** automatically parse `@file` references written inside `AGENTS.md` content (that syntax is for command/chat prompts, not rule files). If you want agent-driven, need-to-know loading of large reference docs without bloating `AGENTS.md` itself, you can explicitly instruct the agent to do so:

```markdown
# TypeScript Project Rules

## External File Loading

CRITICAL: When you encounter a file reference (e.g., @rules/general.md), use your
Read tool to load it on a need-to-know basis. They're relevant to the SPECIFIC task
at hand — do NOT preemptively load all references.

## Development Guidelines

For TypeScript style and best practices: @docs/typescript-guidelines.md
For React component architecture and hooks patterns: @docs/react-patterns.md
For REST API design and error handling: @docs/api-standards.md
```

This pattern is a reasonable fallback, but in most cases a proper **skill** (Section 9) is the cleaner mechanism for "deep reference material loaded only when relevant," since skills are a first-class, model-aware feature rather than a convention you have to teach the model yourself.

## 8. Agents — Creating & Configuring

### 8.1 Two types of agents

- **Primary agents** — the ones you talk to directly. Cycle through them with **Tab** (or your configured `switch_agent` keybind). Their tool access is governed entirely by `permission`, not by a separate "primary vs. subagent" tool list.
- **Subagents** — specialized assistants that primary agents can delegate to, or that you invoke manually with `@mention`.

### 8.2 Built-in agents

| Agent        | Mode                | Description                                                                                                                                                                                                                                  |
| ------------ | ------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `build`      | primary             | **Default** agent. All tools enabled — full file read/write and bash access.                                                                                                                                                                 |
| `plan`       | primary             | Restricted agent for analysis. By default, **all** file edits and **all** bash commands are set to `ask`, so it can propose changes without silently making them.                                                                            |
| `general`    | subagent            | General-purpose agent for researching complex questions and multi-step tasks. Has full tool access **except `todo`**, so it can make file changes when genuinely needed. Good for running multiple units of work in parallel.                |
| `explore`    | subagent            | Fast, **read-only** codebase explorer. Cannot modify files. Use it to find files by pattern, search code for keywords, or answer "where does X happen" questions without touching anything.                                                  |
| `scout`      | subagent            | **Read-only** agent for external docs and dependency research — clone a dependency into OpenCode's managed cache, inspect library source, cross-reference your code against an upstream implementation, all without touching your workspace. |
| `compaction` | hidden system agent | Compacts long context into a summary automatically. Not selectable in the UI.                                                                                                                                                                |
| `title`      | hidden system agent | Auto-generates short session titles. Not selectable.                                                                                                                                                                                         |
| `summary`    | hidden system agent | Creates session summaries. Not selectable.                                                                                                                                                                                                   |

### 8.3 Using agents

**Switch primary agent:** press `Tab` during a session.

**Invoke a subagent manually:**

```
@explore find every file that imports from utils/auth.ts
@general implement the OAuth flow described in docs/oauth.md
@scout check how the upstream `drizzle-orm` package implements relational queries
```

**Navigate parent/child sessions** once a subagent has been spawned:

| Keybind (default) | Action                                        |
| ----------------- | --------------------------------------------- |
| `<Leader>+Down`   | Enter the first child session from the parent |
| `Right`           | Cycle to the next sibling child session       |
| `Left`            | Cycle to the previous sibling child session   |
| `Up`              | Return to the parent session                  |

### 8.4 Configure agents — JSON (in `opencode.json`)

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "agent": {
    "build": {
      "mode": "primary",
      "model": "anthropic/claude-sonnet-4-20250514",
      "prompt": "{file:./prompts/build.txt}",
      "permission": {
        "edit": "allow",
        "bash": "allow",
      },
    },
    "plan": {
      "mode": "primary",
      "model": "anthropic/claude-haiku-4-20250514",
      "permission": {
        "edit": "deny",
        "bash": "deny",
      },
    },
    "code-reviewer": {
      "description": "Reviews code for best practices and potential issues",
      "mode": "subagent",
      "model": "anthropic/claude-sonnet-4-20250514",
      "prompt": "You are a code reviewer. Focus on security, performance, and maintainability.",
      "permission": {
        "edit": "deny",
      },
    },
  },
}
```

### 8.5 Configure agents — Markdown (recommended for anything beyond a one-liner prompt)

Place files in:

- Global: `~/.config/opencode/agents/<name>.md`
- Per-project: `.opencode/agents/<name>.md`

The **filename becomes the agent name** (`review.md` → `@review` / a `review` primary agent if `mode: primary`).

```markdown
---
description: Reviews code for quality, security, and performance without making changes
mode: subagent
model: anthropic/claude-sonnet-4-20250514
temperature: 0.1
permission:
  edit: deny
  bash:
    "*": ask
    "git diff*": allow
    "git log*": allow
    "grep *": allow
  webfetch: deny
---

You are in code review mode. Focus on:

- Code quality and best practices
- Potential bugs and edge cases
- Performance implications
- Security considerations (SQL injection, XSS, hardcoded secrets, overly
  permissive access controls)

Provide constructive feedback without making direct changes. Structure your
review as:

1. **Summary** (2-3 sentences)
2. **Critical Issues** (must fix)
3. **Suggestions** (should fix)
4. **Minor Notes** (optional)
```

### 8.6 Full option reference

| Option        | Type                         | Required           | Description                                                                                                                                                                                                                                                                                                                                            |
| ------------- | ---------------------------- | ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `description` | string                       | **Yes**            | What the agent does and when to use it. Shown in `@` autocomplete and used by primary agents to decide when to delegate to this subagent.                                                                                                                                                                                                              |
| `mode`        | `"primary"` \| `"subagent"`  | No                 | How the agent can be invoked. Defaults based on context.                                                                                                                                                                                                                                                                                               |
| `model`       | string (`provider/model-id`) | No                 | Override the model for this agent. If unset, primary agents use the globally configured model; subagents inherit the model of whichever primary agent invoked them.                                                                                                                                                                                    |
| `temperature` | 0.0–1.0                      | No                 | Randomness control. **0.0–0.2**: focused/deterministic, ideal for analysis and planning. **0.3–0.5**: balanced, good for general dev work. **0.6–1.0**: creative/varied, useful for brainstorming. If unset, OpenCode uses model defaults (typically 0, or 0.55 for Qwen models).                                                                      |
| `top_p`       | 0.0–1.0                      | No                 | Alternative/complementary sampling control to temperature.                                                                                                                                                                                                                                                                                             |
| `steps`       | number                       | No                 | Max agentic iterations before the agent is forced to respond with text only (a cost-control lever). If unset, the agent iterates until the model stops or you interrupt. When the limit is hit, the agent gets a system prompt nudging it to summarize work done and list remaining tasks. _(The older `maxSteps` field is deprecated — use `steps`.)_ |
| `disable`     | boolean                      | No                 | Set `true` to disable the agent entirely.                                                                                                                                                                                                                                                                                                              |
| `prompt`      | string                       | No                 | Path to a custom system prompt file, e.g. `{file:./prompts/code-review.txt}`. Path resolves relative to wherever the config file lives (works the same for global and project config).                                                                                                                                                                 |
| `permission`  | object                       | No                 | Per-tool permission overrides for this agent — see Section 11 for the full key list. Agent-level rules are merged with and take precedence over the global `permission` config.                                                                                                                                                                        |
| `tools`       | object                       | No, **deprecated** | Legacy boolean tool toggles (`{"write": false}`). Prefer `permission` for new configs; `true`/`false` map to `{"*":"allow"}`/`{"*":"deny"}` respectively. Still useful for quickly disabling an entire MCP server with a wildcard, e.g. `{"mymcp_*": false}`.                                                                                          |
| `hidden`      | boolean                      | No                 | Hide a subagent from `@` autocomplete (it can still be invoked programmatically/by other agents).                                                                                                                                                                                                                                                      |
| `color`       | string                       | No                 | UI color for this agent (`#hex` or a named token like `primary`, `accent`).                                                                                                                                                                                                                                                                            |

### 8.7 Practical example — a security auditor agent

```markdown
---
description: Audits code changes for security vulnerabilities before merge. Use for anything touching auth, payments, user input, or external APIs.
mode: subagent
model: anthropic/claude-sonnet-4-20250514
temperature: 0.1
steps: 15
permission:
  edit: deny
  bash:
    "*": deny
    "git diff*": allow
    "git log*": allow
    "grep *": allow
  webfetch: ask
---

You are a senior application security auditor. Review the code provided for:

- Injection vulnerabilities (SQL, command, XSS, SSRF)
- Broken authentication / authorization checks
- Hardcoded secrets, API keys, or credentials
- Insecure deserialization
- Missing input validation on external-facing endpoints
- Dependency vulnerabilities you recognize by name/version

Never modify files. Output a numbered list of findings, each with:
severity (Critical/High/Medium/Low), file:line, and a concrete fix suggestion.
If you find nothing, say so explicitly rather than padding the response.
```

### 8.8 Practical example — a documentation-writer agent

```markdown
---
description: Writes and updates README, API docs, and inline JSDoc/TSDoc comments. Use after a feature is implemented and tested.
mode: subagent
model: anthropic/claude-haiku-4-20250514
temperature: 0.3
permission:
  edit:
    "*.md": allow
    "**/*.ts": ask
    "*": deny
  bash:
    "git diff*": allow
    "*": deny
---

You write clear, concise developer documentation. Match the existing tone and
formatting of the surrounding docs. Prefer examples over abstract descriptions.
Never invent behavior that isn't actually in the code — if you're unsure what
something does, say so instead of guessing.
```

---

## 9. Skills — Reusable AI Instructions

Skills are **on-demand instruction modules**. Unlike `AGENTS.md` (always injected), a skill's full content is only loaded when the agent calls the native `skill` tool — the agent sees just the `name` + `description` upfront, and decides whether the current task matches.

### 9.1 Discovery locations

OpenCode searches these paths, in this order (project paths are found by walking up from your current working directory until it hits the git worktree root):

| Location                                    | Scope                                    |
| ------------------------------------------- | ---------------------------------------- |
| `.opencode/skills/<name>/SKILL.md`          | Project                                  |
| `~/.config/opencode/skills/<name>/SKILL.md` | Global                                   |
| `.claude/skills/<name>/SKILL.md`            | Project (Claude Code compatibility)      |
| `~/.claude/skills/<name>/SKILL.md`          | Global (Claude Code compatibility)       |
| `.agents/skills/<name>/SKILL.md`            | Project (cross-tool "agents" convention) |
| `~/.agents/skills/<name>/SKILL.md`          | Global (cross-tool "agents" convention)  |

### 9.2 Full skill folder structure

```
<skill-name>/
├── SKILL.md           # Required — frontmatter + instructions body
├── scripts/           # Optional — executable code (discovered recursively, depth ≤ 10, identified by the executable bit, not by folder name)
│   ├── validate.py
│   └── deploy.sh
├── references/         # Optional — supplementary docs, read by the agent only when needed
│   ├── api-guide.md
│   └── troubleshooting.md
└── assets/             # Optional — templates, boilerplate, static config
    └── config.template.json
```

The directory name and the frontmatter `name` are usually identical, but technically **the frontmatter `name` is the canonical identifier** — directory names are for human organization and don't have to match exactly (though keeping them in sync avoids confusion and is the recommended convention).

### 9.3 Frontmatter — recognized fields

Each `SKILL.md` **must start with YAML frontmatter**. Only these fields are recognized (unknown fields are silently ignored):

| Field           | Required | Description                                                                                                                                                                                      |
| --------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `name`          | **Yes**  | 1–64 chars, lowercase alphanumeric with single-hyphen separators. Must match regex `^[a-z0-9]+(-[a-z0-9]+)*$` — no leading/trailing hyphen, no `--`. Should match the containing directory name. |
| `description`   | **Yes**  | 1–1024 chars. **This is the only signal the agent uses to decide whether to load the skill** — be specific about exactly when (and when not) to use it.                                          |
| `license`       | No       | e.g. `MIT`                                                                                                                                                                                       |
| `compatibility` | No       | e.g. `opencode`                                                                                                                                                                                  |
| `metadata`      | No       | Free-form string-to-string map for your own bookkeeping (audience, workflow, owner, etc.)                                                                                                        |

### 9.4 Full worked example

`.opencode/skills/git-release/SKILL.md`:

```markdown
---
name: git-release
description: Create consistent releases and changelogs. Use this when preparing a tagged release — drafting release notes, proposing a version bump, or generating the gh release command. Do not use for routine commits or PR descriptions.
license: MIT
compatibility: opencode
metadata:
  audience: maintainers
  workflow: github
---

## What I do

- Draft release notes from merged PRs since the last tag
- Propose a semantic version bump (major/minor/patch) based on the changes
- Provide a copy-pasteable `gh release create` command

## When to use me

Use this when you are preparing a tagged release. Ask clarifying questions if
the target versioning scheme is unclear.

## Steps

1. Run `scripts/collect-prs.sh` to gather merged PRs since the last tag.
2. Categorize each PR as feat / fix / chore based on its title prefix.
3. Propose a version bump following semver (see `references/versioning-policy.md`
   if the repo uses a non-standard scheme).
4. Render the final notes using `assets/release-notes.template.md`.
5. Output the exact `gh release create` command, do not run it yourself.

## Supporting files

- `scripts/collect-prs.sh` — fetches merged PRs via `gh pr list --state merged`
- `references/versioning-policy.md` — read only if the repo's versioning looks unusual
- `assets/release-notes.template.md` — the markdown template to fill in
```

`.opencode/skills/git-release/scripts/collect-prs.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail
LAST_TAG=$(git describe --tags --abbrev=0 2>/dev/null || echo "")
if [ -z "$LAST_TAG" ]; then
  gh pr list --state merged --limit 50 --json title,number,author
else
  gh pr list --state merged --search "merged:>$(git log -1 --format=%aI "$LAST_TAG")" \
    --json title,number,author
fi
```

`.opencode/skills/git-release/assets/release-notes.template.md`:

```markdown
## v{{VERSION}} — {{DATE}}

### Features

{{FEATURES}}

### Fixes

{{FIXES}}

### Chores

{{CHORES}}
```

> Remember to `chmod +x` any script files so OpenCode's recursive discovery actually picks them up as runnable scripts.

### 9.5 How the agent sees and loads skills

OpenCode advertises every discovered skill in the `skill` tool's description, like this:

```
<available_skills>
  <skill>
    <name>git-release</name>
    <description>Create consistent releases and changelogs...</description>
  </skill>
</available_skills>
```

The agent loads one explicitly by calling:

```
skill({ name: "git-release" })
```

— at which point the full `SKILL.md` body (and only that body; not the referenced files) enters its context. The agent then reads `references/*` or runs `scripts/*` itself, by path, only if the instructions tell it to.

### 9.6 Validating names — common mistakes

```
✅ git-release
✅ api-docs
✅ typescript-advanced
❌ Git-Release        (must be lowercase)
❌ git_release        (underscores not allowed, use hyphens)
❌ -git-release        (cannot start with a hyphen)
❌ git--release        (no consecutive hyphens)
```

### 9.7 Configure skill permissions

```jsonc
// opencode.json
{
  "permission": {
    "skill": {
      "*": "allow",
      "pr-review": "allow",
      "internal-*": "deny", // hide internal-only skills entirely
      "experimental-*": "ask", // prompt before loading experimental skills
    },
  },
}
```

| Permission | Behavior                                                                                   |
| ---------- | ------------------------------------------------------------------------------------------ |
| `allow`    | Skill loads immediately when the agent requests it                                         |
| `deny`     | Skill is **hidden from the agent entirely** — it won't even appear in `<available_skills>` |
| `ask`      | User is prompted for approval before the skill loads                                       |

Patterns support wildcards: `internal-*` matches `internal-docs`, `internal-tools`, etc.

### 9.8 Override skill permissions per agent

**For custom Markdown agents:**

```markdown
---
permission:
  skill:
    "documents-*": "allow"
---
```

**For built-in agents, via `opencode.json`:**

```jsonc
{
  "agent": {
    "plan": {
      "permission": {
        "skill": {
          "internal-*": "allow",
        },
      },
    },
  },
}
```

### 9.9 Disabling the skill tool entirely for an agent

**Custom agent:**

```markdown
---
tools:
  skill: false
---
```

**Built-in agent:**

```jsonc
{
  "agent": {
    "plan": {
      "tools": {
        "skill": false,
      },
    },
  },
}
```

When disabled, the entire `<available_skills>` block is omitted from that agent's context — useful for a narrowly-scoped agent that should never go reading from your skill library.

### 9.10 Troubleshooting a skill that doesn't show up

1. Verify the filename is exactly `SKILL.md`, all caps.
2. Confirm the frontmatter includes both `name` and `description`.
3. Make sure the skill name is unique across **all** discovery locations (project + global + Claude-compat).
4. Check your permission config — skills set to `deny` are deliberately hidden from the agent.
5. Validate the `name` regex (Section 9.6) — a malformed name silently fails discovery.

### 9.11 Project skills vs. global skills — when to use which

| Use a **project** skill (`.opencode/skills/`) for: | Use a **global** skill (`~/.config/opencode/skills/`) for:    |
| -------------------------------------------------- | ------------------------------------------------------------- |
| Repo-specific deployment steps                     | Personal git workflow helpers                                 |
| "How we structure API endpoints in this codebase"  | General code-review checklists you use everywhere             |
| Database migration runbooks for this project       | Commit-message conventions you personally follow              |
| Team-shared, version-controlled procedures         | Cross-project automation you don't want to duplicate per repo |

---

## 10. Custom Commands

Custom commands are reusable prompt templates, triggered with `/command-name` in the TUI — the templated equivalent of "I keep typing basically the same prompt every day."

### 10.1 Create command files

Place Markdown files in:

- Global: `~/.config/opencode/commands/<name>.md`
- Per-project: `.opencode/commands/<name>.md`

**The filename becomes the command name.** `test.md` → `/test`.

```markdown
---
description: Run tests with coverage
agent: build
model: anthropic/claude-3-5-sonnet-20241022
---

Run the full test suite with coverage report and show any failures.
Focus on the failing tests and suggest fixes.
```

### 10.2 Configure via JSON instead (in `opencode.json`)

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "command": {
    "test": {
      "template": "Run the full test suite with coverage report and show any failures.\nFocus on the failing tests and suggest fixes.",
      "description": "Run tests with coverage",
      "agent": "build",
      "model": "anthropic/claude-3-5-sonnet-20241022",
    },
  },
}
```

### 10.3 Prompt placeholders

**`$ARGUMENTS`** — everything typed after the command name:

```markdown
---
description: Create a new React component
---

Create a new React component named $ARGUMENTS with TypeScript support.
Include proper typing and basic structure.
```

```
/component Button
```

**Positional arguments** — `$1`, `$2`, `$3`, ...:

```markdown
---
description: Create a new file with content
---

Create a file named $1 in the directory $2 with the following content: $3
```

```
/create-file config.json src "{ \"key\": \"value\" }"
```

**Shell output injection** — `!`command``:

```markdown
---
description: Review recent changes
---

Recent git commits:
!`git log --oneline -10`

Review these changes and suggest any improvements.
```

Commands run in your project's root directory, and their output becomes part of the prompt sent to the model.

**File reference injection** — `@filename`:

```markdown
---
description: Review component
---

Review the component in @src/components/Button.tsx.
Check for performance issues and suggest improvements.
```

The file's content is included in the prompt automatically — no need to ask the model to "go read the file" as a separate step.

### 10.4 Full option reference

| Option        | Required | Description                                                                                                                                                                                                                                                        |
| ------------- | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `template`    | **Yes**  | The prompt sent to the LLM (Markdown body if using the file format; this key if using JSON).                                                                                                                                                                       |
| `description` | **Yes**  | Shown in the TUI's `/` autocomplete.                                                                                                                                                                                                                               |
| `agent`       | No       | Which agent executes this command. Defaults to whatever your current agent is if unspecified. If the named agent is a **subagent**, the command triggers a subagent invocation by default.                                                                         |
| `subtask`     | No       | Boolean. Set `true` to **force** subagent invocation — keeps this command's work out of your main session's context, even if the target agent's `mode` is `primary`. Set `false` to suppress the default subagent-triggering behavior for a subagent-mode command. |
| `model`       | No       | Override the model just for this command.                                                                                                                                                                                                                          |

### 10.5 A practical, fully worked command — `/review-pr`

```markdown
---
description: Review staged changes like a senior engineer
agent: reviewer
subtask: true
---

Review the following staged git changes:
!`git diff --cached`

Context — recent commit history:
!`git log --oneline -5`

Provide a structured review following the reviewer agent's checklist:

1. Summary (2-3 sentences)
2. Critical issues (must fix)
3. Suggestions (should fix)
4. Minor notes (optional)
```

### 10.6 A practical command with both shell output and positional args — `/standup`

```markdown
---
description: Summarize commits from the last N days for standup
agent: general
subtask: true
model: anthropic/claude-haiku-4-20250514
---

Summarize these git commits from the last $1 day(s) in 3 concise bullet points
suitable for a standup update. Group related commits together, skip merge commits.

!`git log --oneline --since="$1 days ago"`
```

```
/standup 1
```

### 10.7 Built-in commands

OpenCode ships with built-ins like `/init`, `/undo`, `/redo`, `/share`, `/help`, and more (full list in Section 13). **Custom commands can override built-ins** — if you define your own command with the same name, yours takes precedence.

---

## 11. Permissions — Whitelisting & Denying

> As of OpenCode `v1.1.1`, the legacy `tools` boolean config was deprecated in favor of `permission` (still supported for backwards compatibility). This section reflects the current `permission` system.

### 11.1 The three actions

| Action    | Behavior                           |
| --------- | ---------------------------------- |
| `"allow"` | Run without any approval prompt    |
| `"ask"`   | Prompt for approval before running |
| `"deny"`  | Block the action entirely          |

### 11.2 Basic configuration

```jsonc
// opencode.json
{
  "$schema": "https://opencode.ai/config.json",
  "permission": {
    "*": "ask",
    "bash": "allow",
    "edit": "deny",
  },
}
```

Or set everything at once:

```jsonc
{
  "permission": "allow",
}
```

### 11.3 Granular rules (object syntax)

For most permission keys, you can pass an object mapping a pattern to an action instead of a flat string:

```jsonc
{
  "permission": {
    "bash": {
      "*": "ask",
      "git *": "allow",
      "npm *": "allow",
      "rm *": "deny",
      "grep *": "allow",
    },
    "edit": {
      "*": "deny",
      "packages/web/src/content/docs/*.mdx": "allow",
    },
  },
}
```

**Rules are evaluated by pattern match, and the last matching rule wins.** Convention: put the catch-all `"*"` rule first, more specific rules after it.

### 11.4 Wildcard syntax

- `*` matches zero or more of any character
- `?` matches exactly one character
- everything else matches literally

### 11.5 Home directory expansion

`~` or `$HOME` at the start of a pattern expand to your home directory — handy specifically for `external_directory` rules:

- `~/projects/*` → `/home/username/projects/*`
- `$HOME/projects/*` → `/home/username/projects/*`

### 11.6 External directories

`external_directory` governs tool calls that touch paths **outside** the working directory OpenCode was started in — applies to any path-taking tool (`read`, `edit`, `glob`, `grep`, and many `bash` commands).

> Home expansion (`~/...`) only changes how you _write_ the pattern — it does **not** automatically make that path part of the workspace. Paths outside the working directory always need an explicit `external_directory` allow rule.

```jsonc
{
  "permission": {
    "external_directory": {
      "~/projects/personal/**": "allow",
    },
  },
}
```

Any directory allowed here inherits the **same defaults** as the current workspace — since `read` defaults to `allow`, reads under that path are also allowed unless you add a more specific override:

```jsonc
{
  "permission": {
    "external_directory": {
      "~/projects/personal/**": "allow",
    },
    "edit": {
      "~/projects/personal/**": "deny", // allow reading, block editing
    },
  },
}
```

Keep this list focused on genuinely trusted paths.

### 11.7 Complete list of available permission keys

| Key                  | Gates                                                                    | Notes                                                     |
| -------------------- | ------------------------------------------------------------------------ | --------------------------------------------------------- |
| `read`               | reading a file                                                           | matches the file path                                     |
| `edit`               | `write`, `edit`, `apply_patch`                                           | all file modifications                                    |
| `glob`               | file globbing                                                            | matches the glob pattern                                  |
| `grep`               | content search                                                           | matches the regex pattern                                 |
| `list`               | listing directory contents                                               |                                                           |
| `bash`               | running shell commands                                                   | matches the parsed command, e.g. `git status --porcelain` |
| `task`               | launching subagents                                                      | matches the subagent type                                 |
| `skill`              | loading a skill                                                          | matches the skill name                                    |
| `lsp`                | LSP queries                                                              | non-granular (shorthand action only)                      |
| `question`           | the agent asking you questions mid-execution                             | shorthand action only                                     |
| `webfetch`           | fetching a URL                                                           | matches the URL                                           |
| `websearch`          | web search                                                               | matches the query                                         |
| `external_directory` | any tool touching paths outside the project worktree                     | see 11.6                                                  |
| `todowrite`          | `todowrite`, `todoread`                                                  | shorthand action only                                     |
| `doom_loop`          | recovery prompts when the same tool call repeats 3× with identical input | shorthand action only                                     |

`read`, `edit`, `glob`, `grep`, `list`, `bash`, `task`, `external_directory`, `lsp`, and `skill` accept **either** a shorthand action (`"allow"|"ask"|"deny"`) **or** a pattern→action object for fine-grained control. The remaining keys (`question`, `webfetch`, `websearch`, `todowrite`, `doom_loop`) accept the shorthand action only.

> Permission keys are matched as wildcard patterns against the underlying **tool name**, so the same syntax works uniformly across built-in tools, custom tools, and MCP tools. For example `"mymcp_*": "deny"` denies every tool an MCP server exposes, while `"mymcp_search": "ask"` targets just one of them.

### 11.8 Defaults if you specify nothing

- Most permissions default to `"allow"`.
- `doom_loop` and `external_directory` default to `"ask"`.
- `read` defaults to `"allow"`, **except** `.env` files, which are denied by default:

```jsonc
{
  "permission": {
    "read": {
      "*": "allow",
      "*.env": "deny",
      "*.env.*": "deny",
      "*.env.example": "allow",
    },
  },
}
```

### 11.9 What happens when an action resolves to "ask"

The UI offers three outcomes:

| Choice   | Effect                                                                                                   |
| -------- | -------------------------------------------------------------------------------------------------------- |
| `once`   | Approve just this one request                                                                            |
| `always` | Approve future requests matching the suggested pattern, for the rest of the **current** OpenCode session |
| `reject` | Deny the request                                                                                         |

The set of patterns `always` would approve is supplied by the tool itself — for instance, a bash approval typically suggests whitelisting a safe command prefix like `git status*`.

### 11.10 Per-agent permission overrides

Agent-level permissions merge with the global config, and **agent rules take precedence** on conflict.

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "permission": {
    "bash": {
      "*": "ask",
      "git *": "allow",
      "git commit *": "deny",
      "git push *": "deny",
      "grep *": "allow",
    },
  },
  "agent": {
    "build": {
      "permission": {
        "bash": {
          "*": "ask",
          "git *": "allow",
          "git commit *": "ask",
          "git push *": "deny",
          "grep *": "allow",
        },
      },
    },
  },
}
```

Or in a Markdown agent:

```markdown
---
description: Code review without edits
mode: subagent
permission:
  edit: deny
  bash: ask
  webfetch: deny
---

Only analyze code and suggest changes.
```

> **Tip:** match commands precisely. `"grep *"` allows `grep pattern file.txt`; `"grep"` alone would block any invocation with arguments. Commands like `git status` need an explicit `"git status *"` rule once arguments are involved — a bare `"git status"` rule does not automatically cover `git status --porcelain`.

### 11.11 A recommended, production-grade permission config

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "permission": {
    "read": {
      "*": "allow",
      "*.env": "deny",
      "*.env.*": "deny",
      "*.env.example": "allow",
      "*.pem": "deny",
      "*.key": "deny",
    },
    "edit": "ask",
    "bash": {
      "*": "ask",
      "git status*": "allow",
      "git diff*": "allow",
      "git log*": "allow",
      "git add *": "allow",
      "git stash*": "allow",
      "git branch*": "allow",
      "grep *": "allow",
      "cat *": "allow",
      "ls *": "allow",
      "pwd": "allow",
      "which *": "allow",
      "npm run build": "allow",
      "npm run test*": "allow",
      "npm run lint*": "allow",
      "pnpm run *": "allow",
      "git commit*": "ask",
      "git push*": "ask",
      "git merge*": "ask",
      "rm -rf *": "deny",
      "sudo *": "deny",
      "curl * | bash": "deny",
      "wget * | sh": "deny",
    },
    "external_directory": "ask",
    "doom_loop": "ask",
    "skill": {
      "*": "allow",
      "experimental-*": "ask",
    },
  },
}
```

---

## 12. Session Management

### 12.1 Where sessions live

OpenCode persists all sessions in a **SQLite database**:

```
~/.local/share/opencode/opencode.db
```

Sessions survive terminal restarts, reboots, and closing the TUI. You can return to any past session at any time, from any terminal, as long as it points at the same database.

### 12.2 Session commands — TUI

| Command     | Default keybind | Description                                                                      |
| ----------- | --------------- | -------------------------------------------------------------------------------- |
| `/sessions` | `Ctrl+x l`      | Open the session browser — list, search, switch                                  |
| `/new`      | `Ctrl+x n`      | Start a new session                                                              |
| `/compact`  | `Ctrl+x c`      | Compact (summarize) the current session's context                                |
| `/undo`     | `Ctrl+x u`      | Undo the last message and revert the file changes it caused (git-snapshot based) |
| `/redo`     | `Ctrl+x r`      | Redo a previously undone change                                                  |

### 12.3 Session commands — CLI

```bash
# List sessions
opencode session list
opencode session list --format json
opencode session list -n 10              # last 10 only

# Resume
opencode --continue                       # resume the most recent session in this directory
opencode -c
opencode --session <sessionID>            # resume a specific session by ID
opencode --session <sessionID> --fork     # continue but branch into a new session

# Delete
opencode session delete <sessionID>

# Export / import (for sharing or transplanting a conversation)
opencode export <sessionID>
opencode export <sessionID> --sanitize    # redact likely-sensitive content
opencode import session.json
opencode import https://opncd.ai/s/abc123

# Usage / cost stats
opencode stats
opencode stats --days 7
```

> Sessions are tied to the **project path** they were started in. If `--continue` reports "session not found," double-check you're in the exact directory the session was originally launched from, or pass `--session <id>` explicitly after looking it up with `opencode session list`.

### 12.4 Parent / child sessions — how subagents actually work

When a primary agent delegates to a subagent (either automatically, based on the subagent's `description`, or because you `@mention`ed it), OpenCode spins up a **child session** for that subagent's work. This is the real mechanism behind "multiple agents collaborating" — not a chat-room-style shared channel, but a tree of sessions where:

- the **parent** session is your main conversation
- each **child** session is a self-contained delegation, with its own context, that reports its result back into the parent's conversation once done

**Navigating the tree:**

| Keybind (default)                       | Action                                        |
| --------------------------------------- | --------------------------------------------- |
| `<Leader>+Down` (`session_child_first`) | Enter the first child session from the parent |
| `Right` (`session_child_cycle`)         | Cycle to the next sibling child session       |
| `Left` (`session_child_cycle_reverse`)  | Cycle to the previous sibling child session   |
| `Up` (`session_parent`)                 | Return to the parent session                  |

This is genuinely useful when you've kicked off two or three subagents in parallel (e.g. `@explore` looking at one part of the codebase while `@general` implements another) and want to check in on each one's progress individually.

### 12.5 Can sessions communicate with each other?

There's no live, shared "memory channel" between two arbitrary unrelated sessions. What you actually have is:

1. **The parent/child subagent tree (Section 12.4)** — this is the native, built-in way work gets coordinated across multiple agent "threads" within one overarching task.
2. **Shared files on disk** — every session, regardless of relation, reads the current state of your project files. Work done by one session is automatically visible to a completely separate session opened afterward, because nothing is cached as a stale snapshot — it's just whatever is on disk.
3. **Export + import** — `opencode export <id>` followed by `opencode import file.json` lets you transplant a conversation's content into a different context (e.g. handing off a half-finished investigation to a teammate, or seeding a new session with a previous one's findings).
4. **Server mode + multiple attached clients** — run one headless server and connect several TUI/CLI clients to the same backend state:

```bash
# Terminal 1 — start the server
opencode serve --port 4096

# Terminal 2 — attach an interactive TUI client to it
opencode attach http://localhost:4096

# Terminal 3 — run one-off non-interactive prompts against the same server
opencode run --attach http://localhost:4096 "Summarize the changes made so far"
```

This is the closest thing to "two sessions talking to each other in real time" — both clients are operating against the exact same underlying session/server state.

### 12.6 Direct SQLite inspection (read-only, advanced)

```bash
sqlite3 ~/.local/share/opencode/opencode.db

.headers on
.mode table

-- Recent sessions
SELECT id, title, message_count, cost, datetime(created_at/1000, 'unixepoch') as created
FROM sessions ORDER BY updated_at DESC LIMIT 20;

-- Search sessions by title
SELECT id, title FROM sessions WHERE title LIKE '%auth%';

-- Messages per session
SELECT s.title, COUNT(m.id) as messages
FROM sessions s JOIN messages m ON m.session_id = s.id
GROUP BY s.id ORDER BY messages DESC;
```

> Treat the database as **read-only** for manual inspection. Always take a backup before any direct write, and prefer the official CLI/TUI for actual mutations.

### 12.7 Backing up sessions

```bash
cp ~/.local/share/opencode/opencode.db \
   ~/.local/share/opencode/opencode.db.backup.$(date +%Y%m%d)
```

---

## 13. All TUI Slash Commands

Typed directly into the OpenCode TUI prompt box.

### 13.1 Session & Navigation

| Command            | Description                                                  |
| ------------------ | ------------------------------------------------------------ |
| `/new`             | Start a new session                                          |
| `/sessions`        | Browse, search, switch sessions                              |
| `/share`           | Generate a shareable link for the current session            |
| `/unshare`         | Revoke the shareable link                                    |
| `/export`          | Export the current session (opens in `$EDITOR`, i.e. Neovim) |
| `/exit` or `/quit` | Exit the TUI                                                 |

### 13.2 Context & Agent Control

| Command         | Description                                    |
| --------------- | ---------------------------------------------- |
| `/init`         | Scan the repo and create/update `AGENTS.md`    |
| `/compact`      | Summarize and compact the conversation context |
| `/undo`         | Undo the last message and revert file changes  |
| `/redo`         | Redo a previously undone change                |
| `/agents`       | List available agents                          |
| `/models`       | List/select available models                   |
| `/model <name>` | Switch model for the current session           |

### 13.3 Editing & Files

| Command      | Description                                                       |
| ------------ | ----------------------------------------------------------------- |
| `/editor`    | Open external `$EDITOR` (Neovim) to compose a long prompt         |
| `/paste`     | Paste clipboard image/content                                     |
| `@<path>`    | Reference/inject a file into the prompt (autocomplete supported)  |
| `!<command>` | Run a one-off shell command and inject its output into the prompt |

### 13.4 Tools & Permissions

| Command        | Description                                       |
| -------------- | ------------------------------------------------- |
| `/permissions` | View/modify current session permission rules      |
| `/tools`       | List available tools and their status             |
| `/mcp`         | List configured MCP servers and connection status |

### 13.5 Info & Diagnostics

| Command   | Description                                              |
| --------- | -------------------------------------------------------- |
| `/help`   | Show help and the live keybinding reference              |
| `/status` | Show session info: model, cost, token usage              |
| `/stats`  | Show usage statistics                                    |
| `/theme`  | Switch TUI theme                                         |
| `/doctor` | Run diagnostics (config validity, provider connectivity) |
| `/bug`    | Report a bug (opens a GitHub issue template)             |

### 13.6 Custom commands

Any file under `commands/` or `.opencode/commands/` automatically becomes a `/your-command-name` slash command, alongside all built-ins above (and, as noted in Section 10.7, a custom command can deliberately override a built-in of the same name).

### 13.7 Key TUI keybindings (defaults)

| Keybind                             | Action                                  |
| ----------------------------------- | --------------------------------------- |
| `Tab`                               | Switch primary agent (Build ↔ Plan)     |
| `Ctrl+x`                            | Leader key (prefix for many actions)    |
| `Ctrl+x n`                          | New session                             |
| `Ctrl+x l`                          | Session list                            |
| `Ctrl+x c`                          | Compact context                         |
| `Ctrl+x u`                          | Undo                                    |
| `Ctrl+x r`                          | Redo                                    |
| `<Leader>+Down`                     | Enter first child session from parent   |
| `Right` (in a session)              | Cycle to next sibling child session     |
| `Left` (in a session)               | Cycle to previous sibling child session |
| `Up` (in a child session)           | Return to parent session                |
| `Ctrl+p`                            | Command palette                         |
| `Ctrl+c` (×2)                       | Interrupt the current agent turn        |
| `Esc`                               | Cancel current input / close dialog     |
| `Up` / `Down` (in the prompt input) | Navigate prompt history                 |
| `Ctrl+e` or `/editor`               | Open `$EDITOR` for a multi-line prompt  |

> Keybindings can change between releases — run `/help` inside the TUI for the live, version-accurate list, and customize them in `opencode.json` under `keybinds` if a default doesn't suit your muscle memory.

---

## 14. Full CLI Reference

### 14.1 Launching

```bash
opencode                                       # Launch TUI in current directory
opencode /path/to/project                      # Launch TUI in a specific directory
opencode --continue                             # Resume last session
opencode --session <id>                         # Resume a specific session
opencode --model anthropic/claude-sonnet-4-20250514   # Override model for this launch
opencode --agent plan                            # Launch directly into a specific agent
```

### 14.2 Non-interactive ("headless") execution — for scripts & automation

```bash
# Run a single prompt and exit, printing the result to stdout
opencode run "Summarize the diff of the last commit"

# Pipe input
git diff | opencode run "Review this diff for bugs"

# Specify model/agent
opencode run --model anthropic/claude-haiku-4-20250514 "List all TODO comments in src/"
opencode run --agent reviewer "Review the staged changes"

# JSON output (for scripting/piping into other tools)
opencode run --format json "..."

# Continue a session non-interactively
opencode run --continue "Now write tests for what you just built"
```

### 14.3 Server mode

```bash
opencode serve                          # Start headless API server (default port 4096)
opencode serve --port 5000
opencode serve --hostname 0.0.0.0       # Expose beyond localhost — use with care
```

### 14.4 Web UI

```bash
opencode web
opencode web --port 3000
```

### 14.5 Authentication

```bash
opencode auth login
opencode auth login --provider anthropic
opencode auth list
opencode auth logout
opencode auth logout --provider openai
```

### 14.6 MCP server management

```bash
opencode mcp list                        # List MCP servers and their auth status
opencode mcp auth <server-name>          # Manually trigger an OAuth flow
opencode mcp logout <server-name>        # Remove stored OAuth credentials
opencode mcp auth list                   # View auth status for all OAuth-capable servers
opencode mcp debug <server-name>         # Diagnose connection / OAuth issues
```

### 14.7 Agent management

```bash
opencode agent create     # Interactive agent creation wizard
opencode agent list
```

### 14.8 Session management

```bash
opencode session list
opencode session delete <id>
opencode export <id>
opencode export <id> --sanitize
opencode import <file-or-url>
```

### 14.9 Models & providers

```bash
opencode models
opencode models --provider anthropic
```

### 14.10 Stats & diagnostics

```bash
opencode stats
opencode stats --days 30
opencode doctor
opencode --version
opencode upgrade
opencode upgrade v1.2.0
```

### 14.11 Common global flags

| Flag                       | Description                                                |
| -------------------------- | ---------------------------------------------------------- |
| `--model <provider/model>` | Override model                                             |
| `--agent <name>`           | Override agent                                             |
| `--format json\|text`      | Output format                                              |
| `--quiet`                  | Suppress non-essential output                              |
| `--cwd <path>`             | Run as if launched from this directory                     |
| `--config <path>`          | Use a custom config file (equivalent to `OPENCODE_CONFIG`) |

---

## 15. Environment Variables

| Variable                              | Purpose                                                         |
| ------------------------------------- | --------------------------------------------------------------- |
| `ANTHROPIC_API_KEY`                   | Anthropic API key (alternative to `auth login`)                 |
| `OPENAI_API_KEY`                      | OpenAI API key                                                  |
| `GEMINI_API_KEY`                      | Google Gemini API key                                           |
| `OPENCODE_CONFIG`                     | Path to a custom config file to merge in                        |
| `OPENCODE_CONFIG_CONTENT`             | Inline JSON string to merge into config                         |
| `OPENCODE_MODEL`                      | Default model override                                          |
| `OPENCODE_DISABLE_CLAUDE_CODE`        | Disable **all** `.claude/*` Claude Code compatibility           |
| `OPENCODE_DISABLE_CLAUDE_CODE_PROMPT` | Disable only the `~/.claude/CLAUDE.md` rules fallback           |
| `OPENCODE_DISABLE_CLAUDE_CODE_SKILLS` | Disable only the `.claude/skills/` skills fallback              |
| `EDITOR`                              | External editor used by `/editor` and `/export` (set to `nvim`) |
| `NO_COLOR`                            | Disable colored output                                          |
| `OPENCODE_LOG_LEVEL`                  | `debug` \| `info` \| `warn` \| `error`                          |

Recommended additions to `~/.bashrc`:

```bash
export EDITOR="nvim"
export ANTHROPIC_API_KEY="sk-ant-..."     # optional, if not using `auth login`
export OPENCODE_LOG_LEVEL="info"
```

---

## 16. MCP Servers

OpenCode connects external tools (databases, ticket trackers, browsers, observability platforms, etc.) via the **Model Context Protocol**. Once added, MCP tools sit alongside built-in tools and the model can call them the same way.

> **Caveat — context cost.** Every MCP server you enable adds its tool definitions to context on every turn. This adds up fast with "kitchen sink" servers (the GitHub MCP server is a commonly cited example that can eat a large chunk of your context budget on its own). Be deliberate about which servers you keep enabled, and prefer the per-agent scoping pattern in 16.5 once you have more than a couple.

### 16.1 Enable an MCP server

Define servers under `mcp` in `opencode.json`, each with a unique name you'll refer to by when prompting:

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "mcp": {
    "name-of-mcp-server": {
      "enabled": true,
      // ...
    },
    "name-of-other-mcp-server": {
      // ...
    },
  },
}
```

Set `enabled: false` to temporarily disable a server without deleting its config.

> **Org-wide defaults:** an organization can push default MCP servers via its `.well-known/opencode` endpoint, often disabled by default so individuals opt in. To enable one of those, just add the same key to your local config with `enabled: true` — your local value overrides the remote default.

### 16.2 Local MCP servers

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "mcp": {
    "my-local-mcp-server": {
      "type": "local",
      "command": ["npx", "-y", "my-mcp-command"], // or ["bun", "x", "my-mcp-command"]
      "enabled": true,
      "environment": {
        "MY_ENV_VAR": "my_env_var_value",
      },
    },
  },
}
```

Example — the official MCP "everything" test server:

```jsonc
{
  "mcp": {
    "mcp_everything": {
      "type": "local",
      "command": ["npx", "-y", "@modelcontextprotocol/server-everything"],
    },
  },
}
```

Then in your prompt: `use the mcp_everything tool to add 3 and 4`.

**Local server options:**

| Option        | Type    | Required | Description                                                    |
| ------------- | ------- | -------- | -------------------------------------------------------------- |
| `type`        | string  | Yes      | Must be `"local"`                                              |
| `command`     | array   | Yes      | Command + args to launch the server                            |
| `cwd`         | string  | No       | Working directory (relative paths resolve from the workspace)  |
| `environment` | object  | No       | Env vars passed to the server process                          |
| `enabled`     | boolean | No       | Enable/disable on startup                                      |
| `timeout`     | number  | No       | Timeout (ms) for fetching tools from the server. Default 5000. |

### 16.3 Remote MCP servers

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "mcp": {
    "my-remote-mcp": {
      "type": "remote",
      "url": "https://my-mcp-server.com",
      "enabled": true,
      "headers": {
        "Authorization": "Bearer MY_API_KEY",
      },
    },
  },
}
```

**Remote server options:**

| Option    | Type              | Required | Description                                                   |
| --------- | ----------------- | -------- | ------------------------------------------------------------- |
| `type`    | string            | Yes      | Must be `"remote"`                                            |
| `url`     | string            | Yes      | URL of the remote server                                      |
| `enabled` | boolean           | No       | Enable/disable on startup                                     |
| `headers` | object            | No       | Headers sent with every request                               |
| `oauth`   | object \| `false` | No       | OAuth config (Section 16.4), or `false` to disable auto-OAuth |
| `timeout` | number            | No       | Timeout (ms) for fetching tools. Default 5000.                |

### 16.4 OAuth for remote servers

OpenCode automatically handles OAuth for remote MCP servers: it detects a `401`, initiates the flow (using Dynamic Client Registration / RFC 7591 if the server supports it), and stores tokens securely.

**Automatic — usually no config needed:**

```jsonc
{
  "mcp": {
    "my-oauth-server": {
      "type": "remote",
      "url": "https://mcp.example.com/mcp",
    },
  },
}
```

OpenCode prompts you to authenticate the first time you use it. You can also trigger it manually with `opencode mcp auth <server-name>`.

**Pre-registered client credentials:**

```jsonc
{
  "mcp": {
    "my-oauth-server": {
      "type": "remote",
      "url": "https://mcp.example.com/mcp",
      "oauth": {
        "clientId": "{env:MY_MCP_CLIENT_ID}",
        "clientSecret": "{env:MY_MCP_CLIENT_SECRET}",
        "scope": "tools:read tools:execute",
      },
    },
  },
}
```

**Disabling OAuth (e.g. for servers using plain API keys instead):**

```jsonc
{
  "mcp": {
    "my-api-key-server": {
      "type": "remote",
      "url": "https://mcp.example.com/mcp",
      "oauth": false,
      "headers": {
        "Authorization": "Bearer {env:MY_API_KEY}",
      },
    },
  },
}
```

**Auth management commands:**

```bash
opencode mcp auth my-oauth-server      # opens browser, completes the flow
opencode mcp list                       # list servers + auth status
opencode mcp logout my-oauth-server     # remove stored credentials
opencode mcp auth list                  # auth status for all OAuth-capable servers
opencode mcp debug my-oauth-server      # diagnose connection/OAuth issues
```

Tokens are stored in `~/.local/share/opencode/mcp-auth.json`.

### 16.5 Managing MCP tools globally and per-agent

Since MCP tools register alongside built-in tools, you manage them the same way — through `tools`/`permission`.

**Disable a specific MCP globally:**

```jsonc
{
  "mcp": {
    "my-mcp-foo": {
      "type": "local",
      "command": ["bun", "x", "my-mcp-command-foo"],
    },
    "my-mcp-bar": {
      "type": "local",
      "command": ["bun", "x", "my-mcp-command-bar"],
    },
  },
  "tools": {
    "my-mcp-foo": false,
  },
}
```

**Disable all tools matching a glob:**

```jsonc
{
  "tools": {
    "my-mcp*": false,
  },
}
```

**Scope an MCP to just one agent** (disable globally, re-enable for a specific agent — useful once you have several heavyweight MCP servers and don't want every agent paying their context cost):

```jsonc
{
  "$schema": "https://opencode.ai/config.json",
  "mcp": {
    "my-mcp": {
      "type": "local",
      "command": ["bun", "x", "my-mcp-command"],
      "enabled": true,
    },
  },
  "tools": {
    "my-mcp*": false,
  },
  "agent": {
    "my-agent": {
      "tools": {
        "my-mcp*": true,
      },
    },
  },
}
```

> MCP server tools are registered with the server name as a prefix, so `"my-mcp*"` reliably matches every tool that server exposes.

### 16.6 Common, useful MCP servers

```jsonc
{
  "mcp": {
    "context7": {
      "type": "remote",
      "url": "https://mcp.context7.com/mcp",
      "enabled": true,
    },
    "sentry": {
      "type": "remote",
      "url": "https://mcp.sentry.dev/mcp",
      "enabled": true,
    },
    "grep": {
      "type": "remote",
      "url": "https://mcp.grep.app",
      "enabled": true,
    },
    "postgres": {
      "type": "local",
      "command": [
        "npx",
        "-y",
        "@modelcontextprotocol/server-postgres",
        "postgres://localhost/mydb",
      ],
      "environment": { "PGPASSWORD": "{env:DB_PASSWORD}" },
    },
    "playwright": {
      "type": "local",
      "command": ["npx", "-y", "@playwright/mcp@latest"],
    },
  },
}
```

Check live status any time with `/mcp` in the TUI or `opencode mcp list` from the CLI.

---

## 17. Neovim + Tmux Integration Workflow

This is the workflow you asked for: **no Neovim plugin** — OpenCode and Neovim run as separate Tmux panes/windows, sharing the same project files on disk.

### 17.1 Why this works well

- OpenCode edits files directly on disk, inside whatever paths your `permission` config allows.
- Neovim auto-detects file changes from disk if you tell it to (`autoread` + a `checktime` autocmd).
- Tmux gives you instant pane switching with zero context-switching overhead between apps.
- No plugin to maintain, no API surface to lock into — if OpenCode's CLI/TUI ever changes shape, your Neovim config doesn't break.

### 17.2 Required Neovim setting

Add to `~/.config/nvim/init.lua`:

```lua
vim.opt.autoread = true

-- Force Neovim to check for external file changes on focus / buffer enter / idle
vim.api.nvim_create_autocmd({ "FocusGained", "BufEnter", "CursorHold", "CursorHoldI" }, {
  pattern = "*",
  command = "if mode() != 'c' | checktime | endif",
})

-- Optional: notify when a file was changed externally
vim.api.nvim_create_autocmd("FileChangedShellPost", {
  pattern = "*",
  callback = function()
    vim.notify("File changed on disk, buffer reloaded", vim.log.levels.WARN)
  end,
})
```

> Without `autoread` + the `checktime` autocmd, Neovim won't notice OpenCode's edits until you manually run `:e!` or `:checktime`.

### 17.3 Recommended Tmux layout

```
┌─────────────────────────────┬────────────────────────────┐
│                             │                            │
│        Neovim (70%)         │      OpenCode TUI (30%)    │
│                             │                            │
│  - Review/edit code         │  - Chat with the agent     │
│  - Jump to LSP diagnostics  │  - Approve permission asks │
│  - :Git diff / fugitive     │  - Run /commands           │
│                             │                            │
└─────────────────────────────┴────────────────────────────┘
```

### 17.4 Tmux config additions

Add to `~/.tmux.conf`:

```bash
# Split window vertically — OpenCode in a 30%-width pane on the right
bind-key o split-window -h -p 30 'opencode'

# Or open OpenCode in a dedicated window instead
bind-key O new-window -n opencode 'opencode'

# Pane navigation, if not already bound
bind-key h select-pane -L
bind-key l select-pane -R
bind-key j select-pane -D
bind-key k select-pane -U

# Avoid accidentally typing into both panes at once
setw -g synchronize-panes off

# Bigger scrollback for reviewing long agent output
set -g history-limit 50000
```

```bash
tmux source-file ~/.tmux.conf
```

### 17.5 Daily workflow

```bash
tmux new -s myproject -c ~/projects/myproject
nvim .
# then inside tmux:
prefix + o      # splits and launches opencode in the side pane
```

Inside the OpenCode pane:

```
> Implement a rate limiter middleware for the Express API.
  Follow the patterns in src/middleware/auth.ts.
```

OpenCode edits files on disk → switch panes (`prefix + h`/`l`, or your bound arrow keys) → Neovim auto-reloads thanks to the autocmd in 17.2 (or `:checktime` manually as a fallback) → review the diff (`:Gdiffsplit` with vim-fugitive, or any diff plugin you already use) → approve/iterate back in the OpenCode pane.

### 17.6 Optional — dedicated git review pane

Since OpenCode tracks edits via git snapshots, a tight loop looks like:

```bash
# In the Neovim pane (with vim-fugitive or similar)
:Git diff      " review what OpenCode changed
:Git add %     " stage if it looks good
```

Or add a third pane just for this:

```bash
lazygit
```

### 17.7 Persisting the layout across reboots/WSL restarts

```bash
# ~/.tmux.conf, using TPM
set -g @plugin 'tmux-plugins/tmux-resurrect'
set -g @plugin 'tmux-plugins/tmux-continuum'
```

This restores pane layout, but you'll still need to relaunch OpenCode manually inside its pane — use `opencode --continue` to pick up exactly where you left off rather than starting a brand-new session.

---

## 18. Tmux Layout Setup Script

Save as `~/.local/bin/dev-session`, then `chmod +x` it, for a one-command project launcher:

```bash
#!/usr/bin/env bash
# dev-session — launch a Neovim + OpenCode tmux workspace
# Usage: dev-session <project-path> [session-name]

set -euo pipefail

PROJECT_DIR="${1:-$(pwd)}"
SESSION_NAME="${2:-$(basename "$PROJECT_DIR")}"

if tmux has-session -t "$SESSION_NAME" 2>/dev/null; then
  echo "Session '$SESSION_NAME' already exists. Attaching..."
  exec tmux attach -t "$SESSION_NAME"
fi

tmux new-session -d -s "$SESSION_NAME" -c "$PROJECT_DIR" -n main

# Left pane: Neovim (70% width)
tmux send-keys -t "$SESSION_NAME:main" "nvim ." C-m

# Right pane: OpenCode (30% width)
tmux split-window -h -p 30 -t "$SESSION_NAME:main" -c "$PROJECT_DIR"
tmux send-keys -t "$SESSION_NAME:main.1" "opencode" C-m

# Optional third pane below OpenCode for git/lazygit
tmux split-window -v -p 40 -t "$SESSION_NAME:main.1" -c "$PROJECT_DIR"
tmux send-keys -t "$SESSION_NAME:main.2" "lazygit" C-m 2>/dev/null || true

# Focus back on Neovim pane
tmux select-pane -t "$SESSION_NAME:main.0"

tmux attach -t "$SESSION_NAME"
```

```bash
dev-session ~/projects/my-api my-api
```

## 19. Best Practices & Pro Tips

### 19.1 AGENTS.md hygiene

- Keep `AGENTS.md` under ~200 lines — it's injected into context on every single message, so bloat is a recurring token/cost tax.
- Push rarely-needed deep-dive material into **skills** instead (Section 9), so it loads only on demand.
- Re-run `/init` periodically so the file doesn't drift from reality as the codebase evolves.

### 19.2 Use Plan mode before Build mode

- Switch to the `plan` agent (`Tab`) for anything non-trivial — by default it can read and reason but every edit and bash command is `ask`, forcing you to approve a concrete plan before any mutation happens.
- This single habit is the highest-leverage way to avoid unwanted file changes.

### 19.3 Model selection strategy

- Use a strong model (e.g. Claude Sonnet) for `build`/implementation work.
- Use a cheaper/faster model (e.g. Claude Haiku) for `plan`, `explore`, `scout`, and lightweight commands like `/standup`.
- Set `small_model` in `opencode.json` for internal housekeeping (titles, compaction) so you're not burning premium tokens on it.
- Override `model` per-agent or per-command rather than globally whenever the task's complexity genuinely differs.

### 19.4 Permission whitelisting strategy

- Start restrictive (`"*": "ask"`), then progressively whitelist exact commands you find yourself approving repeatedly.
- Explicitly `"deny"` destructive patterns (`rm -rf *`, `sudo *`, `git push --force*`) **regardless** of how permissive the rest of your config is — `deny` rules are your last line of defense, not an afterthought, and the "last matching rule wins" semantics mean you should put them after any broad allow rules.
- Never grant blanket `read` access to `.env*`, `*.pem`, `*.key` — leaking credentials into model context is a real, avoidable risk.
- Use `external_directory` deliberately and narrowly; don't leave it on a blanket `"allow"`.

### 19.5 Subagents for parallelism and context isolation

- Use `@explore` for fast, read-only investigation so it doesn't pollute your main session's context with file contents you don't need long-term.
- Use `subtask: true` on custom commands that do heavy one-off work (like `/standup` or `/review-pr`) so their output doesn't bloat your primary coding session's context.
- Use `@scout` specifically when you need to understand a third-party dependency's actual source, rather than guessing from memory.

### 19.6 Context compaction discipline

- Run `/compact` proactively before starting a large new feature within the same session, rather than waiting for auto-compaction to kick in mid-task unpredictably.
- For very long-running work, prefer `/new` with a fresh, focused prompt over endlessly continuing one giant session — smaller, well-scoped sessions consistently produce better results than one mega-session.

### 19.7 Skills as a personal/team knowledge base

- Treat `~/.config/opencode/skills/` as your personal "how I do X" library (commit conventions, debugging runbooks, code review checklists).
- Treat `.opencode/skills/` (committed to the repo) as the team's shared equivalent.
- Keep each skill's `description` field extremely precise — it's the _only_ signal the agent uses to decide whether to load it. A vague description means the skill effectively never gets used.
- Use `scripts/`, `references/`, and `assets/` subfolders deliberately: scripts for anything deterministic the agent shouldn't "wing" by writing ad-hoc bash each time, references for material that's only needed occasionally, assets for boilerplate the agent should copy rather than regenerate from scratch.

### 19.8 Reusability roadmap (building your own toolkit over time)

1. Start with a personal global `AGENTS.md` (communication style, language/library preferences).
2. Extract anything project-specific into that project's own `AGENTS.md`.
3. When you notice yourself typing a similar multi-step request repeatedly, turn it into a `command`.
4. When you notice reference material the agent needs only occasionally, turn it into a `skill` (with subfolders once it grows beyond a single short file).
5. When you notice a recurring "persona" with genuinely distinct permissions (e.g. a read-only auditor, a docs-only writer), turn it into a custom `agent`.
6. Push your `~/.config/opencode/` directory to a private dotfiles repo so it's portable across machines.

### 19.9 Tmux + Neovim specific tips

- Use `tmux-resurrect`/`continuum` for layout persistence, but relaunch OpenCode with `opencode --continue` rather than relying on tmux to "remember" a running process across a WSL restart.
- Bind a single tmux key (`prefix + o`, as in Section 17.4) so opening the AI pane becomes pure muscle memory.
- Keep OpenCode's pane narrower (~30%) — you'll spend more time reading/typing prompts than reading full code, while Neovim needs the width for actual review.

### 19.10 Cost & token tracking

- Run `opencode stats` periodically to monitor spend by model/provider.
- Set `"share": "manual"` (not `"auto"`) to avoid accidentally publishing session content that contains proprietary code.
- Watch your enabled MCP server count — each one adds to every turn's context cost (Section 16); prune anything you're not actively using.

---

## 20. Troubleshooting

| Issue                                                   | Likely Cause                                                | Fix                                                                                                                       |
| ------------------------------------------------------- | ----------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |
| `command not found: opencode`                           | PATH not updated                                            | `export PATH="$HOME/.local/bin:$PATH"` in `~/.bashrc`, then `source ~/.bashrc`                                            |
| Slow performance, file lag                              | Project located under `/mnt/c/...`                          | Move the project into the Linux filesystem (`~/projects/...`)                                                             |
| Neovim doesn't show OpenCode's edits                    | `autoread` not set / no `checktime` autocmd                 | Add the autocmd block from Section 17.2; manually run `:e!` as a fallback                                                 |
| Agent keeps asking permission for the same safe command | No whitelist rule matches that exact pattern                | Add a specific `"allow"` rule in `permission.bash`, remembering the **last matching rule wins**                           |
| Provider login fails                                    | Network blocked, malformed API key                          | Re-run `opencode auth login`; verify with `opencode auth list`; check `opencode doctor`                                   |
| `/init` produces a thin `AGENTS.md`                     | Repo lacks clear structure or README                        | Manually flesh it out using the template in Section 7.3                                                                   |
| Session not found on `--continue`                       | Wrong working directory (sessions are tied to project path) | `cd` into the exact directory used originally, or pass `--session <id>` after looking it up with `opencode session list`  |
| MCP server not connecting                               | Missing env var, wrong command path, OAuth not completed    | Check `/mcp` in TUI; run `opencode mcp debug <name>`; verify the command resolves manually in bash                        |
| Skill never gets used by the agent                      | Vague/missing `description`, malformed `name`               | Rewrite `description` to state precisely when to use it (and when not to); verify `name` matches the regex in Section 9.6 |
| Skill scripts don't run                                 | Missing executable bit                                      | `chmod +x` the script file — OpenCode identifies scripts by the executable bit, not just file location                    |
| High token cost on small tasks                          | Default model too large for trivial requests                | Set `small_model`, and override `model` per agent/command as in Section 19.3                                              |
| Too many MCP tools bloating context                     | Multiple heavyweight MCP servers enabled globally           | Disable globally and scope per-agent (Section 16.5), or just disable servers you're not actively using                    |

## Quick Reference Card

```bash
# Install
curl -fsSL https://opencode.ai/install | bash

# Auth
opencode auth login

# Launch
cd ~/projects/myapp && opencode

# Resume
opencode --continue

# Headless one-shot
opencode run "explain this error: ..."

# Generate project rules
# (inside TUI) /init

# Check everything is healthy
opencode doctor
```

```
~/.config/opencode/                 ← global config (applies everywhere)
  opencode.json
  AGENTS.md
  agents/      commands/      skills/      plugins/
    <name>.md    <name>.md      <name>/SKILL.md (+ scripts/, references/, assets/)

<project>/.opencode/                ← project-only config
  agents/      commands/      skills/      plugins/
<project>/AGENTS.md                 ← project rules (commit to git)
<project>/opencode.json             ← project config overrides (commit to git)

~/.local/share/opencode/
  auth.json                         ← API keys
  mcp-auth.json                     ← MCP OAuth tokens
  opencode.db                       ← all session history (SQLite)
```

**End of guide.**
