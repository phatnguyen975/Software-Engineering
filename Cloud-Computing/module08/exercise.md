<div align="center">
  <h1>Connecting Networks</h1>
  <sub>March 20, 2026</sub>
</div>

**Question 1: What is the simplest way to connect 100 virtual private clouds (VPCs) together?**

- [x] Connect the VPCs to AWS Transit Gateway.
- [ ] Create a hub-and-spoke network by using AWS VPN CloudHub.
- [ ] Connect each VPC to all the other VPCs by using VPC peering.
- [ ] Chain VPCs together by using VPC peering.

**Explanation:** AWS Transit Gateway acts as a central Regional router. Managing 100 VPCs via a "Hub-and-Spoke" model is significantly simpler than managing thousands of individual peering connections in a full mesh network.

**Question 2: A company needs network traffic to flow between an AWS account in one Region to another account in a different Region. What should they set up between the transit gateways in each region?**

- [x] Transit gateway peering attachment
- [ ] AWS PrivateLink
- [ ] AWS Direct Connect
- [ ] AWS Site-to-Site VPN

**Explanation:** Transit Gateway peering attachments allow you to connect Transit Gateways across different AWS Regions and accounts, enabling private traffic flow over the AWS global network infrastructure.

**Question 3: A company has two virtual private clouds (VPCs). VPC A has a Classless Inter-Domain Routing (CIDR) block of 10.1.0.0/16. VPC B has CIDR block of 10.2.0.0/16. Both VPCs belong to the same AWS account. What is the simplest way to connect the two VPCs so that they can route all traffic between them?**

- [ ] VPC endpoints
- [ ] AWS Site-to-Site VPN
- [ ] AWS Direct Connect
- [x] VPC peering

**Explanation:** For connecting just two VPCs with non-overlapping CIDR blocks within the same account or region, VPC Peering is the most straightforward, native, and cost-effective solution.

**Question 4: Systems in a secure subnet in a virtual private cloud (VPC) must access a bucket in Amazon S3. Which solutions stop traffic from crossing the internet? (Select TWO.)**

- [ ] Create a VPC peering connection to Amazon S3.
- [ ] Use the private IP address of Amazon S3.
- [x] Create a VPC gateway endpoint for Amazon S3.
- [x] Use VPC interface endpoints.
- [ ] Use a private IP address for the system.

**Explanation:** VPC Endpoints (Gateway or Interface) allow private connectivity to S3. Gateway endpoints modify route tables to point to S3, while Interface endpoints provide private IPs within your subnet for the service, ensuring traffic stays within the AWS network.

**Question 5: A company has three virtual private clouds (VPCs). VPCs A, B, and C have Classless Inter-Domain Routing (CIDR) blocks that do not overlap. Both A and C have separate VPC peering connections with B. However, A cannot communicate with C. What is the simplest and most cost-effective way to enable full communication between A and C?**

- [ ] Create VPC endpoints in A and C for the individual hosts that need to communicate with each other.
- [x] Add a peering connection between A and C, and route traffic between A and C through the peering connection.
- [ ] Link all three VPCs through a transit VPC, and route all traffic through the transit VPC.
- [ ] Add routes to B to enable traffic between A and C through B.

**Explanation:** VPC Peering is non-transitive. Even if A and C are both peered with B, they cannot talk to each other through B. The simplest fix is to create a direct peering connection between A and C.

**Question 6: Because of a natural disaster, a company moved a secondary data center to a temporary facility with internet connectivity. It needs a secure connection to the company's virtual private cloud (VPC) that must be operational as soon as possible. The data center will move again in 2 weeks. Which option meets the requirements?**

- [ ] VPC endpoints
- [ ] AWS Direct Connect
- [x] AWS Site-to-Site VPN
- [ ] VPC peering

**Explanation:** Site-to-Site VPN can be established quickly over existing internet connections. AWS Direct Connect takes weeks to provision, making it unsuitable for an urgent, 2-week temporary requirement.

**Question 7: A company is concerned about internet disruptions. It wants to efficiently route traffic from their on-premises network to an AWS edge location close to their customer gateway device. What should they use?**

- [ ] AWS Direct Connect
- [x] AWS Global Accelerator
- [ ] AWS Transit Gateway
- [ ] AWS VPN CloudHub

**Explanation:** AWS Global Accelerator uses the AWS global network to route traffic to the nearest AWS Edge Location, bypassing internet congestion and providing a more stable path for VPN traffic.

**Question 8: A company is implementing a system to back up on-premises systems to AWS. Which network connectivity method provides a solution with the most consistent performance?**

- [ ] Virtual private cloud (VPC) peering
- [ ] AWS Site-to-Site VPN
- [x] AWS Direct Connect
- [ ] Virtual private cloud (VPC) endpoints

**Explanation:** AWS Direct Connect provides a dedicated, private physical connection. Because it does not rely on the public internet, it offers the most consistent and predictable network performance.

**Question 9: A company uses a single AWS Direct Connect connection between their on-premises network and their virtual private cloud (VPC). They want to ensure that the network connectivity is highly available by adding a backup connection. Which network connectivity method provides the most cost-effective solution for the backup connection?**

- [ ] Another AWS Direct Connect connection through a different Direct Connect location
- [ ] Another AWS Direct Connect connection through the same Direct Connect location
- [x] An on-demand AWS Site-to-Site VPN connection across the internet
- [ ] An on-demand AWS Client VPN connection across the internet

**Explanation:** Using a Site-to-Site VPN as a backup to Direct Connect is a standard Well-Architected practice. It provides high availability at a much lower cost than maintaining a second dedicated physical line.

**Question 10: A company is connecting a virtual private cloud (VPC) to multiple on-premises data centers using a virtual private network (VPN). Which implementation ensures resiliency and predictable bandwidth requirements?**

- [ ] Use a many-to-many mesh topology, such as Amazon VPC peering.
- [ ] Establish multiple Border Gateway Protocol (BGP) sessions for each VPC to create connectivity to multiple VPCs across multiple AWS Regions.
- [x] Implement Direct Connect as the primary connection and use the VPN as a secondary failover connection from each data center.
- [ ] Implement AWS Transit Gateway to connect to each on-premises data center.

**Explanation:** Direct Connect meets the "predictable bandwidth" requirement, while the VPN provides "resiliency" by acting as a secondary failover path in case the primary line goes down.

**Question 11: A FinTech company is merging with a partner. Both have VPCs using the 10.0.0.0/16 CIDR block. They need to allow a specific application in VPC A to access a database in VPC B privately. What is the best solution?**

- [ ] Use VPC Peering.
- [x] Use AWS PrivateLink.
- [ ] Use AWS Transit Gateway.
- [ ] Use an Internet Gateway.

**Explanation:** VPC Peering and Transit Gateway cannot connect VPCs with overlapping CIDR blocks. AWS PrivateLink allows you to expose a service to another VPC using an Interface Endpoint, which works even if IP ranges overlap.

**Question 12: An enterprise needs to centralize internet egress for 50 VPCs through a single security inspection VPC. Which service should be the core of this architecture?**

- [ ] VPC Peering.
- [ ] AWS Direct Connect.
- [x] AWS Transit Gateway.
- [ ] AWS Global Accelerator.

**Explanation:** AWS Transit Gateway allows you to route outbound traffic from multiple "spoke" VPCs to a central "hub" VPC (Egress VPC) for inspection before it reaches the internet.

**Question 13: A media studio needs to transfer 500 TB of video files to Amazon S3 every month. They require high throughput and must avoid the fluctuations of the public internet. Which connectivity option should they choose?**

- [ ] AWS Site-to-Site VPN.
- [ ] AWS Snowball.
- [x] AWS Direct Connect with a Public Virtual Interface.
- [ ] AWS Direct Connect with a Private Virtual Interface.

**Explanation:** Direct Connect provides the necessary high throughput and consistency. A Public VIF is required specifically to access public AWS services like S3 over the Direct Connect line.

**Question 14: A company wants to improve the performance of their global Site-to-Site VPN for branch offices in different continents. How can they reduce latency over the "first mile" of the internet?**

- [ ] Add more VPN tunnels.
- [ ] Use VPC Peering.
- [x] Enable Accelerated Site-to-Site VPN.
- [ ] Use a NAT Gateway.

**Explanation:** Accelerated Site-to-Site VPN uses AWS Global Accelerator to land VPN traffic at the closest AWS edge location, significantly reducing latency by using the AWS private global network.

**Question 15: A Cloud Architect needs to ensure that the Marketing VPC cannot send any traffic to the Finance VPC, even though both are connected to the same Transit Gateway. What is the best way to enforce this?**

- [ ] Use different AWS Accounts.
- [x] Use separate Transit Gateway Route Tables for each VPC.
- [ ] Use VPC Peering limits.
- [ ] Use a Public VIF.

**Explanation:** Transit Gateway Route Tables allow for fine-grained traffic isolation. By not adding a route to the Finance VPC in the Marketing VPC's TGW route table, traffic will be dropped.

**Question 16: A government agency requires all traffic between their office and AWS to be encrypted using IPsec, but they also require the high, consistent 10 Gbps speed of Direct Connect. What is the solution?**

- [ ] Use Direct Connect only (it is encrypted by default).
- [ ] Use Site-to-Site VPN only.
- [x] Establish a Site-to-Site VPN over an AWS Direct Connect connection.
- [ ] Use AWS PrivateLink.

**Explanation:** Direct Connect provides the high-speed private path, and running a Site-to-Site VPN over that private path provides the required IPsec encryption for data in transit.

**Question 17: A company has 10 branch offices that need to communicate with each other and with a central VPC. What is the most efficient way to enable this "any-to-any" branch communication?**

- [ ] Mesh VPC Peering.
- [x] AWS VPN CloudHub.
- [ ] AWS Global Accelerator.
- [ ] AWS PrivateLink.

**Explanation:** AWS VPN CloudHub allows multiple remote sites to communicate with each other using a simple hub-and-spoke model through a single Virtual Private Gateway.

**Question 18: A developer needs to securely access a private EC2 instance in a VPC while working from a coffee shop. The security policy forbids SSH over the public internet. What should be implemented?**

- [ ] Site-to-Site VPN.
- [ ] Direct Connect.
- [x] AWS Client VPN.
- [ ] VPC Peering.

**Explanation:** AWS Client VPN is a managed client-based VPN service that enables individual users (like developers) to securely access AWS resources from any location.

**Question 19: A company is designing a network for 4 VPCs that need to share data. The budget is very tight, and the data volume is very low. Which option has the lowest starting cost?**

- [ ] AWS Transit Gateway.
- [x] VPC Peering.
- [ ] AWS Direct Connect.
- [ ] AWS PrivateLink.

**Explanation:** VPC Peering has no hourly connection fee, making it the most cost-effective for a small number of VPCs with low traffic. Transit Gateway charges an hourly fee per attachment.

**Question 20: An organization wants to use a single Direct Connect connection to access VPCs in multiple AWS Regions (e.g., US-East and EU-West). What component do they need?**

- [ ] A Public VIF.
- [x] A Direct Connect Gateway.
- [ ] A Transit Gateway.
- [ ] An Internet Gateway.

**Explanation:** A Direct Connect Gateway is a global resource that allows a single Direct Connect connection to interface with VPCs located in any AWS Region (except China).

**Question 21: To adhere to the Reliability pillar of the Well-Architected Framework, which network topology is preferred for a large, growing number of VPCs?**

- [ ] Many-to-many mesh.
- [x] Hub-and-spoke.
- [ ] Linear chain.
- [ ] Single VPC only.

**Explanation:** The Well-Architected Framework explicitly recommends hub-and-spoke topologies (like those enabled by Transit Gateway) over many-to-many mesh designs for better scalability and reliability.

**Question 22: A company needs to migrate 100 TB of data to AWS within 48 hours. Their internet connection is only 50 Mbps. What is the most practical solution?**

- [ ] Site-to-Site VPN.
- [ ] Direct Connect.
- [x] AWS Snowball.
- [ ] Multi-part upload over the internet.

**Explanation:** 100 TB over 50 Mbps would take months. A Snowball device allows for physical data transfer and is the only practical way to move that much data in 48 hours with a slow internet connection.

**Question 23: Which AWS networking best practice helps protect data in transit from being intercepted or read by unauthorized parties?**

- [ ] Provision redundant connectivity.
- [ ] Prefer hub-and-spoke topologies.
- [x] Enforce encryption in transit.
- [ ] Select components to reduce costs.

**Explanation:** Enforcing encryption in transit (using protocols like TLS or IPsec) ensures that data is unreadable if intercepted, fulfilling a core security requirement.

**Question 24: A company wants to see the source and destination IP addresses of all traffic passing through their Transit Gateway for troubleshooting. What should they use?**

- [ ] CloudWatch Metrics.
- [x] Transit Gateway Flow Logs.
- [ ] S3 Access Logs.
- [ ] AWS Trusted Advisor.

**Explanation:** Transit Gateway Flow Logs capture detailed information about the IP traffic going through the Transit Gateway, which is essential for network troubleshooting and security analysis.

**Question 25: A hospital needs to connect its on-premises imaging system to AWS. The connection must never touch the public internet for regulatory reasons. Which option is required?**

- [ ] Site-to-Site VPN.
- [ ] Site-to-Site VPN with Global Accelerator.
- [x] AWS Direct Connect.
- [ ] VPC Peering.

**Explanation:** AWS Direct Connect provides a dedicated physical connection that bypasses the public internet entirely, meeting the strict regulatory requirement.

**Question 26: A startup is using a NAT Gateway to allow EC2 instances to download updates from S3. They want to reduce the data processing costs of the NAT Gateway. What should they do?**

- [ ] Use an Internet Gateway.
- [x] Create a Gateway VPC Endpoint for S3.
- [ ] Use a Direct Connect.
- [ ] Use VPC Peering.

**Explanation:** Gateway VPC Endpoints for S3 are free of charge and provide a direct path to S3, bypassing the NAT Gateway and its associated data processing fees.

**Question 27: A company currently uses a complex "Transit VPC" setup with manual EC2-based routers. They want to move to an AWS-managed service that provides the same functionality. What is the modern replacement?**

- [ ] AWS PrivateLink.
- [ ] AWS Direct Connect Gateway.
- [x] AWS Transit Gateway.
- [ ] AWS VPN CloudHub.

**Explanation:** AWS Transit Gateway is the managed replacement for the legacy "Transit VPC" design, providing a scalable and easier-to-manage hub-and-spoke router.

**Question 28: A branch office in Japan experiences high jitter when connecting to a US-based VPC via VPN. Which service can optimize the entry point into the AWS network for this VPN?**

- [ ] AWS Direct Connect.
- [ ] VPC Peering.
- [x] AWS Global Accelerator.
- [ ] AWS PrivateLink.

**Explanation:** Global Accelerator provides static IP addresses that act as an entry point to the AWS global network at the edge location closest to the branch office, reducing jitter and latency.

**Question 29: An architect is following the Well-Architected principle to "provision redundant connectivity." The company already has one Direct Connect line. What is the most resilient addition?**

- [ ] A second VIF on the same line.
- [x] A second Direct Connect line from a different provider at a different location.
- [ ] A VPC Peer to another region.
- [ ] A larger instance size for the web servers.

**Explanation:** True redundancy requires a different path. A second line from a different provider at a different location ensures the connection stays up even if one provider or location fails.

**Question 30: A company has VPCs in 5 regions and needs them all to communicate. What is the most scalable way to connect them?**

- [ ] Regional VPC Peering in a mesh.
- [ ] A single global Transit Gateway.
- [x] A Transit Gateway in each region, connected via Transit Gateway Peering.
- [ ] One Direct Connect line to each region.

**Explanation:** Transit Gateways are regional. To connect multiple regions at scale, you place a TGW in each region and then peer the TGWs together to create a global network.
