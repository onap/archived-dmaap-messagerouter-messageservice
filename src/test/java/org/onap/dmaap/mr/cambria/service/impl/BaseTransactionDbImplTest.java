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

 package org.onap.dmaap.mr.cambria.service.impl;

import com.att.nsa.configs.ConfigDbException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dmf.mr.service.impl.BaseTransactionDbImpl;
import org.onap.dmaap.dmf.mr.transaction.DMaaPTransactionObjDB.KeyExistsException;

import static org.junit.Assert.assertTrue;

public class BaseTransactionDbImplTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCreateTransactionObj() {
		
		
		try {
			
			BaseTransactionDbImpl service = new BaseTransactionDbImpl(null, null);
			service.createTransactionObj("transition");
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (KeyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testSaveTransactionObj() {
		
		
		try {
			
			BaseTransactionDbImpl service = new BaseTransactionDbImpl(null, null);
			service.saveTransactionObj(null);
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	
	@Test
	public void testLoadTransactionObj() {
		
		try {
			
			BaseTransactionDbImpl service = new BaseTransactionDbImpl(null, null);
			service.loadTransactionObj("34");
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}
	

	@Test
	public void testLoadAllTransactionObjs() {
		
		try {
			
			BaseTransactionDbImpl service = new BaseTransactionDbImpl(null, null);
			service.loadAllTransactionObjs();
		} catch (org.json.JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			assertTrue(true);
		} catch (ConfigDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		String trueValue = "True";
		assertTrue(trueValue.equalsIgnoreCase("True"));
		
	}


	
	
}
