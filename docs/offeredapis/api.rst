.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Offered APIs
~~~~~~~~~~~~

.. toctree::
    :maxdepth: 3

DMaaP Message Router utilizes an HTTP REST API to service all Publish
and Consume transactions. HTTP and REST standards are followed so
clients as varied as cURL, Java applications and even Web Browsers will
work to interact with Message Router. Message Router uses AAF for user's
authentication and authorization.

General HTTP Requirements
-------------------------

A DMaaP Message Router transaction consists of 4 distinct segments:
HTTP URL, HTTP Header, HTTP Body (POST) and HTTP Response. The general
considerations for each segment are as follows and are required for each
of the specific transactions described in this section.

HTTP URL
--------

http[s]://serverBaseURL{/routing}{resourcePath}

- The serverBaseURL points to DMaaP Message Router host/port that will service the request.

- The resourcePath specifies the specific service, or Topic, that the client is attempting to reach

HTTP Header
-----------

Specifies HTTP Headers, such as Content-Type, that define the parameters
of the HTTP Transaction

HTTP Body
---------

The HTTP Body contains the topic content when Publishing or Consuming.
The Body may contain topic messages in several formats (like below) but
it must be noted, that, except in very specific circumstances, messages
are not inspected for content.


+-------------------------+-----------------------------------------------------------------------------------------------------------------+
| Content-Type            |  Description                                                                                                    |
+=========================+=================================================================================================================+
| text/plain              | Each line in the POST body is treated as a separate message. No partition key is specified, and therefore no    |
|                         | order is guaranteed. This format is mainly for test, as messages are highly likely to be re-ordered when        |
|                         | delivered through the Kafka cluster.                                                                            |
+-------------------------+-----------------------------------------------------------------------------------------------------------------+
| application/json        | The payload maybe a single JSON object or a JSON array of JSON objects. Each object is handled as an individual |
|                         | message. Note that use of this format may result in equivalent but altered JSON objects sent to consumers.      |
|                         | That's because MR uses a standard JSON parser to read each object into memory before pushing the object to the  |
|                         | Kafka system. At that point, the JSON object is re-written from the in-memory object. This can result in        |
|                         | re-ordered fields or changes in whitespace. If you want to preserve JSON objects exactly,                       |
|                         | use application/cambria. Following the JSON format is recommended.                                              |
+-------------------------+-----------------------------------------------------------------------------------------------------------------+

Publishers
-----------

**Description**: Publishes data to Kafka server on the topic mentioned in the URL.
Messages will be in the request body

The MessageRouter service has no requirements on what publishers can put
onto a topic. The messages are opaque to the service and are treated as
raw bytes. In general, passing JSON messages is preferred, but this is
due to higher-level features and related systems, not the MessageRouter
broker itself. 

Request URL
===========

POST http(s)://{HOST:PORT}/events/{topicname}

Request Parameters
==================

+--------------------+------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| Name               | Description                  | Param Type       | Data type  | Max Len   | Required    | Format                         | Valid/EXample values        |
+====================+==============================+==================+============+===========+=============+================================+=============================+
| Topicname          | topic name to be posted      | Path             | String     | 40        | Y           |                                | org.onap.dmaap.mr.testtopic |
+--------------------+------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| content-type       | To specify type of message   | Header           | String     | 20        | N           |                                | application/json            |
+--------------------+------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| Username           | userid                       | Header           | String     |           | N           | Basic Authentication Header    |                             |
+--------------------+------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| Password           |                              | Header           | String     |           | N           | Basic Authentication Header    |                             |
+--------------------+------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| partitionKey       |                              | QueryParam       | String     |           | N           | String value                   | ?Partitionkey=123           |
+--------------------+------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+


**NOTE**: To publish data to the authenticated topic, Publisher must
have the AAF permission org.onap.dmaap.mr.topic|:topic.<topic name>|pub. 
Publishers may use DMaaP BusController for provisoning the AAF permissions


Response Parameters
===================

+------------------+------------------------+------------+--------------+------------------------------+
| Name             | Description            | Type       | Format       | Valid/Example Values         |
+==================+========================+============+==============+==============================+
| httpStatusCode   |                        |            |              | 200, 201 etc.                |
+------------------+------------------------+------------+--------------+------------------------------+
| mrErrorCode      | Numeric error code     |            |              | 200, 201 etc.                |
+------------------+------------------------+------------+--------------+------------------------------+
| errorMessage     |                        |            |              | SUCCESS, or error message.   |
+------------------+------------------------+------------+--------------+------------------------------+
| helpURL          | helpurl                |            |              |                              |
+------------------+------------------------+------------+--------------+------------------------------+
| transactionid    | transaction-id value   |            |              |                              |
+------------------+------------------------+------------+--------------+------------------------------+



Response /Error Codes
=====================

+---------------------------+------------------------------------+
| Response statusCode       |  Response statusMessage            |
+===========================+====================================+
| 200-299                   | Success                            |
+---------------------------+------------------------------------+
| 400-499                   | the client request has a problem   |
+---------------------------+------------------------------------+
| 500-599                   | the DMaaP service has a problem    |
+---------------------------+------------------------------------+

+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| Error code             |  HTTPCode     |  Description                    | Issue Reason                                                                                            |
+========================+===============+=================================+=========================================================================================================+
| DMaaP\_MR\_ERR\_3001   | 413           | Request Entity too large        | Message size exceeds the batch limit <limit>. Reduce the batch size and try again                       |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3002   | 500           | Internal Server Error           | Unable to publish messages. Please contact administrator                                                |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3003   | 400           | Bad Request                     | Incorrect Batching format. Please correct the batching format and try again                             |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3004   | 413           | Request Entity too large        | Message size exceeds the message size limit <limit>. Reduce the message size and try again              |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3005   | 400           | Bad Request                     | Incorrect JSON object. Please correct the JSON format and try again                                     |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3006   | 504           | Network Connect Timeout Error   | Connection to the DMaaP MR was timed out. Please try again                                              |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3007   | 500           | Internal Server Error           | Failed to publish  messages to topic <topicName>. Successfully published <count > number of messages.   |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+

Sample Request:
===============

+-----------------------------------------------------------------------------------+
| POST                                                                              |
|                                                                                   |
| *Payload-* *{"message":"message description"}* *Content-Type: application/json*   |
|                                                                                   |
| Example:                                                                          |
|                                                                                   |
| curl -u XXXX@abc.com:X -H 'Content-Type:text/plain' -X POST -d @sampleMsg.txt     |
|                                                                                   |
| {    "count": 1,                                                                  |
|                                                                                   |
|     "serverTimeMs": 19"                                                           |
|                                                                                   |
| }                                                                                 |
+-----------------------------------------------------------------------------------+

Sample Response:
================

+---------------------------------------------------------------------+
| HTTP/1.1 200 OK                                                     |
|                                                                     |
|     Server: Apache-Coyote/1.1                                       |
|                                                                     |
|     transactionId: 28-12-2015::08:18:50:682::<IP>::28122015552391   |
|                                                                     |
|     Content-Type: application/json                                  |
|                                                                     |
|     Content-Length: 42                                              |
|                                                                     |
|     Date: Mon, 28 Dec 2015 13:18:50 GMT                             |
+---------------------------------------------------------------------+



Subscribers
-----------
**Description**: To subscribe to a MessageRouter topic, a subscriber issues a GET to the RESTful HTTP endpoint for events.

Request URL:
============

GET http(s)://{HOST:PORT}}/events/{topicname}/{consumergroup}/{consumerid}/{timeout=x}

Request Parameters:
===================

+---------------+---------------------------------+------------------+------------+--------------+-------------+---------------------+------------------------+
| Name          | Description                     |  Param Type      |  data type |   MaxLen     |  Required   |  Format             |  Valid/Example Values  |
+===============+=================================+==================+============+==============+=============+=====================+========================+
| topicname     | Topic name to be posted         |     Path         |   String   |    40        |     Y       |                     |                        |
+---------------+---------------------------------+------------------+------------+--------------+-------------+---------------------+------------------------+
| consumergroup | A name that uniquely identifies |     Path         |   String   |              |     Y       |                     | CG1                    |
|               | your subscribers                |                  |            |              |             |                     |                        |
+---------------+---------------------------------+------------------+------------+--------------+-------------+---------------------+------------------------+
| consumerid    | Within your subscribers group,  |     Path         |   String   |              |     Y       |                     | C1                     |
|               | a name that uniquely identifies |                  |            |              |             |                     |                        |
|               | your subscribers process        |                  |            |              |             |                     |                        |
+---------------+---------------------------------+------------------+------------+--------------+-------------+---------------------+------------------------+
| content-type  | To specify type of message      |     Header       |   String   |     20       |     N       |                     | application/json       |
|               | content(json, text or cambria)  |                  |            |              |             |                     |                        |
+---------------+---------------------------------+------------------+------------+--------------+-------------+---------------------+------------------------+
| Username      | userid                          |     Header       |   String   |     1        |     N       |                     |                        |
+---------------+---------------------------------+------------------+------------+--------------+-------------+---------------------+------------------------+
| Password      |                                 |     Header       |   String   |     1        |     N       |                     |                        |
+---------------+---------------------------------+------------------+------------+--------------+-------------+---------------------+------------------------+

**NOTE**: To read data from an authenticated topic, User must have the 
AAF permission org.onap.dmaap.mr.topic|:topic.<topic name>|sub. 
Subscribers may use DMaaP BusController for provisioning the permissions in AAF

Response Parameters:
====================

+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| Name             | Description                    |  Type      | Format       | Valid/Example Values                                      |
+==================+================================+============+==============+===========================================================+
| httpStatusCode   |                                |            |              | 200, 201 etc.                                             |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| mrErrorCode      | Numeric error code             |            |              | 200, 201 etc.                                             |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| errorMessage     |                                |            |              | SUCCESS, or error message.                                |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| helpURL          | helpurl                        |            |              |                                                           |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| transactionid    | transaction-id value           |            |              | 28-12-2015::08:18:50:682::<IP>::28122015552391            |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| ResponseBody     | Messages consumed from topic   | Json       | Json         |                                                           |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+


Response /Error Codes
=====================


+---------------------------+------------------------------------+
| Response statusCode       | Response statusMessage             |
+===========================+====================================+
| 200-299                   | Success                            |
+---------------------------+------------------------------------+
| 400-499                   | the client request has a problem   |
+---------------------------+------------------------------------+
| 500-599                   | the DMaaP service has a problem    |
+---------------------------+------------------------------------+

+-------------------------+-----------------+----------------------------+---------------------------------------------------------------------------------------------+
| Error code              |  HTTPCode       |  Description               | Issue Reason                                                                                |
+=========================+=================+============================+=============================================================================================+
| DMaaP\_MR\_ERR\_3008    | 413             | Request Entity too large   | Message size exceeds the batch limit <limit>.Reduce the batch size and try again            |
+-------------------------+-----------------+----------------------------+---------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3009    | 500             | Internal Server Error      | Unable to publish messages. Please contact administrator                                    |
+-------------------------+-----------------+----------------------------+---------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3010    | 400             | Bad Request                | Incorrect Batching format. Please correct the batching format and try again                 |
+-------------------------+-----------------+----------------------------+---------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3011    | 413             | Request Entity too large   | Message size exceeds the message size limit <limit>.Reduce the message size and try again   |
+-------------------------+-----------------+----------------------------+---------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_5012    | 429             | Too many requests          | This client is making too many requests. Please use a long poll setting to decrease the     |
|                         |                 |                            | number of requests that result in empty responses.                                          |
+-------------------------+-----------------+----------------------------+---------------------------------------------------------------------------------------------+
|                         | 503             | Service Unavailable        | Service Unavailable.                                                                        |
+-------------------------+-----------------+----------------------------+---------------------------------------------------------------------------------------------+

Sample Request:
===============

+-----------------------------------------------------------------------------+
| GET http ://{hostname}:3904/events/org.onap.dmaap.mr.sprint/mygroup/mycus   |
|                                                                             |
| Content-Type: application/json                                              |
|                                                                             |
| Example:                                                                    |
|                                                                             |
|curl -u XXX@csp.abc.com:MRDmap2016$ -X GET -d 'MyfirstMessage'               |
|                                                                             |
|http ://10.12.7.22:3904/events/com.att.ecomp_test.crm.preDeo/myG/C1          |
|                                                                             |
|[I am r sending first msg,I am R sending first msg]                          |
+-----------------------------------------------------------------------------+

Provisioning
------------

**Description**: To create, modify or delete the MessageRouter topics. These APIs can also be used by other applications to provision topics in MessageRouter.
DMaaP BusController is recommended for topic and AAF permissions provisioning

Create Topic
============
Request URL:
============

POST http(s)://{HOST:PORT}/topics/create

Request Parameters:
===================

+-------------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+
| Name              | Description                     |  Param Type      |  datatype  |   MaxLen     |  Required   |  Format     |  Valid/Example Values             |
+===================+=================================+==================+============+==============+=============+=============+===================================+
| Topicname         | topicname to be created in MR   |     Body         |   String   |     20       |     Y       | Json        | org.onap.dmaap.mr.metrics         |
+-------------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+
| topicDescription  | description for topic           |     Body         |   String   |     15       |     Y       |             |                                   |
+-------------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+
| partitionCount    | Kafka topic partition           |     Body         |   String   |     1        |     Y       |             |                                   |
+-------------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+
| replicationCount  | Kafka topic replication         |     Body         |   String   |     1        |     Y       |             | 3 (Default -for 3 node Kafka )    |
+-------------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+
| transaction       | to create transaction id for    |     Body         |  Boolean   |     1        |     N       |             | true                              |
| Enabled           | each message transaction        |                  |            |              |             |             |                                   |
+-------------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+
| Content-Type      | application/json                |     Header       |   String   |              |             |             | application/json                  |
+-------------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+

**NOTE**: To Create an authenticated topic, user must have the AAF permission
 org.onap.dmaap.mr.topicFactory|:org.onap.dmaap.mr.topic:org.onap.dmaap.mr|create 


+---------------------------+------------------------------------+
| Response statusCode       | Response statusMessage             |
+===========================+====================================+
| 200-299                   | Success                            |
+---------------------------+------------------------------------+
| 400-499                   | the client request has a problem   |
+---------------------------+------------------------------------+
| 500-599                   | the DMaaP service has a problem    |
+---------------------------+------------------------------------+


+-------------------------+-----------------+--------------------------------------------------+
| Error code              |  HTTP Code      |  Description                                     |
+=========================+=================+==================================================+
| DMaaP\_MR\_ERR\_5001    | 500             | Failed to retrieve list of all topics            |
+-------------------------+-----------------+--------------------------------------------------+
| DMaaP\_MR\_ERR\_5002    | 500             | Failed to retrieve details of topic:<topicName>  |
+-------------------------+-----------------+--------------------------------------------------+
| DMaaP\_MR\_ERR\_5003    | 500             | Failed to create topic:<topicName>               |
+-------------------------+-----------------+--------------------------------------------------+
| DMaaP\_MR\_ERR\_5004    | 500             | Failed to delete topic:<topicName>               |
+-------------------------+-----------------+--------------------------------------------------+


Response Parameters
====================

+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| Name             | Description                    |  Type      | Format       | Valid/Example Values                                      |
+==================+================================+============+==============+===========================================================+
| httpStatusCode   |                                |            |              | 200, 201 etc.                                             |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| mrErrorCode      | Numeric error code             |            |              | 5005                                                      |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| errorMessage     |                                |            |              | SUCCESS, or error message.                                |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| helpURL          | helpurl                        |            |              |                                                           |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| ResponseBody     | Topic details (owner,          |  Json      |  Json        |                                                           |
|                  | trxEnabled=true)               |            |              |                                                           |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+


Sample Request:
===============

 .. code:: bash

    POST   http: //<hostname>:3904/topics/create
    Request Body
    {"topicName":"org.onap.dmaap.mr.testtopic","description":"This is a test Topic ",
    "partitionCount":"1","replicationCount":"3","transactionEnabled":"true"}
    Content-Type: application/json
    Example:
    curl -u XXXc@csp.abc.com:xxxxx$  -H 'Content-Type:application/json' -X POST -d
    @topicname.txt  http: //message-router:3904/topics/create
    {
    "writerAcl": {
    "enabled": false,
    "users": []
    },
    "description": "This is a TestTopic",
    "name": "org.onap.dmaap.mr.testtopic",
    "readerAcl": {
    "enabled": false,
    "users": []


GetTopic Details
----------------

Request URL
===========

GET http(s)://{HOST:PORT}/topics    : To list the details of all the topics in Message Router.

GET http(s)://{HOST:PORT}/topics/{topicname} : To list the details of a specified topic.

Request Parameters
==================

+--------------------------+-------------------------+------------------+------------+-----------+-------------+-----------------+-----------------------------+
| Name                     | Description             | Param Type       | Data type  | Max Len   | Required    | Format          | Valid/EXample values        |
+==========================+=========================+==================+============+===========+=============+=================+=============================+
| topicname                | topic name details      | Body             | String     | 20        | Y           |  Json           | org.onap.dmaap.mr.testtopic |
+--------------------------+-------------------------+------------------+------------+-----------+-------------+-----------------+-----------------------------+

**NOTE**: To view an authenticated topic, user must have the AAF permission
 org.onap.dmaap.mr.topic|*|view 


Response Parameters
====================

+------------------+------------------------+------------+----------+---------+----------------------------+
| Name             | Description            | ParamType  | datatype |Format   | Valid/Example Values       |
+==================+========================+============+==========+=========+============================+
| topicname        |  topic name details    |      Body  |   String |   Json  | org.onap.dmaap.mr.testopic |
+------------------+------------------------+------------+----------+---------+----------------------------+
| description      |                        |            |   String |         |                            |
+------------------+------------------------+------------+----------+---------+----------------------------+
| owner            |user id who created the |            |          |         |                            |
|                  |         topic          |            |          |         |                            |
+------------------+------------------------+------------+----------+---------+----------------------------+
| txenabled        |     true or false      |            |  boolean |         |                            |
+------------------+------------------------+------------+----------+---------+----------------------------+

+---------------------------+------------------------------------+
| Response statusCode       | Response statusMessage             |
+===========================+====================================+
| 200-299                   | Success                            |
+---------------------------+------------------------------------+
| 400-499                   | the client request has a problem   |
+---------------------------+------------------------------------+
| 500-599                   | the DMaaP service has a problem    |
+---------------------------+------------------------------------+


Sample Request:
===============

+-----------------------------------------------------------------------------------------------------------------------------------+
| GET   http ://<hostname>:3904/topic/org.onap.dmaap.mr.testtopic                                                                   |
|       curl -u XXX@csp.abc.com:x$  -X                                                                                              |
| GET  http ://10.12.7.22:3904/topics                                                                                               |
|    {"topics": [                                                                                                                   |
|    {"txenabled": true,"description": "This is a TestTopic","owner": "XXXX@csp.abc.com","topicName": "org.onap.dmaap.mr.Load9"     |
|    {"txenabled": false,"description": "", "owner": "", "topicName": "org.onap.dmaap.mr.Load1"                                     |
|    ]},                                                                                                                            |
+-----------------------------------------------------------------------------------------------------------------------------------+


Delete Topics
-------------

Request URL:
============

DELETE http(s)://{HOST:PORT}/topics/{topicname}

**NOTE**: To delete a topic, user must have the AAF permission
org.onap.dmaap.mr.topicFactory|:org.onap.dmaap.mr.topic:org.onap.dmaap.mr|destroy 

Sample Request:
===============
ex: http ://<hostname>:3904/topics/org.onap.dmaap.mr.testopic

+---------------------------+------------------------------------+
| Response statusCode       | Response statusMessage             |
+===========================+====================================+
| 200-299                   | Success                            |
+---------------------------+------------------------------------+
| 400-499                   | the client request has a problem   |
+---------------------------+------------------------------------+
| 500-599                   | the DMaaP service has a problem    |
+---------------------------+------------------------------------+

+-------------------------+---------------------------------------------+----------------------+
| Error code              |    Description                              |HTTP code             |
+=========================+=============================================+======================+
|  DMaaP\_MR\_ERR\_5004   |  Failed to delete topic:<topicName>         |   500                |
+-------------------------+---------------------------------------------+----------------------+

API Inventory
-------------

+-----------+--------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           |   API Name         |   API Method                            |   REST API Path                       |                | Comments                         |
+===========+====================+=========================================+=======================================+================+==================================+
| Topics    | GetAll Topics      | getTopics()                             | /topics                               | GET            |                                  |
|           | List               |                                         |                                       |                |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Get All Topics     |                                         |                                       |                |                                  |
|           | List with details  | getAllTopics()                          | /topics/listAll                       | GET            |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Get individual     |                                         |                                       |                |                                  |
|           | Topic Details      | getTopic(String topicName)              | /topics/{topicName}                   | GET            |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Create Topic       | createTopic(TopicBean topicBean)        | /topics/create                        | POST           |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Delete Topic       | deleteTopic(String topicName)           | /topics/{topicName}                   | DELETE         |  Not used in current MR version  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Get Publishers     | getPublishersByTopicName                |                                       |                |                                  |
|           | for a Topic        | (String topicName)                      | /topics/{topicName}/producers         | GET            |  UEB Backward Compatibility      |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Add a Publisher    | permitPublisherForTopic                 | /topics/{topicName}/producers/        | PUT            |                                  |
|           | to write ACL on    | (String topicName, String producerId)   | {producerId}                          |                |                                  |
|           | a Topic            |                                         |                                       |                |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Remove a Publisher | denyPublisherForTopic(String topicName, | /topics/{topicName}/producers/        | DELETE         |                                  |
|           | from write ACL on  | String producerId)                      | {producerId}                          |                |                                  |
|           | a Topic            |                                         |                                       |                |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Get Consumers for  | getConsumersByTopicName                 | /topics/{topicName}/consumers         |  GET           |                                  |
|           | a Topic            | (String topicName)                      |                                       |                |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Add a Consumer     | permitConsumerForTopic(String           | /topics/{topicName}/consumers/        |  PUT           |                                  |
|           | to read ACL        | topicName, String consumerId)           | {consumerId}                          |                |                                  |
|           | on a Topic         |                                         |                                       |                |                                  |
|           +--------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Remove a consumer  | denyPublisherForTopic(String topicName, | /topics/{topicName}/consumers/        | DELETE         |                                  |
|           | from write         | String consumerId)                      | {consumerId}                          |                |                                  |
|           | ACL on a Topic     |                                         |                                       |                |                                  |
+-----------+--------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
