package kahoot.clabs.kahoot_clabs.shared.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.s3")
public record S3StorageProperties(String bucket, String region, String publicBaseUrl) {
}
