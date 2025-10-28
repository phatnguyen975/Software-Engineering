<div align="center">
  <h1>Software Modeling</h1>
  <sub>November 04, 2025</sub>
</div>

This chapter introduces **system modeling**, the process of creating abstract models of a software system, usually using graphical notations like the Unifi**ed Modeling Language (UML)**. Models are used throughout the software process:

- During **requirements engineering** to understand existing systems and clarify requirements for new systems.
- During **design** to explain the proposed system, discuss design proposals, and document the system for implementation.
- After **implementation** to document the system's structure and operation.

Models are abstractions, deliberately omitting detail to focus on salient characteristics. Different models represent different **perspectives**:

1. **External:** Modeling the system's context or environment.
2. **Interaction:** Modeling interactions between the system and its environment, or between system components.
3. **Structural:** Modeling the system's organization or data structure.
4. **Behavioral:** Modeling the system's dynamic behavior and response to events.

The chapter focuses on five key UML diagram types:

- **Activity diagrams:** Show activities in a process.
- **Use case diagrams**: Show system-environment interactions.
- **Sequence diagrams:** Show interactions between actors and objects over time.
- **Class diagrams:** Show object classes and their associations.
- **State diagrams:** Show how the system reacts to events.

Graphical models can be used informally for discussion, formally for documentation, or as detailed descriptions for code generation (as in Model-Driven Engineering).

## Context Models

These models define the system's boundaries and its relationships with other systems in its environment. They are often represented using simple block diagrams or architectural models (e.g., Figure 5.1 showing the MHC-PMS context). UML **Activity diagrams** can supplement context models by showing the business processes in which the system is used (e.g., Figure 5.2).

## Interaction Models

These models represent interactions between users and the system, system components, or different systems.

- **Use Case Modeling:** Shows interactions between the system and external actors (users or other systems). Each use case represents a discrete task (e.g., Figure 5.3, 5.5). Use cases are typically supplemented with tabular or textual descriptions (e.g., Figure 5.4).
- **Sequence Diagrams:** Show the sequence of interactions between actors and objects over time during a specific use case. They depict objects, lifelines, messages passed between objects, and can show alternatives (alt) and object creation (e.g., Figure 5.6, 5.7).

## Structural Models

These models display the static or dynamic organization of a system in terms of its components and their relationships.

**Class Diagrams:** Show the object classes in the system and the associations (relationships) between them. They can show **multiplicity** (how many objects are involved, Figure 5.8, 5.9), **attributes** and **operations** of a class (Figure 5.10), **generalization** (inheritance, Figure 5.11, 5.12), and **aggregation** (composition, Figure 5.13).

## Behavioral Models

These models show the dynamic behavior of the system as it executes, responding to data or events.

- **Data-Driven Modeling:** Shows the sequence of actions involved in processing input data to generate an output. UML **Activity diagrams** (e.g., Figure 5.14) can show processing steps and data flow. **Sequence diagrams** (Figure 5.15) can also show sequential processing, emphasizing objects rather than functions.
- **Event-Driven Modeling:** Shows how a system responds to internal or external events, often by transitioning between states. UML **State diagrams** (based on Statecharts) show system states, events causing transitions, and actions taken in states (e.g., Figure 5.16, 5.17). **Superstates** can be used to manage complexity by encapsulating sub-states (Figure 5.18).

## Model-Driven Engineering (MDE)

MDE is an approach where models are the primary development outputs, and executable code is generated automatically from them. It aims to raise the level of abstraction. **Model-Driven Architecture (MDA)** is a specific MDE approach using UML subsets.

- **MDA Model Types:**
  - **Computation Independent Model (CIM):** Models domain abstractions.
  - **Platform Independent Model (PIM):** Models system operation without reference to implementation platform (usually UML).
  - **Platform Specific Model (PSM):** Transformation of the PIM for a specific platform.
- **Transformations:** Tools may automatically transform models between levels (CIM -> PIM -> PSM -> Code). PIM to PSM transformation is more mature than CIM to PIM.
- **Executable UML (xUML):** A subset of UML 2 designed to enable automated model transformation to code, using Domain, Class, and State models.

## Exercises

### 1. Exercise 1

> Explain why it is important to model the context of a system that is being developed. Give two examples of possible errors that could arise if software engineers do not understand the system context.

It's important to model the context to define the **system boundaries** and understand the system's **relationships** and **dependencies** with its environment (other systems, users, processes). This helps ensure the system delivers the required functionality and interoperates correctly.

Two possible errors from not understanding the context:

1. **Omission Errors:** The engineers might wrongly assume some functionality is provided by another system in the environment, leading to the new system lacking essential features. For example, assuming an external patient record system provides allergy information when it doesn't, resulting in the new system failing to check for allergies.
2. **Interface Errors:** The engineers might make incorrect assumptions about the interface, data format, or protocol used by other systems in the environment. For example, assuming an external appointments system provides data in XML when it actually uses JSON, leading to communication failures.

### 2. Exercise 2

> How might you use a model of a system that already exists? Explain why it is not always necessary for such a system model to be complete and correct. Would the same be true if you were developing a model of a new system?

Models of existing systems are used during requirements engineering to:

- Help **clarify what the existing system does**.
- Act as a **basis for discussing strengths and weaknesse**s, leading to requirements for the new system.
- Help **understand the system's context and interfaces**.
- Serve as a **baseline for planning migration** to a new system.

It's not always necessary for such a model to be complete and correct because its primary purpose might be to facilitate discussion or understand specific aspects of the system. If used just to stimulate discussion about potential changes, highlighting key areas might be sufficient, and minor inaccuracies or omissions might not hinder the goal.

For a **new system**, the situation is different. If the model is used:

- **For discussion:** It can still be incomplete or informal.
- **As documentation:** It needs to be correct and accurate for the parts it covers.
- **For code generation (MDE):** It must be complete and correct, as the implementation is derived directly from it. If the model used for implementation is incorrect or incomplete, the resulting system will be flawed.

### 3. Exercise 3

### 4. Exercise 4

### 5. Exercise 5

### 6. Exercise 6

### 7. Exercise 7

### 8. Exercise 8

### 9. Exercise 9

### 10. Exercise 10

> You are a software engineering manager and your team proposes that model-driven engineering should be used to develop a new system. What factors should you take into account when deciding whether or not to introduce this new approach to software development?

Factors to consider:

- **Team Skills and Training:** Does the team possess the necessary skills in modeling, UML/xUML, MDE tools, and abstraction? Is training available and budgeted for?
- **Tool Availability and Maturity:** Are mature, reliable MDE tools available that support the required target platform and application domain? What are the tool costs (licensing, maintenance)? Do the tools generate complete and efficient code?
- **Type of System:** Is the system in a domain where MDE has proven successful (e.g., embedded systems, systems with well-defined domains)? Is it a large, long-lifetime system where platform independence is a significant benefit? MDE might be less suitable for UI-intensive or rapidly evolving web applications.
- **Development Process:** Is the organization's current process compatible with an MDE approach? MDE often implies a more plan-driven, upfront modeling effort, which might conflict with agile practices.
- **Integration with Existing Systems:** Will the generated code need to integrate with legacy systems or systems developed using different approaches? How well do the MDE tools support this?
- **Quality Requirements:** Can MDE help achieve specific non-functional requirements like dependability or security? Does the approach make testing and validation easier or harder? (Testing generated code can be complex).
- **Costs vs. Benefits:** What are the expected benefits (e.g., faster development, platform independence, higher abstraction) versus the costs (tools, training, potential inefficiencies in generated code, vendor lock-in)? Is the potential return on investment justified for this specific project?
- **Maintainability:** Will the generated code be maintainable? Will future maintenance require modifications at the model level or the code level? What skills will the maintenance team need?
