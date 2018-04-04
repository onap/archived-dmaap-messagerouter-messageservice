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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

//import com.att.eelf.configuration.EELFLogger;
//import com.att.eelf.configuration.EELFManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.att.cadi.filter.CadiFilter;
import javax.servlet.FilterConfig;

/**
 * This is a Servlet Filter class overriding the AjscCadiFilter
 */
@Component
public class DMaaPAuthFilter extends CadiFilter {

	// private Logger log = Logger.getLogger(DMaaPAuthFilter.class.toString());

	// private static final EELFLogger log =
	// EELFManager.getInstance().getLogger(DMaaPAuthFilter.class);
	private Logger log = LoggerFactory.getLogger(DMaaPAuthFilter.class);

	final Boolean enabled = "authentication-scheme-1".equalsIgnoreCase(System.getProperty("CadiAuthN"));

	/**
	 * This method will disable Cadi Authentication if cambria headers are
	 * present in the request else continue with Cadi Authentication
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

		try {

			super.init(filterConfig);

		} catch (Exception ex) {
			log.error("Ajsc Cadi Filter Exception" + ex);

		}
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		log.info("inside servlet filter Cambria Auth Headers checking before doing other Authentication");
		HttpServletRequest request = (HttpServletRequest) req;

		boolean forceAAF = Boolean.valueOf(System.getProperty("forceAAF"));
		if (forceAAF || 
			null != request.getHeader("Authorization") || 
			(null != request.getHeader("AppName") && 
				request.getHeader("AppName").equalsIgnoreCase("invenio") )) {

			if (!enabled || 
					request.getMethod().equalsIgnoreCase("head") || 
					request.getHeader("DME2HealthCheck") != null) {
				
				chain.doFilter(req, res);
				
			} else {
				
				super.doFilter(req, res, chain);
				
			}
		} else {

			System.setProperty("CadiAuthN", "authentication-scheme-2");
			chain.doFilter(req, res);

		}

	}

	@Override
	public void log(Exception e, Object... elements) {
		// TODO Auto-generated method stub
		// super.log(e, elements);
		// System.out.println(convertArrayToString(elements));
		log.error(convertArrayToString(elements), e);

	}

	@Override
	public void log(Level level, Object... elements) {

		// System.out.println(willWrite().compareTo(level) );
		if (willWrite().compareTo(level) <= 0) {
			switch (level) {
			case DEBUG:
				log.debug(convertArrayToString(elements));
				break;
			case INFO:
				log.info(convertArrayToString(elements));
				break;
			case ERROR:
				log.error(convertArrayToString(elements));
				break;
			case AUDIT:
				log.info(convertArrayToString(elements));
				break;
			case INIT:
				log.info(convertArrayToString(elements));
				break;
			case WARN:
				log.warn(convertArrayToString(elements));
				break;
			default:

				log.warn(convertArrayToString(elements));

			}

		}

	}

	private String convertArrayToString(Object[] elements) {

		StringBuilder strBuilder = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			if (elements[i] instanceof String)
				strBuilder.append((String) elements[i]);
			else if (elements[i] instanceof Integer)
				strBuilder.append((Integer) elements[i]);
			else
				strBuilder.append(elements[i]);
		}
		String newString = strBuilder.toString();
		return newString;
	}

}
