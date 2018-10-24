	beans{
	xmlns cxf: "http://camel.apache.org/schema/cxf"
	xmlns jaxrs: "http://cxf.apache.org/jaxrs"
	xmlns util: "http://www.springframework.org/schema/util"
	
	echoService(org.onap.dmaap.JaxrsEchoService)
	userService(org.onap.dmaap.JaxrsUserService)
		
	util.list(id: 'jaxrsServices') {
		ref(bean:'echoService')
		ref(bean:'userService')
		
	}
}