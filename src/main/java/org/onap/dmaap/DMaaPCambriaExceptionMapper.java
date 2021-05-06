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
 package org.onap.dmaap;


import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Exception Mapper class to handle
 * CambriaApiException 
 * @author rajashree.khare
 *
 */
@Provider
@Singleton
public class DMaaPCambriaExceptionMapper implements ExceptionMapper<CambriaApiException>{

/**
 * Error response obj
 */
	private ErrorResponse errRes;

/**
 * Logger obj
 */
	

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DMaaPCambriaExceptionMapper.class);
	

	/**
	 * Error msg obj
	 */
	@Autowired
	private DMaaPErrorMessages msgs;
	
	/**
	 * HttpServletRequest obj
	 */
	@Context
	private HttpServletRequest req;
	
	/**
	 * HttpServletResponse obj
	 */
	@Context
	private HttpServletResponse res;
	
	/**
	 * Contructor for DMaaPCambriaExceptionMapper
	 */
	public DMaaPCambriaExceptionMapper() {
		super();
		LOGGER.info("Cambria Exception Mapper Created..");
	}
	
	/**
	 * The toResponse method is called when 
	 * an exception of type CambriaApiException
	 * is thrown.This method will send a custom error
	 * response to the client.
	 */
	@Override
	public Response toResponse(CambriaApiException ex) {

		LOGGER.info("Reached Cambria Exception Mapper..");
		
		/**
		 * Cambria Generic Exception
		 */
		/*if(ex instanceof CambriaApiException)
		{*/
			
			errRes = ex.getErrRes();
			if(errRes!=null) {
				
				Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
						errRes.getErrMapperStr()).build();
				
				return response;
			}
			else
			{
				
				Response response = Response.status(ex.getStatus()).header("exception",
						ex.getMessage()).build();
				
				return response;
			}
			
			
		/*}
		else
		{
			errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND, 
					DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(), msgs.getServerUnav());
			
			Response response = Response.status(errRes.getHttpStatusCode()).header("exception", 
					errRes.getErrMapperStr()).build();
			
			return response;
		}*/
		
	}

	
	
}
