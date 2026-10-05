# Claude Code (TUI) — A Senior Dev's Field Guide for Ubuntu (WSL) + Neovim + Tmux

> Written from the perspective of a senior engineer who has run AI coding agents in production workflows for 5+ years. This reflects Claude Code's behavior as of mid-2026 (release line 2.1.x — auto memory alone requires v2.1.59+, several features below require v2.1.118–2.1.175+). Run `claude --version` to confirm your build, and treat any version-gated feature called out below as "verify with `claude doctor` if it doesn't behave as described."
>
> Core philosophy baked into this guide: **Claude Code is the agent, Neovim is your inspection/editing cockpit, tmux is the window manager that holds them side by side.** We are explicitly _not_ installing any Claude Code Neovim plugin — Claude Code runs as a standalone TUI process in its own pane, and Neovim just opens the same files on disk, the same way it would if a human teammate were editing them over SSH.

## Table of contents

1. [What Claude Code actually is](#1-what-claude-code-actually-is)
2. [Install on Ubuntu (WSL)](#2-install-on-ubuntu-wsl)
3. [Login and authentication](#3-login-and-authentication)
4. [The configuration system — scopes and files](#4-the-configuration-system--scopes-and-files)
5. [Permissions — the full picture](#5-permissions--the-full-picture)
6. [The `.claude/` directory — full structure, project and global](#6-the-claude-directory--full-structure-project-and-global)
7. [Skills — building blocks for reusable behavior](#7-skills--building-blocks-for-reusable-behavior)
8. [Subagents — isolated specialists](#8-subagents--isolated-specialists)
9. [Hooks — deterministic automation](#9-hooks--deterministic-automation)
10. [Rules and Memory — CLAUDE.md, `.claude/rules/`, and auto memory](#10-rules-and-memory--claudemd-clauderules-and-auto-memory)
11. [Session management — the complete picture](#11-session-management--the-complete-picture)
12. [MCP servers — connecting external tools](#12-mcp-servers--connecting-external-tools)
13. [Complete command reference](#13-complete-command-reference)
14. [The Tmux + Neovim + Claude Code workflow](#14-the-tmux--neovim--claude-code-workflow)
15. [Other strengths worth knowing about](#15-other-strengths-worth-knowing-about)
16. [Suggested starting configuration](#16-suggested-starting-configuration)
17. [Quick reference cheat sheet](#17-quick-reference-cheat-sheet)
18. [Where to go deeper](#18-where-to-go-deeper)

## 1. What Claude Code actually is

Claude Code is an agentic coding CLI: it reads your repository, edits files directly on disk, runs shell commands, and iterates inside a terminal UI (TUI) you control with permission rules. It runs an agentic loop — read, plan, call a tool, observe the result, repeat — until the task looks done or it genuinely needs your input. It is not autocomplete; it is closer to a second developer sitting at your keyboard, except everything it does is logged, interruptible, and (if you configure it right) gated by rules you wrote in advance.

Five extension mechanisms sit on top of that core loop, and most of this guide is about using them well:

| Mechanism             | What it is                                                       | Enforced or advisory?                                     |
| --------------------- | ---------------------------------------------------------------- | --------------------------------------------------------- |
| **CLAUDE.md / Rules** | Standing instructions loaded into context every session          | Advisory — Claude tries to follow them, no guarantee      |
| **Skills**            | On-demand instruction sets, invoked as `/name` or auto-triggered | Advisory, but only loads when relevant — cheap on context |
| **Subagents**         | Isolated context windows with their own tools/model/prompt       | Advisory for behavior, but tool access is enforced        |
| **Hooks**             | Shell commands fired at fixed lifecycle points                   | **Enforced** — runs regardless of what the model decides  |
| **Permissions**       | allow/deny/ask rules on every tool call                          | **Enforced** — the client checks before any tool runs     |

Because Claude Code writes to your filesystem like a second developer, your job in a tmux+Neovim setup is to: give it scoped, verifiable tasks; watch/approve risky actions; and use Neovim to diff, review, and hand-correct what it produces. The rest of this guide takes you from zero to a productive daily setup, then into building your own skills/hooks/agents for reuse — which is explicitly your stated end goal.

## 2. Install on Ubuntu (WSL)

### 2.1 Requirements

- WSL2 strongly recommended over WSL1 (WSL1 can hit `Exec format error` on the native binary, and WSL2 is required if you later want OS-level sandboxing — see §5.6).
- Ubuntu 20.04+ inside WSL.
- 4 GB+ RAM, internet connectivity.
- `ripgrep` ships bundled with Claude Code; you don't need to install it separately.

### 2.2 Install (native installer — recommended)

Run this **inside your WSL Ubuntu terminal** (not PowerShell/CMD):

```bash
curl -fsSL https://claude.ai/install.sh | bash
```

This is the recommended method over npm because it installs a self-contained native binary that **auto-updates in the background**. Never run it with `sudo` — `sudo npm install -g`-style installs are explicitly discouraged because they create permission/security problems later (root-owned files inside `~/.claude`, broken updates).

Alternative: install via npm (requires Node.js 18+):

```bash
npm install -g @anthropic-ai/claude-code
```

npm installs the same native binary under the hood via a platform-specific optional dependency, but it does **not** auto-update — you'll need `npm install -g @anthropic-ai/claude-code@latest` manually. Prefer the native installer unless you specifically manage all your tooling through npm in CI.

You can also pin a specific version or release channel:

```bash
curl -fsSL https://claude.ai/install.sh | bash -s stable   # ~1 week behind, skips major regressions
curl -fsSL https://claude.ai/install.sh | bash -s 2.1.89   # exact version
```

### 2.3 Verify

```bash
claude --version
claude doctor      # full diagnostic: install type, update status, config issues, skill-budget warnings
```

### 2.4 WSL-specific notes

- Run `claude` from inside the WSL shell, in your project directory, ideally under `~/` or `/home/<user>/projects/...`, **not** `/mnt/c/...`. File IO on the Windows-mounted drive through WSL is much slower and makes search/grep feel sluggish — this is a documented "slow search on WSL" troubleshooting item.
- If OAuth login fails inside WSL2 (browser doesn't open / can't reach the localhost callback), see §3.4.
- "Permission denied" on npm global installs and "native binary not found after npm install" are common WSL gotchas; switch to the native installer instead of fighting npm's optional-dependency resolution.
- Sandboxing (§5.6) requires WSL2 specifically, not WSL1.

### 2.5 Updating / uninstalling

```bash
claude update                 # force an update now
```

To fully remove Claude Code and **all** local config/sessions:

```bash
rm -f ~/.local/bin/claude
rm -rf ~/.local/share/claude
rm -rf ~/.claude
rm -f ~/.claude.json
# inside a specific project, also:
rm -rf .claude
rm -f .mcp.json
```

## 3. Login and authentication

### 3.1 First login

Just run:

```bash
claude
```

On first launch it opens a browser for OAuth login. If the browser doesn't auto-open in WSL, press `c` inside the prompt to copy the login URL, then paste it into your Windows browser manually.

You can authenticate with:

- **Claude Pro / Max subscription** — personal plan, recommended for an individual dev.
- **Claude for Teams / Enterprise** — org-managed.
- **Claude Console** (API-key billing) — `claude auth login --console`.
- **Cloud provider** (Bedrock / Vertex / Foundry) — set env vars, no browser needed; out of scope for a personal WSL box.

### 3.2 Useful auth commands

```bash
claude auth login            # interactive login
claude auth login --console  # login via Anthropic Console (API billing)
claude auth logout
claude auth status            # JSON; --text for human-readable; exit code 0 = logged in
claude setup-token             # generate a long-lived OAuth token for CI/scripts
```

Inside the TUI: `/login`, `/logout`, `/status` (also shows version, model, account, and which settings sources are loaded — see §4.4).

### 3.3 Authentication precedence

When multiple auth methods are present, the highest-precedence one wins (highest to lowest): cloud-provider env vars → `ANTHROPIC_AUTH_TOKEN` → `ANTHROPIC_API_KEY` → `apiKeyHelper` script → subscription OAuth (`/login`). If you have a Pro/Max subscription but accidentally have `ANTHROPIC_API_KEY` set in your shell profile, the API key silently wins and you get billed per-token instead of through your subscription — `unset ANTHROPIC_API_KEY` and check `/status` if auth looks wrong.

### 3.4 If OAuth fails in WSL2

This is a known class of issue ("OAuth login fails in WSL2, SSH, or containers" in the official troubleshooting docs). If the browser callback can't reach back into WSL:

1. Make sure you're on WSL2, not WSL1: `wsl -l -v` from PowerShell.
2. Retry `claude auth login` after confirming Windows' default browser opens `localhost` URLs correctly (WSL2 forwards localhost to Windows by default in recent builds).
3. Fallback: authenticate with a long-lived token instead. Run `claude setup-token` on a machine where the browser flow works, then export the result as `ANTHROPIC_AUTH_TOKEN` in your WSL shell profile.

### 3.5 Where credentials live

On Linux/WSL, credentials are stored at `~/.claude/.credentials.json` with file mode `0600` (this is not the macOS Keychain, which is macOS-only). Deleting this file plus `~/.claude.json` is the "log out and forget everything" nuclear option.

## 4. The configuration system — scopes and files

This is the section most people get wrong, so read carefully before you write your first skill/hook/rule.

### 4.1 The four (really five) configuration layers

Claude Code merges settings from several layers every time it starts. From **highest to lowest** precedence:

| #   | Layer                | Location                                                                                                                                                                                                                                                                                                       | Who controls it                          | Notes                                                            |
| --- | -------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------- | ---------------------------------------------------------------- |
| 1   | **Managed / policy** | Linux & WSL: `/etc/claude-code/managed-settings.json` (or a `managed-settings.d/` drop-in dir merged alphabetically); macOS: `/Library/Application Support/ClaudeCode/managed-settings.json`; Windows: `C:\Program Files\ClaudeCode\managed-settings.json` or `HKLM\SOFTWARE\Policies\ClaudeCode` registry key | IT/DevOps via MDM, Group Policy, Ansible | Cannot be overridden by anything below, including your own files |
| 2   | **CLI flags**        | Whatever you pass to `claude ...`                                                                                                                                                                                                                                                                              | You, per-invocation                      | Session-only, doesn't persist                                    |
| 3   | **Local**            | `.claude/settings.local.json` (project)                                                                                                                                                                                                                                                                        | You, this repo only                      | Auto-gitignored, never committed                                 |
| 4   | **Project**          | `.claude/settings.json` (project)                                                                                                                                                                                                                                                                              | You + team                               | Committed to git                                                 |
| 5   | **User**             | `~/.claude/settings.json`                                                                                                                                                                                                                                                                                      | You, every project                       | Applies when nothing else specifies the setting                  |

**How merging actually works**, since this trips nearly everyone up at least once: for a **scalar** value (a string, number, boolean — e.g. `defaultMode`, `model`), the highest-precedence layer that sets it wins outright. For an **array** value (most importantly `permissions.allow` / `permissions.deny` / `permissions.ask`, and `additionalDirectories`), the arrays from every layer **concatenate** rather than override — a `deny` rule written in your personal `~/.claude/settings.json` still blocks an action even if the project's `.claude/settings.json` allows it. There are two documented exceptions to "scalars override, arrays concatenate": `fallbackModel`, where the highest-precedence scope supplies the _whole_ fallback chain rather than merging chains, and `availableModels`, where a managed/policy value _replaces_ lower-precedence entries entirely (as of v2.1.175) rather than concatenating.

Verify what's actually active rather than guessing:

```text
/status    # "Setting sources" line lists each layer that loaded, e.g. "User settings", "Project local settings", "Enterprise managed settings (remote)"
/config    # tabbed settings UI — shows resolved values after precedence, lets you edit
```

`/status` tells you _which files_ loaded; it does not tell you which layer supplied a specific key when several layers set it. For that, just `grep` the key across the candidate files directly — that's the only fully reliable source of truth.

### 4.2 `settings.json` — anatomy of the main config file

Practical placement rule of thumb, regardless of the precedence table above: **team-wide settings go in `.claude/settings.json` and get committed; your personal cross-project defaults go in `~/.claude/settings.json`; one-off personal tweaks for _this_ repo go in `.claude/settings.local.json`.** Never put your personal shell allowlist or a private internal URL into the committed project file — anyone who clones the repo inherits it.

A representative `settings.json` (works at any of the three writable layers):

```json
{
  "$schema": "https://json.schemastore.org/claude-code-settings.json",
  "permissions": {
    "allow": [
      "Bash(git diff *)",
      "Bash(git log *)",
      "Bash(npm run *)",
      "Bash(npm test *)"
    ],
    "deny": [
      "Read(./.env)",
      "Read(./.env.*)",
      "Read(./secrets/**)",
      "Bash(curl *)"
    ],
    "defaultMode": "default",
    "additionalDirectories": []
  },
  "env": { "EDITOR": "nvim" },
  "model": "sonnet",
  "autoMemoryEnabled": true,
  "hooks": {},
  "skillListingBudgetFraction": 0.01
}
```

The `$schema` line is optional but worth keeping — most editors (including Neovim with a JSON LSP) will then offer autocomplete and inline docs for every key. Claude Code automatically creates timestamped backups of `settings.json` and keeps the five most recent, so a broken edit is always recoverable by hand.

Note: a handful of UI-only preferences (`editorMode`, `theme`, `autoConnectIde`) actually live in `~/.claude.json`, not `settings.json` — putting them in `settings.json` causes a schema error. The safest path is to change these through `/config` or `/theme` and let Claude Code write the correct file; `cat` the result afterward to learn where it landed.

Edits to `settings.json` (permissions, hooks, etc.) are watched and picked up live, without restarting the session.

### 4.3 `~/.claude.json` — the _other_ config file

This holds things `settings.json` does **not**: your OAuth session state, per-project trust decisions (the "do you trust this folder?" dialog), personal/local-scope MCP server definitions, and a few UI toggles (`editorMode`, `autoConnectIde`). You'll rarely hand-edit this directly; `/config`, `/login`, `/theme`, and the trust-prompt approvals manage it for you.

### 4.4 Settings worth knowing exist (a sampler, not exhaustive)

| Key                                                       | Effect                                                                                                    |
| --------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| `permissions.deny`                                        | Blocklist for tools/paths — replaces the deprecated `ignorePatterns`                                      |
| `permissions.defaultMode`                                 | Session-start permission mode — see §5.4                                                                  |
| `permissions.additionalDirectories`                       | Grants extra directories file access (not config discovery — see §7.5)                                    |
| `sandbox.enabled` / `sandbox.network.allowedDomains`      | OS-level Bash isolation, independent of permission rules — see §5.6                                       |
| `disableBypassPermissionsMode`                            | Set `"disable"` (typically in managed settings) to forbid `bypassPermissions` org-wide, even via CLI flag |
| `hooks`                                                   | Lifecycle automation — see §9                                                                             |
| `autoMemoryEnabled` / `autoMemoryDirectory`               | Toggle/relocate auto memory — see §10.5                                                                   |
| `claudeMdExcludes`                                        | Skip specific CLAUDE.md files in a large monorepo — see §10.4                                             |
| `skillListingBudgetFraction` / `maxSkillDescriptionChars` | Context budget for skill descriptions — see §7.10                                                         |
| `skillOverrides`                                          | Per-skill visibility override without touching the skill's own frontmatter — see §7.9                     |
| `disableSkillShellExecution`                              | Disables `` !`command` `` dynamic context injection in skills, policy use — see §7.8                      |
| `forceLoginMethod` / `forceLoginOrgUUID`                  | Lock authentication method/org — managed settings use                                                     |
| `attribution.coAuthoredBy` etc.                           | Control git commit attribution trailers Claude adds                                                       |
| `disableBundledSkills`                                    | Turn off the bundled `/code-review`, `/debug`, `/loop`, `/batch`, `/claude-api` skills                    |

A `managed-settings.json` aimed at locking down a fleet of dev machines looks like this (informative even on a solo box, since it shows the ceiling of what's configurable):

```json
{
  "permissions": {
    "deny": [
      "Bash(curl *)",
      "Bash(wget *)",
      "Read(./.env)",
      "Read(./secrets/**)"
    ],
    "disableBypassPermissionsMode": "disable"
  },
  "sandbox": {
    "enabled": true,
    "allowUnsandboxedCommands": false,
    "network": {
      "allowedDomains": ["github.com", "*.npmjs.org", "registry.yarnpkg.com"]
    }
  },
  "allowManagedHooksOnly": true,
  "allowManagedPermissionRulesOnly": true,
  "companyAnnouncements": ["All code requires review before merge."]
}
```

## 5. Permissions — the full picture

This is the system that decides, for every single tool call Claude wants to make, whether it runs immediately, runs after asking you, or is blocked outright. Get comfortable here before writing skills/hooks/agents, since permission rules are the floor everything else operates above.

### 5.1 Where permission rules live

| Source                                                                            | Editable how                                            |
| --------------------------------------------------------------------------------- | ------------------------------------------------------- |
| `permissions.allow` / `.deny` / `.ask` arrays in any `settings.json` layer (§4.1) | Hand-edit, or via `/permissions`                        |
| `--allowedTools` / `--disallowedTools` CLI flags                                  | Session-scoped, doesn't persist                         |
| `allowed-tools` / `disallowed-tools` in a skill's or agent's frontmatter          | Scoped to while that skill/agent is active — see §7, §8 |
| `PreToolUse` hooks                                                                | Can override the static rules programmatically — see §9 |

### 5.2 Rule syntax

A rule is `ToolName` (matches the whole tool) or `ToolName(specifier)` (matches a pattern inside that tool's input):

```json
{
  "permissions": {
    "allow": [
      "Bash(npm run *)",
      "Bash(git commit *)",
      "Read",
      "Edit(/src/**/*.ts)",
      "WebFetch(domain:docs.anthropic.com)",
      "mcp__github__create_pull_request"
    ],
    "ask": ["Bash(git push *)"],
    "deny": ["Bash(rm -rf *)", "Read(./.env)", "WebFetch", "Agent(Explore)"]
  }
}
```

Key syntax details worth internalizing:

- **Bare tool name** (`Bash`, `Read`, `WebFetch`) matches every invocation of that tool. As a `deny`, it removes the tool from Claude's awareness entirely; as an `allow`, it pre-approves all uses.
- **Word-boundary wildcards**: the space before `*` matters. `Bash(npm run *)` matches anything starting with `npm run ` (note the trailing space) — so `Bash(ls *)` matches `ls -la` but **not** `lsof`, while `Bash(ls*)` (no space) matches both.
- **Shell-operator awareness**: Claude Code parses the command, so `Bash(safe-cmd *)` does **not** match `safe-cmd && malicious-cmd` — a chained/injected command after `&&`, `;`, or `|` is evaluated as its own separate command against the rules, it isn't waved through just because the first segment matched.
- **MCP tools** use the double-underscore form `mcp__<server>__<tool>` with no parentheses — see §12.6.
- **Subagents**: `Agent(AgentName)` controls whether Claude can spawn that subagent type (built-in or custom) — useful for disabling `Explore` org-wide, or restricting a coordinator agent to only spawn specific teammates.
- **Skills**: `Skill(name)` for exact match, `Skill(name *)` for prefix match with arguments — see §7.11.

### 5.3 Evaluation order

**Deny is checked first, then ask, then allow — the first matching rule wins, regardless of how specific or broad it is.** A broad `deny` always beats a narrower `allow`, even one written at a higher-precedence settings layer. The one documented exception: `bypassPermissions` mode skips the entire permission layer, including deny rules — which is exactly why that mode should only ever be used in a disposable container, never on a machine with real secrets on disk.

### 5.4 Permission modes — the session-wide stance

Switch any time with `Shift+Tab`, or fix one at startup with `--permission-mode <mode>` / `permissions.defaultMode` in settings:

| Mode                | Behavior                                                                                                                                                                                                                                                          |
| ------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `default`           | Prompts on first use of each distinct tool/pattern (the standard, safest starting point)                                                                                                                                                                          |
| `acceptEdits`       | Auto-accepts file edits + safe filesystem commands (`mkdir`, `mv`, etc.) inside your working dir; shell commands with side effects still prompt                                                                                                                   |
| `plan`              | Read-only exploration, no edits at all — pairs with `/plan`, see §15.1                                                                                                                                                                                            |
| `auto`              | A background classifier auto-approves routine, low-risk actions and still prompts for anything that looks risky (research preview; can be disabled fleet-wide by setting `auto: "disable"` in managed settings, which also removes it from the `Shift+Tab` cycle) |
| `dontAsk`           | Auto-**denies** anything not explicitly in `allow` — good for hands-off CI-style runs where you've pre-built a tight allowlist                                                                                                                                    |
| `bypassPermissions` | Skips essentially all prompts, including deny rules — **container/VM only**, never your real WSL filesystem                                                                                                                                                       |

Practical recommendation for a personal WSL box: live in `default` or `acceptEdits` day-to-day; build your `allow` list organically as you keep approving the same handful of build/test/lint commands; reserve `dontAsk`/`bypassPermissions` for sandboxed or fully disposable environments (a scratch worktree you don't mind nuking, or a CI runner).

### 5.5 `/permissions` — the interactive editor

Inside a session, `/permissions` (alias `/allowed-tools`) opens a UI listing every active rule along with which settings file it came from — the fastest way to answer "why did Claude just ask me about this" or "why is this still being denied even though I added an allow rule." Add/remove rules here instead of hand-editing JSON when you're not sure of the exact syntax; it writes the rule to the correct file for you.

### 5.6 Sandbox — a second, independent safety layer

Beyond permission rules, Claude Code supports OS-level sandboxing of Bash specifically (`sandbox.enabled: true`), restricting filesystem and network access for shell commands regardless of what permission rules say. Supported on WSL2 (not WSL1). This is defense-in-depth, not a replacement for §5.2–5.4 — most solo devs don't need it turned on day one, but it's the right tool once you start granting `bypassPermissions` for any kind of autonomous/background run, since it puts a hard ceiling under the permission system rather than around it.

### 5.7 Debugging permission surprises

1. `/permissions` to see the resolved rule set and its source file.
2. Remember deny-beats-allow-beats-ask, and that arrays merge across _all_ layers — a stray `deny` in `~/.claude/settings.json` from months ago can silently block something a project's `allow` list clearly permits.
3. Check whether a `PreToolUse` hook is intercepting the call before the static rules even apply (§9.1).
4. `claude doctor` flags malformed permission syntax.

## 6. The `.claude/` directory — full structure, project and global

Two roots: your **project root** and your **home directory** (`~`). Below is the complete map; §7–§10 explain how to populate each piece.

### 6.1 Project root (committed to git, except where noted)

```
your-project/
├── CLAUDE.md                  # project instructions (or ./.claude/CLAUDE.md — both work), committed
├── CLAUDE.local.md            # YOUR personal project notes — gitignore this yourself
├── .mcp.json                  # team-shared MCP server definitions, committed
├── .worktreeinclude           # gitignored files to copy into new git worktrees, committed
└── .claude/
    ├── settings.json          # permissions, hooks, model, env — shared with team, committed
    ├── settings.local.json    # your personal overrides, this repo only — auto-gitignored
    ├── rules/                 # topic-scoped instructions, optionally path-gated, committed
    │   ├── code-style.md
    │   ├── testing.md
    │   └── api-design.md
    ├── skills/                # reusable behaviors, one directory per skill, committed
    │   └── deploy/
    │       └── SKILL.md
    ├── commands/               # legacy single-file commands; skills/ is the modern equivalent
    │   └── fix-issue.md
    ├── agents/                 # subagent definitions, committed
    │   └── code-reviewer.md
    ├── agent-memory/           # auto-generated: persistent subagent memory (project-scoped)
    │   └── code-reviewer/MEMORY.md
    └── worktrees/              # created by `claude --worktree`, gitignore this
```

### 6.2 Home directory (`~`) — applies to _every_ project, never committed

```
~/.claude.json                 # OAuth session, per-project trust state, personal MCP servers
~/.claude/
├── CLAUDE.md                  # YOUR personal instructions, loaded in every project
├── settings.json              # YOUR default settings for every project
├── keybindings.json           # custom keyboard shortcuts
├── rules/                     # personal rules applied everywhere
│   ├── preferences.md
│   └── workflows.md
├── skills/                    # personal skills available in every project
│   └── summarize-changes/SKILL.md
├── commands/                  # personal legacy commands
├── agents/                    # personal subagents available everywhere
├── agent-memory/              # persistent memory for subagents with memory: user
├── teams/                     # agent-team state (experimental) — see §11.6
├── tasks/                     # agent-team shared task lists (experimental)
├── output-styles/             # personal output styles
└── projects/<project-hash>/
    ├── <session-id>.jsonl     # SESSION TRANSCRIPTS — see §11.1
    └── memory/                # AUTO MEMORY — Claude's own notes, written automatically
        ├── MEMORY.md
        └── debugging.md
```

### 6.3 What each file means, in plain language

| File / folder                                   | Who writes it           | Purpose                                                                                                                                                                                                                                          |
| ----------------------------------------------- | ----------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `CLAUDE.md` (project or `~/.claude/CLAUDE.md`)  | You                     | Persistent instructions injected every session: build commands, conventions, architecture. All discovered CLAUDE.md files concatenate; broader scope loads first, your working directory's file loads last (read last = weighted most recently). |
| `CLAUDE.local.md`                               | You                     | Same purpose, personal-only, never committed.                                                                                                                                                                                                    |
| `.claude/rules/*.md`                            | You                     | Same idea as CLAUDE.md but split by topic, optionally gated by `paths:` frontmatter so a rule only enters context when Claude touches matching files.                                                                                            |
| `.claude/settings.json` / `settings.local.json` | You / team              | **Enforced configuration**: permissions, hooks, model default, env vars — not advisory like CLAUDE.md.                                                                                                                                           |
| `.mcp.json`                                     | You / team              | Project-scoped MCP server definitions (e.g. a shared GitHub or Postgres MCP server everyone on the team should have).                                                                                                                            |
| `.claude/skills/<name>/SKILL.md`                | You                     | A reusable, on-demand instruction set invoked as `/name` or auto-triggered by Claude.                                                                                                                                                            |
| `.claude/agents/*.md`                           | You                     | Subagent definitions — isolated context windows with their own tools/model/system prompt.                                                                                                                                                        |
| `.claude/agent-memory/<agent>/MEMORY.md`        | Claude (auto)           | A subagent's own persistent notes, created once you set `memory: project` on that agent.                                                                                                                                                         |
| `~/.claude/projects/<project>/memory/`          | Claude (auto)           | Auto memory for your _main_ session on this project — different from agent memory above.                                                                                                                                                         |
| `~/.claude.json`                                | Claude Code (app state) | OAuth tokens, per-project trust decisions, personal-scope MCP servers, a few UI toggles.                                                                                                                                                         |

### 6.4 Inspect what actually loaded — don't guess

```text
/context      # token usage by category: system prompt, memory, skills, MCP, messages
/memory       # which CLAUDE.md / rules files loaded this session; browse auto-memory
/agents       # configured subagents
/hooks        # active hook configuration
/mcp          # connected MCP servers
/skills       # available skills (project, user, plugin)
/permissions  # current allow/deny/ask rules and their source file
/doctor       # full diagnostics, including skill-description-budget warnings
```

### 6.5 Going global: making a skill/rule/hook apply to _every_ project

No special flag needed — anything placed under `~/.claude/` instead of `<project>/.claude/` is automatically global to you, on this machine, across every project:

```bash
mkdir -p ~/.claude/rules   && nvim ~/.claude/rules/personal-style.md       # global rule
mkdir -p ~/.claude/skills/my-skill && nvim ~/.claude/skills/my-skill/SKILL.md  # global skill
mkdir -p ~/.claude/agents && nvim ~/.claude/agents/code-reviewer.md        # global subagent
nvim ~/.claude/settings.json                                               # global hooks / permissions
nvim ~/.claude/CLAUDE.md                                                   # global instructions
```

If you ever need org-wide enforcement (not your solo-WSL use case, but worth knowing it exists), the managed-policy location on Linux/WSL is `/etc/claude-code/` (`managed-settings.json`, `CLAUDE.md`) — that tier overrides everything, including your own `~/.claude/` files, and individual users cannot exclude it.

## 7. Skills — building blocks for reusable behavior

Skills are the single most important extension mechanism, and the one you'll spend the most time building once you're past day one. **Custom commands (`.claude/commands/*.md`) have been merged into skills** — a file at `commands/deploy.md` and a skill at `skills/deploy/SKILL.md` both create `/deploy` and behave the same way, but skills support a directory of supporting files, finer invocation control, and dynamic context injection. Use skills for everything new; leave existing `commands/` files as-is if they already work, since they keep functioning unchanged.

### 7.1 Mental model: when to reach for a skill vs. something else

| If the thing is...                                                            | Use                                                 |
| ----------------------------------------------------------------------------- | --------------------------------------------------- |
| A fact/rule that should always apply (coding standards, build commands)       | CLAUDE.md or a rule                                 |
| A multi-step workflow you trigger when needed (deploy, review, open a PR)     | **Skill**                                           |
| Something that must happen deterministically, no exceptions                   | Hook                                                |
| Background knowledge Claude needs sometimes, but isn't an action              | Skill with `user-invocable: false`                  |
| Verbose/expensive work whose output you don't want polluting your main thread | Skill with `context: fork` (or a subagent directly) |

### 7.2 Anatomy of a skill

```
deploy/
├── SKILL.md          # required — frontmatter + instructions, the entry point
├── reference.md       # optional — detailed docs Claude reads on demand
├── examples.md        # optional — example output showing expected format
└── scripts/
    └── helper.sh       # optional — executable Claude can run, not loaded as text
```

The directory name becomes the `/command-name` you type. Only `SKILL.md` is required; everything else loads only when Claude actually needs it, so bulky reference material costs near-zero context until used. Keep `SKILL.md` itself under 500 lines — once a skill is invoked its full content stays in your conversation for the rest of the session (see §7.7), so every line is a recurring token cost; move detail into the supporting files instead.

### 7.3 Full frontmatter reference

```yam
name: my-skill                 # display name; falls back to directory name if omitted
description: What this does and when to use it   # MOST IMPORTANT FIELD — see §7.4
when_to_use: Extra trigger phrases or example requests   # appended to description
argument-hint: <issue-number>  # autocomplete hint
arguments: [issue, branch]     # named positional args -> $issue, $branch
disable-model-invocation: true # only YOU can invoke via /name; Claude never auto-triggers
user-invocable: false           # only CLAUDE can invoke; hidden from the / menu
allowed-tools: Bash(git add *) Bash(git commit *)   # pre-approved while this skill is active
disallowed-tools: AskUserQuestion   # removed from the tool pool while this skill is active
model: sonnet                   # override model just for this skill's turn
effort: high                    # low | medium | high | xhigh | max
context: fork                   # run in an isolated subagent instead of inline
agent: Explore                  # which subagent type to use, when context: fork is set
paths: ["src/api/**/*.ts"]      # only auto-load when Claude touches matching files
shell: bash                     # bash (default) or powershell, for inline `!command` blocks
hooks: {}                       # hooks scoped to this skill's own lifecycl
```

All fields are optional; only `description` is realistically required, since it's what Claude reads to decide whether to auto-trigger the skill. The combined `description` + `when_to_use` text is truncated at 1,536 characters (configurable via `maxSkillDescriptionChars`) in the skill listing Claude sees, so **put the key trigger phrase first**.

### 7.4 Writing a good `description` — the field that actually matters

Claude scans every available skill's `description` against your prompt before deciding what to auto-load. A weak description ("Deploy tool") either never triggers or triggers on everything; a good one names the concrete situation: _"Deploy the application to production. Use when the user says deploy, ship, or release, or after a successful merge to main."_ If a skill keeps firing in the wrong context, the fix is almost always to tighten this field before reaching for `disable-model-invocation`.

### 7.5 What a `SKILL.md` body should actually contain

There's no rigid required structure, but a well-built task-skill typically has these sections, in this order:

1. **A one-line restatement of the goal** (often just the `description` rephrased as an imperative).
2. **Numbered steps.** Claude follows numbered sequences more reliably than prose paragraphs — "1. Run the test suite. 2. Build. 3. Push." beats "first run tests, then build, then push."
3. **Failure handling.** Explicitly state what to do if a step fails: _"If tests fail, stop and report the failing test names — do not proceed to build."_ Without this, an agent under autonomy will sometimes barrel forward.
4. **Pointers to supporting files**, written as a normal markdown link so Claude knows what's in them and when to read them: `For complete API details, see [reference.md](reference.md)`.
5. **(Optional) Dynamic context block** — live shell output injected via `` !`command` ``, placed near the top so the rest of the instructions can refer to it.

A reference-style skill (no actions, just domain knowledge) is simpler: just the facts, organized under a couple of headers, no numbered steps needed.

### 7.6 Worked example — a side-effecting skill only _you_ can trigger

```bash
mkdir -p .claude/skills/deploy
```

`.claude/skills/deploy/SKILL.md`:

```yam
name: deploy
description: Deploy the application to production. Use when the user says deploy, ship, or release.
disable-model-invocation: true
allowed-tools: Bash(npm run build) Bash(npm run deploy) Bash(git tag *)
argument-hint: <environment

Deploy $ARGUMENTS to production.

## Environment

!`git branch --show-current`
!`git status --short`

## Steps

1. Confirm the working tree is clean (no output from `git status --short` above). If it isn't, stop and report what's uncommitted.
2. Run the full test suite. If anything fails, stop and report the failing tests — do not proceed.
3. Run `npm run build`.
4. Run `npm run deploy -- --env $ARGUMENTS`.
5. Tag the release: `git tag deploy-$(date +%Y%m%d-%H%M)`.
6. Report the deployment URL and confirm the health check passes.

## On failure

If any step fails, stop immediately, report exactly which step and why, and do not attempt automatic rollback — ask the user how to proceed.
```

Run it with `/deploy staging`. `$ARGUMENTS` expands to `staging`; use `$0`/`$1` (shorthand for `$ARGUMENTS[0]`/`$ARGUMENTS[1]`) for positional access, or declare named arguments via the `arguments:` frontmatter list and reference them as `$name`.

### 7.7 Worked example — a reference skill Claude triggers automatically

`~/.claude/skills/summarize-changes/SKILL.md` (personal, global):

```yam
description: Summarizes uncommitted changes and flags anything risky. Use when the user asks what changed, wants a commit message, or asks to review their diff

## Current changes

!`git diff HEAD`

## Instructions

Summarize the changes above in two or three bullet points, then list any risks you notice such as missing error handling, hardcoded values, or tests that need updating. If the diff is empty, say there are no uncommitted changes.
```

The `` !`command` `` syntax is **dynamic context injection**: the shell command runs _before_ Claude ever sees the prompt, and its output is substituted in place — this is preprocessing, not something Claude executes, so it's safe to use even for an auto-invoked skill. The inline form only triggers when `!` starts a line or follows whitespace; for multi-line commands use a fenced ` ```! ` block instead. Substitution runs once over the original file — a command's output is not re-scanned for further placeholders. Set `disableSkillShellExecution: true` (typically in managed settings) to disable this feature policy-wide.

### 7.8 Skill content lifecycle (why concise bodies matter)

When invoked, the rendered `SKILL.md` enters the conversation as one message and stays there for the rest of the session — Claude Code does not re-read the file on later turns. Write standing instructions, not "do this once now" framing, if the guidance should keep applying. After `/compact`, the most recently invoked instance of each skill is re-attached (first 5,000 tokens of each, shared 25,000-token budget across all re-attached skills, most-recent-first) — older invocations in a session with many skills can be dropped entirely. If a skill seems to "stop working" mid-session, it's usually still present but being out-competed by other instructions; tighten the description/instructions, or re-invoke it after a compaction.

### 7.9 Where skills live, and precedence when names collide

| Location   | Path                               | Applies to                     |
| ---------- | ---------------------------------- | ------------------------------ |
| Enterprise | managed settings                   | Everyone in your org           |
| Personal   | `~/.claude/skills/<name>/SKILL.md` | All your projects              |
| Project    | `.claude/skills/<name>/SKILL.md`   | This project only              |
| Plugin     | `<plugin>/skills/<name>/SKILL.md`  | Wherever the plugin is enabled |

Same-name collision order: **enterprise > personal > project**; any of these also overrides a bundled skill of the same name. Plugin skills are namespaced (`plugin-name:skill-name`) so they never collide. Skills also load from nested `.claude/skills/` directories inside subdirectories below your working directory — useful in a monorepo, where a nested skill that collides by name with a root one becomes addressable as `/apps/web:deploy` while `/deploy` still resolves to the root one. Editing/adding/removing a `SKILL.md` under an already-watched skills directory takes effect mid-session without restart; creating a _brand-new_ top-level skills directory requires a restart.

Use `skillOverrides` in `settings.json` to flip a skill's visibility without touching its own frontmatter (handy for skills you don't own, e.g. ones from a shared repo or an MCP server):

```json
{ "skillOverrides": { "legacy-context": "name-only", "deploy": "off" } }
```

Valid values: `"on"` (default), `"name-only"` (hides the description but keeps it in the `/` menu), `"user-invocable-only"` (hidden from Claude's auto-discovery, still runnable via `/`), `"off"` (fully hidden).

### 7.10 Context budget for skill descriptions

Every available skill's name+description is loaded so Claude knows it exists. With many skills, descriptions get truncated to fit a budget that scales at 1% of the model's context window (`skillListingBudgetFraction`); when it overflows, your least-used skills' descriptions get shortened or dropped first. Run `/doctor` to see if this is happening to you. Raise the budget with `skillListingBudgetFraction` (e.g. `0.02`), or free space by setting low-priority skills to `"name-only"` in `skillOverrides`.

### 7.11 Restricting which skills Claude can invoke

```text
# In /permissions, deny rules:
Skill                  # disables the Skill tool entirely
Skill(deploy *)        # deny one specific skill, any arguments
```

```text
# Allow only specific skills:
Skill(commit)
Skill(review-pr *)
```

Or just put `disable-model-invocation: true` on the skill itself, which additionally removes its description from context entirely (the permission-rule approach still lists the description, it just blocks the tool call).

### 7.12 Running a skill inside a subagent (`context: fork`)

```yam
name: deep-research
description: Research a topic thoroughly
context: fork
agent: Explor

Research $ARGUMENTS thoroughly:
1. Find relevant files using Glob and Grep
2. Read and analyze the code
3. Summarize findings with specific file references
```

`context: fork` only makes sense for skills with an actual task — a reference-only skill with no instructions, forked, gives the subagent guidelines but no actionable prompt and returns nothing useful. The built-in `Explore`/`Plan` agents skip loading CLAUDE.md to stay lean, so a forked skill using them sees only the SKILL.md content plus that agent's own system prompt — good for "go research X and report back" patterns that would otherwise flood your main conversation with file-reading noise.

## 8. Subagents — isolated specialists

Use a subagent whenever a side task would flood your main conversation with stuff you'll never reference again (test output, log dumps, codebase exploration). The subagent works in its own context window and reports back only a summary.

### 8.1 Built-in subagents (zero setup)

| Agent             | Model                  | Tools     | Purpose                                                                                      |
| ----------------- | ---------------------- | --------- | -------------------------------------------------------------------------------------------- |
| `Explore`         | Fast (Haiku-class)     | Read-only | Quick codebase search/discovery; skips loading CLAUDE.md and git status to stay small        |
| `Plan`            | Inherits session model | Read-only | Research during plan mode; same lean startup as Explore                                      |
| `general-purpose` | Inherits               | All tools | Complex multi-step research + edits; the default if a skill's `context: fork` omits `agent:` |

### 8.2 Full frontmatter reference for a custom subagent

```yam
name: code-reviewer            # required — this is what makes @code-reviewer / Agent(code-reviewer) work
description: Expert code review specialist. Proactively reviews code for quality, security, and maintainability. Use immediately after writing or modifying code.
tools: Read, Grep, Glob, Bash   # tool allowlist for this agent; omit to inherit the session's full set
model: sonnet                   # haiku | sonnet | opus | inherit
effort: high                    # optional reasoning-effort override
memory: project                 # project | user | local | (omit = no persistent memory)
skills: [api-conventions]       # preload these skills' full content at startup (see §8.6)
color: blue                     # cosmetic, shown in the TUI / agent picke

You are a senior code reviewer ensuring high standards of code quality and security.

When invoked:
1. Run `git diff` to see recent changes.
2. Focus on modified files only.
3. Begin review immediately.

Review checklist:
- Correctness, edge cases, error handling
- No exposed secrets or hardcoded credentials
- Input validation, injection risks
- Test coverage

Organize feedback by priority: Critical / Warnings / Suggestions, with a concrete fix
suggestion for each. Before reviewing, check your agent memory for patterns you've
previously flagged in this codebase. After reviewing, write anything worth remembering
back to memory.
```

A well-built agent `.md` body generally has these sections: a one-paragraph **role statement** ("You are a..."), a **"when invoked" procedure** (what to do first, second, third), a **checklist or rubric** specific to the agent's job, and an **output format instruction** (how you want findings organized, since a subagent's summary is the only thing that reaches your main conversation).

### 8.3 Where subagents live, and how to create them

| Location                | Scope                          |
| ----------------------- | ------------------------------ |
| `~/.claude/agents/*.md` | All your projects              |
| `.claude/agents/*.md`   | This project, shared with team |

Quickest path: run `/agents` inside a session → Library tab → Create new agent → choose Personal or Project → describe what you want in plain English and let Claude generate the system prompt → pick tools/model/memory/color → save. Hand-writing the markdown file directly (as in §8.2) works identically and is often faster once you know the shape.

### 8.4 Invoking a subagent

```text
Use the code-reviewer subagent to look at my recent changes
```

Guarantee it runs (instead of letting Claude decide) with an @-mention:

```text
@code-reviewer review the auth changes
```

Or make the **entire session** run as that subagent from the start:

```bash
claude --agent code-reviewer
```

### 8.5 Persistent subagent memory

Setting `memory: project` gives a subagent its own `.claude/agent-memory/<agent-name>/MEMORY.md` that it reads and writes across sessions — it accumulates institutional knowledge (recurring issues it's flagged before, codebase-specific conventions it's learned). Use `memory: user` if the knowledge should follow you across _all_ projects instead of just this repo, or `memory: local` to keep it out of version control for this project. This is the same `MEMORY.md`-as-index pattern as auto memory (§10.5), just scoped per-agent instead of per-main-session.

### 8.6 Preloading skills into a subagent

The `skills:` frontmatter field injects the **full content** of named skills directly into the subagent's startup context (different from a normal session, where only skill _descriptions_ are preloaded and full content loads on invocation):

```yam
name: api-builder
skills: [api-conventions, error-handling-patterns
```

This is the inverse pairing of `context: fork` from §7.12: there, a skill drives a subagent as its task; here, a subagent treats skills as reference material baked into its system context from turn one.

### 8.7 Restricting which subagents can spawn what

In a coordinator agent's frontmatter:

```yaml
tools: Agent(worker, researcher), Read, Bash
```

This allowlists only `worker` and `researcher` as spawnable from that agent. To globally block a specific built-in agent (e.g. disable `Explore` everywhere), add to any `settings.json`:

```json
{ "permissions": { "deny": ["Agent(Explore)"] } }
```

## 9. Hooks — deterministic automation

Hooks are the layer that runs **regardless of what the model decides** — plain shell commands fired at fixed lifecycle points. Use them for anything that must happen with zero exceptions: auto-formatting, blocking edits to `.env`, desktop notifications, audit logging.

### 9.1 Lifecycle events

| Event                            | Fires when                                                                      | Typical use                                                                               |
| -------------------------------- | ------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| `SessionStart`                   | Session begins/resumes                                                          | Re-inject context after compaction, load env vars, print current branch                   |
| `UserPromptSubmit`               | You submit a prompt                                                             | Inject extra context before Claude sees it                                                |
| `PreToolUse`                     | Before any tool call, before the static permission rules even finish evaluating | Block dangerous commands, validate input, override a permission decision programmatically |
| `PostToolUse`                    | After a tool call succeeds                                                      | Auto-format edited files, run a linter, log what changed                                  |
| `Notification`                   | Claude needs input, or finishes a turn                                          | Desktop notification — **critical for tmux workflows**, see §14.4                         |
| `Stop`                           | Claude finishes responding                                                      | A verification gate (e.g. block "done" until tests actually pass)                         |
| `SubagentStart` / `SubagentStop` | A subagent begins/ends                                                          | Setup/teardown specific to one agent type                                                 |
| `SessionEnd`                     | Session terminates                                                              | Cleanup temp files                                                                        |
| `InstructionsLoaded`             | Diagnostic — fires when any CLAUDE.md/rules file loads                          | Debugging path-scoped rules or lazy-loaded subdirectory files (§10.9)                     |

### 9.2 Where to add hooks (scope decides who's affected)

| Location                               | Scope                                              |
| -------------------------------------- | -------------------------------------------------- |
| `~/.claude/settings.json`              | All your projects, just you                        |
| `.claude/settings.json`                | This project, shared with team                     |
| `.claude/settings.local.json`          | This project, just you                             |
| Skill/agent frontmatter `hooks:` field | Scoped to that skill or agent's own lifecycle only |

### 9.3 Anatomy of a hook entry

```json
{
  "hooks": {
    "<EventName>": [
      {
        "matcher": "Edit|Write",
        "hooks": [{ "type": "command", "command": "your-shell-command-here" }]
      }
    ]
  }
}
```

`matcher` is a regex against the tool name (omit or use `""` to match every tool/every notification). The `type` field on each handler can be one of several kinds beyond a plain shell command:

| Handler `type` | Behavior                                                                                            |
| -------------- | --------------------------------------------------------------------------------------------------- |
| `command`      | Runs a shell command; receives JSON on stdin (`tool_name`, `tool_input`, `cwd`, `session_id`, etc.) |
| `http`         | POSTs the event payload to a URL                                                                    |
| `mcp_tool`     | Invokes a tool on a connected MCP server                                                            |
| `prompt`       | Sends a prompt to a model and uses its decision as the hook's verdict                               |
| `agent`        | Runs a subagent to validate/decide on the action                                                    |

Most hooks you'll write day-to-day are `command` hooks. **Exit-code semantics for `command` hooks:** `0` = no objection, proceed normally; `2` = **block** the action, with stderr fed back to Claude as feedback so it can adjust its approach; anything else = proceed anyway, but the error is logged.

### 9.4 Worked example — auto-format every file Claude edits

`.claude/settings.json`:

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "jq -r '.tool_input.file_path' | xargs npx prettier --write"
          }
        ]
      }
    ]
  }
}
```

### 9.5 Worked example — hard-block edits to protected files

`.claude/hooks/protect-files.sh`:

```bash
#!/bin/bash
INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | jq -r '.tool_input.file_path // empty')
for pattern in ".env" "package-lock.json" ".git/"; do
  if [[ "$FILE_PATH" == *"$pattern"* ]]; then
    echo "Blocked: $FILE_PATH matches protected pattern '$pattern'" >&2
    exit 2   # block, message goes back to Claude as feedback
  fi
done
exit 0
```

```bash
chmod +x .claude/hooks/protect-files.sh
```

`.claude/settings.json`:

```json
{
  "hooks": {
    "PreToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          {
            "type": "command",
            "command": "\"$CLAUDE_PROJECT_DIR\"/.claude/hooks/protect-files.sh"
          }
        ]
      }
    ]
  }
}
```

Note this is a stronger guarantee than a permission `deny` rule covering the same path: a `PreToolUse` hook runs custom logic (here, a substring check across multiple patterns at once) rather than a static glob, and it's the documented mechanism for anything you want enforced "regardless of what Claude decides," per Anthropic's own framing of when to prefer a hook over a CLAUDE.md instruction.

### 9.6 Generating hooks conversationally

You can ask Claude to write hooks for you instead of hand-rolling the JSON: _"Write a hook that runs eslint after every file edit"_ or _"write a hook that blocks writes to the migrations folder."_ It tends to produce a working `settings.json` snippet directly — verify the result with `/hooks` afterward, and test the block path deliberately (try to trigger it) before trusting it.

### 9.7 `/hooks` — inspect what's active

```text
/hooks
```

Lists every configured hook and its source file, the same way `/permissions` does for permission rules. Use this whenever a hook "isn't firing" — it's almost always a typo in the event name or a `matcher` regex that doesn't match the tool you expected.

## 10. Rules and Memory — CLAUDE.md, `.claude/rules/`, and auto memory

This section covers everything advisory-context-related: what you write (`CLAUDE.md`, `.claude/rules/`) and what Claude writes for itself (auto memory). All of it is **context, not enforced configuration** — Claude tries to follow it, with no hard guarantee, which is the central fact that should shape how you write every file in this section.

### 10.1 CLAUDE.md vs. auto memory vs. rules — pick the right tool

|               | CLAUDE.md                                             | `.claude/rules/*.md`                                                                    | Auto memory                                                                    |
| ------------- | ----------------------------------------------------- | --------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------ |
| Who writes it | You                                                   | You                                                                                     | Claude                                                                         |
| Contains      | Instructions/rules                                    | Topic-scoped instructions/rules                                                         | Learnings and patterns                                                         |
| Loaded        | Every session, in full                                | Every session (no `paths:`) or on-demand (with `paths:`)                                | `MEMORY.md` every session (first 200 lines/25KB); topic files on demand        |
| Best for      | Coding standards, project architecture, "always do X" | The same, but split for a large project so unrelated instructions don't load every time | Build commands Claude figured out itself, debugging insights, your corrections |

If something must run at a specific, non-negotiable point (before every commit, after every edit), none of the three is the right tool — write a **hook** (§9) instead. If you want it injected at the system-prompt level for scripts/CI specifically, use `--append-system-prompt` (must be passed every invocation).

### 10.2 CLAUDE.md — locations and load order

| Scope          | Location                                                                                       | Shared with                             |
| -------------- | ---------------------------------------------------------------------------------------------- | --------------------------------------- |
| Managed policy | Linux/WSL: `/etc/claude-code/CLAUDE.md` (or the `claudeMd` key inside `managed-settings.json`) | Everyone in the org, cannot be excluded |
| User           | `~/.claude/CLAUDE.md`                                                                          | Just you, every project                 |
| Project        | `./CLAUDE.md` or `./.claude/CLAUDE.md`                                                         | Team, via git                           |
| Local          | `./CLAUDE.local.md`                                                                            | Just you, this project                  |

Claude Code walks **up** the directory tree from your working directory, collecting every `CLAUDE.md`/`CLAUDE.local.md` it finds. All discovered files are **concatenated**, not merged with override semantics — every one contributes. Ordering: broader/ancestor files load first, files closer to your working directory load last (so they're "freshest" in context); within one directory, `CLAUDE.local.md` is appended right after that level's `CLAUDE.md`. Files in _subdirectories_ below your working directory are not loaded at launch — they load on demand only when Claude actually reads a file in that subdirectory, which is how a monorepo keeps per-package instructions from bloating every session's startup context.

### 10.3 What should and shouldn't go in CLAUDE.md

Add an entry whenever: Claude makes the same mistake twice, a code review catches something Claude should already have known, you type the same clarification two sessions in a row, or a new teammate would need the same context to be productive. Keep it to durable facts: build/test commands, naming/architecture conventions, "always/never" rules. If an entry is actually a multi-step procedure, or only matters for one part of the codebase, it belongs in a skill or a `paths:`-scoped rule instead — this is the single most common CLAUDE.md anti-pattern (turning it into a how-to manual instead of a fact sheet).

Writing guidance that measurably improves adherence:

- **Be concrete enough to verify.** "Use 2-space indentation" beats "format code properly." "Run `npm test` before committing" beats "test your changes."
- **Target under 200 lines.** Longer files cost more context and Claude follows them less reliably; split into `.claude/rules/` once you're near that ceiling.
- **Use headers and bullets**, not dense paragraphs — Claude scans structure the way a human reader does.
- **Resolve contradictions.** If two CLAUDE.md files (or a rule and a CLAUDE.md) give conflicting guidance for the same behavior, Claude may pick one arbitrarily; periodically audit for this, especially after merging branches.
- **Emphasis markers work as real priority signals**, not just decoration — reserving `IMPORTANT` or `YOU MUST` for one or two genuinely critical rules measurably improves how reliably the model follows them; overusing the markers dilutes the effect.

A reasonable project `CLAUDE.md`:

```markdown
# Project conventions

## Commands

- Build: `npm run build`
- Test: `npm test`
- Lint: `npm run lint`

## Stack

- TypeScript strict mode, React 19, functional components only

## Conventions

- Named exports, never default exports
- Tests live next to source: foo.ts -> foo.test.ts
- API handlers live in src/api/handlers/

IMPORTANT: never commit directly to main — always open a PR.
```

Generate a first draft automatically with `/init`, which inspects your codebase and writes build commands, test instructions, and detected conventions; running it again on an existing CLAUDE.md suggests improvements rather than overwriting. Setting `CLAUDE_CODE_NEW_INIT=1` enables an interactive multi-phase `/init` that also proposes skills and hooks, not just CLAUDE.md, and shows you a reviewable plan before writing anything.

### 10.4 `.claude/rules/` — splitting a large CLAUDE.md

Once a project's `CLAUDE.md` is creeping past ~150–200 lines, split it by topic:

```
.claude/
├── CLAUDE.md           # short index / cross-cutting facts
└── rules/
    ├── code-style.md
    ├── testing.md
    └── security.md
```

All `.md` files under `rules/` are discovered recursively, so subdirectories like `frontend/`/`backend/` work fine. A rule **without** `paths:` frontmatter loads unconditionally, at the same priority as `CLAUDE.md` itself. A rule **with** `paths:` only enters context once Claude reads a file matching the glob — this is how you keep a large monorepo's per-team conventions from front-loading into every session:

```yam
paths:
  - "src/api/**/*.ts

# API Development Rules
- All API endpoints must include input validation
- Use the standard error response format
- Include OpenAPI documentation comments
```

| Pattern                | Matches                                                |
| ---------------------- | ------------------------------------------------------ |
| `**/*.ts`              | All TypeScript files, any directory                    |
| `src/**/*`             | Everything under `src/`                                |
| `*.md`                 | Markdown files in the project root only                |
| `src/components/*.tsx` | React components in one specific directory             |
| `src/**/*.{ts,tsx}`    | Brace-expansion for multiple extensions in one pattern |

**Sharing rules across projects via symlinks** — genuinely useful once you have more than one repo:

```bash
ln -s ~/shared-claude-rules .claude/rules/shared
ln -s ~/company-standards/security.md .claude/rules/security.md
```

Symlinks are resolved and loaded normally; circular symlinks are detected and handled gracefully. **User-level rules** (`~/.claude/rules/*.md`) apply to every project on your machine and load _before_ project rules, so project-level instructions take priority on conflict — put truly personal, non-project-specific preferences here.

For excluding noisy ancestor files in a large monorepo, `claudeMdExcludes` (any settings layer, arrays merge) skips specific paths/globs:

```json
{
  "claudeMdExcludes": [
    "**/monorepo/CLAUDE.md",
    "/home/user/monorepo/other-team/.claude/rules/**"
  ]
}
```

### 10.5 Auto memory — Claude's self-written notes

Requires Claude Code v2.1.59+. Auto memory lets Claude accumulate knowledge across sessions without you writing anything: build commands it had to discover the hard way, debugging insights, your corrections to its behavior, workflow habits. It decides what's worth saving — it doesn't write something every session.

Storage: `~/.claude/projects/<project>/memory/`, where `<project>` is derived from the git repo (so all worktrees of the same repo share one memory directory; outside a git repo, the project root is used). Relocate it with `autoMemoryDirectory` in settings; disable with `autoMemoryEnabled: false` or `CLAUDE_CODE_DISABLE_AUTO_MEMORY=1`.

```
~/.claude/projects/<project>/memory/
├── MEMORY.md          # concise index, loaded into every session
├── debugging.md       # detailed notes, loaded on demand
├── api-conventions.md
└── ...
```

Only the **first 200 lines or 25KB** of `MEMORY.md` (whichever comes first) load at session start; Claude keeps it as a lean index and moves detail into topic files it reads on demand with its normal file tools. This limit is specific to `MEMORY.md` — CLAUDE.md files load in full regardless of length (though shorter still adheres better). Auto memory is **machine-local**: it doesn't sync across machines or cloud environments, only across worktrees/subdirectories of the same repo on the same box.

You can explicitly steer it conversationally — "always use pnpm, not npm" or "remember the API tests need a local Redis instance" gets saved to auto memory directly; "add this to CLAUDE.md" routes to the durable file instead.

### 10.6 `/memory` — the single place to audit everything

```text
/memory
```

Lists every `CLAUDE.md`/`CLAUDE.local.md`/rules file loaded this session, toggles auto memory on/off, and links to the auto-memory folder. Selecting any file opens it in your editor (which, with `EDITOR=nvim` set per §16, opens it right in your Neovim pane). If a file you expect isn't listed here, Claude genuinely cannot see it — that's the first thing to check whenever an instruction "isn't being followed."

### 10.7 Imports — `@path` syntax

```markdown
See @README for project overview and @package.json for available npm commands.

# Additional Instructions

- git workflow: @docs/git-instructions.md
```

Both relative (resolved relative to the _referencing_ file, not your working directory) and absolute paths work; imports can recursively import, up to 4 hops. Wrapping a path in backticks (`` `@README` ``) keeps it literal instead of importing it. The first time a project uses external imports, Claude Code shows an approval dialog listing the files — declining permanently disables that project's imports until you change your mind in settings (this exists specifically as prompt-injection protection for cloned repos with a CLAUDE.md you haven't read). Imported content still loads in full at launch — imports help organization, not context size.

For personal preferences that should follow you across every **worktree** of a repo (where a gitignored `CLAUDE.local.md` would otherwise only exist in the one worktree you created it in), import from your home directory instead:

```markdown
# Individual Preferences

- @~/.claude/my-project-instructions.md
```

### 10.8 Interop with `AGENTS.md` (if you already use other AI coding tools)

Claude Code reads `CLAUDE.md`, not `AGENTS.md`. If your repo already standardizes on `AGENTS.md` for other tools, don't duplicate content — import it and append Claude-specific notes:

```markdown
@AGENTS.md

## Claude Code

Use plan mode for changes under `src/billing/`.
```

A plain symlink (`ln -s AGENTS.md CLAUDE.md`) works too if you don't need Claude-specific additions (on Windows this needs admin/Developer Mode, so the import form is more portable). `/init` run in a repo that already has an `AGENTS.md` reads it and folds the relevant parts into the generated `CLAUDE.md` automatically, along with picking up `.cursorrules` and `.windsurfrules` if present.

### 10.9 Troubleshooting

| Symptom                                         | Likely cause / fix                                                                                                                                                                                                                                                                                                                              |
| ----------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Claude ignores a CLAUDE.md instruction          | Run `/memory` — if the file isn't listed, it's not loading from where you think it is. Otherwise make the instruction more specific/concrete.                                                                                                                                                                                                   |
| Instructions disappeared after `/compact`       | Project-root `CLAUDE.md` is automatically re-read from disk after compaction; nested CLAUDE.md files in subdirectories are not auto-reinjected — they reload only the next time Claude reads a file there. A conversation-only instruction (never written to a file) is simply gone after compaction — promote anything important to CLAUDE.md. |
| Two files give contradictory guidance           | Claude may pick arbitrarily; audit and remove the stale one. Use `claudeMdExcludes` in a monorepo to stop pulling in irrelevant ancestor files.                                                                                                                                                                                                 |
| Need to know exactly which files loaded and why | Add an `InstructionsLoaded` hook (§9.1) to log it explicitly — most useful for debugging lazy-loaded nested files or `paths:`-scoped rules.                                                                                                                                                                                                     |
| `MEMORY.md` feels bloated                       | It's capped at 200 lines/25KB on load anyway; move detail into topic files and keep `MEMORY.md` as a pure index.                                                                                                                                                                                                                                |

## 11. Session management — the complete picture

### 11.1 What a session is, and where it lives on disk

A session is one conversation tied to a project directory, saved continuously as JSONL:

```
~/.claude/projects/<project-hash>/<session-id>.jsonl
```

Each line is one message/tool-call/metadata event — you can `cat`/`jq` this file directly if you ever need to extract something programmatically. Removed automatically after 30 days (`cleanupPeriodDays` in settings to change). Subagent transcripts are stored separately at `~/.claude/projects/{project}/{sessionId}/subagents/agent-{agentId}.jsonl` and persist independently of main-session compaction — handy if you want to review exactly what a subagent did after the fact.

### 11.2 Resuming

```bash
claude --continue            # resume most recent session in this directory
claude --resume               # interactive picker
claude --resume auth-refactor # resume by name, exact match
claude -n "feature-auth"      # name a NEW session at startup
```

Inside a session, `/resume` opens the same picker without exiting first. Picker shortcuts worth memorizing: `Ctrl+W` widens the list to all worktrees of this repo, `Ctrl+A` widens it to every project on the machine, `Space` previews a session without resuming it, `Ctrl+R` renames in place.

### 11.3 Naming sessions, and why it matters for a tmux setup

```text
/rename auth-refactor
```

Once named, `claude --resume auth-refactor` jumps straight to it from any terminal. Running several Claude panes across several tmux windows for several features, naming sessions as you start them is the difference between "which pane was the OAuth one?" three hours later, and just knowing.

### 11.4 Branching (forking) a conversation

```text
/branch try-streaming-approach
```

or from the shell when resuming:

```bash
claude --continue --fork-session
```

Creates a copy of the conversation and switches you into it; the original stays untouched and independently resumable. Use this before trying a risky alternative approach so you can always fall back to the working path without re-litigating the conversation.

### 11.5 Checkpoints (`/rewind`) — local undo for code _and_ conversation

Every prompt you send creates a checkpoint automatically. Press `Esc` `Esc` (with an empty input box) or run `/rewind` to choose:

- **Restore code and conversation** to that point
- **Restore conversation only**, keep current code
- **Restore code only**, keep conversation
- **Summarize from/up to here** — compress part of a long session to free context without touching files on disk

Limitation worth internalizing: checkpoints only track Claude's native Edit/Write tool calls, **not** changes made via Bash (`rm`, `mv`, scripted edits). Git remains your real version-history mechanism — checkpoints are session-level local undo, not a git replacement.

### 11.6 Can sessions talk to each other? Three distinct answers

This is worth understanding precisely, since the three mechanisms look superficially similar but are architecturally different.

**1. Subagents (within one session).** A subagent only ever reports a summary back to the main agent that spawned it. Subagents never talk to each other directly, and they don't persist as independently resumable sessions (though you can ask Claude to continue a named subagent's own thread within the same parent session).

**2. Worktrees (separate sessions, separate checkouts, zero built-in messaging).** `claude --worktree feature-auth` gives you an isolated git checkout at `<repo>/.claude/worktrees/feature-auth/` on a new branch, plus a brand-new, fully independent `claude` process/session. Two worktree sessions run totally in parallel with **no communication channel between them at all** — you are the coordinator, manually carrying context between terminals/panes if you need to. This is the simplest mental model, and the one this guide leans on for the tmux setup in §14.

**3. Agent teams (experimental, multi-session, do message each other).** Enable with `CLAUDE_CODE_EXPERIMENTAL_AGENT_TEAMS=1`. One session becomes a "team lead," spawns "teammate" sessions (each a full independent Claude Code instance with its own context window), and they communicate via an automatic message-delivery system ("mailbox") plus a shared task list with file-locked claiming, stored at `~/.claude/teams/{team-name}/` and `~/.claude/tasks/{team-name}/`. Real limitations apply: no session-resume support for in-process teammates, one team at a time, no nested teams. Display mode is `tmux` (real split panes per teammate) or `in-process`, set via `teammateMode` in settings or `claude --teammate-mode tmux`. This is genuinely powerful for parallel research/review/competing-hypothesis debugging once you're comfortable with the basics — but it's explicitly marked experimental.

For your stated workflow (you manually manage tmux panes, Neovim inspects the result), **worktrees are almost certainly the right default**, with agent teams as a deliberate later upgrade once single-session and worktree workflows feel automatic.

### 11.7 Managing context within a session

```text
/clear     # wipe context, start fresh (previous convo still independently resumable)
/compact [instructions]   # summarize history, optionally focused on what to keep
/context    # visualize exactly what's eating your context window right now
/btw <question>   # side question, answered in a dismissible overlay, NEVER added to history
```

Rule of thumb: if you've corrected Claude twice on the same issue without success, `/clear` and write a sharper prompt rather than continuing to argue inside a context that's already anchored on the wrong approach.

## 12. MCP servers — connecting external tools

MCP (Model Context Protocol) is how Claude Code reaches things beyond its built-in toolset: issue trackers, databases, browsers, design tools, internal company APIs.

### 12.1 Where MCP server definitions live

| Scope           | Location                                      | Shared with             |
| --------------- | --------------------------------------------- | ----------------------- |
| Local (default) | `~/.claude.json`, scoped to this project path | Just you, this project  |
| User            | `~/.claude.json`, global key                  | Just you, every project |
| Project         | `.mcp.json` at repo root                      | Team, via git           |

### 12.2 Adding servers

```bash
# Hosted HTTP server, local scope (default — this project, just you)
claude mcp add --transport http claude-code-docs https://code.claude.com/docs/mcp

# Local stdio server, runs as a subprocess
claude mcp add playwright -- npx -y @playwright/mcp@latest

# Team-shared — writes .mcp.json, commit it
claude mcp add --scope project --transport http github https://mcp.github.com

# Personal, available in every project
claude mcp add --scope user playwright -- npx -y @playwright/mcp@latest
```

A `.mcp.json` you'd commit to a repo so the whole team gets the same servers:

```json
{
  "mcpServers": {
    "github": { "type": "http", "url": "https://mcp.github.com" },
    "postgres-readonly": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-postgres",
        "postgresql://localhost/mydb"
      ]
    }
  }
}
```

### 12.3 Checking and managing servers

```bash
claude mcp list                 # status of all configured servers
claude mcp get <name>           # details on one server
claude mcp remove <name>
```

Inside a session, `/mcp` shows live connection status and handles OAuth sign-in for servers that need it (Sentry, Linear, Notion, and similar SaaS connectors typically require this).

### 12.4 Project-level trust for `.mcp.json`

Because a cloned repo's `.mcp.json` can point Claude at arbitrary commands/servers, Claude Code prompts for approval the first time a project's `.mcp.json` is loaded — same workspace-trust gate that governs `.claude/settings.json` and project skills. Review a new repo's `.mcp.json` before accepting, the same way you'd review its CI config.

### 12.5 Permission control over MCP tools

MCP tools use a double-underscore naming convention with no parentheses, unlike built-in tools:

```json
{
  "permissions": {
    "allow": ["mcp__github__create_pull_request"],
    "deny": ["mcp__postgres-readonly__execute_write_query"]
  }
}
```

This lets you grant a server broadly while still denying one specific dangerous tool it exposes, or vice versa.

### 12.6 Debugging an MCP connection

1. `/mcp` for live status — it'll show whether the server is connected, needs auth, or failed to start.
2. For stdio servers, check that the underlying command (`npx`, a local binary, etc.) actually runs standalone in your WSL shell first — most "MCP server won't connect" issues are really "the subprocess command itself is broken in this environment."
3. `claude mcp list` cross-referenced against `.mcp.json`/`~/.claude.json` confirms which scope actually defined the server you're debugging.

## 13. Complete command reference

### 13.1 Slash commands (inside the TUI)

| Command                                   | Purpose                                            |
| ----------------------------------------- | -------------------------------------------------- |
| `/help`                                   | Show available commands                            |
| `/init`                                   | Generate a starting `CLAUDE.md` from your codebase |
| `/config` (alias `/settings`)             | Open the settings UI                               |
| `/permissions` (alias `/allowed-tools`)   | Manage allow/ask/deny rules                        |
| `/agents`                                 | Manage subagents                                   |
| `/skills`                                 | List available skills                              |
| `/hooks`                                  | View configured hooks                              |
| `/mcp`                                    | Manage MCP server connections                      |
| `/memory`                                 | Edit CLAUDE.md / browse auto-memory                |
| `/model [name]`                           | Switch model                                       |
| `/effort [low\|medium\|high\|xhigh\|max]` | Set reasoning effort                               |
| `/plan [description]`                     | Enter plan mode                                    |
| `/clear` (aliases `/reset`, `/new`)       | Wipe context                                       |
| `/compact [instructions]`                 | Summarize conversation                             |
| `/context`                                | Visualize context usage                            |
| `/rewind` (alias `/checkpoint`)           | Open checkpoint/undo menu                          |
| `/branch [name]` (alias `/fork`)          | Branch the conversation                            |
| `/resume [session]` (alias `/continue`)   | Resume a session                                   |
| `/rename [name]`                          | Rename current session                             |
| `/diff`                                   | Interactive diff viewer                            |
| `/export [filename]`                      | Export conversation as plain text                  |
| `/cost`                                   | Token usage stats                                  |
| `/usage`                                  | Plan rate-limit status                             |
| `/status`                                 | Version/account/connectivity/settings-sources info |
| `/doctor`                                 | Diagnostics                                        |
| `/add-dir <path>`                         | Grant access to another directory this session     |
| `/cd <path>`                              | Relocate the session's working directory           |
| `/btw <question>`                         | Side question, never enters history                |
| `/statusline`                             | Configure the status line                          |
| `/terminal-setup`                         | Fix Shift+Enter in terminals that need it          |
| `/theme`                                  | Change color theme                                 |
| `/vim`                                    | Toggle Vim editing mode for the prompt box         |
| `/security-review`                        | Analyze pending changes for vulnerabilities        |
| `/install-github-app`                     | Set up Claude GitHub Actions                       |
| `/pr-comments [PR]`                       | Fetch PR comments (requires `gh`)                  |
| `/feedback [report]` (alias `/bug`)       | Submit feedback                                    |
| `/logout`, `/login`                       | Auth                                               |
| `/exit` (alias `/quit`)                   | Exit                                               |

Plus bundled, **prompt-based skills** that appear alongside built-ins when you type `/` (these orchestrate Claude's own tools rather than executing fixed logic, and can be disabled fleet-wide with `disableBundledSkills`):

| Skill                  | Purpose                                                                                         |
| ---------------------- | ----------------------------------------------------------------------------------------------- |
| `/code-review`         | Review pending changes in a fresh subagent context                                              |
| `/debug`               | Structured debugging workflow                                                                   |
| `/loop`                | Repeat a task until a stated condition holds                                                    |
| `/batch`               | Apply the same operation across many items                                                      |
| `/run`                 | Launch and drive your app to see a change working live                                          |
| `/verify`              | Build and run the app to confirm a change does what it should, beyond just tests                |
| `/run-skill-generator` | Records how to build/launch your project once, as a reusable `.claude/skills/run-<name>/` skill |
| `/claude-api`          | Helper for the in-artifact "Claude calling Claude" API pattern                                  |

### 13.2 CLI commands (run from your shell, not inside the TUI)

| Command                           | Purpose                                           |
| --------------------------------- | ------------------------------------------------- |
| `claude`                          | Start interactive session                         |
| `claude "prompt"`                 | Start with an initial prompt                      |
| `claude -p "prompt"`              | Non-interactive (headless) mode, prints and exits |
| `claude -c`                       | Continue most recent session in this directory    |
| `claude -r "<name-or-id>"`        | Resume a specific session                         |
| `claude -n "name"`                | Name a new session                                |
| `claude -w <name>` (`--worktree`) | Start in an isolated git worktree                 |
| `claude --agent <name>`           | Run the whole session as a given subagent         |
| `claude update`                   | Update to latest version                          |
| `claude doctor`                   | Diagnostics                                       |
| `claude auth login/logout/status` | Auth management                                   |
| `claude mcp add/list/remove/get`  | MCP server management                             |
| `claude agents`                   | Open agent view (background-session monitor)      |
| `claude attach <id>`              | Attach to a background session                    |
| `claude plugin install/list/...`  | Plugin management                                 |
| `claude setup-token`              | Long-lived token for CI                           |
| `claude project purge [path]`     | Delete all local state for a project              |

### 13.3 Most useful CLI flags

| Flag                                      | Purpose                                                                         |
| ----------------------------------------- | ------------------------------------------------------------------------------- |
| `--print`, `-p`                           | Headless mode (scripting/CI)                                                    |
| `--output-format text\|json\|stream-json` | Machine-readable output                                                         |
| `--permission-mode <mode>`                | Start in a specific permission mode                                             |
| `--dangerously-skip-permissions`          | Equivalent to `bypassPermissions` — sandboxed environments only                 |
| `--allowedTools` / `--disallowedTools`    | Session-scoped allow/deny rules                                                 |
| `--add-dir <path>`                        | Grant access to extra directories (and, uniquely, load their `.claude/skills/`) |
| `--model <alias-or-id>`                   | Pick a model for this session                                                   |
| `--continue`, `-c`                        | Resume most recent session                                                      |
| `--resume`, `-r`                          | Resume picker or by name/id                                                     |
| `--fork-session`                          | Branch instead of overwrite when resuming                                       |
| `--worktree`, `-w`                        | Isolated git worktree session                                                   |
| `--mcp-config <file>`                     | Load MCP servers from a JSON file                                               |
| `--settings <file-or-json>`               | One-off settings override                                                       |
| `--append-system-prompt <text>`           | Append to the system prompt (every invocation, scripting-oriented)              |
| `--max-turns <n>`                         | Cap agentic turns (headless mode)                                               |
| `--bare`                                  | Minimal/fast startup, skip hooks/skills/MCP discovery                           |
| `--setting-sources <list>`                | Restrict which settings layers load for this invocation                         |

## 14. The Tmux + Neovim + Claude Code workflow

This is the heart of what you asked for. The model: **tmux owns the panes, Claude Code TUI runs natively in one, Neovim runs natively in another, and the filesystem is the only thing they share.**

### 14.1 Minimal two-pane layout

```bash
tmux new-session -s work -n main
tmux split-window -h          # vertical split: left/right panes
tmux select-pane -t 0
nvim .                         # left pane: Neovim
tmux select-pane -t 1
claude                          # right pane: Claude Code TUI
```

Or as a one-liner from outside tmux:

```bash
tmux new-session -d -s work -n main \; \
  split-window -h \; \
  send-keys -t work:main.0 'nvim .' C-m \; \
  send-keys -t work:main.1 'claude' C-m \; \
  attach -t work
```

Save that as `~/bin/cc-work` (`chmod +x`) and you have a one-command launcher for the whole layout per project.

### 14.2 Critical Neovim setting: auto-reload files Claude changes on disk

Neovim does **not** auto-reload buffers when a file changes outside the editor by default. Since Claude Code is editing the same files your Neovim buffers point at, you need:

```lua
-- init.lua
vim.o.autoread = true

vim.api.nvim_create_autocmd({ "FocusGained", "BufEnter", "CursorHold", "CursorHoldI" }, {
  pattern = "*",
  command = "if mode() != 'c' | checktime | endif",
})
```

`autoread` alone is not sufficient in many terminal setups — Vim only checks file timestamps on specific events, so the `checktime` autocommand on `FocusGained`/`BufEnter` is what actually makes "switch to the Neovim pane and see Claude's edits" work reliably. Without this, you'll be looking at stale buffers and wondering why your file doesn't match what Claude says it wrote.

If you use a plugin manager, the equivalent off-the-shelf options are `djoshea/vim-autoread` or simply the autocommand above — no need for anything Claude-specific.

### 14.3 tmux config additions worth making

`~/.tmux.conf`:

```tmux
# Let escape sequences (desktop notifications, progress bar) pass through
# from Claude Code to your real terminal (Windows Terminal / WSL terminal app)
set -g allow-passthrough on

# Faster pane switching, since you'll bounce between nvim and claude constantly
bind -n M-Left select-pane -L
bind -n M-Right select-pane -R
bind -n M-Up select-pane -U
bind -n M-Down select-pane -D

# Bigger scrollback for the Claude pane (it can produce long tool output)
set -g history-limit 50000

# Truecolor inside tmux (Claude Code clamps to 256 colors under tmux otherwise)
set -ga terminal-overrides ',*:Tc'
```

`allow-passthrough on` matters specifically because Claude Code's `Notification` hook and its terminal progress bar rely on escape sequences that tmux otherwise swallows — without this setting, you lose desktop notifications when Claude needs your input while you're focused in the Neovim pane. After adding the `terminal-overrides` line, set `CLAUDE_CODE_TMUX_TRUECOLOR=1` in your shell profile so Claude Code actually uses the wider color range instead of clamping defensively.

### 14.4 Get notified when Claude needs you, while you're heads-down in Neovim

This is the single highest-leverage hook for this workflow. Add to `~/.claude/settings.json`:

```json
{
  "hooks": {
    "Notification": [
      {
        "matcher": "",
        "hooks": [
          {
            "type": "command",
            "command": "notify-send 'Claude Code' 'Needs your attention'"
          }
        ]
      }
    ]
  }
}
```

`notify-send` requires `libnotify-bin` (`sudo apt install libnotify-bin`) and a notification daemon — on WSL with WSLg or a Windows toast bridge (e.g. wsl-notify-send / SnoreToast) this surfaces as a native Windows notification. If that's not set up, a cheap fallback is a terminal bell instead:

```json
{
  "hooks": {
    "Notification": [
      {
        "matcher": "",
        "hooks": [{ "type": "command", "command": "printf '\\a'" }]
      }
    ]
  }
}
```

Narrow the matcher if you only want to be pinged for approval prompts specifically rather than every idle moment — check `/hooks` after editing to confirm the matcher you wrote actually applies where you expect.

### 14.5 Reviewing Claude's diffs without leaving Neovim

Two complementary options:

- **Inside Claude Code**: `/diff` opens an interactive diff viewer (left/right arrows switch between the git diff and individual Claude turns).
- **Inside Neovim**: your normal git diff tooling (`:Gdiffsplit` with vim-fugitive, or a plain `git diff` in a terminal split) works unmodified, since Claude writes straight to disk and git is the shared source of truth. This is precisely why a Neovim plugin isn't necessary for this workflow.

### 14.6 Parallel feature work: one worktree per tmux window

For real parallelism (not just split panes on one branch), combine tmux windows with `--worktree`:

```bash
# Window 1: feature A
tmux new-window -n feat-auth
claude --worktree feature-auth -n feat-auth

# Window 2: feature B, fully isolated checkout, different branch
tmux new-window -n bugfix-123
claude --worktree bugfix-123 -n bugfix-123
```

Each `--worktree` invocation creates an isolated checkout at `<repo>/.claude/worktrees/<name>/` on a new branch, so simultaneous edits from two Claude sessions never collide on disk. Add `.claude/worktrees/` to `.gitignore`. Open a Neovim instance per worktree directory if you want to inspect both simultaneously (`nvim <repo>/.claude/worktrees/feature-auth`).

Claude Code also has a native `--tmux` flag, but it's specifically for **agent-team** split panes (`claude -w feature-auth --tmux`, §11.6), not a general multi-worktree launcher — for your manual two-pane (nvim/claude) setup, the launcher script in §14.1 plus `tmux new-window` per feature is the more direct fit.

### 14.7 Suggested daily loop

1. `cc-work` (your launcher script) opens nvim + claude side by side.
2. In the Claude pane: describe the task, optionally start in plan mode for anything non-trivial (`/plan fix the auth bug`, or `Shift+Tab` to cycle modes).
3. While Claude works, flip to the Neovim pane to read the surrounding code, or just wait for the `notify-send` ping.
4. When Claude finishes a chunk, review with `/diff` or your Neovim git-diff workflow.
5. Course-correct directly in the Claude pane (`Esc` to interrupt mid-action, context preserved) rather than hand-fixing in Neovim and hoping Claude notices — keep the agent and the ground truth in sync.
6. `/rename` the session once it has a clear identity, so you can `claude --resume <name>` tomorrow.

## 15. Other strengths worth knowing about

- **Plan mode** (`/plan`, or `Shift+Tab` to cycle) — forces Claude into read-only research before touching files. Use this for anything non-trivial; skip it for one-line fixes you could describe as a single diff.
- **Subagents for context hygiene** — the single best lever against context rot. "Use a subagent to investigate X" keeps verbose exploration out of your main thread.
- **Extended thinking / effort levels** — `/effort high` (or `xhigh`/`max` on capable models) for genuinely hard problems; `low` for trivial mechanical tasks to save tokens/latency.
- **Output styles** (`~/.claude/output-styles/*.md`) — swap the system prompt's "personality" without touching CLAUDE.md, e.g. a "Teaching" style that explains reasoning and leaves small edits for you to do by hand.
- **Status line** — `/statusline` to show git branch, context usage %, or cost live at the bottom of the TUI; genuinely useful once you're managing several parallel sessions across tmux windows.
- **Plugins** (`/plugin`) — bundle skills + hooks + subagents + MCP servers into one installable unit; check the official marketplace for a code-intelligence plugin matching your primary language before hand-rolling LSP-style tooling yourself.
- **Headless/scripted use** — `claude -p "prompt" --output-format json` is how you wire Claude Code into pre-commit hooks, CI pipelines, or batch migration scripts (loop over a file list calling `claude -p` per file with `--allowedTools` scoped tight).
- **`/code-review` and `/security-review`** — bundled skills worth running before opening a PR, each running in a fresh subagent so review feedback isn't biased by the same context that wrote the code.
- **`/run`, `/verify`, `/run-skill-generator`** — confirm changes against your actually-running app rather than just tests/types; run `/run-skill-generator` once per project so this stops needing to rediscover your launch process every time.
- **Adversarial review pattern** — after a non-trivial change, explicitly ask for "a subagent to review this diff against the plan and report only correctness gaps" before calling the task done; a fresh context catches more than the implementing context re-reading itself.

## 16. Suggested starting configuration

A reasonable first-week baseline. Adjust as you learn your own patterns — that's the whole point of this system being so configurable.

`~/.claude/CLAUDE.md`:

```markdown
# Personal preferences

- Keep explanations concise; show the verification command after any change
- Use conventional commit format
- Prefer composition over inheritance
- I review diffs in Neovim via git; don't ask me to view files yourself unless investigating
```

`~/.claude/settings.json`:

```json
{
  "$schema": "https://json.schemastore.org/claude-code-settings.json",
  "permissions": {
    "allow": [
      "Bash(git status)",
      "Bash(git diff *)",
      "Bash(git log *)",
      "Bash(npm run *)",
      "Bash(npm test *)"
    ],
    "deny": [
      "Read(./.env)",
      "Read(./.env.*)",
      "Read(./secrets/**)",
      "Bash(curl *)"
    ]
  },
  "env": { "EDITOR": "nvim" },
  "hooks": {
    "Notification": [
      {
        "matcher": "",
        "hooks": [
          {
            "type": "command",
            "command": "notify-send 'Claude Code' 'Needs your attention'"
          }
        ]
      }
    ]
  }
}
```

First-week routine:

1. Day 1–2: run in `default` permission mode, let the prompts teach you which commands you keep approving, add them to `allow`.
2. Day 3: run `/init` in your main project to get a real `CLAUDE.md`, then prune it ruthlessly — delete anything Claude would've figured out anyway.
3. Day 4–5: write your first project-scoped skill for whatever repetitive task you keep typing into chat (a deploy step, a "summarize my diff" habit, a test-runner wrapper).
4. Week 2: write your first subagent (a read-only code reviewer is the easiest useful one) and try `--worktree` for two genuinely parallel features.
5. Once comfortable: experiment with hooks for anything you keep doing manually after every edit (formatting, protected-file guards), then revisit `.claude/rules/` once your `CLAUDE.md` starts feeling crowded.

## 17. Quick reference cheat sheet

```text
INSTALL          curl -fsSL https://claude.ai/install.sh | bash
VERIFY           claude --version && claude doctor
LOGIN            claude  (or /login inside session)
START            claude
NAMED START      claude -n "feature-x"
RESUME LAST      claude -c
RESUME PICKER    claude -r
RESUME NAMED     claude -r feature-x
NEW WORKTREE     claude -w feature-x
HEADLESS         claude -p "prompt" --output-format json
PERMISSIONS UI   /permissions
SETTINGS UI      /config
WHAT LOADED      /context  /memory  /agents  /hooks  /mcp  /skills
PLAN MODE        /plan  or  Shift+Tab
UNDO             Esc Esc   or   /rewind
CLEAR CONTEXT    /clear
COMPACT          /compact [focus instructions]
BRANCH SESSION   /branch [name]
RENAME SESSION   /rename [name]
DIFF VIEW        /diff
SKILL FILE       .claude/skills/<name>/SKILL.md   (or ~/.claude/skills/... for global)
SUBAGENT FILE    .claude/agents/<name>.md          (or ~/.claude/agents/... for global)
RULE FILE        .claude/rules/<topic>.md          (or ~/.claude/rules/... for global)
HOOKS            "hooks" key in settings.json (project or ~/.claude/)
MCP SERVERS      .mcp.json (project) or ~/.claude.json (personal) — claude mcp add ...
GLOBAL CLAUDE.md ~/.claude/CLAUDE.md
SESSION FILES    ~/.claude/projects/<project>/<session-id>.jsonl
AUTO MEMORY      ~/.claude/projects/<project>/memory/MEMORY.md
PERMISSION RULE  ToolName(specifier) — deny > ask > allow, first match wins
```

## 18. Where to go deeper

Everything above is sourced from the official documentation, which stays current as Claude Code ships new releases roughly weekly:

- Docs index: `https://code.claude.com/docs/en/claude_code_docs_map.md`
- Settings: `https://code.claude.com/docs/en/settings`
- Permissions: `https://code.claude.com/docs/en/permissions`
- Skills: `https://code.claude.com/docs/en/skills`
- Subagents: `https://code.claude.com/docs/en/sub-agents`
- Hooks: `https://code.claude.com/docs/en/hooks` and `https://code.claude.com/docs/en/hooks-guide`
- Memory (CLAUDE.md, rules, auto memory): `https://code.claude.com/docs/en/memory`
- Sessions: `https://code.claude.com/docs/en/sessions`
- Worktrees: `https://code.claude.com/docs/en/worktrees`
- Agent teams: `https://code.claude.com/docs/en/agent-teams`
- MCP: `https://code.claude.com/docs/en/mcp-quickstart`

Since the product changes fast, when in doubt, ask Claude Code itself: _"What does the `X` setting do — check your own docs"_ — it has a built-in `claude-code-guide` helper agent specifically for answering questions about its own features accurately.
