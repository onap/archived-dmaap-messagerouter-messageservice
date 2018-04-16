.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Architecture
============

.. note::
   * This section is used to describe a software component from a high level
     view of capability, common usage scenarios, and interactions with other
     components required in the usage scenarios.  
   
   * The architecture section is typically: provided in a platform-component
     and sdk collections; and referenced from developer and user guides.
   
   * This note must be removed after content has been added.


Capabilities
------------
Message Router is a RESTful web service used for any needed action with Kaka

Usage Scenarios
---------------
Message Router endpoints are used to create/view/delete a topic in Kafka. Clients can use the Message Router endpoints to publish a message to a topic and subscribe to a topic 

Interactions
------------
Message Service REST service uses the message service API to interact with the ZooKeeper/Kafka