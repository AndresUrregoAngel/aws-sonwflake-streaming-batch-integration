# Kinesis streaming integration with Snowflake

[Snowflake](https://www.snowflake.com/) is a high-tech platform which approaches the Enterprises Data Warehouse (EDW), in a brand new and cutting-edge manner. This document is focus on how can we easily integrate a streaming source of data such as [Kinesis DS](https://aws.amazon.com/kinesis/data-streams/) in a streaming/micro-batching flows into Snowflake as landing zone. Depending on the use case it's possible to look up for serverless solutions at very affordable costs within the AWS stack. For instance, as part of the methodology carry out in this post , we explore  [Lambda functions](https://aws.amazon.com/lambda/) which are the cheapest way how we can automate pipelines in a very cost-effective manner, leveraging different integration and managing functionalities.

### Solution architecture

I mainly will configure an entry point capable of generate random data with a different number of batch in a time frame, the best quick and very easy to deploy producer for pilots in AWS is [KDG Kinesis Data Generator](https://aws.amazon.com/blogs/big-data/test-your-streaming-data-solution-with-the-new-amazon-kinesis-data-generator/). From this poinf the data will flow trhoughout a streaming services which collects the data in real-time and dispatch autimatically the functions that manipulate the data up to the two landing zones: [S3](https://aws.amazon.com/s3/) and [Snowflake](https://www.snowflake.com/)


![architecture](https://github.com/AndresUrregoAngel/cloud/blob/master/architectures/aws-connector-usecase.png)
