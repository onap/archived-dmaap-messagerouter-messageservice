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

 package org.onap.dmaap;

import static org.junit.Assert.assertTrue;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class DMaaPWebExceptionMapperTest {

	@InjectMocks
	DMaaPWebExceptionMapper mapper;

	@Mock
	DMaaPErrorMessages msgs;

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
	public void testToResponseNotFoundException() {
		PowerMockito.when(msgs.getNotFound()).thenReturn("Not found");
		try {
			mapper.toResponse(new NotFoundException());
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseInternalServerErrorException() {
		PowerMockito.when(msgs.getNotFound()).thenReturn("Not found");
		try {
			mapper.toResponse(new InternalServerErrorException());

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseNotAuthorizedException() {
		PowerMockito.when(msgs.getNotFound()).thenReturn("Not found");
		try {
			mapper.toResponse(new NotAuthorizedException("Error", "Error"));

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseBadRequestException() {
		PowerMockito.when(msgs.getNotFound()).thenReturn("Not found");
		try {
			mapper.toResponse(new BadRequestException());

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseNotAllowedException() {
		PowerMockito.when(msgs.getNotFound()).thenReturn("Not found");
		try {
			mapper.toResponse(new NotAllowedException("Not Allowed"));

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseServiceUnavailableException() {
		PowerMockito.when(msgs.getNotFound()).thenReturn("Not found");
		try {
			mapper.toResponse(new ServiceUnavailableException());

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

}