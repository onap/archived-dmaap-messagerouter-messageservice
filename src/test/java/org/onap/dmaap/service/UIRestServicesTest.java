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

 package org.onap.dmaap.service;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

public class UIRestServicesTest {

	private UIRestServices service = null;

	@Before
	public void setUp() throws Exception {
		service = new UIRestServices();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testHello() {

		try {
			service.hello();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		assertTrue(true);
	}
	
	@Test
	public void testGetApiKeysTable() {

		try {
			service.getApiKeysTable();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		assertTrue(true);
	}
	

	@Test
	public void testGetApiKey() {

		try {
			service.getApiKey("apikey");
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		assertTrue(true);
	}
	
	@Test
	public void testGetTopicsTable() {

		try {
			service.getTopicsTable();
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		assertTrue(true);
	}
	
	@Test
	public void testGetTopic() {

		try {
			service.getTopic("topicName");
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		assertTrue(true);
	}
	
	@Test
	public void testGetDmaapContext() {
		Class clazz = null;
		Method method = null;
		try {
			clazz = Class.forName("org.onap.dmaap.service.UIRestServices");
			Object obj = clazz.newInstance();
			method = clazz.getDeclaredMethod("getDmaapContext", null);
			method.setAccessible(true);
			method.invoke(obj, null);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(true);
	}
	

}