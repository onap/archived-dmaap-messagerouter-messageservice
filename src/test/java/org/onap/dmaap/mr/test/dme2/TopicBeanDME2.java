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
/**
 * 
 */
package org.onap.dmaap.mr.test.dme2;

import java.io.Serializable;

public class TopicBeanDME2 implements Serializable {

	private static final long serialVersionUID = -8620390377775457949L;
	private String topicName;
	private String description;

	
	private int partitionCount;
	private int replicationCount;
	private boolean transactionEnabled = false;

	public boolean isTransactionEnabled() {
		return transactionEnabled;
	}

	public void setTransactionEnabled(boolean transactionEnabled) {
		this.transactionEnabled = transactionEnabled;
	}

	public TopicBeanDME2() {
		super();
	}

	public TopicBeanDME2(String topicName, String description, int partitionCount, int replicationCount,
			boolean transactionEnabled) {
		super();
		this.topicName = topicName;
		this.description = description;
		this.partitionCount = partitionCount;
		this.replicationCount = replicationCount;
		this.transactionEnabled = transactionEnabled;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getPartitionCount() {
		return partitionCount;
	}

	public void setPartitionCount(int partitionCount) {
		this.partitionCount = partitionCount;
	}

	public int getReplicationCount() {
		return replicationCount;
	}

	public void setReplicationCount(int replicationCount) {
		this.replicationCount = replicationCount;
	}

}
