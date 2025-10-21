<div align="center">
  <h1>Software Requirements</h1>
  <sub>October 28, 2025</sub>
</div>

This chapter introduces **requirements engineering (RE)**, the process of discovering, analyzing, documenting, and checking the services a system should provide and the constraints on its operation. Requirements reflect customer needs.

## Types of Requirements

- **User Requirements:** High-level statements in natural language and diagrams describing what services the system should provide and its operational constraints, intended for customers and end-users.
- **System Requirements:** More detailed descriptions of the system's functions, services, and operational constraints, intended for developers and potentially part of the contract. It's crucial to distinguish these as they serve different readers and purposes.
- **Functional Requirements:** Statements of services the system should provide, how it should react to inputs, and its behavior in particular situations. They should ideally be **complete** (all required services defined) and **consistent** (no contradictions), though this is hard to achieve fully in complex systems.
- **Non-functional Requirements:** Constraints on the services or functions offered, such as timing constraints, development process constraints, or standards. These often apply to the system as a whole and can be more critical than individual functional requirements. They are classified into:
  - **Product requirements** (e.g., performance, reliability, security, usability).
  - **Organizational requirements** (e.g., process standards, operational constraints).
  - **External requirements** (e.g., regulatory, legislative, ethical constraints). Non-functional requirements should be stated quantitatively whenever possible so they can be objectively tested.

## The Software Requirements Document (SRS)

This is the official statement of what the system developers should implement, including both user and system requirements. Its structure and level of detail depend on the system type and development process. Agile methods often use less formal alternatives like user stories. The SRS has diverse users, including customers, managers, system engineers, developers, and testers. A suggested structure based on IEEE standards includes sections like Introduction, Glossary, User Requirements Definition, System Architecture, System Requirements Specification, System Models, System Evolution, Appendices, and Index.

## Requirements Specification

This is the process of writing down the user and system requirements.

- **User requirements** should use natural language, diagrams, and tables, avoiding technical jargon.
- **System requirements** add detail and may use natural language, structured natural language, graphical notations (like UML), or formal mathematical specifications.
- **Natural language specification** is expressive but prone to ambiguity. Guidelines include using standard formats, consistent language (shall/should), highlighting key parts, avoiding jargon, and associating rationale with requirements.
- **Structured specifications** use templates or standard forms to reduce variability and improve organization. Tables can be useful for describing alternative situations.

## Requirements Engineering Processes

RE involves four high-level activities: feasibility study, elicitation and analysis, specification, and validation. These are often interleaved in an iterative, spiral-like process. The main activities in elicitation and analysis are:

1. **Requirements discovery:** Gathering information from stakeholders, documentation, and existing systems.
2. **Requirements classification and organization:** Grouping related requirements.
3. **Requirements prioritization and negotiation:** Resolving conflicts and setting priorities.
4. **Requirements specification:** Documenting the requirements.

## Requirements Elicitation and Analysis

This involves working with stakeholders (end-users, managers, engineers, domain experts, regulators, etc.) to discover requirements. Challenges include stakeholders not knowing what they want, difficulties in articulation, implicit domain knowledge, conflicting needs, and political factors. Techniques include:

- **Interviewing:** Formal (closed) or informal (open) discussions with stakeholders. Good for overall understanding but less effective for domain knowledge or organizational politics.
- **Scenarios:** Real-life examples of system interaction, useful for adding detail and validating requirements.
- **Use cases (UML):** Identify actors and interactions. Effective for interaction requirements but less so for constraints or non-functional requirements.
- **Ethnography:** Observing people in their actual working environment to discover implicit requirements derived from how work is really done.

## Requirements Validation

This is the process of checking that the requirements document defines the system the customer actually wants. Errors found at this stage are much cheaper to fix than later in the development cycle. Checks include:

- **Validity checks:** Does the system provide the functions that best support customer needs?
- **Consistency checks:** Are there any contradictory requirements?
- **Completeness checks:** Are all functions and constraints required by the user included?
- **Realism checks:** Can the requirements be implemented within the available technology, budget, and schedule?
- **Verifiability:** Can the requirements be tested?

Validation techniques include requirements reviews, prototyping, and test-case generation.

## Requirements Management

Requirements inevitably change due to evolving stakeholder understanding, changing business environments, and diverse user needs. Requirements management is the process of understanding and controlling these changes. It requires planning for:

- **Requirements identification:** Unique identification for traceability.
- **Change management process:** Assessing the impact and cost of changes.
- **Traceability policies:** Defining links between requirements and design.
- **Tool support:** For storage, change management, and traceability management.

The change management process involves problem analysis, change analysis and costing, and change implementation.

## Exercises

### 1. Exercise 1

> Identify and briefly describe four types of requirement that may be defined for a computer-based system.

1. **User Requirements:** These are high-level statements in natural language with diagrams of what services the system is expected to provide to system users and the constraints under which it must operate. They are written for the customer.
2. **System Requirements:** These are more detailed descriptions of the software system’s functions, services, and operational constraints. They precisely define what is to be implemented and are often part of the contract between the system buyer and developers.
3. **Functional Requirements:** These are statements that describe the services the system should provide, how the system should react to particular inputs, and how it should behave in particular situations. They define what the system should do.
4. **Non-functional Requirements:** These are constraints on the services or functions offered by the system. They include timing constraints, process constraints, and standards. They often apply to the system as a whole rather than individual features and define emergent properties like reliability, performance, and security.

### 2. Exercise 2

> Discover ambiguities or omissions in the following statement of requirements for part of a ticket-issuing system:
> An automated ticket-issuing system sells rail tickets. Users select their destination and input a credit card and a personal identification number. The rail ticket is issued and their credit card account charged. When the user presses the start button, a menu display of potential destinations is activated, along with a message to the user to select a destination. Once a destination has been selected, users are requested to input their credit card. Its validity is checked and the user is then requested to input a personal identifier. When the credit transaction has been validated, the ticket is issued.

Ambiguities and Omissions:

- **Ticket Type:** The system only sells **"rail tickets"**. It does not specify ticket types (e.g., single, return, first-class, standard, off-peak).
- **User Interface Flow:** The sequence of actions is confusing. It says users **"select their destination and input a credit card"** at the beginning, but later states that after pressing **"start"**, they select a destination and then input their credit card.
- **PIN vs. Personal Identifier:** The terms **"personal identification number"** and **"personal identifier"** are used. It's unclear if they refer to the same thing (the card's PIN) or something else.
- **Credit Card Validity Check:** How is the card's validity checked? What happens if it is invalid? The process flow for an invalid card is not described.
- **Transaction Validation:** How is the credit transaction validated? What happens if the validation fails (e.g., insufficient funds)?
- **Error Handling:** There is no mention of how to handle errors such as incorrect PIN entry, network failure during transaction, or the machine running out of tickets.
- **Issuing the Ticket:** How is the ticket issued (e.g., printed)? What happens if the printing fails? Is the customer's account still charged?
- **Start Button:** The purpose of the **"start button"** is unclear if the first step for the user is to select a destination anyway.

### 3. Exercise 3

> Rewrite the above description using the structured approach described in this chapter. Resolve the identified ambiguities in an appropriate way.

**Function:** Purchase Rail Ticket.

**Description:** Allows a user to purchase a standard-class, single rail ticket to a pre-defined destination and pay by credit card.

**Inputs:** Destination selection, Credit Card, Personal Identification Number (PIN).

**Source:** User input via touchscreen and card reader.

**Outputs:** Printed rail ticket, confirmation message.

**Destination:** User, Credit Card System.

**Action:**

1. The user is prompted to select a destination from a displayed list.
2. Once a destination is selected, the fare is displayed, and the user is asked to confirm.
3. Upon confirmation, the user is prompted to insert their credit card.
4. The system reads the card details and validates the card with the external banking system.
5. If the card is valid, the user is prompted to enter their PIN.
6. The system validates the PIN and processes the transaction.
7. If the transaction is approved, the system prints the ticket and returns the credit card. A confirmation is displayed.

**Requires:** A network connection to the external banking system for card validation.

**Pre-condition:** The user must be at the ticket machine's main screen.

**Post-condition:** The user has a printed ticket, and their credit card account has been debited for the correct fare.

**Side Effects:** A record of the transaction is logged.

**Exceptions:**

- **Invalid Card:** If the card is invalid, an error message is displayed, and the card is returned. The system returns to the start screen.
- **Incorrect PIN:** If the PIN is incorrect, the user is prompted to re-enter it. After three incorrect attempts, the transaction is cancelled, the card is returned, and an error message is displayed.
- **Transaction Fails:** If the credit transaction fails, an error message is displayed, the card is returned, and the system returns to the start screen.
- **Printing Fails:** If the ticket cannot be printed, a failure message is displayed, and the transaction is automatically voided with the banking system. The card is returned.

### 4. Exercise 4

> Write a set of non-functional requirements for the ticket-issuing system, setting out its expected reliability and response time.

- **Reliability:** The system shall have an availability of 99.5% during its specified operational hours (06:00 to 23:00).
- **Reliability:** No more than 1 in 1000 transactions should fail due to software faults in the ticket-issuing system.
- **Response Time**: The time between a user selecting a destination and the fare being displayed shall be less than 2 seconds.
- **Response Time**: The credit card validation process shall take no longer than 5 seconds.
- **Usability:** A new user should be able to successfully purchase a ticket within 90 seconds.
- **Security:** All communication with the external banking system shall be encrypted.
- **Security:** The system shall not store any user's credit card number or PIN after the transaction is complete.

### 5. Exercise 5

> Using the technique suggested here, where natural language descriptions are presented in a standard format, write plausible user requirements for the following functions:
> An unattended petrol (gas) pump system that includes a credit card reader. The customer swipes the card through the reader then specifies the amount of fuel required. The fuel is delivered and the customer’s account debited.
> The cash-dispensing function in a bank ATM.
> The spelling-check and correcting function in a word processor.

- **An unattended petrol (gas) pump system:** The system shall allow a user to pay for fuel with a credit or debit card and dispense the specified amount of fuel. It must debit the customer's account correctly.
- **The cash-dispensing function in a bank ATM:** The system shall allow a user with a valid bank card and PIN to withdraw a specified amount of cash, provided the amount does not exceed pre-defined limits and the user has sufficient funds in their account.
- **The spelling-check and correcting function in a word processor:** The system shall check the spelling of words in the document against a built-in dictionary and highlight any words that are not found. It shall provide the user with a list of suggested corrections for misspelled words.

### 6. Exercise 6

> Suggest how an engineer responsible for drawing up a system requirements specification might keep track of the relationships between functional and non-functional requirements.

An engineer can keep track of these relationships using several methods, often supported by requirements management tools:

- **Cross-Referencing:** Explicitly include references within the description of each requirement. For example, a functional requirement description could list the IDs of the non-functional requirements (e.g., performance, security) that constrain it. Conversely, a non-functional requirement could list the functional requirements it applies to.
- **Requirements Traceability Matrix:** Create a matrix where rows represent functional requirements and columns represent non-functional requirements (or vice versa). A mark in a cell indicates a relationship between the corresponding requirements. This provides a clear visual overview.
- **Grouping in the SRS:** Structure the Software Requirements Specification (SRS) so that related functional and non-functional requirements are grouped together. For example, under a specific feature (functional requirement), list the specific performance, security, or usability constraints (non-functional requirements) that apply directly to that feature. General non-functional requirements applying to the whole system could still be in a separate section but cross-referenced.
- **Using a Requirements Management Tool:** Many specialized tools provide features to explicitly define and manage links (dependencies, constraints) between different types of requirements. These tools can automatically maintain traceability and help with impact analysis if requirements change.

### 7. Exercise 7

> Using your knowledge of how an ATM is used, develop a set of use cases that could serve as a basis for understanding the requirements for an ATM system.

Use Cases:

- **Withdraw Cash:** Customer inserts card, enters PIN, selects withdrawal option, enters amount, receives cash and card (potentially receipt). Interaction with Bank System for validation and debiting.
- **Check Balance:** Customer inserts card, enters PIN, selects balance inquiry option, views balance on screen (potentially prints receipt), receives card. Interaction with Bank System for balance retrieval.
- **Deposit Funds:** (Assuming machine supports this) Customer inserts card, enters PIN, selects deposit option, inserts cash/cheque into deposit slot, confirms amount, receives receipt and card. Interaction with Bank System for crediting account (possibly delayed).
- **Transfer Funds:** (Between customer's own accounts) Customer inserts card, enters PIN, selects transfer option, specifies source/destination accounts and amount, confirms transfer, receives receipt and card. Interaction with Bank System for debiting/crediting accounts.
- **Change PIN:** Customer inserts card, enters current PIN, selects PIN change option, enters new PIN twice, confirms change, receives card. Interaction with Bank System to update PIN.
- **System Startup:** Maintenance Technician initializes the ATM, loads cash, checks supplies. Interaction with Bank System to bring ATM online.
- **System Shutdown:** Maintenance Technician takes the ATM offline for maintenance. Interaction with Bank System.
- **Authenticate User:** (Implicit, part of most customer use cases) System validates card and PIN against Bank System data.

### 8. Exercise 8

> Who should be involved in a requirements review? Draw a process model showing how a requirements review might be organized.

Who should be involved:

- **Users/Customers:** To ensure the requirements meet their actual needs.
- **System Developers/Engineers:** To check for feasibility and clarity from an implementation perspective.
- **Testers/QA Team:** To ensure the requirements are verifiable and to start planning tests.
- **Project Manager:** To assess the impact of requirements on the project schedule and resources.

Process Model (Simplified):

1. **Planning:** The review team is formed, and the requirements document is distributed. A review meeting is scheduled.
2. **Individual Preparation:** Each reviewer reads the document to identify issues, conflicts, and omissions.
3. **Review Meeting:** The document author "walks through" the requirements. The team discusses the issues found during preparation. A scribe records all agreed-upon problems and actions.
4. **Rework:** The author revises the requirements document based on the actions from the meeting.
5. **Follow-up:** The review chair checks that all agreed actions have been completed. If necessary, another review cycle is initiated.

### 9. Exercise 9

> When emergency changes have to be made to systems, the system software may have to be modified before changes to the requirements have been approved. Suggest a model of a process for making these modifications that will ensure that the requirements document and the system implementation do not become inconsistent.

A process for emergency changes could be:

- **Log the Emergency:** The emergency change is immediately logged, even if informally (e.g., in a bug tracker with a "critical" flag).
- **Authorize the Fix:** Authorization for an emergency fix is obtained from a designated authority (e.g., technical lead or project manager).
- **Implement the "Quick Fix":** A developer implements the minimum change necessary to resolve the emergency. This fix is tested and deployed. The code for the fix is clearly marked as temporary.
- **Create Formal Change Request:** A formal Change Request (CR) is created immediately after the emergency, referencing the quick fix.
- **Formal Process:** The CR goes through the standard change management process. This involves analyzing the problem properly and designing a robust, long-term solution.
- **Re-implement the Fix:** The temporary "quick fix" is replaced by the properly designed and tested solution from the formal change process. The requirements document is updated at this stage to reflect the change. This ensures that the documentation and implementation are brought back into sync.

### 10. Exercise 10

> You have taken a job with a software user who has contracted your previous employer to develop a system for them. You discover that your company’s interpretation of the requirements is different from the interpretation taken by your previous employer. Discuss what you should do in such a situation. You know that the costs to your current employer will increase if the ambiguities are not resolved. However, you have also a responsibility of confidentiality to your previous employer.

This is a classic ethical dilemma involving conflicting responsibilities.

**Responsibilities:**

- **To the current employer:** A duty to act in their best interests, which includes pointing out risks that could lead to increased costs or project failure.
- **To the previous employer:** A duty of confidentiality, which typically means not disclosing proprietary information, including internal discussions or interpretations about a project.

**Course of Action:** The professional and ethical approach is to address the ambiguity without revealing confidential information from the previous employer.

1. **Do not disclose the previous employer's interpretation:** Directly stating "My old company interpreted this requirement differently" would be a breach of confidentiality.
2. **Raise the ambiguity as a risk:** Approach your current manager and state that you have identified a potential ambiguity in a specific requirement. Frame it as your own analysis based on your expertise.
3. **Focus on the requirement itself:** Say something like, "Requirement X could be interpreted in two ways: A and B. Our current plan assumes interpretation A. However, if the client is expecting interpretation B, this could lead to significant rework and cost overruns later. I recommend we seek immediate clarification from the client".
4. **Document the risk:** Formally document this as a project risk and propose the mitigation step of getting written clarification from the contracting party (your previous employer) and the end client.

By taking this approach, you fulfill your duty to your current employer by highlighting a critical risk, while also respecting your confidentiality agreement with your previous employer. You are flagging a problem with the requirements document itself, not disclosing the internal workings of another company.
