# AI-LOG.md — template

## Entry format

```md
## <YYYY-MM-DD> — <what you were working on>

Tool: <Claude Code / Copilot / Cursor / something else / none>
Asked for: <the task you gave it, in one sentence>
Kept: <what you took as it came>
Changed: <what you rewrote, and why>
Rejected: <what you threw away, and why>
By hand: <the parts you wrote yourself>
```

`Rejected` and `By hand` are the two lines we read most closely. "I wrote this part myself" is a perfectly good entry.

## A filled example

````md
## 2026-10-09 — feature spec (IA#1)

```
Tool: Claude Code.
Asked for: a first draft of the acceptance criteria from my Flow section.
Kept: the three-way split arithmetic criterion.
Changed: its criteria said "works correctly" — rewrote each one with concrete values so it can fail.
Rejected: its refund flow. Refunds are a non-goal; I wrote that line instead.
By hand: the contract and the error table.
```

## 2026-10-10 — eval for the summarise feature

```
Tool: none. Written by hand: the failure cases are the point of the exercise.
```
````

## What a weak log looks like

```md
## Week 3

Used AI for the frontend. Fixed some things.
```

It says nothing we can check, and nothing that helps you at the oral. If a task took you three hours of argument with a tool, that story is worth one line — it is evidence you were in control.
