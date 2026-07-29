package kahoot.clabs.kahoot_clabs.identity.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.AuditableEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Image attached to a user (avatar, profile gallery, etc.).
 * Child entity of the {@code User} aggregate — mutate via the root.
 */
public class UserImages extends AuditableEntity {

    private static final int URL_MAX = 150;
    private static final int TYPE_MAX = 100;
    private static final int ALT_MAX = 100;
    private static final int SLUG_MAX = 100;

    private UUID userId;
    private String url;
    private String type;
    private String alt;
    private String slug;

    private UserImages(
            UUID id,
            UUID userId,
            String url,
            String type,
            String alt,
            String slug,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        super(id, createdAt, updatedAt);
        this.userId = userId;
        this.url = requireUrl(url);
        this.type = requireType(type);
        this.alt = requireAlt(alt);
        this.slug = requireSlug(slug);
    }

    public static UserImages create(UUID userId, String url, String type, String alt, String slug) {
        if (userId == null) {
            throw new DomainException("User id is required");
        }
        return new UserImages(null, userId, url, type, alt, slug, null, null);
    }

    public static UserImages rehydrate(
            UUID id,
            UUID userId,
            String url,
            String type,
            String alt,
            String slug) {
        return rehydrate(id, userId, url, type, alt, slug, null, null);
    }

    public static UserImages rehydrate(
            UUID id,
            UUID userId,
            String url,
            String type,
            String alt,
            String slug,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        if (userId == null) {
            throw new DomainException("User id is required");
        }
        return new UserImages(id, userId, url, type, alt, slug, createdAt, updatedAt);
    }

    void assignUserId(UUID userId) {
        if (userId == null) {
            throw new DomainException("User id is required");
        }
        this.userId = userId;
        touch();
    }

    public void changeUrl(String url) {
        this.url = requireUrl(url);
        touch();
    }

    public void changeType(String type) {
        this.type = requireType(type);
        touch();
    }

    public void changeAlt(String alt) {
        this.alt = requireAlt(alt);
        touch();
    }

    public void changeSlug(String slug) {
        this.slug = requireSlug(slug);
        touch();
    }

    public void update(String url, String type, String alt, String slug) {
        this.url = requireUrl(url);
        this.type = requireType(type);
        this.alt = requireAlt(alt);
        this.slug = requireSlug(slug);
        touch();
    }

    public UUID getUserId() {
        return userId;
    }

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getAlt() {
        return alt;
    }

    public String getSlug() {
        return slug;
    }

    private static String requireUrl(String url) {
        return requireText(url, "Image url", URL_MAX);
    }

    private static String requireType(String type) {
        return requireText(type, "Image type", TYPE_MAX);
    }

    private static String requireAlt(String alt) {
        return requireText(alt, "Image alt", ALT_MAX);
    }

    private static String requireSlug(String slug) {
        return requireText(slug, "Image slug", SLUG_MAX);
    }

    private static String requireText(String value, String label, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new DomainException(label + " is required");
        }
        String normalized = value.trim();
        if (normalized.length() > maxLength) {
            throw new DomainException(label + " must be at most " + maxLength + " characters");
        }
        return normalized;
    }
}
