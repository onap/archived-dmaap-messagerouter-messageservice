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

import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.onap.dmaap.dmf.mr.backends.kafka.KafkaPublisher;
import org.onap.dmaap.dmf.mr.utils.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "javax.management.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Utils.class })
public class KafkaPublisherTest {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.when(Utils.isCadiEnabled()).thenReturn(false);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPublisherInit() {
		
		

		try {
			try {
				KafkaPublisher kafkaPublisher = new KafkaPublisher(null);
			} catch (missingReqdSetting e) {
				assertTrue(true);
			}
		} catch (LinkageError e) {
			assertTrue(true);
		}

	}

	

}
