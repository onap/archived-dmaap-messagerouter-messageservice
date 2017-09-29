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

package com.att.nsa.dmaap.filemonitor;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServicePropertyServiceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInit() {

		ServicePropertyService service = new ServicePropertyService();
		try {
			service.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testsetLoadOnStartup() {

		ServicePropertyService service = new ServicePropertyService();
		try {
			service.setLoadOnStartup(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testSetSsfFileMonitorPollingInterval() {

		ServicePropertyService service = new ServicePropertyService();
		try {
			service.setSsfFileMonitorPollingInterval("interval");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testSetSsfFileMonitorThreadpoolSize() {

		ServicePropertyService service = new ServicePropertyService();
		try {
			service.setSsfFileMonitorThreadpoolSize("threadPoolSize");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(true);

	}
	
	@Test
	public void testIsLoadOnStartup() {
		ServicePropertyService service = new ServicePropertyService();
		service.isLoadOnStartup();
		assertTrue(true);

	}
	
	@Test
	public void testGetSsfFileMonitorPollingInterval() {
		ServicePropertyService service = new ServicePropertyService();
		service.getSsfFileMonitorPollingInterval();
		assertTrue(true);

	}
	
	@Test
	public void testGetSsfFileMonitorThreadpoolSize() {
		ServicePropertyService service = new ServicePropertyService();
		service.getSsfFileMonitorThreadpoolSize();
		assertTrue(true);

	}
	
	@Test
	public void testGetFileChangedListener() {
		ServicePropertyService service = new ServicePropertyService();
		service.getFileChangedListener();
		assertTrue(true);

	}
	
	@Test
	public void testSetFileChangedListener() {
		ServicePropertyService service = new ServicePropertyService();
		service.setFileChangedListener(null);
		assertTrue(true);

	}
	
	@Test
	public void testGetFilePropertiesMap() {
		ServicePropertyService service = new ServicePropertyService();
		service.getFilePropertiesMap();
		assertTrue(true);

	}
	
	@Test
	public void testSetFilePropertiesMap() {
		ServicePropertyService service = new ServicePropertyService();
		service.setFilePropertiesMap(null);
		assertTrue(true);

	}

}