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
import static org.mockito.Mockito.when;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.att.nsa.security.db.simple.NsaSimpleApiKey;
import java.io.IOException;
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
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.beans.TopicBean;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticator;
import org.onap.dmaap.dmf.mr.service.TopicService;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//@RunWith(MockitoJUnitRunner.class)
@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest({ PropertiesMapBean.class, DMaaPResponseBuilder.class })
public class TopicRestServiceTest {

	@InjectMocks
	TopicRestService topicRestService;

	@Mock
	private TopicService topicService;

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

		Assert.assertNotNull(topicRestService);

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

		topicRestService.getTopics();
	}

	@Test
	public void testGetTopics_nullAuth() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicRestService);

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

		topicRestService.getTopics();
	}

	@Test
	public void testGetTopics_error() throws DMaaPAccessDeniedException, TopicExistsException, ConfigDbException {

		Assert.assertNotNull(topicRestService);

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
		try {
			PowerMockito.doThrow(new IOException()).when(topicService).getTopics(any());
		} catch (JSONException | ConfigDbException | IOException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.getTopics();
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testGetAllTopics() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicRestService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.namespace.aaf"))
				.thenReturn("namespace");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicRestService.getAllTopics();
	}

	@Test
	public void testGetAllTopics_nullAuth() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicRestService);
		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.namespace.aaf"))
				.thenReturn("namespace");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn(null);

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicRestService.getAllTopics();
	}

	@Test
	public void testGetAllTopics_error() throws DMaaPAccessDeniedException, TopicExistsException, ConfigDbException {

		Assert.assertNotNull(topicRestService);
		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.namespace.aaf"))
				.thenReturn("namespace");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn(null);

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).getAllTopics(any());
		} catch (JSONException | ConfigDbException | IOException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.getAllTopics();
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test(expected = DMaaPAccessDeniedException.class)
	public void testGetTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicRestService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn("Authorization");

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicRestService.getTopic("topicName");
	}

	@Test
	public void testGetTopic_nullAuth() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, JSONException, ConfigDbException {

		Assert.assertNotNull(topicRestService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn(null);

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		topicRestService.getTopic("topicName");
	}

	@Test
	public void testGetTopic_error() throws DMaaPAccessDeniedException, ConfigDbException {

		Assert.assertNotNull(topicRestService);

		PowerMockito.mockStatic(PropertiesMapBean.class);

		assertTrue(true);
		when(PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, "enforced.topic.name.AAF"))
				.thenReturn("enfTopicName");

		PowerMockito.mockStatic(DMaaPResponseBuilder.class);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);
		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(httpServReq.getHeader("Authorization")).thenReturn(null);

		when(dmaapContext.getResponse()).thenReturn(httpServRes);

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).getTopic(any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.getTopic("topicName");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test
	public void testCreateTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.createTopic(topicBean);
	}

	@Test
	public void testCreateTopic_error() {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).createTopic(any(), any());
		} catch (TopicExistsException | IOException | AccessDeniedException | DMaaPAccessDeniedException excp) {
			assertTrue(false);
		} catch (CambriaApiException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.createTopic(topicBean);
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}

		try {
			PowerMockito.doThrow(new TopicExistsException("error")).when(topicService).createTopic(any(), any());
		} catch (TopicExistsException | IOException | AccessDeniedException | DMaaPAccessDeniedException excp) {
			assertTrue(false);
		} catch (CambriaApiException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.createTopic(topicBean);
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}

		try {
			PowerMockito.doThrow(new AccessDeniedException()).when(topicService).createTopic(any(), any());
		} catch (TopicExistsException | IOException | AccessDeniedException | DMaaPAccessDeniedException excp) {
			assertTrue(false);
		} catch (CambriaApiException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.createTopic(topicBean);
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test
	public void testDeleteTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.deleteTopic("enfTopicNamePlusExtra");
	}

	@Test
	public void testDeleteTopic_error()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).deleteTopic(any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.deleteTopic("enfTopicNamePlusExtra");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}

		try {
			PowerMockito.doThrow(new AccessDeniedException()).when(topicService).deleteTopic(any(),
					any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.deleteTopic("enfTopicNamePlusExtra");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test
	public void testGetPublishersByTopicName()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.getPublishersByTopicName("enfTopicNamePlusExtra");
	}

	@Test
	public void testGetPublishersByTopicName_error() {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).getPublishersByTopicName(any(),
					any());
		} catch (TopicExistsException | ConfigDbException | IOException e) {
			assertTrue(false);
		}

		try {
			topicRestService.getPublishersByTopicName("enfTopicNamePlusExtra");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test
	public void testPermitPublisherForTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(any())).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.permitPublisherForTopic("enfTopicNamePlusExtra", "producerID");
	}

	@Test
	public void testPermitPublisherForTopic_error()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).permitPublisherForTopic(any(),
					any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.permitPublisherForTopic("enfTopicNamePlusExtra", "producerID");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}

		try {
			PowerMockito.doThrow(new AccessDeniedException()).when(topicService).permitPublisherForTopic(any(),
					any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.permitPublisherForTopic("enfTopicNamePlusExtra", "producerID");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test
	public void testDenyPublisherForTopic()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.denyPublisherForTopic("enfTopicNamePlusExtra", "producerID");
	}

	@Test
	public void testDenyPublisherForTopic_error()
			throws DMaaPAccessDeniedException, CambriaApiException, IOException, TopicExistsException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).denyPublisherForTopic(any(),
					any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.denyPublisherForTopic("enfTopicNamePlusExtra", "producerID");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}

		try {
			PowerMockito.doThrow(new AccessDeniedException()).when(topicService).denyPublisherForTopic(any(),
					any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.denyPublisherForTopic("enfTopicNamePlusExtra", "producerID");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}

	}

	@Test
	public void testGetConsumersByTopicName() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.getConsumersByTopicName("enfTopicNamePlusExtra");
	}

	@Test
	public void testGetConsumersByTopicName_error() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).getConsumersByTopicName(any(),
					any());
		} catch (TopicExistsException | ConfigDbException | IOException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.getConsumersByTopicName("enfTopicNamePlusExtra");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test
	public void testPermitConsumerForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(any())).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.permitConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
	}

	@Test
	public void testPermitConsumerForTopic_error() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(any())).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).permitConsumerForTopic(any(),
					any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.permitConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

	@Test
	public void testPermitConsumerForTopicWithException() throws DMaaPAccessDeniedException, CambriaApiException,
			IOException, TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.permitConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
	}

	@Test
	public void testDenyConsumerForTopic() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		topicRestService.denyConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
	}

	@Test
	public void testDenyConsumerForTopic_error() throws DMaaPAccessDeniedException, CambriaApiException, IOException,
			TopicExistsException, AccessDeniedException {

		Assert.assertNotNull(topicRestService);

		when(dmaapContext.getRequest()).thenReturn(httpServReq);
		when(dmaaPAuthenticator.authenticate(dmaapContext)).thenReturn(nsaSimpleApiKey);
		when(configReader.getfSecurityManager()).thenReturn(dmaaPAuthenticator);
		when(dmaapContext.getConfigReader()).thenReturn(configReader);

		TopicBean topicBean = new TopicBean();
		topicBean.setTopicName("enfTopicNamePlusExtra");

		try {
			PowerMockito.doThrow(new IOException()).when(topicService).denyConsumerForTopic(any(),
					any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.denyConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}

		try {
			PowerMockito.doThrow(new AccessDeniedException()).when(topicService).denyConsumerForTopic(any(),
					any(), any());
		} catch (TopicExistsException | ConfigDbException | IOException | AccessDeniedException
				| DMaaPAccessDeniedException excp) {
			assertTrue(false);
		}

		try {
			topicRestService.denyConsumerForTopic("enfTopicNamePlusExtra", "consumerID");
		} catch (CambriaApiException excp) {
			assertTrue(true);
		}
	}

}
