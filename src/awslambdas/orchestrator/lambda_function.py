import json
import boto3
import base64
from content import contentMessage
from s3storer import s3storer
from snowflakeIngestor import snowIngestor
import pandas as pd

def lambda_handler(event, context):

    lstrecords = []


    for record in event['Records']:
        payload = base64.b64decode(record['kinesis']['data'])
        objRecord = json.loads(payload)
        filename = f'source-{objRecord["sensorId"]}'
        lstrecords.append(objRecord)


    df = pd.DataFrame(lstrecords)
    s3pipeline = s3storer(df,filename)
    s3pipeline.storerecords()


    snowflakeIngestor = snowIngestor(lstrecords)
    snowflakeIngestor.hookupIngestor()


    return {
        'statusCode': 200,
        'body': json.dumps('Hello from Lambda!')
    }
