<div align="center">
  <h1>Introduction to Cloud Architecture</h1>
  <sub>March 03, 2026</sub>
</div>

**Total:** 30 Questions (Single Choice)

---

**Question 1: How is "Cloud architecture" defined?**

A. The process of migrating physical servers to a virtualized environment. \
B. The practice of applying cloud characteristics to a solution that uses cloud services and features to meet an organization's technical needs and business use cases. \
C. The use of a single data center to store enterprise data. \
D. Building a private physical data center for an organization.

> **Answer: B** - Cloud architecture is the practice of applying cloud characteristics to a solution that uses cloud services and features to meet an organization's technical needs and business use cases.

**Question 2: As a Cloud Architect, which task belongs to the "Plan" phase?**

A. Analyze solutions for business needs and requirements. \
B. Design prototype solutions. \
C. Manage the adoption and migration. \
D. Investigate cloud services specifications.

> **Answer: A** - The Plan phase focuses on strategy and analysis (business needs, requirements, target outcomes) before building and operating.

**Question 3: How many pillars are in the AWS Well-Architected Framework?**

A. 4 \
B. 5 \
C. 6 \
D. 7

> **Answer: C** - AWS Well-Architected has six pillars: Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, and Sustainability.

**Question 4: Which pillar of the AWS Well-Architected Framework focuses on running and monitoring systems that deliver business value?**

A. Security \
B. Reliability \
C. Operational Excellence \
D. Cost Optimization

> **Answer: C** - Operational Excellence is about running and monitoring systems to deliver business value and continually improving processes and procedures.

**Question 5: "Apply security at all layers" is a practice belonging to which pillar?**

A. Performance Efficiency \
B. Security \
C. Sustainability \
D. Reliability

> **Answer: B** - “Apply security at all layers” is a core design principle of the Security pillar.

**Question 6: Which of the following is a principle of the "Reliability" pillar?**

A. Recover quickly. \
B. Democratize advanced technologies. \
C. Measure efficiency. \
D. Implement a strong identity foundation.

> **Answer: A** - “Recover quickly” aligns with the Reliability pillar’s focus on failure recovery, resilience, and meeting demand.

**Question 7: Which of the following is a principle of the "Performance Efficiency" pillar?**

A. Reduce downstream impact. \
B. Consider using managed services. \
C. Democratize advanced technologies. \
D. Mitigate disruptions.

> **Answer: C** - “Democratize advanced technologies” is one of the Performance Efficiency principles.

**Question 8: "Eliminate unneeded expense" is a principle of which pillar?**

A. Cost Optimization \
B. Sustainability \
C. Operational Excellence \
D. Reliability

> **Answer: A** - “Eliminate unneeded expense” is a Cost Optimization principle.

**Question 9: What is the main function of the AWS Well-Architected Tool?**

A. Automatically writes code for your applications. \
B. Automatically patches security vulnerabilities in your system. \
C. Helps you review the state of your workloads and compares them to the latest AWS architectural best practices. \
D. Manages your monthly AWS billing.

> **Answer: C** - The AWS Well-Architected Tool helps you assess workloads against AWS best practices and identify risks and improvements.

**Question 10: When designing a solution, which of the following is an example of "Design trade-offs"?**

A. Always choosing the most expensive solution to ensure safety. \
B. Trading consistency, durability, and space for time and latency to deliver higher performance. \
C. Reducing storage capacity but increasing the number of operations staff. \
D. Exclusively using free services.

> **Answer: B** - Architecture often requires trade-offs (e.g., between latency/performance and consistency/durability/cost).

**Question 11: To implement scalability when demand changes, which AWS service is typically used to scale out automatically?**

A. Amazon S3 \
B. Amazon CloudFront \
C. Amazon EC2 Auto Scaling \
D. AWS Well-Architected Tool

> **Answer: C** - Amazon EC2 Auto Scaling adjusts capacity automatically based on demand.

**Question 12: What is the main benefit of using Infrastructure as Code (IaC)?**

A. It reduces configuration errors from manual configuration. \
B. It writes application code faster. \
C. It reduces the monthly fee for Amazon S3. \
D. It completely prevents DDoS attacks.

> **Answer: A** - IaC makes infrastructure changes repeatable and versionable, reducing manual mistakes and configuration drift.

**Question 13: What action does the "Treating resources as disposable" principle encourage?**

A. Trying to fix a server when it crashes to save money. \
B. Keeping unused resources just in case of an emergency. \
C. Automating deployment of new resources with identical configurations and replacing old resources with updated ones. \
D. Only using physical hardware instead of virtual machines.

> **Answer: C** - Disposable resources means you replace (via automation) rather than “pet” and manually repair servers.

**Question 14: To design loosely coupled components between Web servers and Application servers, what is the best practice?**

A. Connecting every Web server directly to every Application server. \
B. Putting all components on the same server. \
C. Decoupling Web servers with Elastic Load Balancing (ELB). \
D. Removing the Application servers completely.

> **Answer: C** - Use load balancing to avoid tight coupling and to route traffic dynamically across healthy application instances.

**Question 15: The "Designing services, not servers" principle advises you to do what with static web assets?**

A. Store them on a Web server (EC2). \
B. Store them off server, such as on Amazon Simple Storage Service (Amazon S3). \
C. Delete them to save memory. \
D. Store them in a relational database.

> **Answer: B** - Static assets are better hosted by managed services like Amazon S3 (often paired with CloudFront) instead of tying them to a server.

**Question 16: Which of the following is NOT typically a criterion for choosing the right database solution?**

A. Read and write needs. \
B. Total storage requirements. \
C. The programming language of the user interface. \
D. Latency requirements.

> **Answer: C** - DB choice is driven by access patterns, scale, latency, consistency, and data model—not the UI’s programming language.

**Question 17: To avoid a single point of failure for a primary database server, what should you do?**

A. Reboot the server frequently. \
B. Create a secondary (standby) database server and replicate the data. \
C. Double the hard drive capacity of the current server. \
D. Only allow one user to access it at a time.

> **Answer: B** - Use redundancy (e.g., standby/replica; Multi-AZ where applicable) so the system can fail over if the primary fails.

**Question 18: To optimize for cost, which of the following is an important question to ask?**

A. How do I turn off resources that are not in use? \
B. How do I use the most complex hardware? \
C. How do I buy the cheapest physical server? \
D. How do I keep the application running 24/7 at maximum capacity?

> **Answer: A** - A key cost-optimization practice is eliminating waste, including shutting down idle resources and right-sizing.

**Question 19: Which AWS service is used to cache content at edge nodes to minimize redundant data retrieval operations?**

A. Amazon EC2 \
B. Amazon S3 \
C. Elastic Load Balancing \
D. Amazon CloudFront

> **Answer: D** - Amazon CloudFront caches content at edge locations to reduce latency and origin load.

**Question 20: Which of the following is a best practice for securing your entire infrastructure on AWS?**

A. Using the same password for all services. \
B. Encrypting data in transit and at rest. \
C. Granting maximum access to all users for convenience. \
D. Disabling logging to save storage space.

> **Answer: B** - Encrypting in transit and at rest is a fundamental security best practice (along with least privilege, logging, and monitoring).

**Question 21: What does an AWS Region typically consist of?**

A. A single data center. \
B. Two or more Availability Zones. \
C. Only Local Zones. \
D. A server cluster located at the customer's office.

> **Answer: B** - A Region is a geographic area that contains multiple, isolated Availability Zones (typically two or more).

**Question 22: Which of the following accurately describes an Availability Zone?**

A. It is a country where AWS services are available. \
B. It is a single standalone server. \
C. It is made up of one or more data centers and is designed for fault isolation. \
D. It is an AWS content delivery network service.

> **Answer: C** - An Availability Zone (AZ) consists of one or more data centers with independent power, networking, and connectivity.

**Question 23: What is the main purpose of AWS Local Zones?**

A. To store long-term backup data. \
B. To completely replace Regions. \
C. To run latency-sensitive portions of applications closer to end users and resources in a specific geography where no Regions exist today. \
D. To provide satellite internet services.

> **Answer: C** - Local Zones extend a parent Region into a specific metro area so you can run latency-sensitive workloads closer to end users.

**Question 24: What is a characteristic of an AWS data center?**

A. It only contains around 100 servers. \
B. It typically has tens of thousands of servers. \
C. It is frequently taken offline for maintenance. \
D. It only uses hardware from a single vendor.

> **Answer: B** - AWS data centers are large-scale facilities that can host tens of thousands of servers.

**Question 25: What is the function of an Edge location in the AWS Global Infrastructure?**

A. It is the headquarters of AWS. \
B. It is a data center that permanently archives your data. \
C. It consists of AWS data centers and servers located close to customers designed to deliver services with the lowest latency possible. \
D. It is where AWS network hardware is manufactured.

> **Answer: C** - Edge locations (Points of Presence) help deliver content and services to users with low latency (e.g., CloudFront).

**Question 26: What is a Regional edge cache?**

A. A cache stored locally on the user's computer. \
B. AWS data centers between the origin server and the edge location that have a longer cache. \
C. An independent compute service. \
D. A type of relational database.

> **Answer: B** - Regional edge caches sit between origin and edge locations and keep content cached longer to further reduce origin fetches.

**Question 27: The task "Design the transformation roadmap with milestones, work streams, and owners" belongs to which phase of a Cloud Architect's role?**

A. Research \
B. Plan \
C. Build \
D. Maintain

> **Answer: B** - Creating a transformation roadmap (milestones, work streams, ownership) is a planning activity before implementation.

**Question 28: Under the "Treating resources as disposable" principle, how should you handle updates?**

A. Update directly on the running system. \
B. Test updates on new resources, and then replace old resources with updated ones. \
C. Wait until the old system completely breaks before replacing it. \
D. Shut down the entire system to apply updates.

> **Answer: B** - Prefer immutable/replace-over-update: validate on new resources, then swap over and retire the old ones.

**Question 29: According to design principles, considering the use of "managed services" can help achieve benefits in which areas?**

A. Only for security purposes. \
B. Only for scaling purposes. \
C. It can be used for user authentication/state storage, as well as for securing the infrastructure and optimizing costs. \
D. It helps gain full manual control over server hardware.

> **Answer: C** - Managed services reduce undifferentiated heavy lifting and can improve security, scalability, reliability, and cost efficiency.

**Question 30: Choosing a Region to deploy an application is typically based on which criteria?**

A. The developer's personal preference. \
B. Compliance requirements or to reduce latency. \
C. Whichever Region has the most services available. \
D. The color scheme of the data center.

> **Answer: B** - Region selection is commonly driven by compliance/data residency needs and proximity to users to minimize latency.
