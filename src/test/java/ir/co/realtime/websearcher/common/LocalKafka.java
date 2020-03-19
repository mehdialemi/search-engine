package ir.co.realtime.websearcher.common;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.kafka.test.utils.KafkaTestUtils.getSingleRecord;

@Component
@EmbeddedKafka(partitions = 1, ports = 9092)
public class LocalKafka {
    private static final Logger logger = LoggerFactory.getLogger(LocalKafka.class);

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    private Consumer<String, byte[]> consumer;
    private Producer<String, byte[]> producer;

    private String consumerTopic;
    private String producerTopic;

    protected void init(String consumerTopic, String producerTopic) {
        this.consumerTopic = consumerTopic;
        this.producerTopic = producerTopic;

        logger.info("Running kafka on {}", Arrays.toString(embeddedKafka.getBrokerAddresses()));

        addTopic(consumerTopic);
        addTopic(producerTopic);

        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "false", embeddedKafka));
        consumer = new DefaultKafkaConsumerFactory<>(configs, new StringDeserializer(), new ByteArrayDeserializer()).createConsumer();
        embeddedKafka.consumeFromEmbeddedTopics(consumer, consumerTopic);

        producer = new DefaultKafkaProducerFactory<>(new HashMap<>(KafkaTestUtils.producerProps(embeddedKafka)),
                new StringSerializer(), new ByteArraySerializer()).createProducer();
    }

    protected void send(String key, byte[] value) {
        logger.info("Sending key {} to kafka topic {}", key, producerTopic);
        producer.send(new ProducerRecord<>(producerTopic, key, value));
        producer.flush();
        logger.info("Record wit key {} sent to kafka topic: {}", key, producerTopic);
    }

    protected byte[] getPublishedValue() {
        return getSingleRecord(consumer, consumerTopic).value();
    }

    private void addTopic(String topic) {
        try {
            embeddedKafka.addTopics(topic);
            logger.info("Topic {} created successfully", topic);
        } catch (Throwable e) { }
    }
}
