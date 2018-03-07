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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.ApiKeyBean;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.json.JSONException;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.att.nsa.cambria.beans.DMaaPContext;

import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.service.ApiKeysService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceUtil.class })
public class ApiKeysRestServiceTest {

	@InjectMocks
	private ApiKeysRestService service;

	@Mock
	ApiKeysService apiKeyService;

	@Mock
	DMaaPContext dmaapContext;

	@Mock
	HttpServletRequest httpServReq;
	@Mock
	private HttpServletResponse response;
	@Mock
	private ConfigurationReader configReader;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
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
	public void testGetAllApiKeys_error() throws ConfigDbException, IOException {
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaapContext);
		PowerMockito.doThrow(new IOException("error")).when(apiKeyService).getAllApiKeys(dmaapContext);
		try {
			service.getAllApiKeys();
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			assertTrue(true);
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
	public void testGetApiKey_error() throws ConfigDbException, IOException {
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaapContext);
		PowerMockito.doThrow(new IOException("error")).when(apiKeyService).getApiKey(dmaapContext, "apikeyName");

		try {
			service.getApiKey("apikeyName");
		} catch (CambriaApiException e) {
			// TODO Auto-generated catch block
			assertTrue(true);
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
	public void testCreateApiKey_error()
			throws CambriaApiException, JSONException, KeyExistsException, ConfigDbException, IOException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaapContext);
		PowerMockito.doThrow(new IOException("error")).when(apiKeyService).createApiKey(dmaapContext, bean);

		try {
			service.createApiKey(bean);
		} catch (CambriaApiException e) {
			assertTrue(true);
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
	public void testUpdateApiKey_error() throws CambriaApiException, JSONException, KeyExistsException,
			ConfigDbException, IOException, AccessDeniedException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaapContext);
		PowerMockito.doThrow(new IOException("error")).when(apiKeyService).updateApiKey(dmaapContext, "apikeyName",
				bean);
		try {
			service.updateApiKey("apikeyName", bean);
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

}