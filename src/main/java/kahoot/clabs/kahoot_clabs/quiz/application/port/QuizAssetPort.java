package kahoot.clabs.kahoot_clabs.quiz.application.port;

/**
 * Port used by Quiz to upload question assets without coupling use cases to S3.
 */
public interface QuizAssetPort {

    String upload(String objectKey, byte[] content, String contentType);
}
