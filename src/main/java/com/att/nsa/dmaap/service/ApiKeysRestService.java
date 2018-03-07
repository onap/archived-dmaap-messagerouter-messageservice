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
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.ApiKeyBean;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.service.ApiKeysService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.db.NsaApiDb.KeyExistsException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * This class is a CXF REST service 
 * which acts as gateway for Cambria Api
 * Keys.
 * @author author
 *
 */
@Component
@Path("/")
public class ApiKeysRestService {

	/**
	 * Logger obj
	 */
	//private Logger log = Logger.getLogger(ApiKeysRestService.class.toString());
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ApiKeysRestService.class);
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
	 * ApiKeysService obj
	 */
	@Autowired
	private ApiKeysService apiKeyService;

	/**
	 * Returns a list of all the existing Api keys
	 * @throws CambriaApiException 
	 * 
	 * @throws IOException
	 * */
	@GET
	public void getAllApiKeys() throws CambriaApiException {

		log.info("Inside ApiKeysRestService.getAllApiKeys");

		try {
			apiKeyService.getAllApiKeys(ServiceUtil.getDMaaPContext(configReader, request, response));
			log.info("Fetching all API keys is Successful");
		} catch (ConfigDbException | IOException e) {
			log.error("Error while retrieving API keys: " + e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while retrieving API keys: "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}

	}

	/**
	 * Returns details of a particular api key whose <code>name</code> is passed
	 * as a parameter
	 * 
	 * @param apiKeyName
	 *            - name of the api key
	 * @throws CambriaApiException 
	 * @throws IOException
	 * */
	@GET
	@Path("/{apiKey}")
	public void getApiKey(@PathParam("apiKey") String apiKeyName) throws CambriaApiException {
		log.info("Fetching details of api key: " + apiKeyName);

		try {
			apiKeyService.getApiKey(ServiceUtil.getDMaaPContext(configReader, request, response), apiKeyName);
			log.info("Fetching specific API key is Successful");
		} catch (ConfigDbException | IOException e) {
			log.error("Error while retrieving API key details: " + e);
			
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while retrieving API key details: "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}
	
	

	/**
	 * Creates api key using the <code>email</code> and <code>description</code>
	 * 
	 * @param nsaApiKey
	 * @throws CambriaApiException 
	 * @throws JSONException 
	 * */
	@POST
	@Path("/create")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createApiKey(ApiKeyBean nsaApiKey) throws CambriaApiException, JSONException {
		log.info("Creating Api Key.");

		try {
			apiKeyService.createApiKey(ServiceUtil.getDMaaPContext(configReader, request, response), nsaApiKey);
			log.info("Creating API key is Successful");
		} catch (KeyExistsException | ConfigDbException | IOException e) {
			log.error("Error while Creating API key : " + e.getMessage(), e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while Creating API key : "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}

	}

	/**
	 * Updates an existing apiKey using the key name passed a parameter and the
	 * details passed.
	 * 
	 * @param apiKeyName
	 *            - name of the api key to be updated
	 * @param nsaApiKey
	 * @throws CambriaApiException 
	 * @throws JSONException 
	 * @throws IOException
	 * @throws AccessDeniedException
	 * */
	@PUT
	@Path("/{apiKey}")
	public void updateApiKey(@PathParam("apiKey") String apiKeyName,
			ApiKeyBean nsaApiKey) throws CambriaApiException, JSONException {
		log.info("Updating Api Key.");

		try {
			
			apiKeyService
					.updateApiKey(ServiceUtil.getDMaaPContext(configReader, request, response), apiKeyName, nsaApiKey);
			log.error("API key updated sucessfully");
		} catch (ConfigDbException | IOException | AccessDeniedException e) {
			log.error("Error while Updating API key : " + apiKeyName, e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while Updating API key : "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
	}

	/**
	 * Deletes an existing apiKey using the key name passed as a parameter.
	 * 
	 * @param apiKeyName
	 *            - name of the api key to be updated
	 * @throws CambriaApiException 
	 * @throws IOException
	 * @throws AccessDeniedException
	 * */
	@DELETE
	@Path("/{apiKey}")
	public void deleteApiKey(@PathParam("apiKey") String apiKeyName) throws CambriaApiException {
		log.info("Deleting Api Key: " + apiKeyName);
		try {
			apiKeyService.deleteApiKey(ServiceUtil.getDMaaPContext(configReader, request, response), apiKeyName);
			log.info("Api Key deleted successfully: " + apiKeyName);
		} catch (ConfigDbException | IOException | AccessDeniedException e) {
			log.error("Error while deleting API key : " + apiKeyName, e);

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GENERIC_INTERNAL_ERROR.getResponseCode(), 
					"Error while deleting API key : "+ e.getMessage());
			log.info(errRes.toString());
			throw new CambriaApiException(errRes);

		}
	}


}