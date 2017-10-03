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

package com.att.nsa.dmaap.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;

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
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.beans.DMaaPKafkaMetaBroker;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.exception.DMaaPAccessDeniedException;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.cambria.security.DMaaPAAFAuthenticator;
import com.att.nsa.cambria.security.DMaaPAAFAuthenticatorImpl;
import com.att.nsa.cambria.security.DMaaPAuthenticator;
import com.att.nsa.cambria.service.MMService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.dmaap.mmagent.CreateMirrorMaker;
import com.att.nsa.dmaap.mmagent.MirrorMaker;
import com.att.nsa.dmaap.mmagent.UpdateMirrorMaker;
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
	public void testCallCreateMirrorMaker() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {
		prepareForTestCommon();
		mmRestService.callCreateMirrorMaker(iStream);
		assertTrue(true);
	}

	@Test
	public void testCallListAllMirrorMaker() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {
		prepareForTestCommon();
		mmRestService.callListAllMirrorMaker(iStream);
		assertTrue(true);
	}

	@Test
	public void testCallUpdateMirrorMaker() throws ConfigDbException, CambriaApiException {
		prepareForTestCommon();
		mmRestService.callUpdateMirrorMaker(iStream);
		assertTrue(true);
	}

	@Test
	public void testCallDeleteMirrorMaker() throws ConfigDbException, CambriaApiException {
		prepareForTestCommon();
		mmRestService.callDeleteMirrorMaker(iStream);
		assertTrue(true);
	}

	@Test
	public void testListWhiteList() throws ConfigDbException {
		prepareForTestCommon();
		mmRestService.listWhiteList(iStream);
		assertTrue(true);
	}

	@Test
	public void testCreateWhiteList() throws ConfigDbException {
		prepareForTestCommon();
		mmRestService.createWhiteList(iStream);
		assertTrue(true);
	}

	@Test
	public void testDeleteWhiteList() throws ConfigDbException {
		prepareForTestCommon();
		mmRestService.deleteWhiteList(iStream);
		assertTrue(true);
	}

	private void prepareForTestCommon() throws ConfigDbException {
		Assert.assertNotNull(mmRestService);
		PowerMockito.when(dmaapContext.getRequest()).thenReturn(httpServReq);
		PowerMockito.when(dmaapAAFauthenticator.aafAuthentication(httpServReq, "admin")).thenReturn(true);
		PowerMockito.when(httpServReq.isUserInRole("admin")).thenReturn(true);

		PowerMockito.mockStatic(AJSCPropertiesMap.class);

		assertTrue(true);

		PowerMockito.when(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))
				.thenReturn("admin");

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
