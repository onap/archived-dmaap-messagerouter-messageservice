#!/bin/bash
#
# ============LICENSE_START=======================================================
# ONAP DMAAP MR 
# ================================================================================
# Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
# Modification copyright (C) 2021 Nordix Foundation.
# ================================================================================
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ============LICENSE_END============================================
# ===================================================================
# ECOMP is a trademark and service mark of AT&T Intellectual Property.

# function to launch DMaaP MR docker containers.
# sets global var DMAAP_MR_IP with assigned IP address of MR container.
# (kafka and zk containers are not called externally)

function dmaap_mr_launch() {
    mkdir -p $WORKSPACE/archives/dmaap/mr/last_run_logs

    # start DMaaP MR containers with docker compose and configuration from docker-compose.yml
    docker login -u docker -p docker nexus3.onap.org:10001
    docker-compose -f ${WORKSPACE}/scripts/dmaap-message-router/docker-compose/docker-compose.yml up -d
    docker ps

    # Wait for initialization of Docker containers for DMaaP MR, Kafka and Zookeeper
    for i in {1..50}; do
        if [[ $(docker inspect --format '{{ .State.Running }}' dmaap-mr) ]] && \
            [[ $(docker inspect --format '{{ .State.Running }}' dmaap-kafka) ]] && \
            [[ $(docker inspect --format '{{ .State.Running }}' dmaap-zookeeper) ]]
        then
            echo "DMaaP Service Running"
            break
        else
            echo sleep $i
            sleep $i
        fi
    done

DMAAP_MR_IP=`get-instance-ip.sh dmaap-mr`
echo MR_IP=${DMAAP_MR_IP}
export DMAAP_MR_IP

echo "Waiting for dmaap-message-router to come up ..."
for i in {1..20}; do
    dmaap_state=$(curl --write-out '%{http_code}' --silent --output /dev/null $DMAAP_MR_IP:3904/topics)
    if [ ${dmaap_state} == "200" ]
    then
      break
    else
      sleep 5
    fi
done

}

