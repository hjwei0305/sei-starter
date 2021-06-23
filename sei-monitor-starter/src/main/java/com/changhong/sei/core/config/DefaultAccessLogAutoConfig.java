package com.changhong.sei.core.config;

import com.changhong.sei.core.config.properties.AccessLogProperties;
import com.changhong.sei.core.log.interceptor.AccessLogHandlerInterceptor;
import com.changhong.sei.core.log.producer.AccessLogProducer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@ConditionalOnClass(KafkaTemplate.class)
@ConditionalOnProperty(value = "sei.log.access.enable", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({AccessLogProperties.class})
public class DefaultAccessLogAutoConfig implements WebMvcConfigurer {

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
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public AccessLogProducer accessLogProducer(KafkaTemplate<String, String> kafkaTemplate) {
        return new AccessLogProducer(kafkaTemplate);
    }
}
