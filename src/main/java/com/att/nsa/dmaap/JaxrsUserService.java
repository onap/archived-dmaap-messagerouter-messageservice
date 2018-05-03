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
package com.att.nsa.dmaap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Map;
import java.util.HashMap;

/**
 * Example JAX-RS User Service
 * @author author
 *
 */
@Path("/user")
public class JaxrsUserService {
	
	private static final Map<String,String> userIdToNameMap;
	static {
		userIdToNameMap = new HashMap<>();
		userIdToNameMap.put("user1","User One");
		userIdToNameMap.put("user2","User Two");
	}
	
    /**
     * Method to fetch user details
     * @param userId user
     * @return userDetails
     */
	@GET
    @Path("/{userId}")
    @Produces("text/plain")
    public String lookupUser(@PathParam("userId") String userId) {
    	String name = userIdToNameMap.get(userId);
        return name != null ? name : "unknown id";
    }
    
}