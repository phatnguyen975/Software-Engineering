<div align="center">
  <h1>Introducing Cloud Architecting</h1>
  <sub>March 03, 2026</sub>
</div>

**Question 1: How is "Cloud architecture" defined?**

- [ ] The process of migrating physical servers to a virtualized environment.
- [x] The practice of applying cloud characteristics to a solution that uses cloud services and features to meet an organization's technical needs and business use cases.
- [ ] The use of a single data center to store enterprise data.
- [ ] Building a private physical data center for an organization.

**Explanation:** Cloud architecture is the practice of designing solutions that leverage cloud-specific characteristics and services to solve technical and business problems efficiently.

**Question 2: As a Cloud Architect, which task belongs to the "Plan" phase?**

- [x] Analyze solutions for business needs and requirements.
- [ ] Design prototype solutions.
- [ ] Manage the adoption and migration.
- [ ] Investigate cloud services specifications.

**Explanation:** The "Plan" phase involves gap analysis, technical requirement gathering, and analyzing how solutions meet business needs.

**Question 3: How many pillars are in the AWS Well-Architected Framework?**

- [ ] 4
- [ ] 5
- [x] 6
- [ ] 7

**Explanation:** The AWS Well-Architected Framework consists of 6 pillars: Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, and Sustainability.

**Question 4: Which pillar of the AWS Well-Architected Framework focuses on running and monitoring systems that deliver business value?**

- [ ] Security
- [ ] Reliability
- [x] Operational Excellence
- [ ] Cost Optimization

**Explanation:** The Operational Excellence pillar focuses on running and monitoring systems, and continually improving processes and procedures.

**Question 5: "Apply security at all layers" is a practice belonging to which pillar?**

- [ ] Performance Efficiency
- [x] Security
- [ ] Sustainability
- [ ] Reliability

**Explanation:** Defense in depth (security at all layers) is a fundamental design principle of the Security pillar.

**Question 6: Which of the following is a principle of the "Reliability" pillar?**

- [x] Recover quickly.
- [ ] Democratize advanced technologies.
- [ ] Measure efficiency.
- [ ] Implement a strong identity foundation.

**Explanation:** Reliability focuses on ensuring a workload performs its intended function and can recover quickly from failures.

**Question 7: Which of the following is a principle of the "Performance Efficiency" pillar?**

- [ ] Reduce downstream impact.
- [ ] Consider using managed services.
- [x] Democratize advanced technologies.
- [ ] Mitigate disruptions.

**Explanation:** Democratizing advanced technologies (making complex tech easy to consume) is a design principle for Performance Efficiency.

**Question 8: "Eliminate unneeded expense" is a principle of which pillar?**

- [x] Cost Optimization
- [ ] Sustainability
- [ ] Operational Excellence
- [ ] Reliability

**Explanation:** Cost Optimization focuses on avoiding unnecessary costs through principles like monitoring and eliminating unneeded expenses.

**Question 9: What is the main function of the AWS Well-Architected Tool?**

- [ ] Automatically writes code for your applications.
- [ ] Automatically patches security vulnerabilities in your system.
- [x] Helps you review the state of your workloads and compares them to the latest AWS architectural best practices.
- [ ] Manages your monthly AWS billing.

**Explanation:** The tool provides a consistent process for reviewing architectures and identifying areas for improvement based on best practices.

**Question 10: When designing a solution, which of the following is an example of "Design trade-offs"?**

- [ ] Always choosing the most expensive solution to ensure safety.
- [x] Trading consistency, durability, and space for time and latency to deliver higher performance.
- [ ] Reducing storage capacity but increasing the number of operations staff.
- [ ] Exclusively using free services.

**Explanation:** Architecting involves making informed decisions to trade one aspect (like consistency) for another (like lower latency) based on workload requirements.

**Question 11: To implement scalability when demand changes, which AWS service is typically used to scale out automatically?**

- [ ] Amazon S3
- [ ] Amazon CloudFront
- [x] Amazon EC2 Auto Scaling
- [ ] AWS Well-Architected Tool

**Explanation:** EC2 Auto Scaling automatically adds or removes EC2 instances based on defined conditions or schedules to handle changes in demand.

**Question 12: What is the main benefit of using Infrastructure as Code (IaC)?**

- [x] It reduces configuration errors from manual configuration.
- [ ] It writes application code faster.
- [ ] It reduces the monthly fee for Amazon S3.
- [ ] It completely prevents DDoS attacks.

**Explanation:** IaC allows for consistent, repeatable deployments, significantly reducing the risk of errors associated with manual setup.

**Question 13: What action does the "Treating resources as disposable" principle encourage?**

- [ ] Trying to fix a server when it crashes to save money.
- [ ] Keeping unused resources just in case of an emergency.
- [x] Automating deployment of new resources with identical configurations and replacing old resources with updated ones.
- [ ] Only using physical hardware instead of virtual machines.

**Explanation:** In the cloud, you should treat servers as temporary; if one fails or needs an update, you replace it with a fresh, automated deployment.

**Question 14: To design loosely coupled components between Web servers and Application servers, what is the best practice?**

- [ ] Connecting every Web server directly to every Application server.
- [ ] Putting all components on the same server.
- [x] Decoupling Web servers with Elastic Load Balancing (ELB).
- [ ] Removing the Application servers completely.

**Explanation:** Load balancers act as intermediaries, allowing components to interact without being dependent on specific individual instances.

**Question 15: The "Designing services, not servers" principle advises you to do what with static web assets?**

- [ ] Store them on a Web server (EC2).
- [x] Store them off server, such as on Amazon Simple Storage Service (Amazon S3).
- [ ] Delete them to save memory.
- [ ] Store them in a relational database.

**Explanation:** Offloading static content to storage services like S3 reduces the load on web servers and simplifies the architecture.

**Question 16: Which of the following is NOT typically a criterion for choosing the right database solution?**

- [ ] Read and write needs.
- [ ] Total storage requirements.
- [x] The programming language of the user interface.
- [ ] Latency requirements.

**Explanation:** While the UI language matters for development, the database choice is driven by data characteristics and performance needs, not the UI's code.

**Question 17: To avoid a single point of failure for a primary database server, what should you do?**

- [ ] Reboot the server frequently.
- [x] Create a secondary (standby) database server and replicate the data.
- [ ] Double the hard drive capacity of the current server.
- [ ] Only allow one user to access it at a time.

**Explanation:** Multi-AZ deployments with standby replicas ensure high availability by providing an automatic failover target.

**Question 18: To optimize for cost, which of the following is an important question to ask?**

- [x] How do I turn off resources that are not in use?
- [ ] How do I use the most complex hardware?
- [ ] How do I buy the cheapest physical server?
- [ ] How do I keep the application running 24/7 at maximum capacity?

**Explanation:** Cost optimization involves matching supply with demand; turning off resources during idle periods is a key technique.

**Question 19: Which AWS service is used to cache content at edge nodes to minimize redundant data retrieval operations?**

- [ ] Amazon EC2
- [ ] Amazon S3
- [ ] Elastic Load Balancing
- [x] Amazon CloudFront

**Explanation:** CloudFront is a Content Delivery Network (CDN) that caches data in global Edge Locations to deliver content with low latency.

**Question 20: Which of the following is a best practice for securing your entire infrastructure on AWS?**

- [ ] Using the same password for all services.
- [x] Encrypting data in transit and at rest.
- [ ] Granting maximum access to all users for convenience.
- [ ] Disabling logging to save storage space.

**Explanation:** Encryption is a core security best practice that protects data regardless of where it resides or how it's moving.

**Question 21: What does an AWS Region typically consist of?**

- [ ] A single data center.
- [x] Two or more Availability Zones.
- [ ] Only Local Zones.
- [ ] A server cluster located at the customer's office.

**Explanation:** A Region is a physical location in the world where AWS has multiple Availability Zones.

**Question 22: Which of the following accurately describes an Availability Zone?**

- [ ] It is a country where AWS services are available.
- [ ] It is a single standalone server.
- [x] It is made up of one or more data centers and is designed for fault isolation.
- [ ] It is an AWS content delivery network service.

**Explanation:** AZs consist of one or more discrete data centers with redundant power and networking, separated to isolate failures.

**Question 23: What is the main purpose of AWS Local Zones?**

- [ ] To store long-term backup data.
- [ ] To completely replace Regions.
- [x] To run latency-sensitive portions of applications closer to end users and resources in a specific geography where no Regions exist today.
- [ ] To provide satellite internet services.

**Explanation:** Local Zones extend Regions closer to metropolitan areas to support applications requiring single-digit millisecond latency.

**Question 24: What is a characteristic of an AWS data center?**

- [ ] It only contains around 100 servers.
- [x] It typically has tens of thousands of servers.
- [ ] It is frequently taken offline for maintenance.
- [ ] It only uses hardware from a single vendor.

**Explanation:** AWS data centers are large-scale facilities housing tens of thousands of servers to support cloud services.

**Question 25: What is the function of an Edge location in the AWS Global Infrastructure?**

- [ ] It is the headquarters of AWS.
- [ ] It is a data center that permanently archives your data.
- [x] It consists of AWS data centers and servers located close to customers designed to deliver services with the lowest latency possible.
- [ ] It is where AWS network hardware is manufactured.

**Explanation:** Edge locations are used by services like CloudFront and Route 53 to provide low-latency delivery of content and DNS.

**Question 26: What is a Regional edge cache?**

- [ ] A cache stored locally on the user's computer.
- [x] AWS data centers between the origin server and the edge location that have a longer cache.
- [ ] An independent compute service.
- [ ] A type of relational database.

**Explanation:** Regional edge caches sit between origin servers and edge locations to improve cache hit ratios for content not frequently accessed.

**Question 27: The task "Design the transformation roadmap with milestones, work streams, and owners" belongs to which phase of a Cloud Architect's role?**

- [ ] Research
- [ ] Plan
- [x] Build
- [ ] Maintain

**Explanation:** Designing the specific roadmap for migration and implementation occurs in the "Build" phase of the architect's engagement.

**Question 28: Under the "Treating resources as disposable" principle, how should you handle updates?**

- [ ] Update directly on the running system.
- [x] Test updates on new resources, and then replace old resources with updated ones.
- [ ] Wait until the old system completely breaks before replacing it.
- [ ] Shut down the entire system to apply updates.

**Explanation:** Immutable infrastructure means replacing current resources with new ones that have the updates, rather than updating in-place.

**Question 29: According to design principles, considering the use of "managed services" can help achieve benefits in which areas?**

- [ ] Only for security purposes.
- [ ] Only for scaling purposes.
- [x] It can be used for user authentication/state storage, as well as for securing the infrastructure and optimizing costs.
- [ ] It helps gain full manual control over server hardware.

**Explanation:** Managed services reduce operational overhead and often provide built-in security, scaling, and cost benefits.

**Question 30: Choosing a Region to deploy an application is typically based on which criteria?**

- [ ] The developer's personal preference.
- [x] Compliance requirements or to reduce latency.
- [ ] Whichever Region has the most services available.
- [ ] The color scheme of the data center.

**Explanation:** Legal compliance (data residency) and performance (closeness to users) are the primary drivers for Region selection.
