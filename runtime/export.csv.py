from pymongo import MongoClient
import json
import os
import logging
import datetime
import bson
import pandas as pd
from collections import OrderedDict

# setup logging
log = logging.getLogger(__name__)
log.setLevel(logging.INFO)
file_handler = logging.FileHandler('logs/export.links.log')
file_handler.setLevel(logging.DEBUG)
file_handler_formatter = logging.Formatter('%(asctime)s %(levelname)-8s %(message)s')
file_handler.setFormatter(file_handler_formatter)
stream_handler = logging.StreamHandler()
stream_handler.setLevel(logging.INFO)
stream_handler_formatter = logging.Formatter('%(asctime)s %(message)s')
stream_handler.setFormatter(stream_handler_formatter)
log.addHandler(stream_handler)
log.addHandler(file_handler)

EXPORT_PATH = "dump"



client = MongoClient("mongodb://localhost:27017")
db = client["test"]

cursor = db.news.find()
print("Dumping news...")
df = pd.DataFrame(list(cursor))
df.to_csv("dump/news.csv")
print("Dumped: %s" % len(df))

cursor = db.link.find()
print("Dumping links...")
df = pd.DataFrame(list(cursor))
df.to_csv("dump/link.csv")
print("Dumped: %s" % len(df))
