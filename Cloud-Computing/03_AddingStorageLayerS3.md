<div align="center">
  <h1>Adding a Storage Layer with Amazon S3</h1>
  <sub>March 04, 2026</sub>
</div>

**Question 1: Due to a company merger, a data engineer needs to increase their object storage capacity. They are not sure how much storage they will need. They want a highly scalable service that can store unstructured, semistructured, and structured data. Which service would be the most cost effective to accomplish this task?**

- [x] Amazon S3
- [ ] Amazon Elastic Block Store (Amazon EBS)
- [ ] AWS Storage Gateway
- [ ] Amazon RDS

**Explanation:** Amazon S3 stores massive (unlimited) amounts of unstructured data.

**Question 2: Amazon S3 provides a good solution for which use case?**

- [ ] Ledger data that is updated and accessed frequently
- [x] An internet-accessible storage location for video files that an external website can access
- [ ] Hourly storage of frequently accessed temporary files
- [ ] A data warehouse for business intelligence

**Explanation:** Amazon S3 is often used to store and distribute videos, photos, music files, and other media.

**Question 3: A company is interested in using Amazon S3 to host their website instead of a traditional web server. Which types of content does Amazon S3 support for static web hosting? (Select THREE.)**

- [x] HTML files and image files
- [ ] Database engine
- [x] Client-side scripts
- [x] Video and sound files
- [ ] E. Dynamic HTML files
- [ ] F. Server-side scripts

**Explanation:** Amazon S3 supports static content, including HTML files, images, and videos.

**Question 4: A company wants to use an S3 bucket to store sensitive data. Which actions can they take to protect their data? (Select TWO.)**

- [ ] Enabling server-side encryption on the S3 bucket after uploading sensitive data
- [x] Enabling server-side encryption on the S3 bucket before uploading sensitive data
- [ ] Using Secure File Transfer Protocol (SFTP) to connect directly to Amazon S3
- [x] Using client-side encryption to protect data in transit before it is sent to Amazon S3
- [ ] E. Uploading unencrypted files to Amazon S3 because Amazon S3 encrypts the files by default

**Explanation:** You can protect data by encrypting it on the client side before uploading, or you can allow Amazon S3 to encrypt objects via server-side encryption before it saves the objects to disk.

**Question 5: A company must create a common place to store shared files. Which requirements does Amazon S3 support? (Select TWO.)**

- [ ] Attach comments to files.
- [ ] Lock a file so that only one person at a time can edit it.
- [x] Recover deleted files.
- [x] Maintain different versions of files.

**Explanation:** Amazon S3 versioning protects objects from accidental overwrites and deletes, allowing you to maintain and recover different versions of files.

**Question 6: A customer service team accesses case data daily for up to 30 days. Cases can be reopened and require immediate access for 1 year after they are closed. Reopened cases require 2 days to process. Which solution meets the requirements and is the most cost efficient?**

- [ ] Store all case data in S3 Standard so that it is available whenever it is needed.
- [ ] Store case data in S3 Standard. Use a lifecycle policy to move the data into Amazon S3 Glacier Flexible Retrieval after 30 days.
- [ ] Store case data in S3 Intelligent-Tiering to automatically move data between tiers based on access frequency.
- [x] Store case data in S3 Standard. Use a lifecycle policy to move the data into S3 Standard-Infrequent Access (S3 Standard-IA) after 30 days.

**Explanation:** S3 Standard-IA has a minimum storage duration of 30 days and requires a 128 KB minimum capacity charge, making it suitable for less frequently accessed data that still requires immediate retrieval.

**Question 7: Which option takes advantage of edge locations in Amazon CloudFront to transfer files over long distances to an S3 bucket?**

- [ ] AWS Transfer Family
- [ ] AWS SDKs
- [ ] Amazon S3 REST API
- [x] Amazon S3 Transfer Acceleration

**Explanation:** S3 Transfer Acceleration optimizes transfer speeds from across the world into S3 buckets by utilizing globally distributed edge locations in CloudFront.

**Question 8: A video producer must regularly transfer several video files to Amazon S3. The files range from 100–700 MB. The internet connection has been unreliable, causing some uploads to fail. Which solution provides the fastest, most reliable, and most cost-effective way to transfer these files to Amazon S3?**

- [ ] AWS Management Console
- [ ] Amazon S3 Transfer Acceleration
- [x] Amazon S3 multipart uploads
- [ ] AWS Transfer Family

**Explanation:** Multipart uploads recover quickly from any network issues and allow you to pause and resume object uploads, making it highly reliable for larger files.

**Question 9: Which Amazon S3 storage class is designed for backup copies of on-premises data or easily re-creatable data?**

- [ ] S3 Standard-Infrequent Access (S3 Standard-IA)
- [x] S3 One Zone-Infrequent Access (S3 One Zone-IA)
- [ ] S3 Glacier Instant Retrieval
- [ ] S3 Intelligent-Tiering

**Explanation:** S3 One Zone-IA stores data in a single Availability Zone, meaning it lacks the geographic redundancy of other classes and is therefore best for easily re-creatable data.

**Question 10: A company needs to retain records for regulatory purposes for a 7-year period. These records are rarely accessed (once or twice a year). What is the lowest-cost storage class for Amazon S3?**

- [x] S3 Glacier Deep Archive
- [ ] S3 One Zone-Infrequent Access (S3 One Zone-IA)
- [ ] S3 Intelligent-Tiering
- [ ] S3 Standard-Infrequent Access (S3 Standard-IA)

**Explanation:** S3 Glacier Deep Archive has a minimum storage duration of 180 days and does not require a minimum capacity charge, making it ideal for long-term, rarely accessed archives.

**Question 11: What type of storage service is Amazon S3 classified as?**

- [ ] Block storage
- [ ] File storage
- [x] Object storage
- [ ] Relational storage

**Explanation:** Amazon S3 is an object storage service.

**Question 12: What is the maximum file size limit for a single object stored in Amazon S3?**

- [ ] 1 TB
- [x] 5 TB
- [ ] 160 GB
- [ ] Unlimited

**Explanation:** Five TB is the maximum file size of a single object.

**Question 13: What level of durability does Amazon S3 Standard storage provide?**

- [ ] 99.99% (4 nines)
- [ ] 99.9% (3 nines)
- [x] 99.999999999% (11 nines)
- [ ] 100%

**Explanation:** S3 Standard storage provides 11 nines (or 99.999999999 percent) of durability.

**Question 14: In the context of Amazon S3, what exactly is a "bucket"?**

- [ ] A file directory
- [ ] A storage volume
- [x] A container for objects
- [ ] A database table

**Explanation:** A bucket is a container for objects.

**Question 15: Which Amazon S3 storage class automatically transfers data to a different storage class without requiring changes to your application?**

- [ ] S3 Standard
- [x] S3 Intelligent-Tiering
- [ ] S3 One Zone-IA
- [ ] S3 Lifecycle Configuration

**Explanation:** S3 Intelligent-Tiering is a specific object storage class designed for this purpose, whereas setting an S3 lifecycle policy can also automatically transfer your data to a different storage class.

**Question 16: What types of actions can be defined using Amazon S3 lifecycle configurations?**

- [ ] Encryption and decryption actions
- [x] Transition actions and expiration actions
- [ ] Replication and backup actions
- [ ] Access control actions

**Explanation:** Amazon S3 lifecycle configurations define actions that Amazon S3 applies to a group of objects, specifically transition actions to another storage class and expiration actions defining when objects expire.

**Question 17: If versioning is disabled or suspended on an S3 bucket, what happens when you upload an object with the same key as an existing object?**

- [ ] The upload is rejected.
- [ ] A new object is created with a different version ID.
- [x] It overwrites the original object, and the previous object is no longer retrievable.
- [ ] The new object is saved with a modified key name.

**Explanation:** If versioning is disabled or suspended, uploading an object with the same key overwrites the original object, and the previous object is no longer retrievable.

**Question 18: When a standard GET request is made for an object key in a version-enabled S3 bucket, what does Amazon S3 return?**

- [x] The most recent version
- [ ] The original version
- [ ] A list of all versions
- [ ] A 404 error

**Explanation:** Requests for an object key return the most recent version.

**Question 19: What level of data consistency does the Amazon S3 data consistency model provide for all GET, LIST, PUT, and DELETE operations?**

- [ ] Eventual consistency
- [x] Read-after-write consistency
- [ ] Strong consistency only for S3 Standard
- [ ] No consistency guarantees

**Explanation:** Amazon S3 provides read-after-write consistency for all GET, LIST, PUT, and DELETE operations on objects in S3 buckets.

**Question 20: Which fully managed AWS service is used to transfer files into and out of Amazon S3 using protocols like SFTP, FTPS, and FTP?**

- [ ] S3 Transfer Acceleration
- [ ] AWS Storage Gateway
- [ ] Amazon CloudFront
- [x] AWS Transfer Family

**Explanation:** AWS Transfer Family is used to transfer files into and out of Amazon S3 storage over Secure Shell (SSH) File Transfer Protocol (SFTP), File Transfer Protocol Secure (FTPS), and File Transfer Protocol (FTP).

**Question 21: What is the default access configuration for newly created S3 buckets and objects?**

- [ ] Public read-only
- [ ] Public full access
- [x] Private and protected
- [ ] Accessible to any authenticated AWS user

**Explanation:** S3 buckets and objects created are private and protected by default.

**Question 22: Which Amazon S3 feature defines a way for client web applications loaded in one domain to interact with resources located in a different domain?**

- [ ] Access control lists (ACLs)
- [ ] Bucket policies
- [x] Cross-origin resource sharing (CORS)
- [ ] Preassigned URLs

**Explanation:** CORS defines a way for client web applications that are loaded in one domain to interact with resources in a different domain.

**Question 23: Which of the following is NOT an approved tool or method for uploading objects to Amazon S3?**

- [ ] AWS Management Console
- [ ] AWS Command Line Interface (AWS CLI)
- [x] Remote Desktop Protocol (RDP)
- [ ] Amazon S3 REST API

**Explanation:** You can upload objects to Amazon S3 using the AWS Management Console, AWS CLI, AWS SDKs, or the Amazon S3 REST API.

**Question 24: Which tool or feature specifically makes S3 buckets completely inaccessible to the public?**

- [ ] AWS IAM policies
- [x] Block Public Access feature
- [ ] AWS Trusted Advisor
- [ ] Amazon S3 access points

**Explanation:** The Block Public Access feature makes buckets inaccessible to the public.

**Question 25: When using the AWS Management Console's drag-and-drop feature to upload a file to S3, what is the maximum file size permitted?**

- [x] 160 GB
- [ ] 5 TB
- [ ] 100 MB
- [ ] 5 GB

**Explanation:** The AWS Management Console wizard-based approach, including the drag and drop option, has a maximum size limit of 160 GB.

**Question 26: What is the minimum storage duration charge applied to objects stored in S3 Glacier Instant Retrieval?**

- [ ] 30 days
- [ ] 60 days
- [x] 90 days
- [ ] 180 days

**Explanation:** S3 Glacier Instant Retrieval has a minimum storage duration charge of 90 days.

**Question 27: For which of the following scenarios does Amazon S3 NOT charge a data transfer fee?**

- [ ] Data transferred out to the internet after the first 100 GB
- [x] Data transferred out to CloudFront
- [ ] Data transferred to a different AWS Region
- [ ] Gigabytes of objects stored per month

**Explanation:** There is no charge for data transferred out to CloudFront.

**Question 28: How does Amazon S3 implement the concept of a "folder structure" within a bucket?**

- [ ] By using physical directories
- [x] By using object prefixes
- [ ] By utilizing metadata tagging
- [ ] By configuring sub-buckets

**Explanation:** You use prefixes to imply a folder structure in an S3 bucket.

**Question 29: Which feature should you use to audit and report on the replication and encryption status of your S3 objects for compliance needs?**

- [ ] S3 Transfer Acceleration
- [ ] Bucket Policies
- [x] Amazon S3 Inventory
- [ ] AWS Trusted Advisor

**Explanation:** You use Amazon S3 Inventory to audit and report on the replication and encryption status of your objects for business, compliance, and regulatory needs.

**Question 30: According to the AWS Well-Architected Framework, performing a cost analysis for different usage over time falls under which best practice pillar?**

- [ ] Performance Efficiency
- [ ] Reliability
- [ ] Security
- [x] Cost Optimization

**Explanation:** Performing cost analysis for different usage over time is a best practice approach for Cost Optimization.
