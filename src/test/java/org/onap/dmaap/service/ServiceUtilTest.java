/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2019 IBM Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dmaap.service;

import org.junit.Test;
import org.mockito.Mockito;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ServiceUtilTest {

    @Test
    public void getDMaaPContext() {
        ConfigurationReader configurationReader = Mockito.mock(ConfigurationReader.class);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        DMaaPContext actual = ServiceUtil.getDMaaPContext(configurationReader, request, response);
        assertThat(actual.getConfigReader(), is(configurationReader));
        assertThat(actual.getRequest(), is(request));
        assertThat(actual.getResponse(), is(response));
    }
}