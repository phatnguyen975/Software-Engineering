<div align="center">
  <h1>Securing Access</h1>
  <sub>March 03, 2026</sub>
</div>

## 1. Security Principles

Security is the highest priority at AWS. For a Cloud Architect, the goal is to minimize risk by applying security best practices at every layer of the architecture. This involves a shift from perimeter-based security to a "Zero Trust" model where every request is authenticated and authorized.

### The Shared Responsibility Model

The foundation of all AWS security is the **Shared Responsibility Model**. It defines a clear boundary between the provider (AWS) and the consumer (the customer).

- **Security OF the Cloud (AWS Responsibility):** AWS manages and protects the physical infrastructure that runs all services offered in the AWS Cloud.
  - **Physical Security:** Data centers, Regions, Availability Zones, and Edge Locations.
  - **Foundation Services:** Hardware and software for Compute, Storage, Database, and Networking.
- **Security IN the Cloud (Customer Responsibility):** You are responsible for everything you build and configure within the AWS environment.
  - **Data Protection:** Managing customer data, including integrity and encryption.
  - **Access Management:** Identity and Access Management (IAM) configurations.
  - **Infrastructure Configuration:** Operating system patching, network and firewall (Security Groups/ACLs) configurations.
  - **Network Security:** Protecting network traffic through encryption and identity.

### The Well-Architected Framework: Security Pillar

Security is one of the six pillars of the AWS Well-Architected Framework, providing a structured set of design principles to architect secure solutions.

#### Key Design Principles

- **Implement a Strong Identity Foundation:** Centralize identity management and grant access based on a need-to-know basis.
- **Maintain Traceability:** Use logging and monitoring (like CloudTrail) to audit every action and change in your environment.
- **Apply Security at All Layers:** Rather than just a perimeter fence, apply security to every edge, VPC, subnet, and resource.
- **Protect Data in Transit and at Rest:** Use encryption and access controls to keep data confidential and intact.
- **Keep People Away from Data:** Use automation and tools to reduce manual processing, which minimizes human error and exposure.
- **Automate Security Best Practices:** Create secure, repeatable architectures using Infrastructure as Code (IaC).
- **Prepare for Security Events:** Have an incident response plan and practice it regularly.

### The Principle of Least Privilege

This is a critical architectural best practice: a principal (user, group, or role) should be granted only the permissions required to perform their specific task.

- **Implementation Strategy:** Start with a minimum set of permissions and add more only as necessary.
- **Refinement:** Constantly review usage and revoke unnecessary permissions to reduce the "blast radius" of a potential compromise.
- **Visual Example:** While an Administrator (e.g., John) might have an IAM policy with full permissions to all S3 buckets, a Marketing user (e.g., Mary) might only have "Read only" access to a specific bucket (S3 bucket 1) and be explicitly denied access to others (S3 bucket 2).

### Protecting Data through Encryption

Encryption is the primary mechanism for protecting data from unauthorized access, whether it is moving across the network or sitting on a disk.

#### Data in Transit

- Protect data as it travels between a client and an AWS service.
- **Mechanism:** Use cryptographic protocols like **TLS** (Transport Layer Security) to secure the communication channel.

#### Data at Rest

- **Client-Side Encryption:** The client encrypts data before sending it to the AWS Cloud storage bucket. The client is responsible for decrypting it upon retrieval.
- **Server-Side Encryption:** The client sends unencrypted data, and the AWS service (the server) automatically encrypts the data as it is stored. AWS then decrypts the data automatically when it is requested.

## 2. Authenticating and Securing Access

### Core Concepts of IAM

**Authentication** answers the critical question: **"Who is requesting access to the AWS account and its resources?"**.

- **Principal**: This refers to the person or application that signs in and makes requests to AWS.
- **IAM Entity**: These are resource objects (users and roles) that AWS uses specifically for the authentication process.
- **IAM Identity**: These resource objects (user, group, or role) can be authorized in policies to access resources.
- **Identity Types**: The requester can be a human user or an automated application.

### IAM Components and Hierarchy

AWS Identity and Access Management (IAM) provides a granular framework to control access.

#### IAM User

An IAM user represents a person or service that interacts with AWS. It is typically used for long-term identities.

> **Note:** For modern cloud-native applications, minimize the use of permanent IAM users in favor of federated identities or roles to reduce the risk of static credential theft.

#### IAM Group

A group is a collection of IAM users who are granted identical authorization.

- **Best Practice**: Always attach policies to groups rather than individual users.
- **Efficiency**: Assigning users to groups ensures consistency across teams (e.g., Sales, IT).

#### IAM Role

A role is a temporary identity used to grant a specific set of permissions for making service requests.

- **Temporary Credentials**: Unlike users, roles do not have long-term passwords or access keys; they provide temporary security credentials.
- **Use Cases**:
  - Applications running on Amazon EC2 instances.
  - Cross-account access (allowing a user in Account A to access resources in Account B).
  - Mobile applications requiring AWS resource access.

### Credential Types and Usage

Establishing identity requires specific credentials depending on the method of access.

| Action                             | Required Credentials                        |
| :--------------------------------- | :------------------------------------------ |
| **AWS Management Console Sign-in** | Username and password                       |
| **AWS CLI Commands**               | AWS access key (Access key ID + Secret key) |
| **Programmatic API Calls**         | AWS access key (Access key ID + Secret key) |

### Protecting the Root User

The root user has full, unrestricted access to every resource in the account. It is the most sensitive identity in your architecture.

- **Rule of Zero**: Only use the root user for tasks that absolutely require it (e.g., changing support plans or closing the account).
- **Daily Operations**: For daily administrative tasks, create an admin user in **IAM Identity Center**.

#### Steps to Securely Set Up an Administrator

1.  Log in as the root user.
2.  Immediately set up **Multi-Factor Authentication (MFA)** for the root user.
3.  Create a new admin user (e.g., "John") with appropriate permissions and MFA.
4.  Download programmatic keys for the admin user if needed.
5.  Log out as the root user and use the admin user for all future tasks.

### Security Best Practices for Access

To maintain a high security posture, follow these established principles:

- **Enable MFA**: This should be mandatory for all human users to mitigate the risk of password compromise.
- **Require Temporary Credentials**: Human users should access AWS via temporary credentials whenever possible.
- **Rotate Access Keys**: If your use case requires long-term credentials, implement a regular rotation schedule.
- **Enable Traceability**: Use **AWS CloudTrail** to record and audit all account activity.
- **Strong Passwords**: Enforce complex password requirements for all IAM users.
- **Federation**: Use **federated identity management** to allow users to sign in with their existing corporate credentials.

## 3. Authorizing Users

Once a principal is authenticated, AWS must determine what they are allowed to do. This is handled by **IAM Policies**.

### Policy Types

There are two primary ways to apply these permissions:

- **Identity-based Policies:** Attached to users, groups, or roles. They define what the identity can do.
- **Resource-based Policies:** Attached directly to a resource (e.g., an S3 bucket or a KMS key). They define _who_ has access to that specific resource.

### The Evaluation Logic

AWS follows a strict hierarchy when evaluating permissions:

1. **Default Deny**: By default, all requests are denied.
2. **Explicit Deny**: The evaluator first looks for any "Deny" statement. An explicit deny overrides any "Allow" statement.
3. **Explicit Allow**: If no explicit deny is found, the evaluator looks for an "Allow" statement. If found, the request is permitted.
4. **Implicit Deny**: If there is neither an explicit deny nor an explicit allow, the request remains denied.

### Interaction Between Policy Types (Case Study)

Architects often use both identity-based and resource-based policies together. The interaction can be complex.

#### Example: The Case of "Bob" and Bucket X

Consider a user named Bob who needs to interact with an S3 bucket named "Bucket X":

- **Identity Policy (on Bob)**: Allows `GET`, `PUT`, and `LIST` for Bucket X.
- **Resource Policy (on Bucket X)**: Allows `GET` and `LIST`, but explicitly **Denies** `PUT` for Bob.
- **Outcome**: Bob **cannot** perform a `PUT` action. Even though his identity policy allows it, the explicit deny in the resource policy takes precedence.

#### Example: Access via Resource Policy Only

- **Identity Policy (on Bob)**: Only allows `LIST` on Bucket Y.
- **Resource Policy (on Bucket Y)**: Explicitly allows Bob to `GET` and `LIST`.
- **Outcome**: Bob **can** perform the `GET` action. In most cases, an allow in either an identity-based OR a resource-based policy is sufficient to grant access, provided there is no explicit deny elsewhere.

## 4. Anatomy of an IAM Policy Document

IAM policies are stored as **JSON (JavaScript Object Notation)** documents. This machine-readable format allows for versioning, automation, and precise control. Each policy consists of one or more **statements**, where each statement defines a single permission.

### Key Elements of a Policy Statement

To build a functional policy, you must understand the specific elements that comprise a statement:

| Element       | Purpose                                                                                                                              |
| :------------ | :----------------------------------------------------------------------------------------------------------------------------------- |
| **Version**   | Defines the version of the policy language you want to use (standard is "2012-10-17").                                               |
| **Statement** | The main container for the policy details (can have multiple statements); it defines what is allowed or denied.                      |
| **Effect**    | Specifies whether the statement results in an "**Allow**" or a "**Deny**".                                                           |
| **Action**    | The specific API operations that are being permitted or forbidden (e.g., `s3:GetObject`, `ec2:TerminateInstances`, `iam:ListUsers`). |
| **Resource**  | The specific AWS objects (identified by ARNs) that the actions apply to.                                                             |
| **Principal** | **Required for resource-based policies** only; it specifies which account, user, or role is being granted/denied access.             |
| **Condition** | (Optional) Defines specific circumstances under which the policy is in effect (e.g., source IP address or specific instance types).  |

### Policy Examples Analysis

The slides provide concrete examples of how these elements work in practice for different scenarios.

#### A. Resource-Based Policy (S3 & DynamoDB)

In this example, the policy is attached to the resource itself.

- **Allow Statement**: Explicitly allows any action (`*`) on a specific DynamoDB table and S3 bucket.
- **Deny Statement**: Uses the **NotResource** element to deny actions on all other tables or buckets _except_ for the ones specifically listed.
- **Architect's Insight**: Using `NotResource` with `Deny` is a powerful way to "whitelist" specific resources while blocking everything else.

#### B. Identity-Based Policy (IAM Self-Management)

This policy is attached to a user to allow them to manage their own credentials.

- **Action**: Lists specific IAM actions like `iam:*LoginProfile` and `iam:*AccessKey*`.
- **Resource**: Uses a variable `${aws:username}` to ensure the user can only perform these actions on their own IAM entity.

#### C. Cross-Account Resource-Based Policy

This allows a principal from a different AWS account (e.g., Account B) to access a resource in the current account (e.g., Account A).

- **Principal**: Specifically identifies the 12-digit AWS account ID of Account B.
- **Action**: Grants full S3 access (`s3:*`) to the external account for a specific bucket.

### Advanced Logic: Conditions

Conditions add a layer of "context-aware" security.

- **IP Restriction**: A policy can allow an action but include a `Deny` statement with a `NotIpAddress` condition. This ensures that even with a valid password, the action is blocked unless the request comes from a specific corporate IP range.
- **Instance Type Restriction**: You can deny the `ec2:RunInstances` action if the `ec2:InstanceType` is not a specific allowed type (e.g., denying anything that isn't `t2.micro` or `t2.small`).

## 5. Professional Best Practices for Cloud Architects

To build a production-ready environment, follow these high-level standards:

1.  **Protect the Root User:** The root user has "God-mode" access. Never use it for daily tasks. Delete its access keys, enable hardware-based MFA, and lock the credentials away.
2.  **Principle of Least Privilege:** Grant only the minimum permissions required to perform a task. Start with zero access and add permissions as needed.
3.  **Use IAM Identity Center (Successor to AWS SSO):** For human users, use a centralized identity provider rather than creating IAM users in every account.
4.  **Enable MFA Everywhere:** Multi-Factor Authentication is the single most effective way to prevent unauthorized access from compromised passwords.
5.  **Audit with CloudTrail:** Ensure CloudTrail is enabled in all regions to log every API call made in your account. This is essential for forensic analysis after a security event.
6.  **Rotate Credentials:** For any long-term credentials (like IAM User access keys), implement a 90-day rotation policy.
