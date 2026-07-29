package kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.UUID;

@Entity
@Table(name = "OrganizationJobs")
public class OrganizationJobs {
    @Id
    @Column(length = 36, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, length = 100)
    private String description;
    
    
}
