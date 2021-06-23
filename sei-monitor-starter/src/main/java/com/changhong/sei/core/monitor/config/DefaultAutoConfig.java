package com.changhong.sei.core.monitor.config;

import com.changhong.sei.core.monitor.interceptor.AccessLogHandlerInterceptor;
import com.changhong.sei.core.monitor.producer.AccessLogProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 实现功能：
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-06-23 16:39
 */
@Configuration
public class DefaultAutoConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessLogHandlerInterceptor());
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Bean
    public AccessLogHandlerInterceptor accessLogHandlerInterceptor() {
        return new AccessLogHandlerInterceptor();
    }

    @Bean
    public AccessLogProducer accessLogProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new AccessLogProducer(kafkaTemplate);
    }
}
