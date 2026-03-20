<div align="center">
  <h1>Creating a Networking Environment</h1>
  <sub>March 20, 2026</sub>
</div>

**Question 1: Which definition describes a virtual private cloud (VPC)?**

- [x] A logically isolated virtual network that you define in the AWS Cloud
- [ ] An extension of an on-premises network into AWS
- [ ] A virtual private network (VPN) in the AWS Cloud
- [ ] A fully managed service that extends the AWS Cloud to customer premises

**Explanation:** A VPC is a programmatically defined, logically isolated virtual network that belongs to one Region and is sized by a CIDR block.

**Question 2: Which component does not have direct access to the internet?**

- [ ] Elastic IP address interface
- [ ] Network address translation (NAT) gateway inside a public subnet
- [ ] EC2 instance inside a public subnet
- [x] EC2 instance inside a private subnet

**Explanation:** By definition, a private subnet does not have a route to an Internet Gateway, therefore instances inside it lack direct internet access.

**Question 3: A company's VPC has the CIDR block `172.16.0.0/21` (`2048` addresses). It has two subnets. Each subnet must support `100` usable addresses now, but this number is expected to rise to `254` soon. Which subnet addressing scheme follows AWS best practices?**

- [x] Subnet A: `172.16.0.0/23` (`512` addresses); Subnet B: `172.16.2.0/23` (`512` addresses)
- [ ] Subnet A: `172.16.0.0/24` (`256` addresses); Subnet B: `172.16.1.0/24` (`256` addresses)
- [ ] Subnet A: `172.16.0.0/22` (`1024` addresses); Subnet B: `172.16.4.0/22` (`1024` addresses)
- [ ] Subnet A: `172.16.0.0/25` (`128` addresses); Subnet B: `172.16.0.128/25` (`128` addresses)

**Explanation:** AWS reserves 5 IP addresses per subnet. A `/24` provides `256 - 5 = 251` usable IPs, which is not enough for `254`. A `/23` provides `512 - 5 = 507`, meeting the requirement while allowing for future growth.

**Question 4: Several EC2 instances launch in a VPC that has internet access. These instances should not be accessible from the internet, but they must be able to download updates. How should the instances launch?**

- [ ] Without public IP addresses, in a subnet with a default route to an internet gateway
- [x] Without public IP addresses, in a subnet with a default route to a NAT gateway
- [ ] With public IP addresses, in a subnet with a default route to an internet gateway
- [ ] With Elastic IP addresses, in a subnet with a default route to an internet gateway

**Explanation:** To prevent inbound access but allow outbound traffic for updates, instances should be in a private subnet using a NAT gateway in a public subnet.

**Question 5: A group of consultants requires access to an EC2 instance from the internet for 3 days each week. The instance is shut down the rest of the week. How should you assign an IPv4 address to maintain a consistent entry point?**

- [x] Associate an Elastic IP address with the EC2 instance
- [ ] Enable automatic address assignment for the subnet
- [ ] Enable automatic address assignment for the EC2 instance
- [ ] Assign the IP address in the operating system (OS) boot configuration

**Explanation:** Standard public IPs are lost when an instance is stopped. An Elastic IP is static and remains associated with the account/instance even after a restart.

**Question 6: An application uses a bastion host to allow access to EC2 instances in a private subnet. What security group configurations are required for SSH access? (Select the most critical pair)**

- [ ] Allow SSH from 0.0.0.0/0 to the private instance; Allow SSH from Bastion to private instance
- [ ] Allow SSH from Source IP to Bastion; Allow Port 80 from Bastion to private instance
- [x] Allow SSH from Source IP to Bastion; Allow SSH from Bastion Security Group to private instance
- [ ] Deny all traffic to Bastion; Allow all traffic from Bastion to private instance

**Explanation:** You must allow SSH from your specific IP to the Bastion, and then the private instance must allow SSH specifically from the Bastion's Security Group.

**Question 7: A solution deployed in a VPC needs a subnet with limited access to specific internet addresses. How can an architect configure the network to limit traffic using a Network ACL?**

- [ ] Add rules to the default network ACL to allow specific traffic
- [x] Add rules to a subnet custom network ACL to allow traffic from/to allowed addresses
- [ ] Use a Security Group to deny all traffic except specific IP addresses
- [ ] Change the VPC CIDR block to match only the allowed internet addresses

**Explanation:** Custom NACLs are used to define specific allow/deny rules at the subnet level. Since custom NACLs deny all traffic by default, you only need to add "Allow" rules for specific IPs.

**Question 8: Which actions are best practices for designing a VPC?**

- [ ] Match the CIDR size exactly to current hosts; use one subnet for all AZs
- [ ] Use the same CIDR as on-premises; create as many small subnets as possible
- [x] Reserve address space for growth; create one subnet per AZ for unique routing needs
- [ ] Only use public subnets; assign Elastic IPs to every instance

**Explanation:** Well-Architected frameworks suggest planning for growth, ensuring high availability across AZs, and layering subnets based on routing and security needs.

**Question 9: Where can you have VPC flow logs delivered?**

- [ ] Amazon Athena, AWS Management Console, and Amazon S3
- [ ] Amazon OpenSearch, Amazon CloudWatch, and Amazon Kinesis
- [x] Amazon S3 bucket, Amazon CloudWatch Logs, and Amazon Kinesis Data Firehose
- [ ] AWS CloudTrail, Amazon S3, and Amazon Route 53

**Explanation:** VPC Flow Logs can be natively delivered to S3, CloudWatch Logs, or Kinesis Data Firehose for real-time streaming.

**Question 10: An EC2 instance must connect to an Amazon S3 bucket. What component provides this connectivity with no additional charge and no throughput limits?**

- [ ] Public region access point
- [x] Gateway VPC endpoint
- [ ] Interface VPC endpoint
- [ ] Gateway Load Balancer endpoint

**Explanation:** Gateway Endpoints for S3 and DynamoDB are free of charge and do not have the throughput limits or packet size constraints associated with Interface Endpoints.

**Question 11: Your database servers in a private subnet need to sync with an external time server on the internet. You have a NAT Gateway in the public subnet. Which rule is needed in the Private Subnet's Route Table?**

- [ ] Destination: `0.0.0.0/0` -> Target: Internet Gateway ID
- [x] Destination: `0.0.0.0/0` -> Target: NAT Gateway ID
- [ ] Destination: `10.0.0.0/16` -> Target: local
- [ ] Destination: Time Server IP -> Target: Peering Connection

**Explanation:** To reach the internet from a private subnet, the route table must point the default route (`0.0.0.0/0`) to a NAT Gateway located in a public subnet.

**Question 12: A security auditor wants to see a copy of all network traffic entering a specific EC2 instance for analysis without interrupting the traffic. Which tool should you use?**

- [ ] VPC Flow Logs
- [ ] Reachability Analyzer
- [x] Traffic Mirroring
- [ ] Network Access Analyzer

**Explanation:** Traffic Mirroring allows you to copy and forward network traffic from an elastic network interface to security and monitoring appliances.

**Question 13: You are designing a network for a high-traffic web app. You want to inspect all inbound traffic from the internet for malicious patterns before it reaches your instances. Which service adds this layer of security?**

- [ ] Security Groups
- [ ] Network ACLs
- [x] AWS Network Firewall
- [ ] Internet Gateway

**Explanation:** AWS Network Firewall provides intrusion detection and prevention (IDS/IPS) and can filter traffic at the VPC level.

**Question 14: An application in a private subnet needs to access Amazon DynamoDB. To save costs and keep traffic within the AWS network, which endpoint type is best?**

- [ ] Interface VPC Endpoint
- [x] Gateway VPC Endpoint
- [ ] NAT Gateway
- [ ] Bastion Host

**Explanation:** Gateway Endpoints are free and support DynamoDB, making them the most cost-effective choice for this scenario.

**Question 15: Your application is experiencing high latency for customers in Asia, while your VPC is located in the US East (N. Virginia) region. Following the Performance Efficiency pillar, what is the best practice?**

- [ ] Add more NAT Gateways
- [ ] Switch all subnets to Public subnets
- [x] Deploy the application in an AWS Region closer to the customers
- [ ] Increase the CIDR block size of the VPC

**Explanation:** Well-Architected principles suggest choosing Regions based on proximity to users to reduce latency.

**Question 16: You have two EC2 instances in the same VPC but different subnets. They cannot communicate. You want to check if the path is blocked by a configuration error without checking every SG/NACL rule manually. Which tool do you use?**

- [ ] VPC Flow Logs
- [x] Reachability Analyzer
- [ ] Amazon CloudWatch
- [ ] Amazon S3

**Explanation:** Reachability Analyzer tests connectivity between a source and destination in a VPC and identifies blocking components.

**Question 17: A company has a monorepo project and wants to ensure that their database in a private subnet cannot be reached directly by developers at home. How should they administer the database?**

- [ ] Assign a Public IP to the database
- [ ] Use an Internet Gateway route in the private subnet
- [x] Set up a Bastion Host in a public subnet
- [ ] Use an Interface VPC Endpoint

**Explanation:** A Bastion Host (jump box) allows secure administration of private resources from external environments.

**Question 18: You noticed that an EC2 instance in your public subnet cannot reach the internet. The instance has a Public IP and the Security Group allows all outbound traffic. What is the most likely missing component?**

- [ ] NAT Gateway
- [x] A route in the subnet table pointing `0.0.0.0/0` to the Internet Gateway
- [ ] A Gateway VPC Endpoint
- [ ] An Elastic IP address

**Explanation:** A public subnet is only "public" if its route table has an entry trashing internet-bound traffic to an Internet Gateway.

**Question 19: You are designing a VPC for a disaster relief app. You need the network to be resilient to a single data center failure. How should you distribute your subnets?**

- [ ] Put all subnets in a single Availability Zone
- [x] Create subnets in at least two different Availability Zones
- [ ] Use a larger CIDR block for the VPC
- [ ] Use a secondary VPC in a different Region

**Explanation:** Distributing resources across multiple AZs ensures that the application remains available if one AZ (data center) fails.

**Question 20: A developer accidentally deleted a Security Group rule, and now the application is failing. You need to identify exactly when and what traffic was rejected. Which tool provides this data?**

- [x] VPC Flow Logs
- [ ] Reachability Analyzer
- [ ] AWS Network Firewall
- [ ] Traffic Mirroring

**Explanation:** VPC Flow Logs record the "REJECT" or "ACCEPT" status of IP traffic, helping to diagnose security rule issues.

**Question 21: You want to connect your on-premises data center to an Amazon S3 bucket. Which VPC endpoint type allows this access over a VPN or Direct Connect?**

- [x] Interface VPC Endpoint
- [ ] Gateway VPC Endpoint
- [ ] NAT Instance
- [ ] Internet Gateway

**Explanation:** Interface Endpoints allow access from on-premises environments, whereas Gateway Endpoints do not.

**Question 22: You have a batch-processing application that doesn't need to be reached from the internet but needs to download large datasets from an external API. How should you configure its subnet?**

- [ ] Public Subnet with Internet Gateway
- [x] Private Subnet with NAT Gateway
- [ ] Public Subnet with NAT Instance
- [ ] Private Subnet with Gateway Endpoint

**Explanation:** Private subnets with NAT Gateways allow instances to initiate outbound connections for downloads while remaining hidden from the internet.

**Question 23: Your company is merging with another, and you need to connect your VPCs. You discover both use the `10.0.0.0/16` CIDR block. What is the main issue here?**

- [ ] The CIDR block is too small
- [ ] AWS doesn't allow `10.0.0.0/16`
- [x] IP addresses overlap, preventing easy routing between networks
- [ ] NACLs will not work across identical CIDRs

**Explanation:** Overlapping CIDR blocks prevent standard routing/peering between networks. Unique ranges are a best practice.

**Question 24: You are using an Interface VPC Endpoint to access a managed service. You want to restrict which specific IAM users can use this endpoint. What should you attach to the endpoint?**

- [ ] Security Group
- [ ] Network ACL
- [x] Endpoint IAM Policy
- [ ] Route Table

**Explanation:** Interface endpoints support IAM policies to control which principals can access the service through the endpoint.

**Question 25: An EC2 instance in a private subnet needs to access S3. You have both a NAT Gateway and a Gateway VPC Endpoint configured. To minimize costs, how should the traffic be routed?**

- [ ] Through the NAT Gateway
- [x] Through the Gateway VPC Endpoint
- [ ] Through the Internet Gateway
- [ ] Through a Peering connection

**Explanation:** Gateway Endpoints are free, while NAT Gateways charge for data processing. Routing to S3 via the Endpoint is more cost-effective.

**Question 26: You are architecting a solution where web servers and database servers are in the same VPC. Which setup provides the best security layering?**

- [ ] All servers in one public subnet with one Security Group
- [ ] Web servers in public subnet, DB in public subnet with different Security Groups
- [x] Web servers in public subnet, DB in private subnet with strict Security Groups
- [ ] Web servers in private subnet, DB in public subnet

**Explanation:** Layering security involves placing sensitive data (DB) in private subnets and using strict Security Groups to control access from the web tier.

**Question 27: You need to inspect traffic between two subnets in the same VPC. Which feature allows you to redirect this internal traffic through a security appliance?**

- [ ] Internet Gateway
- [x] Gateway Load Balancer Endpoint
- [ ] NAT Gateway
- [ ] VPC Peering

**Explanation:** Gateway Load Balancer endpoints are used to transparently inject network services like firewalls into the traffic path.

**Question 28: A network admin wants to audit the VPC to find any instances that have unintended access to the internet. Which tool is specifically designed for this?**

- [ ] Reachability Analyzer
- [x] Network Access Analyzer
- [ ] VPC Flow Logs
- [ ] Amazon CloudWatch

**Explanation:** Network Access Analyzer helps identify unintended network access to your resources on AWS.

**Question 29: You create a new Security Group. By default, what traffic is allowed?**

- [ ] All Inbound and All Outbound
- [ ] All Inbound, No Outbound
- [x] No Inbound, All Outbound
- [ ] No Inbound, No Outbound

**Explanation:** New Security Groups allow no inbound traffic by default but allow all outbound traffic.

**Question 30: You create a new Custom Network ACL. By default, what is its behavior?**

- [ ] All Inbound and All Outbound are Allowed
- [ ] All Inbound and All Outbound are Denied
- [x] It denies all traffic until you add allow rules
- [ ] It allows all traffic until you add deny rules

**Explanation:** Unlike the Default NACL (which allows all), a new Custom NACL denies all inbound and outbound traffic until rules are added.
