package kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "organization_jobs")
public class OrganizationJobEntity {

    @Id
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false, length = 150, unique = true)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
