package kahoot.clabs.kahoot_clabs.shared.infrastructure.storage;

import java.util.Map;

import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.config.S3StorageProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3Storage{

    private final S3Client s3Client;
    private final S3StorageProperties properties;

    public S3Storage(S3Client s3Client, S3StorageProperties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    public String upload(String objectKey, byte[] content, String contentType) {
        if (properties.bucket() == null || properties.bucket().isBlank()) {
            throw new DomainException("S3 bucket is not configured");
        }
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(properties.bucket())
                        .key(objectKey)
                        .contentType(contentType)
                        .metadata(Map.of("uploaded-by", "kahoot-clabs"))
                        .build(),
                RequestBody.fromBytes(content));
        return publicUrl(objectKey);
    }

    private String publicUrl(String objectKey) {
        if (properties.publicBaseUrl() != null && !properties.publicBaseUrl().isBlank()) {
            return properties.publicBaseUrl().replaceAll("/+$", "") + "/" + objectKey;
        }
        return "https://%s.s3.%s.amazonaws.com/%s"
                .formatted(properties.bucket(), properties.region(), objectKey);
    }
}
