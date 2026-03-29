<div align="center">
  <h1>Adding a Compute Layer with Amazon EC2</h1>
  <sub>March 04, 2026</sub>
</div>

## 1. Adding Compute with Amazon EC2

### AWS Compute Runtime Choices

AWS offers a variety of compute services tailored to different levels of abstraction. As an architect, your choice depends on the balance between **Operational Overhead** and **Granular Control**.

| Service Category           | Examples          | Key Characteristics                                                                                                                             |
| :------------------------- | :---------------- | :---------------------------------------------------------------------------------------------------------------------------------------------- |
| **Virtual Machines (VMs)** | Amazon EC2        | Provides the highest level of control, including access to the OS kernel, custom drivers, and specific CPU features.                            |
| **Containers**             | ECS, EKS, Fargate | Ideal for microservices. Fargate, specifically, is a "serverless" container offering where AWS manages the underlying EC2 instances for you.    |
| **Serverless**             | AWS Lambda        | Event-driven execution. You provide the code; AWS handles all infrastructure scaling.                                                           |
| **PaaS**                   | Elastic Beanstalk | An abstraction layer that automates the deployment of web applications on EC2, handling load balancing and capacity provisioning automatically. |

> **Note:** Use EC2 when you have legacy "monolithic" applications, require specific software licenses tied to hardware, or need a specific OS version not supported by managed services.

### Amazon EC2

Amazon Elastic Compute Cloud (EC2) provides resizable compute capacity in the cloud.

#### Virtualization Architecture

- **Host Server:** The physical hardware in an AWS Data Center.
- **Hypervisor:** The software layer that creates and runs VMs (Instances).
- **Guest OS:** The operating system running inside the EC2 instance (Linux, Windows, etc.).
- **Networking & Storage:** Instances connect to the Internet/VPC and can attach to ephemeral Instance Stores or persistent Elastic Block Store (EBS) volumes.

#### Core Features

- **Rapid Provisioning:** Servers can be launched in minutes.
- **Scalability:** Capacity can scale up or down automatically based on demand.
- **Flexible Pricing:** Pay-as-you-go model ensures you only pay for used capacity.

### The 8-Step Provisioning Workflow

Launching an EC2 instance involves a sequence of architectural decisions:

1. **Select an AMI:** Choose the base OS and pre-installed software.
2. **Choose Instance Type:** Balance CPU, RAM, and Network performance.
3. **Configure Key Pair:** Secure your instance for remote access.
4. **Network Settings:** Assign the instance to a VPC and Subnet.
5. **Define Security Group:** Set firewall inbound/outbound rules for traffic control.
6. **Configure Storage:** Attach EBS volumes for persistent data.
7. **Assign IAM Role:** Enable the instance to access AWS resources securely.
8. **Add User Data:** Provide scripts that run on the first boot to configure the environment.

### Primary Use Cases

Amazon EC2 is versatile and supports virtually any server-based workload:

- **Web & Application Servers:** Hosting websites and backend logic.
- **Database Servers:** Running stand-alone SQL or NoSQL databases.
- **Media Processing:** Transcoding and streaming video.
- **Generative AI:** Training and deploying large language models.

## 2. Amazon Machine Images (AMI)

### What is an AMI?

An Amazon Machine Image (AMI) provides the information required to launch an instance. It serves as a master blueprint for your virtual servers.

#### Key Benefits

- **Repeatability:** Launch identical instances with precision.
- **Reusability:** Use the same image for multiple environments (Dev, Test, Prod).
- **Recoverability:** Replace failed instances quickly or use AMIs as restorable backups.

#### Components of an AMI

An AMI is essentially the unit of deployment in EC2. It is not just a virtual disk; it is a package that includes:

- **Root Volume Template:** This contains the guest OS and any pre-installed software.
- **Launch Permissions:** These dictate which AWS accounts can use the AMI (Private, Public, or shared with specific IDs).
- **Block Device Mapping:** This tells AWS which EBS volumes or instance stores to attach to the instance upon boot.

> Think of an AMI as a "frozen" state of a server. Because you can launch multiple instances from a single AMI, it ensures that every server in your web tier is a perfect clone of the others.

### AMI Selection Factors

When choosing or creating an AMI, consider the following technical constraints:

- **Region:** AMIs are tied to a specific AWS Region.
- **OS & Software:** Linux, Windows, or specialized appliance OS.
- **Architecture:** Choose between 64-bit x86 or ARM (AWS Graviton).
- **Storage Type:** EBS-backed (persistent) vs. Instance Store-backed (temporary).
- **Virtualization:** HVM (Hardware Virtual Machine) is the modern standard for performance.

### Storage Backing: EBS vs. Instance Store

| Feature           | EBS-Backed                        | Instance Store-Backed              |
| :---------------- | :-------------------------------- | :--------------------------------- |
| **Boot Time**     | Fast                              | Slower                             |
| **Persistence**   | Data survives instance stop/start | Data is lost on stop/termination   |
| **Stop Capacity** | Can be stopped and restarted      | Cannot be stopped; only terminated |
| **Root Size**     | Up to 16 TiB                      | Up to 10 GiB                       |

### AMI Deployment Models

To manage the lifecycle of images, architects use different "baking" models:

- **Basic AMI:** OS only; all configurations are done via scripts (User Data) at boot.
- **Silver AMI:** "Half-baked"; contains common tools and security agents.
- **Golden AMI:** "Fully-baked"; contains the OS, all software, and configurations. Best for fast scaling.

### Automation with EC2 Image Builder

EC2 Image Builder automates the creation, management, and deployment of up-to-date and compliant golden VM images.

- Provides a graphical interface to create image-building pipelines.
- Creates and maintains Amazon EC2 AMIs and on-premises VM images.
- Produces secure, validated, and up-to-date images.
- Enforces version control.
