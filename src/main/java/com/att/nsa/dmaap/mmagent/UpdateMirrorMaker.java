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
package com.att.nsa.dmaap.mmagent;

import java.util.Date;

import org.apache.http.HttpStatus;
import org.json.JSONObject;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.exception.DMaaPResponseCode;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.utils.Utils;

public class UpdateMirrorMaker {
	String messageID;
	MirrorMaker updateMirrorMaker;
	
	public MirrorMaker getUpdateMirrorMaker() {
		return updateMirrorMaker;
	}

	public void setUpdateMirrorMaker(MirrorMaker updateMirrorMaker) {
		this.updateMirrorMaker = updateMirrorMaker;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}
	public void validateJSON(JSONObject jsonObj) throws CambriaApiException
	{
		ErrorResponse errRes = new ErrorResponse(HttpStatus.SC_BAD_REQUEST,
				DMaaPResponseCode.INCORRECT_JSON.getResponseCode(), "", null, Utils.getFormattedDate(new Date()), null, null, null, 
						null,
				"");

	
		if(jsonObj.has("consumer")&& null==this.updateMirrorMaker.getConsumer())
		{
			errRes.setErrorMessage("Please provide Consumer host:port details");
			throw new CambriaApiException(errRes);
		}
		if(jsonObj.has("producer")&& null==this.updateMirrorMaker.getProducer())
		{
			errRes.setErrorMessage("Please provide Producer host:port details");
			throw new CambriaApiException(errRes);
		}
		if(jsonObj.has("numStreams")&& this.updateMirrorMaker.getNumStreams()<=0)
		{
			errRes.setErrorMessage("Please provide numStreams value");
			throw new CambriaApiException(errRes);
		}
		if(jsonObj.has("whitelist"))
		{
			errRes.setErrorMessage("Please use Create Whitelist API to add whitelist topics");
			throw new CambriaApiException(errRes);
		}
	
	}
}
