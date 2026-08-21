# Postman Scripting Guide

Patterns for Pre-request Scripts and Test Scripts used in generated collections.

> **Source:** [Postman Sandbox API Reference](https://learning.postman.com/docs/writing-scripts/script-references/postman-sandbox-api-reference/), [Postman Learning Center — Writing Scripts](https://learning.postman.com/docs/writing-scripts/intro-to-scripts/)

## Section 1 — Collection-Level Pre-request Script

This script runs before every request in the collection.

### Pattern: Custom Header Injection + Auth Token Management

```javascript
// ── 1. Custom header injection ──────────────────────────────────────
const customHeaderName = pm.environment.get("injectHeaderName");
const customHeaderValue = pm.environment.get("injectHeaderValue");
if (customHeaderName && customHeaderValue) {
  pm.request.headers.add({
    key: customHeaderName,
    value: customHeaderValue,
  });
}

// ── 2. Auth token management ─────────────────────────────────────────
// Only runs for endpoints that require auth.
// Remove this block entirely for public endpoints.
const token = pm.environment.get("userToken"); // or "adminToken" for admin endpoints
if (!token) {
  const loginUrl = pm.environment.get("baseUrl") + "/api/login";
  const loginBody = {
    email: pm.environment.get("userEmail"),
    password: pm.environment.get("userPassword"),
  };

  pm.sendRequest(
    {
      url: loginUrl,
      method: "POST",
      header: { "Content-Type": "application/json" },
      body: { mode: "raw", raw: JSON.stringify(loginBody) },
    },
    function (err, res) {
      if (!err && res.code === 200) {
        pm.environment.set("userToken", res.json().token);
      }
    },
  );
}
```

**Notes:**

- Use `pm.environment.get()` in scripts — `{{variable}}` syntax is not interpreted in script context.
- For admin endpoints, replace `userToken`/`userEmail`/`userPassword` with `adminToken`/`adminEmail`/`adminPassword`.
- If the endpoint requires no auth, remove Section 2 entirely.

## Section 2 — Folder-Level Setup/Teardown Scripts

### Pattern: Setup — Create Prerequisite Data

Use in Folder Pre-request Script when TC group requires existing data (e.g., a product must exist before adding to cart).

```javascript
// ── Folder Pre-request: Create prerequisite product ──────────────────
const createUrl = pm.environment.get("baseUrl") + "/api/products";
const adminToken = pm.environment.get("adminToken");

pm.sendRequest(
  {
    url: createUrl,
    method: "POST",
    header: {
      "Content-Type": "application/json",
      Authorization: "Bearer " + adminToken,
    },
    body: {
      mode: "raw",
      raw: JSON.stringify({
        name: "Test Product",
        price: 100000,
        description: "Prerequisite for cart tests",
        imageUrl: "",
        category_id: 1,
      }),
    },
  },
  function (err, res) {
    if (!err && res.code === 200) {
      // Store the created resource's ID for use in TC requests
      pm.environment.set("prerequisiteProductId", res.json().id);
    }
  },
);
```

### Pattern: Teardown — Delete Prerequisite Data

Use in Folder Test Script after TC group completes. Only call endpoints documented in the contract.

```javascript
// ── Folder Test Script: Teardown — delete prerequisite product ───────
const productId = pm.environment.get("prerequisiteProductId");
if (productId) {
  const deleteUrl =
    pm.environment.get("baseUrl") + "/api/products/" + productId;
  const adminToken = pm.environment.get("adminToken");

  pm.sendRequest(
    {
      url: deleteUrl,
      method: "DELETE",
      header: { Authorization: "Bearer " + adminToken },
    },
    function (err, res) {
      // Unset the variable regardless of delete outcome
      pm.environment.unset("prerequisiteProductId");
    },
  );
}
```

**Rules:**

- Always call `pm.environment.unset()` for every variable set during setup — even if the teardown request fails.
- If no delete endpoint is documented in the contract, skip the teardown request. Still unset environment variables.
- Never add teardown requests as visible collection items — always use Folder Test Script.

## Section 3 — Request-Level Test Script Patterns

### Pattern: Basic Status + Body Assertion

```javascript
pm.test("Status is 200", function () {
  pm.response.to.have.status(200);
});

pm.test("Response contains id", function () {
  const body = pm.response.json();
  pm.expect(body).to.have.property("id");
  pm.expect(body.id).to.be.a("number");
  pm.expect(body.id).to.be.above(0);
});

pm.test("Response contains message", function () {
  const body = pm.response.json();
  pm.expect(body.message).to.be.a("string");
});
```

### Pattern: Error Response Assertion

```javascript
pm.test("Status is 400", function () {
  pm.response.to.have.status(400);
});

pm.test("Response has error field", function () {
  const body = pm.response.json();
  pm.expect(body).to.have.property("error");
  pm.expect(body.error).to.be.a("string");
  pm.expect(body.error.length).to.be.above(0);
});
```

### Pattern: Schema Validation (TC-SCH)

```javascript
pm.test("Response schema is valid", function () {
  const schema = {
    type: "object",
    required: ["message", "id"],
    properties: {
      message: { type: "string" },
      id: { type: "number" },
    },
    additionalProperties: false,
  };
  pm.response.to.have.jsonSchema(schema);
});

pm.test("Content-Type is application/json", function () {
  pm.response.to.have.header("Content-Type");
  pm.expect(pm.response.headers.get("Content-Type")).to.include(
    "application/json",
  );
});
```

### Pattern: Chained Request — Extract and Store ID

Use in the Test Script of a request whose response provides data needed by subsequent requests.

```javascript
pm.test("Status is 200", function () {
  pm.response.to.have.status(200);
});

// Store created resource ID for subsequent TC requests
const body = pm.response.json();
if (body && body.id) {
  pm.environment.set("createdCouponId", body.id);
}
```

### Pattern: Auth Assertion (TC-SEC — Auth Bypass)

```javascript
pm.test("Status is 401 for unauthenticated request", function () {
  pm.response.to.have.status(401);
});

pm.test("Response does not leak internal details", function () {
  const body = pm.response.json();
  pm.expect(body).to.not.have.property("stack");
  pm.expect(body).to.not.have.property("query");
  pm.expect(JSON.stringify(body)).to.not.include("SELECT");
  pm.expect(JSON.stringify(body)).to.not.include("syntax error");
});
```

### Pattern: Rate Limit Check (TC-RL)

```javascript
// Run after the N+1th request that should be rejected
pm.test("Status is 429 after rate limit exceeded", function () {
  pm.response.to.have.status(429);
});

pm.test("Retry-After header is present", function () {
  pm.response.to.have.header("Retry-After");
});
```

## Section 4 — Data-Driven Request Pattern

For TCs marked `Data-driven: Yes`, place them in the `Data-Driven Template` request. Replace literal values with `{{variable}}` placeholders:

```json
{
  "body": {
    "mode": "raw",
    "raw": "{
      \"name\": \"{{name}}\",
      \"email\": \"{{email}}\",
      \"password\": \"{{password}}\"
    }"
  }
}
```

Corresponding Test Script using data file variables (`tc_id` and `expected_status` must be present in the CSV):

```javascript
const tc_id = pm.iterationData.get("tc_id");
const expectedStatus = parseInt(pm.iterationData.get("expected_status"), 10);
const expectedMessage = pm.iterationData.get("expected_message");

pm.test(
  tc_id + " — Status matches expected (" + expectedStatus + ")",
  function () {
    pm.response.to.have.status(expectedStatus);
  },
);

if (expectedMessage) {
  pm.test(tc_id + " — Response message matches", function () {
    const body = pm.response.json();
    pm.expect(body.message || body.error).to.equal(expectedMessage);
  });
}
```
