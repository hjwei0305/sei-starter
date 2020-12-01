package com.changhong.sei.core.mq;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.exception.ServiceException;
import com.changhong.sei.util.IdGenerator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 实现功能:
 * SEI消息队列生产者
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2020-01-08 13:31
 */
@Component
public class MqProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送队列消息
     * @param topic MQ topic
     * @param key 消息键值
     * @param message 消息
     */
    public void send(String topic, String key, String message) {
        kafkaTemplate.send(topic, key, message);
    }

    /**
     * 发送消息
     * @param key 消息键值
     * @param message 消息
     */
    public void send(String key, String message) {
        String topic = ContextUtil.getProperty("sei.mq.topic");
        if (StringUtils.isBlank(topic)) {
            throw new ServiceException("应用配置中没有消息队列的主题【sei.mq.topic】！");
        }
        send(topic, key, message);
    }

    /**
     * 发送消息
     * @param message 消息
     */
    public void send(String message) {
        String key = IdGenerator.uuid();
        send(key, message);
    }
}
