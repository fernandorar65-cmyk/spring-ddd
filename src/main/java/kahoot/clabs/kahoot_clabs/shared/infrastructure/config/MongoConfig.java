package kahoot.clabs.kahoot_clabs.shared.infrastructure.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

/**
 * Persists {@link UUID} fields as BSON strings (e.g. {@code "a840f87b-fb7c-6bc5-65ec-ece12b0db2a3"})
 * instead of Binary subtype 3/4, so Compass and ad-hoc queries stay readable.
 * <p>
 * Domain / JPA keep UUID; only the Mongo wire format changes.
 * Existing Binary UUID documents remain readable via Spring's default Binary→UUID converters.
 * Drop Mongo collections (or re-seed) if you want a uniform string layout.
 */
@Configuration
public class MongoConfig {

    @Bean
    MongoCustomConversions mongoCustomConversions() {
        return MongoCustomConversions.create(config -> {
            config.registerConverter(UuidToStringConverter.INSTANCE);
            config.registerConverter(StringToUuidConverter.INSTANCE);
        });
    }

    @WritingConverter
    enum UuidToStringConverter implements Converter<UUID, String> {
        INSTANCE;

        @Override
        public String convert(UUID source) {
            return source.toString();
        }
    }

    @ReadingConverter
    enum StringToUuidConverter implements Converter<String, UUID> {
        INSTANCE;

        @Override
        public UUID convert(String source) {
            return UUID.fromString(source);
        }
    }
}
