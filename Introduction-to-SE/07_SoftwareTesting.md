<div align="center">
  <h1>Software Testing</h1>
  <sub>December 09, 2025</sub>
</div>

This chapter covers the crucial process of software testing, which is intended to show that a program does what it is intended to do and to discover program defects before use. Testing is part of the broader Verification and Validation (V&V) process. Verification asks, "Are we building the product right?" while Validation asks, "Are we building the right product?".

## Development Testing

Development testing includes all testing activities carried out by the team developing the system. It typically occurs at three levels of granularity:

- **Unit Testing:** Testing individual program units or object classes. It focuses on testing the functionality of objects or methods.
- **Component Testing:** Testing integrated composite components. It focuses on testing component interfaces.
- **System Testing:** Testing the integrated system as a whole. It focuses on testing component interactions.

### 1. Unit Testing

This involves testing individual components (methods or classes) in isolation.

- **Object Class Testing:** When testing classes, you should design tests to provide coverage of all object features: test all operations, set/check all attributes, and simulate all events that cause state changes.
- **Automated Testing:** Whenever possible, unit testing should be automated using frameworks like JUnit. An automated test consists of a setup part (initializing the system), a call part (calling the method to be tested), and an assertion part (comparing the result to the expectation).
- **Mock Objects:** These are used to simulate external dependencies (like databases) to allow objects to be tested in isolation.

### 2. Choosing Unit Test Cases

Testing is expensive, so selecting effective test cases is critical. Two key strategies are:

1. **Partition Testing:** This involves identifying groups of inputs (equivalence partitions) that have common characteristics and should be processed in the same way.

- **Equivalence Partitions:** Input data and output results often fall into classes where the program behaves comparably for all members. You should identify these partitions and choose test cases from within them, specifically focusing on the boundaries.

2. **Guideline-based Testing:** Using testing guidelines based on experience of common errors. Examples include testing with sequences of zero or one length, and forcing buffers to overflow.

### 3. Component Testing

This focuses on testing the interfaces between components as they are integrated. Interface errors are common and include **interface misuse**, **interface misunderstanding**, and **timing errors**.

- **Guidelines for Interface Testing:** Include testing with extreme parameter values, testing with null pointers, deliberately causing component failure, and stress testing message-passing systems.

### 4. System Testing

This involves integrating components to create a version of the system and testing the integrated system. It checks that components are compatible and interact correctly.

- **Use-case testing:** Since system testing focuses on interactions, use cases are effective for deriving test cases. They force interactions between multiple components/objects.
- **Exhaustive testing** is impossible, so testing is based on a subset of cases, often prioritized by risk or usage frequency.

## Test-Driven Development (TDD)

TDD is an approach where you interleave testing and code development. You write the test before you write the code.

**The TDD Process:**

1. Identify a small increment of functionality.
2. Write an automated test for this functionality.
3. Run the test (it will fail initially).
4. Implement the functionality and refactor.
5. Re-run the test (it should pass).

**Benefits:**

- **Code Coverage:** Every code segment has at least one associated test.
- **Regression Testing:** A test suite is developed incrementally, allowing regression tests to be run easily to check that changes haven't broken existing code.
- **Simplified Debugging:** When a test fails, the problem is usually in the recently written code.
- **System Documentation:** The tests act as a form of documentation.

## Release Testing

Release testing is the process of testing a particular release of a system intended for use outside the development team. Ideally, this is done by a separate team (black-box testing).

- **Requirements-based Testing:** A systematic approach where you consider each requirement and derive a set of tests to demonstrate that the system satisfies it.
- **Scenario Testing:** Using typical usage scenarios (stories) to develop test cases. This tests combinations of requirements and how the system performs in realistic situations.
- **Performance Testing:** Testing for emergent properties like performance and reliability. This often involves **stress testing**, where the load on the system is increased until it fails. This helps understand failure behavior and discover defects that only appear under heavy load.

## User Testing

User testing is the stage where users or customers provide input and advice on system testing.

- **Alpha Testing:** Users work with the development team at the developer's site to test the software as it is being developed.
- **Beta Testing:** A release is made available to a larger group of users to experiment with in their own environment.
- **Acceptance Testing:** A formal process where the customer tests the system to decide whether it should be accepted. It involves six stages: defining acceptance criteria, planning, deriving tests, running tests, negotiating results, and accepting or rejecting the system.

## Exercises

### 1. Exercise 1

> Explain why it is not necessary for a program to be completely free of defects before it is delivered to its customers.

It is not necessary (and often impossible) for a program to be completely defect-free because:

1. **Diminishing Returns:** As testing progresses, finding the remaining, more obscure faults becomes increasingly expensive and time-consuming. The cost of finding the last few bugs may outweigh the cost of the failures they cause.
2. **Usage Patterns:** Many defects may be in code that is rarely executed or triggered only by obscure combinations of inputs that users are unlikely to encounter in normal operation.
3. **Market Pressure:** In many industries, time-to-market is critical. It may be better to release a product with known, minor bugs than to delay release and lose market share.
4. **Workarounds:** Users can often find workarounds for minor defects, meaning the system is still "fit for purpose" even if not perfect.

### 2. Exercise 2

> Explain why testing can only detect the presence of errors, not their absence.

Testing involves executing a program with a specific set of test data (inputs). Since the input space for any non-trivial program is effectively infinite, testing can only cover a tiny fraction of all possible execution paths. Therefore, if a test fails, we know an error exists. However, if all tests pass, it simply means no errors were found with that specific set of data. It does not prove that the program will behave correctly for the infinite number of untested inputs.

### 3. Exercise 3

> Some people argue that developers should not be involved in testing their own code but that all testing should be the responsibility of a separate team. Give arguments for and against testing by the developers themselves.

**Arguments For Developer Testing:**

- **Knowledge:** Developers know the code best and can efficiently write tests that cover specific paths and logic (white-box testing).
- **Efficiency:** It avoids the overhead of handing off code to a separate team for every small change. TDD relies on this rapid feedback loop.
- **Responsibility:** It encourages developers to take ownership of the quality of their work.

**Arguments Against Developer Testing:**

- **Lack of Objectivity:** Developers may subconsciously avoid testing "dangerous" cases or test only for what the code does rather than what it should do ("testing their own assumptions").
- **Blind Spots:** Developers may overlook requirements or misunderstand specifications in the same way they did when writing the code.
- **Skill Set:** Developers may lack specific testing skills or the mindset required to break the software ("destructive" testing vs. "constructive" coding).

### 4. Exercise 4

> You have been asked to test a method called `catWhiteSpace` in a `Paragraph` object that, within the paragraph, replaces sequences of blank characters with a single blank character. Identify testing partitions for this example and derive a set of tests for the `catWhiteSpace` method.

**Equivalence Partitions:**

1. Strings with **no** white space.
2. Strings with **single** white space characters (no replacement needed).
3. Strings with **sequences** of white space characters (replacement needed).
4. Strings with white space at the **beginning** or _end_.
5. **Empty** strings or null input.
6. Strings with **mixed** characters and white space sequences.

**Derived Test Cases:**

- **Input:** "Thequickbrownfox" -> **Output:** "Thequickbrownfox" (No spaces)
- **Input:** "The quick brown fox" -> **Output:** "The quick brown fox" (Single spaces)
- **Input:** "The quick brown fox" -> **Output:** "The quick brown fox" (Multiple spaces in middle)
- **Input:** " The quick brown fox " -> **Output:** " The quick brown fox " (Leading/Trailing sequence - note: usually these become single spaces, or are trimmed depending on exact spec. Assuming replacement rule applies: " " becomes " ").
- **Input:** " " -> **Output:** " " (Sequence of only spaces)
- **Input:** "" -> **Output:** "" (Empty string)

### 5. Exercise 5

> What is regression testing? Explain how the use of automated tests and a testing framework such as JUnit simplifies regression testing.

- **Regression Testing:** This is the process of re-running previously successful tests after changes have been made to the system (e.g., bug fixes or new features) to ensure that the changes have not introduced new bugs or broken existing functionality.
- **Simplification via Automation:** Regression testing is expensive if done manually because the entire test suite needs to be re-executed frequently. Automated testing frameworks like JUnit allow the entire suite of tests to be run automatically and very quickly (often in seconds). This makes it feasible to run regression tests after every code change (as in TDD), ensuring that regressions are caught immediately.

### 6. Exercise 6

> The MHC-PMS is constructed by adapting an off-the-shelf information system. What do you think are the differences between testing such a system and testing software that is developed using an object-oriented language such as Java?

**Testing COTS-based MHC-PMS:**

- **Focus on Configuration:** Testing focuses on ensuring the system is configured correctly for the specific environment and requirements, rather than testing the core logic of the COTS product (which is assumed to be tested by the vendor).
- **Focus on Integration:** Extensive testing of how the system interacts with other systems and databases.
- **Black-box nature:** You likely cannot do unit testing or white-box testing as source code is unavailable.
- **Scenario-based:** Testing will rely heavily on end-to-end user scenarios to verify workflows.

**Testing Java Software:**

- **Full Scope:** Involves the full pyramid of testing: unit testing (methods/classes), component testing, and system testing.
- **White-box access:** Testers can inspect code to design tests (path coverage, etc.).
- **Automated Unit Tests:** Extensive use of frameworks like JUnit for fine-grained logic testing.

### 7. Exercise 7

> Write a scenario that could be used to help design tests for the wilderness weather station system.

**Scenario: Severe Storm Data Collection**

"It is mid-winter and a severe storm is approaching a remote weather station. The wind speed sensors register a steady increase, exceeding 80 km/h. The temperature drops rapidly to -15 degrees Celsius. The system must switch to 'high frequency' transmission mode due to the extreme wind conditions. It attempts to transmit data to the satellite every 10 minutes. However, heavy snow obstructs the transmitter signal, causing a transmission failure. The system must detect this failure, store the data locally in its flash memory, and retry transmission after 15 minutes. Once the storm passes and the signal is re-established, the system must transmit the backlog of stored data in chronological order without losing any current readings, while sending a status report indicating the temporary communication failure and the battery status (which may be depleted due to cold)."

### 8. Exercise 8

> What do you understand by the term "stress testing"? Suggest how you might stress test the MHC-PMS.

**Stress Testing:** A form of performance testing where the system is deliberately overloaded to test its failure behavior. The goal is to ensure the system fails "softly" (e.g., slowing down or rejecting connections) rather than crashing or corrupting data when the load exceeds its design limits.

**Stress Testing MHC-PMS:**

- Simulate a scenario where all clinics in the network attempt to upload their daily records simultaneously at 5:00 PM.
- Simulate multiple users attempting to update the same patient record at the exact same instant to test data locking and integrity.
- Overload the system with a massive number of concurrent queries for patient history to see if the database response time degrades gracefully or if the server crashes.

### 9. Exercise 9

> What are the benefits of involving users in release testing at an early stage in the testing process? Are there disadvantages in user involvement?

**Benefits:**

- **Realistic Usage:** Users use the system in ways developers didn't anticipate, revealing defects related to actual workflows.
- **Requirement Validation:** It helps confirm if the system actually meets the user's business needs, not just the written specification.
- **Early Feedback:** Identifying usability or functional issues early allows for fixes before the final release.

**Disadvantages:**

- **Scope Creep:** Users may suggest changes or new features that are out of scope for the current release.
- **Disruption:** It can disrupt the user's actual work if the system is unstable.
- **Bias:** Users might focus on superficial UI issues rather than critical functionality.
- **Lack of Structure:** User testing is often less systematic than professional testing, potentially missing edge cases.

### 10. Exercise 10

> A common approach to system testing is to test the system until the testing budget is exhausted and then deliver the system to customers. Discuss the ethics of this approach for systems that are delivered to external customers.

**Ethical Issues:**

- **Professional Responsibility:** Delivering a system known to be inadequately tested (simply because money ran out) violates the engineer's responsibility to produce high-quality work and potentially the ACM/IEEE code of ethics regarding product standards.
- **Transparency:** If the customer is not informed that testing was cut short due to budget, it is deceptive. The customer believes they are paying for a fully validated product.
- **Risk:** If the system is critical (e.g., MHC-PMS), cutting testing could lead to safety or security failures, harming people. This is negligent.

**Discussion:**

- While budget constraints are real, quality targets should be defined by reliability requirements, not just remaining funds.
- If the budget is exhausted, the ethical approach is to negotiate with the customer: either provide more budget/time to finish testing, or agree to reduce the scope (release fewer features) so that the delivered features can be tested properly. Delivering a full-scope but buggy system is unprofessional.
