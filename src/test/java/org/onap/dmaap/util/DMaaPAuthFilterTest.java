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

 package org.onap.dmaap.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import javax.security.cert.X509Certificate;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


@RunWith(MockitoJUnitRunner.class)
public class DMaaPAuthFilterTest {

	@Spy
	private DMaaPAuthFilter filter;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;

	@Mock
	private FilterChain chain;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	public void doFilter_shouldNotUseCadiFilter_whenCadiNotEnabled() throws IOException, ServletException {
		//given
		when(filter.isCadiEnabled()).thenReturn(false);

		//when
		filter.doFilter(request, response, chain);

		//then
		verify(chain).doFilter(request, response);
	}

	@Test
	public void shouldFilterWithCADI_willBeFalse_whenCadiEnabled_noAuthData_affNotForced_notInvenioApp() {
		//given
		configureSettingsFlags(false);

		//when
		boolean filteringWithCADI = filter.shouldFilterWithCADI(request);

		//then
		assertFalse(filteringWithCADI);
	}

	@Test
	public void shouldFilterWithCADI_willBeTrue_whenCadiEnabled_andAAFforcedFlagSet() {
		//given
		configureSettingsFlags(true);

		//when
		boolean filteringWithCADI = filter.shouldFilterWithCADI(request);

		//then
		assertTrue(filteringWithCADI);
	}

	@Test
	public void shouldFilterWithCADI_willBeTrue_whenCadiEnabled_andBasicAuthorization() {
		//given
		configureSettingsFlags(false);
		request.addHeader(DMaaPAuthFilter.AUTH_HEADER, Base64.encodeBase64("user/pass".getBytes()));

		//when
		boolean filteringWithCADI = filter.shouldFilterWithCADI(request);

		//then
		assertTrue(filteringWithCADI);
	}

	@Test
	public void shouldFilterWithCADI_willBeTrue_whenCadiEnabled_andClientCertificate() {
		//given
		configureSettingsFlags(false);
		request.setAttribute(DMaaPAuthFilter.X509_ATTR, new X509Certificate[]{});

		//when
		boolean filteringWithCADI = filter.shouldFilterWithCADI(request);

		//then
		assertTrue(filteringWithCADI);
	}

	@Test
	public void shouldFilterWithCADI_willBeTrue_whenCadiEnabled_andInvenioAppWithCookie() {
		//given
		configureSettingsFlags(false);
		request.addHeader(DMaaPAuthFilter.APP_HEADER, "invenio");
		request.addHeader(DMaaPAuthFilter.COOKIE_HEADER, "value");

		//when
		boolean filteringWithCADI = filter.shouldFilterWithCADI(request);

		//then
		assertTrue(filteringWithCADI);
	}

	@Test
	public void shouldFilterWithCADI_willBeFalse_whenCadiEnabled_andInvenioAppWithoutCookie() {
		//given
		configureSettingsFlags(false);
		request.addHeader(DMaaPAuthFilter.APP_HEADER, "invenio");

		//when
		boolean filteringWithCADI = filter.shouldFilterWithCADI(request);

		//then
		assertFalse(filteringWithCADI);
	}

	@Test
	public void shouldFilterWithCADI_willBeFalse_whenCadiEnabled_andNotInvenioApp() {
		//given
		configureSettingsFlags(false);
		request.addHeader(DMaaPAuthFilter.APP_HEADER, "application");

		//when
		boolean filteringWithCADI = filter.shouldFilterWithCADI(request);

		//then
		assertFalse(filteringWithCADI);
	}

	private void configureSettingsFlags(boolean isAAFforced) {
		when(filter.isCadiEnabled()).thenReturn(true);
		when(filter.isAAFforced()).thenReturn(isAAFforced);
	}
	
}