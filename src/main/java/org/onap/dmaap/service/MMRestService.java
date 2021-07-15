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
package org.onap.dmaap.service;

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
import org.apache.http.HttpStatus;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.onap.dmaap.dmf.mr.utils.ConfigurationReader;
import org.onap.dmaap.dmf.mr.utils.DMaaPResponseBuilder;
import org.onap.dmaap.dmf.mr.utils.Utils;
import com.att.nsa.configs.ConfigDbException;
import org.onap.dmaap.mmagent.*;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import edu.emory.mathcs.backport.java.util.Arrays;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;

import org.json.JSONArray;
import org.json.JSONException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.constants.CambriaConstants;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticator;
import org.onap.dmaap.dmf.mr.security.DMaaPAAFAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.security.DMaaPAuthenticatorImpl;
import org.onap.dmaap.dmf.mr.service.MMService;

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

	private ErrorResponse errResJson = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,
			DMaaPResponseCode.INCORRECT_JSON.getResponseCode(), "", null, Utils.getFormattedDate(new Date()), topic,
			null, null, "mirrorMakerAgent", null);

	private DMaaPAAFAuthenticator dmaapAAFauthenticator = new DMaaPAAFAuthenticatorImpl();

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
	public void callCreateMirrorMaker(InputStream msg) throws Exception {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))) {

			loadProperty();
			String input = null;
			String randomStr = getRandomNum();

			InputStream inStream = null;
			Gson gson = new Gson();
			CreateMirrorMaker createMirrorMaker = new CreateMirrorMaker();
			LOGGER.info("Starting Create MirrorMaker");
			try {
				input = IOUtils.toString(msg, "UTF-8");

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if the request has CreateMirrorMaker
				try {
					createMirrorMaker = gson.fromJson(input, CreateMirrorMaker.class);

				} catch (JsonSyntaxException ex) {

					errResJson.setErrorMessage(errorMessages.getIncorrectJson() + ex.getMessage());
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

				// send error message if it is not a CreateMirrorMaker request.
				if (createMirrorMaker.getCreateMirrorMaker() == null) {

					errResJson.setErrorMessage("This is not a CreateMirrorMaker request. Please try again.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				} else {
					createMirrorMaker.validateJSON();
				}

				String name = createMirrorMaker.getCreateMirrorMaker().getName();

				// if empty, blank name is entered
				if (StringUtils.isBlank(name)) {

					errResJson.setErrorMessage("Name can not be empty or blank.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

				// Check if the name contains only Alpha Numeric
				else if (!isAlphaNumeric(name)) {

					errResJson.setErrorMessage(NAME_DOES_NOT_MEET_REQUIREMENT);
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);

				} else {

					if (null == createMirrorMaker.getMessageID() || createMirrorMaker.getMessageID().isEmpty()) {
						createMirrorMaker.setMessageID(randomStr);
					}
					inStream = IOUtils.toInputStream(gson.toJson(createMirrorMaker), "UTF-8");
					JSONObject existMirrorMaker = validateMMExists(ctx, name);
					if (!(boolean) existMirrorMaker.get("exists")) {
						JSONObject finalJsonObj = callPubSub(createMirrorMaker.getMessageID(), ctx, inStream, name,
								false);
						DMaaPResponseBuilder.respondOk(ctx, finalJsonObj);
					} else {

						errResJson.setErrorMessage("MirrorMaker " + name + " already exists");
						LOGGER.info(errResJson.toString());
						throw new CambriaApiException(errResJson);

					}
				}

			} catch (IOException e) {

				throw e;
			}
		}
		// Send error response if user does not provide Authorization
		else {
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_ADMIN_PERMISSION, null,
					Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
					ctx.getRequest().getRemoteHost());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}

	}

	@POST
	@Produces("application/json")
	@Path("/listall")
	public void callListAllMirrorMaker(InputStream msg) throws Exception {

		DMaaPContext ctx = getDmaapContext();

		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))) {

			loadProperty();

			String input = null;
			Gson gson = new Gson();

			try {
				input = IOUtils.toString(msg, "UTF-8");

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				String randomStr = getRandomNum();
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {
					errResJson.setErrorMessage(errorMessages.getIncorrectJson());
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

				// Check if request has listAllMirrorMaker and
				// listAllMirrorMaker is empty
				if (jsonOb.has("listAllMirrorMaker") && jsonOb.getJSONObject("listAllMirrorMaker").length() == 0) {

					if (!jsonOb.has("messageID") || jsonOb.isNull("messageID")) {
						jsonOb.put("messageID", randomStr);
					}

					InputStream inStream = null;
					MirrorMaker mirrormaker = gson.fromJson(input, MirrorMaker.class);

					inStream = IOUtils.toInputStream(jsonOb.toString(), "UTF-8");

					JSONObject responseJson = callPubSub(jsonOb.getString("messageID"), ctx, inStream, mirrormaker.name,
							true);
					DMaaPResponseBuilder.respondOk(ctx, responseJson);

				} else {

					ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
							DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),
							"This is not a ListAllMirrorMaker request. Please try again.", null,
							Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
							ctx.getRequest().getRemoteHost());
					LOGGER.info(errRes.toString());
					throw new CambriaApiException(errRes);
				}

			} catch (IOException ioe) {

				throw ioe;
			}

		} else {

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_ADMIN_PERMISSION, null,
					Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
					ctx.getRequest().getRemoteHost());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	@POST
	@Produces("application/json")
	@Path("/update")
	public void callUpdateMirrorMaker(InputStream msg) throws Exception {
		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))) {

			loadProperty();
			String input = null;
			String randomStr = getRandomNum();

			InputStream inStream = null;
			Gson gson = new Gson();
			UpdateMirrorMaker updateMirrorMaker = new UpdateMirrorMaker();
			JSONObject jsonOb, jsonObInput = null;

			try {
				input = IOUtils.toString(msg, "UTF-8");

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if the request has UpdateMirrorMaker
				try {
					updateMirrorMaker = gson.fromJson(input, UpdateMirrorMaker.class);
					jsonOb = new JSONObject(input);
					jsonObInput = (JSONObject) jsonOb.get("updateMirrorMaker");

				} catch (JsonSyntaxException ex) {

					errResJson.setErrorMessage(errorMessages.getIncorrectJson() + ex.getMessage());
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

				// send error message if it is not a UpdateMirrorMaker request.
				if (updateMirrorMaker.getUpdateMirrorMaker() == null) {

					errResJson.setErrorMessage("This is not a UpdateMirrorMaker request. Please try again.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				} else {
					updateMirrorMaker.validateJSON(jsonObInput);
				}

				String name = updateMirrorMaker.getUpdateMirrorMaker().getName();
				// if empty, blank name is entered
				if (StringUtils.isBlank(name)) {

					errResJson.setErrorMessage("Name can not be empty or blank.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

				// Check if the name contains only Alpha Numeric
				else if (!isAlphaNumeric(name)) {
					errResJson.setErrorMessage(NAME_DOES_NOT_MEET_REQUIREMENT);
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);

				}

				// Set a random number as messageID, convert Json Object to
				// InputStream and finally call publisher and subscriber
				else {

					if (null == updateMirrorMaker.getMessageID() || updateMirrorMaker.getMessageID().isEmpty()) {
						updateMirrorMaker.setMessageID(randomStr);
					}

					JSONObject existMirrorMaker = validateMMExists(ctx, name);

					if ((boolean) existMirrorMaker.get("exists")) {
						JSONObject existMM = (JSONObject) existMirrorMaker.get("listMirrorMaker");

						if (!jsonObInput.has("numStreams")) {
							updateMirrorMaker.getUpdateMirrorMaker().setNumStreams(existMM.getInt("numStreams"));
						}
						if (!jsonObInput.has("enablelogCheck")) {
							updateMirrorMaker.getUpdateMirrorMaker()
									.setEnablelogCheck(existMM.getBoolean("enablelogCheck"));
						}
						inStream = IOUtils.toInputStream(gson.toJson(updateMirrorMaker), "UTF-8");
						JSONObject finalJsonObj = callPubSub(updateMirrorMaker.getMessageID(), ctx, inStream, name,
								false);
						DMaaPResponseBuilder.respondOk(ctx, finalJsonObj);
					} else {

						errResJson.setErrorMessage("MirrorMaker " + name + " does not exist");
						LOGGER.info(errResJson.toString());
						throw new CambriaApiException(errResJson);

					}

				}

			} catch (IOException e) {
				LOGGER.error("Error in callUpdateMirrorMaker:", e);
			}
		}
		// Send error response if user does not provide Authorization
		else {
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_ADMIN_PERMISSION, null,
					Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
					ctx.getRequest().getRemoteHost());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	@POST
	@Produces("application/json")
	@Path("/delete")
	public void callDeleteMirrorMaker(InputStream msg) throws JSONException, Exception {
		DMaaPContext ctx = getDmaapContext();

		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeradmin.aaf"))) {

			loadProperty();

			String input = null;
			Gson gson = new Gson();
			MirrorMaker mirrormaker = new MirrorMaker();

			try {
				input = IOUtils.toString(msg, "UTF-8");

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				String randomStr = getRandomNum();
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);
					mirrormaker = gson.fromJson(input, MirrorMaker.class);

				} catch (JSONException ex) {

					errResJson.setErrorMessage(errorMessages.getIncorrectJson());
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

				// Check if request has DeleteMirrorMaker and
				// DeleteMirrorMaker has MirrorMaker object with name variable
				// and check if the name contain only alpha numeric

				if (jsonOb.has("deleteMirrorMaker") && jsonOb.getJSONObject("deleteMirrorMaker").length() == 1
						&& jsonOb.getJSONObject("deleteMirrorMaker").has("name")
						&& !StringUtils.isBlank(jsonOb.getJSONObject("deleteMirrorMaker").getString("name"))
						&& isAlphaNumeric(jsonOb.getJSONObject("deleteMirrorMaker").getString("name"))) {

					if (!jsonOb.has("messageID") || jsonOb.isNull("messageID")) {
						jsonOb.put("messageID", randomStr);
					}

					InputStream inStream = null;

					inStream = IOUtils.toInputStream(jsonOb.toString(), "UTF-8");

					JSONObject deleteMM = jsonOb.getJSONObject("deleteMirrorMaker");

					JSONObject existMirrorMaker = validateMMExists(ctx, deleteMM.getString("name"));

					if ((boolean) existMirrorMaker.get("exists")) {

						JSONObject finalJsonObj = callPubSub(jsonOb.getString("messageID"), ctx, inStream,
								mirrormaker.name, false);
						DMaaPResponseBuilder.respondOk(ctx, finalJsonObj);
					} else {

						errResJson.setErrorMessage("MirrorMaker " + deleteMM.getString("name") + " does not exist");
						LOGGER.info(errResJson.toString());
						throw new CambriaApiException(errResJson);

					}

				} else {

					errResJson.setErrorMessage("This is not a DeleteMirrorMaker request. Please try again.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);

				}

			} catch (IOException ioe) {
				LOGGER.error("Error in callDeleteMirrorMaker:", ioe);
				throw ioe;
			}

		} else {

			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_ADMIN_PERMISSION, null,
					Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
					ctx.getRequest().getRemoteHost());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
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

	private String checkJsonFormate(String jsonStr) {

		String json = jsonStr;
		if (jsonStr != null && jsonStr.length() > 0 && jsonStr.startsWith("[") && !jsonStr.endsWith("]")) {
			json = json + "]";
		}
		return json;
	}

	private boolean checkMirrorMakerPermission(DMaaPContext ctx, String permission) {

		boolean hasPermission = false;

		if (dmaapAAFauthenticator.aafAuthentication(ctx.getRequest(), permission)) {
			hasPermission = true;
		}
		return hasPermission;
	}

	public JSONObject callPubSub(String randomstr, DMaaPContext ctx, InputStream inStream, String name, boolean listAll)
			throws Exception {
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
				if (jsonObj.has("listMirrorMaker")) {
					jsonArray = (JSONArray) jsonObj.get("listMirrorMaker");
					if (true == listAll) {
						return jsonObj;
					} else {
						for (int i = 0; i < jsonArray.length(); i++) {
							jsonObj = jsonArray.getJSONObject(i);
							if (null != name && !name.isEmpty()) {
								if (jsonObj.getString("name").equals(name)) {
									finalJsonObj.put("listMirrorMaker", jsonObj);
									break;
								}
							} else {
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
						Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
						ctx.getRequest().getRemoteHost());
				LOGGER.info(errRes.toString());
				throw new CambriaApiException(errRes);

			}

		} catch (Exception e) {
			LOGGER.error("Error in callPubSub", e);
			throw e;
		}

	}

	private void sendErrResponse(DMaaPContext ctx, String errMsg) {
		JSONObject err = new JSONObject();
		err.append(ERROR, errMsg);

		try {
			DMaaPResponseBuilder.respondOk(ctx, err);
			LOGGER.error(errMsg);

		} catch (JSONException | IOException e) {
			LOGGER.error("Error at sendErrResponse method:" + errMsg + "Exception name:" + e);
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Produces("application/json")
	@Path("/listallwhitelist")
	public void listWhiteList(InputStream msg) throws Exception {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf"))) {

			loadProperty();
			String input = null;

			try {
				input = IOUtils.toString(msg, "UTF-8");

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if it is correct Json object
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {

					errResJson.setErrorMessage(errorMessages.getIncorrectJson());
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);

				}

				// Check if the request has name and name contains only alpha
				// numeric
				// and check if the request has namespace and namespace contains
				// only alpha numeric
				if (jsonOb.length() == 2 && jsonOb.has("name") && !StringUtils.isBlank(jsonOb.getString("name"))
						&& isAlphaNumeric(jsonOb.getString("name")) && jsonOb.has("namespace")
						&& !StringUtils.isBlank(jsonOb.getString("namespace"))) {

					String permission = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
							"msgRtr.mirrormakeruser.aaf.create") + jsonOb.getString("namespace") + "|create";

					// Check if the user have create permission for the
					// namespace
					if (checkMirrorMakerPermission(ctx, permission)) {

						JSONObject listAll = new JSONObject();
						JSONObject emptyObject = new JSONObject();

						// Create a listAllMirrorMaker Json object
						try {
							listAll.put("listAllMirrorMaker", emptyObject);

						} catch (JSONException e) {

							errResJson.setErrorMessage(errorMessages.getIncorrectJson());
							LOGGER.info(errResJson.toString());
							throw new CambriaApiException(errResJson);
						}

						// set a random number as messageID
						String randomStr = getRandomNum();
						listAll.put("messageID", randomStr);
						InputStream inStream = null;

						// convert listAll Json object to InputStream object
						inStream = IOUtils.toInputStream(listAll.toString(), "UTF-8");

						JSONObject listMirrorMaker = new JSONObject();
						listMirrorMaker = callPubSub(randomStr, ctx, inStream, null, true);

						String whitelist = null;
						JSONArray listMMArray = new JSONArray();
						if (listMirrorMaker.has("listMirrorMaker")) {
							listMMArray = (JSONArray) listMirrorMaker.get("listMirrorMaker");
							for (int i = 0; i < listMMArray.length(); i++) {

								JSONObject mm = new JSONObject();
								mm = listMMArray.getJSONObject(i);
								String name = mm.getString("name");

								if (name.equals(jsonOb.getString("name")) && mm.has("whitelist")) {
									whitelist = mm.getString("whitelist");
									break;
								}
							}

							if (!StringUtils.isBlank(whitelist)) {

								List<String> topicList = new ArrayList<String>();
								List<String> finalTopicList = new ArrayList<String>();
								topicList = Arrays.asList(whitelist.split(","));

								for (String topic : topicList) {
									if (topic != null && !topic.equals("null")
											&& getNamespace(topic).equals(jsonOb.getString("namespace"))) {

										finalTopicList.add(topic);
									}
								}

								String topicNames = "";

								if (finalTopicList.size() > 0) {
									topicNames = StringUtils.join(finalTopicList, ",");
								}

								JSONObject listAllWhiteList = new JSONObject();
								listAllWhiteList.put("name", jsonOb.getString("name"));
								listAllWhiteList.put("whitelist", topicNames);

								DMaaPResponseBuilder.respondOk(ctx, listAllWhiteList);
							}

						} else {

							errResJson.setErrorMessage(
									"listWhiteList is not available, please make sure MirrorMakerAgent is running");
							LOGGER.info(errResJson.toString());
							throw new CambriaApiException(errResJson);

						}

					} else {
						ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
								DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_USER_CREATE_PERMISSION,
								null, Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
								ctx.getRequest().getRemoteHost());
						LOGGER.info(errRes.toString());
						throw new CambriaApiException(errRes);
					}

				} else {

					errResJson.setErrorMessage("This is not a ListAllWhitelist request. Please try again.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

			} catch (IOException e) {
				LOGGER.error("Error in listWhiteList", e);
			}
		} else {
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_USER_PERMISSION, null,
					Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
					ctx.getRequest().getRemoteHost());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Produces("application/json")
	@Path("/createwhitelist")
	public void createWhiteList(InputStream msg) throws Exception {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf"))) {

			loadProperty();
			String input = null;

			try {
				input = IOUtils.toString(msg, "UTF-8");

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if it is correct Json object
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {
					errResJson.setErrorMessage(errorMessages.getIncorrectJson());
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

				// Check if the request has name and name contains only alpha
				// numeric,
				// check if the request has namespace and
				// check if the request has whitelistTopicName
				// check if the topic name contains only alpha numeric
				if (jsonOb.length() == 3 && jsonOb.has("name") && !StringUtils.isBlank(jsonOb.getString("name"))
						&& isAlphaNumeric(jsonOb.getString("name")) && jsonOb.has("namespace")
						&& !StringUtils.isBlank(jsonOb.getString("namespace")) && jsonOb.has("whitelistTopicName")
						&& !StringUtils.isBlank(jsonOb.getString("whitelistTopicName"))
						&& isAlphaNumeric(jsonOb.getString("whitelistTopicName").substring(
								jsonOb.getString("whitelistTopicName").lastIndexOf(".") + 1, jsonOb
										.getString("whitelistTopicName").length()))) {

					String permission = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
							"msgRtr.mirrormakeruser.aaf.create") + jsonOb.getString("namespace") + "|create";

					// Check if the user have create permission for the
					// namespace
					if (checkMirrorMakerPermission(ctx, permission)) {

						JSONObject listAll = new JSONObject();
						JSONObject emptyObject = new JSONObject();

						// Create a listAllMirrorMaker Json object
						try {
							listAll.put("listAllMirrorMaker", emptyObject);

						} catch (JSONException e) {

							errResJson.setErrorMessage(errorMessages.getIncorrectJson());
							LOGGER.info(errResJson.toString());
							throw new CambriaApiException(errResJson);
						}

						// set a random number as messageID
						String randomStr = getRandomNum();
						listAll.put("messageID", randomStr);
						InputStream inStream = null;

						// convert listAll Json object to InputStream object
						inStream = IOUtils.toInputStream(listAll.toString(), "UTF-8");

						String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
						// call listAllMirrorMaker
						mirrorService.pushEvents(ctx, topic, inStream, null, null);

						// subscribe for listMirrorMaker
						long startTime = System.currentTimeMillis();

						while (!isListMirrorMaker(msgFrmSubscribe, randomStr)
								&& (System.currentTimeMillis() - startTime) < timeout) {
							msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
						}

						JSONArray listMirrorMaker = null;

						if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
								&& isListMirrorMaker(msgFrmSubscribe, randomStr)) {

							listMirrorMaker = getListMirrorMaker(msgFrmSubscribe, randomStr);
							String whitelist = null;

							for (int i = 0; i < listMirrorMaker.length(); i++) {
								JSONObject mm = new JSONObject();
								mm = listMirrorMaker.getJSONObject(i);
								String name = mm.getString("name");

								if (name.equals(jsonOb.getString("name")) && mm.has("whitelist")) {
									whitelist = mm.getString("whitelist");
									break;
								}
							}

							List<String> topicList = new ArrayList<String>();
							List<String> finalTopicList = new ArrayList<String>();

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
									&& getNamespace(newTopic).equals(jsonOb.getString("namespace"))) {

								UpdateWhiteList updateWhiteList = new UpdateWhiteList();
								MirrorMaker mirrorMaker = new MirrorMaker();
								mirrorMaker.setName(jsonOb.getString("name"));
								finalTopicList.add(newTopic);
								String newWhitelist = "";

								if (finalTopicList.size() > 0) {
									newWhitelist = StringUtils.join(finalTopicList, ",");
								}

								mirrorMaker.setWhitelist(newWhitelist);

								String newRandom = getRandomNum();
								updateWhiteList.setMessageID(newRandom);
								updateWhiteList.setUpdateWhiteList(mirrorMaker);

								Gson g = new Gson();
								g.toJson(updateWhiteList);
								InputStream inputStream = null;
								inputStream = IOUtils.toInputStream(g.toJson(updateWhiteList), "UTF-8");
								// callPubSub(newRandom, ctx, inputStream);
								callPubSubForWhitelist(newRandom, ctx, inputStream, jsonOb);

							} else if (topicList.contains(newTopic)) {
								ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,
										DMaaPResponseCode.INCORRECT_JSON.getResponseCode(), "The topic already exist.",
										null, Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
										ctx.getRequest().getRemoteHost());
								LOGGER.info(errRes.toString());
								throw new CambriaApiException(errRes);

							} else if (!getNamespace(newTopic).equals(jsonOb.getString("namespace"))) {
								ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR,
										DMaaPResponseCode.INCORRECT_JSON.getResponseCode(),
										"The namespace of the topic does not match with the namespace you provided.",
										null, Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
										ctx.getRequest().getRemoteHost());
								LOGGER.info(errRes.toString());
								throw new CambriaApiException(errRes);
							}
						} else {

							ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
									DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),
									"listWhiteList is not available, please make sure MirrorMakerAgent is running",
									null, Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
									ctx.getRequest().getRemoteHost());
							LOGGER.info(errRes.toString());
							throw new CambriaApiException(errRes);
						}

					} else {
						ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
								DMaaPResponseCode.UNABLE_TO_AUTHORIZE.getResponseCode(), NO_USER_CREATE_PERMISSION,
								null, Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
								ctx.getRequest().getRemoteHost());
						LOGGER.info(errRes.toString());
						throw new CambriaApiException(errRes);
					}

				} else {

					errResJson.setErrorMessage("This is not a createWhitelist request. Please try again.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);
				}

			} catch (IOException | CambriaApiException | ConfigDbException | AccessDeniedException
					| TopicExistsException | missingReqdSetting | UnavailableException e) {

				throw e;
			}
		}
		// Send error response if user does not provide Authorization
		else {
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.UNABLE_TO_AUTHORIZE.getResponseCode(), NO_USER_PERMISSION, null,
					Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
					ctx.getRequest().getRemoteHost());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);

		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Produces("application/json")
	@Path("/deletewhitelist")
	public void deleteWhiteList(InputStream msg) throws Exception {

		DMaaPContext ctx = getDmaapContext();
		if (checkMirrorMakerPermission(ctx,
				AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop, "msgRtr.mirrormakeruser.aaf"))) {

			loadProperty();
			String input = null;

			try {
				input = IOUtils.toString(msg, "UTF-8");

				if (input != null && input.length() > 0) {
					input = removeExtraChar(input);
				}

				// Check if it is correct Json object
				JSONObject jsonOb = null;

				try {
					jsonOb = new JSONObject(input);

				} catch (JSONException ex) {

					errResJson.setErrorMessage(errorMessages.getIncorrectJson());
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);

				}

				// Check if the request has name and name contains only alpha
				// numeric,
				// check if the request has namespace and
				// check if the request has whitelistTopicName
				if (jsonOb.length() == 3 && jsonOb.has("name") && isAlphaNumeric(jsonOb.getString("name"))
						&& jsonOb.has("namespace") && jsonOb.has("whitelistTopicName")
						&& isAlphaNumeric(jsonOb.getString("whitelistTopicName").substring(
								jsonOb.getString("whitelistTopicName").lastIndexOf(".") + 1,
								jsonOb.getString("whitelistTopicName").length()))) {

					String permission = AJSCPropertiesMap.getProperty(CambriaConstants.msgRtr_prop,
							"msgRtr.mirrormakeruser.aaf.create") + jsonOb.getString("namespace") + "|create";

					// Check if the user have create permission for the
					// namespace
					if (checkMirrorMakerPermission(ctx, permission)) {

						JSONObject listAll = new JSONObject();
						JSONObject emptyObject = new JSONObject();

						// Create a listAllMirrorMaker Json object
						try {
							listAll.put("listAllMirrorMaker", emptyObject);

						} catch (JSONException e) {

							errResJson.setErrorMessage(errorMessages.getIncorrectJson());
							LOGGER.info(errResJson.toString());
							throw new CambriaApiException(errResJson);
						}

						// set a random number as messageID
						String randomStr = getRandomNum();
						listAll.put("messageID", randomStr);
						InputStream inStream = null;

						// convert listAll Json object to InputStream object
						inStream = IOUtils.toInputStream(listAll.toString(), "UTF-8");

						// call listAllMirrorMaker
						mirrorService.pushEvents(ctx, topic, inStream, null, null);

						// subscribe for listMirrorMaker
						long startTime = System.currentTimeMillis();
						String msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);

						while (!isListMirrorMaker(msgFrmSubscribe, randomStr)
								&& (System.currentTimeMillis() - startTime) < timeout) {
							msgFrmSubscribe = mirrorService.subscribe(ctx, topic, consumergroup, consumerid);
						}

						JSONObject jsonObj = new JSONObject();
						JSONArray jsonArray = null;
						JSONArray listMirrorMaker = null;

						if (msgFrmSubscribe != null && msgFrmSubscribe.length() > 0
								&& isListMirrorMaker(msgFrmSubscribe, randomStr)) {
							msgFrmSubscribe = removeExtraChar(msgFrmSubscribe);
							jsonArray = new JSONArray(msgFrmSubscribe);

							for (int i = 0; i < jsonArray.length(); i++) {
								jsonObj = jsonArray.getJSONObject(i);

								if (jsonObj.has("messageID") && jsonObj.get("messageID").equals(randomStr)
										&& jsonObj.has("listMirrorMaker")) {
									listMirrorMaker = jsonObj.getJSONArray("listMirrorMaker");
									break;
								}
							}
							String whitelist = null;
							for (int i = 0; i < listMirrorMaker.length(); i++) {

								JSONObject mm = new JSONObject();
								mm = listMirrorMaker.getJSONObject(i);
								String name = mm.getString("name");

								if (name.equals(jsonOb.getString("name")) && mm.has("whitelist")) {
									whitelist = mm.getString("whitelist");
									break;
								}
							}

							List<String> topicList = new ArrayList<String>();

							if (whitelist != null) {
								topicList = Arrays.asList(whitelist.split(","));
							}
							boolean removeTopic = false;
							String topicToRemove = jsonOb.getString("whitelistTopicName");

							if (topicList.contains(topicToRemove)) {
								removeTopic = true;
							} else {
								errResJson.setErrorMessage(errorMessages.getTopicNotExist());
								LOGGER.info(errResJson.toString());
								throw new CambriaApiException(errResJson);
							}

							if (removeTopic) {
								UpdateWhiteList updateWhiteList = new UpdateWhiteList();
								MirrorMaker mirrorMaker = new MirrorMaker();

								mirrorMaker.setName(jsonOb.getString("name"));

								if (StringUtils.isNotBlank((removeTopic(whitelist, topicToRemove)))) {
									mirrorMaker.setWhitelist(removeTopic(whitelist, topicToRemove));
								}

								String newRandom = getRandomNum();

								updateWhiteList.setMessageID(newRandom);
								updateWhiteList.setUpdateWhiteList(mirrorMaker);

								Gson g = new Gson();
								g.toJson(updateWhiteList);

								InputStream inputStream = null;
								inputStream = IOUtils.toInputStream(g.toJson(updateWhiteList), "UTF-8");
								callPubSubForWhitelist(newRandom, ctx, inputStream, jsonOb);
								// mmAgentUtil.getNamespace(topicToRemove));
							}

						} else {

							ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
									DMaaPResponseCode.SERVER_UNAVAILABLE.getResponseCode(),
									"listWhiteList is not available, please make sure MirrorMakerAgent is running",
									null, Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
									ctx.getRequest().getRemoteHost());
							LOGGER.info(errRes.toString());
							throw new CambriaApiException(errRes);
						}

					} else {
						ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
								DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_USER_CREATE_PERMISSION,
								null, Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
								ctx.getRequest().getRemoteHost());
						LOGGER.info(errRes.toString());
						throw new CambriaApiException(errRes);
					}

				} else {

					errResJson.setErrorMessage("This is not a DeleteAllWhitelist request. Please try again.");
					LOGGER.info(errResJson.toString());
					throw new CambriaApiException(errResJson);

				}

			} catch (IOException | CambriaApiException | ConfigDbException | AccessDeniedException
					| TopicExistsException | missingReqdSetting | UnavailableException e) {

				throw e;
			}
		}
		// Send error response if user does not provide Authorization
		else {
			ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_UNAUTHORIZED,
					DMaaPResponseCode.ACCESS_NOT_PERMITTED.getResponseCode(), NO_USER_PERMISSION, null,
					Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
					ctx.getRequest().getRemoteHost());
			LOGGER.info(errRes.toString());
			throw new CambriaApiException(errRes);

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

					if (jsonObj.has("messageID") && jsonObj.get("messageID").equals(randomStr)
							&& jsonObj.has("listMirrorMaker")) {
						jsonArrayNamespace = jsonObj.getJSONArray("listMirrorMaker");
					}
				}

				JSONObject finalJasonObj = new JSONObject();
				JSONArray finalJsonArray = new JSONArray();

				if (jsonArrayNamespace != null) {
					for (int i = 0; i < jsonArrayNamespace.length(); i++) {

						JSONObject mmObj = new JSONObject();
						mmObj = jsonArrayNamespace.getJSONObject(i);
						if (mmObj.has("name") && mmName.equals(mmObj.getString("name"))) {

							finalJsonArray.put(mmObj);
						}

					}
				}
				finalJasonObj.put("listMirrorMaker", finalJsonArray);

				DMaaPResponseBuilder.respondOk(ctx, finalJasonObj);

			} else {

				ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_SERVICE_UNAVAILABLE,
						DMaaPResponseCode.RESOURCE_NOT_FOUND.getResponseCode(),
						"listMirrorMaker is not available, please make sure MirrorMakerAgent is running", null,
						Utils.getFormattedDate(new Date()), topic, null, null, "mirrorMakerAgent",
						ctx.getRequest().getRemoteHost());
				LOGGER.info(errRes.toString());
				throw new CambriaApiException(errRes);
			}

		} catch (Exception e) {
			LOGGER.error("Error in callPubSubForWhitelist:", e);
		}
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
	

	public JSONObject validateMMExists(DMaaPContext ctx, String name) throws Exception {
		// Create a listAllMirrorMaker Json object
		JSONObject listAll = new JSONObject();
		try {
			listAll.put("listAllMirrorMaker", new JSONObject());

		} catch (JSONException e) {
			LOGGER.error("Error while creating a listAllMirrorMaker Json object:", e);
		}

		// set a random number as messageID
		String randomStr = getRandomNum();
		listAll.put("messageID", randomStr);
		InputStream inStream = null;

		// convert listAll Json object to InputStream object
		inStream = IOUtils.toInputStream(listAll.toString(), "UTF-8");

		JSONObject listMirrorMaker = new JSONObject();
		listMirrorMaker = callPubSub(randomStr, ctx, inStream, name, false);
		if (null != listMirrorMaker && listMirrorMaker.length() > 0) {
			listMirrorMaker.put("exists", true);
			return listMirrorMaker;

		}

		if(null != listMirrorMaker) {
			listMirrorMaker.put("exists", false);
		}

		return listMirrorMaker;
	}
}
