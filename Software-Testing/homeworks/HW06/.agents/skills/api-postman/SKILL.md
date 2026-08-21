---
name: api-postman
description: Set up Postman workspace, collections, environment, mock server, and monitor on Postman cloud via the postman-sync agent and Postman MCP. Use this skill when you need to sync local collection files to Postman App without manual import steps. Trigger when the user says "set up Postman", "upload to Postman", "sync collections to Postman", "create Postman workspace", or "set up mock server on Postman".
---

# `api-postman` Skill

## Overview

Orchestrate the setup of Postman cloud resources — workspace, collections, environment, and optionally a mock server and monitor — by delegating to the `postman-sync` agent via Postman MCP (remote US server, OAuth, Full mode).

This skill is the **human-facing wrapper** for `postman-sync`. It handles input validation, constructs a complete setup plan, invokes the agent, and presents a verified resource summary for human confirmation. The human then opens Postman App to verify, run collections via Collection Runner, and capture screenshots for evidence.

**This skill does not execute any API requests against the SUT.** The Postman MCP remote server cannot reach localhost — it only manages Postman cloud resources.

**Primary output:** Resources created on Postman cloud — workspace, collection(s), environment, and optionally mock server and monitor.

## When to Use

- When local collection files are ready and need to be uploaded to Postman App for demonstration or validation
- When setting up a Postman workspace for a new project
- When re-syncing updated collections to an existing workspace

## When NOT to Use

- When the goal is to execute API tests — use Newman CLI instead (Postman MCP cannot reach localhost-hosted SUTs)
- When the collection files have not yet been verified locally — sync only verified collections
- When the user wants to manually manage resources in Postman App — this skill is for automated setup only

## Inputs

| Name               | Type       | Required | Description                                                                                                                    |
| ------------------ | ---------- | -------- | ------------------------------------------------------------------------------------------------------------------------------ |
| `workspace_name`   | `string`   | ✅       | Name of the Postman workspace to create or use. If a workspace with this name already exists, it will be reused — not replaced |
| `collection_files` | `string[]` | ✅       | Array of paths to `collection.json` files to import. Minimum 1 file                                                            |
| `environment_file` | `string`   | ✅       | Path to the shared environment JSON file to upload                                                                             |
| `create_mock`      | `boolean`  | ❌       | Whether to create a mock server from the first collection. Default: `false`                                                    |
| `create_monitor`   | `boolean`  | ❌       | Whether to create a monitor for the first collection. Default: `false`                                                         |

**Validation rules — reject and ask the user to correct before proceeding:**

- `workspace_name` must be non-empty
- `collection_files` must be a non-empty array; each path must exist on disk and be a valid Postman collection JSON (must have `info.schema` field matching Postman Collection Format v2.1)
- `environment_file` must exist on disk and be a valid Postman environment JSON (must have `values` array)
- If `create_mock = true` or `create_monitor = true`, `collection_files` must contain at least one collection

## Output

No files are written to disk. All outputs are Postman cloud resources.

| Resource      | Created when            | Description                            |
| ------------- | ----------------------- | -------------------------------------- |
| Workspace     | Always                  | Created or reused by `workspace_name`  |
| Collection(s) | Always                  | One per file in `collection_files`     |
| Environment   | Always                  | Uploaded from `environment_file`       |
| Mock server   | `create_mock = true`    | Created from the first collection      |
| Monitor       | `create_monitor = true` | Scheduled run for the first collection |

After the agent completes, this skill presents a **resource summary** containing direct URLs and IDs for all created resources — see the human gate step for the expected format.

## Core Principles

1. **Sync only, never execute.** The MCP remote server cannot reach localhost. This skill sets up cloud resources only. No test execution happens here.
2. **Reuse before create.** If a workspace with `workspace_name` already exists, use it. Do not create a duplicate workspace. Check for existing collections with the same name before importing — confirm with the user if a duplicate would be overwritten.
3. **Destructive actions require explicit confirmation.** Any action that would overwrite or delete an existing resource must be surfaced to the human before proceeding. The agent must not silently overwrite.
4. **Fail fast on validation.** Malformed collection or environment files cause MCP operations to fail mid-way. Validate file structure before invoking the agent.

## Setup Process

> Read [`references/postman-mcp-guide.md`](references/postman-mcp-guide.md) before starting Step 3.

### Step 1 — Input Validation

Validate all inputs per the rules above. For each collection file, verify it contains the required `info.schema` field. For the environment file, verify it contains a `values` array. Stop and ask the user to correct any invalid input before proceeding.

### Step 2 — Setup Plan Construction

Before invoking the agent, construct and present a setup plan to the user:

```
Setup plan:
- Workspace: "{workspace_name}" (create new / reuse existing)
- Collections to import: {N} files
    - {collection_file_1}
    - {collection_file_2}
- Environment: {environment_file}
- Mock server: Yes / No
- Monitor: Yes / No
```

Ask the user to confirm the plan before invoking the agent. This is a lightweight pre-gate to catch mistakes before any cloud resources are created.

### Step 3 — Invoke `postman-sync` Agent

Pass all validated inputs to the `postman-sync` agent. The agent will:

1. Find or create the workspace
2. Import each collection
3. Upload the environment
4. Create mock server if `create_mock = true`
5. Create monitor if `create_monitor = true`

The agent returns a resource dictionary containing URLs and IDs for all created resources.

If the agent reports an error at any step: surface the error to the user with the specific step that failed and stop. Do not retry automatically.

### Step 4 — Resource Summary Review

Verify the returned resource dictionary is complete — all expected resources have URLs/IDs. Run the **Setup Completeness Checklist** below.

### Step 5 — Human Gate

Present the resource summary to the user in this format:

```
Postman setup complete. Resources created:

Workspace:    {workspace_name}
              URL: https://www.postman.com/workspaces/{workspace_id}

Collections:
  - {collection_name_1}
    URL: https://www.postman.com/{workspace}/collections/{collection_id}
  - {collection_name_2}
    URL: https://www.postman.com/{workspace}/collections/{collection_id}

Environment:  {environment_name}
              ID: {environment_id}

Mock Server:  {mock_name} (if created)
              URL: https://{mock_id}.mock.pstmn.io

Monitor:      {monitor_name} (if created)
              ID: {monitor_id}
```

State clearly: **"Please open Postman App and verify each resource is correctly set up. Then run each collection using Collection Runner with the uploaded environment and data files. Capture screenshots of: workspace view, Collection Runner execution, environment variables panel, and any mock server or monitor dashboards."**

Do not close the task until the user explicitly confirms they have verified the resources.

## Setup Completeness Checklist

Run before presenting to the user. Every item must pass:

- [ ] Workspace URL or ID is present in the resource summary
- [ ] One collection entry per file in `collection_files` — none missing
- [ ] Environment ID is present
- [ ] If `create_mock = true`: mock server URL is present
- [ ] If `create_monitor = true`: monitor ID is present
- [ ] No resource entry has a null or empty URL/ID

## Anti-Patterns

- **Silently overwriting existing collections.** If a collection with the same name already exists in the workspace, stop and ask the user whether to overwrite or create a new one.
- **Creating a new workspace when one already exists.** Always search for `workspace_name` first. Duplicate workspaces cause confusion.
- **Invoking the agent without a pre-gate confirmation.** The setup plan must be presented and confirmed before the agent creates any cloud resources.
- **Attempting to run collections via MCP.** The remote Postman MCP server cannot reach localhost-hosted SUTs. Do not attempt to trigger collection runs through MCP.
- **Skipping validation of collection file structure.** An invalid collection JSON causes MCP import to fail silently or mid-way. Always validate before invoking.

## Best Practices

- Import all collections to the same workspace in a single agent invocation rather than separate invocations — reduces round trips and keeps the workspace setup atomic.
- When `create_mock = true`, the mock server is created from the first collection in `collection_files`. If the user wants a mock from a specific collection, note which one will be used in the setup plan.
- Mock servers on Postman cloud are useful for demonstrating API responses without a running SUT — appropriate for demo and documentation purposes.
- Monitors are scheduled runs and do not reach localhost. They are useful for demonstrating the monitor feature exists in the workspace, but will fail at execution time if the `baseUrl` is `localhost`. Note this limitation clearly in the human gate message.
- Reference [Postman MCP Server documentation](https://learning.postman.com/docs/reference/postman-api/postman-mcp-server/) for available MCP tools and their parameters.
- Reference [Postman Collection Format v2.1 specification](https://schema.getpostman.com/json/collection/v2.1.0/collection.json) for valid collection JSON structure.

## Process Quality Checklist

Verify before closing the task:

- [ ] All inputs validated — collection and environment files confirmed to be valid JSON with correct structure
- [ ] Setup plan was presented and user confirmed before agent invocation
- [ ] `postman-sync` agent was invoked with all required parameters
- [ ] No destructive action was taken without explicit user confirmation
- [ ] Setup Completeness Checklist passed with zero unchecked items
- [ ] Resource summary was presented with direct URLs for all created resources
- [ ] Monitor limitation (cannot reach localhost) was noted if monitor was created
- [ ] Human gate was presented and user confirmation explicitly requested

## Common Rationalizations to Reject

- _"A workspace with this name already exists — I'll create a new one with a slightly different name."_ → Ask the user. Do not decide autonomously whether to reuse or create a new workspace.
- _"The collection file is almost valid — I'll fix it before importing."_ → Do not modify collection files. Fix the validation error first, then re-import.
- _"The user didn't confirm the setup plan explicitly, but they said 'go ahead' earlier — that counts."_ → The setup plan confirmation must be explicit and specific to this invocation. Previous approvals do not carry over.
- _"Mock server creation failed but everything else worked — I'll just skip it."_ → Surface the failure to the user. Do not silently omit resources that were requested.
- _"I'll trigger a test run via MCP to verify the collection works."_ → Not possible and not the purpose of this skill. Collection execution on localhost requires Newman CLI, not Postman MCP.
