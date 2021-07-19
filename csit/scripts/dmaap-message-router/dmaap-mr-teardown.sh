#!/bin/bash
#
# Copyright 2016-2017 Huawei Technologies Co., Ltd.
# Modification copyright (C) 2021 Nordix Foundation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# Modifications copyright (c) 2018 AT&T Intellectual Property
#

function dmaap_mr_teardown() {
    cd ${WORKSPACE}/archives/dmaap/mr
    rm -rf last_run_logs/*
    docker cp dmaap-mr:/appl/logs/EELF last_run_logs/dmaap_mr_logs
    docker cp dmaap-kafka:/var/lib/kafka/data/ last_run_logs/kafka_logs
    docker logs dmaap-kafka  > last_run_logs/kafka_logs/kafka.log
    docker logs dmaap-zookeeper  > last_run_logs/zookeeper.log
    docker-compose -f ${WORKSPACE}/scripts/dmaap-message-router/docker-compose/docker-compose.yml rm -sf
}
