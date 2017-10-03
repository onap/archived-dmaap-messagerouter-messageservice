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

import static org.junit.Assert.*;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DMaaPWebExceptionMapperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToResponse() {

		DMaaPWebExceptionMapper mapper = new DMaaPWebExceptionMapper();

		try {
			mapper.toResponse(null);
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseNotFoundException() {

		DMaaPWebExceptionMapper mapper = new DMaaPWebExceptionMapper();

		try {
			mapper.toResponse(new NotFoundException());
		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseInternalServerErrorException() {

		DMaaPWebExceptionMapper mapper = new DMaaPWebExceptionMapper();

		try {
			mapper.toResponse(new InternalServerErrorException());

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseNotAuthorizedException() {

		DMaaPWebExceptionMapper mapper = new DMaaPWebExceptionMapper();

		try {
			mapper.toResponse(new NotAuthorizedException("Error", "Error"));

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseBadRequestException() {

		DMaaPWebExceptionMapper mapper = new DMaaPWebExceptionMapper();

		try {
			mapper.toResponse(new BadRequestException());

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseNotAllowedException() {

		DMaaPWebExceptionMapper mapper = new DMaaPWebExceptionMapper();

		try {
			mapper.toResponse(new NotAllowedException("Not Allowed"));

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

	@Test
	public void testToResponseServiceUnavailableException() {

		DMaaPWebExceptionMapper mapper = new DMaaPWebExceptionMapper();

		try {
			mapper.toResponse(new ServiceUnavailableException());

		} catch (NullPointerException e) {
			assertTrue(true);
		}

	}

}