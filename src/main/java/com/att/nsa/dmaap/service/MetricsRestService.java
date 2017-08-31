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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.service.MetricsService;
import com.att.nsa.cambria.utils.ConfigurationReader;

/**
 * This class is a CXF REST service which acts 
 * as gateway for MR Metrics Service.
 * @author author
 *
 */
@Component
@Path("/")
public class MetricsRestService {

	/**
	 * Logger obj
	 */
	//private Logger log = Logger.getLogger(MetricsRestService.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ConfigurationReader.class);
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

	/**
	 * MetricsService obj
	 */
	@Autowired
	private MetricsService metricsService;

	/**
	 * Get Metrics method
	 * @throws CambriaApiException ex
	 */
	@GET
	@Produces("text/plain")
	public void getMetrics() throws CambriaApiException {
		try {
			log.info("MetricsRestService: getMetrics : START");
			metricsService.get(getDmaapContext());
			log.info("MetricsRestService: getMetrics : Completed");
		} catch (IOException e) {
			log.error("Error while fetching metrics data : ", e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_FORBIDDEN, 
					DMaaPResponseCode.GET_METRICS_ERROR.getResponseCode(), 
					"Error while fetching metrics data"+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * This method is for get the metrics details by the metrics name
	 * 
	 * @param metricName
	 * @throws CambriaApiException 
	 */
	@GET
	@Path("/{metricName}")
	@Produces("text/plain")
	public void getMetricsByName(@PathParam("metricName") String metricName) 
			throws CambriaApiException {

		try {
			log.info("MetricsProducer: getMetricsByName : START");
			metricsService.getMetricByName(getDmaapContext(), metricName);
			log.info("MetricsRestService: getMetricsByName : Completed");
		} catch (IOException | CambriaApiException e) {
			log.error("Error while fetching metrics data : ", e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GET_METRICS_ERROR.getResponseCode(), 
					"Error while fetching metrics data"+ e.getMessage());
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
		dmaapContext.setConfigReader(configReader);
		dmaapContext.setRequest(request);
		dmaapContext.setResponse(response);
		return dmaapContext;
	}

}