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
package org.onap.dmaap.util;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.utils.Utils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.stereotype.Component;

import org.onap.aaf.cadi.filter.CadiFilter;

/**
 * This is a Servlet Filter class overriding the AjscCadiFilter
 */
@Component
public class DMaaPAuthFilter extends CadiFilter {

    private static final String FORCE_AAF_FLAG = "forceAAF";
    private static final String USE_CUSTOM_ACLS = "useCustomAcls";
    static final String X509_ATTR = "javax.servlet.request.X509Certificate";
    static final String AUTH_HEADER = "Authorization";
    static final String APP_HEADER = "AppName";
    static final String COOKIE_HEADER = "cookie";
    private static final EELFLogger log = EELFManager.getInstance().getLogger(DMaaPAuthFilter.class);

    public DMaaPAuthFilter() {
        super();
    }

    /**
     * This method will disable Cadi Authentication if cambria headers are present in the request else continue with
     * Cadi Authentication
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws ServletException, IOException {
        log.info("inside servlet filter Cambria Auth Headers checking before doing other Authentication");
        if (shouldFilterWithCADI((HttpServletRequest) req)) {
            super.doFilter(req, res, chain);
        } else {
            System.setProperty("CadiAuthN", "authentication-scheme-2");
            chain.doFilter(req, res);
        }
    }

    boolean shouldFilterWithCADI(HttpServletRequest request) {
        return isCadiEnabled() &&
            (isAAFforced() || isAuthDataProvided(request) || isInvenioApp(request));
    }

    private boolean isAuthDataProvided(HttpServletRequest request) {
        return (null != request.getHeader(AUTH_HEADER)) || hasClientCertificate(request);
    }

    private boolean isInvenioApp(HttpServletRequest request) {
        return (null != request.getHeader(APP_HEADER)) && request.getHeader(APP_HEADER).equalsIgnoreCase("invenio") &&
            (null != request.getHeader(COOKIE_HEADER));
    }

    private boolean hasClientCertificate(HttpServletRequest request) {
        return request.getAttribute(X509_ATTR) != null;
    }

    boolean isCadiEnabled() {
        return Utils.isCadiEnabled();
    }

    boolean isAAFforced() {
        return Boolean.parseBoolean(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, FORCE_AAF_FLAG));
    }

    public static boolean isUseCustomAcls() {
        return Boolean.parseBoolean(AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, USE_CUSTOM_ACLS));
    }

}

