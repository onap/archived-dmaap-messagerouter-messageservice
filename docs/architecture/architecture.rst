.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Architecture
============


Capabilities
------------
Message Router is a RESTful web service used for any needed action with Kafka.

Usage Scenarios
---------------
Message Router REST service endpoints are used to create/view/delete a topic in Kafka. Clients can use the Message Router REST API to publish a message to a topic and subscribe to a topic 

Interactions
------------
Message Router REST service uses the Kafka API to interact with the Kafka

   |image0|
   
   .. |image0| image:: architecture.png
