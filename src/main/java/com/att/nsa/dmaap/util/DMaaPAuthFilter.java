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

import com.att.dmf.mr.utils.Utils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.stereotype.Component;

import org.onap.aaf.cadi.filter.CadiFilter;
//import ajsc.external.plugins.cadi.AjscCadiFilter;
import javax.servlet.FilterConfig;

/**
	 * This is a Servlet Filter class
	 * overriding the AjscCadiFilter
	 */
@Component	
public class DMaaPAuthFilter extends CadiFilter {
	
		//private Logger log = Logger.getLogger(DMaaPAuthFilter.class.toString());

		private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPAuthFilter.class);
				
		public DMaaPAuthFilter() throws Exception {
			super();
		}
		
	/*	public void init(FilterConfig filterConfig) throws ServletException {
              
			super.init(filterConfig);
              System.out.println("---------------------------- in init method");
		}*/
		
		/**
		 * This method will disable Cadi Authentication 
		 * if cambria headers are present in the request
		 * else continue with Cadi Authentication
		 */
		@Override
		public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
				ServletException {
		log.info("inside servlet filter Cambria Auth Headers checking before doing other Authentication");
			HttpServletRequest request = (HttpServletRequest) req;
				boolean forceAAF = Boolean.valueOf(System.getProperty("forceAAF"));
				
				//if (forceAAF || null != request.getHeader("Authorization") ){
					if (Utils.isCadiEnabled()&&(forceAAF || null != request.getHeader("Authorization") || 
							(null != request.getHeader("AppName") &&  request.getHeader("AppName").equalsIgnoreCase("invenio") &&
							 null != request.getHeader("cookie")))){
						super.doFilter(req, res, chain);
						
				} else { 
					System.setProperty("CadiAuthN", "authentication-scheme-2");
						chain.doFilter(req, res);
					
					
				} 

		}

	}

