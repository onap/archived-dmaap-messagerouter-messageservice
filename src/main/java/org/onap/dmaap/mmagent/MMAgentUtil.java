/**
 * 
 */
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
 package org.onap.dmaap.mmagent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import  org.onap.dmaap.dmf.mr.CambriaApiException;
import  org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import  org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import  org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import  org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import  org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import  org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import  org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import  org.onap.dmaap.dmf.mr.service.MMService;
import  org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import  org.onap.dmaap.dmf.mr.utils.Utils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;

/**
 * @author rajashree.khare
 *Util class for MM Rest Service
 */
@Component
public class MMAgentUtil {/*
	@Autowired
	@Qualifier("configurationReader")
	private ConfigurationReader configReader;

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	@Autowired
	private MMService mirrorService;

	private String topic;
	private int timeout;
	private String consumergroup;
	private String consumerid;
	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(MMAgentUtil.class);
		
	public JSONObject callPubSub(String randomstr, DMaaPContext ctx, InputStream inStream, String name, boolean listAll) throws Exception {
		loadProperty();
		JSONObject jsonObj = new JSONObject();
		JSONObject finalJsonObj = new JSONObject();
		JSONArray jsonArray = null;
		try {
			String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
			mirrorService.pushEvents(ctx, topic, inStream, null, null);
			long startTime = System.currentTimeMillis();
			
			while (!isListMirrorMaker(msgFrmSubscribe, randomstr)
					&& ((System.currentTimeMillis() - startTime) < timeout)) {
				msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
			
			}
			

			if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
					&& isListMirrorMaker(msgFrmSubscribe, randomstr)) {
				msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
				
				jsonArray = new JSONArray(msgFrmSubscribe);
				jsonObj = jsonArray.getJSONObject(0);
				if(jsonObj.has("listMirrorMaker"))
				{
					jsonArray = (JSONArray) jsonObj.get("listMirrorMaker");
					if(true==listAll)
					{
						return jsonObj;
					} 
					else 
					{
						for (int i = 0; i < jsonArray.length(); i++) 
						{
							jsonObj = jsonArray.getJSONObject(i);
								if(null!=name && !name.isEmpty())
								{
										if(jsonObj.getString("name").equals(name))
											{
												finalJsonObj.put("listMirrorMaker", jsonObj);
												break;
											}
								}
								else
								{
									finalJsonObj.put("listMirrorMaker", jsonObj);
								}
						
						}
					}
				}
				return finalJsonObj;

			} else {

				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
						DMaaPResponseCode.RESOURCE_NOT_FOUND.getResponseCode(),
						"listMirrorMaker is not available, please make sure MirrorMakerAgent is running", null,
						Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent", ctx.getRequest().getRemoteHost());
				LOGGER.info(errRes.toString());
				throw new CambriaApiException(errRes);
				
			}
			
		} catch (Exception e) {
			
			throw e;
		}
	}

	public void sendErrResponse(DMaaPContext ctx, String errMsg) {
		JSONObject err = new JSONObject();
		err.append("Error", errMsg);

		try {
			DMaaPResponseBuilder.respondOk(ctx, err);
			LOGGER.error(errMsg.toString());

		} catch (JSONException | IOException e) {
			LOGGER.error(errMsg.toString());
		}
	}
	public boolean isListMirrorMaker(String msg, String messageID) {
		String topicmsg = msg;
		topicmsg = removeExtraChar(topicmsg);
		JSONObject jObj = new JSONObject();
		JSONArray jArray = null;
		boolean exist = false;

		if (!StringUtils.isBlank(topicmsg) && topicmsg.length() > 2) {
			jArray = new JSONArray(topicmsg);

			for (int i = 0; i < jArray.length(); i++) {
				jObj = jArray.getJSONObject(i);
				
				
				if (jObj.has("messageID") && jObj.get("messageID").equals(messageID) && jObj.has("listMirrorMaker")) {
					exist = true;
					break;
				}
			}
		}
		return exist;
	}

	public void loadProperty() {

		this.timeout = Integer.parseInt(
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.timeout").trim());
		this.topic = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.topic").trim();
		this.consumergroup = AJSCPropertiesMap
				.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.consumergroup").trim();
		this.consumerid = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.consumerid")
				.trim();
		

	}

	public String removeExtraChar(String message) {
		String str = message;
		str = checkJsonFormate(str);

		if (str != null && str.length() > 0) {
			str = str.replace("\\", "");
			str = str.replace("\"{", "{");
			str = str.replace("}\"", "}");
		}
		return str;
	}

	public String getRandomNum() {
		long random = Math.round(Math.random() * 89999) + 10000;
		String strLong = Long.toString(random);
		return strLong;
	}

	public boolean isAlphaNumeric(String name) {
		String pattern = "^[a-zA-Z0-9]*$";
		if (name.matches(pattern)) {
			return true;
		}
		return false;
	}

	// This method validate IPv4
	public boolean validateIPPort(String ipPort) {
		String pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]):"
				+ "([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
		if (ipPort.matches(pattern)) {
			return true;
		}
		return false;
	}

	public String checkJsonFormate(String jsonStr) {

		String json = jsonStr;
		if (jsonStr != null && jsonStr.length() > 0 && jsonStr.startsWith("[") && !jsonStr.endsWith("]")) {
			json = json + "]";
		}
		return json;
	}

	public boolean checkMirrorMakerPermission(DMaaPContext ctx, String permission) {

		boolean hasPermission = false;

		DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();

		if (aaf.aafAuthentication(ctx.getRequest(), permission)) {
			hasPermission = true;
		}
		return hasPermission;
	}
	
	public String getNamespace(String topic) {
		return topic.substring(0, topic.lastIndexOf("."));
	}

	public String removeTopic(String whitelist, String topicToRemove) {
		List<String> topicList = new ArrayList<String>();
		List<String> newTopicList = new ArrayList<String>();

		if (whitelist.contains(",")) {
			topicList = Arrays.asList(whitelist.split(","));

		}

		if (topicList.contains(topicToRemove)) {
			for (String topic : topicList) {
				if (!topic.equals(topicToRemove)) {
					newTopicList.add(topic);
				}
			}
		}

		String newWhitelist = StringUtils.join(newTopicList, ",");

		return newWhitelist;
	}

	public void callPubSubForWhitelist(String randomStr, DMaaPContext ctx, InputStream inStream, JSONObject jsonOb) {
		
		loadProperty();
		try {
			String namespace = jsonOb.getString("namespace");
			String mmName = jsonOb.getString("name");
			
			String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
			mirrorService.pushEvents(ctx, topic, inStream, null, null);
			long startTime = System.currentTimeMillis();

			while (!isListMirrorMaker(msgFrmSubscribe, randomStr)
					&& (System.currentTimeMillis() - startTime) < timeout) {
				msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
			}
			
			JSONObject jsonObj = new JSONObject();
			JSONArray jsonArray = null;
			JSONArray jsonArrayNamespace = null;

			if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
					&& isListMirrorMaker(msgFrmSubscribe, randomStr)) {
				msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
				jsonArray = new JSONArray(msgFrmSubscribe);

				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = jsonArray.getJSONObject(i);
					

					if (jsonObj.has("messageID") && jsonObj.get("messageID").equals(randomStr) && jsonObj.has("listMirrorMaker")) {
						jsonArrayNamespace = jsonObj.getJSONArray("listMirrorMaker");
					}
				}
				JSONObject finalJasonObj = new JSONObject();
				JSONArray finalJsonArray = new JSONArray();

				for (int i = 0; i < jsonArrayNamespace.length(); i++) {

					JSONObject mmObj = new JSONObject();
					mmObj = jsonArrayNamespace.getJSONObject(i);
					if(mmObj.has("name")&& mmName.equals(mmObj.getString("name")))
					{
										
						finalJsonArray.put(mmObj);
					}
					
				}
				finalJasonObj.put("listMirrorMaker", finalJsonArray);

				DMaaPResponseBuilder.respondOk(ctx, finalJasonObj);

			} else {

				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
						DMaaPResponseCode.RESOURCE_NOT_FOUND.getResponseCode(),
						"listMirrorMaker is not available, please make sure MirrorMakerAgent is running", null,
						Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent", ctx.getRequest().getRemoteHost());
				LOGGER.info(errRes.toString());
				throw new CambriaApiException(errRes);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getWhitelistByNamespace(String originalWhitelist, String namespace) {

		String whitelist = null;
		List<String> resultList = new ArrayList<String>();
		List<String> whitelistList = new ArrayList<String>();
		whitelistList = Arrays.asList(originalWhitelist.split(","));

		for (String topic : whitelistList) {
			if (StringUtils.isNotBlank(originalWhitelist) && getNamespace(topic).equals(namespace)) {
				resultList.add(topic);
			}
		}
		if (resultList.size() > 0) {
			whitelist = StringUtils.join(resultList, ",");
		}

		return whitelist;
	}
	
	public JSONArray getListMirrorMaker(String msgFrmSubscribe, String randomStr) {
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONArray listMirrorMaker = new JSONArray();
		
		msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
		jsonArray = new JSONArray(msgFrmSubscribe);
		jsonObj = jsonArray.getJSONObject(0);
		
		for (int i = 0; i < jsonArray.length(); i++) {
			jsonObj = jsonArray.getJSONObject(i);
			
			if (jsonObj.has("messageID") && jsonObj.get("messageID").equals(randomStr) && jsonObj.has("listMirrorMaker")) {
				listMirrorMaker = jsonObj.getJSONArray("listMirrorMaker");
				break;
			}
		}
		return listMirrorMaker;		
	}
	
	public  JSONObject validateMMExists(DMaaPContext ctx,String name) throws Exception
	{
		// Create a listAllMirrorMaker Json object
		JSONObject listAll = new JSONObject();
		try {
			listAll.put("listAllMirrorMaker", new JSONObject());

		} catch (JSONException e) {

			e.printStackTrace();
		}

		// set a random number as messageID
		String randomStr = getRandomNum();
		listAll.put("messageID", randomStr);
		InputStream inStream = null;

		// convert listAll Json object to InputStream object
		try {
			inStream = IOUtils.toInputStream(listAll.toString(), "UTF-8");

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		JSONObject listMirrorMaker =new JSONObject();
		listMirrorMaker = callPubSub(randomStr, ctx, inStream, name, false);
		if (null!=listMirrorMaker && listMirrorMaker.length()>0){
			listMirrorMaker.put("exists", true);
			return listMirrorMaker;
			
		}
		listMirrorMaker.put("exists", false);
		return listMirrorMaker;
	
	}
*/}
