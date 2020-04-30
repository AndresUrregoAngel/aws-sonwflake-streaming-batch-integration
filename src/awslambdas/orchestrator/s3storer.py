import pandas as pd
from io import StringIO
import boto3
import os


class s3storer:
    def __init__(self,df,filename):
        self._s3 = boto3.resource('s3')
        self.bucket = os.environ['bucketraw']
        self.df = df
        self.filename = filename

    def storerecords(self):
        try:
            csv_buffer = StringIO()
            self.df.to_csv(csv_buffer,index=False)
            self._s3.Object(self.bucket, f'kinesiscsv/{self.filename}').put(Body=csv_buffer.getvalue())
        except Exception as error:
            print(error)
