=================================
DMAAP MESSAGE ROUTER INSTALLATION
=================================
This document describes how to install and access DMaaP Message Router.
Message Router has 3 docker containers. Dmaap\_container,
kafka\_contaienr and zookeeper\_container. Zookeeper runs on 172.18.0.3,
kafka runs on 172.18.0.2 and dmaap on 172.18.0.4.

1) Clone message service repo

   git clone http://gerrit.onap.org/r/dmaap/messagerouter/messageservice

2) copy
   messageservice/bundleconfig-local/etc/appprops/MsgRtrApi.properties
   to /var/tmp directory

3) In /var/tmp/MsgRtrApi.properties, change value of below variables as
   shown below:

   config.zk.servers=172.18.0.3

   kafka.metadata.broker.list=172.18.0.2:9092

4) Install docker and docker-compose

5) Go to messageservice/src/main/resources/docker-compose and run:

   docker-compose up –d

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


