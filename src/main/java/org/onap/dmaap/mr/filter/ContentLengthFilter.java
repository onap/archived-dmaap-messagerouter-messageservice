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
package org.onap.dmaap.mr.filter;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.mr.filter.DefaultLength;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Servlet Filter implementation class ContentLengthFilter
 */
public class ContentLengthFilter implements Filter {

	private DefaultLength defaultLength;

	private FilterConfig filterConfig = null;
	DMaaPErrorMessages errorMessages = null;

	private static final EELFLogger log = EELFManager.getInstance().getLogger(org.onap.dmaap.mr.filter.ContentLengthFilter.class);
	/**
	 * Default constructor.
	 */

	public ContentLengthFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {
		log.info("inside servlet do filter content length checking before pub/sub");
		HttpServletRequest request = (HttpServletRequest) req;
		JSONObject jsonObj = null;
		int requestLength = 0;
		try {
			// retrieving content length from message header

			if (null != request.getHeader("Content-Length")) {
				requestLength = Integer.parseInt(request.getHeader("Content-Length"));
			}
			// retrieving encoding from message header
			String transferEncoding = request.getHeader("Transfer-Encoding");
			// checking for no encoding, chunked and requestLength greater then
			// default length
			if (null != transferEncoding && !(transferEncoding.contains("chunked"))
					&& (requestLength > Integer.parseInt(defaultLength.getDefaultLength()))) {
				jsonObj = new JSONObject().append("defaultlength", defaultLength)
						.append("requestlength", requestLength);
				log.error("message length is greater than default");
				throw new CambriaApiException(jsonObj);
			} else if (null == transferEncoding && (requestLength > Integer.parseInt(defaultLength.getDefaultLength()))) {
				jsonObj = new JSONObject().append("defaultlength", defaultLength.getDefaultLength()).append(
						"requestlength", requestLength);
				log.error("Request message is not chunked or request length is greater than default length");
				throw new CambriaApiException(jsonObj);
			} else {
				chain.doFilter(req, res);
			}
		} catch (CambriaApiException | NumberFormatException e) {
			log.error("message size is greater then default", e);
            if (jsonObj != null) {
                ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_EXPECTATION_FAILED,
                        DMaaPResponseCode.MSG_SIZE_EXCEEDS_MSG_LIMIT.getResponseCode(),
                        errorMessages.getMsgSizeExceeds()
                                + jsonObj.toString());
                log.info(errRes.toString());
            }
		}

	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		this.filterConfig = fConfig;
		log.info("Filter Content Length Initialize");
		ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(fConfig
				.getServletContext());
		DefaultLength defLength = (DefaultLength) ctx.getBean("defLength");
		DMaaPErrorMessages errMessages = (DMaaPErrorMessages) ctx.getBean("DMaaPErrorMessages");
		this.errorMessages = errMessages;
		this.defaultLength = defLength;

	}

}
