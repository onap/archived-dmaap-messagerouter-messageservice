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

import java.io.IOException;
import java.util.Properties;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServer;
import org.apache.kafka.common.utils.Time;


public class KafkaLocal {
 
	public KafkaServer kafka;
	public ZooKeeperLocal zookeeper;
	
	public KafkaLocal(Properties kafkaProperties, Properties zkProperties) throws IOException, InterruptedException{
		KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
		
		//start local zookeeper
		System.out.println("starting local zookeeper...");
		zookeeper = new ZooKeeperLocal(zkProperties);
		zookeeper.run();
		System.out.println("done");
		Thread.sleep(5000);
		//start local kafka broker
		final scala.Option<String> prefix = scala.Option.apply("kafka");
		kafka = new KafkaServer(kafkaConfig, Time.SYSTEM, prefix, false);
		System.out.println("starting local kafka broker...");
		kafka.startup();
		System.out.println("done");
	}
	
	
	public void stop(){
		//stop kafka broker
		System.out.println("stopping kafka...");
		kafka.shutdown();
		kafka.awaitShutdown();
		System.out.println("done");
		System.out.println("stopping zookeeper...");
		zookeeper.stop();
		System.out.println("done");
	}
	
}