package com.changhong.sei.webservice.config;

import org.apache.cxf.Bus;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jws.WebService;

/**
 * @Author: 刘松林
 * @Contact: songlin.liu@changhong.com
 * @Date: 2020/3/24 12:23
 * @Description: web-service 发布配置
 */
@Configuration
@ConditionalOnProperty(value = "sei.webservice.enable",havingValue = "true", matchIfMissing = true)
public class WebServiceConfig implements InitializingBean{

    @Autowired
    private ApplicationContext context;

    @Bean
    public ServletRegistrationBean cxfServlet() {
        ServletRegistrationBean<CXFServlet> registration = new ServletRegistrationBean<>(new CXFServlet());
        registration.setName("cxfServlet");
        registration.addUrlMappings("/webservice/*");
        return registration;
    }
    @Bean(name = Bus.DEFAULT_BUS_ID)
    public SpringBus springBus() {
        return new SpringBus();
    }

    @Override
    public void afterPropertiesSet(){
        EndpointImpl endpoint;
        WebService webService;
        SpringBus bus = springBus();
        for (String beanName : context.getBeanDefinitionNames()) {
            webService = context.findAnnotationOnBean(beanName, WebService.class);
            if (webService != null) {
                endpoint = new EndpointImpl(bus, context.getBean(beanName));
                endpoint.publish(webService.serviceName());
            }
        }
    }
}
