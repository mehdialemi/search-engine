package ir.co.realtime.websearcher.crawler.fetcher;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static ir.co.realtime.websearcher.document.Document.Page;

@Service
@EnableAutoConfiguration
public class FetcherService {
    private static final Logger logger = LoggerFactory.getLogger(FetcherService.class);

    @Autowired
    public KafkaTemplate<String, byte[]> kafka;

    @Value("${crawler.fetch.update-topic}")
    private String updateTopic;

    @Autowired
    private Downloader downloader;

    @KafkaListener(topics = "${crawler.fetch.download-topic}")
    public void download(ConsumerRecord<String, byte[]> record) throws IOException {
        String url = new String(record.value());
        Page download = downloader.get(url);
        logger.info("Sending fetch content of url {} to kafka topic {}", url, updateTopic);
        kafka.send(new ProducerRecord<>(updateTopic, url, download.toByteArray()));
    }
}
