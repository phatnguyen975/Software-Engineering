<div align="center">
  <h1>Software Evolution</h1>
  <sub>December 16, 2025</sub>
</div>

## Exercises

### 1. Exercise 1

> Explain why a software system that is used in a real-world environment must change or become progressively less useful.

Software systems model segments of the real world (e.g., a business process). The real world is dynamic; business rules change, new technologies emerge, and user expectations evolve. If the software remains static while the environment changes, the "gap" between the system's capabilities and the user's needs widens, making the software progressively less useful. This concept is encapsulated in Lehman's law of "Continuing Change."

### 2. Exercise 2

> Explain the rationale underlying Lehman’s laws. Under what circumstances might the laws break down?

- **Rationale:** Lehman's laws describe the behavior of "E-type" systems (systems operating in the real world). They are derived from the observation that these systems are subject to feedback loops from users and the organization. For example, "Increasing Complexity" occurs because developers naturally add code to fix immediate problems without taking time to restructure the system, causing entropy to increase.
- **Break down:** The laws might break down in systems that are not "E-type" systems, such as strictly mathematical software where the problem definition is static. They might also apply less to systems developed using agile methodologies where refactoring (simplification) is a continuous, integral part of the process, potentially countering the law of increasing complexity.

### 3. Exercise 3

> From Figure 9.4, you can see that impact analysis is an important subprocess in the software evolution process. Using a diagram, suggest what activities might be involved in change impact analysis.

A process flow for impact analysis would include:

1. **Identify affected components:** Trace the change request to specific requirements, design elements, and code modules.
2. **Trace dependencies:** Identify other components that depend on the affected modules.
3. **Assess platform impact:** Determine if the change requires hardware or operating system updates.
4. **Estimate resources:** Calculate the time, cost, and personnel required.
5. **Risk analysis:** Evaluate the risk of introducing new faults or degrading performance.
6. **Report:** Produce an impact statement summarizing costs and risks.

### 4. Exercise 4

> Suggest how you might set up a program to analyze the maintenance process and discover appropriate maintainability metrics for your company.

1. **Baseline Data Collection:** Start by recording data on current maintenance efforts: time taken to fix bugs, time to implement new features, and the number of outstanding change requests.
2. **Component Analysis:** Correlate this effort data with characteristics of the system components (e.g., cyclomatic complexity, size in lines of code, coupling).
3. **Identify Correlations:** Look for patterns. For example, do components with high complexity ratings consistently require more time to fix?
4. **Define Metrics:** Based on these correlations, define specific metrics (e.g., "Complexity > X implies High Maintenance Risk").
5. **Monitor:** Continuously track these metrics to predict costs for future maintenance tasks.

### 5. Exercise 5

> Briefly describe the three main types of software maintenance. Why is it sometimes difficult to distinguish between them?

1. **Fault Repair (Corrective):** Fixing bugs or errors in the code.
2. **Environmental Adaptation (Adaptive):** Modifying the software to run on new hardware, OS, or with new middleware.
3. **Functionality Addition (Perfective):** Adding new features to support business changes. Difficulty: It is difficult to distinguish them because a single change often involves elements of all three. For example, a user might report a "bug" (fault repair) which is actually a misunderstanding of functionality, leading to a requirement change (functionality addition). Or, adapting to a new OS (adaptive) might enable a new user interface feature (functionality).

### 6. Exercise 6

> What are the principal factors that affect the costs of system reengineering?

1. **Quality of the original software:** Is it modular or spaghetti code?
2. **Tool support:** Are there automated tools available for the specific languages involved?
3. **Extent of data conversion:** Does the database schema need to change significantly?
4. **Availability of expert staff:** Are the original developers or experts in the legacy language available?

### 7. Exercise 7

> Under what circumstances might an organization decide to scrap a system when the system assessment suggests that it is of high quality and of high business value?

This might happen if:

- **Standardization:** The organization decides to standardize on a specific technology platform (e.g., moving everything to SAP or the Cloud) for strategic reasons, even if the current custom system works well.
- **Mergers/Acquisitions:** Following a merger, the organization may decide to consolidate onto the other partner's systems to unify processes, even if the legacy system is technically superior.
- **Regulatory Change:** A new regulation might require a fundamentally different architectural approach that the current system cannot support despite its quality.

### 8. Exercise 8

> What are the strategic options for legacy system evolution? When would you normally replace all or part of a system rather than continue maintenance of the software?

**Options:** Scrap, Maintain, Reengineer, Replace.

**You typically replace a system when:**

- The hardware/platform is becoming obsolete and cannot be supported.
- The maintenance costs have become prohibitive (e.g., higher than the cost of a new system).
- Off-the-shelf (COTS) alternatives are available that offer the same functionality at a much lower cost.
- The system is structurally degraded to the point where reengineering is not technically viable.

### 9. Exercise 9

> Explain why problems with support software might mean that an organization has to replace its legacy systems.

Legacy systems often rely on specific versions of compilers, operating systems, or database management systems. If the vendors of this support software stop supporting these old versions (e.g., no longer providing security patches), the organization is left vulnerable. Upgrading the support software might break the legacy system. If the cost of modifying the legacy system to work with new support software is too high, the entire system must be replaced.

### 10. Exercise 10

> Do software engineers have a professional responsibility to produce code that can be maintained and changed even if this is not explicitly requested by their employer?

Yes. According to professional codes of ethics (like ACM/IEEE), engineers have a responsibility to produce work of high quality. Unmaintainable code acts as "technical debt" that harms the employer in the long run through excessive costs and risks. Producing code that is purposefully difficult to maintain or obscure is unprofessional. However, engineers must balance this with business constraints (deadlines); if a quick-and-dirty solution is explicitly required for a prototype or emergency fix, the engineer should document the debt and advocate for future refactoring.
