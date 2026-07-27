package kahoot.clabs.kahoot_clabs.identity.application.port;

public interface AvatarStoragePort {

    String upload(String objectKey, byte[] content, String contentType);
}
