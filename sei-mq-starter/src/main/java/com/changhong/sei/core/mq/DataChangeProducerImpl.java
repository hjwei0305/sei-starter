package com.changhong.sei.core.mq;

import com.changhong.sei.core.context.ContextUtil;
import com.changhong.sei.core.datachange.DataChangeProducer;
import com.changhong.sei.core.log.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 实现功能: 数据变更记录队列生产者
 *
 * @author 王锦光 wangjg
 * @version 2020-04-22 21:17
 */
@Component
public class DataChangeProducerImpl implements DataChangeProducer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    // 约定的MQ Topic Key
    private final static String KEY = "DataHistoryRecord";

    /**
     * 获取系统应用配置的MQ Topic值
     * @return MQ Topic
     */
    private String getTopic() {
        return ContextUtil.getProperty("sei.datachange.topic");
    }

    /**
     * 发送消息
     *
     * @param message 消息
     */
    @Override
    public void send(String message) {
        String topic = getTopic();
        if (StringUtils.isBlank(topic)) {
            return;
        }
        kafkaTemplate.send(topic, KEY, message);
        LogUtil.bizLog("数据变更记录队列生产者："+message);
    }
}
