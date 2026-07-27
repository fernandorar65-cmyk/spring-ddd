package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter.aws;

import org.springframework.stereotype.Component;
import kahoot.clabs.kahoot_clabs.identity.application.port.AvatarStoragePort;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.storage.S3Storage;

@Component
public class AvatarStorageAdapter implements AvatarStoragePort {
    private final S3Storage storage;

    public AvatarStorageAdapter(S3Storage storage) {
        this.storage = storage;
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        return storage.upload(objectKey, content, contentType);
    }
}
