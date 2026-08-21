# Postman MCP Guide — `api-postman`

Reference guide for working with the Postman MCP remote server via the `postman-sync` agent.

**Source:** [Postman MCP Server documentation](https://learning.postman.com/docs/reference/postman-api/postman-mcp-server/), [Postman API documentation](https://www.postman.com/postman/postman-public-workspace/documentation/190fy4p/postman-api)

## MCP Server Configuration

| Property       | Value                                                                                    |
| -------------- | ---------------------------------------------------------------------------------------- |
| Server type    | Remote HTTP                                                                              |
| Endpoint       | `https://mcp.postman.com/mcp`                                                            |
| Mode           | Full (`/mcp`) — provides all workspace, collection, environment, mock, and monitor tools |
| Authentication | OAuth (US server) — browser-based consent flow on first connection                       |
| Network reach  | Postman cloud only — **cannot reach localhost or private networks**                      |

**Connection note:** The remote US server uses OAuth for authentication. On first use, the MCP client opens a browser window for OAuth consent. Subsequent connections reuse the OAuth session until it expires.

## Available MCP Operations (via `postman-sync` agent)

### Workspace Operations

**Find workspace by name:**

- Tool: `get-workspaces` or equivalent list tool
- Use to check if `workspace_name` already exists before creating
- Returns: array of workspace objects with `id`, `name`, `type`

**Create workspace:**

- Tool: `create-workspace`
- Parameters: `name`, `type` (use `"personal"` for individual use, `"team"` for shared)
- Returns: workspace `id` and URL

### Collection Operations

**Import collection:**

- Tool: `create-collection` or `import-collection`
- Parameters: workspace `id`, collection JSON content
- The collection JSON must conform to [Postman Collection Format v2.1](https://schema.getpostman.com/json/collection/v2.1.0/collection.json)
- Returns: collection `id`, `uid`, and URL

**Check for existing collection by name:**

- Tool: `get-collections` filtered by workspace
- Use before importing to detect name conflicts

### Environment Operations

**Create environment:**

- Tool: `create-environment`
- Parameters: workspace `id`, environment name, variables array
- Variables format: `[{ "key": "...", "value": "...", "enabled": true }]`
- Returns: environment `id` and URL

### Mock Server Operations

**Create mock server:**

- Tool: `create-mock`
- Parameters: collection `id`, workspace `id`, name
- Returns: mock server `id`, `mockUrl` (e.g., `https://{mock_id}.mock.pstmn.io`)
- **Limitation:** Mock server will return 404 for any request not matching a saved example in the collection. Ensure the collection has at least one saved example, or note this limitation to the user.

**Mock server reach limitation:**

- The mock server URL is publicly accessible
- Mock servers respond to requests based on saved examples in the collection — they do not proxy to the SUT
- Mock servers do NOT connect to localhost — they are cloud-hosted

### Monitor Operations

**Create monitor:**

- Tool: `create-monitor`
- Parameters: collection `id`, environment `id`, workspace `id`, name, schedule
- Schedule format: cron expression (e.g., `"0 * * * *"` for hourly)
- Returns: monitor `id` and schedule

**Monitor reach limitation:**

- Monitors run on Postman cloud infrastructure
- Monitors **cannot reach localhost or private network SUTs**
- If `baseUrl` in the environment is `localhost:*`, the monitor will fail at execution time
- Always inform the user of this limitation when `create_monitor = true` and `baseUrl` is localhost

## Setup Sequence (Performed by `postman-sync` Agent)

```
1. Find workspace
   → GET workspaces → filter by workspace_name
   → If found: use existing workspace_id
   → If not found: CREATE workspace → get new workspace_id

2. For each collection_file:
   → Check for name conflict in workspace
     → If conflict: PAUSE and report to api-postman skill (do not overwrite silently)
   → Import collection → get collection_id

3. Create environment
   → Read environment_file
   → CREATE environment in workspace → get environment_id

4. If create_mock = true:
   → CREATE mock server from first collection → get mock_url

5. If create_monitor = true:
   → CREATE monitor for first collection + environment → get monitor_id
   → NOTE: monitor will not function correctly if baseUrl is localhost

6. Return resource dictionary:
   {
     "workspace": { "name": "...", "id": "...", "url": "..." },
     "collections": [ { "name": "...", "id": "...", "url": "..." } ],
     "environment": { "name": "...", "id": "..." },
     "mock": { "name": "...", "url": "..." },       // null if not created
     "monitor": { "name": "...", "id": "..." }       // null if not created
   }
```

## Collection File Validation

Before invoking the agent, verify each collection file has this minimum structure:

```json
{
  "info": {
    "name": "string",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": []
}
```

Reject and report if:

- `info` object is missing
- `info.schema` does not contain `v2.1.0`
- `item` array is missing (even if empty, it must be present)

## Environment File Validation

Before invoking the agent, verify the environment file has this minimum structure:

```json
{
  "name": "string",
  "values": [{ "key": "string", "value": "string", "enabled": true }]
}
```

Reject and report if:

- `values` array is missing or not an array
- Any value entry is missing `key` or `enabled`

## Error Handling

| Error scenario                                  | Action                                                                                   |
| ----------------------------------------------- | ---------------------------------------------------------------------------------------- |
| Workspace creation fails (name conflict policy) | Surface to user — ask whether to reuse existing or choose a different name               |
| Collection import fails (invalid JSON)          | Surface to user — do not attempt to fix the collection                                   |
| Collection name conflict in workspace           | Surface to user — ask whether to overwrite or import as a new collection                 |
| Environment creation fails                      | Surface to user — report the specific error from MCP                                     |
| Mock creation fails                             | Surface to user — note that mock creation is optional and can be done manually           |
| Monitor creation fails                          | Surface to user — note limitation and suggest manual setup if needed                     |
| OAuth session expired                           | Inform user that re-authentication is needed — the MCP client will open a browser window |
