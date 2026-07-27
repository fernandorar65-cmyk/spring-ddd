package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter.azure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizAssetPort;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.storage.AzureBlobStorage;

@Component
@ConditionalOnProperty(prefix = "app", name = "storage", havingValue = "azure")
public class AzureQuizAssetAdapter implements QuizAssetPort {

    private final AzureBlobStorage storage;

    public AzureQuizAssetAdapter(AzureBlobStorage storage) {
        this.storage = storage;
    }

    @Override
    public String upload(String objectKey, byte[] content, String contentType) {
        return storage.upload(objectKey, content, contentType);
    }
}
