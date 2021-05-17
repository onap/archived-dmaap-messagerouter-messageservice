/*-
 * ============LICENSE_START=======================================================
 * ONAP Policy Engine
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.dmaap.mr.cambria.service.impl;

import junit.framework.TestSuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.onap.dmaap.dmf.mr.service.impl.EventsServiceImplTest;
import org.onap.dmaap.dmf.mr.service.impl.TopicServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses({UIServiceImplTest.class, AdminServiceImplemTest.class, ApiKeysServiceImplTest.class,
    ShowConsumerCacheTest.class, TopicServiceImplTest.class, TransactionServiceImplTest.class,
    MMServiceImplTest.class,
    BaseTransactionDbImplTest.class, MetricsServiceImplTest.class, EventsServiceImplTest.class})
public class JUnitTestSuite {

    private static final Logger LOGGER = LogManager.getLogger(JUnitTestSuite.class);

    public static void main(String[] args) {
        LOGGER.info("Running the test suite");

        TestSuite tstSuite = new TestSuite();
        LOGGER.info("Total Test Counts " + tstSuite.countTestCases());
    }
}
