---
name: ai-logging
description: Log AI-assisted work into a repo's AI-LOG.md file, following the course template (Tool / Asked for / Kept / Changed / Rejected / By hand). Use this skill whenever the user asks to log, record, document, or write up an AI interaction (e.g. "log this", "ghi vào AI-LOG", "update the AI-LOG", "cập nhật AI-LOG"), or whenever a standing instruction (such as a line in AGENTS.md) tells Claude to invoke this skill after AI-assisted work. Only skill for maintaining AI-LOG.md entries — always use it instead of editing AI-LOG.md by hand.
---

# AI Logging

Maintains a project's `AI-LOG.md` file: one entry per AI-assisted task, appended in the format required by the course (Tool / Asked for / Kept / Changed / Rejected / By hand). The point of the log is accountability — it must read as an honest, checkable account of what the AI did and what the human did, not a transcript and not a formality.

## When this skill runs

It can be invoked two ways, and both end up in the same workflow below:

- **Explicitly** — the user asks to log, record, or write up the AI-assisted work just done.
- **Standing instruction** — something outside this conversation (e.g. a rule in the repo's `AGENTS.md`) tells Claude to invoke this skill after AI-assisted interactions. Follow that instruction if present; this skill does not assume or require it.

## Workflow

### Step 1 — Decide if the task is worth logging

Not every exchange deserves an entry. Look back at the AI-assisted work just completed and judge whether it was a **meaningful contribution:**

- **Log it:** new or changed logic, generated code, tests, architecture or design decisions, debugging that involved real reasoning, a spec or document drafted with AI help.
- **Skip it:** typo fixes, formatting, pure Q&A that produced no change, trivial one-line edits, re-running something unchanged.

If it's skip-worthy, say so in one short line in English (e.g. "This one's too minor to log, skipping it.") and stop — do not create an entry, do not ask for confirmation to skip.

If genuinely unsure which side it falls on, ask the user in one short question rather than guessing silently.

### Step 2 — Locate AI-LOG.md

- If there is exactly one `AI-LOG.md` at the current git repo's root, use it.
- If none exists at the root, check whether this is clearly a fresh milestone (e.g. nothing else in the repo yet) — if so, create a new `AI-LOG.md` there using the header from `assets/AI-LOG.template.md`.
- If it's ambiguous — no file found and unclear whether one should exist, multiple candidate locations, multiple milestones/subfolders each with their own log, or any other doubt — ask the user for the path before doing anything else. Never guess silently and never create a duplicate log next to an existing one.

### Step 3 — Draft the entry

Compose all six fields yourself from the conversation context, then show the full draft to the user before writing anything to disk. Never write directly without a draft step — the user may have reasoning (e.g. what they changed by hand) that isn't visible in the conversation.

Field by field:

- **Tool:** which AI tool/assistant was used (infer from context — Claude Code, Codex, etc.). If genuinely `none`, say so plainly per the template's own example.
- **Asked for:** the task given to the AI, in one sentence — reconstruct from what the user actually requested.
- **Kept:** what was taken as-is from the AI's output.
- **Changed:** what was rewritten after the AI produced it, and why. If the conversation shows the user editing, correcting, or redirecting the AI's output, that's the material for this line.
- **Rejected:** what was thrown away or not used, and why.
- **By hand:** the parts the user wrote themselves, with no AI involvement.

Keep every line short and concrete — the template's own guidance is "three honest lines are enough" and these are "the account, not a transcript." Avoid vague filler like "fixed some things" or "works correctly" — use specifics (what, why) the same way the template's own filled example does. If the conversation genuinely gives no signal for a field (e.g. nothing was rejected), write a short honest line saying so (e.g. "Rejected: nothing — kept the first draft") rather than inventing content.

Present the draft like this before writing it:

```
Here's the entry I drafted — take a look and correct anything that's off:

## <YYYY-MM-DD> — <task title>

Tool: ...
Asked for: ...
Kept: ...
Changed: ...
Rejected: ...
By hand: ...
```

Wait for the user to confirm or correct it.

### Step 4 — Append

Once confirmed (or corrected and then confirmed), append the entry to the end of `AI-LOG.md`:

- Never overwrite or reorder existing entries.
- Add a blank line between the previous content and the new entry.
- Use today's date unless the user specifies otherwise.
- If the file didn't exist yet, create it first with the standard header (see `assets/AI-LOG.template.md`) before adding the entry.

## Entry format reference

The exact format (and a filled example, and what a weak entry looks like) is in `assets/AI-LOG.template.md` — read it if you need the full template text, including the five rules the course attaches to this file. The core shape to reproduce for every entry:

```md
## <YYYY-MM-DD> — <what you were working on>

Tool: <Claude Code / Copilot / Cursor / something else / none>
Asked for: <the task you gave it, in one sentence>
Kept: <what you took as it came>
Changed: <what you rewrote, and why>
Rejected: <what you threw away, and why>
By hand: <the parts you wrote yourself>
```
