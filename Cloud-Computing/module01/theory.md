<div align="center">
  <h1>Welcome to AWS Academy Cloud Architecting</h1>
  <sub>March 03, 2026</sub>
</div>

## 1. Course Mission & Certification Alignment

This course is meticulously designed to transform a student into a **Solutions Architect**. It isn't just about knowing what a service does; it's about knowing **how to combine them** to solve a problem.

### Key Objectives

- **Well-Architected Framework:** This is the "Bible" for architects. It focuses on **six pillars**: Operational Excellence, Security, Reliability, Performance Efficiency, Cost Optimization, and Sustainability.
- **Scalability & Resilience:** Moving away from "fixed" hardware to "elastic" infrastructure that grows and shrinks based on demand.
- **Managed Services:** Shifting the burden of maintenance (patching, backups) to AWS so the business can focus on innovation.

### SAA-C03 Certification

The course aligns with the **AWS Certified Solutions Architect – Associate** exam. In the industry, this certification proves you can design secure, high-performing, and cost-optimized architectures. It validates your ability to review an existing architecture and suggest improvements—a core task of a consultant.

## 2. The Learning Path (Prerequisites & Structure)

Success in this course requires a "Cloud First" mindset. You shouldn't just think about servers; you should think about **Distributed Systems**.

### Prerequisites

- **Networking:** You must understand IP addressing, CIDR blocks, and DNS.
- **Architecture:** Understanding multi-tier apps (Web -> App -> DB) is vital because that is exactly how we decompose the Café's monolith.

### The Lab Ecosystem

- **Guided Labs:** These build your muscle memory.
- **Challenge Labs:** These are "problem-based." You are given a goal but no instructions. This is where you truly learn to be an architect.
- **Capstone Project:** The final "boss" where you build a complex environment from scratch.

## 3. The Café Business Case (The Heart of the Course)

Architecture doesn't exist in a vacuum; it exists to support a business. We follow Frank and Martha's café.

### Key Characters

- **The Owners (Frank & Martha):** They represent the **Business Stakeholders**. They don't care about "subnets"—they care about "online ordering working."
- **Technical Staff (Sofía & Nikhil):** They are your **Internal Devs**. They need tools that are easy to use so they can focus on the café's supply chain and design.
- **The Consultants (Olivia, Faythe, Mateo):** These represent the **Specialists**.
  - _Olivia (Architect):_ Sees the big picture.
  - _Faythe (Developer):_ Uses SDKs and APIs.
  - _Mateo (SysAdmin):_ Obsessed with backups and automation.

## 4. The Architecture Evolution (V1 to V7)

This is the most critical part of the module. It shows the transition from a "Startup" to an "Enterprise" grade system.

| Version                   | Technical Shift          | Professional Architect's Insight                                                                                                            |
| :------------------------ | :----------------------- | :------------------------------------------------------------------------------------------------------------------------------------------ |
| **V1: Static**            | **Amazon S3**            | Use S3 for cost-efficiency. There's no server to manage, and it has 99.999999999% durability.                                               |
| **V2: Dynamic**           | **Amazon EC2**           | Introduction of compute. This allows for logic (PHP, Python, etc.) but adds the burden of OS patching.                                      |
| **V3: Decoupled DB**      | **Amazon RDS**           | Moving the DB off EC2 to RDS. This is "Undifferentiated Heavy Lifting" handed to AWS. RDS handles backups and failover automatically.       |
| **V4: Networking**        | **VPC & Subnets**        | Security by isolation. The DB goes into a "Private Subnet" so it's never accessible from the public internet.                               |
| **V5: High Availability** | **ELB, ASG, Multi-AZ**   | "Design for failure." If one Data Center (AZ) goes down, the Load Balancer sends traffic to the other. Auto Scaling handles traffic spikes. |
| **V6: Automation**        | **CloudFormation (IaC)** | "Infrastructure as Code." You can delete the whole café and recreate it in a different country (Region) in 10 minutes using a script.       |
| **V7: Serverless**        | **AWS Lambda**           | The pinnacle of optimization. You only pay for the milliseconds your code runs to generate reports. No servers to manage at all.            |

## 5. Professional Roles in the Cloud Era

The cloud has redefined traditional IT roles:

- **IT Professional:** Now acts as a **Cloud Generalist**. They manage environments but need to learn cloud-native tools to stay relevant.
- **IT Leader:** Becomes a **Cloud Strategist**. They focus on "FinOps"—managing the cloud budget and ensuring the team is choosing the right services.
- **Developer:** Focuses on **Cloud-Native Development**. They no longer worry about "where" the code runs, only "how" it interacts with APIs.
- **DevOps Engineer:** The bridge. They focus on **CI/CD** (Continuous Integration/Continuous Deployment) and **Reliability**. They make sure V6 (Automation) actually works.
- **Cloud Architect:** The **Visionary**. They stay ahead of the curve, providing the "Blueprints" (documentation and best practices) that everyone else follows.

## 6. Summary & Final Thoughts

Module 1 sets the stage. You aren't just learning AWS; you are learning how to evolve a business from a simple website to a global, automated, serverless powerhouse.

**The Architect's Mantra:** "Everything fails, all the time." Your job throughout this course is to build a system for the Café that survives those failures.
