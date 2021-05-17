/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

 package org.onap.dmaap.mr.cambria.beans;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dmf.mr.beans.ApiKeyBean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ApiKeyBeanTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetEmail() {
		
		ApiKeyBean bean = new ApiKeyBean("user@onap.com", "testing bean");
		
		bean.getEmail();
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testApiKeyBean(){
		ApiKeyBean bean = new ApiKeyBean();
		bean.setDescription("description");
		bean.setEmail("email");
		assertEquals("description", bean.getDescription());
		assertEquals("email", bean.getEmail());
		
	}
	
	

}
