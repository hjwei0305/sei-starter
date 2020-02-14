package com.changhong.sei.envent.listener;

import com.changhong.sei.envent.PublishConfigEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.ApplicationListener;

import java.util.Set;

public class PublishConfigEventListener implements ApplicationListener<PublishConfigEvent> {

    private static final Logger log = LoggerFactory.getLogger(PublishConfigEventListener.class);

    @Value("${spring.cloud.config.name:default}")
    private String currentCode;

    @Value("${spring.cloud.config.profile:dev}")
    private String currentProfile;

    private ContextRefresher contextRefresher;

    public PublishConfigEventListener(ContextRefresher contextRefresher){
        this.contextRefresher = contextRefresher;
    }

    @Override
    public void onApplicationEvent(PublishConfigEvent event) {
        log.info("接收到配置更新事件：{}",event);
        if(currentCode.equals(event.getAppCode()) && currentProfile.equals(event.getProfile())){
            Set<String> keys = contextRefresher.refresh();
            log.info("更新了key值包括：{}", keys);
        }

    }
}
