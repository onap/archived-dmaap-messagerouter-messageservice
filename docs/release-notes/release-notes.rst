.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0


Release Notes
=============

Version: 6.0.0 (Frankfurt Release)
------------------------

:Release Date: 2019-05-20

**New Features**

- Kafka and Zookeeper images are created using confluent community edition 5.3.0 
- Prometheus monitoring
- Protected all the Kafka operations using AAF
- Remove the AAF dependency by toggling  a flag


**Bug Fixes**

NA

**Known Issues**
	NA

**Security Notes**

DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `Dublin <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_.

Quick Links:

- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_


**Upgrade Notes**
  NA

**Deprecation Notes**

Version: 1.1.16 (ElAlto)
------------------------

:Release Date: 2019-10-10

**New Features**

- Cert based authentication support in Message Router
- Improved Kafka and Zookeeper cluster lookup
- Pluggable Kafka server.properties,log4j.properties  and Message Router logback.xml


**Bug Fixes**

- Fixed for security vulnerabilities in Message Router
- Fixed  authorization issues in Message Router

**Known Issues**
	NA

**Security Notes**

DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `Dublin <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_.

Quick Links:

- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_
- `Dublin Project Vulnerability Review Table for DMAAP <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_

**Upgrade Notes**
  NA

**Deprecation Notes**

Version: 1.1.14 (Dublin)
------------------------

:Release Date: 2019-06-06

**New Features**

- Upgrade Kafka to v1.1.1
- Support for Authenticated topics
- Add Scaling support
- Support for multi-site applications 
- Add MirrorMaker to allow for message replication across Kafka clusters

**Bug Fixes**
	NA
**Known Issues**
	NA

**Security Notes**

DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `Dublin <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_.

Quick Links:

- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_
- `Dublin Project Vulnerability Review Table for DMAAP <https://wiki.onap.org/pages/viewpage.action?pageId=64003715>`_

**Upgrade Notes**
  NA

**Deprecation Notes**


Version: 1.1.8 (Casablanca)
---------------------------

:Release Date: 2018-11-30

**New Features**

 - DMaaP client changes to fix some known issues
 - Kafka upgrade  to 0.11.0.1 and corresponding changes in the Message Router
 - New Kafka image was created instead of using the publicly available Kafka image

**Bug Fixes**
	NA
	
**Known Issues**

If the ZooKeeper  is restarted, Message Router works as expected only after restarting the Message Router . Refer  `Jira <https://jira.onap.org/browse/DMAAP-893>`_  for more details

**Security Notes**

DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `Casablanca <https://wiki.onap.org/pages/viewpage.action?pageId=42598688>`_.

Quick Links:

- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_
- `Casablanca Project Vulnerability Review Table for DMAAP <https://wiki.onap.org/pages/viewpage.action?pageId=42598688>`_

**Upgrade Notes**
  NA

**Deprecation Notes**



Version: 1.1.4
--------------

:Release Date: 2018-06-07

**New Features**

 - Topic creation with out AAF
 - DMaaP client changes to call the message Router with out authentication
 - Kafka upgrade from 0.8.11 to 1.1.0
 - Fixes for docker image packaging issues

**Bug Fixes**
	NA
**Known Issues**
	NA

**Security Notes**

DMAAP code has been formally scanned during build time using NexusIQ and all Critical vulnerabilities have been addressed, items that remain open have been assessed for risk and determined to be false positive. The DMAAP open Critical security vulnerabilities and their risk assessment have been documented as part of the `project <https://wiki.onap.org/pages/viewpage.action?pageId=28379799>`_.

Quick Links:
- `DMAAP project page <https://wiki.onap.org/display/DW/DMaaP+Planning>`_
- `Passing Badge information for DMAAP <https://bestpractices.coreinfrastructure.org/en/projects/1751>`_
- `Project Vulnerability Review Table for DMAAP <https://wiki.onap.org/pages/viewpage.action?pageId=28379799>`_

**Upgrade Notes**
  NA

**Deprecation Notes**

Version: 1.0.1
--------------

:Release Date: 2017-11-16


New Features:

 - Pub/sub messaging metaphor to broaden data processing opportunities
 - A single solution for most event distribution needs to support a range of environments
 - Standardized topic names
 - Implements a RESTful HTTP API for provisioning
 - Implements a RESTful HTTP API for message transactions (i.e. pub, sub)
 - Implements a RESTful HTTP API for transaction metrics
 - Topic registry and discovery



Bug Fixes
   - `DMAAP-165 <https://jira.onap.org/browse/DMAAP-165>`_ Correct documentation rst file errors and warnings
   - `DMAAP-160 <https://jira.onap.org/browse/DMAAP-160>`_ DMaaP periodically loses connection to Kafka
   - `DMAAP-157 <https://jira.onap.org/browse/DMAAP-157>`_ SDC service models distribution fails
   - `DMAAP-151 <https://jira.onap.org/browse/DMAAP-151>`_ Fix docker image bug
   - `DMAAP-1 <https://jira.onap.org/browse/DMAAP-1>`_ MSO DB is not populated with the models from SDC
   
Known Issues
   - `DMAAP-164 <https://jira.onap.org/browse/DMAAP-164>`_ The dependency from kafka for zookeeper created issues when the vm is restarted

Other

