	beans{
	xmlns cxf: "http://camel.apache.org/schema/cxf"
	xmlns jaxrs: "http://cxf.apache.org/jaxrs"
	xmlns util: "http://www.springframework.org/schema/util"
	
	echoService(com.att.nsa.dmaap.JaxrsEchoService)
	userService(com.att.nsa.dmaap.JaxrsUserService)
	topicService(com.att.nsa.dmaap.service.TopicRestService)
	eventService(com.att.nsa.dmaap.service.EventsRestService)
	adminService(com.att.nsa.dmaap.service.AdminRestService)
	apiKeyService(com.att.nsa.dmaap.service.ApiKeysRestService)
	metricsService(com.att.nsa.dmaap.service.MetricsRestService)
	transactionService(com.att.nsa.dmaap.service.TransactionRestService)
	UIService(com.att.nsa.dmaap.service.UIRestServices)
	mirrorService(com.att.nsa.dmaap.service.MMRestService)
	
	util.list(id: 'jaxrsServices') {
		ref(bean:'echoService')
		ref(bean:'userService')
		
	}
}