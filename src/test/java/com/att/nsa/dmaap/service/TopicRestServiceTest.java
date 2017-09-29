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

import static org.junit.Assert.*;

/*import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;*/

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
/*import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;*/

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.beans.DMaaPKafkaMetaBroker;
import com.att.nsa.cambria.beans.TopicBean;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.exception.DMaaPAccessDeniedException;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.cambria.metabroker.Topic;
import com.att.nsa.cambria.security.DMaaPAAFAuthenticator;
import com.att.nsa.cambria.security.DMaaPAuthenticator;
import com.att.nsa.cambria.service.TopicService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;

//@RunWith(MockitoJUnitRunner.class)
/*@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropertiesMapBean.class })*/
public class TopicRestServiceTest {/*

	@InjectMocks
	TopicRestService topicService;

	@Mock
	private TopicService tService;

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
	DMaaPAAFAuthenticator dmaapAAFauthenticator;
	@Mock
	NsaApiKey user;

	@Mock
	NsaSimpleApiKey nsaSimpleApiKey;

	@Mock
	HttpServletRequest httpServReq;

	@Mock
	HttpServletResponse httpServRes;

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

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testGetTopics() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.namespace.aaf"))
				.thenReturn("namespace");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");

		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		when(configReader.getfMetaBroker()).thenReturn(dmaapKafkaMetaBroker);
		when(httpServReq.getMethod()).thenReturn("HEAD");

		when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(null);

		topicService.getTopics();
	}

	@Test
	public void testGetTopics_nullAuth() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.namespace.aaf"))
				.thenReturn("namespace");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn(null);

		when(dmaapContext.getResponse()).thenReturn(httpServRes);
		String perms = "namespace" + "|" + "*" + "|" + "view";
		when(dmaapAAFauthenticator.aafAuthentication(httpServReq, perms)).thenReturn(true);

		when(dmaapKafkaMetaBroker.getTopic(anyString())).thenReturn(null);

		topicService.getTopics();
	}

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testGetAllTopics() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.namespace.aaf"))
				.thenReturn("namespace");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicService.getAllTopics();
	}

	@Test
	public void testGetAllTopics_nullAuth() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);
		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.namespace.aaf"))
				.thenReturn("namespace");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn(null);

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicService.getAllTopics();
	}

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testGetTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicService.getTopic("topicName");
	}

	@Test
	public void testGetTopic_nullAuth() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn(null);

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicService.getTopic("topicName");
	}

	@Test
	public void testCreateTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.createTopic(topicBean);
	}

	@Test
	public void testDeleteTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.deleteTopic("enfTopicNamePlusExtra");
	}

	@Test
	public void testGetPublishersByTopicName()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.getPublishersByTopicName("enfTopicNamePlusExtra");
	}

	@Test
	public void testPermitPublisherForTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.permitPublisherForTopic("enfTopicNamePlusExtra", "producerID");
	}

	@Test
	public void testDenyPublisherForTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.denyPublisherForTopic("enfTopicNamePlusExtra", "producerID");
	}

	@Test
	public void testGetConsumersByTopicName() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.getConsumersByTopicName("enfTopicNamePlusExtra");
	}

	@Test
	public void testPermitConsumerForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.permitConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
	}

	@Test
	public void testDenyConsumerForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicService.denyConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
	}

*/}
