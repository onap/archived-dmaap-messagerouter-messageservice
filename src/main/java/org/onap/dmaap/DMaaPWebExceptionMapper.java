/*******************************************************************************
 *  ============LICENSE_START=======================================================
 *  org.onap.dmaap
 *  ================================================================================
 *  Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 *
 *  Modifications Copyright (C) 2019 IBM.
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


import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAllowedException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServiceUnavailableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;

/**
 * Exception Mapper class to handle
 * Web Exceptions
 * @author rajashree.khare
 *
 */
@Provider
@Singleton
public class DMaaPWebExceptionMapper implements ExceptionMapper<WebApplicationException>{
	
	/**
	 * Logger obj
	 */

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(DMaaPWebExceptionMapper.class);
	/**
	 * Error response obj
	 */
	private ErrorResponse errRes;
	/**
	 * Error msg obj
	 */
	@Autowired
	private DMaaPErrorMessages msgs;
	
	/**
	 * Contructor for DMaaPWebExceptionMapper
	 */
	public DMaaPWebExceptionMapper() {
		super();
		LOGGER.info("WebException Mapper Created..");
	}

	/**
	 * The toResponse method is called when 
	 * an exception of type WebApplicationException
	 * is thrown.This method will send a custom error
	 * response to the client
	 */
	@Override
	public Response toResponse(WebApplicationException ex) {
		LOGGER.info("Reached WebException Mapper");
		
		/**
		 * Resource Not Found
		 */
		if(ex instanceof NotFoundException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_NOT_FOUND,DMaaPResponseCode.RESOURCE_NOT_FOUND.
					getResponseCode(),msgs.getNotFound());
			
			LOGGER.info(errRes.toString());

			return Response.status(errRes.getHttpStatusCode()).header("exception",
					errRes.getErrMapperStr()).build();
			
		}
		/**
		 * Internal Server Error
		 */
		if(ex instanceof InternalServerErrorException)
		{
		
			int errCode = HttpStatus.SC_INTERNAL_SERVER_ERROR;
			int dmaapErrCode = DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode();
			String errMsg = msgs.getServerUnav();
			
		
			if(ex.getCause().toString().contains("Json")) {
				errCode = HttpStatus.SC_BAD_REQUEST;
				dmaapErrCode = DMaaPResponseCode.INCORRECT_JSON.getResponseCode();
				errMsg = ex.getCause().getMessage().substring(0, ex.getCause().getMessage().indexOf("[Source")-3);
			}
			else if (ex.getCause().toString().contains("UnrecognizedPropertyException")) {
				errCode = HttpStatus.SC_BAD_REQUEST;
				dmaapErrCode = DMaaPResponseCode.INCORRECT_JSON.getResponseCode();
				errMsg = ex.getCause().getMessage().substring(0, ex.getCause().getMessage().indexOf("[Source")-3);
			}
			errRes = new ErrorResponse(errCode,dmaapErrCode,errMsg);
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).header("exception",
					errRes.getErrMapperStr()).build();
		}
		/**
		 * UnAuthorized 
		 */
		if(ex instanceof NotAuthorizedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,DMaaPResponseCode.ACCESS_NOT_PERMITTED.
					getResponseCode(),msgs.getAuthFailure());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).header("exception",
					errRes.getErrMapperStr()).build();
		}
		/**
		 * Malformed request
		 */
		if(ex instanceof BadRequestException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,DMaaPResponseCode.INCORRECT_JSON.
					getResponseCode(),msgs.getBadRequest());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).header("exception",
					errRes.getErrMapperStr()).build();
		}
		/**
		 * HTTP Method not allowed
		 */
		if(ex instanceof NotAllowedException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_METHOD_NOT_ALLOWED,DMaaPResponseCode.METHOD_NOT_ALLOWED.
					getResponseCode(),msgs.getMethodNotAllowed());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).header("exception",
					errRes.getErrMapperStr()).build();
		}
		
		/**
		 * Server unavailable
		 */
		if(ex instanceof ServiceUnavailableException)
		{
			errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,DMaaPResponseCode.SERVER_UNAVAILABLE.
					getResponseCode(),msgs.getServerUnav());
			
			LOGGER.info(errRes.toString());
			return Response.status(errRes.getHttpStatusCode()).header("exception",
					errRes.getErrMapperStr()).build();
		}
		
		
		
		return Response.serverError().build();
	}

	

	
}

