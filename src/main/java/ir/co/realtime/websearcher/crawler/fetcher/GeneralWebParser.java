package ir.co.realtime.websearcher.crawler.fetcher;

import com.google.protobuf.ByteString;
import ir.co.realtime.websearcher.document.Document.Page;
import ir.co.realtime.websearcher.document.Document.Page.Field;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class GeneralWebParser {

    public Page parse(Page page) {
        String html = page.getValue().toStringUtf8();
        Document doc = Jsoup.parse(html);
        return Page.newBuilder(page)
                .setTitle(doc.title())
                .setMeta(meta(doc))
                .setHtml(html(doc))
                .setStatus(Page.Status.PARSED)
                .build();
    }


    private Page.Meta meta(Document doc) {
        Element head = doc.head();

        String charset = head.selectFirst("meta[charset]").attr("charset");
        String keywords =  head.select("[name=keywords]").first().attr("content");
        String description = head.select("[name=description]").first().attr("content");

        return Page.Meta.newBuilder()
                .setCharset(charset)
                .setKeywords(keywords)
                .setDescription(description)
                .build();
    }

    private Page.Html html(Document doc) {
        Page.Html.Builder htmlBuilder = Page.Html.newBuilder();

        Elements headers = doc.body().select("h1, h2, h3, h4, h5, h6");

        for (Element header : headers) {
            Field field = field(header);
            switch (header.text()) {
                case "h1": htmlBuilder.addH1Fields(field); break;
                case "h2": htmlBuilder.addH2Fields(field); break;
                case "h3": htmlBuilder.addH3Fields(field); break;
                case "h4": htmlBuilder.addH4Fields(field); break;
                case "h5": htmlBuilder.addH5Fields(field); break;
                case "h6": htmlBuilder.addH6Fields(field); break;
            }
        }

        for (Element element : doc.body().select("a[href]")) {
            htmlBuilder.addLinks(link(element));
        }

        for (Element element : doc.body().select("[src]")) {
            if (element.tagName().equals("img")) {
                htmlBuilder.addImages(image(element));
            }
        }

        htmlBuilder.setBody(doc.normalise().text());

        return htmlBuilder.build();
    }

    private Page.Link link(Element element) {
        Page.Link.Builder builder = Page.Link.newBuilder();

        String link = element.attr("href");
        if (link.isEmpty())
            return builder.build();

        builder.setUrl(link);

        // TODO consider domain name too
        if (!link.startsWith("http"))
            builder.setRelative(true);

        builder.setText(element.text());

        return builder.build();
    }

    private Page.Image image(Element element) {
        Page.Image.Builder builder = Page.Image.newBuilder();

        String link = element.attr("src");
        if (link.isEmpty())
            return builder.build();

        builder.setSrc(link);

        // TODO consider domain name too
        if (!link.startsWith("http")) {
            builder.setRelative(true);
        }

        builder.setAltText(element.attr("alt"));
        try {
            builder.setWidth(Integer.parseInt(element.attr("width")));
            builder.setHeight(Integer.parseInt(element.attr("height")));
        } catch (RuntimeException e) {}

        return builder.build();
    }

    private Field field(Element element) {
        return Field.newBuilder()
                .setFieldType(Field.FieldType.TEXT)
                .setFieldValue(ByteString.copyFrom(element.text().getBytes()))
                .build();
    }
}
