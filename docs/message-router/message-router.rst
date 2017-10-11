============================================
Message Router (MR) API Guide
============================================
Architecture
-------------

In DMaaP Message Router, Restful web service is exposed to client to perform any needed action with Kafka. After getting the request it calls the Message router service layer which is created using AJSC ( AT&T Java Service Container) . AJSC finally calls Kafka services and response is sent back.

   |image0|

   .. |image0| image:: image1.png
   

HTTP Service APIs
------------------

DMaaP Message Router utilizes an HTTP REST API to service all Publish
and Consume transactions. HTTP and REST standards are followed so
clients as varied as CURL, Java applications and even Web Browsers will
work to interact with Message Router.Message Router uses AAF for user's
authentication and authorization.

General HTTP Requirements
=========================

A DMaaP Message Router transactions consists of 4 distinct segments,
HTTP URL, HTTP Header, HTTP Body (POST) and HTTP Response. The general
considerations for each segment are as follows and are required for each
of the specific transactions described in this section.

HTTP URL
========

http[s]://Username:Password@serverBaseURL{/routing}{resourcePath}

- The Username:Password utilizes HTTP Basic Authentication and HTTPS/TLS
to securely transmit the authorization and authentication credentials
that AAF needs to validate the client's access to the requested
resource.
- The serverBaseURL points to DMaaP Message Router host/port that will
service the request. Optionally DME2 service end points for Message
Router can be used.
- The resourcePath specifies the specific service, or Topic, that the
client is attempting to reach

HTTP Header
===========

Specifies HTTP Headers, such as Content-Type, that define the parameters
of the HTTP Transaction

HTTP Body
=========

The HTTP Body contains the topic content when Publishing or Consuming.
The Body may contain topic messages in several formats (like below) but
it must be noted, that, except in very specific circumstances, messages
are not inspected for content.


+-------------------------+----------------------------------------------------------------------------------------------------------------+
| Content-Type            |  Description                                                                                                   |
+=========================+================================================================================================================+
| text/plain              | Each line in the POST body is treated as a separate message. No partition key is specified, and therefore no   |
|		                  |	order is guaranteed. This format is mainly for test, as messages are highly likely to be re-ordered when       |
|	                      | delivered through the Kafka cluster.                                                                           |
+-------------------------+----------------------------------------------------------------------------------------------------------------+
| application/json        | The payload maybe a single JSON object or a JSON array of JSON objects. Each object is handled as an individual|
|						  | message..Note that use of this format may result in equivalent but altered JSON objects sent to consumers.     | 
|						  | That's because MR uses a standard JSON parser to read each object into memory before pushing the object to the |
|						  | Kafka system. At that point, the JSON object is re-written from the in-memory object. This can result in       |
|						  | re-ordered fields or changes in whitespace. If you want to preseve JSON objects exactly,                       |
|						  | use application/cambria. Recommended to follow the JSON format after validating the                            |
|						  | message in https://jsonformatter.curiousconcept.com/      													   |
+-------------------------+----------------------------------------------------------------------------------------------------------------+

Publishers
-----------

**Description**:Publishes data to Kafka server on the topic mentioned in the URL.
Messages will be in the request body

The MessageRouter service has no requirements on what publishers can put
onto a topic. The messages are opaque to the service and are treated as
raw bytes. In general, passing JSON messages is preferred, but this is
due to higher-level features and related systems, not the MessageRouter
broker itself. The only constraint placed on messages is their on their
size – messages must be under the maximum size, which is currently
configured at 1 MB.

Request URL
===========

POST http(s)://{HOST:PORT}/events/{topicname}

Request Parameters
==================

+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| Name                     | Description                     | Param Type       | Data type  | Max Len   | Req’d       | Format                         | Valid/EXample values        |
+==========================+=================================+==================+============+===========+=============+================================+=============================+
| Topicname                | topic name to be posted         | Path             | String     | 40        | Y           |  <app namespace>.<topicname>   | org.onap.crm.empdetails     |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| content-type             | To specify type of message      | Header           | String     | 20        | N           |                                | application/json            |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+| Username                 | userid                          | Header           | String     |           | N           | Basic Authentication Header    |                             |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| Password                 | userid                          | Header           | String     |           | N           | Basic Authentication Header    |                             |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+
| partitionKey             |                                 |  QueryParam      | String     |           | N           | String value                   |?Partitionkey=123            |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+-----------------------------+

**NOTE **: Publishers/user should have access on the topics. The user (id) and
permissions details needs to be in AAF.

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
| DMaaP\_MR\_ERR\_3001   | 413           | Request Entity too large        | Message size exceeds the batch limit <limit>.Reduce the batch size and try again                        |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3002   | 500           | Internal Server Error           | Unable to publish messages. Please contact administrator                                                |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3003   | 400           | Bad Request                     | Incorrect Batching format. Please correct the batching format and try again                             |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3004   | 413           | Request Entity too large        | Message size exceeds the message size limit <limit>.Reduce the message size and try again               |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3005   | 400           | Bad Request                     | Incorrect JSON object. Please correct the JSON format and try again                                     |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3006   | 504           | Network Connect Timeout Error   | Connection to the DMaaP MR was timed out.Please try again                                               |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3007   | 500           | Internal Server Error           | Failed to publish  messages to topic <topicName>. Successfully published <count > number of messages.   |
+------------------------+---------------+---------------------------------+---------------------------------------------------------------------------------------------------------+

Sample Request:
==============

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
===============

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
**Description**:To subscribe to a MessageRouter topic, a subscriber issues a GET to the RESTful HTTP endpoint for events.

Request URL:
============

GET http(s)://{HOST:PORT}}/events/{topicname}/{consumegroup}/{consumerid}/{timeout=x}

Request Parameters:
==================

+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
| Name        | Description                     |  Param Type      |  data type |   MaxLen     |  Req’d      |  Format     |  Valid/Example Values                           |
+=============+=================================+==================+============+==============+=============+=============+=================================================+
| Topicname   | topic name to be posted         |     Path         |   String   |        40    |     Y       | namespace.  |												 |
|			  |									| 				   |            |              |             |  String     |                                                 |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+	       
|Consumergroup| A name that uniquely identifies |     Path         |    String  |              |             |             |                                                 |
|			  | your subscriber's               |                  |            |              |      Y      |             |               CG1                               |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
| consumerId  | Within your subscriber's group, |                  |            |              |             |             |                                                 |
|			  | a name that uniquely identifies |      Path        |   String   |              |       y     |             |              C1                                 |
|			  | your subscriber's  process      |                  |            |              |             |             |                                                 |         
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
| content-type| To specify type of message      |                  |            |              |             |             |aplication/json                                  |
|			  | content(json,text or cambria)   |      Header      |   String   |         20   |      N      |             |                                                 |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
|Username     |   userid                        | Header           | String     | 1            | N           |             |                                                 |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
| Password    |                                 | Header           | String     | 1            | N           |             |                                                 |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+       

**NOTE1**:Subscribers /user should have access on the topics. The user () and
permissions details needs to be in AAF.

Response Parameters:
===================

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
| tranactionid     | transaction-id value           |            |              | 28-12-2015::08:18:50:682::135.25.227.66::28122015552391   |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| ResponseBody     | Messages consumed from topic   | Json       | Json         |                                                           |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+

+---------------------------+------------------------------------+
| Response statusCode       | Response statusMessage             |
+===========================+====================================+
| 200-299                   | Success                            |
+---------------------------+------------------------------------+
| 400-499                   | the client request has a problem   |
+---------------------------+------------------------------------+
| 500-599                   | the DMaaP service has a problem    |
+---------------------------+------------------------------------+

+-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| Error code              |  HTTP Code      |  Description               |Issue reason                                                                                        |
+=========================+=================+============================+====================================================================================================+
| DMaaP\_MR\_ERR\_3008    | 413             | Request Entity too large   | Message size exceeds the batch limit <limit>.Reduce the batch size and try again                   | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3009    | 500             | Internal Server Error      | Unable to publish messages. Please contact administartor                                           | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3010    | 400             | Bad Request                | Incorrect Batching format. Please correct the batching format and try again                        | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3011    | 413             | Request Entity too large   | Message size exceeds the message size limit <limit>.Reduce the message size and try again          | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+


Sample Request:
==============

+----------------------------------------------------------------------------------------------------+
| GET  http://<hostname>:3904/events/com.att.dmaap.mr.sprint/mygroup/mycus                           |
|  Content-Type: application/json                                                                    |
| Example:                                                                                           |
|curl -u XXX@csp.abc.com:MRDmap2016$ -X GET -d 'MyfirstMessage'                                      | 
|http://mrlocal00.dcae.proto.research.att.com:3904/events/com.att.ecomp_test.crm.preDeo/myG/C1       |
|[I am r sending first msg,I am R sending first msg]                                                 |
+----------------------------------------------------------------------------------------------------+

Provisioning
------------
**Description**: To create , modify or delete the MessageRouter topics. Generally Invenio application will use these  below apis to create , assign topics to the users. These APIs can also be used by other applications to provision topics in MessageRouter

Create Topic
============
Request URL:
============

POST http(s)://{HOST:PORT}/topics/create

Request Parameters:
==================

+----------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+
| Name           | Description                     |  Param Type      |  data type |   MaxLen     |  Req’d      |  Format     |  Valid/Example Values             |
+================+=================================+==================+============+==============+=============+=============+===================================+
| Topicname      | topicname to be created in MR   |     Body         |   String   |        20    |     Y       | Json        |		com.att.dmaap.mr.metrics      |
+----------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+       
|topicDescription|   description for topic         |      Body        |   String   |     15       |     Y       |             |                                   |
+----------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+  
|partitionCount  |   Kafka topic partition         |     Body         |   String   |     1        | Y           |             |                                   |
+----------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+ 
|replicationCount|   Kafka topic replication       |     Body         |   String   |     1        | Y           |             |  3 (Default -for 3 node Kafka )   |
+----------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+ 
|transaction     |to create transaction id for     |     Body         | Boolean    |              |             |             |                                   |
|                |	each message transaction       |                  |            |      1       |        N    |             |   true                            |
| Enabled        |                                 |                  |            |              |             |             |                                   | 
+----------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+ 
|Content-Type    |   application/json              |     Header       |   String   |              |             |             |  application/json                 |
+----------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-----------------------------------+

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
| DMaaP\_MR\_ERR\_5001    | 500             | Failed to retrieve list of all topics            | +-------------------------+-----------------+--------------------------------------------------+
| DMaaP\_MR\_ERR\_5002    | 500             | Failed to retrieve details of topic:<topicName>  |     +-------------------------+-----------------+--------------------------------------------------+
| DMaaP\_MR\_ERR\_5003    | 500             |Failed to create topic:<topicName>                | +-------------------------+-----------------+--------------------------------------------------+
| DMaaP\_MR\_ERR\_5004    | 500             | Failed to delete topic:<topicName>               | +-------------------------+-----------------+--------------------------------------------------+

Response Parameters
====================

+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| Name             | Description                    |  Type      | Format       | Valid/Example Values                                      |
+==================+================================+============+==============+===========================================================+
| httpStatusCode   |                                |            |              | 200, 201 etc.                                             |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| mrErrorCode      | Numeric error code             |            |              | 5005                                                       |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| errorMessage     |                                |            |              | SUCCESS, or error message.                                |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| helpURL          | helpurl                        |            |              |                                                           |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+
| ResponseBody     | Topic details (owner,          |            |              |                                                           |
|                  | trxEnabled=true)               | Json       | Json         |                                                           |
+------------------+--------------------------------+------------+--------------+-----------------------------------------------------------+


Sample Request:
==============

+-----------------------------------------------------------------------------------+
| POST   http://<hostname>:3904/topic/create                                        |
|Request Body                                                                       |
|{"topicName":"com.abc.dmaap.mr.topicname","description":"This is a SAPTopic ",     |
| "partitionCount":"1","replicationCount":"3","transactionEnabled":"true"}          |
| Content-Type: application/json                                                    |
|Example:                                                                           |
|curl -u XXXc@csp.abc.com:xxxxx$  -H 'Content-Type:application/json' -X POST -d     |
|@topicname.txt  http://mrlocal00.dcae.proto.research.abc.com:3904/topics/create    |
|{                                                                                  |
|    "writerAcl": {                                                                 |
|        "enabled": false,                                                          |
|        "users": []                                                                |
|    },                                                                             |
|    "description": "This is a TestTopic",                                          |
|    "name": "com.abc.ecomp_test.crm.Load9",                                        |
|    "readerAcl": {                                                                 |
|        "enabled": false,                                                          |
|        "users": []                                                                |
+-----------------------------------------------------------------------------------+

GetTopic Details
----------------

Request URL
===========

GET http(s)://{HOST:PORT}/topics    : To list the details of all the topics in Message Router.

GET http(s)://{HOST:PORT}/topics/{topicname} : To list the details of specified topic .

Request Parameters
==================

+--------------------------+---------------------------------+------------------+------------+-----------+-------------+-----------------+-----------------------------+
| Name                     | Description                     | Param Type       | Data type  | Max Len   | Req’d       | Format          | Valid/EXample values        |
+==========================+=================================+==================+============+===========+=============+=================+=============================+
| Topicname                | topic name details              | Body             | String     | 20        | Y           |  Json           | com.abc.dmaap.mr.metrics    |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+-----------------+-----------------------------+


Response Parameters
====================

+------------------+------------------------+------------+----------+---------+--------------------------+
| Name             | Description            | ParamType  | datatype |Format   | Valid/Example Values     |
+==================+========================+============+==========+=========+==========================+
| topicname        |  topic name details    |      Body  |   String |   Json  | com.abc.dmaap.mr.metrics | 
+------------------+------------------------+------------+----------+---------+--------------------------+
| description      |                        |            |   String |         |                          | 
+------------------+------------------------+------------+----------+---------+--------------------------+
|owner             |user id who created the |            |          |         |                          |
|                  |         topic          |            |          |         |                          | 
+------------------+------------------------+------------+----------+---------+--------------------------+
| txenabled        |     true or false      |            |   boolean|         |                          | 
+------------------+------------------------+------------+----------+---------+--------------------------+

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
==============

+-----------------------------------------------------------------------------------+
| GET   http://<hostname>:3904/topic/com.att.dmaap.mr.testtopic                     |
|       curl -u XXX@csp.abc.com:x$  -X                                              |
| GET  http://mrlocal00.dcae.proto.research.att.com:3904/topics                     |
|    {"topics": [                                                                   |
|    {                                                                              |
|       "txenabled": true,                                                          |
|        "description": "This is a TestTopic",                                      |
|      "owner": "XXXX@csp.abc.com",                                               |
|        "topicName": "com.abc.ecomp_test.crm.Load9"                                |
|    },                                                                             |
|    {                                                                              |
|        "txenabled": false,                                                        |
|        "description": "",                                                         |
|        "owner": "",                                                               |
|        "topicName": "com.abc.ecomp_test.crm.Load1"                                |
|    },                                                                             |
+-----------------------------------------------------------------------------------+


Delete Topics
-------------

Request URL:
===========

DELETE http(s)://{HOST:PORT}/topic/{topicname}

Sample Request:
==============
ex: http://<hostname>:3904/dmaap/v1/topics/com.att.dmaap.mr.testopic

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

+-----------+-------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           |   API Name        |   API Method                            |   REST API Path                       |                | Comments                         |
+===========+===================+=========================================+=======================================+================+==================================+
| Topics    | GetAll Topics List|  getTopics()                            | /topics                               | GET            |                                  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Get All Topics    |                                         |                                       |                |                                  |
|           |List with details  | getAllTopics()                          | /topics/listAll                       | GET            |                                  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Get individual    |                                         |                                       |                |                                  |
|           | Topic Details     | getTopic(String topicName)              | /topics/{topicName}                   | GET            |                                  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Create Topic      | createTopic(TopicBean topicBean)        | /topics/create                        | POST           |                                  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Delete Topic      | deleteTopicString topicName)            | /topics/{topicName}                   | DELETE         |  Not used in current MR version  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
|           | Get Publishers for| getPublishersByTopicName                |                                       |                |                                  |
|           | a Topic           | (String topicName)                      | /topics/{topicName}/producers         | GET            |  UEB Backward Compatibility      |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Add a Publisher to|permitPublisherForTopic(String topicName,| /topics/{topicName}/producers/        |                |                                  |
|           |write ACLon a Topic|     String producerId)                  |    {producerId}                       | PUT            |                                  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Remove a Publisher|denyPublisherForTopic(String   topicName,|/topics/{topicName}/producers/         |                |                                  | 
|           |from write   ACL on|String producerId)                       |{producerId}                           |   DELETE       |                                  |
|           | a Topic           |                                         |                                       |                |                                  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           |Get Consumers for a| getConsumersByTopicName                 |  /topics/{topicName}/consumers        |       GET      |                                  |
|           |  Topic            | (String topicName)                      |                                       |                |                                  |
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Add a Consumer to | permitConsumerForTopic(String topicName,|   /topics/{topicName}/consumers/      |      PUT       |                                  |
|           |read ACL on a Topic|      String consumerId)                 |       {consumerId}                    |                |                                  |    
|           +-------------------+-----------------------------------------+---------------------------------------+----------------+                                  |
|           | Remove a consumer |denyPublisherForTopic(String   topicName,|/topics/{topicName}/consumers/         |                |                                  | 
|           |from write   ACL on|String consumerId)                       |{consumerId}                           |                |                                  |
|           | a Topic           |                                         |                                       |    DELETE      |                                  |
+-----------+-------------------+-----------------------------------------+---------------------------------------+----------------+----------------------------------+
