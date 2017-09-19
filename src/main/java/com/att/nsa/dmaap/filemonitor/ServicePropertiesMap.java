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
package com.att.nsa.dmaap.filemonitor;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ServicePropertiesMap class
 * @author author
 *
 */
@SuppressWarnings("squid:S1118") 
public class ServicePropertiesMap 
{
	private static HashMap<String, HashMap<String, String>> mapOfMaps = 
			new HashMap<String, HashMap<String, String>>();
//	static final Logger logger = LoggerFactory.getLogger(ServicePropertiesMap.class);

	private static final EELFLogger logger = EELFManager.getInstance().getLogger(ServicePropertiesMap.class);
	/**
	 * refresh method
	 * @param file file
	 * @throws Exception ex
	 */
	public static void refresh(File file) throws Exception
	{
		String filePath= null;
		try
		{
			logger.info("Loading properties - " + (file != null?file.getName():""));
			
			//Store .json & .properties files into map of maps
			if (file != null) {
				filePath = file.getPath();
			}
			
			if(filePath.lastIndexOf(".json")>0){
				
				ObjectMapper om = new ObjectMapper();
				TypeReference<HashMap<String, String>> typeRef = 
						new TypeReference<HashMap<String, String>>() {};
				HashMap<String, String> propMap = om.readValue(file, typeRef);
				HashMap<String, String> lcasePropMap = new HashMap<>();
				for (Map.Entry<String,String> entry : propMap.entrySet())
				{
					String key = entry.getKey();
					String lcaseKey = ifNullThenEmpty(key);
					lcasePropMap.put(lcaseKey, propMap.get(key));
				}
				
				mapOfMaps.put(file.getName(), lcasePropMap);
				
				
			}else if(filePath.lastIndexOf(".properties")>0){
				Properties prop = new Properties();
				FileInputStream fis = new FileInputStream(file);
				prop.load(fis);
				
				@SuppressWarnings("unchecked")
				HashMap<String, String> propMap = new HashMap<>((Map)prop);
				
				mapOfMaps.put(file.getName(), propMap);
			}

			logger.info("File - " + file.getName() + " is loaded into the map and the "
					+ "corresponding system properties have been refreshed");
		}
		catch (Exception e)
		{
			logger.error("File " + (file != null?file.getName():"") + " cannot be loaded into the map ", e);
			throw new Exception("Error reading map file " + (file != null?file.getName():""), e);
		}
	}
	/**
	 * Get property
	 * @param fileName fileName
	 * @param propertyKey propertyKey
	 * @return str
	 */
	public static String getProperty(String fileName, String propertyKey)
	{
		HashMap<String, String> propMap = mapOfMaps.get(fileName);
		return propMap!=null?propMap.get(ifNullThenEmpty(propertyKey)):"";
	}
	/**
	 * get properties
	 * @param fileName fileName
	 * @return mapProp
	 */
	public static Map<String, String> getProperties(String fileName){
		return mapOfMaps.get(fileName);
	}
	
	private static String ifNullThenEmpty(String key) {
		if (key == null) {
			return "";
		} else {					
			return key;
		}		
	}

}
