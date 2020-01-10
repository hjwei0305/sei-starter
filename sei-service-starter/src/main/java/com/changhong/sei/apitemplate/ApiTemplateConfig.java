package com.changhong.sei.apitemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 实现功能：
 * 平台调用API服务的客户端工具
 *
 * @author 王锦光(wangj)
 * @version 1.0.00      2017-03-27 10:07
 */
@Configuration
public class ApiTemplateConfig {



    @Bean(name = "loadBalancedRestTemplate")
    @Primary
    @LoadBalanced
    RestTemplate loadBalancedRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        restTemplate.setErrorHandler(new SeiRestTemlateErrorHandle());
        return restTemplate;
    }

    @Bean(name = "urlRestTemplate")
    RestTemplate urlRestTemplate(){
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        restTemplate.setErrorHandler(new SeiRestTemlateErrorHandle());
        return restTemplate;
    }

    @Bean
    ApiTemplate apiTemplate(){
        return new ApiTemplate();
    }
}
