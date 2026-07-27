package kahoot.clabs.kahoot_clabs.quiz.infrastructure.adapter.aws;

import kahoot.clabs.kahoot_clabs.quiz.application.port.QuizAssetPort;
import kahoot.clabs.kahoot_clabs.shared.infrastructure.storage.S3ImageStorage;

public class S3QuizAssetAdapter implements QuizAssetPort {

    private final S3ImageStorage storage;

    public S3QuizAssetAdapter(S3ImageStorage storage) {
        this.storage = storage;
    }

    @Override
    public String upload(
            String objectKey,
            byte[] content,
            String contentType) {
        return storage.upload(
                objectKey,
                content,
                contentType);
    }
}
