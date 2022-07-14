/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *  ================================================================================
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
 *  ECOMP is a trademark and service mark of AT&T Intellectual Property.
 *
 *******************************************************************************/
package org.onap.dmaap.service;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.beans.TopicBean;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.TopicService;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * This class is a CXF REST service which acts 
 * as gateway for MR Topic Service.
 * @author Ramkumar Sembaiyan
 *
 */

@Component
@Path("/")
public class TopicRestService {

	/**
	 * Logger obj
	 */
	private static final EELFLogger LOGGER = EELFManager.getLogger(TopicRestService.class);
	private static final String READ = " read ";
	/**
	 * Config Reader
	 */
	@Autowired
	@Qualifier("configurationReader")
	private ConfigurationReader configReader;

	/**
	 * HttpServletRequest obj
	 */
	@Context
	private HttpServletRequest request;

	/**
	 * HttpServletResponse obj
	 */
	@Context
	private HttpServletResponse response;

	/**
	 * TopicService obj
	 */
	@Autowired
	private TopicService topicService;

	/**
	 * DMaaPErrorMessages obj
	 */
	@Autowired
	private DMaaPErrorMessages errorMessages;

	private DMaaPContext getDmaapContext() {
		DMaaPContext dmaapContext = new DMaaPContext();
		dmaapContext.setRequest(request);
		dmaapContext.setResponse(response);
		dmaapContext.setConfigReader(configReader);
		return dmaapContext;
	}

	/**
	 * Fetches a list of topics from the current kafka instance and converted
	 * into json object.
	 *
	 * @throws AccessDeniedException
	 * @throws CambriaApiException
	 * @throws IOException
	 * @throws JSONException
	 * */
	@GET
	public void getTopics() throws CambriaApiException {
		try {
			LOGGER.info("Authenticating the user before fetching the topics");
			String mrNameS = com.att.ajsc.beans.PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop,"msgRtr.namespace.aaf");
			String permission = mrNameS+"|"+"*"+"|"+"view";
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
			//Check if client is using AAF CADI Basic Authorization
			//If yes then check for AAF role authentication else display all topics 
			if(null != getDmaapContext().getRequest().getHeader(Utils.AUTH_HEADER) && !aaf.aafAuthentication(getDmaapContext().getRequest(), permission)) {
					ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						errorMessages.getNotPermitted1() + READ + errorMessages.getNotPermitted2());
					LOGGER.info(errRes.toString());
					throw new DMaaPAccessDeniedException(errRes);
			}
			LOGGER.info("Fetching all Topics");
			topicService.getTopics(getDmaapContext());
			LOGGER.info("Returning List of all Topics");
		} catch (JSONException | ConfigDbException | IOException excp) {
			LOGGER.error("Failed to retrieve list of all topics: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
				DMaaPResponseCode.GET_TOPICS_FAIL.getResponseCode(),
				errorMessages.getTopicsfailure() + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Fetches a list of topics from the current kafka instance and converted
	 * into json object.
	 *
	 * @return list of the topics in json format
	 * @throws AccessDeniedException
	 * @throws CambriaApiException
	 * @throws IOException
	 * @throws JSONException
	 * */
	@GET
	@Path("/listAll")
	public void getAllTopics() throws CambriaApiException {
		try {
			LOGGER.info("Authenticating the user before fetching the topics");
			String mrNameS = com.att.ajsc.beans.PropertiesMapBean.getProperty(CambriaConstants.msgRtr_prop,"msgRtr.namespace.aaf");
			String permission = mrNameS+"|"+"*"+"|"+"view";
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
			//Check if client is using AAF CADI Basic Authorization
			//If yes then check for AAF role authentication else display all topics 
			if(null != getDmaapContext().getRequest().getHeader(Utils.AUTH_HEADER)) {
				if(!aaf.aafAuthentication(getDmaapContext().getRequest(), permission)) {
					ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						errorMessages.getNotPermitted1() + READ + errorMessages.getNotPermitted2());
					LOGGER.info(errRes.toString());
					throw new DMaaPAccessDeniedException(errRes);
				}
			}
			LOGGER.info("Fetching all Topics");
			topicService.getAllTopics(getDmaapContext());
			LOGGER.info("Returning List of all Topics");
		} catch (JSONException | ConfigDbException | IOException excp) {
			LOGGER.error("Failed to retrieve list of all topics: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
				DMaaPResponseCode.GET_TOPICS_FAIL.getResponseCode(),
				errorMessages.getTopicsfailure()+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Returns details of the topic whose name is passed as a parameter
	 *
	 * @param topicName
	 *            - name of the topic
	 * @return details of a topic whose name is mentioned in the request in json
	 *         format.
	 * @throws AccessDeniedException
	 * @throws DMaaPAccessDeniedException
	 * @throws IOException
	 * */
	@GET
	@Path("/{topicName}")
	public void getTopic(@PathParam("topicName") String topicName) throws CambriaApiException {
		try {
			LOGGER.info("Authenticating the user before fetching the details about topic = {}", topicName);
			DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();
			//Check if client is using AAF CADI Basic Authorization
			//If yes then check for AAF role authentication else display all topics 
			if(null != getDmaapContext().getRequest().getHeader(Utils.AUTH_HEADER)) {
				String permission = aaf.aafPermissionString(topicName, "view");
				if(!aaf.aafAuthentication(getDmaapContext().getRequest(), permission)) {
					ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
						DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(),
						errorMessages.getNotPermitted1() + READ + errorMessages.getNotPermitted2());
					LOGGER.info(errRes.toString());
					throw new DMaaPAccessDeniedException(errRes);
				}
			}
			LOGGER.info("Fetching Topic: {}", topicName);
			topicService.getTopic(getDmaapContext(), topicName);
			LOGGER.info("Fetched details of topic: {}", topicName);
		} catch (ConfigDbException | IOException | TopicExistsException excp) {
			LOGGER.error("Failed to retrieve details of topic: " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
				DMaaPResponseCode.GET_TOPICS_DETAILS_FAIL.getResponseCode(),
				errorMessages.getTopicDetailsFail()+topicName+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * This method is still not working. Need to check on post call and how to
	 * accept parameters for post call
	 *
	 * @param topicBean
	 *            it will have the bean object
	 * @throws TopicExistsException
	 * @throws CambriaApiException
	 * @throws JSONException
	 * @throws IOException
	 * @throws AccessDeniedException
	 *
	 * */
	@POST
	@Path("/create")
	@Consumes({ MediaType.APPLICATION_JSON })
	public void createTopic(TopicBean topicBean) throws CambriaApiException, JSONException {
		try {
			LOGGER.info("Creating Topic."+topicBean.getTopicName());
			topicService.createTopic(getDmaapContext(), topicBean);
			LOGGER.info("Topic created Successfully.");
		} catch (TopicExistsException ex){
			LOGGER.error("Error while creating a topic: " + ex.getMessage(), ex);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_CONFLICT,
				DMaaPResponseCode.CREATE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getCreateTopicFail()+ ex.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (AccessDeniedException | DMaaPAccessDeniedException excp) {
			LOGGER.error("Error while creating a topic: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
				DMaaPResponseCode.CREATE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getCreateTopicFail()+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (CambriaApiException |  IOException excp) {
			LOGGER.error("Error while creating a topic: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
				DMaaPResponseCode.CREATE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getCreateTopicFail()+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Deletes existing topic whose name is passed as a parameter
	 *
	 * @param topicName
	 *            topic
	 * @throws CambriaApiException
	 * @throws IOException
	 * */
	@DELETE
	@Path("/{topicName}")
	public void deleteTopic(@PathParam("topicName") String topicName) throws CambriaApiException {
		try {
			LOGGER.info("Deleting Topic: " + topicName);
			topicService.deleteTopic(getDmaapContext(), topicName);
			LOGGER.info("Topic [" + topicName + "] deleted successfully.");
		} catch (DMaaPAccessDeniedException| AccessDeniedException excp) {
			LOGGER.error("Error while deleting a topic: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
				DMaaPResponseCode.CREATE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getCreateTopicFail()+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (IOException | ConfigDbException | CambriaApiException | TopicExistsException excp) {
			LOGGER.error("Error while deleting topic: " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
				DMaaPResponseCode.DELETE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getDeleteTopicFail()+ topicName + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * This method will fetch the details of publisher by giving topic name
	 *
	 * @param topicName
	 * @throws CambriaApiException
	 * @throws AccessDeniedException
	 */
	@GET
	@Path("/{topicName}/producers")
	public void getPublishersByTopicName(
		@PathParam("topicName") String topicName) throws CambriaApiException {
		try {
			LOGGER.info("Fetching list of all the publishers for topic " + topicName);
			topicService.getPublishersByTopicName(getDmaapContext(), topicName);
			LOGGER.info("Returning list of all the publishers for topic " + topicName);
		} catch (IOException | ConfigDbException | TopicExistsException excp) {
			LOGGER.error("Error while fetching list of publishers for topic " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
				DMaaPResponseCode.GET_PUBLISHERS_BY_TOPIC.getResponseCode(),
				"Error while fetching list of publishers for topic: " + topicName + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * proving permission for the topic for a particular publisher id
	 *
	 * @param topicName
	 * @param producerId
	 * @throws CambriaApiException
	 */
	@PUT
	@Path("/{topicName}/producers/{producerId}")
	public void permitPublisherForTopic(
		@PathParam("topicName") String topicName,
		@PathParam("producerId") String producerId) throws CambriaApiException {
		try {
			LOGGER.info("Granting write access to producer [" + producerId + "] for topic " + topicName);
			topicService.permitPublisherForTopic(getDmaapContext(), topicName, producerId);
			LOGGER.info("Write access has been granted to producer [" + producerId + "] for topic " + topicName);
		} catch (AccessDeniedException | DMaaPAccessDeniedException excp) {
			LOGGER.error("Error while creating a topic: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
				DMaaPResponseCode.CREATE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getCreateTopicFail()+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch ( ConfigDbException | IOException | TopicExistsException excp) {
			LOGGER.error("Error while granting write access to producer ["
				+ producerId + "] for topic " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
				DMaaPResponseCode.PERMIT_PUBLISHER_FOR_TOPIC.getResponseCode(),
				"Error while granting write access to producer ["
					+ producerId + "] for topic " + topicName + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Removing access for a publisher id for any particular topic
	 *
	 * @param topicName
	 * @param producerId
	 * @throws CambriaApiException
	 */
	@DELETE
	@Path("/{topicName}/producers/{producerId}")
	public void denyPublisherForTopic(@PathParam("topicName") String topicName,
		@PathParam("producerId") String producerId) throws CambriaApiException {
		try {
			LOGGER.info("Revoking write access to producer [" + producerId + "] for topic " + topicName);
			topicService.denyPublisherForTopic(getDmaapContext(), topicName, producerId);
			LOGGER.info("Write access revoked for producer [" + producerId + "] for topic " + topicName);
		} catch (DMaaPAccessDeniedException | AccessDeniedException excp) {
			LOGGER.error("Error while creating a topic: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
				DMaaPResponseCode.CREATE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getCreateTopicFail()+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch ( ConfigDbException | IOException | TopicExistsException excp) {
			LOGGER.error("Error while revoking write access for producer [" + producerId + "] for topic " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
				DMaaPResponseCode.REVOKE_PUBLISHER_FOR_TOPIC.getResponseCode(),
				"Error while revoking write access to producer ["
					+ producerId + "] for topic " + topicName + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Get the consumer details by the topic name
	 *
	 * @param topicName
	 * @throws CambriaApiException
	 */
	@GET
	@Path("/{topicName}/consumers")
	public void getConsumersByTopicName(@PathParam("topicName") String topicName) throws CambriaApiException {
		try {
			LOGGER.info("Fetching list of all consumers for topic " + topicName);
			topicService.getConsumersByTopicName(getDmaapContext(), topicName);
			LOGGER.info("Returning list of all consumers for topic " + topicName);
		} catch (IOException | ConfigDbException | TopicExistsException excp) {
			LOGGER.error("Error while fetching list of all consumers for topic " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
				DMaaPResponseCode.GET_CONSUMERS_BY_TOPIC.getResponseCode(),
				"Error while fetching list of all consumers for topic: " + topicName+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * providing access for consumer for any particular topic
	 *
	 * @param topicName
	 * @param consumerId
	 * @throws CambriaApiException
	 */
	@PUT
	@Path("/{topicName}/consumers/{consumerId}")
	public void permitConsumerForTopic(
		@PathParam("topicName") String topicName,
		@PathParam("consumerId") String consumerId) throws CambriaApiException {
		try {
			LOGGER.info("Granting read access to consumer [" + consumerId + "] for topic " + topicName);
			topicService.permitConsumerForTopic(getDmaapContext(), topicName, consumerId);
			LOGGER.info("Read access granted to consumer [{0}] for topic {1}", consumerId, topicName);
		} catch (AccessDeniedException | ConfigDbException | IOException
			| TopicExistsException excp) {
			LOGGER.error("Error while granting read access to consumer [" + consumerId + "] for topic " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
				DMaaPResponseCode.PERMIT_CONSUMER_FOR_TOPIC.getResponseCode(),
				"Error while granting read access to consumer ["
					+ consumerId + "] for topic " + topicName+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Removing access for consumer for any particular topic
	 *
	 * @param topicName
	 * @param consumerId
	 * @throws CambriaApiException
	 */
	@DELETE
	@Path("/{topicName}/consumers/{consumerId}")
	public void denyConsumerForTopic(@PathParam("topicName") String topicName,
		@PathParam("consumerId") String consumerId) throws CambriaApiException {
		try {
			LOGGER.info("Revoking read access to consumer [" + consumerId + "] for topic " + topicName);
			topicService.denyConsumerForTopic(getDmaapContext(), topicName, consumerId);
			LOGGER.info("Read access revoked to consumer [" + consumerId + "] for topic " + topicName);
		} catch ( ConfigDbException | IOException
			| TopicExistsException excp) {
			LOGGER.error("Error while revoking read access to consumer [" + consumerId + "] for topic " + topicName, excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN,
				DMaaPResponseCode.REVOKE_CONSUMER_FOR_TOPIC.getResponseCode(),
				"Error while revoking read access to consumer ["
					+ consumerId + "] for topic " + topicName+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (DMaaPAccessDeniedException | AccessDeniedException excp) {
			LOGGER.error("Error while creating a topic: " + excp.getMessage(), excp);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
				DMaaPResponseCode.CREATE_TOPIC_FAIL.getResponseCode(),
				errorMessages.getCreateTopicFail()+ excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	public TopicService getTopicService() {
		return topicService;
	}

	public void setTopicService(TopicService topicService) {
		this.topicService = topicService;
	}

}
