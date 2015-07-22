import json

from urllib.request import Request
from urllib.request import urlopen

es_host = "localhost"
es_port = "9200"
es_index = "conformance"

es_mapping_process_log_event = {
    "properties": {
        "processInstanceID": { "type": "string", "index": "not_analyzed" },
        "activityID": { "type": "integer" },
        "activityStatus": { "type": "integer" },
        "activityStartTime": { "type": "long" },
        "activityLastExecutionTime": { "type": "long" },
        "activityStatesSnapshot": {
            "type": "object",
            "properties": {
                "id": { "type": "integer", "index": "no" },
                "state": { "type": "integer", "index": "no" }
            }
        },
        "log": { "type": "string", "index": "analyzed" },
        "logTime": { "type": "long" },
        "tags": { "type": "string", "index": "not_analyzed" }
    }
}

es_index_data = {
    "mappings": {
        "logEvent": es_mapping_process_log_event
    }
}

put_mapping_response = urlopen(
    Request(
        "http://{host}:{port}/{index}".format(
            host=es_host, port=es_port, index=es_index),
        data=json.dumps(es_index_data).encode(),
        method="PUT"))

print(
    "Response for creating index {index}: "
    "headers={headers}, status={status}".
    format(
        index=es_index,
        headers=put_mapping_response.getheaders(),
        status=put_mapping_response.status))
