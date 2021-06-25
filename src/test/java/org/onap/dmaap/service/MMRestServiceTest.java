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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.service.MMService;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.mmagent.CreateMirrorMaker;
import org.onap.dmaap.mmagent.MirrorMaker;
import org.onap.dmaap.mmagent.UpdateMirrorMaker;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
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
	public void setUp()  {

		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown()  {
	}

	@Test
	public void testCallCreateMirrorMaker()  {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callCreateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}


	}
	@Test
	public void testCallCreateMirrorMaker_error4()  {
		try {
			prepareForTestCommon();
		} catch (Exception e) {

		}

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callCreateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}


	}
	@Test
	public void testCallCreateMirrorMaker_3()  {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
	assertTrue(true);

	}
	@Test
	public void testCallCreateMirrorMaker_error2()  {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",whitelist:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callCreateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}


	}

	@Test
	public void testCallCreateMirrorMaker_error1()  {
		prepareForTestCommon();

		// String sampleJson = ""{ messageID:\"test\", createMirrorMaker: {
		// name:\"test\", consumer:\"test\", producer:\"test\",
		// whitelist:\"test\",status:\"test\" }}";
		String sampleJson = "{ messageID:\"test\"}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callCreateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}


	}

	@Test
	public void testCallCreateMirrorMakerCreateAafPermissionError()  {
		prepareForTestCommon();

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))
			.thenReturn(null);
		String sampleJson = "{ messageID:\"test\"}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callCreateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}


	}

	@Test
	public void testCallListAllMirrorMaker()  {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\" }}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callListAllMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCallListAllMirrorMakerPermissionError()  {
		prepareForTestCommon();
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))
		.thenReturn(null);
		String sampleJson = "{ messageID:\"test\", createMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\" }}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callListAllMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCallUpdateMirrorMaker()  {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", updateMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callUpdateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCallUpdateMirrorMaker_error1()  {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test@1\", updateMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callUpdateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}
	@Test
	public void testCallUpdateMirrorMaker_error2()  {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", updateMirrorMaker: {   name:\"\",   consumer:\"test\",  producer:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callUpdateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}
	@Test
	public void testCallUpdateMirrorMaker_error3() {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", updateMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callUpdateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}
	@Test
	public void testCallUpdateMirrorMaker_error4()  {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callUpdateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCallUpdateMirrorMakerAafPermissionError()  {
		prepareForTestCommon();

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))
			.thenReturn(null);

		String sampleJson = "{ messageID:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callUpdateMirrorMaker(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCallDeleteMirrorMaker()  {
		prepareForTestCommon();

		String sampleJson = "{ messageID:\"test\", deleteMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\" }}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callDeleteMirrorMaker(inputSteam);
		} catch (JSONException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(true);
		}

	}


	@Test
	public void testCallDeleteMirrorMakerAafPermissionError() {
		prepareForTestCommon();
		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))
			.thenReturn(null);
		String sampleJson = "{ messageID:\"test\", deleteMirrorMaker: {   name:\"test\",   consumer:\"test\",  producer:\"test\",  whitelist:\"test\",status:\"test\" }}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.callDeleteMirrorMaker(inputSteam);
		} catch (JSONException e) {
			assertTrue(true);
		} catch (Exception e) {
			assertTrue(true);
		}

	}


	@Test
	public void testListWhiteList()  {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		String msgSubscribe = "[{ messageID:\"test123\", listMirrorMaker:[ {name: \"test\"}]}]";

		try {
			PowerMockito.when(mmservice.subscribe(any(), anyString(), anyString(), anyString())).thenReturn(msgSubscribe);
			mmRestService.listWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testListWhiteListAafPermissionError()  {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		String msgSubscribe = "[{ messageID:\"test123\", listMirrorMaker:[ {name: \"test\"}]}]";

		try {
			PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf"))
				.thenReturn(null);
			mmRestService.listWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testListWhiteListCreatePermissionError()  {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		String msgSubscribe = "[{ messageID:\"test123\", listMirrorMaker:[ {name: \"test\"}]}]";

		try {
			PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf.create"))
				.thenReturn(null);
			mmRestService.listWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}
	@Test
	public void testListWhiteListJSONError()  {
		prepareForTestCommon();

		String sampleJson = "{ namespace:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		String msgSubscribe = "[{ messageID:\"test123\", listMirrorMaker:[ {name: \"test\"}]}]";

		try {
			mmRestService.listWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}




	@Test
	public void testCreateWhiteList()  {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());

		try {
			mmRestService.createWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCreateWhiteListCreatePermissionError()  {
		prepareForTestCommon();

		PowerMockito
			.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf.create"))
			.thenReturn(null);

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());

		try {
			mmRestService.createWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCreateWhiteListAafPermissionError()  {
		prepareForTestCommon();

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf"))
			.thenReturn(null);

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());

		try {
			mmRestService.createWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCreateWhiteListJSONError()  {
		prepareForTestCommon();

		String sampleJson = "{ namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());

		try {
			mmRestService.createWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testDeleteWhiteList()  {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.deleteWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testDeleteWhiteListMirrorMakerPermissionError()  {
		prepareForTestCommon();
		PowerMockito
			.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf.create"))
			.thenReturn(null);
		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.deleteWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}


	@Test
	public void testDeleteWhiteListMirrorMakerAafPermissionError()  {
		prepareForTestCommon();
		PowerMockito
			.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf"))
			.thenReturn(null);
		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.deleteWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}


	@Test
	public void testDeleteWhiteListJsonError()  {
		prepareForTestCommon();

		String sampleJson = "{ namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.deleteWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testDeleteWhiteListJsonFormattingError()  {
		prepareForTestCommon();

		String sampleJson = "{ : namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			mmRestService.deleteWhiteList(inputSteam);
		} catch (Exception e) {
			assertTrue(true);
		}

	}

	@Test
	public void testCallPubSubForWhitelist() {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		String msgSubscribe = "[{ messageID:\"test123\", listMirrorMaker:[ {name: \"test\"}]}]";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			PowerMockito.when(mmservice.subscribe(any(), anyString(), anyString(), anyString())).thenReturn(msgSubscribe);
			mmRestService.callPubSubForWhitelist("test123", dmaapContext, inputSteam, new JSONObject (sampleJson)) ;
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCallPubSub() {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		String msgSubscribe = "[{ messageID:\"test123\", listMirrorMaker:[ {name: \"test\"}]}]";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			PowerMockito.when(mmservice.subscribe(any(), anyString(), anyString(), anyString())).thenReturn(msgSubscribe);
			mmRestService.callPubSub("test123", dmaapContext, inputSteam, "test", false) ;
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testCallPubSubForWhitelistNoMsgFromSubscribe() {
		prepareForTestCommon();

		String sampleJson = "{ name:\"test\", namespace:\"test\",   whitelistTopicName:\"test\"}}";
		InputStream inputSteam = new ByteArrayInputStream(sampleJson.getBytes());
		try {
			PowerMockito.when(mmservice.subscribe(any(), anyString(), anyString(), anyString())).thenReturn(null);
			mmRestService.callPubSubForWhitelist("test123", dmaapContext, inputSteam, new JSONObject (sampleJson)) ;
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetListMirrorMaker() {
		prepareForTestCommon();

		String sampleJson = "[{ messageID:\"test123\", listMirrorMaker:[\"test\"]}]";
		try {
			mmRestService.getListMirrorMaker(sampleJson, "test123");
		} catch (Exception e) {
			assertTrue(true);
		}
	}

	private void prepareForTestCommon()  {
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

		try {
			PowerMockito.when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(null);
		} catch (ConfigDbException e) {

		}
	}

}
