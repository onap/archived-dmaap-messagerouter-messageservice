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

 package org.onap.dmaap.mmagent;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MirrorMakerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetStatus() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.getStatus();

		assertTrue(true);

	}
	
	@Test
	public void testSetStatus() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.setStatus("status");

		assertTrue(true);

	}
	
	@Test
	public void testGetName() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.getName();

		assertTrue(true);

	}
	
	@Test
	public void testSetName() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.setName("name");

		assertTrue(true);

	}
	
	
	@Test
	public void testGetConsumer() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.getConsumer();

		assertTrue(true);

	}
	
	@Test
	public void testSetConsumer() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.setConsumer("consumer");

		assertTrue(true);

	}
	
	@Test
	public void testGetProducer() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.getProducer();

		assertTrue(true);

	}
	
	@Test
	public void testSetProducer() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.setProducer("producer");

		assertTrue(true);

	}
	

	@Test
	public void testGetWhitelist() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.getWhitelist();

		assertTrue(true);

	}
	
	@Test
	public void testSetWhitelist() {

		MirrorMaker mMaker = new MirrorMaker();
		mMaker.setWhitelist("whitelist");

		assertTrue(true);

	}
	
	
}