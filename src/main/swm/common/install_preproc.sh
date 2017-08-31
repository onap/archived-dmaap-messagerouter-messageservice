#!/bin/sh
#*******************************************************************************
#  ============LICENSE_START=======================================================
#  org.onap.dmaap
#  ================================================================================
#  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
#  ================================================================================
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#        http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#  ============LICENSE_END=========================================================
#
#  ECOMP is a trademark and service mark of AT&T Intellectual Property.
#  
#*******************************************************************************

. `dirname $0`/deinstall.env

LRMCLI=${INSTALL_ROOT}/opt/app/aft/scldlrm/bin/lrmcli
PATH=$PATH:`dirname $0`/utils; export PATH
if [ -d $LRMCLI ]; then
runningCount=`${LRMCLI} -running | grep -w ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_PREVIOUS_VERSION} | wc -l` || fail 300 "Unable to determine how many instances are running prior to notifying LRM of the upgrade"

if [ "${runningCount}" -eq 0 ]; then

${LRMCLI} -delete -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_PREVIOUS_VERSION} -routeoffer ${AFT_SERVICE_ENV} || exit 101

	else 
		${LRMCLI} -shutdown -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_PREVIOUS_VERSION} -routeoffer ${AFT_SERVICE_ENV} -ttw ${RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS} || exit 100
		${LRMCLI} -delete -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_PREVIOUS_VERSION} -routeoffer ${AFT_SERVICE_ENV} || exit 101
		
fi

rm -rf ${INSTALL_ROOT}/${ROOT_DIR}/logs || {
    echo "WARNING: Unable to purge logs directory during deinstall"
}
fi

exit 0
