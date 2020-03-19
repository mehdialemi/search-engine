package ir.co.realtime.websearcher.crawler.fetcher;

import com.google.protobuf.ByteString;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static ir.co.realtime.websearcher.document.Document.Page;

@Component
@EnableAutoConfiguration
public class FetcherService {
    private static final Logger logger = LoggerFactory.getLogger(FetcherService.class);

    @Autowired
    public KafkaTemplate<String, byte[]> kafka;

    @Value("${web-searcher.crawler.fetcher.temp-dir:web-searcher}")
    private String tmpDir;

    @Value("${crawler.fetch.update-topic}")
    private String updateTopic;

    @KafkaListener(topics = "${crawler.fetch.download-topic}")
    public void download(ConsumerRecord<String, byte[]> record) throws IOException {

        String url = new String(record.value());
        logger.info("Downloading url: {}", url);

        URL link = new URL(url);
        ReadableByteChannel readableByteChannel = Channels.newChannel(link.openStream());
        File tempFile = File.createTempFile(tmpDir, ".html");
        FileOutputStream file = new FileOutputStream(tempFile);
        long length = file.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        byte[] bytes = new byte[(int) length];
        file.write(bytes);
        tempFile.deleteOnExit();
        logger.info("Url {} is downloaded successfully", url);

        Page pageUpdate = Page.newBuilder().setUrl(url)
                .setBytes(ByteString.copyFrom(bytes))
                .setFetchStatus(Page.FetchStatus.FETCHED)
                .setFetchTime(System.nanoTime())
                .build();

        logger.info("Sending fetch content of url {} to kafka topic {}", url, updateTopic);
        kafka.send(new ProducerRecord<>(updateTopic, url, pageUpdate.toByteArray()));
    }
}
