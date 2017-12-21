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
package com.att.nsa.dmaap.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.att.nsa.cambria.utils.ConfigurationReader;
import com.att.nsa.cambria.utils.DMaaPResponseBuilder;
import com.att.nsa.cambria.utils.Utils;
import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.dmaap.mmagent.*;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.emory.mathcs.backport.java.util.Arrays;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.nsa.cambria.CambriaApiException;
import com.att.nsa.cambria.backends.ConsumerFactory.UnavailableException;

import org.json.JSONArray;
import org.json.JSONException;
import com.att.nsa.cambria.beans.DMaaPContext;
import com.att.nsa.cambria.constants.CambriaConstants;
import com.att.nsa.cambria.exception.DMaaPErrorMessages;
import com.att.nsa.cambria.metabroker.Broker.TopicExistsException;
import com.att.nsa.cambria.security.DMaaPAAFAuthenticator;
import com.att.nsa.cambria.security.DMaaPAAFAuthenticatorImpl;
import com.att.nsa.cambria.service.MMService;

/**
 * Rest Service class for Mirror Maker proxy Rest Services
 * 
 * @author <a href="mailto:"></a>
 *
 * @since May 25, 2016
 */

@Component
public class MMRestService {

	private static final EELFLogger LOGGER = EELFManager.getInstance().getLogger(MMRestService.class);
	private static final String NO_ADMIN_PERMISSION = "No Mirror Maker Admin permission.";
	private static final String NO_USER_PERMISSION = "No Mirror Maker User permission.";
	private static final String NO_USER_CREATE_PERMISSION = "No Mirror Maker User Create permission.";
	private static final String NAME_DOES_NOT_MEET_REQUIREMENT = "Mirror Maker name can only contain alpha numeric";
	private static final String INVALID_IPPORT = "This is not a valid IP:Port";
	private static final String MIRROR_MAKERADMIN = "msgRtr.mirrormakeradmin.aaf";
	private static final String MIRROR_MAKERUSER = "msgRtr.mirrormakeruser.aaf";
	private static final String UTF_8 = "UTF-8";
	private static final String MESSAGE = "message";
	private static final String LISTMIRRORMAKER = "listMirrorMaker";
	private static final String ERROR = "error";
	private static final String NAMESPACE = "namespace";

	private String topic;
	private int timeout;
	private String consumergroup;
	private String consumerid;

	@Autowired
	@Qualifier("configurationReader")
	private ConfigurationReader configReader;

	@Context
	private HttpServletRequest request;

	@Context
	private HttpServletResponse response;

	@Autowired
	private MMService mirrorService;

	@Autowired
	private DMaaPErrorMessages errorMessages;

	/**
	 * This method is used for taking Configuration Object,HttpServletRequest
	 * Object,HttpServletRequest HttpServletResponse Object,HttpServletSession
	 * Object.
	 * 
	 * @return DMaaPContext object from where user can get Configuration
	 *         Object,HttpServlet Object
	 * 
	 */
	private DMaaPContext getDmaapContext() {
		DMaaPContext dmaapContext = new DMaaPContext();
		dmaapContext.setRequest(request);
		dmaapContext.setResponse(response);
		dmaapContext.setConfigReader(configReader);
		dmaapContext.setConsumerRequestTime(Utils.getFormattedDate(new Date()));

		return dmaapContext;
	}

	@POST
	@Produces("application/json")
	@Path("/create")
	public void callCreateMirrorMaker(InputStream msg) {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, MIRROR_MAKERADMIN))) {

			loadProperty();
			String input = null;
			String randomStr = getRandomNum();

			InputStream inStream = null;
			Gson gson = new Gson();
			CreateMirrorMaker createMirrorMaker = new CreateMirrorMaker();

			try {
				input = IOUtils.toString(msg, UTF_8);

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if the request has CreateMirrorMaker
				try {
					createMirrorMaker = gson.fromJson(input, CreateMirrorMaker.class);

				} catch (JsonSyntaxException ex) {

					sendErrResponse(ctx, errorMessages.getIncorrectJson());
					LOGGER.error("JsonSyntaxException: ", ex);
				}
				String name = createMirrorMaker.getCreateMirrorMaker().getName();
				// send error message if it is not a CreateMirrorMaker request.
				if (createMirrorMaker.getCreateMirrorMaker() == null) {
					sendErrResponse(ctx, "This is not a CreateMirrorMaker request. Please try again.");
				}

				// MirrorMaker whitelist and status should not be passed
				else if (createMirrorMaker.getCreateMirrorMaker().getWhitelist() != null
						|| createMirrorMaker.getCreateMirrorMaker().getStatus() != null) {
					sendErrResponse(ctx, "This is not a CreateMirrorMaker request. Please try again.");
				}
				
				// if empty, blank name is entered
				else if (StringUtils.isBlank(name)) {
					sendErrResponse(ctx, "Name can not be empty or blank.");
				}

				// Check if the name contains only Alpha Numeric
				else if (!isAlphaNumeric(name)) {
					sendErrResponse(ctx, NAME_DOES_NOT_MEET_REQUIREMENT);

				}

				// Validate the IP and Port
				else if (!StringUtils.isBlank(createMirrorMaker.getCreateMirrorMaker().getConsumer())
						&& !StringUtils.isBlank(createMirrorMaker.getCreateMirrorMaker().getProducer())
						&& !validateIPPort(createMirrorMaker.getCreateMirrorMaker().getConsumer())
						|| !validateIPPort(createMirrorMaker.getCreateMirrorMaker().getProducer())) {
					sendErrResponse(ctx, INVALID_IPPORT);

				}
				// Set a random number as messageID, convert Json Object to
				// InputStream and finally call publisher and subscriber
				else if (isAlphaNumeric(name) && validateIPPort(createMirrorMaker.getCreateMirrorMaker().getConsumer())
						&& validateIPPort(createMirrorMaker.getCreateMirrorMaker().getProducer())) {

					createMirrorMaker.setMessageID(randomStr);
					inStream = IOUtils.toInputStream(gson.toJson(createMirrorMaker), UTF_8);
					callPubSub(randomStr, ctx, inStream);
				}

			} catch (IOException e) {

				LOGGER.error("IOException: ", e);
			}
		}
		// Send error response if user does not provide Authorization
		else {
			sendErrResponse(ctx, NO_ADMIN_PERMISSION);
		}
	}

	@POST
	@Produces("application/json")
	@Path("/listall")
	public void callListAllMirrorMaker(InputStream msg) throws CambriaApiException {
		DMaaPContext ctx = getDmaapContext();

		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, MIRROR_MAKERADMIN))) {

			loadProperty();

			String input = null;

			try {
				input = IOUtils.toString(msg, UTF_8);

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				String randomStr = getRandomNum();
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {

					sendErrResponse(ctx, errorMessages.getIncorrectJson());
					LOGGER.error("JSONException: ", ex);
				}

				// Check if request has listAllMirrorMaker and
				// listAllMirrorMaker is empty
				if ((jsonOb != null) && (jsonOb.has("listAllMirrorMaker") &&
                                    jsonOb.getJSONObject("listAllMirrorMaker").length() == 0)) {
					jsonOb.put("messageID", randomStr);
					InputStream inStream = null;

					try {
						inStream = IOUtils.toInputStream(jsonOb.toString(), UTF_8);

					} catch (IOException ioe) {
						LOGGER.error("IOException: ", ioe);
					}

					callPubSub(randomStr, ctx, inStream);

				} else {

					sendErrResponse(ctx, "This is not a ListAllMirrorMaker request. Please try again.");
				}

			} catch (IOException ioe) {

				LOGGER.error("IOException: ", ioe);
			}

		} else {

			sendErrResponse(getDmaapContext(), NO_ADMIN_PERMISSION);
		}
	}

	@POST
	@Produces("application/json")
	@Path("/update")
	public void callUpdateMirrorMaker(InputStream msg) throws CambriaApiException {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, MIRROR_MAKERADMIN))) {

			loadProperty();
			String input = null;
			String randomStr = getRandomNum();

			InputStream inStream = null;
			Gson gson = new Gson();
			UpdateMirrorMaker updateMirrorMaker = new UpdateMirrorMaker();

			try {
				input = IOUtils.toString(msg, UTF_8);

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if the request has UpdateMirrorMaker
				try {
					updateMirrorMaker = gson.fromJson(input, UpdateMirrorMaker.class);

				} catch (JsonSyntaxException ex) {

					sendErrResponse(ctx, errorMessages.getIncorrectJson());
					LOGGER.error("JsonSyntaxException: ", ex);

				}
				String name = updateMirrorMaker.getUpdateMirrorMaker().getName();

				// send error message if it is not a UpdateMirrorMaker request.
				if (updateMirrorMaker.getUpdateMirrorMaker() == null) {
					sendErrResponse(ctx, "This is not a UpdateMirrorMaker request. Please try again.");
				}

				// MirrorMaker whitelist and status should not be passed
				else if (updateMirrorMaker.getUpdateMirrorMaker().getWhitelist() != null
						|| updateMirrorMaker.getUpdateMirrorMaker().getStatus() != null) {
					sendErrResponse(ctx, "This is not a UpdateMirrorMaker request. Please try again.");
				}
				
				// if empty, blank name is entered
				else if (StringUtils.isBlank(name)) {
					sendErrResponse(ctx, "Name can not be empty or blank.");
				}

				// Check if the name contains only Alpha Numeric
				else if (!isAlphaNumeric(name)) {
					sendErrResponse(ctx, NAME_DOES_NOT_MEET_REQUIREMENT);

				}

				// Validate the IP and Port
				else if (!StringUtils.isBlank(updateMirrorMaker.getUpdateMirrorMaker().getConsumer())
						&& !StringUtils.isBlank(updateMirrorMaker.getUpdateMirrorMaker().getProducer())
						&& !validateIPPort(updateMirrorMaker.getUpdateMirrorMaker().getConsumer())
						|| !validateIPPort(updateMirrorMaker.getUpdateMirrorMaker().getProducer())) {
					sendErrResponse(ctx, INVALID_IPPORT);

				}
				// Set a random number as messageID, convert Json Object to
				// InputStream and finally call publisher and subscriber
				else if (isAlphaNumeric(name) && validateIPPort(updateMirrorMaker.getUpdateMirrorMaker().getConsumer())
						&& validateIPPort(updateMirrorMaker.getUpdateMirrorMaker().getProducer())) {

					updateMirrorMaker.setMessageID(randomStr);
					inStream = IOUtils.toInputStream(gson.toJson(updateMirrorMaker), UTF_8);
					callPubSub(randomStr, ctx, inStream);
				}

			} catch (IOException e) {

				LOGGER.error("IOException: ", e);
			}
		}
		// Send error response if user does not provide Authorization
		else {
			sendErrResponse(ctx, NO_ADMIN_PERMISSION);
		}
	}

	@POST
	@Produces("application/json")
	@Path("/delete")
	public void callDeleteMirrorMaker(InputStream msg) throws CambriaApiException {
		DMaaPContext ctx = getDmaapContext();

		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, MIRROR_MAKERADMIN))) {

			loadProperty();

			String input = null;

			try {
				input = IOUtils.toString(msg, UTF_8);

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				String randomStr = getRandomNum();
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {

					sendErrResponse(ctx, errorMessages.getIncorrectJson());
					LOGGER.error("JSONException: ", ex);
				}

				// Check if request has DeleteMirrorMaker and
				// DeleteMirrorMaker has MirrorMaker object with name variable
				// and check if the name contain only alpha numeric
				if ((jsonOb != null) && (jsonOb.has("deleteMirrorMaker") 
						&& jsonOb.getJSONObject("deleteMirrorMaker").length() == 1
						&& jsonOb.getJSONObject("deleteMirrorMaker").has("name") 
						&& !StringUtils.isBlank(jsonOb.getJSONObject("deleteMirrorMaker").getString("name"))
						&& isAlphaNumeric(jsonOb.getJSONObject("deleteMirrorMaker").getString("name")))) {

					jsonOb.put("messageID", randomStr);
					InputStream inStream = null;

					try {
						inStream = IOUtils.toInputStream(jsonOb.toString(), UTF_8);

					} catch (IOException ioe) {
						LOGGER.error("IOException: ", ioe);
					}

					callPubSub(randomStr, ctx, inStream);

				} else {

					sendErrResponse(ctx, "This is not a DeleteMirrorMaker request. Please try again.");
				}

			} catch (IOException ioe) {
				LOGGER.error("IOException: ", ioe);
			}

		} else {

			sendErrResponse(getDmaapContext(), NO_ADMIN_PERMISSION);
		}
	}

	private boolean isListMirrorMaker(String msg, String messageID) {
		String topicmsg = msg;
		topicmsg = removeExtraChar(topicmsg);

		JSONObject jObj;
		JSONArray jArray;
		boolean exist = false;

		if (!StringUtils.isBlank(topicmsg) && topicmsg.length() > 2) {
			jArray = new JSONArray(topicmsg);

			for (int i = 0; i < jArray.length(); i++) {
				jObj = jArray.getJSONObject(i);
				
				JSONObject obj = new JSONObject();
				if (jObj.has(MESSAGE)) {
					obj = jObj.getJSONObject(MESSAGE);
				}
				if (obj.has("messageID") && obj.get("messageID").equals(messageID) && obj.has(LISTMIRRORMAKER)) {
					exist = true;
					break;
				}
			}
		}
		return exist;
	}

	private void loadProperty() {

		this.timeout = Integer.parseInt(
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.timeout").trim());
		this.topic = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.topic").trim();
		this.consumergroup = AJSCPropertiesMap
				.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.consumergroup").trim();
		this.consumerid = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormaker.consumerid")
				.trim();
	}

	private String removeExtraChar(String message) {
		String str = message;
		str = checkJsonFormate(str);

		if (str != null && str.length() > 0) {
			str = str.replace("\\", "");
			str = str.replace("\"{", "{");
			str = str.replace("}\"", "}");
		}
		return str;
	}

	private String getRandomNum() {
		long random = Math.round(Math.random() * 89999) + 10000;
		String strLong = Long.toString(random);
		return strLong;
	}

	private boolean isAlphaNumeric(String name) {
		String pattern = "^[a-zA-Z0-9]*$";
		if (name.matches(pattern)) {
			return true;
		}
		return false;
	}

	// This method validate IPv4
	private boolean validateIPPort(String ipPort) {
		String pattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]):"
				+ "([1-9][0-9]{0,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5])$";
		if (ipPort.matches(pattern)) {
			return true;
		}
		return false;
	}

	private String checkJsonFormate(String jsonStr) {

		String json = jsonStr;
		if (jsonStr != null && jsonStr.length() > 0 && jsonStr.startsWith("[") && !jsonStr.endsWith("]")) {
			json = json + "]";
		}
		return json;
	}

	private boolean checkMirrorMakerPermission(DMaaPContext ctx, String permission) {

		boolean hasPermission = false;

		DMaaPAAFAuthenticator aaf = new DMaaPAAFAuthenticatorImpl();

		if (aaf.aafAuthentication(ctx.getRequest(), permission)) {
			hasPermission = true;
		}
		return hasPermission;
	}

	private void callPubSub(String randomstr, DMaaPContext ctx, InputStream inStream) {
		try {
			mirrorService.pushEvents(ctx, topic, inStream, null, null);
			long startTime = System.currentTimeMillis();
			String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);

			while (!isListMirrorMaker(msgFrmSubscribe, randomstr)
					&& (System.currentTimeMillis() - startTime) < timeout) {
				msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
			}

			JSONObject jsonObj;
			JSONObject finalJsonObj = new JSONObject();
			JSONArray jsonArray;

			if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
					&& isListMirrorMaker(msgFrmSubscribe, randomstr)) {
				msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
				jsonArray = new JSONArray(msgFrmSubscribe);

				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = jsonArray.getJSONObject(i);
					
					JSONObject obj = new JSONObject();
					if (jsonObj.has(MESSAGE)) {
						obj = jsonObj.getJSONObject(MESSAGE);
					}
					if (obj.has("messageID") && obj.get("messageID").equals(randomstr) && obj.has(LISTMIRRORMAKER)) {
						finalJsonObj.put(LISTMIRRORMAKER, obj.get(LISTMIRRORMAKER));
						break;
					}
				}

				DMaaPResponseBuilder.respondOk(ctx, finalJsonObj);

			} else {

				JSONObject err = new JSONObject();
				err.append(ERROR, "listMirrorMaker is not available, please make sure MirrorMakerAgent is running");
				DMaaPResponseBuilder.respondOk(ctx, err);
			}

		} catch (Exception e) {
			LOGGER.error("Exception: ", e);			
		}
	}

	private void sendErrResponse(DMaaPContext ctx, String errMsg) {
		JSONObject err = new JSONObject();
		err.append(ERROR, errMsg);

		try {
			DMaaPResponseBuilder.respondOk(ctx, err);
			LOGGER.error(errMsg);

		} catch (JSONException | IOException e) {
			LOGGER.error(errMsg);
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Produces("application/json")
	@Path("/listallwhitelist")
	public void listWhiteList(InputStream msg) {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, MIRROR_MAKERUSER))) {

			loadProperty();
			String input = null;

			try {
				input = IOUtils.toString(msg, UTF_8);

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if it is correct Json object
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {

					sendErrResponse(ctx, errorMessages.getIncorrectJson());
					LOGGER.error("JSONException: ", ex);
				}

				// Check if the request has name and name contains only alpha
				// numeric
				// and check if the request has namespace and namespace contains
				// only alpha numeric
				if (jsonOb != null 
						&& jsonOb.length() == 2 && jsonOb.has("name")
						&& !StringUtils.isBlank(jsonOb.getString("name"))
						&& isAlphaNumeric(jsonOb.getString("name")) && jsonOb.has(NAMESPACE)
						&& !StringUtils.isBlank(jsonOb.getString(NAMESPACE))) {

					String permission = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
							"msgRtr.mirrormakeruser.aaf.create") + jsonOb.getString(NAMESPACE) + "|create";

					// Check if the user have create permission for the
					// namespace
					if (checkMirrorMakerPermission(ctx, permission)) {

						JSONObject listAll = new JSONObject();
						JSONObject emptyObject = new JSONObject();

						// Create a listAllMirrorMaker Json object
						try {
							listAll.put("listAllMirrorMaker", emptyObject);

						} catch (JSONException e) {

							LOGGER.error("JSONException: ", e);
						}

						// set a random number as messageID
						String randomStr = getRandomNum();
						listAll.put("messageID", randomStr);
						InputStream inStream = null;

						// convert listAll Json object to InputStream object
						try {
							inStream = IOUtils.toInputStream(listAll.toString(), UTF_8);

						} catch (IOException ioe) {
							LOGGER.error("IOException: ", ioe);
						}
						// call listAllMirrorMaker
						mirrorService.pushEvents(ctx, topic, inStream, null, null);

						// subscribe for listMirrorMaker
						long startTime = System.currentTimeMillis();
						String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);

						while (!isListMirrorMaker(msgFrmSubscribe, randomStr)
								&& (System.currentTimeMillis() - startTime) < timeout) {
							msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
						}

						if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
								&& isListMirrorMaker(msgFrmSubscribe, randomStr)) {
							
							JSONArray listMirrorMaker;
							listMirrorMaker = getListMirrorMaker(msgFrmSubscribe, randomStr);

							String whitelist = null;
							for (int i = 0; i < listMirrorMaker.length(); i++) {

								JSONObject mm;
								mm = listMirrorMaker.getJSONObject(i);
								String name = mm.getString("name");

								if (name.equals(jsonOb.getString("name")) && mm.has("whitelist")) {
									whitelist = mm.getString("whitelist");
									break;
								}
							}

							if (!StringUtils.isBlank(whitelist)) {

								List<String> topicList = new ArrayList<>();
								List<String> finalTopicList = new ArrayList<>();
								topicList = Arrays.asList(whitelist.split(","));

								for (String topic : topicList) {
									if (topic != null && !topic.equals("null")
											&& getNamespace(topic).equals(jsonOb.getString(NAMESPACE))) {

										finalTopicList.add(topic);
									}
								}

								String topicNames = "";

								if (!finalTopicList.isEmpty()) {
									topicNames = StringUtils.join(finalTopicList, ",");
								}

								JSONObject listAllWhiteList = new JSONObject();
								listAllWhiteList.put("name", jsonOb.getString("name"));
								listAllWhiteList.put("whitelist", topicNames);

								DMaaPResponseBuilder.respondOk(ctx, listAllWhiteList);
							}

						} else {

							JSONObject err = new JSONObject();
							err.append(ERROR,
									"listWhiteList is not available, please make sure MirrorMakerAgent is running");
							DMaaPResponseBuilder.respondOk(ctx, err);
						}

					} else {
						sendErrResponse(ctx, NO_USER_CREATE_PERMISSION);
					}

				} else {

					sendErrResponse(ctx, "This is not a ListAllWhitelist request. Please try again.");
				}

			} catch (IOException | CambriaApiException | ConfigDbException | AccessDeniedException
					| TopicExistsException | missingReqdSetting | UnavailableException e) {

				LOGGER.error("IOException: ", e);
			}
		} else {
			sendErrResponse(ctx, NO_USER_PERMISSION);
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Produces("application/json")
	@Path("/createwhitelist")
	public void createWhiteList(InputStream msg) {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, MIRROR_MAKERUSER))) {

			loadProperty();
			String input = null;

			try {
				input = IOUtils.toString(msg, UTF_8);

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if it is correct Json object
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {

					sendErrResponse(ctx, errorMessages.getIncorrectJson());
					LOGGER.error("JSONException: ", ex);
				}

				// Check if the request has name and name contains only alpha numeric,
				// check if the request has namespace and
				// check if the request has whitelistTopicName
				// check if the topic name contains only alpha numeric
				if (jsonOb != null && jsonOb.length() == 3 && jsonOb.has("name")
						&& !StringUtils.isBlank(jsonOb.getString("name")) 
						&& isAlphaNumeric(jsonOb.getString("name")) 
						&& jsonOb.has(NAMESPACE) && !StringUtils.isBlank(jsonOb.getString(NAMESPACE))
						&& jsonOb.has("whitelistTopicName") && !StringUtils.isBlank(jsonOb.getString("whitelistTopicName"))
						&& isAlphaNumeric(jsonOb.getString("whitelistTopicName").substring(jsonOb.getString("whitelistTopicName").lastIndexOf(".")+1, 
								jsonOb.getString("whitelistTopicName").length()))) {

					String permission = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
							"msgRtr.mirrormakeruser.aaf.create") + jsonOb.getString(NAMESPACE) + "|create";

					// Check if the user have create permission for the
					// namespace
					if (checkMirrorMakerPermission(ctx, permission)) {

						JSONObject listAll = new JSONObject();
						JSONObject emptyObject = new JSONObject();

						// Create a listAllMirrorMaker Json object
						try {
							listAll.put("listAllMirrorMaker", emptyObject);

						} catch (JSONException e) {

							LOGGER.error("JSONException: ", e);
						}

						// set a random number as messageID
						String randomStr = getRandomNum();
						listAll.put("messageID", randomStr);
						InputStream inStream = null;

						// convert listAll Json object to InputStream object
						try {
							inStream = IOUtils.toInputStream(listAll.toString(), UTF_8);

						} catch (IOException ioe) {
							ioe.printStackTrace();
							LOGGER.error("IOException: ", ioe);
						}
						// call listAllMirrorMaker
						mirrorService.pushEvents(ctx, topic, inStream, null, null);

						// subscribe for listMirrorMaker
						long startTime = System.currentTimeMillis();
						String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);

						while (!isListMirrorMaker(msgFrmSubscribe, randomStr)
								&& (System.currentTimeMillis() - startTime) < timeout) {
							msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
						}

						JSONArray listMirrorMaker;

						if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
								&& isListMirrorMaker(msgFrmSubscribe, randomStr)) {
							
							listMirrorMaker = getListMirrorMaker(msgFrmSubscribe, randomStr);
							String whitelist = null;

							for (int i = 0; i < listMirrorMaker.length(); i++) {
								JSONObject mm;
								mm = listMirrorMaker.getJSONObject(i);
								String name = mm.getString("name");

								if (name.equals(jsonOb.getString("name")) && mm.has("whitelist")) {
									whitelist = mm.getString("whitelist");
									break;
								}
							}

							List<String> topicList = new ArrayList<>();
							List<String> finalTopicList = new ArrayList<>();

							if (whitelist != null) {
								topicList = Arrays.asList(whitelist.split(","));
							}

							for (String st : topicList) {
								if (!StringUtils.isBlank(st)) {
									finalTopicList.add(st);
								}
							}

							String newTopic = jsonOb.getString("whitelistTopicName");

							if (!topicList.contains(newTopic)
									&& getNamespace(newTopic).equals(jsonOb.getString(NAMESPACE))) {

								UpdateWhiteList updateWhiteList = new UpdateWhiteList();
								MirrorMaker mirrorMaker = new MirrorMaker();
								mirrorMaker.setName(jsonOb.getString("name"));
								finalTopicList.add(newTopic);
								String newWhitelist = "";

								if (!finalTopicList.isEmpty()) {
									newWhitelist = StringUtils.join(finalTopicList, ",");
								}

								mirrorMaker.setWhitelist(newWhitelist);

								String newRandom = getRandomNum();
								updateWhiteList.setMessageID(newRandom);
								updateWhiteList.setUpdateWhiteList(mirrorMaker);

								Gson g = new Gson();
								g.toJson(updateWhiteList);
								InputStream inputStream;
								inputStream = IOUtils.toInputStream(g.toJson(updateWhiteList), UTF_8);
								// callPubSub(newRandom, ctx, inputStream);
								callPubSubForWhitelist(newRandom, ctx, inputStream, jsonOb.getString(NAMESPACE));

							} else if (topicList.contains(newTopic)) {
								sendErrResponse(ctx, "The topic already exist.");

							} else if (!getNamespace(newTopic).equals(jsonOb.getString(NAMESPACE))) {
								sendErrResponse(ctx,
										"The namespace of the topic does not match with the namespace you provided.");
							}
						} else {

							JSONObject err = new JSONObject();
							err.append(ERROR,
									"listWhiteList is not available, please make sure MirrorMakerAgent is running");
							DMaaPResponseBuilder.respondOk(ctx, err);
						}

					} else {
						sendErrResponse(ctx, NO_USER_CREATE_PERMISSION);
					}

				} else {

					sendErrResponse(ctx, "This is not a createWhitelist request. Please try again.");
				}

			} catch (IOException | CambriaApiException | ConfigDbException | AccessDeniedException
					| TopicExistsException | missingReqdSetting | UnavailableException e) {

				LOGGER.error("IOException: ", e);
			}
		}
		// Send error response if user does not provide Authorization
		else {
			sendErrResponse(ctx, NO_USER_PERMISSION);
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Produces("application/json")
	@Path("/deletewhitelist")
	public void deleteWhiteList(InputStream msg) {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, MIRROR_MAKERUSER))) {

			loadProperty();
			String input = null;

			try {
				input = IOUtils.toString(msg, UTF_8);

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if it is correct Json object
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {

					sendErrResponse(ctx, errorMessages.getIncorrectJson());
					LOGGER.error("JSONException: ", ex);
				}

				// Check if the request has name and name contains only alpha numeric,
				// check if the request has namespace and
				// check if the request has whitelistTopicName
				if (jsonOb != null && jsonOb.length() == 3 && jsonOb.has("name") && isAlphaNumeric(jsonOb.getString("name"))
						&& jsonOb.has(NAMESPACE) && jsonOb.has("whitelistTopicName") 
						&& isAlphaNumeric(jsonOb.getString("whitelistTopicName").substring(jsonOb.getString("whitelistTopicName").lastIndexOf(".")+1, 
								jsonOb.getString("whitelistTopicName").length()))) {

					String permission = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
							"msgRtr.mirrormakeruser.aaf.create") + jsonOb.getString(NAMESPACE) + "|create";

					// Check if the user have create permission for the
					// namespace
					if (checkMirrorMakerPermission(ctx, permission)) {

						JSONObject listAll = new JSONObject();
						JSONObject emptyObject = new JSONObject();

						// Create a listAllMirrorMaker Json object
						try {
							listAll.put("listAllMirrorMaker", emptyObject);

						} catch (JSONException e) {

							LOGGER.error("JSONException: ", e);
						}

						// set a random number as messageID
						String randomStr = getRandomNum();
						listAll.put("messageID", randomStr);
						InputStream inStream = null;

						// convert listAll Json object to InputStream object
						try {
							inStream = IOUtils.toInputStream(listAll.toString(), UTF_8);

						} catch (IOException ioe) {
							ioe.printStackTrace();
							LOGGER.error("IOException: ", ioe);
						}
						// call listAllMirrorMaker
						mirrorService.pushEvents(ctx, topic, inStream, null, null);

						// subscribe for listMirrorMaker
						long startTime = System.currentTimeMillis();
						String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);

						while (!isListMirrorMaker(msgFrmSubscribe, randomStr)
								&& (System.currentTimeMillis() - startTime) < timeout) {
							msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
						}

						JSONObject jsonObj;
						JSONArray jsonArray;
						JSONArray listMirrorMaker = null;

						if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
								&& isListMirrorMaker(msgFrmSubscribe, randomStr)) {
							msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
							jsonArray = new JSONArray(msgFrmSubscribe);

							for (int i = 0; i < jsonArray.length(); i++) {
								jsonObj = jsonArray.getJSONObject(i);
								
								JSONObject obj = new JSONObject();
								if (jsonObj.has(MESSAGE)) {
									obj = jsonObj.getJSONObject(MESSAGE);
								}
								if (obj.has("messageID") && obj.get("messageID").equals(randomStr) && obj.has(LISTMIRRORMAKER)) {
									listMirrorMaker = obj.getJSONArray(LISTMIRRORMAKER);
									break;
								}
							}
							String whitelist = null;
							if (listMirrorMaker != null) {
								for (int i = 0; i < listMirrorMaker.length(); i++) {

									JSONObject mm = new JSONObject();
									mm = listMirrorMaker.getJSONObject(i);
									String name = mm.getString("name");

									if (name.equals(jsonOb.getString("name")) && mm.has("whitelist")) {
										whitelist = mm.getString("whitelist");
										break;
									}
								}
							}

							List<String> topicList = new ArrayList<>();

							if (whitelist != null) {
								topicList = Arrays.asList(whitelist.split(","));
							}
							boolean removeTopic = false;
							String topicToRemove = jsonOb.getString("whitelistTopicName");

							if (topicList.contains(topicToRemove)) {
								removeTopic = true;
							} else {
								sendErrResponse(ctx, "The topic does not exist.");
							}


							if (removeTopic) {
								UpdateWhiteList updateWhiteList = new UpdateWhiteList();
								MirrorMaker mirrorMaker = new MirrorMaker();
								
								mirrorMaker.setName(jsonOb.getString("name"));
								mirrorMaker.setWhitelist(removeTopic(whitelist, topicToRemove));
							
								String newRandom = getRandomNum();
								
								updateWhiteList.setMessageID(newRandom);
								updateWhiteList.setUpdateWhiteList(mirrorMaker);
								
								Gson g = new Gson();
								g.toJson(updateWhiteList);
								
								InputStream inputStream;
								inputStream = IOUtils.toInputStream(g.toJson(updateWhiteList), UTF_8);
								callPubSubForWhitelist(newRandom, ctx, inputStream, getNamespace(topicToRemove));
							}

						} else {

							JSONObject err = new JSONObject();
							err.append(ERROR,
									"listWhiteList is not available, please make sure MirrorMakerAgent is running");
							DMaaPResponseBuilder.respondOk(ctx, err);
						}

					} else {
						sendErrResponse(ctx, NO_USER_CREATE_PERMISSION);
					}

				} else {

					sendErrResponse(ctx, "This is not a DeleteAllWhitelist request. Please try again.");
				}

			} catch (IOException | CambriaApiException | ConfigDbException | AccessDeniedException
					| TopicExistsException | missingReqdSetting | UnavailableException e) {

				LOGGER.error("IOException: ", e);
			}
		}
		// Send error response if user does not provide Authorization
		else {
			sendErrResponse(ctx, NO_USER_PERMISSION);
		}
	}

	private String getNamespace(String topic) {
		return topic.substring(0, topic.lastIndexOf("."));
	}

	private String removeTopic(String whitelist, String topicToRemove) {
		List<String> topicList = new ArrayList<>();
		List<String> newTopicList = new ArrayList<>();

		if (whitelist.contains(",")) {
			topicList = Arrays.asList(whitelist.split(","));

		}

		if (topicList.contains(topicToRemove)) {
			for (String topic: topicList) {
				if (!topic.equals(topicToRemove)) {
					newTopicList.add(topic);
				}
			}
		}

		String newWhitelist = StringUtils.join(newTopicList, ",");

		return newWhitelist;
	}

	private void callPubSubForWhitelist(String randomStr, DMaaPContext ctx, InputStream inStream, String namespace) {
		
		try {
			mirrorService.pushEvents(ctx, topic, inStream, null, null);
			long startTime = System.currentTimeMillis();
			String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);

			while (!isListMirrorMaker(msgFrmSubscribe, randomStr)
					&& (System.currentTimeMillis() - startTime) < timeout) {
				msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
			}

			JSONObject jsonObj;
			JSONArray jsonArray;
			JSONArray jsonArrayNamespace = null;

			if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
					&& isListMirrorMaker(msgFrmSubscribe, randomStr)) {
				msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
				jsonArray = new JSONArray(msgFrmSubscribe);

				for (int i = 0; i < jsonArray.length(); i++) {
					jsonObj = jsonArray.getJSONObject(i);
					
					JSONObject obj = new JSONObject();
					if (jsonObj.has(MESSAGE)) {
						obj = jsonObj.getJSONObject(MESSAGE);
					}
					if (obj.has("messageID") && obj.get("messageID").equals(randomStr) && obj.has(LISTMIRRORMAKER)) {
						jsonArrayNamespace = obj.getJSONArray(LISTMIRRORMAKER);
					}
				}
				JSONObject finalJasonObj = new JSONObject();
				JSONArray finalJsonArray = new JSONArray();

				for (int i = 0; i < jsonArrayNamespace.length(); i++) {

					JSONObject mmObj;
					mmObj = jsonArrayNamespace.getJSONObject(i);
					String whitelist;

					if (mmObj.has("whitelist")) {
						whitelist = getWhitelistByNamespace(mmObj.getString("whitelist"), namespace);

						if (whitelist != null) {
							mmObj.remove("whitelist");
							mmObj.put("whitelist", whitelist);
						} else {
							mmObj.remove("whitelist");
						}
					}
					finalJsonArray.put(mmObj);
				}
				finalJasonObj.put(LISTMIRRORMAKER, finalJsonArray);

				DMaaPResponseBuilder.respondOk(ctx, finalJasonObj);

			} else {

				JSONObject err = new JSONObject();
				err.append(ERROR, "listMirrorMaker is not available, please make sure MirrorMakerAgent is running");
				DMaaPResponseBuilder.respondOk(ctx, err);
			}

		} catch (Exception e) {
			LOGGER.error("Exception: ", e);
		}
	}

	private String getWhitelistByNamespace(String originalWhitelist, String namespace) {

		String whitelist = null;
		List<String> resultList = new ArrayList<>();
		List<String> whitelistList = new ArrayList<>();
		whitelistList = Arrays.asList(originalWhitelist.split(","));

		for (String topic : whitelistList) {
			if (StringUtils.isNotBlank(originalWhitelist) && getNamespace(topic).equals(namespace)) {
				resultList.add(topic);
			}
		}
		if (!resultList.isEmpty()) {
			whitelist = StringUtils.join(resultList, ",");
		}

		return whitelist;
	}
	
	private JSONArray getListMirrorMaker(String msgFrmSubscribe, String randomStr) {
		JSONObject jsonObj;
		JSONArray jsonArray;
		JSONArray listMirrorMaker = new JSONArray();
		
		msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
		jsonArray = new JSONArray(msgFrmSubscribe);

		for (int i = 0; i < jsonArray.length(); i++) {
			jsonObj = jsonArray.getJSONObject(i);
			
			JSONObject obj = new JSONObject();
			if (jsonObj.has(MESSAGE)) {
				obj = jsonObj.getJSONObject(MESSAGE);
			}
			if (obj.has("messageID") && obj.get("messageID").equals(randomStr) && obj.has(LISTMIRRORMAKER)) {
				listMirrorMaker = obj.getJSONArray(LISTMIRRORMAKER);
				break;
			}
		}
		return listMirrorMaker;		
	}
}
