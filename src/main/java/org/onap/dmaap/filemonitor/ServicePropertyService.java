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
 package org.onap.dmaap.filemonitor;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.annotation.PostConstruct;

/**
 * ServicePropertyService class
 * @author rajashree.khare
 *
 */
public class ServicePropertyService {
	private boolean loadOnStartup;
	private ServicePropertiesListener fileChangedListener;
	private ServicePropertiesMap filePropertiesMap;
	private String ssfFileMonitorPollingInterval;
	private String ssfFileMonitorThreadpoolSize;
	private List<File> fileList;
	private static final String FILE_CHANGE_LISTENER_LOC = System
			.getProperty("AJSC_CONF_HOME") + "/etc";
	private static final String USER_CONFIG_FILE = "service-file-monitor.properties";

	private static final EELFLogger logger = EELFManager.getInstance().getLogger(ServicePropertyService.class);

	// do not remove the postConstruct annotation, init method will not be
	// called after constructor
	/**
	 * Init method
	 * @throws Exception ex
	 */
	@PostConstruct
	public void init()  {

		try {
			getFileList(FILE_CHANGE_LISTENER_LOC);

		} catch (Exception ex) {
			logger.error("Error creating property map ", ex);
		}

	}

	private void getFileList(String dirName) throws IOException {
		File directory = new File(dirName);

		if (fileList == null)
			fileList = new ArrayList<>();

		// get all the files that are ".json" or ".properties", from a directory
		// & it's sub-directories
		File[] fList = directory.listFiles();

		for (File file : fList) {
			// read service property files from the configuration file
			if (file.isFile() && file.getPath().endsWith(USER_CONFIG_FILE)) {
				try(FileInputStream fis = new FileInputStream(file)) {

					Properties prop = new Properties();
					prop.load(fis);

					for (String filePath : prop.stringPropertyNames()) {
						fileList.add(new File(prop.getProperty(filePath)));
					}
				} catch (Exception ioe) {
					logger.error("Error reading the file stream ", ioe);
				}
			} else if (file.isDirectory()) {
				getFileList(file.getPath());
			}
		}

	}

	public void setLoadOnStartup(boolean loadOnStartup) {
		this.loadOnStartup = loadOnStartup;
	}

	public void setSsfFileMonitorPollingInterval(
			String ssfFileMonitorPollingInterval) {
		this.ssfFileMonitorPollingInterval = ssfFileMonitorPollingInterval;
	}

	public void setSsfFileMonitorThreadpoolSize(
			String ssfFileMonitorThreadpoolSize) {
		this.ssfFileMonitorThreadpoolSize = ssfFileMonitorThreadpoolSize;
	}

	public boolean isLoadOnStartup() {
		return loadOnStartup;
	}

	public String getSsfFileMonitorPollingInterval() {
		return ssfFileMonitorPollingInterval;
	}

	public String getSsfFileMonitorThreadpoolSize() {
		return ssfFileMonitorThreadpoolSize;
	}

	public ServicePropertiesListener getFileChangedListener() {
		return fileChangedListener;
	}

	public void setFileChangedListener(
			ServicePropertiesListener fileChangedListener) {
		this.fileChangedListener = fileChangedListener;
	}

	public ServicePropertiesMap getFilePropertiesMap() {
		return filePropertiesMap;
	}

	public void setFilePropertiesMap(ServicePropertiesMap filePropertiesMap) {
		this.filePropertiesMap = filePropertiesMap;
	}
}
