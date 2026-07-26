package kahoot.clabs.kahoot_clabs.shared.application.port;

public interface ImageStorage {

    String upload(String objectKey, byte[] content, String contentType);
}
