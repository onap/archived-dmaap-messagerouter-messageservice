.. This work is licensed under a Creative Commons Attribution 4.0 International License.

Release Notes
=============

.. note::
   * This Release Notes must be updated each time the team decides to Release new artifacts.
   * The scope of this Release Notes is for this particular component. In other words, each ONAP component has its Release Notes.
   * This Release Notes is cumulative, the most recently Released artifact is made visible in the top of this Release Notes.
   * Except the date and the version number, all the other sections are optional but there must be at least one section describing the purpose of this new release.
   * This note must be removed after content has been added.


Version: 1.0.1
--------------


:Release Date: 2017-11-16



**New Features**

 - Pub/sub messaging metaphor to broaden data processing opportunities
 - A single solution for most event distribution needs to support a range of environments
 - Standardized topic names
 - Implements a RESTful HTTP API for provisioning
 - Implements a RESTful HTTP API for message transactions (i.e. pub, sub)
 - Implements a RESTful HTTP API for transaction metrics
 - Topic registry and discovery



**Bug Fixes**
   - `DMAAP-165 <https://jira.onap.org/browse/DMAAP-165>`_ Correct documentation rst file errors and warnings
   - `DMAAP-160 <https://jira.onap.org/browse/DMAAP-160>`_ DMaaP periodically loses connection to Kafka
   - `DMAAP-157 <https://jira.onap.org/browse/DMAAP-157>`_ SDC service models distribution fails
   - `DMAAP-151 <https://jira.onap.org/browse/DMAAP-151>`_ Fix docker image bug
   - `DMAAP-1 <https://jira.onap.org/browse/DMAAP-1>`_ MSO DB is not populated with the models from SDC
   
**Known Issues**
   - `DMAAP-164 <https://jira.onap.org/browse/DMAAP-164>`_ The dependency from kafka for zookeeper created issues when the vm is restarted.
         

**Security Issues**
   N/A

**Upgrade Notes**
   N/A
**Deprecation Notes**
   N/A
**Other**

===========
