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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.service.EventsService;
import com.att.nsa.cambria.service.TransactionService;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.aft.dme2.internal.jettison.json.JSONException;
import org.powermock.api.mockito.PowerMockito;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesMapBean.class })
public class TransactionRestServiceTest {

	@InjectMocks
	TransactionRestService transactionRestService;

	@Mock
	private TransactionService transactionService;

	@Mock
	DMaaPContext dmaapContext;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetAllTransactionObjs() throws CambriaApiException {

		transactionRestService.getAllTransactionObjs();
		assertTrue(true);

	}

	@Test
	public void testGetTransactionObj() throws CambriaApiException {

		transactionRestService.getTransactionObj("transactionId");
		assertTrue(true);

	}

	@Test
	public void testGetAllTransactionObjsError() throws CambriaApiException {

		try {
			PowerMockito.doThrow(new IOException()).when(transactionService).getAllTransactionObjs(dmaapContext);
		} catch (ConfigDbException | IOException e) {
			assertTrue(false);
		}

		try {
			transactionRestService.getAllTransactionObjs();
		} catch (CambriaApiException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testGetTransactionObjError() {

		try {
			PowerMockito.doThrow(new IOException()).when(transactionService).getTransactionObj(dmaapContext,
					"transactionId");
		} catch (ConfigDbException | JSONException | IOException e) {
			assertTrue(false);
		}

		try {
			transactionRestService.getTransactionObj("transactionId");
		} catch (CambriaApiException e) {
			assertTrue(true);
		}

	}

}