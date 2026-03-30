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

## 3. Selecting an Amazon EC2 Instance Type

### Anatomy of an Instance Type

An Amazon EC2 instance type defines the specific hardware configuration allocated to your virtual machine:

- **vCPU:** Virtual CPUs provided by the host.
- **Memory (RAM):** Amount of memory available for the workload.
- **Storage:** Choice between local Instance Store (ephemeral) or network-attached EBS (persistent).
- **Network Performance:** Bandwidth capacity (ranging from "Up to 10 Gbps" to 400+ Gbps).

### Deciphering the Naming Convention

AWS uses a structured naming system to identify instance capabilities. Example: `c7gn.xlarge`.

- **Family (c):** Optimized for compute-intensive tasks.
- **Generation (7):** Represents the hardware generation. Newer generations usually offer better price-to-performance.
- **Processor Family (g):** Indicates the processor family (e.g., 'g' stands for AWS Graviton - ARM-based processors).
- **Additional Capabilities (n):** Suffixes like `n` mean "Network optimized," `d` means "Local NVMe SSD (Instance Store) included," and `b` means "EBS-optimized".
- **Size (xlarge):** Determines the proportional amount of resources (vCPU, RAM) allocated.

### Instance Families and Workload Suitability

Strategic selection requires matching the application's resource demands to the correct family:

#### General Purpose (M, T)

- **Use Cases:** Web servers, development and test environments, gaming servers, enterprise applications.
- **Note:** T-series instances are "burstable," providing a baseline performance with the ability to burst higher using CPU Credits.

#### Compute Optimized (C)

- **Use Cases:** High-performance web servers, scientific modeling, batch processing, distributed analytics.
- **Target:** Applications that benefit from high-performance processors.

#### Memory Optimized (R, X, Z)

- **Use Cases:** In-memory caches (Redis/Memcached), big data analytics, high-performance databases.
- **Target:** Workloads that process large datasets in memory.

#### Storage Optimized (I, D, H)

- **Use Cases:** Distributed file systems, data warehouses, transactional DBs, real-time analytics.
- **Target:** High-performance local storage and high sequential I/O.

#### Accelerated Computing (P, G, Inf)

- **Use Cases:** Machine Learning (ML), Artificial Intelligence (AI), graphics rendering, video transcoding.
- **Target:** Hardware-accelerated tasks using GPUs or specialized AI chips.

#### HPC Optimized (Hpc)

- **Use Cases:** Weather forecasting, computational fluid dynamics.
- **Target:** Optimized for low-latency network interconnects.

### Optimization and Selection Tools

- **EC2 Console Instance Types Page:** Filter by vCPU, Memory, or Storage requirements during creation.
- **AWS Compute Optimizer:** A managed service that analyzes workload patterns to recommend optimal instance types, identifying under-provisioned (too small) or over-provisioned (too large) resources.
- **Price-Performance Rule:** Always prioritize the latest generation instances (e.g., moving from M5 to M6i) as they typically offer better performance for the same or lower price point.

## 4. Adding Storage to an Amazon EC2 Instance

### The Hierarchy of EC2 Storage

Amazon EC2 instances utilize different storage types based on the required balance of persistence, performance, and sharing capabilities.

- **Root Volume:** The primary volume containing the OS. Usually EBS-backed or Instance Store-backed.
- **Data Volume:** Optional additional volumes for application data or logs.

### Block Storage

#### Instance Store (Ephemeral)

- **Characteristics:** Ultra-low latency and high IOPS, locally-attached SSD/HDD storage.
- **Persistence:** Non-persistent. Data is lost if the instance is stopped, hibernated, or terminated.
- **Best For:** Temporary data, buffers, scratch space, and distributed cache.

#### Amazon Elastic Block Store (EBS)

- **Characteristics:** Persistent, network-attached block storage, can be encrypted.
- **Persistence:** Data survives even if the instance is stopped or terminated.
- **Availability:** EBS volumes are replicated within an Availability Zone (AZ) to prevent data loss from a single hardware failure.
- **Snapshots:** You can take incremental backups (Snapshots) that are stored in Amazon S3 for long-term durability.

#### EBS Volume Types

AWS categorizes EBS based on whether the bottleneck is **IOPS** (random access) or **Throughput** (sequential access).

- Amazon EBS **SSD-backed** volumes are suited for use cases where the performance focus is on IOPS.

| Volume Type                        | Description                                                                                           | Use Cases                                                                                                                                        |
| :--------------------------------- | :---------------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------- |
| **General Purpose SSD (gp3/gp2)**  | Balances price and performance for a wide variety of workloads.                                       | Recommended for most workloads. Can be a boot volume.                                                                                            |
| **Provisioned IOPS SSD (io2/io1)** | Highest-performance SSD volume. Good for mission-critical, low-latency, or high-throughput workloads. | Critical business applications that require sustained IOPS performance. Large database workloads. Transactional workloads. Can be a boot volume. |

- Amazon EBS **HDD-backed** volumes work well when the focus is on throughput.

| Volume Type                        | Description                                                                             | Use Cases                                                                                                                                                  |
| :--------------------------------- | :-------------------------------------------------------------------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Throughput Optimized HDD (st1)** | Low-cost volume type. Designed for frequently accessed, throughput-intensive workloads. | Streaming workloads. Big data. Data warehouses. Log processing. Can't be a boot volume.                                                                    |
| **Cold HDD (sc1)**                 | Lowest-cost HDD volume. Designed for less frequently accessed workloads.                | Throughput-oriented storage for large volumes of infrequently accessed data. Use cases where the lowest storage cost is important. Can't be a boot volume. |

### Performance Optimization: EBS-Optimized Instances

Certain EC2 instance types can be EBS-optimized.

#### Benefits

- It provides a dedicated network connection to attached EBS volumes.
- It increases I/O performance.
- Additional performance is achieved if using an Amazon EC2 Nitro System-based instance type.

#### Usage

- For EBS-optimized instance types, optimization is enabled by default.
- For other instances types that support it, optimization must be manually enabled.

### Shared File Systems

When multiple instances require concurrent access to a single data source:

#### Amazon Elastic File System (EFS)

- **Protocol:** NFSv4 (Linux-based).
- **Key Traits:** Fully managed, scales automatically to petabytes, Regional availability.
- **Use Cases:** Content management, web serving, home directories.

#### Amazon FSx for Windows File Server

- **Protocol:** SMB (Windows-based).
- **Key Traits:** Native Windows compatibility, NTFS support, Active Directory integration.
- **Use Cases:** Windows application lift-and-shift, media processing, Windows home directories.

## 5. Other EC2 Configuration Considerations

### Instance Automation with User Data

User Data allows you to automate the configuration of an instance immediately upon launch by providing a script (Shell script or `cloud-init` directive).

#### Key Principles

- **Execution:** Runs automatically during the first boot cycle as the **root** user.
- **Formats:** Supports shell scripts and cloud-init directives.
- **Update Workflow:** To modify User Data on an existing instance, the instance must be stopped. Updating the script does not automatically re-run it unless specific cloud-init configuration files are cleared.
- **Architectural Best Practice:** Always update the User Data script if manual changes are made to a running instance. This ensures that future instances launched from the same configuration reflect the current state.

### Instance Metadata Service (IMDS)

IMDS provides instances with internal access to their own configuration data without requiring external API calls.

#### Usage and Security

- **Access URL:** Accessible only from within the instance at the link-local address `http://169.254.169.254/latest/meta-data/`.
- **Retrievable Data:** Instance ID, Public/Private IP addresses, Security group names, MAC addresses, and IAM role security credentials.
- **Security Insight:** Modern architectures should prioritize IMDSv2, which utilizes session-oriented tokens to prevent unauthorized metadata access via SSRF attacks.

### AMI Deployment Strategies (Image Baking)

Architects choose between different levels of pre-configuration for their images:

- **Basic AMI:** Only the OS is included. High flexibility but results in slow boot times as all software must be installed at runtime.
- **Silver AMI:** Contains the OS and common enterprise tools (monitoring, logging). Provides a balance between boot speed and flexibility.
- **Golden AMI:** The application and all dependencies are "baked" into the image. This provides the fastest boot times and is ideal for rapid auto-scaling.

### Placement Groups

Placement groups allow you to influence the physical location of your instances on the AWS host hardware to meet specific application needs.

- **Cluster Placement:** Packs instances close together inside a single Availability Zone. Use this for high-performance computing (HPC) or low-latency network communication.
- **Spread Placement:** Ensures that each instance is on a distinct piece of hardware (different rack, network, and power source). Use this for small groups of critical instances (like database nodes) to avoid simultaneous hardware failure.
- **Partition Placement:** Instances are distributed across logical partitions. Often used for large distributed data workloads like Hadoop or Kafka to ensure that hardware failures only affect a portion of the cluster.

### Summary of Best Practices

- Automate compute protection via IAM and security groups.
- Use User Data for dynamic configuration and Golden AMIs for rapid scaling.
- Leverage Instance Metadata for self-discovery and dynamic credential management.
- Align placement group strategy with the networking and availability requirements of the specific workload.

## 6. Amazon EC2 Pricing Options

### AWS Free Tier for EC2

New AWS customers can explore EC2 via the 12-month Free Tier:

- **Usage:** 750 hours per month.
- **Instance Types:** `t2.micro` or `t3.micro` (depending on region).
- **Architecture:** Supports Linux, RHEL, SLES, and Windows.

### Core Purchase Models

Selecting the right model depends on your workload's predictability and fault tolerance.

#### On-Demand Instances

- **Pricing:** Pay by the second or hour. No upfront payment.
- **Flexibility:** Highest. Stop or terminate anytime.
- **Best For:** Short-term, unpredictable workloads, or initial application testing.

#### Savings Plans & Reserved Instances (RI)

- **Commitment:** 1-year or 3-year term.
- **Savings:** Up to 72% discount compared to On-Demand.
- **Types of Savings Plans:**
  - **Compute Savings Plans:** Flexible across Instance Family, Region, OS, and even other compute services like Fargate and Lambda.
  - **EC2 Instance Savings Plans:** Deepest discounts, but restricted to a specific instance family in a region.
- **Best For:** Steady-state, predictable workloads.

#### Spot Instances

- **Pricing:** Utilizes spare AWS capacity at up to 90% discount.
- **Risk:** Instances can be reclaimed by AWS with a 2-minute interruption notice.
- **Best For:** Stateless, fault-tolerant, or flexible batch applications (e.g., big data processing, CI/CD runners, or web crawlers).

### Dedicated Hardware Options

For industries with strict regulatory requirements (like Banking or Healthcare), shared hardware isn't an option. Thus, AWS provides single-tenant hardware.

- **Dedicated Instances:** Instances that run on hardware dedicated to a single customer, but you don't have control over the physical placement. Billing is per-instance.
- **Dedicated Hosts:** You get a physical server all to yourself. Provides visibility into sockets and cores, enabling "Bring Your Own License" (BYOL) scenarios for software like Windows Server or SQL Server. Billing is per-host.

### Capacity Management

- **On-Demand Capacity Reservations:** Guarantees instance availability in a specific Availability Zone. Does not provide a discount unless paired with a Savings Plan/RI.
- **Capacity Blocks for ML:** Allows reservation of GPU instances for a future date, specifically for Machine Learning model training and experimentation.

### Cost Optimization Guideline (The "Mixed" Strategy)

A Well-Architected system uses a combination of these models:

1. **Savings Plans/RIs:** For the baseline, known steady-state load.
2. **On-Demand:** For the "spiky" or unpredictable parts of the application.
3. **Spot Instances:** For scaling horizontal, stateless web tiers or background processing tasks.

## 7. Applying the AWS Well-Architected Framework to Compute

### Security Pillar

The focus is on protecting information, systems, and assets while delivering business value through risk assessment and mitigation strategies.

#### Best Practices for Compute

- **Automate Protection:** Use EC2 Image Builder and AWS Systems Manager to automate patching and vulnerability management.
- **Defense in Depth:** Implement Security Groups and Network ACLs to control traffic at multiple layers.
- **Principle of Least Privilege:** Use IAM Roles instead of long-term access keys to grant instances access to AWS resources.
- **Auditability:** Enable VPC Flow Logs and CloudTrail to monitor access and changes to compute resources.

### Performance Efficiency Pillar

Focuses on the efficient use of computing resources to meet requirements and to maintain that efficiency as demand changes and technologies evolve.

#### Best Practices for Compute

- **Right-Sizing:** Use AWS Compute Optimizer to select the optimal instance type and size based on actual utilization patterns.
- **Selection:** Match the instance family (C, M, R, etc.) to the specific bottleneck of the application (CPU, RAM, or I/O).
- **Scale Out, Not Up:** Prefer horizontal scaling (adding more small instances) over vertical scaling (making one instance bigger) to increase resiliency.

### Cost Optimization Pillar

Focuses on avoiding unnecessary costs.

#### Best Practices for Compute

- **Pricing Models:** Leverage a combination of Savings Plans (for steady-state), On-Demand (for spikes), and Spot Instances (for stateless, fault-tolerant tasks).
- **Generation Management:** Always move to the latest generation of instance types to take advantage of better price-performance ratios.
- **Managed Services:** Use Fargate or Lambda to reduce the operational cost and overhead of managing underlying EC2 instances.

### Sustainability Pillar

Focuses on minimizing the environmental impacts of running cloud workloads.

#### Best Practices for Compute

- **Maximize Utilization:** Right-size instances to ensure high hardware utilization, reducing the total physical footprint required.
- **Efficient Hardware:** Utilize AWS Graviton processors, which are designed for superior power efficiency.
- **Lifecycle Management:** Automate the termination of non-production instances during off-hours to save both cost and energy.
