package kahoot.clabs.kahoot_clabs.gameplay.infrastructure.adapter.aws;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.gameplay.application.port.QuizAssetPort;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.storage.S3Storage;

@Component
@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "aws", matchIfMissing = true)
public class S3QuizAssetAdapter implements QuizAssetPort {

    private final S3Storage storage;

    public S3QuizAssetAdapter(S3Storage storage) {
        this.storage = storage;
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        return storage.upload(objectKey, content, contentType);
    }
}
