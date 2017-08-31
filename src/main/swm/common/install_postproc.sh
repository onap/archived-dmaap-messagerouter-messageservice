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

if [ -f "/tmp/exitdmaapMRpostproc" ]; then
echo "file /tmp/exitdmaapMRpostproc found .exiting..........."
exit 0;
fi
. `dirname $0`/install.env

mkdir -p ${ROOT_DIR}/conf || fail 100 "Error on creating the conf directory."
mkdir -p ${ROOT_DIR}/docs || fail 100 "Error on creating the docs directory."
mkdir -p ${ROOT_DIR}/lib  || fail 100 "Error on creating the lib directory."
mkdir -p ${ROOT_DIR}/log || fail 100 "Error on creating the logs directory."

##############################################################################
# REMOVING THE DATA DIRECTORY
# The following if statement is checking to see if a new version is being installed
# on top of another version. If a new version is installed on top of the current
# version WITHOUT a proper deinstall, this will remove the data directory which
# is necessary to cleanup old AJSC route metadata. If CSTEM chooses to re-run
# the install_postproc.sh to update swm node variables, this if statement will NOT
# remove the data directory which is necessary for the SAME version to utilize the
# correct data directory route metadata.
##############################################################################
if [ "${AFTSWM_ACTION_NEW_VERSION}" != "${AFTSWM_ACTION_PREVIOUS_VERSION}" ]
then
rm -rf ${ROOT_DIR}/data
fi

# Cleaning the jetty directory which contains the AJSC exploded war as well as
# any other apps running under jetty directory
rm -rf ${ROOT_DIR}/jetty

# A simple override for the SOA Cloud platform value.  Normally this is not
# needed outside of SOA Cloud development sandboxes
# this is used in the template.lrm.xml file during startup of the service
if [ ! -z "${SCLD_PLATFORM}" ]; then
	SCLD_OPTIONAL_PLATFORM_FLAG="-Dplatform=${SCLD_PLATFORM}"; export SCLD_OPTIONAL_PLATFORM_FLAG
fi

##############################################################################
# PROCESS TEMPLATE FILES FROM ENVIRONMENT
# pattern: looks for all files starting with "template.", processes them using the
# current environment, then renames them by removing the "template." in the same
# directory
##############################################################################
utilpath=`dirname $0`/utils 
for tfile in `ls ${ROOT_DIR}/bundleconfig/etc/sysprops/template.* ${ROOT_DIR}/bundleconfig/etc/appprops/template.* ${ROOT_DIR}/bin/template.* ${ROOT_DIR}/etc/template.* 2>/dev/null`; do
    dfile=`echo ${tfile} | sed -e 's@/template\.@/@g'`
    sh ${utilpath}/findreplace.sh ${tfile} ${dfile} || exit 200
done

runningCount=`${LRMCLI} -running | grep -w ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} | wc -l` || fail 300 "Unable to determine how many instances are running prior to notifying LRM of the upgrade"

##############################################################################
# DEPLOY CONTAINER TO LRM
##############################################################################

if [ -z "${RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS}" ]
then
  RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS=180
  export RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS
fi



DTE_TME_STAMP=`date +%Y%m%d_%H%M%S`

LRM_ADD_LOG=/tmp/${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME}_ADD_${DTE_TME_STAMP}.out 
        echo "Adding resource to lrm" 
        echo "${LRMCLI} -add -file ${ROOT_DIR}/etc/lrm.xml -ttw ${RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS}" 
        ${LRMCLI} -add -file ${ROOT_DIR}/etc/lrm.xml -ttw ${RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS} > ${LRM_ADD_LOG}
        LRM_ADD_RC=$?
        echo "LRMCLI ADD RC : ${LRM_ADD_RC}"
        if [ "${LRM_ADD_RC}" -ne "0" ]; then
        
		RSRC_EXIST=`cat ${LRM_ADD_LOG} | grep SCLD-LRM-1024` # resource is already added 
            if [ "${RSRC_EXIST:-}x" = "x" ]; then 
                echo "LRM add for Resource ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} failed..." 
                cat ${LRM_ADD_LOG} 
                rm -f ${LRM_ADD_LOG} 
                exit 1 
            fi 
            echo "LRM Resource for ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} already exists. Proceeding with either addOrUpgrade or modify" 
            echo "Get the number of configured ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} instance" 
            versionCtr=`${LRMCLI} -configured | grep ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} | awk {'print $3'} | wc -l`
            if [ ${versionCtr} -eq 1 ]; then
                echo "Updating lrm resource"
                echo "${LRMCLI} -addOrUpgrade -file ${ROOT_DIR}/etc/lrm.xml -ttw ${RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS}"
                ${LRMCLI} -addOrUpgrade -file ${ROOT_DIR}/etc/lrm.xml -ttw ${RESOURCE_MANAGER_WAIT_TIME_IN_SECONDS} || abort "lrmcli addOrUpgrade failed"
            else
                echo "Modifying lrm resource"
                echo "${LRMCLI} -modify -file ${ROOT_DIR}/etc/lrm.xml"
                ${LRMCLI} -modify -file ${ROOT_DIR}/etc/lrm.xml || abort "lrmcli modify failed"
            fi
        fi
                
		echo "LRMCLI execution on ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} executed succesfully!"

ls ${ROOT_DIR}/bundleconfig/etc/appprops/MsgRtrApi.properties

if  [ ! -z $CONFIG_ZK_SERVERS ]; then
sed -i '/config.zk.servers=/c\config.zk.servers='$CONFIG_ZK_SERVERS ${ROOT_DIR}/bundleconfig/etc/appprops/MsgRtrApi.properties
fi

if  [ ! -z $TRANSID_REQD ]; then
sed -i '/transidUEBtopicreqd=/c\transidUEBtopicreqd='$TRANSID_REQD ${ROOT_DIR}/bundleconfig/etc/appprops/MsgRtrApi.properties
fi

if [ ! -z $MR_TOPICFACTOTRYCLASS ]; then
sed -i '/msgRtr.topicfactory.aaf=/c\msgRtr.topicfactory.aaf='$MR_TOPICFACTOTRYCLASS ${ROOT_DIR}/bundleconfig/etc/appprops/MsgRtrApi.properties
fi

if [ ! -z $MR_NAMESPACE ]; then
sed -i '/msgRtr.namespace.aaf=/c\msgRtr.namespace.aaf='$MR_NAMESPACE ${ROOT_DIR}/bundleconfig/etc/appprops/MsgRtrApi.properties
fi


if [ ! -z $CADI_KEYFILE ]; then
sed -i '/cadi_keyfile=/c\cadi_keyfile='$CADI_KEYFILE ${ROOT_DIR}/etc/cadi.properties
fi

if [ ! -z $AAF_URL ]; then
sed -i '/aaf_url=/c\aaf_url='$AAF_URL ${ROOT_DIR}/etc/cadi.properties
fi

if [ ! -z $AAF_ID ]; then
sed -i '/aaf_id=/c\aaf_id='$AAF_ID ${ROOT_DIR}/etc/cadi.properties
fi

if [ ! -z $AAF_PWD ]; then
sed -i '/aaf_password=/c\aaf_password='$AAF_PWD ${ROOT_DIR}/etc/cadi.properties
fi

if [ ! -z $MR_LOGLOC ]; then
sed -i '/<property name="logDirectory" value=/c\<property name="logDirectory" value="'$MR_LOGLOC'"/>' ${ROOT_DIR}/bundleconfig/etc/logback.xml
fi

if [ ! -z $MR_KSPATH ]; then
sed -i '/<Set name="KeyStorePath">/c\<Set name="KeyStorePath">'$MR_KSPATH'</Set>' ${ROOT_DIR}/etc/ajsc-jetty.xml
fi

if [ ! -z $MR_KSPWD ]; then
sed -i '/<Set name="KeyStorePassword">/c\<Set name="KeyStorePassword">'$MR_KSPWD'</Set>' ${ROOT_DIR}/etc/ajsc-jetty.xml
fi


if [ ! -z $MR_KMPWD ]; then
sed -i '/<Set name="KeyManagerPassword">/c\<Set name="KeyManagerPassword">'$MR_KMPWD'</Set>' ${ROOT_DIR}/etc/ajsc-jetty.xml
fi

 

if [ "${runningCount}" -eq 0 ]; then
    if [ "${LRM_START_SVC}" = "true" ]; then
        echo "${LRMCLI} -start -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${AFT_SERVICE_ENV} | egrep SUCCESS\|SCLD-LRM-1041"
       ${LRMCLI} -start -name ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${AFT_SERVICE_ENV} | egrep SUCCESS\|SCLD-LRM-1041 
        if [ $? -ne 0 ]; then 
    	    fail 500 "Start of ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME} with routeOffer ${AFT_SERVICE_ENV} failed" 
	    fi
	    echo "${LRMCLI} -running | grep -w ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME}"
        ${LRMCLI} -running | grep -w ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME}
    else
        echo "PROC_USER_MSG: LRM_START_SVC is set to false and no running instances were found prior to upgrading so ending install with no running service instances."
    fi 
else
    ${LRMCLI} -running | grep -w ${SOA_CLOUD_NAMESPACE}.${AFTSWM_ACTION_ARTIFACT_NAME}
fi

    
exit 0
