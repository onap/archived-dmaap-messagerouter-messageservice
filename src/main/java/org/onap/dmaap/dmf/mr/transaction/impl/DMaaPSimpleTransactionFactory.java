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
package org.onap.dmaap.dmf.mr.transaction.impl;

import org.json.JSONObject;
import org.onap.dmaap.dmf.mr.transaction.DMaaPTransactionFactory;
import org.onap.dmaap.dmf.mr.transaction.DMaaPTransactionObj;
import org.onap.dmaap.dmf.mr.transaction.TransactionObj;

/**
 * A factory for the simple Transaction implementation
 * 
 * 
 * @author nilanjana.maity
 *
 */
public class DMaaPSimpleTransactionFactory implements DMaaPTransactionFactory<DMaaPTransactionObj> {
	/**
	 * 
	 * @param data
	 * @return DMaaPTransactionObj
	 */
	@Override
	public DMaaPTransactionObj makeNewTransactionObj(String data) {
		JSONObject jsonObject = new JSONObject(data);
		return new TransactionObj(jsonObject.getString("transactionId"), jsonObject.getLong("totalMessageCount"),
				jsonObject.getLong("successMessageCount"), jsonObject.getLong("failureMessageCount"));
	}

	/**
	 * 
	 * @param id
	 * @return TransactionObj
	 * 
	 * 
	 */
	@Override
	public DMaaPTransactionObj makeNewTransactionId(String id) {
		return new TransactionObj(id);
	}

}
