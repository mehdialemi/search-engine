package ir.co.realtime.websearcher.crawler;

import com.google.protobuf.util.JsonFormat;
import ir.co.realtime.websearcher.common.LocalKafka;
import ir.co.realtime.websearcher.crawler.fetcher.Downloader;
import ir.co.realtime.websearcher.crawler.fetcher.GeneralWebParser;
import ir.co.realtime.websearcher.document.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ParserTest extends LocalKafka {

    @Autowired
    private GeneralWebParser webParser;

    @Autowired
    private Downloader downloader;

    @Test
    public void testSimple() throws IOException {
        Document.Page download = downloader.get("https://www.varzesh3.com");
        Document.Page parse = webParser.parse(download);
        Document.Page build = Document.Page.newBuilder()
                .setUrl(parse.getUrl())
                .setTitle(parse.getTitle())
                .setMeta(parse.getMeta())
                .setHtml(parse.getHtml())
                .build();
        String print = JsonFormat.printer().print(build);
        System.out.println(print);
    }
}
