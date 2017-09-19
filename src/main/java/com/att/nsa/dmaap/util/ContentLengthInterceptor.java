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
package com.att.nsa.dmaap.util;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpStatus;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.exception.DMaaPResponseCode;
import com.att.nsa.cambria.exception.ErrorResponse;
import ajsc.beans.interceptors.AjscInterceptor;

/**
 * AJSC Intercepter implementation of ContentLengthFilter
 */
@Component
public class ContentLengthInterceptor implements AjscInterceptor{

	
	private String defLength;
	private static final EELFLogger log = EELFManager.getInstance().getLogger(ContentLengthInterceptor.class);


	/**
	 * Intercepter method to intercept requests before processing
	 */
	@Override
	public boolean allowOrReject(HttpServletRequest httpservletrequest, HttpServletResponse httpservletresponse,
			Map map) throws Exception {
				
		log.info("inside Interceptor allowOrReject content length checking before pub/sub");
		
		JSONObject jsonObj = null;
		int requestLength = 0;
		setDefLength(System.getProperty("maxcontentlength"));
		try {
			// retrieving content length from message header

			if (null != httpservletrequest.getHeader("Content-Length")) {
				requestLength = Integer.parseInt(httpservletrequest.getHeader("Content-Length"));
			}
			// retrieving encoding from message header
			String transferEncoding = httpservletrequest.getHeader("Transfer-Encoding");
			// checking for no encoding, chunked and requestLength greater then
			// default length
				if (null != transferEncoding && !(transferEncoding.contains("chunked"))
						&& (requestLength > Integer.parseInt(getDefLength()))) {
					jsonObj = new JSONObject().append("defaultlength", getDefLength())
							.append("requestlength", requestLength);
					log.error("message length is greater than default");
					throw new CambriaApiException(jsonObj);
				} 
				else if (null == transferEncoding && (requestLength > Integer.parseInt(getDefLength()))) 
				{
					jsonObj = new JSONObject().append("defaultlength", getDefLength()).append(
							"requestlength", requestLength);
					log.error("Request message is not chunked or request length is greater than default length");
					throw new CambriaApiException(jsonObj);
				
				
				} 
				else 
				{
				//chain.doFilter(req, res);
				return true;
				}
			
		} catch (CambriaApiException | NumberFormatException | JSONException e) {
			
			log.info("Exception obj--"+e);
			log.error("message size is greater then default"+e.getMessage());
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_REQUEST_TOO_LONG,
					DMaaPResponseCode.MSG_SIZE_EXCEEDS_MSG_LIMIT.getResponseCode(), System.getProperty("msg_size_exceeds")
							+ e.toString());
			log.info(errRes.toString());
			
			
			map.put(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"test");
			httpservletresponse.setStatus(HttpStatus.SC_REQUEST_TOO_LONG);
			httpservletresponse.getOutputStream().write(errRes.toString().getBytes());
			return false;
		}

		
		
	}

	
	/**
	 * Get Default Content Length
	 * @return defLength
	 */
	public String getDefLength() {
		return defLength;
	}
	/**
	 * Set Default Content Length
	 * @param defLength
	 */
	public void setDefLength(String defLength) {
		this.defLength = defLength;
	}

	

}
