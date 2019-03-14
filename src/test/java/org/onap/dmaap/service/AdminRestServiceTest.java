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
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.any;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.service.AdminService;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ServiceUtil.class })
public class AdminRestServiceTest {

	@InjectMocks
	AdminRestService adminRestService;

	@Mock
	AdminService adminService;

	@Mock
	DMaaPContext dmaapContext;

	@Mock
	HttpServletRequest httpServReq;
	@Mock
	private HttpServletResponse response;

	@Mock
	Enumeration headerNames;
	@Mock
	private DMaaPContext dmaaPContext;
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
	public void testGetConsumerCache() throws CambriaApiException, AccessDeniedException {
		adminRestService.getConsumerCache();

	}

	@Test
	public void testGetConsumerCache_error() throws CambriaApiException, AccessDeniedException, IOException {

		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaaPContext);
		PowerMockito.doThrow(new IOException("error")).when(adminService).showConsumerCache(dmaaPContext);
		try {
			adminRestService.getConsumerCache();
		} catch (CambriaApiException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testDropConsumerCache() throws CambriaApiException, AccessDeniedException {
		adminRestService.dropConsumerCache();

	}
	
	@Test
	public void testDropConsumerCach_error() throws CambriaApiException, AccessDeniedException ,IOException{
		
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaaPContext);
		PowerMockito.doThrow(new IOException("error")).when(adminService).dropConsumerCache(dmaaPContext);
		try {
		adminRestService.dropConsumerCache();
		}
		catch (CambriaApiException e) {
			assertTrue(true);
		}
		

	}
	@Test
	public void testDropConsumerCach_error1() throws CambriaApiException, AccessDeniedException,IOException {
		
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaaPContext);
		PowerMockito.doThrow(new AccessDeniedException("error")).when(adminService).dropConsumerCache(dmaaPContext);
		try {
		adminRestService.dropConsumerCache();
		}
		catch (CambriaApiException e) {
			assertTrue(true);
		}
		

	}

	@Test
	public void testGetBlacklist() throws CambriaApiException, AccessDeniedException {
		Vector headers = new Vector();
		headers.add("Content-type");
		Enumeration headerNms = headers.elements();

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeaderNames()).thenReturn(headerNms);
		when(headerNames.nextElement()).thenReturn("key");
		when(httpServReq.getHeader("key")).thenReturn("value");

		adminRestService.getBlacklist();

	}
	
	@Test
	public void testGetBlacklist_error() throws CambriaApiException, AccessDeniedException,IOException {
		
		PowerMockito.doThrow(new IOException("error")).when(adminService).getBlacklist(any(DMaaPContext.class));
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeaderNames()).thenReturn(headerNames);
		when(headerNames.nextElement()).thenReturn("key");
		when(httpServReq.getHeader("key")).thenReturn("value");

		try {
		adminRestService.getBlacklist();
		}
		catch (CambriaApiException e) {
			assertTrue(true);
		}
	}
	
	@Test
	public void testGetBlacklist_error1() throws CambriaApiException, AccessDeniedException,IOException {
		
		PowerMockito.doThrow(new AccessDeniedException("error")).when(adminService).getBlacklist(any(DMaaPContext.class));
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeaderNames()).thenReturn(headerNames);
		when(headerNames.nextElement()).thenReturn("key");
		when(httpServReq.getHeader("key")).thenReturn("value");

		try {
		adminRestService.getBlacklist();
		}
		catch (CambriaApiException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testAddToBlacklist() throws CambriaApiException, AccessDeniedException {

		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		adminRestService.addToBlacklist("120.120.120.120");

	}
	
	@Test
	public void testAddToBlacklist_error() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException {
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaaPContext);
		PowerMockito.doThrow(new AccessDeniedException("error")).when(adminService).addToBlacklist(dmaaPContext,"120.120.120.120");

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		try {
		adminRestService.addToBlacklist("120.120.120.120");
		}catch (CambriaApiException e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void testAddToBlacklist_error1() throws CambriaApiException, AccessDeniedException,IOException, ConfigDbException {
		
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaaPContext);
		PowerMockito.doThrow(new IOException("error")).when(adminService).addToBlacklist(dmaaPContext,"120.120.120.120");

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		try {
		adminRestService.addToBlacklist("120.120.120.120");
		}catch (CambriaApiException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testRemoveFromBlacklist() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException {

		when(dmaapContext.getRequest()).thenReturn(httpServReq);

		adminRestService.removeFromBlacklist("120.120.120.120");

	}
	
	@Test
	public void testRemoveFromBlacklist_error() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException{
		
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaaPContext);
		PowerMockito.doThrow(new IOException("error")).when(adminService).removeFromBlacklist(dmaaPContext,"120.120.120.120");


		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		try {

		adminRestService.removeFromBlacklist("120.120.120.120");
		}catch (CambriaApiException e) {
			assertTrue(true);
		}
		catch (AccessDeniedException e) {
			assertTrue(true);
		}
		catch (ConfigDbException e) {
			assertTrue(true);
		}

	}
	
	@Test
	public void testRemoveFromBlacklist_error1() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException {
		
		PowerMockito.mockStatic(ServiceUtil.class);
		PowerMockito.when(ServiceUtil.getDMaaPContext(configReader, httpServReq, response)).thenReturn(dmaaPContext);
		PowerMockito.doThrow(new AccessDeniedException("error")).when(adminService).removeFromBlacklist(dmaaPContext,"120.120.120.120");


		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		try {

		adminRestService.removeFromBlacklist("120.120.120.120");
		}catch (CambriaApiException e) {
			assertTrue(true);
		}
		catch (AccessDeniedException e) {
			assertTrue(true);
		}
		catch (ConfigDbException e) {
			assertTrue(true);
		}

	}

}