package com.changhong.sei.core.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * <strong>实现功能:</strong>
 * <p>SEI消息队列生产者</p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2020-01-08 13:31
 */
@Component
public class MqProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Value("${sei.mq.topic}")
    private String kafkaTopic;

    /**
     * 发送消息
     * @param key 消息键值
     * @param message 消息
     */
    public void send(String key, String message) {
        kafkaTemplate.send(kafkaTopic, key, message);
    }

    /**
     * 发送消息
     * @param message 消息
     */
    public void send(String message) {
        String key = UUID.randomUUID().toString();
        send(key, message);
    }
}
