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

package com.att.nsa.dmaap.util;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.exception.DMaaPResponseCode;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesMapBean.class, DMaaPResponseCode.class })
public class DMaaPAuthFilterTest {

	@InjectMocks
	DMaaPAuthFilter filter;

	@Mock
	HttpServletRequest req;

	@Mock
	ServletResponse res;

	@Mock
	FilterChain chain;

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
	public void testDoFilter() throws IOException, ServletException {

		PowerMockito.when(dmaapContext.getRequest()).thenReturn(req);
		PowerMockito.when(req.getHeader("Authorization")).thenReturn("Authorization");
		// when(dmaapContext.getResponse()).thenReturn(res);
		filter.doFilter(req, res, chain);

	}

	@Test
	public void testDoFilter_nullAuth() throws IOException, ServletException {

		PowerMockito.when(dmaapContext.getRequest()).thenReturn(req);
		PowerMockito.when(req.getHeader("Authorization")).thenReturn("Authorization");

		// when(dmaapContext.getResponse()).thenReturn(res);
		filter.doFilter(req, res, chain);

	}

}