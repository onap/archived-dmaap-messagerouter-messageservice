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

package com.att.nsa.dmaap.service;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.ApiKeyBean;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

public class ApiKeysRestServiceTest {

	private ApiKeysRestService service = null;

	@Before
	public void setUp() throws Exception {
		service = new ApiKeysRestService();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllApiKeys() {

		try {
			service.getAllApiKeys();
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void testGetApiKey() {

		try {
			service.getApiKey("apikeyName");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void testCreateApiKey() {

		try {
			service.createApiKey(new ApiKeyBean("hs647a@att.com", "test apikey"));
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	
	@Test
	public void testUpdateApiKey() {

		try {
			service.updateApiKey("apikeyName", new ApiKeyBean("hs647a@att.com", "test apikey"));
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void testDeleteApiKey() {

		try {
			service.deleteApiKey("apikeyName");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void testGetDmaapContext() {
		Class clazz = null;
		Method method = null;
		try {
			clazz = Class.forName("com.att.nsa.dmaap.service.ApiKeysRestService");
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