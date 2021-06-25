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

import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.service.AdminService;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

@RunWith(MockitoJUnitRunner.class)
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
	Enumeration headerNames;

	@Test
	public void testGetConsumerCache() throws CambriaApiException, AccessDeniedException {
		adminRestService.getConsumerCache();

	}

	@Test(expected=CambriaApiException.class)
	public void testGetConsumerCache_IOException() throws CambriaApiException, AccessDeniedException, IOException {
		doThrow(new IOException("error")).when(adminService).showConsumerCache(ArgumentMatchers.any(DMaaPContext.class));

		adminRestService.getConsumerCache();
		fail("Was expecting an exception to be thrown");
	}

	@Test
	public void testDropConsumerCache() throws CambriaApiException, AccessDeniedException {
		adminRestService.dropConsumerCache();
	}

	@Test(expected=CambriaApiException.class)
	public void testDropConsumerCach_IOException() throws CambriaApiException, AccessDeniedException ,IOException{
		doThrow(new IOException("error")).when(adminService).dropConsumerCache(ArgumentMatchers.any(DMaaPContext.class));

		adminRestService.dropConsumerCache();
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testDropConsumerCache_AccessDeniedException() throws CambriaApiException, AccessDeniedException,IOException {
		doThrow(new AccessDeniedException("error")).when(adminService).dropConsumerCache(ArgumentMatchers.any(DMaaPContext.class));

		adminRestService.dropConsumerCache();
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testDropConsumerCache_JSONException() throws CambriaApiException, AccessDeniedException,IOException {
		doThrow(new JSONException("error")).when(adminService).dropConsumerCache(ArgumentMatchers.any(DMaaPContext.class));

		adminRestService.dropConsumerCache();
		fail("Was expecting an exception to be thrown");
	}

	@Test
	public void testGetBlacklist() throws CambriaApiException {
		Vector headers = new Vector();
		headers.add("Content-type");
		Enumeration headerNms = headers.elements();
		when(httpServReq.getHeaderNames()).thenReturn(headerNms);

		adminRestService.getBlacklist();
	}

	@Test(expected=CambriaApiException.class)
	public void testGetBlacklist_AccessDeniedException() throws CambriaApiException, AccessDeniedException, IOException {

		when(httpServReq.getHeaderNames()).thenReturn(headerNames);
		doThrow(new AccessDeniedException("error")).when(adminService).getBlacklist(ArgumentMatchers.any(DMaaPContext.class));

		adminRestService.getBlacklist();
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testGetBlacklist_IOException() throws CambriaApiException, AccessDeniedException,IOException {
		when(httpServReq.getHeaderNames()).thenReturn(headerNames);
		doThrow(new IOException("error")).when(adminService).getBlacklist(ArgumentMatchers.any(DMaaPContext.class));

		adminRestService.getBlacklist();
		fail("Was expecting an exception to be thrown");
	}

	@Test
	public void testAddToBlacklist() throws CambriaApiException, AccessDeniedException {

		adminRestService.addToBlacklist("120.120.120.120");

	}

	@Test(expected=CambriaApiException.class)
	public void testAddToBlacklist_IOException() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException {
		doThrow(new IOException("error")).when(adminService).addToBlacklist(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		adminRestService.addToBlacklist("120.120.120.120");
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testAddToBlacklist_AccessDeniedException() throws CambriaApiException, AccessDeniedException,IOException, ConfigDbException {
		doThrow(new AccessDeniedException("error")).when(adminService).addToBlacklist(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		adminRestService.addToBlacklist("120.120.120.120");
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testAddToBlacklist_ConfigDbException() throws CambriaApiException, AccessDeniedException,IOException, ConfigDbException {
		doThrow(new ConfigDbException("error")).when(adminService).addToBlacklist(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		adminRestService.addToBlacklist("120.120.120.120");
		fail("Was expecting an exception to be thrown");
	}

	@Test
	public void testRemoveFromBlacklist() throws CambriaApiException, AccessDeniedException, ConfigDbException {

		adminRestService.removeFromBlacklist("120.120.120.120");

	}

	@Test(expected=CambriaApiException.class)
	public void testRemoveFromBlacklist_ConfigDbException() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException{
		doThrow(new ConfigDbException("error")).when(adminService).removeFromBlacklist(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		adminRestService.removeFromBlacklist("120.120.120.120");
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testRemoveFromBlacklist_IOException() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException{
		doThrow(new IOException("error")).when(adminService).removeFromBlacklist(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		adminRestService.removeFromBlacklist("120.120.120.120");
		fail("Was expecting an exception to be thrown");
	}

	@Test(expected=CambriaApiException.class)
	public void testRemoveFromBlacklist_AccessDeniedException() throws CambriaApiException, AccessDeniedException, ConfigDbException,IOException {
		doThrow(new AccessDeniedException("error")).when(adminService).removeFromBlacklist(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class));

		adminRestService.removeFromBlacklist("120.120.120.120");
		fail("Was expecting an exception to be thrown");
	}

}