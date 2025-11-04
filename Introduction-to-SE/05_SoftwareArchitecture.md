<div align="center">
  <h1>Software Architecture</h1>
  <sub>November 11, 2025</sub>
</div>

This chapter introduces **architectural design** as the process of understanding how a system should be organized and designing its overall structure. It is the critical link between requirements engineering and detailed design, identifying the main structural components and their relationships. The output of this process is an architectural model.

## Architectural Design Decisions

Architectural design is a creative process focused on making a series of fundamental decisions about the system's structure. The architecture has a profound effect on the system's non-functional requirements.

Key decisions include:

- **Generic Architecture:** Is there a generic application architecture (e.g., for an information system) that can be used?
- **Distribution:** How will the system be distributed across hardware or processor cores?
- **Patterns:** What architectural patterns or styles are appropriate (e.g., Client-Server, Layered)?
- **Structure and Decomposition:** What fundamental approach will be used to structure the system and decompose components?
- **Control Strategy:** How will the operation of components be controlled?
- **Non-Functional Requirements:** How will the architecture achieve the non-functional requirements? The architecture is the dominant influence on attributes like:
  - **Performance:** Achieved by localizing critical operations within a few components.
  - **Security:** Often achieved using a layered architecture, protecting assets in the innermost layers.
  - **Safety:** Achieved by locating safety-related operations in a small number of components.
  - **Availability:** Achieved by designing in redundant components.
  - **Maintainability:** Achieved by using fine-grain, self-contained components. A designer must manage conflicts between these requirements (e.g., the large components needed for performance may be less maintainable).
- **Evaluation and Documentation:** How will the design be evaluated and documented?

The advantages of an explicit architecture include facilitating **stakeholder communication**, enabling **system analysis**, and supporting **large-scale reuse**.

## Architectural Views

It is impossible to represent all relevant information about a system’s architecture in a single model. Therefore, multiple **views** (perspectives) are used for both design and documentation.

A well-known model is **Krutchen’s 4+1 view model**:

1. **Logical view:** Shows the key abstractions, such as objects and object classes.
2. **Process view:** Shows the interacting processes at runtime, which is useful for assessing performance and availability.
3. **Development view:** Shows how the software is decomposed for development by teams.
4. **Physical view:** Shows the system hardware and how software components are distributed across processors.
5. **Use cases (the +1):** Scenarios that relate the other four views.

Hofmeister et al. also suggest a **Conceptual view**, which is an abstract view used for requirements decomposition and reuse.

Simple block diagrams are often used for communication , but more formal notations like UML or specialized **Architectural Description Languages (ADLs)** can be used for detailed documentation.

## Architectural Patterns

Architectural patterns (or styles) are abstract descriptions of good, reusable design practices. They describe a system organization and its strengths and weaknesses.

- **Model-View-Controller (MVC) Pattern:** Separates presentation and interaction from the system data.
  - **Model:** Manages the system data and associated operations.
  - **View:** Manages how the data is presented to the user.
  - **Controller:** Manages user interaction and passes these to the View and Model.
  - **Used for:** Interactive systems with multiple data views (e.g., web applications).
- **Layered Architecture Pattern:** Organizes the system into layers, where each layer provides services only to the layer immediately above it.
  - **Used for:** Building new facilities on existing systems or when development is spread across teams. It supports separation of concerns and incremental development.
  - **Example:** A generic information system (Figure 6.16) or the LIBSYS library system (Figure 6.7).
- **Repository Pattern:** Organizes the system so that all data is managed in a central repository, accessible to all components. Components do not interact directly.
  - **Used for:** Systems that generate large volumes of data that must be stored for a long time (e.g., CAD systems, IDEs).
  - **Disadvantage:** The repository is a single point of failure.
- **Client-Server Pattern:** Organizes the system as a set of services (provided by servers) and clients that access those services.
  - **Used for:** Distributed systems where data is shared (e.g., a film library).
  - **Advantage:** It is a distributed architecture.
- **Pipe and Filter Pattern:** Organizes the system as a series of processing components (filters) that transform data, with data flowing sequentially (through pipes) from one component to another.
  - **Used for:** Data processing applications (e.g., invoice processing).

## Application Architectures

These are generic architectures that describe the structure of systems in a specific application domain (e.g., business systems, real-time systems). They serve as a starting point for design, a checklist, or a way to organize development.

- **Transaction Processing Applications:** Database-centered systems that process user requests for information and updates (e.g., banking systems, e-commerce). A transaction is an atomic (all-or-nothing) sequence of operations.
- **Information Systems:** A type of transaction processing system that provides controlled access to a large database (e.g., library catalog, MHC-PMS). They often use a layered architecture (Figure 6.16) and are frequently implemented as multi-tier client-server systems.
- **Language Processing Systems:** Systems that translate a formal language into another representation (e.g., compilers). The generic architecture (Figure 6.18) includes a **translator** and an **interpreter**. A compiler's architecture (Figure 6.19) is often a pipe-and-filter model (lexical analysis, syntactic analysis, semantic analysis, code generation) that shares a repository (symbol table, syntax tree).

## Exercises

### 1. Exercise 1

> When describing a system, explain why you may have to design the system architecture before the requirements specification is complete.

You may have to design the system architecture before the requirements specification is complete for two main reasons:

1. To structure the requirements: Architectural decomposition is often necessary to structure and organize the requirements specification. By identifying the main sub-systems, you can group related requirements, making the specification easier to understand and manage.
2. To facilitate stakeholder communication: A high-level architectural model is a useful framework for discussion with system stakeholders. It provides a visual representation that stakeholders can understand, allowing them to provide feedback on the requirements and design proposals.

### 2. Exercise 2

> You have been asked to prepare and deliver a presentation to a non-technical manager to justify the hiring of a system architect for a new project. Write a list of bullet points setting out the key points in your presentation. Naturally, you have to explain what is meant by system architecture.

**Subject: Justifying the Role of a System Architect**

- **What is System Architecture?** The architecture is the high-level "blueprint" for the software. It defines the main building blocks of the system and how they fit and work together to deliver the final product.
- **Why Do We Need an Architect?**
  - **To Manage Complexity:** The architect creates a high-level plan that hides unnecessary detail, allowing the team to focus on the key parts of the system and how they connect.
  - **To Improve Communication:** The architectural plan is a "focus for discussion". It gives managers, customers, and developers a clear, shared picture of the system, preventing misunderstandings.
  - **To Meet Critical Requirements:** Critical goals like performance, security, and reliability are not add-ons; they depend directly on the system's architecture. The architect's main job is to design a structure that ensures these goals can be met.
  - **To Enable Smart Reuse:** The architect can identify opportunities for "large-scale reuse" by recognizing common patterns or using existing components, which saves time and money.
  - **To Make Key Technical Decisions:** The architect makes the critical technical choices early on, such as how the system will be distributed or what patterns to use, which prevents costly mistakes later in the project.

### 3. Exercise 3

> Explain why design conflicts might arise when designing an architecture for which both availability and security requirements are the most important non-functional requirements.

Conflicts arise because the typical architectural solutions for availability and security are often contradictory:

- **Availability** often requires **redundancy** - having multiple copies of components running. If one component fails, another takes over.
- **Security** often requires a **layered structure** where critical assets are isolated in inner layers and access is strictly controlled through a minimal number of "hardened" interfaces.

The conflict is this:

- Adding redundant components (for availability) **increases complexity and the number of system interfaces and connections**. This creates a larger "attack surface" for an attacker, potentially introducing new security vulnerabilities that are difficult to manage.
- Implementing strict security layers **makes redundancy difficult**. For example, switching over to a redundant component (for availability) might require bypassing or relaxing the strict access rules defined by the security layers, creating a potential security hole. Managing the secure state across multiple redundant components is also complex.

### 4. Exercise 4

> Draw diagrams showing a conceptual view and a process view of the architectures of the following systems:
>
> - An automated ticket-issuing system used by passengers at a railway station.
> - A computer-controlled video conferencing system that allows video, audio, and computer data to be visible to several participants at the same time.
> - A robot floor cleaner that is intended to clean relatively clear spaces such as corridors. The cleaner must be able to sense walls and other obstructions.

### 5. Exercise 5

> Explain why you normally use several architectural patterns when designing the architecture of a large system. Apart from the information about patterns that I have discussed in this chapter, what additional information might be useful when designing large systems?

**Why use several patterns:** Large systems are rarely monolithic; they are complex and heterogeneous. Different parts of the system often have different requirements. You might use:

- A **Layered Architecture** for the overall system structure.
- A **Client-Server** pattern to handle distributed access.
- An **MVC** pattern within the client to manage the user interface.
- A **Repository** pattern to manage shared data. The text explicitly notes that conflicts between non-functional requirements (like performance and maintainability) can sometimes be resolved by "using different architectural patterns or styles for different parts of the system".

**Additional useful information:**

- **Performance/Scalability trade-offs:** Detailed data on the network overhead and processing load associated with each pattern.
- **Known integration conflicts:** Which patterns are known to be difficult to combine (e.g., integrating two systems that both assume they control the main event loop ).
- **Implementation/Technology constraints:** Which platforms, middleware, or frameworks best support a given pattern.
- **Detailed NFR analysis:** More specific information on how each pattern supports or hinders specific NFRs (e.g., specific security vulnerabilities associated with a pattern).

### 6. Exercise 6

> Suggest an architecture for a system (such as iTunes) that is used to sell and distribute music on the Internet. What architectural patterns are the basis for this architecture?

The architecture would be a **multi-tier client-server system**.

- **Tier 1 (Client):** A rich, "fat-client" application (like the iTunes desktop app) or a "thin-client" (web browser). This handles presentation, user interaction, and local music library management.
- **Tier 2 (Application Server):** A set of application servers handling specific functions. This tier would likely be composed of multiple components or services.
- **Tier 3 (Database Server):** A set of database servers hosting the music catalog, customer accounts, and transaction data.

The architecture would be based on these patterns:

1. **Client-Server Pattern:** This is the fundamental architecture. The client application accesses services from the remote servers that manage the store and user accounts.
2. **Layered Architecture:** The server side would be layered, separating application logic (e.g., managing purchases) from data management. For example: Web Server (handling store UI) -> Transaction Server -> Account Database.
3. **Repository Pattern:** The music catalog and customer account information are stored in a massive central repository (database) that is accessed by the transaction, search, and store-front components.

### 7. Exercise 7

> Explain how you would use the reference model of CASE environments (available on the book’s web pages) to compare the IDEs offered by different vendors of a programming language such as Java.

### 8. Exercise 8

> Using the generic model of a language processing system presented here, design the architecture of a system that accepts natural language commands and translates these into database queries in a language such as SQL.

This system would use the **language processing system architecture** shown in Figure 6.19. It is a **Pipe and Filter** architecture combined with a **Repository**.

The components (filters) would be:

1. **Lexical Analyzer:** Breaks the natural language command (e.g., "Show me sales for June") into a stream of tokens ("Show", "me", "sales", "for", "June").
2. **Syntactic Analyzer:** Parses the token stream against a grammar for the natural language subset, creating a Syntax Tree that represents the command's structure.
3. **Semantic Analyzer:** This is the most complex component. It would "walk" the syntax tree and use a Repository (which would include a dictionary/ontology and a database schema) to map the natural language concepts to database concepts (e.g., "sales" maps to the `SALES_TABLE`, "June" maps to `WHERE month=6`).
4. **SQL Generator:** Traverses the semantically analyzed tree to generate the final SQL query (e.g., `SELECT * FROM SALES_TABLE WHERE month = 6;`).

The **Repository** (Symbol Table) would be critical, containing the mapping from words (like "sales", "June") to the database schema (tables, columns, and values).

### 9. Exercise 9

> Using the basic model of an information system, as presented in Figure 6.16, suggest the components that might be part of an information system that allows users to view information about flights arriving and departing from a particular airport.

Based on the layered model in Figure 6.16:

- **Layer 1: User Interface:** Web browsers (for public access), mobile app interfaces (for passengers), and dedicated terminal UIs (for airport staff).
- **Layer 2: User Communications / Data Management:**
  - **User Authentication** (for staff access).
  - **Query Manager** (to handle user searches by flight number, destination, or airline).
  - **Data Validation** (to check that inputs like flight numbers are in a valid format).
  - **Screen Generator** (to format the flight data into HTML tables for the web or app).
- **Layer 3: Information Retrieval / Application Logic:**
  - **Flight Data Manager** (handles the core logic of retrieving and updating flight info).
  - **Flight Status Updater** (a component that receives real-time updates from airline/air traffic control systems).
  - **Gate/Carousel Assigner** (a component for staff to assign gates and baggage carousels).
  - **Alert Generator** (to flag flights that are delayed or cancelled).
- **Layer 4: Transaction Management / Database:**
  - **Transaction Manager** (standard middleware).
  - **Flight Database** (storing flight schedules, originating/destination airports, expected/actual times, gate numbers, status (e.g., "On Time", "Delayed", "Landed"), and baggage carousel info).

### 10. Exercise 10

> Should there be a separate profession of "software architect" whose role is to work independently with a customer to design the software system architecture? A separate software company would then implement the system. What might be the difficulties of establishing such a profession?

**Arguments for:** The chapter emphasizes that architecture is a distinct, critical activity focused on NFRs , stakeholder communication , and reuse. A separate architect could, in theory, provide objective expertise in these areas, ensuring the "blueprint" is sound before implementation begins.

**Difficulties:**

1. **Intangibility and Change:** Software is intangible and requirements change. A building architect designs a static artifact. A software architect's design must evolve. If the architect is independent, the design will quickly become disconnected from the implementation, leading to "architectural drift" or irrelevance.
2. **Implementation Feedback:** Software design is not a single, upfront activity. Implementation often reveals design problems or performance issues that require architectural changes. An independent architect is not part of this feedback loop and cannot easily respond to implementation-time discoveries.
3. **Technical Knowledge:** The architect must understand the detailed implications of their design on the implementation platform, middleware, and available COTS components. An independent architect might create a design that is inefficient, overly complex, or impossible to implement, leading to an "architectural mismatch".
4. **Accountability:** If the project fails, who is to blame? The architect for a flawed design, or the implementer for failing to execute it properly? This separation creates a "blame gap" that is detrimental to the project.
5. **Process Mismatch:** This model is strictly plan-driven. It is completely incompatible with agile methods, which rely on incremental design and a cohesive, co-located team.
