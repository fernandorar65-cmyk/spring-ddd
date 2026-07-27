package kahoot.clabs.kahoot_clabs.identity.application.port;

/**
 * Port used by Identity to upload user avatars without coupling use cases to S3.
 */
public interface AvatarStoragePort {

    String upload(String objectKey, byte[] content, String contentType);
}
