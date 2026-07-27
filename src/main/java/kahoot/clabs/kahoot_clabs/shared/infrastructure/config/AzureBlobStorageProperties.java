package kahoot.clabs.kahoot_clabs.shared.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.azure")
public record AzureBlobStorageProperties(
        String connectionString,
        String container,
        String accountName,
        String publicBaseUrl) {
}
