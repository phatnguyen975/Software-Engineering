<div align="center">
  <h1>Adding a Database Layer</h1>
  <sub>March 04, 2026</sub>
</div>

**Question 1: Which statement that compares a database service that AWS manages with a database on an Amazon EC2 instance is true?**

- [ ] A. Configuring backups for a database on an EC2 instance isn't needed.
- [x] B. AWS manages database patches for a database on a managed database service.
- [ ] C. Configuring backups for a database on a managed database service isn't needed.
- [ ] D. AWS manages operating system (OS) patches for a database on an EC2 instance.

**Explanation:** When hosting a database on a managed AWS database service (like Amazon RDS), AWS assumes responsibility for tasks such as OS installation, OS patches, database installation, and database patches.

**Question 2: A small startup company is deciding which database service to use for an enrollment system for their online training website. Which requirements might lead them to select Amazon RDS rather than Amazon DynamoDB? (Select TWO.)**

- [ ] A. Data and transactions must be encrypted to protect personal information.
- [x] B. Student, course, and registration data are stored in many different tables.
- [x] C. The data is highly structured.
- [ ] D. The enrollment system must be highly available.
- [ ] E. Data must be backed up in case of disasters.

**Explanation:** Relational databases like Amazon RDS are optimized for highly structured data stored in tables. They also support complex queries through joins, making them ideal when data is distributed across many different interrelated tables. Features like encryption, high availability, and backups are supported by both RDS and DynamoDB.

**Question 3: A startup company is building an order inventory system with a web frontend and is looking for a real-time transactional database. Which service would best meet their needs?**

- [ ] A. Amazon DocumentDB (with MongoDB compatibility)
- [ ] B. Amazon Redshift
- [x] C. Amazon DynamoDB
- [ ] D. Amazon Neptune

**Explanation:** Amazon DynamoDB is a fully managed NoSQL database that delivers millisecond performance and is specifically designed for mission-critical workloads that prioritize speed, making it an excellent fit for real-time transactional systems.

**Question 4: A small game company is designing an online game, where thousands of players can create their own in-game objects. The current design uses a MySQL database in Amazon RDS to store data for player-created objects. Which proposed online game object features could make Amazon DynamoDB a better solution? (Select TWO.)**

- [ ] A. Game data items that include binary data and might exceed 700 MB
- [x] B. Unpredictable object attributes for player-created objects
- [x] C. A high amount of read activity on player-created objects and a low amount of writes
- [ ] D. Game items that can be modified using data contained in other tables
- [ ] E. A set of common object attributes for player-created objects

**Explanation:** DynamoDB is a NoSQL database that uses flexible schemas, which is perfect for data with unpredictable attributes. It is also highly optimized for fast access to semi-structured data with high read and write throughput, and is commonly used as a caching layer to improve read performance.

**Question 5: An organization is concerned about an increase in fraud. Which service could help with building real-time graph database queries for fraud detection?**

- [ ] A. Amazon DynamoDB
- [x] B. Amazon Neptune
- [ ] C. Amazon Redshift
- [ ] D. Amazon RDS

**Explanation:** Amazon Neptune is a purpose-built graph database designed to find connections and navigate highly connected datasets. Fraud detection is one of its primary common use cases.

**Question 6: A data engineer must host a new Microsoft SQL Server database in AWS for a project. Which service could they use to accomplish this task?**

- [ ] A. Amazon DocumentDB (with MongoDB compatibility)
- [X] B. Amazon Aurora
- [ ] C. Amazon Neptune
- [ ] D. Amazon DynamoDB

**Explanation:** Amazon RDS provides seven familiar database engines to choose from, one of which specifically includes RDS for SQL Server. None of the visible options in the image natively support Microsoft SQL Server.

**Question 7: Which techniques should be used to secure an Amazon RDS database? (Select THREE.)**

- [ ] A. A virtual private gateway (VGW) to filter traffic from restricted networks
- [x] B. Security groups to control network access to individual RDS instances
- [x] C. A virtual private cloud (VPC) to provide instance isolation
- [ ] D. AWS Identity and Access Management (IAM) policies to define access at the table, row, and column levels
- [x] E. Encryption both at rest and in transit to protect sensitive data
- [ ] F. An Amazon Virtual Private Cloud (Amazon VPC) gateway endpoint to prevent traffic from traversing the internet

**Explanation:** Security best practices for Amazon RDS include running the instance in a VPC for network isolation, using security groups to control connecting IP addresses, and utilizing SSL/TLS for transit encryption along with AWS KMS for encryption at rest.

**Question 8: Which techniques should be used to secure Amazon DynamoDB? (Select THREE.)**

- [x] A. An Amazon Virtual Private Cloud (Amazon VPC) gateway endpoint to prevent traffic from traversing the internet
- [ ] B. Security groups to control network access to individual instances
- [ ] C. A virtual private gateway (VGW) to filter traffic from restricted networks
- [x] D. Encryption to protect sensitive data
- [x] E. AWS Identity and Access Management (IAM) policies to define access at the table, item, or attribute level
- [ ] F. A virtual private cloud (VPC) to provide instance isolation and firewall protection

**Explanation:** DynamoDB security best practices include using a VPC endpoint and policies to access DynamoDB securely without traversing the public internet. It also relies on IAM policy conditions for fine-grained access control, and automatically encrypts all customer data at rest by default.

**Question 9: A company wants to migrate their on-premises Oracle database to Amazon Aurora MySQL. Which process describes the high-level steps most accurately?**

- [ ] A. Use AWS Database Migration Service (AWS DMS) to directly migrate from the Oracle database to Amazon Aurora MySQL.
- [x] B. Use AWS Schema Conversion Tool (AWS SCT) to convert the schema, and then use AWS Database Migration Service (AWS DMS) to migrate the data.
- [ ] C. Use AWS Database Migration Service (AWS DMS) to migrate the data, and then use AWS Schema Conversion Tool (AWS SCT) to convert the schema.
- [ ] D. Use AWS Schema Conversion Tool (AWS SCT) to synchronously convert the schema and migrate the data.

**Explanation:** When performing a heterogeneous migration (e.g., migrating from Oracle to MySQL), you must first use the AWS Schema Conversion Tool (AWS SCT) to convert your source schema and SQL code into the target format. After the schema is converted, you use AWS DMS to handle the actual data migration replication task.

**Question 10: A cloud architect is setting up an application to use an Amazon RDS MySQL DB instance. The database must be architected for high availability across Availability Zones and AWS Regions with minimal downtime. How should they meet this requirement?**

- [ ] A. Set up an RDS MySQL Single-AZ DB instance. Copy automated snapshots to at least one other Region.
- [ ] B. Set up an RDS MySQL Single-AZ DB instance. Configure a read replica in a different Region.
- [ ] C. Set up an RDS MySQL Multi-AZ DB instance. Configure an appropriate backup window.
- [x] D. Set up an RDS MySQL Multi-AZ DB instance. Configure a read replica in a different Region.

**Explanation:** A Multi-AZ deployment is required to achieve high availability across multiple Availability Zones through synchronous replication. To extend this high availability and scalability across different AWS Regions, you configure cross-Region read replicas.

**Question 11: Which database scaling approach involves upgrading the compute power and memory of a single existing database instance?**

- [ ] A. Horizontal scaling
- [ ] B. Elastic scaling
- [x] C. Vertical scaling
- [ ] D. Cluster scaling

**Explanation:** Vertical scaling refers to expanding the capacity of a single machine or database instance, such as increasing its CPU or RAM (e.g., upgrading from a smaller instance type to a larger one).

**Question 12: When evaluating database storage characteristics, which type of database relies primarily on flexible schemas and scales horizontally?**

- [ ] A. Relational databases
- [x] B. Non-relational databases
- [ ] C. Legacy SQL databases
- [ ] D. On-premises databases

**Explanation:** Non-relational databases feature flexible schemas and are designed to scale horizontally to provide high scalability and performance.

**Question 13: In the architecture of an Amazon Aurora database cluster, what is the role of the Cluster Volume?**

- [ ] A. It acts as a caching layer between the web server and the database.
- [ ] B. It manages identity and access permissions for database users.
- [x] C. It is a virtual database storage volume that spans multiple Availability Zones.
- [ ] D. It is an isolated Amazon EC2 instance used solely for reporting queries.

**Explanation:** An Aurora cluster volume is a virtual database storage volume that spans multiple Availability Zones, ensuring that each Availability Zone has a copy of the DB cluster data for high availability and durability.

**Question 14: What problem does Amazon RDS Proxy primarily solve for applications connecting to relational databases?**

- [ ] A. It translates NoSQL queries into SQL queries automatically.
- [x] B. It prevents a large number of open connections from exhausting the database's compute and memory resources.
- [ ] C. It provides a graphical interface for viewing database schemas.
- [ ] D. It permanently stores immutable ledger entries.

**Explanation:** A large number of open connections can exhaust a database server's resources. Amazon RDS Proxy sits between the application and the database to pool and share connections, ensuring the database receives fewer connections and works efficiently.

**Question 15: If a database administrator needs to retain a backup of an Amazon RDS instance indefinitely until they choose to manually delete it, which backup method should they use?**

- [ ] A. Automated Backups
- [x] B. Database Snapshots
- [ ] C. Amazon RDS Proxy
- [ ] D. Multi-AZ Replication

**Explanation:** Database Snapshots are user-initiated backups that are kept until the user explicitly deletes them. In contrast, automated backups are automatically deleted after the configured retention period expires.

**Question 16: Which AWS service provides a fully managed, in-memory database designed for workloads requiring latency-sensitive performance and no data loss?**

- [ ] A. Amazon DocumentDB
- [x] B. Amazon MemoryDB
- [ ] C. Amazon Keyspaces
- [ ] D. Amazon Redshift

**Explanation:** Amazon MemoryDB (for Redis) is an in-memory database service that stores the entire dataset in memory to minimize response times while providing data durability and recoverability with no data loss.

**Question 17: Which component in an Amazon DynamoDB table is uniquely identifiable among all other items?**

- [ ] A. Partition key
- [ ] B. Table attribute
- [ ] C. Sort key
- [x] D. An item

**Explanation:** An item in a DynamoDB table is a group of attributes that is uniquely identifiable among all of the other items within that table.

**Question 18: What is the key characteristic of an Amazon DynamoDB Local Secondary Index (LSI) compared to the base table?**

- [ ] A. It uses a completely different partition key and sort key.
- [x] B. It uses the same partition key as the base table but an alternate sort key.
- [ ] C. It must be hosted in a different AWS Region than the base table.
- [ ] D. It only supports eventual consistency.

**Explanation:** A Local Secondary Index (LSI) uses the same partition key as the base table but features an alternate sort key. It also supports both eventual or strong consistency.

**Question 19: A development team needs an on-demand, auto-scaling relational database configuration that provides hands-off capacity management. Which solution meets these criteria?**

- [x] A. Amazon Aurora Serverless
- [ ] B. Amazon DynamoDB Global Tables
- [ ] C. Amazon RDS Custom
- [ ] D. Amazon Redshift Serverless

**Explanation:** Aurora Serverless is an on-demand auto scaling configuration for Amazon Aurora that provides hands-off capacity management, making it highly suitable for variable workloads.

**Question 20: According to the AWS Well-Architected Framework, implementing secure key management and enforcing encryption at rest fall under which pillar?**

- [ ] A. Cost Optimization
- [x] B. Security
- [ ] C. Performance Efficiency
- [ ] D. Operational Excellence

**Explanation:** The Security pillar of the AWS Well-Architected Framework focuses on data protection best practices, which explicitly include implementing secure key management and enforcing encryption at rest.

**Question 21: Which AWS purpose-built database service is specifically optimized to provide an immutable and cryptographically verifiable transaction log?**

- [ ] A. Amazon Timestream
- [x] B. Amazon QLDB
- [ ] C. Amazon Keyspaces
- [ ] D. Amazon DocumentDB

**Explanation:** Amazon Quantum Ledger Database (Amazon QLDB) is a ledger database that provides a transparent, immutable, and cryptographically verifiable transaction log for maintaining an accurate history of application data.

**Question 22: An application relies on data generated by Internet of Things (IoT) sensors over time. The development team needs to query and analyze this data efficiently using SQL. Which database is best suited for this?**

- [ ] A. Amazon Neptune
- [x] B. Amazon Timestream
- [ ] C. Amazon RDS for SQL Server
- [ ] D. Amazon Aurora

**Explanation:** Amazon Timestream is a timeseries database designed to collect, store, and process data sequenced by time. It has built-in timeseries functions for quick analysis of data (like IoT trends) using SQL.

**Question 23: If a company wants to perform a "homogeneous migration" of their on-premises MySQL database to Amazon RDS for MySQL, which core AWS tool should they use?**

- [ ] A. AWS Schema Conversion Tool (AWS SCT)
- [x] B. AWS Database Migration Service (AWS DMS)
- [ ] C. Amazon RDS Proxy
- [ ] D. Amazon S3

**Explanation:** AWS DMS is a managed migration and replication service used to move databases. For a homogeneous migration (same source and target engine, like MySQL to MySQL), AWS DMS is used directly to run the replication task without needing SCT for schema conversion.

**Question 24: Which instance type family in Amazon RDS is best suited for workloads that require significant RAM, such as the `db.r6g.large` instance?**

- [ ] A. General purpose
- [ ] B. Compute-optimized
- [x] C. Memory-optimized
- [ ] D. Storage-optimized

**Explanation:** Instance types like R6g, R5, X2g, and X1E belong to the memory-optimized family, which is designed for workloads requiring large amounts of memory (RAM).

**Question 25: A company needs to store semi-structured JSON documents and perform complex one-time querying and indexing on them. Which database service is the best fit?**

- [x] A. Amazon DocumentDB
- [ ] B. Amazon QLDB
- [ ] C. Amazon Redshift
- [ ] D. Amazon Keyspaces

**Explanation:** Amazon DocumentDB is a MongoDB-compatible JSON document database that is ideal for storing complex, dynamic documents that require one-time querying, indexing, and aggregations.

**Question 26: In Amazon DynamoDB, what is created by combining a partition key and a sort key?**

- [ ] A. A global table
- [x] B. A composite primary key
- [ ] C. A simple primary key
- [ ] D. An eventual consistency read

**Explanation:** A composite primary key in DynamoDB is composed of two attributes: the partition key and the sort key.

**Question 27: To optimize the cost of an Amazon RDS architecture, what is the best practice approach according to the Cost Optimization pillar?**

- [ ] A. Always select the largest available instance size to prevent future bottlenecks.
- [x] B. Select the resource type, size, and number based on data.
- [ ] C. Disable automated backups to save storage costs.
- [ ] D. Use unencrypted snapshots to reduce compute overhead.

**Explanation:** The AWS Well-Architected Framework's Cost Optimization pillar dictates that you should use a data-driven approach to select the correct resource type, size, and number to achieve cost-effective resources.

**Question 28: How does Amazon RDS Proxy improve the security posture of an application?**

- [ ] A. It automatically encrypts data at rest using a customer-managed KMS key.
- [ ] B. It acts as a firewall that blocks all traffic from the public internet.
- [x] C. It enforces IAM authentication and stores credentials securely in AWS Secrets Manager.
- [ ] D. It isolates the database in a dedicated Virtual Private Cloud (VPC).

**Explanation:** Amazon RDS Proxy improves application security by enforcing IAM authentication and securely storing database credentials in AWS Secrets Manager rather than embedding them in application code.

**Question 29: Which managed AWS database provides a wide-column data model that stores data in flexible columns and is compatible with Apache Cassandra?**

- [ ] A. Amazon DocumentDB
- [x] B. Amazon Keyspaces
- [ ] C. Amazon Neptune
- [ ] D. Amazon Timestream

**Explanation:** Amazon Keyspaces (for Apache Cassandra) uses a wide-column data model that stores data in flexible columns, allowing data to evolve over time, and is compatible with Cassandra workloads.

**Question 30: Which of the following is an example of a detective security control for Amazon DynamoDB?**

- [ ] A. Using IAM policies for base authorization
- [ ] B. Using a VPC endpoint to access the database
- [x] C. Monitoring DynamoDB operations by using AWS CloudTrail
- [ ] D. Encrypting all customer data at rest by default

**Explanation:** Monitoring DynamoDB operations using AWS CloudTrail is categorized as a Detective security best practice, whereas IAM roles, policies, and VPC endpoints are categorized as Preventative controls.
