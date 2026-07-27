package kahoot.clabs.kahoot_clabs.identity.infrastructure.adapter.aws;

import org.springframework.stereotype.Component;
import kahoot.clabs.kahoot_clabs.identity.application.port.AvatarStoragePort;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.storage.S3ImageStorage;

@Component
public class AvatarStorageAdapter implements AvatarStoragePort {
    private final S3ImageStorage storage;

    public AvatarStorageAdapter(S3ImageStorage storage) {
        this.storage = storage;
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        return storage.upload(objectKey, content, contentType);
    }
}
