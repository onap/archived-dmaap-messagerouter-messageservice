	beans{
	xmlns cxf: "http://camel.apache.org/schema/cxf"
	xmlns jaxrs: "http://cxf.apache.org/jaxrs"
	xmlns util: "http://www.springframework.org/schema/util"
	
	echoService(org.onap.dmaap.JaxrsEchoService)
	userService(org.onap.dmaap.JaxrsUserService)
	topicService(org.onap.dmaap.service.TopicRestService)
	eventService(org.onap.dmaap.service.EventsRestService)
	adminServiceorg.(org.onap.dmaap.service.AdminRestService)
	apiKeyService(org.onap.dmaap.service.ApiKeysRestService)
	metricsService(org.onap.dmaap.service.MetricsRestService)
	transactionService(org.onap.dmaap.service.TransactionRestService)
	UIService(org.onap.dmaap.service.UIRestServices)
	mirrorService(org.onap.dmaap.service.MMRestService)
	
	util.list(id: 'jaxrsServices') {
		ref(bean:'echoService')
		ref(bean:'userService')
		
	}
}