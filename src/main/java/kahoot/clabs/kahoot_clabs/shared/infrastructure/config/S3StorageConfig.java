package kahoot.clabs.kahoot_clabs.shared.infrastructure.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "aws", matchIfMissing = true)
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3StorageConfig {

    @Bean
    S3Client s3Client(S3StorageProperties properties) {
        return S3Client.builder()
                .region(Region.of(properties.region()))
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }
}
