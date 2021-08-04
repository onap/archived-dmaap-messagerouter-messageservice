/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

 package org.onap.dmaap.mr.cambria.backends.kafka;

import static org.junit.Assert.assertTrue;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.curator.framework.CuratorFramework;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.dmaap.dmf.mr.backends.Consumer;
import org.onap.dmaap.dmf.mr.backends.MetricsSet;
import org.onap.dmaap.dmf.mr.backends.kafka.Kafka011Consumer;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaConsumerCache;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaConsumerCache.KafkaConsumerCacheException;


@RunWith(MockitoJUnitRunner.class)
public class KafkaConsumerCacheTest {

	@Mock
	private ConcurrentHashMap<String, Kafka011Consumer> fConsumers;
	@Mock
	private MetricsSet fMetrics;
	@Mock
	private AJSCPropertiesMap ajscPropertiesMap;
	@InjectMocks
	private KafkaConsumerCache kafkaConsumerCache;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSweep() {
		kafkaConsumerCache.sweep();
		Collection<? extends Consumer> cachedConsumers =
			kafkaConsumerCache.getConsumers();
		assertTrue(cachedConsumers.isEmpty());
	}


	// DOES NOT WORK
	@Test
	public void testStartCache() {

		/*
		 * KafkaConsumerCache kafka = null;
		 *
		 * try { kafka = new KafkaConsumerCache("123", null);
		 *
		 * } catch (NoClassDefFoundError e) { try { kafka.startCache("DMAAP",
		 * null); } catch (NullPointerException e1) { // TODO Auto-generated
		 * catch block assertTrue(true); } catch (KafkaConsumerCacheException
		 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); } }
		 */


		new CuratorFrameworkImpl();
		new MetricsSetImpl();
		KafkaConsumerCache kafka=null;
		try {
			kafka = new KafkaConsumerCache();
			kafka.setfApiId("1");
			kafka.startCache("DMAAP", null);
		} catch (NoClassDefFoundError e) {

		} catch (KafkaConsumerCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testGetCuratorFramework() {

		CuratorFramework curator = new CuratorFrameworkImpl();
		new MetricsSetImpl();
		try {

		} catch (NoClassDefFoundError e) {

			KafkaConsumerCache.getCuratorFramework(curator);
		}

	}

	/*
	 * @Test public void testStopCache() {
	 *
	 * KafkaConsumerCache kafka = null; new CuratorFrameworkImpl(); new
	 * MetricsSetImpl(); try { kafka = new KafkaConsumerCache("123", null);
	 * kafka.stopCache(); } catch (NoClassDefFoundError e) {
	 *
	 * }
	 *
	 * }
	 */

	@Test(expected= KafkaConsumerCacheException.class)
	public void testGetConsumerThrowNoConnectionToCache() throws KafkaConsumerCacheException{
		KafkaConsumerCache kafka = new KafkaConsumerCache();
		kafka.getConsumerFor("testTopic", "CG1", "23");
	}

	@Test
	public void testGetConsumerFor() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();
			kafka.getConsumerFor("testTopic", "CG1", "23");
		} catch (NoClassDefFoundError e) {

		} catch (KafkaConsumerCacheException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testPutConsumerFor() {

		Kafka011Consumer consumer = null;
		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();

		} catch (NoClassDefFoundError e) {
			try {
				kafka.putConsumerFor("testTopic", "CG1", "23", consumer);
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			} catch (KafkaConsumerCacheException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	@Test
	public void testGetConsumers() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();

		} catch (NoClassDefFoundError e) {
			try {
				kafka.getConsumers();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testDropAllConsumers() {

		KafkaConsumerCache kafka = null;
		try {
			kafka = new KafkaConsumerCache();

		} catch (NoClassDefFoundError e) {
			try {
				kafka.dropAllConsumers();
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}

	@Test
	public void testSignalOwnership() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();
		 try {
			kafka.signalOwnership("testTopic", "CG1", "23");
		} catch (KafkaConsumerCacheException e) {
			assertTrue(true);
		}
		} catch (NoClassDefFoundError e) {}

		//
	}

	@Test
	public void testDropConsumer() {

		KafkaConsumerCache kafka = null;

		try {
			kafka = new KafkaConsumerCache();
			// kafka.dropConsumer("testTopic", "CG1", "23");
		} catch (NoClassDefFoundError e) {
			try {
				kafka.dropConsumer("testTopic", "CG1", "23");
			} catch (NullPointerException e1) {
				// TODO Auto-generated catch block
				assertTrue(true);
			}
		}

	}


}
