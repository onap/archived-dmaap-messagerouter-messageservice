.. This work is licensed under a Creative Commons Attribution 4.0 International License.
.. http://creativecommons.org/licenses/by/4.0


Release Notes
=============



Version: 1.1.4
--------------

:Release Date: 2018-06-07

**New Features**

This release fixes the packaging and security issues.

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

