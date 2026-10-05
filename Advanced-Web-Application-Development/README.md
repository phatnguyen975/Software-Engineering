<div align="center">
  <h1>Advanced Web Application Development</h1>
  <sub>October 05, 2026</sub>
</div>

## Course Description

This graduation course of the Software Engineering major provides advanced knowledge of building and operating a multi-component web application: specifying before implementing, designing and evolving the data schema, API contracts, security and authorization, automated quality gates, packaging and releasing with the ability to roll back, and observability and incident handling.

The course starts where Web Application Development ends: students can already build a single-page application with authentication, so React basics, REST basics and JWT basics are not taught again here.

In parallel, the course trains the way software engineers work today: specify first, delegate to an agent, verify with automated guardrails, and keep the decision at the points that cannot be undone. Students also learn to put an LLM inside the product itself (streaming, tool calling, RAG) and to handle the hardest part of such features: non-determinism, cost, latency, and the consequences of a wrong answer.

## Course Plan

| Week | Topic                                                                                                                        |
| :--: | :--------------------------------------------------------------------------------------------------------------------------- |
|  1   | Introduction. From vibe coding to vibe engineering. The course's AI-use policy                                               |
|  2   | Specifications for agents: from user story to an executable spec; where specs are always incomplete                          |
|  3   | Harness and working environment: convention files, tools, MCP, least privilege, per-environment configuration                |
|  4   | Data: modelling, indexes, transactions, zero-downtime migration, backup and restore                                          |
|  5   | API contracts: unified error model, pagination, idempotency, versioning                                                      |
|  6   | Security: owner-based authorization and RBAC, secrets management, supply chain, prompt injection                             |
|  7   | Quality gates: the testing pyramid, contract tests, Playwright, CI. AI reviewing AI                                          |
|  8   | Packaging and release: Docker, per-environment configuration, migration vs. deploy ordering, canary, rollback, feature flags |
|  9   | Putting an LLM into the product: calling models, streaming, tool calling, RAG                                                |
|  10  | Non-determinism: evals, guardrails, human gates, cost and latency                                                            |
|  11  | Operations: logs, metrics, traces, alerting, SLOs, incidents and post-mortems, queues, retries, rate limiting, cost          |
