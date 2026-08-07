package kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "organization_departments")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationDepartmentReadDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private String name;

    private String description;
}
