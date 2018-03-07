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
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.http.HttpStatus;
//import org.apache.log4j.Logger;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import com.att.nsa.cambria.service.AdminService;
import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;

/**
 * Rest Service class
 * for Admin Services
 * @author author
 *
 */
@Component
@Path("/")
public class AdminRestService {

	/**
	 * Logger obj
	 */
	//private static final Logger LOGGER = Logger
		//	.getLogger(AdminRestService.class);
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(AdminRestService.class);
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
	 * AdminService obj
	 */
	@Autowired
	private AdminService adminService;
	
	private DMaaPContext dmaaPContext;

	/**
	 * Fetches a list of all the registered consumers along with their created
	 * time and last accessed details
	 * 
	 * @return consumer list in json string format
	 * @throws CambriaApiException 
	 * @throws AccessDeniedException 
	 * @throws IOException
	 * */
	@GET
	@Path("/consumerCache")
	//@Produces(MediaType.TEXT_PLAIN)
	public void getConsumerCache() throws CambriaApiException, AccessDeniedException {
		LOGGER.info("Fetching list of registered consumers.");
		try {
			adminService.showConsumerCache(ServiceUtil.getDMaaPContext(configReader, request, response));
			LOGGER.info("Fetching Consumer Cache Successfully");
		} catch (IOException e) {
			LOGGER.error("Error while Fetching list of registered consumers : "
					+ e.getMessage(), e);
			
					
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GET_CONSUMER_CACHE.getResponseCode(), 
					"Error while Fetching list of registered consumers " + e.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * Clears consumer cache
	 * @throws CambriaApiException ex
	 * @throws AccessDeniedException 
	 * 
	 * @throws IOException ex
	 * @throws JSONException ex
	 * */
	@POST
	@Path("/dropConsumerCache")
	//@Produces(MediaType.TEXT_PLAIN)
	public void dropConsumerCache() throws CambriaApiException, AccessDeniedException {
		LOGGER.info("Dropping consumer cache");
		try {
			adminService.dropConsumerCache(ServiceUtil.getDMaaPContext(configReader, request, response));
			LOGGER.info("Dropping Consumer Cache successfully");
		} catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while dropConsumerCache : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch (JSONException | IOException e) {
			LOGGER.error(
					"Error while Dropping consumer cache : " + e.getMessage(),
					e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.DROP_CONSUMER_CACHE.getResponseCode(), 
					"Error while Dropping consumer cache " + e.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}
	
	/**
	 * Get list of blacklisted ips
	 * @throws CambriaApiException excp
	 */
	@GET
	@Path("/blacklist")
	//@Produces(MediaType.TEXT_PLAIN)
	public void getBlacklist() throws CambriaApiException {
		LOGGER.info("Fetching list of blacklist ips.");
		try {
			Enumeration headerNames = ServiceUtil.getDMaaPContext(configReader, request, response).getRequest().getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
			
			}
			
			adminService.getBlacklist(ServiceUtil.getDMaaPContext(configReader, request, response));
			LOGGER.info("Fetching list of blacklist ips Successfully");
		}catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while Fetching list  of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch ( IOException excp) {
			LOGGER.error("Error while Fetching list  of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
			
	}

	/**
	 * Add ip to list of blacklist ips
	 * @param ip ip
	 * @throws CambriaApiException excp
	 */
	@POST
	@Path("/blacklist/{ip}")
	//@Produces(MediaType.TEXT_PLAIN)
	public void addToBlacklist (@PathParam("ip") String ip ) throws CambriaApiException
	{
		LOGGER.info("Adding ip to list of blacklist ips.");
		try {
			adminService.addToBlacklist(ServiceUtil.getDMaaPContext(configReader, request, response), ip);
			LOGGER.info("Fetching list of blacklist ips Successfully");
		} catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while blacklist : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while Fetching list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		} catch (IOException |  ConfigDbException excp) {
			LOGGER.error("Error while adding ip to list of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.ADD_BLACKLIST.getResponseCode(), 
					"Error while adding ip to list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
		
	}
	/**
	 * Remove ip from blacklist
	 * @param ip ip
	 * @throws CambriaApiException excp
	 * @throws AccessDeniedException excp
	 * @throws ConfigDbException excp
	 */
	@DELETE
	@Path("/blacklist/{ip}")
	//@Produces(MediaType.TEXT_PLAIN)
	public void removeFromBlacklist(@PathParam("ip") String ip) throws CambriaApiException, AccessDeniedException, ConfigDbException {
		LOGGER.info("Fetching list of blacklist ips.");
		try {
			adminService.removeFromBlacklist(ServiceUtil.getDMaaPContext(configReader, request, response), ip);
			LOGGER.info("Fetching list of blacklist ips Successfully");
		}catch ( AccessDeniedException   excp) {
			LOGGER.error("Error while blacklist : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED, 
					DMaaPResponseCode.GET_BLACKLIST.getResponseCode(), 
					"Error while removeFromBlacklist list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}  catch (IOException |  ConfigDbException excp) {
			LOGGER.error("Error while removing ip from list of blacklist ips : "
					+ excp.getMessage(), excp);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.REMOVE_BLACKLIST.getResponseCode(), 
					"Error while removing ip from list of blacklist ips " + excp.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
			
		}
	}

	

}
