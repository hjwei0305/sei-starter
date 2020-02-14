package com.changhong.sei.config;

import com.changhong.sei.envent.PublishConfigEvent;
import com.changhong.sei.envent.listener.PublishConfigEventListener;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration;
import org.springframework.cloud.bus.jackson.RemoteApplicationEventScan;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能：
 * 平台事件监听配置
 *
 * @author 刘松林(wangj)
 * @version 1.0.00      2020-02-14 10:07
 */
@Configuration
@RemoteApplicationEventScan(basePackageClasses = PublishConfigEvent.class)
@AutoConfigureAfter(LifecycleMvcEndpointAutoConfiguration.class) // so actuator endpoints have needed dependencies
public class EventConfig {

    @Bean("publishConfigEventListener")
    @ConditionalOnProperty(value = "sei.bus.refresh.enabled",
            matchIfMissing = true)
    public PublishConfigEventListener refreshListener(ContextRefresher contextRefresher) {
        return new PublishConfigEventListener(contextRefresher);
    }
}
