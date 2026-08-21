# AI-Driven API Test Generator Design

This document details the architectural design and pseudocode for an autonomous AI test generation system, implementing the workflow defined for the EShop API Testing project.

The system takes raw API specifications and security requirements as inputs and outputs a fully automated, data-driven Postman test suite that is executed via Newman, complete with bug reports.

## 1. System Architecture Diagram

The system employs a multi-agent, pipeline-based approach with explicit "Human Validation Gates" between critical stages to prevent AI hallucinations from cascading into the execution phase.

```mermaid
flowchart TD
    subgraph Inputs
        A1[Raw API Spec] --> |docs/sut/api-spec.md| B
        A2[Security Rules] --> |docs/sut/srs.md| B
    end

    subgraph Phase 1: Specification
        B(Skill: <code>api-contract</code>) -->|Infers business rules| C[API Contract <br> CONTRACT.md]
        C --> HG1{Human Gate:<br>Approve Contract}

        HG1 -- Approved --> D(Skill: <code>api-openapi</code>)
        D --> E[OpenAPI Spec <br> openapi.yaml]
        E --> HG2{Human Gate:<br>Approve Spec}
    end

    subgraph Phase 2: Test Case Design
        HG2 -- Approved --> F(Skill: <code>api-test-design</code>)
        C --> F

        F -.-> |Sub-routine| F1[<code>functional-test-design</code>]
        F1 -.-> |Domain, State, Error TCs| F

        F --> G[Test Cases <br> test-cases.md]
        G --> HG3{Human Gate:<br>Audit & Extend}
    end

    subgraph Phase 3: Automation & Execution
        HG3 -- Approved --> H(Skill: <code>api-collection</code>)
        C --> H
        H --> I[Postman Collection & Data Files]

        I --> J(Skill: <code>api-newman</code>)
        J --> |Executes Newman CLI| K[Newman Report & Results]
        K --> |Updates Actual Results| G
    end

    subgraph Phase 4: Reporting
        G --> HG4{Human Gate:<br>Verify FAILs}
        HG4 -- Approved --> L(Skill: <code>api-bug-report</code>)
        C --> L
        L --> M[Structured Bug Report]
    end

    style B fill:#4f81bd,stroke:#385d8a,stroke-width:2px,color:#fff
    style D fill:#4f81bd,stroke:#385d8a,stroke-width:2px,color:#fff
    style F fill:#4f81bd,stroke:#385d8a,stroke-width:2px,color:#fff
    style H fill:#4f81bd,stroke:#385d8a,stroke-width:2px,color:#fff
    style J fill:#4f81bd,stroke:#385d8a,stroke-width:2px,color:#fff
    style L fill:#4f81bd,stroke:#385d8a,stroke-width:2px,color:#fff
    style HG1 fill:#f79646,stroke:#b26b32,color:#fff
    style HG2 fill:#f79646,stroke:#b26b32,color:#fff
    style HG3 fill:#f79646,stroke:#b26b32,color:#fff
    style HG4 fill:#f79646,stroke:#b26b32,color:#fff
```

## 2. Agent Skills Pseudocode

The implementation is broken down into distinct skills (functions) executed by an LLM orchestration agent. Below is the conceptual pseudocode for the primary pipeline.

```python
class AITestGenerator:
    def __init__(self, llm_agent, human_reviewer):
        self.ai = llm_agent
        self.human = human_reviewer
        self.skills = {
            "api-contract": self.api_contract_skill,
            "api-openapi": self.api_openapi_skill,
            "api-test-design": self.api_test_design_skill,
            "api-collection": self.api_collection_skill,
            "api-newman": self.api_newman_skill,
            "api-bug-report": self.api_bug_report_skill
        }

    def generate_full_suite(self, api_endpoint, raw_spec, srs_doc):
        """Main pipeline orchestrator"""

        # Step 1: Formalize Contract
        contract = self.skills["api-contract"](api_endpoint, raw_spec, srs_doc)
        if not self.human.approve("Review inferred business & security rules", contract):
            raise Exception("Pipeline halted at Contract Approval")

        # Step 2: Generate OpenAPI Specification
        openapi_spec = self.skills["api-openapi"](contract)
        if not self.human.approve("Review OpenAPI Schema against Swagger UI", openapi_spec):
            raise Exception("Pipeline halted at OpenAPI Approval")

        # Step 3: Design Multi-Dimensional Test Cases
        test_cases = self.skills["api-test-design"](contract, openapi_spec)

        # Human Audit: Mark VALID/INVALID, extend with missing edge cases
        audited_test_cases = self.human.audit_and_extend(test_cases)

        # Step 4: Automate - Build Postman Collection
        collection, data_files = self.skills["api-collection"](audited_test_cases, contract)
        if not self.human.approve("Review Postman variables and setup/teardown logic", collection):
            raise Exception("Pipeline halted at Collection Approval")

        # Step 5: Execute via Newman
        executed_test_cases = self.skills["api-newman"](collection, data_files, audited_test_cases)

        # Human Gate: Confirm which failures are real SUT bugs vs Script errors
        verified_test_cases = self.human.verify_failures(executed_test_cases)

        # Step 6: Generate Bug Report
        bug_report = self.skills["api-bug-report"](verified_test_cases, contract)
        return bug_report

    # ----------------- SKILL DEFINITIONS -----------------

    def api_contract_skill(self, endpoint, raw_spec, srs_doc):
        prompt = f"""
        Act as a Senior QA Architect. Read {raw_spec} and {srs_doc}.
        Extract and infer implicit business rules for an e-commerce system for {endpoint}.
        Output a CONTRACT.md containing:
        - HTTP Request/Response signatures
        - Field constraints (type, length, boundaries)
        - Security requirements (SEC-01 to SEC-07 mapping)
        - Expected state transitions
        """
        return self.ai.generate(prompt)

    def api_test_design_skill(self, contract, openapi_spec):
        """Uses sub-routines to generate combinations systematically"""

        # Sub-routine: functional-test-design (Black-box techniques)
        functional_tcs = self.ai.invoke_skill("functional-test-design", {
            "inputs": contract,
            "techniques": ["Equivalence Partitioning", "Boundary Value Analysis", "Error Guessing"]
        })

        # Sub-routine: security-test-design
        security_tcs = self.ai.generate_security_vectors(contract.security_rules)

        # Format into unified test-cases.md table
        return self.ai.format_as_markdown_table([functional_tcs, security_tcs])

    def api_collection_skill(self, test_cases, contract):
        prompt = f"""
        Read the approved {test_cases} and {contract}.
        Generate a Postman Collection JSON v2.1.0.
        Rule 1: Use pm.environment.get() for dynamic data-driven variables.
        Rule 2: Inject X-Student-Id header in the Collection-level Pre-request script.
        Rule 3: Generate setup/teardown logic using pm.sendRequest() (wrapped in success conditionals).
        Output the JSON and a data-domain.csv for data-driven testing.
        """
        return self.ai.execute(prompt)

    def api_newman_skill(self, collection, data_files, test_cases):
        # 1. Execute system CLI tool
        sys.run(f"newman run {collection} -d {data_files} --reporters json,htmlextra")

        # 2. Parse results via AI or Python script
        results_json = sys.read("newman-summary.json")

        prompt = f"""
        Read {results_json}. Find failed assertions.
        Update the 'Status' and 'Actual Result' columns in {test_cases}.
        Do not output raw JSON in Actual Result; summarize the failure in English.
        """
        return self.ai.generate(prompt)

    def api_bug_report_skill(self, executed_test_cases, contract):
        prompt = f"""
        Filter {executed_test_cases} for Status == FAIL.
        Cross-reference expected behavior with {contract}.
        Group failures sharing the same root cause into single bug entries.
        Assign severity/priority.
        Output a structured Bug Report Markdown file ready for GitHub Issues.
        """
        return self.ai.generate(prompt)
```

## 3. Design Philosophy

This test generator architecture explicitly rejects the single-prompt ("zero-shot") approach. Building a reliable API test suite requires high context retention and deterministic intermediate artifacts.

By chaining specific AI Agent Skills (Contract → OpenAPI → TC Design → Collection Gen), the system:

1. **Reduces Hallucinations:** The AI builds the test suite from a rigidly defined `CONTRACT.md` rather than loosely interpreting the raw SUT specs.
2. **Enables Human Steering:** The `Human Gates` ensure that edge cases missed by the AI can be injected at the design phase (`test-cases.md`) before automated execution begins.
3. **Optimizes Token Context:** Each skill operates with only the context it needs (e.g., `api-bug-report` only reads failures and the contract, ignoring execution logs of passing tests).
