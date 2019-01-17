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

import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class UpdateMirrorMakerTest {

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	MirrorMaker mirrorMaker;
	UpdateMirrorMaker updateMirrorMaker;
	JSONObject jsonObject;

	@Before
	public void setUp() throws Exception {
		mirrorMaker = new MirrorMaker();
		updateMirrorMaker = new UpdateMirrorMaker();
		jsonObject = PowerMockito.mock(JSONObject.class);

		mirrorMaker.setConsumer("test");
		PowerMockito.when(jsonObject.has("consumer")).thenReturn(true);

		mirrorMaker.setProducer("test");
		PowerMockito.when(jsonObject.has("producer")).thenReturn(true);

		mirrorMaker.setNumStreams(1);
		PowerMockito.when(jsonObject.has("numStreams")).thenReturn(true);

		PowerMockito.when(jsonObject.has("whitelist")).thenReturn(true);
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


	@Test
	public void testValidateJSONNullConsumer() throws CambriaApiException {
		exceptionRule.expect(CambriaApiException.class);
		exceptionRule.expectMessage("Please provide Consumer host:port details");

		mirrorMaker.setConsumer(null);
		updateMirrorMaker.setUpdateMirrorMaker(mirrorMaker);
		updateMirrorMaker.validateJSON(jsonObject);
	}

	@Test
	public void testValidateJSONNullProducer() throws CambriaApiException {
		exceptionRule.expect(CambriaApiException.class);
		exceptionRule.expectMessage("Please provide Producer host:port details");

		mirrorMaker.setProducer(null);
		updateMirrorMaker.setUpdateMirrorMaker(mirrorMaker);
		updateMirrorMaker.validateJSON(jsonObject);
	}

	@Test
	public void testValidateJSONNoNumStreams() throws CambriaApiException {
		exceptionRule.expect(CambriaApiException.class);
		exceptionRule.expectMessage("Please provide numStreams value");

		mirrorMaker.setNumStreams(0);
		updateMirrorMaker.setUpdateMirrorMaker(mirrorMaker);
		updateMirrorMaker.validateJSON(jsonObject);
	}

	@Test
	public void testValidateJSONWhitelist() throws CambriaApiException {
		exceptionRule.expect(CambriaApiException.class);
		exceptionRule.expectMessage("Please use Create Whitelist API to add whitelist topics");

		PowerMockito.when(jsonObject.has("whitelist")).thenReturn(true);

		updateMirrorMaker.setUpdateMirrorMaker(mirrorMaker);
		updateMirrorMaker.validateJSON(jsonObject);
	}
}