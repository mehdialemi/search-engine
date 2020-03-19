package ir.co.realtime.websearcher.crawler.fetcher;

import com.google.protobuf.ByteString;
import ir.co.realtime.websearcher.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

@Service
public class FetcherService {
    private static final Logger logger = LoggerFactory.getLogger(FetcherService.class);

    @Value("${web-searcher.crawler.fetcher.temp-dir:web-searcher}")
    private String tmpDir;

    public Document.Web download(String url) throws IOException {
        logger.trace("Downloading url: {}", url);

        URL link = new URL(url);
        ReadableByteChannel readableByteChannel = Channels.newChannel(link.openStream());

        File tempFile = File.createTempFile(tmpDir, ".html");
        FileOutputStream file = new FileOutputStream(tempFile);
        long length = file.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        byte[] bytes = new byte[(int) length];
        file.write(bytes);
        tempFile.deleteOnExit();

        return Document.Web.newBuilder()
                .setUrl(url)
                .setFetchTime(System.nanoTime())
                .setContent(ByteString.copyFrom(bytes))
                .build();
    }
}
