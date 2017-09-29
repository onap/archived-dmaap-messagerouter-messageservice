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

package com.att.nsa.dmaap.mmagent;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UpdateMirrorMakerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUpdateMirrorMaker() {

		UpdateMirrorMaker mMaker = new UpdateMirrorMaker();
		mMaker.getUpdateMirrorMaker();

		assertTrue(true);

	}
	
	@Test
	public void testSetUpdateMirrorMaker() {

		UpdateMirrorMaker mMaker = new UpdateMirrorMaker();
		mMaker.setUpdateMirrorMaker(new MirrorMaker());

		assertTrue(true);

	}
	
	@Test
	public void testGetMessageID() {

		UpdateMirrorMaker mMaker = new UpdateMirrorMaker();
		mMaker.getMessageID();

		assertTrue(true);

	}
	
	@Test
	public void testSetMessageID() {

		UpdateMirrorMaker mMaker = new UpdateMirrorMaker();
		mMaker.setMessageID("messageID");

		assertTrue(true);

	}



}