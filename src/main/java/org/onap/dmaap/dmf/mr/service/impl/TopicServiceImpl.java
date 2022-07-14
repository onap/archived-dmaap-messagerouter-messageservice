/*
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
 *  Copyright (C) 2019 Nokia Intellectual Property. All rights reserved.
 * =================================================================================
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ============LICENSE_END=========================================================
 *  
 */
package org.onap.dmaap.dmf.mr.service.impl;

import static org.onap.dmaap.util.DMaaPAuthFilter.isUseCustomAcls;

import com.att.ajsc.beans.PropertiesMapBean;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.NsaAcl;
import com.att.nsa.security.NsaApiKey;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import joptsimple.internal.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.DMaaPKafkaMetaBroker;
import org.onap.dmaap.dmf.mr.beans.TopicBean;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.metabroker.Broker1;
import org.onap.dmaap.dmf.mr.metabroker.Topic;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.TopicService;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import org.onap.dmaap.dmf.mr.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.Principal;

/**
 * @author muzainulhaque.qazi
 *
 */
@Service
public class TopicServiceImpl implements TopicService {

	private static final String TOPIC_CREATE_OP = "create";
	private static final EELFLogger LOGGER = EELFManager.getLogger(TopicServiceImpl.class);
	@Autowired
	private DMaaPErrorMessages errorMessages;

	public DMaaPErrorMessages getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(DMaaPErrorMessages errorMessages) {
		this.errorMessages = errorMessages;
	}


  String getPropertyFromAJSCbean(String propertyKey) {
		return PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop, propertyKey);
	}

	String getPropertyFromAJSCmap(String propertyKey) {
		return AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, propertyKey);
	}

	NsaApiKey getDmaapAuthenticatedUser(DMaaPContext dmaapContext) {
		return DMaaPAuthenticatorImpl.getAuthenticatedUser(dmaapContext);
	}

	void respondOk(DMaaPContext context, String msg) {
		DMaaPResponseBuilder.respondOkWithHtml(context, msg);
	}

	void respondOk(DMaaPContext context, JSONObject json) throws IOException {
		DMaaPResponseBuilder.respondOk(context, json);
	}

	boolean isCadiEnabled() {
		return Utils.isCadiEnabled();
	}
	/**
	 * @param dmaapContext
	 * @throws JSONException
	 * @throws ConfigDbException
	 * @throws IOException
	 * 
	 */
	@Override
	public void getTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException {
		LOGGER.info("Fetching list of all the topics.");
		JSONObject json = new JSONObject();
		JSONArray topicsList = new JSONArray();
		for (Topic topic : getMetaBroker(dmaapContext).getAllTopics()) {
			topicsList.put(topic.getName());
		}
		json.put("topics", topicsList);
		LOGGER.info("Returning list of all the topics.");
		respondOk(dmaapContext, json);
	}

	/**
	 * @param dmaapContext
	 * @throws JSONException
	 * @throws ConfigDbException
	 * @throws IOException
	 * 
	 */
	public void getAllTopics(DMaaPContext dmaapContext) throws JSONException, ConfigDbException, IOException {
		LOGGER.info("Fetching list of all the topics.");
		JSONObject json = new JSONObject();
		JSONArray topicsList = new JSONArray();
		for (Topic topic : getMetaBroker(dmaapContext).getAllTopics()) {
			JSONObject obj = new JSONObject();
			obj.put("topicName", topic.getName());
			
			obj.put("owner", topic.getOwner());
			obj.put("txenabled", topic.isTransactionEnabled());
			topicsList.put(obj);
		}
		json.put("topics", topicsList);
		LOGGER.info("Returning list of all the topics.");
		respondOk(dmaapContext, json);
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 */
	@Override
	public void getTopic(DMaaPContext dmaapContext, String topicName)
			throws ConfigDbException, IOException, TopicExistsException {
		LOGGER.info("Fetching details of topic " + topicName);
		Topic t = getMetaBroker(dmaapContext).getTopic(topicName);
		if (null == t) {
			LOGGER.error("Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException("Topic [" + topicName + "] does not exist.");
		}
		JSONObject o = new JSONObject();
		o.put("name", t.getName());
		o.put("description", t.getDescription());
		if (null != t.getOwners())
			o.put("owner", t.getOwners().iterator().next());
		if (null != t.getReaderAcl())
			o.put("readerAcl", aclToJson(t.getReaderAcl()));
		if (null != t.getWriterAcl())
			o.put("writerAcl", aclToJson(t.getWriterAcl()));
		LOGGER.info("Returning details of topic " + topicName);
		respondOk(dmaapContext, o);
	}

	/**
	 * @param dmaapContext
	 * @param topicBean
	 * @throws CambriaApiException
	 * @throws AccessDeniedException
	 * @throws IOException
	 * @throws TopicExistsException
	 * @throws JSONException
	 * 
	 * 
	 * 
	 */
	@Override
	public void createTopic(DMaaPContext dmaapContext, TopicBean topicBean) throws CambriaApiException, IOException {
		String topicName = topicBean.getTopicName();
		LOGGER.info("Creating topic {}",topicName);
		String key = authorizeClient(dmaapContext, topicName, TOPIC_CREATE_OP);
		try {
			final int partitions = getValueOrDefault(topicBean.getPartitionCount(), "default.partitions");
			final int replicas = getValueOrDefault(topicBean.getReplicationCount(), "default.replicas");
			final Topic t = getMetaBroker(dmaapContext).createTopic(topicName, topicBean.getTopicDescription(),
				key, partitions, replicas, topicBean.isTransactionEnabled());
			LOGGER.info("Topic {} created successfully. Sending response", topicName);
			respondOk(dmaapContext, topicToJson(t));
		} catch (JSONException ex) {
			LOGGER.error("Failed to create topic "+ topicName +". Couldn't parse JSON data.", ex);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,
					DMaaPResponseCode.INCORRECT_JSON.getResponseCode(), errorMessages.getIncorrectJson());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (ConfigDbException ex) {
			LOGGER.error("Failed to create topic "+ topicName +".  Config DB Exception", ex);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,
					DMaaPResponseCode.INCORRECT_JSON.getResponseCode(), errorMessages.getIncorrectJson());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (Broker1.TopicExistsException ex) {
			LOGGER.error( "Failed to create topic "+ topicName +".  Topic already exists.",ex);
		}
	}

	private String authorizeClient(DMaaPContext dmaapContext, String topicName, String operation) throws DMaaPAccessDeniedException {
		String clientId = Strings.EMPTY;
		if(isCadiEnabled() && isTopicWithEnforcedAuthorization(topicName)) {
			LOGGER.info("Performing AAF authorization for topic {} creation.", topicName);
			String permission = buildPermission(topicName, operation);
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
			clientId = getAAFclientId(dmaapContext.getRequest());

			if (!aaf.aafAuthentication(dmaapContext.getRequest(), permission)) {
				LOGGER.error("Failed to {} topic {}. Authorization failed for client {} and permission {}",
					operation, topicName, clientId, permission);
				throw new DMaaPAccessDeniedException(new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
					"Failed to "+ operation +" topic: Access Denied. User does not have permission to create topic with perm " + permission));
			}
		} else if (operation.equals(TOPIC_CREATE_OP)){
			final NsaApiKey user = getDmaapAuthenticatedUser(dmaapContext);
			clientId = (user != null) ? user.getKey() : Strings.EMPTY;
		}
		return clientId;
	}

	private String getAAFclientId(HttpServletRequest request) {
		Principal principal = request.getUserPrincipal();
		if (principal !=null) {
			return principal.getName();
		} else {
			LOGGER.warn("Performing AAF authorization but user has not been provided in request.");
			return null;
		}
	}

	private boolean isTopicWithEnforcedAuthorization(String topicName) {
		String enfTopicNamespace = getPropertyFromAJSCbean("enforced.topic.name.AAF");
		return enfTopicNamespace != null && topicName.startsWith(enfTopicNamespace);
	}

	int getValueOrDefault(int value, String defaultProperty) {
		int returnValue = value;
		if (returnValue <= 0) {
			String defaultValue = getPropertyFromAJSCmap(defaultProperty);
			returnValue = StringUtils.isNotEmpty(defaultValue) ? NumberUtils.toInt(defaultValue) : 1;
			returnValue = (returnValue <= 0) ? 1 : returnValue;
		}
		return returnValue;
	}

	private String buildPermission(String topicName, String operation) {
		String nameSpace = (topicName.indexOf('.') > 1) ?
			topicName.substring(0, topicName.lastIndexOf('.')) : "";

		String mrFactoryValue = getPropertyFromAJSCmap("msgRtr.topicfactory.aaf");
		return mrFactoryValue + nameSpace + "|" + operation;
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 * @throws AccessDeniedException
	 */
	@Override
	public void deleteTopic(DMaaPContext dmaapContext, String topicName) throws IOException, ConfigDbException,
			CambriaApiException, TopicExistsException, DMaaPAccessDeniedException, AccessDeniedException {
		LOGGER.info(" Deleting topic " + topicName);
		authorizeClient(dmaapContext, topicName, "destroy");
		final Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);
		if (topic == null) {
			LOGGER.error("Failed to delete topic. Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException("Failed to delete topic. Topic [" + topicName + "] does not exist.");
		}
		// metabroker.deleteTopic(topicName);
		LOGGER.info("Topic [" + topicName + "] deleted successfully. Sending response.");
		respondOk(dmaapContext, "Topic [" + topicName + "] deleted successfully");
	}

	/**
	 * 
	 * @param dmaapContext
	 * @return
	 */
	DMaaPKafkaMetaBroker getMetaBroker(DMaaPContext dmaapContext) {
		return (DMaaPKafkaMetaBroker) dmaapContext.getConfigReader().getfMetaBroker();
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 * 
	 */
	@Override
	public void getPublishersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws ConfigDbException, IOException, TopicExistsException {
		LOGGER.info("Retrieving list of all the publishers for topic " + topicName);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);
		if (topic == null) {
			LOGGER.error("Failed to retrieve publishers list for topic. Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException(
					"Failed to retrieve publishers list for topic. Topic [" + topicName + "] does not exist.");
		}
		final NsaAcl acl = topic.getWriterAcl();
		LOGGER.info("Returning list of all the publishers for topic " + topicName + ". Sending response.");
		respondOk(dmaapContext, aclToJson(acl));
	}

	/**
	 * 
	 * @param acl
	 * @return
	 */
	private static JSONObject aclToJson(NsaAcl acl) {
		final JSONObject o = new JSONObject();
		if (acl == null) {
			o.put("enabled", false);
			o.put("users", new JSONArray());
		} else {
			o.put("enabled", acl.isActive());

			final JSONArray a = new JSONArray();
			for (String user : acl.getUsers()) {
				a.put(user);
			}
			o.put("users", a);
		}
		return o;
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 */
	@Override
	public void getConsumersByTopicName(DMaaPContext dmaapContext, String topicName)
			throws IOException, ConfigDbException, TopicExistsException {
		LOGGER.info("Retrieving list of all the consumers for topic " + topicName);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);
		if (topic == null) {
			LOGGER.error("Failed to retrieve consumers list for topic. Topic [" + topicName + "] does not exist.");
			throw new TopicExistsException(
					"Failed to retrieve consumers list for topic. Topic [" + topicName + "] does not exist.");
		}
		final NsaAcl acl = topic.getReaderAcl();
		LOGGER.info("Returning list of all the consumers for topic " + topicName + ". Sending response.");
		respondOk(dmaapContext, aclToJson(acl));

	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	static JSONObject topicToJson(Topic t) {
		final JSONObject o = new JSONObject();

		o.put("name", t.getName());
		o.put("description", t.getDescription());
		o.put("owner", t.getOwner());
		o.put("readerAcl", aclToJson(t.getReaderAcl()));
		o.put("writerAcl", aclToJson(t.getWriterAcl()));

		return o;
	}

	/**
	 * @param dmaapContext
	 * 			@param topicName @param producerId @throws
	 *            ConfigDbException @throws IOException @throws
	 *            TopicExistsException @throws AccessDeniedException @throws
	 * 
	 */
	@Override
	public void permitPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, TopicExistsException {
		LOGGER.info("Granting write access to producer [" + producerId + "] for topic " + topicName);
		final NsaApiKey user = getDmaapAuthenticatedUser(dmaapContext);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);
		if (null == topic) {
			LOGGER.error("Failed to permit write access to producer [" + producerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to permit write access to producer [" + producerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}
		if (isUseCustomAcls()) {
			topic.permitWritesFromUser(producerId, user);
			LOGGER.info("Write access has been granted to producer [" + producerId + "] for topic [" + topicName
				+ "]. Sending response.");
		} else {
			LOGGER.info("Ignoring acl update");
		}
		respondOk(dmaapContext, "Write access has been granted to publisher.");
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @param producerId
	 * @throws ConfigDbException
	 * @throws IOException
	 * @throws TopicExistsException
	 * @throws AccessDeniedException
	 * @throws DMaaPAccessDeniedException
	 * 
	 */
	@Override
	public void denyPublisherForTopic(DMaaPContext dmaapContext, String topicName, String producerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,
			DMaaPAccessDeniedException {
		LOGGER.info("Revoking write access to producer [" + producerId + "] for topic " + topicName);
		final NsaApiKey user = getDmaapAuthenticatedUser(dmaapContext);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);
		if (null == topic) {
			LOGGER.error("Failed to revoke write access to producer [" + producerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to revoke write access to producer [" + producerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}
		topic.denyWritesFromUser(producerId, user);
		LOGGER.info("Write access has been revoked to producer [" + producerId + "] for topic [" + topicName
				+ "]. Sending response.");
		respondOk(dmaapContext, "Write access has been revoked for publisher.");
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws DMaaPAccessDeniedException
	 */
	@Override
	public void permitConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, TopicExistsException {

		LOGGER.info("Granting read access to consumer [" + consumerId + "] for topic " + topicName);
		final NsaApiKey user = getDmaapAuthenticatedUser(dmaapContext);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);
		if (null == topic) {
			LOGGER.error("Failed to permit read access to consumer [" + consumerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to permit read access to consumer [" + consumerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}
		if (isUseCustomAcls()) {
			topic.permitReadsByUser(consumerId, user);
			LOGGER.info("Read access has been granted to consumer [" + consumerId + "] for topic [" + topicName
				+ "]. Sending response.");
		} else {
			LOGGER.info("Ignoring acl update");
		}
		respondOk(dmaapContext,
				"Read access has been granted for consumer [" + consumerId + "] for topic [" + topicName + "].");
	}

	/**
	 * @param dmaapContext
	 * @param topicName
	 * @param consumerId
	 * @throws DMaaPAccessDeniedException
	 */
	@Override
	public void denyConsumerForTopic(DMaaPContext dmaapContext, String topicName, String consumerId)
			throws AccessDeniedException, ConfigDbException, IOException, TopicExistsException,
			DMaaPAccessDeniedException {

		LOGGER.info("Revoking read access to consumer [" + consumerId + "] for topic " + topicName);
		final NsaApiKey user = getDmaapAuthenticatedUser(dmaapContext);
		Topic topic = getMetaBroker(dmaapContext).getTopic(topicName);
		if (null == topic) {
			LOGGER.error("Failed to revoke read access to consumer [" + consumerId + "] for topic. Topic [" + topicName
					+ "] does not exist.");
			throw new TopicExistsException("Failed to permit read access to consumer [" + consumerId
					+ "] for topic. Topic [" + topicName + "] does not exist.");
		}
		topic.denyReadsByUser(consumerId, user);
		LOGGER.info("Read access has been revoked to consumer [" + consumerId + "] for topic [" + topicName
				+ "]. Sending response.");
		respondOk(dmaapContext,
				"Read access has been revoked for consumer [" + consumerId + "] for topic [" + topicName + "].");

	}

}
