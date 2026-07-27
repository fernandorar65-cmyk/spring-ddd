package kahoot.clabs.kahoot_clabs.shared.infrastructure.storage;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlobParallelUploadOptions;

import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.config.AzureBlobStorageProperties;

@Component
@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "azure")
public class AzureBlobStorage {

    private final BlobServiceClient blobServiceClient;
    private final AzureBlobStorageProperties properties;

    public AzureBlobStorage(BlobServiceClient blobServiceClient, AzureBlobStorageProperties properties) {
        this.blobServiceClient = blobServiceClient;
        this.properties = properties;
    }

    public String upload(String objectKey, byte[] content, String contentType) {
        if (properties.container() == null || properties.container().isBlank()) {
            throw new DomainException("Azure blob container is not configured");
        }
        if (content == null || content.length == 0) {
            throw new DomainException("Cannot upload empty content to Azure Blob Storage");
        }

        BlobClient blobClient = blobServiceClient
                .getBlobContainerClient(properties.container())
                .getBlobClient(objectKey);

        BlobHttpHeaders headers = new BlobHttpHeaders().setContentType(contentType);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("uploadedBy", "kahoot-clabs");

        blobClient.uploadWithResponse(
                new BlobParallelUploadOptions(BinaryData.fromBytes(content))
                        .setHeaders(headers)
                        .setMetadata(metadata),
                null,
                null);

        return publicUrl(objectKey);
    }

    private String publicUrl(String objectKey) {
        if (properties.publicBaseUrl() != null && !properties.publicBaseUrl().isBlank()) {
            return properties.publicBaseUrl().replaceAll("/+$", "") + "/" + objectKey;
        }
        if (properties.accountName() == null || properties.accountName().isBlank()) {
            throw new DomainException(
                    "Configure storage.azure.account-name or storage.azure.public-base-url to build blob URLs");
        }
        return "https://%s.blob.core.windows.net/%s/%s"
                .formatted(properties.accountName(), properties.container(), objectKey);
    }
}
