.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0
.. _architecture:

Architecture
============


Capabilities
------------
Message Router is a RESTful web service used for any needed action with Kaka.

Usage Scenarios
---------------
Message Router endpoints are used to create/view/delete a topic in Kafka. Clients can use the Message Router endpoints to publish a message to a topic and subscribe to a topic 

Interactions
------------
Message Service REST service uses the message service API to interact with the ZooKeeper/Kafka

   |image0|
   
   .. |image0| image:: architecture.png
