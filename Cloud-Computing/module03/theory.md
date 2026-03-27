<div align="center">
  <h1>Securing Access</h1>
  <sub>March 03, 2026</sub>
</div>

## 1. Introduction to AWS Security Principles

Security is the highest priority at AWS. For a Cloud Architect, the goal is to minimize risk by applying security best practices at every layer of the architecture. This involves a shift from perimeter-based security to a "Zero Trust" model where every request is authenticated and authorized.

### The Shared Responsibility Model

Understanding where AWS's responsibility ends and the customer's begins is crucial for compliance and risk management.

- **Security OF the Cloud (AWS Responsibility):** AWS protects the infrastructure that runs all services offered in the AWS Cloud. This includes the hardware, software, networking, and facilities (Regions, Availability Zones, and Edge Locations).
- **Security IN the Cloud (Customer Responsibility):** Customers are responsible for managing their data, classifying their assets, and using IAM for appropriate access control. This includes OS patching on EC2, network firewall configurations, and encrypting data.
- **Architect's Insight:** The "line" of responsibility shifts depending on the service model (IaaS, PaaS, or SaaS). For example, with Lambda (Serverless), AWS handles more of the OS and networking layer than they do with EC2.

## 2. The Well-Architected Framework: Security Pillar

The Security pillar focuses on protecting information and systems. Key design principles include:

1.  **Implement a Strong Identity Foundation:** Centralize identity management and rely on temporary credentials.
2.  **Maintain Traceability:** Monitor, alert, and audit actions and changes to your environment in real time using services like AWS CloudTrail and Amazon GuardDuty.
3.  **Apply Security at All Layers:** Rather than just a perimeter fence, apply security to every edge, VPC, subnet, and individual resource.
4.  **Automate Security Best Practices:** Use Infrastructure as Code (IaC) to create secure, repeatable architectures.
5.  **Protect Data in Transit and at Rest:** Use encryption as a non-negotiable standard.
6.  **Keep People Away from Data:** Use mechanisms and tools to reduce the need for direct access or manual processing of data.
7.  **Prepare for Security Events:** Have an incident response process in place.

## 3. Identity and Access Management (IAM) Essentials

IAM is a global service that allows you to manage access to AWS services and resources securely. It provides the "Who" (Authentication) and the "What" (Authorization).

### Key Terminology

- **Principal:** An entity (person or application) that can make a request for an action or operation on an AWS resource.
- **IAM User:** A permanent identity for a specific person or service.
- **IAM Group:** A collection of IAM users. Policies attached to a group apply to all members.
- **IAM Role:** An identity with temporary credentials. It is not "owned" by one person but can be "assumed" by anyone who needs it (users, applications, or AWS services).
- **Architect's Insight:** Always prefer **IAM Roles** over IAM Users for applications and cross-account access to eliminate the risk of long-term leaked credentials.

### Authentication Methods

- **Console Access:** Requires a Username and Password (plus MFA).
- **Programmatic Access (CLI/SDK):** Requires an **Access Key ID** and **Secret Access Key**. These should never be hardcoded and should be rotated regularly.

## 4. Authorization: How Permissions Work

Once a principal is authenticated, AWS must determine what they are allowed to do. This is handled by **IAM Policies**.

### The Evaluation Logic

AWS follows a strict hierarchy when evaluating permissions:

1.  **Explicit Deny:** If any policy contains a "Deny" for the requested action, the request is rejected immediately. **Deny always wins.**
2.  **Explicit Allow:** If there is an "Allow" and no "Deny", the request is permitted.
3.  **Default Deny (Implicit Deny):** If there is neither an explicit allow nor a deny, the request is denied by default.

### Policy Types

- **Identity-based Policies:** Attached to users, groups, or roles. They define what the identity can do.
- **Resource-based Policies:** Attached directly to a resource (e.g., an S3 bucket or a KMS key). They define _who_ has access to that specific resource.

## 5. Anatomy of an IAM Policy Document

IAM policies are JSON documents. Understanding their structure is vital for fine-grained control.

| Element       | Description                                                                                                  |
| :------------ | :----------------------------------------------------------------------------------------------------------- |
| **Version**   | The policy language version (standard is "2012-10-17").                                                      |
| **Statement** | The main container for policy elements (can have multiple statements).                                       |
| **Effect**    | Either "Allow" or "Deny".                                                                                    |
| **Principal** | (Resource-based only) The entity allowed or denied access.                                                   |
| **Action**    | The specific API calls allowed or denied (e.g., `s3:GetObject`, `ec2:TerminateInstances`).                   |
| **Resource**  | The Amazon Resource Name (ARN) the actions apply to.                                                         |
| **Condition** | (Optional) When the policy is in effect (e.g., "only if MFA is present" or "only from a specific IP range"). |

## 6. Data Protection and Encryption

Architects must ensure data integrity and confidentiality through encryption.

- **Encryption in Transit:** Protecting data as it moves between a client and a server, typically using **TLS (Transport Layer Security)**.
- **Encryption at Rest:** Protecting data stored on disks or in buckets.
  - **Client-side Encryption:** Data is encrypted by the client before being sent to AWS. The customer manages the keys.
  - **Server-side Encryption (SSE):** AWS encrypts the data on the customer's behalf as it is written to the storage device.

## 7. Professional Best Practices for Cloud Architects

To build a production-ready environment, follow these high-level standards:

1.  **Protect the Root User:** The root user has "God-mode" access. Never use it for daily tasks. Delete its access keys, enable hardware-based MFA, and lock the credentials away.
2.  **Principle of Least Privilege:** Grant only the minimum permissions required to perform a task. Start with zero access and add permissions as needed.
3.  **Use IAM Identity Center (Successor to AWS SSO):** For human users, use a centralized identity provider rather than creating IAM users in every account.
4.  **Enable MFA Everywhere:** Multi-Factor Authentication is the single most effective way to prevent unauthorized access from compromised passwords.
5.  **Audit with CloudTrail:** Ensure CloudTrail is enabled in all regions to log every API call made in your account. This is essential for forensic analysis after a security event.
6.  **Rotate Credentials:** For any long-term credentials (like IAM User access keys), implement a 90-day rotation policy.
