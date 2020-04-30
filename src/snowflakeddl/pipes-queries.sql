
// CREATE PIPE

CREATE OR REPLACE PIPE "DB"."SCHEMA"."PIPENAME" auto_ingest=true  AS
  COPY INTO "DB"."SCHEMA"."KINESIS_SENSORS_MICROBATCH"
      FROM @PERSONALAWS/kinesiscsv/
      FILE_FORMAT = (FORMAT_NAME = 'CSV');
      
      
 // VALIDATE RESULTS
 
 select 'streaming mode' as "ingestion way", count(*) as "num records" from "DB"."SCHEMA"."KINESIS_SENSORS_STREAMING"
union
select 'micro-batch mode' as "ingestion way", count(*) as "num records" from "DB"."SCHEMA"."KINESIS_SENSORS_MICROBATCH";
