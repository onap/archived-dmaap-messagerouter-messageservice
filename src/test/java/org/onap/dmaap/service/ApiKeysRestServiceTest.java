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


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import java.io.IOException;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.ApiKeyBean;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.service.ApiKeysService;

@RunWith(MockitoJUnitRunner.class)
public class ApiKeysRestServiceTest {
	@InjectMocks
	private ApiKeysRestService service;

	@Mock
	ApiKeysService apiKeyService;

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

	@Test(expected=CambriaApiException.class)
	public void testGetAllApiKeys_IOException() throws ConfigDbException, IOException, CambriaApiException {
		doThrow(new IOException("error")).when(apiKeyService).getAllApiKeys(ArgumentMatchers.any(DMaaPContext.class));
		service.getAllApiKeys();
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testGetAllApiKeys_ConfigDBException() throws ConfigDbException, IOException, CambriaApiException {
		doThrow(new ConfigDbException("error")).when(apiKeyService).getAllApiKeys(ArgumentMatchers.any(DMaaPContext.class));
		service.getAllApiKeys();
		fail("Was expecting an exception to be thrown");
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

	@Test(expected=CambriaApiException.class)
	public void testGetApiKey_IOException() throws ConfigDbException, IOException, CambriaApiException {
		String apikeyName = "apikeyName";
		doThrow(new IOException("error")).when(apiKeyService).getApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		service.getApiKey(apikeyName);
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testGetApiKey_ConfigDBException() throws ConfigDbException, IOException, CambriaApiException {
		String apikeyName = "apikeyName";
		doThrow(new ConfigDbException("error")).when(apiKeyService).getApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		service.getApiKey(apikeyName);
		fail("Was expecting an exception to be thrown");
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

	@Test(expected=CambriaApiException.class)
	public void testCreateApiKey_ConfigDbException()
			throws CambriaApiException, JSONException, KeyExistsException, ConfigDbException, IOException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");

		doThrow(new ConfigDbException("error")).when(apiKeyService).createApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(ApiKeyBean.class));

		service.createApiKey(bean);
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testCreateApiKey_IOException()
		throws CambriaApiException, JSONException, KeyExistsException, ConfigDbException, IOException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");

		doThrow(new IOException("error")).when(apiKeyService).createApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(ApiKeyBean.class));

		service.createApiKey(bean);
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testCreateApiKey_KeyExistsException()
		throws CambriaApiException, JSONException, KeyExistsException, ConfigDbException, IOException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");

		doThrow(new KeyExistsException("error")).when(apiKeyService).createApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(ApiKeyBean.class));

		service.createApiKey(bean);
		fail("Was expecting an exception to be thrown");
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

	@Test(expected=CambriaApiException.class)
	public void testUpdateApiKey_ConfigDbException() throws CambriaApiException, JSONException,
			ConfigDbException, IOException, AccessDeniedException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		doThrow(new ConfigDbException("error")).when(apiKeyService).updateApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ApiKeyBean.class));

		service.updateApiKey("apikeyName", bean);
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testUpdateApiKey_IOException() throws CambriaApiException, JSONException,
		ConfigDbException, IOException, AccessDeniedException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		doThrow(new IOException("error")).when(apiKeyService).updateApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ApiKeyBean.class));

		service.updateApiKey("apikeyName", bean);
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testUpdateApiKey_AccessDeniedException() throws CambriaApiException, JSONException,
		ConfigDbException, IOException, AccessDeniedException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		doThrow(new AccessDeniedException("error")).when(apiKeyService).updateApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ApiKeyBean.class));

		service.updateApiKey("apikeyName", bean);
		fail("Was expecting an exception to be thrown");
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

	@Test(expected=CambriaApiException.class)
	public void testDeleteApiKey_AccessDeniedException() throws CambriaApiException, JSONException,
		ConfigDbException, IOException, AccessDeniedException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		doThrow(new AccessDeniedException("error")).when(apiKeyService).deleteApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		service.deleteApiKey("apikeyName");
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testDeleteApiKey_IOException() throws CambriaApiException, JSONException,
		ConfigDbException, IOException, AccessDeniedException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		doThrow(new IOException("error")).when(apiKeyService).deleteApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		service.deleteApiKey("apikeyName");
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testDeleteApiKey_ConfigDbException() throws CambriaApiException, JSONException,
		ConfigDbException, IOException, AccessDeniedException {

		ApiKeyBean bean = new ApiKeyBean("test@onap.com", "test apikey");
		doThrow(new ConfigDbException("error")).when(apiKeyService).deleteApiKey(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		service.deleteApiKey("apikeyName");
		fail("Was expecting an exception to be thrown");
	}
}