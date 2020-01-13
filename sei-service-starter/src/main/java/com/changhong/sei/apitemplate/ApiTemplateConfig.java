package com.changhong.sei.apitemplate;

import org.springframework.cloud.bus.ConditionalOnBusEnabled;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
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
        restTemplate.setErrorHandler(new SeiRestTemplateErrorHandle());
        return restTemplate;
    }

    @Bean(name = "urlRestTemplate")
    RestTemplate urlRestTemplate(){
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        restTemplate.setErrorHandler(new SeiRestTemplateErrorHandle());
        return restTemplate;
    }

    @Bean
    ApiTemplate apiTemplate(){
        return new ApiTemplate();
    }


    /**
     * 创建Feign请求拦截器，在发送请求前设置认证的token,各个微服务将token设置到环境变量中来达到通用
     * @return
     */
    @ConditionalOnBusEnabled
    @Bean
    public FeignBasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new FeignBasicAuthRequestInterceptor();
    }
}
