package ir.co.realtime.websearcher.crawler.fetcher;

import ir.co.realtime.websearcher.document.Document.Page;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ParserService {
    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);

    @Autowired
    public KafkaTemplate<String, byte[]> kafka;

    @Value("${crawler.fetch.parse-topic}")
    private String parseTopic;

    @Autowired
    private GeneralWebParser webParser;

    @KafkaListener(topics = "${crawler.fetch.update-topic}")
    public void parse(ConsumerRecord<String, byte[]> record) throws IOException {
        Page page = Page.parseFrom(record.value());
        if (page.getPageType() == Page.PageType.HTML) {
            Page parsed = webParser.parse(page);

            if (parsed.getStatus() == Page.Status.PARSED) {
                // after parse, store the result or send it to another topic in kafka
                logger.info("Sending fetch content of url {} to kafka topic {}", parsed.getUrl(), parseTopic);
                kafka.send(new ProducerRecord<>(parseTopic, parsed.getUrl(), parsed.toByteArray()));
            }
        }
    }
}
