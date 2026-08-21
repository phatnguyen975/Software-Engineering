# Newman Summary Parsing Guide — `api-newman`

This guide defines how to parse `newman-summary.json` and map results to TC IDs in `test-cases.md`.

**Source:** [Newman CLI documentation — `--export-summary`](https://learning.postman.com/docs/collections/using-newman-cli/command-line-integration-with-newman/#newman-run-options), [Newman GitHub — Summary format](https://github.com/postmanlabs/newman)

## `newman-summary.json` Structure

Newman's `--export-summary` flag produces a JSON file with this top-level structure:

```json
{
  "collection": { "info": { "name": "..." } },
  "environment": { "values": [...] },
  "run": {
    "stats": {
      "requests": { "total": N, "failed": F },
      "assertions": { "total": A, "failed": AF }
    },
    "timings": { "started": "...", "completed": "...", "responseAverage": N },
    "executions": [
      {
        "item": {
          "name": "TC-FR01-FR-001 — Register account with valid inputs",
          "id": "..."
        },
        "response": {
          "status": "OK",
          "code": 200,
          "responseTime": 45,
          "stream": { "data": [...] }
        },
        "assertions": [
          {
            "assertion": "Status is 200",
            "skipped": false,
            "error": null
          },
          {
            "assertion": "Response contains id",
            "skipped": false,
            "error": {
              "name": "AssertionError",
              "message": "expected response to have property 'id'"
            }
          }
        ]
      }
    ],
    "failures": [
      {
        "error": { "message": "..." },
        "source": { "name": "TC-FR01-FR-001 — ...", "type": "assertion" }
      }
    ]
  }
}
```

## TC ID Extraction from Request Name and Router Architecture Support

With the introduction of the Iteration Router Architecture, execution items fall into three categories:

1. **Static Requests:** `item.name` follows the convention: `TC-{feature_id}-{CATEGORY}-{number} — {TC Title}`. **Extraction rule:** Split on ` — `. The first segment is the TC ID.
2. **Data-Driven Template:** `item.name` is exactly `"Data-Driven Template"`. In this case, the TC ID is dynamically generated in the assertions. **Extraction rule:** Look at the first assertion's `assertion` name (e.g., `"TC-FR01-FR-021 — Status code is 200"`). Split on ` — `. The first segment is the TC ID.
3. **Router Dummy Request:** `item.name` is `"[Control] Iteration Router"`. **Extraction rule:** Ignore this request entirely. Do not flag it as unmapped, just skip it.

If `item.name` does not match any of the above patterns: flag as unmapped. Do not attempt to infer the TC ID.

## Pass/Fail Determination per Execution

An execution is **PASS** if:

- All assertions in `assertions[]` have `error: null` AND `skipped: false`

An execution is **FAIL** if:

- At least one assertion has a non-null `error` object
- OR `response` is null (request could not be sent — connection error)

An execution is **SKIP** if:

- The item was not present in `executions[]` at all (collection ran but this request was not reached)
- OR all assertions have `skipped: true`

## Actual Result Construction

For each execution, construct the Actual Result string as follows:

**For PASS:**

```
PASS — HTTP {code} {status}
```

Example: `PASS — HTTP 200 OK`

**For FAIL:**

```
FAIL — HTTP {code} {status} | Failed assertions: {assertion names joined by "; "}
Response body: {first 200 chars of response body}
```

Example:

```
FAIL — HTTP 200 OK | Failed assertions: "Response contains id"; "Response schema is valid"
Response body: {"message":"User registered successfully"}
```

**For connection error (response is null):**

```
FAIL — No response received | Error: {error message from Newman}
```

**For SKIP:**

```
SKIPPED — Request not executed (reason: {reason if determinable, else "unknown"})
```

## Data-Driven Run Aggregation

When `--iteration-data` is used, Newman executes each request multiple times (once per data row). In `executions[]`, the same request name appears multiple times with different iteration indices.

**Aggregation rule:**

- If ALL iterations for a TC pass → TC Status = PASS, Actual Result = `PASS — All {N} iterations passed`
- If ANY iteration fails → TC Status = FAIL, Actual Result = `FAIL — {M} of {N} iterations failed | Iteration {i}: HTTP {code} | Failed: {assertions}`
- List the failing iterations explicitly, not just the count

## Mapping Algorithm

```
1. Build a map: { "TC-FR01-FR-001": execution_result, ... }
   - For each item in run.executions:
       - Extract TC ID from item.name using the extraction rule above
       - If extraction fails: add to unmapped_list
       - If TC ID already in map (data-driven repeat): aggregate per rules above

2. For each TC row in tc_file:
   - If TC ID found in map: write Actual Result and Status
   - If TC ID not in map: write Status = SKIP, Actual Result = "SKIPPED — Not executed"

3. Report unmapped_list to human gate
```

## Response Body Extraction

Newman stores response body as a byte stream in `response.stream.data` (array of byte values). To extract the body:

```javascript
// Convert byte array to string
const bytes = execution.response.stream.data;
const body = Buffer.from(bytes).toString("utf8");

// Truncate to first 300 characters for Actual Result column
const excerpt = body.length > 300 ? body.substring(0, 300) + "..." : body;
```

If the body is valid JSON, pretty-print the first level only (do not expand nested objects beyond one level) to keep the Actual Result column readable.

## Common Parsing Errors

| Error                                                        | Cause                                                                                  | Resolution                                                    |
| ------------------------------------------------------------ | -------------------------------------------------------------------------------------- | ------------------------------------------------------------- |
| `executions` array is empty                                  | Newman failed before running any request (invalid collection JSON, unreachable server) | Report as execution error — do not write results to `tc_file` |
| Request name has no `—` separator                            | Collection was not generated following TC naming convention                            | Flag as unmapped, report to user                              |
| `response` is null                                           | Connection error or timeout                                                            | Mark TC as FAIL with "No response received"                   |
| Same TC ID appears in `failures[]` but not in `executions[]` | Newman internal inconsistency                                                          | Use `executions[]` as the authoritative source                |
