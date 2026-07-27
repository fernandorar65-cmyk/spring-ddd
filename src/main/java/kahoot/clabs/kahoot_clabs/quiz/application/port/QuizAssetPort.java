package kahoot.clabs.kahoot_clabs.quiz.application.port;

public interface QuizAssetPort {

    String upload(String objectKey, byte[] content, String contentType);
}
