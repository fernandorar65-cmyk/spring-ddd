package kahoot.clabs.kahoot_clabs.shared.application.port;

/**
 * Technology-agnostic port for object/blob storage uploads.
 * Shared across bounded contexts that need asset storage.
 */
public interface AssetsStoragePort {

    String upload(String objectKey, byte[] content, String contentType);
}
