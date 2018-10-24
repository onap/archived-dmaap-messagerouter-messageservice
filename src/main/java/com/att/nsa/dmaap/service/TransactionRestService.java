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

import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.aft.dme2.internal.jettison.json.JSONException;
import  org.onap.dmaap.dmf.mr.CambriaApiException;
import  org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import  org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import  org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import  org.onap.dmaap.dmf.mr.service.TransactionService;
import  org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import com.att.nsa.configs.ConfigDbException;

/**
 * This class is a CXF REST service 
 * which acts as gateway for DMaaP
 * Transaction Ids.
 * @author rajashree.khare
 *
 */
@Component
@Path("/")
public class TransactionRestService {

	/**
	 * Logger obj
	 */
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(TransactionRestService.class);

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
	private TransactionService transactionService;

	/**
	 * 
	 * Returns a list of all the existing Transaction Ids
	 * @throws CambriaApiException 
	 * 
	 * @throws IOException
	 * @exception ConfigDbException
	 * @exception IOException
	 * 
	 * 
	 */
	@GET
	public void getAllTransactionObjs() throws CambriaApiException {
		try {
			LOGGER.info("Retrieving list of all transactions.");

			transactionService.getAllTransactionObjs(getDmaapContext());

			LOGGER.info("Returning list of all transactions.");
		} catch (ConfigDbException | IOException e) {
			LOGGER.error("Error while retrieving list of all transactions: "
					+ e.getMessage(), e);
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_EXPECTATION_FAILED, 
					DMaaPResponseCode.RETRIEVE_TRANSACTIONS.getResponseCode(), 
					"Error while retrieving list of all transactions:"+e.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	/**
	 * 
	 * Returns details of a particular transaction id whose <code>name</code> is
	 * passed as a parameter
	 * 
	 * @param transactionId
	 *            - id of transaction
	 * @throws CambriaApiException 
	 * @throws IOException
	 * @exception ConfigDbException
	 * @exception IOException
	 * @exception JSONException
	 * 
	 * 
	 */
	@GET
	@Path("/{transactionId}")
	public void getTransactionObj(
			@PathParam("transactionId") String transactionId) throws CambriaApiException {

		LOGGER.info("Fetching details of Transaction ID : " + transactionId);

		try {
			transactionService.getTransactionObj(getDmaapContext(),
					transactionId);
		} catch (ConfigDbException | JSONException | IOException e) {
			LOGGER.error("Error while retrieving transaction details for id: "
					+ transactionId, e);
		
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_EXPECTATION_FAILED, 
					DMaaPResponseCode.RETRIEVE_TRANSACTIONS_DETAILS.getResponseCode(), 
					"Error while retrieving transaction details for id: ["
							+ transactionId + "]: " + e.getMessage());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);

		}

		LOGGER.info("Returning details of transaction " + transactionId);

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