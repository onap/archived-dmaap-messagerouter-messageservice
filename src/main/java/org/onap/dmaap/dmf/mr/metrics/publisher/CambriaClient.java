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
package org.onap.dmaap.dmf.mr.metrics.publisher;



//
import com.att.eelf.configuration.EELFLogger;


/**
 * 
 * @author anowarul.islam
 *
 */
public interface CambriaClient {
	/**
	 * An exception at the Cambria layer. This is used when the HTTP transport
	 * layer returns a success code but the transaction is not completed as
	 * expected.
	 */
	public class CambriaApiException extends Exception {
		/**
		 * 
		 * @param msg
		 */
		public CambriaApiException(String msg) {
			super(msg);
		}

		/**
		 * 
		 * @param msg
		 * @param t
		 */
		public CambriaApiException(String msg, Throwable t) {
			super(msg, t);
		}

		private static final long serialVersionUID = 1L;
	}

	/**
	 * Optionally set the Logger to use
	 * 
	 * @param log
	 */
	void logTo(EELFLogger  log);

	/**
	 * Set the API credentials for this client connection. Subsequent calls will
	 *  include authentication headers.who i
	 * 
	 * @param apiKey
	 * @param apiSecret
	 */
	void setApiCredentials(String apiKey, String apiSecret);

	/**
	 * Remove API credentials, if any, on this connection. Subsequent calls will
	 * not include authentication headers.
	 */
	void clearApiCredentials();

	/**
	 * Close this connection. Some client interfaces have additional close
	 * capability.
	 */
	void close();
}
