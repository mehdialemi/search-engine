package ir.co.realtime.websearcher.common;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

import static ir.co.realtime.websearcher.document.Document.Page;

class PageSerialization {

    public static abstract class Adaptor {

        public void configure(Map<String, ?> configs, boolean isKey) { }

        public void close() { }
    }

    public static class PageSerializer extends Adaptor implements Serializer<Page> {

        @Override
        public byte[] serialize(String s, Page page) {
            return page.toByteArray();
        }
    }

    public static class PageDeserializer extends Adaptor implements Deserializer<Page> {

        @Override
        public Page deserialize(String s, byte[] bytes) {
            try {
                return Page.parseFrom(bytes);
            } catch (InvalidProtocolBufferException e) {
                throw new RuntimeException("Error in deserialize the given bytes from kafka", e);
            }
        }
    }
}
