# AWS Academy Cloud Architecting - Module 06: Adding a Database Layer Practice Quiz

**Question 1: Which statement that compares a database service that AWS manages with a database on an Amazon EC2 instance is true?**

- [x] AWS manages database patches for a database on a managed database service.
- [ ] Configuring backups for a database on an EC2 instance isn't needed.
- [ ] Configuring backups for a database on a managed database service isn’t needed.
- [ ] AWS manages operating system (OS) patches for a database on an EC2 instance.

**Explanation:** In a managed service like Amazon RDS, AWS is responsible for patching the database software and the underlying OS. If you run a database on EC2, you are responsible for managing the OS and the database software yourself.

**Question 2: A small startup company is deciding which database service to use for an enrollment system for their online training website. Which requirements might lead them to select Amazon RDS rather than Amazon DynamoDB? (Select TWO.)**

- [ ] Data must be backed up in case of disasters.
- [x] Student, course, and registration data are stored in many different tables.
- [ ] The enrollment system must be highly available.
- [x] The data is highly structured.
- [ ] Data and transactions must be encrypted to protect personal information.

**Explanation:** Amazon RDS is optimized for highly structured data and supports complex queries involving joins across multiple tables. DynamoDB is a NoSQL service better suited for flexible schemas and simple key-value lookups.

**Question 3: A startup company is building an order inventory system with a web frontend and is looking for a real-time transactional database. Which service would best meet their needs?**

- [ ] Amazon Redshift
- [ ] Amazon DocumentDB (with MongoDB compatibility)
- [ ] Amazon Neptune
- [x] Amazon DynamoDB

**Explanation:** Amazon DynamoDB provides millisecond latency and high throughput, making it ideal for real-time transactional web applications like inventory systems.

**Question 4: A small game company is designing an online game, where thousands of players can create their own in-game objects. The current design uses a MySQL database in Amazon RDS to store data for player-created objects. Which proposed online game object features could make Amazon DynamoDB a better solution? (Select TWO.)**

- [ ] Game data items that include binary data and might exceed 700 MB
- [x] A high amount of read activity on player-created objects and a low amount of writes
- [ ] A set of common object attributes for player-created objects
- [x] Unpredictable object attributes for player-created objects
- [ ] Game items that can be modified using data contained in other tables

**Explanation:** DynamoDB's flexible schema handles unpredictable attributes well, and its ability to scale horizontally makes it superior for high-concurrency read/write workloads typical in gaming.

**Question 5: An organization is concerned about an increase in fraud. Which service could help with building real-time graph database queries for fraud detection?**

- [ ] Amazon Redshift
- [ ] Amazon RDS
- [ ] Amazon DynamoDB
- [x] Amazon Neptune

**Explanation:** Amazon Neptune is a purpose-built graph database designed to store and navigate complex relationships, which is a core requirement for real-time fraud detection.

**Question 6: A data engineer must host a new Microsoft SQL Server database in AWS for a project. Which service could they use to accomplish this task?**

- [ ] Amazon Neptune
- [ ] Amazon DocumentDB (with MongoDB compatibility)
- [ ] Amazon DynamoDB
- [x] Amazon RDS

**Explanation:** Amazon RDS supports multiple relational engines, including Microsoft SQL Server, whereas services like Aurora (currently) only support MySQL and PostgreSQL compatibility.

**Question 7: Which techniques should be used to secure an Amazon RDS database? (Select THREE.)**

- [x] Security groups to control network access to individual RDS instances
- [x] A virtual private cloud (VPC) to provide instance isolation
- [x] Encryption both at rest and in transit to protect sensitive data
- [ ] AWS Identity and Access Management (IAM) policies to define access at the table, row, and column levels
- [ ] An Amazon Virtual Private Cloud (Amazon VPC) gateway endpoint to prevent traffic from traversing the internet
- [ ] A virtual private gateway (VGW) to filter traffic from restricted networks

**Explanation:** Securing RDS involves network isolation (VPC), host-level firewall rules (Security Groups), and data protection (Encryption). Internal DB access (table/row) is managed by the engine's own security features.

**Question 8: Which techniques should be used to secure Amazon DynamoDB? (Select THREE.)**

- [ ] Security groups to control network access to individual instances
- [ ] A virtual private cloud (VPC) to provide instance isolation and firewall protection
- [x] Encryption to protect sensitive data
- [x] An Amazon Virtual Private Cloud (Amazon VPC) gateway endpoint to prevent traffic from traversing the internet
- [x] AWS Identity and Access Management (IAM) policies to define access at the table, item, or attribute level
- [ ] A virtual private gateway (VGW) to filter traffic from restricted networks

**Explanation:** As a serverless service, DynamoDB is secured via IAM (including fine-grained access control), default encryption at rest, and VPC Endpoints for private network connectivity.

**Question 9: A company wants to migrate their on-premises Oracle database to Amazon Aurora MySQL. Which process describes the high-level steps most accurately?**

- [x] Use AWS Schema Conversion Tool (AWS SCT) to convert the schema, and then use AWS Database Migration Service (AWS DMS) to migrate the data.
- [ ] Use AWS Schema Conversion Tool (AWS SCT) to synchronously convert the schema and migrate the data.
- [ ] Use AWS Database Migration Service (AWS DMS) to migrate the data, and then use AWS Schema Conversion Tool (AWS SCT) to convert the schema.
- [ ] Use AWS Database Migration Service (AWS DMS) to directly migrate from the Oracle database to Amazon Aurora MySQL.

**Explanation:** For heterogeneous migrations (different engines), SCT is required first to convert the schema/code before DMS can migrate the actual data.

**Question 10: A cloud architect is setting up an application to use an Amazon RDS MySQL DB instance. The database must be architected for high availability across Availability Zones and AWS Regions with minimal downtime. How should they meet this requirement?**

- [ ] Set up an RDS MySQL Single-AZ DB instance. Copy automated snapshots to at least one other Region.
- [x] Set up an RDS MySQL Multi-AZ DB instance. Configure a read replica in a different Region.
- [ ] Set up an RDS MySQL Single-AZ DB instance. Configure a read replica in a different Region.
- [ ] Set up an RDS MySQL Multi-AZ DB instance. Configure an appropriate backup window.

**Explanation:** Multi-AZ provides synchronous replication and automatic failover within a Region. A cross-region Read Replica provides a disaster recovery target in a different Region.

**Question 11: A fintech company needs a relational database that can automatically handle massive bursts of traffic for their tax-filing application, which is only used heavily for two months a year. They want to avoid paying for idle capacity during the off-season. Which solution is most appropriate?**

- [ ] Amazon RDS for MySQL with Multi-AZ
- [ ] Amazon Redshift with Reserved Instances
- [x] Amazon Aurora Serverless
- [ ] Amazon DynamoDB with On-Demand capacity

**Explanation:** Aurora Serverless is an on-demand, auto-scaling configuration for Aurora. it automatically starts up, shuts down, and scales capacity up or down based on your application's needs, making it perfect for variable or seasonal workloads.

**Question 12: A developer is building a serverless application using AWS Lambda that connects to an Amazon RDS MySQL database. During peak hours, the application fails because the database reaches the maximum number of concurrent connections. Which service can resolve this issue?**

- [ ] Amazon ElastiCache
- [ ] Amazon RDS Read Replicas
- [x] Amazon RDS Proxy
- [ ] Amazon DynamoDB Accelerator (DAX)

**Explanation:** Amazon RDS Proxy sits between the application and the database to pool and share established database connections, improving scalability and prevents applications from exhausting database connection limits.

**Question 13: A logistics company wants to track the real-time location and temperature of thousands of shipping containers. They need to perform trend analysis on this data over months. Which database is specifically designed for this "time-stamped" data?**

- [ ] Amazon DocumentDB
- [ ] Amazon Neptune
- [ ] Amazon Keyspaces
- [x] Amazon Timestream

**Explanation:** Amazon Timestream is a purpose-built time-series database for IoT and operational applications that can store and process trillions of events per day up to 1,000 times faster than relational databases.

**Question 14: A government agency needs a centralized database to track the ownership of high-value assets. The database must maintain an immutable, cryptographically verifiable log of all changes made by all users. Which service should they use?**

- [ ] Amazon RDS with Multi-AZ
- [ ] Amazon DynamoDB with Streams
- [x] Amazon QLDB
- [ ] Amazon S3 with Object Lock

**Explanation:** Amazon Quantum Ledger Database (QLDB) is a fully managed ledger database that provides a transparent, immutable, and cryptographically verifiable transaction log owned by a central trusted authority.

**Question 15: A global gaming company requires a database that allows players in North America, Europe, and Asia to read and write to their user profiles with local latency (under 10ms). Which DynamoDB feature should they implement?**

- [ ] Local Secondary Indexes (LSI)
- [ ] DynamoDB Streams
- [x] Global Tables
- [ ] DynamoDB Accelerator (DAX)

**Explanation:** DynamoDB Global Tables provide a fully managed, multi-region, and multi-active database that provides fast, local read and write performance for massive-scale, global applications.

**Question 16: A social media platform needs to store "user following" relationships (e.g., User A follows User B, who follows User C). They need to suggest "Friends of Friends" in real-time. Which database engine is optimized for this type of complex relationship navigation?**

- [ ] Amazon Aurora
- [ ] Amazon DocumentDB
- [x] Amazon Neptune
- [ ] Amazon Redshift

**Explanation:** Amazon Neptune is a graph database optimized for storing and navigating highly connected datasets with complex relationships, such as social graphs and recommendation engines.

**Question 17: A company is migrating an on-premises Apache Cassandra cluster to AWS. They want to maintain their current application code that uses Cassandra Query Language (CQL) but want a serverless, managed experience. Which service should they choose?**

- [ ] Amazon RDS for MariaDB
- [ ] Amazon DynamoDB
- [x] Amazon Keyspaces
- [ ] Amazon DocumentDB

**Explanation:** Amazon Keyspaces is a scalable, highly available, and managed Apache Cassandra-compatible database service that allows you to run Cassandra workloads using the same CQL code.

**Question 18: A retail company needs to store millions of customer reviews. Each review can have different fields (some have photos, some have ratings for different categories, some have just text). They need a managed service that is compatible with MongoDB. Which service fits?**

- [ ] Amazon RDS for PostgreSQL
- [x] Amazon DocumentDB
- [ ] Amazon DynamoDB
- [ ] Amazon Redshift

**Explanation:** Amazon DocumentDB (with MongoDB compatibility) is a fast, scalable, highly available, and fully managed document database service that supports JSON data and flexible schemas.

**Question 20: A marketing company wants to perform complex analytical queries on 10 years of historical sales data (petabytes in size) to identify buying patterns. Which service is designed for this Online Analytical Processing (OLAP) workload?**

- [ ] Amazon RDS for Oracle
- [ ] Amazon Aurora
- [ ] Amazon DynamoDB
- [x] Amazon Redshift

**Explanation:** Amazon Redshift is a fast, fully managed data warehouse that makes it simple and cost-effective to analyze all your data using standard SQL and your existing Business Intelligence (BI) tools.

**Question 21: An e-commerce site uses an RDS MySQL database. During a major sale, the primary DB instance is overwhelmed by "READ" requests for product descriptions, causing the website to slow down. What is the most cost-effective way to scale the "READ" capacity?**

- [ ] Upgrade the primary instance to a larger size (Vertical scaling).
- [x] Create RDS Read Replicas and point the application's read traffic to them.
- [ ] Enable Multi-AZ deployment for the existing instance.
- [ ] Migrate the database to Amazon Redshift.

**Explanation:** Read Replicas provide horizontal scalability for read-heavy workloads by offloading read traffic from the primary DB instance.

**Question 22: A cloud architect is designing a database for a highly confidential project. They must ensure that the data is encrypted at rest using keys that they can rotate and manage. Which AWS service should be integrated with Amazon RDS to manage these keys?**

- [ ] AWS Secrets Manager
- [ ] AWS CloudTrail
- [x] AWS Key Management Service (KMS)
- [ ] AWS IAM

**Explanation:** Amazon RDS uses AWS KMS keys to encrypt your database instances and snapshots at rest.

**Question 23: A company is using an Amazon RDS for MySQL database. They have a requirement to store the database credentials securely and rotate them every 30 days without updating the application code manually. Which combination of services should they use?**

- [ ] IAM Roles and AWS Config
- [x] AWS Secrets Manager and RDS Proxy
- [ ] AWS KMS and Amazon CloudWatch
- [ ] Systems Manager Parameter Store and Lambda

**Explanation:** AWS Secrets Manager allows you to rotate, manage, and retrieve database credentials. Integrating it with RDS Proxy allows the application to authenticate seamlessly even after rotations.

**Question 24: An application requires a database that can handle millions of requests per second with microsecond latency for a gaming leaderboard. The data must be persistent but the primary requirement is speed. Which service is best?**

- [ ] Amazon RDS for MySQL
- [ ] Amazon ElastiCache
- [x] Amazon MemoryDB for Redis
- [ ] Amazon DynamoDB

**Explanation:** Amazon MemoryDB for Redis is a durable, in-memory database service that delivers ultra-fast performance (microsecond read and single-digit millisecond write latency).

**Question 25: You are migrating a 5 TB on-premises SQL Server database to AWS. You have determined that there are many proprietary SQL features used in the schema that are not compatible with Amazon Aurora PostgreSQL. Which tool should you use first to assess the conversion effort?**

- [ ] AWS DMS Fleet Advisor
- [x] AWS Schema Conversion Tool (AWS SCT)
- [ ] AWS DataSync
- [ ] AWS Server Migration Service (SMS)

**Explanation:** AWS SCT converts your source schema and SQL code into an equivalent target schema and code, highlighting any parts that cannot be converted automatically.

**Question 26: A company's database storage requirements are unpredictable. They are using Amazon RDS for MySQL and want to ensure the database does not run out of space without having to manually provision more storage in advance. Which feature should they enable?**

- [ ] Multi-AZ Deployment
- [ ] Provisioned IOPS
- [x] RDS Storage Auto-scaling
- [ ] Aurora Serverless

**Explanation:** RDS Storage Auto-scaling automatically increases storage capacity when it detects that the database is running out of free space.

**Question 27: A university is building a data lake on Amazon S3. They need to continuously replicate data from their operational MariaDB database on RDS to S3 for analysis. Which service is designed for this data replication task?**

- [ ] AWS Glue
- [x] AWS Database Migration Service (DMS)
- [ ] AWS Snowball Edge
- [ ] Amazon Kinesis Data Firehose

**Explanation:** AWS DMS can be used for one-time migrations and for continuous data replication from a supported source (like RDS MariaDB) to a target (like Amazon S3).

**Question 28: A startup is using a single Amazon RDS instance for their production database. They want to ensure that if the underlying hardware fails, the database can recover automatically with minimal data loss and downtime. Which configuration should they use?**

- [ ] Read Replicas in the same AZ
- [ ] Automated Backups with a 1-day retention period
- [x] Multi-AZ Deployment
- [ ] Cross-region Snapshots

**Explanation:** Multi-AZ deployment provides high availability and automatic failover. AWS automatically provisions a standby instance in a different AZ and replicates data synchronously.

**Question 29: You have an existing Amazon RDS for SQL Server instance that was created without encryption. Your security auditor requires all databases to be encrypted at rest. How can you encrypt this existing database?**

- [ ] Modify the RDS instance and check the "Enable Encryption" box.
- [ ] Use an IAM policy to force encryption on the instance.
- [x] Take a snapshot, copy the snapshot to a new encrypted snapshot, and restore from that encrypted snapshot.
- [ ] Enable SSL/TLS on the application connection strings.

**Explanation:** You cannot enable encryption on an existing unencrypted RDS instance. You must take a snapshot, copy it to an encrypted version, and restore a new instance from that version.

**Question 30: A company wants to follow the AWS Well-Architected Framework's "Performance Efficiency" pillar for their database layer. What is a key best practice for this pillar?**

- [ ] Always select the largest instance type available to ensure no performance issues.
- [ ] Manually patch the database weekly.
- [x] Use a data-driven approach to select the correct database type and size based on workload characteristics.
- [ ] Use a single database type for all microservices to simplify management.

**Explanation:** Performance Efficiency involves selecting the right resource type (relational vs non-relational) and size based on data characteristics and access patterns.
