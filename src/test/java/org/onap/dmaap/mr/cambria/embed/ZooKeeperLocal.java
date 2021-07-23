/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modification copyright (C) 2021 Nordix Foundation.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

 package org.onap.dmaap.mr.cambria.embed;

import java.util.Properties;
import org.apache.zookeeper.server.ServerConfig;
import org.apache.zookeeper.server.ZooKeeperServerMain;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;

public class ZooKeeperLocal {

	ZooKeeperServerMain testingZooKeeperMain = null;
	ServerConfig conf;
	Thread t1;

	public ZooKeeperLocal(Properties zkProperties) {
		QuorumPeerConfig quorumConfiguration = new QuorumPeerConfig();
		try {
		    quorumConfiguration.parseProperties(zkProperties);
		} catch(Exception e) {
		    throw new RuntimeException(e);
		}
		conf = new ServerConfig();
		conf.readFrom(quorumConfiguration);
	}

	public void run() {
		if (testingZooKeeperMain == null){
		t1 = new Thread(() -> {
			try {
				testingZooKeeperMain = new ZooKeeperServerMain();
				testingZooKeeperMain.runFromConfig(conf);
			} catch (Exception e) {
				System.out.println("Start of Local ZooKeeper Failed");
				e.printStackTrace(System.err);
			}
		});
		t1.start();
	}}

	public void stop() {
		testingZooKeeperMain.close();
		t1.stop();
	}

}
