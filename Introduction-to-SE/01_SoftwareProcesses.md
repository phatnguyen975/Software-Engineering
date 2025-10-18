<div align="center">
  <h1>Software Processes</h1>
  <sub>October 14, 2025</sub>
</div>

This chapter introduces the concept of a software process as a structured set of activities required to develop a software system. All software processes include four fundamental activities: **software specification, software design and implementation, software validation, and software evolution**.

## Software Process Models

A **software process model** is a simplified, abstract representation of a software process. Each model represents a different approach to software development and can be seen as a framework that can be adapted. The three generic models discussed are the waterfall model, incremental development, and reuse-oriented software engineering.

### 1. The Waterfall Model

This is a plan-driven model that represents fundamental development activities as separate, sequential phases, such as requirements specification, software design, implementation, and testing. In principle, one phase must be completed before the next begins.

**Phases:**

1. Requirements analysis and definition
2. System and software design
3. Implementation and unit testing
4. Integration and system testing
5. Operation and maintenance

**Pros:** It is a plan-driven model where documentation is produced at each stage, making the process visible to managers.

**Cons:** It is inflexible and makes it difficult to respond to changing customer requirements because commitments must be made early in the process.

### 2. Incremental Development

This model interleaves the activities of specification, development, and validation. The system is developed as a series of versions (increments), with each version adding functionality to the previous one.

**Benefits:**

- The cost of accommodating changing customer requirements is reduced.
- It is easier to get customer feedback on the development work.
- Useful software can be delivered to the customer more rapidly.

**Problems:**

- The process is not highly visible, making it harder for managers to measure progress.
- The system structure can degrade as new increments are added without refactoring.

### 3. Reuse-Oriented Software Engineering

This approach is based on the existence of a significant number of reusable components, and the development process focuses on integrating these components rather than developing them from scratch.

**Phases:**

1. **Component analysis:** A search is made for components to implement the requirements specification.
2. **Requirements modification:** The requirements are analyzed and 3odified to reflect the available components.
3. **System design with reuse:** The framework of the system is designed, taking the reused components into account.
4. **Development and integration:** The components are integrated to create the new system.

**Pros:** This approach reduces the amount of software to be developed, leading to lower costs, reduced risk, and faster delivery.

## Fundamental Process Activities

All software processes include four basic activities, which are organized differently depending on the process model used.

- **Software Specification (Requirements Engineering):** The process of understanding and defining what services are required from the system and identifying the constraints on its operation. It involves a feasibility study, requirements elicitation and analysis, requirements specification, and requirements validation.
- **Software Design and Implementation:** The process of converting a system specification into an executable system. This stage involves architectural design, interface design, component design, and database design, followed by programming and debugging.
- **Software Validation (V&V):** This process is intended to show that a system conforms to its specification and meets the customer's expectations. The main validation technique is program testing, which typically occurs in three stages: development testing, release testing, and user testing.
- **Software Evolution:** This involves modifying the software to reflect changing customer and market requirements. Development and maintenance are increasingly seen as a continuum, where software evolves throughout its lifetime.

## Coping with Change

Change is inevitable in large software projects. Processes must be able to accommodate change to reduce the cost of rework. Two related approaches for this are **change avoidance** (anticipating changes) and **change tolerance** (designing the process so that changes are low-cost).

- **Prototyping:** An initial version of a system is developed quickly to demonstrate concepts, try out design options, and clarify requirements. This is a key change avoidance technique.
- **Incremental Delivery:** System increments are delivered to the customer for use in their operational environment. This allows customers to gain experience with the system and provide feedback for later increments. It supports both change avoidance and change tolerance.
- **Boehm's Spiral Model:** This is a risk-driven software process model where the process is represented as a spiral. Each loop in the spiral represents a phase of the process and includes objective setting, risk assessment, development, and planning. It explicitly recognizes risk and combines change avoidance with change tolerance.

## The Rational Unified Process (RUP)

The RUP is a modern, hybrid process model that brings together elements from all of the generic process models. It is described from three perspectives:

1. **A dynamic perspective:** Shows the phases of the model over time. The four main phases are **Inception, Elaboration, Construction, and Transition**.
2. **A static perspective:** Shows the process activities, called **workflows**, that are enacted. There are six core process workflows (e.g., Requirements, Analysis and Design, Testing) and three core supporting workflows (e.g., Project Management, Configuration Management).
3. **A practice perspective:** Suggests six fundamental best practices, such as developing software iteratively, managing requirements, and using component-based architectures.

## Exercises

### 1. Exercise 1

> Suggest the most appropriate generic software process model for the following systems, giving reasons for your answer.

**A system to control anti-lock braking in a car**

- **Model:** The **waterfall model** or a formal, plan-driven process.
- **Reason:** This is a safety-critical system where requirements are well-understood and unlikely to change, as they are determined by the physical hardware of the car. A plan-driven approach allows for a complete and analyzable specification to be developed, which is crucial for safety analysis before any implementation begins.

**A virtual reality system to support software maintenance**

- **Model:** **Incremental development** with prototyping.
- **Reason:** This is a highly interactive system where the requirements for the user interface and functionality are difficult to specify in advance. An incremental approach allows users to experiment with early versions and provide rapid feedback, which is essential for developing a usable and effective system.

**A university accounting system that replaces an existing system**

- **Model:** A hybrid approach, combining a **plan-driven model with incremental development**.
- **Reason:** Many core requirements will be well-understood from the existing system, making a plan-driven approach suitable for these parts. However, the system is large, and new features or user interface requirements will evolve. For these parts, an incremental approach allows for flexibility and user feedback. The text notes that combining the best features of different models is sensible for large systems.

**An interactive travel planning system that helps users plan journeys with the lowest environmental impact**

- **Model:** **Incremental development** or an agile process.
- **Reason:** This is an interactive system where requirements will likely change as users provide feedback. An incremental approach allows for rapid delivery of useful features (e.g., basic journey planning) and allows the system to evolve based on user priorities and feedback on new features like environmental impact calculations.

### 2. Exercise 2

> Explain why incremental development is the most effective approach for developing business software systems. Why is this model less appropriate for real-time systems engineering?

**For business systems:** Incremental development is effective because business environments change rapidly, leading to unstable software requirements. The incremental model is well-suited for this because:

- It is cheaper to accommodate changing customer requirements than in a waterfall model.
- It allows customers to provide feedback on working software increments, which is more effective than reviewing design documents.
- It enables the rapid delivery and deployment of useful software, allowing the business to gain value from the system earlier.

**For real-time systems:** This model is less appropriate for real-time systems because these systems often have stringent safety and reliability requirements that must be analyzed for the entire system before development begins. An incremental approach makes it difficult to assess emergent properties related to timing and safety, as critical interactions between components might be missed until late in the development process. For safety-critical systems, a complete specification is usually required upfront.

### 3. Exercise 3

> Consider the reuse-based process model shown in Figure 2.3. Explain why it is essential to have two separate requirements engineering activities in the process.

The reuse-based model includes an initial **Requirements Specification** phase and a later **Requirements Modification** phase. Both are essential for different reasons:

- **Initial Requirements Specification:** This phase is necessary to establish an initial understanding of what the customer wants. This provides the criteria needed to begin the search for suitable reusable components. Without an initial specification, there would be no basis for the component analysis.
- **Requirements Modification:** This phase is crucial because it is very unlikely that off-the-shelf components will be an exact match for the initial requirements. This activity involves analyzing the discovered components and then compromising and changing the initial requirements to align with what the available components can provide. This allows the project to gain the benefits of reuse (cost, speed) while still delivering a system that meets the customer's essential needs.

### 4. Exercise 4

> Suggest why it is important to make a distinction between developing the user requirements and developing system requirements in the requirements engineering process.

It is important to distinguish between user and system requirements because they are intended for different audiences and serve different purposes:

- **User Requirements:** These are high-level, abstract statements written in natural language for the **customer, end-users, and client managers**. Their purpose is to define the system's expected services and constraints in a way that is understandable to readers without a technical background. This allows for agreement on the system's objectives at a high level.
- **System Requirements:** These are more detailed and precise descriptions of the system's functions and operational constraints, intended for **system architects and software developers**. Their purpose is to provide a detailed specification that can serve as a basis for system design and implementation. This level of detail is often part of the contract between the system buyer and developers.

### 5. Exercise 5

> Describe the main activities in the software design process and the outputs of these activities. Using a diagram, show possible relationships between the outputs of these activities.

According to the model in the chapter, the main design activities for an information system are:

1. **Architectural design:** Identifying the overall structure of the system and its principal components.
2. **Interface design:** Defining the interfaces between the system components.
3. **Component design:** Designing the operation of individual components.
4. **Database design:** Designing the system's data structures and how they are represented in a database.

The outputs of these activities are:

- System Architecture
- Interface Specification
- Component Specification
- Database Specification

The diagram below shows the relationships between these outputs. The System Architecture is the primary output that influences all others. The Interface and Component Specifications are dependent on the architecture, and the Component Specification is also dependent on the interfaces it must implement.

```text
graph TD
    A[System Architecture] --> B(Interface Specification);
    A --> C(Component Specification);
    A --> D(Database Specification);
    B --> C;
    C --> D;
```

- The **System Architecture** defines the components, so it must be created before the **Interface, Component, and Database Specifications**.
- The **Interface Specification** defines how components in the architecture interact, so it influences the detailed **Component Specification**.
- The **Component Specification** details each component and must conform to its specified interface.
- The **Database Specification** is also derived from the architecture and may be influenced by the data needs of individual components.

### 6. Exercise 6

> Explain why change is inevitable in complex systems and give examples (apart from prototyping and incremental delivery) of software process activities that help predict changes and make the software being developed more resilient to change.

Change is inevitable in large software projects because the system's environment is constantly changing. Businesses must respond to new market opportunities and competitive pressures, and management priorities can shift. Furthermore, as users gain experience with a system, their understanding of what they need evolves, leading to new and changed requirements.

Examples of activities that help manage change (other than prototyping and incremental delivery):

- **Formal Change Management:** Implementing a formal process to manage change requests. This process involves analyzing the impact and cost of a proposed change, allowing for controlled and managed evolution rather than chaotic updates.
- **Traceability Management:** Maintaining links between requirements, design components, and test cases. This helps in performing impact analysis when a change is proposed, making it easier to see which parts of the system will be affected.
- **Refactoring:** This is the process of continuously improving the internal structure of the software without changing its external behavior. A well-structured system that has been regularly refactored is easier and safer to change, making it more resilient to future requirements modifications.

### 7. Exercise 7

> Explain why systems developed as prototypes should not normally be used as production systems.

Systems developed as prototypes (specifically, throwaway prototypes) should not be used as production systems for several reasons:

- **Poor Non-Functional Characteristics:** During prototyping, non-functional requirements such as performance, reliability, and security are often ignored to speed up development. It may be impossible to re-engineer the prototype to meet these essential production standards.
- **Lack of Documentation:** Prototypes are developed rapidly, and the focus is on demonstrating functionality, not on creating documentation. The prototype's code is often the only design specification, which is inadequate for long-term system maintenance.
- **Degraded Structure:** The process of rapid, iterative change during prototyping often leads to a degraded and poorly structured system. This makes the system difficult and expensive to maintain and evolve.
- **Lower Quality Standards:** To accelerate development, normal organizational quality standards for code and documentation are usually relaxed for prototypes. The resulting system is therefore not of production quality.

### 8. Exercise 8

> Explain why Boehm's spiral model is an adaptable model that can support both change avoidance and change tolerance activities. In practice, this model has not been widely used. Suggest why this might be the case.

**Adaptability of the Spiral Model:** The spiral model is highly adaptable because it is an iterative, risk-driven process. Each loop in the spiral includes explicit risk assessment, which allows the process to be tailored to the specific project needs and uncertainties.

- It supports **change avoidance** because the risk assessment phase in each loop can identify requirements uncertainty as a major risk. To counter this, activities like prototyping can be initiated to clarify requirements before committing to detailed development, thus avoiding later changes.
- It supports **change tolerance** because the development model for each loop is chosen after the risk analysis. This means the process can be adapted to accommodate changes that arise, for example, by choosing an incremental development approach for a particular loop.

**Reasons for Limited Use:**

- **Complexity:** The model is complex and demands a high level of expertise in risk assessment and management, which can be a significant overhead.
- **Contractual Difficulties:** It works best for internal, large-scale projects. It is difficult to use with external clients because it does not provide a fixed budget or a detailed specification upfront, making it incompatible with fixed-price contracts.
- **Reliance on Risk Assessment:** The model's success depends heavily on the quality of the risk assessment. If risks are not properly identified and managed, the entire process can fail.

### 9. Exercise 9

> What are the advantages of providing static and dynamic views of the software process as in the Rational Unified Process?

The main advantage of separating the dynamic (phases) and static (workflows/activities) views in the RUP is the **flexibility and realism** it brings to modeling the software process. It breaks the rigid link between process activities and project phases found in the waterfall model.

This separation provides several benefits:

- It recognizes that technical activities (static workflows like **Requirements, Design, and Testing**) are not confined to a single, discrete project phase but can occur concurrently and iteratively throughout the entire life cycle.
- The **dynamic view** (Inception, Elaboration, Construction, Transition) is useful for project managers and customers, as it focuses on business goals and project milestones.
- The **static view** (workflows) is useful for engineers, as it focuses on the technical activities they need to perform.
- This separation of concerns allows the process to be adapted. For example, a team can perform implementation and testing activities during the Elaboration phase to create a prototype, without violating the overall process model.

### 10. Exercise 10

> Historically, the introduction of technology has caused profound changes in the labor market and, temporarily at least, displaced people from jobs. Discuss whether the introduction of extensive process automation is likely to have the same consequences for software engineers. If you don't think it will, explain why not. If you think that it will reduce job opportunities, is it ethical for the engineers affected to passively or actively resist the introduction of this technology?

**Impact of Automation on Software Engineers:** Extensive process automation, through tools like advanced CASE tools, will likely **change the nature of a software engineer's job rather than eliminate it**.

- **Argument against displacement:** Software engineering is fundamentally a creative and problem-solving discipline that relies on human judgment and decision-making. Automation can handle repetitive and formalizable tasks (e.g., code generation from models, running tests, system builds), but it cannot replace the core activities of requirements negotiation, architectural design, and complex debugging. Instead, automation acts as a tool that can free up engineers from mundane tasks, allowing them to focus on more complex, higher-value activities. As the demand for increasingly complex software continues to grow, the need for skilled engineers to manage this complexity will likely persist.
- **Argument for job changes:** The roles of some engineers may be displaced. For instance, those who primarily write simple code from detailed specifications or perform manual testing may find their roles reduced. The skill set required will shift towards more abstract thinking, modeling, and managing complex automated systems. Engineers who fail to adapt their skills may be displaced.

**Ethical Considerations of Resistance:** Resisting technological advancement purely to preserve existing jobs is ethically questionable from a professional standpoint.

- According to professional codes of conduct, engineers have a primary responsibility to act in the **public interest**. Resisting automation that leads to better, cheaper, and more reliable software would conflict with this principle.
- A more ethical and professional response would be to **adapt and evolve**. Engineers should embrace new technologies, learn the skills required to use them effectively, and help guide their implementation to ensure that quality and safety are maintained or improved.
- However, if an engineer believes that a specific implementation of automation is flawed and will lead to lower-quality or unsafe products, they have an ethical duty to raise these concerns. In this case, resistance is not about job preservation but about upholding professional standards and protecting the public.
