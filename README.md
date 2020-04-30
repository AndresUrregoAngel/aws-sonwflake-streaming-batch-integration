# Kinesis streaming integration with Snowflake

[Snowflake](https://www.snowflake.com/) is a high-tech platform which approaches the Enterprises Data Warehouse (EDW), in a brand new and cutting-edge manner. This document is focus on how can we easily integrate a streaming source of data such as [Kinesis DS](https://aws.amazon.com/kinesis/data-streams/) in a streaming/micro-batching flows into Snowflake as landing zone. Depending on the use case it's possible to look up for serverless solutions at very affordable costs within the AWS stack. For instance, as part of the methodology carry out in this post , we explore  [Lambda functions](https://aws.amazon.com/lambda/) which are the cheapest way how we can automate pipelines in a very cost-effective manner, leveraging different integration and managing functionalities.

### Solution architecture

I mainly will configure an entry point capable of generate random data with a different number of batch in a time frame, the best quick and very easy to deploy producer for pilots in AWS is [KDG Kinesis Data Generator](https://aws.amazon.com/blogs/big-data/test-your-streaming-data-solution-with-the-new-amazon-kinesis-data-generator/). From this poinf the data will flow trhoughout a streaming services which collects the data in real-time and dispatch autimatically the functions that manipulate the data up to the two landing zones: [S3](https://aws.amazon.com/s3/) and [Snowflake](https://www.snowflake.com/). To have a closer look over the architeacture deploy in this process please see below:


![architecture](https://github.com/AndresUrregoAngel/cloud/blob/master/architectures/aws-connector-uc.png)


### Prior requirements within the AWS configuration:

* [IAM roles](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles.html) to enable the lambda functions reach the services along the AWS ecosystem.
* [VPC](https://aws.amazon.com/vpc/) configuration with an active [NAT Gateway](https://docs.aws.amazon.com/vpc/latest/userguide/vpc-nat-gateway.html) to enable the lambda function that reach the Snowflake environemnt go throughout internet securely.
* Configure the [Kinesis Data Generator](https://awslabs.github.io/amazon-kinesis-data-generator/web/help.html) as producer into your environment
* Count with a valide Snowflake account and configure an external stage using the S3 bucket where the data is going to be store for the micro-batching approach.
* Inlcude a [lambda layer](https://docs.aws.amazon.com/lambda/latest/dg/configuration-layers.html) with [Pandas](https://pandas.pydata.org/docs/) python package 
* Create the two staging tables for both ingestion process on snowflake, find these scripts [here](https://github.com/AndresUrregoAngel/aws-sonwflake-streaming-batch-integration/blob/master/src/snowflakeddl/createtables.sql)
* Configure the [SNOWPIPE](https://docs.snowflake.com/en/user-guide/data-load-snowpipe-auto-s3.html#step-2-create-a-pipe-with-auto-ingest-enabled) procedure that will carry out the movement from your [external staging](https://docs.snowflake.com/en/user-guide/data-load-s3-create-stage.html) `S3` into the micro-batch staging table


### Configuration pipeline 

1. [Kinesis Data Generator](https://awslabs.github.io/amazon-kinesis-data-generator/web/help.html) is deployed within the AWS region where I'm going to set the full environment. As part of the produced ata I will implement a very basic data schema as expose below:

```
{
    "sensorId": {{random.number(50)}},
    "currentTemperature": {{random.number(
        {
            "min":10,
            "max":150
        }
    )}},
    "status": "{{random.arrayElement(
        ["OK","FAIL","WARN"]
    )}}"
}
```
Which give us the sample data input in this format: `{"sensorId": 6,"currentTemperature": 64,"status": "OK"}` One of the cool features around this AWS producer tool is the fact that we can scale on the flight the number of records we inster per second from 1 to 1k. This is wonderful to test how resiliant and scalable is our pipeline.

2. Kinesis Data Stream Listener is the entry point into the AWS stack, initially I have configure this service with a very simple and basic operationality with a single shard. 1 shard will empower my entry listener point to process up to 1K records per second and read equialy a max of 2MB/sec and write 1MB/sec. If you on your escenario need to pilot a pipeline with greater than these features please increase the number of shards or read more about this [here](https://docs.aws.amazon.com/streams/latest/dev/key-concepts.html).

3. Lambda functions

* SnowflakeOrchestrator : `Python 3.6` This function will consum the content of the Kinesis data stream, afterwards will turn the content of the messages in a file compiling the number of records we configure on the reading settings into a single file into S3 `Raw Landing Zone`. Finally, will pass over a second lambda function the content of the pulled records from the entry Kinesis data streaming , to trigger the streaming ingestion service.
    * To verify the code of this fucntion please go [here](https://github.com/AndresUrregoAngel/aws-sonwflake-streaming-batch-integration/tree/master/src/awslambdas/orchestrator)
    * The configuration of the reading kinesis data streaming for this function is below: notice the continues reading in batches of 100 records.
    ![kinesis-config](https://github.com/AndresUrregoAngel/cloud/blob/master/architectures/aws-connector-kinesis.png)

* SnowflakeUpsert : `Java 8` Once the prio function execute the invoke over this pipeline, the records are processed and insert right stratight into the snowflake `Staging Zone`, find out the source code for this function [here](https://github.com/AndresUrregoAngel/aws-sonwflake-streaming-batch-integration/tree/master/src/main/java/lambdaroot)

4. S3 raw landing zone: This bucket will host the data insert as result of the step 3 `SnowflakeOrchestrator` execution. This step will create sequentially `csv` files in this bucket over the path `kinesiscsv/sensor-<sendorId>`. Each of these files content the number of records sets on the batch to read from the Kinesis Data Streaming configuration of the lambda fucntion `SnowflakeOrchestrator`. These files will feed the micro-batch process trigger by Snowflake [SNOWPIPE](https://docs.snowflake.com/en/user-guide/data-load-snowpipe-auto-s3.html#step-2-create-a-pipe-with-auto-ingest-enabled)

5. Snowflake staging landing zone : The pipeline will end up having an automare feed process to populate two tables into the snowflake staging zone `KINESIS_SENSORS_STREAMING` and `KINESIS_SENSORS_MICROBATCH`, the scripts for these tables are available [here](https://github.com/AndresUrregoAngel/aws-sonwflake-streaming-batch-integration/blob/master/src/snowflakeddl/createtables.sql). Once the pipeline will be executed the data will flow all the architecture long until end up landing in each of these tables.




