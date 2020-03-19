package ir.co.realtime.websearcher.crawler;

import com.google.protobuf.InvalidProtocolBufferException;
import ir.co.realtime.websearcher.common.LocalKafka;
import ir.co.realtime.websearcher.document.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class FetcherTest extends LocalKafka {

    @Value("${crawler.fetch.download-topic}")
    private String downloadTopic;

    @Value("${crawler.fetch.update-topic}")
    private String updateTopic;

    @Before
    public void setup() {
        init(updateTopic, downloadTopic);
    }

    @Test
    public void simpleDownloadTest() throws InvalidProtocolBufferException {
        String url = "https://www.google.com";
        send(url, url.getBytes());

        Document.Page page = Document.Page.parseFrom(getPublishedValue());

        assertEquals(url, page.getUrl());
        assertTrue(page.getFetchTime() > 0);
        assertFalse(page.getValue().isEmpty());
    }
}
