package kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "OrganizationMenberStatus")
public class OrganizationMenberStatus {
    @Id
    @Column(length = 36, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;
    
}
