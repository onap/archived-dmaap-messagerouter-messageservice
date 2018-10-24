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
 package org.onap.dmaap.tools;

import org.onap.dmaap.dmf.mr.beans.DMaaPMetricsSet;
import com.att.nsa.cmdtool.CommandContext;
import com.att.nsa.configs.ConfigDb;
import com.att.nsa.drumlin.till.nv.rrNvReadable;

public class ConfigToolContext implements CommandContext
{
	public ConfigToolContext ( ConfigDb db, String connStr, rrNvReadable cs )
	{
		fDb = db;
		fConnStr = connStr;
		fMetrics = new DMaaPMetricsSet( cs );
	}
	
	@Override
	public void requestShutdown ()
	{
		fQuit = true;
	}

	@Override
	public boolean shouldContinue ()
	{
		return !fQuit;
	}

	public ConfigDb getDb ()
	{
		return fDb;
	}

	public String getConnectionString ()
	{
		return fConnStr;
	}

	public DMaaPMetricsSet getMetrics ()
	{
		return fMetrics;
	}

	private final ConfigDb fDb;
	private final String fConnStr;
	private boolean fQuit = false;
	private DMaaPMetricsSet fMetrics;
}
