package com.changhong.sei.core.monitor.producer;

import com.changhong.sei.core.monitor.vo.AccessLogVo;
import com.changhong.sei.core.util.JsonUtils;
import com.changhong.sei.util.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 实现功能: 访问日志队列生产者
 *
 * @author 马超(Vision.Mac)
 * @version 1.0.00  2021-06-23 16:39
 */
public class AccessLogProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${sei.topic.logger.access:SeiAccessLog}")
    private String topic;

    public AccessLogProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * 发送队列消息
     *
     * @param log 日志消息
     */
    public void send(AccessLogVo log) {
        String message = JsonUtils.toJson(log);
        kafkaTemplate.send(topic, IdGenerator.uuid(), message);
    }
}
