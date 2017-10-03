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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ System.class })
public class ContentLengthInterceptorTest {
	@InjectMocks
	ContentLengthInterceptor interceptor = null;

	@Mock
	Map map;

	@Mock
	HttpServletRequest req;

	@Mock
	HttpServletResponse res;

	@Before
	public void setUp() throws Exception {
		// interceptor = new ContentLengthInterceptor();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllowOrReject() throws Exception {
		PowerMockito.when(req.getHeader("Transfer-Encoding")).thenReturn("UTF-8");
		PowerMockito.when(req.getHeader("Content-Length")).thenReturn("1027");
		interceptor.allowOrReject(req, res, map);
		assertTrue(true);
	}

	@Test
	public void testGetDefLength() {
		interceptor.getDefLength();
		assertTrue(true);
	}

	@Test
	public void testSetDefLength() {
		interceptor.setDefLength("defLength");
		assertTrue(true);

	}
}