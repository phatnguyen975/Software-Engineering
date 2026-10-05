# Antigravity CLI — Complete Setup & Workflow Guide (WSL + tmux + Neovim)

> Written from a senior-dev, AI-agent-workflow perspective. Current as of **mid-June 2026**.
> Antigravity CLI (`agy`) reached general availability on **May 19, 2026** as the closed-source successor to the open-source Gemini CLI, and Google has shipped near-daily changes to it since launch (the in-app changelog alone lists multiple dated entries per week). Several file paths in this space are genuinely contested across sources — including Google's own documentation pages, which don't always agree with each other. Every time that happens below, this guide says so explicitly and gives you the command to check the truth on your own machine (`agy inspect`, `/skills`, `/agents`, `/hooks`, `/mcp`, `agy --help`). Treat this document as a strong, heavily cross-checked starting map — not a substitute for those introspection commands.

## Table of Contents

1. [Context You Should Know Before You Start](#1-context-you-should-know-before-you-start)
2. [Installing Antigravity CLI on Ubuntu (WSL)](#2-installing-antigravity-cli-on-ubuntu-wsl)
3. [Authentication / Login](#3-authentication--login)
4. [Configuration Files — Where Everything Lives](#4-configuration-files--where-everything-lives)
5. [Permissions — The Full Picture](#5-permissions--the-full-picture)
6. [Directory Structure: Global vs. Project](#6-directory-structure-global-vs-project)
7. [Rules (`AGENTS.md` / `GEMINI.md`) — In Depth](#7-rules-agentsmd--geminimd--in-depth)
8. [Skills — In Depth](#8-skills--in-depth)
9. [Custom Commands / Workflows — In Depth](#9-custom-commands--workflows--in-depth)
10. [Agents / Subagents — In Depth](#10-agents--subagents--in-depth)
11. [Hooks — In Depth](#11-hooks--in-depth)
12. [MCP Servers — In Depth](#12-mcp-servers--in-depth)
13. [Session Management — In Depth](#13-session-management--in-depth)
14. [Full Command & Flag Reference](#14-full-command--flag-reference)
15. [Other Notable Strengths / Power-User Features](#15-other-notable-strengths--power-user-features)
16. [Integrating with Your tmux + Neovim Workflow](#16-integrating-with-your-tmux--neovim-workflow)
17. [Quick Setup Checklist](#17-quick-setup-checklist)
18. [Final Caveats & How Reliable Each Section Is](#18-final-caveats--how-reliable-each-section-is)

## 1. Context You Should Know Before You Start

**What it is.** Antigravity CLI (binary `agy`) is Google's terminal-first agentic coding tool. It shares the exact same "agent harness" — planning loop, tool-calling, permission engine, Skills/Rules/Hooks system — as the Antigravity 2.0 desktop app and the original Antigravity IDE. Settings, permissions, MCP servers, and (optionally) entire conversations can move between the terminal and the GUI.

**Why it replaced Gemini CLI.** Gemini CLI was Apache-2.0 open source, with a large external contributor base. Antigravity CLI is closed-source. Google's stated rationale is abuse prevention; the move was controversial in the open-source community regardless. Practically, this means: no reading the source when documentation is ambiguous (which happens — see §18), and you are dependent on Google's release cadence for bug fixes.

**Why "Go-based" matters to you.** Unlike the Node-based Gemini CLI, `agy` is a compiled Go binary: fast cold start, low memory footprint, and well-behaved over SSH/WSL.

**The hard deadline.** Gemini CLI stopped serving free, AI Pro, and AI Ultra individual users on **June 18, 2026**. If you depend on Gemini CLI today, migrate now — `agy` ships a one-shot importer for exactly this (§3.6 in the old revision, now folded into §4.6 below). Organizations on Gemini Code Assist Standard/Enterprise licenses are unaffected and can stay on Gemini CLI indefinitely.

**Multi-model, not single-model.** Antigravity CLI is not Gemini-only. Depending on your plan, `/model` lets you switch between Gemini 3.5 Flash, Gemini 3.1 Pro, Claude Sonnet, Claude Opus, and GPT-OSS 120B inside the same tool.

**Quota is shared across the whole ecosystem.** CLI usage, Antigravity 2.0 desktop usage, and IDE usage draw from the _same_ quota pool on Pro/Ultra plans. Several users have reported the effective quota feeling tighter than the old unmetered Gemini 3 Flash days, especially once you start firing off multiple async subagents in parallel (each one consumes quota concurrently). Keep `/usage` open if you're on a metered plan and plan to lean on subagents heavily.

**Security posture.** Like any agentic CLI (Claude Code, Codex CLI, etc.), `agy` can execute arbitrary shell commands, edit files, and reach the network on your behalf. Treat the permissions configuration in §5 as part of setup, not an afterthought — autonomous code execution, prompt injection via untrusted file/web content, and supply-chain risk through MCP servers or community Skills you didn't write are all real, documented concerns, not theoretical ones.

## 2. Installing Antigravity CLI on Ubuntu (WSL)

### 2.1 Prerequisites

```bash
# From PowerShell on Windows — confirm WSL2, not WSL1
wsl --list --verbose
# If needed:
wsl --install
```

Inside Ubuntu/WSL:

```bash
sudo apt update && sudo apt install -y curl
```

### 2.2 Install directly inside WSL (recommended for your setup)

You want `agy` running natively inside Ubuntu as a plain terminal program — not the Windows desktop app reaching into WSL. Install the native Linux binary directly:

```bash
curl -fsSL https://antigravity.google/cli/install.sh | bash
```

This auto-detects your CPU architecture (x86_64 / aarch64), installs the `agy` executable to `~/.local/bin/agy`, and prints a `PATH` hint if needed. Useful flags: `--skip-aliases` (don't touch existing `agy`/`antigravity` shell aliases), `--skip-path` (don't modify your shell profile).

### 2.3 Put `agy` on PATH persistently

```bash
export PATH="$HOME/.local/bin:$PATH"
command -v agy && agy --version

touch "$HOME/.bashrc"
grep -qxF 'export PATH="$HOME/.local/bin:$PATH"' "$HOME/.bashrc" || \
  printf '\nexport PATH="$HOME/.local/bin:$PATH"\n' >> "$HOME/.bashrc"
source "$HOME/.bashrc"
```

(Add the same line to `~/.zshrc` if you use Zsh in WSL.)

### 2.4 Upgrading

Re-run the same installer; it upgrades the binary in place:

```bash
curl -fsSL https://antigravity.google/cli/install.sh | bash
```

The in-app changelog (check `/help` or the startup banner) lists fixes per release — worth a skim after every upgrade since this product changes weekly.

## 3. Authentication / Login

### 3.1 First run

```bash
cd ~/projects/your-project
agy
```

On first launch, `agy` checks the OS secure keyring first (on Linux: the Secret Service API / D-Bus, typically backed by `gnome-keyring` or `kwallet`). If a cached token is valid, sign-in is silent. Otherwise it tries to open a local browser for Google Sign-In.

### 3.2 WSL-specific flow (important)

WSL can't reliably pop a browser window from the Linux side, and the CLI's SSH-aware authentication handles WSL the same way it handles a real remote/SSH session:

1. Run `agy` inside WSL.
2. It detects it can't launch a local browser and prints an authorization URL.
3. Open that URL in your **Windows** browser, sign in with Google.
4. Copy the short authorization code shown in the browser.
5. Paste it back into the WSL terminal prompt.

Credentials are then cached in the Linux keyring, so subsequent launches are silent.

### 3.3 Workspace trust prompt

The first time `agy` points at a new directory:

```
Accessing workspace: /home/you/projects/your-project
Do you trust the contents of this project?
Antigravity CLI requires permission to read, edit, and execute files here.
> Yes, I trust this folder
  No, exit
```

Say yes only for folders you actually trust.

### 3.4 Account tiers (context, verify current pricing yourself)

During the launch window, free-tier rate limits were reported as generous; Google AI Pro (~$20/mo) and Google AI Ultra (~$100/mo, roughly 5x Pro's limits) apply beyond that. Treat the exact numbers as indicative — this is exactly the kind of detail that shifts without notice; check `antigravity.google` for current pricing.

### 3.5 Signing out

```text
/logout
```

## 4. Configuration Files — Where Everything Lives

This is the single most version-volatile part of the whole ecosystem. The table below gives you the most credible path for each file based on cross-checking Google's own documentation pages against each other and against independent hands-on write-ups. Where two reasonably authoritative sources disagree, both candidates are listed — run `agy inspect` to see which one(s) your actual build is reading from.

| What                                                                | Most-supported location                                                                             | Also reported (verify with `agy inspect` / `/skills` / `/mcp`) | Confidence                                                                                                  |
| ------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------- | -------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------- |
| Main CLI settings                                                   | `~/.gemini/antigravity-cli/settings.json`                                                           | —                                                              | High                                                                                                        |
| Keybindings                                                         | `~/.gemini/antigravity-cli/keybindings.json`                                                        | —                                                              | Medium-High                                                                                                 |
| CLI cache / run metadata (incl. `-p` headless runs)                 | `~/.gemini/antigravity-cli/cache/`                                                                  | —                                                              | High (confirmed by a changelog bugfix entry)                                                                |
| Global rules                                                        | `~/.gemini/GEMINI.md` and/or `~/.gemini/AGENTS.md`                                                  | —                                                              | High                                                                                                        |
| Global Skills                                                       | `~/.gemini/config/skills/`                                                                          | `~/.gemini/antigravity-cli/skills/`                            | Medium (genuinely two candidate paths in different official-looking sources)                                |
| Global MCP servers (shared across CLI/IDE/2.0)                      | `~/.gemini/config/mcp_config.json`                                                                  | —                                                              | High — this one is consistently confirmed across `antigravity.google/docs/mcp` and multiple Google Codelabs |
| Global hooks                                                        | `~/.gemini/config/hooks.json`                                                                       | —                                                              | Medium                                                                                                      |
| OAuth tokens for MCP servers                                        | `~/.gemini/antigravity/mcp_oauth_tokens.json`                                                       | —                                                              | High                                                                                                        |
| Installed plugins                                                   | `~/.gemini/antigravity-cli/plugins/<plugin_name>/`                                                  | —                                                              | High (directly from `antigravity.google/docs/cli-features`)                                                 |
| Custom subagent definitions (community-reverse-engineered, see §10) | `~/.gemini/antigravity-cli/agents/<name>/agent.json`                                                | —                                                              | Low-Medium — not in official "how to author" docs yet                                                       |
| Per-conversation transcript & artifacts                             | `<workspace>/.gemini/antigravity/transcript.jsonl` and `<workspace>/.gemini/antigravity/artifacts/` | —                                                              | High (this exact shape appears in the official hooks payload schema)                                        |
| Task/Plan/Walkthrough artifact "brain"                              | `~/.gemini/antigravity/brain/<conversation-GUID>/`                                                  | —                                                              | Medium (community tooling, consistent with artifact concepts in official docs)                              |

### 4.1 Editing settings safely

You _can_ hand-edit `settings.json`, but a malformed file throws an error on next startup. Google's own guidance — and every hands-on write-up — recommends going through the in-app panel instead:

```text
/config
```

or

```text
/settings
```

This opens a full-screen menu; selections write to disk immediately. If you do hand-edit, restart `agy` afterward rather than trusting hot-reload.

### 4.2 `agy inspect` — your single most useful debugging command

```bash
agy inspect
```

Shows you, for the current workspace, exactly what's loaded: which global and project Skills, which Plugins (including imported Gemini CLI extensions), which Hooks are registered, and which MCP servers are connected from `mcp_config.json`. **Whenever this guide and your machine disagree, trust `agy inspect`.**

### 4.3 Sandbox configuration

```json
{
  "enableTerminalSandbox": false
}
```

Default is `false`. Configured in `settings.json`. See §5.4 for how this interacts with the permission engine.

### 4.4 Sharing config across the whole Antigravity ecosystem

`~/.gemini/config/` is the deliberate "shared" folder: MCP servers (`mcp_config.json`) and (per most sources) hooks placed here apply to Antigravity CLI, the Antigravity IDE, and the Antigravity 2.0 desktop app simultaneously, because all three load the same harness configuration from this location. This is also where Plugins are described as living in some docs (vs. the CLI-specific `~/.gemini/antigravity-cli/plugins/` path in others) — another spot where you should let `agy inspect` settle the ambiguity for your build.

### 4.5 A known day-one rough edge worth knowing about

Independent reports describe an environment-variable bug in the _global_ MCP configuration that, as of shortly after launch, forced people to hardcode API keys directly into `mcp_config.json` rather than referencing a shell environment variable. If your MCP server needs a secret and env-var substitution silently isn't working, hardcoding it (and keeping the file out of version control / git-ignoring it) is the known workaround — check whether this has since been fixed in your version before assuming you've misconfigured something.

### 4.6 Migrating an existing Gemini CLI setup

If `agy` detects a legacy `~/.gemini/` Gemini CLI profile on first launch, it offers a one-time import; you can also trigger it explicitly:

```text
/plugin import gemini
```

or from the shell:

```bash
agy plugin import gemini
```

This walks the legacy `~/.gemini/` directory, converts each Gemini CLI **extension** into the new **Plugin** format, migrates MCP server registrations, allow/deny command lists, custom keybindings, model preference, and rewrites your settings into the new schema. Original files are left untouched until you confirm. The official migration guide is documented at `antigravity.google/docs/gcli-migration` and covers edge cases like custom auth providers and air-gapped enterprise installs. Key behavioral differences worth knowing if you're migrating hand-written hooks specifically: Gemini CLI hooks evaluated a shell exit code (non-zero = block) and read the command via `.tool_input.command`; Antigravity hooks instead read a JSON `decision` field (`"allow"`/`"deny"`) from stdout and read the command via `.toolCall.args.CommandLine` — a straight exit-code hook script will not work unmodified.

## 5. Permissions — The Full Picture

This is the system that answers your original "whitelist" question, and it's worth understanding completely before you touch anything else, because it's the actual safety boundary between an LLM and your shell.

### 5.1 The resource model: `action(target)`

Every sensitive operation the agent wants to perform is represented as a string:

```
action(target)
```

Confirmed action types, straight from `antigravity.google/docs/cli-permissions`:

| Action             | Meaning                                                                             |
| ------------------ | ----------------------------------------------------------------------------------- |
| `command(...)`     | Run a specific shell command (regex-matched)                                        |
| `unsandboxed(...)` | Allow a specific command to bypass the sandbox even when sandboxing is generally on |
| `read_file(...)`   | Read a file or directory path                                                       |
| `write_file(...)`  | Write/edit a file or directory path                                                 |
| `read_url(...)`    | Passively fetch a URL's content                                                     |
| `execute_url(...)` | Have the agent take an action against a URL (e.g. trigger something, not just read) |
| `mcp(...)`         | Call a tool exposed by a named MCP server                                           |

### 5.2 The official example — study this one closely

```json
{
  "permissions": {
    "allow": [
      "command(git)",
      "command(npm run (build|lint|test))",
      "unsandboxed(git push)",
      "read_file(/var/log/app)",
      "write_file(src/)",
      "read_url(google.com)",
      "mcp(linter/*)"
    ],
    "deny": [
      "command(rm -rf)",
      "command(curl .*)",
      "command(sudo)",
      "write_file(.git/)",
      "write_file(/home/user/.ssh)"
    ],
    "ask": ["command(*)", "execute_url(aws.amazon.com)", "mcp(sql/execute)"]
  }
}
```

Notice the pattern: `allow` is short and specific (the handful of commands you genuinely run dozens of times a day), `deny` blocks a small number of categorically dangerous things outright, and `ask` is the broad catch-all (`command(*)`) that covers everything you haven't explicitly classified yet. That's the intended shape — don't invert it.

### 5.3 Precedence rule (memorize this)

**Deny > Ask > Allow.** If a request matches rules in more than one list, the strictest applicable rule wins. The official docs use exactly this example: `command(*)` in `ask` plus `command(git)` in `allow` still results in **ask** for every command, because the broader `ask` rule outranks the narrower `allow` rule whenever both match. The practical lesson: don't leave a bare `command(*)` sitting in `allow` unless you've deliberately decided to run fully autonomously — it will swallow everything, including things you didn't mean to pre-approve.

Two derived rules:

- **Write implies Read** — granting `write_file` on a path automatically grants `read_file` on it.
- **Deny-Read implies Deny-Write** — denying `read_file` on a path automatically blocks `write_file` on it too.

The wildcard `*` matches every target within that action's namespace: `command(*)`, `read_file(*)`, `mcp(*)` each mean "everything of this type."

### 5.4 Tool Permission preset (the coarse-grained dial)

Beyond the fine-grained lists, one high-level setting governs overall posture, reachable via `/config` → **Tool Permission**:

- **`request-review`** (default) — the agent pauses and asks before running terminal commands not already in `allow`. Safest day-to-day default.
- **`proceed-in-sandbox`** — unapproved/risky commands auto-run inside an OS-level sandbox instead of being blocked.
- **`always-proceed`** — runs everything without asking ("YOLO mode" via settings rather than the CLI flag).
- **`strict`** — tightest: terminal execution is forced to "always ask" regardless of your allowlist (the terminal allowlist is explicitly ignored in strict mode), browser JavaScript execution is forced to "always ask," and artifact-based actions (acting on a Task Plan / Implementation Plan the agent wrote) are forced to require confirmation too.

### 5.5 Sandbox interaction

```json
{ "enableTerminalSandbox": true }
```

When sandboxing is **on**, the confirmation prompt for a risky command includes a one-off "**Yes, and run without sandbox restrictions**" escape hatch for a specific trusted command. When sandboxing is **off**, the prompt instead offers "**Yes, and run in sandbox**" so you can contain a specific risky command without turning on global sandboxing. Network access can be controlled independently of filesystem sandboxing via a separate "Sandbox Network Access" toggle — useful if you want the agent able to write files freely but never reach the network unsupervised.

### 5.6 Live editing without touching JSON

```text
/permissions
```

Lets you add/edit/remove rules across the merged permission sources directly in the TUI. As of recent CLI versions, the permission system merges **three layers**: project-level permissions, permissions inherited from settings shared across the wider Antigravity ecosystem (`~/.gemini/config/`), and the CLI's own `settings.json`. `/permissions` edits whichever layer is appropriate for what you're changing.

### 5.7 Scope-editing pending requests (a genuinely nice UX detail)

When the agent hits an `ask`-tier file, URL, or MCP operation, an interactive prompt card appears. Before clicking Allow, you can edit the target string inline — e.g. broaden a single-file request like `/project/file.txt` to the parent directory `/project` — and the CLI validates that your edited scope safely covers the operation before applying it for the rest of the turn. This avoids getting re-prompted three times for three sibling files. Note this scope-widening is **not** available for terminal commands — only file/URL/MCP targets.

### 5.8 `--dangerously-skip-permissions` ("YOLO mode")

```bash
agy --dangerously-skip-permissions
```

Bypasses the entire permission system for the session. Genuinely useful for a disposable scratch repo or a throwaway container; a bad default for anything touching your real codebase, credentials, or infrastructure. Prefer a tight `allow` list plus `request-review` for real work — you get most of the speed without losing the safety net.

### 5.9 A known rough edge

There are user-reported bugs where a command explicitly present in the `allow` list still triggers a confirmation prompt — e.g., a PowerShell pipeline being asked about even with "Always proceed" configured. If you hit unexpected prompts despite a seemingly-correct allow rule, this is a recognized class of bug rather than necessarily something wrong with your regex; check the in-app changelog for fixes before assuming you misconfigured the rule.

## 6. Directory Structure: Global vs. Project

### 6.1 Global (applies to every project on your machine)

```
~/.gemini/
├── GEMINI.md                          # Global rules (legacy/native path)
├── AGENTS.md                          # Global rules (newer, cross-tool standard)
├── antigravity-cli/
│   ├── settings.json                  # Main CLI settings
│   ├── keybindings.json               # Custom keybindings
│   ├── skills/<name>/SKILL.md         # CLI-specific global skills (one candidate location — see §4)
│   ├── agents/<name>/agent.json       # Custom subagent definitions (community-documented format)
│   ├── plugins/<plugin_name>/         # Installed plugins, see structure below
│   ├── cache/                         # projects.json + headless (-p) run metadata
│   └── import_manifest.json           # Tracks what was migrated from Gemini CLI
├── antigravity/
│   ├── mcp_oauth_tokens.json          # Cached OAuth tokens for MCP servers
│   └── brain/<conversation-GUID>/     # Task/Plan/Walkthrough artifacts per conversation
└── config/                            # Shared across CLI + IDE + Antigravity 2.0
    ├── skills/<name>/SKILL.md         # Cross-product global skills (other candidate location)
    ├── mcp_config.json                # Cross-product global MCP servers
    └── hooks.json                     # Cross-product global hooks
```

Plugin internal structure (under `~/.gemini/antigravity-cli/plugins/<plugin_name>/`):

```
<plugin_name>/
├── plugin.json           # Required marker file
├── mcp_config.json       # Optional MCP server definitions
├── hooks.json            # Optional event hooks
├── skills/                # Optional skills
├── agents/                # Optional subagents
└── rules/                 # Optional rules
```

**Why two global rule files?** `GEMINI.md` is the original Antigravity/Gemini-lineage path; `AGENTS.md` is the newer cross-tool standard also read by Claude Code, Codex CLI, and Cursor. Use `GEMINI.md` if you only use Antigravity; use `AGENTS.md` if your workflow spans multiple agentic tools, keeping `GEMINI.md` for Antigravity-specific overrides. Both are merged at session start, with `GEMINI.md` taking precedence on conflicts. **Known gotcha:** if Gemini CLI is still installed side-by-side, both tools can write to the same `~/.gemini/GEMINI.md` path and silently clobber each other — prefer `AGENTS.md` for anything you don't want overwritten by the other tool.

### 6.2 Project-level (one specific repo/workspace)

```
your-project/
├── AGENTS.md (or GEMINI.md)            # Project rules — always-on context for this repo
├── .agents/
│   ├── skills/
│   │   └── <skill-name>/
│   │       ├── SKILL.md                # Required
│   │       ├── scripts/                # Optional helper scripts
│   │       ├── references/             # Optional docs/templates (also seen as "examples/")
│   │       └── assets/                 # Optional images/logos
│   ├── agents/                         # Custom subagent definitions, project-scoped
│   ├── rules/*.md                      # Additional modular rule files
│   ├── workflows/*.md                  # Multi-step macro commands (custom /slash-commands)
│   ├── hooks.json                      # Project-level lifecycle hooks
│   ├── hooks/*.sh                      # Conventional location for the scripts hooks.json calls
│   └── mcp_config.json                 # Project-level MCP servers
├── (your normal source code)
```

> **Naming drift you will see in the wild.** A fair amount of community content — including some older Antigravity-IDE-specific tutorials — uses the **singular** `.agent/` directory instead of `.agents/`. Antigravity now defaults to `.agents/skills` (and the equivalent for other component types) but still maintains backward compatibility with `.agent/skills`. If a Skill or Hook you wrote isn't being picked up, try the other spelling, then confirm with `/skills` or `agy inspect`.

### 6.3 What each piece means, one line each

- **`AGENTS.md` / `GEMINI.md` (root)** — your plain-English "constitution" for the repo, prepended to every prompt processed in that directory tree.
- **`.agents/skills/`** — modular, on-demand capability packages, auto-discovered and activated when the task matches a skill's `description`.
- **`.agents/agents/`** — custom subagent persona definitions the main agent can delegate to (format still maturing — see §10).
- **`.agents/rules/`** — same purpose as `AGENTS.md`, split across multiple smaller files for large rule sets.
- **`.agents/workflows/`** — repeatable multi-step procedures invoked as `/workflow-name`. Conceptually: Rules are _passive_ (always-injected), Workflows are _active_ (you trigger them).
- **`.agents/hooks.json`** — JSON-defined interceptors firing at specific points in the agent's execution lifecycle.
- **`.agents/mcp_config.json`** — Model Context Protocol server definitions scoped to this project only.

## 7. Rules (`AGENTS.md` / `GEMINI.md`) — In Depth

Rules are the simplest, highest-leverage customization primitive: plain prose that gets prepended to every prompt processed in scope. Treat the file as a living document, not a one-time setup task — update it the moment you notice the agent repeating the same mistake twice.

### 7.1 Where rules can live and how they stack

| Scope            | File(s)                                      | Applies to                     |
| ---------------- | -------------------------------------------- | ------------------------------ |
| Global           | `~/.gemini/GEMINI.md`, `~/.gemini/AGENTS.md` | Every project on this machine  |
| Project          | `<project-root>/AGENTS.md` or `GEMINI.md`    | This repo only                 |
| Project, modular | `.agents/rules/*.md`                         | This repo only, split by topic |

All applicable files are merged into context at session start. `GEMINI.md` wins over `AGENTS.md` on direct conflicts; project-level rules are additive to global rules (they don't replace them) — so don't repeat your global conventions at the project level, only the project-specific deltas.

### 7.2 Recommended internal structure for a root `AGENTS.md`

There's no single mandated schema (this file is just Markdown the model reads as instructions), but a structure that consistently produces good results in practice looks like this:

```markdown
# AGENTS.md — Project: <name>

## Stack & Conventions

- Language/framework, version constraints.
- Formatting/linting tool and how it's invoked.
- Folder layout conventions (where new components/tests/migrations go).

## Hard Rules (never violate)

- Things that are non-negotiable: no `any` types, no committing secrets,
  no direct writes to the `main` branch, no disabling tests to make a build pass.

## Workflow Expectations

- What to run after every change (`pnpm test`, `pnpm lint`, etc.).
- Commit message convention (e.g. Conventional Commits).
- When to ask before proceeding vs. when to just proceed.

## Domain Context

- Business rules a generic model wouldn't know
  (e.g. "prices are stored in cents," "user IDs are UUIDv7, not incrementing ints").

## Known Gotchas

- Things that have bitten you before: a flaky test, a quirky build step,
  a dependency with a non-obvious API.

## Pointers to Deeper Docs

- @docs/architecture.md
- @docs/database-schema.md
```

The `@path` syntax lets the agent pull in a referenced file on demand instead of inlining everything — useful for keeping the always-loaded root file lean while still making deep documentation reachable. Paths resolve relative to the filesystem root first, then fall back to the workspace root.

### 7.3 Best practices specific to Rules

- **Keep it short relative to Skills.** Everything in `AGENTS.md`/`GEMINI.md` is loaded on _every single turn_ regardless of relevance — unlike Skills, which load on demand. Anything that's only relevant to a specific recurring task (deployments, database migrations, a particular library's quirks) belongs in a Skill, not in the always-on rules file.
- **Prefer "never do X" over "please try to avoid X."** Imperative, unambiguous language produces more reliable compliance than hedged language.
- **Split by topic once the file exceeds roughly a screen and a half.** Move sections into `.agents/rules/testing.md`, `.agents/rules/security.md`, etc. — all files in that directory merge in alongside the root file, so this is purely an organizational split, not a behavioral one.
- **Re-derive rules from real failures.** The highest-value rules are the ones written immediately after the agent did something wrong once — "never do that again" turned into a standing instruction is far more effective than trying to anticipate every mistake up front.
- **Don't duplicate what a Hook can enforce deterministically.** If a rule is "always run the linter after editing a file," a `PostToolUse` Hook (§11) that actually runs the linter is strictly more reliable than asking the model to remember to do it.

## 8. Skills — In Depth

### 8.1 What a Skill is, formally

Per `antigravity.google/docs/skills`: a Skill is a directory containing a `SKILL.md` definition file plus optional supporting assets. Skills are part of an **open, cross-tool standard** ("Agent Skills"), not an Antigravity-only invention — the same basic `name`+`description` format is recognized (with varying levels of extra-field support) by Claude Code, Gemini CLI, and others.

The lifecycle, straight from the official docs:

1. **Discovery** — at the start of a conversation, the agent sees a lightweight list of every available skill's `name` + `description` only (not the full body). This keeps the always-loaded context small even with dozens of skills installed.
2. **Activation** — if a skill's description looks relevant to the current task, the agent reads the _full_ `SKILL.md` content.
3. **Execution** — the agent follows the skill's instructions while working.

You never have to explicitly invoke a skill — the agent decides based on semantic matching against the `description` field — but you can mention a skill by name in your prompt to make sure it gets used.

### 8.2 Minimal required format

The **only two required frontmatter fields**, confirmed directly from Google's own documentation and codelabs, are `name` and `description`:

```markdow
name: git-commit-formatter
description: Formats git commit messages according to the Conventional Commits specification

# Git Commit Formatter

When the user is ready to commit staged changes:
1. Inspect the staged diff to determine the correct type (feat, fix, chore, docs, refactor, test).
2. Write a commit message in the form `type(scope): short summary`.
3. If the change is breaking, add a `BREAKING CHANGE:` footer explaining the impact.
```

**A note on extra frontmatter fields you may see elsewhere (`disable-model-invocation`, `argument-hint`, `allowed-tools`, `model`, `context: fork`, inline `hooks:`).** These are real fields — but they are documented as **Claude Code-specific extensions** to the open Agent Skills standard, not confirmed parts of Antigravity's own SKILL.md implementation. Some of them (like `allowed-tools`) are reported to have partial support in Gemini CLI, but there's no first-party Antigravity documentation confirming any of them work in `agy` today. Don't build a workflow that depends on `disable-model-invocation: true` actually suppressing auto-invocation in Antigravity until you've verified it with `/skills` against your own build — assume name+description is the only guaranteed-portable subset, and treat the rest as "Workflows" instead (§9), which is Antigravity's own confirmed mechanism for "only runs when I type it."

### 8.3 Recommended `SKILL.md` body structure

Beyond the two required fields, the body is just Markdown, but a structure that the official Google Codelab walkthrough and independent guides converge on looks like this:

```markdow
name: code-review
description: Reviews code changes for bugs, security issues, style, and best practices. Use when the user asks to review code, check a diff, or audit a PR

# Code Review

## When to use this skill
- The user asks to review, audit, or critique a file, diff, or PR.
- The user mentions "is this safe to merge."

## How to use it
1. Read the target file(s)/diff in full before commenting.
2. Report findings in this priority order: correctness bugs, security issues,
   then style/maintainability (keep this section brief).
3. Reference findings as `file:line`.
4. Do not rewrite the code yourself unless explicitly asked — only report findings.

## Examples of issues to flag
- Unhandled errors / swallowed exceptions
- Secrets or credentials committed in plaintext
- SQL built via string concatenation instead of parameterized queries

## Output format
A numbered list, one finding per item, each with severity (high/medium/low).
```

A "Mission Statement" section (one sentence on _why_ the skill exists) is also a pattern worth adopting for skills you'll hand off to teammates — it helps a human skim the skill catalog without reading the full instructions.

### 8.4 Full directory layout

```
.agents/skills/my-skill/
├── SKILL.md       # Required — metadata + instructions
├── scripts/       # Optional — Python/Bash/Node/Go helper scripts the agent can execute
├── references/    # Optional — text, documentation, or templates (also seen as "examples/")
└── assets/        # Optional — images, logos, other binary assets
```

Antigravity is explicitly **language-agnostic** for scripts — it will shell out to whatever's on `PATH` (Python, Node, Bash, Go), though Python is the most common choice for readability. If a script takes flags (e.g. `--env`), the `SKILL.md` instructions must spell out how the agent should map natural-language intent (the user said "deploy to staging") onto the correct invocation (`--env staging`) — the agent does not introspect the script's argument parser on its own.

### 8.5 Scope: project vs. global

| Scope   | Location                                                                                                                      | Use case                                                                                                                    |
| ------- | ----------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| Project | `<project-root>/.agents/skills/<name>/SKILL.md`                                                                               | Team conventions, this repo's deployment process, project-specific testing rules — shared via version control automatically |
| Global  | `~/.gemini/config/skills/<name>/SKILL.md` (or `~/.gemini/antigravity-cli/skills/<name>/SKILL.md` — verify with `agy inspect`) | Personal utilities and general-purpose tools you want in every project                                                      |

### 8.6 Best practices, straight from official guidance + accumulated community experience

- **The `description` field is everything.** It's literally how the agent finds the skill among potentially dozens of others. Write it the way you'd brief a new hire, in third person, packed with the actual keywords/phrases a user would type. "Helps with stuff" will never trigger reliably; "Generates pytest-style unit tests for Python modules following this repo's fixture conventions" will.
- **One skill, one job.** Don't build a monolithic `dev-workflow` skill; split into `code-review`, `release-notes-drafter`, `database-migrator`, etc. Smaller, sharply-scoped skills both trigger more reliably and keep the per-activation context small.
- **Treat bundled scripts as black boxes the agent should run, not read.** Instruct the agent to call a helper script with `--help` first rather than reading its full source — this saves context budget that would otherwise go toward re-deriving something a one-line usage string already tells it.
- **Use decision trees in the instructions for branchy tasks.** E.g., explicitly: "If the user mentions 'production,' use `deploy_prod.sh`; otherwise use `deploy_staging.sh`." This is far more reliable than hoping the model infers the right branch from vibes.
- **Adopt a `risk:` convention even though Antigravity doesn't enforce one natively.** Community skill libraries informally tag skills `none` / `safe` / `critical` / `offensive` / `unknown` based on whether the skill is read-only, mutates files, or does something destructive. Even though the CLI itself won't act on this field, putting it at the top of your own `SKILL.md` files (and reviewing anything marked `critical` before installing it from someone else) is good hygiene — remember a skill is just text instructing the agent, and a malicious or careless skill can absolutely tell the agent to run something destructive.
- **Skills are an attack surface.** Anything you `git clone` from a community skill repository and drop into `.agents/skills/` is, functionally, a set of instructions an LLM with shell access will follow. Read every skill you install from outside your own org before trusting it, exactly as you would a shell script someone emailed you.

### 8.7 Verifying a skill loaded

```text
/skills
```

Lists currently discovered skills with their name + description, for both scopes.

## 9. Custom Commands / Workflows — In Depth

### 9.1 Why this exists separately from Skills

Skills are _semantically_ triggered — the model decides when they're relevant. Sometimes you want a command that **only** runs when you explicitly type it: a release checklist, a deploy pipeline, anything with side effects where _timing_ matters and you don't want the model deciding on its own that "now seems like a good time." That's what Workflows are for, stored as Markdown files under `.agents/workflows/`, invoked the same way as a Skill but as an explicit, user-triggered macro (`/workflow-name`) rather than something the model can self-activate.

### 9.2 Recommended file structure

```markdow
description: Deploys the current branch to staging and runs smoke tests

## Steps

### 1. Build
- Run the production build command.
- Fail fast if the build errors; report the error verbatim and stop.

### 2. Deploy
- Push the build artifact to the staging bucket.
- Tag the deploy with the current git SHA.

### 3. Smoke test
- Hit the `/healthz` endpoint and confirm HTTP 200.
- Report pass/fail clearly; on failure, do not proceed to notify anyone — just report.
```

Argument passing follows the same convention as Skills' optional `argument-hint`/`$ARGUMENTS` pattern when supported by your build — typing `/deploy-staging hotfix-1234` should make `hotfix-1234` available to the workflow body as free text. Verify the exact substitution syntax against `/help` on your version, since this is one of the less battle-tested corners of the spec.

Workflows can call other workflows — e.g. a top-level `/ship-feature` workflow whose steps explicitly instruct "now run `/run-tests`" as one of its own steps — which is the closest thing to composable pipelines in this system.

### 9.3 A genuinely powerful pattern: simulating a multi-role "team" with Workflows + Skills + Rules

This is a pattern documented in an official Google Codelab ("Build Autonomous Developer Pipelines using agents.md and skills.md in Antigravity") and worth adopting directly, because it gets you most of the value of "multiple specialized agents" using only the well-confirmed primitives (Rules, Skills, Workflows) rather than the less mature native subagent format from §10.

The recipe:

1. **A rules file (`AGENTS.md`, or a dedicated `team.md` referenced from it) defines your "roles."** Literally: "You have a Product Manager, a Full-Stack Engineer, a QA Engineer, and a DevOps persona. When acting as a given role, follow that role's skill."
2. **Each role gets a corresponding Skill** with concrete instructions and an explicit artifact handoff — e.g. `generate_code.md`, `audit_code.md`, `deploy_app.md` — each one written as "when acting as Role X, do Y, then write/update artifact Z for the next role to pick up."
3. **A Workflow chains them**, instructing the agent to shift persona sequentially:

```markdow
description: Runs the full PM → Engineer → QA → DevOps development cycle for a feature request

## Steps

1. Act as the **Product Manager**. Draft `Technical_Specification.md` from the user's request.
   If the user provides feedback/comments on the file, revise and re-present it.
   Loop until the user types "Approved."
2. Act as the **Full-Stack Engineer**. Execute the `generate_code` skill against the approved spec.
3. Act as the **QA Engineer**. Execute the `audit_code` skill against the generated code.
4. Act as the **DevOps** persona. Execute the `deploy_app` skill.
```

Triggered as `/startcycle "I need a real-time chat feature for customer support"`. Because each "role" hands off through a concrete file artifact (a spec doc, a code diff, an audit report) rather than relying on implicit memory, this pattern stays coherent even across a long, multi-step run — and it's robust to the underlying subagent-authoring format changing later, since it's built entirely on Rules/Skills/Workflows.

## 10. Agents / Subagents — In Depth

This is the area with the biggest gap between "officially documented and rock-solid" and "exists, but the authoring format is still settling." Treat the two halves of this section very differently.

### 10.1 Built-in async Subagents (solid, officially documented)

**What they are.** Independent, concurrent agent sessions the _main_ agent spawns to tackle background work — documentation lookups, running a build, validating a fix — **without blocking your active conversation**. This is the headline feature Antigravity CLI has that Gemini CLI never did.

**Capabilities.** Subagents have full access to the same tool surface as the main agent: code search, file editing, terminal commands, web search, and browser automation.

**Dispatching one explicitly:**

```text
/agent <subagent-type> "<task description>"
```

Example: `/agent refactor "Convert all callback-based handlers in src/api to async/await"`. The main agent can also spawn these on its own initiative without you typing this — the explicit form is for when you want to force parallelism deliberately rather than wait for the model to decide to delegate.

**Monitoring and approving them — the Agent Manager Panel:**

```text
/agents
```

Opens a live panel listing active and completed subagents, their status (`running`/`done`/`killed`/etc.), and the step they're currently on. Selecting one opens a full detail view: the entire subagent conversation, its reasoning steps, and its tool execution log.

**Approving a subagent's pending action:**

- A **Fast Path Alert** appears directly above your main prompt box whenever a subagent is waiting on a permission decision, so you don't have to go hunting for it.
- Inside the detail view, there's a dedicated interaction section listing all pending approvals where you selectively approve or deny.
- `Ctrl+J` "teleports" you straight from the main conversation to the detail view of whichever subagent is next waiting on your approval — the fastest way to triage a pile of parallel approvals without manually opening `/agents` and scrolling.

**Practical recipes worth trying** (these are usage patterns, not a special config format — just prompt the orchestrator this way): a "researcher + writer" pair where one subagent gathers context and another drafts based on it, or a "scout + fixer" pair where one subagent locates every occurrence of a problem across a codebase and a second one fixes them once located.

**A real demonstrated example.** Given a messy CSV and a high-level instruction, the main agent has been shown to autonomously: inspect the data, decide on three specialized subagent roles (clean, analyze, visualize), run two of them in parallel once the data was clean, and assemble a self-contained interactive HTML report at the end — without you hand-defining any of the three subagent roles in advance. The orchestrator designed the team itself.

**Note on quota.** Each parallel subagent consumes quota concurrently with the main conversation and with every other subagent — see §1's note on the shared quota pool.

### 10.2 Custom, user-authored subagent personas (newer, not yet first-party documented for authoring)

There is community-reverse-engineered evidence of a JSON-based format for defining a _named, reusable_ subagent persona, found at:

```
~/.gemini/antigravity-cli/agents/<name>/agent.json
```

Example, reconstructed from a Google AI Developers community discussion:

```json
{
  "name": "code_reviewer",
  "description": "A subagent specialized in reviewing diffs for bugs, security issues, and style violations before merge.",
  "hidden": false,
  "config": {
    "customAgent": {
      "systemPromptSections": [
        {
          "title": "Agent System Instructions",
          "content": "You are a strict senior code reviewer. Report bugs first, then security issues, then style. Reference findings as file:line. Never rewrite code yourself."
        }
      ],
      "toolNames": ["find_by_name", "grep_search", "view_file", "list_dir"],
      "systemPromptConfig": {
        "includeSections": [
          "user_information",
          "mcp_servers",
          "skills",
          "subagent_reminder",
          "messaging",
          "artifacts",
          "user_rules"
        ]
      }
    }
  }
}
```

Field meanings, as best understood:

- `name` / `description` — identity and the semantic trigger the main agent uses to decide when to delegate to this persona.
- `hidden` — whether the persona shows up in user-facing listings.
- `config.customAgent.systemPromptSections` — the persona's actual system-prompt content, as one or more titled sections.
- `toolNames` — restricts which tools this persona can call (omit entries to scope it down — e.g. a pure "researcher" persona that can read and search but never write or run shell commands).
- `systemPromptConfig.includeSections` — which standard context blocks (MCP server list, skills list, user rules, etc.) get spliced into this persona's prompt alongside its own custom section.

**Caveat, stated plainly:** this format comes from community reverse-engineering and a GitHub discussion thread asking specifically "what's the subagent format for antigravity-cli," not from a first-party "how to author a subagent" tutorial on `antigravity.google/docs`. There was also an open feature request on Google's own AI Developers forum (dated shortly before this format surfaced) explicitly asking for Claude-Code/Cursor-style user-defined subagents to be added — implying this capability may have been newly landing or still maturing around the time this guide was written. **Don't build critical workflow automation on this exact schema yet.** Prototype with it, verify the result actually shows up correctly in `/agents`, and prefer the Rules+Skills+Workflows "simulated team" pattern from §9.3 for anything you need to be reliable today.

## 11. Hooks — In Depth

Hooks are deterministic interceptors at specific points in the agent's execution lifecycle — the right place for guardrails and side effects you don't want to depend on the model "remembering" to do.

### 11.1 File location and top-level shape

`hooks.json`, scoped per §6 (project: `.agents/hooks.json`; global/shared: `~/.gemini/config/hooks.json`). Top-level keys are your own labels for organizing related hook blocks — they have no special meaning beyond that:

```json
{
  "my-linter-hook": {
    "PostToolUse": [
      {
        "matcher": "run_command",
        "hooks": [
          { "type": "command", "command": "./scripts/lint.sh", "timeout": 10 }
        ]
      }
    ]
  },
  "safety-gate": {
    "enabled": false,
    "PreToolUse": [
      {
        "matcher": "run_command",
        "hooks": [{ "command": "./scripts/safety-check.sh" }]
      }
    ]
  },
  "reminder": {
    "PreInvocation": [{ "type": "command", "command": "./scripts/reminder.sh" }]
  }
}
```

Any block can be temporarily switched off without deleting it via `"enabled": false` at the same level as the event keys.

### 11.2 Confirmed lifecycle events (directly from `antigravity.google/docs/hooks`)

| Event            | Fires                              | Structure                                                                        |
| ---------------- | ---------------------------------- | -------------------------------------------------------------------------------- |
| `PreToolUse`     | Before a tool executes             | List of `{matcher, hooks: [...]}` — matcher is meaningful here                   |
| `PostToolUse`    | After a tool completes             | Same shape as `PreToolUse`                                                       |
| `PreInvocation`  | Before a model invocation          | Simpler — a flat list of handlers directly under the key; **matcher is ignored** |
| `PostInvocation` | After a model invocation           | Same simplified shape as `PreInvocation`                                         |
| `Stop`           | When the execution loop terminates | Same simplified shape                                                            |

For `PreToolUse`/`PostToolUse`, `matcher` is a regular expression evaluated against the tool name. Confirmed/observed tool names you can match against include `run_command` (shell execution), file-mutation tools like `write_to_file`, `replace_file_content`, `multi_replace_file_content`, read-oriented tools like `view_file`/`read_file`, and browser-automation tools prefixed `browser_*`. The official docs describe these as grouped into categories with a fuller reference list — check `/hooks` or the docs site directly for the complete, current set, since this is exactly the kind of enumeration that grows as new tools get added.

Matcher examples:

```
"matcher": "*"                          → matches every tool
"matcher": "run_command"                → matches exactly this tool
"matcher": "run_command|view_file"      → matches either
"matcher": "browser_.*"                 → matches anything prefixed browser_
```

### 11.3 Input/output contract (this is the part that actually matters)

**Input** arrives on **stdin** as JSON, camelCase field names. All events share a common metadata envelope:

```json
{
  "conversationId": "ec33ebf9-0cba-4100-8142-c61503f6c587",
  "workspacePaths": ["/workspace/project"],
  "transcriptPath": "/workspace/project/.gemini/antigravity/transcript.jsonl",
  "artifactDirectoryPath": "/workspace/project/.gemini/antigravity/artifacts"
}
```

`PreToolUse`/`PostToolUse` additionally include the tool call itself, plus a `stepIdx`:

```json
{
  "toolCall": {
    "name": "run_command",
    "args": {
      "CommandLine": "npm test",
      "Cwd": "/workspace/project",
      "WaitMsBeforeAsync": 5000
    }
  },
  "stepIdx": 19,
  "conversationId": "...",
  "workspacePaths": ["..."],
  "transcriptPath": "...",
  "artifactDirectoryPath": "..."
}
```

`Stop` includes termination context instead:

```json
{
  "executionNum": 1,
  "terminationReason": "model_stop",
  "error": "",
  "fullyIdle": true,
  "conversationId": "...",
  "workspacePaths": ["..."],
  "transcriptPath": "...",
  "artifactDirectoryPath": "..."
}
```

**Output** goes on **stdout** as JSON. For `PreToolUse`, your script can actually steer the agent:

```json
{
  "decision": "ask",
  "reason": "Requires confirmation for test execution.",
  "permissionOverrides": ["command(npm test)"]
}
```

`decision` is `"allow"`, `"deny"`, or `"ask"`. `reason` is shown back to the agent/user explaining why. `permissionOverrides` can grant a one-off permission scoped to this call. For `PostToolUse`, the hook is purely observational — return an empty `{}`; there's nothing left to block at that point.

### 11.4 A complete, realistic example: blocking destructive git/shell operations

This is adapted from a real migration write-up and is a genuinely good starting point for any serious project:

`.agents/hooks.json`:

```json
{
  "block-destructive-ops": {
    "PreToolUse": [
      {
        "matcher": "run_command",
        "hooks": [
          {
            "type": "command",
            "command": ".agents/hooks/block-destructive-ops.sh"
          }
        ]
      }
    ]
  }
}
```

`.agents/hooks/block-destructive-ops.sh`:

```bash
#!/usr/bin/env bash
set -euo pipefail

input=$(cat)

# Prefer jq; fall back to a crude sed extraction so the gate never silently
# fails open if jq is missing.
cmd=""
if command -v jq >/dev/null 2>&1; then
  cmd=$(printf '%s' "$input" | jq -r '.toolCall.args.CommandLine // empty' 2>/dev/null)
fi
[ -z "$cmd" ] && cmd=$(printf '%s' "$input" | sed -n 's/.*"CommandLine"[[:space:]]*:[[:space:]]*"\([^"]*\)".*/\1/p')

patterns=(
  'rm[[:space:]]+-r?f?[[:space:]]+/[[:space:]]*$'
  'rm[[:space:]]+-r?f?[[:space:]]+~'
  'git[[:space:]]+push([[:space:]].*)?--force'
  'git[[:space:]]+push([[:space:]].*)?[[:space:]]-f([[:space:]]|$)'
  'git[[:space:]]+reset[[:space:]]+--hard'
  'git[[:space:]]+branch[[:space:]]+-D'
  'chmod[[:space:]]+-?R?[[:space:]]*777'
  'curl[^|]+\|[[:space:]]*(bash|sh)([[:space:]]|$)'
  'wget[^|]+\|[[:space:]]*(bash|sh)([[:space:]]|$)'
)

for p in "${patterns[@]}"; do
  if [[ "$cmd" =~ $p ]]; then
    printf '{"decision":"deny","reason":"Blocked potentially destructive command: %s"}\n' "$cmd"
    exit 0
  fi
done

printf '{"decision":"allow"}\n'
```

### 11.5 Other high-value hook ideas

- **`PostToolUse` auto-formatting/linting** after every `write_to_file`/`replace_file_content`, so style is enforced deterministically instead of relying on the model remembering your style rules.
- **`SessionStart`-equivalent context loading** (model your own version using `PreInvocation` on the very first turn, or check whether your build exposes a literal `SessionStart` event — some docs reference one) — e.g. running `git status --short` and printing open TODOs so the agent always starts with fresh repo state instead of stale assumptions.
- **Memory persistence/retrieval** — a `Stop` hook to persist a session summary to a memory store, and a `PreInvocation` hook to retrieve relevant memories before the model is invoked. This is the recommended pattern for adding durable cross-session memory without relying on the model to explicitly decide to call a "save memory" tool.
- **Telemetry/logging** — hooks are also simply a good place to put your own observability: every tool call passing through a `PostToolUse` hook is a natural point to emit structured logs or metrics.

## 12. MCP Servers — In Depth

### 12.1 What MCP gives you here, concretely

Model Context Protocol (MCP) is how Antigravity reaches outside the local filesystem/shell sandbox into live external systems — databases, issue trackers, internal docs, SaaS APIs — without you manually pasting context into the conversation. Two illustrative official examples: when writing a SQL query, the agent can inspect your live database schema via an MCP server instead of guessing column names; when debugging, it can pull recent build logs from a CI/CD provider directly.

### 12.2 Configuration file and location

```
~/.gemini/config/mcp_config.json     # Global, shared across Antigravity CLI + IDE + Antigravity 2.0
.agents/mcp_config.json              # Project-scoped (reported; verify with /mcp on your build)
```

(Windows equivalent of the global path: `%userprofile%\.gemini\config\mcp_config.json` — not relevant to your WSL setup, but useful if you ever cross-reference a Windows-focused tutorial.)

The file has a single top-level `mcpServers` object, one key per server.

### 12.3 Local (stdio) servers

For a server that's a local executable:

```json
{
  "mcpServers": {
    "my-local-server": {
      "command": "path/to/executable",
      "args": ["--arg1", "value1"],
      "env": {
        "API_KEY": "your-api-key"
      }
    }
  }
}
```

Real-world example (GitHub's official MCP server via Docker):

```json
{
  "mcpServers": {
    "github": {
      "command": "docker",
      "args": [
        "run",
        "-i",
        "--rm",
        "-e",
        "GITHUB_PERSONAL_ACCESS_TOKEN",
        "ghcr.io/github/github-mcp-server"
      ],
      "env": { "GITHUB_PERSONAL_ACCESS_TOKEN": "YOUR_GITHUB_PAT" }
    }
  }
}
```

### 12.4 Remote (HTTP) servers — the field name to get right

```json
{
  "mcpServers": {
    "my-remote-server": {
      "serverUrl": "https://api.example.com/mcp/",
      "headers": {
        "Authorization": "Bearer your-token"
      }
    }
  }
}
```

**Critical detail multiple independent sources flag explicitly: Antigravity uses `serverUrl`, not `url` or `httpUrl`** — this is different from the field name Cursor and VS Code use for the equivalent concept, and copy-pasting a config from one of those tools without renaming the field is the single most common setup mistake reported. (One CLI changelog entry does mention adding support for a plain `url` key too in a later release — if `serverUrl` doesn't seem to be picking up, try `url` as a fallback and check `/mcp`, but treat `serverUrl` as the primary, most broadly-compatible field name.)

### 12.5 Authentication options

**Custom headers** (API keys, bearer tokens) — shown above via `headers`.

**Google Application Default Credentials**, for first-party Google Cloud MCP services:

```json
{
  "mcpServers": {
    "my-gcp-service": {
      "serverUrl": "https://example.googleapis.com/mcp/",
      "authProviderType": "google_credentials"
    }
  }
}
```

This requires ADC to already be configured on your machine (`gcloud auth application-default login`).

**OAuth with manually-supplied client credentials:**

```json
{
  "mcpServers": {
    "oauth-server": {
      "serverUrl": "https://api.example.com/mcp/",
      "oauth": {
        "clientId": "your-client-id",
        "clientSecret": "your-client-secret"
      }
    }
  }
}
```

If you supply client credentials manually, you must register the matching redirect URI with your OAuth provider. Authenticate either through the desktop app's **Agent Settings → Customizations** tab (Cmd+, / Ctrl+,) by clicking **Authenticate** next to the server, or — relevant to your terminal-only workflow — directly inside `agy`:

```text
/mcp
```

Select the server, choose **Authenticate**, open the printed URL in your browser, complete the flow, copy the authorization code, and paste it back into the terminal prompt. Tokens land in `~/.gemini/antigravity/mcp_oauth_tokens.json`; expired tokens refresh automatically, invalid ones get removed automatically.

**Servers supporting Dynamic Client Registration (DCR)** need no manual `oauth` block at all — Antigravity handles the OAuth handshake automatically.

### 12.6 Other fields

- `disabled: true` — keep a server's configuration on file without it being active (useful for servers you only need occasionally, to avoid wasting context budget on tool definitions you're not using right now).
- `env` — environment variables passed to a local stdio server's process.

### 12.7 A known footgun

There's a documented day-one bug where environment-variable substitution inside the **global** `mcp_config.json` doesn't reliably work, forcing API keys to be hardcoded directly into the file as a workaround. If you do this: make sure `~/.gemini/config/mcp_config.json` itself is never committed anywhere, and treat any secret placed there the same way you'd treat a `.env` file — file permissions matter (`chmod 600`).

### 12.8 Verifying and managing servers

```text
/mcp
```

Lists configured MCP servers, their connection status, and lets you trigger authentication for ones that need it.

### 12.9 Best practices

- **Keep total enabled tools under roughly 50** across all your active MCP servers for best performance — every tool definition counts against context budget and against the model's ability to pick the right one. Disable servers you're not actively using for the current task rather than leaving everything on permanently.
- **Prefer the official, first-party MCP server when one exists** over a generic community wrapper for the same service — first-party servers tend to track API changes faster and have narrower, better-curated toolsets.
- **Scope MCP credentials as tightly as the underlying service allows.** A GitHub PAT scoped to one repo, a database role that's read-only — apply the same least-privilege thinking here you'd apply to any other credential, since an MCP server's tools are just as "real" as a shell command from the permission engine's perspective (note the `mcp(...)` permission action type from §5 — you can `deny`/`ask`/`allow` individual MCP tool calls the same way you control shell commands).
- **Project-scope servers that are project-specific** (a project's own database, its own issue tracker project) in `.agents/mcp_config.json` rather than the global file, so other projects' agents don't see tools they have no legitimate use for.

## 13. Session Management — In Depth

### 13.1 Where conversations actually live

Conversation storage moved to a **SQLite-based format** (`.db` / `.db-wal` files) as of a recent point release — this is now described as the CLI's standard conversation format going forward. Earlier builds, and the desktop app historically, used protobuf (`.pb`) files; both formats can coexist on a machine that's been through an upgrade, which is why `/resume`'s lazy-loading logic was specifically updated to scan both SQLite and legacy formats.

Beyond raw chat history, Antigravity also persists structured **artifacts** per conversation — Task lists, Implementation Plans, and Walkthroughs the agent writes as it works — under a per-conversation folder keyed by a GUID, roughly:

```
~/.gemini/antigravity/brain/<conversation-GUID>/
├── task.md                          # The agent's working task checklist
├── task.md.metadata.json
├── implementation_plan.md           # The agent's plan before executing
├── implementation_plan.md.metadata.json
├── walkthrough.md                   # A human-readable summary of what was done
├── walkthrough.md.metadata.json
├── *.md.resolved.N                  # Versioned snapshots (N increments on each update)
└── *.md.resolved.N.metadata.json
```

Each artifact is versioned — every update to `task.md`/`implementation_plan.md`/`walkthrough.md` creates an incrementing `.resolved.N` backup, giving you a full audit trail and rollback capability for the agent's own planning documents, independent of your actual source code's git history. Per-conversation transcripts and tool-execution artifacts (the files referenced inside hook payloads, §11.3) live under the workspace itself at `<workspace>/.gemini/antigravity/transcript.jsonl` and `<workspace>/.gemini/antigravity/artifacts/`.

Exact filenames/paths have shifted across recent releases — this is one of the fastest-moving areas of the whole product. Don't script anything fragile directly against these files; use the commands below instead.

### 13.2 Core conversation commands

| Command                     | What it does                                                                                                                                                                                                              |
| --------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `/clear`                    | Starts a brand-new conversation thread. The old one isn't deleted — you can switch back via `/resume`.                                                                                                                    |
| `/resume` (alias `/switch`) | Opens a picker of past conversations. Press `Tab` inside the picker to cycle between **CLI** sessions and **Antigravity 2.0 desktop** sessions — you can pull a desktop conversation straight into the terminal this way. |
| `/rewind` (alias `/undo`)   | Rolls the _active_ conversation back to an earlier turn — useful when a step went wrong and you want to retry from before it, without losing the whole thread.                                                            |
| `/fork`                     | Clones the entire conversation history up to the current turn into a **new, independent session** with its own session ID. Your active terminal switches to the new branch immediately.                                   |
| `/rename <name>`            | Gives the active conversation a human-readable title instead of an auto-generated identifier.                                                                                                                             |
| `/export`                   | Pushes the current CLI conversation into the Antigravity 2.0 desktop app, for richer visual diffs/graph views.                                                                                                            |

Launch-time shortcuts:

```bash
agy --continue              # or -c — jump into the most recent session for this workspace, skipping the picker
agy --conversation <id>     # resume a specific previous conversation by ID
```

### 13.3 Important caveat about `/fork`

`/fork` clones the **conversation thread**, not your **git working tree**. Two forked sessions are still pointed at the same uncommitted files on disk and will fight over them if both start editing. For true parallel experiments — different code states, not just different chat histories — pair `/fork` (or simply two fresh sessions) with separate git branches or, better, `git worktree`:

```bash
git worktree add ../your-project-experiment-b feature/experiment-b
```

### 13.4 Can sessions communicate with each other?

There's no built-in "session A sends a message to session B" API — no direct IPC channel between two independent `agy` processes. What you actually have, and how to use each as a substitute depending on what you're trying to achieve:

1. **Shared on-disk context.** Every session in the same project reads the same `AGENTS.md`/`GEMINI.md` and `.agents/rules/`. For two parallel sessions (e.g. one per tmux pane) that need to stay loosely coordinated, have them read/write a shared handoff file — a `STATUS.md`, a `TASK.md`, or a dedicated artifacts folder — and add a rule instructing the agent to check that file at the start of a session and update it after finishing a unit of work.
2. **Background Subagents within one session (§10.1)** — the officially supported mechanism for one piece of agent work to feed directly into another's, all visible and approvable from a single `/agents` panel. Prefer this over multiple independent top-level sessions whenever the tasks are genuinely related and you want centralized oversight.
3. **`/fork` + git branches/worktrees** for "try two different approaches, then compare and pick a winner" experiments.
4. **A custom MCP server**, if you genuinely need structured coordination across multiple long-running agents (e.g. a shared task queue with locking) — this is the "build it yourself" option once shared files and subagents aren't enough.

## 14. Full Command & Flag Reference

### 14.1 Shell-level flags

This list is reconstructed directly from a captured `agy --help` output, so it reflects real, currently-shipping flags (some guides online describe flags — like `--output-format json` — that were _suggested_ but errored as "not defined" when actually tested; that flag is **not** confirmed to exist and is omitted below):

```
agy                                   # launch interactive TUI in the current directory
agy --version                         # print version
agy --help                            # print all flags/subcommands for your installed build
agy --print "<prompt>"                # alias: --prompt, -p — run a single prompt non-interactively, print the response, exit
agy --print-timeout <duration>        # timeout for print-mode wait (default 5m0s)
agy --prompt-interactive "<prompt>"   # alias: -i — start interactively, but seeded with this prompt
agy --continue                        # alias: -c — continue the most recent conversation
agy --conversation <id>               # resume a specific previous conversation by ID
agy --add-dir <path>                  # add an extra directory to the workspace (repeatable)
agy --dangerously-skip-permissions    # auto-approve all tool permission requests without prompting
agy --log-file <path>                 # override the CLI's log file path
agy inspect                           # debug: shows exactly which skills/plugins/hooks/MCP servers loaded for this workspace
agy plugin import gemini              # one-shot import of a legacy Gemini CLI profile
agy plugin install <name>             # install a named plugin
agy plugin list                       # list installed plugins
```

`-p`/`--print` is the flag you'll use most for scripting/automation/keybindings (§16.5) — it's a true one-shot, non-interactive call that prints the model's response and exits, distinct from just seeding the interactive TUI.

### 14.2 Slash commands (inside the interactive TUI)

Type `/` for the live, filtering command menu, or `?` for the full offline help reference.

| Command                  | Purpose                                                                                                                                       |
| ------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------- |
| `/help`, `?`             | Show all commands and keybindings                                                                                                             |
| `/usage`                 | Offline developer manual; also shows quota usage if you're on a metered plan                                                                  |
| `/config`, `/settings`   | Open the full settings panel (theme, model, permissions, sandbox, Tool Permission)                                                            |
| `/permissions`           | Add/edit/remove fine-grained allow/ask/deny rules across the merged permission layers                                                         |
| `/keybindings`           | View/edit custom keybindings                                                                                                                  |
| `/model`                 | Switch the active model (Gemini 3.5 Flash, Gemini 3.1 Pro, Claude Sonnet, Claude Opus, GPT-OSS 120B, subject to plan)                         |
| `/clear`                 | Start a new conversation                                                                                                                      |
| `/resume` (`/switch`)    | Reopen a previous conversation (CLI or desktop)                                                                                               |
| `/fork`                  | Branch the current conversation into a new session                                                                                            |
| `/rewind` (`/undo`)      | Roll back conversation history                                                                                                                |
| `/rename <name>`         | Rename the active conversation                                                                                                                |
| `/goal`                  | Hand the orchestrator a high-level goal and let it autonomously plan, profile the workspace, and spawn whatever subagents it decides it needs |
| `/agent <type> "<task>"` | Explicitly dispatch a background subagent for a specific task                                                                                 |
| `/agents`                | Open the Agent Manager Panel (subagent status, detail view, approvals)                                                                        |
| `/tasks`                 | View shell execution logs                                                                                                                     |
| `/skills`                | List currently loaded skills (project + global)                                                                                               |
| `/hooks`                 | Inspect currently active hooks                                                                                                                |
| `/mcp`                   | Manage MCP servers — list, check connection status, authenticate                                                                              |
| `/diff`                  | Show the diff of files the agent has modified                                                                                                 |
| `/export`                | Push this conversation to the Antigravity 2.0 desktop app                                                                                     |
| `/browser`               | Explicitly engage the browser subagent for the current task                                                                                   |
| `/add-dir`               | Add an additional directory to the agent's workspace context                                                                                  |
| `/statusline`            | Configure the custom status line                                                                                                              |
| `/plugin import gemini`  | Import a Gemini CLI profile                                                                                                                   |
| `/logout`                | Sign out and purge cached credentials                                                                                                         |
| `/quit`                  | Exit the CLI (also: `Ctrl+D` twice)                                                                                                           |

### 14.3 Default keybindings worth knowing

| Key           | Action                                                                                                                                                      |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `Ctrl+I`      | Command mode — quick, inline one-shot assistance without leaving your current context (terminal equivalent of "give me a completion and get out of my way") |
| `Ctrl+R`      | Open the Artifact Review Panel — works even while a permission confirmation is pending, preserving your progress on the confirmation when you toggle back   |
| `Ctrl+K`      | Fast-approve a pending action shown above the prompt                                                                                                        |
| `Ctrl+J`      | Teleport to the detail view of the next subagent waiting on your approval                                                                                   |
| `Ctrl+D` (x2) | Quit                                                                                                                                                        |

All editable via `/keybindings` or `keybindings.json` directly.

## 15. Other Notable Strengths / Power-User Features

- **Async background subagents** (§10.1) — the headline differentiator over the old Gemini CLI.
- **Browser subagent** — an MCP-driven headless-Chrome agent that navigates pages, clicks through real UI flows, takes screenshots, and verifies a feature actually works end-to-end in a browser, not just "the code compiles." Invoke explicitly with `/browser` to guarantee this behavior rather than leaving it to model judgment; no extra configuration is required to use it.
- **`/goal`-driven autonomous orchestration** — hand the agent a high-level outcome and let it decide its own subagent breakdown, as demonstrated in the CSV-cleaning example in §10.1.
- **Plugins** — a single deployable bundle of skills + subagents + rules + hooks + MCP servers, the right format once you're building reusable internal tooling for a team instead of wiring up each piece by hand per project (`~/.gemini/antigravity-cli/plugins/<name>/`).
- **Custom statusline / window titles** — pipe live JSON agent metadata (cwd, active model, token usage, agent state) into your own shell script to drive a custom status bar. Worth knowing for narrow tmux panes specifically: the statusline layout was recently improved to merge the active tip and artifact status onto a single line and truncate with an ellipsis on narrow terminals, specifically to avoid collisions in exactly the kind of split-pane layout you're building in §16.
- **LaTeX rendering** in the terminal viewport for mathematical formulas.
- **Shared harness with Antigravity 2.0 desktop** — settings and permissions sync; conversations move via `/export`/`/resume` when you want richer diff/graph views than a TUI can offer.
- **Artifacts as first-class, versioned documents** (§13.1) — Task/Implementation Plan/Walkthrough files the agent writes and revises, each automatically versioned with rollback capability, independent of your project's own git history.
- **Multi-model support** — switch between Gemini and non-Gemini models mid-session via `/model`, useful for routing a task to whichever model your team has found best for that kind of work.

## 16. Integrating with Your tmux + Neovim Workflow

Your stated goal: tmux owns pane/window layout, Neovim is purely for reading/editing code, `agy` runs as a plain terminal program in its own pane — no editor-integration plugin required. This is a clean, low-coupling setup that won't break if either tool updates independently of the other.

### 16.1 Minimal tmux layout script

```bash
#!/usr/bin/env bash
# ~/.local/bin/dev-session
SESSION="agydev"
PROJECT="${1:-$PWD}"

tmux new-session -d -s "$SESSION" -c "$PROJECT"
tmux rename-window -t "$SESSION:0" "code"

# Left pane: Neovim (70% width)
tmux send-keys -t "$SESSION:0" "nvim ." C-m

# Right pane: Antigravity CLI (30% width)
tmux split-window -h -p 30 -t "$SESSION:0" -c "$PROJECT"
tmux send-keys -t "$SESSION:0.1" "agy" C-m

tmux select-pane -t "$SESSION:0.0"
tmux attach -t "$SESSION"
```

Usage: `dev-session ~/projects/your-project`.

### 16.2 Smooth pane navigation between Neovim and tmux

Not Antigravity-specific, but the piece that makes the two-pane layout pleasant: a navigator letting `Ctrl-h/j/k/l` move seamlessly between tmux panes and Neovim splits as one continuous grid. `christoomey/vim-tmux-navigator` (or the Lua-native `alexghergh/nvim-tmux-navigation`) is standard.

`~/.tmux.conf`:

```tmux
set -g focus-events on   # required — see §16.3

bind-key -n C-h if-shell "$is_vim" "send-keys C-h" "select-pane -L"
bind-key -n C-j if-shell "$is_vim" "send-keys C-j" "select-pane -D"
bind-key -n C-k if-shell "$is_vim" "send-keys C-k" "select-pane -U"
bind-key -n C-l if-shell "$is_vim" "send-keys C-l" "select-pane -R"
```

(`$is_vim` is defined by whichever navigator plugin you install — follow its setup instructions.)

### 16.3 The real problem: Neovim noticing Antigravity's file edits

`agy` edits files on disk from a separate process. Neovim caches buffers in memory and won't notice the change until forced to re-check. Fix with `autoread` plus autocommands that aggressively re-check on focus/activity — exactly what tmux pane-switching triggers once `focus-events` is on:

`init.lua`:

```lua
vim.o.autoread = true

vim.api.nvim_create_autocmd({ "FocusGained", "BufEnter", "CursorHold", "CursorHoldI" }, {
  pattern = "*",
  command = "if mode() != 'c' | checktime | endif",
})

vim.api.nvim_create_autocmd("FileChangedShellPost", {
  pattern = "*",
  command = "echohl WarningMsg | echo 'File changed on disk (reloaded by Antigravity CLI?)' | echohl None",
})
```

With `set -g focus-events on` in tmux and this Neovim config, switching from the `agy` pane back to Neovim automatically triggers `checktime` — any file Antigravity just wrote shows up immediately, no manual `:e!` needed.

### 16.4 Reviewing changes before accepting them

Two independent, complementary review surfaces:

- **Inside `agy`**: `/diff` (text diff in the TUI) or `Ctrl+R` (the fuller Artifact Review Panel).
- **Inside Neovim**: `lewis6991/gitsigns.nvim` or `tpope/vim-fugitive` give you the same information against the actual git index, in the same diff view you already use for manual changes.

Keep `Tool Permission` on `request-review` so the agent pauses before shell commands, but pre-approve safe, repeated commands (`git status`, `git diff`, your test runner, your linter) via the `allow` list (§5) so you're not confirming the same harmless command twenty times a day.

### 16.5 One-shot calls from a tmux/Neovim keybinding

`agy -p "..."` (headless mode) is the right tool for a single answer without dropping into the interactive TUI:

```tmux
# ~/.tmux.conf
bind-key g run-shell "tmux split-window -h \"agy -p 'Explain the architecture of #{pane_current_path}'\""
```

Since you don't want an Antigravity plugin inside Neovim itself, the cleanest way to trigger a one-shot call from an editor keybinding is a thin shell wrapper invoked via `:!` or a terminal job, rather than any vim-side Antigravity integration.

### 16.6 True parallel experiments across tmux windows

Since `/fork` only branches the chat, not the git tree (§13.3), the practical pattern for "two tmux windows, two real parallel attempts" is `git worktree`:

```bash
git worktree add ../your-project-experiment-b feature/experiment-b
```

Open a second tmux window pointed at that worktree, with its own Neovim + `agy` pane pair, fully isolated from the first.

## 17. Quick Setup Checklist

1. `wsl --list --verbose` → confirm WSL2.
2. `curl -fsSL https://antigravity.google/cli/install.sh | bash` inside Ubuntu/WSL.
3. Export `PATH`, confirm with `agy --version`.
4. `agy` → complete login (copy/paste URL+code flow if browser doesn't auto-launch from WSL).
5. `/config` → set theme, confirm `Tool Permission: request-review`, decide on `enableTerminalSandbox`.
6. Run `agy inspect` once just to see your actual on-disk paths for skills/plugins/hooks/MCP, and note any divergence from this guide's tables.
7. Create `AGENTS.md` at your project root with your core conventions (§7.2).
8. `mkdir -p .agents/skills .agents/rules .agents/workflows .agents/hooks`.
9. Write one trivial skill to confirm discovery: `/skills` should list it.
10. Set up `.agents/hooks.json` with at least the destructive-command guard from §11.4.
11. Tighten the `allow` list for your everyday safe commands (`git`, your test runner, your linter) so `request-review` doesn't nag you for the basics.
12. Set up the tmux layout script (§16.1) and the Neovim `autoread`/`checktime` autocommands (§16.3).

## 18. Final Caveats & How Reliable Each Section Is

This guide reflects the best publicly cross-checked information as of **mid-June 2026**, roughly a month after general availability. Antigravity CLI has shipped near-daily changes since launch (new flags, changed default paths, new permission fields, in-flight subagent-authoring formats). A rough honesty ranking of the sections above, so you know where to be most skeptical:

- **High confidence, directly sourced from `antigravity.google/docs/*` pages**: the permission schema and precedence rule (§5), the hooks schema and event list (§11), the minimal Skill format (§8.2), the global MCP config path and schema (§12), subagent monitoring via `/agents` (§10.1).
- **Medium confidence, consistent across multiple independent hands-on sources but not pinned to a single official page**: exact global Skills/Hooks paths (§4 table flags this explicitly), the `--print`/`-p` flag family (§14.1, reconstructed from a captured `--help` output), session storage format (§13.1).
- **Lower confidence, explicitly flagged inline**: the custom subagent `agent.json` authoring format (§10.2), the exact Workflow argument-substitution syntax (§9.2), whether Claude-Code-style Skill frontmatter extensions (`disable-model-invocation`, etc.) work in Antigravity at all (§8.2).

Whenever something here and what `agy --help` / `/help` / `/skills` / `/hooks` / `/mcp` / `agy inspect` tell you on your own machine disagree, **trust your own machine** — none of this replaces actually running those introspection commands against your specific installed version.

Finally: treat permission configuration as a first step, not a "later" task. An agentic CLI with broad shell/file access and a default of `request-review` is reasonably safe; `--dangerously-skip-permissions` or a careless `allow: ["command(*)"]` removes that safety net entirely.
