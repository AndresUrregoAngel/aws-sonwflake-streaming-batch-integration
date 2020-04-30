import boto3
import json
import os

class snowIngestor:
    def __init__(self,lstrecords):
        self._lambda = boto3.client('lambda')
        self.payload = json.dumps({"content": lstrecords})


    def hookupIngestor(self):
        try:
            print(self.payload)
            self._lambda.invoke(
                FunctionName=os.environ['ingestorsf'],
                InvocationType='Event',
                LogType='Tail',
                Payload = self.payload
            )
        except Exception as error:
            print(error)