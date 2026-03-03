<div align="center">
  <h1>Adding a Storage Layer with Amazon S3</h1>
  <sub>March 04, 2026</sub>
</div>

**Question 1: Due to a company merger, a data engineer needs to increase their object storage capacity. They are not sure how much storage they will need. They want a highly scalable service that can store unstructured, semistructured, and structured data. Which service would be the most cost effective to accomplish this task?**

- [x] A. Amazon S3
- [ ] B. Amazon Elastic Block Store (Amazon EBS)
- [ ] C. AWS Storage Gateway
- [ ] D. Amazon RDS

**Explanation:** Amazon S3 stores massive (unlimited) amounts of unstructured data.

**Question 2: Amazon S3 provides a good solution for which use case?**

- [ ] A. Ledger data that is updated and accessed frequently
- [x] B. An internet-accessible storage location for video files that an external website can access
- [ ] C. Hourly storage of frequently accessed temporary files
- [ ] D. A data warehouse for business intelligence

**Explanation:** Amazon S3 is often used to store and distribute videos, photos, music files, and other media.

**Question 3: A company is interested in using Amazon S3 to host their website instead of a traditional web server. Which types of content does Amazon S3 support for static web hosting? (Select THREE.)**

- [x] A. HTML files and image files
- [ ] B. Database engine
- [x] C. Client-side scripts
- [x] D. Video and sound files
- [ ] E. Dynamic HTML files
- [ ] F. Server-side scripts

**Explanation:** Amazon S3 supports static content, including HTML files, images, and videos.

**Question 4: A company wants to use an S3 bucket to store sensitive data. Which actions can they take to protect their data? (Select TWO.)**

- [ ] A. Enabling server-side encryption on the S3 bucket after uploading sensitive data
- [x] B. Enabling server-side encryption on the S3 bucket before uploading sensitive data
- [ ] C. Using Secure File Transfer Protocol (SFTP) to connect directly to Amazon S3
- [x] D. Using client-side encryption to protect data in transit before it is sent to Amazon S3
- [ ] E. Uploading unencrypted files to Amazon S3 because Amazon S3 encrypts the files by default

**Explanation:** You can protect data by encrypting it on the client side before uploading, or you can allow Amazon S3 to encrypt objects via server-side encryption before it saves the objects to disk.

**Question 5: A company must create a common place to store shared files. Which requirements does Amazon S3 support? (Select TWO.)**

- [ ] A. Attach comments to files.
- [ ] B. Lock a file so that only one person at a time can edit it.
- [x] C. Recover deleted files.
- [x] D. Maintain different versions of files.

**Explanation:** Amazon S3 versioning protects objects from accidental overwrites and deletes, allowing you to maintain and recover different versions of files.

**Question 6: A customer service team accesses case data daily for up to 30 days. Cases can be reopened and require immediate access for 1 year after they are closed. Reopened cases require 2 days to process. Which solution meets the requirements and is the most cost efficient?**

- [ ] A. Store all case data in S3 Standard so that it is available whenever it is needed.
- [ ] B. Store case data in S3 Standard. Use a lifecycle policy to move the data into Amazon S3 Glacier Flexible Retrieval after 30 days.
- [ ] C. Store case data in S3 Intelligent-Tiering to automatically move data between tiers based on access frequency.
- [x] D. Store case data in S3 Standard. Use a lifecycle policy to move the data into S3 Standard-Infrequent Access (S3 Standard-IA) after 30 days.

**Explanation:** S3 Standard-IA has a minimum storage duration of 30 days and requires a 128 KB minimum capacity charge, making it suitable for less frequently accessed data that still requires immediate retrieval.

**Question 7: Which option takes advantage of edge locations in Amazon CloudFront to transfer files over long distances to an S3 bucket?**

- [ ] A. AWS Transfer Family
- [ ] B. AWS SDKs
- [ ] C. Amazon S3 REST API
- [x] D. Amazon S3 Transfer Acceleration

**Explanation:** S3 Transfer Acceleration optimizes transfer speeds from across the world into S3 buckets by utilizing globally distributed edge locations in CloudFront.

**Question 8: A video producer must regularly transfer several video files to Amazon S3. The files range from 100–700 MB. The internet connection has been unreliable, causing some uploads to fail. Which solution provides the fastest, most reliable, and most cost-effective way to transfer these files to Amazon S3?**

- [ ] A. AWS Management Console
- [ ] B. Amazon S3 Transfer Acceleration
- [x] C. Amazon S3 multipart uploads
- [ ] D. AWS Transfer Family

**Explanation:** Multipart uploads recover quickly from any network issues and allow you to pause and resume object uploads, making it highly reliable for larger files.

**Question 9: Which Amazon S3 storage class is designed for backup copies of on-premises data or easily re-creatable data?**

- [ ] A. S3 Standard-Infrequent Access (S3 Standard-IA)
- [x] B. S3 One Zone-Infrequent Access (S3 One Zone-IA)
- [ ] C. S3 Glacier Instant Retrieval
- [ ] D. S3 Intelligent-Tiering

**Explanation:** S3 One Zone-IA stores data in a single Availability Zone, meaning it lacks the geographic redundancy of other classes and is therefore best for easily re-creatable data.

**Question 10: A company needs to retain records for regulatory purposes for a 7-year period. These records are rarely accessed (once or twice a year). What is the lowest-cost storage class for Amazon S3?**

- [x] A. S3 Glacier Deep Archive
- [ ] B. S3 One Zone-Infrequent Access (S3 One Zone-IA)
- [ ] C. S3 Intelligent-Tiering
- [ ] D. S3 Standard-Infrequent Access (S3 Standard-IA)

**Explanation:** S3 Glacier Deep Archive has a minimum storage duration of 180 days and does not require a minimum capacity charge, making it ideal for long-term, rarely accessed archives.

**Question 11: What type of storage service is Amazon S3 classified as?**

- [ ] A. Block storage
- [ ] B. File storage
- [x] C. Object storage
- [ ] D. Relational storage

**Explanation:** Amazon S3 is an object storage service.

**Question 12: What is the maximum file size limit for a single object stored in Amazon S3?**

- [ ] A. 1 TB
- [x] B. 5 TB
- [ ] C. 160 GB
- [ ] D. Unlimited

**Explanation:** Five TB is the maximum file size of a single object.

**Question 13: What level of durability does Amazon S3 Standard storage provide?**

- [ ] A. 99.99% (4 nines)
- [ ] B. 99.9% (3 nines)
- [x] C. 99.999999999% (11 nines)
- [ ] D. 100%

**Explanation:** S3 Standard storage provides 11 nines (or 99.999999999 percent) of durability.

**Question 14: In the context of Amazon S3, what exactly is a "bucket"?**

- [ ] A. A file directory
- [ ] B. A storage volume
- [x] C. A container for objects
- [ ] D. A database table

**Explanation:** A bucket is a container for objects.

**Question 15: Which Amazon S3 storage class automatically transfers data to a different storage class without requiring changes to your application?**

- [ ] A. S3 Standard
- [x] B. S3 Intelligent-Tiering
- [ ] C. S3 One Zone-IA
- [ ] D. S3 Lifecycle Configuration

**Explanation:** S3 Intelligent-Tiering is a specific object storage class designed for this purpose, whereas setting an S3 lifecycle policy can also automatically transfer your data to a different storage class.

**Question 16: What types of actions can be defined using Amazon S3 lifecycle configurations?**

- [ ] A. Encryption and decryption actions
- [x] B. Transition actions and expiration actions
- [ ] C. Replication and backup actions
- [ ] D. Access control actions

**Explanation:** Amazon S3 lifecycle configurations define actions that Amazon S3 applies to a group of objects, specifically transition actions to another storage class and expiration actions defining when objects expire.

**Question 17: If versioning is disabled or suspended on an S3 bucket, what happens when you upload an object with the same key as an existing object?**

- [ ] A. The upload is rejected.
- [ ] B. A new object is created with a different version ID.
- [x] C. It overwrites the original object, and the previous object is no longer retrievable.
- [ ] D. The new object is saved with a modified key name.

**Explanation:** If versioning is disabled or suspended, uploading an object with the same key overwrites the original object, and the previous object is no longer retrievable.

**Question 18: When a standard GET request is made for an object key in a version-enabled S3 bucket, what does Amazon S3 return?**

- [x] A. The most recent version
- [ ] B. The original version
- [ ] C. A list of all versions
- [ ] D. A 404 error

**Explanation:** Requests for an object key return the most recent version.

**Question 19: What level of data consistency does the Amazon S3 data consistency model provide for all GET, LIST, PUT, and DELETE operations?**

- [ ] A. Eventual consistency
- [x] B. Read-after-write consistency
- [ ] C. Strong consistency only for S3 Standard
- [ ] D. No consistency guarantees

**Explanation:** Amazon S3 provides read-after-write consistency for all GET, LIST, PUT, and DELETE operations on objects in S3 buckets.

**Question 20: Which fully managed AWS service is used to transfer files into and out of Amazon S3 using protocols like SFTP, FTPS, and FTP?**

- [ ] A. S3 Transfer Acceleration
- [ ] B. AWS Storage Gateway
- [ ] C. Amazon CloudFront
- [x] D. AWS Transfer Family

**Explanation:** AWS Transfer Family is used to transfer files into and out of Amazon S3 storage over Secure Shell (SSH) File Transfer Protocol (SFTP), File Transfer Protocol Secure (FTPS), and File Transfer Protocol (FTP).

**Question 21: What is the default access configuration for newly created S3 buckets and objects?**

- [ ] A. Public read-only
- [ ] B. Public full access
- [x] C. Private and protected
- [ ] D. Accessible to any authenticated AWS user

**Explanation:** S3 buckets and objects created are private and protected by default.

**Question 22: Which Amazon S3 feature defines a way for client web applications loaded in one domain to interact with resources located in a different domain?**

- [ ] A. Access control lists (ACLs)
- [ ] B. Bucket policies
- [x] C. Cross-origin resource sharing (CORS)
- [ ] D. Preassigned URLs

**Explanation:** CORS defines a way for client web applications that are loaded in one domain to interact with resources in a different domain.

**Question 23: Which of the following is NOT an approved tool or method for uploading objects to Amazon S3?**

- [ ] A. AWS Management Console
- [ ] B. AWS Command Line Interface (AWS CLI)
- [x] C. Remote Desktop Protocol (RDP)
- [ ] D. Amazon S3 REST API

**Explanation:** You can upload objects to Amazon S3 using the AWS Management Console, AWS CLI, AWS SDKs, or the Amazon S3 REST API.

**Question 24: Which tool or feature specifically makes S3 buckets completely inaccessible to the public?**

- [ ] A. AWS IAM policies
- [x] B. Block Public Access feature
- [ ] C. AWS Trusted Advisor
- [ ] D. Amazon S3 access points

**Explanation:** The Block Public Access feature makes buckets inaccessible to the public.

**Question 25: When using the AWS Management Console's drag-and-drop feature to upload a file to S3, what is the maximum file size permitted?**

- [x] A. 160 GB
- [ ] B. 5 TB
- [ ] C. 100 MB
- [ ] D. 5 GB

**Explanation:** The AWS Management Console wizard-based approach, including the drag and drop option, has a maximum size limit of 160 GB.

**Question 26: What is the minimum storage duration charge applied to objects stored in S3 Glacier Instant Retrieval?**

- [ ] A. 30 days
- [ ] B. 60 days
- [x] C. 90 days
- [ ] D. 180 days

**Explanation:** S3 Glacier Instant Retrieval has a minimum storage duration charge of 90 days.

**Question 27: For which of the following scenarios does Amazon S3 NOT charge a data transfer fee?**

- [ ] A. Data transferred out to the internet after the first 100 GB
- [x] B. Data transferred out to CloudFront
- [ ] C. Data transferred to a different AWS Region
- [ ] D. Gigabytes of objects stored per month

**Explanation:** There is no charge for data transferred out to CloudFront.

**Question 28: How does Amazon S3 implement the concept of a "folder structure" within a bucket?**

- [ ] A. By using physical directories
- [x] B. By using object prefixes
- [ ] C. By utilizing metadata tagging
- [ ] D. By configuring sub-buckets

**Explanation:** You use prefixes to imply a folder structure in an S3 bucket.

**Question 29: Which feature should you use to audit and report on the replication and encryption status of your S3 objects for compliance needs?**

- [ ] A. S3 Transfer Acceleration
- [ ] B. Bucket Policies
- [x] C. Amazon S3 Inventory
- [ ] D. AWS Trusted Advisor

**Explanation:** You use Amazon S3 Inventory to audit and report on the replication and encryption status of your objects for business, compliance, and regulatory needs.

**Question 30: According to the AWS Well-Architected Framework, performing a cost analysis for different usage over time falls under which best practice pillar?**

- [ ] A. Performance Efficiency
- [ ] B. Reliability
- [ ] C. Security
- [x] D. Cost Optimization

**Explanation:** Performing cost analysis for different usage over time is a best practice approach for Cost Optimization.
