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
root_directory="/appl/${project.artifactId}"
config_directory="/appl/${project.artifactId}/bundleconfig"
runner_file="appl/${project.artifactId}/lib/ajsc-runner-${ajscRuntimeVersion}.jar"
echo "AJSC HOME directory is " $root_directory
echo "AJSC Conf Directory is" $config_directory
echo "Starting using" $runner_file

java -jar  -XX:MaxPermSize=256m -XX:PermSize=32m -DSOACLOUD_SERVICE_VERSION=0.0.1 -DAJSC_HOME=$root_directory -DAJSC_CONF_HOME=$config_directory -DAJSC_SHARED_CONFIG=$config_directory -DAJSC_HTTPS_PORT=3905 -Dplatform=NON-PROD -DPid=1306 -Dlogback.configurationFile=/appl/dmaapMR1/bundleconfig/etc/logback.xml -Xmx512m -Xms512m  $runner_file context=/ port=3904 sslport=3905
