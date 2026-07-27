package kahoot.clabs.kahoot_clabs.shared.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "azure")
@EnableConfigurationProperties(AzureBlobStorageProperties.class)
public class AzureBlobStorageConfig {

    @Bean
    BlobServiceClient blobServiceClient(AzureBlobStorageProperties properties) {
        if (properties.connectionString() == null || properties.connectionString().isBlank()) {
            throw new IllegalStateException(
                    "storage.azure.connection-string is required when app.storage=azure");
        }
        return new BlobServiceClientBuilder()
                .connectionString(properties.connectionString())
                .buildClient();
    }
}
