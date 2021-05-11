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
package org.onap.dmaap.dmf.mr;

import com.att.nsa.apiServer.NsaAppException;
import org.json.JSONObject;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;

public class CambriaApiException extends NsaAppException
{
	/*
	 * defined long type constant serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private transient ErrorResponse errRes;
	/**
	 * Implements constructor CambriaApiException
	 * @param jsonObject
	 * 
	 */
	public CambriaApiException ( JSONObject jsonObject )
	{
		super ( jsonObject );
	}

	/**
	 * Implements constructor CambriaApiException
	 * @param status
	 * @param msg
	 */
	public CambriaApiException ( int status, String msg )
	{
		super ( status, msg );
	}

	/**
	 * Implements constructor CambriaApiException
	 * @param status
	 * @param jsonObject
	 */
	public CambriaApiException ( int status, JSONObject jsonObject )
	{
		super ( status, jsonObject );
	}
	
	public CambriaApiException (ErrorResponse errRes)
	{
		super(errRes.getHttpStatusCode(),errRes.getErrorMessage());
		this.errRes = errRes;
	}
	
	public ErrorResponse getErrRes() {
		return errRes;
	}

	public void setErrRes(ErrorResponse errRes) {
		this.errRes = errRes;
	}
}
