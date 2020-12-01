package com.changhong.sei.core.mq;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import java.util.Objects;

/**
 * <strong>实现功能:</strong>
 * <p>SEI消息队列消费者</p>
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2020-01-08 13:34
 */
public abstract class MqConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(MqConsumer.class);
    /**
     * 消息的键值标识
     */
    private String key;

    /**
     * 获取当前消息的分类键
     *
     * @return 键值标识
     */
    protected String getKey() {
        return key;
    }

    /**
     * 处理收到的监听消息
     *
     * @param record 消息纪录
     */
    @KafkaListener(topics = "${sei.mq.topic}")
    public void processMessage(ConsumerRecord<String, String> record) {
        if (Objects.isNull(record)) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("received key='{}' message = '{}'", record.key(), record.value());
        }
        // 执行业务处理逻辑
        try {
            this.key = record.key();
            process(record.value());
        } catch (Exception e) {
            LOG.error("MqConsumer process message error!", e);
        }
    }

    /**
     * 收到的监听消息后的业务处理
     *
     * @param message 队列消息
     */
    public abstract void process(String message);
}
