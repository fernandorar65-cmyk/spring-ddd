package kahoot.clabs.kahoot_clabs.users.domain.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

import kahoot.clabs.kahoot_clabs.users.domain.model.Enums.OrganizationStatus;

@Getter
public class Organization {

    private final UUID id;
    private String name;
    private String slug;
    private String logo;
    private String description;
    private String timezone;
    private String language;
    private OrganizationStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Organization(UUID id, String name, String slug) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.slug = slug;
        this.status = OrganizationStatus.ACTIVE;
        this.timezone = "America/Bogota";
        this.language = "es";
        this.createdAt = LocalDateTime.now();
    }

    public static Organization create(String name, String slug) {
        return new Organization(null, name, slug);
    }

    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
}