from pymongo import MongoClient
import json
import os
import logging
import datetime
import bson
from collections import OrderedDict

# setup logging
log = logging.getLogger(__name__)
log.setLevel(logging.INFO)
file_handler = logging.FileHandler('logs/export.news.log')
file_handler.setLevel(logging.DEBUG)
file_handler_formatter = logging.Formatter('%(asctime)s %(levelname)-8s %(message)s')
file_handler.setFormatter(file_handler_formatter)
stream_handler = logging.StreamHandler()
stream_handler.setLevel(logging.INFO)
stream_handler_formatter = logging.Formatter('%(asctime)s %(message)s')
stream_handler.setFormatter(stream_handler_formatter)
log.addHandler(stream_handler)
log.addHandler(file_handler)

EXPORT_PATH = "dump/news"


def json_handler(x):
    if isinstance(x, datetime.datetime):
        return x.isoformat()
    elif isinstance(x, bson.objectid.ObjectId):
        return str(x)
    else:
        raise TypeError(x)


def write_to_file(news):
    dir_path = os.path.join(EXPORT_PATH, news["host"])
    if not os.path.exists(dir_path):
        os.makedirs(dir_path, exist_ok=True)
    file_path = os.path.join(dir_path, news["id"] + ".json")
    log.info("Processing : %s -> %s" % (file_path, news["url"]))
    with open(file_path, 'w') as out_file:
        json.dump(news, out_file, default=json_handler, indent=4, ensure_ascii=False)


client = MongoClient("mongodb://localhost:27017")
db = client["test"]
cursor = db.news.find()
for news in cursor:
    obj = OrderedDict()
    obj["id"] = news["_id"]
    obj["host"] = news["host"]
    obj["url"] = news["url"]
    obj["title"] = news["title"]
    obj["date"] = news.get("date")
    obj["modified"] = news["modified"]
    obj["categories"] = news["categories"]
    obj["content"] = news["content"]
    obj["images"] = news.get("images")

    if len(obj["title"]) > 0:
        obj["title"] = obj["title"].strip()
    if len(obj["content"]) > 0:
        obj["content"] = obj["content"].strip()

    if len(obj["title"]) < 1 or len(obj["content"]) < 1:
        continue;
    write_to_file(obj)
