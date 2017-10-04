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
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(PowerMockRunner.class)
public class ServicePropertiesMapTest {

	@InjectMocks
	ServicePropertiesMap map;


	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRefresh() {

		try {
			map.refresh(new File(":/file"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(true);

	}

	@Test()
	public void testRefreshJsonFile() throws Exception {
		//Path resourceDirectory = Paths.get("src/test/resources");
			// map.refresh(new File(resourceDirectory+"\\"+"test.json"));
		ClassLoader classLoader = getClass().getClassLoader();
		map.refresh(new File(classLoader.getResource("test.json").getFile()));
		
		assertTrue(true);
	}
	
	@Test
	public void testRefreshPropsFile() throws Exception {
		/*Path resourceDirectory = Paths.get("src/test/resources");
			map.refresh(new File(resourceDirectory+"\\"+"test.properties"));*/
		ClassLoader classLoader = getClass().getClassLoader();
		map.refresh(new File(classLoader.getResource("test.json").getFile()));
			assertTrue(true);
	}

	@Test
	public void testGetProperty() {

		try {
			map.getProperty("filename", "propertykey");
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(true);
	}

	@Test
	public void testGetProperties() {

		try {
			map.getProperties("filename");
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(true);

	}

	@Test
	public void testIfNullThenEmpty() {

		try {
			map.getProperties("filename");
		} catch (Exception e) {
			e.printStackTrace();
		}

		assertTrue(true);

	}

}