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
package com.att.nsa.dmaap.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import kafka.common.TopicExistsException;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.service.UIService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder;
import com.att.nsa.configs.ConfigDbException;

/**
 * UI Rest Service
 * @author author
 *
 */
@Component
public class UIRestServices {

	/**
	 * Logger obj
	 */
	//private static final Logger LOGGER = Logger.getLogger(UIRestServices.class);
	
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(UIRestServices.class);

	@Autowired
	private UIService uiService;
	
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
	 * getting the hello
	 */
	@GET
	@Path("/")
	public void hello() {
		try {
			LOGGER.info("Calling hello page.");

			uiService.hello(getDmaapContext());

			LOGGER.info("Hello page is returned.");
		} catch (IOException excp) {
			LOGGER.error("Error while calling hello page: " + excp.getMessage(), excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while calling hello page: " + excp.getMessage());
		}
	}

	/**
	 * getApikeysTable
	 */
	@GET
	@Path("/ui/apikeys")
	public void getApiKeysTable() {
		try {
			LOGGER.info("Fetching list of all api keys.");

			uiService.getApiKeysTable(getDmaapContext());

			LOGGER.info("Returning list of all api keys.");
		} catch (ConfigDbException | IOException excp) {
			LOGGER.error("Error while fetching list of all api keys: " + excp.getMessage(), excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching list of all api keys: " + excp.getMessage());
		}
	}

	/**
	 * getApiKey
	 * 
	 * @param apiKey
	 * @exception Exception
	 */
	@GET
	@Path("/ui/apikeys/{apiKey}")
	public void getApiKey(@PathParam("apiKey") String apiKey) {
		try {
			LOGGER.info("Fetching details of api key: " + apiKey);

			uiService.getApiKey(getDmaapContext(), apiKey);

			LOGGER.info("Returning details of api key: " + apiKey);
		} catch (Exception excp) {
			LOGGER.error("Error while fetching details of api key: " + apiKey, excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching details of api key: " + apiKey);
		}
	}

	@GET
	@Path("/ui/topics")
	public void getTopicsTable() {
		try {
			LOGGER.info("Fetching list of all topics.");

			uiService.getTopicsTable(getDmaapContext());

			LOGGER.info("Returning list of all topics.");
		} catch (ConfigDbException | IOException excp) {
			LOGGER.error("Error while fetching list of all topics: " + excp, excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching list of all topics: " + excp.getMessage());
		}
	}

	/**
	 * 
	 * @param topic
	 */
	@GET
	@Path("/ui/topics/{topic}")
	public void getTopic(@PathParam("topic") String topic) {
		try {
			LOGGER.info("Fetching details of topic: " + topic);

			uiService.getTopic(getDmaapContext(), topic);

			LOGGER.info("Returning details of topic: " + topic);
		} catch (ConfigDbException | IOException | TopicExistsException excp) {
			LOGGER.error("Error while fetching details of topic: " + topic, excp);
			DMaaPResponseBuilder.respondWithError(getDmaapContext(), HttpStatus.SC_NOT_FOUND,
					"Error while fetching details of topic: " + topic);
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
		dmaapContext.setConfigReader(configReader);
		dmaapContext.setRequest(request);
		dmaapContext.setResponse(response);
		return dmaapContext;
	}
}
