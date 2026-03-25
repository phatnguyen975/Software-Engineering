<div align="center">
  <h1>Adding a Compute Layer with Amazon EC2</h1>
  <sub>March 04, 2026</sub>
</div>

**Question 1: Which AWS compute service category provides Platform as a Service (PaaS)?**

- [ ] Amazon EC2
- [ ] Amazon ECS
- [x] AWS Elastic Beanstalk
- [ ] AWS Lambda

**Explanation:** AWS Elastic Beanstalk is classified under the Platform as a Service (PaaS) category.

**Question 2: What is the primary function of an Amazon EC2 instance?**

- [ ] It provides a serverless execution environment.
- [x] It provides virtual machines (servers) in the cloud.
- [ ] It hosts containerized applications exclusively.
- [ ] It provides a fully managed platform as a service.

**Explanation:** Amazon EC2 provides virtual machines (servers) in the cloud.

**Question 3: What critical components does an Amazon Machine Image (AMI) contain?**

- [ ] Security groups and IAM roles.
- [ ] Subnets and VPC configurations.
- [x] A template for the root volume, launch permissions, and block device mappings.
- [ ] Load balancers and Auto Scaling groups.

**Explanation:** An AMI provides the information needed to launch an instance, including a template for the root volume, launch permissions, and block device mappings.

**Question 4: Which of the following is a primary benefit of using AMIs?**

- [ ] They automatically secure the network traffic.
- [x] They provide repeatability by allowing you to launch instances with efficiency and precision repeatedly.
- [ ] They reduce the cost of Amazon EBS volumes.
- [ ] They automatically scale your application.

**Explanation:** A key benefit of an AMI is repeatability, meaning it can be used repeatedly to launch instances with efficiency and precision.

**Question 5: What is a key difference in capabilities between an Amazon EBS-backed instance and an instance store-backed instance?**

- [ ] An instance store-backed instance boots faster.
- [x] An EBS-backed instance can be placed in a stopped state, whereas an instance store-backed instance cannot.
- [ ] An instance store-backed instance allows changing the instance type.
- [ ] An EBS-backed instance has a maximum root device size of 10 GiB.

**Explanation:** An Amazon EBS-backed instance can be placed in a stopped state, while an instance store-backed instance cannot be in a stopped state; it is either running or terminated.

**Question 6: What is the maximum size limit for the root device of an Amazon EBS-backed instance?**

- [ ] 10 GiB
- [ ] 16 GiB
- [x] 16 TiB
- [ ] 10 TiB

**Explanation:** The maximum size of a root device for an Amazon EBS-backed instance is 16 TiB.

**Question 7: Which AWS service provides a graphical interface and automates the creation, management, and deployment of up-to-date and compliant golden VM images?**

- [ ] AWS Auto Scaling
- [x] EC2 Image Builder
- [ ] AWS Compute Optimizer
- [ ] Amazon Elastic File System

**Explanation:** EC2 Image Builder provides a graphical interface to automate the creation, management, and deployment of up-to-date and compliant golden VM images.

**Question 8: In the EC2 instance type name `c7gn.xlarge`, what does the `c` represent?**

- [ ] Generation
- [ ] Additional capabilities
- [ ] Size
- [x] Family

**Explanation:** In the instance type naming convention (e.g., `c7gn.xlarge`), the first letter or group of letters represents the instance Family.

**Question 9: Which instance type family is strictly recommended for high-performance databases, real-time analytics, and transactional workloads?**

- [ ] General purpose instance types
- [ ] Compute optimized instance types
- [x] Storage optimized instance types
- [ ] Memory optimized instance types

**Explanation:** Storage optimized instance types are designed for workloads such as high-performance databases, real-time analytics, and transactional workloads.

**Question 10: Which AWS tool analyzes workload patterns to recommend the optimal instance type, instance size, and Auto Scaling group configuration?**

- [ ] AWS Well-Architected Tool
- [x] AWS Compute Optimizer
- [ ] Amazon CloudWatch
- [ ] AWS Systems Manager

**Explanation:** AWS Compute Optimizer analyzes workload patterns and recommends optimal instance type, instance size, and Auto Scaling group configurations.

**Question 11: Which storage option provides temporary, non-persistent block-level storage where data is permanently lost when the instance is stopped or terminated?**

- [ ] Amazon EBS
- [ ] Amazon EFS
- [x] Instance store
- [ ] Amazon S3

**Explanation:** An instance store provides temporary block-level storage, and the instance store data is lost when the instance is stopped or terminated.

**Question 12: Which Amazon EBS SSD-backed volume type is the highest-performance option, suited for mission-critical, low-latency, or high-throughput workloads?**

- [ ] General Purpose SSD (gp2)
- [x] Provisioned IOPS SSD (io1)
- [ ] Throughput Optimized HDD (st1)
- [ ] Cold HDD (sc1)

**Explanation:** Provisioned IOPS SSD (io1) is the highest-performance SSD volume and is good for mission-critical, low-latency, or high-throughput workloads.

**Question 13: Which Amazon EBS volume type is a low-cost volume strictly designed for frequently accessed, throughput-intensive workloads like big data and data warehouses?**

- [ ] General Purpose SSD (gp2)
- [ ] Provisioned IOPS SSD (io1)
- [x] Throughput Optimized HDD (st1)
- [ ] Cold HDD (sc1)

**Explanation:** Throughput Optimized HDD (st1) is a low-cost volume type designed for frequently accessed, throughput-intensive workloads such as big data and data warehouses.

**Question 14: If you need to attach a fully managed shared file system to multiple Linux EC2 instances, which service is the best option?**

- [ ] Amazon EBS
- [ ] Amazon S3
- [x] Amazon Elastic File System (Amazon EFS)
- [ ] Amazon FSx for Windows File Server

**Explanation:** Amazon EFS provides fully managed elastic file system storage for Linux-based workloads that can be mounted to multiple guest OSes.

**Question 15: Which shared file system storage service uses the Native Server Message Block (SMB) protocol and integrates with Microsoft Active Directory?**

- [ ] Amazon EFS
- [ ] Amazon EBS
- [ ] Amazon S3
- [x] Amazon FSx for Windows File Server

**Explanation:** Amazon FSx for Windows File Server provides fully managed shared file system storage that uses the Native SMB protocol and integrates with Microsoft Active Directory.

**Question 16: How can you automatically run an initialization script (like a bash shell script) during the launch of an EC2 instance?**

- [ ] By querying instance metadata
- [x] By specifying user data
- [ ] By deploying a Golden AMI
- [ ] By attaching an Amazon EFS volume

**Explanation:** When you launch an EC2 instance, you can specify user data to run an initialization script, such as a shell script.

**Question 17: What specific URL is accessed from within a running EC2 instance to retrieve instance metadata, such as its public hostname?**

- [x] http://169.254.169.254/latest/meta-data/
- [ ] http://254.169.254.169/latest/meta-data/
- [ ] http://192.168.1.1/latest/meta-data/
- [ ] http://10.0.0.1/latest/user-data/

**Explanation:** Instance metadata is accessible from your instance at the URL `http://169.254.169.254/latest/meta-data/`.

**Question 18: Which AMI deployment model has configurations fully baked into the AMI, resulting in shorter boot times but increased build times?**

- [ ] Basic AMI
- [ ] Silver AMI
- [x] Golden AMI
- [ ] Bronze AMI

**Explanation:** A Golden AMI consists of customized immutable AMIs with configurations fully baked in, resulting in shorter boot times but increases in build times.

**Question 19: What is a distinct benefit of using Amazon EC2 Placement groups?**

- [ ] They automatically back up your EC2 instances to Amazon S3.
- [ ] They allow an instance to be launched in multiple placement groups simultaneously.
- [x] They give you control of where interdependent instances run to increase network performance.
- [ ] They automatically change the instance type based on utilization.

**Explanation:** Placement groups give you control of where a group of interdependent instances run, which can increase network performance between instances.

**Question 20: Which of the following is NOT a valid placement strategy for EC2 Placement groups?**

- [ ] Cluster
- [ ] Partition
- [ ] Spread
- [x] Distributed

**Explanation:** The valid placement strategies for Placement groups are Cluster, Partition, and Spread. Distributed is not a valid strategy.

**Question 21: Which Amazon EC2 pricing option allows you to pay for compute capacity by the second or hour with absolutely no long-term commitments?**

- [ ] Reserved Instances
- [ ] Savings Plans
- [ ] Amazon EC2 Spot
- [x] On-Demand

**Explanation:** The On-Demand purchase model lets you pay for compute capacity by the second or by the hour with no long-term commitments.

**Question 22: Which EC2 pricing option offers spare Amazon EC2 capacity at a substantial savings and is primarily recommended for fault-tolerant and stateless workloads?**

- [ ] Dedicated Hosts
- [ ] Reserved Instances
- [x] Amazon EC2 Spot
- [ ] On-Demand

**Explanation:** Amazon EC2 Spot provides spare EC2 capacity at substantial savings and is recommended for fault-tolerant, flexible, and stateless workloads.

**Question 23: Which purchasing option provides discounts similar to Reserved Instances but offers more flexibility in exchange for a $/hour commitment?**

- [ ] On-Demand
- [ ] Amazon EC2 Spot
- [x] Savings Plans
- [ ] Dedicated Instances

**Explanation:** Savings Plans offer the same discounts as Reserved Instances with more flexibility in exchange for a $/hour commitment.

**Question 24: Which feature guarantees that you always have access to EC2 capacity when you need it by letting you reserve compute capacity in a specific Availability Zone?**

- [ ] Amazon EC2 Capacity Blocks for ML
- [x] On-Demand Capacity Reservations
- [ ] Dedicated Hosts
- [ ] EC2 Instance Savings Plans

**Explanation:** On-Demand Capacity Reservations let you reserve compute capacity in a specific Availability Zone, guaranteeing that you always have access to EC2 capacity when you need it.

**Question 25: Which EC2 dedicated computing option features per-host billing and allows you to use your server-bound software licenses to address compliance requirements?**

- [ ] Dedicated Instances
- [x] Dedicated Hosts
- [ ] Reserved Instances
- [ ] Capacity Reservations

**Explanation:** Dedicated Hosts feature per-host billing and allow you to use your server-bound software licenses to address compliance requirements.

**Question 26: According to the Cost Optimization pillar of the AWS Well-Architected Framework, which of the following is a recommended best practice?**

- [ ] Automate compute protection.
- [ ] Use the minimum amount of hardware.
- [x] Select the best pricing model and select the correct resource type, size, and number.
- [ ] Control traffic at all layers.

**Explanation:** The Cost Optimization best practices include selecting the correct resource type, size, and number, as well as selecting the best pricing model.

**Question 27: According to the Sustainability pillar of the AWS Well-Architected Framework, what is a recommended best practice for hardware and services?**

- [ ] Always use On-Demand instances to prevent over-provisioning.
- [ ] Scale the best compute options for your workload.
- [x] Use the minimum amount of hardware to meet your needs and use managed services.
- [ ] Configure and right-size compute resources.

**Explanation:** Best practices for Sustainability include using the minimum amount of hardware to meet your needs, using instance types with the least impact, and using managed services.

**Question 28: If you need to manually run commands to update the user data script on an already running EC2 instance, what is Step 1 of the process?**

- [ ] Terminate the instance.
- [x] Stop the instance.
- [ ] Remove the `config_scripts_user` file immediately.
- [ ] Reboot the instance.

**Explanation:** To work with user data on running instances, Step 1 is to stop the instance before modifying the user data script.

**Question 29: Which instance type families belong to the Accelerated computing category, suitable for machine learning and AI workloads?**

- [ ] M7, M6, T4, T3
- [x] P5, P4, Inf2, G5
- [ ] R7, R6, X2, Z1
- [ ] I4, Im4, D3, H1

**Explanation:** Accelerated computing instance types, such as P5, P4, Inf2, and G5, are used for machine learning, artificial intelligence (AI), and HPC workloads.

**Question 30: A solutions architect has a workload that will run for at least 1 year uninterrupted in the same Region. The workload will remain steady, except for occasional spikes during peak seasons. During these spikes, the size of the instance type might need to be increased. However, the instance family will remain the same. Which pricing option should be used to purchase the instance at the lowest cost?**

- [ ] Dedicated instance
- [ ] Compute Saving Plans instance
- [x] EC2 Instance Savings Plans instance
- [ ] On-Demand instance

**Explanation:** Because the workload runs uninterrupted for at least 1 year and the instance family remains exactly the same, an EC2 Instance Savings Plans instance provides the lowest cost over a 1-year or 3-year term for steady workloads compared to On-Demand.
