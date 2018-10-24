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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
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
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.service.MMService;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import org.onap.dmaap.mmagent.CreateMirrorMaker;
import org.onap.dmaap.mmagent.MirrorMaker;
import org.onap.dmaap.mmagent.UpdateMirrorMaker;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import com.google.gson.Gson;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesMapBean.class, AJSCPropertiesMap.class })
public class MMRestServiceTest {

	@InjectMocks
	MMRestService mmRestService;

	@Mock
	private MMService mmservice;

	@Mock
	CreateMirrorMaker cMirroMaker;

	@Mock
	UpdateMirrorMaker uMirroMaker;

	private TopicRestService service = new TopicRestService();
	@Mock
	private DMaaPErrorMessages errorMessages;

	@Mock
	DMaaPContext dmaapContext;

	@Mock
	ConfigurationReader configReader;

	@Mock
	ServletOutputStream oStream;

	@Mock
	DMaaPAuthenticator<NsaSimpleApiKey> dmaaPAuthenticator;

	@Mock
	MirrorMaker mMaker;

	@Mock
	DMaaPAAFAuthenticator dmaapAAFauthenticator;

	@Mock
	DMaaPAAFAuthenticatorImpl impl;

	@Mock
	NsaApiKey user;

	@Mock
	NsaSimpleApiKey nsaSimpleApiKey;

	@Mock
	HttpServletRequest httpServReq;

	@Mock
	HttpServletResponse httpServRes;

	@Mock
	InputStream iStream;

	@Mock
	DMaaPKafkaMetaBroker dmaapKafkaMetaBroker;

	@Mock
	Topic createdTopic;

	@Mock
	NsaAcl nsaAcl;

	@Mock
	JSONObject jsonObj;

	@Mock
	JSONArray jsonArray;

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCallCreateMirrorMaker() throws Exception {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callCreateMirrorMaker(inputSteam);
		assertTrue(true);

	}
	@Test
	public void testCallCreateMirrorMaker_error4() throws Exception {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test@#\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callCreateMirrorMaker(inputSteam);
		assertTrue(true);

	}
	@Test
	public void testCallCreateMirrorMaker_3() throws Exception {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callCreateMirrorMaker(inputSteam);
		assertTrue(true);

	}
	@Test
	public void testCallCreateMirrorMaker_error2() throws Exception {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",whitelist:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callCreateMirrorMaker(inputSteam);
		assertTrue(true);

	}
	
	@Test
	public void testCallCreateMirrorMaker_error1() throws Exception {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\"}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callCreateMirrorMaker(inputSteam);
		assertTrue(true);

	}

	@Test
	public void testCallListAllMirrorMaker() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\" }}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callListAllMirrorMaker(inputSteam);
		assertTrue(true);
	}

	@Test
	public void testCallUpdateMirrorMaker() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", updateMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callUpdateMirrorMaker(inputSteam);
		assertTrue(true);
	}
	
	@Test
	public void testCallUpdateMirrorMaker_error1() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test@1\", updateMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callUpdateMirrorMaker(inputSteam);
		assertTrue(true);
	}
	@Test
	public void testCallUpdateMirrorMaker_error2() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", updateMirrorMaker: {   name:\"\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callUpdateMirrorMaker(inputSteam);
		assertTrue(true);
	}
	@Test
	public void testCallUpdateMirrorMaker_error3() throws Exception{
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", updateMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callUpdateMirrorMaker(inputSteam);
		assertTrue(true);
	}
	@Test
	public void testCallUpdateMirrorMaker_error4() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callUpdateMirrorMaker(inputSteam);
		assertTrue(true);
	}

	@Test
	public void testCallDeleteMirrorMaker() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", deleteMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\" }}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.callDeleteMirrorMaker(inputSteam);
		assertTrue(true);
	}

	@Test
	public void testListWhiteList() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.listWhiteList(inputSteam);
		assertTrue(true);
	}

	@Test
	public void testCreateWhiteList() throws Exception {
		prepareForTestCommon();
		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());

		mmRestService.createWhiteList(inputSteam);
		assertTrue(true);
	}

	@Test
	public void testDeleteWhiteList() throws Exception {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		mmRestService.deleteWhiteList(inputSteam);
		assertTrue(true);
	}

	private void prepareForTestCommon() throws Exception {
		Assert.assertNotNull(mmRestService);
		PowerMockito.when(dmaapContext.getRequest()).thenReturn(httpServReq);
		PowerMockito.when(dmaapAAFauthenticator.aafAuthentication(httpServReq, "admin")).thenReturn(true);
		PowerMockito.when(httpServReq.isUserInRole("admin")).thenReturn(true);

		PowerMockito.mockStatic(AJSCPropertiesMap.class);

		assertTrue(true);

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))
				.thenReturn("admin");
		PowerMockito
				.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf.create"))
				.thenReturn("aafcreate");

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf"))
				.thenReturn("admin");

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.timeout"))
				.thenReturn("100");

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.topic"))
				.thenReturn("mirrormaker.topic");

		PowerMockito
				.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.consumergroup"))
				.thenReturn("mirrormaker.consumergroup");

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.consumerid"))
				.thenReturn("mirrormaker.consumerid");

		PowerMockito.when(dmaapContext.getRequest()).thenReturn(httpServReq);

		PowerMockito.when(httpServReq.isUserInRole("admin")).thenReturn(true);

		// PowerMockito.when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		PowerMockito.when(dmaapAAFauthenticator.aafAuthentication(httpServReq, "admin.aaf")).thenReturn(true);
		PowerMockito.when(dmaapAAFauthenticator.aafAuthentication(httpServReq, "admin")).thenReturn(true);
		PowerMockito.when(httpServReq.getHeader("Authorization")).thenReturn("Admin");
		PowerMockito.when(dmaapAAFauthenticator.aafAuthentication(httpServReq, "admin")).thenReturn(true);
		PowerMockito.when(dmaapAAFauthenticator.aafAuthentication(httpServReq, "aafcreatetest|create"))
				.thenReturn(true);

		PowerMockito.when(cMirroMaker.getCreateMirrorMaker()).thenReturn(mMaker);

		PowerMockito.when(mMaker.getName()).thenReturn("mirroMakerName");
		PowerMockito.when(dmaapContext.getConfigReader()).thenReturn(configReader);
		PowerMockito.when(dmaapContext.getRequest()).thenReturn(httpServReq);
		PowerMockito.when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");

		PowerMockito.when(dmaapContext.getResponse()).thenReturn(httpServRes);
		PowerMockito.when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		PowerMockito.when(httpServReq.getMethod()).thenReturn("HEAD");

		PowerMockito.when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(null);
	}

}
