package com.changhong.sei.core.mq;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.protocol.types.Field;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

/**
 * <strong>实现功能:</strong>
 * <p>Kafka生产者配置</p>
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

    @Bean
    public MqProducer mqProducer(){
        return new MqProducer();
    }

    @Bean
    public DataChangeProducerImpl dataChangeProducer() {
        return new DataChangeProducerImpl();
    }
}
