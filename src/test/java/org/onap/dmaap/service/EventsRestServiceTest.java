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
 package org.onap.dmaap.service;


import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.att.nsa.configs.ConfigDbException;
import com.att.nsa.drumlin.till.nv.rrNvReadable.missingReqdSetting;
import com.att.nsa.security.ReadWriteSecuredResource.AccessDeniedException;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.onap.dmaap.dmf.mr.CambriaApiException;
import org.onap.dmaap.dmf.mr.backends.ConsumerFactory.UnavailableException;
import org.onap.dmaap.dmf.mr.beans.DMaaPContext;
import org.onap.dmaap.dmf.mr.exception.DMaaPAccessDeniedException;
import org.onap.dmaap.dmf.mr.exception.DMaaPErrorMessages;
import org.onap.dmaap.dmf.mr.exception.ErrorResponse;
import org.onap.dmaap.dmf.mr.metabroker.Broker.TopicExistsException;
import org.onap.dmaap.dmf.mr.service.EventsService;
import org.springframework.test.context.ContextConfiguration;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration
public class EventsRestServiceTest {

	@InjectMocks
	EventsRestService eventsRestService;

	@Mock
	private EventsService eventsService;

	@Mock
	ErrorResponse errorResponse;

	@Mock
	InputStream iStream;

	@Mock
	ServletInputStream servletInputStream;

	@Mock
	HttpServletRequest request;

	@Mock
	DMaaPErrorMessages errorMessages;

	@Test
	public void testGetEvents() throws CambriaApiException {

		eventsRestService.getEvents("topicName", "consumergroup", "consumerid");

	}

	@Test(expected = CambriaApiException.class)
	public void testGetEvents_TopicExistException() throws CambriaApiException, ConfigDbException, TopicExistsException,
			UnavailableException, IOException, AccessDeniedException {

		doThrow(new TopicExistsException("topic exists")).when(eventsService).getEvents(any(),
				any(), any(), any());

		eventsRestService.getEvents("topicName", "consumergroup", "consumerid");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEvents_DMaaPAccessDeniedException() throws CambriaApiException, ConfigDbException,
			TopicExistsException, UnavailableException, IOException, AccessDeniedException {

		doThrow(new DMaaPAccessDeniedException(errorResponse)).when(eventsService).getEvents(any(),
				any(), any(), any());

		eventsRestService.getEvents("topicName", "consumergroup", "consumerid");
	}

	 @Test(expected = CambriaApiException.class)
	 public void testGetEvents_AccessDeniedException() throws CambriaApiException,
		 ConfigDbException, TopicExistsException, UnavailableException, IOException, AccessDeniedException {
		 doThrow(new ConfigDbException("error")).when(eventsService).getEvents( ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		 eventsRestService.getEvents("topicName", "consumergroup", "consumerid");
	 }

	@Test(expected = CambriaApiException.class)
	public void testGetEvents_ConfigDbException() throws CambriaApiException,
		ConfigDbException, TopicExistsException, UnavailableException, IOException, AccessDeniedException {
		doThrow(new ConfigDbException("error")).when(eventsService).getEvents( ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		eventsRestService.getEvents("topicName", "consumergroup", "consumerid");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEvents_UnavailableException() throws CambriaApiException,
		ConfigDbException, TopicExistsException, UnavailableException, IOException, AccessDeniedException {
		doThrow(new ConfigDbException("error")).when(eventsService).getEvents( ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		eventsRestService.getEvents("topicName", "consumergroup", "consumerid");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEvents_IOException() throws CambriaApiException,
		ConfigDbException, TopicExistsException, UnavailableException, IOException, AccessDeniedException {
		doThrow(new ConfigDbException("error")).when(eventsService).getEvents( ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		eventsRestService.getEvents("topicName", "consumergroup", "consumerid");
	}


	@Test
	public void testPushEvents() throws CambriaApiException {

		eventsRestService.pushEvents("topicName", iStream, "partitionKey");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents_TopicExistsException()
		throws AccessDeniedException, CambriaApiException, IOException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		doThrow(new TopicExistsException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.isNull());

		eventsRestService.pushEvents("topicName", iStream, "partitionKey");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents_DMaaPAccessDeniedException()
		throws AccessDeniedException, CambriaApiException, IOException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		doThrow(new DMaaPAccessDeniedException(errorResponse)).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.isNull());

		eventsRestService.pushEvents("topicName", iStream, "partitionKey");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents_ConfigDbException()
		throws AccessDeniedException, CambriaApiException, IOException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		doThrow(new ConfigDbException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.isNull());

		eventsRestService.pushEvents("topicName", iStream, "partitionKey");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents_IOException()
		throws AccessDeniedException, CambriaApiException, IOException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		doThrow(new IOException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.isNull());

		eventsRestService.pushEvents("topicName", iStream, "partitionKey");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents_missingReqdSetting()
		throws AccessDeniedException, CambriaApiException, IOException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		doThrow(new missingReqdSetting("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.isNull());

		eventsRestService.pushEvents("topicName", iStream, "partitionKey");

	}

	@Test(expected = CambriaApiException.class)
	public void testPushEvents_AccessDeniedException()
		throws AccessDeniedException, CambriaApiException, IOException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		doThrow(new AccessDeniedException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.isNull());

		eventsRestService.pushEvents("topicName", iStream, "partitionKey");

	}

	@Test(expected = CambriaApiException.class)
	public void testGetEventsToException() throws CambriaApiException {

		eventsRestService.getEventsToException("/topic");
	}

	@Test(expected = CambriaApiException.class)
	public void testGetEventsToExceptionWithConsumerGroup() throws CambriaApiException {
		eventsRestService.getEventsToException("/topic", "1234");
	}

	@Test
	public void testPushEventsWithTransaction() throws CambriaApiException, IOException {
		when(request.getInputStream()).thenReturn(servletInputStream);
		eventsRestService.pushEventsWithTransaction("topicName", "partitionKey");
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWithTransaction_TopicExistsException()
		throws IOException, CambriaApiException, AccessDeniedException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		when(request.getInputStream()).thenReturn(servletInputStream);
		doThrow(new TopicExistsException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));
		eventsRestService.pushEventsWithTransaction("topicName", "partitionKey");
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWithTransaction_AccessDeniedException()
		throws IOException, CambriaApiException, AccessDeniedException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		when(request.getInputStream()).thenReturn(servletInputStream);
		doThrow(new AccessDeniedException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));
		eventsRestService.pushEventsWithTransaction("topicName", "partitionKey");
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWithTransaction_DMaaPAccessDeniedException()
		throws IOException, CambriaApiException, AccessDeniedException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		when(request.getInputStream()).thenReturn(servletInputStream);
		doThrow(new DMaaPAccessDeniedException(errorResponse)).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		eventsRestService.pushEventsWithTransaction("topicName", "partitionKey");
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWithTransaction_missingReqdSetting()
		throws IOException, CambriaApiException, AccessDeniedException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		when(request.getInputStream()).thenReturn(servletInputStream);
		doThrow(new missingReqdSetting("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		eventsRestService.pushEventsWithTransaction("topicName", "partitionKey");
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWithTransaction_IOException()
		throws IOException, CambriaApiException, AccessDeniedException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		when(request.getInputStream()).thenReturn(servletInputStream);
		doThrow(new IOException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		eventsRestService.pushEventsWithTransaction("topicName", "partitionKey");
	}

	@Test(expected = CambriaApiException.class)
	public void testPushEventsWithTransaction_ConfigDbException()
		throws IOException, CambriaApiException, AccessDeniedException, TopicExistsException, ConfigDbException,
		missingReqdSetting {
		when(request.getInputStream()).thenReturn(servletInputStream);
		doThrow(new ConfigDbException("error")).when(eventsService).pushEvents(ArgumentMatchers.any(DMaaPContext.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class));

		eventsRestService.pushEventsWithTransaction("topicName", "partitionKey");
	}
}
