package kahoot.clabs.kahoot_clabs.users.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.AggregateRoot;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;
import kahoot.clabs.kahoot_clabs.users.domain.enums.OrganizationStatus;

public class Organization extends AggregateRoot {

    private final UUID id;
    private String name;
    private String slug;
    private String logo;
    private String description;
    private String timezone;
    private String language;
    private OrganizationStatus status;

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Organization(String name, String slug) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Organization name is required");
        }
        if (slug == null || slug.isBlank()) {
            throw new DomainException("Organization slug is required");
        }
        this.id = UUID.randomUUID();
        this.name = name.trim();
        this.slug = slug.trim().toLowerCase();
        this.status = OrganizationStatus.ACTIVE;
        this.timezone = "America/Bogota";
        this.language = "es";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public static Organization create(String name, String slug) {
        return new Organization(name, slug);
    }

    public void updateDetails(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Organization name is required");
        }
        this.name = name.trim();
        this.description = description;
        touch();
    }

    public void changeSlug(String slug) {
        if (slug == null || slug.isBlank()) {
            throw new DomainException("Organization slug is required");
        }
        this.slug = slug.trim().toLowerCase();
        touch();
    }

    public void activate() {
        this.status = OrganizationStatus.ACTIVE;
        touch();
    }

    public void deactivate() {
        this.status = OrganizationStatus.INACTIVE;
        touch();
    }

    public void suspend() {
        this.status = OrganizationStatus.SUSPENDED;
        touch();
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getLogo() {
        return logo;
    }

    public String getDescription() {
        return description;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getLanguage() {
        return language;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
