package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter.azure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.identity.application.port.AvatarStoragePort;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.storage.AzureBlobStorage;

@Component
@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "azure")
public class AzureAvatarStorageAdapter implements AvatarStoragePort {

    private final AzureBlobStorage storage;

    public AzureAvatarStorageAdapter(AzureBlobStorage storage) {
        this.storage = storage;
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        return storage.upload(objectKey, content, contentType);
    }
}
