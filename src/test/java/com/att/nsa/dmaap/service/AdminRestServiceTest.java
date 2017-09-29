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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/*import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.att.nsa.cambria.CambriaApiException;
import static org.mockito.Mockito.when;
*/
import javax.servlet.http.HttpServletRequest;

import com.att.nsa.cambria.beans.DMaaPContext;
import java.util.Enumeration;
import com.att.nsa.cambria.service.AdminService;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

public class AdminRestServiceTest {/*

	@InjectMocks
	AdminRestService adminRestService;

	@Mock
	AdminService adminService;

	@Mock
	DMaaPContext dmaapContext;

	@Mock
	HttpServletRequest httpServReq;

	@Mock
	Enumeration headerNames;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetConsumerCache() throws CambriaApiException, AccessDeniedException {
		adminRestService.getConsumerCache();

	}

	@Test
	public void testDropConsumerCache() throws CambriaApiException, AccessDeniedException {
		adminRestService.dropConsumerCache();

	}

	@Test
	public void testGetBlacklist() throws CambriaApiException, AccessDeniedException {

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeaderNames()).thenReturn(headerNames);
		when(headerNames.nextElement()).thenReturn("key");
		when(httpServReq.getHeader("key")).thenReturn("value");

		adminRestService.getBlacklist();

	}

	@Test
	public void testAddToBlacklist() throws CambriaApiException, AccessDeniedException {

		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		adminRestService.addToBlacklist("120.120.120.120");

	}
	
	@Test
	public void testRemoveFromBlacklist() throws CambriaApiException, AccessDeniedException, ConfigDbException {

		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		adminRestService.removeFromBlacklist("120.120.120.120");

	}

*/}