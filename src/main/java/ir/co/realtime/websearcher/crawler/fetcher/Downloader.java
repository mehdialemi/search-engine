package ir.co.realtime.websearcher.crawler.fetcher;

import com.google.protobuf.ByteString;
import ir.co.realtime.websearcher.document.Document;
import ir.co.realtime.websearcher.document.Document.Page;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Component
public class Downloader {
    private static final Logger logger = LoggerFactory.getLogger(Downloader.class);

    @Value("${web-searcher.crawler.fetcher.temp-dir:web-searcher}")
    private String tmpDir;

    public Page get(String url) throws IOException {
        String html = Jsoup.connect(url).get().html();
        return Page.newBuilder().setUrl(url)
                .setValue(ByteString.copyFromUtf8(html))
                .setStatus(Page.Status.DOWNLOADED)
                .setFetchTime(System.nanoTime())
                .build();
    }

    private Page download(String url) throws IOException {
        logger.info("Downloading url: {}", url);
        URL link = new URL(url);

        ReadableByteChannel readableByteChannel = Channels.newChannel(link.openStream());
        File tempFile = File.createTempFile(tmpDir, ".html");
        FileOutputStream file = new FileOutputStream(tempFile);
        long length = file.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        byte[] bytes = new byte[(int) length];
        file.write(bytes, 0, bytes.length);
        String html = new String(bytes);
        System.out.println(html);
        tempFile.deleteOnExit();

        logger.info("Url {} is downloaded successfully, size: {} bytes", url, bytes.length);

        return Page.newBuilder().setUrl(url)
                .setValue(ByteString.copyFrom(bytes))
                .setStatus(Page.Status.DOWNLOADED)
                .setFetchTime(System.nanoTime())
                .build();

    }
}
