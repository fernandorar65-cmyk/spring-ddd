package kahoot.clabs.kahoot_clabs.quiz.domain.entity;

import java.util.UUID;

import kahoot.clabs.kahoot_clabs.shared.domain.BaseEntity;
import kahoot.clabs.kahoot_clabs.shared.domain.DomainException;

/**
 * Classification of quizzes inside an organization.
 */
public class Category extends BaseEntity {

    private final UUID organizationId;
    private String name;
    private String description;
    private String color;

    private Category(UUID id, UUID organizationId, String name, String description, String color) {
        super(id);
        if (organizationId == null) {
            throw new DomainException("Organization id is required");
        }
        if (name == null || name.isBlank()) {
            throw new DomainException("Category name is required");
        }
        this.organizationId = organizationId;
        this.name = name.trim();
        this.description = description;
        this.color = color;
    }

    public static Category create(UUID organizationId, String name) {
        return new Category(null, organizationId, name, null, null);
    }

    public static Category rehydrate(UUID id, UUID organizationId, String name, String description, String color) {
        return new Category(id, organizationId, name, description, color);
    }

    public void rename(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Category name is required");
        }
        this.name = name.trim();
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void changeColor(String color) {
        this.color = color;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getColor() {
        return color;
    }
}
