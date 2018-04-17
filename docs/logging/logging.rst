.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Logging
=======

.. note::
   * This section is used to describe the informational or diagnostic messages emitted from 
     a software component and the methods or collecting them.
   
   * This section is typically: provided for a platform-component and sdk; and
     referenced in developer and user guides
   
   * This note must be removed after content has been added.


Where to Access Information
---------------------------
Message Router uses logback framework to generate logs and all the logs are emitted to the console.

Error / Warning Messages
------------------------
Following are the error codes

| RESOURCE_NOT_FOUND=3001
| SERVER_UNAVAILABLE=3002
| METHOD_NOT_ALLOWED=3003
| GENERIC_INTERNAL_ERROR=1004
| MSG_SIZE_EXCEEDS_BATCH_LIMIT=5001
| UNABLE_TO_PUBLISH=5002
| INCORRECT_BATCHING_FORMAT=5003
| MSG_SIZE_EXCEEDS_MSG_LIMIT=5004
| INCORRECT_JSON=5005
| CONN_TIMEOUT=5006
| PARTIAL_PUBLISH_MSGS=5007
| CONSUME_MSG_ERROR=5008
| PUBLISH_MSG_ERROR=5009
| RETRIEVE_TRANSACTIONS=5010
| RETRIEVE_TRANSACTIONS_DETAILS=5011
| TOO_MANY_REQUESTS=5012
| RATE_LIMIT_EXCEED=301
| GET_TOPICS_FAIL=6001
| GET_TOPICS_DETAILS_FAIL=6002
| CREATE_TOPIC_FAIL=6003
| DELETE_TOPIC_FAIL=6004
| GET_PUBLISHERS_BY_TOPIC=6005
| GET_CONSUMERS_BY_TOPIC=6006
| GET_CONSUMER_CACHE=6011
| DROP_CONSUMER_CACHE=6012
| GET_METRICS_ERROR=6013
| TOPIC_NOT_IN_AAF=6017

