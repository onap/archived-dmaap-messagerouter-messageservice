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
package org.onap.dmaap.dmf.mr.resources.streamReaders;

import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.Publisher.message;
import org.onap.dmaap.dmf.mr.beans.LogDetails;
import org.onap.dmaap.dmf.mr.resources.CambriaEventSet.reader;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This stream reader just pulls single lines. It uses the default partition if provided. If
 * not, the key is the current time, which does not guarantee ordering.
 * 
 * @author peter
 *
 */
public class CambriaTextStreamReader implements reader
{
	/**
	 * This is the constructor for Cambria Text Reader format
	 * @param is
	 * @param defPart
	 * @throws CambriaApiException
	 */
	public CambriaTextStreamReader ( InputStream is, String defPart ) throws CambriaApiException
	{
		fReader = new BufferedReader ( new InputStreamReader ( is ) );
		fDefPart = defPart;
	}

	@Override
	/**
	 * next() method iterates through msg length
	 * throws IOException
	 * throws CambriaApiException
	 * 
	 */ 
	public message next () throws CambriaApiException
	{
		try
		{
			final String line = fReader.readLine ();
			if ( line == null ) {
				return null;
			}
			
			return new message ()
			{
				private LogDetails logDetails;
				private boolean transactionEnabled;

				/**
				 * returns boolean value which 
				 * indicates whether transaction is enabled
				 * @return
				 */
				public boolean isTransactionEnabled() {
					return transactionEnabled;
				}

				/**
				 * sets boolean value which 
				 * indicates whether transaction is enabled
				 */
				public void setTransactionEnabled(boolean transactionEnabled) {
					this.transactionEnabled = transactionEnabled;
				}
				
				@Override
				/**
				 * @returns key
				 * It ch4ecks whether fDefPart value is Null.
				 * If yes, it will return ystem.currentTimeMillis () else
				 * it will return fDefPart variable value
				 */
				public String getKey ()
				{
					return fDefPart == null ? "" + System.currentTimeMillis () : fDefPart;
				}

				@Override
				/**
				 * returns the message in String type object
				 * @return
				 */
				public String getMessage ()
				{
					return line;
				}

				@Override
				/**
				 * set log details in logDetails variable
				 */
				public void setLogDetails(LogDetails logDetails) {
					this.logDetails = logDetails;
				}

				@Override
				/**
				 * get the log details
				 */
				public LogDetails getLogDetails() {
					return this.logDetails;
				}
			};
		}
		catch ( IOException e )
		{
			throw new CambriaApiException ( HttpServletResponse.SC_BAD_REQUEST, e.getMessage () );
		}
	}
	
	private final BufferedReader fReader;
	private final String fDefPart;
}
