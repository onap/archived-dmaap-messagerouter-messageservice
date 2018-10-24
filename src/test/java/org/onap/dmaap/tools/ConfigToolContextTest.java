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

 package org.onap.dmaap.tools;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigToolContextTest {
	
	private ConfigToolContext context;
	@Before
	public void setUp() throws Exception {
		context = new ConfigToolContext(null, "connStr", null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRequestShutdown() {

		context.requestShutdown();

		assertTrue(true);

	}
	
	@Test
	public void testShouldContinue() {

		context.shouldContinue();

		assertTrue(true);

	}
	
	@Test
	public void testGetDb() {

		context.getDb();

		assertTrue(true);

	}
	
	@Test
	public void testGetMetrics() {

		context.getMetrics();

		assertTrue(true);

	}
	
	@Test
	public void testGetConnectionString() {

		context.getConnectionString();

		assertTrue(true);

	}

}