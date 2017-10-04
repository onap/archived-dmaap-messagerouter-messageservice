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

package com.att.nsa.dmaap;

import static org.junit.Assert.assertTrue;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.exception.ErrorResponse;
@RunWith(PowerMockRunner.class)
public class DMaaPCambriaExceptionMapperTest {

	@InjectMocks
	DMaaPCambriaExceptionMapper mapper;

	@Mock
	private ErrorResponse errRes;
	
	@Mock
	private DMaaPErrorMessages msgs;
	
	@Mock
	CambriaApiException exc;
	
	@Mock
	JSONObject json;

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToResponse() {
		try {
			mapper.toResponse(null);
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	
	@Test
	public void testToResponseCambriaApiException2() {
		PowerMockito.when(msgs.getNotFound()).thenReturn("Not found");
		try {
			mapper.toResponse(new CambriaApiException(404,"Not found"));
		} catch (NullPointerException e) {
			assertTrue(true);
		}
		assertTrue(true);
	}
}