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

 package org.onap.dmaap.mr.cambria.resources.streamReaders;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.Publisher.message;
import org.onap.dmaap.dmf.mr.resources.streamReaders.CambriaRawStreamReader;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;

public class CambriaRawStreamReaderTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNext() {
		
		CambriaRawStreamReader test = null;
		message msg = null;

		String source = "{'name': 'tester', 'id': '2'}";
		InputStream stream = null;
		try {
			stream = IOUtils.toInputStream(source, "UTF-8");
			test = new CambriaRawStreamReader(stream,"hello");
			msg = test.next();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CambriaApiException e1) {
			e1.printStackTrace();
		}
		
		assertNotNull(msg);
	
		
	}
	

}
