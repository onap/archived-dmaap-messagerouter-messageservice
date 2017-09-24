============================================
Message Router (MR) API Guide
============================================

HTTP Service APIs:
==================

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

• The Username:Password utilizes HTTP Basic Authentication and HTTPS/TLS
to securely transmit the authorization and authentication credentials
that AAF needs to validate the client's access to the requested
resource.

• The serverBaseURL points to DMaaP Message Router host/port that will
service the request. Optionally DME2 service end points for Message
Router can be used.

• The resourcePath specifies the specific service, or Topic, that the
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
|						  |	order is guaranteed. This format is mainly for test, as messages are highly likely to be re-ordered when       |
|						  | delivered through the Kafka cluster.                                                                           |
+-------------------------+----------------------------------------------------------------------------------------------------------------+
| application/json        | The payload maybe a single JSON object or a JSON array of JSON objects. Each object is handled as an individual|
|						  | message..Note that use of this format may result in equivalent but altered JSON objects sent to consumers.     | 
|						  | That's because MR uses a standard JSON parser to read each object into memory before pushing the object to the |
|						  | Kafka system. At that point, the JSON object is re-written from the in-memory object. This can result in       |
|						  | re-ordered fields or changes in whitespace. If you want to preseve JSON objects exactly,                       |
|						  | use application/cambria. Recommended to follow the JSON format after validating the                            |
|						  | message in https://jsonformatter.curiousconcept.com/      													   |
+-------------------------+----------------------------------------------------------------------------------------------------------------+




DME2 Service endpoints:
=======================

Message Router supports DME2 clients. That is , Client application may
use DME2Client and DME2 service address to call the MessageRouter
service.

Example DME2 service address:

Please use DMaap Message Router Run Book for complete details

TEST: http://hostname/events?version=XXX&envContext=XXX&partner=XX

PROD: https://hostname/events?version=XXX &envContext=XXX&partner=XX

The values of version/envContext/routerOffer may change based upon the
environment.

The specific details for each API are described as below



Publishers
==========

Description:
===========

Publishes data to Kafka server on the topic mentioned in the URL.
Messages will be in the request body

The MessageRouter service has no requirements on what publishers can put
onto a topic. The messages are opaque to the service and are treated as
raw bytes. In general, passing JSON messages is preferred, but this is
due to higher-level features and related systems, not the MessageRouter
broker itself. The only constraint placed on messages is their on their
size – messages must be under the maximum size, which is currently
configured at 1 MB.

Request URL:
===========

POST http(s)://{HOST:PORT}/events/{topicname}

Request Parameters
==================

+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+---------------------------------------------------+
| Name                     | Description                     | Param Type       | Data type  | Max Len   | Req’d       | Format                         |  Valid/Example Values                             |
+==========================+=================================+==================+============+===========+=============+================================+===================================================+
| Topicname                | topic name to be posted         | Path             | String     | 40        | Y           |  <app namespace>.<topicname>   | Org.onap.crm.empdetails                           |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+---------------------------------------------------+
| `content-type            | To specify type of message      | Header           | String     | 20        | N           |                                | application/json text/plain application/cambria   |
|                          |                                 |                  |            |           |             |                                |                                                   |
|                          | content(json,text or cambria)   |                  |            |           |             |                                |                                                   |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+---------------------------------------------------+
| Username                 | userid                          | Header           | String     |           | N           | Basic Authentication Header    |                                                   |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+---------------------------------------------------+
| Password                 |                                 | Header           | String     |           | N           | Basic Authentication Header    |                                                   |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+---------------------------------------------------+
| partitionKey             |                                 |  QueryParam      | String     |           | N           | String value                   |  ?partitionKey=123                                |
+--------------------------+---------------------------------+------------------+------------+-----------+-------------+--------------------------------+---------------------------------------------------+

 

 

Note:
-----

Publishers/user should have access on the topics. The user (id) and
permissions details needs to be in AAF.

Response Parameters:
====================

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

 

Response /Error Codes:
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

+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| Error code             |   HTTPCode     |  Description                    | Issue Reason                                                                                            |
+========================+================+=================================+=========================================================================================================+
| DMaaP\_MR\_ERR\_3001   | 413            | Request Entity too large        | Message size exceeds the batch limit <limit>.Reduce the batch size and try again                        |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3002   | 500            | Internal Server Error           | Unable to publish messages. Please contact administrator                                                |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3003   | 400            | Bad Request                     | Incorrect Batching format. Please correct the batching format and try again                             |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3004   | 413            | Request Entity too large        | Message size exceeds the message size limit <limit>.Reduce the message size and try again               |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3005   | 400            | Bad Request                     | Incorrect JSON object. Please correct the JSON format and try again                                     |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3006   | 504            | Network Connect Timeout Error   | Connection to the DMaaP MR was timed out.Please try again                                               |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3007   | 500            | Internal Server Error           | Failed to publish  messages to topic <topicName>. Successfully published <count > number of messages.   |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+
|                        |  503           |  Service Unavailable            |  Service Unavailable                                                                                    |
+------------------------+----------------+---------------------------------+---------------------------------------------------------------------------------------------------------+

 

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

 

Subscribers:
===========

Description:

 To subscribe to a MessageRouter topic, a subscriber issues a GET to the RESTful HTTP endpoint for events.



Request URL:
============

GET http(s)://{HOST:PORT}}/events/{topicname}/{consumegroup}/{consumerid}?{timeout=x}

Request Parameters:
==================

+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
| Name        | Description                     |  Param Type      |  data type |   MaxLen     |  Req’d      |  Format     |  Valid/Example Values                           |
| 			  | 								|				   |            |              |             |             | 												 |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
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
|			  |                                 |                  |            |              |             |             |                                                 |         
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
|Username     |   userid                        | Header           | String     | 1            | N           |             |                                                 |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+
| Password    |                                 | Header           | String     | 1            | N           |             |                                                 |
+-------------+---------------------------------+------------------+------------+--------------+-------------+-------------+-------------------------------------------------+       

Note1: 
======
Subscribers /user should have access on the topics. The user () and
permissions details needs to be in AAF.

Note 2:
=======


A few  consumer client app team who does continuous polling (by
multiple rest calls) complained that they receive "503 consumer lock
exception". The messages will not be lost from the topic when this
exception occurs and it will be still in the topic. This happens when
the concurrent rest call requests are made between several nodes in a
cluster. Consumer lock is done at zookeeper level to ensure the offset
for each consumer group is maintained to avoid losing the message in
multiple concurrent calls of same consumer Group. We are looking
otherways to resolve this issue. But for these continuous polling
scenario, the suggested workaround is to ensure the request is made to
only one node of the cluster. Session stickiness in DME2 is example of
handling this. If this exception occurs , waiting for few minutes with
out making api calls will release the lock in zookeeper.**

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
+-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3008    | 413             | Request Entity too large   | Message size exceeds the batch limit <limit>.Reduce the batch size and try again                   | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3009    | 500             | Internal Server Error      | Unable to publish messages. Please contact administartor                                           | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3010    | 400             | Bad Request                | Incorrect Batching format. Please correct the batching format and try again                        | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
| DMaaP\_MR\_ERR\_3011    | 413             | Request Entity too large   | Message size exceeds the message size limit <limit>.Reduce the message size and try again          | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
|  DMaaP\_MR\_ERR\_5012   | 429             | Too many requests          |  This client is making too many requests. Please use a long poll setting to decrease the number of |     |                         |                 |                            | requests that result in empty responses.                                                           |
+-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+
|                         |  503            |  Service Unavailable       |  Service Unavailable                                                                               | +-------------------------+-----------------+----------------------------+----------------------------------------------------------------------------------------------------+





