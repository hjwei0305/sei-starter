package com.changhong.sei.core.mq.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现功能:
 * Kafka消费者配置
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2017-11-01 13:01
 */
@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Value("${sei.mq.group-id:spring.application.name}")
    private String groupId;

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> propsMap = new HashMap<>();
        propsMap.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        propsMap.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        /*
         spring kafka 设置超时时间（session.timeout.ms和max.poll.interval.ms） 防止出现rebalance
         kafka会有一个心跳线程来同步服务端，告诉服务端自己是正常可用的，默认是3秒发送一次心跳，超过session.timeout.ms（默认10秒）服务端没有收到心跳就会认为当前消费者失效。
         max.poll.interval.ms决定了获取消息后提交偏移量的最大时间，超过设定的时间（默认5分钟），服务端也会认为该消费者失效。
         */
        // 毫秒
        propsMap.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
        // 毫秒
        propsMap.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);
        propsMap.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        propsMap.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        propsMap.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        propsMap.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        return propsMap;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean(name = "batchListener")
    public ConcurrentKafkaListenerContainerFactory<String, String> batchListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);
        return factory;
    }
}
