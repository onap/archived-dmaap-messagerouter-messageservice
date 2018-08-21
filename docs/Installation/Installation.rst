.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0

Installation
============

Environment
-----------
Message Router is developed using Kafka, Zookeeper and Java. AJSC framework is used to create the REST service and Docker was used to package the service.

Steps
-----
Message Router has 3 docker containers. Dmaap\_container,
kafka\_contaienr and zookeeper\_container. Zookeeper runs on 172.18.0.2,
kafka runs on 172.18.0.3 and dmaap on 172.18.0.4.

1) Clone message service repo

   git clone http://gerrit.onap.org/r/dmaap/messagerouter/messageservice

2) copy
   messageservice/bundleconfig-local/etc/appprops/MsgRtrApi.properties
   to /var/tmp directory

3) In /var/tmp/MsgRtrApi.properties, change value of below variables as
   shown below:

   config.zk.servers=172.18.0.2(Change as per where  ZooKeepeer is deployed)

   kafka.metadata.broker.list=172.18.0.3:9092(Change as per where Kafka is deployed)

4) Install docker and docker-compose

5) Go to messageservice/src/main/resources/docker-compose and run:
   
   .. code:: bash
   
		docker-compose up  # add -d argument to start process as a daemon (background process)

   This should start 3 containers.

6) Run docker ps. It should show 3 containers.

   |image0|
   
   .. |image0| image:: docker.png
  
Testing
-------

-  For publishing, create a sample.txt file with some content in the
   directory where you will run below rest api. Run below rest api:

   curl -H "Content-Type:text/plain" -X POST -d @sample.txt
   http://172.18.0.4:3904/events/TestTopic1

-  For subscribing, run below rest api:

   curl -H "Content-Type:text/plain" -XGET
   http://172.18.0.4:3904/events/TestTopic1/CG1/C1?timeout=1000

   Note: You will only receive messages which have been published after
   you have subscribed to a topic.