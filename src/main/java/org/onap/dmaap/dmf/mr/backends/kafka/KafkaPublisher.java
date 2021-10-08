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
package org.onap.dmaap.dmf.mr.backends.kafka;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.drumlin.till.nv.rrNvReadable;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.JSONException;
import org.onap.dmaap.dmf.mr.backends.Publisher;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.utils.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


/**
 * Sends raw JSON objects into Kafka.
 *
 * Could improve space: BSON rather than JSON?
 *
 * @author peter
 *
 */

public class KafkaPublisher implements Publisher {
	/**
	 * constructor initializing
	 *
	 * @param settings
	 * @throws rrNvReadable.missingReqdSetting
	 */
	public KafkaPublisher(@Qualifier("propertyReader") rrNvReadable settings) throws rrNvReadable.missingReqdSetting {

		final Properties props = new Properties();
		String kafkaConnUrl= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"kafka.metadata.broker.list");
		if(StringUtils.isEmpty(kafkaConnUrl)){

			kafkaConnUrl="localhost:9092";
		}


		if(Utils.isCadiEnabled()){
			props.putAll(Utils.addSaslProps());
		}
		transferSetting( props, "bootstrap.servers",kafkaConnUrl);

		transferSetting( props, "request.required.acks", "1");
		transferSetting( props, "message.send.max.retries", "5");
		transferSetting(props, "retry.backoff.ms", "150");



		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");



		fProducer = new KafkaProducer<>(props);
	}

	/**
	 * Send a message with a given topic and key.
	 *
	 * @param msg
	 * @throws FailedToSendMessageException
	 * @throws JSONException
	 */
	@Override
	public void sendMessage(String topic, message msg) throws IOException{
		final List<message> msgs = new LinkedList<>();
		msgs.add(msg);
		sendMessages(topic, msgs);
	}

	/**
	 * method publishing batch messages
	 * This method is commented from 0.8 to 0.11 upgrade
	 * @param topic
	 * @param kms
	 * throws IOException
	 *
	public void sendBatchMessage(String topic, ArrayList<KeyedMessage<String, String>> kms) throws IOException {
	try {
	fProducer.send(kms);

	} catch (FailedToSendMessageException excp) {
	log.error("Failed to send message(s) to topic [" + topic + "].", excp);
	throw new FailedToSendMessageException(excp.getMessage(), excp);
	}

	} */


	/*
	 * Kafka 11.0 Interface
	 * @see com.att.nsa.cambria.backends.Publisher#sendBatchMessageNew(java.lang.String, java.util.ArrayList)
	 */
	public void sendBatchMessageNew(String topic, ArrayList <ProducerRecord<String,String>> kms) throws IOException {
		try {
			for (ProducerRecord<String,String> km : kms) {
				fProducer.send(km);
			}

		} catch (Exception excp) {
			log.error("Failed to send message(s) to topic [" + topic + "].", excp);
			throw new IOException(excp.getMessage(), excp);
		}

	}

	/**
	 * Send a set of messages. Each must have a "key" string value.
	 *
	 * @param topic
	 * @param msg
	 * @throws FailedToSendMessageException
	 * @throws JSONException
	 *
	 @Override
	 public void sendMessages(String topic, List<? extends message> msgs)
	 throws IOException, FailedToSendMessageException {
	 log.info("sending " + msgs.size() + " events to [" + topic + "]");

	 final List<KeyedMessage<String, String>> kms = new ArrayList<KeyedMessage<String, String>>(msgs.size());
	 for (message o : msgs) {
	 final KeyedMessage<String, String> data = new KeyedMessage<String, String>(topic, o.getKey(), o.toString());
	 kms.add(data);
	 }
	 try {
	 fProducer.send(kms);

	 } catch (FailedToSendMessageException excp) {
	 log.error("Failed to send message(s) to topic [" + topic + "].", excp);
	 throw new FailedToSendMessageException(excp.getMessage(), excp);
	 }
	 } */
	@Override
	public void sendMessagesNew(String topic, List<? extends message> msgs) throws IOException {
		log.info("sending " + msgs.size() + " events to [" + topic + "]");
		try {
			for (message o : msgs) {
				final ProducerRecord<String, String> data =
						new ProducerRecord<>(topic, o.getKey(), o.toString());
				fProducer.send(data);
			}
		} catch (Exception e) {
			log.error("Failed to send message(s) to topic [" + topic + "].", e);
		}
	}


	private Producer<String, String> fProducer;

	/**
	 * It sets the key value pair
	 * @param topic
	 * @param msg
	 * @param key
	 * @param defVal
	 */
	private void transferSetting(Properties props, String key, String defVal) {
		String kafkaProp= com.att.ajsc.filemonitor.AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,"kafka." + key);
		if (StringUtils.isEmpty(kafkaProp)) kafkaProp=defVal;
		props.put(key, kafkaProp);
	}

	private static final EELFLogger log = EELFManager.getInstance().getLogger(KafkaPublisher.class);

	@Override
	public void sendMessages(String topic, List<? extends message> msgs) throws IOException {
		// TODO Auto-generated method stub

	}
}
