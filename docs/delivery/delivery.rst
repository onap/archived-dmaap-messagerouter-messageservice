.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Delivery
========

Message Router is comprised of Message Router service and Message Router API. Message Router uses Kafka and ZooKeeper

.. blockdiag::
   

   blockdiag layers {
   orientation = portrait
   MR_SERVIVE -> MR_API;
   MR_SERVIVE -> KAFKA;
   MR_SERVIVE -> ZOOKEEPER;
   group l1 {
	color = blue;
	label = "MR container";
	MR_SERVIVE; MR_API; 
	}
   group l2 {
	color = yellow;
	label = "Kafka Container";
	KAFKA; 
	}
   group l3 {
	color = orange;
	label = "ZooKeeper Container";
	ZOOKEEPER;
	}

   }

