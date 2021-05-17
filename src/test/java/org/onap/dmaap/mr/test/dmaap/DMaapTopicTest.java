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
package org.onap.dmaap.mr.test.dmaap;

public class DMaapTopicTest {
	/*private static final Logger LOGGER = Logger.getLogger(DMaapTopicTest.class);
	Client client = ClientBuilder.newClient();
	String topicapikey, topicsecretKey, serverCalculatedSignature;
	Properties prop = LoadPropertyFile.getPropertyFileData();
	String topicName = prop.getProperty("topicName");
	String url = prop.getProperty("url");
	String date = prop.getProperty("date");
	WebTarget target = client.target(url);
	DmaapApiKeyTest keyInstance = new DmaapApiKeyTest();



	public boolean topicExist(String topicName) {
		target = target.path("/topics/" + topicName);
		InputStream is, issecret;
		Response response = target.request().get();
		if (response.getStatus() == HttpStatus.SC_OK) {
			is = (InputStream) response.getEntity();
			Scanner s = new Scanner(is);
			s.useDelimiter("\\A");
			JSONObject dataObj = new JSONObject(s.next());
			s.close();
			// get owner of a topic
			topicapikey = (String) dataObj.get("owner");
			target = client.target(url);
			target = target.path("/apiKeys/");
			target = target.path(topicapikey);
			Response response2 = target.request().get();
			issecret = (InputStream) response2.getEntity();
			Scanner st = new Scanner(issecret);
			st.useDelimiter("\\A");
			JSONObject dataObj1 = new JSONObject(st.next());
			st.close();
			// get secret key of this topic//
			topicsecretKey = (String) dataObj1.get("secret");
			serverCalculatedSignature = sha1HmacSigner.sign(date, topicsecretKey);
			return true;
		} else
			return false;
	}

	public void testCreateTopic() {
		LOGGER.info("test case create topic");
		createTopic(topicName);
		LOGGER.info("Returning after create topic");
	}

	public void testOneTopic() {
		LOGGER.info("test case get specific topic name " + topicName);
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		Response response = target.request().get();
		LOGGER.info("Successfully returned after fetching topic" + topicName);
		keyInstance.assertStatus(response);
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		JSONObject dataObj = new JSONObject(s.next());
		LOGGER.info("Details of " + topicName + " : " + dataObj.toString());
		s.close();
	}

	public void testdeleteTopic() {
		LOGGER.info("test case delete topic name " + topicName);
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		Response response = target.request().header("X-CambriaAuth", topicapikey + ":" + serverCalculatedSignature)
				.header("X-CambriaDate", date).delete();
		keyInstance.assertStatus(response);
		LOGGER.info("Successfully returned after deleting topic" + topicName);
	}

	public void testAllTopic() {
		LOGGER.info("test case fetch all topic");
		target = client.target(url);
		target = target.path("/topics");
		Response response = target.request().get();
		keyInstance.assertStatus(response);
		LOGGER.info("successfully returned after fetching all the topic");
		InputStream is = (InputStream) response.getEntity();
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		JSONObject dataObj = new JSONObject(s.next());
		s.close();
		LOGGER.info("List of all topics " + dataObj.toString());
	}

	public void testPublisherForTopic() {
		LOGGER.info("test case get all publishers for topic: " + topicName);
		// creating topic to check
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/producers");
		// checking all producer for a particular topic
		Response response = target.request().get();
		keyInstance.assertStatus(response);
		LOGGER.info("Successfully returned after getting all the publishers" + topicName);
	}


	public void testConsumerForTopic() {
		LOGGER.info("test case get all consumers for topic: " + topicName);
		// creating topic to check
		createTopic(topicName);
		target = client.target(url);
		target = target.path("/topics/");
		target = target.path(topicName);
		target = target.path("/consumers");
		// checking all consumer for a particular topic
		Response response = target.request().get();
		keyInstance.assertStatus(response);
		LOGGER.info("Successfully returned after getting all the consumers" + topicName);
	}


*/
}
