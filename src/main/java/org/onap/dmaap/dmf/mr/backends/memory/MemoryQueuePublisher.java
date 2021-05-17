/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
*  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *  
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *  
 *******************************************************************************/
package org.onap.dmaap.dmf.mr.backends.memory;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.onap.dmaap.dmf.mr.backends.Publisher;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author anowarul.islam
 *
 */
public class MemoryQueuePublisher implements Publisher {
	/**
	 * 
	 * @param q
	 * @param b
	 */
	public MemoryQueuePublisher(MemoryQueue q, MemoryMetaBroker b) {
		fBroker = b;
		fQueue = q;
	}

	
	/**
	 * 
	 * @param topic
	 * @param msg
	 * @throws IOException
	 */
	@Override
	public void sendMessage(String topic, message msg) throws IOException {
		if (null == fBroker.getTopic(topic)) {
			try {
				fBroker.createTopic(topic, topic, null, 8, 3, false);
			} catch (TopicExistsException e) {
				throw new RuntimeException(e);
			}
		}
		fQueue.put(topic, msg);
	}

	@Override
	/**
	 * @param topic
	 * @param msgs
	 * @throws IOException
	 */

	public void sendBatchMessageNew(String topic, ArrayList<ProducerRecord<String, String>> kms) throws IOException {

	}

	public void sendMessagesNew(String topic, List<? extends message> msgs) throws IOException {
	}

	public void sendMessages(String topic, List<? extends message> msgs) throws IOException {
		for (message m : msgs) {
			sendMessage(topic, m);
		}
	}

	private final MemoryMetaBroker fBroker;
	private final MemoryQueue fQueue;
}
