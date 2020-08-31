package com.changhong.sei.core.mq.config;

import com.changhong.sei.core.datachange.DataChangeProducer;
import com.changhong.sei.core.mq.MqProducer;
import com.changhong.sei.core.mq.support.DefaultDataChangeProducer;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 实现功能:
 * Kafka生产者配置
 *
 * @author 王锦光 wangj
 * @version 1.0.1 2017-11-01 12:48
 */
@Configuration
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    private Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * SEI消息队列生产者
     */
    @Bean
    public MqProducer mqProducer() {
        return new MqProducer();
    }

    /**
     * 数据变更记录队列生产者
     */
    @Bean
    public DataChangeProducer dataChangeProducer() {
        return new DefaultDataChangeProducer();
    }
}
