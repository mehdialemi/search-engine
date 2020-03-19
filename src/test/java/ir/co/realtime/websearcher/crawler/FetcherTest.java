package ir.co.realtime.websearcher.crawler;

import ir.co.realtime.websearcher.crawler.fetcher.FetcherService;
import ir.co.realtime.websearcher.document.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.IOException;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:test.properties")
@EnableAutoConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FetcherTest {

    @Autowired
    private FetcherService fetcherService;

    @Test
    public void simpleDownloadTest() throws IOException {
        Document.Web webDocument = fetcherService.download("https://www.google.com/");
        assertNotNull(webDocument.getContent());
        assertFalse(webDocument.getContent().isEmpty());
    }
}
