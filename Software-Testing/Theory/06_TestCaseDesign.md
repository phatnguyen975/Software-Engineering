<div align="center">
  <h1>Test Case Design</h1>
  <sub>June 04, 2026</sub>
</div>

## 1. Core Concepts & Role of Test Cases

### 1.1. What is a Test Case?

In software engineering, a test case is the foundational unit of test execution. At its core, it is a scientifically designed experiment to verify a specific behavior of the software.

According to the standard defined by IEEE 729-1983, a test case is "a specific set of test data and associated procedures developed for a particular objective." Furthermore, industry experts like Kaner, Faulk, and Nguyen emphasize that an ideal test case executes a single, well-defined test objective.

In practical application, a test case acts as a precise contract. It documents the exact preconditions, inputs, execution steps, and the expected outcomes required to validate a feature. Whether you are testing a complex user interface or validating backend logic in a Go or Java application, the test case ensures that the verification process is systematic, objective, and independent of the person executing it.

### 1.2. The Seven Core Purposes of Writing Test Cases

Creating test cases requires investment, but it provides a massive return in software quality and team alignment. The primary reasons for writing test cases include:

- **Accountability:** Test cases provide a clear, documented trail of what was tested, who tested it, and what the results were. This transparency is crucial for stakeholder sign-off and ensures that quality assurance is a measurable metric rather than a vague promise.
- **Reproducibility:** A well-written test case guarantees that a test can be executed multiple times with the exact same setup and steps, yielding the exact same results. For instance, if a bug is discovered in a pure Java codebase tracked strictly via Git without automated build managers, a precise test case allows any developer to check out the exact branch, input the specific test data, and reproduce the anomaly flawlessly.
- **Tracking:** Test cases form the backbone of testing progress. By mapping test cases to specific features or requirements, project managers can track the health of a release. It answers the critical question: "How much of the system has been validated?"
- **Automation:** Manual test cases are the blueprints for automated scripts. Before writing an automated test, the logical flow, test data, and assertions must be mapped out. Structured manual test cases are directly translated into automated test frameworks, significantly reducing regression testing time.
- **To Find Bugs:** The proactive process of writing test cases—thinking through edge cases, invalid inputs, and boundary conditions—often uncovers logical flaws even before the code is executed. During execution, test cases systematically stress the application to expose defects.
- **To Verify Execution Correctness:** Test cases ensure that the software does exactly what the business requirements state it should do. They prevent "feature drift" by validating the software against the initial specifications rather than just verifying that the code compiles and runs without crashing.
- **To Measure Test Coverage:** By writing test cases against all accepted requirements, teams can calculate test coverage. This metric reveals blind spots in the testing strategy, highlighting areas of the application that are vulnerable and untested.

### 1.3. The Communication Value of Test Cases

Beyond the technical execution, test cases serve as a unified language across the development team. Product Owners use them to confirm that their business rules are understood. Developers use them as a checklist during the implementation phase to ensure their code meets the acceptance criteria before pushing to the repository. By reading the test cases before coding begins, developers gain a clear understanding of the exact scenarios their code must handle, which drastically reduces the defect rate in the first iteration.

### 1.4. The Role of AI in Test Case Management

The landscape of software testing is rapidly evolving with the integration of AI. AI significantly enhances the conceptualization and management of test cases:

- **Automated Generation:** Modern AI models can ingest requirements, user stories, or even raw code snippets, and automatically generate comprehensive test case scenarios. They excel at identifying edge cases and negative testing scenarios that a human tester might initially overlook.
- **Test Case Optimization:** AI tools can analyze large repositories of existing test cases to identify redundancies and duplicates. By consolidating overlapping tests, AI helps maintain an economical and efficient test suite.
- **Self-Healing Automation:** When UI elements or API structures change, AI-driven automation frameworks can dynamically update the test scripts to adapt to the new structure, reducing the maintenance burden of automated test cases.
- **Predictive Analytics:** By analyzing historical bug data and test execution results, AI can predict which modules of the software are most likely to fail. This allows QA teams to prioritize their test case execution, focusing on high-risk areas during tight release cycles.

## 2. Core Structure of a Test Case

### 2.1. The Anatomy of a Standard Test Case

To ensure accountability, reproducibility, and clarity, every test case must contain a specific set of essential fields. These fields provide the tracking information and execution guidelines necessary for any tester or automated script to perform the validation.

- **Test Case ID:** A unique identifier used for tracking, mapping to requirements, and logging bugs.
- **Test Case Objective/Title:** A concise summary of what the test case is verifying.
- **Test Case Description:** Additional context or background information about the test scenario.
- **Pre-conditions:** The exact state the system must be in before the test begins (e.g., "User must be logged in with admin privileges").
- **Steps:** A sequential, numbered list of actions the tester must perform.
- **Test Data:** The specific inputs, files, or database states required to execute the steps. This includes input values, expected outputs, and default system states.
- **Expected Results:** The precise, anticipated behavior of the system after executing the steps.
- **Observed Results:** The actual behavior of the system recorded during execution (filled out during the testing phase).
- **Status:** The outcome of the test execution (Pass, Fail, Blocked, or Skipped).
- **Test Environment:** The hardware, software, browser, operating system, and network configuration used during the test.
- **Bug ID:** If the test fails, this links to the corresponding defect tracking ticket.
- **Comments/Notes:** Any additional observations, workarounds, or execution anomalies.

### 2.2. Mastering the Test Case Objective/Title

The Objective or Title is widely considered the most important essential field of a test case. In many fast-paced agile environments, the title might be the only part of the documented test case that managers, developers, or other QA members read during a review.

A good test name makes peer reviews significantly easier and facilitates a smoother handover to other testers or the automation team. To maintain consistency and immediate readability, industry best practices dictate a strict syntax for writing test case titles:

**Syntax:** `Action + Function + Operating Condition`

- **Action:** The verb describing the intent (e.g., Verify, Test, Validate, Execute, Run, Print).
- **Function:** The specific feature, component, or validation point being tested.
- **Operating Condition:** The specific data state, environment, or condition under which the function is tested.

**Examples of the Syntax in Practice:**

- _Run_ (Action) + _annual report_ (Function) + _from standard data file location_ (Condition).
- _Validate_ (Action) + _user login_ (Function) + _with valid credentials and active session_ (Condition).
- _Verify_ (Action) + _checkout process_ (Function) + _when the shopping cart is empty_ (Condition).

### 2.3. Defining Clear Validation Points

A validation point is the core expected result mapped to a specific action. It defines clearly what behavior, result, or state you are attempting to validate at a given moment.

Instead of waiting until the very last step to verify the system, complex test cases should include validation points embedded within the execution steps. For example, if step 3 is "Upload the profile picture," a validation point should immediately follow: "Verify that a success message is displayed and the thumbnail updates immediately." This prevents false positives and pinpoints exactly where a multi-step process breaks down.

### 2.4. Best Practices for Steps and Test Data Management

Writing steps requires balancing detail with efficiency. Steps should be economical, meaning there are no unnecessary actions. However, they must remain repeatable and self-standing. A tester should not need to guess how to navigate to a screen or what data to input.

Test data must be managed carefully. Hardcoding dynamic values (like dates or one-time use tokens) inside test steps leads to brittle test cases. Instead, reference test data abstractly or clearly define how to generate it in the pre-conditions. Furthermore, a highly professional test case always includes a "Teardown" or "Self-cleaning" step to revert the database or system state back to its original form, ensuring it does not block subsequent test cases.

### 2.5. The Integration of AI in Test Case Structuring

AI is heavily utilized in structuring and maintaining the essential components of test cases:

- **Syntax Enforcement and Title Generation:** Natural Language Processing (NLP) models are integrated into test management tools to automatically review test case titles. They can flag titles that do not follow the `Action + Function + Operating Condition` syntax and suggest auto-corrected versions based on the test steps provided.
- **Synthetic Data Generation:** One of the most time-consuming aspects of test preparation is creating valid, secure test data. AI algorithms can instantly generate large sets of synthetic test data (names, formatted emails, valid financial records) that mimic production data without violating privacy regulations.
- **Step Optimization:** AI tools can analyze manual test steps and identify ambiguous language or missing pre-conditions. For instance, if a step says "Click submit," an AI assistant can prompt the QA engineer: "Consider specifying which form is being submitted and adding a validation point for the immediate UI response."

## 3. Test Case Template & Quality Standards

### 3.1. The Standard Test Case Template

A well-structured test case template ensures consistency across the QA team and makes the documentation process highly systematic. While modern agile teams often use test management software (like Jira, Zephyr, or TestRail) rather than static spreadsheets, the underlying data structure remains identical.

A standard template typically encompasses the following columns:

- **TC ID:** A unique alphanumeric identifier (e.g., `TC_LOGIN_001`).
- **Description / Objective:** A very clear and specific statement detailing exactly what behavior the program is supposed to exhibit.
- **Pre-condition:** The exact setup required before the test can commence. You need to pre-determine the state of the system, necessary data, or required configurations.
- **Steps:** The explicit, numbered actions the tester must perform (e.g., Action 1, Action 2).
- **Expected Result:** The exact outcome that validates the test objective. This defines what you expect to get when executing the test case.
- **Observed Result:** Left blank during the writing phase, this is filled out during execution to record what actually happened if it differs from the expected result.
- **Status:** The final verdict of the execution (Passed / Failed / Blocked / Skipped).

### 3.2. The Seven Characteristics of a Good Test Case

Writing a test case is easy; writing a _good_ test case requires discipline. A high-quality test case must strictly adhere to the following seven criteria:

- **Accurate:** The test must test exactly what it was designed to test, and nothing else. If the objective is to validate a password masking feature, the test case should not simultaneously attempt to validate database connection timeouts. Keep the focus sharp to avoid false negatives.
- **Economical:** An economical test case contains no unnecessary steps. Testers should get straight to the point. If a user needs to be logged in to test a shopping cart feature, do not write out the ten steps of the login process. Instead, abstract the login process into the Pre-conditions (e.g., "User is logged in and on the checkout page"). This saves execution time and reduces maintenance overhead.
- **Repeatable and Reusable:** The test case must keep going on through multiple release cycles and yield consistent results. A repeatable test does not rely on hardcoded dates (e.g., "Select May 14th, 2026") that will expire next week. Instead, it uses dynamic references (e.g., "Select the current system date + 1 day").
- **Traceable:** Every valid test case must map directly back to a specific business requirement, user story, or technical specification. This traceability ensures comprehensive test coverage and proves to stakeholders that every requested feature has been validated.
- **Appropriate:** The test must be appropriate for the specific test environment. Environmental context is critical. For instance, if you are validating terminal-based applications or compiling backend Go services, the test case must explicitly dictate the operating condition, such as ensuring the execution takes place within a native Ubuntu environment rather than a default Windows host shell, to prevent false command recognition errors.
- **Self-standing:** A test case must be completely independent of its writer. A newly onboarded QA engineer should be able to pick up the document and execute it flawlessly without needing to tap the original author on the shoulder for clarification. For example, if a test relies on a pure Java repository that does not use build tools like Maven or Gradle but is tracked strictly via Git, the test case must explicitly state the exact `javac` compilation commands and the specific branch to pull. It cannot assume the tester "just knows" how to build the project.
- **Self-cleaning:** A professional test case picks up after itself. If a test case creates a new user account, uploads a mock file, or alters a database configuration, the final steps (often called the "Teardown") must instruct the tester or the automation script to delete that data. Failing to clean up test data pollutes the environment and frequently causes subsequent test cases to fail unexpectedly.

### 3.3. AI in Quality Standards and Template Management

The application of AI in software testing is transforming how teams maintain these quality standards:

- **Automated Traceability (Requirement Mapping):** AI algorithms can ingest an entire backlog of requirements and cross-reference them with the test case repository. The AI can instantly highlight "orphaned" test cases that map to deprecated features, or flag requirements that lack sufficient test coverage.
- **Static Analysis of Test Cases:** Much like IDEs run static analysis on code to enforce formatting (such as enforcing GoogleStyle formatting conventions), NLP-driven AI tools can statically analyze written test cases. The AI can warn the QA engineer if a test case is not "Economical" (e.g., detecting excessively long, redundant steps) or if it violates the "Self-standing" rule by using overly ambiguous language.
- **Intelligent Teardown Verification:** For the "Self-cleaning" criteria, AI embedded in automated testing frameworks can monitor database states before and after a test run. If the test script fails to delete a mock entity it created, the AI can automatically identify the residual data anomaly and execute a cleanup routine, ensuring the environment remains pure for the next execution cycle.

## 4. Test Case Design Techniques

### 4.1. The Purpose of Test Design Techniques

Writing test cases without a structured methodology often leads to two major problems: testing too much (wasting time and resources) or testing too little (leaving dangerous gaps in test coverage). Test case design techniques are mathematical and logical approaches that help QA engineers select the minimum number of test cases required to achieve the maximum possible coverage. They ensure testing is both economical and highly accurate.

### 4.2. Equivalence Partitioning (EP)

Equivalence Partitioning is a black-box testing technique that divides input data into distinct groups, or "partitions," where the software is expected to exhibit the same behavior for every value within a particular group.

The core principle is that testing one value from a partition is equivalent to testing all other values in that partition. If one value works, all are assumed to work; if one fails, all are assumed to fail. This drastically reduces the number of test cases needed.

**How to Apply EP:**

1. Identify the condition or input field.
2. Divide the data into Valid partitions (data that should be accepted) and Invalid partitions (data that should be rejected).
3. Select exactly one test value from each partition.

**Example:** An e-commerce site accepts a quantity of 1 to 10 items per order.

- **Invalid Partition 1:** 0 or fewer items (e.g., test with -1)
- **Valid Partition:** 1 to 10 items (e.g., test with 5)
- **Invalid Partition 2:** 11 or more items (e.g., test with 15)

Instead of testing infinite numbers, you only need three test cases to validate the quantity logic.

### 4.3. Boundary Value Analysis (BVA)

Boundary Value Analysis is built on the statistical reality that errors are most likely to occur at the extreme edges of input ranges rather than in the center. BVA is almost always used in conjunction with Equivalence Partitioning.

Instead of selecting a random value from within a partition, BVA focuses entirely on the minimum and maximum boundaries of those partitions.

**How to Apply BVA:** For any given boundary `n`, you should test:

- `n - 1` (just below the boundary)
- `n` (the exact boundary)
- `n + 1` (just above the boundary)

**Example:** Using the previous example of allowing 1 to 10 items:

- **Lower Boundaries:** 0 (Invalid), 1 (Valid), 2 (Valid)
- **Upper Boundaries:** 9 (Valid), 10 (Valid), 11 (Invalid)

Testing these exact edges ensures that developers did not accidentally use a strictly less than `<` operator when they should have used a less than or equal to `<=` operator.

### 4.4. Decision Table Testing

When business logic becomes highly complex and involves multiple combinations of conditions resulting in different actions, EP and BVA are no longer sufficient. Decision Table testing is a technique used to systematically map out complex logical relationships.

A decision table captures all possible combinations of input conditions and defines the exact expected outcome for each combination. This guarantees that no logical permutation is overlooked.

**Example:** Consider a login system where a user must have both a valid username and a correct password to gain access.

| Test Case | Condition 1: Valid Username | Condition 2: Correct Password | Expected Outcome           |
| :-------- | :-------------------------- | :---------------------------- | :------------------------- |
| TC_01     | Yes                         | Yes                           | Access Granted             |
| TC_02     | Yes                         | No                            | Error: Incorrect Password  |
| TC_03     | No                          | Yes                           | Error: Invalid Username    |
| TC_04     | No                          | No                            | Error: Invalid Credentials |

### 4.5. State Transition Testing

Some software behaviors depend not just on the current input, but on the historical sequence of inputs (the current "state" of the system). State Transition Testing is used to validate systems that have finite, well-defined states and transitions.

**Example:** An ATM system locks a user's card after three consecutive incorrect PIN attempts.

- **State 1:** Start (0 failures). Input: Incorrect PIN -> Transition to State 2.
- **State 2:** 1 failure. Input: Incorrect PIN -> Transition to State 3.
- **State 3:** 2 failures. Input: Incorrect PIN -> Transition to State 4 (Account Locked).

A test case using this technique maps out the explicit sequence of actions required to force the system through its entire lifecycle of states.

### 4.6. The Role of AI in Test Case Design

AI is fundamentally changing how these design techniques are applied in modern QA environments, removing much of the manual calculation and mapping:

- **Automated Combinatorial Testing:** AI algorithms can instantly generate mathematically perfect Decision Tables from raw business requirements. They automatically calculate orthogonal arrays and pairwise test combinations to cover thousands of conditional permutations with just a handful of generated test cases.
- **Intelligent Boundary Extraction:** Natural Language Processing (NLP) models can read user stories (e.g., "The system must process files up to 50MB") and automatically extract the boundaries. The AI then auto-generates the specific BVA test cases (49.9MB, 50.0MB, 50.1MB) without human intervention.
- **State Transition Modeling:** AI agents can passively monitor user traffic or crawl applications to automatically map out hidden system states. They then generate state transition diagrams and create test cases to ensure no dead-ends or infinite loops exist within the application's flow.
- **Predictive Partitioning:** Using machine learning based on historical defect data, AI can dynamically adjust the Equivalence Partitions. If the AI notices that a specific sub-range of inputs has historically caused memory leaks, it will divide that partition further, commanding the generation of more localized test cases where the risk is highest.

## 5. The Strategic Value of Test Cases

### 5.1. Beyond Administrative Documentation

At the culmination of these core concepts, it is vital to recognize that a test case is far more than a simple administrative checklist or a mandatory project artifact. In the realm of professional Quality Assurance, a test case is a strategic asset. It represents a scientifically designed experiment that executes a single, well-defined objective to validate software behavior.

By translating vague business requirements into concrete, verifiable actions, test cases serve as the ultimate source of truth for software quality. They establish accountability, ensure complete reproducibility of defects, and provide a transparent metric for tracking project health and test coverage.

### 5.2. The Anatomy of Precision

The effectiveness of a test case relies heavily on its structural integrity. While fields like test data, preconditions, and expected results form the mechanical execution of the test, the **Test Case Objective/Title** is the most critical component for team communication.

Mastering the syntactic structure—`Action + Function + Operating Condition`—is what separates junior testers from senior QA engineers. This standardized naming convention ensures that anyone, from a Product Owner to an Automation Engineer, can instantly grasp the exact scope and context of the test without reading every individual step.

### 5.3. The Hallmarks of Excellence

Writing a test case is a straightforward task, but designing a robust, high-quality test suite requires strict adherence to seven core characteristics. A truly professional test case must be:

- **Accurate:** Laser-focused on its specific validation point.
- **Economical:** Stripped of unnecessary, time-wasting steps.
- **Repeatable:** Capable of yielding the exact same results across infinite execution cycles.
- **Traceable:** Explicitly linked back to the original business requirement.
- **Appropriate:** Contextually tailored for the specific testing environment.
- **Self-standing:** Comprehensible and executable by anyone, completely independent of the original author.
- **Self-cleaning:** Designed to revert any data or state changes it caused, leaving the environment pristine for the next test.

### 5.4. The Prerequisite for Automation

A common misconception is that manual testing and automated testing are entirely separate disciplines. In reality, excellent manual test cases are the foundational blueprints for all automated testing frameworks. You cannot automate chaos. If a manual test case is not economical, self-standing, and self-cleaning, the automated script derived from it will be brittle, slow, and prone to false failures. Structuring test cases with precise inputs, explicit preconditions, and isolated validation points is the first, mandatory step toward building a successful Continuous Integration/Continuous Deployment (CI/CD) pipeline.

### 5.5. AI and the Future of Test Engineering

The integration of AI represents a paradigm shift in how quality assurance is managed at a strategic level. AI is no longer just a tool for execution; it acts as a co-pilot throughout the entire software testing lifecycle:

- **Holistic Suite Management:** As test suites grow into the tens of thousands, they often become bloated. AI models are now deployed to perform continuous maintenance on these massive repositories, automatically archiving deprecated tests, merging duplicates, and optimizing the suite for maximum coverage with minimal execution time.
- **Intelligent Requirement Analysis:** Before a single test case is written, AI can analyze product requirements or user stories to detect logical conflicts, missing edge cases, and architectural vulnerabilities, prompting human engineers to design test cases for scenarios they might not have initially conceived.
- **Dynamic Risk Assessment:** Rather than running every test case for every minor code commit, AI analyzes the specific code changes and historical defect patterns to dynamically select and execute only the test cases relevant to the highest-risk areas. This targeted approach drastically reduces release times while maintaining rigorous quality standards.
- **Evolution from Tester to Quality Engineer:** With AI handling the heavy lifting of test data generation, syntax checking, and test script maintenance, the role of the human QA professional is evolving. Senior QA engineers now focus on strategic test architecture, exploratory testing, and interpreting the complex analytical insights provided by AI systems.

## 6. Comprehensive Practice & Applied Exercises

### 6.1. Multiple-Choice Questions (MCQs)

**Question 1: According to IEEE 729-1983, how is a test case defined?**

- A. A script that automatically finds software defects.
- **B. A specific set of test data and associated procedures developed for a particular objective.**
- C. A document that defines the business requirements of a system.
- D. A set of guidelines for writing clean application code.

**Explanation:** The provided standard directly defines a test case as a specific set of test data and associated procedures designed to achieve a particular validation objective.

**Question 2: What is the correct, standardized syntax for writing a Test Case Objective/Title?**

- A. Operating Condition + Action + Function
- B. Function + Action + Operating Condition
- **C. Action + Function + Operating Condition**
- D. Action + Expected Result + Pre-condition

**Explanation:** The standard syntax explicitly starts with the verb (Action), followed by what is being tested (Function), and ends with the context or data state (Operating Condition).

**Question 3: Based on the standard syntax, which of the following is the BEST example of a test case title?**

- A. Check that the login works if the user types the right password.
- B. Valid login test case.
- **C. Verify user login with valid credentials and active session.**
- D. Run the system and validate the output from the database.

**Explanation:** Option C strictly follows the `Action (Verify) + Function (user login) + Operating Condition (with valid credentials and active session)` format.

**Question 4: What does it mean when a test case is described as "Economical"?**

- A. It requires very little computational power to execute.
- **B. It contains no unnecessary steps.**
- C. It is written using the fewest words possible.
- D. It only uses free, open-source testing tools.

**Explanation:** An economical test case gets straight to the point, avoiding redundant or unneeded steps (like documenting a full login process if the test is only about the shopping cart).

**Question 5: A test case creates a mock user profile to verify a dashboard feature. Which characteristic requires the test case to delete this profile at the end?**

- A. Self-standing
- B. Appropriate
- C. Traceable
- **D. Self-cleaning**

**Explanation:** "Self-cleaning" means the test case picks up after itself, ensuring that any data created or modified during the test is reverted, leaving the environment pure for the next test.

**Question 6: Why is it crucial for a test case to be "Traceable"?**

- **A. To map it directly back to a specific business requirement.**
- B. To allow the execution path to be logged in the console.
- C. To track which developer wrote the buggy code.
- D. To monitor the amount of time it takes to execute.

**Explanation:** Traceability ensures that every test case validates a specific requirement, proving to stakeholders that the requested features have been covered and tested.

**Question 7: According to industry best practices, what is considered the MOST important essential field of a test case?**

- A. Pre-conditions
- B. Test Data
- **C. Test case Objective/Title**
- D. Bug ID

**Explanation:** The Objective/Title is the most important because it gives reviewers an immediate idea of the test. In fast-paced environments, it is often the only part of the test case that is fully read.

**Question 8: What is the primary role of a "Validation Point" within a test case?**

- A. To verify that the testing environment is online.
- **B. To define clearly what expected behavior or result you are attempting to validate at a specific step.**
- C. To validate the syntax of the automated script.
- D. To check if the test case title matches the requirement.

**Explanation:** A validation point is written as a step to explicitly state the expected outcome of a specific action, ensuring testers know exactly what constitutes a pass or fail at that moment.

**Question 9: Which of the following is NOT listed as a core reason to write test cases?**

- A. To measure test coverage
- B. To find bugs
- **C. To replace the need for developer code reviews**
- D. Reproducibility

**Explanation:** Test cases are written for accountability, reproducibility, tracking, automation, finding bugs, verifying correctness, and measuring coverage. They do not replace code reviews.

**Question 10: A test case is described as "Self-standing". What does this imply?**

- A. It can execute itself automatically without human intervention.
- B. It does not require a testing environment.
- C. It only uses default system data.
- **D. It is independent of the writer and can be executed by anyone.**

**Explanation:** A self-standing test case is written clearly enough that any newly onboarded QA engineer can read it and execute it flawlessly without needing to ask the original author for clarification.

### 6.2. Applied Exercises

#### Format 1: Objective/Title Syntax Formulation

**Exercise 1:**

- **Context:** You need to test the generation of an annual financial report. The test must be executed precisely on the last day of the fiscal year to ensure the date logic holds up.
- **Task:** Write the Test Case Title using the standard syntax.
- **Solution:**
  - **Title:** Run annual report on the last day of the fiscal year.
  - **Explanation:**
    - **Action:** Run
    - **Function:** annual report
    - **Operating Condition:** on the last day of the fiscal year. This exactly matches the required structure and clearly conveys the test's purpose.

**Exercise 2:**

- **Context:** You are testing an e-commerce website. You need to ensure that if a user clicks the "Proceed to Checkout" button without adding any items to their cart, the system prevents them from moving forward.
- **Task:** Write the Test Case Title using the standard syntax.
- **Solution:**
  - **Title:** Validate checkout process when the shopping cart is empty.
  - **Explanation:**
    - **Action:** Validate
    - **Function:** checkout process
    - **Operating Condition:** when the shopping cart is empty.

#### Format 2: Complete the Test Case Template

**Exercise 3:**

- **Context:** You are testing the Login page of a web application. The requirement states that if a user attempts to log in with an email that does not exist in the database, the system must display a red error message stating "Account not found."
- **Task:** Create the full test case template for this scenario.
- **Solution:**

| TC ID      | Objective                                            | Pre-conditions                                                                                                | Steps                                                                                                                                 | Expected Result                                                                                          |
| :--------- | :--------------------------------------------------- | :------------------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------- |
| TC_AUTH_02 | Verify user login with an unregistered email address | 1. User is on the application Login page.<br>2. The email `fakeuser@test.com` does not exist in the database. | 1. Enter `fakeuser@test.com` into the Email field.<br>2. Enter `Password123` into the Password field.<br>3. Click the "Login" button. | 1. The user is not logged in.<br>2. A red error message appears displaying exactly: "Account not found." |

- **Explanation:** This test case is **Economical** (no unnecessary steps to navigate to the login page, it's handled in pre-conditions) and **Accurate** (it tests exactly what the requirement dictates, providing specific test data and explicit expected results).

**Exercise 4:**

- **Context:** You are testing a Search function on a blog. If the user searches for a term that has no matching articles, the page should display "No results found for [term]".
- **Task:** Create the full test case template for this scenario.
- **Solution:**

| TC ID      | Objective                                            | Pre-conditions                     | Steps                                                                                                          | Expected Result                                                                                       |
| :--------- | :--------------------------------------------------- | :--------------------------------- | :------------------------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------------- |
| TC_SRCH_01 | Validate search function with a non-existent keyword | 1. User is on the blog's homepage. | 1. Click on the search bar.<br>2. Type the keyword `xyzqwerty` into the search bar.<br>3. Press the Enter key. | 1. The search results page loads.<br>2. The page displays the text: "No results found for xyzqwerty". |

- **Explanation:** The steps are explicit and **Self-standing**. The validation point is clearly defined in the Expected Result, showing exactly what string must appear on the screen.

#### Format 3: Test Case Audit & Correction

**Exercise 5:**

- **Flawed Test Case:**
  - **Title:** Test file upload
  - **Pre-conditions:** None
  - **Steps:** 1. Go to the upload page. 2. Upload a file. 3. Check if it worked.
  - **Expected Result:** File is uploaded.
- **Task:** Audit this test case, identify which of the 7 characteristics it violates, and rewrite it correctly.
- **Solution & Audit:**
  - **Violations:**
    - **Accurate/Syntax:** The title lacks an Operating Condition.
    - **Self-standing:** The steps are incredibly vague. What is the URL? What type of file? What size? "Check if it worked" is not a valid instruction.
    - **Economical/Traceable:** It does not specify the exact parameters of the test, making it impossible to map to a specific requirement (e.g., is this testing PDF uploads or image uploads?).
  - **Corrected Test Case:**
    - **Title:** Verify profile picture upload with a valid PNG file under 5MB.
    - **Pre-conditions:** 1. User is logged into their account. 2. User is on the 'Edit Profile' page. 3. A test file named `avatar_test.png` (2MB) is available on the local machine.
    - **Steps:** 1. Click the "Upload Avatar" button. 2. Select the `avatar_test.png` file from the local machine and click "Open". 3. Click the "Save Changes" button.
    - **Expected Result:** A green success toast message appears, and the profile thumbnail immediately updates to display `avatar_test.png`.

**Exercise 6:**

- **Flawed Test Case:**
  - **Title:** Validate database connection when creating a new product.
  - **Pre-conditions:** Admin is logged in.
  - **Steps:**
    1. Navigate to /admin/products.
    2. Click 'Add Product'.
    3. Fill in product name as "Test Widget 99".
    4. Click 'Save'.
  - **Expected Result:** Product is saved to the database.
- **Task:** Audit this test case, identify which of the 7 characteristics it violates, and rewrite it correctly.
- **Solution & Audit:**
  - **Violations:**
    - **Self-cleaning:** This is the major violation. The test case creates a persistent entity ("Test Widget 99") in the database but provides no instructions to delete it. Running this test 100 times will result in 100 junk products cluttering the admin panel.
    - **Accurate:** The title says "Validate database connection", but the steps are performing an end-to-end UI test for product creation.
  - **Corrected Test Case:**
    - **Title:** Verify new product creation with valid standard inputs.
    - **Pre-conditions:** 1. Admin is logged into the dashboard. 2. Admin is on the `/admin/products` page.
    - **Steps:**
      1. Click the 'Add Product' button.
      2. Enter "Test Widget 99" into the Product Name field.
      3. Enter "10" into the Price field.
      4. Click 'Save'.
      5. **[Validation Point]** Verify a success message is displayed and "Test Widget 99" appears in the product list.
      6. **[Teardown]** Click the 'Delete' icon next to "Test Widget 99" and confirm deletion.
    - **Expected Result:** The product is successfully created, visible on the UI, and subsequently removed without leaving residual data.
