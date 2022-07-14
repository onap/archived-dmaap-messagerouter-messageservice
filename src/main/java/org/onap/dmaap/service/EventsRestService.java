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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.service.EventsService;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.Utils;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
/**
 * This class is a CXF REST service which acts 
 * as gateway for MR Event Service.
 * @author rajashree.khare
 *
 */
@Component
@Path("/")
public class EventsRestService {

	/**
	 * Logger obj
	 */
	private static final EELFLogger log = EELFManager.getLogger(EventsRestService.class);
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
	 * Config Reader
	 */
	@Autowired
	@Qualifier("configurationReader")
	private ConfigurationReader configReader;

	@Autowired
	private EventsService eventsService;

	@Autowired
	private DMaaPErrorMessages errorMessages;
	

	/**
	 * This method is used to consume messages.Taking three parameter
	 * topic,consumerGroup and consumerId .Consumer decide to which topic they
	 * want to consume messages.In on consumer Group there might be many
	 * consumer may be present.
	 * 
	 * @param topic
	 *            specify- the topic name
	 * @param consumergroup
	 *            - specify the consumer group
	 * @param consumerid
	 *            -specify the consumer id
	 * 
	 *            handles CambriaApiException | ConfigDbException |
	 *            TopicExistsException | AccessDeniedException |
	 *            UnavailableException | IOException in try catch block
	 * @throws CambriaApiException
	 * 
	 */
	@GET
	@Path("/{topic}/{consumergroup}/{consumerid}")
	public void getEvents(@PathParam("topic") String topic, @PathParam("consumergroup") 
	String consumergroup,
			@PathParam("consumerid") String consumerid) throws CambriaApiException {
		log.info("Consuming message from topic " + topic );
		DMaaPContext dMaaPContext = getDmaapContext();
		dMaaPContext.setConsumerRequestTime(Utils.getFormattedDate(new Date()));

		try {
			eventsService.getEvents(dMaaPContext, topic, consumergroup, consumerid);
		} catch (TopicExistsException  e) {
			log.error("Error while reading data from topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_CONFLICT,
					DMaaPResponseCode.CONSUME_MSG_ERROR.getResponseCode(), errorMessages.getConsumeMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic, null, null, 
							consumerid,
					request.getRemoteHost());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (DMaaPAccessDeniedException | AccessDeniedException  e) {
			log.error("Error while reading data from topic [" + topic + "].", e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.CONSUME_MSG_ERROR.getResponseCode(), errorMessages.getConsumeMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic, null, null, 
							consumerid,
					request.getRemoteHost());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (ConfigDbException | UnavailableException | IOException e) {
			log.error("Error while reading data from topic [" + topic + "].", e);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
					DMaaPResponseCode.CONSUME_MSG_ERROR.getResponseCode(), errorMessages.getConsumeMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic, null, null, 
							consumerid,
					request.getRemoteHost());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}
	
	
	/**
	 * This method is used to throw an exception back to the client app if CG/CID is not passed
	 *  while consuming messages
	 */
	@GET
	@Path("/{topic}")
	public void getEventsToException(@PathParam("topic") String topic) throws CambriaApiException {
		// log.info("Consuming message from topic " + topic );
		DMaaPContext dMaaPContext = getDmaapContext();
		dMaaPContext.setConsumerRequestTime(Utils.getFormattedDate(new Date()));

		try {

			throw new TopicExistsException("Incorrect URL");
		} catch (TopicExistsException  e) {
			log.error("Error while reading data from topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
					DMaaPResponseCode.CONSUME_MSG_ERROR.getResponseCode(), "Incorrect url - Expects consumer Group and ID in " + request.getRequestURI() + " from "+request.getRemoteHost()
					);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}
	
	/**
	 * This method is used to throw an exception back to the client app if CG/CID is not passed
	 *  while consuming messages
	 */
	@GET
	@Path("/{topic}/{consumergroup}")
	public void getEventsToException(@PathParam("topic") String topic, @PathParam("consumergroup") 
	String consumergroup) throws CambriaApiException {
		// log.info("Consuming message from topic " + topic );
		DMaaPContext dMaaPContext = getDmaapContext();
		dMaaPContext.setConsumerRequestTime(Utils.getFormattedDate(new Date()));
		try {
			throw new TopicExistsException("Incorrect URL");
		} catch (TopicExistsException  e) {
			log.error("Error while reading data from topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
					DMaaPResponseCode.CONSUME_MSG_ERROR.getResponseCode(), "Incorrect url - Expects consumer ID in " + request.getRequestURI() + " from "+request.getRemoteHost()
					);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * This method is used to publish messages.Taking two parameter topic and
	 * partition.Publisher decide to which topic they want to publish message
	 * and kafka decide to which partition of topic message will send,
	 * 
	 * @param topic
	 * @param msg
	 * @param partitionKey
	 * 
	 *            handles CambriaApiException | ConfigDbException |
	 *            TopicExistsException | AccessDeniedException | IOException in
	 *            try catch block
	 * @throws CambriaApiException
	 */

	@POST
	@Produces("application/json")
	@Path("/{topic}")
	public void pushEvents(@PathParam("topic") String topic, InputStream msg,
			@QueryParam("partitionKey") String partitionKey) throws CambriaApiException {
		log.info("Publishing message to topic " + topic);
		try {
			eventsService.pushEvents(getDmaapContext(), topic, msg, partitionKey, null);
		} catch ( TopicExistsException  e) {
			log.error("Error while publishing to topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_CONFLICT,
					DMaaPResponseCode.PUBLISH_MSG_ERROR.getResponseCode(), errorMessages.getPublishMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic,
					Utils.getUserApiKey(request), request.getRemoteHost(), null, null);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch ( DMaaPAccessDeniedException | AccessDeniedException  e) {
			log.error("Error while publishing to topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.PUBLISH_MSG_ERROR.getResponseCode(), errorMessages.getPublishMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic,
					Utils.getUserApiKey(request), request.getRemoteHost(), null, null);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (ConfigDbException |   IOException | missingReqdSetting e) {
			log.error("Error while publishing to topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
					DMaaPResponseCode.PUBLISH_MSG_ERROR.getResponseCode(), errorMessages.getPublishMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic,
					Utils.getUserApiKey(request), request.getRemoteHost(), null, null);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * This method is used to publish messages by passing an optional header
	 * called 'transactionId'. If the 'transactionId' is not provided in the
	 * input then a new transaction object will be created. Else the existing
	 * transaction object will be updated with the counter details.
	 * 
	 * @param topic
	 * @param partitionKey
	 * 
	 *            handles CambriaApiException | ConfigDbException |
	 *            TopicExistsException | AccessDeniedException | IOException in
	 *            try catch block
	 * @throws CambriaApiException
	 */
	@POST
	@Produces("application/json")
	@Path("/transaction/{topic}")
	public void pushEventsWithTransaction(@PathParam("topic") String topic,
			@QueryParam("partitionKey") String partitionKey) throws CambriaApiException {
		try {
			eventsService.pushEvents(getDmaapContext(), topic, request.getInputStream(), 
					partitionKey,
					Utils.getFormattedDate(new Date()));
		} catch ( TopicExistsException  e) {
			log.error("Error while publishing to topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_CONFLICT,
					DMaaPResponseCode.PUBLISH_MSG_ERROR.getResponseCode(), errorMessages.getPublishMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic,
					Utils.getUserApiKey(request), request.getRemoteHost(), null, null);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch ( DMaaPAccessDeniedException| AccessDeniedException  e) {
			log.error("Error while publishing to topic [" + topic + "].", e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.PUBLISH_MSG_ERROR.getResponseCode(), errorMessages.getPublishMsgError()
							+ e.getMessage(), null, Utils.getFormattedDate(new Date()), topic,
					Utils.getUserApiKey(request), request.getRemoteHost(), null, null);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		} catch (ConfigDbException  | IOException | missingReqdSetting  e) {
			log.error("Error while publishing to topic : " + topic, e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,
					DMaaPResponseCode.PUBLISH_MSG_ERROR.getResponseCode(), "Transaction-"
							+ errorMessages.getPublishMsgError() + e.getMessage(), null,
					Utils.getFormattedDate(new Date()), topic, Utils.getUserApiKey(request), 
					request.getRemoteHost(),
					null, null);
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * This method is used for taking Configuration Object,HttpServletRequest
	 * Object,HttpServletRequest HttpServletResponse Object,HttpServletSession
	 * Object.
	 * 
	 * @return DMaaPContext object from where user can get Configuration
	 *         Object,HttpServlet Object
	 * 
	 */
	private DMaaPContext getDmaapContext() {

		DMaaPContext dmaapContext = new DMaaPContext();
		dmaapContext.setRequest(request);
		dmaapContext.setResponse(response);
		dmaapContext.setConfigReader(configReader);

		return dmaapContext;
	}
	

}