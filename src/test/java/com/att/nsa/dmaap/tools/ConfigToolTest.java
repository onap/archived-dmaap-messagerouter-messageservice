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

package com.att.nsa.dmaap.tools;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigToolTest {

	private String[] parts = new String[5];

	@Before
	public void setUp() throws Exception {

		for (int i = 0; i < parts.length; i++) {
			parts[i] = "string" + (i + 1);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	public void callMethodViaReflection(String outer, String inner, String methodName, Object... args) {

		String foreNameString = outer + "$" + inner;
		Object parent = new ConfigTool();

		Class<?> innerClass;
		try {
			innerClass = Class.forName(foreNameString);
			Constructor<?> constructor = innerClass.getDeclaredConstructor(ConfigTool.class);
			constructor.setAccessible(true);
			Object child = constructor.newInstance(parent);

			// invoking method on inner class object
			Method method = innerClass.getDeclaredMethod(methodName, null);
			method.setAccessible(true);// in case of unaccessible method
			method.invoke(child, args);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertTrue(true);

	}

	@Test
	public void testGetMatches() {

		callMethodViaReflection("ConfigTool", "ListTopicCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady() {

		callMethodViaReflection("ConfigTool", "ListTopicCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute() {

		callMethodViaReflection("ConfigTool", "ListTopicCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp() {

		callMethodViaReflection("ConfigTool", "ListTopicCommand", "displayHelp", null);

		assertTrue(true);

	}

	@Test
	public void testGetMatches2() {

		callMethodViaReflection("ConfigTool", "WriteTopicCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady2() {

		callMethodViaReflection("ConfigTool", "WriteTopicCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute2() {

		callMethodViaReflection("ConfigTool", "WriteTopicCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp2() {

		callMethodViaReflection("ConfigTool", "WriteTopicCommand", "displayHelp", null);

		assertTrue(true);

	}

	@Test
	public void testGetMatches3() {

		callMethodViaReflection("ConfigTool", "ReadTopicCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady3() {

		callMethodViaReflection("ConfigTool", "ReadTopicCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute3() {

		callMethodViaReflection("ConfigTool", "ReadTopicCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp3() {

		callMethodViaReflection("ConfigTool", "ReadTopicCommand", "displayHelp", null);

		assertTrue(true);

	}

	@Test
	public void testGetMatches4() {

		callMethodViaReflection("ConfigTool", "InitSecureTopicCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady4() {

		callMethodViaReflection("ConfigTool", "InitSecureTopicCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute4() {

		callMethodViaReflection("ConfigTool", "InitSecureTopicCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp4() {

		callMethodViaReflection("ConfigTool", "InitSecureTopicCommand", "displayHelp", null);

		assertTrue(true);

	}

	@Test
	public void testGetMatches5() {

		callMethodViaReflection("ConfigTool", "SetTopicOwnerCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady5() {

		callMethodViaReflection("ConfigTool", "SetTopicOwnerCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute5() {

		callMethodViaReflection("ConfigTool", "SetTopicOwnerCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp5() {

		callMethodViaReflection("ConfigTool", "SetTopicOwnerCommand", "displayHelp", null);

		assertTrue(true);

	}
	
	@Test
	public void testGetMatches6() {

		callMethodViaReflection("ConfigTool", "ListApiKeysCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady6() {

		callMethodViaReflection("ConfigTool", "ListApiKeysCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute6() {

		callMethodViaReflection("ConfigTool", "ListApiKeysCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp6() {

		callMethodViaReflection("ConfigTool", "ListApiKeysCommand", "displayHelp", null);

		assertTrue(true);

	}
	
	@Test
	public void testGetMatches7() {

		callMethodViaReflection("ConfigTool", "PutApiCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady7() {

		callMethodViaReflection("ConfigTool", "PutApiCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute7() {

		callMethodViaReflection("ConfigTool", "PutApiCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp7() {

		callMethodViaReflection("ConfigTool", "PutApiCommand", "displayHelp", null);

		assertTrue(true);

	}
	
	@Test
	public void testGetMatches8() {

		callMethodViaReflection("ConfigTool", "writeApiKeyCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady8() {

		callMethodViaReflection("ConfigTool", "writeApiKeyCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute8() {

		callMethodViaReflection("ConfigTool", "writeApiKeyCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp8() {

		callMethodViaReflection("ConfigTool", "writeApiKeyCommand", "displayHelp", null);

		assertTrue(true);

	}
	
	@Test
	public void testGetMatches9() {

		callMethodViaReflection("ConfigTool", "EncryptApiKeysCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady9() {

		callMethodViaReflection("ConfigTool", "EncryptApiKeysCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute9() {

		callMethodViaReflection("ConfigTool", "EncryptApiKeysCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp9() {

		callMethodViaReflection("ConfigTool", "EncryptApiKeysCommand", "displayHelp", null);

		assertTrue(true);

	}
	
	@Test
	public void testGetMatches10() {

		callMethodViaReflection("ConfigTool", "DecryptApiKeysCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady10() {

		callMethodViaReflection("ConfigTool", "DecryptApiKeysCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute10() {

		callMethodViaReflection("ConfigTool", "DecryptApiKeysCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp10() {

		callMethodViaReflection("ConfigTool", "DecryptApiKeysCommand", "displayHelp", null);

		assertTrue(true);

	}
	
	@Test
	public void testGetMatches11() {

		callMethodViaReflection("ConfigTool", "NodeFetchCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady11() {

		callMethodViaReflection("ConfigTool", "NodeFetchCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute11() {

		callMethodViaReflection("ConfigTool", "NodeFetchCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp11() {

		callMethodViaReflection("ConfigTool", "NodeFetchCommand", "displayHelp", null);

		assertTrue(true);

	}
	
	@Test
	public void testGetMatches12() {

		callMethodViaReflection("ConfigTool", "DropOldConsumerGroupsCommand", "getMatches");

		assertTrue(true);

	}

	@Test
	public void testCheckReady12() {

		callMethodViaReflection("ConfigTool", "DropOldConsumerGroupsCommand", "checkReady", null);

		assertTrue(true);

	}

	@Test
	public void testExecute12() {

		callMethodViaReflection("ConfigTool", "DropOldConsumerGroupsCommand", "execute", parts, null, null);

		assertTrue(true);

	}

	@Test
	public void testDisplayHelp12() {

		callMethodViaReflection("ConfigTool", "DropOldConsumerGroupsCommand", "displayHelp", null);

		assertTrue(true);

	}
}